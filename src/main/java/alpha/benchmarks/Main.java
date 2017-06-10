package alpha.benchmarks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.System.exit;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("This is the benchmark generator factory.");
        if (args.length > 0 ) {
            if ( args.length == 2 && args[0].equals("-conf")) {
                System.out.println("Reading configuration from: " + args[1]);
                List<String> lines = Files.readAllLines(Paths.get(args[1]));
                String confstring = lines.get(0);
                String[] config = confstring.split("\\s+");
                String randomseed = lines.get(1).split("=")[1];
                generate(config, Long.parseLong(randomseed));
            } else {
                generate(args, System.nanoTime());
            }
        } else {
            printUsage();
            System.out.println("Generator must be specified.");
            exit(1);
        }
    }

    private static void generate(String[] args, long randomseed) throws IOException {
        Generator generator = GeneratorFactory.getInstance(args[0]);
        if (generator == null) {
            printUsage();
            System.out.println("alpha.benchmarks.Generator name unknown: " + args[0]);
            exit(1);
        }
        generator.setRandom(new Random(randomseed));
        String[] params = Arrays.copyOfRange(args, 1, args.length);
        printConfiguration(args, randomseed);
        generator.generate(params);
    }

    private static void printUsage() {
        System.out.println("Usage: <GeneratorName> [GeneratorSpecificParameters..]\n" +
                "OR \t-conf [configfile]");
    }

    private static void printConfiguration(String[] config, long randomseed) {
        System.out.println("Configuration is:");
        boolean isFirst = true;
        for (String s : config) {
            if (!isFirst) {
                System.out.print(" ");
            }
            isFirst = false;
            System.out.print(s);
        }
        System.out.println("\nRandomseed=" + randomseed);
    }
}
