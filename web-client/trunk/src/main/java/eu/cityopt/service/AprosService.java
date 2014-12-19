/**
 * 
 */
package eu.cityopt.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.cityopt.model.Component;
import eu.cityopt.model.InputParameter;

/**
 * @author Olli Stenlund
 *
 */
public class AprosService {
	private final String NODE = "node";
	private final String PROPERTY = "property";
	private final String NAME = "name";
	private final String TYPE = "type";
	private final String VALUE = "value";
	private final String CONSTANT = "constant";
	private final String EXPRESSION = "expression";
	
	public List<Component> listNewComponents = new ArrayList<Component>();
	public List<InputParameter> listNewInputParams = new ArrayList<InputParameter>(); 
	private int newId = 1;
	
	public void readDiagramFile(String xmlFile) {
        try 
        {
            File file = new File("demo-nodes.xml");//xmlFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            
            //Node rootNode = doc.getFirstChild();
            NodeList nodeLst = doc.getElementsByTagName(NODE);
            Node rootNode = nodeLst.item(0);
            handleNode(rootNode, null, 0);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private void handleNode(Node node, Component parentComponent, int level)
	{
	    NodeList nodeChildren = node.getChildNodes();
	    
	    for (int i = 0; i < nodeChildren.getLength(); i++)
        {
            Node childNode = nodeChildren.item(i);
            
            if (childNode.getAttributes() == null || childNode.getAttributes().getNamedItem(NAME) == null)
            {
            	continue;
            }
            
            if (childNode.getNodeName().equals(PROPERTY))
            {
            	String type = childNode.getAttributes().getNamedItem(TYPE).getNodeValue();
            	String name = childNode.getAttributes().getNamedItem(NAME).getNodeValue();
            	String value = childNode.getAttributes().getNamedItem(VALUE).getNodeValue();

            	if (type.equals(CONSTANT))
            	{
            		
            	}
            	else if (type.equals(EXPRESSION))
            	{
            		
            	}
            	
            	InputParameter inputParam = new InputParameter();
            	inputParam.setComponent(parentComponent);
            	inputParam.setName(name);
            	inputParam.setDefaultvalue(value);
            	inputParam.getInputid();
                listNewInputParams.add(inputParam);
        	}
            else if (childNode.getNodeName().equals(NODE))
            {
                Component component = new Component();
                String compName = childNode.getAttributes().getNamedItem(NAME).getNodeValue();
                component.setName(compName);
                component.setComponentid(newId);
                newId++;
                listNewComponents.add(component);

                handleNode(childNode, parentComponent, level + 1);
            }
        }
	}
}
