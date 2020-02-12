import java.util.List;

public class Questions {
    public static int[][] convertToAnswersMatrixVer1(int[][] data) {
        for(int i=0; i<data.length; i++) {
            for(int j=1; j<data[0].length; j++) {
                data[i][j] = data[i][j] > 128 ? 1 : 0;
            }
        }

        return data;
    }

    private static int[] askRowQuestion(int[][] pixelMap) {
        int dim = pixelMap.length;
        int factor = 4;
        int numOfRows = dim/factor;  // 7 rows

        int[] blackInRows = new int[numOfRows];  // initial values are 0
        for(int i=0; i<dim; i++) {
            int rowNum = i / factor;
            for(int j=0; j<dim; j++) {
                blackInRows[rowNum] += pixelMap[i][j];
            }
        }




        return null;
    }

    private static int[] askColumnQuestion(int[][] pixelMap) {
        int dim = pixelMap.length;
        return null;
    }

    private static int[] askBlockQuestion(int[][] pixelMap) {
        int dim = pixelMap.length;
        return null;
    }

    private static int[][] make2D(int[] image, int dim) {
        int[][] res = new int[dim][dim];
        for(int i=0; i<image.length-1; i++) {  // shifted due to the label at index 0
            int pix = image[i+1];  // here too
            res[i/dim][i%dim] = pix;
        }

        return res;
    }


    public static int[][] convertToAnswersMatrixVer2(int[][] data) {
        int[][] answers_list = new int[data.length][1000];
        int dim = (int) Math.sqrt(data[0].length - 1);  // 28

        for(int im=0; im<data.length; im++) {
            int[][] pixelMap = make2D(data[im], dim);


            int[] rowAnswers = askRowQuestion(pixelMap);
            int[] colAnswers = askColumnQuestion(pixelMap);
            int[] blockAnswers = askBlockQuestion(pixelMap);

//            data[im] = append(data[im][0], rowAnswers, colAnswers, blockAnswers);

//            for(int pix=1; pix<data[0].length; pix++) {
        }
        return data;

    }
}


