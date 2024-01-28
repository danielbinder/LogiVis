package marker;

import util.Error;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Parser<TT extends TokenType, T extends Token<TT>, R extends ConceptRepresentation> {
    protected List<T> tokens;
    protected T current;
    protected int i;

    protected abstract R start();

    protected R parse(String input, Function<String, List<T>> tokenizer) {
        List<T> tokens = tokenizer.apply(input);

        try {
            return parse(tokens);
        } catch(IllegalArgumentException e) {
            Error.printPosition(input.split("\n")[current.line - 1], current.col);
            throw e;
        }
    }

    protected R parse(List<T> tokens) {
        this.tokens = tokens;
        i = 0;
        advance();

        return start();
    }

    /* P A T T E R N S */

    // sub { TT sub }
    protected R subRepetitionTTSub_(Supplier<R> sub, TT tokenType, BiFunction<R, R, R> nodeCreator) {
        R result = sub.get();

        while(isType(tokenType)) {
            advance();
            result = nodeCreator.apply(result, sub.get());
        }

        return result;
    }

    // [TT] sub
    protected R optionalTT_Sub(TT tokenType, Supplier<R> sub, Function<R, R> nodeCreator) {
        if(isType(tokenType)) {
            advance();
            return nodeCreator.apply(optionalTT_Sub(tokenType, sub, nodeCreator));
        }

        return sub.get();
    }

    /* H E L P E R S */

    protected void advance() {
        if(i < tokens.size()) current = tokens.get(i++);
    }

    protected void check(TT type) {
        if(isType(type)) advance();
        else throw new IllegalArgumentException("Illegal Token " + current);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    protected final boolean isType(TT... types) {
        return Arrays.stream(types)
                .anyMatch(t -> current.type == t);
    }
}
