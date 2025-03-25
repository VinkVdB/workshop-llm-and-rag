package infosupport.be.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for:
 * Parsing user input expressions like "king - (man - woman)".
 * Performing the embedding arithmetic with the help of {@link EmbeddingManager}.
 * Exposing a method to parse & compute expressions, returning top-k similar results.
 * Example of usage: {@code calculate("king - (man - woman)"} returns a list of
 * the top-5 similar terms (excluding "king", "man", and "woman").
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingCalculator {

    private final EmbeddingManager embeddingManager;

    /**
     * Main entry point for computing the top-5 nearest terms for a given expression.
     * Steps:
     * Validate the raw input to ensure it contains only allowed characters.
     * Parse the expression and compute the resulting {@link EmbeddingVector}.
     * Gather terms used in the expression (to exclude them in similarity calculation).
     * Compute top-5 similar terms using {@link EmbeddingManager#findTopKClosest(EmbeddingVector, int, List)}.
     *
     * @param input The arithmetic expression (e.g., "king - (man - woman)").
     * @return A list of up to 5 entries (term -> similarity), or {@code null} if expression invalid.
     * @throws IllegalArgumentException if the input contains disallowed characters.
     */
    public List<Map.Entry<String, Double>> calculate(String input) {
        // 1) Validate the input
        validateInput(input);

        // 2) Parse expression and compute the resulting EmbeddingVector
        final EmbeddingVector result = parseAndComputeExpression(input);
        if (result == null) {
            throw new IllegalArgumentException("Could not parse expression: [" + input + "]");
        }

        // 3) Collect distinct terms used in the expression (for excluding them from results)
        final Set<String> usedTerms = extractDistinctTerms(input);

        // 4) Find the top-5 similar terms
        return embeddingManager.findTopKClosest(result, 5, new ArrayList<>(usedTerms));
    }

    /**
     * Parses the expression and returns an {@link EmbeddingVector} representing the result
     * of any embedding arithmetic (e.g., king - (man - woman)).
     */
    private EmbeddingVector parseAndComputeExpression(String input) {
        final List<String> tokens = tokenize(input);
        return evaluateExpression(tokens);
    }

    /**
     * Splits the input string into tokens while respecting quoted substrings.
     * Regex captures:
     * Quoted substrings (group(1) if "some text").
     * Parentheses {@code ( )}.
     * Plus or minus signs {@code +, -}.
     * Any sequence of non-whitespace that isn't +, -, or parentheses.
     */
    private List<String> tokenize(String input) {
        final List<String> result = new ArrayList<>();
        final Pattern pattern = Pattern.compile("\"([^\"]*)\"|\\(|\\)|\\+|-|[^\\s+\\-()]+");
        final Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            // If group(1) is non-null, it's a quoted term:
            if (matcher.group(1) != null) {
                // e.g. "some phrase"
                result.add(matcher.group(1).trim());
            } else {
                // Otherwise, it's a normal token (operator, parenthesis, or single word)
                result.add(matcher.group().trim());
            }
        }
        return result;
    }

    /**
     * Checks if the raw input contains only allowed characters:
     * letters, digits, quotes, plus, minus, parentheses, underscores, and whitespace.
     */
    private void validateInput(String input) {
        final String allowedCharsRegex = "^[a-zA-Z0-9\"+\\-()_\\s]*$";
        if (!input.matches(allowedCharsRegex)) {
            throw new IllegalArgumentException(
                    "Input contains illegal characters. Only letters, digits, \"+-()_\", quotes, and spaces are allowed."
            );
        }
    }

    /**
     * Evaluates the list of tokens (already produced by {@link #tokenize(String)}).
     * Uses a simple recursive descent parser to handle parentheses and +/-.
     */
    private EmbeddingVector evaluateExpression(List<String> tokens) {
        return new Parser(tokens).parseExpression();
    }

    /**
     * Extracts distinct terms from the expression, ignoring operators and parentheses.
     */
    private Set<String> extractDistinctTerms(String input) {
        return tokenize(input).stream()
                .filter(token -> !Set.of("+", "-", "(", ")").contains(token))
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * Helper method to fetch or compute the embedding for a single term.
     */
    private EmbeddingVector embed(String term) {
        return embeddingManager.valueOf(term);
    }

    /**
     * Inner class implementing a recursive descent parser over the token list.
     */
    private class Parser {
        private final List<String> tokens;
        private int position = 0;

        Parser(List<String> tokens) {
            this.tokens = Objects.requireNonNull(tokens);
        }

        /**
         * expression := term { ( '+' | '-' ) term }
         */
        EmbeddingVector parseExpression() {
            EmbeddingVector current = parseTerm();
            while (hasNext()) {
                final String op = peek();
                if ("+".equals(op) || "-".equals(op)) {
                    nextToken(); // consume '+' or '-'
                    final EmbeddingVector rhs = parseTerm();
                    current = "+".equals(op) ? current.plus(rhs) : current.minus(rhs);
                } else {
                    // Not a +/-, so we're done with the expression
                    break;
                }
            }
            return current;
        }

        /**
         * term := factor | '(' expression ')'
         */
        EmbeddingVector parseTerm() {
            final String token = peek();
            if ("(".equals(token)) {
                // sub-expression in parentheses
                nextToken(); // consume '('
                final EmbeddingVector subExpr = parseExpression();

                // Expect a closing ')'
                if (!hasNext() || !")".equals(peek())) {
                    throw new IllegalArgumentException("Missing closing parenthesis in expression.");
                }
                nextToken(); // consume ')'
                return subExpr;
            }
            return parseFactor();
        }

        /**
         * factor := single token (quoted or unquoted).
         * At this level, we assume it's a term to embed.
         */
        EmbeddingVector parseFactor() {
            if (!hasNext()) {
                throw new IllegalArgumentException("Unexpected end of expression.");
            }
            final String term = nextToken();
            return embed(term);
        }

        private String peek() {
            return tokens.get(position);
        }

        private String nextToken() {
            return tokens.get(position++);
        }

        private boolean hasNext() {
            return position < tokens.size();
        }
    }
}
