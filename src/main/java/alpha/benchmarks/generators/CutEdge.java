package alpha.benchmarks.generators;

import alpha.benchmarks.Generator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class CutEdge extends Generator {
    @Override
    public void generate(String[] parameters) throws IOException {
        System.out.println("This generator expects: <numRandomInstancesPerSetting> [numVertices_percentEdges] ...");
        int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
        for (String parameter : Arrays.copyOfRange(parameters, 1, parameters.length)) {
            String[] split = parameter.split("_");
            int numVertices = Integer.parseInt(split[0]);
            int percentEdges = Integer.parseInt(split[1]);
            if (percentEdges > 100) {
                System.out.println("Percentage cannot be more than 100.");
                return;
            }
            for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
                String instance = generateInstance(numVertices, percentEdges);
                String fileName = "instance" + parameter + "_" + i + ".lp";
                writeInstanceToFile(instance, fileName);
            }
        }
    }

    private String generateInstance(int numVertices, int percentEdges) {
        StringBuilder sb = new StringBuilder();
        sb.append("delete(X,Y) :- edge(X,Y), not keep(X,Y).\n");
        sb.append("keep(X,Y) :- edge(X,Y), delete(X1,Y1), X1 != X.\n");
        sb.append("keep(X,Y) :- edge(X,Y), delete(X1,Y1), Y1 != Y.\n");
        sb.append("reachable(X,Y) :- keep(X,Y).\n");
        int startVertex = random.nextInt(numVertices) + 1;
        sb.append("reachable(X,").append(startVertex).append(") :- reachable(X,Z),reachable(Z,").append(startVertex).append(").\n");
        for (int i = 1; i <= numVertices; i++) {
            for (int j = 1; j <= numVertices; j++) {
                if (random.nextInt(100) + 1 <= percentEdges) {
                    sb.append("edge(").append(i).append(",").append(j).append(").\n");
                }
            }
        }
        return sb.toString();
    }
}
