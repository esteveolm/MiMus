package util;

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

import model.MiMusBibEntry;

public class MiMusBiblioReader {
	
	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;
	
	public static ArrayList<MiMusBibEntry> read(String path) {
		ArrayList<MiMusBibEntry> entries = new ArrayList<>();
		Document doc = documentFromPath(path);
		if (doc != null) {
			/* Iterates <entry> nodes */
			NodeList nl = doc.getElementsByTagName("entry");
			for (int i=0; i<nl.getLength(); i++) {
				Node node = nl.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					MiMusBibEntry entry = entryFromXMLElement(elem);
					if (!entries.contains(entry)) {
						entries.add(entry);
					}
				}
			}
		}
		return entries;
	}
	
	public static Document documentFromPath(String path) {
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			return doc;
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
		/* If can't read the XML tree, null is returned instead */
		return null;
	}
	
	public static MiMusBibEntry entryFromXMLElement(Element elem) {
		String[] authors = new String[NUM_AUTHORS];
		for (int i=0; i<NUM_AUTHORS; i++) {
			authors[i] = elem.getElementsByTagName("autor"+(i+1))
					.item(0).getTextContent();
		}
		String[] secondaries = new String[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			secondaries[i] = elem.getElementsByTagName("autor_secundari"+(i+1))
					.item(0).getTextContent();
		}
		String year = elem.getElementsByTagName("any")
				.item(0).getTextContent();
		String distinction = elem.getElementsByTagName("distincio")
				.item(0).getTextContent();
		String title = elem.getElementsByTagName("titol")
				.item(0).getTextContent();
		String mainTitle = elem.getElementsByTagName("titol_principal")
				.item(0).getTextContent();
		String volume = elem.getElementsByTagName("volum")
				.item(0).getTextContent();
		String place = elem.getElementsByTagName("lloc")
				.item(0).getTextContent();
		String editorial = elem.getElementsByTagName("editorial")
				.item(0).getTextContent();
		String series = elem.getElementsByTagName("serie")
				.item(0).getTextContent();
		int id = Integer.parseInt(elem.getElementsByTagName("id")
				.item(0).getTextContent());
		
		return new MiMusBibEntry(authors, secondaries, year, distinction, 
				title, mainTitle, volume, place, editorial, series, id);
	}
	
	public static void append(String path, MiMusBibEntry entry) {
		Document doc = documentFromPath(path);
		if (doc != null) {
			Node node = doc.getElementsByTagName("bibliography").item(0);
			Element elem = doc.createElement("entry");
			Element a1 = doc.createElement("autor1");
			a1.appendChild(doc.createTextNode(entry.getAuthor(0)));
			Element a2 = doc.createElement("autor2");
			a2.appendChild(doc.createTextNode(entry.getAuthor(1)));
			Element a3 = doc.createElement("autor3");
			a3.appendChild(doc.createTextNode(entry.getAuthor(2)));
			Element a4 = doc.createElement("autor4");
			a4.appendChild(doc.createTextNode(entry.getAuthor(3)));
			Element s1 = doc.createElement("autor_secundari1");
			s1.appendChild(doc.createTextNode(entry.getSecondaryAuthor(0)));
			Element s2 = doc.createElement("autor_secundari2");
			s1.appendChild(doc.createTextNode(entry.getSecondaryAuthor(1)));
			Element s3 = doc.createElement("autor_secundari3");
			s1.appendChild(doc.createTextNode(entry.getSecondaryAuthor(2)));
			Element s4 = doc.createElement("autor_secundari4");
			s1.appendChild(doc.createTextNode(entry.getSecondaryAuthor(3)));
			Element s5 = doc.createElement("autor_secundari5");
			s1.appendChild(doc.createTextNode(entry.getSecondaryAuthor(4)));
			Element s6 = doc.createElement("autor_secundari6");
			s1.appendChild(doc.createTextNode(entry.getSecondaryAuthor(5)));
			Element year = doc.createElement("any");
			year.appendChild(doc.createTextNode(entry.getYear()));
			Element distinction = doc.createElement("distincio");
			distinction.appendChild(doc.createTextNode(entry.getDistinction()));
			Element title = doc.createElement("titol");
			title.appendChild(doc.createTextNode(entry.getTitle()));
			Element mainTitle = doc.createElement("titol_principal");
			mainTitle.appendChild(doc.createTextNode(entry.getMainTitle()));
			Element volume = doc.createElement("volum");
			volume.appendChild(doc.createTextNode(entry.getVolume()));
			Element place = doc.createElement("lloc");
			place.appendChild(doc.createTextNode(entry.getPlace()));
			Element editorial = doc.createElement("editorial");
			editorial.appendChild(doc.createTextNode(entry.getEditorial()));
			Element series = doc.createElement("serie");
			series.appendChild(doc.createTextNode(entry.getSeries()));
			Element id = doc.createElement("id");
			id.appendChild(doc.createTextNode(String.valueOf(entry.getId())));
			
			elem.appendChild(id);
			elem.appendChild(a1);
			elem.appendChild(a2);
			elem.appendChild(a3);
			elem.appendChild(a4);
			elem.appendChild(s1);
			elem.appendChild(s2);
			elem.appendChild(s3);
			elem.appendChild(s4);
			elem.appendChild(s5);
			elem.appendChild(s6);
			elem.appendChild(year);
			elem.appendChild(distinction);
			elem.appendChild(title);
			elem.appendChild(mainTitle);
			elem.appendChild(volume);
			elem.appendChild(place);
			elem.appendChild(editorial);
			elem.appendChild(series);
			node.appendChild(elem);
			write(path, doc);
		}
	}
	
	public static void remove(String path, MiMusBibEntry entry) {
		Document doc = documentFromPath(path);
		if (doc != null) {
			/* Find id to remove */
			NodeList listIDs = doc.getElementsByTagName("id");
			int removeID = -1;
			for (int i=0; i<listIDs.getLength(); i++) {
				Node nodeID = listIDs.item(i);
				if (Integer.parseInt(nodeID.getTextContent()) == entry.getId()) {
					removeID = i;
					break;
				}
			}
			
			/* Delete node with id to remove by removing it from its parent */
			if (removeID > -1) {
				Element toRemove = (Element) listIDs.item(removeID).getParentNode();
				listIDs.item(removeID).getParentNode().getParentNode().removeChild(toRemove);
				write(path, doc);
			}
		}
	}
	
	public static void write(String path, Document doc) {
		doc.getDocumentElement().normalize();
		try {
			/* Converts Java XML Document to file-system XML */
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        DOMSource source = new DOMSource(doc);
	        
	        File f = new File(path);
	        StreamResult console = new StreamResult(f);
	        transformer.transform(source, console);
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			System.out.println("Could not write XML.");
		} catch (TransformerException e) {
			e.printStackTrace();
			System.out.println("Could not write XML.");
		}
	}
	
}
