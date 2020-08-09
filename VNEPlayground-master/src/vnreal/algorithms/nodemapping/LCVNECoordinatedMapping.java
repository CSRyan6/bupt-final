package vnreal.algorithms.nodemapping;

import edu.uci.ics.jung.graph.util.EdgeType;
import mulavito.algorithms.shortestpath.ksp.Eppstein;
import mulavito.algorithms.shortestpath.ksp.KShortestPathAlgorithm;
import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.LinkWeight;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.constraints.demands.AbstractDemand;
import vnreal.constraints.demands.BandwidthDemand;
import vnreal.constraints.demands.CpuDemand;
import vnreal.constraints.resources.AbstractResource;
import vnreal.constraints.resources.BandwidthResource;
import vnreal.constraints.resources.CpuResource;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: CalvonBai
 * Date: 2019/9/11
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class LCVNECoordinatedMapping extends AbstractNodeMapping {

    private int distance;
    private int k;
    private static Map<VirtualNode, List<SubstrateNode>> candiNodes;
    private static SubstrateNetwork cg = new SubstrateNetwork(false,false);
    private static KShortestPathAlgorithm<SubstrateNode, SubstrateLink> kshortest;
    private static Map<List<SubstrateLink>,SubstrateNode> cgnodes;    // Used to store cgnodes
    private static Map<VirtualLink,List<List<SubstrateLink>>> pather;
    private static Map<SubstrateNode,List<List<SubstrateLink>>> pathvs;
    private static List<SubstrateNode> maxclique;


    public LCVNECoordinatedMapping(int distance,int k ){
        this.distance = distance;
        this.k = k;
    }


    @Override
    protected boolean nodeMapping(SubstrateNetwork sNet, VirtualNetwork vNet){

        List<VirtualNode> unMappedVirtualNodes = super.getUnmappedvNodes();
        List<SubstrateNode> unMappedSubstrateNodes = super.getUnmappedsNodes();

        candiNodes = new LinkedHashMap<>();
        cgnodes = new LinkedHashMap<>();
        pather = new LinkedHashMap<>();
        pathvs = new LinkedHashMap<>();
        kshortest = candiSLink(sNet);
        maxclique = new LinkedList<>();


        if(originalCandidate(unMappedVirtualNodes,unMappedSubstrateNodes,distance)){
            preprocessing(sNet,vNet);
            if(constructCG(sNet,vNet)){
                return MCMC(sNet,vNet);
            }else{
                return false;
            }
        }else{
            return false;
        }


    }

    private boolean originalCandidate(List<VirtualNode> unmappedvNodes, List<SubstrateNode> unmappedsNodes, int distance){
        List<SubstrateNode> candidates = new LinkedList<SubstrateNode>();
        for(Iterator<VirtualNode> vn = unmappedvNodes
                .iterator();vn.hasNext();){
            VirtualNode tmpvNode = vn.next();
            candidates = findFulfillingNodes(tmpvNode,
                    unmappedsNodes, distance);
            candiNodes.put(tmpvNode, candidates);

            if(candidates.isEmpty()){
                return false;
            }

        }
        return true;
    }

    /**
     * Method to find the substrate nodes that can be considered to be mapped to
     * one virtual node(satisfy the loc)
     *
     * @param vNode
     * @param filtratedsNodes
     * @param dist
     * @return
     */

    private List<SubstrateNode> findFulfillingNodes(VirtualNode vNode, List<SubstrateNode> filtratedsNodes, int dist) {
        List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
        for (SubstrateNode n : filtratedsNodes) {
            if (nodeDistance(vNode, n, dist)) {
                nodes.add(n);
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

    // O(|V^r|*|V^s|^2)
    private void preprocessing(SubstrateNetwork sNet, VirtualNetwork vNet){

        Map<SubstrateNode,List<VirtualNode>> temp = new LinkedHashMap<>();
        VirtualNode tmpvNode;
        SubstrateNode tmpsNode;
        double tmpCdem = 0.0;
        double tmpCres = 0.0;
        for(Iterator<VirtualNode> vnodes = vNet.getVertices()
                .iterator();vnodes.hasNext();){
            tmpvNode = vnodes.next();
            for(Iterator<SubstrateNode> snodes = candiNodes
                    .get(tmpvNode).iterator();snodes.hasNext();){
                tmpsNode = snodes.next();

                for (AbstractDemand dem : tmpvNode) {
                    if (dem instanceof CpuDemand) {
                        tmpCdem = ((CpuDemand) dem).getDemandedCycles();
                    }
                }

                for (AbstractResource res : tmpsNode) {
                    if (res instanceof CpuResource) {
                        tmpCres = ((CpuResource) res)
                                .getAvailableCycles();
                    }
                }


                if(tmpCdem > tmpCres){
                    candiNodes.get(tmpvNode).remove(tmpsNode);
                }
            }

        }

        for(Iterator<VirtualNode> vnodes = vNet.getVertices()
                .iterator();vnodes.hasNext();) {
            tmpvNode = vnodes.next();
            for (Iterator<SubstrateNode> snodes = candiNodes
                    .get(tmpvNode).iterator(); snodes.hasNext();) {
                tmpsNode = snodes.next();

                if(temp.get(tmpsNode) == null){
                    List<VirtualNode> l = new LinkedList<>();
                    l.add(tmpvNode);
                    temp.put(tmpsNode,l);
                }else{
                    temp.get(tmpsNode).add(tmpvNode);
                }

            }
        }

        for(Iterator<SubstrateNode> snodes = sNet.getVertices()
                .iterator();snodes.hasNext();){
            tmpsNode = snodes.next();
            if(temp.get(tmpsNode).size() > 1){

                VirtualNode vmin = argmin(temp.get(tmpsNode),sNet);
                if(vmin != null){
                    temp.get(tmpsNode).remove(vmin);
                }

                // Some problem when deleting
                for(Iterator<VirtualNode> vnodes = temp.get(tmpsNode)
                        .iterator();vnodes.hasNext();){
                    tmpvNode = vnodes.next();
                    candiNodes.get(tmpvNode).remove(tmpsNode);
                }

            }
        }

    }

    // 给Per建一个Map
    private boolean constructCG(SubstrateNetwork sNet,VirtualNetwork vNet){
        VirtualLink tmpvLink;

        for(Iterator<VirtualLink> vlinks = vNet.getEdges()
                .iterator();vlinks.hasNext();){

            tmpvLink = vlinks.next();

            double tmpbw = 0.0;

            for (AbstractDemand dem : tmpvLink) {
                if (dem instanceof BandwidthDemand) {
                    tmpbw = ((BandwidthDemand) dem).getDemandedBandwidth();
                }
            }

            List<List<SubstrateLink>> paths = new LinkedList<>();


            for(Iterator<SubstrateNode> node1 = candiNodes.get(vNet
                    .getEndpoints(tmpvLink).getFirst())
                    .iterator();node1.hasNext();){
                SubstrateNode snode = node1.next();

                for(Iterator<SubstrateNode> node2 = candiNodes
                        .get(vNet.getEndpoints(tmpvLink).getSecond())
                        .iterator();node2.hasNext();){
                    SubstrateNode dnode = node2.next();

                    List<List<SubstrateLink>> tmpPaths = new LinkedList<>(kshortest.getShortestPaths(snode, dnode, k));
                    paths.addAll(tmpPaths);

                }
            }

            pather.put(tmpvLink,paths);


            for(Iterator<List<SubstrateLink>> listp = paths
                    .iterator();listp.hasNext();){
                List<SubstrateLink> path = listp.next();

                if(minbw(path) >= tmpbw){

                    SubstrateNode node = new SubstrateNode();
                    cgnodes.put(path,node);
                    assertTrue(cg.addVertex(node));
                }else{
                    paths.remove(path);
                }

            }

            if(paths.isEmpty()){
                return false;
            }

        }



        for(Iterator<SubstrateNode> sNode1 = sNet.getVertices()
                .iterator();sNode1.hasNext();){
            SubstrateNode tmpsNode1 = sNode1.next();
            List<List<SubstrateLink>> paths = new LinkedList<>();

            for(Iterator<SubstrateNode> sNode2 = sNet.getVertices()
                    .iterator();sNode2.hasNext();){
                SubstrateNode tmpsNode2 = sNode2.next();

                if(tmpsNode1 == tmpsNode2){
                    break;
                }

                paths.addAll(kshortest.getShortestPaths(tmpsNode1, tmpsNode2, k));
                paths.addAll(kshortest.getShortestPaths(tmpsNode2, tmpsNode1, k));


            }

            pathvs.put(tmpsNode1,paths);

        }





        for(Iterator<VirtualLink> vLinks1 = vNet.getEdges()
                .iterator();vLinks1.hasNext();) {

            VirtualLink er1 = vLinks1.next();

            for(Iterator<VirtualLink> vLinks2 = vNet.getEdges()
                    .iterator();vLinks2.hasNext();){
                VirtualLink er2 = vLinks2.next();

                if(er1 == er2){
                    break;
                }

                if(isNeiborLink(er1,er2,vNet)){
                    for(Iterator<SubstrateNode> snodes = candiNodes
                            .get(getVr(er1,er2,vNet))
                            .iterator();snodes.hasNext();){

                        SubstrateNode vs =snodes.next();

                        List<List<SubstrateLink>> pather1vs = new LinkedList<>(pather.get(er1));
                        pather1vs.retainAll(pathvs.get(vs));

                        List<List<SubstrateLink>> pather2vs = new LinkedList<>(pather.get(er2));
                        pather2vs.retainAll(pathvs.get(vs));

                        for(Iterator<List<SubstrateLink>> it1 = pather1vs
                                .iterator();it1.hasNext();){

                            List<SubstrateLink> p1 = it1.next();
                            SubstrateNode tmpn1 = cgnodes.get(p1);

                            for(Iterator<List<SubstrateLink>> it2 = pather2vs
                                    .iterator();it2.hasNext();){
                                List<SubstrateLink> p2 = it2.next();

                                SubstrateNode tmpn2 = cgnodes.get(p2);

                                SubstrateLink link = new SubstrateLink();


                                assertTrue(cg.addEdge(link,tmpn1,tmpn2, EdgeType.UNDIRECTED));

                            }

                        }

                    }
                }else{

                    List<List<SubstrateLink>> pather1vs = new LinkedList<>(pather.get(er1));
                    List<List<SubstrateLink>> pather2vs = new LinkedList<>(pather.get(er2));
                    for(Iterator<List<SubstrateLink>> it1 = pather1vs
                            .iterator();it1.hasNext();){

                        List<SubstrateLink> p1 = it1.next();
                        SubstrateNode tmpn1 = cgnodes.get(p1);

                        for(Iterator<List<SubstrateLink>> it2 = pather2vs
                                .iterator();it2.hasNext();){
                            List<SubstrateLink> p2 = it2.next();

                            SubstrateNode tmpn2 = cgnodes.get(p2);

                            SubstrateLink link = new SubstrateLink();


                            assertTrue(cg.addEdge(link,tmpn1,tmpn2, EdgeType.UNDIRECTED));
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean MCMC(SubstrateNetwork sNet,VirtualNetwork vNet){
        // cg is also an input;

        List<List<SubstrateLink>> pn = new LinkedList<>(fsp());

        List<List<SubstrateLink>> tempp = new LinkedList<>();

        Map<VirtualLink,Double> linkSortedByBW = new LinkedHashMap<>();
        double tmpbw = 0.0;

        for(Iterator<VirtualLink> links = vNet.getEdges()
                .iterator();links.hasNext();){
            VirtualLink tmpvLink = links.next();

            for(AbstractDemand dem : tmpvLink){
                if (dem instanceof BandwidthDemand) {
                    tmpbw = ((BandwidthDemand) dem).getDemandedBandwidth();
                }
            }
            linkSortedByBW.put(tmpvLink,tmpbw);

        }

        sortByValueDescending(linkSortedByBW);

        for(Iterator<VirtualLink> itlinks = linkSortedByBW.keySet()
                .iterator();itlinks.hasNext();){
            VirtualLink tmpvLink = itlinks.next();
            tempp = new LinkedList<>(pather.get(tmpvLink));
            tempp.retainAll(pn);
            List<List<SubstrateLink>> psharp = new LinkedList<>(tempp);





            if(psharp.isEmpty()){
                return false;
            }

            // select pm using greedy
            List<SubstrateLink> pm = new LinkedList<>();
            int size = sNet.getEdgeCount();
            for(Iterator<List<SubstrateLink>> it = psharp
                    .iterator();it.hasNext();){
                List<SubstrateLink> tmpsLink = it.next();

                if(tmpsLink.size() <= size){
                    pm = tmpsLink;
                }


            }








            // M
            SubstrateNode pn1 = fspinver(pm);
            maxclique.add(pn1);



            // PN
            List<List<SubstrateLink>> tmpPs = new LinkedList<>();
            for(Iterator<SubstrateLink> itlink = cg.getEdges()
                    .iterator();itlink.hasNext();){

                SubstrateLink tmpsLink = itlink.next();


                if(cg.getEndpoints(tmpsLink).contains(pn1)){
                    SubstrateNode tmpsNode = null;

                    if(cg.getEndpoints(tmpsLink).getFirst() == pn1){

                        tmpsNode = cg.getEndpoints(tmpsLink).getSecond();

                    }

                    if(cg.getEndpoints(tmpsLink).getSecond() == pn1){
                        tmpsNode = cg.getEndpoints(tmpsLink).getFirst();
                    }

                    for(Iterator<List<SubstrateLink>> it = cgnodes.keySet()
                            .iterator();it.hasNext();){
                        List<SubstrateLink> path = it.next();

                        if(cgnodes.get(path) == tmpsNode){
                            tmpPs.add(path);
                            break;
                        }

                    }


                }


            }

            pn.retainAll(tmpPs);


            List<SubstrateLink> tmpPath = null;
            for(Iterator<List<SubstrateLink>> it = cgnodes.keySet()
                    .iterator();it.hasNext();){
                List<SubstrateLink> path = it.next();
                if(cgnodes.get(path) == pn1){


                    tmpPath = path;
                    break;

                }

            }



            // update bw resources
            if (!NodeLinkAssignation.vlm(tmpvLink,tmpPath)) {
                throw new AssertionError("But we checked before!");

            }else{

                for(SubstrateNode n : candiNodes.get(vNet.getEndpoints(tmpvLink).getFirst())){
                    if(sNet.getEndpoints(tmpPath.get(0)).contains(n)){
                        nodeMapping.put(vNet.getEndpoints(tmpvLink).getFirst(),n);
                        break;
                    }
                }

                for(SubstrateNode n : candiNodes.get(vNet.getEndpoints(tmpvLink).getSecond())){
                    if(sNet.getEndpoints(tmpPath.get(0)).contains(n)){
                        nodeMapping.put(vNet.getEndpoints(tmpvLink).getSecond(),n);
                        break;
                    }
                }





            }



        }

        // update cpu
        for(Iterator<VirtualNode> it = nodeMapping.keySet().iterator();it.hasNext();){
            VirtualNode vn = it.next();
            if (!NodeLinkAssignation.vnm(vn,nodeMapping.get(vn))) {
                throw new AssertionError("But we checked before!");

            }


        }




        return true;
    }


    private List<List<SubstrateLink>> fsp(){

        List<List<SubstrateLink>> pn = new LinkedList<>();


        for(Iterator<SubstrateNode> it = cg.getVertices()
                .iterator();it.hasNext();){

            SubstrateNode tmpsNode = it.next();

            for(Iterator<List<SubstrateLink>> itlinks = cgnodes.keySet()
                    .iterator();itlinks.hasNext();){
                List<SubstrateLink> link = itlinks.next();

                if(cgnodes.get(link) == tmpsNode){
                    pn.add(link);
                }

            }




        }

        return pn;
    }


    private SubstrateNode fspinver(List<SubstrateLink> path){

        return cgnodes.get(path);
    }


    private boolean isNeiborLink(VirtualLink vLink1,VirtualLink vLink2 , VirtualNetwork vNet){


        List<VirtualNode> vnodes1 = new ArrayList<>(vNet.getIncidentVertices(vLink1)) ;
        List<VirtualNode> vnodes2 = new ArrayList<>(vNet.getIncidentVertices(vLink2)) ;
        vnodes1.retainAll(vnodes2);
        if(vnodes1 == null || vnodes1.size() == 0){
            return false;
        }
        return true;

    }

    private VirtualNode getVr(VirtualLink vLink1,VirtualLink vLink2 , VirtualNetwork vNet){
        List<VirtualNode> vnodes1 = new ArrayList<>(vNet.getIncidentVertices(vLink1)) ;
        List<VirtualNode> vnodes2 = new ArrayList<>(vNet.getIncidentVertices(vLink2));
        VirtualNode result = null;
        vnodes1.retainAll(vnodes2);
        for(Iterator<VirtualNode> nodes = vnodes1.iterator();nodes.hasNext();){
            result = nodes.next();
            break;
        }
        return result;
    }

    private double minbw(List<SubstrateLink> path){
        double minbw = -1.0;
        double tmpbw = 0.0;

        for(Iterator<SubstrateLink> links = path.iterator();links.hasNext();){
            SubstrateLink sLink = links.next();
            for (AbstractResource res : sLink) {
                if (res instanceof BandwidthResource) {
                    tmpbw = ((BandwidthResource) res).getAvailableBandwidth();
                }
            }
            if(minbw == -1.0){
                minbw = tmpbw;
            }

            if(tmpbw < minbw){
                minbw = tmpbw;

            }

        }

        return minbw;
    }

    private VirtualNode argmin(List<VirtualNode> vrlist, SubstrateNetwork sNet){
        VirtualNode vr = null;
        VirtualNode tmpvNode;
        int tmpsize = sNet.getVertexCount();
        for(Iterator<VirtualNode> vnodes = vrlist.iterator();vnodes.hasNext();){
            tmpvNode = vnodes.next();
            if(tmpsize >= candiNodes.get(tmpvNode).size() ){
                vr = tmpvNode;
                tmpsize = candiNodes.get(tmpvNode).size();
            }
        }

        return vr;
    }


    private KShortestPathAlgorithm<SubstrateNode, SubstrateLink> candiSLink(SubstrateNetwork sNet){


        LinkWeight linkWeight = new LinkWeight();
        KShortestPathAlgorithm<SubstrateNode, SubstrateLink> kshortestPaths = null;

        kshortestPaths = new Eppstein<SubstrateNode, SubstrateLink>(sNet, linkWeight);

        return kshortestPaths;

    }


    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }



}


