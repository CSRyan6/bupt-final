package tests.algorithms.coordinatedmappingTest;
import org.junit.Test;
import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.AlgorithmParameter;
import vnreal.algorithms.ViNEYard.CoordinatedMapping;
import vnreal.algorithms.simplify.Preprocess;
import vnreal.algorithms.utils.dataSolverFile;
import vnreal.constraints.demands.BandwidthDemand;
import vnreal.constraints.demands.CpuDemand;
import vnreal.constraints.demands.IdDemand;
import vnreal.constraints.resources.BandwidthResource;
import vnreal.constraints.resources.CpuResource;
import vnreal.constraints.resources.IdResource;
import vnreal.core.Consts;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: CalvonBai
 * Date: 2019/7/23
 * Time: 11:34
 * To change this template use File | Settings | File Templates.
 */
public class CoordinatedMappingTest {

    private SubstrateNetwork createSubstrate() {
        SubstrateNetwork subsNetwork = new SubstrateNetwork(false,false);
        IdResource idRes;
        CpuResource cpuRes;

        SubstrateNode subsNode1 = new SubstrateNode();
        idRes = new IdResource(subsNode1, subsNetwork);
        idRes.setId(Integer.toString(1));
        assertTrue(subsNode1.add(idRes));
        cpuRes = new CpuResource(subsNode1);
        cpuRes.setCycles(100.0);
        assertTrue(subsNode1.add(cpuRes));
        assertTrue(subsNetwork.addVertex(subsNode1));

        SubstrateNode subsNode2 = new SubstrateNode();
        idRes = new IdResource(subsNode2, subsNetwork);
        idRes.setId(Integer.toString(2));
        assertTrue(subsNode2.add(idRes));
        cpuRes = new CpuResource(subsNode2);
        cpuRes.setCycles(100.0);
        assertTrue(subsNode2.add(cpuRes));
        assertTrue(subsNetwork.addVertex(subsNode2));

        SubstrateNode subsNode3 = new SubstrateNode();
        idRes = new IdResource(subsNode3, subsNetwork);
        idRes.setId(Integer.toString(3));
        assertTrue(subsNode3.add(idRes));
        cpuRes = new CpuResource(subsNode3);
        cpuRes.setCycles(100.0);
        assertTrue(subsNode3.add(cpuRes));
        assertTrue(subsNetwork.addVertex(subsNode3));

        SubstrateNode subsNode4 = new SubstrateNode();
        idRes = new IdResource(subsNode4, subsNetwork);
        idRes.setId(Integer.toString(4));
        assertTrue(subsNode4.add(idRes));
        cpuRes = new CpuResource(subsNode4);
        cpuRes.setCycles(100.0);
        assertTrue(subsNode4.add(cpuRes));
        assertTrue(subsNetwork.addVertex(subsNode4));

        SubstrateNode subsNode5 = new SubstrateNode();
        idRes = new IdResource(subsNode5, subsNetwork);
        idRes.setId(Integer.toString(5));
        assertTrue(subsNode5.add(idRes));
        cpuRes = new CpuResource(subsNode5);
        cpuRes.setCycles(100.0);
        assertTrue(subsNode5.add(cpuRes));
        assertTrue(subsNetwork.addVertex(subsNode5));

        // Create links
        BandwidthResource bwRes;
        SubstrateLink subsLink12 = new SubstrateLink();
        bwRes = new BandwidthResource(subsLink12);
        bwRes.setBandwidth(100.0);
        assertTrue(subsLink12.add(bwRes));
        assertTrue(subsNetwork.addEdge(subsLink12, subsNode1, subsNode2,UNDIRECTED));

        SubstrateLink subsLink23 = new SubstrateLink();
        bwRes = new BandwidthResource(subsLink23);
        bwRes.setBandwidth(100.0);
        assertTrue(subsLink23.add(bwRes));
        assertTrue(subsNetwork.addEdge(subsLink23, subsNode2, subsNode3,UNDIRECTED));

        SubstrateLink subsLink34 = new SubstrateLink();
        bwRes = new BandwidthResource(subsLink34);
        bwRes.setBandwidth(100.0);
        assertTrue(subsLink34.add(bwRes));
        assertTrue(subsNetwork.addEdge(subsLink34, subsNode3, subsNode4,UNDIRECTED));

        SubstrateLink subsLink45 = new SubstrateLink();
        bwRes = new BandwidthResource(subsLink45);
        bwRes.setBandwidth(100.0);
        assertTrue(subsLink45.add(bwRes));
        assertTrue(subsNetwork.addEdge(subsLink45, subsNode4, subsNode5,UNDIRECTED));

        SubstrateLink subsLink15 = new SubstrateLink();
        bwRes = new BandwidthResource(subsLink15);
        bwRes.setBandwidth(100.0);
        assertTrue(subsLink15.add(bwRes));
        assertTrue(subsNetwork.addEdge(subsLink15, subsNode1, subsNode5,UNDIRECTED));

        SubstrateLink subsLink24 = new SubstrateLink();
        bwRes = new BandwidthResource(subsLink24);
        bwRes.setBandwidth(100.0);
        assertTrue(subsLink24.add(bwRes));
        assertTrue(subsNetwork.addEdge(subsLink24, subsNode2, subsNode4,UNDIRECTED));

        return subsNetwork;
    }

    private List<VirtualNetwork> createVNDemands() {
        List<VirtualNetwork> vns = new LinkedList<VirtualNetwork>();
        IdDemand idDem;
        CpuDemand cpuDem;
        BandwidthDemand bwDem;

        VirtualNetwork vn0 = new VirtualNetwork(1,false,false);
        vns.add(vn0);

        VirtualNode vA = new VirtualNode(1);

        cpuDem = new CpuDemand(vA);
        cpuDem.setDemandedCycles(25.0);
        assertTrue(vA.add(cpuDem));
        assertTrue(vn0.addVertex(vA));

        VirtualNode vB = new VirtualNode(1);
        cpuDem = new CpuDemand(vB);
        cpuDem.setDemandedCycles(27.5);
        assertTrue(vB.add(cpuDem));
        assertTrue(vn0.addVertex(vB));



        VirtualNode vC = new VirtualNode(1);
        cpuDem = new CpuDemand(vC);
        cpuDem.setDemandedCycles(2.5);
        assertTrue(vC.add(cpuDem));
        assertTrue(vn0.addVertex(vC));

        VirtualNode vD = new VirtualNode(1);
        cpuDem = new CpuDemand(vD);
        cpuDem.setDemandedCycles(21.5);
        assertTrue(vD.add(cpuDem));
        assertTrue(vn0.addVertex(vD));


        VirtualLink eAB = new VirtualLink(1);
        bwDem = new BandwidthDemand(eAB);
        bwDem.setDemandedBandwidth(1.0);
        assertTrue(eAB.add(bwDem));
        assertTrue(vn0.addEdge(eAB, vA, vB,UNDIRECTED));

        VirtualLink eAC = new VirtualLink(1);
        bwDem = new BandwidthDemand(eAC);
        bwDem.setDemandedBandwidth(2.0);
        assertTrue(eAC.add(bwDem));
        assertTrue(vn0.addEdge(eAC, vA, vC,UNDIRECTED));

        VirtualLink eBC = new VirtualLink(1);
        bwDem = new BandwidthDemand(eBC);
        bwDem.setDemandedBandwidth(5.0);
        assertTrue(eBC.add(bwDem));
        assertTrue(vn0.addEdge(eBC, vB, vC,UNDIRECTED));

        VirtualLink eAD = new VirtualLink(1);
        bwDem = new BandwidthDemand(eAD);
        bwDem.setDemandedBandwidth(6.0);
        assertTrue(eAD.add(bwDem));
        assertTrue(vn0.addEdge(eAD, vA, vD,UNDIRECTED));

        VirtualLink eBD = new VirtualLink(1);
        bwDem = new BandwidthDemand(eBD);
        bwDem.setDemandedBandwidth(4.0);
        assertTrue(eBD.add(bwDem));
        assertTrue(vn0.addEdge(eBD, vB, vD,UNDIRECTED));

        VirtualLink eCD = new VirtualLink(1);
        bwDem = new BandwidthDemand(eCD);
        bwDem.setDemandedBandwidth(3.0);
        assertTrue(eCD.add(bwDem));
        assertTrue(vn0.addEdge(eCD, vC,vD,UNDIRECTED));

        // Virtual network 1
        /*
        VirtualNetwork vn1 = new VirtualNetwork(2);
        vns.add(vn1);

        VirtualNode vC = new VirtualNode(2);
        idDem = new IdDemand(vC);
        idDem.setDemandedId(Integer.toString(5));
        assertTrue(vC.add(idDem));
        cpuDem = new CpuDemand(vC);
        cpuDem.setDemandedCycles(25.0);
        assertTrue(vC.add(cpuDem));
        assertTrue(vn1.addVertex(vC));

        VirtualNode vD = new VirtualNode(2);
        idDem = new IdDemand(vD);
        idDem.setDemandedId(Integer.toString(2));
        assertTrue(vD.add(idDem));
        cpuDem = new CpuDemand(vD);
        cpuDem.setDemandedCycles(23.5);
        assertTrue(vD.add(cpuDem));
        assertTrue(vn1.addVertex(vD));

        VirtualLink eCD = new VirtualLink(2);
        bwDem = new BandwidthDemand(eCD);
        bwDem.setDemandedBandwidth(1.0);
        assertTrue(eCD.add(bwDem));
        assertTrue(vn1.addEdge(eCD, vC, vD));
        */
        return vns;
    }

    // Test passed!!
    @Test
    public void algorithmTest() {

        /*
        List<VirtualNetwork> vnlist = new LinkedList<>();
        Preprocess pre = new Preprocess();
        for(VirtualNetwork vn : createVNDemands()){
            vnlist.add(pre.reduceLinks(vn));
        }
        */

        NetworkStack stack = new NetworkStack(createSubstrate(), createVNDemands());

        AlgorithmParameter param = new AlgorithmParameter();
        param.put("PathSplitting", "False");
        String distance = "-1";
        param.put("distance", distance);
        param.put("kShortestPaths", "3");
        String overload =  "False";
        param.put("overload", overload);

        // finally generate the algorithm
        AbstractAlgorithm algo = new CoordinatedMapping(param);

        algo.setStack(stack);

        long startTimeOri=System.currentTimeMillis();
        algo.performEvaluation();
        long endTimeOri=System.currentTimeMillis();
        System.out.println(stack);
        System.out.println(" Time origin:"+ (endTimeOri-startTimeOri) + "ms");
        //System.out.println(acceptedVnrRatio.toString() + "origin:" + acceptedVnrRatio.calculate(stackOri));

    }


}
