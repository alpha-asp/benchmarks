package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;
import alpha.benchmarks.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class ConnectedSubGraph extends Generator{
	@Override
	public void generate(String[] parameters) throws IOException {
		System.out.println("This generator expects: <numRandomInstancesPerSetting> [numVertices_numEdges] ...");
		int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
		for (String parameter : Arrays.copyOfRange(parameters, 1, parameters.length)) {
			String[] split = parameter.split("_");
			int numVertices = Integer.parseInt(split[0]);
			int numEdges = Integer.parseInt(split[1]); // * (numVertices * numVertices) / 100;
			for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
				System.out.println("Generating instance" + parameter + "_" + i + ".lp with V:" + numVertices + " / E:" + numEdges);
				String instance = generateInstance(numVertices, numEdges);
				String fileName = "instance" + parameter + "_" + i + ".lp";
				writeInstanceToFile(instance, fileName);
			}
		}
	}

	private String generateInstance(int numVertices, int numEdges) {
		String program =
				"% Pick a subgraph of specified size.\n" +
				"node(X) :- edge(X, _).\n" +
				"node(Y) :- edge(_, Y).\n" +
				"\n" +
				"{subgraph_node(X) : node(X)}.\n" +
				":- K1 <= #count {X : subgraph_node(X)}, K1 = K + 1, subgraph_node_count(K).\n" +
				"%enough_vertices :- K <= #count {X : subgraph_node(X)}, subgraph_node_count(K).\n" +
				"%:- not enough_vertices.\n" +
				"\n" +
				"% Constrain the subgraph to have enough induced edges.\n" +
				"subgraph_edge(X, Y) :- edge(X, Y), subgraph_node(X), subgraph_node(Y).\n" +
				"%enough_edges :-\n" +
				"%    K <= #count {X, Y : edge(X, Y), subgraph_node(X), subgraph_node(Y)},\n" +
				"%    subgraph_edge_count(K).\n" +
				"%:- not enough_edges.\n" +
				"% Ensure connectedness.\n" +
				"connected(U,V) :- subgraph_edge(U,V).\n" +
				"connected(U,W) :- connected(U,V), connected(V,W).\n" +
				"subgraph_node(X) :- subgraph_edge(X,_).\n" +
				"subgraph_node(Y) :- subgraph_edge(_,Y).\n" +
				":- subgraph_node(X), subgraph_node(Y), not connected(X,Y).\n";
		StringBuilder sb = new StringBuilder(program);
		int originalDensity = numEdges / (numVertices * (numVertices - 1));
		float subgraphNodePercentage = 0.1f;
		int subgraphNodes = (int) Math.max(2,(Math.log(numVertices)));
		int subgraphEdges = Math.max(1,subgraphNodes * originalDensity);
		sb.append("subgraph_node_count(").append(subgraphNodes).append(").\n");
		sb.append("subgraph_edge_count(").append(subgraphEdges).append(").\n");

		HashSet<Pair<Integer,Integer>> edges = new HashSet<>();
		while(edges.size() < numEdges) {
			int a = random.nextInt(numVertices) + 1;
			int b = random.nextInt(numVertices) + 1;
			if ( a == b) {
				// No loops in the graph, i.e., a simple graph.
				continue;
			}
			Pair<Integer, Integer> link = new Pair<>(a, b);
			if (!edges.contains(link)) {
				edges.add(link);
			}
		}
		for (Pair<Integer, Integer> link : edges) {
			sb.append("edge(").append(link.getKey()).append(",").append(link.getValue()).append(").\n");
		}
		return sb.toString();
	}
}
