package data;

public class EdgeFlow {
	private int source, sink;
	private double flow;
	
	public EdgeFlow(int source, int sink, double flow) {
		this.source = source;
		this.sink = sink;
		this.flow = flow;
	}
	
	public int getSource() {
		return source;
	}
	public int getSink() {
		return sink;
	}
	public double getFlow() {
		return flow;
	}
	public String toString() {
		return source + "," + sink + "(" + flow  +")";
	}
}
