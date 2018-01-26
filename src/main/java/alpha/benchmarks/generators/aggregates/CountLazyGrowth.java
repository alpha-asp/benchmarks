package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class CountLazyGrowth extends Generator {
	@Override
	public void generate(String[] parameters) throws IOException {
		System.out.println("This generator expects: <numRandomInstancesPerSetting> [domainSize] ...");
		int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
		for (String parameter : Arrays.copyOfRange(parameters, 1, parameters.length)) {
			int domainSize = Integer.parseInt(parameter);
			for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
				String instance = generateInstance(domainSize);
				String fileName = "instance" + parameter + "_" + i + ".lp";
				writeInstanceToFile(instance, fileName);
			}
		}
	}

	private String generateInstance(int domainSize) {
		return "dom(1.." + domainSize + ")." +
				"{ sel(X) } :- dom(X)." +
				":- sel(X), sel(Y), X != Y." +
				"num(K) :- K <= #count { X,Y,Z : sel(X), sel(Y), sel(Z)}, dom(K).";
	}
}
