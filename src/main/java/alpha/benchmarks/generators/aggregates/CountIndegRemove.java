package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class CountIndegRemove extends Generator {
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
		String program = "delete(X,Y) :- edge(X,Y), not keep(X,Y).\n" +
				"keep(X,Y) :- edge(X,Y), delete(X1,Y1), X1 != X.\n" +
				"keep(X,Y) :- edge(X,Y), delete(X1,Y1), Y1 != Y.\n" +
				"vertex(X) :- delete(X,_).\n" +
				"vertex(X) :- delete(_,X).\n" +
				"bound(1.." + numVertices + ").\n" +
				"indeg(V,K) :- K <= #count { X : keep(X,V)}, vertex(V), bound(K).\n";
		StringBuilder sb = new StringBuilder(program);
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
