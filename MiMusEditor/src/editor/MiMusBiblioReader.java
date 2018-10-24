package editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
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
		return entries;
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
		int year = Integer.parseInt(elem.getElementsByTagName("any")
				.item(0).getTextContent());
		String distinction = elem.getElementsByTagName("distincio")
				.item(0).getTextContent();
		String title = elem.getElementsByTagName("titol")
				.item(0).getTextContent();
		String mainTitle = elem.getElementsByTagName("titol_principal")
				.item(0).getTextContent();
		int volume = Integer.parseInt(elem.getElementsByTagName("volum")
				.item(0).getTextContent());
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
	
}
