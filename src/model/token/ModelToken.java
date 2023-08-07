package model.token;

import java.util.Objects;

public class ModelToken {
    public final ModelTokenType type;
    public final String value;
    public final int line;
    public final int col;

    public ModelToken(ModelTokenType type, String value, int line, int col) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.col = col;
    }

    public ModelToken(ModelTokenType type, int line, int col) {
        this(type, "", line, col);
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
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        ModelToken ModelToken = (ModelToken) o;

        if(type != ModelToken.type) return false;
        return Objects.equals(value, ModelToken.value);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
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
