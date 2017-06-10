package alpha.benchmarks;

import java.io.IOException;
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
}
