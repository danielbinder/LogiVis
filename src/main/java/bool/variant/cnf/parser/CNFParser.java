package bool.variant.cnf.parser;

import bool.variant.cnf.lexer.CNFLexer;
import bool.variant.cnf.lexer.token.CNFToken;
import bool.variant.cnf.lexer.token.CNFTokenType;
import bool.variant.cnf.parser.cnfnode.*;
import marker.Generator;
import marker.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static bool.variant.cnf.lexer.token.CNFTokenType.*;

public class CNFParser extends Parser<CNFTokenType, CNFToken, Conjunction> {
    private List<Variable> variables;

    public static Conjunction parse(String input) {
        return new CNFParser().parse(input, CNFLexer::tokenize);
    }

    @Override
    protected Conjunction start() {
        return formula();
    }

    private Conjunction formula() {
        check(P);
        check(CNF);

        if(isType(END_OF_LINE)) return Conjunction.UNSAT_CONJUNCTION;

        if(!isType(NUMBER)) throw new IllegalArgumentException("Illegal Token " + current);

        variables = Generator.generateVariableNames(Integer.parseInt(current.value)).stream()
                .map(Variable::new)
                .toList();

        advance();
        check(NUMBER);

        List<Clause> clauses = new ArrayList<>();
        while(!isType(EOF)) clauses.add(clause());

        return new Conjunction(clauses, variables, Map.of());
    }

    private Clause clause() {
        List<AbstractVariable> currentVariables = new ArrayList<>();

        while(!isType(END_OF_LINE)) {
            if(!isType(NUMBER)) throw new IllegalArgumentException("Illegal Token " + current);

            if(current.value.startsWith("-"))
                currentVariables.add(new Not(variables.get(Math.abs(Integer.parseInt(current.value)) - 1)));
            else currentVariables.add(variables.get(Integer.parseInt(current.value) - 1));

            advance();
        }

        advance();

        return new Clause(currentVariables);
    }
}
