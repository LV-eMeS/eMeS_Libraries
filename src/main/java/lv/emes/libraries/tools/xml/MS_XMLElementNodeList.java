package lv.emes.libraries.tools.xml;

import lv.emes.libraries.tools.lists.IBaseListWithItems;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Mocks a node list to provide list of nodes that are compatible with MS_XMLElementNode.
 * <p>Public methods:
 * <p>-item
 * -getLength
 * -getNode
 * <p>Setters and getters:
 *
 * @author eMeS
 * @version 1.1.
 * @see MS_XMLElementNode
 */
public class MS_XMLElementNodeList implements IBaseListWithItems<MS_XMLElementNode> {
    boolean flagForLoopBreaking;
    private static final String MESSAGE_FOR_NODE_NOT_FOUND_EXCEPTION = "Node with index (%d) didn't found in list";

    public static class NodeNotFoundException extends RuntimeException {
        public NodeNotFoundException(int index) {
            super(String.format(MESSAGE_FOR_NODE_NOT_FOUND_EXCEPTION, index));
        }
    }

    private String tag;
    private NodeList actualNodeList;

    /**
     * @return tag of this node list.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Mocks instance of NodeList.
     *
     * @param actualNodeList
     */
    public MS_XMLElementNodeList(NodeList actualNodeList, String tag) {
        this.actualNodeList = actualNodeList;
        this.tag = tag;
    }

    /**
     * Returns node at index <b>index</b>.
     *
     * @param index 0..getLength()-1
     * @return a eMeS XML node.
     * @throws NodeNotFoundException if element in node list not found or not valid.
     */
    private MS_XMLElementNode getNode(int index) throws NodeNotFoundException {
        Node node = actualNodeList.item(index);
        if (node == null)
            throw new NodeNotFoundException(index);
        MS_XMLElementNode res = new MS_XMLElementNode(node);
        return res;
    }

    @Override
    public int count() {
        return actualNodeList.getLength();
    }

    @Override
    public MS_XMLElementNode get(int aIndex) {
        return getNode(aIndex);
    }

    @Override
    public void setBreakDoWithEveryItem(boolean value) {
        flagForLoopBreaking = value;
    }

    @Override
    public boolean getBreakDoWithEveryItem() {
        return flagForLoopBreaking;
    }
}
