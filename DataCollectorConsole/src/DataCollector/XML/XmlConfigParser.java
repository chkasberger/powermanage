package DataCollector.XML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import DataCollector.D0;

public class XmlConfigParser {
	private static Logger logger = Logger.getRootLogger();

	//No generics
	List<XmlConfig> myEmpls;
	Document dom;
	String filePath = null;

	public XmlConfigParser(){
		//create a list to hold the employee objects
		myEmpls = new ArrayList<XmlConfig>();
	}

	public void parseFile(String xmlFile) {

		//parse the xml file and get the dom object
		parseXmlFile(xmlFile);

		//get each employee element and create a Employee object
		parseDocument();

		//Iterate through the list and print the data
		printData();

	}


	private void parseXmlFile(String xmlFile){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlFile);
			logger.debug("File name: " + xmlFile);


		}catch(ParserConfigurationException pce) {
			logger.error(pce.getMessage());
		}catch(SAXException se) {
			logger.error(se.getMessage());
		}catch(IOException ioe) {
			logger.error(ioe.getMessage());
		}
	}


	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("interface");
		if(nl != null && nl.getLength() > 0) {

			for(int i = 0 ; i < nl.getLength();i++) {

				//logger.debug(nl.item(i));

				Element el = (Element)nl.item(i);

				logger.debug(el.getAttributes().getNamedItem("type"));

				switch (el.getAttribute("type")) {
				case "JSON":
					logger.debug("--> found JSON interface type");
					logger.debug(getTextValue(el, "url"));
					break;
				case "D0":
					logger.debug("found D0 interface type\r\n\t"
							+ getTextValue(el, "port") + "\r\n\t"
							+ getTextValue(el, "baudRate") + "\r\n\t"
							+ getTextValue(el, "parity") + "\r\n\t"
							+ getTextValue(el, "dataBits") + "\r\n\t"
							+ getTextValue(el, "stopBits") + "\r\n\t"
							+ getTextValue(el, "maxBaudRate") + "\r\n\t"
							+ getTextValue(el, "interval"));

					D0 d0 = new D0();
					d0.setPortName(getTextValue(el, "port"));
					d0.setBaudRate(Integer.parseInt(getTextValue(el, "baudRate")));
					d0.setParity(Integer.parseInt(getTextValue(el, "parity")));
					d0.setDataBits(Integer.parseInt(getTextValue(el, "dataBits")));
					d0.setStopBits(Integer.parseInt(getTextValue(el, "stopBits")));
					d0.setMaxBaudRate(Integer.parseInt(getTextValue(el, "maxBaudRate")));
					d0.setInterval(Integer.parseInt(getTextValue(el, "interval")));
					break;
				case "S0":
					logger.debug("--> found S0 interface type");
					logger.debug(getTextValue(el, "ticksPerKwh"));
					logger.debug(getTextValue(el, "offset"));
					break;
				case "DB":
					logger.debug("--> found DB interface type");
					logger.debug(getTextValue(el, "name"));
					logger.debug(getTextValue(el, "table"));
					logger.debug(getTextValue(el, "user"));
					logger.debug(getTextValue(el, "password"));
					break;

				default:
					break;
				}




				/*
				//get the employee element

				Element el = (Element)nl.item(i);

				//get the Employee object
				XmlConfig xmlConfig = getConfigElement(el);

				//add it to list
				myEmpls.add(xmlConfig);
				 */
			}
		}
	}


	/**
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
	 * @param empEl
	 * @return
	 */
	private XmlConfig getConfigElement(Element empEl) {


		NodeList foo = empEl.getChildNodes();


		for(int i = 0; i < foo.getLength(); i++){
			logger.debug(foo.item(i).getAttributes());
		}

		/*
		//for each <employee> element get text or int values of
		//name ,id, age and name
		String name = getTextValue(empEl,"url");
		int id = getIntValue(empEl,"Id");
		int age = getIntValue(empEl,"Age");

		String type = empEl.getAttribute("type");

		//Create a new Employee with the value read from the xml nodes
		XmlConfig cfg = new XmlConfig(name,id,age,type);
		return cfg;
		 */
		return new XmlConfig();
	}


	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element el, String tagName) {
		String textVal = null;
		NodeList nl = el.getElementsByTagName(tagName);

		if(nl != null && nl.getLength() > 0) {
			Element ele = (Element)nl.item(0);
			textVal = ele.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}

	/**
	 * Iterate through the list and print the
	 * content to console
	 */
	private void printData(){

		System.out.println("No of Employees '" + myEmpls.size() + "'.");

		Iterator<XmlConfig> it = myEmpls.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}
}