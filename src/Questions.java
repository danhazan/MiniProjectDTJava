public class Questions {
    public static void convertToAnswersMatrixVer1(int[][] data) {
        for(int i=0; i<data.length; i++) {
            for(int j=1; j<data[0].length; j++) {
                data[i][j] = data[i][j] > 128 ? 1 : 0;
            }
        }
    }
}
