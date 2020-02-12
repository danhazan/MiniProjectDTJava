public class PredictMain {
    private static final String DATABSE_FILE_PATH = "resources/mnist_test.csv";
//    private static final String DATABSE_FILE_PATH = "resources/kannada_test.csv";

    public static void main(String[] args) {
        int L = 8;
        double P = 20/100.0;
        // TODO: is p=0 or p=100 acceptable
        DecisionTree t = TreeMaker.makeTreeVer1(L, P);
        int[][] images_matrix = Images.parse_csv(DATABSE_FILE_PATH);

        TreePredictor.runPrediction(t, images_matrix);
    }
}
