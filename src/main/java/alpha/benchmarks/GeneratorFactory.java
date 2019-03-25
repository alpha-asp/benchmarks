package alpha.benchmarks;

import alpha.benchmarks.generators.*;
import alpha.benchmarks.generators.aggregates.*;
import alpha.benchmarks.generators.justification.JustificationExponential;
import alpha.benchmarks.generators.justification.JustificationLocoMove;
import alpha.benchmarks.generators.justification.JustificationProjection;

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
			case "agg:exponentialsizecount":
				return new ExponentialSizeCount();
			case "agg:connectedsubgraph":
				return new ConnectedSubGraph();
			case "agg:graphcolorcounting":
				return new GraphColorCounting();
			case "agg:simulation":
				return new Simulation();
			case "just:exponential":
				return new JustificationExponential();
			case "just:locomove":
				return new JustificationLocoMove();
			case "just:projection":
				return new JustificationProjection();
            default:
                return null;
        }
    }
}
