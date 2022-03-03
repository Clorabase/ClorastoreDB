package com.clorabase.clorastore;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A class to perform query and sort data on the basis of some condition. Only the data inside the provided
 * collection is queried. Query operations may take longer time if size of document is large. If so, wrap it
 * in a background thread.
 */
public class Query {
    private final File collection;

    /**
     * Creates a query from the provided collection.
     * @param collection The collection from where to start querying.
     */
    public Query(Collection collection){
        this.collection = collection.root;
    }


    /**
     * Query collections which contains the provided collection or document in it.
     * @param docOrColl_name The name of the document or collection which you have to query
     * @return The collections which has the following document or collection in it.
     */
    public Collection[] whichHas(String docOrColl_name){
        Object[] collections = FileUtils.listFiles(collection, null, true)
                .stream()
                .filter(file -> file.getName().equals(docOrColl_name))
                .map(file -> new Collection(file.getParentFile()))
                .toArray();
        return Arrays.copyOf(collections,collections.length,Collection[].class);
    }


    /**
     * Finds the document on the basis of its data. It will query all the document from the current collection till it
     * finds at least one document whose filed 'field' has value 'value'.
     * @param field The field name in the document
     * @param value The value of the field that is to be checked across
     * @return an array of the {@link Document} which has field with the corresponding value.
     */
    public Document[] whereEqual(@NonNull String field,@NonNull Object value){
        return where(stringMap -> Objects.equals(stringMap.get(field), value));
    }


    /**
     * Query documents on the basis of given condition. The condition is evaluated on the data (Map)
     * of the document. If the data of the document matches the given predicate, then it is considered otherwise not.
     * @param condition The boolean function that is to be checked
     * @return Array of {@link Document} which satisfy the given predicate.
     * @throws NullPointerException if the data is not same in documents (under the querying collection)
     * and the key in predicate is not present. To avoid this, use {@link Map#getOrDefault(Object, Object) method}
     */
    public Document[] where(@NonNull Predicate<Map<String,? super Object>> condition){
        Object[] docs = FileUtils.listFiles(collection, new String[]{"doc"}, true)
                .stream()
                .map(Document::new)
                .filter(document -> condition.test(document.getData()))
                .toArray();

        return Arrays.copyOf(docs,docs.length,Document[].class);
    }


    /**
     * Collect the document which has POJO that meets your condition.
     * @param clazz The class of your POJO
     * @param condition A boolean function that is evaluated as the condition to include the document.
     * @return Array of {@link Document} as the query result
     */
    public <T> Document[] whereObject(Class<T> clazz,Predicate<T> condition){
        Object[] docs = FileUtils.listFiles(collection, new String[]{"doc"}, true)
                .stream()
                .map(Document::new)
                .filter(doc -> doc.getData().entrySet().stream().allMatch(data -> {
                    try {
                        T object = new Gson().fromJson(new JSONObject((String) data.getValue()).toString(), clazz);
                        return condition.test(object);
                    } catch (JSONException e) {
                        return false;
                    }
                }))
                .toArray();

        return Arrays.copyOf(docs,docs.length,Document[].class);
    }


    /**
     * Returns the document where the 'value' is greater then value of 'field' in the data of document.
     * @param field The filed to compare
     * @param value The value to compare across
     * @return An array of {@link Document} which satisfy the given condition.
     * @throws ClorastoreException if field does not denotes a number
     */
    public Document[] whereGreater(@NonNull String field,Number value){
        if (value instanceof Long val)
            return where(stringMap -> ((long) stringMap.getOrDefault(field,0)) > val);
        else if (value instanceof Integer val)
            return where(stringMap -> ((int) stringMap.getOrDefault(field, 0)) > val);
        else if (value instanceof Double val)
            return where(stringMap -> ((double) stringMap.getOrDefault(field,0)) > val);
        else
            throw new ClorastoreException("Invalid number. the field does not denotes a number",Reasons.INVALID_DATATYPE);    }


    /**
     * Returns the document where the 'value' is smaller then value of 'field' in the data of document.
     * @param field The filed to compare
     * @param value The value to compare across
     * @return An array of {@link Document} which satisfy the given condition.
     * @throws ClorastoreException if field does not denotes a number
     */
    public Document[] whereSmaller(@NonNull String field,Number value){
        if (value instanceof Long val)
            return where(stringMap -> ((long) stringMap.getOrDefault(field,0)) < val);
        else if (value instanceof Integer val)
            return where(stringMap -> ((int) stringMap.getOrDefault(field, 0)) < val);
        else if (value instanceof Double val)
            return where(stringMap -> ((double) stringMap.getOrDefault(field,0)) < val);
        else
            throw new ClorastoreException("Invalid number. the field does not denotes a number",Reasons.INVALID_DATATYPE);
    }
}
