package model;

import lexer.Lexer;
import marker.Parser;
import model.token.ModelToken;
import model.token.ModelTokenType;

import java.util.*;

import static model.token.ModelTokenType.*;

public class ModelParser implements Parser {
    private Model model;
    private List<ModelToken> modelTokens;
    private ModelToken current;
    private int i;
    // Map<Node1, Map<Node2, Label>>
    private final Map<String, Map<String, String>> delayedTransitions = new HashMap<>();

    public Model parse(String input) {
        try {
            return parse(Lexer.tokenizeModel(input));
        } catch(IllegalArgumentException e) {
            System.out.println(input.split("\n")[current.line - 1]);
            System.out.println(" ".repeat(current.col - 1) + "^");
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public Model parse(List<ModelToken> modelTokens) {
        this.modelTokens = modelTokens;
        i = 0;
        advance();

        model = new Model();
        model();

        return model;
    }

    private void model() {
        if(isType(PART_TYPE)) traditional();
        else compact();
    }

    /** T R A D I T I O N A L */

    private void traditional() {
        if(!isType(PART_TYPE)) throw new IllegalArgumentException("Illegal Token! Expected PART_TYPE, but got " + current);
        while(i < modelTokens.size() && isType(PART_TYPE)) {
            switch(current.value) {
                case "S" -> states();
                case "I" -> initial();
                case "T" -> transitions();
                case "F" -> final_();
                default -> throw new IllegalArgumentException("Illegal Part type " + current);
            }
        }
        if(i < modelTokens.size()) throw new IllegalArgumentException("Part of the input was not parsed! Stopped at " + current);
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
            String stateName= current.value;
            model.get(stateName).isInitialNodeNode = true;
            advance();

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

            model.get(startState).successors.put(model.get(endState), label());

            if(isType(COMMA)) advance();
        }

        check(RBRACE);
    }

    private void final_() {
        advance();
        check(EQUALS);
        check(LBRACE);

        while(isType(NAME)) {
            String stateName= current.value;
            model.get(stateName).isFinalNode = true;
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

        if(i < modelTokens.size() - 1) throw new IllegalArgumentException("Illegal input - not all tokens parsed!");

        // resolve delayed transitions since all nodes exist now
        delayedTransitions.entrySet().stream()
                .filter(e -> !e.getKey().endsWith("_label"))
                .forEach(e -> e.getValue()
                        .forEach((succ, label) -> model.get(e.getKey()).successors.put(model.get(succ), label)));
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
            if(!delayedTransitions.containsKey(node2)) delayedTransitions.put(node1, new HashMap<>());
            delayedTransitions.get(node2).put(node1, "");
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
                    node.isInitialNodeNode = true;
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

            while(!isType(RBRACKET)) {
                labelString.append(current.value);
                advance();

                if(!isType(RBRACKET)) labelString.append(" ");
            }
            advance();
        }

        return labelString.toString();
    }

    // H E L P E R S

    private void advance() {
        if(i < modelTokens.size()) current = modelTokens.get(i++);
    }

    private void check(ModelTokenType type) {
        if(isType(type)) advance();
        else throw new IllegalArgumentException("Illegal Token " + current);
    }

    private boolean isType(ModelTokenType...types) {
        return Arrays.stream(types)
                .anyMatch(t -> current.type == t);
    }
}
