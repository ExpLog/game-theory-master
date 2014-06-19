package data;

public class Bid implements Comparable<Bid>{
	private String owner;
	private double bid;
	private int source, sink;
	
	public Bid(String owner, int source, int sink, double bid) {
		this.owner = owner;
		this.bid = bid;
		this.source = source;
		this.sink = sink;
	}

	public String getOwner() {
		return owner;
	}

	public double getBid() {
		return bid;
	}

	public int getSource() {
		return source;
	}

	public int getSink() {
		return sink;
	}

	@Override
	public int compareTo(Bid o) {
		return (int)Math.signum(bid - o.bid);
	}

    @Override
    public String toString() {
        return owner+" "+source+" "+sink+" "+bid;
    }
}
