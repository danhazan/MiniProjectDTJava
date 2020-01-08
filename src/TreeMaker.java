import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TreeMaker {
    //    private final String DATABSE_FILE_PATH = "resources/mnist_test.csv";
    public static final String DATABSE_FILE_PATH = "resources/mnist_train.csv";

    public static DecisionTree makeTreeVer1(int T) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        int[][] images_matrix = Images.parse_csv(TreeMaker.DATABSE_FILE_PATH);
        System.out.println(images_matrix[10000]);
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        Questions.convertToAnswersMatrixVer1(images_matrix);

        DecisionTree tree = new DecisionTree(T, images_matrix);
        return tree;
    }
}