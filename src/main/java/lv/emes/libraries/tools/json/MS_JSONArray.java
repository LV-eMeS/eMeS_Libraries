package lv.emes.libraries.tools.json;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_CodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;

/**
 * JSON array extended from {@link JSONArray} that provides additional utilities.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.2.
 */
public class MS_JSONArray extends JSONArray {

    //*** Constructors ***

    /**
     * Creates {@link MS_JSONArray} from org.json array by using its elements sequentially.
     *
     * @param arr JSON.org array.
     */
    public MS_JSONArray(JSONArray arr) {
        this();
        if (!MS_JSONUtils.isEmpty(arr)) {
            arr.forEach(el -> {
                if (el == null) {
                    this.put(MS_JSONObject.NULL);
                } else if (MS_JSONUtils.isOrgJsonObject(el)) {
                    this.put(new MS_JSONObject((org.json.JSONObject) el));
                } else if (MS_JSONUtils.isOrgJsonArray(el)) {
                    this.put(new MS_JSONArray((JSONArray) el));
                } else {
                    this.put(el);
                }
            });
        }
    }

    /**
     * Creates new JSON array, which consists of JSON objects in same sequence as given list of <b>objects</b>.
     *
     * @param objects list of JSON objects to be added to constructed JSON array.
     */
    public MS_JSONArray(List<? extends org.json.JSONObject> objects) {
        this();
        if (objects != null && objects.size() != 0)
            objects.forEach(this::put);
    }

    /**
     * Creates new array which consists of given array list combined sequentially.
     * In case list is null or empty, an empty array is created.
     *
     * @param arrays list of JSON arrays, which needs to be combined into one.
     */
    public static MS_JSONArray newJSONArray(List<? extends JSONArray> arrays) {
        MS_JSONArray res = new MS_JSONArray();
        if (!MS_CodingUtils.isEmpty(arrays)) {
            arrays.forEach(res::concat);
        }
        return res;
    }

    //*** MS_JSONArray constructors ***

    public MS_JSONArray() {
        super();
        upgradeOrgJsonElementsToExtendedOnes();
    }

    public MS_JSONArray(JSONTokener x) throws JSONException {
        super(x);
        upgradeOrgJsonElementsToExtendedOnes();
    }

    public MS_JSONArray(String source) throws JSONException {
        super(source);
        upgradeOrgJsonElementsToExtendedOnes();
    }

    public MS_JSONArray(Collection<?> collection) {
        super(collection);
        upgradeOrgJsonElementsToExtendedOnes();
    }

    public MS_JSONArray(Object array) throws JSONException {
        super(array);
        upgradeOrgJsonElementsToExtendedOnes();
    }

    //method to convert org.json array elements to extended ones
    private void upgradeOrgJsonElementsToExtendedOnes() {
        for (int i = 0; i < this.length(); i++) {
            Object element = super.get(i);
            if (MS_JSONUtils.isOrgJsonObject(element)) { //if this array has redundant objects
                MS_JSONObject newObject = new MS_JSONObject((org.json.JSONObject) element);
                this.put(i, newObject); //rewrite element
            } else if (MS_JSONUtils.isOrgJsonArray(element)) { //if this array has redundant arrays
                MS_JSONArray newArray = new MS_JSONArray((JSONArray) element);
                this.put(i, newArray); //also rewrite element
            }
        }
    }

    //*** Static utilities ***

    /**
     * Casts given object to JSON array. It also supports {@link JSONArray}, but converts it
     * to {@link MS_JSONArray} in the process.
     *
     * @param fromObject any JSON array object.
     * @return same instance of given <b>fromObject</b>, but casted to {@link MS_JSONArray} type.
     * @throws ClassCastException if <tt>fromObject = null</tt> or is not JSON array.
     */
    public static MS_JSONArray cast(Object fromObject) {
        if (MS_JSONUtils.isOrgJsonArray(fromObject)) {
            return new MS_JSONArray((JSONArray) fromObject);
        } else {
            return (MS_JSONArray) fromObject; //let ClassCastException fly in case of problems
        }
    }

    //*** Utilities ***

    /**
     * Concatenates arrays: this + <b>tail</b>.
     *
     * @param tail array, which will be concatenated to this array.
     * @return reference to this array.
     */
    public MS_JSONArray concat(JSONArray tail) {
        if (!MS_JSONUtils.isEmpty(tail))
            tail.forEach(this::put);
        return this;
    }

    /**
     * Extracts all elements from array and treat them as JSON objects.
     * <p><u>Warning</u>: <u>all</u> objects in this array <u>must</u> be JSON objects.
     *
     * @param ignoreNonJsonObjects whether elements that are not JSON object needs to be skipped (not included in list).
     *                             If <tt>ignoreNonJsonObjects = true</tt> then {@link JSONException} will be never thrown by this method.
     * @return list of JSON objects in this array.
     * @throws JSONException if <tt>ignoreNonJsonObjects = false</tt> and some of elements in array cannot be cast to {@link MS_JSONObject}.
     */
    public List<MS_JSONObject> toJSONObjectList(boolean ignoreNonJsonObjects) {
        List<MS_JSONObject> res = new ArrayList<>();
        for (int i = 0; i < this.length(); i++) {
            Object obj = this.get(i);
            MS_JSONObject jsonObject;
            if (obj instanceof MS_JSONObject) {
                jsonObject = (MS_JSONObject) obj;
            } else {
                if (ignoreNonJsonObjects)
                    continue;
                else
                    throw new JSONException("MS_JSONArray[" + i + "] is not a MS_JSONObject.");
            }
            res.add(jsonObject);
        }
        return res;
    }

    /**
     * Extracts all elements from array and treat them as JSON objects.
     * <p><u>Warning</u>: <u>all</u> objects in this array <u>must</u> be JSON objects.
     *
     * @return list of JSON objects in this array.
     * @throws JSONException if some of elements in array cannot be cast to {@link MS_JSONObject}.
     */
    public List<MS_JSONObject> toJSONObjectList() {
        return toJSONObjectList(false);
    }

    /**
     * Iterates through elements in JSON array and performs given action <b>action</b>.
     * <p><u>Warning</u>: only elements of specified type <b>elementType</b> will be looped, others will be ignored.
     * <p><u>Example</u>:<br>
     * <code>
     * array.forEachElement(MS_JSONObject.class, jsonObject -&gt; {<br>
     * //do something with jsonObject <br>
     * });<br>
     * </code>
     *
     * @param elementType class of elements that will participate in operation.
     * @param action      non-null consumer, which accepts JSON objects.
     * @param <T>         type of elements we are looping through.
     * @throws NullPointerException if given <b>elementType</b> class or <b>action</b> is <tt>null</tt>.
     */
    public <T> void forEachElement(Class<T> elementType, Consumer<T> action) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(action);

        this.forEach((object) -> {
            try {
                action.accept(elementType.cast(object));
            } catch (ClassCastException ignored) {
            }
        });
    }

    /**
     * Iterates through elements in JSON array and performs given action <b>action</b> while <b>breakLoop</b> flag,
     * which is passed as second argument of <b>action</b> bi-consumer is <b>false</b>.
     * <p><u>Warning</u>: only elements of specified type <b>elementType</b> will be looped, others will be ignored.
     * <p><u>Example</u>:<br>
     * <code>
     * array.forEachElement(MS_JSONObject.class, (jsonObject, breakLoop) -&gt; {<br>
     * //do something with jsonObject <br>
     * breakLoop.set(true); //here break is done right after first iteration<br>
     * });<br>
     * </code>
     *
     * @param elementType class of elements that will participate in operation.
     * @param action      non-null bi-consumer, which accepts JSON objects and flag of type {@link AtomicBoolean}, with
     *                    initial value <b>false</b>. Iterating will continue unless the value of this flag will be set to
     *                    <b>true</b>, which will be signal to break iterating and thus next JSON object will not be iterated.
     * @param <T>         type of elements we are looping through.
     * @throws NullPointerException if given <b>elementType</b> class or <b>action</b> is <tt>null</tt>.
     */
    public <T> void forEachElement(Class<T> elementType, BiConsumer<T, AtomicBoolean> action) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(action);

        AtomicBoolean breakLoop = new AtomicBoolean(false);
        Iterator<Object> iterator = this.iterator();
        while (iterator.hasNext() && !breakLoop.get()) {
            try {
                action.accept(elementType.cast(iterator.next()), breakLoop);
            } catch (ClassCastException ignored) {
            }
        }
    }

    /**
     * Filters MS_JSONArray containing only JSON objects according to given predicate.
     * <p><u>Example</u>:<br>
     * <code>MS_JSONArray array = new MS_JSONArray().put(new MS_JSONObject().put("someIntField", 5)).put(new MS_JSONObject().put("someIntField", 0));<br>
     * MS_JSONArray arrayWithJust1Element = array.filter(MS_JSONObject.class, (jsonObject) -&gt; jsonObject.getInt("someIntField") > 1
     * );<br>
     * </code>
     *
     * @param elementType class of elements that will participate in operation.
     * @param predicate   predicate according to which filtering will be done.
     * @param <T>         type of elements we are filtering.
     * @return filtered MS_JSONArray or empty array if no elements match filtering conditions.
     * @throws NullPointerException if <b>limit</b> or <b>predicate</b> is null.
     */
    public <T> MS_JSONArray filter(Class<T> elementType, Predicate<T> predicate) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(predicate);

        MS_JSONArray res = new MS_JSONArray();
        this.forEach((object) -> {
            try {
                T jsonElement = elementType.cast(object);
                if (predicate.test(jsonElement)) {
                    res.put(jsonElement);
                }
            } catch (ClassCastException ignored) {
            }
        });
        return res;
    }

    /**
     * Filters MS_JSONArray containing only JSON objects according to given predicate with limited count of matching objects returned.
     * <p><u>Example</u>:<br>
     * <code>MS_JSONArray array = new MS_JSONArray().put(new MS_JSONObject().put("someIntField", 5)).put(new MS_JSONObject().put("someIntField", 2));<br>
     * MS_JSONArray arrayWithJust1Element = array.filter(MS_JSONObject.class, 1, (jsonObject) -&gt; jsonObject.getInt("someIntField") > 1
     * );<br>
     * </code>
     *
     * @param elementType class of elements that will participate in operation.
     * @param limit       maximum count of objects in array returned by this filtering method.
     * @param predicate   predicate according to which filtering will be done.
     * @param <T>         type of elements we are filtering.
     * @return filtered MS_JSONArray or empty array if no elements match filtering conditions.
     * @throws MS_BadSetupException    if <b>limit</b> is less than 1.
     * @throws NullPointerException if <b>limit</b> or <b>predicate</b> is null.
     */
    public <T> MS_JSONArray filter(Class<T> elementType, Integer limit, Predicate<T> predicate) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(limit);
        Objects.requireNonNull(predicate);
        if (limit < 1) throw new MS_BadSetupException("Limit must be greater than 0. Value passed: " + limit);

        MS_JSONArray res = new MS_JSONArray();
        this.forEachElement(elementType, (element, breakLoop) -> {
            if (predicate.test(element)) {
                res.put(element);
                if (res.length() == limit) breakLoop.set(true);
            }
        });
        return res;
    }

    /**
     * Extracts all JSON objects from array with all or only selected fields included mapped by custom key.
     * <p><u>Example</u>:<br>
     * <code>
     * Map&lt;String, MS_JSONObject&gt; extractedBookings = array.extract(<br>
     * ((booking, index) -&gt; booking.getString("bookingId")),<br>
     * "priceWithVat", "journey");<br><br>
     * //one of use cases - performing assertions:<br>
     * Map&lt;String, MS_JSONObject&gt; expectedBookings = new LinkedHashMap&lt;&gt;();<br>
     * expectedBookings.put("id1", new MS_JSONObject()<br>
     * .put("priceWithVat", 404.112d)<br>
     * .put("journey", new MS_JSONObject().put("depStation", "Riga").put("arrStation", "Moscow")));<br>
     * assertThat(extractedBookings).containsAllEntriesOf(expectedBookings);
     * </code>
     *
     * @param keyCompositionAction action describing, how resulting map's key will be composed.
     * @param fieldsToTakeDescr    if none specified then all fields from each JSON object will be added into map, otherwise
     *                             only specified fields will be taken.<p>
     *                             Also "field.childField as fieldChild" syntax is supported in order to extract only
     *                             specific sub-objects.
     * @param <T>                  type of resulting map's key.
     * @return map containing all <b>array</b> objects reduced to <b>fieldsToTakeDescr</b> and mapped with key
     * produced by <b>keyCompositionAction</b>.
     */
    public <T> Map<T, MS_JSONObject> extract(BiFunction<MS_JSONObject, Integer, T> keyCompositionAction,
                                             String... fieldsToTakeDescr) {
        Objects.requireNonNull(keyCompositionAction);
        Objects.requireNonNull(fieldsToTakeDescr);

        Map<T, MS_JSONObject> res = new LinkedHashMap<>();
        for (int i = 0; i < this.length(); i++) {
            MS_JSONObject json = this.getJSONObject(i);
            final MS_JSONObject reducedJson = new MS_JSONObject();
            if (fieldsToTakeDescr.length == 0) {
                json.keys().forEachRemaining(key -> reducedJson.put(key, json.get(key)));
            } else {
                List<String> fieldDescriptions = MS_CodingUtils.arrayToList(fieldsToTakeDescr);
                Map<String, String> fieldsToTake = new LinkedHashMap<>(fieldDescriptions.size());
                fieldDescriptions.forEach(descr -> {
                    if (descr.contains(" as ")) {
                        String[] split = descr.split(" as ");
                        fieldsToTake.put(split[0], split[1]);
                    } else {
                        fieldsToTake.put(descr, descr);
                    }
                });

                fieldsToTake.forEach((sourcePath, destPath) -> {
                    Object fieldValue = getObjectNode(json, sourcePath);
                    if (fieldValue != null) putInJSONObject(reducedJson, destPath, fieldValue);
                });
            }
            res.put(keyCompositionAction.apply(json, i), reducedJson);
        }
        return res;
    }

    /**
     * @param elementType      class type of (root) elements in array (other types will be ignored).
     * @param extractingAction function, describing how/what elements should be extracted.
     * @param <T>              resulting type of elements that will be extracted.
     * @param <E>              type of (root) elements in array.
     * @return list of type <b>T</b> containing extracted elements.
     */
    public <T, E> List<T> extract(Class<E> elementType, Function<E, T> extractingAction) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(extractingAction);

        List<T> res = new ArrayList<>();
        this.forEach((object) -> {
            try {
                E element = elementType.cast(object);
                res.add(extractingAction.apply(element));
            } catch (ClassCastException ignored) {
            }
        });
        return res;
    }

    //*** Methods from super class to override ***

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;

        if (obj instanceof JSONArray) {
            JSONArray other = (JSONArray) obj;
            if (this.length() == other.length()) {
                for (int i = 0; i < other.length(); i++) {
                    //Either element itself, either at least string representation of object must be equal to this element
                    Object thisElement = this.get(i);
                    Object otherElement = other.get(i);

                    boolean bothElementsAreEqual = Objects.equals(thisElement, otherElement);
                    BooleanSupplier bothElementToStringAreEqual = () -> {
                        String thisToString = Objects.toString(thisElement);
                        String otherToString = Objects.toString(otherElement);
                        return Objects.equals(thisToString, otherToString);
                    };

                    if (!(bothElementsAreEqual || bothElementToStringAreEqual.getAsBoolean()))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public MS_JSONArray getJSONArray(int index) throws JSONException {
        Object element = super.get(index);
        if (element instanceof MS_JSONArray) {
            return (MS_JSONArray) element;
        }
        throw new JSONException("MS_JSONArray[" + index + "] is not a MS_JSONArray.");
    }

    @Override
    public MS_JSONObject getJSONObject(int index) throws JSONException {
        Object element = super.get(index);
        if (element instanceof MS_JSONObject) {
            return (MS_JSONObject) element;
        }
        throw new JSONException("MS_JSONArray[" + index + "] is not a MS_JSONObject.");
    }

    @Override
    public MS_JSONArray optJSONArray(int index) {
        return (MS_JSONArray) super.optJSONArray(index);
    }

    @Override
    public MS_JSONObject optJSONObject(int index) {
        return (MS_JSONObject) super.optJSONObject(index);
    }

    //*** Methods that can be re-used with this type ***

    @Override
    public MS_JSONArray put(boolean value) {
        super.put(value);
        return this;
    }

    @Override
    public MS_JSONArray put(Collection<?> value) {
        super.put(MS_JSONObject.wrap(value));
        return this;
    }

    @Override
    public MS_JSONArray put(double value) throws JSONException {
        super.put(value);
        return this;
    }

    @Override
    public MS_JSONArray put(int value) {
        super.put(value);
        return this;
    }

    @Override
    public MS_JSONArray put(long value) {
        super.put(value);
        return this;
    }

    @Override
    public MS_JSONArray put(Map<?, ?> value) {
        super.put(MS_JSONObject.wrap(value));
        return this;
    }

    @Override
    public MS_JSONArray put(Object value) {
        super.put(MS_JSONObject.wrap(value));
        return this;
    }

    @Override
    public MS_JSONArray put(int index, boolean value) throws JSONException {
        super.put(index, value);
        return this;
    }

    @Override
    public MS_JSONArray put(int index, Collection<?> value) throws JSONException {
        super.put(index, MS_JSONObject.wrap(value));
        return this;
    }

    @Override
    public MS_JSONArray put(int index, double value) throws JSONException {
        super.put(index, value);
        return this;
    }

    @Override
    public MS_JSONArray put(int index, int value) throws JSONException {
        super.put(index, value);
        return this;
    }

    @Override
    public MS_JSONArray put(int index, long value) throws JSONException {
        super.put(index, value);
        return this;
    }

    @Override
    public MS_JSONArray put(int index, Map<?, ?> value) throws JSONException {
        super.put(index, MS_JSONObject.wrap(value));
        return this;
    }

    @Override
    public MS_JSONArray put(int index, Object value) throws JSONException {
        super.put(index, MS_JSONObject.wrap(value));
        return this;
    }

    //*** Private methods ***

    private static Object getObjectNode(org.json.JSONObject obj, String nodePath) {
        if (!MS_JSONUtils.isJsonObject(obj))
            throw new MS_BadSetupException("Object [%s] is not a JSON Object.\nPath of node in object: [%s]", obj, nodePath);

        String[] path = nodePath.split("\\."); //delimited by dot
        return getObjectNode(obj, MS_CodingUtils.arrayToList(path));
    }

    private static Object getObjectNode(org.json.JSONObject obj, List<String> nodePath) {
        if (nodePath.size() > 0) {
            String rootNodeKey = nodePath.get(0);
            if (!obj.has(rootNodeKey))
                return null;

            if (nodePath.size() > 1) {
                if (!MS_JSONUtils.isJsonObject(obj.get(rootNodeKey)))
                    throw new MS_BadSetupException("Node with key [%s] is not a JSON object in JSON: [%s]", rootNodeKey, obj);

                nodePath.remove(0); //use tail for next operations
                return getObjectNode(obj.getJSONObject(rootNodeKey), nodePath);
            } else if (nodePath.size() == 1) {
                return obj.get(rootNodeKey);
            }
        }
        throw new MS_BadSetupException("Empty path to node"); //should not happen unless implementation changed
    }

    private static void putInJSONObject(org.json.JSONObject obj, String path, Object field) {
        if (!MS_JSONUtils.isJsonObject(obj))
            throw new MS_BadSetupException("Object [%s] is not a JSON Object.\nPath of node in object: [%s]", obj, path);

        putInJSONObject(obj, MS_CodingUtils.arrayToList(path.split("\\.")), field);
    }

    private static void putInJSONObject(org.json.JSONObject obj, List<String> nodes, Object field) {
        if (nodes.size() > 0) {
            String rootNodeKey = nodes.get(0);

            if (nodes.size() > 1) {
                if (!obj.has(rootNodeKey)) {
                    //Node doesn't exist, let's add new object
                    org.json.JSONObject newNode = new MS_JSONObject();
                    obj.put(rootNodeKey, newNode);
                    nodes.remove(0); //go deeper with rest of nodes
                    putInJSONObject(newNode, nodes, field);
                    return;
                } else {
                    //Node already exists, let's check, if it's an object, in which we can add new structures
                    if (!MS_JSONUtils.isJsonObject(obj.get(rootNodeKey)))
                        throw new MS_BadSetupException("Node with key [%s] is not a JSON object in JSON: [%s]", rootNodeKey, obj);

                    //If all good, let's go deeper in this specific object
                    nodes.remove(0);
                    putInJSONObject(obj.getJSONObject(rootNodeKey), nodes, field);
                    return;
                }
            } else {
                obj.put(rootNodeKey, field);
                return;
            }
        }
        throw new MS_BadSetupException("Empty path to node"); //should not happen unless implementation changed
    }
}
