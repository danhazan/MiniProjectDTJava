import com.sun.source.tree.Tree;

import java.text.SimpleDateFormat;
import java.util.*;

public class PredictMain {
    private static final String TRAIN_FILE_PATH = "resources/mnist_train.csv";
    private static final String TEST_FILE_PATH = "resources/mnist_test.csv";
//    private static final String TRAIN_FILE_PATH = "resources/kannada_train.csv";
//    private static final String TEST_FILE_PATH = "resources/kannada_test.csv";

    private static Map<String, Integer> BEST_CONFIG = new HashMap<>() {{
        put(Questions.ROWS_NUM, 14);
        put(Questions.ROWS_LEVELS, 15);
        put(Questions.COLS_NUM, 14);
        put(Questions.COLS_LEVELS, 15);
        put(Questions.BLOCKS_NUM_SQR, 28);
        put(Questions.BLOCKS_LEVELS, 1);
    }};


    public static void main(String[] args) {
//        configTester();
        experiment(1, 1);
        experiment(1, 2);
        experiment(2, 1);
        experiment(2, 2);
    }

    public static void main2(String[] args) {
        int L = 8;
        double P = 20/100.0;
        // TODO: is p=0 or p=100 acceptable

        //TRAIN::

        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        int[][] images_matrix = Images.parse_csv(TRAIN_FILE_PATH);
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
//        DecisionTree t = TreeMaker.makeTree(L, P, null);
        DecisionTree t = TreeMaker.makeTree(L, P, images_matrix, BEST_CONFIG);



        //PREDICT::

        images_matrix = Images.parse_csv(TEST_FILE_PATH);
//        int[][] answers_matrix = Questions.convertToAnswersMatrixVer1(images_matrix);
        int[][] answers_matrix = Questions.convertToAnswersMatrixVer2(images_matrix, BEST_CONFIG);
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


    public static void experiment(int ver, int dataset) {
        String testDS;
        String trainDS;
        if(dataset == 1) {
            testDS = "resources/mnist_test.csv";
            trainDS = "resources/mnist_train.csv";
        } else {
            testDS = "resources/kannada_test.csv";
            trainDS = "resources/kannada_train.csv";
        }


        for(double i=0.01; i<=1.0; i+=0.01) {
            int L = 8;
            double P = 20/100.0;

            //TRAIN::

            int[][] all_images_matrix = Images.parse_csv(trainDS);
            int[][] indexes = TreeMaker.splitValidationSet(all_images_matrix.length, i);
            int[][] images_matrix = TreeMaker.fromIndexes(all_images_matrix, indexes[0]);

            DecisionTree t;

            if(ver == 1) {
                t = TreeMaker.makeTree(L, P, images_matrix, null);
            } else {
                t = TreeMaker.makeTree(L, P, images_matrix, BEST_CONFIG);
            }




            //PREDICT::

            images_matrix = Images.parse_csv(testDS);
            int[][] answers_matrix;
            if(ver == 1) {
                answers_matrix = Questions.convertToAnswersMatrixVer1(images_matrix);
            } else {
                answers_matrix = Questions.convertToAnswersMatrixVer2(images_matrix, BEST_CONFIG);
            }
            TreePredictor.runPrediction(t, answers_matrix);

        }

    }


    public static void configTester() {
        int L = 8;
        double P = 20/100.0;


        List<Integer> vals = Arrays.asList(2, 4, 7, 14, 28);
        List<double[]> results = new ArrayList<>();
        Map<String, Integer> config;
//        int bn = 2;
        for(Integer n: vals) {
//            if(n == 7 || n == 4){
//                bn = n;
//            }
            for(int l=1; l<=30; l+=3) {
                config = makeConfig(n, l, n, l, 28, 1);

//                system.out.println();
//                system.out.println("units:" + string.valueof(n) + ", levels: " + string.valueof(l));
//                decisiontree t = treemaker.maketree(l, p, config);
//                int[][] images_matrix = images.parse_csv(databse_file_path);
//                int[][] answers_matrix = questions.converttoanswersmatrixver2(images_matrix, config);
//
//                double rate = treepredictor.runprediction(t, answers_matrix);

//                results.add(new double[]{n, l, rate});
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
