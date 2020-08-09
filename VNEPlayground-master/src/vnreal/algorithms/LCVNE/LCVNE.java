package vnreal.algorithms.LCVNE;

import vnreal.algorithms.AlgorithmParameter;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.algorithms.linkmapping.FoolLinkMapping;
import vnreal.algorithms.nodemapping.LCVNECoordinatedMapping;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.network.NetworkStack;

/**
 * Created by IntelliJ IDEA.
 * User: CalvonBai
 * Date: 2019/9/10
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
public class LCVNE extends GenericMappingAlgorithm{
    private static int DEFAULT_DIST = -1; // No distance calculation
    private static int DEFAULT_KSP = 1;

    public LCVNE(AlgorithmParameter param){

        int distance = param.getInteger("distance", DEFAULT_DIST);
        int k = param.getInteger("kShortestPath", DEFAULT_KSP);

        this.nodeMappingAlgorithm = new LCVNECoordinatedMapping(distance,k);
        this.linkMappingAlgorithm = new FoolLinkMapping();
    }

    @Override
    public void setStack(NetworkStack stack) {
        this.ns = MiscelFunctions.sortByRevenues(stack);
    }

}
