package alpha.benchmarks;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public abstract class Generator {

    protected Random random;

    public abstract void generate(String[] parameters) throws IOException;

    void setRandom(Random random) {
        this.random = random;
    }

    protected void writeInstanceToFile(String instance, String fileName) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("ASCII"));
        writer.write(instance);
        writer.close();
    }
}
