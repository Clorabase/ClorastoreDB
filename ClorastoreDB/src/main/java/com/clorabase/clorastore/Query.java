package com.clorabase.clorastore;

import androidx.annotation.NonNull;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A class to perform query and sort data on the basis of some condition. Only the data inside the provided
 * collection is queried. Query operations may take longer time if size of document is large. If so, wrap it
 * in a background thread.
 */
public class Query {
    private final File collection;

    /**
     * Creates a query from the provided collection.
     *
     * @param collection The collection from where to start querying.
     */
    public Query(Collection collection) {
        this.collection = collection.root;
    }


    /**
     * Query collections which contains the provided collection or document in it.
     *
     * @param docOrColl_name The name of the document or collection which you have to query
     * @return The collections which has the following document or collection in it.
     */
    public List<Collection> whichHas(String docOrColl_name) {
        return FileUtils.listFiles(collection, null, true)
                .stream()
                .filter(file -> file.getName().equals(docOrColl_name))
                .map(file -> new Collection(file.getParentFile()))
                .collect(Collectors.toList());
    }


    /**
     * Finds the document on the basis of its data. It will query all the document from the current collection till it
     * finds at least one document whose filed 'field' has value 'value'.
     *
     * @param field The field name in the document
     * @param value The value of the field that is to be checked across
     * @return an list of the {@link Document} which has field with the corresponding value.
     */
    public List<Document> whereEqual(@NonNull String field, @NonNull Object value) {
        return where(stringMap -> Objects.equals(stringMap.get(field), value));
    }


    /**
     * Query documents on the basis of given condition. The condition is evaluated on the data (Map)
     * of the document. If the data of the document matches the given predicate, then it is considered otherwise not.
     *
     * @param condition The boolean function that is to be checked
     * @return List of {@link Document} which satisfy the given predicate.
     * @throws NullPointerException if the data is not same in documents (under the querying collection)
     *                              and the key in predicate is not present. To avoid this, use {@link Map#getOrDefault(Object, Object) method}
     */
    public List<Document> where(@NonNull Predicate<Map<String, ? super Object>> condition) {
        return FileUtils.listFiles(collection, new String[]{"doc"}, true)
                .stream()
                .map(Document::new)
                .filter(document -> condition.test(document.getData()))
                .collect(Collectors.toList());
    }


    /**
     * Collect the document which has POJO that meets your condition.
     *
     * @param clazz     The class of your POJO
     * @param condition A boolean function that is evaluated as the condition to include the document.
     * @return Array of {@link Document} as the query result
     */
    public <T> List<Document> whereObject(Class<T> clazz, Predicate<T> condition) {
        return FileUtils.listFiles(collection, new String[]{"doc"}, true)
                .stream()
                .map(Document::new)
                .filter(doc -> {
                    try {
                        var obj = doc.getAsObject(clazz);
                        return obj.getClass() == clazz;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassCastException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

    }


    /**
     * Returns the document where the 'value' is greater then value of 'field' in the data of document.
     *
     * @param field The filed to compare
     * @param value The value to compare across
     * @return An array of {@link Document} which satisfy the given condition.
     * @throws ClorastoreException if field does not denotes a number
     */
    public List<Document> whereGreater(@NonNull String field, double value) {
        return where(stringMap -> ((double) stringMap.getOrDefault(field, 0.0)) > value);
    }


    /**
     * Returns the document where the 'value' is smaller then value of 'field' in the data of document.
     *
     * @param field The filed to compare
     * @param value The value to compare across
     * @return An array of {@link Document} which satisfy the given condition.
     * @throws ClorastoreException if field does not denotes a number
     */
    public List<Document> whereSmaller(@NonNull String field, double value) {
        return where(stringMap -> ((double) stringMap.getOrDefault(field, 0.0)) < value);
    }

    /**
     * Sort the documents on the basis of the given field.
     *
     * @param field     The field to sort on
     * @param ascending Whether to sort in ascending order or not
     * @return List of {@link Document} which are sorted on the basis of the field.
     */
    public List<Document> orderBy(@NonNull String field, boolean ascending) {
        var docs = FileUtils.listFiles(collection, new String[]{"doc"}, false)
                .stream()
                .map(Document::new)
                .filter(document -> document.data.containsKey(field))
                .collect(Collectors.toList());

        var type = docs.get(0).data.get(field);
        Comparator<Document> comparator;
        if (type instanceof Number) {
            comparator = (t0, t1) -> {
                var field0 = t0.getNumber(field, 0);
                var field1 = t1.getNumber(field, 0);
                return (int) (field1.doubleValue() - field0.doubleValue());
            };
        } else if (type instanceof Boolean) {
            comparator = (t0, t1) -> {
                var field0 = t0.getBoolean(field, false);
                var field1 = t1.getBoolean(field, false);
                return Boolean.compare(field0, field1);
            };
        } else if (type instanceof String) {
            comparator = Comparator.comparing(document -> document.getString(field, ""));
        } else {
            throw new ClorastoreException("Unsupported datatype", Reasons.INVALID_DATATYPE);
        }

        docs.sort(comparator);
        if (!ascending) {
            Collections.reverse(docs);
        }
        return docs;
    }
}
