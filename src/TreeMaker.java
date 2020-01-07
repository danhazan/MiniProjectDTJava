public class TreeMaker {
    //    private final String DATABSE_FILE_PATH = "../resources/mnist_test.csv";
    public static final String DATABSE_FILE_PATH = "../resources/mnist_train.csv";

    public static DecisionTree makeTreeVer1(int T) {
        int[][] images_matrix = Images.parse_csv(TreeMaker.DATABSE_FILE_PATH);
        Questions.convertToAnswersMatrixVer1(images_matrix);

        DecisionTree tree = new DecisionTree(T, images_matrix);
        return tree;
    }
}