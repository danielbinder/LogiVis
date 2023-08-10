package bool.interpreter;

import bool.parser.logicnode.*;
import servlet.Result;

import java.util.Arrays;
import java.util.Comparator;

public class Simplification {
    public static Result ofAsResult(String formula) {
        return new Result(of(formula).toString());
    }

    public static Result ofAsResult(LogicNode formula) {
        return new Result(of(formula).toString());
    }

    public static LogicNode of(String formula) {
        return of(LogicNode.of(formula));
    }

    public static LogicNode of(LogicNode formula) {
        LogicNode current = formula;
        LogicNode simplification = simplifyChildren(current);

        while(simplest(current, simplification) != current) {
            current = simplification;
            simplification = simplifyChildren(current);
        }

        return current;
    }

    /**
     * Returns the simplest Node. Is used for comparing simplification strategies
     * @param nodes to compare
     * @return Simplest node i.e. node with fewest sub-nodes
     */
    private static LogicNode simplest(LogicNode...nodes) {
        return Arrays.stream(nodes)
                .min(Comparator.comparingInt(Simplification::nodeCount))
                .orElse(null);
    }

    private static LogicNode simplifyChildren(LogicNode node) {
        return simplify(switch(node) {
            case NegationNode n -> new NegationNode(simplifyChildren(n.child()));
            case AndNode n -> new AndNode(simplifyChildren(n.left()), simplifyChildren(n.right()));
            case OrNode n -> new OrNode(simplifyChildren(n.left()), simplifyChildren(n.right()));
            case ImplicationNode n -> new ImplicationNode(simplifyChildren(n.left()), simplifyChildren(n.right()));
            case DoubleImplicationNode n -> new DoubleImplicationNode(simplifyChildren(n.left()), simplifyChildren(n.right()));
            default -> node;
        });
    }

    /**
     * This simplifies the node on current level
     * If you're adding something new, also add the opposite to the expand method!
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
                // DeMorgan
                if(n.child() instanceof AndNode a) {
                    // !(!a & !b) = a | b
                    if(a.left() instanceof NegationNode n1 && a.right() instanceof NegationNode n2)
                        yield new OrNode(n1.child(), n2.child());
                    // !(!a & b) = a | !b
                    if(a.left() instanceof NegationNode n1)
                        yield new OrNode(n1.child(), new NegationNode(a.right()));
                    // !(a & !b) = !a | b
                    if(a.right() instanceof NegationNode n1)
                        yield new OrNode(new NegationNode(a.left()), n1.child());
                }
                if(n.child() instanceof OrNode o) {
                    // !(!a | !b) = a & b
                    if(o.left() instanceof NegationNode n1 && o.right() instanceof NegationNode n2)
                        yield new AndNode(n1.child(), n2.child());
                    // !(!a | b) = a & !b
                    if(o.left() instanceof NegationNode n1)
                        yield new AndNode(n1.child(), new NegationNode(o.right()));
                    // (a | !b) = !a & b
                    if(o.right() instanceof NegationNode n1)
                        yield new AndNode(new NegationNode(o.left()), n1.child());
                }
                // !(!a -> !b) = !(a | !b) = !a & b
                // !(a -> !b) = !(!a | !b) = a & b
                if(n.child() instanceof ImplicationNode i && i.right() instanceof NegationNode n1)
                        yield new AndNode(i.left(), new NegationNode(i.right()));

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
                    if(c.bool()) yield a.left();
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
                if(a.left() instanceof AndNode a1 && a.right() instanceof AndNode a2) {
                    // (a & b) & (a & c) = (a & (b & c))
                    if(a1.left().equals(a2.left())) yield new AndNode(a1.left(), new AndNode(a1.right(), a2.right()));
                    // (a & b) & (c & a) = (a & (b & c))
                    if(a1.left().equals(a2.right())) yield new AndNode(a1.left(), new AndNode(a1.right(), a2.left()));
                    // (b & a) & (a & c) = (a & (b & c))
                    if(a1.right().equals(a2.left())) yield new AndNode(a1.right(), new AndNode(a1.left(), a2.right()));
                    // (b & a) & (c & a) = (a & (b & c))
                    if(a1.right().equals(a2.right())) yield new AndNode(a1.right(), new AndNode(a1.left(), a2.left()));
                }
                // Consensus
                // (a | b) & ((!a | c) & (b | c))
                if(a.left() instanceof OrNode o1 && a.right() instanceof AndNode a2 &&
                        a2.left() instanceof OrNode o2 && a2.right() instanceof OrNode o3)
                    yield getConsensus(a, o1, o2, o3);
                // ((a | b) & (!a | c)) & (b | c)
                if(a.left() instanceof AndNode a2 && a2.left() instanceof OrNode o1 &&
                        a2.right() instanceof OrNode o2 && a.right() instanceof OrNode o3)
                    yield getConsensus(a, o1, o2, o3);
                // (a | b) & ((b | c) & (!a | c))
                if(a.left() instanceof OrNode o1 && commute(a.right()) instanceof AndNode a2 &&
                        a2.left() instanceof OrNode o2 && a2.right() instanceof OrNode o3)
                    yield getConsensus(a, o1, o3, o2);
                // ((!a | c) & (a | b)) & (b | c)
                if(commute(a.left()) instanceof AndNode a2 && a2.left() instanceof OrNode o1 &&
                        a2.right() instanceof OrNode o2 && a.right() instanceof OrNode o3)
                    yield getConsensus(a, o2, o1, o3);
                // Double implications
                if(a.left() instanceof ImplicationNode i1 && a.right() instanceof ImplicationNode i2) {
                    // (a -> b) & (b -> a) = a <-> b
                    if(i1.left().equals(i2.right()) && i1.right().equals(i2.left()))
                        yield new DoubleImplicationNode(i1.left(), i1.right());
                    // (a -> b) & (a -> c) = a -> b & c
                    if(i1.left().equals(i2.left()))
                        yield new ImplicationNode(i1.left(), new AndNode(i1.right(), i2.right()));
                }
                yield a;
            }
            case OrNode o -> {
                // Idem potency
                // a | a = a
                if(o.left().equals(o.right())) yield o.left();
                // Complementation
                // a | !a = true; !a & a = true
                if(o.right() instanceof NegationNode n1 && o.left().equals(n1.child()) ||
                        o.left() instanceof NegationNode n2 && o.right().equals(n2.child()))
                    yield new ConstantNode(true);
                // Identity and Null Law
                // true | a = true; false | a = a
                if(o.left() instanceof ConstantNode c) {
                    if(c.bool()) yield c;
                    else yield o.right();
                }
                // a | true = true; a | false = a;
                if(o.right() instanceof ConstantNode c) {
                    if(c.bool()) yield c;
                    else yield o.left();
                }
                // DeMorgan
                // !a | !b = !(a & b)
                if(o.left() instanceof NegationNode n1 && o.right() instanceof NegationNode n2)
                    yield new NegationNode(new AndNode(n1.child(), n2.child()));
                // Absorption
                // a | (a & b) = a; a | (b & a) = a
                if(o.right() instanceof AndNode a && (o.left().equals(a.left()) || o.left().equals(a.right())))
                    yield o.left();
                // (a & b) | a = a; (b & a) | a = a;
                if(o.left() instanceof AndNode a && (o.right().equals(a.left()) || o.right().equals(a.right())))
                    yield o.right();
                // Simplification
                // (!a & b) | a = a | b
                if(o.left() instanceof AndNode a && a.left() instanceof NegationNode n && n.child().equals(o.right()))
                    yield new OrNode(n.child(), a.right());
                // (b & !a) | a = a | b
                if(commute(o.left()) instanceof AndNode a && a.left() instanceof NegationNode n && n.child().equals(o.right()))
                    yield new OrNode(n.child(), a.right());
                // a | (!a & b) = a | b
                if(o.right() instanceof AndNode a && a.left() instanceof NegationNode n && n.child().equals(o.left()))
                    yield new AndNode(n.child(), a.right());
                // a | (b & !a) = a | b
                if(commute(o.right()) instanceof AndNode a && a.left() instanceof NegationNode n && n.child().equals(o.left()))
                    yield new AndNode(n.child(), a.right());
                // Distributive
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
                if(o.left() instanceof OrNode o1 && o.right() instanceof OrNode o2) {
                    // (a | b) | (a | c) = (a | (b | c))
                    if(o1.left().equals(o2.left())) yield new OrNode(o1.left(), new OrNode(o1.right(), o2.right()));
                    // (a | b) | (c | a) = (a | (b | c))
                    if(o1.left().equals(o2.right())) yield new OrNode(o1.left(), new OrNode(o1.right(), o2.left()));
                    // (b | a) | (a | c) = (a | (b | c))
                    if(o1.right().equals(o2.left())) yield new OrNode(o1.right(), new OrNode(o1.left(), o2.right()));
                    // (b | a) | (c | a) = (a | (b | c))
                    if(o1.right().equals(o2.right())) yield new OrNode(o1.right(), new OrNode(o1.left(), o2.left()));
                }
                // Consensus
                // (a & b) | ((!a & c) | (b & c))
                if(o.left() instanceof AndNode a1 && o.right() instanceof OrNode o2 &&
                        o2.left() instanceof AndNode a2 && o2.right() instanceof AndNode a3)
                    yield getConsensus(o, a1, a2, a3);
                // ((a & b) | (!a & c)) | (b & c)
                if(o.left() instanceof OrNode o2 && o2.left() instanceof AndNode a1 &&
                        o2.right() instanceof AndNode a2 && o.right() instanceof AndNode a3)
                    yield getConsensus(o, a1, a2, a3);
                // (a & b) | ((b & c) | (!a & c))
                if(o.left() instanceof AndNode a1 && commute(o.right()) instanceof OrNode o2 &&
                        o2.left() instanceof AndNode a2 && o2.right() instanceof AndNode a3)
                    yield getConsensus(o, a1, a3, a2);
                // ((!a & c) | (a & b)) | (b & c)
                if(commute(o.left()) instanceof OrNode o2 && o2.left() instanceof AndNode a1 &&
                        o2.right() instanceof AndNode a2 && o.right() instanceof AndNode a3)
                    yield getConsensus(o, a2, a1, a3);
                // Double implications
                if(o.left() instanceof ImplicationNode i1 && o.right() instanceof ImplicationNode i2) {
                    // (a -> b) | (b -> a) = true
                    if(i1.left().equals(i2.right()) && i1.right().equals(i2.left()))
                        yield new ConstantNode(true);
                    // (a -> b) | (a -> c) = a -> b | c
                    if(i1.left().equals(i2.left()))
                        yield new ImplicationNode(i1.left(), new OrNode(i1.right(), i2.right()));
                }

                yield o;
            }
            case ImplicationNode i -> {
                if(i.left() instanceof ConstantNode c) {
                    // true -> a = a; false -> a = true;
                    if(c.bool()) yield i.right();
                    else yield new ConstantNode(true);
                }
                // a -> b -> c = a & b -> c; Semantic simplification
                if(i.right() instanceof ImplicationNode i2)
                    yield new ImplicationNode(new AndNode(i.left(), i2.left()), i2.right());
                yield i;
            }
            case DoubleImplicationNode di -> {
                if(di.left().equals(di.right())) yield new ConstantNode(true);

                yield di;
            }
            default -> node;
        };
    }

    /**
     * The node with the negation needs to be in the middle
     * The individual nodes can be commuted
     * @param original original node in case the consensus rule is not applicable
     * @param o1 (a | b) or (b | c)
     * @param o2 (!a | c)
     * @param o3 (a | b) or (b | c)
     * @return the applied consensus rule
     */
    private static LogicNode getConsensus(AndNode original, OrNode o1, OrNode o2, OrNode o3) {
        // (a | b) & (!a | c) & (b | c) = (a | b) & (!a | c)
        if(isConsensus(o1.left(), o1.right(), o2.left(), o2.right(), o3.left(), o3.right())) return new AndNode(o1, o2);
        OrNode temp = o1;
        o1 = o3;
        o3 = temp;
        // (b | c) & (!a | c) & (a | b)  = (a | b) & (!a | c)
        if(isConsensus(o1.left(), o1.right(), o2.left(), o2.right(), o3.left(), o3.right())) return new AndNode(o3, o2);
        return original;
    }

    /**
     * The node with the negation needs to be in the middle
     * The individual nodes can be commuted
     * @param original original node in case the consensus rule is not applicable
     * @param a1 (a & b) or (b & c)
     * @param a2 (!a & c)
     * @param a3 (a & b) or (b & c)
     * @return the applied consensus rule
     */
    private static LogicNode getConsensus(OrNode original, AndNode a1, AndNode a2, AndNode a3) {
        // (a & b) | (!a & c) | (b & c) = (a & b) | (!a & c)
        if(isConsensus(a1.left(), a1.right(), a2.left(), a2.right(), a3.left(), a3.right())) return new OrNode(a1, a2);
        AndNode temp = a1;
        a1 = a3;
        a3 = temp;
        // (b & c) | (!a & c) | (a & b)  = (a & b) | (!a & c)
        if(isConsensus(a1.left(), a1.right(), a2.left(), a2.right(), a3.left(), a3.right())) return new OrNode(a3, a2);
        return original;
    }

    /**
     * Needs to be in the following format, but the nodes themselves can be commuted
     * @param n1L Node1 left
     * @param n1R Node1 right
     * @param n2L Node2 left
     * @param n2R Node2 right
     * @param n3L Node3 left
     * @param n3R Node3 right
     * @return true if the Consensus rule can be applied
     */
    private static boolean isConsensus(LogicNode n1L, LogicNode n1R,
                                       LogicNode n2L, LogicNode n2R,
                                       LogicNode n3L, LogicNode n3R) {
        // (a | b) & (!a | c) & (b | c) = (a | b) & (!a | c)
        return (n2L instanceof NegationNode n && n1L.equals(n.child()) &&
                n1R.equals(n3L) && n2R.equals(n3R)) ||
                // (b | a) & (!a | c) & (b | c) = (b | a) & (!a | c)
                (n2L instanceof NegationNode n2 && n1R.equals(n2.child()) &&
                        n1R.equals(n3L) && n2R.equals(n3R)) ||
                // (a | b) & (c | !a) & (b | c) = (a | b) & (!a | c)
                (n2L instanceof NegationNode n3 && n1L.equals(n3.child()) &&
                        n1R.equals(n3L) && n2R.equals(n3R)) ||
                // (b | a) & (c | !a) & (b | c)
                (n2R instanceof NegationNode n4 && n1R.equals(n4.child()) &&
                        n1L.equals(n3L) && n2L.equals(n3R)) ||
                // (a | b) & (!a | c) & (c | b)
                (n2L instanceof NegationNode n5 && n1L.equals(n5.child()) &&
                        n1R.equals(n3R) && n2R.equals(n3L)) ||
                // (b | a) & (!a | c) & (c | b)
                (n2L instanceof NegationNode n6 && n1R.equals(n6.child()) &&
                        n1L.equals(n3R) && n2R.equals(n3L)) ||
                // (a | b) & (c | !a) & (c | b)
                (n2R instanceof NegationNode n7 && n1L.equals(n7.child()) &&
                        n1R.equals(n3R) && n2L.equals(n3L)) ||
                // (b | a) & (c | !a) & (c | b)
                (n2R instanceof NegationNode n8 && n1R.equals(n8.child()) &&
                        n1L.equals(n3R) && n2L.equals(n3L));
    }

    /**
     * Commutes all nodes that allow for commutation
     * @return Commuted node
     */
    private static LogicNode commute(LogicNode node) {
        return switch(node) {
            case AndNode n -> new AndNode(n.right(), n.left());
            case OrNode n -> new OrNode(n.right(), n.left());
            case DoubleImplicationNode n -> new DoubleImplicationNode(n.right(), n.left());
            default -> node;
        };
    }

    /**
     * Counts this and all sub-nodes
     * @return node count
     */
    private static int nodeCount(LogicNode node) {
        return 1 + switch(node) {
            case NegationNode n -> nodeCount(n.child());
            case AndNode n -> nodeCount(n.left()) + nodeCount(n.right());
            case OrNode n -> nodeCount(n.left()) + nodeCount(n.right());
            case DoubleImplicationNode n -> nodeCount(n.left()) + nodeCount(n.right());
            case ImplicationNode n -> nodeCount(n.left()) + nodeCount(n.right());
            default -> 0;
        };
    }
}
