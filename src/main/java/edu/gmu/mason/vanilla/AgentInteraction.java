package edu.gmu.mason.vanilla;

/**
 * General description_________________________________________________________
 * A simple data structure class to capture interactions between agents.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class AgentInteraction implements java.io.Serializable {
	private static final long serialVersionUID = -8031734082457483780L;
	private long agent1;
	private long agent2;
	private long interactionStartStep;
	private long interactionEndStep;

	public AgentInteraction(long agent1, long agent2, long startingStep) {
		this.agent1 = agent1;
		this.agent2 = agent2;
		this.interactionStartStep = startingStep;
		this.interactionEndStep = startingStep;
	}

	public long getInteractionStartStep() {
		return interactionStartStep;
	}

	public void setInteractionStartStep(long interactionStartStep) {
		this.interactionStartStep = interactionStartStep;
	}

	public long getInteractionEndStep() {
		return interactionEndStep;
	}

	public void setInteractionEndStep(long interactionEndStep) {
		this.interactionEndStep = interactionEndStep;
	}

	public long getAgent1() {
		return agent1;
	}

	public void setAgent1(long agent1) {
		this.agent1 = agent1;
	}

	public long getAgent2() {
		return agent2;
	}

	public void setAgent2(long agent2) {
		this.agent2 = agent2;
	}

}
