package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.Bid;
import data.Edge;
import data.EdgeFlow;
import data.EdgeInfo;
import data.EdgePair;
import data.TransportationInstance;
import lp.SolverStub;

public class InstanceHandlerImpl implements InstanceHandler{

	//mapa de pessoas que apostaram na aresta com bid minimo
	private Map<Edge, List<String>> edgeWinners;
	//mapa de bids minimos nas arestas
	private Map<Edge, Double> edgeBids;
	//mapa de dono-lucro bruto
	private Map<String, Double> payoffMap;
	//mapa de pessoas que apostaram na aresta
	private Map<Edge, List<String>> edgeOwners;
	//instancia do problema
	private TransportationInstance problem;

	@Override
	public Map<String, Double> getPayoffMap() {
		return payoffMap;
	}

    @Override
    public TransportationInstance getInstance() {
        return problem;
    }
	
	@Override
	public void init(List<String> players, TransportationInstance instance) {

		problem = instance;

		edgeOwners = new HashMap<Edge, List<String>> ();
		payoffMap = new HashMap<String, Double>();
		for(Edge e : problem.getEdges()) {
			edgeOwners.put(e, new ArrayList<String>());
		}
		for(String player : players) {
			payoffMap.put(player, 0.0);
		}
		edgeWinners = new HashMap<Edge, List<String>>();
		edgeBids = new HashMap<Edge, Double>();
	}
	
	public Map<Edge, List<EdgeInfo>> solveTP(List<Bid> bids) {
		bids = filterRedundantBids(bids);
		subtractFixedCostPayoff(bids);
		Map<Edge,List<EdgeInfo>> results = computeResultsInfo(bids);
		addFlowReward(results);
		return results;
	}
	
	private Map<Edge,List<EdgeInfo>> computeResultsInfo(List<Bid> bids) {
		Map<Edge, List<EdgeInfo>> eInfo = new HashMap<Edge, List<EdgeInfo>>();
		Map<Edge, Integer> edgeBidCounter = new HashMap<Edge,Integer>();
		Collections.sort(bids);
		//apaga os vencedores da arestas do round anterior
		//uma vez que serao considerados novamente nesse round
		edgeWinners.clear();
		
		//pega um bid por aresta e computa informacao do vencedor
		for(Bid b : bids) {
			Edge e = problem.getEdge(b.getSource(), b.getSink());
			//se nao viu essa aresta ainda, aposta atual e menor, entao associa a aresta
			if(!eInfo.containsKey(e)) {
				//se nao tem jogadores que ganharam essa aresta cria uma lista
				if(!edgeWinners.containsKey(e)) {
					edgeWinners.put(e, new ArrayList<String>());
				}
				e.setVarCost(b.getBid());
				//adiciona esse apostador a lista de vencedores desta aresta
				edgeWinners.get(e).add(b.getOwner());
				//adiciona esse valor de aposta como vencedora para essa lista
				edgeBids.put(e, b.getBid());
			}
			
			if(!edgeBidCounter.containsKey(e)){
				edgeBidCounter.put(e, 0);
			}
			edgeBidCounter.put(e,edgeBidCounter.get(e)+1);
		}
		
		//checa por empates
		for(Bid b : bids) {
			Edge e = problem.getEdge(b.getSource(), b.getSink());
			//se alguem empatou o bid minimo em alguma aresta
			if(Math.abs(edgeBids.get(e) - b.getBid()) < Double.MIN_VALUE
					&& !edgeWinners.get(e).contains(b.getOwner())) {
				//adiciona essa pessoa na lista de vencedores
				edgeWinners.get(e).add(b.getOwner());
			}
		}
		

		//resolve transportation problem
		TPSolver solver = new SolverStub();
		List<EdgeFlow> eFlows = solver.solve(problem);


		//adiciona a informacao de fluxo nas arestas
		for(EdgeFlow flow : eFlows) {
			Edge e = problem.getEdge(flow.getSource(), flow.getSink());
            if(edgeWinners.containsKey(e)) {
            	eInfo.put(e, new ArrayList<EdgeInfo>());
            	double F = flow.getFlow()/(double)edgeWinners.get(e).size();
            	for(String winner : edgeWinners.get(e)) {
            		EdgeInfo info = new EdgeInfo();
            		info.setFlow(F);
            		info.setNbids(edgeBidCounter.get(e));
            		info.setOwner(winner);
            		info.setVarCost(e.getVarCost());
            		eInfo.get(e).add(info);
            	}
            }
		}

		return eInfo;
	}
	
	private void addFlowReward(Map<Edge,List<EdgeInfo>> results) {
		//pra cada aresta checa todos os empates e divide o fluxo entre os players que
		//apostaram com o bid minimo
		for(Edge e : results.keySet()) {
			List<EdgeInfo> info = results.get(e);
			
			for(EdgeInfo i : info) {
				double bid = i.getVarCost();
				double p = payoffMap.get(i.getOwner());
				payoffMap.put(i.getOwner(), p + i.getFlow()*bid);
			}
		}
	}
	
	private List<Bid> filterRedundantBids(List<Bid> bids) {
		bids = addOldBids(bids);
		Collections.sort(bids);
		List<Bid> filterBids = new ArrayList<Bid>();
		Map<String,Set<EdgePair>> edgesBidded = new HashMap<String, Set<EdgePair>>();
		
		//bids on crescent order of priceBid
		for(Bid bid : bids) {
			//if its the first bid of this player
			if(!edgesBidded.containsKey(bid.getOwner())) {
				//says it has bidded on some edges
				edgesBidded.put(bid.getOwner(), new HashSet<EdgePair>());
			}
			//check edges bidded by this player
			Set<EdgePair> ownedEdges = edgesBidded.get(bid.getOwner());
			//check current edge bidded
			EdgePair bidEdge = new EdgePair(bid.getSource(),bid.getSink());
			
			//if this player haven't bidded on this edge before
			if(!ownedEdges.contains(bidEdge)) {
				//add bid to the list
				ownedEdges.add(bidEdge);
				filterBids.add(bid);
			}
			//otherwise its a redundant bid
		}
		return filterBids;
	}
	
	private List<Bid> addOldBids(List<Bid> bids) {
		
		List<Bid> totalBids = new ArrayList<Bid>();
		List<Bid> oldBids = new ArrayList<Bid>();
		
		
		//adiciona as bids dos vencedores do round anterior
		for(Edge e : edgeWinners.keySet()) {
			for(String owner : edgeWinners.get(e)) {
				Bid b = new Bid(owner, e.getSourceId(),e.getSinkId(),edgeBids.get(e));
				oldBids.add(b);
			}
		}
		
		totalBids.addAll(bids);
		totalBids.addAll(oldBids);
		
		return totalBids;
	}
	
	private void subtractFixedCostPayoff(List<Bid> bids) {
		Map<Edge, List<String>> newBidders = new HashMap<Edge,List<String>>();

		//Pra cada aposta pega o dono e associa a aresta apostada
		for(Bid b : bids) {
			Edge e = problem.getEdge(b.getSource(),b.getSink());
			if(!newBidders.containsKey(e)) {
				newBidders.put(e, new ArrayList<String>());
			}
			newBidders.get(e).add(b.getOwner());
		}

		//seleciona apenas os novos apostadores para aquela aresta
		for(Edge e: newBidders.keySet()) {
			newBidders.get(e).removeAll(edgeOwners.get(e));
		}

		//redistribui o custo fixo entre os apostadores de cada aresta 
		for(Edge e : newBidders.keySet()) {
			int n = newBidders.get(e).size();
			int m = edgeOwners.get(e).size();
			double C = e.getFixedCost()/(double)(n+m);
			for(String player : newBidders.get(e)) {
				double p1 = payoffMap.get(player);
				payoffMap.put(player, p1 - C);
				for(String oldPlayer : edgeOwners.get(e)) {
					double p2 = payoffMap.get(oldPlayer);
					payoffMap.put(oldPlayer, p2 + C/m);
				}
			}
		}

		//adiciona os novos apostadores na lista de donos da aresta
		for(Edge e : newBidders.keySet()) {
			edgeOwners.get(e).addAll(newBidders.get(e));
		}
	}
	
	@Deprecated
	public Map<Edge,EdgeInfo> solve(List<Bid> bids) {
		bids = filterRedundantBids(bids);
		subtractFixedCostPayoff(bids);
		Map<Edge,EdgeInfo> results = computeResults(bids);
		addFlowRewardPayoffs(results);
		return results;
	} 

	@Deprecated
	private Map<Edge,EdgeInfo> computeResults(List<Bid> bids) {
		Map<Edge, EdgeInfo> eInfo = new HashMap<Edge, EdgeInfo>();
		Collections.sort(bids);
		//apaga os vencedores da arestas do round anterior
		//uma vez que serao considerados novamente nesse round
		edgeWinners.clear();
		
		//pega um bid por aresta e computa informacao do vencedor
		for(Bid b : bids) {
			Edge e = problem.getEdge(b.getSource(), b.getSink());
			//se nao viu essa aresta ainda, aposta atual e menor, entao associa a aresta
			if(!eInfo.containsKey(e)) {
				EdgeInfo inf = new EdgeInfo();
				inf.setVarCost(b.getBid());
				e.setVarCost(b.getBid());
                e.setOwner(b.getOwner());
				inf.setOwner(b.getOwner());
				//se nao tem jogadores que ganharam essa aresta cria uma lista
				if(!edgeWinners.containsKey(e)) {
					edgeWinners.put(e, new ArrayList<String>());
				}
				//adiciona esse apostador a lista de vencedores desta aresta
				edgeWinners.get(e).add(b.getOwner());
				//adiciona esse valor de aposta como vencedora para essa lista
				edgeBids.put(e, b.getBid());
				eInfo.put(e, inf);
			}

			EdgeInfo inf = eInfo.get(e);
			inf.setNbids(inf.getNbids()+1);
		}
		
		//checa por empates
		for(Bid b : bids) {
			Edge e = problem.getEdge(b.getSource(), b.getSink());
			//se alguem empatou o bid minimo em alguma aresta
			if(Math.abs(edgeBids.get(e) - b.getBid()) < Double.MIN_VALUE
					&& !edgeWinners.get(e).contains(b.getOwner())) {
				//adiciona essa pessoa na lista de vencedores
				edgeWinners.get(e).add(b.getOwner());
			}
		}
		

		//resolve transportation problem
		TPSolver solver = new SolverStub();
		List<EdgeFlow> eFlows = solver.solve(problem);


		//adiciona a informacao de fluxo nas arestas
		for(EdgeFlow flow : eFlows) {
			Edge e = problem.getEdge(flow.getSource(), flow.getSink());
            if(eInfo.containsKey(e)) {
                eInfo.get(e).setFlow(flow.getFlow());
            }
		}

		return eInfo;
	}

	@Deprecated
	private void addFlowRewardPayoffs(Map<Edge,EdgeInfo> results) {
		//pra cada aresta checa todos os empates e divide o fluxo entre os players que
		//apostaram com o bid minimo
		for(Edge e : results.keySet()) {
			List<String> owners = edgeWinners.get(e);
			EdgeInfo info = results.get(e);
			double flow = info.getFlow()/(double)owners.size();
			
			for(String owner : owners) {
				double bid = info.getVarCost();
				double p = payoffMap.get(owner);
                //maybe subtract the unitary cost per flow here
				payoffMap.put(owner, p + flow*bid);
			}
		}
	}
}
