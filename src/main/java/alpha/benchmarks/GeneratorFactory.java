package alpha.benchmarks;

import alpha.benchmarks.generators.CutEdge;
import alpha.benchmarks.generators.GroundExplosion;
import alpha.benchmarks.generators.RandomGraphColoring;

/**
 * Copyright (c) 2017, the Alpha Team.
 */
public class GeneratorFactory {

    public static Generator getInstance(String name) {
        String nameLowerCase = name.toLowerCase();
        switch (nameLowerCase) {
            case "groundexplosion":
                return new GroundExplosion(false);
            case "groundexplosion2":
                return new GroundExplosion(true);
            case "graphcoloring":
                return new RandomGraphColoring(true);
            case "graphcoloringnosupport":
                return new RandomGraphColoring(false);
            case "cutedge":
                return new CutEdge();
            default:
                return null;
        }
    }
}
