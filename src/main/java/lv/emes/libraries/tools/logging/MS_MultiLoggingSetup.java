package lv.emes.libraries.tools.logging;

import lv.emes.libraries.tools.lists.MS_List;

import java.util.List;

/**
 * A multi logger setup. Holds information about:
 * <ul>
 * <li>repositories, in which to log events;</li>
 * <li>common logging line formatting style.</li>
 * </ul>
 * <p>Properties:
 * <ul>
 * <li>repositories</li>
 * <li>delimiterLineText</li>
 * </ul>
 * <p>Setters and getters:
 * <ul>
 * <li>withRepositories</li>
 * <li>withRepository</li>
 * <li>withDelimiterLineText</li>
 * <li>getRepositories</li>
 * <li>getDelimiterLineText</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_MultiLoggingSetup {

    private MS_List<MS_LoggingRepository> repositories;
    private String delimiterLineText = ILoggingOperations._LINE;

    public MS_List<MS_LoggingRepository> getRepositories() {
        return repositories;
    }

    public String getDelimiterLineText() {
        return delimiterLineText;
    }

    /**
     * Sets clone of presented repository list.
     * If presented repositories are <i>null</i>, value for repositories placeholder is "nulled" as well.
     *
     * @param repositories eMeS list of repositories.
     * @return reference to logging configuration itself.
     */
    @SuppressWarnings("unchecked")
    public MS_MultiLoggingSetup withRepositories(MS_List<MS_LoggingRepository> repositories) {
        this.repositories = repositories == null ? null : (MS_List<MS_LoggingRepository>) repositories.clone();
        return this;
    }

    /**
     * Sets repositories presented by making new eMeS list and adding native list's elements.
     * If presented repositories are <i>null</i>, value for repositories placeholder is "nulled" as well.
     *
     * @param repositories list of repositories.
     * @return reference to logging configuration itself.
     */
    public MS_MultiLoggingSetup withRepositories(List<MS_LoggingRepository> repositories) {
        this.repositories = repositories == null ? null : MS_List.newInstance(repositories);
        return this;
    }

    /**
     * Adds a repository to repository list. If presented repository is <i>null</i>, nothing happens.
     *
     * @param repositoryToAdd a some kind of logging repository.
     * @return reference to logging configuration itself.
     */
    public MS_MultiLoggingSetup withRepository(MS_LoggingRepository repositoryToAdd) {
        if (repositories == null) repositories = new MS_List<>();
        if (repositoryToAdd != null)
            this.repositories.add(repositoryToAdd);
        return this;
    }

    /**
     * Changes delimiter line text.
     * <p>If set to null, nothing will be logged on method's {@link ILoggingOperations#line()} call.
     * By default this text is {@link ILoggingOperations#_LINE}.
     *
     * @param delimiterLineText new line text.
     * @return reference to logging configuration itself.
     */
    public MS_MultiLoggingSetup withDelimiterLineText(String delimiterLineText) {
        this.delimiterLineText = delimiterLineText;
        return this;
    }
}
