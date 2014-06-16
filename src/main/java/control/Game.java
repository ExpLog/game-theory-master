package control;

import data.Edge;
import data.TransportationInstance;
import utils.InstanceConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	private File instancesDir;
	private GameMatch match;
	private List<String> players;
	
	public Game(File instancesDir, GameMatch match) {
		this.instancesDir = instancesDir;
		players = new ArrayList<String>();
		this.match = match;
	}
	
	public void addPlayer(String player) {
		players.add(player);
	}
	
	public Map<String,Double> start(int rounds, int k) throws Exception {
		File[] f = instancesDir.listFiles();
		Map<String,Double> totalPayoff = new HashMap<String, Double>();
		for(String player : players) {
			totalPayoff.put(player, 0.0);
		}
		for(File instFile : f) {
			TransportationInstance tp = InstanceConverter.convert(instFile.getAbsolutePath());
			//variable cost augmented k times
			augment(tp, k);
			match.init(players, tp);
			Map<String, Double> matchPayoff = match.start(rounds);
			for(String player : matchPayoff.keySet()) {
				double currentPayoff = totalPayoff.get(player);
				totalPayoff.put(player, currentPayoff + matchPayoff.get(player));
			}
		}
		return totalPayoff;
	}
	
	private void augment(TransportationInstance ti, int k) {
		for(Edge e : ti.getEdges()) {
			e.setVarCost(e.getVarCost() * k);
		}
	}
}
