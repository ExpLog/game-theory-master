package test;

import control.GameMatch;
import data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameMatchImpl extends GameMatch{
	private int edges;

	@Override
	protected int edgesPerRound(TransportationInstance instance, int nplayers, int rounds) {
		this.edges =(int)((instance.getEdges().size()*0.4)/(nplayers*rounds));
		return edges;
	}

	@Override
	protected void send(List<String> players, String message) {
		System.out.println("\nsending message:\n" + message + "\nto players: " + Arrays.toString(players.toArray()));
	}

	@Override
	protected String makeStartMessage(TransportationInstance instance, int edgesPerRound) {
		
		return "START " + instance.getName() + "," + edgesPerRound;
	}

	@Override
	public String makeResultMessage(Map<Edge,EdgeInfo> resultsInfo) {
		StringBuilder results = new StringBuilder("RESULTS\n");
		for(Edge e : resultsInfo.keySet()) {
			results.append(e.getSourceId() + ", " + e.getSinkId() + ", " + resultsInfo.get(e).csv() + "\n");
		}
		return results.toString();
	}

	@Override
	protected String makeEndMessage(Map<String, Double> payoffMap) {
		return "END OF GAME";
	}

	@Override
	protected List<Bid> receiveAnswer(List<String> players) {
		//Random Bids on Random Edges
		List<Bid> bids = new ArrayList<Bid>();
		List<Node> sources = getInstance().getSources();
		List<Node> sinks = getInstance().getSinks();
		Random rand = new Random();
		
		int edgesPerRound = getEdgesPerRound();
		
		for(String player : getPlayers()) {
			int count = rand.nextInt(edgesPerRound);
			for(int i = 0; i < count; i++) {
				int source = rand.nextInt(sources.size());
				int sink = rand.nextInt(sinks.size());
				int sourceId = sources.get(source).getID();
				int sinkId = sinks.get(sink).getID();
				Edge e = getInstance().getEdge(sourceId, sinkId);
				double cost = rand.nextDouble() * e.getVarCost();
				bids.add(new Bid(player, sourceId, sinkId,cost));
			}
		}
		
		return bids;
	}

}
