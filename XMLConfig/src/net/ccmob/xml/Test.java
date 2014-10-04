package net.ccmob.xml;

import net.ccmob.xml.XMLConfig.XMLAttribute;
import net.ccmob.xml.XMLConfig.XMLNode;

public class Test {

	public static void main(String[] args) {
		//XMLConfig config = new XMLConfig("/Users/Marcel/Dropbox/xmlExample.xml");
		XMLConfig config = new XMLConfig("/Users/Marcel/Desktop/test.xml");
		XMLNode.printNode(config.getConfigNode());
		//XMLNode node = new XMLNode("block");
		//node.add(new XMLAttribute("id", "23"));
		//config.getConfigNode().getChilds().get(0).add(node);
		//config.save("/Users/Marcel/Desktop/test.xml");
	}
	/*
	public void save(String filename){
		try {
			File f = new File(filename);
			if(f.exists()){
				f.delete();
			}
			FileWriter writer = new FileWriter(f);
			writeNode(getConfigNode(), 0, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String formAttribute(XMLAttribute attr){
		return attr.getAttributeName() + "=\"" + attr.getAttributeValue() + "\"";
	}
	
	private String formTabs(int tabIndex){
		String tabs = "";
		for(int i = 0;i<tabIndex;i++){
			tabs += "\t";
		}
		return tabs;
	}
	
	private void writeNode(XMLNode node, int tabIndex, FileWriter writer) throws IOException{
		String line = formTabs(tabIndex) + "<" + node.getNodeName();
		if(node.getAttributes().size() > 0){
			for(int i = 0;i<node.getAttributes().size();i++){
				line += " " + formAttribute(node.getAttributes().get(i));
			}
		}
		if(node.getChilds().size() == 0){
			line += "/>";
			writer.write(line + "\n");
		}else{
			line += ">";
			for(XMLNode child : node.getChilds()){
				writeNode(child, tabIndex + 1, writer);
			}
			writer.write(formTabs(tabIndex) + "</" + node.getNodeName() + ">");
		}
	}*/
	
}
