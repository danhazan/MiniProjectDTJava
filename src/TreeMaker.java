import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TreeMaker {
//    public static final String DATABSE_FILE_PATH = "resources/mnist_test.csv";
    public static final String DATABSE_FILE_PATH = "resources/mnist_train.csv";

    public static DecisionTree makeTreeVer1(int T) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        int[][] images_matrix = Images.parse_csv(TreeMaker.DATABSE_FILE_PATH);
        System.out.println(images_matrix.length);
        System.out.println(images_matrix[0].length);
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        Questions.convertToAnswersMatrixVer1(images_matrix);

        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        DecisionTree tree = new DecisionTree(T, images_matrix);


        List<IGStruct> leafsIGs = new ArrayList<>(T);  // is it T??
        LeafNode root = (LeafNode) tree.getRoot();
        leafsIGs.add(root.getBestIG());

        for(int i=0; i<T; i++) {
            IGStruct bestIG = new IGStruct(null, -1, -1);
            for(IGStruct l: leafsIGs){
                bestIG.setIfBigger(l);
            }

            leafsIGs.remove(bestIG);  // this is okay because of the overriden equals() in IGStruct
            LeafNode chosenLeaf = bestIG.getLeaf();
            InternalNode newInternal = chosenLeaf.apply(bestIG.getQuestion());
            LeafNode left = (LeafNode) newInternal.getLeft();
            LeafNode right = (LeafNode) newInternal.getRight();

            //TODO: optimization option: don't calculate the best IG immediately.
            //TODO: It might be better to save the maximum possible IG first (N(L)*log(10))
            //TODO: And do the calculation only if it has a potential to be the chosen leaf

            //TODO: the last calculation can probably be skipped, since the results aren't needed anymore
            leafsIGs.add(left.getBestIG());
            leafsIGs.add(right.getBestIG());
        }


        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        return tree;
    }
}