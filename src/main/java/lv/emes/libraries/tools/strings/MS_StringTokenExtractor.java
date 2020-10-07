package lv.emes.libraries.tools.strings;

import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits given string into fragments and extracts tokens from them.
 * <p>Public methods:
 * <ul>
 *     <li>extract</li>
 * </ul>
 * <p>Getters and setters:
 * <ul>
 *     <li>getTokenNames</li>
 *     <li>setTokenNames</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.0.
 */
public class MS_StringTokenExtractor {

    private Set<String> tokenNames;

    public MS_StringTokenExtractor(Set<String> tokenNames) {
        this.tokenNames = tokenNames;
    }

    public MS_StringTokenExtractor(String... tokenNames) {
        this.tokenNames = new HashSet<>(MS_CodingUtils.arrayToList(tokenNames));
    }

    public MS_StringTokenExtractor() {
    }

    /**
     * Extracts tokens from given <b>string</b>. There are two main types of tokens:
     * <ol>
     *     <li>{@link MS_VariableStringToken} in a form of <b>tokenName</b>[<b>variable content</b>]</li>
     *     <li>{@link MS_ArbitraryStringToken} in a form of any other string that is not a {@link MS_VariableStringToken}</li>
     * </ol>
     *
     * @param string non-null string that will be split into several types of tokens.
     * @return list of tokens.
     */
    public List<MS_StringToken> extract(String string) {
        List<MS_StringToken> variableTokens = new ArrayList<>();
        Pattern p = Pattern.compile("(" + String.join("|", tokenNames) + ")\\((.*?)\\)");
        Matcher m = p.matcher(Objects.requireNonNull(string, "Provided string must not be null"));
        while (m.find()) variableTokens.add(new MS_VariableStringToken(m.group(1)).withValue(m.group(2)));

        if (variableTokens.size() == 0)
            return Collections.singletonList(new MS_ArbitraryStringToken("Command text", string));

        return collectAndJoinWithRestOfTokens(string, variableTokens);
    }

    private List<MS_StringToken> collectAndJoinWithRestOfTokens(String string, List<MS_StringToken> variableTokens) {
        StringBuilder text = new StringBuilder(string);
        // Get rest of tokens around variableTokens
        int charactersCollected = 0;
        int varTokenIndex = 0;
        List<MS_StringToken> res = new ArrayList<>();
        while (charactersCollected < string.length() && varTokenIndex < variableTokens.size()) {
            MS_StringToken varToken = variableTokens.get(varTokenIndex);
            String varTokenContent = varToken.getWholeToken();
            int posOfVar = MS_StringUtils.pos(text.toString(), varTokenContent);
            String arbitraryString = text.substring(0, posOfVar);
            // If variable token is not the first one here prefix is an arbitrary string
            if (arbitraryString.length() > 0)
                res.add(new MS_ArbitraryStringToken("Command text", arbitraryString));
            res.add(varToken);
            varTokenIndex++;
            int lengthOfBothTokens = varTokenContent.length() + arbitraryString.length();
            charactersCollected += lengthOfBothTokens;
            text.delete(0, lengthOfBothTokens);
        }

        // If there are still uncollected characters assume that those are not variables
        if (charactersCollected < string.length()) {
            res.add(new MS_ArbitraryStringToken("Command text", text.toString()));
        }

        return res;
    }

    public Set<String> getTokenNames() {
        return tokenNames;
    }

    public void setTokenNames(Set<String> tokenNames) {
        this.tokenNames = tokenNames;
    }
}
