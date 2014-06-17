package data;

public class Edge {
    public static final String MASTER_EDGE = "__MASTER__";
    private String owner;
	private int sourceId;
	private int sinkId;
	private double varCost;
	private double fixedCost;
	public Edge(int source, int sink, double varCost, double fixedCost) {
		this.sourceId = source;
		this.sinkId = sink;
		this.varCost = varCost;
		this.fixedCost = fixedCost;
        this.owner = MASTER_EDGE;
	}

    public Edge(String owner, int source, int sink, double varCost, double fixedCost) {
        this.owner = owner;
        this.sourceId = source;
        this.sinkId = sink;
        this.varCost = varCost;
        this.fixedCost = fixedCost;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

	public int getSourceId() {
		return sourceId;
	}
	public int getSinkId() {
		return sinkId;
	}
	public void setVarCost(double cost) {
		this.varCost = cost;
	}
	public double getVarCost() {
		return varCost;
	}
	public double getFixedCost() {
		return fixedCost;
	}
	public String toString() {
		return sourceId + "," + sinkId + "," + varCost + "," + fixedCost;
	}
    public String getName() { return sourceId + "," + sinkId; }
}
