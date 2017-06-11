package alpha.benchmarks.generators;

import alpha.benchmarks.Generator;

import java.io.IOException;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class GroundExplosion extends Generator {

    private final boolean secondVariant;

    public GroundExplosion(boolean secondVariant) {
        this.secondVariant = secondVariant;
    }


    public void generate(String[] parameters) throws IOException {

        for (String parameter : parameters) {
            int domainSize = Integer.parseInt(parameter);
            String instance = generateInstance(domainSize);
            String fileName = "instance" + domainSize + ".lp";
            writeInstanceToFile(instance, fileName);
        }
    }


    private String generateInstance(int domainSize) {
        StringBuffer sb = new StringBuffer();
        sb.append("p(X1,X2,X3,X4,X5,X6) :- select(X1), select(X2), select(X3), select(X4), select(X5), select(X6).\n");
        sb.append("select(X) :- dom(X), not nselect(X).\n");
        sb.append("nselect(X) :- dom(X), not select(X).\n");
        if (secondVariant) {
            sb.append(":- not nselect(Y), select(X), dom(Y), X != Y.\n");
        } else {
            sb.append(":- select(Y), select(X), X != Y.\n");
        }
        sb.append("\n");
        for (int i = 1; i <= domainSize; i++) {
            sb.append("dom(" + i +").\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
