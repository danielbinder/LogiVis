package interpreter;

import parser.logicnode.*;

import java.util.Arrays;
import java.util.Comparator;

public class Simplification {
    public static LogicNode of(LogicNode node) {
        return simplestFromTree(node);
    }

    private static LogicNode simplestFromTree(LogicNode node) {
        return switch(node) {
            case NegationNode n -> simplest(node,
                                            new NegationNode(new NegationNode(node)),
                                            transform(node),
                                            applyDeMorgan(node),
                                            new NegationNode(simplestFromTree(n.child())));
            case AndNode n -> simplest(node,
                                       new NegationNode(new NegationNode(node)),
                                       transform(node),
                                       applyDeMorgan(node),
                                       commute(node),
                                       new AndNode(simplestFromTree(n.left()), n.right()),
                                       new AndNode(n.left(), simplestFromTree(n.right())),
                                       new AndNode(simplestFromTree(n.left()), simplestFromTree(n.right())));
            case OrNode n -> simplest(node,
                                      new NegationNode(new NegationNode(node)),
                                      transform(node),
                                      applyDeMorgan(node),
                                      commute(node),
                                      new OrNode(simplestFromTree(n.left()), n.right()),
                                      new OrNode(n.left(), simplestFromTree(n.right())),
                                      new OrNode(simplestFromTree(n.left()), simplestFromTree(n.right())));
            case DoubleImplicationNode n -> simplest(node,
                                                     new NegationNode(new NegationNode(node)),
                                                     transform(node),
                                                     commute(node),
                                                     new DoubleImplicationNode(simplestFromTree(n.left()), n.right()),
                                                     new DoubleImplicationNode(n.left(), simplestFromTree(n.right())),
                                                     new DoubleImplicationNode(simplestFromTree(n.left()),
                                                                               simplestFromTree(n.right())));
            case ImplicationNode n -> simplest(node,
                                               new NegationNode(new NegationNode(node)),
                                               transform(node),
                                               new ImplicationNode(simplestFromTree(n.left()), n.right()),
                                               new ImplicationNode(n.left(), simplestFromTree(n.right())),
                                               new ImplicationNode(simplestFromTree(n.left()), simplestFromTree(n.right())));
            default -> node;
        };
    }

    private static LogicNode simplify(LogicNode node) {
        return switch(node) {
            case NegationNode n -> {
                // !true = false; !false = true
                if(n.child() instanceof ConstantNode c) yield new ConstantNode(!c.bool());
                // !!a = a
                if(n.child() instanceof NegationNode n2) yield n2.child();
                yield n;
            }
            case AndNode a -> {
                // a & a = a
                if(a.left().equals(a.right())) yield a.left();
                // a & !a = false
                if(a.right() instanceof NegationNode n && a.left().equals(n.child())) yield new ConstantNode(false);
                // true & a = a; false & a = false
                if(a.left() instanceof ConstantNode c) {
                    if(c.bool()) yield a.right();
                    else yield c;
                }
                // (a | b) & a = a
                if(a.left() instanceof OrNode o && (a.right().equals(o.left()) || a.right().equals(o.right())))
                    yield a.right();
                if(commute(a.left()) instanceof OrNode o && (a.right().equals(o.left()) || a.right().equals(o.right())))
                    yield a.right();
                // (a | b) & (a | c) = a | (b & c)
                if(a.left() instanceof OrNode o1 && a.right() instanceof OrNode o2 && o1.left().equals(o2.left()))
                    yield new OrNode(o1.left(), new AndNode(o1.right(), o2.right()));
                if(commute(a.left()) instanceof OrNode o1 && a.right() instanceof OrNode o2 && o1.left().equals(o2.left()))
                    yield new OrNode(o1.left(), new AndNode(o1.right(), o2.right()));
                if(a.left() instanceof OrNode o1 && commute(a.right()) instanceof OrNode o2 && o1.left().equals(o2.left()))
                    yield new OrNode(o1.left(), new AndNode(o1.right(), o2.right()));
                yield a;
            }
            case OrNode o -> {
                // a | a = a
                if(o.left().equals(o.right())) yield o.left();
                // a | !a = true
                if(o.right() instanceof NegationNode n && o.left().equals(n.child())) yield new ConstantNode(true);
                // true | a = true; false | a = a
                if(o.left() instanceof ConstantNode c) {
                    if(c.bool()) yield c;
                    else yield o.right();
                }
                // (a & b) | a = a | b
                if(o.left() instanceof AndNode a && (o.right().equals(a.left())))
                    yield new OrNode(o.right(), a.right());
                if(commute(o.left()) instanceof AndNode a && (o.right().equals(a.left())))
                    yield new OrNode(o.right(), a.right());
                // (a & b) | (a & c) = a & (b | c)
                if(o.left() instanceof AndNode a1 && o.right() instanceof AndNode a2 && a1.left().equals(a2.left()))
                    yield new AndNode(a1.left(), new OrNode(a1.right(), a2.right()));
                if(commute(o.left()) instanceof AndNode a1 && o.right() instanceof AndNode a2 && a1.left().equals(a2.left()))
                    yield new AndNode(a1.left(), new OrNode(a1.right(), a2.right()));
                if(o.left() instanceof AndNode a1 && commute(o.right()) instanceof AndNode a2 && a1.left().equals(a2.left()))
                    yield new AndNode(a1.left(), new OrNode(a1.right(), a2.right()));
                yield o;
            }
            case ImplicationNode i -> {
                // false -> a = true
                if(i.left() instanceof ConstantNode c && !c.bool()) yield new ConstantNode(true);
                yield i;
            }
            default -> node;
        };
    }

    private static LogicNode simplest(LogicNode...nodes) {
        return Arrays.stream(nodes)
                .map(Simplification::simplify)
                .min(Comparator.comparingInt(Simplification::nodeCount))
                .orElse(null);
    }

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

    private static LogicNode applyDeMorgan(LogicNode node) {
        return switch(node) {
            case NegationNode n -> {
                // !(!a & !b) = a | b
                if(n.child() instanceof AndNode a && a.left() instanceof NegationNode n1 && a.right() instanceof NegationNode n2)
                    yield new OrNode(n1.child(), n2.child());
                // !(!a & b) = a | !b
                if(n.child() instanceof AndNode a && a.left() instanceof NegationNode n1)
                    yield new OrNode(n1.child(), new NegationNode(a.right()));
                // !(a & !b) = a | !b
                if(commute(n.child()) instanceof AndNode a && a.left() instanceof NegationNode n1)
                    yield new OrNode(n1.child(), new NegationNode(a.right()));
                // !(a & b) = !a | !b
                if(n.child() instanceof AndNode a) yield new OrNode(new NegationNode(a.left()), new NegationNode(a.right()));
                // !(!a | !b) = a & b
                if(n.child() instanceof OrNode o && o.left() instanceof NegationNode n1 && o.right() instanceof NegationNode n2)
                    yield new AndNode(n1.child(), n2.child());
                // !(!a | b) = a & !b
                if(n.child() instanceof OrNode o && o.left() instanceof NegationNode n1)
                    yield new AndNode(n1.child(), new NegationNode(o.right()));
                // !(!a | b) = a & !b
                if(commute(n.child()) instanceof OrNode o && o.left() instanceof NegationNode n1)
                    yield new AndNode(n1.child(), new NegationNode(o.right()));
                // !(a | b) = !a & !b
                if(n.child() instanceof OrNode o) yield new AndNode(new NegationNode(o.left()), new NegationNode(o.right()));
                yield node;
            }
            case AndNode a -> {
                // a & b = !(!a | !b)
                yield new NegationNode(new OrNode(new NegationNode(a.left()), new NegationNode(a.right())));
            }
            case OrNode o -> {
                // a | b = !(!a & !b)
                yield new NegationNode(new AndNode(new NegationNode(o.left()), new NegationNode(o.right())));
            }
            default -> node;
        };
    }

    private static LogicNode transform(LogicNode node) {
        return switch(node) {
            case NegationNode n -> {
                if(n.child() instanceof AndNode a) yield new AndNode(new NegationNode(a.left()), new NegationNode(a.right()));
                if(n.child() instanceof OrNode o) yield new OrNode(new NegationNode(o.left()), new NegationNode(o.right()));
                yield n;
            }
            case AndNode n -> {
                if(n.left() instanceof NegationNode n1 && n.right() instanceof NegationNode n2)
                    yield new NegationNode(new AndNode(n1.child(), n2.child()));
                if(n.left() instanceof AndNode a) yield new AndNode(new AndNode(n.left(), a.left()), n.right());
                yield n;
            }
            case OrNode n -> {
                if(n.left() instanceof NegationNode n1 && n.right() instanceof NegationNode n2)
                    yield new NegationNode(new OrNode(n1.child(), n2.child()));
                if(n.left() instanceof OrNode a) yield new OrNode(new OrNode(n.left(), a.left()), n.right());
                yield n;
            }
            case ImplicationNode n -> new OrNode(new NegationNode(n.left()), n.right());
            case DoubleImplicationNode n -> new AndNode(new ImplicationNode(n.left(), n.right()),
                                                        new ImplicationNode(n.right(), n.left()));
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
