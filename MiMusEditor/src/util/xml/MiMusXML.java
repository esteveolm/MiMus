package util.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import control.SharedResources;

/**
 * 
 * TODO: This class could be based on Java Streams.
 * TODO: how to handle failures to append/update/remove?
 * TODO: always calling MiMusXML.open() is too costly in I/O. Make
 * persistent connections.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class MiMusXML {
	
	/* Constants for different files*/
	public static final String BIBLIO;
	public static final String ARTISTA;
	
	private File f;
	private Document doc;
	
	static {
		SharedResources resources = SharedResources.getInstance();
		String repo = resources.getRepoPath();
		BIBLIO = repo + "/bibliography.xml";
		ARTISTA = repo + "/artista.xml";
	}
	
	public MiMusXML(File xmlFile) {
		this.f = xmlFile;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			this.doc = doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.out.println("Could not configure XML Parser.");
		} catch (SAXException e) {
			e.printStackTrace();
			System.out.println("Could not parse XML.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not parse XML.");
		}
		/* If can't read the XML tree, <doc> receives null */
		this.doc = null;
	}
	
	public static MiMusXML open(String name) {
		return MiMusXML.open(new File(name+".xml"));
	}
	
	public static MiMusXML open(File path) {
		return new MiMusXML(path);
	}
	
//	public ArrayList<? extends MiMusWritable> read() {
//		
//	}
	
	public MiMusXML append(MiMusWritable entry) {
		this.doc.appendChild(entry.toXMLElement(this.doc));
		return this;
	}
	
	public MiMusXML update(MiMusWritable entry) {
		if (this.doc != null) {
			/* Find entry to append user, looking at entry id */
			NodeList listIDs = this.doc.getElementsByTagName("id");
			int updateIdx = -1;
			for (int i=0; i<listIDs.getLength(); i++) {
				Node nodeID = listIDs.item(i);
				if (nodeID.getTextContent().equals(entry.getWritableId())) {
					updateIdx = i;
					System.out.println("Found at " + i);
				}
			}
			if (updateIdx > -1) {
				/* Delete old child, add new child */
				Node toRemove = listIDs.item(updateIdx);
				Node parent = toRemove.getParentNode();
				parent.removeChild(toRemove);
				parent.appendChild(entry.toXMLElement(this.doc));
			}
		}
		return this;
	}
	
	public MiMusXML remove(MiMusWritable entry) {
		if (this.doc != null) {
			/* Find id to remove */
			NodeList listIDs = this.doc.getElementsByTagName("id");
			int removeID = -1;
			for (int i=0; i<listIDs.getLength(); i++) {
				Node nodeID = listIDs.item(i);
				if (nodeID.getTextContent().equals(entry.getWritableId())) {
					removeID = i;
					break;
				}
			}
			
			/* Delete node with id to remove by removing it from its parent */
			if (removeID > -1) {
				Element toRemove = (Element) listIDs.item(removeID).getParentNode();
				listIDs.item(removeID).getParentNode().getParentNode().removeChild(toRemove);
			}
		}
		return this;
	}
	
	public boolean write() {
		this.doc.getDocumentElement().normalize();
		try {
			/* Converts Java XML Document to file-system XML */
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        DOMSource source = new DOMSource(this.doc);
	        
	        StreamResult console = new StreamResult(this.f);
	        transformer.transform(source, console);
	        return true;
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			System.out.println("Could not write XML.");
		} catch (TransformerException e) {
			e.printStackTrace();
			System.out.println("Could not write XML.");
		}
		return false;
	}
	
}
