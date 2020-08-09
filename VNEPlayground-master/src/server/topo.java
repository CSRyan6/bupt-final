package server;

import javafx.util.Pair;
import vnreal.constraints.demands.BandwidthDemand;
import vnreal.constraints.demands.CpuDemand;
import vnreal.constraints.demands.IdDemand;
import vnreal.constraints.resources.BandwidthResource;
import vnreal.constraints.resources.CpuResource;
import vnreal.constraints.resources.IdResource;
import vnreal.network.Link;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED;
import static org.junit.Assert.assertTrue;

public class topo {
    public int switchnum = 0;
    public ArrayList<String> infs = new ArrayList<>();
    public ArrayList<node> nodes = new ArrayList<>();
    public ArrayList<edge> edges = new ArrayList<>();
    public SubstrateNetwork subsNetwork = new SubstrateNetwork(false,false);
    public ArrayList<Pair<String,SubstrateNode>> nodereflect = new ArrayList<>();
    public List<VirtualNetwork> vnet = new LinkedList<VirtualNetwork>();
    public List<task> taskList = new LinkedList<>();

    public void getTopo(String s){
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i) == '\"')
            {
                Pair<String,Integer> p;
                p = getWord(s,i);
                i = p.getValue();
                String ts = p.getKey().toString();
//                System.out.println(p.getKey());
                infs.add(ts);
            }
        }
        int start = getNodes() + 1;
        getLinks(start);
    }

    public Pair<String,Integer> getWord(String s, int i)
    {
        int j = i + 1;
        String temp = new String();
        while(s.charAt(j) != '\"')
        {
            temp = temp + s.charAt(j);
            j++;
        }
        i = j;
        Pair<String,Integer> p = new Pair<>(temp,i);
        return p;
    }

    public int getNodes()
    {
        int i = 0;
        for(;i<infs.size();i++)
        {
            if(infs.get(i).equals("node-id"))
            {
                if(infs.get(i+1).charAt(0) == 'h')
                {
                    host temp = new host();
                    temp.type = 0;
                    temp.name = infs.get(i+1);
                    temp.mac = infs.get(i+8);
                    temp.ip = infs.get(i+11);
                    nodes.add(temp);
                    i += 20;
                }
                else if(infs.get(i+1).charAt(0) == 'o')
                {
                    switchnum++;
                    openflow temp = new openflow();
                    temp.type = 1;
                    temp.name = infs.get(i+1);
                    nodes.add(temp);
                    i += 20;
                }
            }
            if(infs.get(i).equals("link"))
                break;
        }
        return i;
    }

    public void getLinks(int start)
    {
        for(int i=start;i<infs.size();i++)
        {
            if(infs.get(i).equals("link-id"))
            {
                String snmae = infs.get(i+4);
                String dname = infs.get(i+9);
                if(checkLink(snmae,dname))
                {
                    edge tempe = new edge();
                    tempe.name1 = snmae;
                    tempe.name2 = dname;
                    edges.add(tempe);
                }
                i += 11;
            }
        }
    }

    public boolean checkLink(String s1,String s2)
    {
        for(int i=0;i<edges.size();i++)
        {
            if(edges.get(i).name1.equals(s2) && edges.get(i).name2.equals(s1))
            {
                return false;
            }
        }
        return true;
    }

    public void printNodes()
    {
        for(int i=0;i<nodes.size();i++)
        {
            if(nodes.get(i).type == 0)
            {
                host h = (host)nodes.get(i);
                System.out.println("node name:" + h.name + " gpu:" + h.gpu + " cpu:" + h.cpu + " memory:" + h.memory + " " );
            }
            else
            {
                System.out.println("node name:" + nodes.get(i).name);
            }
        }
    }

    public void createSubstrate()
    {
        CpuResource cpuRes;
        for(int i=0;i<nodes.size();i++)
        {
            SubstrateNode subsNode = new SubstrateNode();
            subsNode.setName(nodes.get(i).name);
            cpuRes = new CpuResource(subsNode);
            if(nodes.get(i).name.charAt(0) == '0')
            {
                cpuRes.setCycles(0.0);
            }
            else if(nodes.get(i).name.charAt(0) == 'h')
            {
                cpuRes.setCycles(100.0);
            }
            assertTrue(subsNode.add(cpuRes));
            assertTrue(subsNetwork.addVertex(subsNode));
            Pair<String,SubstrateNode> p = new Pair<>(nodes.get(i).name,subsNode);
            nodereflect.add(p);
        }

        for(int i=0;i<edges.size();i++)
        {
            BandwidthResource bwRes;
            SubstrateLink subsLink = new SubstrateLink();
            bwRes = new BandwidthResource(subsLink);
            bwRes.setBandwidth(100.0);
            assertTrue(subsLink.add(bwRes));
            assertTrue(subsNetwork.addEdge(subsLink, findNode(edges.get(i).name1), findNode(edges.get(i).name2),UNDIRECTED));
        }
    }

    public SubstrateNode findNode(String name)
    {
        for(int i=0;i<nodereflect.size();i++)
        {
            if(nodereflect.get(i).getKey().equals(name))
                return nodereflect.get(i).getValue();
        }
        return null;
    }

//    public void createVNDemands(ArrayList<task> tlist)
//    {
//        vnet.clear();
//
//        CpuDemand cpuDem;
//        BandwidthDemand bwDem;
//
//        VirtualNetwork vn = new VirtualNetwork(1,false,false);
//        vnet.add(vn);
//
//        ArrayList<VirtualNode> vnodes = new ArrayList<>();
//        for(int i=0;i<tlist.size();i++)
//        {
//            VirtualNode v = new VirtualNode(1);
//            cpuDem = new CpuDemand(v);
//            cpuDem.setDemandedCycles((double)tlist.get(0).cpu);
//            assertTrue(v.add(cpuDem));
//            assertTrue(vn.addVertex(v));
//            vnodes.add(v);
//        }
//
//        for(int i=0;i<tlist.size();i++)
//        {
//            for(int j=i+1;j<tlist.size();j++)
//            {
//                VirtualLink vl = new VirtualLink(1);
//                bwDem = new BandwidthDemand(vl);
//                bwDem.setDemandedBandwidth((double)tlist.get(i).width);
//                assertTrue(vl.add(bwDem));
//                assertTrue(vn.addEdge(vl, vnodes.get(i), vnodes.get(j),UNDIRECTED));
//            }
//        }
//    }
}
