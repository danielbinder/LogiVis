package interpreter;

import parser.logicnode.*;

public class Simplification {
    public static LogicNode of(LogicNode node) {
        return simplify(node);
    }

    /**
     * This simplifies the node on current level.
     * @param node to simplify
     * @return simplified LogicNode
     */
    private static LogicNode simplify(LogicNode node) {
        return switch(node) {
            case NegationNode n -> {
                // !true = false; !false = true
                if(n.child() instanceof ConstantNode c) yield new ConstantNode(!c.bool());
                // Involution
                // !!a = a
                if(n.child() instanceof NegationNode n2) yield n2.child();
                yield n;
            }
            case AndNode a -> {
                // Idem potency
                // a & a = a
                if(a.left().equals(a.right())) yield a.left();
                // Complementation
                // a & !a = false; !a & a = false
                if(a.right() instanceof NegationNode n1 && a.left().equals(n1.child()) ||
                        a.left() instanceof NegationNode n2 && a.right().equals(n2.child()))
                    yield new ConstantNode(false);
                // Identity and Null Law
                // true & a = a; false & a = false
                if(a.left() instanceof ConstantNode c) {
                    if(c.bool()) yield a.right();
                    else yield c;
                }
                // a & true = a; a & false = false;
                if(a.right() instanceof ConstantNode c) {
                    if(c.bool()) yield a.right();
                    else yield c;
                }
                // DeMorgan
                // !a & !b = !(a | b)
                if(a.left() instanceof NegationNode n1 && a.right() instanceof NegationNode n2)
                    yield new NegationNode(new OrNode(n1.child(), n2.child()));
                // Absorption
                // a & (a | b) = a; a & (b | a) = a
                if(a.right() instanceof OrNode o && (a.left().equals(o.left()) || a.left().equals(o.right())))
                    yield a.left();
                // (a | b) & a = a; (b | a) & a = a;
                if(a.left() instanceof OrNode o && (a.right().equals(o.left()) || a.right().equals(o.right())))
                    yield a.right();
                // Simplification
                // (!a | b) & a = a & b
                if(a.left() instanceof OrNode o && o.left() instanceof NegationNode n && n.child().equals(a.right()))
                    yield new AndNode(a.right(), o.right());
                // (b | !a) & a = a & b
                if(commute(a.left()) instanceof OrNode o && o.left() instanceof NegationNode n && n.child().equals(a.right()))
                    yield new AndNode(a.right(), o.right());
                // a & (!a | b) = a & b
                if(a.right() instanceof OrNode o && o.left() instanceof NegationNode n && n.child().equals(a.left()))
                    yield new AndNode(a.left(), o.right());
                // a & (b | !a) = a & b
                if(commute(a.right()) instanceof OrNode o && o.left() instanceof NegationNode n && n.child().equals(a.left()))
                    yield new AndNode(a.left(), o.right());
                // Distributive
                if(a.left() instanceof OrNode o1 && a.right() instanceof OrNode o2) {
                    // (a | b) & (a | c) = a | (b & c)
                    if(o1.left().equals(o2.left())) yield new OrNode(o1.left(), new AndNode(o1.right(), o2.right()));
                    // (a | b) & (c | a) = a | (b & c)
                    if(o1.left().equals(o2.right())) yield new OrNode(o1.left(), new AndNode(o1.right(), o2.left()));
                    // (b | a) & (a | c) = a | (b & c)
                    if(o1.right().equals(o2.left())) yield new OrNode(o1.right(), new AndNode(o1.left(), o2.right()));
                    // (b | a) & (c | a) = a | (b & c)
                    if(o1.right().equals(o2.right())) yield new OrNode(o1.right(), new AndNode(o1.left(), o2.left()));
                }
                // Consensus
                if(a.left() instanceof OrNode o1 && a.right() instanceof AndNode a2 &&
                        a2.left() instanceof OrNode o2 && a2.right() instanceof OrNode o3) {
                    // (a | b) & (!a | c) & (b | c) = (a | b) & (!a | c)
                    if(o2.left() instanceof NegationNode n && o1.left().equals(n.child()) &&
                            o1.right().equals(o3.left()) && o2.right().equals(o3.right()))
                        yield new AndNode(o1, o2);
                    // (b | a) & (!a | c) & (b | c) = (b | a) & (!a | c)
                    if(o2.left() instanceof NegationNode n && o1.right().equals(n.child()) &&
                            o1.right().equals(o3.left()) && o2.right().equals(o3.right()))
                        yield new AndNode(o1, o2);
                    // (a | b) & (c | !a) & (b | c) = (a | b) & (!a | c)
                    if(o2.left() instanceof NegationNode n && o1.left().equals(n.child()) &&
                            o1.right().equals(o3.left()) && o2.right().equals(o3.right()))
                        yield new AndNode(o1, o2);
                    // (b | a) & (c | !a) & (b | c)
                    // (a | b) & (!a | c) & (c | b)
                    // (b | a) & (!a | c) & (c | b)
                    // (a | b) & (c | !a) & (c | b)
                    // (b | a) & (c | !a) & (c | b)
                    //TODO collapse all if statements to a single statement
                }
                if(a.left() instanceof AndNode a2 && a2.left() instanceof OrNode o1 &&
                        a2.right() instanceof OrNode o2 && a.right() instanceof OrNode o3) {
                    //TODO: literally copy content of if from above
                }
                //TODO consider commutations of the and nodes
                yield a;
            }
            case OrNode o -> {
                // a | a = a
                if(o.left().equals(o.right())) yield o.left();
                // a | !a = true; !a | a = true
                if(o.right() instanceof NegationNode n1 && o.left().equals(n1.child()) ||
                        o.left() instanceof NegationNode n2 && o.right().equals(n2.child()))
                    yield new ConstantNode(true);
                // true | a = true; false | a = a
                if(o.left() instanceof ConstantNode c) {
                    if(c.bool()) yield c;
                    else yield o.right();
                }
                // a | true = true; a | false = a
                if(o.right() instanceof ConstantNode c) {
                    if(c.bool()) yield c;
                    else yield o.left();
                }
                // (a & b) | a = a & b
                if(o.left() instanceof AndNode a && (a.left().equals(o.right()) || a.right().equals(o.right())))
                    yield a;
                // a | (a & b) = a & b
                if(o.right() instanceof AndNode a && (a.left().equals(o.left()) || a.right().equals(o.left())))
                    yield a;
                if(o.left() instanceof AndNode a1 && o.right() instanceof AndNode a2) {
                    // (a & b) | (a & c) = a & (b | c)
                    if(a1.left().equals(a2.left())) yield new AndNode(a1.left(), new OrNode(a1.right(), a2.right()));
                    // (b & a) | (a & c) = a & (b | c)
                    if(a1.right().equals(a2.left())) yield new AndNode(a1.right(), new OrNode(a1.left(), a2.right()));
                    // (a & b) | (c & a) = a & (b | c)
                    if(a1.left().equals(a2.right())) yield new AndNode(a1.left(), new OrNode(a1.right(), a2.left()));
                    // (b & a) | (c & a) = a & (b | c)
                    if(a1.right().equals(a2.right())) yield new AndNode(a1.right(), new OrNode(a1.left(), a2.left()));
                }
                yield o;
            }
            case ImplicationNode i -> {
                if(i.left() instanceof ConstantNode c) {
                    // true -> a = a; false -> a = true;
                    if(c.bool()) yield i.right();
                    else yield new ConstantNode(true);
                }
                yield i;
            }
            default -> node;
        };
    }

    private static LogicNode commute(LogicNode node) {
        return switch(node) {
            case AndNode n -> new AndNode(n.right(), n.left());
            case OrNode n -> new OrNode(n.right(), n.left());
            case DoubleImplicationNode n -> new DoubleImplicationNode(n.right(), n.left());
            default -> node;
        };
    }
}
