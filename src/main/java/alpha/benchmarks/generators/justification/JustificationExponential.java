package alpha.benchmarks.generators.justification;

import alpha.benchmarks.Generator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class JustificationExponential extends Generator {

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
		return "dom(1.." + domainSize + ").\n" +
				"{q(X)} :- dom(X).\n" +
				"p(X) :- q(X).\n" +
				"r(X) :- q(X).\n" +
				"p(X) :- r(X).\n" +
				":- not p(5).\n" +
				":- not p(7).\n";
	}

}
