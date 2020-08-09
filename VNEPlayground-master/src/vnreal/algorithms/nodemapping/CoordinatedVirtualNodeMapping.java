package vnreal.algorithms.nodemapping;

import edu.uci.ics.jung.graph.util.EdgeType;
import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.*;
import vnreal.constraints.demands.AbstractDemand;
import vnreal.constraints.demands.CpuDemand;
import vnreal.constraints.resources.AbstractResource;
import vnreal.constraints.resources.BandwidthResource;
import vnreal.constraints.resources.CpuResource;
import vnreal.core.Consts;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: CalvonBai
 * Date: 2019/9/4
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public class CoordinatedVirtualNodeMapping extends AbstractNodeMapping{

    private int distance;
    private boolean randomize;
    private SubstrateNetwork augmentedSubstrate;    // Used to store the augmented graph
    private static Map<VirtualNode, List<SubstrateNode>> candiNodes;	// Used to store the map between vnodes and its candidate snodes
    private static Map<VirtualNode, SubstrateNode> augmentedCorrespond;  // Used to store the map between vnodes and its meta nodes

    public CoordinatedVirtualNodeMapping(int distance, boolean randomize){
        this.distance = distance;
        this.randomize = randomize;

    }

    @Override
    protected boolean nodeMapping(SubstrateNetwork sNet, VirtualNetwork vNet) {

        // Randomly obtain the dataFileName from 0-2019
        Random intGenerator = new Random();
        String dataFileName = Consts.LP_SOLVER_DATAFILE
                + Integer.toString(intGenerator.nextInt(2019)) + ".dat";

        candiNodes = new LinkedHashMap<VirtualNode, List<SubstrateNode>>();
        LpSolver problemSolver = new LpSolver();
        List<VirtualNode> unMappedVirtualNodes = super.getUnmappedvNodes();
        List<SubstrateNode> unMappedSubstrateNodes = super.getUnmappedsNodes();

        if (augmentedCreation(sNet, unMappedVirtualNodes,
                unMappedSubstrateNodes, distance)) {

            Map<List<String>, Double> solverResult;
            Map<List<String>, Double> solverResultFlow;
            dataSolverFile lpNodeMappingData = new dataSolverFile(
                    Consts.LP_SOLVER_FOLDER + dataFileName);

            lpNodeMappingData.createDataSolverFile(augmentedSubstrate, sNet,
                    vNet, nodeMapping, true);


//            problemSolver.solve(Consts.LP_SOLVER_FOLDER,
//                    Consts.LP_VINEYARDNODEMAPPING_MODEL, dataFileName);

            // Node mapping result  Map<List<String>, Double> , List<String> is like("47","0")
            solverResult = MiscelFunctions.processSolverResult(
                    problemSolver.getSolverResult(), "lambda[]");

            // Link mapping result Map<List<String>, Double> , List<String> is like("47","69","0","6")
            solverResultFlow = MiscelFunctions.processSolverResult(
                    problemSolver.getSolverResultFlow(), "flow[]");

            return virtualNodeMapping(sNet, unMappedVirtualNodes, augmentedSubstrate,
                     solverResult, solverResultFlow);
        } else {
            return false;
        }
    }

    /**
     * Augmented substrate creation
     *
     * @param unmappedvNodes
     *            (updated in the abstract node mapping)
     * @param unmappedsNodes
     *            (updated in the abstract node mapping)
     * @param distance
     * @return
     */
    private boolean augmentedCreation(SubstrateNetwork sNet,
                                      List<VirtualNode> unmappedvNodes,
                                      List<SubstrateNode> unmappedsNodes, int distance) {
        List<SubstrateNode> candidates = new LinkedList<SubstrateNode>();
        CopyNetwork copy = new CopyNetwork(sNet,true,false);
        augmentedCorrespond = new LinkedHashMap<VirtualNode, SubstrateNode>();
        augmentedSubstrate = copy.returnSubsCopy();
        CpuResource cpuRes;
        SubstrateNode newSubNode, tmpCandidate;
        BandwidthResource bwRes;
        double maxLinkResource = maxLinkResource(sNet);
        SubstrateLink augSubsLink1;
        // The augmented substrate with the candidate nodes is created
        for (Iterator<VirtualNode> vn = unmappedvNodes.iterator(); vn.hasNext();) {
            VirtualNode tmpvNode = vn.next();

            // 1. Find candidate nodes whose location demand satisfy the requirment
            for (AbstractDemand dem : tmpvNode) {
                if (dem instanceof CpuDemand) {
                    candidates = findFulfillingNodes(tmpvNode, dem,
                            unmappedsNodes, distance);
                    candiNodes.put(tmpvNode, candidates);
                }
            }

            // 2.Create augmented graph with meta nodes

            if (!candidates.isEmpty()) {
                newSubNode = new SubstrateNode();
                cpuRes = new CpuResource(newSubNode);

                double tmpcpu = 0.0;
                for (AbstractDemand dem : tmpvNode) {
                    if (dem instanceof CpuDemand) {
                        tmpcpu = ((CpuDemand) dem).getDemandedCycles();
                    }
                }

                cpuRes.setCycles(tmpcpu);
                newSubNode.add(cpuRes);
                augmentedSubstrate.addVertex(newSubNode);
                augmentedCorrespond.put(tmpvNode, newSubNode);
                nodeMapping.put(tmpvNode, newSubNode);
                for (Iterator<SubstrateNode> itSubNode = candidates.iterator(); itSubNode
                        .hasNext();) {
                    augSubsLink1 = new SubstrateLink();
                    tmpCandidate = itSubNode.next();
                    bwRes = new BandwidthResource(augSubsLink1);
                    // This value should be modified to be always a very
                    // high value (infinity)

                    bwRes.setBandwidth(maxLinkResource);
                    augSubsLink1.add(bwRes);
                    augmentedSubstrate.addEdge(augSubsLink1, newSubNode,
                            tmpCandidate, EdgeType.UNDIRECTED);

                    // FIXME: This value should be modified to be always a very
                    // high value
                    // in comparison with the values of the substrate network
                    // cpu resource.
                }
            } else {
                return false;
            }

        }
        return true;
    }

    /**
     * This method receives the output of the LP solver and performs the node
     * mapping.
     * Ω(n^v) is the corresponding n^s which satisfy the loc constraint,which is cnadiNodes
     * μ(n^v) is the meta node of n^s, which is augmentcorrespond
     */
    private boolean virtualNodeMapping(SubstrateNetwork sNet, List<VirtualNode> unmappedvNodes,
                                       SubstrateNetwork augmentedSub,
                                       Map<List<String>, Double> result,
                                       Map<List<String>, Double> resultFlow) {

        double vtmp;
        double totalWeight = 0.0;
        List<String> tmpValues;
        SubstrateNode unode,z,mappedNode;    // μ(n^v)
        Map<SubstrateNode, Double> candiNodesPz = new LinkedHashMap<SubstrateNode, Double>();

        for(VirtualNode vnode : unmappedvNodes){
            unode = augmentedCorrespond.get(vnode);

            for(Iterator<List<String>> cad = resultFlow.keySet().iterator(); cad
                    .hasNext();){
                tmpValues = cad.next();
                vtmp = resultFlow.get(tmpValues);
                if(vtmp != 0.0 && (unode.getId() == Long
                        .parseLong(tmpValues.get(1)))||unode.getId() == Long.parseLong(tmpValues.get(2))) {


                    if(unode.getId() == Long.parseLong(tmpValues.get(1))){
                        z = getSubstrateNode(sNet, Long.parseLong(tmpValues.get(2)));
                        if(candiNodesPz.get(z) != null){
                            totalWeight = candiNodesPz.get(z) + vtmp;
                        }else{
                            totalWeight = vtmp;
                        }

                        candiNodesPz.put(z,totalWeight);

                    }

                    if(unode.getId() == Long.parseLong(tmpValues.get(2))){
                        z = getSubstrateNode(sNet, Long.parseLong(tmpValues.get(1)));
                        if(candiNodesPz.get(z) != null){
                            totalWeight = candiNodesPz.get(z) + vtmp;
                        }else{
                            totalWeight = vtmp;
                        }

                        candiNodesPz.put(z,totalWeight);

                    }

                }

            }

            // Calculate the x(μ—>z)*f
            for(Iterator<List<String>> cad = result.keySet().iterator(); cad.hasNext();){

                tmpValues = cad.next();
                vtmp = result.get(tmpValues);

                if(unode.getId() == Long.parseLong(tmpValues.get(0))){
                    z = getSubstrateNode(sNet, Long.parseLong(tmpValues.get(1)));
                    if (candiNodesPz.get(z) != null)
                        candiNodesPz.put(z, vtmp * candiNodesPz.get(z));
                }
            }

            // Whether is randomize / deterministic
            if(randomize){
                mappedNode = randomizedElection(candiNodesPz,candiNodes.get(vnode),vnode);
            }else{
                mappedNode = greatest(candiNodesPz,candiNodes.get(vnode), vnode);
            }

            if (mappedNode != null) {
                if (NodeLinkAssignation.vnm(vnode, mappedNode)) {
                    nodeMapping.put(vnode, mappedNode);
                } else {
                    throw new AssertionError("But we checked before!");
                }

            } else {
                // Node mapping can not be performed.
                return false;
            }

            candiNodesPz.clear();

        }

        return true;
    }


    private SubstrateNode greatest(Map<SubstrateNode, Double> candiNodesPz, List<SubstrateNode> candiNodes,VirtualNode currVNode){
        double greatest = 0.0;
        double greatestAR = 0.0;
        SubstrateNode greatestSNode = null;
        SubstrateNode tmpSNode;



        for(Iterator<SubstrateNode> it = candiNodesPz.keySet().iterator();it.hasNext();){
            tmpSNode = it.next();
            if (candiNodesPz.get(tmpSNode) > greatest
                    && !nodeMapping.containsValue(tmpSNode)) {
                greatest = candiNodesPz.get(tmpSNode);
                greatestSNode = tmpSNode;
            }
        }

        if (greatestSNode == null) {
            for (Iterator<SubstrateNode> it = candiNodes.iterator(); it
                    .hasNext();) {
                tmpSNode = it.next();
                if (getAvResources(tmpSNode) > greatestAR
                        && !nodeMapping.containsValue(tmpSNode)) {
                    greatestAR = getAvResources(tmpSNode);
                    greatestSNode = tmpSNode;
                }

            }
        }

        return greatestSNode;
    }


    private SubstrateNode randomizedElection(
            Map<SubstrateNode, Double> candiNodesPz,
            List<SubstrateNode> candiNodes, VirtualNode currVNode) {

        SubstrateNode tmpSnode;
        SubstrateNode tempCandiSNode = null;
        double psum = 0.0;
        double greaterAR = 0;

        if(candiNodesPz.isEmpty()){
            for (SubstrateNode tempSNode : candiNodes) {
                if (getAvResources(tempSNode) > greaterAR
                        && !nodeMapping.containsValue(tempSNode)) {
                    greaterAR = getAvResources(tempSNode);
                    tempCandiSNode = tempSNode;
                }
            }
            return tempCandiSNode;
        }


        for(Iterator<SubstrateNode> it = candiNodesPz.keySet()
                .iterator();it.hasNext();){
            tmpSnode = it.next();
            psum += candiNodesPz.get(tmpSnode);
        }
        if(psum != 0){
            for(Iterator<SubstrateNode> it = candiNodesPz.keySet()
                    .iterator();it.hasNext();){
                tmpSnode = it.next();
                double pz = candiNodesPz.get(tmpSnode);
                candiNodesPz.put(tmpSnode, pz / psum);
            }
        }


        tempCandiSNode = randomChoose(candiNodesPz);

        if (tempCandiSNode == null) {
            for (Iterator<SubstrateNode> it = candiNodes.iterator(); it
                    .hasNext();) {
                tmpSnode = it.next();
                if (getAvResources(tmpSnode) > greaterAR
                        && !nodeMapping.containsValue(tmpSnode)) {
                    greaterAR = getAvResources(tmpSnode);
                    tempCandiSNode = tmpSnode;
                }
            }
        }


        return tempCandiSNode;
    }

    /**
     * Method that returns the available resources (AR) of one substrate node.
     * AR is defined as the CPU capacity of the resource, multiplied by the sum
     * of the bandwidth of its incident arcs. Input: Substrate Node Output: The
     * available resources of the node (Available resources are calculated as
     * the product of the node's remaining CPU capacity and the sum of the
     * remaining bandwidth of its incident links)
     *
     * @param sNode
     * @return
     */
    private double getAvResources(SubstrateNode sNode) {
        double sumIncBW = 0, nodeCpu = 0;
        List<SubstrateLink> augmentedLinks = new LinkedList<SubstrateLink>(
                augmentedSubstrate.getEdges());
        SubstrateLink tmpSubsLink;
        for (Iterator<SubstrateLink> itSubLink = augmentedLinks.iterator(); itSubLink
                .hasNext();) {
            tmpSubsLink = itSubLink.next();
            if (sNode.equals(augmentedSubstrate.getEndpoints(tmpSubsLink).getFirst())) {
                for (AbstractResource res : tmpSubsLink) {
                    if (res instanceof BandwidthResource)
                        sumIncBW += ((BandwidthResource) res)
                                .getAvailableBandwidth();

                }
            }

        }
        for (AbstractResource res : sNode) {
            if (res instanceof CpuResource)
                nodeCpu = ((CpuResource) res).getAvailableCycles();

        }
        return (nodeCpu * sumIncBW);
    }

    /**
     * Method to find the substrate nodes that can be considered to be mapped to
     * one virtual node(satisfy the loc & CPU demands)
     *
     * @param vNode
     * @param dem
     * @param filtratedsNodes
     * @param dist
     * @return
     */

    private List<SubstrateNode> findFulfillingNodes(VirtualNode vNode,
                                                    AbstractDemand dem, List<SubstrateNode> filtratedsNodes, int dist) {
        List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
        for (SubstrateNode n : filtratedsNodes) {
            if (nodeDistance(vNode, n, dist)) {
                for (AbstractResource res : n)
                    if (res.accepts(dem) && res.fulfills(dem)) {
                        nodes.add(n);
                    }
            }
        }
        return nodes;
    }

    /**
     * Method to know if a substrate node is located in a distance less or equal
     * than the predefined distance parameter
     *
     * @param vNode
     * @param sNode
     * @param distance
     * @return
     */
    private boolean nodeDistance(VirtualNode vNode, SubstrateNode sNode,
                                 int distance) {
        if (distance < 0)
            return true;		// Interpret distance < 0 as ignoring distance
        double dis;
        dis = Math.pow(sNode.getCoordinateX() - vNode.getCoordinateX(), 2)
                + Math.pow(sNode.getCoordinateY() - vNode.getCoordinateY(), 2);
        if (Math.sqrt(dis) <= distance) {
            return true;
        } else {
            return false;
        }
    }

    private SubstrateNode getSubstrateNode(SubstrateNetwork sNet, long id) {
        for (SubstrateNode n : sNet.getVertices()) {
            if (n.getId() == id) {
                return n;
            }
        }
        return null;
    }


    private SubstrateNode randomChoose(Map<SubstrateNode, Double> candiNodesPz){
        double r = Math.random();
        double sum = 0.0;
        SubstrateNode tmpSNode;

        for(Iterator<SubstrateNode> it = candiNodesPz.keySet().iterator();it.hasNext();){
            tmpSNode = it.next();
            sum += candiNodesPz.get(tmpSNode);
            if(sum > r){
                return tmpSNode;
            }

        }

        return null;

    }

    // Returns the maximum node resource multiplied by 100
    private Double maxNodeResource(SubstrateNetwork sNet) {
        double max = 0;
        for (SubstrateNode n : sNet.getVertices()) {
            for (AbstractResource res : n) {
                // Change it, and instance of abstract resource
                if (res instanceof CpuResource) {
                    if ((((CpuResource) res).getAvailableCycles()) >= max)
                        max = (((CpuResource) res).getAvailableCycles());

                }
            }
        }
        return (max * 100);
    }

    // Returns the maximum link resource multiplied by 100
    private Double maxLinkResource(SubstrateNetwork sNet) {
        double max = 0;
        for (Iterator<SubstrateLink> links = sNet.getEdges().iterator(); links
                .hasNext();) {
            SubstrateLink temp = links.next();
            for (AbstractResource res : temp) {
                if (res instanceof BandwidthResource) {
                    if ((((BandwidthResource) res).getAvailableBandwidth()) >= max)
                        max = (((BandwidthResource) res)
                                .getAvailableBandwidth());
                }
            }

        }
        return (max * 100);
    }


}
