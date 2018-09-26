package search;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XPathEntityCriteria implements XPathCriteria {

	private String contains;
	private String type;
	private String subtype;
	
	public XPathEntityCriteria(String contains, String type, String subtype) {
		if (type.equals("")) {
			type = null;
		}
		if (subtype.equals("")) {
			subtype = null;
		}
		this.contains = contains;
		this.type = type;
		this.subtype = subtype;
	}
	
	@Override
	public List<Result> evaluate(Document xml, XPathExpression expression) {
		List<Result> results = new ArrayList<>();
		try {
			NodeList nodes = (NodeList) expression.evaluate(xml, XPathConstants.NODESET);
			for (int i=0; i<nodes.getLength(); i++) {
				Element e = (Element) nodes.item(i);
				String text = e.getElementsByTagName("text").item(0).getTextContent();
				String type = e.getElementsByTagName("type").item(0).getTextContent();
				String subtype = e.getElementsByTagName("subtype").item(0).getTextContent();
				String doc = "001";
				Result res = new Result(text, type, subtype, doc);
				results.add(res);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			System.out.println("XPath Expression Exception");
		}
		return results;
	}

	@Override
	public String getExpression() {
		//return "entity[type='"+getType()+"' and subtype='" + getSubtype() + "']";
		String constraints = "[";
		if (type!=null) {
			constraints += "type='" + type + "'";
			if (subtype!=null) {
				constraints += " and subtype='" + subtype + "'";
			}
		}
		constraints += ']';
		System.out.println(constraints);
		return "/document/entities/entity" + constraints;
	}

	/* Getters and setters */
	
	public String getContains() {
		return contains;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

}
