package data;

public class EdgeInfo {
	private int nBids;
	private double varCost, flow;
	private String owner;
	
	public EdgeInfo(){
		
	}
	
	public void setNbids(int nbids) {
		this.nBids = nbids;
	}

	public void setVarCost(double varCost) {
		this.varCost = varCost;
	}

	public void setFlow(double flow) {
		this.flow = flow;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getNbids() {
		return nBids;
	}
	public double getVarCost() {
		return varCost;
	}
	public double getFlow() {
		return flow;
	}
	public String getOwner() {
		return owner;
	}
	public String toString() {
		return owner + " #nBids = " + nBids + " #varCost = " + varCost + " #flow = " + flow; 
	}
	public String csv() {
		return owner + ", " + nBids + ", " + varCost + ", " + flow;
	}
	
}
