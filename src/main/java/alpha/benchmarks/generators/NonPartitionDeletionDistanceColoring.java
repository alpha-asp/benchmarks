package alpha.benchmarks.generators;

import alpha.benchmarks.Generator;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class NonPartitionDeletionDistanceColoring extends Generator {

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
        sb.append("keep(X) :- vertex(X), not delete(X).\n")
                .append("delete(X) :- vertex(X), not keep(X).\n")
                .append(":- delete(X), vertex(Y), not keep(Y), X != Y.\n")
                .append("kept_edge(V1, V2) :- keep(V1), keep(V2), edge(V1, V2).\n")
                .append("reachable(X, Y) :- kept_edge(X, Y).\n")
                .append("reachable(X, Z) :- reachable(X, Y), reachable(Y, Z).\n")
                .append(":- delete(D), edge(V1, D), edge(D, V2), not reachable(V1, V2).\n");

        sb.append("blue(N) :- keep(N), not red(N), not green(N).\n")
                .append("red(N) :- keep(N), not blue(N), not green(N).\n")
                .append("green(N) :- keep(N), not red(N), not blue(N).\n")
                .append(":- kept_edge(N1,N2), blue(N1), blue(N2).\n")
                .append(":- kept_edge(N1,N2), red(N1), red(N2).\n")
                .append(":- kept_edge(N1,N2), green(N1), green(N2).\n");

        for (int i = 1; i <= numVertices; i++) {
            sb.append("vertex(").append(i).append(").\n");
        }
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
