package alpha.benchmarks.generators;

import alpha.benchmarks.Generator;
import alpha.benchmarks.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class Reachability extends Generator {

    @Override
    public void generate(String[] parameters) throws IOException {
        System.out.println("This generator expects: <numRandomInstancesPerSetting> [numVertices_multiplierEdges] ...");
        int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
        for (String parameter : Arrays.copyOfRange(parameters, 1, parameters.length)) {
            String[] split = parameter.split("_");
            int numVertices = Integer.parseInt(split[0]);
            int percentEdges = Integer.parseInt(split[1]);
            for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
                String instance = generateInstance(numVertices, percentEdges);
                String fileName = "instance" + parameter + "_" + i + ".lp";
                writeInstanceToFile(instance, fileName);
            }
        }
    }

    private String generateInstance(int numVertices, int edgesMultiplier) {
        StringBuilder sb = new StringBuilder();
        sb.append("reachable(X, Y) :- edge(X, Y).\n")
                .append("reachable(S, Y) :- start(S), reachable(S, X), reachable(X, Y).\n");
        sb.append("start(").append(random.nextInt(numVertices) + 1).append(").\n");

        HashSet<Pair<Integer,Integer>> edges = new HashSet<>();
        while(edges.size() < edgesMultiplier * numVertices) {
            int a = random.nextInt(numVertices) + 1;
            int b = random.nextInt(numVertices) + 1;
            Pair<Integer, Integer> link = new Pair<>(a, b);
            if (!edges.contains(link)) {
                edges.add(link);
            }
        }
        for (Pair<Integer, Integer> edge : edges) {
            sb.append("edge(").append(edge.getKey()).append(",").append(edge.getValue()).append(").\n");
        }
        return sb.toString();
    }
}
