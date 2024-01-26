package model.lexer.token;

import marker.Token;

public class ModelToken extends Token<ModelTokenType> {
    public ModelToken(ModelTokenType type, String value, int line, int col) {
        super(type, value, line, col);
    }

    public ModelToken(ModelTokenType type, int line, int col) {
        super(type, line, col);
    }

    public static ModelToken fromString(String s, int line, int col) {
        ModelTokenType type = ModelTokenType.fromString(s);

        return new ModelToken(type,
                              type == ModelTokenType.PART_TYPE ||
                                      type == ModelTokenType.STRING ||
                                      type == ModelTokenType.NAME
                                      ? s
                                      : "",
                              line,
                              col);
    }

    @Override
    public String toString() {
        return type.toString() +
                "[" + line + "|" + col + "]" +
                (type == ModelTokenType.PART_TYPE ||
                        type == ModelTokenType.STRING ||
                        type == ModelTokenType.NAME
                        ? ":" + value
                        : "");
    }
}
