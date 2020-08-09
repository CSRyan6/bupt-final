package createNetwork;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class createVN_XML {
	static int vnNodeNum ;
	static int vnNum=2;
	static int snNodeNum=50;
	static int snLinkNum =  (int) Math.ceil((double)snNodeNum * (snNodeNum - 1) / 4);
	static List<String> nodePair=new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		for(int i=2;i<=9;i++){
			vnNum=i;
			printVNXML();
		}
		

	}
	
	public static void printVNXML() throws Exception{
		DecimalFormat df = new DecimalFormat("0.000");
		SAXReader reader=new SAXReader();
		Document doc=reader.read("src//XML//SN.xml");
		Element Scenario=doc.getRootElement();
		Element VirtualNetworks= Scenario.addElement("VirtualNetworks");
		Random rand=new Random();
		String destination="";
		String source="";
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
			OutputFormat format=OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter=new XMLWriter(new FileOutputStream("src/XML/SN+"+vnNum+"VN.xml"), format);
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
