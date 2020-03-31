package edu.gmu.mason.vanilla.log;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * General description_________________________________________________________
 * Log schedule class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class EventList implements Serializable {
	private static final long serialVersionUID = 1923207658906581595L;
	private static final int DEFAULT_QUEUE_SIZE = 2;
	private long currentTime;
	private Object id;
	private List<Item> list;
	@Skip
	private int maxNumOfKeptEvents;
	@Skip
	private boolean isIndividualUpdateTime;

	public EventList(Object id) {
		list = new ArrayList<Item>();
		this.id = id;
		maxNumOfKeptEvents = DEFAULT_QUEUE_SIZE;
	}

	public void add(Supplier s) {
		Item item = new Item(maxNumOfKeptEvents);
		item.s = s;
		list.add(item);
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public UpdateStatus update(long currentTime) {
		this.currentTime = currentTime;
		boolean hasUpdate = false;
		for (Item item : list) {
			Deque<ImmutablePair<Long, String>> q = item.queue;
			long tmp = currentTime;
			
			Object obj;
			try {
				obj = item.s.get();
			} catch(NullPointerException e) {
				return UpdateStatus.REMOVED;
			}
			String text = obj == null ? "" : obj.toString();
			int size = q.size();
			if (size > 0) {
				ImmutablePair<Long, String> last = q.getLast();
				if (last.left < tmp && !last.right.equals(text)) {
					// this means it is a new item
					ImmutablePair<Long, String> pair = new ImmutablePair<Long, String>(tmp, text);
					q.addLast(pair);
					while (q.size() > maxNumOfKeptEvents)
						q.removeFirst();
					
					hasUpdate |= true;
				}
			} else {
				// this means first time
				ImmutablePair<Long, String> pair = new ImmutablePair<Long, String>(tmp, text);
				q.addLast(pair);
				hasUpdate |= true;
			}
		}
		return hasUpdate? UpdateStatus.UPDATED : UpdateStatus.NO_UPDATE;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public void enableIndividualUpdateTime(boolean enable) {
		isIndividualUpdateTime = enable;
	}

	public enum UpdateStatus {
		NO_UPDATE,
		UPDATED,
		REMOVED
	}

	class Item implements Serializable {
		private static final long serialVersionUID = -5532267430477759209L;
		Deque<ImmutablePair<Long,String>> queue;
		@Skip
		Supplier s;
		Item(int size) {
			queue = new LinkedList<>();
		}

		public boolean enabledUpdateTime() {
			return isIndividualUpdateTime;
		}
	}

	public static class ItemTypeAdapter extends TypeAdapter<Item> {
		@Override
		public void write(JsonWriter out, Item value) throws IOException {
			Deque<ImmutablePair<Long,String>> q = value.queue;
			int size = q.size();
			if(size > 0) {
				ImmutablePair<Long,String> last = q.getLast();
				if(value.enabledUpdateTime())
					out.value(last.toString());
				else
					out.value(last.getValue());
			}
			else {
				out.nullValue();
			}
		}

		@Override
		public Item read(JsonReader in) throws IOException {
			// TODO: implement this when parsing is needed
			throw new UnsupportedOperationException("Not implemented");
		}
	}
}
