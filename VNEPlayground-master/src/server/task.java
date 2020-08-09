package server;

import vnreal.constraints.demands.BandwidthDemand;
import vnreal.constraints.demands.CpuDemand;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.util.*;

import static edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED;
import static org.junit.Assert.assertTrue;

public class task {
//    public String username = null;
    public int id;
    public int num;
    public double gpu = 100;
    public double cpu = 100;
    public int memory = 100;
    public double width = 2;
    public List<VirtualNetwork> vnet = new LinkedList<VirtualNetwork>();
    public int liveTime = 0;
    public int TTL = 50000;
    public Timer timer = new Timer();
    public TimerTask timertk = new TimerTask() {
        @Override
        public void run() {
            vnet.get(0).clearVnrMappings();
            System.out.println("第" + id + "号任务结束");
            timer.cancel();
        }
    };

    public task(int curid,int curnum)
    {
        id = curid;
        num = curnum;
    }

    public task(int curid,int curnum,double curgpu,double curcpu,int curmemory,double curwidth)
    {
        id = curid;
        num = curnum;
        gpu = curgpu;
        cpu = curcpu;
        memory = curmemory;
        width = curwidth;
    }

    public void createVNDemands()
    {
        CpuDemand cpuDem;
        BandwidthDemand bwDem;

        VirtualNetwork vn = new VirtualNetwork(1,false,false);
        vnet.add(vn);

        ArrayList<VirtualNode> vnodes = new ArrayList<>();
        if(num > 1)
        {
            for(int i=0;i<num;i++)
            {
                VirtualNode v = new VirtualNode(1);
                cpuDem = new CpuDemand(v);
                cpuDem.setDemandedCycles(cpu);
                assertTrue(v.add(cpuDem));
                assertTrue(vn.addVertex(v));
                vnodes.add(v);
            }

            for(int i=0;i<num;i++)
            {
                for(int j=i+1;j<num;j++)
                {
                    VirtualLink vl = new VirtualLink(1);
                    bwDem = new BandwidthDemand(vl);
                    bwDem.setDemandedBandwidth(width);
                    assertTrue(vl.add(bwDem));
                    assertTrue(vn.addEdge(vl, vnodes.get(i), vnodes.get(j),UNDIRECTED));
                }
            }
        }
        else if(num == 1)
        {
            VirtualNode v = new VirtualNode(1);
            cpuDem = new CpuDemand(v);
            cpuDem.setDemandedCycles(cpu);
            assertTrue(v.add(cpuDem));
            assertTrue(vn.addVertex(v));
            vnodes.add(v);

            VirtualNode v1 = new VirtualNode(1);
            cpuDem = new CpuDemand(v1);
            cpuDem.setDemandedCycles(0.0);
            assertTrue(v1.add(cpuDem));
            assertTrue(vn.addVertex(v1));
            vnodes.add(v1);

            VirtualLink vl = new VirtualLink(1);
            bwDem = new BandwidthDemand(vl);
            bwDem.setDemandedBandwidth(0.0);
            assertTrue(vl.add(bwDem));
            assertTrue(vn.addEdge(vl, vnodes.get(0), vnodes.get(1),UNDIRECTED));
        }
    }

    public void run()
    {
        createVNDemands();
        timer.schedule(timertk,TTL);
    }

    public void cancel()
    {
        System.out.println("第" + id + "号任务结束");
        vnet.get(0).clearVnrMappings();
        timer.cancel();
    }
}
