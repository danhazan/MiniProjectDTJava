public class DecisionTree {
    private int T;
    private int[][] matrix;
    private Node root;

    DecisionTree(int t, int[][] matrix) {
        this.T = t;
        this.matrix = matrix;
        this.root = new LeafNode(0);

    }

    abstract class Node {
        
    }

    class InternalNode extends Node {
        private int question;
        private Node left;
        private Node right;

        InternalNode() {
            left = null;
            right = null;

        }
    }

    class LeafNode extends Node {
        private int digit;

        LeafNode(int digit) {
            this.digit = digit;
        }
    }
}
   
