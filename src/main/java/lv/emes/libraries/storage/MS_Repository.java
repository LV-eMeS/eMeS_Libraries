package lv.emes.libraries.storage;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.lists.IFuncForEachItemLoopAction;
import lv.emes.libraries.tools.lists.MS_IterableListWithItems;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A standalone repository to store some items and get them by ID when needed.
 * Physical destination of this repository is determined by <b>repositoryRoot</b> and <b>repositoryCategoryName</b>, that both are
 * passed as constructor parameters. For example, in file system repository case repository will be located in
 * some path like "[destination of all the repositories]/[<b>repositoryRoot</b>]/[<b>repositoryCategoryName</b>]/".
 * <p>Public methods:
 * <ul>
 * <li>init</li>
 * <li>add</li>
 * <li>put</li>
 * <li>remove</li>
 * <li>get</li>
 * <li>find</li>
 * <li>findAll</li>
 * <li>findPage</li>
 * <li>forEachItem</li>
 * <li>removeAll</li>
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
 * <li>doAdd</li>
 * <li>doRemove</li>
 * <li>doFind</li>
 * <li>doFindAll</li>
 * <li>doFindPage</li>
 * <li>doGetSize</li>
 * <li>doRemoveAll</li>
 * </ul>
 * <p>Getters:
 * <ul>
 * <li>getRepositoryRoot</li>
 * <li>getRepositoryCategoryName</li>
 * </ul>
 *
 * @param <T>  type of items.
 * @param <ID> type of item identifiers.
 * @author eMeS
 * @version 2.1.
 */
public abstract class MS_Repository<T, ID> implements MS_IRepositoryOperations<T, ID>, MS_IterableListWithItems<T, ID> {

    private String repositoryRoot;
    private String repositoryCategoryName;
    private boolean flagForLoopBreaking;

    /**
     * Constructs instance of repository without initialization.
     * This constructor calls {@link MS_Repository#MS_Repository(String, String, boolean)} with
     * <b>autoInitialize</b> = false.
     *
     * @param repositoryRoot         root location of repository.
     * @param repositoryCategoryName specific category which helps to locate this repository in repository root.
     */
    public MS_Repository(String repositoryRoot, String repositoryCategoryName) {
        this(repositoryRoot, repositoryCategoryName, false);
    }

    /**
     * Constructs instance of repository and initializes it if <b>autoInitialize</b> is set to true.
     *
     * @param repositoryRoot         root location of repository.
     * @param repositoryCategoryName specific category which helps to locate this repository in repository root.
     * @param autoInitialize         if true then repository will be initialized right after construction.
     */
    public MS_Repository(String repositoryRoot, String repositoryCategoryName, boolean autoInitialize) {
        this.repositoryRoot = repositoryRoot;
        this.repositoryCategoryName = repositoryCategoryName;
        if (autoInitialize) init();
    }

    /**
     * Check, if repository for <b>repositoryRoot</b> and <b>repositoryCategoryName</b> is initialized.
     *
     * @return true if repository is ready and can be accessed, false otherwise
     */
    public abstract boolean isInitialized();

    //PROTECTED METHODS

    /**
     * Does all the actions to initialize repository.
     */
    protected abstract void doInitialize();

    /**
     * Adds an item to repository.
     * This method is used only in <b>add</b> and <b>put</b> methods and shouldn't be used anywhere else.
     * <p>In <b>add</b> check for existing item with such <b>identifier</b> is made and in case
     * item exists, do nothing!
     * <p>In <b>put</b> check for existing item with such <b>identifier</b> is made and in case
     * item exists, remove first before adding new one!
     *
     * @param identifier an item identifier.
     * @param item       an item that will be added to repository.
     */
    protected abstract void doAdd(ID identifier, T item);

    /**
     * Removes / deletes item from the repository.
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
     * <p><u>Note</u>: that is recommended to use {@link LinkedHashMap} or {@link TreeMap} implementation here to preserve item order.
     */
    protected abstract Map<ID, T> doFindAll();

    /**
     * Standard implementation of page lookup.
     * <p><u>Warning</u>: this is only imitation of how actually this operation should be performed, as this implementation uses
     * doFindAll() method and takes only requested page entries from it, which in the end does not give any performance
     * gain. It's recommended to have proper repository specific implementation.
     *
     * @param page requested page (0 and 1 are the same).
     * @param size item count in this page.
     * @return specified page of item values mapped by ID. If repository have no items then empty map is returned.
     * <p><u>Note</u>: that is recommended to use {@link LinkedHashMap} or {@link TreeMap} implementation here to preserve item order.
     */
    protected Map<ID, T> doFindPage(int page, int size) {
        Map<ID, T> allItems = doFindAll();
        int totalPossiblePages = allItems.size() / size;
        int itemsInLastPage = allItems.size() - size * totalPossiblePages;
        if (itemsInLastPage > 0) totalPossiblePages++;

        Map<ID, T> res;
        if (page < totalPossiblePages) { //we will get full page with "size" count of items
            res = new LinkedHashMap<>(size);
        } else { //we will get, what's left from last page
            res = new LinkedHashMap<>(itemsInLastPage);
        }

        final int firstItemInPageIndex = (page - 1) * size;
        int i = 0;
        int itemsCollected = 0;
        for (Map.Entry<ID, T> itemEntry : allItems.entrySet()) {
            if (i++ < firstItemInPageIndex) continue;
            res.put(itemEntry.getKey(), itemEntry.getValue());
            if (++itemsCollected == size) break;
        }
        return res;
    }

    /**
     * Removes all the items from the repository.
     * By default <b>doRemove</b> calls are made by this method.
     * If more effective algorithm can be performed to remove all the items, override this method (without calling super)!
     */
    protected void doRemoveAll() {
        doFindAll().forEach((id, item) -> doRemove(id));
    }

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
    public void add(ID identifier, T item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        T previous = doFind(identifier);
        if (previous == null)
            doAdd(identifier, item);
    }

    @Override
    public T put(ID identifier, T item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        T previous = doFind(identifier);
        if (previous != null)
            remove(identifier);
        add(identifier, item);
        return previous;
    }

    @Override
    public void remove(ID identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        if (doFind(identifier) != null) {
            doRemove(identifier);
        }
    }

    @Override
    public T find(ID identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        return doFind(identifier);
    }

    public Map<ID, T> findAll() throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        return doFindAll();
    }

    public Map<ID, T> findPage(int page, int size) throws MS_BadSetupException, UnsupportedOperationException, MS_RepositoryDataExchangeException {
        if (page < 0 || size < 1)
            throw new MS_BadSetupException("Invalid page request. Page number must be non-negative, and size of page must be greater than 0");
        checkAndThrowNotInitializedException();
        return doFindPage(page == 0 ? 1 : page, size);
    }

    @Override
    public void removeAll() throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        doRemoveAll();
    }

    @Override
    public int size() {
        checkAndThrowNotInitializedException();
        return doGetSize();
    }

    @Override
    public void forEachItem(IFuncForEachItemLoopAction<T, ID> action) {
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

    /**
     * Alias to {@link MS_Repository#find(Object)}.
     * Looks for item with presented identifier <b>identifier</b>.
     *
     * @param identifier an item identifier.
     * @return an existing item or null if such item couldn't be found in the repository.
     * @throws UnsupportedOperationException      if this operation is not supported for this kind of repository or some
     *                                            specific conditions in order to operate within this repository isn't met.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    @Override
    public T get(ID identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        return doFind(identifier);
    }

    /**
     * <u><b>Unsupported for any of repositories.</b></u>
     *
     * @param startFromIndex ignored.
     * @param action         ignored.
     */
    @Override
    public void forEachItem(ID startFromIndex, IFuncForEachItemLoopAction<T, ID> action) {
        throwLoopingException();
    }

    /**
     * <u><b>Unsupported for any of repositories.</b></u>
     *
     * @param startFromIndex ignored.
     * @param endIndex       ignored.
     * @param action         ignored.
     */
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

    //*** Private and protected methods ***

    private void throwLoopingException() {
        throw new UnsupportedOperationException("Repositories doesn't support looping through just a part of elements.");
    }

    protected final void checkAndThrowNotInitializedException() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException(repositoryNotInitializedErrorMessage());
        }
    }

    /**
     * Override this method to change error message on {@link MS_Repository#checkAndThrowNotInitializedException()}.
     *
     * @return error message that will be printed when some repository operation is requested while repository is uninitialized.
     */
    protected String repositoryNotInitializedErrorMessage() {
        return "Cannot perform operation. Repository must be initialized first.";
    }

    protected void throwUnsupportedWhenNoImplementationNeeded() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot perform operation. This repository doesn't support it.");
    }

    // *** Getters and setters

    public String getRepositoryRoot() {
        return repositoryRoot;
    }

    public String getRepositoryCategoryName() {
        return repositoryCategoryName;
    }
}
