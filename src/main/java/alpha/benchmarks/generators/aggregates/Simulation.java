package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;
import alpha.benchmarks.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class Simulation extends Generator {

	@Override
	public void generate(String[] parameters) throws IOException {
		System.out.println("This generator expects: <numRandomInstancesPerSetting> [numVertices_numEdges_numTimesteps_numMutex] ...");
		int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
		for (String parameter : Arrays.copyOfRange(parameters, 1, parameters.length)) {
			String[] split = parameter.split("_");
			int numVertices = Integer.parseInt(split[0]);
			int numEdges = 4 * numVertices; //Integer.parseInt(split[1]);
			int numTimesteps = numVertices / 2; //Integer.parseInt(split[2]);
			int numMutex = numEdges / 2; //Integer.parseInt(split[3]);
			for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
				String instance = generateInstance(numVertices, numEdges, numTimesteps, numMutex);
				String fileName = "instance" + parameter + "_" + i + ".lp";
				writeInstanceToFile(instance, fileName);
			}
		}
	}

	private String generateInstance(int numVertices, int numEdges, int numTimesteps, int numMutex) {
		StringBuilder sb = new StringBuilder();
		int startNode = random.nextInt(numVertices) + 1;
		int numMoves = (int) Math.max(numTimesteps * 0.75f, 10);
		sb.append("pos(" + startNode + ",1). % starting position with node/timestep\n" +
				"time(1.." + numTimesteps + ").\n" +
				"numMoves(" + numMoves + ").\n" +
				"% Goal\n" +
				":- pos(" + startNode + ",1), pos(" + startNode + "," + numTimesteps + ").\n" +
				"\n% Guess at most one move per time step\n" +
				"{goto(P,T)} :- pos(V,T), link(V,P), not depleted(T).\n" +
				":- goto(P1,T), goto(P2,T), P1 != P2.\n" +
				"% Move position\n" +
				"pos(N,T1) :- goto(N,T), T1 = T+1, time(T1).\n" +
				"% Inertia\n" +
				"moves(T) :- goto(_,T).\n" +
				"pos(N,T1) :- pos(N,T), T1 = T+1, time(T1), not moves(T).\n" +
				"% Cannot visit mutually exclusive nodes.\n" +
				"visited(P) :- pos(P,_).\n" +
				":- visited(P1), visited(P2), P1 != P2, mutex(P1,P2). % mutex relation is symmetric\n" +
				"depleted(T1) :- M <= #count { P,Ts : goto(P,Ts), Ts <= T }, numMoves(M), time(T), T1 = T + 1.\n" +
				"");
		HashSet<Pair<Integer,Integer>> links = new HashSet<>();
		while(links.size() < numEdges) {
			int a = random.nextInt(numVertices) + 1;
			int b = random.nextInt(numVertices) + 1;
			if (a == b) {
				continue;	// No self-loops.
			}
			Pair<Integer, Integer> link = new Pair<>(a, b);
			if (!links.contains(link)) {
				links.add(link);
			}
		}
		for (Pair<Integer, Integer> link : links) {
			sb.append("link(").append(link.getKey()).append(",").append(link.getValue()).append(").\n");
			sb.append("link(").append(link.getValue()).append(",").append(link.getKey()).append(").\n");
		}
		int mutexCount = 0;
		for (Pair<Integer, Integer> link : links) {
			if (++mutexCount > numMutex) {
				break;
			}
			sb.append("mutex(").append(link.getKey()).append(",").append(link.getValue()).append(").\n");
			sb.append("mutex(").append(link.getValue()).append(",").append(link.getKey()).append(").\n");
		}
		sb.append("\n");
		return sb.toString();
	}
}
