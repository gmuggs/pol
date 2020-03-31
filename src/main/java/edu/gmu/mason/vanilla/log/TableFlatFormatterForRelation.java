package edu.gmu.mason.vanilla.log;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class TableFlatFormatterForRelation extends AbstractFormatter {
	private static final long serialVersionUID = 4871711945409433387L;
	private Supplier<LocalDateTime> timeSuppiler; 
	
	public TableFlatFormatterForRelation(Supplier<LocalDateTime> timeSuppiler) {
		this.timeSuppiler = timeSuppiler;
	}

	@Override
	protected String format(Object value) {
		// we assume have graph data
		StringBuilder sb = new StringBuilder();
		if (value instanceof Graph) {
			Graph graph = (Graph) value;
			Collection<Node> fromNodes = graph.getNodeSet();
			Iterator<Node> iter = null;
			Node toNode = null;
			for (Node fromNode : fromNodes) {
				while (true) {
					if (iter == null) {
						iter = fromNode.getNeighborNodeIterator();
					}

					if (iter.hasNext()) {
						//sb.append((long) graph.getStep()).append("\t");
						sb.append(timeSuppiler.get().toString(ISODateTimeFormat.dateTimeNoMillis())).append("\t");
						sb.append(fromNode.getId()).append("\t");
						toNode = iter.next();
						sb.append(toNode.getId()).append("\t");
						sb.append("\n");
					} else {
						iter = null;
						break;
					}
				}
			}
		}
		if(sb.length()>1)
			sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

}