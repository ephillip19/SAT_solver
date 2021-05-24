/**
 * Variable
 *
 * Representation of a variable in a given CNF clause
 *
 * @author Evan Phillips
 * @author Sumer Vaidya
 */

public class Variable {
    public int value; // -1 or 1 for example
    public String VariableAssign; // True, False, or Null

    Variable(int value, String VariableAssign) {
        this.value = value;
        this.VariableAssign = VariableAssign;
    }

}