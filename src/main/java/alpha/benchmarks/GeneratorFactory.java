package alpha.benchmarks;

import alpha.benchmarks.generators.*;
import alpha.benchmarks.generators.aggregates.CountIndegRemove;
import alpha.benchmarks.generators.aggregates.CountLazyGrowth;
import alpha.benchmarks.generators.aggregates.CountLazyNormalization;
import alpha.benchmarks.generators.aggregates.SumOverGuess;

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
            case "nonpartitiondeletiondistancecoloring":
                return new NonPartitionDeletionDistanceColoring();
            case "reachability":
                return new Reachability();
            case "threecoloring":
                return new ThreeColorability();
            case "colorfulreachability":
                return new ColorfulReachability();
			case "agg:sumoverguess":
				return new SumOverGuess();
			case "agg:countlazygrowth":
				return new CountLazyGrowth();
			case "agg:countlazynormalization":
				return new CountLazyNormalization();
			case "agg:countindegremove":
				return new CountIndegRemove();
            default:
                return null;
        }
    }
}
