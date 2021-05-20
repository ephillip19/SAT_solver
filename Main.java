import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Main
 *
 * Reads in CNF and applies DPLL algorithm. Includes helper functions
 *
 * @author Evan Phillips
 * @author Sumer Vaidya
 */

public class Main {
    public static ArrayList<Variable[]> cnf;
    public static int num_of_variables;
    public static ArrayList<Variable> var_list; // list of variables in cnf

    public static int readFile(String file) {

        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            cnf = new ArrayList<Variable[]>();
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if (line.contains("p")) {
                    String info[] = line.split(" ");
                    Main.num_of_variables = Integer.parseInt(info[2]);
                } else if (line.charAt(line.length() - 1) == '0') {
                    String literals_str[] = line.split(" ");
                    Variable clause[] = new Variable[literals_str.length - 1]; // don't include 0 as variable
                    for (int i = 0; i < literals_str.length - 1; i++) {
                        clause[i] = new Variable(Integer.parseInt(literals_str[i]), "null"); // intialize each literal
                    }
                    cnf.add(clause);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        // create list of variables present in cnf
        var_list = new ArrayList<Variable>();
        for (int i = 1; i < num_of_variables + 1; i++) {
            var_list.add(new Variable(i, "null"));
        }

        return 0;
    }

    /**
     * checks if the cnf is consistent: at least one literal evaluates to true in
     * each clause
     */
    public static boolean isConsistent() {
        for (Variable[] clause : cnf) {
            boolean isSat = false;
            for (Variable lit : clause) {
                if (lit.VariableAssign.equals("true")) {
                    isSat = true;
                }
            }
            if (!isSat) {
                return false;
            }
        }
        return true;
    }

    /**
     * checks if the cnf contains an empty clause: a clause that only contains
     * literals that evaluate to false
     */
    public static boolean containsEmpty() {
        for (Variable[] clause : cnf) {
            boolean isEmpty = true;
            for (Variable lit : clause) {
                if (!(lit.VariableAssign.equals("false"))) {
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                return true;
            }
        }
        return false;

    }

    /**
     * sets the value of a literal to True. Only takes in positive lits
     * 
     * @param literal the variable which we set to True
     */
    public static ArrayList<Variable[]> setVarTrue(Variable literal) {
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
        return cnf;
    }

    /**
     * sets the value of a literal to False. Only takes in positive lits
     * 
     * @param literal the literal which we set to False
     */
    public static ArrayList<Variable[]> setVarFalse(Variable literal) {
        for (Variable[] clause : cnf) {
            for (Variable lit : clause) {
                if (lit.value == literal.value) {
                    lit.VariableAssign = "false";
                }
                if (lit.value * -1 == literal.value) {
                    lit.VariableAssign = "true";
                }
            }
        }
        return cnf;

    }

    /**
     * if a literal is a unit clause, appliies unit propagation: assigns the
     * necessary value to the literal in order to make the literal true
     * 
     * @param literal the variable which we attempt to apply unit propogation
     */
    public static boolean unitProp(Variable literal) {
        for (Variable[] clause : cnf) {
            if (clause.length == 1 && clause[0].value == literal.value) {
                setVarTrue(literal);
                return true;
            }
            if (clause.length == 1 && clause[0].value * -1 == literal.value) {
                setVarFalse(literal);
                return true;
            }
        }
        return false;
    }

    /**
     * if a literal is a pure literal, applies pure literal elimination: assigns
     * literal in a way that makes all clauses containing it true
     * 
     * @param literal the literal which we attempt to apply pure literal elimination
     */
    public static boolean pureLiteral(Variable literal) {
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

        if (isPositive && !isNegative) {
            setVarTrue(literal);
            return true;
        }
        if (!isPositive && isNegative) {
            setVarFalse(literal);
            return true;
        }
        return false;
    }

    public static boolean DPLL(ArrayList<Variable[]> cnf) {
        if (isConsistent()) {
            return true;
        }
        if (containsEmpty()) {
            return false;
        }
        Variable var = var_list.remove(0); // retrieves literals in sequential order
        System.out.println(var.value);
        // if (unitProp(var) || pureLiteral(var)) {
        // return DPLL(cnf);
        // } else {
        // return DPLL(setVarTrue(var)) || DPLL(setVarFalse(var));
        // }

        return DPLL(setVarTrue(var)) || DPLL(setVarFalse(var));

    }

    public static void main(String[] args) {
        readFile("test.cnf"); // "HG-5SAT-V50-C900-13.cnf" uf20-01
        if (DPLL(cnf)) {
            System.out.println("TRUE");
        } else {
            System.out.println("FALSE");
        }
        // for (Variable[] clause : cnf) {
        // for (Variable v : clause) {
        // System.out.println(v.VariableAssign);
        // }
        // }
    }
}