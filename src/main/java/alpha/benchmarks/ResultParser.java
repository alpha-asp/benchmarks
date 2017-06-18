package alpha.benchmarks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
class ResultParser {

    final static String BENCHMARK_NAME_PREFIX = "# Benchmark:";
    final static String CONFIGURATION_PREFIX = "# Configuration:\"instance\";";
    final static String CONFIGURATION_SPLITTER = ";";
    final static float TIMEOUT_DEFAULT = 300.0f;

    void parseResults(String resultFile) throws IOException {
        // Read result file.
        List<String> resultLines = Files.readAllLines(Paths.get(resultFile));
        String benchmarkName = resultLines.get(0).replaceFirst(BENCHMARK_NAME_PREFIX, "");
        String configuration = resultLines.get(1).replaceFirst(CONFIGURATION_PREFIX, "");
        String[] configurations = configuration.split(CONFIGURATION_SPLITTER);
        int numConfigurations = configurations.length;
        int numInstances = resultLines.size() - 2;
        // Sort runs of the same instance.
        HashMap<String, ArrayList<String>> runsOfInstance = new HashMap<>();
        for (int i = 0; i < numInstances; i++) {
            String run = resultLines.get(i + 2);
            String instanceName = run.split("_\\d+\\.lp ")[0].replaceFirst("instances/instance","");
            String instanceRunResult = run.split("_\\d+\\.lp ")[1];
            runsOfInstance.putIfAbsent(instanceName, new ArrayList<>());
            runsOfInstance.get(instanceName).add(instanceRunResult);
        }
        // Compute averages per instance.
        HashMap<String, String> instanceAverage = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> runsPerInstance : runsOfInstance.entrySet()) {
            int numRuns = runsPerInstance.getValue().size();
            float[] absolutes = new float[numConfigurations];
            int[] fails = new int[numConfigurations];
            // Count absolute times and failures.
            for (String run : runsPerInstance.getValue()) {
                String[] runTimes = run.split(" ");
                for (int i = 0; i < runTimes.length; i++) {
                    if (i % 2 == 0) {
                        // Skip odd entries
                        continue;
                    }
                    switch (runTimes[i]) {
                        case "---":
                        case "===":
                        case "FAIL":
                            fails[i/2]++;
                            absolutes[i/2] += TIMEOUT_DEFAULT;
                            break;
                        default:
                            absolutes[i/2] += Float.parseFloat(runTimes[i]);
                    }
                }
            }
            String resultOfInstance = "";
            for (int i = 0; i < numConfigurations; i++) {
                if (i != 0) {
                    resultOfInstance += ";";
                }
                resultOfInstance += String.format("%.2f",absolutes[i] / numRuns) + "(" + fails[i] + ")";
            }
            instanceAverage.put(runsPerInstance.getKey(), resultOfInstance);
        }
        // Print out results:
        System.out.println("Benchmark: " + benchmarkName + "\nConfiguration: " + configuration);
        for (Map.Entry<String, String> inst : instanceAverage.entrySet()) {
            System.out.println(inst.getKey() + ": " + inst.getValue());
        }
    }
}
