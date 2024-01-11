package parser;

import parser.Grammar;
import parser.GrammarException;
import parser.TreeNode;
import parser.Pair;

import java.util.*;

public class ParsingTree {
    Grammar grammar;
    TreeNode root;
    int currentIndex;
    List<TreeNode> nodes;

    public ParsingTree(Grammar grammar) {
        this.grammar = grammar;
        this.currentIndex = 0;
        this.nodes = new ArrayList<>();
    }

    TreeNode addNode(String info, TreeNode parent, TreeNode leftSibling) {
        currentIndex++;
        TreeNode newNode = new TreeNode(currentIndex, info, parent, leftSibling);
        nodes.add(newNode);
        return newNode;
    }

    void buildTree(Stack<Pair<String, Integer>> workingStack) throws GrammarException {
        Stack<Pair<String, Integer>> stack = reverseStack(workingStack);

        root = addNode(stack.peek().getFirst(), null, null);

        dfsBuild(stack, root);
    }

    void dfsBuild(Stack<Pair<String, Integer>> stack, TreeNode currentNode) throws GrammarException {
        while (!stack.isEmpty() && !grammar.getNonterminals().contains(stack.peek().getFirst())) {
            stack.pop(); //skip terminals
        }

        if (stack.isEmpty() || !grammar.getNonterminals().contains(currentNode.info)) {
            return;
        }

        Pair<String, Integer> p = stack.pop();
        String nonTerminal = p.getFirst();
        int productionNumber = p.getSecond() - 1;
        List<String> production = grammar.getProductionsForNonterminal(nonTerminal).get(productionNumber);

        TreeNode lastSibling = null;
        for (String symbol : production) {
            TreeNode newNode = addNode(symbol, currentNode, lastSibling);
            lastSibling = newNode;

            //dfs only if nonterminal
            if (grammar.getNonterminals().contains(symbol)) {
                dfsBuild(stack, newNode);
            }
        }
    }

    void printTreeAsItWasBuilt() {
        System.out.println("Index | Info | Parent | Left sibling");
        for (TreeNode node : nodes) {
            int parentIndex = node.parent != null ? node.parent.index : 0;
            int leftSiblingIndex = node.leftSibling != null ? node.leftSibling.index : 0;
            System.out.println(node.index + "\t  |\t " + node.info + "   |\t " + parentIndex + "  \t|\t " + leftSiblingIndex);
        }
    }

    void printTreeOnLevels() {
        if (nodes.isEmpty()) {
            return;
        }

        System.out.println("Index | Info | Parent | Left sibling");

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(nodes.get(0));

        while (!queue.isEmpty()) {
            TreeNode currentNode = queue.remove();

            int parentIndex = currentNode.parent != null ? currentNode.parent.index : 0;
            int leftSiblingIndex = currentNode.leftSibling != null ? currentNode.leftSibling.index : 0;
            System.out.println(currentNode.index + "\t  |\t " + currentNode.info + "   |\t " + parentIndex + "  \t|\t " + leftSiblingIndex);

            //add children of current node to queue, bfs like
            for (TreeNode node : nodes) {
                if (node.parent == currentNode) {
                    queue.add(node);
                }
            }
        }
    }


    Stack<Pair<String, Integer>> reverseStack(Stack<Pair<String, Integer>> stack) {
        Stack<Pair<String, Integer>> auxStack = new Stack<>();
        while (!stack.isEmpty()) {
            auxStack.push(stack.pop());
        }
        return auxStack;
    }

    void printTreeVisually() {
        if (nodes.isEmpty()) {
            return;
        }

        Map<Integer, List<TreeNode>> levelMap = new HashMap<>();
        buildLevelMap(nodes.get(0), 0, levelMap);

        for (int level : levelMap.keySet()) {
            System.out.println("Level " + level + ":");
            for (TreeNode node : levelMap.get(level)) {
                int depth = getDepth(node);
                printIndentation(depth);
                System.out.println("|-- " + node.info + " (Index: " + node.index + ")");
            }
        }
    }

    private void buildLevelMap(TreeNode node, int level, Map<Integer, List<TreeNode>> levelMap) {
        if (node == null) {
            return;
        }

        levelMap.putIfAbsent(level, new ArrayList<>());
        levelMap.get(level).add(node);

        for (TreeNode child : nodes) {
            if (child.parent == node) {
                buildLevelMap(child, level + 1, levelMap);
            }
        }
    }

    private int getDepth(TreeNode node) {
        int depth = 0;
        while (node.parent != null) {
            depth++;
            node = node.parent;
        }
        return depth;
    }

    private void printIndentation(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("   ");
        }
    }


}