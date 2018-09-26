package search;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

public class XPathEntityCriteria implements XPathCriteria {

	private String contains;
	private String type;
	private String subtype;
	
	public XPathEntityCriteria(String contains, String type, String subtype) {
		this.contains = contains;
		this.type = type;
		this.subtype = subtype;
	}
	
	@Override
	public List<Result> evaluate(Document xml, XPathExpression expression) {
		List<Result> results = new ArrayList<>();
		try {
			String strResult = expression.evaluate(xml);
			System.out.println("String result is:\n" + strResult);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			System.out.println("XPath Expression Exception");
		}
		return results;
	}

	@Override
	public String getExpression() {
		return "entity[type='"+getType()+"' and subtype='" + getSubtype() + "']";
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
