/**
 * Variable
 *
 * Representation of a variable in a given CNF clause
 *
 * @author Evan Phillips
 * @author Sumer Vaidya
 */

public class Variable {
    public int value;
    public String VariableAssign;

    Variable(int value, String VariableAssign) {
        this.value = value;
        this.VariableAssign = VariableAssign;
    }

}