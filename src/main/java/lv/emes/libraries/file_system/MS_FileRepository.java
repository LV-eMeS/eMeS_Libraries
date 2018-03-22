package lv.emes.libraries.file_system;

import lv.emes.libraries.storage.MS_Repository;

import static lv.emes.libraries.file_system.MS_FileSystemTools.SLASH;

/**
 * A repository for files of some type that are located in some file system.
 * Single file is stored and can be found by its short name + extension.
 * Describes, how file system repository should get initialized.
 *
 * @param <T> type of items that will be stored in file repository
 *            (for example, text files, images, videos, etc.).
 * @author eMeS
 * @version 2.0.
 */
public abstract class MS_FileRepository<T> extends MS_Repository<T, String> {

    private String pathToRepository;

    public MS_FileRepository(String repositoryRoot, String repositoryCategoryName) {
        super(repositoryRoot, repositoryCategoryName);
        pathToRepository = repositoryRoot + SLASH + repositoryCategoryName + SLASH;
    }

    public MS_FileRepository(String repositoryRoot, String repositoryCategoryName, boolean autoInitialize) {
        this(repositoryRoot, repositoryCategoryName);
        if (autoInitialize)
            init();
    }

    @Override
    public boolean isInitialized() {
        return MS_FileSystemTools.directoryExists(pathToRepository);
    }

    @Override
    protected void doInitialize() {
        MS_FileSystemTools.createNewDirectory(pathToRepository);
    }

    /**
     * @return path to file repository containing 2 slashes: first - after <b>repositoryRoot</b>; and
     * second - after <b>repositoryCategoryName</b>.
     */
    public String getPathToRepository() {
        return pathToRepository;
    }
}
