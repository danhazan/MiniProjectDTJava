import java.util.*;

public class PredictMain {
    private static final String DATABSE_FILE_PATH = "resources/mnist_test.csv";
//    private static final String DATABSE_FILE_PATH = "resources/kannada_test.csv";


    public static void main(String[] args) {
        configTester();
    }

    public static void main2(String[] args) {
        int L = 8;
        double P = 20/100.0;
        // TODO: is p=0 or p=100 acceptable

        Map<String, Integer> config = new HashMap<String, Integer>() {{
            put(Questions.ROWS_NUM, 7);
            put(Questions.ROWS_LEVELS, 10);
            put(Questions.COLS_NUM, 7);
            put(Questions.COLS_LEVELS, 10);
            put(Questions.BLOCKS_NUM_SQR, 7);
            put(Questions.BLOCKS_LEVELS, 10);
        }};

        DecisionTree t = TreeMaker.makeTree(L, P, config);
        int[][] images_matrix = Images.parse_csv(DATABSE_FILE_PATH);
//        int[][] answers_matrix = Questions.convertToAnswersMatrixVer1(images_matrix);
        int[][] answers_matrix = Questions.convertToAnswersMatrixVer2(images_matrix, config);

        TreePredictor.runPrediction(t, answers_matrix);
    }


    private static Map<String, Integer> makeConfig(int ru, int rl, int cu, int cl, int bu, int bl) {
        Map<String, Integer> config = new HashMap<String, Integer>() {{
            put(Questions.ROWS_NUM, ru);
            put(Questions.ROWS_LEVELS, rl);
            put(Questions.COLS_NUM, cu);
            put(Questions.COLS_LEVELS, cl);
            put(Questions.BLOCKS_NUM_SQR, bu);
            put(Questions.BLOCKS_LEVELS, bl);
        }};

        return config;
    }


    public static void configTester() {
        int L = 8;
        double P = 20/100.0;


        List<Integer> vals = Arrays.asList(2, 4, 7, 14, 28);
        List<double[]> results = new ArrayList<>();
        Map<String, Integer> config;
        int bn = 2;
        for(Integer n: vals) {
            if(n == 7 || n == 4){
                bn = n;
            }
            for(int l=5; l<=25; l+=2) {
                config = makeConfig(n, l, n, l, bn, l);

                System.out.println();
                System.out.println("units:" + String.valueOf(n) + ", levels: " + String.valueOf(l));
                DecisionTree t = TreeMaker.makeTree(L, P, config);
                int[][] images_matrix = Images.parse_csv(DATABSE_FILE_PATH);
                int[][] answers_matrix = Questions.convertToAnswersMatrixVer2(images_matrix, config);

                double rate = TreePredictor.runPrediction(t, answers_matrix);

                results.add(new double[]{n, l, rate});
            }
        }


        System.out.println();
        System.out.println("Results:");

        double[] bestRes = new double[]{-1, -1, -1};
        for(double[] res: results) {
            System.out.println(Arrays.toString(res));

            if(bestRes[2] < res[2]) {
                bestRes = res;
            }
        }

        System.out.println("Best:");
        System.out.println(Arrays.toString(bestRes));

    }

}
