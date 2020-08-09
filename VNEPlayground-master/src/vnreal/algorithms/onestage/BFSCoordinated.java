package vnreal.algorithms.onestage;

import vnreal.algorithms.AlgorithmParameter;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.algorithms.linkmapping.FoolLinkMapping;
import vnreal.algorithms.nodemapping.BFSNRCoordinatedMapping;
import vnreal.algorithms.nodemapping.BFSNRnodeMappingPathSplitting;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.network.NetworkStack;

/**
 * Created by IntelliJ IDEA.
 * User: CalvonBai
 * Date: 2019/9/10
 * Time: 19:11
 * To change this template use File | Settings | File Templates.
 */
public class BFSCoordinated  extends GenericMappingAlgorithm {
    // Default values
    private static int DEFAULT_DIST = -1; // No distance calculation
    private static boolean DEFAULT_PS = false; // No path splitting
    private static double DEFAULT_WCPU = 1;
    private static double DEFAULT_WBW = 1;
    private static int DEFAULT_MAXHOPS = 100;

    public BFSCoordinated(AlgorithmParameter param) {

        int distance = param.getInteger("distance", DEFAULT_DIST);
        int maxHops = param.getInteger("maxHops", DEFAULT_MAXHOPS);

        if (param.getBoolean("PathSplitting", DEFAULT_PS)) {
            double weightCpu = param.getDouble("weightCpu", DEFAULT_WCPU);
            double weightBw = param.getDouble("weightBw", DEFAULT_WBW);
            this.nodeMappingAlgorithm = new BFSNRnodeMappingPathSplitting(
                    weightCpu, weightBw, 0.0001, distance, maxHops);
        } else {
            this.nodeMappingAlgorithm = new BFSNRCoordinatedMapping(
                    0.0001, distance, maxHops);
        }

        // This link mapping is just a decoration, because this BFS algorithm is an one stage VNE algorithm, but if
        // this class wanna extends GenericMappingAlgorithm, it should use this fool link mapping phase.
        this.linkMappingAlgorithm = new FoolLinkMapping();
    }


    @Override
    public void setStack(NetworkStack stack) {
        this.ns = MiscelFunctions.sortByRevenues(stack);
    }
}
