package alpha.benchmarks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class TritonResultParser {

	private static class InstanceParameters {
		boolean outOfMemory = false;
		boolean timeout = false;
		float runtimeWallClock = 0;
		float runtimeCPUTime = 0;
	}

	private static class AveragedInstance {
		int numTimeouts;
		int numMemouts;
		float averageCPUTime;
		float averageWallClockTime;
	}

	public static void parseResult(String directory) throws IOException {
		File benchDir = new File("./" + directory);
		System.out.println("Files in given directory: " + Arrays.toString(benchDir.listFiles()));
		File[] configurations = benchDir.listFiles()[0].listFiles();	// Walk over the 'sm' subdirectory.
		for (File configuration : configurations) {
			System.out.println("Found configuration: " + configuration.getName());
			HashMap<String, ArrayList<InstanceParameters>> foundInstances = new LinkedHashMap<>();
			File[] instances = configuration.listFiles();
			for (File instance : instances) {
				String fullInstanceName = instance.getName();
				//System.out.println("Found instance " + fullInstanceName);
				final int lastUnderline = fullInstanceName.lastIndexOf("_");
				String instanceName = fullInstanceName.substring(0, lastUnderline);
				//System.out.println("Instance name is: " + instanceName);

				InstanceParameters instanceParameters = readInstance(instance);
				foundInstances.putIfAbsent(instanceName, new ArrayList<>());
				foundInstances.get(instanceName).add(instanceParameters);
			}
			// Compute averages.
			HashMap<String, AveragedInstance> averagedInstances = new LinkedHashMap<>();
			for (Map.Entry<String, ArrayList<InstanceParameters>> foundInstance : foundInstances.entrySet()) {
				if (foundInstance.getValue().size() != 10) {
					throw new RuntimeException("Did not find 10 runs for instance " + foundInstance.getKey()
							+ " only " + foundInstance.getValue().size() + " runs.");
				}
				float totalCPUTime = 0f;
				float totalWCTime = 0f;
				int numTimeouts = 0;
				int numMemouts = 0;
				for (InstanceParameters instanceParameters : foundInstance.getValue()) {
					if (instanceParameters.timeout) {
						numTimeouts++;
					}
					if (instanceParameters.outOfMemory) {
						numMemouts++;
					}
					totalCPUTime += instanceParameters.runtimeCPUTime;
					totalWCTime += instanceParameters.runtimeWallClock;
				}
				averagedInstances.putIfAbsent(foundInstance.getKey(), new AveragedInstance());
				AveragedInstance averagedInstance = averagedInstances.get(foundInstance.getKey());
				averagedInstance.averageCPUTime = totalCPUTime / 10;
				averagedInstance.averageWallClockTime = totalWCTime / 10;
				averagedInstance.numTimeouts = numTimeouts;
				averagedInstance.numMemouts = numMemouts;
			}
			DecimalFormat df = new DecimalFormat("0.00");
			StringBuilder sb = new StringBuilder(configuration.getName());
			sb.append("\n");
			for (Map.Entry<String, AveragedInstance> averagedInstanceEntry : averagedInstances.entrySet()) {
				AveragedInstance averagedInstance = averagedInstanceEntry.getValue();
				sb.append(averagedInstanceEntry.getKey()).append(": ");
				sb.append(df.format(averagedInstance.averageCPUTime)).append("/").append(df.format(averagedInstance.averageWallClockTime))
						.append(" CPU/WC, TO: ").append(averagedInstance.numTimeouts)
						.append(" MO: ").append(averagedInstance.numMemouts)
						.append("\n");
			}
			System.out.println(sb);
		}
	}

	private static InstanceParameters readInstance(File instance) throws IOException {
		List<String> resultLines = Files.readAllLines(instance.toPath());
		InstanceParameters instanceParameters = new InstanceParameters();
		boolean foundCPUTime = false;
		for (String line : resultLines) {
			if (line.contains("CPUTIME")) {
				instanceParameters.runtimeCPUTime = Float.parseFloat(line.split(": ")[1]);
				//System.out.println("Parsed CPUTIME: " + instanceParameters.runtimeCPUTime);
				foundCPUTime = true;
			}
			if (line.contains("WCTIME")) {
				instanceParameters.runtimeWallClock = Float.parseFloat(line.split(": ")[1]);
				//System.out.println("Parsed WCTIME: " + instanceParameters.runtimeWallClock);
			}
			if (line.contains("MAXVM")) {
				int memory = Integer.parseInt(line.split(": ")[1]);
				if (memory >= 8192000) {
					instanceParameters.outOfMemory = true;
				}
			}
		}
		if (!foundCPUTime) {
			throw new RuntimeException("Did not find a CPUTIME in instance: " + instance.getName());
		}
		if (instanceParameters.runtimeCPUTime >= 300.00f) {
			instanceParameters.runtimeCPUTime = 300.00f;
			instanceParameters.timeout = true;
		}
		if (instanceParameters.outOfMemory) {
			instanceParameters.runtimeCPUTime = 300.00f;
		}
		return instanceParameters;
	}
}
