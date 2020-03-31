package edu.gmu.mason.vanilla.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import edu.gmu.mason.vanilla.Person;

/**
 * General description_________________________________________________________
 * This class is a specialized PriorityQueue to be used in adding instructions
 * at the agent-level
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class InstructionQueue extends PriorityQueue<AgentInstruction> {

	
	private static final long serialVersionUID = -6817933677139171356L;
	
	private InstructionQueue(Comparator<AgentInstruction> comparator) {
		super(comparator);
	}
	
	@Override
	public boolean add(AgentInstruction element) {
		if (this.isEmpty() == false && element.isOnlyAllowOneInstanceAtATime() && instructionTypeExists(element)) {
			// now we check whether any instance of this instruction is available
			return false;
		}
		return super.add(element);
	}
	
	public InstructionQueue() {
		this(new InstructionComparator());
	}
	
	static class InstructionComparator implements Comparator<AgentInstruction>, Serializable {
		private static final long serialVersionUID = 1728272137520879941L;

		@Override
		public int compare(AgentInstruction o1, AgentInstruction o2) {
			int a = o1.getPriority();
			int b = o2.getPriority();
			
			if (a==b){
				// tie breaker works by comparing class names alphabetically
				return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
			}
			return a-b;
		}
	}
	
	/**
	 * This method goes through all instructions and executes one if that satisfies the precondition
	 * @param agent
	 */
	public void processElements(Person agent) {
		List<AgentInstruction> list = new ArrayList<>();
		AgentInstruction instruction;
		while ( (instruction = this.poll()) != null) {
			if (instruction.preCondition(agent) == true) {
				instruction.planOfActions(agent);
				break;
			}
			list.add(instruction);
		}
		if (list.isEmpty() == false) {
			this.addAll(list);
		}
	}
	
	public boolean instructionTypeExists(AgentInstruction element) {
		return this.stream().anyMatch(p -> p.getClass() == element.getClass());
	}

}
