package net.ccmob.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;

public class XMLConfig {
	
	private ArrayList<String> configLines;
	private XMLNode rootNode = new XMLNode("XMLConfig");

	public XMLConfig(String filename){
		this(new File(filename));
	}
	
	public XMLConfig(File f){
		this();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			while((line = reader.readLine()) != null){
				this.getConfigLines().add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parse();
	}
	
	public XMLConfig(ArrayList<String> lines){
		this();
		this.setConfigLines(lines);
		parse();
	}
	
	public XMLConfig(String[] lines){
		this();
		for(int i = 0;i<lines.length;i++){
			this.getConfigLines().add(lines[i]);
		}
		parse();
	}
	
	public void parse(){
		StringBuilder configText = new StringBuilder();
		for(int i = 0;i<this.getConfigLines().size();i++){
			configText.append(this.getConfigLines().get(i) + " ");
		}
		try {
			parseTextBlock(configText.toString().trim(), this.getConfigNode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseTextBlock(String block, XMLNode parent) throws Exception{
		XMLNode node = new XMLNode();
		String nodeName = "";
		System.out.println("Current block : \n" + block);
		boolean blockOpen = false;
		boolean nodeBlockFinished = false;
		boolean nodeNameFinished = false;
		boolean atAttributes = false;
		int attrStart = 0;
		int attrEnd = 0;
		char prevChar = '\0';
		for(int i = 0;i<block.length();i++){
			char c = block.charAt(i);
			if(i > 0){
				prevChar = block.charAt(i - 1);
			}
			switch(c){
				case '<':{
					if(block.charAt(i+1) == '/'){
						i = block.indexOf('>', i) + 2;
						blockOpen = false;
						
						break;
					}else{
						attrStart = 0;
						attrEnd = 0;
						atAttributes = false;
						if(!blockOpen){
							blockOpen = true;
						}
					}
					nodeBlockFinished = false;
					nodeNameFinished = false;
					nodeName = "";
					break;
				}
				case ' ':{
					if(blockOpen){
						if(atAttributes && attrStart == 0){
							attrStart = i + 1;
						}
						if(blockOpen && !nodeBlockFinished && !nodeNameFinished){
							nodeNameFinished = true;
							node.setNodeName(nodeName);
							atAttributes = true;
							attrStart = i + 1;
						}
					}
					break;
				}
				case '"':{
					if(i != (block.length() - 1)){
						if(block.charAt(i + 1) == ' ' || block.charAt(i+1) == '>' || block.charAt(i+1) == '/'){
							attrEnd = i;
							node.add(new XMLAttribute(block.substring(attrStart, attrEnd).substring(0, block.substring(attrStart, attrEnd).indexOf('=')), block.substring(attrStart, attrEnd).substring(block.substring(attrStart, attrEnd).indexOf('=') + 2, block.substring(attrStart, attrEnd).length())));
							attrStart = 0;
							attrEnd = 0;
						}
					}else{
						throw new Exception("File is corrupted");
					}
					break;
				}
				case '/':{
					attrEnd = 0;
					attrStart = 0;
					atAttributes = false;
					break;
				}
				case '>':{
					if(blockOpen && !nodeBlockFinished && !nodeNameFinished){
						nodeNameFinished = true;
						node.setNodeName(nodeName);
					}
					blockOpen = false;
					if(prevChar == '/'){
						parent.add(node);
						node = new XMLNode();
						break;
					}else{
						int ntc = 0;
						int ei = 0;
						for(int j = i;j<block.length();j++){
							char nc = block.charAt(j);
							if((nc == '/'  && block.charAt(j+1) == ' ' && block.charAt(j+2) == '>') || (nc == '/' && block.charAt(j+1) == '>')){
								ntc++;
							}
						}
						System.out.println("NTC: " + ntc);
						ei = i;
						int nei = 0;
						for(int j = 0;j<ntc;j++){
							nei = block.indexOf("</", ei) + 1;
							ei = nei;
							System.out.println(ei);
							System.out.println(nei);
						}
						System.out.println("EI: " + ei);
						if(i + 1 < ei){
							parseTextBlock(block.substring(i + 1, ei-1), node);
							parent.add(node);
							node = new XMLNode();
							i = block.lastIndexOf('<') - 1;
						}
					}
					break;
				}
				default:{
					if(blockOpen && !nodeBlockFinished && !nodeNameFinished){
						nodeName += c;
					}
					break;
				}
			}
		}
	}
	
	private XMLConfig(){
		this.setConfigLines(new ArrayList<String>());
	}

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
			writer.write(line + "\n");
			for(XMLNode child : node.getChilds()){
				writeNode(child, tabIndex + 1, writer);
			}
			writer.write(formTabs(tabIndex) + "</" + node.getNodeName() + ">\n");
		}
	}
	
	/**
	 * @return the configLines
	 */
	public ArrayList<String> getConfigLines() {
		return configLines;
	}

	/**
	 * @param configLines the configLines to set
	 */
	public void setConfigLines(ArrayList<String> configLines) {
		this.configLines = configLines;
	}
	
	/**
	 * @return the configNode
	 */
	public XMLNode getConfigNode() {
		return rootNode;
	}

	/**
	 * @param configNode the configNode to set
	 */
	public void setConfigNode(XMLNode configNode) {
		this.rootNode = configNode;
	}

	public static class XMLNode {
		
		private ArrayList<XMLNode> childs;
		private ArrayList<XMLAttribute> attributes;
		private String nodeName = "";
		
		public XMLNode(){
			this("");
		}
		
		public XMLNode(String name, ArrayList<XMLAttribute> attributes) {
			if(attributes != null){
				this.setAttributes(attributes);
			}else{
				this.setAttributes(new ArrayList<XMLConfig.XMLAttribute>());
			}
			this.setNodeName(name);
			this.setChilds(new ArrayList<XMLConfig.XMLNode>());
		}
		
		public XMLNode(String name){
			this(name, null);
		}

		/**
		 * @return the childs
		 */
		public ArrayList<XMLNode> getChilds() {
			return childs;
		}

		/**
		 * @param childs the childs to set
		 */
		public void setChilds(ArrayList<XMLNode> childs) {
			this.childs = childs;
		}

		/**
		 * @return the nodeName
		 */
		public String getNodeName() {
			return nodeName;
		}

		/**
		 * @param nodeName the nodeName to set
		 */
		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		/**
		 * @return the attributes
		 */
		public ArrayList<XMLAttribute> getAttributes() {
			return attributes;
		}

		/**
		 * @param attributes the attributes to set
		 */
		public void setAttributes(ArrayList<XMLAttribute> attributes) {
			this.attributes = attributes;
		}

		/**
		 * @param e
		 * @return
		 * @see java.util.ArrayList#add(java.lang.Object)
		 */
		public boolean add(XMLNode e) {
			return childs.add(e);
		}

		/**
		 * @param e
		 * @return
		 * @see java.util.ArrayList#add(java.lang.Object)
		 */
		public boolean add(XMLAttribute e) {
			return attributes.add(e);
		}
		
		public XMLNode getNodeByName(String nodeName){
			for(XMLNode child : this.getChilds()){
				if(child.getNodeName().equals(nodeName))
					return child;
			}
			return null;
		}
		
		public static void printNode(XMLNode node){
			pNode(node, 0);
		}
		
		private static void pNode(XMLNode node, int tabIndex){
			String tabs = "";
			for(int i = 0;i<tabIndex;i++){
				tabs += "  ";
			}
			String tabs2 = tabs + "  ";
			System.out.println(tabs + "[" + node.getNodeName() + "] {");
			for(XMLAttribute attr : node.getAttributes()){
				System.out.println(tabs2 + attr.getAttributeName() + " - " + attr.getAttributeValue());
			}
			for(int i = 0;i<node.getChilds().size();i++){
				pNode(node.childs.get(i), tabIndex+1);
			}
			System.out.println(tabs + "}");
		}
		
	}
	
	public static class XMLAttribute {
		
		private String attributeName = "";
		private Object attributeValue = "";
		
		public XMLAttribute(String name, Object value) {
			this.setAttributeName(name);
			this.setAttributeValue(value);
		}
		
		public XMLAttribute(String name) {
			this(name, null);
		}

		/**
		 * @return the attributeName
		 */
		public String getAttributeName() {
			return attributeName;
		}

		/**
		 * @param attributeName the attributeName to set
		 */
		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		/**
		 * @return the attributeValue
		 */
		public Object getAttributeValue() {
			return attributeValue;
		}

		/**
		 * @param attributeValue the attributeValue to set
		 */
		public void setAttributeValue(Object attributeValue) {
			this.attributeValue = attributeValue;
		}
		
		
		
	}
	
}
