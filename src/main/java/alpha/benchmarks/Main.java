package alpha.benchmarks;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.System.exit;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("This is the benchmark generator factory.");
        if (args.length > 0 ) {
            Generator generator = GeneratorFactory.getInstance(args[0]);
            if (generator == null) {
                printUsage();
                System.out.println("alpha.benchmarks.Generator name unknown: " + args[0]);
                exit(1);
            }
            String[] params = Arrays.copyOfRange(args, 1, args.length);
            generator.generate(params);
        } else {
            printUsage();
            System.out.println("alpha.benchmarks.Generator must be specified.");
            exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: <GeneratorName> [GeneratorSpecificParameters..]");
    }
}
