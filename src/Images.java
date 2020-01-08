import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Images {
    public static int[][] parse_csv(String file_path) {
        List<int[]> listMatrix = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
            String line = br.readLine();
            while (line != null) {
                String[] stringVals = line.split(",");
                int[] intVals = Arrays.stream(stringVals)
                        .mapToInt(Integer::parseInt)
                        .toArray();

                listMatrix.add(intVals);
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
            return null;
        }

        int[][] arrayMatrix = new int[listMatrix.size()][listMatrix.get(0).length];

        for (int i=0; i<listMatrix.size(); i++) {
            arrayMatrix[i] = listMatrix.get(i);
        }

        return arrayMatrix;
    }


    private static Function<String, int[]> mapToItem = (line) -> {
        String[] stringVals = line.split(",");
        int[] intVals = Arrays.stream(stringVals)
                .mapToInt(Integer::parseInt)
                .toArray();

        return intVals;
    };


    public static int[][] parse_csv2(String file_path) {
        List<int[]> listMatrix = new ArrayList<>();
        try{
            File inputF = new File(file_path);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            listMatrix = br.lines().map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
            return null;
        }
        int[][] arrayMatrix = new int[listMatrix.size()][listMatrix.get(0).length];

        for (int i=0; i<listMatrix.size(); i++) {
            arrayMatrix[i] = listMatrix.get(i);
        }

        return arrayMatrix;
    }

}
