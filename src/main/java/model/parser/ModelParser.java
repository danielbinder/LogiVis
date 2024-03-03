package model.parser;

import marker.Parser;
import model.lexer.ModelLexer;
import model.lexer.token.ModelToken;
import model.lexer.token.ModelTokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.lexer.token.ModelTokenType.*;

public class ModelParser extends Parser<ModelTokenType, ModelToken, Model> {
    private Model model;
    // Map<Node1, Map<Node2, Label>>
    private final Map<String, Map<String, String>> delayedTransitions = new HashMap<>();
    private final List<String> delayedInitialStates = new ArrayList<>();
    private final List<String> delayedFinalStates = new ArrayList<>();

    public static Model parse(String input) {
        return new ModelParser().parse(input, ModelLexer::tokenize);
    }

    @Override
    protected Model start() {
        model = new Model();
        model();

        return model;
    }

    private void model() {
        if(isType(PART_TYPE)) traditional();
        else compact();

        // resolve delayed transitions since all nodes exist now
        delayedTransitions
                .forEach((startState, endStates) -> endStates
                        .forEach((endState, label) -> model.get(startState).successors.put(model.get(endState), label)));
    }

    /** T R A D I T I O N A L */

    private void traditional() {
        if(!isType(PART_TYPE)) throw new IllegalArgumentException("Illegal Token! Expected PART_TYPE, but got " + current);
        while(i < tokens.size() && isType(PART_TYPE)) {
            switch(current.value) {
                case "S" -> states();
                case "I" -> initial();
                case "T" -> transitions();
                case "F" -> final_();
                default -> throw new IllegalArgumentException("Illegal Part type " + current);
            }
        }

        delayedInitialStates.forEach(s -> model.get(s).isInitialNode = true);
        delayedFinalStates.forEach(s -> model.get(s).isFinalNode = true);

        // Misses the following case: [validInput] + '}'
        if(i < tokens.size() || !isType(EOF)) throw new IllegalArgumentException(
                "Illegal Token " + current.type + " at [" + current.line + "|" + current.col + "]");
    }

    private void states() {
        advance();
        check(EQUALS);
        check(LBRACE);

        if(!isType(NAME)) throw new IllegalArgumentException("Illegal Token " + current);
        ModelNode node = new ModelNode(current.value);
        model.add(node);
        advance();

        if(isType(ENCODING_START)) {
            node.isEncodingStartPoint = true;
            advance();
        }

        if(isType(ENCODING_END)) {
            node.isEncodingEndPoint = true;
            advance();
        }

        node.label = label();

        while(isType(COMMA)) {
            advance();

            if(!isType(NAME)) throw new IllegalArgumentException("Illegal Token " + current);
            node = new ModelNode(current.value);
            model.add(node);
            advance();

            if(isType(ENCODING_START)) {
                node.isEncodingStartPoint = true;
                advance();
            }

            if(isType(ENCODING_END)) {
                node.isEncodingEndPoint = true;
                advance();
            }

            node.label = label();
        }

        check(RBRACE);
    }

    private void initial() {
        advance();
        check(EQUALS);
        check(LBRACE);

        while(isType(NAME)) {
            delayedInitialStates.add(current.value);
            advance();

            label();        // read over initial labels, since they are only for display purposes

            if(isType(COMMA)) advance();
        }

        check(RBRACE);
    }

    private void transitions() {
        advance();
        check(EQUALS);
        check(LBRACE);

        while(isType(LPAREN)) {
            advance();
            if(!isType(NAME)) throw new IllegalArgumentException("Expected state name but got " + current);
            String startState = current.value;
            advance();
            check(COMMA);
            if(!isType(NAME)) throw new IllegalArgumentException("Expected state name but got " + current);
            String endState = current.value;
            advance();
            check(RPAREN);

            if(!delayedTransitions.containsKey(startState)) delayedTransitions.put(startState, new HashMap<>());
            delayedTransitions.get(startState).put(endState, label());

            if(isType(COMMA)) advance();
        }

        check(RBRACE);
    }

    private void final_() {
        advance();
        check(EQUALS);
        check(LBRACE);

        while(isType(NAME)) {
            delayedFinalStates.add(current.value);
            advance();

            if(isType(COMMA)) advance();
        }

        check(RBRACE);
    }

    /** C O M P A C T */

    private void compact() {
        cTransitions();
    }

    private void cTransitions() {
        cTransition();

        while(isType(COMMA)) {
            advance();
            cTransition();
        }

        if(i < tokens.size()) throw new IllegalArgumentException("Illegal input - not all tokens parsed!");
    }

    private void cTransition() {
        String node1 = cState();

        if(isType(UNIDIRECTIONAL_TRANSITION)) {
            advance();
            String label = label();
            String node2 = cState();
            if(!delayedTransitions.containsKey(node1)) delayedTransitions.put(node1, new HashMap<>());
            delayedTransitions.get(node1).put(node2, label);
        } else if(isType(BIDIRECTIONAL_TRANSITION)) {
            advance();
            String label = label();
            String node2 = cState();
            if(!delayedTransitions.containsKey(node1)) delayedTransitions.put(node1, new HashMap<>());
            delayedTransitions.get(node1).put(node2, label);
            if(!delayedTransitions.containsKey(node2)) delayedTransitions.put(node2, new HashMap<>());
            delayedTransitions.get(node2).put(node1, label);
        }

        if(isType(LBRACKET)) throw new IllegalArgumentException(
                "2 Labels in succession. If this is a transition label, move it in front of the state name");
    }

    private String cState() {
        if(!isType(NAME)) throw new IllegalArgumentException("Expected state name but got " + current);

        String stateName = current.value;
        ModelNode node = model.contains(stateName) ? model.get(stateName) : new ModelNode(stateName);
        advance();

        while(isType(UNDERSCORE, STAR, ENCODING_START, ENCODING_END)) {
            switch(current.type) {
                case UNDERSCORE -> {
                    node.isInitialNode = true;
                    advance();
                }
                case STAR -> {
                    node.isFinalNode = true;
                    advance();
                }
                case ENCODING_START -> {
                    node.isEncodingStartPoint = true;
                    advance();
                }
                case ENCODING_END -> {
                    node.isEncodingEndPoint = true;
                    advance();
                }
            }
        }

        if(isType(LBRACKET) && !node.label.isBlank())
            throw new IllegalArgumentException("State label defined in 2 locations. This may be very confusing!");
        node.label += label();

        if(!model.contains(stateName)) model.add(node);

        return stateName;
    }

    /** U N I V E R S A L */
    private String label() {
        StringBuilder labelString = new StringBuilder();

        if(isType(LBRACKET)) {
            advance();

            while(isType(NAME, STRING)) {
                labelString.append(current.value);
                advance();

                if(!isType(RBRACKET)) labelString.append(" ");
            }
            check(RBRACKET);
        }

        return labelString.toString();
    }
}
