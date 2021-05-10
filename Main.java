import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static ArrayList<Integer[]> cnf;
    public static ArrayList<Variable> list_of_variables; // list of variables
    public static ArrayList<Integer> vars;
    public static ArrayList<String> vals;
    public static int num_of_variables;

    public static int readFile(String file) {
        try {
            File myObj = new File("test.cnf");
            Scanner myReader = new Scanner(myObj);
            cnf = new ArrayList<Integer[]>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.charAt(0) == 'c') {
                    continue;
                } else if (data.contains("p")) {
                    String info[] = data.split(" ");
                    Main.num_of_variables = Integer.parseInt(info[2]);
                } else if (data.charAt(data.length() - 1) == '0') {
                    String literals_str[] = data.split(" ");
                    Integer literals[] = new Integer[literals_str.length];
                    for (int i = 0; i < literals_str.length; i++) {
                        literals[i] = Integer.parseInt(literals_str[i]);
                    }
                    cnf.add(literals);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        list_of_variables = new ArrayList<Variable>();
        for (int i = 1; i < num_of_variables + 1; i++) {
            list_of_variables.add(new Variable(0, i, 0, false, true));
        }

        setVars(cnf);
        return 0;
    }

    public static void setVars(ArrayList<Integer[]> cnf) {
        for (Variable v : list_of_variables) {
            boolean isNegated = false;
            boolean isPositive = false;
            for (Integer[] clause : cnf) {
                for (int var : clause) {
                    if (v.value == Math.abs(var) && var < 0) {
                        isNegated = true;
                        v.count++;
                    }
                    if (v.value == Math.abs(var) && var > 0) {
                        isPositive = true;
                        v.count++;
                    }
                }
            }
            if (isNegated && isPositive) {
                v.isPure = false;
            }
            if (v.count == 1) {
                v.isUnit = true;
            }
        }
    }

    public static void UnitProp(ArrayList<Integer[]> cnf) {

    }

    public static void main(String[] args) {
        readFile("test.cnf");
    }

}