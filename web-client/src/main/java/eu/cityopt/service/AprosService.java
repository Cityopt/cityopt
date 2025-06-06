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

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParameterDTO;

/**
 * @author Olli Stenlund
 *
 */
public class AprosService {
	private final String NODE = "node";
	private final String PROPERTY = "property";
	private final String NAME = "name";
	private final String MODULE_NAME = "moduleName";
	private final String TYPE = "type";
	private final String VALUE = "value";
	private final String CONSTANT = "constant";
	private final String EXPRESSION = "expression";
	
	public List<ComponentDTO> listNewComponents = new ArrayList<ComponentDTO>();
	public List<InputParameterDTO> listNewInputParams = new ArrayList<InputParameterDTO>(); 
	
	@Autowired InputParameterService inputparameterService;
	
	public void readDiagramFile(String xmlFile, int maxLevel) {
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
            handleNode(rootNode, null, 0, maxLevel);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private void handleNode(Node node, ComponentDTO parentComponent, int level, int maxLevel)
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
            		// skip?
            	}
            	
            	InputParameterDTO inputParam = new InputParameterDTO();
            	
            	if (parentComponent != null)
            	{
            		try {
						inputparameterService.update(inputParam, parentComponent.getComponentid(), inputParam.getUnit().getUnitid(), null);
					} catch (EntityNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		//inputParam.setComponent(parentComponent);
            	}
            	
            	inputParam.setName(name);
            	inputParam.setDefaultvalue(value);
            	inputParam.getInputid();
                listNewInputParams.add(inputParam);
        	}
            else if (childNode.getNodeName().equals(NODE))
            {
                ComponentDTO component = new ComponentDTO();
                String compName = childNode.getAttributes().getNamedItem(MODULE_NAME).getNodeValue();
                component.setName(compName);
                //component.setComponentid(newId);
                //newId++;
                listNewComponents.add(component);

                if (level <= maxLevel)
                {
                	handleNode(childNode, component, level + 1, maxLevel);
                }
            }
        }
	}
}
