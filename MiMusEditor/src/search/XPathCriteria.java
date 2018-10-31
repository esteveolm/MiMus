package search;

import java.util.List;

import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;

public interface XPathCriteria {
	
	public List<Result> evaluate(Document xml, XPathExpression expression);
	
	public String getExpression();
}
