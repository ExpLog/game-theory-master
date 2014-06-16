package control;

import data.EdgeFlow;
import data.TransportationInstance;

import java.util.List;

public interface TPSolver {
	public List<EdgeFlow> solve(TransportationInstance problem);
}
