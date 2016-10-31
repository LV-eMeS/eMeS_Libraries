package lv.emes.libraries.file_system.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertTrue;

/**
 * Handles XML node parsing. An instance of element node.
 * @author eMeS
 * @version 0.3.
 */
public class MS_XMLNode {
//	private static final String MESSAGE_FOR_NODE_NOT_FOUND_EXCEPTION = "Cannot find node by tag name \"%s\" in current node";
//	public static class NodeNotFoundException extends RuntimeException {
//		public NodeNotFoundException(String tag) {
//			super(String.format(MESSAGE_FOR_NODE_NOT_FOUND_EXCEPTION, tag));
//		}
//	}

	private Element el;
	
	/**
	 * Constructs new element. It should be <b>Node.ELEMENT_NODE</b>.
	 * @param element an XML element.
	 */
	public MS_XMLNode(Node element) {
		assertTrue(element != null);
		assertTrue(element.getNodeType() == Node.ELEMENT_NODE);
		el = (Element) element;
	}
	
	/**
	 * Extracts node from list's <b>index</b>-th element as new XML node.
	 * @param list
	 * @param index
	 * @return i-th node.
	 */
	public static MS_XMLNode newInstance(NodeList list, int index) {
		return new MS_XMLNode(list.item(index));
	}

	/**
	 * @return node itself as element.
	 */
	public Element toElement() {
		return el;
	}

	/**
	 * Returns attribute of this node. Example:
	 * <p>{student id="593"}
	 * @param name id
	 * @return "593"
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
		return el.getTagName();
	}
	
	/**
	 * @return If it is last element of node, returns text value, otherwise returns empty String.
	 */
	public String getValue() {
		if (el != null)
			return el.getTextContent();	
		else
			return "";
	}

	/**
	 *{student id="593"}{firstname}eMeS{/firstname}{/student}
	 * @param tag "firstname"
	 * @return "eMeS"
	 */
	public Node getChildElement(String tag) {
		return el.getElementsByTagName(tag).item(0);
	}
	
	/**
	 * Gets all nodes which are under <b>tag</b>.
	 * @param tag a valid tag that can be addressed to get list of nodes.
	 * @return list of nodes.
	 */
	public MS_XMLNodeList getChildNodes(String tag) {
		return new MS_XMLNodeList(el.getElementsByTagName(tag), tag);
	}

	/**
	 * Gets all nodes which are under current node.
	 * @return list of nodes.
	 */
	public MS_XMLNodeList getChildNodes() {
		return new MS_XMLNodeList(el.getChildNodes(), "");
	}
	
	/**
	 *<student id="593"><firstname>eMeS</firstname></student>
	 * @param tag "firstname"
	 * @return "eMeS"
	 */
	public MS_XMLNode getChildNode(String tag) {
		Node child = getChildElement(tag);
		if (child == null)
			return null;
		return new MS_XMLNode(child);
	}

	@Override
	public String toString() {
		return "MS_XMLNode{" +
				this.getTagName() + " = " + this.getValue() +
				'}';
	}
}
