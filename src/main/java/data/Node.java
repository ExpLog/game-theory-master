package data;

public class Node {
	private int idx;
	private double amount;
	public Node(int idx, double amount) {
		this.idx = idx;
		this.amount = amount;
	}
	public int getID() {
		return idx;
	}
	public double getAmount() {
		return amount;
	}
	public String toString() {
		return idx + "," + amount;
	}
    public String getLabel(String prefix) { return prefix + idx; }
}
