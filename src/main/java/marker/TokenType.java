package marker;

import java.util.regex.Pattern;

public interface TokenType {
    Pattern CONSTANT_PATTERN = Pattern.compile("[01]|true|false");
    Pattern NAME_PATTERN = Pattern.compile("[a-z][a-z0-9]*");
    Pattern PROPERTY_PATTERN = Pattern.compile("!?[a-z][a-z0-9]*");
    Pattern STRING_PATTERN = Pattern.compile("'.*'");
    Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+");

    Pattern NOT_PATTERN = Pattern.compile("!");
    Pattern AND_PATTERN = Pattern.compile("&");
    Pattern OR_PATTERN = Pattern.compile("\\|");
    Pattern IMPLICATION_PATTERN = Pattern.compile("->");
    Pattern DOUBLE_IMPLICATION_PATTERN = Pattern.compile("<->");

    Pattern EQUALS_PATTERN = Pattern.compile("=");
    Pattern COMMA_PATTERN = Pattern.compile(",");
    Pattern UNDERSCORE_PATTERN = Pattern.compile("_");
    Pattern STAR_PATTERN = Pattern.compile("\\*");
    Pattern MINUS_PATTERN = Pattern.compile("-");

    Pattern START_PATTERN = Pattern.compile(">");
    Pattern END_PATTERN = Pattern.compile("<");

    Pattern LPAREN_PATTERN = Pattern.compile("\\(");
    Pattern RPAREN_PATTERN = Pattern.compile("\\)");
    Pattern LBRACKET_PATTERN = Pattern.compile("\\[");
    Pattern RBRACKET_PATTERN = Pattern.compile("]");
    Pattern LBRACE_PATTERN = Pattern.compile("\\{");
    Pattern RBRACE_PATTERN = Pattern.compile("}");

    Pattern EOF_PATTERN = Pattern.compile("EOF");
}
