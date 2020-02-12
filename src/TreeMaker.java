import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TreeMaker {
    private static final String DATABSE_FILE_PATH = "resources/mnist_test.csv";
//    public static final String DATABSE_FILE_PATH = "resources/mnist_train.csv";
//    public static final String DATABSE_FILE_PATH = "resources/kannada_train.csv";

    /**
     * binary check for a number being a power of 2 (or 0)
     */
    private static boolean isPowerOf2(int n) {
        return (n & (n - 1)) == 0;
    }

    private static int[][] splitValidationSet(int indexes, double P) {
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

    private static int[][] fromIndexes(int[][] data, int[] indexes) {
        int[][] result = new int[indexes.length][];

        for(int im=0; im<indexes.length; im++) {
            result[im] = data[indexes[im]];
        }
        return result;
    }


    public static DecisionTree makeTreeVer1(int L, double P) {
        // parse images and convert to answers matrix
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        int[][] images_matrix = Images.parse_csv(TreeMaker.DATABSE_FILE_PATH);
        System.out.println(images_matrix.length);
        System.out.println(images_matrix[0].length);

        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

        int[][] answers_matrix = Questions.convertToAnswersMatrixVer2(images_matrix);
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

        int[][] sets = splitValidationSet(answers_matrix.length, P);
        int[] validationIndexes = sets[0];
        int[] trainingIndexes = sets[1];

        int maxT = (int) Math.pow(2, L);
        List<DecisionTree> trees = new ArrayList<>(L+1);


        // create empty tree and initialize root, adding it as initial leaf to the leafs array
        DecisionTree tree = new DecisionTree(answers_matrix, trainingIndexes);
        List<IGStruct> leafsIGs = new ArrayList<>(maxT);  // is it T??

        LeafNode root = (LeafNode) tree.getRoot();
        leafsIGs.add(root.getBestIG());

        // main algorithm: for T times, swap the best leaf with an internal node
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

            //TODO: optimization option: don't calculate the best IG immediately.
            //TODO: It might be better to save the maximum possible IG first (N(L)*log(10))
            //TODO: And do the calculation only if it has a potential to be the chosen leaf

            //TODO: the last calculation can probably be skipped, since the results aren't needed anymore

            // add the new leafs to the leaf array
            leafsIGs.add(left.getBestIG());
            leafsIGs.add(right.getBestIG());


            // copy the tree if it's a proper T value (power of 2)
            if (isPowerOf2(i)) {
                DecisionTree copiedTree = new DecisionTree(tree);
                copiedTree.setT(i);
                trees.add(copiedTree);
            }
        }


        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        int[][] validationSet = fromIndexes(answers_matrix, validationIndexes);
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

        double bestRate = -1;
        DecisionTree bestTree = null;
        for(DecisionTree t: trees) {
            double curRate = TreePredictor.getSuccessRate(t, validationSet);

            if(bestRate < curRate) {
                bestRate = curRate;
                bestTree = t;
            }
        }

        System.out.println("chosen:");
        System.out.println(bestTree.getT());
        System.out.println(bestRate);
        return bestTree;
    }
}