import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static ArrayList<Integer[]> cnf;

    public static int readFile(String file) {
        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            cnf = new ArrayList<Integer[]>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.charAt(0) == 'c' || data.charAt(0) == 'p') {
                    continue;
                }
                if (data.charAt(data.length() - 1) == '0') {
                    String literals_str[] = data.split(" ");
                    Integer literals[] = new Integer[literals_str.length];
                    for (int i = 0; i < literals_str.length; i++) {
                        literals[i] = Integer.parseInt(literals_str[i]);
                    }
                    cnf.add(literals);
                }
            }
            System.out.println(Arrays.toString(cnf.get(0)));
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        readFile("test.cnf");
    }

}