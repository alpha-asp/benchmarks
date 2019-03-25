package alpha.benchmarks.generators.aggregates;

import alpha.benchmarks.Generator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class ExponentialSizeCount extends Generator {
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
		StringBuilder expVars = new StringBuilder();
		for (int i = 1; i <= domainSize; i++) {
			if (i == 1) {
				expVars.append("X" + i);
			} else {
				expVars.append(", X" + i);
			}
		}
		StringBuilder expSel = new StringBuilder();
		for (int i = 1; i <= domainSize; i++) {
			if (i == 1) {
				expSel.append("exp(X" + i + ")");
			} else {
				expSel.append(", exp(X" + i + ")");
			}
		}
		String program = "dom(0..1).\n" +
				"{ a;b;c }.\n" +
				":- a, b.\n" +
				"exp(X) :- a,b, dom(X).\n" +
				"holds :- 2 <= #count { " + expVars + " : " + expSel + " }.\n";
		return program;
	}
}
