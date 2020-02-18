import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TreeMaker {
//    private static final String DATABSE_FILE_PATH = "resources/mnist_test.csv";
    public static final String DATABSE_FILE_PATH = "resources/mnist_train.csv";
//    public static final String DATABSE_FILE_PATH = "resources/kannada_train.csv";

    /**
     * binary check for a number being a power of 2 (or 0)
     */
    private static boolean isPowerOf2(int n) {
        return (n & (n - 1)) == 0;
    }

    public static int[][] splitValidationSet(int indexes, double P) {
        List<Integer> allIndexes = IntStream.rangeClosed(0, indexes - 1).boxed().collect(Collectors.toList());
        Collections.shuffle(allIndexes);

        int validationLength = (int) (P * (double) indexes);
        List<Integer> validation = allIndexes.subList(0, validationLength);
        List<Integer> training = allIndexes.subList(validationLength, indexes);

        Collections.sort(validation);
        Collections.sort(training);

        int[][] sets = new int[2][];
        sets[0] = validation.stream().mapToInt(i->i).toArray();
        sets[1] = training.stream().mapToInt(i->i).toArray();

        return sets;
    }

    public static int[][] fromIndexes(int[][] data, int[] indexes) {
        int[][] result = new int[indexes.length][];

        for(int im=0; im<indexes.length; im++) {
            result[im] = data[indexes[im]];
        }
        return result;
    }


    private static List<DecisionTree> runAlgorithm(DecisionTree buildTree, int L, boolean singleTree) {
        int maxT = (int) Math.pow(2, L);
        List<DecisionTree> trees = new ArrayList<>(singleTree ? 1 : L+1);  // list of potential trees (or just the one)
        List<IGStruct> leafsIGs = new ArrayList<>(maxT);  // a list of all the leafs in the tree with their max IGS
        LeafNode root = (LeafNode) buildTree.getRoot();
        leafsIGs.add(root.getBestIG());  // initialized with the root

        // main algorithm: in each iteration, swap the best leaf with an internal node
        for(int i=1; i<=maxT; i++) {
            IGStruct bestIG = new IGStruct(null, -1, -1);

            // find leaf with best information gain
            for(IGStruct l: leafsIGs){
                bestIG.setIfBigger(l);
            }

            leafsIGs.remove(bestIG);  // this is okay because of the overridden equals() in IGStruct

            // swap with an internal node containing the best question
            LeafNode chosenLeaf = bestIG.getLeaf();
            InternalNode newInternal = chosenLeaf.apply(bestIG.getQuestion());
            LeafNode left = (LeafNode) newInternal.getLeft();
            LeafNode right = (LeafNode) newInternal.getRight();

            // add the new leafs to the list
            leafsIGs.add(left.getBestIG());
            leafsIGs.add(right.getBestIG());


            // copy the tree if it's a proper T value (power of 2)
            if (isPowerOf2(i) && !singleTree) {
                trees.add(new DecisionTree(buildTree, i));
            }
        }

        if (singleTree) {
            trees.add(new DecisionTree(buildTree, maxT));
        }

        return trees;
    }


    public static DecisionTree makeTree(int version, double P, int L, int[][] images_matrix, Map<String, Integer> ver2Config) {
        int[][] answers_matrix;
//        System.out.println(images_matrix.length);
//        System.out.println(images_matrix[0].length);
        if(version == 1){
            answers_matrix = Questions.convertToAnswersMatrixVer1(images_matrix);
        } else {
            answers_matrix = Questions.convertToAnswersMatrixVer2(images_matrix, ver2Config);
        }

//        System.out.println(answers_matrix[0].length);
//        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

        int[][] indexes = splitValidationSet(answers_matrix.length, P);
        int[] validationIndexes = indexes[0];
        int[] trainingIndexes = indexes[1];

        // run the algorithm the first time - to generate a tree for every T value
        DecisionTree validationTree = new DecisionTree(answers_matrix, trainingIndexes);
        List<DecisionTree> trees = runAlgorithm(validationTree, L, false);


        // choose the tree with the best success rate using the validation set on the generated trees
        int[][] validationSet = fromIndexes(answers_matrix, validationIndexes);
        double bestRate = -1;
        DecisionTree bestTree = null;
        for(DecisionTree t: trees) {
            double curRate = TreePredictor.getSuccessRate(t, validationSet);

            if(bestRate < curRate) {
                bestRate = curRate;
                bestTree = t;
            }
        }
        validationSet = null;  // free up some memory

//        System.out.println("chosen:");
//        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

        // running algorithm on both the validation and training set with the best sized tree
        int[] allIndexes = splitValidationSet(answers_matrix.length, 0)[1];
        DecisionTree finalTree = new DecisionTree(answers_matrix, allIndexes);
        finalTree = runAlgorithm(finalTree, L, true).get(0);  // only one tree in the list
//        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));



        int error = (int) ((1.0 - bestRate) * 100);
        System.out.println("num: " + String.valueOf(answers_matrix.length));
        System.out.println("error: " + String.valueOf(error));
        System.out.println("size: " + finalTree.getT());
        return finalTree;
    }
}