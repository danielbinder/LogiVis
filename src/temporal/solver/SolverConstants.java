package temporal.solver;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SolverConstants {



    public static final String SymLeftPar = "(";
    public static final String SymRightPar = ")";
    public static final String SymAnd = "&";
    public static final String SymOr = "|";
    public static final String SymNot = "!";
    public static final String SymImplication = "->";
    public static final String SymDoubleImplication = "<->";
    public static final String SymEX = "EX";
    public static final String SymAX = "AX";
    public static final String SymEF = "EF";
    public static final String SymAF = "AF";
    public static final String SymEG = "EG";
    public static final String SymAG = "AG";
    public static final String SymAUStart = "A(";
    public static final String SymEUStart = "E(";
    public static final String SymUntil = "U";
    public static final String SymTrue = "true";
    public static final String SymFalse = "false";
    public static final List<String> TEMPORAL_SYMBOLS = new LinkedList<>();

    static {
        TEMPORAL_SYMBOLS.addAll(Arrays
                        .asList(SymEX, SymAX, SymEF, SymAF, SymEG, SymAG, SymEUStart, SymAUStart, SymUntil));
    }
}
