package control;

import data.Bid;
import data.Edge;
import data.EdgeInfo;
import data.TransportationInstance;

import java.util.List;
import java.util.Map;

public interface InstanceHandler {
	
	public void init(List<String> players, TransportationInstance instance);
	public Map<Edge,EdgeInfo> solve(List<Bid> bids);
	public Map<String, Double> getPayoffMap();
}
