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
    public static ArrayList<ArrayList<Variable>> cnf;
    public static ArrayList<ArrayList<Variable>> orig_cnf;
    public static int num_of_variables;
    public static ArrayList<Variable> var_list; // list of variables in cnf

    public static int readFile(String file) {

        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            cnf = new ArrayList<ArrayList<Variable>>();
            orig_cnf = new ArrayList<ArrayList<Variable>>();
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if (line.contains("p")) {
                    String info[] = line.split(" ");
                    Main.num_of_variables = Integer.parseInt(info[2]);
                } else if (line.charAt(line.length() - 1) == '0') {
                    String literals_str[] = line.split(" ");
                    ArrayList<Variable> clause = new ArrayList<Variable>();
                    for (int i = 0; i < literals_str.length - 1; i++) {
                        clause.add(new Variable(Integer.parseInt(literals_str[i]), "null")); // intialize each literal
                    }
                    cnf.add(clause);
                    orig_cnf.add(clause);
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
     * checks if the cnf is consistent
     */
    public static boolean isConsistent() {
        if (containsEmpty()) { // edge case: if the last clause is empty
            return false;
        }
        for (ArrayList<Variable> clause : cnf) {
            for (Variable lit : clause) {
                for (ArrayList<Variable> clause2 : cnf) {
                    for (Variable lit2 : clause2) {
                        if (lit.value == lit2.value * -1) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;

    }

    /**
     * checks if the cnf contains an empty clause: a clause that only contains
     * literals that evaluate to false
     */
    public static boolean containsEmpty() {
        for (ArrayList<Variable> clause : cnf) {
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
    public static ArrayList<ArrayList<Variable>> setVarTrue(Variable literal) {
        for (ArrayList<Variable> clause : cnf) {
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
    public static ArrayList<ArrayList<Variable>> setVarFalse(Variable literal) {
        for (ArrayList<Variable> clause : cnf) {
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

    public static void removeClause(Variable literal) {
        for (int i = 0; i < cnf.size(); i++) {
            for (Variable lit : cnf.get(i)) {
                if (lit.value == literal.value) {
                    cnf.remove(cnf.get(i));
                    i--;
                }
            }
        }

    }

    /**
     * if a literal is a unit clause, appliies unit propagation: assigns the
     * necessary value to the literal in order to make the literal true
     * 
     * @param literal the variable which we attempt to apply unit propogation
     */
    public static void unitProp() {
        for (int i = 0; i < cnf.size(); i++) {
            if (cnf.get(i).size() == 1 && cnf.get(i).get(0).VariableAssign.equals("null")) {
                setVarTrue(cnf.get(i).get(0)); // discards all complements
                removeClause(cnf.get(i).get(0));
                i--;
            }
        }
    }

    /**
     * if a literal is a pure literal, applies pure literal elimination: assigns
     * literal in a way that makes all clauses containing it true
     * 
     * @param literal the literal which we attempt to apply pure literal elimination
     */
    public static void pureLiteral() {
        for (int i = 0; i < cnf.size(); i++) {
            for (Variable lit : cnf.get(i)) {
                boolean isNotPure = false;
                for (int j = 0; j < cnf.size(); j++) {
                    for (Variable lit2 : cnf.get(j)) {
                        if (lit.value == lit2.value * -1) {
                            isNotPure = true;
                        }
                    }
                }
                if (!isNotPure) {
                    System.out.println(cnf.size());
                    removeClause(lit);
                }

            }
        }
    }

    public static ArrayList<ArrayList<Variable>> addUnit(Variable var) {
        ArrayList<Variable> unit = new ArrayList<Variable>();
        unit.add(var);
        cnf.add(unit);
        return cnf;
    }

    public static void resetVarList() {
        for (int i = 1; i < num_of_variables; i++) {
            var_list.add(new Variable(i * -1, "null")); // i * -1 ??
        }
        cnf.clear();
        for (ArrayList<Variable> clause : orig_cnf) {
            cnf.add(clause);
        }
    }

    public static boolean DPLL(ArrayList<ArrayList<Variable>> cnf) {
        System.out.println(cnf.size());
        // for (ArrayList<Variable> clause : cnf) {
        // for (Variable lit : clause) {
        // System.out.println(lit.value);
        // }
        // System.out.println("\n");
        // }
        // System.out.println("––––––––––––––––––––");

        if (isConsistent()) {
            return true;
        }
        if (containsEmpty()) {
            return false;
        }
        unitProp();
        // pureLiteral();

        if (var_list.size() == 0) { // all vars have been checked
            resetVarList();
            System.out.println(cnf.size());
        }

        Variable var = var_list.remove(0);
        // System.out.println(var.value);
        Variable negVar = new Variable(var.value * -1, "null");
        return DPLL(addUnit(var)) || DPLL(addUnit(negVar));
    }

    public static void main(String[] args) {
        readFile("test.cnf"); // "HG-5SAT-V50-C900-13.cnf" uf20-01
        if (DPLL(cnf)) {
            System.out.println("TRUE");
        } else {
            System.out.println("FALSE");
        }

    }
}