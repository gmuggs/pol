package edu.gmu.mason.vanilla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * General description_________________________________________________________
 * A class to capture the quantities of interests to be reported to T/E team.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class QuantitiesOfInterest extends AnnotatedPropertied {

	private static final long serialVersionUID = -5856201401528559677L;
	private static final int MEMORY_LENGTH_IN_STEPS = 2; 
	
	// QoI keys
	public static final String AVERAGE_SOCIAL_NETWORK_DEGREE = "avgNetworkDegree";
	public static final String AVERAGE_BALANCE = "avgBalance";
	public static final String PERCENTAGE_OF_UNHAPPY_AGENTS = "percentageUnhappy";
	public static final String PUB_VISITS_PER_AGENT = "pubVisitPerAgent";
	public static final String NUM_OF_SOCIAL_INTERACTIONS = "numOfSocialInteractions";

	// in order to show QOI values in a tab of GUI and chart 
	@EditableProperty(group = "Quantity of Interest", description = "Average social network degree", lower = "0.0", upper = "0.0", readOnly = true)
	public double avgNetworkDegree;
	@EditableProperty(group = "Quantity of Interest", description = "Average balance of all agents", lower = "0.0", upper = "0.0", readOnly = true)
	public double avgBalance;
	@EditableProperty(group = "Quantity of Interest", description = "Percentage of unhappy agents", lower = "0.0", upper = "0.0", readOnly = true)
	public double percentageUnhappy;
	@EditableProperty(group = "Quantity of Interest", description = "Number of visits per agent", lower = "0.0", upper = "0.0", readOnly = true)
	public double pubVisitPerAgent;
	@EditableProperty(group = "Quantity of Interest", description = "Number of social interactions", lower = "0", upper = "0", readOnly = true)
	public int numOfSocialInteractions;
	
	// General logging objects
	private Map<String, Map<Long, Double>> quantitiesOfInterestLogs;
	private Map<String, Long> loggingStepInterval;

	// Objects used to calculate complex QoI
	private long pubVisitCount;
	private List<AgentInteraction> agentInteractions;

	public QuantitiesOfInterest(int minutePerStep) {

		long onceADay = (long) (60.0 / minutePerStep * 24.0);
		pubVisitCount = 0;
		agentInteractions = new ArrayList<AgentInteraction>();

		loggingStepInterval = new HashMap<String, Long>();
		quantitiesOfInterestLogs = new HashMap<String, Map<Long, Double>>();

		quantitiesOfInterestLogs.put(AVERAGE_SOCIAL_NETWORK_DEGREE, new HashMap<Long, Double>());
		loggingStepInterval.put(AVERAGE_SOCIAL_NETWORK_DEGREE, onceADay); // will be collected once a day

		quantitiesOfInterestLogs.put(AVERAGE_BALANCE, new HashMap<Long, Double>());
		loggingStepInterval.put(AVERAGE_BALANCE, onceADay); // will be collected once a day

		quantitiesOfInterestLogs.put(PERCENTAGE_OF_UNHAPPY_AGENTS, new HashMap<Long, Double>());
		loggingStepInterval.put(PERCENTAGE_OF_UNHAPPY_AGENTS, onceADay); // will be collected once a day

		quantitiesOfInterestLogs.put(PUB_VISITS_PER_AGENT, new HashMap<Long, Double>());
		loggingStepInterval.put(PUB_VISITS_PER_AGENT, onceADay); // will be collected once a day

		quantitiesOfInterestLogs.put(NUM_OF_SOCIAL_INTERACTIONS, new HashMap<Long, Double>());
		loggingStepInterval.put(NUM_OF_SOCIAL_INTERACTIONS, onceADay); // will be collected once a day
	}

	public void addValue(String key, Double value, Long step) {
		long oldStepThreshold = step - MEMORY_LENGTH_IN_STEPS;
		quantitiesOfInterestLogs.get(key).put(step, value);
		
		// forget old values
		Map<Long,Double> map = quantitiesOfInterestLogs.get(key);
		Iterator<Entry<Long, Double>> mapIterator = map.entrySet().iterator();
		while (mapIterator.hasNext() && mapIterator.next().getKey() <= oldStepThreshold) {
			mapIterator.remove();
		}
	}

	public long getLoggingInterval(String key) {
		return loggingStepInterval.get(key);
	}

	public Map<Long, Double> getCollectedValues(String key) {
		return quantitiesOfInterestLogs.get(key);
	}
	
	public void resetPubVisitorCount() {
		pubVisitCount = 0;
	}

	public void incrementPubVisitCount() {
		pubVisitCount++;
	}

	public long getPubVisitCount() {
		return pubVisitCount;
	}

	public void captureInteractions(Meeting meeting, long step) {
		Long[] participants = meeting.getParticipants().toArray(new Long[0]);

		// capture every single possible combinations
		for (int i = 0; i < participants.length - 1; i++) {
			for (int j = i + 1; j < participants.length; j++) {

				// agent 1 always keeps smaller id
				long agent1 = Math.min(participants[i], participants[j]);
				long agent2 = Math.max(participants[i], participants[j]);

				// do we already have this interaction pair captured?
				// we know that InteractionEndStep would be one step before if
				// this interaction was continuing
				AgentInteraction interaction = agentInteractions
						.stream()
						.filter(p -> p.getAgent1() == agent1
								&& p.getAgent2() == agent2
								&& p.getInteractionEndStep() == step - 1)
						.findFirst().orElse(null);

				if (interaction == null) { // means there was no such interaction
					// we create a new one
					interaction = new AgentInteraction(agent1, agent2, step);
					agentInteractions.add(interaction);
				}

				interaction.setInteractionEndStep(step);
			}
		}
	}

	public List<AgentInteraction> getAgentInteractions() {
		return agentInteractions;
	}
	
	@Override
	protected void initializationWithDefaultValues() {
		avgNetworkDegree = 0.0;
		avgBalance = 0;
		percentageUnhappy = 0;
		pubVisitPerAgent = 0;
		numOfSocialInteractions = 0;
	}
	
	public String toString() {
		return "QOI";
	}
}
