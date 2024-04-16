package bool.variant.cnf.parser.cnfnode;

public sealed interface AbstractVariable permits Variable, Not {
    default boolean isPositive() {
        return this instanceof Variable;
    }

    default boolean isNegated() {
        return this instanceof Not;
    }

    default Variable getVariable() {
        return switch(this) {
            case Variable v -> v;
            case Not n -> n.child();
        };
    }

    default AbstractVariable negated() {
        return switch(this) {
            case Variable v -> new Not(v);
            case Not n -> n.child();
        };
    }
}
