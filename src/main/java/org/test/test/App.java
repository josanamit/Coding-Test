package org.test.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.opencsv.CSVReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class App 
{
	
	public static final Logger LOG=LoggerFactory.getLogger(App.class);
	

    public static void main( String[] args )
    {
        LOG.info("log start");

        App app =new App();
        
        List<String[]> objectList=new ArrayList<String[]>();
        objectList=app.readCSV();
        
        
		if(app.XMLParser(objectList))    
		{
			LOG.info("Xml File created ");
		}
        
        if(app.createJSON(objectList))
        {
        	
        	LOG.info("JSON file created");
        }
    }
    
    /**
     * 
     * @return returns a list of the objects read from CSV file
     */
	public List<String[]> readCSV()
	{
    	List<String []> list =new ArrayList<String []>();

		try {
			CSVReader csvReader=new	CSVReader(new FileReader("src/main/java/Periodic Table of Elements(1).csv"));
			
			list=csvReader.readAll();
			
			
			
			csvReader.close();
			logObjects(list);
			return list;

			
		} catch (FileNotFoundException e) {
			LOG.info("File Not found at the location");
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			LOG.info("Not able to read file");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
		
		
		
	}
	
	
	
	private void logObjects(List<String[]> inputList)
	{
		List<String[]> list=new ArrayList<String[]>();
    	list.addAll(inputList);
    	String[] header=list.get(0);
		list.remove(0);
		Iterator<String[]> itr=list.listIterator();
		while(itr.hasNext())
		{
			String[] listObjects=itr.next();
			String objects="";
			for(int i=0;i<header.length;i++)
			{
				objects=objects.concat(header[i].concat(":").concat(listObjects[i]).concat(" | "));
				
				
			}
			//System.out.println("New Java Object:  "+objects);
			LOG.info("New Object: "+objects );
		}
		
	}
	
	
	/**
	 * 
	 * @param inputList List of objects to be writtn in XML document
	 * @return true is the XML file is written
	 */
    public boolean XMLParser(List<String[]> inputList ) 
	{
    	List<String[]> list=new ArrayList<String[]>();
    	list.addAll(inputList);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		
		
		try{
			documentBuilder = builderFactory.newDocumentBuilder();		
			Document document=documentBuilder.newDocument();
			org.w3c.dom.Element root=document.createElement("Elements");
			
			document.appendChild(root);
			
			
			String[] attributeHeader=list.get(0);
			list.remove(0);
			
			Iterator<String[]> itr=list.iterator();
			
			
			while(itr.hasNext())
			{
				String[] attributeValue=itr.next();
				
				org.w3c.dom.Element row=document.createElement("Element");
				root.appendChild(row);
				for(int i=0;i<attributeHeader.length;i++)
				{
					
					String name=attributeHeader[i];
					name=name.replaceAll("[^\\p{Alpha}\\p{Digit}]+","");
					org.w3c.dom.Element currentElement=document.createElement(name);
					currentElement.appendChild(document.createTextNode(attributeValue[i]));
					
					row.appendChild(currentElement);
				}
				
			}
			
			
			
				FileWriter xmlWriter=new FileWriter(new File("src/main/java/new.xml"));
				
				
				TransformerFactory transformerFactory=TransformerFactory.newInstance();
				Transformer transformer= transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				
				Source source=new  DOMSource(document);
				Result result=new StreamResult(xmlWriter);
				
				transformer.transform(source, result);
				
				LOG.info("XML File Created");
				System.out.println("File Created");
				xmlWriter.close();
				return true;
				
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return false;
	}
	
    /**
     * 
     * @param inputList List of objects to be written on JSON file
     * @return True if JSON file is created
     */
    public boolean createJSON(List<String []> inputList)
    {
    	List<String[]> list=new ArrayList<String[]>();
    	list.addAll(inputList);
    	
    	String[] jsonHeaders=list.get(0);
    	list.remove(0);
    	
    	
    	
    	JSONArray jsonArray=new JSONArray();
    	Iterator<String[]> itr=list.iterator();
    	while(itr.hasNext())
    	{
        	JSONObject jsonObject=new JSONObject();

    		String[] jsonArrayElements=itr.next();
    		
    		for(int i=0;i<jsonHeaders.length;i++)
    		{
    			
    			jsonObject.put(jsonHeaders[i], jsonArrayElements[i]);
    			
    		}
    		jsonArray.add(jsonObject);
    		
    	}
    	
    	
    	JSONObject mainObject=new JSONObject();
    	mainObject.put("Elements",jsonArray);
    	
    	
    	FileWriter jsonFile;
    	
    	try {
    		 jsonFile=new FileWriter(new File("src/main/java/data.json"));
			jsonFile.write(mainObject.toJSONString());
			
			jsonFile.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
   return false;
    }
   

}
