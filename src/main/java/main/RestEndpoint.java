package main;

import java.util.regex.Matcher;

public interface RestEndpoint {
    default String preprocess(String raw) {
        return raw.replaceAll(Matcher.quoteReplacement("$"), "\n");
    }
}
