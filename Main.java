import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static ArrayList<Variable[]> cnf;
    public static ArrayList<Variable> list_of_variables; // list of variables
    public static int num_of_variables;

    public static int readFile(String file) {

        try {
            File myObj = new File("test.cnf");
            Scanner myReader = new Scanner(myObj);
            cnf = new ArrayList<Variable[]>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.charAt(0) == 'c') {
                    continue;
                } else if (data.contains("p")) {
                    String info[] = data.split(" ");
                    Main.num_of_variables = Integer.parseInt(info[2]);
                } else if (data.charAt(data.length() - 1) == '0') {
                    String literals_str[] = data.split(" ");
                    Variable clause[] = new Variable[literals_str.length - 1];
                    for (int i = 0; i < literals_str.length - 1; i++) {
                        clause[i] = new Variable(Integer.parseInt(literals_str[i]), "null");
                    }
                    cnf.add(clause);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isConsistent() {
        for (Variable[] clause : cnf) {
            boolean isSat = false;
            for (Variable lit : clause) {
                if ((lit.value > 0 && lit.VariableAssign.equals("true"))
                        || (lit.value < 0 && lit.VariableAssign.equals("false"))) {
                    isSat = true;
                    break;
                }
                if (!isSat) {
                    return false;

                }
            }
        }
        return true;
    }

    public static boolean containsEmpty() {
        for (Variable[] clause : cnf) {
            boolean isEmpty = true;
            for (Variable lit : clause) {
                if ((lit.value > 0 && lit.VariableAssign.equals("false"))
                        || (lit.value < 0 && lit.VariableAssign.equals("true"))) {
                    continue;
                } else {
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                return true;
            }
        }
        return false;

    }

    public static void setVarTrue(Variable literal) {
        for (Variable[] clause : cnf) {
            for (Variable lit : clause) {
                if (lit.value == literal.value) {
                    lit.VariableAssign = "true";
                }
                if (lit.value * -1 == literal.value) {
                    lit.VariableAssign = "false";
                }
            }
        }

    }

    public static void unitProp(Variable literal) {
        for (Variable[] clause : cnf) {
            if (clause.length == 1 && clause[0].value == Math.abs(literal.value)) {
                setVarTrue(literal);
                break;
            }
        }
    }

    public static void pureLiteral(Variable literal) {
        boolean isPositive = false;
        boolean isNegative = false;
        for (Variable[] clause : cnf) {
            for (Variable lit : clause) {
                // check to see if lit is all positive or all negative
                if (literal.value == lit.value) { // pos case
                    isPositive = true;
                }
                if (literal.value == lit.value * -1) { // neg case
                    isNegative = true;
                }

            }
        }
        if (!(isPositive && isNegative)) {
            setVarTrue(literal);
        }
    }

    public static boolean DPLL() {
        if (isConsistent()) {
            return true;
        }
        if (containsEmpty()) {
            return false;
        }
        // unitProp(var);
        // pureLiteral(var);

        return false; // DPLL(setVarTrue(var))

    }

    public static void main(String[] args) {
        readFile("test.cnf");

        // testing
        // Variable var = new Variable(1, "false");
        // unitProp(var);
        // pureLiteral(var);
        // for (Variable[] clause : cnf) {
        // for (Variable lit : clause) {
        // System.out.println(lit.VariableAssign);
        // }
        // }
    }
}