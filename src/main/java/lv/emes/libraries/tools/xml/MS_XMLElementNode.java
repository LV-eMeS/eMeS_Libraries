package lv.emes.libraries.tools.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles XML node parsing. An instance of element node.
 * If node isn't an element then it acts like node and it has limitations for element actions.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_XMLElementNode {
    public static final String _TEXT_NODE_NAME = "#text";
    private Node actualNode;
    private Element el = null;
    private Boolean nodeIsAnElement;

    /**
     * Tests whether this object is an element.
     *
     * @return true if it is element node, false if it is any other kind of node.
     */
    public Boolean getNodeIsAnElement() {
        return nodeIsAnElement;
    }

    /**
     * Constructs new element. It should be <b>Node.ELEMENT_NODE</b>.
     *
     * @param element an XML element.
     */
    public MS_XMLElementNode(Node element) {
        if (element == null)
            throw new NullPointerException("Cannot create XML element node from null");
        actualNode = element;
        nodeIsAnElement = element.getNodeType() == Node.ELEMENT_NODE;
        if (nodeIsAnElement)
            el = (Element) element;
    }

    /**
     * Extracts node from list's <b>index</b>-th element as new XML node.
     *
     * @param list  list of nodes.
     * @param index 0..count of elements in NodeList.
     * @return i-th node.
     */
    public static MS_XMLElementNode newInstance(NodeList list, int index) {
        return new MS_XMLElementNode(list.item(index));
    }

    /**
     * @return node itself as element or null if node is not an element.
     */
    public Element toElement() {
        return el;
    }

    /**
     * Returns attribute of this node. Example:
     * <p>{student id="593"}
     *
     * @param name id.
     * @return "593".
     * @see org.w3c.dom.Element#getAttribute(String)
     */
    public String getAttribute(String name) {
        return el.getAttribute(name);
    }

    /**
     * @return node tag name.
     * @see Element#getTagName()
     */
    public String getTagName() {
        if (nodeIsAnElement)
            return el.getTagName();
        else
            return actualNode.getNodeName();
    }

    /**
     * @return If it is last element of node, returns text value, otherwise returns empty String.
     */
    public String getValue() {
        return actualNode.getTextContent();
    }

    /**
     * <code>&lt;student id="593"&gt;&lt;firstname&gt;eMeS&lt;/firstname&gt;&lt;/student&gt;</code>
     *
     * @param tag "firstname".
     * @return "eMeS"
     */
    protected Node getChildElement(String tag) {
        if (nodeIsAnElement)
            return el.getElementsByTagName(tag).item(0);
        else
            return null;
    }

    /**
     * Gets all nodes which are under <b>tag</b>.
     *
     * @param tag a valid tag that can be addressed to get list of nodes.
     * @return list of nodes or null if node is not an element.
     */
    public MS_XMLElementNodeList getChildList(String tag) {
        if (nodeIsAnElement)
            return new MS_XMLElementNodeList(el.getElementsByTagName(tag), tag);
        else {
            return null;
        }
    }

    /**
     * Gets all nodes which are under current node.
     *
     * @return list of nodes.
     */
    public MS_XMLElementNodeList getAllChildNodes() {
        return new MS_XMLElementNodeList(actualNode.getChildNodes(), getTagName());
    }

    /**
     * Gets first child element node by its tag. If node doesn't exist, returns null.
     * <p><code>&lt;student id="593"&gt;&lt;firstname&gt;eMeS&lt;/firstname&gt;&lt;/student&gt;</code>
     *
     * @param tag "firstname"
     * @return "eMeS"
     */
    public MS_XMLElementNode getFirstChild(String tag) {
        Node child = getChildElement(tag);
        if (child == null)
            return null;
        return new MS_XMLElementNode(child);
    }

    @Override
    public String toString() {
        return "<" + this.getTagName() + ">" +
                this.getValue() +
                "</" + this.getTagName() + ">";
    }
}
