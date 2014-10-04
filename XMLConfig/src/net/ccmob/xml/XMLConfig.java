package net.ccmob.xml;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class XMLConfig {

	private ArrayList<String> configLines;
	private XMLNode rootNode = new XMLNode("XMLConfig");

	public XMLConfig(String filename) {
		this(new File(filename));
	}

	public XMLConfig(File f) {
		this();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = reader.readLine()) != null) {
				this.getConfigLines().add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parse();
	}

	public XMLConfig(ArrayList<String> lines) {
		this();
		this.setConfigLines(lines);
		parse();
	}

	public XMLConfig(String[] lines) {
		this();
		for (int i = 0; i < lines.length; i++) {
			this.getConfigLines().add(lines[i]);
		}
		parse();
	}

	public void parse() {
		try {
			this.setConfigNode(XMLParser.parseFileLines(this.getConfigLines()));
		} catch (EOFException e) {
			e.printStackTrace();
		}
	}

	private XMLConfig() {
		this.setConfigLines(new ArrayList<String>());
	}

	public void save(String filename) {
		try {
			File f = new File(filename);
			if (f.exists()) {
				f.delete();
			}
			FileWriter writer = new FileWriter(f);
			writeNode(getConfigNode(), 0, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String formAttribute(XMLAttribute attr) {
		return attr.getAttributeName() + "=\"" + attr.getAttributeValue()
				+ "\"";
	}

	private String formTabs(int tabIndex) {
		String tabs = "";
		for (int i = 0; i < tabIndex; i++) {
			tabs += "\t";
		}
		return tabs;
	}

	private void writeNode(XMLNode node, int tabIndex, FileWriter writer)
			throws IOException {
		String line = formTabs(tabIndex) + "<" + node.getName();
		if (node.getAttributes().size() > 0) {
			for (int i = 0; i < node.getAttributes().size(); i++) {
				line += " " + formAttribute(node.getAttributes().get(i));
			}
		}
		if (node.getChilds().size() == 0) {
			line += "/>";
			writer.write(line + "\n");
		} else {
			line += ">";
			writer.write(line + "\n");
			for (XMLNode child : node.getChilds()) {
				writeNode(child, tabIndex + 1, writer);
			}
			writer.write(formTabs(tabIndex) + "</" + node.getName() + ">\n");
		}
	}

	/**
	 * @return the configLines
	 */
	public ArrayList<String> getConfigLines() {
		return configLines;
	}

	/**
	 * @param configLines
	 *            the configLines to set
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
	 * @param configNode
	 *            the configNode to set
	 */
	public void setConfigNode(XMLNode configNode) {
		this.rootNode = configNode;
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
		 * @param attributeName
		 *            the attributeName to set
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
		 * @param attributeValue
		 *            the attributeValue to set
		 */
		public void setAttributeValue(Object attributeValue) {
			this.attributeValue = attributeValue;
		}

	}

	public static class XMLNode {
		private String name;
		private ArrayList<XMLNode> childs;
		private ArrayList<XMLAttribute> attributes;
		private XMLNode parent;

		public XMLNode() {
			this.childs = new ArrayList<XMLNode>();
			this.attributes = new ArrayList<XMLAttribute>();
		}

		public XMLNode(String name) {
			this();
			this.setName(name);
		}

		public void addAttribute(String key, String value) {
			this.attributes.add(new XMLAttribute(key, value));
		}

		public void addChild(XMLNode child) {
			this.childs.add(child);
			child.setParent(this);
		}

		public XMLNode getChild(int index) {
			return (XMLNode) this.childs.get(index);
		}

		public int getNumChilds() {
			return this.childs.size();
		}

		public void setParent(XMLNode parent) {
			this.parent = parent;
		}

		public XMLNode getParent() {
			return this.parent;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getXMLString() {
			return getXMLString(0);
		}

		private String getXMLString(int height) {
			String s = "";
			for (int j = 0; j < height; j++)
				s = s + "\t";
			s = s + "<" + this.name + getAttributeString()
					+ (getNumChilds() == 0 ? " /" : "") + ">\n";
			if (this.childs.size() != 0) {
				for (int i = 0; i < this.childs.size(); i++) {
					s = s + ((XMLNode) this.childs.get(i)).getXMLString(height + 1);
				}
				for (int j = 0; j < height; j++)
					s = s + "\t";
				s = s + "</" + this.name + ">\n";
			}
			return s;
		}

		private String getAttributeString() {
			String s = "";
			for (int i = 0; i < this.attributes.size(); i++) {
				s = s + this.attributes.get(i).getAttributeName() + "=\""
						+ this.attributes.get(i).getAttributeValue() + "\" ";
			}
			s = s.trim();
			if (this.attributes.size() > 0)
				s = " " + s;
			return s;
		}

		/**
		 * @return the childs
		 */
		public ArrayList<XMLNode> getChilds() {
			return childs;
		}

		/**
		 * @param childs
		 *            the childs to set
		 */
		public void setChilds(ArrayList<XMLNode> childs) {
			this.childs = childs;
		}

		/**
		 * @return the attributes
		 */
		public ArrayList<XMLAttribute> getAttributes() {
			return attributes;
		}

		/**
		 * @param attributes
		 *            the attributes to set
		 */
		public void setAttributes(ArrayList<XMLAttribute> attributes) {
			this.attributes = attributes;
		}

		public static void printNode(XMLNode node) {
			pNode(node, 0);
		}

		private static void pNode(XMLNode node, int tabIndex) {
			String tabs = "";
			for (int i = 0; i < tabIndex; i++) {
				tabs += "  ";
			}
			String tabs2 = tabs + "  ";
			System.out.println(tabs + "[" + node.getName() + "] {");
			for (XMLAttribute attr : node.getAttributes()) {
				System.out.println(tabs2 + attr.getAttributeName() + " - "
						+ attr.getAttributeValue());
			}
			for (int i = 0; i < node.getChilds().size(); i++) {
				pNode(node.childs.get(i), tabIndex + 1);
			}
			System.out.println(tabs + "}");
		}
		
	}

	public static class XMLParser
	{
	  public static XMLNode parseText(String text)
	    throws EOFException
	  {
	    XMLNode currentNode = new XMLNode();
	    char[] c = text.toCharArray();
	    for (int i = 0; i < c.length; i++) {
	      if (c[i] == '/') {
	        if (currentNode.getParent().getName() == null) return currentNode;
	        currentNode = currentNode.getParent();
	      }
	      if ((c[i] == '<') && (c[(i + 1)] != '/')) {
	        String tagText = text.substring(i + 1);
	        XMLNode newNode = new XMLNode();
	        currentNode.addChild(newNode);
	        currentNode = newNode;
	        currentNode.setName(tagText.substring(0, tagText.indexOf(">")).split(" ")[0]);
	        int tagEnd = min(tagText.indexOf(">"), tagText.indexOf("/"));

	        String attribText = tagText.substring(0, tagEnd);
	        for (String currentAttribute : attribText.split(" ")) {
	          if (currentAttribute.split("=").length >= 2)
	            currentNode.addAttribute(currentAttribute.split("=")[0], currentAttribute.split("=")[1].replace("\"", ""));
	        }
	        i = i + tagEnd - 1;
	      }
	    }

	    throw new EOFException("Nodes aren't closed properly");
	  }

	  public static XMLNode parseFile(String filePath) throws EOFException
	  {
	    StringBuilder text = new StringBuilder();
	    String line = "";
	    try
	    {
	      BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));

	      while ((line = reader.readLine()) != null) text.append(line);
	      reader.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return parseText(text.toString());
	  }
	  
	  public static XMLNode parseFileLines(ArrayList<String> lines) throws EOFException {
		  StringBuilder text = new StringBuilder();
		  for(int i = 0;i<lines.size();i++){
			  text.append(lines.get(i));
		  }
		  return parseText(text.toString());
	  }

	  private static int min(int a, int b) {
	    return a < b ? a : b;
	  }
	}

}