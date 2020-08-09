package vnreal.algorithms.ViNEYard;

import vnreal.algorithms.AlgorithmParameter;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.algorithms.linkmapping.kShortestPathLinkMapping;
import vnreal.algorithms.nodemapping.CoordinatedVirtualNodeMapping;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.network.NetworkStack;

/**
 * This is a class
 */


public class CoordinatedMapping extends GenericMappingAlgorithm {
	
	// Default values
	private static int DEFAULT_DIST = -1; // No distance calculation
	private static boolean DEFAULT_RANDOMIZE = false; // No randomization
	private static boolean DEFAULT_PS = false; // No path splitting
	private static int DEFAULT_KSP = 1;
	private static boolean DEFAULT_EPPSTEIN = true;
	
	public CoordinatedMapping(AlgorithmParameter param) {
		
		int distance = param.getInteger("distance", DEFAULT_DIST);
		boolean randomize = param.getBoolean("randomize", DEFAULT_RANDOMIZE);

		this.nodeMappingAlgorithm = new CoordinatedVirtualNodeMapping(distance, randomize);


		if (param.getBoolean("PathSplitting", DEFAULT_PS)) {
			// this.linkMappingAlgorithm = new PathSplittingVirtualLinkMapping();

		} else {
			int k = param.getInteger("kShortestPath", DEFAULT_KSP);
			boolean eppstein = param.getBoolean("eppstein", DEFAULT_EPPSTEIN);
			this.linkMappingAlgorithm = new kShortestPathLinkMapping(k, eppstein);
		}

	}
	
	@Override
	public void setStack(NetworkStack stack) {
		this.ns = MiscelFunctions.sortByRevenues(stack);
	}

}
