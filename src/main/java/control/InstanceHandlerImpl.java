package control;

import data.Bid;
import data.Edge;
import data.EdgeFlow;
import data.EdgeInfo;
import data.TransportationInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lp.SolverStub;

public class InstanceHandlerImpl implements InstanceHandler{

	private Map<String, Double> payoffMap;
	private Map<Edge, List<String>> edgeMap;
	private TransportationInstance problem;
	
	@Override
	public void init(List<String> players, TransportationInstance instance) {
		
		problem = instance;
		
		edgeMap = new HashMap<Edge, List<String>> ();
		payoffMap = new HashMap<String, Double>();
		for(Edge e : problem.getEdges()) {
			edgeMap.put(e, new ArrayList<String>());
		}
		for(String player : players) {
			payoffMap.put(player, 0.0);
		}
	}

	@Override
	public Map<Edge,EdgeInfo> solve(List<Bid> bids) {
		subtractFixedCostPayoff(bids);
		Map<Edge,EdgeInfo> results = computeResults(bids);
		addFlowRewardPayoffs(results);
		return results;
	} 
	
	private Map<Edge,EdgeInfo> computeResults(List<Bid> bids) {
		Map<Edge, EdgeInfo> eInfo = new HashMap<Edge, EdgeInfo>();
		
		Collections.sort(bids);
		for(Bid b : bids) {
			Edge e = problem.getEdge(b.getSource(), b.getSink());
			if(!eInfo.containsKey(e)) {
				EdgeInfo inf = new EdgeInfo();
				inf.setVarCost(b.getBid());
				e.setVarCost(b.getBid());
				inf.setOwner(b.getOwner());
				eInfo.put(e, inf);
			}
			EdgeInfo inf = eInfo.get(e);
			inf.setNbids(inf.getNbids()+1);
		}
		
		//Solve transportation problem
		TPSolver solver = new SolverStub();
		List<EdgeFlow> eFlows = solver.solve(problem);
		
		
		for(EdgeFlow flow : eFlows) {
			Edge e = problem.getEdge(flow.getSource(), flow.getSink());
			if(eInfo.containsKey(e)) {
				eInfo.get(e).setFlow(flow.getFlow());
			}
		}
		
		return eInfo;
	}
	
	
	private void addFlowRewardPayoffs(Map<Edge,EdgeInfo> results) {
		for(EdgeInfo info : results.values()) {
			String owner = info.getOwner();
			double flow = info.getFlow();
			double bid = info.getVarCost();
			
			double p = payoffMap.get(owner);
			payoffMap.put(owner, p + flow*bid);
		}
	}
	
	private void subtractFixedCostPayoff(List<Bid> bids) {
		Map<Edge, List<String>> newBidders = new HashMap<Edge,List<String>>();
		
		for(Bid b : bids) {
			Edge e = problem.getEdge(b.getSource(),b.getSink());
			if(!newBidders.containsKey(e)) {
				newBidders.put(e, new ArrayList<String>());
			}
			newBidders.get(e).add(b.getOwner());
		}
		
		for(Edge e: newBidders.keySet()) {
			newBidders.get(e).removeAll(edgeMap.get(e));
		}
		
		for(Edge e : newBidders.keySet()) {
			int n = newBidders.get(e).size();
			int m = edgeMap.get(e).size();
			double C = e.getFixedCost()/(double)(n+m);
			for(String player : newBidders.get(e)) {
				double p1 = payoffMap.get(player);
				payoffMap.put(player, p1 - C);
				for(String oldPlayer : edgeMap.get(e)) {
					double p2 = payoffMap.get(oldPlayer);
					payoffMap.put(oldPlayer, p2 + C/m);
				}
			}
		}
		
		for(Edge e : newBidders.keySet()) {
			edgeMap.get(e).addAll(newBidders.get(e));
		}
	}

	@Override
	public Map<String, Double> getPayoffMap() {
		return payoffMap;
	}

}
