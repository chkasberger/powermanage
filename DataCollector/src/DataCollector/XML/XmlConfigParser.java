/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.XML;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import DataCollector.IO.ComPort.DataBits;
import DataCollector.IO.ComPort.Parity;
import DataCollector.IO.ComPort.StopBits;

public class XmlConfigParser {
	private static Logger logger = Logger.getRootLogger();

	private Document dom;
	private String xmlFile;
	private S0 s0;
	private D0 d0;
	private JSON json;
	private MYSQL mysql;

	public synchronized S0 getS0() {
		return s0;
	}

	public synchronized D0 getD0() {
		return d0;
	}

	public synchronized JSON getJson() {
		return json;
	}

	public synchronized MYSQL getMysql() {
		return mysql;
	}

	public boolean getConfig(String xmlFile) {
		boolean healtyConfig = false;
		this.xmlFile = xmlFile;

		try {
			parseXmlFile(xmlFile);
			parseDocument();

			healtyConfig = true;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error(e.getMessage());
		}

		return healtyConfig;
	}

	public boolean setConfig(String type, String nodeName, String nodeValue) {

		Element rootElement = dom.getDocumentElement();

		NodeList nl = rootElement.getElementsByTagName("interface");
		if (nl != null && nl.getLength() > 0) {

			for (int i = 0; i < nl.getLength(); i++) {

				Element subElement = (Element) nl.item(i);

				if (subElement.getAttribute("type").equalsIgnoreCase(type)) {
					setConfigElement(subElement, nodeName, nodeValue);
					try {
						FileWriter fstream = new FileWriter(xmlFile);
						BufferedWriter out = new BufferedWriter(fstream);

						Transformer transformer = TransformerFactory
								.newInstance().newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");

						StreamResult result = new StreamResult(
								new StringWriter());
						DOMSource source = new DOMSource(dom);
						transformer.transform(source, result);

						String xmlString = result.getWriter().toString();

						out.write(xmlString);
						out.close();
						logger.debug("updated config.xml file with new offset for S0 interface");
					} catch (Exception e) {// Catch exception if any
						System.err.println("Error: " + e.getMessage());
					}
				}
			}
		}
		return false;
	}

	private void parseXmlFile(String xmlFile)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		dom = db.parse(xmlFile);
		logger.debug("File name: " + xmlFile);
	}

	private void parseDocument() {
		Element rootElement = dom.getDocumentElement();

		NodeList nl = rootElement.getElementsByTagName("interface");
		if (nl != null && nl.getLength() > 0) {

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);

				logger.debug(el.getAttributes().getNamedItem("type"));

				switch (el.getAttribute("type")) {
				case "JSON":
					logger.debug("--> found JSON interface type\r\n\t"
							+ getConfigElement(el, "url"));
					json = new JSON();
					json.setUrl(getConfigElement(el, "url"));
					json.setInterval(Integer.parseInt(getConfigElement(el,
							"interval")));
					break;
				case "D0":
					logger.debug("found D0 interface type\r\n\t"
							+ getConfigElement(el, "port") + "\r\n\t"
							+ getConfigElement(el, "baudrate") + "\r\n\t"
							+ getConfigElement(el, "parity") + "\r\n\t"
							+ getConfigElement(el, "databits") + "\r\n\t"
							+ getConfigElement(el, "stopbits") + "\r\n\t"
							+ getConfigElement(el, "maxbaudrate") + "\r\n\t"
							+ getConfigElement(el, "interval"));

					d0 = new D0();
					d0.setPortName(getConfigElement(el, "port"));
					d0.setBaudRate(Integer.parseInt(getConfigElement(el,
							"baudrate")));
					switch (getConfigElement(el, "parity")) {
					case "NONE":
						d0.setParity(Parity.NONE);
						break;
					case "ODD":
						d0.setParity(Parity.ODD);
						break;
					case "EVEN":
						d0.setParity(Parity.EVEN);
						break;
					case "MARK":
						d0.setParity(Parity.MARK);
						break;
					case "SPACE":
						d0.setParity(Parity.SPACE);
						break;
					default:
						d0.setParity(Parity.EVEN);
						break;
					}
					switch (Integer.parseInt(getConfigElement(el, "databits"))) {
					case 7:
						d0.setDataBits(DataBits.SEVEN);
						break;
					case 8:
						d0.setDataBits(DataBits.EIGHT);
						break;
					default:
						d0.setDataBits(DataBits.SEVEN);
						break;
					}
					switch (getConfigElement(el, "stopbits")) {
					case "1":
						d0.setStopBits(StopBits.ONE);
						break;
					case "1.5":
						d0.setStopBits(StopBits.ONEPOINTFIVE);
						break;
					case "2":
						d0.setStopBits(StopBits.TWO);
						break;
					default:
						d0.setStopBits(StopBits.ONE);
						break;
					}
					d0.setMaxBaudRate(Integer.parseInt(getConfigElement(el,
							"maxbaudrate")));
					d0.setInterval(Integer.parseInt(getConfigElement(el,
							"interval")));
					break;
				case "S0":
					logger.debug("--> found S0 interface type\r\n\t"
							+ getConfigElement(el, "ticksperkwh") + "\r\n\t"
							+ getConfigElement(el, "offset") + "\r\n\t"
							+ getConfigElement(el, "gpiopin") + "\r\n\t"
							+ getConfigElement(el, "pullresistance") + "\r\n\t"
							+ getConfigElement(el, "interval"));
					s0 = new S0();
					s0.setTicksPerKwh(Double.parseDouble(getConfigElement(el,
							"ticksperkwh")));
					s0.setOffset(Double.parseDouble(getConfigElement(el,
							"offset")));
					s0.setGpioPin(getConfigElement(el, "gpiopin"));
					s0.setPullResistance(getConfigElement(el, "pullresistance"));
					s0.setInterval(Integer.parseInt(getConfigElement(el,
							"interval")));
					break;
				case "DB":
					logger.debug("--> found DB interface type\r\n\t"
							+ getConfigElement(el, "hostname") + "\r\n\t"
							+ getConfigElement(el, "database") + "\r\n\t"
							+ getConfigElement(el, "table") + "\r\n\t"
							+ getConfigElement(el, "user") + "\r\n\t"
							+ getConfigElement(el, "password"));
					mysql = new MYSQL();
					mysql.setHostname(getConfigElement(el, "hostname"));
					mysql.setDatabase(getConfigElement(el, "database"));
					mysql.setTable(getConfigElement(el, "table"));
					mysql.setPort(Integer
							.parseInt(getConfigElement(el, "port")));
					mysql.setUser(getConfigElement(el, "user"));
					mysql.setPassword(getConfigElement(el, "password"));
					mysql.setInterval(Integer.parseInt(getConfigElement(el,
							"interval")));
					break;

				default:
					break;
				}
			}
		}
	}

	private String getConfigElement(Element el, String tagName) {
		String textVal = null;
		NodeList nl = el.getElementsByTagName(tagName);

		if (nl != null && nl.getLength() > 0) {
			Element ele = (Element) nl.item(0);
			textVal = ele.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	private boolean setConfigElement(Element el, String tagName,
			String nodeValue) {
		boolean changedNodeValue = true;
		NodeList nl = el.getElementsByTagName(tagName);

		if (nl != null && nl.getLength() > 0) {
			Element ele = (Element) nl.item(0);
			try {
				ele.getFirstChild().setNodeValue(nodeValue);
			} catch (DOMException ex) {
				logger.debug(ex.getMessage());
				changedNodeValue = false;
			}
		}
		return changedNodeValue;
	}
}