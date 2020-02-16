import java.util.Map;

public class Questions {
    private static final int MAX_BYTE_VAL = 255;
    public static final String ROWS_NUM = "ROWS_NUM";
    public static final String COLS_NUM = "COLS_NUM";
    public static final String BLOCKS_NUM_SQR = "BLOCKS_NUM_SQR";
    public static final String ROWS_LEVELS = "ROWS_LEVELS";
    public static final String COLS_LEVELS = "COLS_LEVELS";
    public static final String BLOCKS_LEVELS = "BLOCKS_LEVELS";



    public static int[][] convertToAnswersMatrixVer1(int[][] data) {
        for(int i=0; i<data.length; i++) {
            for(int j=1; j<data[0].length; j++) {
                data[i][j] = data[i][j] > 128 ? 1 : 0;
            }
        }

        return data;
    }

    private static int[] makeAnswersFromBlackValues(int[] blackValues, int maxBlack, int levelsOfBlack) {
        int numOfRows = blackValues.length;
        int[] answers = new int[levelsOfBlack * numOfRows];
        int unit = maxBlack / levelsOfBlack;

        for(int row=0; row<blackValues.length; row++) {
            for(int lvl=0; lvl<levelsOfBlack; lvl++) {  // skipping last level - always true that <= maxBlack
                int blackLevel = unit*lvl;

                //TODO: mayble check range, not just <=
                if(blackValues[row] <= blackLevel) {
//                if(blackLevel <= blackValues[row] && blackValues[row] <= blackLevel + unit) {
                    answers[row*levelsOfBlack + lvl] = 1;
                } else {
                    answers[row*levelsOfBlack + lvl] = 0;
                }
            }
        }

        return answers;

    }

    private static int[] askRowQuestion(int[][] pixelMap, int numOfRows, int levelsOfBlack) {
        int dim = pixelMap.length;
        int rowsInLayer = dim/numOfRows;

        int[] blackInRows = new int[numOfRows];  // initial values are 0
        for(int i=0; i<dim; i++) {
            int rowNum = i / rowsInLayer;
            for(int j=0; j<dim; j++) {
                blackInRows[rowNum] += pixelMap[i][j];
            }
        }

        int maxBlack = rowsInLayer * dim * MAX_BYTE_VAL;
        return makeAnswersFromBlackValues(blackInRows, maxBlack, levelsOfBlack);

    }

    private static int[] askColumnQuestion(int[][] pixelMap, int numOfCols, int levelsOfBlack) {
        int dim = pixelMap.length;
        int colsInLayer = dim/numOfCols;

        int[] blackInCols = new int[numOfCols];  // initial values are 0
            for(int j=0; j<dim; j++) {
            int colNum = j / colsInLayer;
                for(int i=0; i<dim; i++) {
                    blackInCols[colNum] += pixelMap[i][j];
                }
        }

        int maxBlack = colsInLayer * dim * MAX_BYTE_VAL;
        return makeAnswersFromBlackValues(blackInCols, maxBlack, levelsOfBlack);
    }

    private static int[] askBlockQuestion(int[][] pixelMap, int blocksInRow, int levelsOfBlack) {
        int dim = pixelMap.length;
        int pixelsInBlock = dim / blocksInRow;
        int numOfBlocks = (int) Math.pow(blocksInRow, 2);

        int[] blackInBlock = new int[numOfBlocks];

        // do outer loop with block-size jumps
        for(int i=0; i<dim; i+=pixelsInBlock) {
            for(int j=0; j<dim; j+=pixelsInBlock) {

                // do inner loop, over block-size
                for(int r=0; r<pixelsInBlock; r++) {
                    for(int c=0; c<pixelsInBlock; c++) {
                        int blockId = (i/pixelsInBlock * blocksInRow) + (j/pixelsInBlock);
                        blackInBlock[blockId] += pixelMap[i+r][j+c];
                    }

                }
            }
        }

        int maxBlack = pixelsInBlock * dim * MAX_BYTE_VAL;
        return makeAnswersFromBlackValues(blackInBlock, maxBlack, levelsOfBlack);
    }

    private static int[][] make2D(int[] image, int dim) {
        int[][] res = new int[dim][dim];
        for(int i=0; i<image.length-1; i++) {  // shifted due to the label at index 0
            int pix = image[i+1];  // here too
            res[i/dim][i%dim] = pix;
        }

        return res;
    }

    private static int[] joinArrays(int[] a, int[] b, int[] c, int[] d) {
        int[] res = new int[a.length + b.length + c.length + d.length];

        int i=0;
        for(; i<a.length; i++) {
            res[i] = a[i];
        }

        int j=0;
        for(; j<b.length; j++) {
            res[i+j] = b[j];
        }

        int k=0;
        for(; k<c.length; k++) {
            res[i+j+k] = c[k];
        }

        int m=0;
        for(; m<d.length; m++) {
            res[i+j+k+m] = d[m];
        }



        return res;
    }


    public static int[][] convertToAnswersMatrixVer2(int[][] data, Map<String, Integer> config) {
        int dim = (int) Math.sqrt(data[0].length - 1);  // 28

        for(int im=0; im<data.length; im++) {
            int[][] pixelMap = make2D(data[im], dim);

            if(config.get(ROWS_NUM) > dim || config.get(COLS_NUM) > dim || config.get(BLOCKS_NUM_SQR) > dim) {
                throw new Error("Num of row/cols/blocks can't exceed: " + String.valueOf(dim));
            }
            if(dim % config.get(ROWS_NUM) != 0 || dim % config.get(COLS_NUM) != 0 || dim % config.get(BLOCKS_NUM_SQR) != 0) {
                throw new Error("num of rows/cols/blocks has to be a divisor of: " + String.valueOf(dim));
            }

            int[] rowAnswers = askRowQuestion(pixelMap, config.get(ROWS_NUM), config.get(ROWS_LEVELS));
            int[] colAnswers = askColumnQuestion(pixelMap, config.get(COLS_NUM), config.get(COLS_LEVELS));
            int[] blockAnswers = askBlockQuestion(pixelMap, config.get(BLOCKS_NUM_SQR), config.get(BLOCKS_LEVELS));

            // TODO: use List conversion
            data[im] = joinArrays(new int[]{data[im][0]}, rowAnswers, colAnswers, blockAnswers);
        }
        return data;

    }
}


