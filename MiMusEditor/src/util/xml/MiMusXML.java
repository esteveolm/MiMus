package util.xml;

import java.io.File;
import java.io.IOException;

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
 * MiMusXML handles connections to the MiMus local database of XML files
 * that is stored locally in the annotator's workspace. Programmatically,
 * it works as a stream where the main methods return the object itself
 * with a modified state, allowing to pipe several operators from the
 * opening of a file to its writing. This pipe may consist of 4 stages:
 * 
 * 1. Open: static methods that search for specific files of the XML
 * database, such as the Bibliography or a Document, and return the
 * MiMusXML object to handle this file. Repeatedly asking to open the
 * same file does not re-create it from scratch. This is done to
 * improve efficiency, because most times the code is performing editions
 * on one file at a time. Open methods are the only way to use MiMusXML,
 * constructors are private.
 * 
 * 2. CRUD operations: objects that properly implement MiMusWritable and
 * follow the naming schema of the XML database can be appended, updated
 * or removed from the file opened.
 * 
 * 3. Write: modifications to the opened file are written down to disk.
 * 
 * 4. Close: the opened file can be closed explicitly. This will make the
 * Open methods to reload the file from disk, even if it was already
 * loaded.
 * 
 * TODO: how to handle failures to append/update/remove?
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class MiMusXML {
	
	private static File f;
	private static Document doc;
	
	private MiMusXML() {}
	
	private MiMusXML(File xmlFile) {
		f = xmlFile;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.out.println("Could not configure XML Parser.");
			doc = null;
		} catch (SAXException e) {
			e.printStackTrace();
			System.out.println("Could not parse XML.");
			doc = null;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not parse XML.");
			doc = null;
		}
		if (doc==null)
			System.exit(1);
		System.out.println("Document is:" + f);
	}
	
	private static MiMusXML openCommon(File file) {
		/* If same file as before is asked, don't re-create it */
		if (f == null || !f.getPath().equals(file.getPath())) {
			return new MiMusXML(file);
		} else {
			return new MiMusXML();
		}
	}
	
	public static MiMusXML openBiblio() {
		return openCommon(new File(
				SharedResources.getInstance().getBiblioPath()));
	}
	
	public static MiMusXML openArtista() {
		return openCommon(new File(
				SharedResources.getInstance().getArtistaPath()));
	}
	
	public static MiMusXML openInstrument() {
		return openCommon(new File(
				SharedResources.getInstance().getInstrumentPath()));
	}
	
	public static MiMusXML openCasa() {
		return openCommon(new File(
				SharedResources.getInstance().getCasaPath()));
	}
	
	public static MiMusXML openPromotor() {
		return openCommon(new File(
				SharedResources.getInstance().getPromotorPath()));
	}
	
	public static MiMusXML openLloc() {
		return openCommon(new File(
				SharedResources.getInstance().getLlocPath()));
	}
	
	public static MiMusXML openOfici() {
		return openCommon(new File(
				SharedResources.getInstance().getOficiPath()));
	}
	
	public static MiMusXML openDoc(String docIdStr) {
		File path = new File(SharedResources.getInstance().getXmlPath()
				+ "/" + docIdStr + ".xml");
		System.out.println("filepath: " + path);
		if (!path.exists()) {
			MiMusXML.createDoc(path).write().close();
		}
		return openCommon(path);
	}
	
	private static MiMusXML createDoc(File path) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			org.w3c.dom.Document newDoc = docBuilder.newDocument();
			Element root = newDoc.createElement("document");
			newDoc.appendChild(root);
			Element entities = newDoc.createElement("entities");
			Element transcriptions = newDoc.createElement("transcriptions");
			Element references = newDoc.createElement("references");
			root.appendChild(entities);
			root.appendChild(transcriptions);
			root.appendChild(references);
			doc = newDoc;
			f = path;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return new MiMusXML();
	}
	
	public MiMusXML append(Persistable entry) {
		System.out.println(entry.toString());
		Node parent = doc.getElementsByTagName(
				entry.getWritableCategory()).item(0);
		System.out.println(parent.getNodeName());
		parent.appendChild(entry.toXMLElement(doc));
		return this;
	}
	
	public MiMusXML update(Persistable entry) {
		if (doc != null) {
			/* Find entry to append user, looking at entry id */
			NodeList listIDs = doc.getElementsByTagName("id");
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
				Node toRemove = listIDs.item(updateIdx).getParentNode();
				Node parent = toRemove.getParentNode();
				parent.removeChild(toRemove);
				parent.appendChild(entry.toXMLElement(doc));
			}
		}
		return this;
	}
	
	public MiMusXML remove(Persistable entry) {
		if (doc != null) {
			/* Find id to remove */
			NodeList listIDs = doc.getElementsByTagName("id");
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
				listIDs.item(removeID)
						.getParentNode().getParentNode().removeChild(toRemove);
			}
		}
		return this;
	}
	
	public MiMusXML write() {
		doc.getDocumentElement().normalize();
		try {
			/* Converts Java XML Document to file-system XML */
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        DOMSource source = new DOMSource(doc);
	        
	        StreamResult console = new StreamResult(f);
	        transformer.transform(source, console);
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			System.out.println("Could not write XML.");
		} catch (TransformerException e) {
			e.printStackTrace();
			System.out.println("Could not write XML.");
		}
		return this;
	}
	
	public MiMusXML close() {
		f = null;
		doc = null;
		return this;
	}

	/* Getters and setters */
	
	public File getF() {
		return f;
	}
	public void setF(File file) {
		f = file;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document d) {
		doc = d;
	}
}
