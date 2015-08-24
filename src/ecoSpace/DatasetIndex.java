package ecoSpace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ecoSpace.Dataset.Analysis;

public final class DatasetIndex {
	private static Document XMLIndex;
	private static Map<String,Long> species=new HashMap<String,Long>();
	private static BufferedWriter speciesNameWriter;
	/**
	 * Opens the XML dataset index and the species list with respective nubKeys
	 */
	public DatasetIndex() {
		File xml=new File("datasets.xml");
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			XMLIndex = dBuilder.parse(xml);
			XMLIndex.getDocumentElement().normalize();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
// read the complete species list, a file with only 2 columns: species name | nubKey
		File spfile=new File("species.csv");
		String line;
		String[] spl;
		try {
			BufferedReader br=new BufferedReader(new FileReader(spfile));
			while((line=br.readLine())!=null) {
				spl=line.split("\t");
				DatasetIndex.species.put(spl[0], Long.parseLong(spl[1]));
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Empty species file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(species.size()+" species found.");
		
		try {
			speciesNameWriter=new BufferedWriter(new FileWriter(spfile,true));
		} catch (IOException e) {
			System.out.println("Error opening species list file: "+e.getMessage());
			speciesNameWriter=null;
		}
	}
	
	/**
	 * Gets the species species name for the given nubKey. Silently ignores if not found.
	 * @param nubKey The GBIF nubKey.
	 * @return The species canonical name
	 */
	public static String getSpeciesFromNub(Long nubKey) {
		for(Entry<String,Long> e: DatasetIndex.species.entrySet()) {
			if(e.getValue().equals(nubKey)) return e.getKey();
		}
		return null;
	}

	/**
	 * Gets the species names for the given nubKey array
	 * @param nubs
	 * @return Array of species names
	 */
	public static String[] getSpeciesFromNub(Long[] nubs) {
		String[] out=new String[nubs.length];
		for(int i=0;i<nubs.length;i++) {
			for(Entry<String,Long> e: DatasetIndex.species.entrySet()) {
				if(e.getValue().equals(nubs[i])) {
					out[i]=e.getKey();
					break;
				}
			}
		}
		return out;
	}
	/**
	 * Gets the species names for the given nubKey array
	 * @param nubs
	 * @return Array of species names
	 */
	public static String[] getSpeciesFromNub(String[] nubs) {
		String[] out=new String[nubs.length];
		for(int i=0;i<nubs.length;i++) {
			for(Entry<String,Long> e: DatasetIndex.species.entrySet()) {
				if(e.getValue().equals(Long.parseLong(nubs[i]))) {
					out[i]=e.getKey();
					break;
				}
			}
		}
		return out;
	}
	/**
	 * Gets the species nub keys from the given species canonical name array
	 * @param species Array of canonical names. Must be an exact match.
	 * @return Array of nubKeys
	 */
	public static Long[] getNubFromSpecies(String[] species) {
		Long[] out=new Long[species.length];
		for(int i=0;i<species.length;i++) {
			out[i]=DatasetIndex.species.get(species[i]);
		}
		return out;
	}

	public static Long getNubFromSpecies(String species) {
		return DatasetIndex.species.get(species);
	}

	/**
	 * Adds a species to the species list if it doesn't exist already, and gets its nubKey from GBIF.
	 * @param name Species canonical name
	 * @return The nubKey, irrespective of whether it was in the list or not.
	 * @throws IOException
	 */
	public static Long addSpecies(String name) throws IOException {
		Long nubKey=DatasetIndex.species.get(name);
		if(nubKey!=null) return nubKey;
		
		JSONArray res;
		URL gbifSp=new URL("http://api.gbif.org/v1/species?datasetKey=d7dddbf4-2cf0-4f39-9b2a-bb099caae36c&name="+name.replace(" ", "%20"));
		JSONObject jobj=(JSONObject)JSONValue.parse(new InputStreamReader(gbifSp.openStream()));
		res=(JSONArray)jobj.get("results");
		if(res.size()!=1) {		// TODO: when more than one record, what to do? Now it's ignored
			System.out.println("Error: "+name+" with "+res.size()+" records");
			return null;
		} else {
			nubKey=(Long)((JSONObject)(res.get(0))).get("nubKey");
			speciesNameWriter.write(name+"\t"+nubKey+"\n");
			speciesNameWriter.flush();
			System.out.println("Added "+name+" "+nubKey);
			return nubKey;
		}
	}

	/**
	 * Closes resources (the species list Writer)
	 */
	public static void close() {
		try {
			speciesNameWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the nubKey from the canonical species name
	 * @param name Canonical species name (infraspecific ranks not allowed)
	 * @return The nubKey
	 */
	public static Long getSpeciesNub(String name) {
		return DatasetIndex.species.get(name);
	}
	/**
	 * Adds a new analysis to the XML index. Does not check if it is repeated (that check has been done before).
	 * @param analysis A newly created {@link Analysis}
	 */
	public static void addAnalysis(Analysis analysis) {
		Element an=XMLIndex.createElement("analysis");
		an.setAttribute("id",analysis.getAnalysisID());
		an.setAttribute("variables",Arrays.toString(analysis.getVariables().toArray()));
		an.setAttribute("minfreq", analysis.getMinFrequency().toString());
		an.setAttribute("sigmaPercent",analysis.getSigmaPercent().toString());
		an.setAttribute("downWeight",analysis.getDownWeight().toString());
		GetDataset(analysis.getDatasetID()).appendChild(an);
	}
	
	public static void addDataset(String dID,String origin,String description,String url) {
		Node parent=XMLIndex.getElementsByTagName("datasets").item(0);
		Element ds=XMLIndex.createElement("dataset");
		ds.setAttribute("id",dID);
		ds.setAttribute("origin",origin);		
		Element desc=XMLIndex.createElement("description");
		desc.appendChild(XMLIndex.createTextNode(description));
		ds.appendChild(desc);
		if(url!=null) {
			Element urlel=XMLIndex.createElement("url");
			urlel.appendChild(XMLIndex.createTextNode(url));
			ds.appendChild(urlel);
		}
		parent.appendChild(ds);
	}
	
	/**
	 * Gets the XML element associated with the given query service
	 * @param name
	 * @return
	 * @throws IOException 
	 */
	public static Element getQueryService(String name) throws IOException {
		NodeList ds=XMLIndex.getElementsByTagName("queryservice");
		for(int i=0;i<ds.getLength();i++) {
			if(((Element)(ds.item(i))).getElementsByTagName("name").item(0).getTextContent().toLowerCase().equals(name.toLowerCase())) return (Element)ds.item(i);
		}
		throw new IOException("Query service "+name+" not found.");
	}
	
	public static NodeList getQueryServices() {
		return XMLIndex.getElementsByTagName("queryservice");
	}
	
	/**
	 * Searches for an existing analysis with the given parameters.
	 * @param dID
	 * @param variables
	 * @param min_freq
	 * @param sigmaPercent
	 * @param downweight
	 * @return Null if not found, otherwise the analysis ID.
	 */
	public static String findAnalysis(String dID,Integer[] variables,Integer min_freq,Float sigmaPercent,Boolean downweight) {
		int i;
		Element ds=GetDataset(dID),an;
		if(ds!=null) {
			NodeList analyses=ds.getElementsByTagName("analysis");
			for(i=0;i<analyses.getLength();i++) {
				an=(Element)analyses.item(i);
				if(an.getAttribute("variables").equals(Arrays.toString(variables)) &&
						an.getAttribute("minfreq").equals(min_freq.toString()) &&
						an.getAttribute("sigmaPercent").equals(sigmaPercent.toString()) &&
						an.getAttribute("downWeight").equals(downweight.toString())
					) return an.getAttribute("id");
			}			
		}
		return(null);
	}
	
	public static Element getVariable(String name) throws IOException {
		NodeList nl=XMLIndex.getElementsByTagName("variable");
		Element tmp;
		for(int i=0;i<nl.getLength();i++) {
			tmp=(Element)nl.item(i);
			if(tmp.getAttribute("file").equals(name)) return tmp;
		}
		throw new IOException("Variable not found");
	}
	
	public static Node GetRoot() {
		return(XMLIndex.getElementsByTagName("datasets").item(0));
	}
	public static NodeList GetDatasets() {
		return(XMLIndex.getElementsByTagName("dataset"));	
	}
	public static NodeList GetVariables() {
		return(XMLIndex.getElementsByTagName("variable"));
	}
	
	public static Element GetDataset(String dID) {
		int i;
		NodeList ds=XMLIndex.getElementsByTagName("dataset");
		for(i=0;i<ds.getLength();i++) {
			if(((Element)(ds.item(i))).getAttribute("id").equals(dID)) return((Element)ds.item(i));
		}
		return(null);
	}

	public static Element getAnalysis(String dID,String aID) {
		Element ds=GetDataset(dID);
		if(ds==null) return(null);
		
		int i;
		NodeList an=ds.getElementsByTagName("analysis");
		for(i=0;i<an.getLength();i++) {
			if(((Element)(an.item(i))).getAttribute("id").equals(aID)) return((Element)an.item(i));
		}
		return(null);
	}
	
	public static void Empty() {
		NodeList nl=GetDatasets();
		List<Node> toremove=new ArrayList<Node>();
		for(int i=0;i<nl.getLength();i++) toremove.add(nl.item(i));
		for(Node n:toremove) GetRoot().removeChild(n);
	}
	
	public static void removeDataset(String dID) {
		Element ds=GetDataset(dID);
		if(ds==null) return; else GetRoot().removeChild(ds);
	}

	public static void removeAnalysis(String dID,String aID) {
		Element an=getAnalysis(dID,aID);
		if(an==null) return; else GetDataset(dID).removeChild(an);
	}
	
// authorization keys are used in an individual basis to create new datasets
	public static boolean checkAuthorizationKey(String keytocheck) {
		NodeList key=XMLIndex.getElementsByTagName("authkey");
		for(int i=0;i<key.getLength();i++) {
			if(((Element)key.item(i)).getAttribute("key").equals(keytocheck)) {
				Node parent=XMLIndex.getElementsByTagName("authkeys").item(0);
				parent.removeChild(key.item(i));
				WriteXML();
				return(true);
			}
		}
		return(false);
	}
	
	public static String newAuthorizationKey() {
		NodeList tmp=XMLIndex.getElementsByTagName("authkeys");
		Element authkeys,newkey;
		if(tmp.getLength()==0) {
			authkeys=XMLIndex.createElement("authkeys");
			XMLIndex.getFirstChild().appendChild(authkeys);
		} else authkeys=(Element)tmp.item(0);
		String key=UUID.randomUUID().toString();
		newkey=XMLIndex.createElement("authkey");
		newkey.setAttribute("key", key);
		authkeys.appendChild(newkey);
		WriteXML();
		return(key);
	}
	
	public static void WriteXML() {
// output XML file		
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer= transformerFactory.newTransformer();
			DOMSource source = new DOMSource(XMLIndex);
			StreamResult result = new StreamResult(new File("datasets.xml"));
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	public static String TranslateDatasetState(DATASETSTATE dsst) {
		if(dsst==null) return("Undefined state");
		
		switch(dsst) {
		case IDLE: return("Idle");
		case EMPTY: return("Empty dataset");
		case REQUESTING_FILE: return("Requesting dataset");
		case WAITING_FILE: return("Waiting for file to be ready for download");
		case PROCESSING_FILE: return("Processing occurrence file");
		case READING_VARIABLES: return("Reading variables from coordinates");
		case ERROR: return("Some error occurred while processing");
		default: return("Undefined");
		}

	}
}