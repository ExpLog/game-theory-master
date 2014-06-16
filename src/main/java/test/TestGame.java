package test;

import control.Game;

import java.io.File;
import java.util.Map;

public class TestGame {
	public static void main(String[] args) throws Exception{
		Game g = new Game(new File("src\\main\\resources"), new GameMatchImpl());
		g.addPlayer("Player1");
		g.addPlayer("Player2");
		g.addPlayer("Player3");
		Map<String,Double> totalPayoff = g.start(3, 20);
		
		System.out.println("TOTAL PAYOFF:");
		for(String s : totalPayoff.keySet()) {
			System.out.println(s + " : " + totalPayoff.get(s));
		}
	}
}
