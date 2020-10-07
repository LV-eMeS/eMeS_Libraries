package lv.emes.libraries.tools;

import lv.emes.libraries.tools.math.MS_ArithmeticOperation;
import lv.emes.libraries.tools.math.MS_PureIntegerOperationCalculator;
import lv.emes.libraries.tools.strings.MS_StringToken;
import lv.emes.libraries.tools.strings.MS_StringTokenExtractor;
import lv.emes.libraries.tools.strings.MS_VariableStringToken;
import lv.emes.libraries.utilities.MS_StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Pre-processor of eMeS script that performs initial script analysis and substitutions of convertible variables.
 * <p>Public methods:
 * <ul>
 *     <li>apply</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.0.
 */
public class MS_ScriptPreProcessor implements UnaryOperator<String> {

    private static final String[] VARIABLE_TYPES = {"Integer", "Variable"};
    public static final MS_StringTokenExtractor TOKEN_EXTRACTOR = new MS_StringTokenExtractor(VARIABLE_TYPES);

    /**
     * Processes string in terms of token extraction and calculations inside variable tokens.
     *
     * @param s string to process.
     * @return processed string with variables substituted with their calculated values.
     * @throws NumberFormatException if variables provided can not be parsed.
     */
    @Override
    public String apply(String s) {
        List<MS_StringToken> tokens = TOKEN_EXTRACTOR.extract(s);
        return tokens.stream()
                .map(token -> {
                    if (token instanceof MS_VariableStringToken) {
                        return extractVariableTokenContent(token);
                    } else {
                        return token.getWholeToken();
                    }
                })
                .collect(Collectors.joining());
    }

    private String extractVariableTokenContent(MS_StringToken token) {
        switch (token.getName()) {
            case "Integer":
                // some arithmetic operation like "1 + 2 * 4" will form a result string like "12"
                String variableContent = token.getMiddlePart().replace(" ", "");
                String[] numbers = variableContent.split("[+|\\-/*]");
                List<String> operations = MS_StringUtils.extractAllSubstrings(variableContent,
                        Arrays.stream(MS_ArithmeticOperation.values()).map(o -> String.valueOf(o.getOperatorAsChar())).collect(Collectors.toSet()));
                if (numbers.length < 2 || numbers.length - 1 != operations.size())
                    return token.getMiddlePart();

                try {
                    Integer first = Integer.parseInt(numbers[0]);
                    MS_PureIntegerOperationCalculator calculator = new MS_PureIntegerOperationCalculator();
                    for (int i = 1; i < numbers.length; i++) {
                        String second = numbers[i];
                        char operation = operations.get(i - 1).charAt(0);
                        first = calculator.calculate(first, Integer.parseInt(second), MS_ArithmeticOperation.from(operation));
                    }
                    return first.toString();
                } catch (NumberFormatException e) {
                    return token.getMiddlePart(); // return variable content as is if variableContent is not valid arithmetic expression
                }
            default:
                return token.getWholeToken();
        }
    }
}
