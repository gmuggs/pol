package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import edu.gmu.mason.vanilla.AgentInterest;
import edu.gmu.mason.vanilla.Person;

/**
 * General description_________________________________________________________
 * A class to record and calculate visitor profile of a pub.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 */
public class VisitorProfile implements java.io.Serializable {
	private static final long serialVersionUID = 5636683287496882456L;
	private long total;
	private double averageAge;
	private double averageIncome;
	private List<AgentInterest> interests;
	private boolean calculated;
	private int minRecordSizeForProfiling;
	private int maxNumberOfInterestsToConsider;

	private Queue<VisitorLog> fifoLogQueue;

	public VisitorProfile(int maxRecordSize, int minRecordSizeForProfiling,
			int maxNumberOfInterestsToConsider) {
		fifoLogQueue = new CircularFifoQueue<>(maxRecordSize);
		this.minRecordSizeForProfiling = minRecordSizeForProfiling;
		this.maxNumberOfInterestsToConsider = maxNumberOfInterestsToConsider;
		calculated = false;
		total = 0;
	}

	public void addVisitor(Person agent) {
		VisitorLog log = new VisitorLog();

		log.setAge(agent.getAge());
		log.setInterest(agent.getInterest());

		if (agent.getFinancialSafetyNeed().isEmployed() == true) {
			log.setIncome(agent.getJob().getHourlyRate());
		} else {
			log.setIncome(0.0);
		}
		total++;
		fifoLogQueue.add(log);
	}

	public long getTotal() {
		return total;
	}

	public void updateProfile() {
		if (fifoLogQueue.size() < minRecordSizeForProfiling) {
			return;
		}
		calculated = true;
		this.averageAge = fifoLogQueue.stream().mapToDouble(VisitorLog::getAge)
				.average().orElse(0.0);
		this.averageIncome = fifoLogQueue.stream()
				.mapToDouble(VisitorLog::getIncome).average().orElse(0.0);
		// Collections.f
		Map<AgentInterest, Long> counts = fifoLogQueue.stream().collect(
				Collectors.groupingBy(VisitorLog::getInterest, TreeMap::new,
						Collectors.counting()));

		LinkedHashMap<AgentInterest, Long> sortedCounts = counts
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(
						Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, (x, y) -> {
									throw new AssertionError();
								}, LinkedHashMap::new));

		interests = new ArrayList<>();
		Iterator<AgentInterest> iterInterest = sortedCounts.keySet().iterator();

		for (int i = 0; i < this.maxNumberOfInterestsToConsider; i++) {
			if (iterInterest.hasNext() == true) {
				interests.add(iterInterest.next());
			} else {
				interests.add(AgentInterest.NA);
			}
		}
	}

	public int getRecordSize() {
		return fifoLogQueue.size();
	}

	public double getAverageAge() {
		return averageAge;
	}

	public double getAverageIncome() {
		return averageIncome;
	}

	public List<AgentInterest> getInterests() {
		return interests;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public void setAverageAge(double averageAge) {
		this.averageAge = averageAge;
	}

	public void setAverageIncome(double averageIncome) {
		this.averageIncome = averageIncome;
	}

	public void setInterests(List<AgentInterest> interests) {
		this.interests = interests;
	}

	public void setisCalculated(boolean calculated) {
		this.calculated = calculated;
	}
}
