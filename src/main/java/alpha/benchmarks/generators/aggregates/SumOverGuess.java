package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class SumOverGuess extends Generator {
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
				"bound(1)." +
				"bound(X1) :- sum_larger_equal_than(X), X1 = X +1." +
				"{ in(X) } :- dom(X)." +
				"sum_larger_equal_than(K) :- K <= #sum { X : in(X)}, bound(K).";
	}
}
