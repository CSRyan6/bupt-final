package vnreal.algorithms.simplify;

import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.constraints.demands.AbstractDemand;
import vnreal.constraints.demands.BandwidthDemand;
import vnreal.network.Link;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: CalvonBai
 * Date: 2019/7/24
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class Preprocess {

    public VirtualNetwork reduceLinks(VirtualNetwork vNet){

        Map<Link<?>, Double> tmpLink = getLinkBW(vNet);

        // 1. rank the link by the increasing bw and save in Queue
        Map<Link<?>, Double> SortedBW = MiscelFunctions.sortByValue(tmpLink);
        Stack<Link<?>> linkStack = new Stack<>();
        for(Link<?> l : SortedBW.keySet()){
            linkStack.push(l);
        }

        // 2. if(!queue.isEmpty())
        while(!linkStack.isEmpty()){

            // 3. pop & find triangle
            Link<?> tmp = linkStack.pop();

            if(triangle((VirtualLink)tmp,vNet)){
                //  delete edge in VN
                vNet.removeEdge((VirtualLink) tmp);
            }
            // linkStack should be ordered again.
            linkStack = order(linkStack);
        }

        return vNet;
    }

    // Key starts with 1
    public Boolean editNodePartition(VirtualNode vNode,int k,Map<Integer,ArrayList<VirtualNode>> nodePartition){
        if(nodePartition.get(k) != null){
            nodePartition.get(k).add(vNode);
            return true;
        }

        ArrayList<VirtualNode> tmpList = new ArrayList<>();
        tmpList.add(vNode);
        nodePartition.put(k,tmpList);
        return true;
    }

    public VirtualNetwork insertNode(VirtualNetwork vNet,Map<Integer,ArrayList<VirtualNode>> nodePartition){

        Map<String,VirtualNode> coreNodes = new HashMap<>();
        BandwidthDemand bwDem;
        int k = nodePartition.size();

        if(k > vNet.getVertices().size()){
            System.out.println("Not enough virtual nodes to support such k value");
            return vNet;
        }

        Iterator<Map.Entry<Integer,ArrayList<VirtualNode>>> entries = nodePartition.entrySet().iterator();
        int sum = 0;
        while(entries.hasNext()){
            Map.Entry<Integer,ArrayList<VirtualNode>> tmpEntry = entries.next();
            sum += tmpEntry.getValue().size();
        }

        if(sum != vNet.getVertexCount()){
            System.out.println("Not each node in partition");
            return vNet;
        }

        // Add all the core nodes into VN
        for(int i = 0; i < k; i++ ){

            VirtualNode v= new VirtualNode(vNet.getLayer());
            v.setName("u"+Integer.toString(i + 1));

            /*
            cpuDem = new CpuDemand(v);
            cpuDem.setDemandedCycles(0.0);
            assertTrue(v.add(cpuDem));
            */

            assertTrue(vNet.addVertex(v));

            // update the map of core nodes
            coreNodes.put(Integer.toString(i + 1), v);

        }

        VirtualNode tmpn1 = new VirtualNode(vNet.getLayer());
        VirtualNode tmpn2 = new VirtualNode(vNet.getLayer());

        for(int m = 0; m < k - 1; m++){

            for(int n = m + 1; n < k; n++){

                for(VirtualNode n1 : vNet.getVertices()){
                    if(n1.getName().equals("u" + Integer.toString(m +1) )){
                        tmpn1 = n1;
                        break;
                    }
                }

                for(VirtualNode n2 : vNet.getVertices()){
                    if(n2.getName().equals("u" + Integer.toString(n + 1) )){
                        tmpn2 = n2;
                        break;
                    }
                }

                if(tmpn1 == null || tmpn2 == null ){
                    System.out.println("Error addEdge");
                    return vNet;
                }

                // Algorithm 2 procedure 4&5
                ArrayList<VirtualNode> vi = nodePartition.get(m + 1);
                ArrayList<VirtualNode> vj = nodePartition.get(n + 1);
                double tmpbw = 0.0;
                double sumbw = 0.0;

                for(VirtualNode ni : vi){
                    for(VirtualNode nj : vj){

                        /*
                        if(getLink(ni,nj,tmpVnet) == null){
                            System.out.println("Error getLink");
                            return tmpVnet;
                        }
                        */

                        for(AbstractDemand res : getLink(ni,nj,vNet)){
                            if(res instanceof BandwidthDemand){
                                tmpbw = ((BandwidthDemand) res).getDemandedBandwidth();

                            }
                        }

                        sumbw += tmpbw;


                        vNet.removeEdge(getLink(ni,nj,vNet));

                    }
                }

                VirtualLink e = new VirtualLink(vNet.getLayer());
                bwDem = new BandwidthDemand(e);
                bwDem.setDemandedBandwidth(sumbw);
                assertTrue(e.add(bwDem));
                assertTrue(vNet.addEdge(e, tmpn1, tmpn2));
            }
        }

        VirtualNode ui = new VirtualNode(vNet.getLayer());
        double sumuv = 0.0;
        double tmpbwv = 0.0;

        for(int i = 1; i <= k; i++){
            ArrayList<VirtualNode> vi = nodePartition.get(i);
            for(VirtualNode n : vNet.getVertices()){
                if(n.getName().equals("u" + Integer.toString(i) )){
                    ui = n;
                    break;
                }
            }
            sumuv = 0.0;

            for(VirtualNode v : vi){
                for(VirtualNode otherv : vNet.getVertices()){
                    if(v.equals(otherv)){
                        continue;
                    }

                    /*
                    if(getLink(v,otherv,tmpVnet) == null){
                        System.out.println("Error getLink");
                        return tmpVnet;
                    }
                    */

                    for(AbstractDemand res : getLink(v,otherv,vNet)){
                        if(res instanceof BandwidthDemand){
                            tmpbwv = ((BandwidthDemand) res).getDemandedBandwidth();
                        }
                    }
                    sumuv += tmpbwv;
                    vNet.removeEdge(getLink(v,otherv,vNet));
                }

                VirtualLink e = new VirtualLink(vNet.getLayer());
                bwDem = new BandwidthDemand(e);
                bwDem.setDemandedBandwidth(sumuv);
                assertTrue(e.add(bwDem));
                assertTrue(vNet.addEdge(e, ui, v));


            }
        }

        return vNet;
    }

    // Used for link reduction
    private boolean triangle(VirtualLink vLink, VirtualNetwork vNet){

        boolean hasN = false;
        double bw = 0.0;

        for(AbstractDemand res : vLink){
            if(res instanceof BandwidthDemand){
                bw = ((BandwidthDemand) res).getDemandedBandwidth();
            }
        }

        VirtualNode node1 = vNet.getEndpoints(vLink).getFirst();
        VirtualNode node2 = vNet.getEndpoints(vLink).getSecond();

        for(VirtualNode n : vNet.getNeighbors(node1)){
            if(vNet.getNeighbors(node2).contains(n)){
                hasN = true;

                // Update bw
                updateBW(node2,n,bw,vNet);
                updateBW(node1,n,bw,vNet);
                break;
            }
        }

        if(!hasN){
            return false;
        }

        return true;
    }

    private VirtualLink getLink(VirtualNode node1, VirtualNode node2, VirtualNetwork vNet){
        for(VirtualLink l : vNet.getIncidentEdges(node1)){
            if(vNet.getIncidentEdges(node2).contains(l)){
                return l;
            }
        }
        System.out.println("no match link");
        return null;
    }

    private void updateBW(VirtualNode node1, VirtualNode node2, double bw , VirtualNetwork vNet){
        for(VirtualLink l : vNet.getIncidentEdges(node1)){
            if(vNet.getIncidentEdges(node2).contains(l)){
                for(AbstractDemand res : l){
                    if(res instanceof BandwidthDemand){
                        double tmpbw = ((BandwidthDemand) res).getDemandedBandwidth();
                        ((BandwidthDemand) res).setDemandedBandwidth(tmpbw + bw);
                    }
                }
            }
        }
    }

    // Get the map of bandwidth for all vLinks
    private Map<Link<?>, Double> getLinkBW(VirtualNetwork vNet){
        Map<Link<?>, Double> result = new LinkedHashMap<>();
        for(VirtualLink l : vNet.getEdges()){
            for(AbstractDemand res : l){
                if(res instanceof BandwidthDemand){
                    result.put(l,((BandwidthDemand) res).getDemandedBandwidth());
                }
            }
        }
        return result;
    }

    private Stack<Link<?>> order(Stack<Link<?>> s){
        if(s == null){
            return null;
        }
        Stack<Link<?>> t = new Stack<>();
        while(!s.empty()){
            Link<?> tmpa = s.pop();
            if(!t.empty()){
                Link<?> tmpb = t.peek();

                double a= 0.0;
                double b = 0.0;

                for(AbstractDemand res : (VirtualLink)tmpa){
                    if(res instanceof BandwidthDemand){
                        a = ((BandwidthDemand) res).getDemandedBandwidth();
                    }
                }

                for(AbstractDemand res : (VirtualLink)tmpb){
                    if(res instanceof BandwidthDemand){
                        b = ((BandwidthDemand) res).getDemandedBandwidth();
                    }
                }

                while( !t.empty() && a > b){
                    s.push(t.pop());
                }
            }
            t.push(tmpa);
        }
        return t;
    }
}
