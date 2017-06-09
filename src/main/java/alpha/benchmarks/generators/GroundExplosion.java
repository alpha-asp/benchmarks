package alpha.benchmarks.generators;

import alpha.benchmarks.Generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class GroundExplosion implements Generator {

    private final boolean secondVariant;

    public GroundExplosion(boolean secondVariant) {
        this.secondVariant = secondVariant;
    }


    @Override
    public void generate(String[] parameters) throws IOException {

        for (String parameter : parameters) {
            int domainSize = Integer.parseInt(parameter);
            String instance = generateInstance(domainSize);
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("instance" + domainSize + ".lp"), Charset.forName("ASCII"));
            writer.write(instance);
            writer.close();
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
