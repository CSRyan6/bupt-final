package server;

import javafx.util.Pair;
import sun.java2d.loops.GeneralRenderer;
import tests.algorithms.coordinatedmappingTest.BFSMappingTest;
import tests.generators.GeneratorParameter;
import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.AlgorithmParameter;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.algorithms.ViNEYard.CoordinatedMapping;
import vnreal.algorithms.onestage.BFSCoordinated;
import vnreal.constraints.resources.CpuResource;
import vnreal.constraints.resources.IdResource;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import static org.junit.Assert.assertTrue;

public class main {

    public static void main(String[] args){
        topo to = new topo();
        int taskCount = 0;

//        ArrayList<Pair<String,String>> users = new ArrayList<>();
//        String fileName = "D:\\VNEPlayground-master\\VNEPlayground-master\\src\\user.txt";
//        users = UserManage.readFile(fileName);
//        for(int i = 0;i<users.size();i++)
//        {
//            System.out.println("帐号" + users.get(i).getKey() + " " + users.get(i).getValue());
//        }
//        boolean flag = UserManage.writeFile(users,"111","222",fileName);
//        System.out.println(flag);

//        String url = "http://localhost:8181/restconf/operational/network-topology:network-topology";
//        String tmp = "Basic " + Base64.getEncoder().encodeToString(("admin:admin").getBytes());
//        HttpRequest.setBasicAuth(tmp);
//        String str = HttpRequest.sendGet(url,"","utf-8");
        String str = "{\"network-topology\":{\"topology\":[{\"topology-id\":\"flow:1\",\"node\":[{\"node-id\":\"host:0a:60:ed:6d:38:e4\",\"termination-point\":[{\"tp-id\":\"host:0a:60:ed:6d:38:e4\"}],\"host-tracker-service:addresses\":[{\"id\":5,\"mac\":\"0a:60:ed:6d:38:e4\",\"last-seen\":1586334034708,\"ip\":\"10.0.0.3\",\"first-seen\":1586334034708}],\"host-tracker-service:attachment-points\":[{\"tp-id\":\"openflow:1:2\",\"corresponding-tp\":\"host:0a:60:ed:6d:38:e4\",\"active\":true}],\"host-tracker-service:id\":\"0a:60:ed:6d:38:e4\"},{\"node-id\":\"host:a6:40:0e:49:74:64\",\"termination-point\":[{\"tp-id\":\"host:a6:40:0e:49:74:64\"}],\"host-tracker-service:addresses\":[{\"id\":1,\"mac\":\"a6:40:0e:49:74:64\",\"last-seen\":1586334034599,\"ip\":\"10.0.0.4\",\"first-seen\":1586334034599}],\"host-tracker-service:attachment-points\":[{\"tp-id\":\"openflow:2:2\",\"corresponding-tp\":\"host:a6:40:0e:49:74:64\",\"active\":true}],\"host-tracker-service:id\":\"a6:40:0e:49:74:64\"},{\"node-id\":\"host:ae:9c:3f:b4:06:8d\",\"termination-point\":[{\"tp-id\":\"host:ae:9c:3f:b4:06:8d\"}],\"host-tracker-service:addresses\":[{\"id\":2,\"mac\":\"ae:9c:3f:b4:06:8d\",\"last-seen\":1586334034602,\"ip\":\"10.0.0.1\",\"first-seen\":1586334034602}],\"host-tracker-service:attachment-points\":[{\"tp-id\":\"openflow:1:1\",\"corresponding-tp\":\"host:ae:9c:3f:b4:06:8d\",\"active\":true}],\"host-tracker-service:id\":\"ae:9c:3f:b4:06:8d\"},{\"node-id\":\"openflow:1\",\"opendaylight-topology-inventory:inventory-node-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']\",\"termination-point\":[{\"tp-id\":\"openflow:1:3\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:3']\"},{\"tp-id\":\"openflow:1:1\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:1']\"},{\"tp-id\":\"openflow:1:LOCAL\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:LOCAL']\"},{\"tp-id\":\"openflow:1:2\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:2']\"}]},{\"node-id\":\"openflow:2\",\"opendaylight-topology-inventory:inventory-node-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']\",\"termination-point\":[{\"tp-id\":\"openflow:2:2\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:2']\"},{\"tp-id\":\"openflow:2:3\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:3']\"},{\"tp-id\":\"openflow:2:1\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:1']\"},{\"tp-id\":\"openflow:2:LOCAL\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:LOCAL']\"}]},{\"node-id\":\"host:aa:23:db:90:fd:10\",\"termination-point\":[{\"tp-id\":\"host:aa:23:db:90:fd:10\"}],\"host-tracker-service:addresses\":[{\"id\":3,\"mac\":\"aa:23:db:90:fd:10\",\"last-seen\":1586334034603,\"ip\":\"10.0.0.2\",\"first-seen\":1586334034603}],\"host-tracker-service:attachment-points\":[{\"tp-id\":\"openflow:2:1\",\"corresponding-tp\":\"host:aa:23:db:90:fd:10\",\"active\":true}],\"host-tracker-service:id\":\"aa:23:db:90:fd:10\"}],\"link\":[{\"link-id\":\"openflow:1:1/host:ae:9c:3f:b4:06:8d\",\"source\":{\"source-node\":\"openflow:1\",\"source-tp\":\"openflow:1:1\"},\"destination\":{\"dest-node\":\"host:ae:9c:3f:b4:06:8d\",\"dest-tp\":\"host:ae:9c:3f:b4:06:8d\"}},{\"link-id\":\"openflow:1:3\",\"source\":{\"source-node\":\"openflow:1\",\"source-tp\":\"openflow:1:3\"},\"destination\":{\"dest-node\":\"openflow:2\",\"dest-tp\":\"openflow:2:3\"}},{\"link-id\":\"openflow:2:2/host:a6:40:0e:49:74:64\",\"source\":{\"source-node\":\"openflow:2\",\"source-tp\":\"openflow:2:2\"},\"destination\":{\"dest-node\":\"host:a6:40:0e:49:74:64\",\"dest-tp\":\"host:a6:40:0e:49:74:64\"}},{\"link-id\":\"openflow:2:3\",\"source\":{\"source-node\":\"openflow:2\",\"source-tp\":\"openflow:2:3\"},\"destination\":{\"dest-node\":\"openflow:1\",\"dest-tp\":\"openflow:1:3\"}},{\"link-id\":\"openflow:2:1/host:aa:23:db:90:fd:10\",\"source\":{\"source-node\":\"openflow:2\",\"source-tp\":\"openflow:2:1\"},\"destination\":{\"dest-node\":\"host:aa:23:db:90:fd:10\",\"dest-tp\":\"host:aa:23:db:90:fd:10\"}},{\"link-id\":\"openflow:1:2/host:0a:60:ed:6d:38:e4\",\"source\":{\"source-node\":\"openflow:1\",\"source-tp\":\"openflow:1:2\"},\"destination\":{\"dest-node\":\"host:0a:60:ed:6d:38:e4\",\"dest-tp\":\"host:0a:60:ed:6d:38:e4\"}},{\"link-id\":\"host:aa:23:db:90:fd:10/openflow:2:1\",\"source\":{\"source-node\":\"host:aa:23:db:90:fd:10\",\"source-tp\":\"host:aa:23:db:90:fd:10\"},\"destination\":{\"dest-node\":\"openflow:2\",\"dest-tp\":\"openflow:2:1\"}},{\"link-id\":\"host:0a:60:ed:6d:38:e4/openflow:1:2\",\"source\":{\"source-node\":\"host:0a:60:ed:6d:38:e4\",\"source-tp\":\"host:0a:60:ed:6d:38:e4\"},\"destination\":{\"dest-node\":\"openflow:1\",\"dest-tp\":\"openflow:1:2\"}},{\"link-id\":\"host:ae:9c:3f:b4:06:8d/openflow:1:1\",\"source\":{\"source-node\":\"host:ae:9c:3f:b4:06:8d\",\"source-tp\":\"host:ae:9c:3f:b4:06:8d\"},\"destination\":{\"dest-node\":\"openflow:1\",\"dest-tp\":\"openflow:1:1\"}},{\"link-id\":\"host:a6:40:0e:49:74:64/openflow:2:2\",\"source\":{\"source-node\":\"host:a6:40:0e:49:74:64\",\"source-tp\":\"host:a6:40:0e:49:74:64\"},\"destination\":{\"dest-node\":\"openflow:2\",\"dest-tp\":\"openflow:2:2\"}}]}]}}";
//                "{\"network-topology\":{\"topology\":[{\"topology-id\":\"flow:1\",\"node\":[{\"node-id\":\"host:b6:11:77:94:76:75\",\"termination-point\":[{\"tp-id\":\"host:b6:11:77:94:76:75\"}],\"host-tracker-service:addresses\":[{\"id\":8,\"mac\":\"b6:11:77:94:76:75\",\"last-seen\":1586702233743,\"ip\":\"10.0.0.3\",\"first-seen\":1586702233743}],\"host-tracker-service:attachment-points\":[{\"tp-id\":\"openflow:1:2\",\"corresponding-tp\":\"host:b6:11:77:94:76:75\",\"active\":true}],\"host-tracker-service:id\":\"b6:11:77:94:76:75\"},{\"node-id\":\"host:2e:be:bb:7a:c0:aa\",\"termination-point\":[{\"tp-id\":\"host:2e:be:bb:7a:c0:aa\"}],\"host-tracker-service:addresses\":[{\"id\":10,\"mac\":\"2e:be:bb:7a:c0:aa\",\"last-seen\":1586702233766,\"ip\":\"10.0.0.5\",\"first-seen\":1586702233766}],\"host-tracker-service:attachment-points\":[{\"tp-id\":\"openflow:1:3\",\"corresponding-tp\":\"host:2e:be:bb:7a:c0:aa\",\"active\":true}],\"host-tracker-service:id\":\"2e:be:bb:7a:c0:aa\"},{\"node-id\":\"openflow:1\",\"opendaylight-topology-inventory:inventory-node-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']\",\"termination-point\":[{\"tp-id\":\"openflow:1:3\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:3']\"},{\"tp-id\":\"openflow:1:4\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:4']\"},{\"tp-id\":\"openflow:1:1\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:1']\"},{\"tp-id\":\"openflow:1:LOCAL\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:LOCAL']\"},{\"tp-id\":\"openflow:1:2\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:1']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:1:2']\"}]},{\"node-id\":\"host:3a:9a:9f:f8:39:fb\",\"termination-point\":[{\"tp-id\":\"host:3a:9a:9f:f8:39:fb\"}],\"host-tracker-service:addresses\":[{\"id\":6,\"mac\":\"3a:9a:9f:f8:39:fb\",\"last-seen\":1586702233731,\"ip\":\"10.0.0.1\",\"first-seen\":1586702233731}],\"host-tracker-service:attachment-points\":[{\"tp-id\":\"openflow:1:1\",\"corresponding-tp\":\"host:3a:9a:9f:f8:39:fb\",\"active\":true}],\"host-tracker-service:id\":\"3a:9a:9f:f8:39:fb\"},{\"node-id\":\"openflow:2\",\"opendaylight-topology-inventory:inventory-node-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']\",\"termination-point\":[{\"tp-id\":\"openflow:2:2\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:2']\"},{\"tp-id\":\"openflow:2:3\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:3']\"},{\"tp-id\":\"openflow:2:1\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:1']\"},{\"tp-id\":\"openflow:2:LOCAL\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:LOCAL']\"},{\"tp-id\":\"openflow:2:4\",\"opendaylight-topology-inventory:inventory-node-connector-ref\":\"/opendaylight-inventory:nodes/opendaylight-inventory:node[opendaylight-inventory:id='openflow:2']/opendaylight-inventory:node-connector[opendaylight-inventory:id='openflow:2:4']\"}]}],\"link\":[{\"link-id\":\"openflow:1:2/host:b6:11:77:94:76:75\",\"source\":{\"source-node\":\"openflow:1\",\"source-tp\":\"openflow:1:2\"},\"destination\":{\"dest-node\":\"host:b6:11:77:94:76:75\",\"dest-tp\":\"host:b6:11:77:94:76:75\"}},{\"link-id\":\"host:3a:9a:9f:f8:39:fb/openflow:1:1\",\"source\":{\"source-node\":\"host:3a:9a:9f:f8:39:fb\",\"source-tp\":\"host:3a:9a:9f:f8:39:fb\"},\"destination\":{\"dest-node\":\"openflow:1\",\"dest-tp\":\"openflow:1:1\"}},{\"link-id\":\"openflow:2:4\",\"source\":{\"source-node\":\"openflow:2\",\"source-tp\":\"openflow:2:4\"},\"destination\":{\"dest-node\":\"openflow:1\",\"dest-tp\":\"openflow:1:4\"}},{\"link-id\":\"openflow:1:4\",\"source\":{\"source-node\":\"openflow:1\",\"source-tp\":\"openflow:1:4\"},\"destination\":{\"dest-node\":\"openflow:2\",\"dest-tp\":\"openflow:2:4\"}},{\"link-id\":\"openflow:1:3/host:2e:be:bb:7a:c0:aa\",\"source\":{\"source-node\":\"openflow:1\",\"source-tp\":\"openflow:1:3\"},\"destination\":{\"dest-node\":\"host:2e:be:bb:7a:c0:aa\",\"dest-tp\":\"host:2e:be:bb:7a:c0:aa\"}},{\"link-id\":\"host:b6:11:77:94:76:75/openflow:1:2\",\"source\":{\"source-node\":\"host:b6:11:77:94:76:75\",\"source-tp\":\"host:b6:11:77:94:76:75\"},\"destination\":{\"dest-node\":\"openflow:1\",\"dest-tp\":\"openflow:1:2\"}},{\"link-id\":\"host:2e:be:bb:7a:c0:aa/openflow:1:3\",\"source\":{\"source-node\":\"host:2e:be:bb:7a:c0:aa\",\"source-tp\":\"host:2e:be:bb:7a:c0:aa\"},\"destination\":{\"dest-node\":\"openflow:1\",\"dest-tp\":\"openflow:1:3\"}},{\"link-id\":\"openflow:1:1/host:3a:9a:9f:f8:39:fb\",\"source\":{\"source-node\":\"openflow:1\",\"source-tp\":\"openflow:1:1\"},\"destination\":{\"dest-node\":\"host:3a:9a:9f:f8:39:fb\",\"dest-tp\":\"host:3a:9a:9f:f8:39:fb\"}}]}]}}\n";

        to.getTopo(str);
        to.createSubstrate();
//        System.out.println("物理拓扑网络：");
//        System.out.println(to.subsNetwork.toString());

//        ArrayList<task> tl = new ArrayList<>();
//        for(int i=0;i<3;i++)
//        {
//            task tempt = new task(taskCount++);
//            tempt.width = 2;
//            tempt.cpu = 30;
//            tempt.gpu = 30;
//            tempt.memory = 100;
//            tl.add(tempt);
//        }
//        to.createVNDemands(tl);
//        System.out.println("虚拟逻辑网络：");
//        System.out.println(to.vnet.get(0).toString());

//        NetworkStack stack = new NetworkStack(to.subsNetwork, to.vnet);
//        AlgorithmParameter param = new AlgorithmParameter();
//        param.put("PathSplitting", "False");
//        String distance = "-1";
//        param.put("distance", distance);
//        param.put("kShortestPaths", "1");
//        String overload =  "False";
//        param.put("overload", overload);
//
//        GenericMappingAlgorithm algo = new BFSCoordinated(param);
//        algo.setStack(stack);
//        algo.performEvaluation();


//        final version
        task tempt = new task(taskCount++,1,20,30,10,20);
        tempt.run();
        System.out.println(tempt.vnet.toString());
        NetworkStack stack = new NetworkStack(to.subsNetwork, tempt.vnet);
        AlgorithmParameter param = new AlgorithmParameter();
        param.put("PathSplitting", "False");
        String distance = "-1";
        param.put("distance", distance);
        param.put("kShortestPaths", "1");
        String overload =  "False";
        param.put("overload", overload);

        String old = to.subsNetwork.toString();
        GenericMappingAlgorithm algo = new BFSCoordinated(param);
        algo.setStack(stack);
        algo.performEvaluation();
        System.out.println("stack完成分配后的物理拓扑：");
        System.out.println(stack.getSubstrate().toString());
        if(to.subsNetwork.toString().equals(old) == false)
            System.out.println(true);
        else
            System.out.println(false);

//        task tempt2 = new task(taskCount++,4,20,20,100,2);
//        tempt2.run();
//        System.out.println(tempt2.vnet.toString());
//        NetworkStack stack2 = new NetworkStack(to.subsNetwork, tempt2.vnet);
//        AlgorithmParameter param2 = new AlgorithmParameter();
//        param2.put("PathSplitting", "False");
//        String distance2 = "-1";
//        param2.put("distance", distance2);
//        param2.put("kShortestPaths", "1");
//        String overload2 =  "False";
//        param2.put("overload", overload2);
//
//        GenericMappingAlgorithm algo2 = new BFSCoordinated(param2);
//        algo2.setStack(stack2);
//        algo2.performEvaluation();
//        System.out.println("完成分配后的物理拓扑：");
//        System.out.println(stack2.getSubstrate().toString());
//
//        System.out.println("test");
//        System.out.println(to.subsNetwork.toString());

////        //主函数挂起一定时间
//        Timer t = new Timer();
//        TimerTask ttk = new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("运行结束后的物理拓扑：");
//                System.out.println(stack.getSubstrate().toString());
//                t.cancel();
//            }
//        };
//        t.schedule(ttk,2000);

////        提前关闭某一个任务
//        Scanner in = new Scanner(System.in);
//        int i = in.nextInt();
//        if(i == 1)
//        {
//            tempt.cancel();
//            System.out.println(stack.getSubstrate().toString());
//        }
    }

    public boolean cancel(ArrayList<task> tlist,int id)
    {
        for(int i=0;i<tlist.size();i++)
        {
            if(tlist.get(i).id == id)
            {
                tlist.get(i).cancel();
                tlist.remove(i);
                return true;
            }
        }
        return false;
    }
}
