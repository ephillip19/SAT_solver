import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Imp1
 *
 * Reads in CNF and applies DPLL algorithm. Includes helper functions
 *
 * @author Evan Phillips
 * @author Sumer Vaidya
 */

public class Imp1 {
    public static ArrayList<ArrayList<Variable>> cnf;
    public static int num_of_variables;
    public static ArrayList<Variable> var_list; // list of variables in cnf
    public static Stack<ArrayList<ArrayList<Variable>>> delCnf;

    public static int readFile(String file) {

        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            cnf = new ArrayList<ArrayList<Variable>>();
            // myCnf = new ArrayList<ArrayList<Variable>>();
            delCnf = new Stack<ArrayList<ArrayList<Variable>>>();
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if (line.contains("p")) {
                    String info[] = line.split(" ");
                    Imp1.num_of_variables = Integer.parseInt(info[2]);
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
     * checks if the cnf is consistent
     */
    public static boolean isConsistent() {
        if (cnf.size() == 0) {
            return true; // all clauses were eliminated in unit propogation
        }
        for (ArrayList<Variable> clause : cnf) {
            for (Variable lit : clause) {
                for (ArrayList<Variable> clause2 : cnf) {
                    for (Variable lit2 : clause2) {
                        if (lit.value == lit2.value * -1) { // if thre exists a literal and its complement
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
                if (!(lit.VariableAssign.equals("false"))) { // if there is any assignment other than false
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                cnf.remove(cnf.size() - 1); // removes last element in the cnf...
                setVarNull(cnf.get(cnf.size() - 1).get(0));
                if (!delCnf.empty()) { // checks to see if there were any deleted clauses
                    for (ArrayList<Variable> i : delCnf.pop()) {
                        addClause(i);
                    }
                }
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
    public static void setVarTrue(Variable literal) {
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
    }

    /**
     * Removes clauses of size greater than 1 that contain the literal we are
     * checking
     * 
     * @param literal the variable which we remove clauses that contain it
     * @return a cnf of the removed clauses that are not unit literals.
     */

    public static ArrayList<ArrayList<Variable>> removeClause(Variable literal) {
        ArrayList<ArrayList<Variable>> myCnf = new ArrayList<ArrayList<Variable>>();
        for (int i = 0; i < cnf.size(); i++) {
            for (Variable lit : cnf.get(i)) {
                if (lit.value == literal.value) {
                    if (cnf.get(i).size() != 1) {
                        myCnf.add(cnf.get(i)); // adds clause to myCnf that is about to be deleted
                    }
                    cnf.remove(cnf.get(i));

                    if (i < 0) { // do not want to go below zero
                        i--;
                    }
                }
            }
        }
        return myCnf;
    }

    /**
     * if a literal is a unit clause, appliies unit propagation: assigns the
     * necessary value to the literal in order to make the literal true
     */

    public static void unitProp() {
        ArrayList<Variable> lastclause = cnf.get(cnf.size() - 1);
        if (lastclause.size() == 1) {
            setVarTrue(lastclause.get(0)); // set true before we remove unit clause
            ArrayList<ArrayList<Variable>> myCnf = removeClause(lastclause.get(0)); // rem clauses that contain unit c
            delCnf.push(myCnf);
        }
    }

    /**
     * if a literal appears pure, delete all clauses it is contained in from our cnf
     */
    public static void pureLit() {

        ArrayList<Variable> pureLits = new ArrayList<Variable>(); // list holding pure literals
        boolean isPure = true;

        for (Variable var : var_list) { // checks if each literal is pure in our cnf
            for (ArrayList<Variable> clause : cnf) {
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
            removeClause(lit);
        }

    }

    /**
     * adds a given unit variable to the cnf
     * 
     * @return cnf such that DPLL can operate on updated cnf
     */
    public static ArrayList<ArrayList<Variable>> addUnit(Variable var) {
        ArrayList<Variable> unit = new ArrayList<Variable>();
        unit.add(var);
        cnf.add(unit);
        return cnf;
    }

    // adds a given clause to the cnf
    public static void addClause(ArrayList<Variable> clause) {
        cnf.add(clause);
    }

    // sets the variable assignment of a given literal to Null
    public static void setVarNull(Variable lit) {
        for (ArrayList<Variable> clause : cnf) {
            for (Variable var : clause) {
                if (lit.value == var.value || lit.value == var.value * -1) {
                    var.VariableAssign = "null";
                }
            }
        }
    }

    public static boolean DPLL(ArrayList<ArrayList<Variable>> cnf) {

        // for (ArrayList<Variable> clause : myCnf) {// Print cnf for each iteration of
        // DPLL
        // for (Variable lit : clause) {
        // System.out.println(lit.value);
        // }
        // System.out.println("-----------"); // end of clause
        // }
        // System.out.println("======================="); // end of cnf

        if (containsEmpty()) {
            return false;
        }

        if (isConsistent()) {
            return true;
        }

        unitProp(); // apply unit Prop
        pureLit(); // apply pure Literal

        Variable var = null;
        boolean isSet = false;
        for (ArrayList<Variable> clause : cnf) {
            for (Variable lit : clause) {
                if (lit.VariableAssign.equals("null") && !isSet) { // if we have't assigned the variable yet
                    var = lit;
                    isSet = true;
                }
            }
        }
        if (var == null) { // start backtracking
            setVarNull(cnf.get(cnf.size() - 1).get(0)); // sets the first lit in the last
            if (!delCnf.empty()) { // checks to see if there were any deleted clauses
                for (ArrayList<Variable> clause : delCnf.pop()) {
                    addClause(clause);
                }
            }
            return false;
        }

        Variable negVar = new Variable(var.value * -1, "null"); // sets the variables complement
        return DPLL(addUnit(var)) || DPLL(addUnit(negVar));

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