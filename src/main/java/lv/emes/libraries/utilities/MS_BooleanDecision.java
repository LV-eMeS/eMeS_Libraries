package lv.emes.libraries.utilities;

/**
 * Static class helps to make decision based on at least two boolean values.
 * It has only one method that can be used in switch block to switch between 4 or more different cases like:
 * <pre><code>switch (MS_BooleanDecision.getCase(isSomethingHappened, isSomethingElseHappened)) {
 *   case MS_BooleanDecision._FIRST: //do something if isSomethingHappened
 *     break;
 *   case MS_BooleanDecision._SECOND: //do something if isSomethingElseHappened
 *     break;
 *   case MS_BooleanDecision._BOTH: //do something if isSomethingHappened and isSomethingElseHappened at the same time
 *     break;
 *   case MS_BooleanDecision._NONE: //do something if nothing of those things happened
 *     break;
 * }
 * </code></pre>
 * Also common constants are provided to be used in decisions involving 2 and 3 boolean parameters.
 * More complicated decisions, involving more than 3 booleans returns string as pattern of "1" and "0" in order,
 * in which boolean parameters were passed, for example, <code>getCase(true, false, true, false)</code> will return
 * string "1010", which afterwards can be used in <i>switch</i> construction just like in example given above
 * with 2 booleans in decision.
 * <p>For complex decisions that is recommended to construct
 * <a href="https://en.wikipedia.org/wiki/Karnaugh_map">Karnaugh map</a>
 * before even writing boolean decision algorithm first to understand, what you are doing.
 * For example, if developer needs to write decision logic with 3 booleans involved:
 * <ol>
 * <li>isFileInFileSystemAlready;</li>
 * <li>fileExistsInDb;</li>
 * <li>thisUserIsAdmin,</li>
 * </ol>
 * and for different combinations of those boolean values corresponding actions needs to be taken, Karnaugh map
 * can look like this:
 * <pre>
 * | 000 | 100 | 010 | 001 | 110 | 011 | 101 | 111 |
 * |     |  X  |     |     |     |     |     |     | IllegalAccessException
 * |  X  |     |     |  X  |     |     |  X  |     | INSERT
 * |     |     |  X  |     |     |  X  |     |     | REPLACE
 * |     |     |     |     |  X  |     |     |  X  | UPDATE (if not admin, check ownership)
 * ________________________________________________________________________________________
 * 1. - File exists in file system (isFileInFileSystemAlready)
 * 2. - File exists in DB (fileExistsInDb)
 * 3. - This user is Admin (thisUserIsAdmin)
 * </pre>
 * When map is ready, switch statement becomes bit easier to write:
 * <pre><code>
 * switch (MS_BooleanDecision.getCase(isFileInFileSystemAlready, fileExistsInDb, thisUserIsAdmin)) {
 *   case MS_BooleanDecision._NONE_OF_3:
 *   case MS_BooleanDecision._THIRD_OF_3:
 *     doSaveRecordToDB(INSERT);
 *     break;
 *   case MS_BooleanDecision._FIRST_OF_3:
 *     throw new IllegalAccessException(); //system use files created by admin
 *   case MS_BooleanDecision._SECOND_OF_3:
 *   case MS_BooleanDecision._SECOND_AND_THIRD_OF_3:
 *     doSaveRecordToDB(REPLACE);
 *     break;
 *   case MS_BooleanDecision._FIRST_AND_THIRD_OF_3:
 *     getLogger().warning("File existed in File system, but didn't exist in DB =&gt; INSERTED in DB as well");
 *     doSaveRecordToDB(INSERT");
 *     break;
 *   case MS_BooleanDecision._ALL_3:
 *     doSaveRecordToDB(UPDATE); //Admin can overwrite any file without even asking permissions (that's, how it works here)
 *     break;
 *   case MS_BooleanDecision._FIRST_AND_SECOND_OF_3:
 *     if (thisUserIsOwner) {
 *       doSaveRecordToDB(UPDATE);
 *     } else {
 *       throw new IllegalAccessException();
 *     }
 * }
 * </code></pre>
 * <u>Note</u>: for case when First (1) and second (2) boolean values are true,
 * one more variable <b>thisUserIsOwner</b> is used. In this case it was better to not to over-complicate already
 * complex 3 level boolean decision algorithm, by turning it into 4 level boolean decision algorithm, because this
 * variable <b>thisUserIsOwner</b> participates only in one special case.
 * <p>Static methods:
 * <ul>
 * <li>getCase</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_BooleanDecision {

    //constants for 2nd level boolean decision
    public static final String _NONE = "00";
    public static final String _FIRST = "10";
    public static final String _SECOND = "01";
    public static final String _BOTH = "11";

    //constants for 3rd level boolean decision
    public static final String _NONE_OF_3 = "000";
    public static final String _FIRST_OF_3 = "100";
    public static final String _SECOND_OF_3 = "010";
    public static final String _THIRD_OF_3 = "001";
    public static final String _FIRST_AND_SECOND_OF_3 = "110";
    public static final String _SECOND_AND_THIRD_OF_3 = "011";
    public static final String _FIRST_AND_THIRD_OF_3 = "101";
    public static final String _ALL_3 = "111";

    public static String getCase(boolean first, boolean second, boolean... following) {
        StringBuilder res = new StringBuilder();
        res.append(MS_CodingUtils.booleanToChar(first));
        res.append(MS_CodingUtils.booleanToChar(second));
        for (boolean boolValue : following) {
            res.append(MS_CodingUtils.booleanToChar(boolValue));
        }
        return res.toString();
    }

    private MS_BooleanDecision() {
    }
}
