package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;
import alpha.benchmarks.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class GraphColorCounting extends Generator {

	@Override
	public void generate(String[] parameters) throws IOException {
		System.out.println("This generator expects: <numRandomInstancesPerSetting> [numVertices_numEdges] ...");
		int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
		for (String parameter : Arrays.copyOfRange(parameters, 1, parameters.length)) {
			String[] split = parameter.split("_");
			int numVertices = Integer.parseInt(split[0]);
			int numEdges = Integer.parseInt(split[1]);
			for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
				String instance = generateInstance(numVertices, numEdges);
				String fileName = "instance" + parameter + "_" + i + ".lp";
				writeInstanceToFile(instance, fileName);
			}
		}
	}

	private String generateInstance(int numVertices, int numEdges) {
		StringBuilder sb = new StringBuilder();
		sb.append("% Guess colours.\n" +
				"chosenColour(N,C) :- node(N), colour(C), not notChosenColour(N,C).\n" +
				"notChosenColour(N,C) :- chosenColour(N,C1), colour(C), C != C1.\n" +
				"%notChosenColour(N,C) :- node(N), colour(C), not chosenColour(N,C).\n" +
				"% At least one color per node.\n" +
				":- node(X), not colored(X).\n" +
				"colored(X) :- chosenColour(X,Fv1).\n" +
				"% Only one color per node.\n" +
				":- node(N), chosenColour(N,C1), chosenColour(N,C2), C1!=C2.\n" +
				"% No two adjacent nodes have the same colour. \n" +
				":- link(X,Y), node(X), node(Y), X<Y, chosenColour(X,C), chosenColour(Y,C).\n" +
				"colour(red0).\n" +
				"colour(green0).\n" +
				"colour(blue0).\n" +
				"colour(yellow0).\n" +
				"colour(cyan0).\n" +
				"bound(1.." + numVertices + ").\n" +
				"rblink(X,Y) :- link(X,Y), chosenColour(X,blue0), chosenColour(Y,red0).\n" +
				"rblink(X,Y) :- link(X,Y), chosenColour(X,red0), chosenColour(Y,blue0).\n" +
				"rbreach(X,Y) :- rbreach(X,Z), rblink(Z,Y), X != Y.\n" +
				"rbreach(X,Y) :- rblink(X,Y), guard(_).\n" +
				"numrbreachable(V,K) :- K <= #count { Y : rbreach(V,Y) }, node(V), bound(K), guard(_).\n" +
				"");
		sb.append(":- node(X), not chosenColour(X,red0), not chosenColour(X,blue0), not chosenColour(X,yellow0), not chosenColour(X,green0), not chosenColour(X,cyan0).\n");
		sb.append("\n");
		// Guard
		sb.append("colored2(X) :- chosenColour(X,_).\n");
		StringBuilder collector = new StringBuilder("guard(a) :- ");
		for (int i = 1; i <= numVertices; i++) {
			if (i%100 == 1) {
				int curI = (int) Math.ceil(i/100.0f);
				sb.append("go(" + curI + ") :- ");
				collector.append( i == 1 ? "" : ", ");
				collector.append("go(" + curI + ")");
			}
			sb.append("colored2(" + i + ")");
			if (i%100 == 0 || i == numVertices) {
				sb.append(".\n");
			} else {
				sb.append(", ");
			}
		}
		sb.append("% Collector:\n" + collector + ".\n");
		for (int i = 1; i <= numVertices; i++) {
			sb.append("node(").append(i).append(").\n");
		}
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
		sb.append("\n");
		return sb.toString();
	}
}
