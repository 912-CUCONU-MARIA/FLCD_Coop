package parser;

public class TreeNode {
    int index;
    String info;
    TreeNode parent;
    TreeNode leftSibling;

    TreeNode(int index, String info, TreeNode parent, TreeNode leftSibling) {
        this.index = index;
        this.info = info;
        this.parent = parent;
        this.leftSibling = leftSibling;
    }
}