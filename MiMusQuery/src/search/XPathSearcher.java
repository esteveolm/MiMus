package search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XPathSearcher {
	
	private XPathCriteria criteria;
	private File collection;
	
	public XPathSearcher(XPathCriteria criteria, File collection) throws IOException {
			this.setCriteria(criteria);
			this.setCollection(collection);
	}

	public List<Result> search() {
		List<Result> results = new ArrayList<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			String strExpression = criteria.getExpression();
			XPathExpression expr = xpath.compile(strExpression);
			
			File[] files = collection.listFiles();
			for (File f: files) {
				if (f.isFile() && f.getName().endsWith(".xml")) {
					try {
						Document doc = builder.parse(f);
						results.addAll(criteria.evaluate(doc, expr));
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("IO Exception parsing document " + f.getAbsolutePath());
					} catch (SAXException e) {
						e.printStackTrace();
						System.out.println("SAX Exception parsing document " + f.getAbsolutePath());
					}
				}
			}
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
			System.out.println("Parser Configuration Exception");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			System.out.println("XPath Expression Exception");
		}
		return results;
	}
	
	/* Getters and setters */
	
	public XPathCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(XPathCriteria criteria) {
		this.criteria = criteria;
	}

	public File getCollection() {
		return collection;
	}

	public void setCollection(File collection)  throws IOException {
		if (collection!=null && collection.exists() && collection.isDirectory()) {
			this.collection = collection;
		} else throw new IOException("Wrong collection of XML files.");
	}
	
}
