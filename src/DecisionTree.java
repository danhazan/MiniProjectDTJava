import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class DecisionTree {
    private int T;
    int[][] matrix;
    private Node root;

    DecisionTree(int t, int[][] matrix) {
        this.T = t;
        this.matrix = matrix;

        int[] initial_indexes = IntStream.rangeClosed(0, matrix.length - 1).toArray();
        this.root = new LeafNode(this, null, 0, initial_indexes);
    }

    public void setRoot(Node newRoot) {
        root = newRoot;
    }

    public Node getRoot() {
        return root;
    }
}

abstract class Node {
    protected DecisionTree tree;
    protected InternalNode parent;
    protected int[] indexes;


    protected Node(DecisionTree tree, InternalNode parent, int[] indexes) {
        this.tree = tree;
        this.parent = parent;
        this.indexes = indexes;
    }

    public double[][] getSplitNiArrays(int question) {
        double[] leftNiArray = new double[10];
        double[] rightNiArray = new double[10];

        for(int im=0; im<indexes.length; im++){
            if (tree.matrix[im][question] == 0) {
                leftNiArray[tree.matrix[im][0]]++;  // left is false
            } else {
                rightNiArray[tree.matrix[im][0]]++;  // right is true
            }
        }

        return new double[][] {leftNiArray, rightNiArray};
    }

}

class InternalNode extends Node {
    private int question;
    private Node left;
    private Node right;

    protected InternalNode(DecisionTree tree, InternalNode parent, int[] indexes) {
        super(tree, parent, indexes);
        left = null;
        right = null;
    }

    public void swapChild(Node oldChild, Node newChild){
        if(this.left == oldChild){
            left = newChild;
        } else if (this.right == oldChild) {
            right = newChild;
        } else {
            throw new NoSuchElementException("Cannot swap child, since oldChild isn't a child of this node");
        }
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    private int[] getMostCommonDigits(int question, double[] leftNiArray, double[] rightNiArray) {

        int leftDigit = -1;
        double leftLargest = -1;
        int rightDigit = -1;
        double rightLargest = -1;

        for(int i=0; i<10; i++) {
            if (leftLargest < leftNiArray[i]) {
                leftDigit = i;
                leftLargest = leftNiArray[i];
            }
            if (rightLargest < rightNiArray[i]) {
                rightDigit = i;
                rightLargest = rightNiArray[i];
            }
        }
        return new int[] {leftDigit, rightDigit};
    }

    public int[][] getSplitIndexes(int question, int leftNL, int rightNL) {
        int[] leftIndexes = new int[leftNL];
        int leftCounter = 0;
        int[] rightIndexes = new int[rightNL];
        int rightCounter = 0;

        for(int im=0; im<indexes.length; im++){
            if (tree.matrix[im][question] == 0) {
                leftIndexes[leftCounter++] = im;  // left is false
            } else {
                rightIndexes[rightCounter++] = im;  // right is true
            }
        }

        return new int[][] {leftIndexes, rightIndexes};
    }

    public void apply(int question) {
        double[][] NiArrays = getSplitNiArrays(question);
        double[] leftNiArray = NiArrays[0];
        double[] rightNiArray = NiArrays[1];
        int leftNL = (int) DoubleStream.of(leftNiArray).sum();
        int rightNL = (int) DoubleStream.of(rightNiArray).sum();

        int[] commonDigits = getMostCommonDigits(question, leftNiArray, rightNiArray);
        int[][] indexes = getSplitIndexes(question, leftNL, rightNL);

        left = new LeafNode(tree, this, commonDigits[0], indexes[0]);
        right = new LeafNode(tree, this, commonDigits[1], indexes[1]);
    }

}

class LeafNode extends Node {
    private int digit;

    protected LeafNode(DecisionTree tree, InternalNode parent, int digit, int[] indexes) {
        super(tree, parent, indexes);
        this.digit = digit;
    }

    private double log2(double n)
    {
        return Math.log(n) / Math.log(2);
    }


    /**
     * Calculates current leaf's entropy - H(L) from the instructions
     * @return - the entropy
     */
    private double HL() {
        //TODO: cache this!!!
        double[] NiArray = new double[10];
        for(int im=0; im<indexes.length; im++){
            NiArray[tree.matrix[im][0]]++;
        }

        double NL = DoubleStream.of(NiArray).sum();
        return HL(NiArray, NL);
    }

    /**
     * Calculates custom leaf entropy - H(L) from the instructions
     * @param NiArray - an array of size 10, containing the number of instances each label got
     * @param NL - the sum of those instances
     * @return - the entropy
     */
    private double HL(double[] NiArray, double NL) {
        double entropy = 0;

        if(NL == 0) {
            return entropy;
        }

        for(int i=0; i<10; i++) {
            if (NiArray[i] != 0) {
                entropy += (NiArray[i] / NL) * log2(NL / NiArray[i]);
            }
        }

        return entropy;
    }


    /**
     * Calculates custom question's entropy - H(X) from the instructions
     * @param q - the question id
     * @return the entropy
     */
    private double HX(int q) {
        double[][] NiArrays = getSplitNiArrays(q);
        double[] leftNiArray = NiArrays[0];
        double[] rightNiArray = NiArrays[1];

        double leftNL = DoubleStream.of(leftNiArray).sum();
        double rightNL = DoubleStream.of(rightNiArray).sum();
        int NL = indexes.length;  // TODO: check before if it's zero

//        System.out.println(q);
//        System.out.println(Arrays.toString(leftNiArray));
//        System.out.println(Arrays.toString(rightNiArray));
//        System.out.println(leftNL + rightNL);
//        System.out.println();

        return (leftNL / NL) * HL(leftNiArray, leftNL) + (rightNL / NL) * HL(rightNiArray, rightNL);
    }

    public IGStruct getBestIG(){
        IGStruct bestIG = new IGStruct(-1, -1);

        // iterate all questions
        for(int q=1; q<tree.matrix[0].length; q++){  // start from 1 to skip labels column
            double currentIG = HL() - HX(q);

            bestIG.setIfBigger(currentIG, q);
        }

        return bestIG;
    }

    public InternalNode apply(int question){
        InternalNode newNode = new InternalNode(tree, parent, indexes);
        if (parent == null) {  // means this is the initial root leaf
            tree.setRoot(newNode);
        } else {
            parent.swapChild(this, newNode);
        }
        newNode.apply(question);
        return newNode;
    }
}


class IGStruct {
    private double IG;
    private int question;
    private LeafNode leaf;

    IGStruct(double ig, int question, LeafNode leaf) {
        set(ig, question, leaf);
    }

    IGStruct(double ig, int question) {
        set(ig, question, null);
    }

    public void set(double ig, int q, LeafNode l) {
        IG = ig;
        question = q;
        leaf = l;
    }

    public void setIfBigger(IGStruct other) {
        if (this.IG < other.getIG()) {
            this.set(other.IG, other.question, other.leaf);
        }
    }

    public void setIfBigger(LeafNode leaf) {
        IGStruct igs = leaf.getBestIG().normalized(leaf.indexes.length);
        setIfBigger(new IGStruct(igs.IG, igs.question, leaf));
    }


    public void setIfBigger(double ig, int question) {
        setIfBigger(new IGStruct(ig, question));
    }

    public void normalize(double NL){
        IG *= NL;
    }

    public IGStruct normalized(double NL) {
        normalize(NL);
        return this;
    }

    public double getIG() {
        return IG;
    }

    public int getQuestion() {
        return question;
    }
     public LeafNode getLeaf(){
         return leaf;
     }
}
