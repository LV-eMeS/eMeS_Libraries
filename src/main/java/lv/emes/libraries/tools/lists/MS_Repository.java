package lv.emes.libraries.tools.lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A standalone repository to store some items and get them by ID when needed.
 * Physical destination of this repository is determined by <b>repositoryRoot</b> and <b>repositoryCategoryName</b>, that both are
 * passed as constructor parameters. For example, in file system repository case repository will be located in
 * some path like "[destination of all the repositories]/[<b>repositoryRoot</b>]/[<b>repositoryCategoryName</b>]/".
 * <p>Public methods:
 * <ul>
 * <li>init</li>
 * <li>put</li>
 * <li>remove</li>
 * <li>get</li>
 * <li>find</li>
 * <li>findAll</li>
 * <li>forEachItem</li>
 * <li>count</li>
 * <li>length</li>
 * <li>setBreakOngoingForLoop</li>
 * <li>getBreakOngoingForLoop</li>
 * <li>breakOngoingForLoop</li>
 * </ul>
 * <p>Methods to override:
 * <ul>
 * <li>isInitialized</li>
 * <li>doInitialize</li>
 * <li>add</li>
 * <li>doRemove</li>
 * <li>doFind</li>
 * <li>doFindAll</li>
 * <li>doGetSize</li>
 * </ul>
 * <p>Getters:
 * <ul>
 * <li>getRepositoryRoot</li>
 * <li>getRepositoryCategoryName</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public abstract class MS_Repository<T, ID> implements IStorageOperations<T, ID>, IBaseListWithItems<T, ID> {

    private String repositoryRoot;
    private String repositoryCategoryName;

    private boolean flagForLoopBreaking;

    /**
     * @param repositoryRoot         root location of repository.
     * @param repositoryCategoryName specific category which helps to located this repository in repository root.
     */
    public MS_Repository(String repositoryRoot, String repositoryCategoryName) {
        this(repositoryRoot, repositoryCategoryName, false);
    }

    /**
     * @param repositoryRoot         root location of repository.
     * @param repositoryCategoryName specific category which helps to located this repository in repository root.
     * @param autoInitialize         if true then repository will be initialized.
     */
    public MS_Repository(String repositoryRoot, String repositoryCategoryName, boolean autoInitialize) {
        this.repositoryRoot = repositoryRoot;
        this.repositoryCategoryName = repositoryCategoryName;
        if (autoInitialize)
            init();
    }

    //PROTECTED METHODS

    /**
     * Check, if repository for <b>repositoryRoot</b> and <b>repositoryCategoryName</b> is initialized.
     *
     * @return true if repository is ready and can be accessed, false otherwise
     */
    protected abstract boolean isInitialized();

    /**
     * Does all the actions to initialize repository.
     */
    protected abstract void doInitialize();

    /**
     * Adds an item to repository.
     * This method is used only in <b>put</b> method and shouldn't be used anywhere else.
     * In <b>put</b> check for existing item with such <b>identifier</b> is made and in case
     * item exists, it is removed first before adding new one.
     *
     * @param identifier an item identifier.
     * @param item       an item that will be added to repository.
     */
    protected abstract void add(ID identifier, T item);

    /**
     * Removes / deletes item from the repository and returns its value.
     *
     * @param identifier an item identifier.
     */
    protected abstract void doRemove(ID identifier);

    /**
     * Looks for specific item in repository.
     *
     * @param identifier an item identifier.
     * @return value of item or null if item couldn't be found.
     */
    protected abstract T doFind(ID identifier);

    /**
     * @return all the item values mapped by ID. If repository have no items then empty map is returned.
     * <p><u>Note</u>: that is recommended to use {@link LinkedHashMap} implementation here to preserve item order.
     */
    protected abstract Map<ID, T> doFindAll();

    /**
     * Counts exisitng items in repository.
     *
     * @return 0..count of items.
     */
    protected abstract int doGetSize();

    //PUBLIC METHODS

    /**
     * Initializes repository to operate with it. This method should be called before any other action with
     * repository data. Otherwise {@link UnsupportedOperationException} will be thrown as soon as some method,
     * that is trying to make manipulations with data, will be called.
     */
    public final void init() {
        if (!isInitialized())
            doInitialize();
    }

    @Override
    public final T put(ID identifier, T item) {
        checkAndThrowNotInitializedException();
        T previous = doFind(identifier);
        if (previous != null)
            remove(identifier);
        add(identifier, item);
        return previous;
    }

    @Override
    public final boolean remove(ID identifier) {
        checkAndThrowNotInitializedException();
        if (doFind(identifier) == null)
            return false;
        else {
            doRemove(identifier);
            return true;
        }
    }

    @Override
    public final T find(ID identifier) {
        checkAndThrowNotInitializedException();
        return doFind(identifier);
    }

    /**
     * Gathers all the existing repository items to list in specific order.
     * This method should be optimized as much as it's possible and should be working faster
     * than calling <b>find</b> method <b>size</b> times.
     *
     * @return list of existing items in repository.
     */
    public final Map<ID, T> findAll() {
        checkAndThrowNotInitializedException();
        return doFindAll();
    }

    @Override
    public final int size() {
        checkAndThrowNotInitializedException();
        return doGetSize();
    }

    @Override
    public final void forEachItem(IFuncForEachItemLoopAction<T, ID> action) {
        checkAndThrowNotInitializedException();
        Map<ID, T> allTheItems = findAll();
        if (allTheItems != null) {
            Iterator<Map.Entry<ID, T>> iter = allTheItems.entrySet().iterator();
            while (iter.hasNext() && !getBreakOngoingForLoop()) {
                Map.Entry<ID, T> entry = iter.next();
                action.doAction(entry.getValue(), entry.getKey());
            }
        }
    }

    @Override
    public final T get(ID identifier) {
        checkAndThrowNotInitializedException();
        return this.doFind(identifier);
    }

    @Override
    public void forEachItem(ID startFromIndex, IFuncForEachItemLoopAction<T, ID> action) {
        throwLoopingException();
    }

    @Override
    public void forEachItem(ID startFromIndex, ID endIndex, IFuncForEachItemLoopAction<T, ID> action) {
        throwLoopingException();
    }

    @Override
    public final int count() {
        return size();
    }

    @Override
    public final int length() {
        return size();
    }

    @Override
    public final void setBreakOngoingForLoop(boolean value) {
        flagForLoopBreaking = value;
    }

    @Override
    public final boolean getBreakOngoingForLoop() {
        return flagForLoopBreaking;
    }

    @Override
    public final void breakOngoingForLoop() {
        setBreakOngoingForLoop(true);
    }

    //PRIVATE METHODS
    private void throwLoopingException() {
        throw new UnsupportedOperationException("Repositories doesn't support looping through just a part of elements.");
    }

    private void checkAndThrowNotInitializedException() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("Cannot perform operation. Repository must be initialized first.");
        }

    }

    // *** Getters and setters

    public String getRepositoryRoot() {
        return repositoryRoot;
    }

    public String getRepositoryCategoryName() {
        return repositoryCategoryName;
    }
}
