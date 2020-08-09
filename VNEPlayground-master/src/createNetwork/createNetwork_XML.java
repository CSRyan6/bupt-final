package createNetwork;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class createNetwork_XML {
	static int snNodeNum = 50;
	static int vnNodeNum ;
	static int vnNum=9;
	static List<String> nodePair=new ArrayList<String>();


	public static void main(String[] args) throws IOException {
		
		printXML();
		//createNodePair(snNodeNum);
	}

	public static void printXML() {
		DecimalFormat df = new DecimalFormat("0.000");
		Document doc = DocumentHelper.createDocument();
		Element Scenario=doc.addElement("Scenario");
		Scenario.addAttribute("xsi:schemaLocation", "http://sourceforge.net/projects/alevin/ ./Alevin.xsd");
		Scenario.addAttribute("xmlns ", "http://sourceforge.net/projects/alevin/");
        Scenario.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Element SubstrateNetwork = Scenario.addElement("SubstrateNetwork");
		Element VirtualNetworks=Scenario.addElement("VirtualNetworks");
		Element SubstrateNodes = SubstrateNetwork.addElement("SubstrateNodes");
		
		for (int i = 1; i <= snNodeNum; i++) {
			Element SubstrateNode = SubstrateNodes.addElement("SubstrateNode");
			String coordinateX = df.format(Math.random() * 20 - 10) + "";
			String coordinateY = df.format(Math.random() * 20 - 10) + "";
			SubstrateNode.addAttribute("coordinateX", coordinateX);
			SubstrateNode.addAttribute("coordinateY", coordinateY);
			SubstrateNode.addAttribute("id", i + "");
			Element Resource1 = SubstrateNode.addElement("Resource");
			Resource1.addAttribute("type", "IdResource");
			Element Parameter1 = Resource1.addElement("Parameter");
			Parameter1.addAttribute("name", "id");
			Parameter1.addAttribute("type", "String");
			Parameter1.addAttribute("value", i + "");
			Element Resource2 = SubstrateNode.addElement("Resource");
			Resource2.addAttribute("type", "CpuResource");
			Element Parameter2 = Resource2.addElement("Parameter");
			Parameter2.addAttribute("name", "Cycles");
			Parameter2.addAttribute("type", "Double");
			String CpuResource = Math.round(Math.random() * 50 + 50) + "";
			Parameter2.addAttribute("value", CpuResource);
		}

		Element SubstrateLinks = SubstrateNetwork.addElement("SubstrateLinks");
		int snLinkNum =  (int) Math.ceil((double)snNodeNum * (snNodeNum - 1) / 4);
		for(int i=1;i<snNodeNum;i++)
		{
			for(int j=i+1;j<snNodeNum+1;j++)
			{
			 nodePair.add(i+"-"+j);
			}
		}
		String destination="";
		String source="";
		Random rand=new Random();
		for (int i = 1; i <= snLinkNum; i++) {
			String NodeNumPair=createNodePair(nodePair);
			int index=rand.nextInt(2);
			destination=NodeNumPair.split("-")[index];
			source=NodeNumPair.split("-")[1-index];			
			Element SubstrateLink = SubstrateLinks.addElement("SubstrateLink");
			SubstrateLink.addAttribute("destination", destination);
			SubstrateLink.addAttribute("id",snNodeNum+i+"");
			SubstrateLink.addAttribute("source", source);
			Element Resource = SubstrateLink.addElement("Resource");
			Resource.addAttribute("type", "BandwidthResource");
			Element Parameter = Resource.addElement("Parameter");
			Parameter.addAttribute("name", "Bandwidth");
			Parameter.addAttribute("type", "Double");
			String Bandwidth = Math.round(Math.random() * 50 + 50) + "";
			Parameter.addAttribute("value", Bandwidth);
		}
		
		
		for(int k=1;k<=vnNum;k++){
			vnNodeNum=(int) (Math.random()*8+2);
//			System.out.println(vnNodeNum);
		int vnLinkNum = (int) Math.ceil((double)vnNodeNum * (vnNodeNum - 1) / 4);
			Element VirtualNetwork=VirtualNetworks.addElement("VirtualNetwork");
			VirtualNetwork.addAttribute("layer", k+"");
			Element VirtualNodes=VirtualNetwork.addElement("VirtualNodes");
			for(int i=1;i<=vnNodeNum;i++){
				Element VirtualNode=VirtualNodes.addElement("VirtualNode");
				String coordinateX = df.format(Math.random() * 20 - 10) + "";
				String coordinateY = df.format(Math.random() * 20 - 10) + "";
				VirtualNode.addAttribute("coordinateX", coordinateX);
				VirtualNode.addAttribute("coordinateY", coordinateY);
				VirtualNode.addAttribute("id",snNodeNum + snLinkNum+(vnNodeNum+vnLinkNum)*(k-1)+i+"" );
				Element Demand1=VirtualNode.addElement("Demand");
				Demand1.addAttribute("type", "IdDemand");
				Element Parameter1=Demand1.addElement("Parameter");
				Parameter1.addAttribute("name", "DemandedId");
				Parameter1.addAttribute("type", "String");
				Parameter1.addAttribute("value", i+"");
				Element Demand2=VirtualNode.addElement("Demand");
				Demand2.addAttribute("type", "CpuDemand");
				Element Parameter2=Demand2.addElement("Parameter");
				Parameter2.addAttribute("name", "DemandedCycles");
				Parameter2.addAttribute("type", "Double");
				String DemandedCycles=Math.round(Math.random()*20)+"";
				Parameter2.addAttribute("value", DemandedCycles);
			}
			Element VirtualLinks = VirtualNetwork.addElement("VirtualLinks");
			
			/*for(int i=0;i<nodePair.size();i++){
				nodePair.remove(i);
			}*/
			nodePair.clear();
//			System.out.println(nodePair);
			for(int i=snNodeNum + snLinkNum+(vnNodeNum+vnLinkNum)*(k-1)+1;i<snNodeNum + snLinkNum+(vnNodeNum+vnLinkNum)*(k-1)+vnNodeNum;i++)
			{
				for(int j=i+1;j<snNodeNum + snLinkNum+(vnNodeNum+vnLinkNum)*(k-1)+1+vnNodeNum;j++)
				{
				 nodePair.add(i+"-"+j);
				}
			}
//			System.out.println(nodePair);
			for (int i = 1; i <= vnLinkNum; i++) {
				String NodeNumPair=createNodePair(nodePair);
				int index=rand.nextInt(2);
				destination=NodeNumPair.split("-")[index];
				source=NodeNumPair.split("-")[1-index];			
				Element VirtualLink = VirtualLinks.addElement("VirtualLink");
				VirtualLink.addAttribute("destination", destination);
				VirtualLink.addAttribute("id", snNodeNum + snLinkNum+vnNodeNum+(vnNodeNum+vnLinkNum)*(k-1)+i+"");
				VirtualLink.addAttribute("source", source);
				Element Demand = VirtualLink.addElement("Demand");
				Demand.addAttribute("type", "BandwidthDemand");
				Element Parameter = Demand.addElement("Parameter");
				Parameter.addAttribute("name", "DemandedBandwidth");
				Parameter.addAttribute("type", "Double");
				String Bandwidth = Math.round(Math.random() * 50) + "";
				Parameter.addAttribute("value", Bandwidth);
			}
		}
		

		try {
			FileWriter fw=new FileWriter("src//XML//network.xml");
			OutputFormat format = new OutputFormat("    ", true);
			format.setEncoding("utf-8");
			// ���԰�System.out��Ϊ��Ҫ������
			XMLWriter xmlWriter = new XMLWriter(new PrintWriter(fw),
					format);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static String createNodePair(List<String> nodePair){
		Random rand=new Random();
				
		int k=rand.nextInt(nodePair.size());
		String s=nodePair.get(k);
		nodePair.remove(k);
		return s;
	}

}
