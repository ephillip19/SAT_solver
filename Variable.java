/**
 * Variable
 *
 * Representation of a variable in a given CNF clause
 *
 * @author Evan Phillips
 * @author Sumer Vaidya
 */

public class Variable {
    int count;
    int value;
    int VariableAssign;
    boolean isUnit;
    boolean isPure;

    Variable(int count, int value, int VariableAssign, boolean isUnit, boolean isPure) {
        this.count = count;
        this.value = value;
        this.VariableAssign = VariableAssign; // 0 for neg 1 for pos
        this.isUnit = isUnit;
        this.isPure = isPure;
    }

}