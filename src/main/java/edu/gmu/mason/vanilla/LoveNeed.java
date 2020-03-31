package edu.gmu.mason.vanilla;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.joda.time.LocalDateTime;

import edu.gmu.mason.vanilla.environment.Pub;
import edu.gmu.mason.vanilla.environment.Travel;
import edu.gmu.mason.vanilla.log.Skip;
import edu.gmu.mason.vanilla.log.State;
import edu.gmu.mason.vanilla.utils.CollectionUtil;
import edu.gmu.mason.vanilla.utils.MersenneTwisterWrapper;
import edu.gmu.mason.vanilla.utils.NetworkUtil;
import sim.field.network.Edge;
import sim.field.network.Network;

/**
 * General description_________________________________________________________
 * A class to handle love need functions such as making friendship, breaking up,
 * choosing a social site to visit and so on. Each agent has one
 * {@code LoveNeed} object.
 *
 * * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class LoveNeed implements Need, java.io.Serializable {
	private static final long serialVersionUID = -8547750305861366811L;
	// these two numbers represent stability of person's network.

	@Skip
	private Person agent;
	@State
	private Long meetingId = null;
	/**
	 * this variable is used to represent the status of the love need based on
	 * number of friends
	 */
	@State
	private LoveNeedStatus status;
	/**
	 * this variable is used to represent the status of the love need based
	 * temporal friendship-making and breaking-up events.
	 */
	//// Sociality Status
	@State
	private double socialStatus;
	@Skip
	private Queue<Double> socialStatusQueue;
	/**
	 * this variable is used to represent the status of the love need based
	 * temporal friendship-making and breaking-up events.
	 */
	@State
	private double socialHappiness;

	public LoveNeed(Person agent) {
		this.agent = agent;
		this.status = LoveNeedStatus.NA;
		this.socialStatusQueue = new CircularFifoQueue<>(
				agent.getModel().params.numberOfDaysToConsiderForMeasuringSocialStatus);
		this.socialStatus = agent.getModel().params.initialSocialStatusWeight;
	}

	/**
	 * Tells whether enough time passed to measure social status
	 * 
	 * @return {@code true} if measures, {@code false} otherwise
	 */
	private boolean isSocialStatusMeasured() {
		return this.socialStatusQueue.size() == agent.getModel().params.numberOfDaysToConsiderForMeasuringSocialStatus;
	}

	/**
	 * Updates both love need statuses.
	 */
	@Override
	public void update() {
		WorldParameters params = agent.getModel().params;
		Network network = agent.getModel().getFriendFamilyNetwork();
		int size = network.getEdgesOut(agent.getAgentId()).size();

		if (size <= 3) {
			this.status = LoveNeedStatus.Awful;
		} else if (size <= 6) {
			this.status = LoveNeedStatus.Unsatisfactory;
		} else if (size <= 9) {
			this.status = LoveNeedStatus.OK;
		} else if (size <= 15) {
			this.status = LoveNeedStatus.Good;
		} else if (size > 25) {
			this.status = LoveNeedStatus.Perfect;
		}

		socialStatus *= params.socialStatusDecayFactor;
		
		if (socialStatus > params.maxSocialStatusValue) {
			socialStatus = params.maxSocialStatusValue;
		} else if (socialStatus < params.minSocialStatusValue) {
			socialStatus = params.minSocialStatusValue;
		}
		socialStatusQueue.add(socialStatus);
	}

	@Override
	public void satisfy() {

		// skip if agent is not alive or transporting
		if (agent.getLifeStatus() != LifeStatus.Alive
				|| agent.getCurrentMode() == PersonMode.Transport) {
			return;
		}

		PersonMode currentMode = agent.getCurrentMode();
		DailyPlan dailyPlanForToday = agent.getTodaysPlan();
		WorldModel model = agent.getModel();

		// if all other needs are satisfied and agent is free, it goes to a bar
		// if needs new friends
		
		if (
				agent.physiologicalNeedsSatisfied() 
				&& agent.getFinancialSafetyNeed().isSatisfied() == true
				&& isSatisfied() == false
				&& currentMode != PersonMode.AtRecreation
				&& (dailyPlanForToday.isWorkDay() == false || (dailyPlanForToday.isWorkDay() == true && 
					dailyPlanForToday.cameBackFromWork()))) {
			
			double usableBudget = agent.getFinancialSafetyNeed().getWeeklyExtraBudget();

			// if usable budget is available
			if (usableBudget > 0) {
				// get nearby pubs
				//List<Pub> pubs = agent.getCurrentUnit() //model.params.numberOfNearestPubs
				Map<Pub, Double> pubDistanceList = agent.getCurrentUnit().getNearestPubDistanceMap(model.params.numberOfNearestPubs);
				
				if (pubDistanceList != null && pubDistanceList.size() > 0) {

					VisitReason reason = VisitReason.None;
					// this variable keeps the pub to go
					Pub pubToGo = null;

					if (pubDistanceList.size() == 1) {
						pubToGo = pubDistanceList.keySet().iterator().next();
						reason = VisitReason.Bar_ItWasTheOnlyChoice;
					}

					Map<Long, Double> scoreMap = new LinkedHashMap<>();

					// calculate each pub's score based on model coefficients
					// and pub profile's similarity with the agent
					for (Entry<Pub, Double> entry: pubDistanceList.entrySet()) {
						PubChoiceSimilarity choiceSimilarity = getPubSimilarity(entry);

						double score = 
								model.params.pubChoiceClosenessCoefficient * choiceSimilarity.closeness + 
								
								model.params.pubChoiceAgeSimilarityCoefficient * choiceSimilarity.age + 
								
								model.params.pubChoiceIncomeSimilarityCoefficient * choiceSimilarity.income + 
								
								model.params.pubChoiceInterestSimilarityCoefficient * choiceSimilarity.interest;

						scoreMap.put(entry.getKey().getId(), score);
					}

					// sort the map based on scores
					scoreMap = scoreMap
							.entrySet()
							.stream()
							.sorted(Map.Entry.comparingByValue(Comparator
									.reverseOrder()))
							.collect(
									Collectors.toMap(Map.Entry::getKey,
											Map.Entry::getValue, (oldValue,
													newValue) -> oldValue,
											LinkedHashMap::new));

					int index = 1;

					// Apply a power function to pub scores. This is inspired by
					// empirical mobility studies that finds Zipf's Law in top
					// place choice.
					for (java.util.Map.Entry<Long, Double> entry : scoreMap
							.entrySet()) {
						entry.setValue(Math.pow(
								2,
								Math.exp(entry.getValue()
										* model.params.pubChoiceExponentialDecayConstant)));
					}

					// We use the scores to calculate a probabilities and choose
					// one randomly.
					MersenneTwisterWrapper rng = new MersenneTwisterWrapper(
							model.random);

					int[] singletons = new int[scoreMap.size()];
					double[] probabilities = new double[scoreMap.size()];

					index = 0;
					for (java.util.Map.Entry<Long, Double> entry : scoreMap
							.entrySet()) {
						singletons[index] = entry.getKey().intValue();
						probabilities[index++] = entry.getValue();
					}

					EnumeratedIntegerDistribution distribution = null;

					try {
						distribution = new EnumeratedIntegerDistribution(rng,
								singletons, probabilities);
					} catch (Exception exp) {
						exp.printStackTrace();
					}

					int chosenPubId = distribution.sample();

					for (Entry<Pub, Double> entry: pubDistanceList.entrySet()) {
						if (entry.getKey().getId() == chosenPubId) {

							pubToGo = entry.getKey();
							PubChoiceSimilarity similarity = getPubSimilarity(entry);
							reason = similarity.getReason();
							break;
						}
					}

					if (pubToGo != null) {
						// get a visit length between min and max minutes as
						// specified in model parameters.

						double rate = agent.getModel().random.nextGaussian();

						// bounding the Gaussian between -3 and 3
						if (rate < -3) {
							rate = -3;
						} else if (rate > 3) {
							rate = 3;
						}

						// shifting to right by 3, so the value is between 0 and
						// 6.
						rate += 3;

						// normalizing it it between 0 and 1
						rate /= 6.0;

						int visitLength = model.params.minimumSiteVisitLengthInMinutes
								+ (int) (rate * (model.params.maximumSiteVisitLengthInMinutes - model.params.minimumSiteVisitLengthInMinutes));
						double visitLengthInHours = visitLength / 60.0;

						if (pubToGo != null
								&& visitLengthInHours * pubToGo.getHourlyCost() < usableBudget) {

							Travel travel = new Travel(agent.getLocation(),
									pubToGo.getLocation(), visitLength, reason);
							
							agent.getMobility().beginToTransport(travel,
									PersonMode.AtRecreation, pubToGo, true);
							return;
						}
					}
				}
			}

		}
		
		else if (currentMode == PersonMode.AtRecreation) {
			// agent is at recreational places like pubs
			tryExpandingNetwork(false);

			// agent stays certain time between based on a budget
			if (agent.getCurrentUnit().isTimeToLeaveForAgent(agent) == true) {
				// send the agent back to home
				agent.travelToHome(VisitReason.Home_ComingBackFromPub);
			}
		} else if (currentMode == PersonMode.AtRestaurant) {
			tryExpandingNetwork(true);
		}

		// if agents are at home while roommate is there, strengthen their
		// connection.
		else if (currentMode == PersonMode.AtHome) {
			strengthenRoommateConnection();
		}
	}

	public void lostFriend() {
		socialStatus -= agent.getModel().params.socialStatusDecreaseValue;
	}

	public void madeNewFriend() {
		socialStatus += agent.getModel().params.socialStatusIncreaseValue;
	}

	/**
	 * This method arranges agent meetings and make agents interact
	 * 
	 * @param useOnlyExistingFriends
	 */
	public void tryExpandingNetwork(boolean useOnlyExistingFriends) {

		if (meetingNow() == true) { // there is a meeting going on, expand the
									// network
			strengthenMeetingTies();
			return;
		}

		// check if there is any agents in this place that the agent is already
		// connected
		Network familyFriendNetwork = agent.getModel().getFriendFamilyNetwork();
		Network workNetwork = agent.getModel().getWorkNetwork();
		boolean useWorkNetwork = agent.getModel().params.useWorkNetwork;

		List<Person> currentAgents = new ArrayList<>(agent.getCurrentUnit()
				.getCurrentAgents()); // get current agents in the building

		currentAgents.remove(agent); // remove itself from the list

		if (currentAgents.size() > 0) { // if there are more than one except for
										// this agent.

			CollectionUtil.shuffle(currentAgents, agent.getModel().random);

			// go through current agents one by one
			for (Person p : currentAgents) {

				// if the current agent is already a friend
				if (familyFriendNetwork.getEdge(agent.getAgentId(),
						p.getAgentId()) != null
						|| (workNetwork.getEdge(agent.getAgentId(),
								p.getAgentId()) != null && useWorkNetwork)) { // these
																				// two
																				// agents
																				// are
																				// already
																				// connected
																				// or
																				// work
																				// friends
					// check if there is an active meeting with this agent and
					// others
					if (p.getLoveNeed().meetingNow()) {
						Meeting meeting = p.getLoveNeed().getMeeting();
						
						if (meeting != null && meeting.size() <= agent.getModel().params.maxGroupMeetingSize) {
							
							p.getLoveNeed().getMeeting().addParticipant(agent.getAgentId());
							this.meetingId = p.getLoveNeed().getMeetingId();
							break; // assume that agent only saw the above agent
									// in the building
						}
						// if the meeting size is too high, agent will go and
						// see if any other friend agents are there
					} else {
						// we need to create the meeting and add both agents to
						// it
						LocalDateTime simTime = agent.getModel()
								.getSimulationTime();
						Long meetingId = agent.getCurrentUnit()
								.createNewMeeting(false, agent.getAgentId(),
										p.getAgentId(), simTime);

						this.meetingId = meetingId;
						p.getLoveNeed().setMeetingId(meetingId);

						break;
					}
				}
			}

			// If the agent is bound to use only existing friends
			// (useOnlyExistingFriends=true) to chat, the following condition is
			// skipped
			// This condition is set to false in restaurants.
			if (useOnlyExistingFriends == false) {
				// let's check if agent is still not in a meeting
				if (meetingNow() == false) {
					// now let's pick a person and try to connect with it

					Person agentToConnect = currentAgents.get(0); // assume that
																	// this is
																	// the
																	// agent,
																	// let's
																	// check if
																	// they have
																	// common
																	// friends
					List<Long> commonFriends = NetworkUtil
							.getCommmonFriendNodes(familyFriendNetwork,
									agent.getAgentId(),
									agentToConnect.getAgentId());
					double chance;

					if (commonFriends.size() > 0) { // there is a more
													// likelihood that they will
													// connect when they have
													// common friends
						chance = agent.getModel().params.cyclicClosureProbability;
					} else {
						chance = agent.getModel().params.focalClosureProbability;
					}

					if (agent.getModel().random.nextDouble() < chance) { // let's
																			// connect
																			// these
																			// two
																			// agents
						// only single agents meet with random people
						if (agentToConnect.getLoveNeed().meetingNow()) {
							
							if(agentToConnect.getLoveNeed().getMeeting().size() <= agent.getModel().params.maxGroupSizeToJoinForAloneAgents) {
							
								agentToConnect.getLoveNeed().getMeeting()
									.addParticipant(agent.getAgentId()); // we
																			// put
																			// them
																			// into
																			// the
																			// same
																			// meeting,
																			// they
																			// will
																			// be
																			// connected
																			// below.
								this.meetingId = agentToConnect.getLoveNeed()
									.getMeetingId();
							}
						} else {
							// we need to create the meeting and add both agents
							// to it
							LocalDateTime simTime = agent.getModel()
									.getSimulationTime();
							Long meetingId = agent.getCurrentUnit()
									.createNewMeeting(false,
											agent.getAgentId(),
											agentToConnect.getAgentId(),
											simTime);

							this.meetingId = meetingId;
							agentToConnect.getLoveNeed()
									.setMeetingId(meetingId);

						}
					}
				}
			}

			if (meetingNow() == true) { // agent finally joined a meeting. let's
										// strengthen ties or create new ones
				strengthenMeetingTies();
			}
		}

	}

	// this method leads to new friendship or strengthen 
	private void strengthenMeetingTies() {
		List<Long> agentIds = getMeeting().getParticipants();

		if (agentIds.size() > 1) {

			// the following nested loop cycles through all possible
			// combinations between all agents meeting
			for (int i = 0; i < agentIds.size(); i++) {
				if (agentIds.get(i) != agent.getAgentId()) {
					Long agentId = agentIds.get(i);
					try {
						agent.getModel().getAgent(agentId).getLoveNeed()
								.strengthenTies(agent.getAgentId());
					} catch (Exception e) {
						System.out.print(agent.getSimulationTime());
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * This method creates a connection for non-connected nodes. If they are
	 * already connected, it strengthens the ties.
	 * 
	 * @param agentToConnect
	 */
	public void strengthenTies(Long agentIdToConnect) {
		Network familyFriendNetwork = agent.getModel().getFriendFamilyNetwork();

		if (familyFriendNetwork.getEdge(agent.getAgentId(), agentIdToConnect) != null) { // strengthen
			Edge edge = familyFriendNetwork.getEdge(agent.getAgentId(),
					agentIdToConnect);
			double y = (double) edge.info;
			double newWeight = y + agent.getModel().params.networkEdgeWeightStrengtheningRate 
					* (1 - y / WorldParameters.NETWORK_WEIGHT_UPPER_BOUND);
			// making sure that it does not go over the upper bound
			newWeight = Math.min(newWeight, WorldParameters.NETWORK_WEIGHT_UPPER_BOUND);
			// GT: node "Friendship Strength"; 1 of 2; next 1 lines
			familyFriendNetwork.updateEdge(edge, edge.from(), edge.to(), newWeight);
		} else {
			
			// create a new connection
			familyFriendNetwork.addEdge(agent.getAgentId(), agentIdToConnect,
					agent.getModel().params.initialNetworkEdgeWeight);
			madeNewFriend();

			String me = String.valueOf(agent.getAgentId());
			String other = String.valueOf(agentIdToConnect);

			if (agent.getModel().getVisualFriendFamilyGraph() != null) {
				agent.getModel().getVisualFriendFamilyGraph()
						.addEdge(me + "--" + other, me, other, true);
			}
		}

	}

	// Sociality Satisfied
	@Override
	public boolean isSatisfied() {
		return socialHappiness > agent.getJoviality();
	}

	//// 
	public void strengthenRoommateConnection() {
		List<Person> roommates = new ArrayList<>(agent.getCurrentUnit()
				.getCurrentAgents()); // get current agents at home
		roommates.remove(agent); // remove itself

		for (Person aRoommate : roommates) {
			// interact with only awake roommate
			if(aRoommate.getSleepNeed().getStatus() == SleepStatus.Awake)
				strengthenTies(aRoommate.getAgentId());
		}
	}

	public Meeting getMeeting() {
		return agent.getCurrentUnit().getMeeting(meetingId);
	}

	public boolean meetingNow() {
		return meetingId != null;
	}

	public LoveNeedStatus getStatus() {
		return status;
	}

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

	public double getSocialStatus() {
		return socialStatus;
	}

	public void setSocialHappiness(double socialHappiness) {
		this.socialHappiness = socialHappiness;
	}

	private PubChoiceSimilarity getPubSimilarity(Entry<Pub, Double> entry) {
		
		double distance = entry.getValue();

		double closeness = 1 - distance / 10000;
		closeness = closeness < 0 ? 0 : closeness; // underflow protection
		PubChoiceSimilarity similarity;

		if (entry.getKey().getVisitorProfile() == null) { // if visitor profile is not yet
												// created for this bar, set all
												// coefficient values as zero.
			similarity = new PubChoiceSimilarity(0, closeness, 0, 0);

		} else {
			
			// here we calculate values to be used in the formula
			double ageDifference = Math.abs(agent.getAge()
					- entry.getKey().getVisitorProfile().getAverageAge());
			double ageSimilarity = 1 - ageDifference / 25;
			ageSimilarity = ageSimilarity < 0 ? 0 : ageSimilarity; // underflow
																	// protection
			double incomeDifference = entry.getKey().getVisitorProfile()
					.getAverageIncome(); // assuming agent might be unemployed
			if (agent.getFinancialSafetyNeed().isEmployed() == true) {
				incomeDifference = Math.abs(agent.getFinancialSafetyNeed()
						.getJob().getHourlyRate()
						- incomeDifference);
			}
			double incomeSimilarity = 1 - incomeDifference / 100;
			incomeSimilarity = incomeSimilarity < 0 ? 0 : incomeSimilarity; // underflow
																			// protection

			
			double interestSimilarity = 0; // assuming no common interest
			List<AgentInterest> topInterests = entry.getKey().getVisitorProfile()
					.getInterests();
			for (int i = 1; i <= topInterests.size(); i++) {
				if (topInterests.get(i - 1) == agent.getInterest()) { // if this
																		// bar
																		// profile's
																		// top
																		// visitor
																		// interests
																		// are
																		// similar
					// set similarity from top to bottom accordingly
					interestSimilarity = 1.0 / topInterests.size()
							* (topInterests.size() + 1 - i);
					break;
				}
			}

			similarity = new PubChoiceSimilarity(ageSimilarity, closeness,
					incomeSimilarity, interestSimilarity);
		}

		return similarity;
	}
	
	public void kill() {
		this.agent = null;
		this.meetingId = null;
		this.socialStatusQueue = null;
	}

	/**
	 * An internal class to capture visit reason based on similarity scores.
	 * 
	 * @author Hamdi Kavak (hkavak at gmu.edu)
	 *
	 */
	protected class PubChoiceSimilarity {
		public double age;
		public double closeness;
		public double income;
		public double interest;

		public PubChoiceSimilarity(double age, double closeness, double income,
				double interest) {
			this.age = age;
			this.closeness = closeness;
			this.income = income;
			this.interest = interest;
		}

		public VisitReason getReason() {

			Map<VisitReason, Double> reasonValue = new HashMap<>();

			reasonValue.put(VisitReason.Bar_Proximity, closeness);
			reasonValue.put(VisitReason.Bar_SimilarAge, age);
			reasonValue.put(VisitReason.Bar_SimilarIncome, income);
			reasonValue.put(VisitReason.Bar_SimilarInterest, interest);

			List<Map.Entry<VisitReason, Double>> shuffledEntries = new ArrayList<>(
					reasonValue.entrySet());
			CollectionUtil.shuffle(shuffledEntries, agent.getModel().random);

			VisitReason reason = VisitReason.None;
			double maxValue = Double.MIN_VALUE;

			for (Map.Entry<VisitReason, Double> entry : shuffledEntries) {
				if (entry.getValue() > maxValue) {
					maxValue = entry.getValue();
					reason = entry.getKey();
				}
			}

			return reason;
		}

	}
}
