package solution.longestrecurringpattern;


public class SuffixTree {

    private static int maxHeight;
    private static int substringIndex;
    private TreeNode rootNode;
    private int remainingSuffixes;
    private EndPositionPointer EPP;
    private char input[];
    private char nullChar = '\u0000';

    //active node, active length, active edge global variables
    private static int activeLength;
    private static int activeEdge;
    private static TreeNode activeNode;

    public SuffixTree(char input[]) {
        this.input = input;
    }

    public static void getLongestRecurringPattern(SuffixTree suffixTree, char[] charArray) {

        walkThoughSuffixTree(suffixTree, suffixTree.rootNode, 0, 0, 0);
        int k;
        for (k = 0; k < maxHeight; k++)
            System.out.print(charArray[k + substringIndex]);
    }

    public static void walkThoughSuffixTree(SuffixTree suffixTree, TreeNode n, int lh, int max,
                                            int substringStartInde) {
        maxHeight = max;
        substringIndex = substringStartInde;
        //base case
        if (n == null) {
            return;
        }
        int i = 0;
        if (n.index == -1) {
            for (i = 0; i < 256; i++) {
                if (n.childNodes[i] != null) {
                    walkThoughSuffixTree(suffixTree, n.childNodes[i], lh + edgeLength(suffixTree, n.childNodes[i]),
                            maxHeight, substringIndex);
                }
            }
        } else if (n.index > -1 && (maxHeight < lh - edgeLength(suffixTree, n))) {
            maxHeight = lh - edgeLength(suffixTree, n);
            substringIndex = n.index;
        }
    }

    public static int edgeLength(SuffixTree st, TreeNode n) {
        if (n == st.rootNode)
            return 0;
        return (n.getEndPositionPointer()) - (n.start) + 1;
    }


    public void createSuffixTree() {

        //Create root node of the suffix tree
        rootNode = TreeNode.createNode(1, new EndPositionPointer(0));

        //Set -1 as the default index: for internal nodes index will be -1
        rootNode.index = -1;
        activeNode = rootNode;
        activeLength = 0;
        activeEdge = -1;

        //active = new Active(rootNode);
        EPP = new EndPositionPointer(-1);

        for (int i = 0; i < input.length; i++) {
            addCharacterOfCurrentPhase(i);
        }

        //Finally get indexes of each path
        getIndexOfSuffixUsingDFS(rootNode, 0, input.length);
    }

    private void addCharacterOfCurrentPhase(int i) {

        //When we have a new character we follow Rule 1 and increase EndPositionPointer index by one, so it adds new character to
        //every existing branch
        EPP.pointer++;
        //Also we increase expected suffix count by 1
        remainingSuffixes++;

        TreeNode lastInternalNode = null;

        //remainingSuffixes is expected suffix in this phase, when this reaches to zero we break the loop and go to next
        //phase
        while (remainingSuffixes > 0) {

            //if active length=0 we are going to start next phase from the root
            if (activeLength == 0) {

                //If there is no path already starting from current active point, we create path according to Rule 2
                if (getNodeInChildren(i) == null) {
                    rootNode.childNodes[input[i]] = TreeNode.createNode(i, EPP);
                    remainingSuffixes--;
                }

                //If there is already a path for the incoming character we continue there and break
                // the loop for the next phase: rule number 3
                else {
                    //we set the active edge to index of the actual character
                    activeEdge = getNodeInChildren(i).start;
                    //increase active length according to rule 3
                    activeLength++;
                    //break and go to next phase
                    break;
                }
                //this is the case we have moved current pointer node from active node
            } else {

                //   try {
                //Get the next character in the input text
                char nextCharacter = nextCharacter(i);

                if (nextCharacter == input[i]) {
                    //we check whether it is null for suffix link
                    if (lastInternalNode != null) {
                        lastInternalNode.suffixLink = getNodeInChildren();
                    }
                    //here we want to check whether the next character in the path of same node or it is after a
                    // jump
                    checkNodeJump(i);
                    //we break the loop as there is a path: rule 3
                    break;

                    //if the next character of the input is not equals the next character in the line, we have to
                    //create suffix for it: Rule2
                } else if (nextCharacter == nullChar) {
                    TreeNode node = getNodeInChildren();
                    node.childNodes[input[i]] = TreeNode.createNode(i, EPP);
                    if (lastInternalNode != null) {
                        lastInternalNode.suffixLink = node;
                    }
                    lastInternalNode = node;
                    if (activeNode != rootNode) {
                        activeNode = activeNode.suffixLink;
                    } else {
                        activeEdge = activeEdge + 1;
                        activeLength--;
                    }
                    remainingSuffixes--;

                } else {
                    //in this case we create internal node
                    TreeNode node = getNodeInChildren();
                    int oldStart = node.start;
                    //we move #characters 'active length from current active node"
                    node.start = node.start + activeLength;
                    //create new internal node
                    TreeNode internalNode = TreeNode.createNode(oldStart, new EndPositionPointer
                            (oldStart + activeLength - 1));
                    //create leaf node
                    TreeNode leafNode = TreeNode.createNode(i, this.EPP);

                    internalNode.childNodes[input[internalNode.start + activeLength]] = node;
                    internalNode.childNodes[input[i]] = leafNode;
                    //make internalNode a internal node
                    internalNode.index = -1;
                    activeNode.childNodes[input[internalNode.start]] = internalNode;

                    //this is the case we create another internal node in same phase, so the suffix link of that new
                    //internal node will be last internal node
                    if (lastInternalNode != null) {
                        lastInternalNode.suffixLink = internalNode;
                    }

                    lastInternalNode = internalNode;
                    internalNode.suffixLink = rootNode;

                    //check where should we go now.
                    if (activeNode == rootNode) {
                        activeEdge = activeEdge + 1;
                        activeLength--;

                        //This is the case if the active node is not the root. Here we follow suffixLink
                    } else {
                        activeNode = activeNode.suffixLink;
                    }
                    remainingSuffixes--;
                }
            }
        }
    }

    private void checkNodeJump(int index) {
        TreeNode node = getNodeInChildren();

        if (branchDistance(node) < activeLength) {
            activeNode = node;
            activeLength = activeLength - branchDistance(node);
            activeEdge = node.childNodes[input[index]].start;
        } else {
            activeLength++;
        }
    }

    private char nextCharacter(int i) {
        TreeNode node = getNodeInChildren();
        if (branchDistance(node) >= activeLength) {
            return input[activeNode.childNodes[input[activeEdge]].start + activeLength];
        }
        if (branchDistance(node) + 1 == activeLength) {
            if (node.childNodes[input[i]] != null) {
                return input[i];
            } else return nullChar;
        } else {
            activeNode = node;
            activeLength = activeLength - branchDistance(node) - 1;
            activeEdge = activeEdge + branchDistance(node) + 1;
            return nextCharacter(i);
        }

    }

    private TreeNode getNodeInChildren() {
        return activeNode.childNodes[input[activeEdge]];
    }

    private TreeNode getNodeInChildren(int index) {
        return activeNode.childNodes[input[index]];
    }


    private int branchDistance(TreeNode node) {
        return node.getEndPositionPointer() - node.start;
    }

    private void getIndexOfSuffixUsingDFS(TreeNode root, int val, int sizeOfInput) {
        //base case
        if (root == null) {
            return;
        }

        val += root.getEndPositionPointer() - root.start + 1;
        //check whether root is an internal node
        //if not
        if (root.index != -1) {
            root.index = sizeOfInput - val;
            return;
        }
        //put suffixes recursively
        for (TreeNode node : root.childNodes) {
            getIndexOfSuffixUsingDFS(node, val, sizeOfInput);
        }
    }

}



