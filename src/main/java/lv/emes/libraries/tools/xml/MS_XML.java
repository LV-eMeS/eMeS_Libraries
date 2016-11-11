package lv.emes.libraries.tools.xml;

import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.file_system.MS_TextFile;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles existing XML file parsing and editing.
 *
 * @author eMeS
 * @version 0.4.
 * @see MS_XMLElementNode
 */
//https://www.tutorialspoint.com/java_xml/java_dom_modify_document.htm
public class MS_XML {
    private static final String MESSAGE_FOR_NODES_NOT_FOUND_EXCEPTION = "Cannot find nodes by tag name \"%s\" in this document";
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public static class NodesNotFoundException extends RuntimeException {
        public NodesNotFoundException(String tag) {
            super(String.format(MESSAGE_FOR_NODES_NOT_FOUND_EXCEPTION, tag));
        }
    }

    private Document doc;
    /**
     * Created either from local file or from input stream.
     */
    private boolean createdFromLocalFile;
    private String xmlFilename = "";
    private byte[] xmlInputAsByteArray = null;

    @Override
    public String toString() {
        if (createdFromLocalFile)
            return MS_TextFile.getFileTextAsString(xmlFilename, "\n");
        else
            return MS_TextFile.getStreamTextAsString(MS_BinaryTools.bytesToIntput(xmlInputAsByteArray));
    }

    public JSONObject toJSON() {
        return XML.toJSONObject(toString());
    }

    /**
     * Links MS_XML to a file with name <b>aFileName</b>.
     *
     * @param aFileName name of existing XML file.
     * @throws IOException if an I/O Exception occurs while reading file.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested.
     * @throws SAXException if any parse errors occur.
     */
    public MS_XML(String aFileName) throws IOException, ParserConfigurationException, SAXException {
        createdFromLocalFile = true;
        xmlFilename = aFileName;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        dBuilder = factory.newDocumentBuilder();
        doc = dBuilder.parse(new File(aFileName));
    }

    /**
     * Links MS_XML to a stream of file. Stream can also be resource; in that case it is recommended to load stream in such way:<p>
     *     <code>MS_XML xml = new MS_XML(MS_FileSystemTools.getResourceInputStream("resource_file.xml"));</code>
     *
     * @param stream a stream of existing XML file to link with.
     * @throws IOException if an I/O Exception occurs while reading stream.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested.
     * @throws SAXException if any parse errors occur.
     * @throws NullPointerException if given stream is null.
     */
    public MS_XML(InputStream stream) throws IOException, ParserConfigurationException, SAXException {
        //clone input stream for later use
        if (stream==null)
            throw new NullPointerException("Stream is null");
        xmlInputAsByteArray = MS_BinaryTools.inputToBytes(stream);
        stream = MS_BinaryTools.bytesToIntput(xmlInputAsByteArray);
        createdFromLocalFile = false;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        dBuilder = factory.newDocumentBuilder();
        doc = dBuilder.parse(stream);
    }

    public Document getDocument() {
        return doc;
    }

    /**
     * Simply returns very first element of XML file.
     *
     * @return root element of XML name.
     */
    public String getRootElementName() {
        return doc.getDocumentElement().getNodeName();
    }

    /**
     * Simply returns root node of XML file.
     *
     * @return root element of XML node.
     */
    public MS_XMLElementNode getRootNode() {
        return this.getNodesByTagName(getRootElementName()).get(0);
    }

    /**
     * Returns list of nodes. Searching by XML tag <b>aTag</b>.
     *
     * @param aTag a valid XML tag of document.
     * @return list of nodes (element collections).
     * @throws NodesNotFoundException if no node with such tag found in this document.
     */
    public MS_XMLElementNodeList getNodesByTagName(String aTag) {
        NodeList res = doc.getElementsByTagName(aTag);
        if (res.getLength() == 0)
            throw new NodesNotFoundException(aTag);
        return new MS_XMLElementNodeList(res, aTag);
    }
}