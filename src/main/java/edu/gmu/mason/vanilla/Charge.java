package edu.gmu.mason.vanilla;

import org.joda.time.LocalDateTime;

/**
 * General description_________________________________________________________
 * A simple data structure class to represent agent spending items.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class Charge implements java.io.Serializable {
	private static final long serialVersionUID = -1217868600107384068L;
	private ExpenseType expenseType;
	private double amount;
	private LocalDateTime date;
	
	public Charge(ExpenseType expenseType, double amount, LocalDateTime date) {
		this.expenseType = expenseType;
		this.amount = amount;
		this.date = date;
	}
	
	public ExpenseType getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(ExpenseType expenseType) {
		this.expenseType = expenseType;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
}
