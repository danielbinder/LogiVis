package temporal.solver;

public class BinarySymbol {

    private boolean binary;
    private String leftExpression;
    private String rightExpression;
    private ExprType type;

    public BinarySymbol(boolean isBinary, String leftExpression, String rightExpression) {
        this.binary = isBinary;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    public BinarySymbol(boolean isBinary, String leftExpression, String rightExpression, ExprType type) {
        this(isBinary, leftExpression, rightExpression);
        this.type = type;
    }

    public boolean isBinary() { return binary; }

    public String getLeftExpression() { return leftExpression.trim(); }

    public String getRightExpression() { return rightExpression.trim(); }

    public ExprType getExprType() { return type; }

    public void setExprType(ExprType type) { this.type = type; }
}
