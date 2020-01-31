import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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


        List<LeafNode> leafs = new ArrayList<>(T);  // is it T??
        leafs.add((LeafNode) tree.getRoot());

        for(int i=0; i<T; i++) {
            IGStruct bestIG = new IGStruct(-1, -1);
            for(LeafNode l: leafs){
                bestIG.setIfBigger(l);
            }
            LeafNode chosenLeaf = bestIG.getLeaf();
            leafs.remove(chosenLeaf);
            InternalNode newInternal = chosenLeaf.apply(bestIG.getQuestion());
            leafs.add((LeafNode) newInternal.getLeft());
            leafs.add((LeafNode) newInternal.getRight());
        }


        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        return tree;
    }
}