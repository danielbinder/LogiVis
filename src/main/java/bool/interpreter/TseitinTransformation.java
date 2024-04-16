package bool.interpreter;

import bool.parser.logicnode.*;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class TseitinTransformation {
    private final List<LogicNode> substitutions = new ArrayList<>();
    private final List<Variable> variables = new ArrayList<>();
    private final Conjunction conjunction;
    private int substitutionCount = 0;

    private TseitinTransformation(LogicNode node) {
        createVariables(node);
        ActionNode substitute = substitute(node);

        List<Clause> clauses = transform();

        conjunction = new Conjunction(clauses, variables, new HashMap<>())
                .withUnitClause(findVariable(substitute.name()));

        conjunction.variables.stream()
                .filter(var -> var.name().equals("true"))
                .forEach(var -> conjunction.withUnitClause(findVariable("true")));
        conjunction.variables.stream()
                .filter(var -> var.name().equals("false"))
                .forEach(var -> conjunction.withUnitClause(findVariable("false").negated()));
    }

    public static Conjunction of(LogicNode node) {
        return new TseitinTransformation(node).conjunction;
    }

    private ActionNode substitute(LogicNode node) {
        return switch(node) {
            case DoubleImplicationNode n -> {
                variables.add(new Variable("sub" + substitutionCount));
                ActionNode substitution = new ActionNode("sub" + substitutionCount++);
                substitutions.add(new DoubleImplicationNode(substitution,
                                                            new DoubleImplicationNode(substitute(n.left()), substitute(n.right()))));
                yield substitution;
            }
            case ImplicationNode n -> {
                variables.add(new Variable("sub" + substitutionCount));
                ActionNode substitution = new ActionNode("sub" + substitutionCount++);
                substitutions.add(new DoubleImplicationNode(substitution,
                                                            new ImplicationNode(substitute(n.left()), substitute(n.right()))));
                yield substitution;
            }
            case OrNode n -> {
                variables.add(new Variable("sub" + substitutionCount));
                ActionNode substitution = new ActionNode("sub" + substitutionCount++);
                substitutions.add(new DoubleImplicationNode(substitution,
                                                            new OrNode(substitute(n.left()), substitute(n.right()))));
                yield substitution;
            }
            case AndNode n -> {
                variables.add(new Variable("sub" + substitutionCount));
                ActionNode substitution = new ActionNode("sub" + substitutionCount++);
                substitutions.add(new DoubleImplicationNode(substitution,
                                                            new AndNode(substitute(n.left()), substitute(n.right()))));
                yield substitution;
            }
            case NegationNode n -> {
                variables.add(new Variable("sub" + substitutionCount));
                ActionNode substitution = new ActionNode("sub" + substitutionCount++);
                substitutions.add(new DoubleImplicationNode(substitution,
                                                            new NegationNode(substitute(n.child()))));
                yield substitution;
            }
            case ActionNode n -> n;
            case ConstantNode n -> {
                if(variables.stream().noneMatch(var -> var.name().equals(n.bool() ? "true" : "false")))
                    variables.add(new Variable(n.bool() ? "true" : "false"));
                yield new ActionNode(n.bool() ? "true" : "false");
            }
        };
    }

    private List<Clause> transform() {
        return substitutions.stream()
                .flatMap(substitution -> transform(substitution).stream())
                .toList();
    }

    private List<Clause> transform(LogicNode substitution) {
        List<Clause> clauses = new ArrayList<>();

        if(substitution instanceof DoubleImplicationNode di && di.left() instanceof ActionNode substitute) {
            Variable sub = findVariable(substitute.name());
            switch(di.right()) {
                // sub <-> (a <-> b) => (!sub | !a | b) & (!sub | a | !b) & (sub | !a | !b) & (sub | a | b)
                case DoubleImplicationNode n -> {
                    Variable left = findVariable(((ActionNode) n.left()).name());
                    Variable right = findVariable(((ActionNode) n.right()).name());

                    clauses.add(new Clause(sub.negated(), left.negated(), right));
                    clauses.add(new Clause(sub.negated(), left, right.negated()));
                    clauses.add(new Clause(sub, left.negated(), right.negated()));
                    clauses.add(new Clause(sub, left, right));
                }
                // sub <-> (a -> b) => (!sub | !a | b) & (sub | a) & (sub | !b)
                case ImplicationNode n -> {
                    Variable left = findVariable(((ActionNode) n.left()).name());
                    Variable right = findVariable(((ActionNode) n.right()).name());

                    clauses.add(new Clause(sub.negated(), left.negated(), right));
                    clauses.add(new Clause(sub, left));
                    clauses.add(new Clause(sub, right.negated()));
                }
                // sub <-> (a | b) => (!sub | a | b) & (sub | !a) & (sub | !b)
                case OrNode n -> {
                    Variable left = findVariable(((ActionNode) n.left()).name());
                    Variable right = findVariable(((ActionNode) n.right()).name());

                    clauses.add(new Clause(sub.negated(), left, right));
                    clauses.add(new Clause(sub, left.negated()));
                    clauses.add(new Clause(sub, right.negated()));
                }
                // sub <-> (a & b) => (!sub | a) & (!sub | b) & (!sub | !a | !b)
                case AndNode n -> {
                    Variable left = findVariable(((ActionNode) n.left()).name());
                    Variable right = findVariable(((ActionNode) n.right()).name());

                    clauses.add(new Clause(sub.negated(), left));
                    clauses.add(new Clause(sub.negated(), right));
                    clauses.add(new Clause(sub, left.negated(), right.negated()));
                }
                // sub <-> (!a) => (sub | a) & (!sub | !a)
                case NegationNode n -> {
                    Variable child = findVariable(((ActionNode) n.child()).name());

                    clauses.add(new Clause(sub, child));
                    clauses.add(new Clause(sub.negated(), child.negated()));
                }
                // sub <-> a => (sub | !a) & (!sub | a)
                case ActionNode n -> {
                    Variable var = findVariable(n.name());

                    clauses.add(new Clause(sub, var.negated()));
                    clauses.add(new Clause(sub.negated(), var));
                }
                case ConstantNode ignored -> throw new IllegalStateException("There should not be any constant node at this point!");
            }
        }

        return clauses;
    }

    private void createVariables(LogicNode node) {
        switch(node) {
            case DoubleImplicationNode n -> {
                createVariables(n.left());
                createVariables(n.right());
            }
            case ImplicationNode n -> {
                createVariables(n.left());
                createVariables(n.right());
            }
            case OrNode n -> {
                createVariables(n.left());
                createVariables(n.right());
            }
            case AndNode n -> {
                createVariables(n.left());
                createVariables(n.right());
            }
            case NegationNode n -> createVariables(n.child());
            case ActionNode n -> {
                if(variables.stream().noneMatch(var -> var.name().equals(n.name())))
                    variables.add(new Variable(n.name()));
            }
            case ConstantNode n -> {
                if(variables.stream().noneMatch(var -> var.name().equals(String.valueOf(n.bool()))))
                    variables.add(new Variable(String.valueOf(n.bool())));
            }
        }
    }

    private Variable findVariable(String name) {
        return variables.stream()
                .filter(var -> var.name().equals(name))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }
}
