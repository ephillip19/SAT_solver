import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Imp2
 *
 * Reads in CNF and applies DPLL algorithm. Includes helper functions
 *
 * @author Evan Phillips
 * @author Sumer Vaidya
 */

public class Imp2 {
    public static ArrayList<ArrayList<Variable>> cnf;
    public static int num_of_variables;
    public static ArrayList<Variable> var_list; // list of variables in cnf

    public static int readFile(String file) {

        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            cnf = new ArrayList<ArrayList<Variable>>();
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if (line.contains("p")) {
                    String info[] = line.split(" ");
                    Imp2.num_of_variables = Integer.parseInt(info[2]);
                } else if (line.charAt(line.length() - 1) == '0') {
                    String literals_str[] = line.split(" ");
                    ArrayList<Variable> clause = new ArrayList<Variable>();
                    for (int i = 0; i < literals_str.length - 1; i++) {
                        clause.add(new Variable(Integer.parseInt(literals_str[i]), "null")); // intialize each literal
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
     * checks if there our cnf is consistent, I.E there every literal appears pure
     */

    public static boolean isConsistent(ArrayList<ArrayList<Variable>> myCnf) {
        if (myCnf.size() == 0) {
            return true; // all clauses were eliminated in unit propogation and pure literal elimination
        }
        for (ArrayList<Variable> clause : myCnf) {
            for (Variable lit : clause) {
                for (ArrayList<Variable> clause2 : myCnf) {
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
    public static boolean containsEmpty(ArrayList<ArrayList<Variable>> myCnf) {

        for (ArrayList<Variable> clause : myCnf) {
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
     * sets the value of a literal to True.
     *
     * @param literal the variable which we set to True
     */

    public static ArrayList<ArrayList<Variable>> setVarTrue(Variable literal, ArrayList<ArrayList<Variable>> myCnf) {
        for (ArrayList<Variable> clause : myCnf) {
            for (Variable lit : clause) {
                if (lit.value == literal.value) {
                    lit.VariableAssign = "true";
                }
                if (lit.value * -1 == literal.value) {
                    lit.VariableAssign = "false";
                }
            }
        }
        return myCnf;
    }

    /**
     * removes all clauses containing a the literal parameter
     *
     * @param literal the variable which we remove from cnf
     * @param myCnf   current Cnf
     */

    public static ArrayList<ArrayList<Variable>> removeClause(Variable literal, ArrayList<ArrayList<Variable>> myCnf) {
        for (int i = 0; i < myCnf.size(); i++) {
            for (Variable lit : myCnf.get(i)) {
                if (lit.value == literal.value) {
                    myCnf.remove(myCnf.get(i));
                    i--;
                }
            }
        }
        return myCnf;
    }

    /**
     * if a literal is a unit clause, appliies unit propagation: assigns the
     * necessary value to the literal in order to make the literal true
     *
     * @param literal the variable which we attempt to apply unit propogation
     */

    public static ArrayList<ArrayList<Variable>> unitProp(ArrayList<ArrayList<Variable>> myCnf) {
        ArrayList<Variable> lastclause = myCnf.get(myCnf.size() - 1); // checks where we add unit literal
        if (lastclause.size() == 1) { // if unit clause
            setVarTrue(lastclause.get(0), myCnf);// set to true and complement to false
            removeClause(lastclause.get(0), myCnf);// remove all clauses containing unit literal
        }
        return myCnf;
    }

    /**
     * if a literal appears pure, delete all clauses it is contained in from our cnf
     *
     * @param literal the variable which we attempt to apply unit propogation
     */

    public static ArrayList<ArrayList<Variable>> pureLit(ArrayList<ArrayList<Variable>> myCnf) {

        ArrayList<Variable> pureLits = new ArrayList<Variable>(); // list holding pure literals
        boolean isPure = true;

        for (Variable var : var_list) { // checks if each literal is pure in our cnf
            for (ArrayList<Variable> clause : myCnf) {
                for (Variable lit : clause) {
                    if (lit.value == -1 * var.value) {
                        isPure = false;
                    }
                }
            }
            if (isPure) { // if literal is pure, add to list of pure
                pureLits.add(var);
            }
            isPure = true;
        }
        for (Variable lit : pureLits) {// if literal is pure, remove all clauses containg it from our cnf
            removeClause(lit, myCnf);
        }
        return myCnf;
    }

    // adds a given unit variable to the cnf
    public static ArrayList<ArrayList<Variable>> addUnit(Variable var, ArrayList<ArrayList<Variable>> myCnf) {
        ArrayList<ArrayList<Variable>> new_cnf = new ArrayList<ArrayList<Variable>>();
        for (ArrayList<Variable> clause : myCnf) { // transfers all old clauses into new cnf
            new_cnf.add(clause);
        }
        ArrayList<Variable> unit = new ArrayList<Variable>();
        unit.add(var);
        new_cnf.add(unit); // adds new unit clause to new cnf
        return new_cnf;
    }

    public static boolean DPLL(ArrayList<ArrayList<Variable>> myCnf) {

        // for (ArrayList<Variable> clause : myCnf) {// Print cnf for each iteration of
        // DPLL
        // for (Variable lit : clause) {
        // System.out.println(lit.value);
        // }
        // System.out.println("-----------"); // end of clause
        // }
        // System.out.println("======================="); // end of cnf

        if (containsEmpty(myCnf)) { // check if our current cnf is UNSAT
            return false;
        }

        if (isConsistent(myCnf)) {// check if our current cnf is SAT
            return true;
        }

        myCnf = unitProp(myCnf); // apply Unit Propogation
        myCnf = pureLit(myCnf); // apply Pure Literal Elimination

        // choose a new variable from our cnf to add as unit clause
        Variable var = null;
        boolean isSet = false;
        for (ArrayList<Variable> clause : cnf) {
            for (Variable lit : clause) {
                if (lit.VariableAssign.equals("null") && !isSet) {
                    var = lit;
                    isSet = true;
                }
            }
        }

        if (var == null) { // if no variable left to check
            if (containsEmpty(myCnf)) { // check if our current cnf is UNSAT
                return false;
            }
            if (isConsistent(myCnf)) {// check if our current cnf is SAT
                return true;
            }
        }

        Variable negVar = new Variable(var.value * -1, "null"); // create a variable for complement of var

        ArrayList<ArrayList<Variable>> cnf_pos = addUnit(var, myCnf); // create new cnf with var as unit clause
        ArrayList<ArrayList<Variable>> cnf_neg = addUnit(negVar, myCnf);// create new cnf with negation of var as unit
                                                                        // clause

        // recursive call on both new cnfs to check next stage of cnf
        return DPLL(cnf_pos) || DPLL(cnf_neg);
    }

    public static void main(String[] args) {
        readFile("tests/SAT_largetest.cnf");
        if (DPLL(cnf)) {
            System.out.println("SAT");
        } else {
            System.out.println("UNSAT");
        }
    }
}