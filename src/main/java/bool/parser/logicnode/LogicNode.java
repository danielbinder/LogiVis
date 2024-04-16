package bool.parser.logicnode;

import bool.interpreter.TseitinTransformation;
import bool.parser.BooleanParser;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import marker.ConceptRepresentation;

public sealed interface LogicNode extends ConceptRepresentation permits
        ActionNode,
        AndNode,
        ConstantNode,
        DoubleImplicationNode,
        ImplicationNode,
        NegationNode,
        OrNode {
    static LogicNode of(String formula) {
        return BooleanParser.parse(formula);
    }

    default Conjunction toCNF() {
        return TseitinTransformation.of(this);
    }
}

