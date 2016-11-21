package com.redblaster.hsl.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * Class-helper.
 * 
 * Takes care of downloading meta data from server and parsing it.
 * 
 * @author w32blaster
 *
 */
public class MetaData {

	/**
	 * Dowloads metadata and returns values as a MAP
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> loadMetadata() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			
			URL url = new URL(Constants.URL_SITE + Constants.URL_METADATA_FILE);
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document doc = db.parse(new InputSource(url.openStream()));
	    	doc.getDocumentElement().normalize();
	    		    	
	    	NodeList nodeList = doc.getElementsByTagName("metadata");
	    	Node node = nodeList.item(0);
	    	 
    		// getMetadataNodes(node, 0, "date");
	    	map.put("md5", this.getMetadataNodes(node, "md5"));
	    	map.put("size-db", this.getMetadataNodes(node, "size-db"));
	    	map.put("size-gz", this.getMetadataNodes(node, "size-gz"));
	    	map.put("size-db-h", this.getMetadataNodes(node, "size-db-h"));
	    	map.put("date-gen", this.getMetadataNodes(node, "date-gen"));
	    	map.put("date-export", this.getMetadataNodes(node, "date-export"));
	    	map.put("recommended", this.getMetadataNodes(node, "recommended"));
	    	map.put("error-message", this.getMetadataNodes(node, "error-message"));
	    	
    		return map;
    		
		} catch (MalformedURLException e) {
			Log.e("ERROR","DatabaseDownloader.MalformedURLException. Error: " + e.getMessage());
			return null;
		} catch (ParserConfigurationException e) {
			Log.e("ERROR","DatabaseDownloader.ParserConfigurationException. Error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("ERROR","DatabaseDownloader.SAXException. Error: " + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("ERROR","DatabaseDownloader.IOException. Error: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Common action. Derive value of child node
	 * 
	 * @param node - parent node
	 * @param i - sequence index
	 * @param tagName - name of XML tag
	 */
	private String getMetadataNodes(Node node, String tagName) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element fstElmnt = (Element) node;
			NodeList fstNmElmntLst = fstElmnt.getElementsByTagName(tagName);
			Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			NodeList fstNm = fstNmElmnt.getChildNodes();
			Node childrenNode = fstNm.item(0);
			return null == childrenNode ? Constants.STR_EMPTY : childrenNode.getNodeValue();
		}
		else {
			return Constants.STR_EMPTY;
		}
	}
}