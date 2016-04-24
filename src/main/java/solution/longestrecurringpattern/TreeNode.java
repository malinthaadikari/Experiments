package solution.longestrecurringpattern;

/**
 * Created by malintha on 4/10/16.
 */
class TreeNode {

    private TreeNode() {
    }
   //Assumption: Every node will have maximum 256 childNodes nodes
    private static final int MAXIMUM_EDGES = 256;

    TreeNode[] childNodes = new TreeNode[MAXIMUM_EDGES];

    int start;
    EndPositionPointer endPositionPointer;
    int index;

    TreeNode suffixLink;

    public static TreeNode createNode(int start, EndPositionPointer end) {
        TreeNode node = new TreeNode();
        node.start = start;
        node.endPositionPointer = end;
        return node;
    }
    public int getEndPositionPointer(){
        return  endPositionPointer.pointer;
    }

}
