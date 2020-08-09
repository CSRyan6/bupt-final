package createNetwork.dom4jutil;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;

public class Dom4jUtils {
	public static final String PATH="src/p1.xml";
	public static Document getDocument(String path){
		
		try {
			SAXReader saxReader=new SAXReader();
			Document document=saxReader.read(path);
			return document;
		} catch (DocumentException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	public static void xmlWriter(String path,Document document){
		
		try {
			OutputFormat format=OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter;
			xmlWriter = new XMLWriter(new FileOutputStream(path),format);
			xmlWriter.write(document);
		    xmlWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
