package is.postur;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Postnumeraskra {

	public static final String DEFAULT_URL="http://www.postur.is/gogn/Gotuskra/postnumer.xml";
	
	Document Postnumeraskra;
	List postnumer;
	
	public static Postnumeraskra cached;
	
	public static Postnumeraskra getCached(){
		if(cached==null){
			try {
				cached=new Postnumeraskra();
				cached.parseData();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cached;
	}
	
	//static String DEFAULT_URL="file:/tmp/Postnumeraskra.xml";
	
	public Postnumeraskra() throws SAXException, IOException, ParserConfigurationException{
		this(createDocument(DEFAULT_URL));
	}
	
	public Postnumeraskra(Document document) {
		this.Postnumeraskra=document;
		//parseData();
	}

	
	protected void parseData(){
		Pattern postOfficeBoxesPattern = Pattern.compile("pósthólf");
		NodeList gotulisti = Postnumeraskra.getElementsByTagName("Postnumer");
		for (int i = 0; i < gotulisti.getLength(); i++) {
			Node nPostnumer = gotulisti.item(i);
			Postnumer postnumer = getPostnumer(nPostnumer, postOfficeBoxesPattern);
			if (postnumer != null) {
				getPostnumer().add(postnumer);
			}
		}
	}
	
	private Postnumer getPostnumer(Node nPostnumer, Pattern postOfficePattern) {
		NodeList PostnumerNodes = nPostnumer.getChildNodes();
		Postnumer postnumer = new Postnumer();
		for (int j = 0; j < PostnumerNodes.getLength(); j++) {
			Node PostnumerNode = PostnumerNodes.item(j);
			if(PostnumerNode.getNodeName().equals("Numer")){
				String id = getNodeChildValueAsString(PostnumerNode);
				try{
					postnumer.setNumerInt(Integer.parseInt(id));
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			else if(PostnumerNode.getNodeName().equals("Heiti")){
				String heiti = getNodeChildValueAsString(PostnumerNode);
				postnumer.setName(heiti);
			}
			else if(PostnumerNode.getNodeName().equals("Heimili")){
				String heimili = getNodeChildValueAsString(PostnumerNode);
				if (heimili != null) {
					Matcher matcher = postOfficePattern.matcher(heimili);
					if (heimili != null && matcher.find()) {
						// do not add post office boxes to the list
						return null;
					}
				}
				postnumer.setHeimili(heimili);
			}
		}
		return postnumer;
	}



	private String getNodeChildValueAsString(Node PostnumerNode) {
		Node firstChild = PostnumerNode.getFirstChild();
		if (firstChild == null) {
			// for this zip code there is no post office
			return null;
		}
		String str = firstChild.getNodeValue();
		return str;
	}

	static Document createDocument(String url) throws SAXException, IOException, ParserConfigurationException{
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			   DocumentBuilder builder = factory.newDocumentBuilder();
			   
			   document = builder.parse(url);

			} catch (SAXParseException spe) {
			
			}
		return document;
	}
	
	
	public static void main(String args[]) throws Exception{
		
		Postnumeraskra skra = new Postnumeraskra();
		skra.parseData();
		
		List gotur = skra.getPostnumer();
		for (Iterator iter = gotur.iterator(); iter.hasNext();) {
			Postnumer Postnumer = (Postnumer) iter.next();
			System.out.println("Postnumer: "+Postnumer.getName());
			
		}
	}

	public Postnumer getPostnumerByNumer(String numer){
		List listi = getPostnumer();
		for (Iterator iter = listi.iterator(); iter.hasNext();) {
			Postnumer num = (Postnumer) iter.next();
			if(num.getNumerString().equals(numer)){
				return num;
			}
		}
		return null;
	}
	
	public List getPostnumer() {
		if(postnumer==null){
			postnumer=new ArrayList();
		}
		return postnumer;
	}

	public void getPostnumer(List postnumer) {
		this.postnumer = postnumer;
	}
	
}
