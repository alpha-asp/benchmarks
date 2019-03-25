package alpha.benchmarks.generators.justification;

import alpha.benchmarks.Generator;
import alpha.benchmarks.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class JustificationLocoMove extends Generator {

	@Override
	public void generate(String[] parameters) throws IOException {
		System.out.println("This generator expects: <numRandomInstancesPerSetting> [timeSteps_numLocos_numStations_numConnections_maxTimePerConnection] ...");
		int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
		for (String parameter : Arrays.copyOfRange(parameters, 1, parameters.length)) {
			String[] split = parameter.split("_");
			int timeSteps = Integer.parseInt(split[0]);
			int numLocos = Integer.parseInt(split[1]);
			int numStations = Integer.parseInt(split[2]);
			int numConnections = Integer.parseInt(split[3]);
			int maxTimePerConnection = Integer.parseInt(split[4]);
			for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
				String instance = generateInstance(timeSteps, numLocos, numStations, numConnections, maxTimePerConnection);
				String fileName = "instance" + parameter + "_" + i + ".lp";
				writeInstanceToFile(instance, fileName);
			}
		}
	}

	private String generateInstance(int timeSteps, int numLocos, int numStations, int numConnections, int maxTimePerConnection) {

		int lastTimeStep = timeSteps  - 1;
		StringBuilder sb = new StringBuilder();
		sb.append("time(0.." + lastTimeStep + ").\n" +
				"{ locomove(T, Id, S1, S2, D) } :- loco(T,Id,S1), S1 != transit, connection(S1,S2,D), time(T), TE=T+D+1, time(TE).\n" +
				"transit(T1,Id) :- locomove(T, Id, _, _, D), time(T1), T < T1, T1 <= TD, TD=T+D.\n" +
				"loco(T,Id,transit) :- transit(T,Id).\n" +
				":- locomove(T, Id, S1, S2, _), locomove(T, Id, S1, S3, _), S2 != S3.\n" +
				"loco(T1,Id,S) :- loco(T,Id,S), S != transit, T1 = T+1, time(T1), not transit(T1,Id).\n" +
				"loco(TE,Id,S2) :- locomove(T, Id, _, S2, D), TE = T+D+1, time(TE).\n" +
				"connection(V1,V2,D) :- connection(V2,V1,D).\n");

		for (int i = 0; i < numLocos; i++) {
			int startstation = random.nextInt(numStations) + 1;
			int endstation = random.nextInt(numStations) + 1;
			sb.append("loco(0,id" + i + ",station" + startstation + ").\n");
			sb.append(":- not loco(" + lastTimeStep + ",id" + i + ",station" + endstation + ").\n");
		}

		HashSet<Pair<Integer,Integer>> links = new HashSet<>();
		while(links.size() < numConnections) {
			int a = random.nextInt(numStations) + 1;
			int b = random.nextInt(numStations) + 1;
			Pair<Integer, Integer> link = new Pair<>(a, b);
			Pair<Integer, Integer> revlink = new Pair<>(b, a);
			if (!links.contains(link) && !links.contains(revlink)) {
				links.add(link);
			}
		}
		for (Pair<Integer, Integer> link : links) {
			int connectionTime = random.nextInt(maxTimePerConnection) + 1;
			sb.append("connection(station" + link.getKey() + ",station" + link.getValue() + "," + connectionTime + ").\n");
		}
		sb.append("\n");
		return sb.toString();
	}
}
