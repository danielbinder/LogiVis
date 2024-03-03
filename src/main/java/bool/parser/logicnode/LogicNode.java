package bool.parser.logicnode;

import bool.parser.BooleanParser;
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
}

