package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class CountLazyNormalization extends Generator {
	@Override
	public void generate(String[] parameters) throws IOException {
		System.out.println("This generator expects: <numRandomInstancesPerSetting> <withoutAggregate> [domainSize] ...");
		int numRandomInstancesPerSetting = Integer.parseInt(parameters[0]);
		boolean withoutAggregate = Boolean.parseBoolean(parameters[1]);
		for (String parameter : Arrays.copyOfRange(parameters, 2, parameters.length)) {
			int domainSize = Integer.parseInt(parameter);
			for (int i = 1; i <= numRandomInstancesPerSetting; i++) {
				String instance = generateInstance(domainSize, withoutAggregate);
				String fileName = "instance" + parameter + "_" + i + ".lp";
				writeInstanceToFile(instance, fileName);
			}
		}
	}

	private String generateInstance(int domainSize, boolean withoutAggregate) {
		return "dom(1..1000)." +
				"sdom(1.." + domainSize + ")." +
				"{ sel(X,Y) } :- sdom(X), sdom(Y)." +
				"{ a;b }." +
				":- a,b." +
				(withoutAggregate ? "" : "num(K) :- 1 <= #count { X,Y : sel(X,Y)}, a,b,dom(K).");
	}
}
