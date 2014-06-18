package utils;

import data.Bid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BidFilter {
    public static List<Bid> filter(List<String> players, List<Bid> bids, int edgesPerRound) {
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
