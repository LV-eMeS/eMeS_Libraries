package lv.emes.libraries.file_system.xml;

import lv.emes.libraries.tools.lists.IBaseListWithItems;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Mocks a node list to provide list of nodes that are compatible with MS_XMLNode.
 * <p>Public methods:
 * <p>-item
 * -getLength
 * -getNode
 * <p>Setters and getters:
 *
 * @author eMeS
 * @version 0.2.
 * @see MS_XMLNode
 */
public class MS_XMLNodeList implements IBaseListWithItems<MS_XMLNode> {
    private static final String MESSAGE_FOR_NODE_NOT_FOUND_EXCEPTION = "Node with index (%d) didn't found in list";
    public static class NodeNotFoundException extends RuntimeException {
        public NodeNotFoundException(int index) {
            super(String.format(MESSAGE_FOR_NODE_NOT_FOUND_EXCEPTION, index));
        }
    }

    private NodeList actualNodeList;

    /**
     * @return tag of this node list.
     */
    public String getTag() {
        return tag;
    }

    private String tag;

    /**
     * Mocks instance of NodeList.
     * @param actualNodeList
     */
    public MS_XMLNodeList(NodeList actualNodeList, String tag) {
        this.actualNodeList = actualNodeList;
        this.tag = tag;
    }

    /**
     * Returns node at index <b>index</b>.
     * @param index 0..getLength()-1
     * @return a eMeS XML node.
     * @throws NodeNotFoundException if element in node list not found or not valid.
     */
    private MS_XMLNode getNode(int index) throws NodeNotFoundException {
        Node node = actualNodeList.item(index);
        if (node == null)
            throw new NodeNotFoundException(index);
        MS_XMLNode res = new MS_XMLNode(node);
        return res;
    }

    @Override
    public int count() {
        return actualNodeList.getLength();
    }

    @Override
    public MS_XMLNode get(int aIndex) {
        return getNode(aIndex);
    }
}
