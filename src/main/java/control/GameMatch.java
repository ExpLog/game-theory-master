package control;

import data.Edge;
import data.EdgeInfo;
import data.Bid;
import data.TransportationInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameMatch {
	private InstanceHandler handler;
	private List<String> players;
	private TransportationInstance instance;
	private int edgesPerRound;
	
	public void init(List<String> players, TransportationInstance instance) {
		this.players = players;
		this.instance = instance;
		handler = new InstanceHandlerImpl();
		handler.init(players, instance);
	}
	
	protected abstract int edgesPerRound(TransportationInstance instance, int nplayers, int rounds);
	protected abstract void send(List<String> players, String message);
	protected abstract String makeStartMessage(TransportationInstance instance, int edgesPerRound);
	protected abstract String makeResultMessage(Map<Edge,EdgeInfo> resultsInfo);
	protected abstract String makeEndMessage(Map<String, Double> payoffMap);
	protected abstract List<Bid> receiveAnswer(List<String> players);
	
	protected TransportationInstance getInstance() {
		return instance;
	}

	protected int getEdgesPerRound() {
		return edgesPerRound;
	}

	protected InstanceHandler getHandler() {
		return handler;
	}

	protected List<String> getPlayers() {
		return players;
	}
	
	public Map<String, Double> start(int nrounds) {
		firstRound(nrounds);
		return handler.getPayoffMap();
	}

	private void firstRound(int nrounds) {
		edgesPerRound = edgesPerRound(instance, players.size(),nrounds);
		send(players, makeStartMessage(instance, edgesPerRound));
		List<Bid> bids = filter(receiveAnswer(players));
		Map<Edge,EdgeInfo> results = handler.solve(bids);
		nextRound(results,nrounds-1);
	}

	private void nextRound(Map<Edge,EdgeInfo> results, int nrounds) {
		if(nrounds == 0) {
			end();
			return;
		}
		send(players,makeResultMessage(results));
		List<Bid> bids = filter(receiveAnswer(players));
		results = handler.solve(bids);
		nextRound(results,nrounds-1);
	}

	private void end() {
		send(players, makeEndMessage(handler.getPayoffMap()));
	}

	private List<Bid> filter(List<Bid> bids) {
		Map<String, Integer> bidCounter = new HashMap<String, Integer>();
		for(String s : players) {
			bidCounter.put(s, 0);
		}
		List<Bid> treatedBids = new ArrayList<Bid>();
		for(Bid b : bids) {
			int c = bidCounter.get(b.getOwner());
			if(c < edgesPerRound) {
				bidCounter.put(b.getOwner(), c+1);
				treatedBids.add(b);
			}
		}
		return treatedBids;
	}
}
