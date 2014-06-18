package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportationInstance {
	private String name;
	private List<Edge> edges;
	private List<Node> sources;
	private List<Node> sinks;
	private Map<EdgePair, Edge> edgeMap;
	private Map<Integer, List<Edge>> edgesFrom;
	private Map<Integer, List<Edge>> edgesTo;
	
	public TransportationInstance(String name, List<Node> sources, List<Node> sinks, List<Edge> edges) {
		this.name = name;
		this.sources = sources;
		this.sinks = sinks;
		this.edges = edges;
		edgeMap = new HashMap<EdgePair,Edge>();
		edgesFrom = new HashMap<Integer,List<Edge>>();
		edgesTo = new HashMap<Integer, List<Edge>>();
		for(Edge e : edges) {
			edgeMap.put(new EdgePair(e.getSourceId(), e.getSinkId()), e);
			if(!edgesFrom.containsKey(e.getSourceId())) {
				edgesFrom.put(e.getSourceId(), new ArrayList<Edge>());
			}
			if(!edgesTo.containsKey(e.getSinkId())) {
				edgesTo.put(e.getSinkId(), new ArrayList<Edge>());
			}
			edgesFrom.get(e.getSourceId()).add(e);
			edgesTo.get(e.getSinkId()).add(e);
		}
	}
	public String getName() {
		return name;
	}
	public List<Node> getSources() {
		return sources;
	}
	public List<Node> getSinks() {
		return sinks;
	}
	public List<Edge> getEdges() {
		return edges;
	}
	public Edge getEdge(int source, int sink) {
		return edgeMap.get(new EdgePair(source,sink));
	}
	public List<Edge> getEdgesFrom(int source) {
		return edgesFrom.get(source);
	}
	public List<Edge> getEdgesTo(int sink) {
		return edgesTo.get(sink);
	}
    public void augment(int k) {
        for(Edge e : getEdges()) {
            e.setVarCost(e.getVarCost() * k);
        }
    }
}
