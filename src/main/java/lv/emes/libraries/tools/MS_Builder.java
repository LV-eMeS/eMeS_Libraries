package lv.emes.libraries.tools;

/**
 * Base class for any builder.
 *
 * @param <T> type of object to build.
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3
 */
public abstract class MS_Builder<T> {

    private T template = newTemplateObject();

    /**
     * @return reference to template object that we are building.
     */
    protected final T templ() {
        return template;
    }

    /**
     * Create new template object, which will be built by this builder.
     *
     * @return reference to newly created object.
     */
    protected abstract T newTemplateObject();

    public final T build() {
        return templ();
    }
}
