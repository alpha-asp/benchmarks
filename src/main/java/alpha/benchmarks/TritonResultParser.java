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
		int maxMemory = 0;
	}

	private static class AveragedInstance {
		int numTimeouts;
		int numMemouts;
		float averageCPUTime;
		float averageWallClockTime;
		int maximumResidentSetSize;
	}

	public static void parseResult(String directoryConfig) throws IOException {
		// Get directory and its timeout/memout settings:
		float timeout = 300.00f; // 900.00f
		int memout = 8192000; // 40960000
		String[] split = directoryConfig.split(":");
		String directory = split[0];
		if (split.length == 3) {
			timeout = Float.parseFloat(split[1]);
			memout = Integer.parseInt(split[2]);
		}
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

				InstanceParameters instanceParameters = readInstance(instance, timeout, memout);
				if (instanceParameters == null) {
					System.out.println("WARNING: Could not parse instance; skipping: " + instance);
					continue;
				}
				foundInstances.putIfAbsent(instanceName, new ArrayList<>());
				foundInstances.get(instanceName).add(instanceParameters);
			}
			// Compute averages.
			Map<String, AveragedInstance> averagedInstances = new TreeMap<>();
			boolean didWarn = false;
			for (Map.Entry<String, ArrayList<InstanceParameters>> foundInstance : foundInstances.entrySet()) {
				int numInstances = foundInstance.getValue().size();
				if (numInstances != 10) {
					System.out.print("WARNING: Did not find 10 runs for instance " + foundInstance.getKey()
							+ " only " + numInstances + " runs. ");
					didWarn = true;
				}
				float totalCPUTime = 0f;
				float totalWCTime = 0f;
				int numTimeouts = 0;
				int numMemouts = 0;
				int maxResidentSetSize = 0;
				for (InstanceParameters instanceParameters : foundInstance.getValue()) {
					if (instanceParameters.timeout) {
						numTimeouts++;
					}
					if (instanceParameters.outOfMemory) {
						numMemouts++;
					}
					totalCPUTime += instanceParameters.runtimeCPUTime;
					totalWCTime += instanceParameters.runtimeWallClock;
					if (instanceParameters.maxMemory > maxResidentSetSize) {
						maxResidentSetSize = instanceParameters.maxMemory;
					}
				}
				averagedInstances.putIfAbsent(foundInstance.getKey(), new AveragedInstance());
				AveragedInstance averagedInstance = averagedInstances.get(foundInstance.getKey());
				averagedInstance.averageCPUTime = totalCPUTime / numInstances;
				averagedInstance.averageWallClockTime = totalWCTime / numInstances;
				averagedInstance.numTimeouts = numTimeouts;
				averagedInstance.numMemouts = numMemouts;
				averagedInstance.maximumResidentSetSize = maxResidentSetSize;
			}
			if (didWarn) {
				System.out.println();
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
						.append(" MaxRSS: ").append(averagedInstance.maximumResidentSetSize).append("KB")
						.append("\n");
			}
			System.out.println(sb);
		}
	}

	private static InstanceParameters readInstance(File instance, float timeout, int memout) throws IOException {
		List<String> resultLines = Files.readAllLines(instance.toPath());
		InstanceParameters instanceParameters = new InstanceParameters();
		instanceParameters.runtimeCPUTime = 0.0f;
		instanceParameters.runtimeWallClock = 0.0f;
		boolean foundCPUTime = false;
		int maxvm = 0;
		for (String line : resultLines) {
			if (line.contains("CPUTIME") || line.contains("P_CPUTIME") || line.contains("P_P_CPUTIME")) {
				instanceParameters.runtimeCPUTime += Float.parseFloat(line.split(": ")[1]);
				//System.out.println("Parsed CPUTIME: " + instanceParameters.runtimeCPUTime);
				foundCPUTime = true;
			}
			if (line.contains("WCTIME") || line.contains("P_WCTIME") || line.contains("P_P_WCTIME")) {
				instanceParameters.runtimeWallClock += Float.parseFloat(line.split(": ")[1]);
				//System.out.println("Parsed WCTIME: " + instanceParameters.runtimeWallClock);
			}
			if (line.contains("MAXVM")) {
				maxvm = Integer.parseInt(line.split(": ")[1]);
			}
			if (line.contains("MEMOUT: true")) {
				instanceParameters.outOfMemory = true;
			}
			// Check for memout with failure to allocate memory.
			if (line.contains("ERROR: (clingo): std::bad_alloc")
					|| line.contains("STDERR:") && line.contains("std::bad_alloc")) {
				instanceParameters.outOfMemory = true;
			}
			if (line.contains("WATCHER: maximum resident set size=")) {
				instanceParameters.maxMemory = Integer.parseInt(line.split("= ")[1]);
			}
		}
		if (!foundCPUTime) {
			return null;
			//throw new RuntimeException("Did not find a CPUTIME in instance: " + instance.getName());
		}
		if (instanceParameters.runtimeCPUTime >= timeout) {
			instanceParameters.runtimeCPUTime = timeout;
			instanceParameters.timeout = true;
		}
		if (instanceParameters.maxMemory == 0) {
			instanceParameters.maxMemory = maxvm;
		}
		if (instanceParameters.maxMemory >=  memout) {
			instanceParameters.outOfMemory = true;
		}

		if (instanceParameters.outOfMemory) {
			instanceParameters.runtimeCPUTime = timeout;
		}
		return instanceParameters;
	}
}
