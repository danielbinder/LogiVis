package ctl.parser.ctlnode;

import ctl.parser.CTLParser;
import marker.ConceptRepresentation;

public sealed interface CTLNode extends ConceptRepresentation permits
        ActionNode,
        AndNode,
        ConstantNode,
        DoubleImplicationNode,
        ImplicationNode,
        NegationNode,
        OrNode,
        EXNode,
        EFNode,
        EGNode,
        EUNode,
        AXNode,
        AFNode,
        AGNode,
        AUNode {

    static CTLNode of(String input) {
        return CTLParser.parse(input);
    }
}
