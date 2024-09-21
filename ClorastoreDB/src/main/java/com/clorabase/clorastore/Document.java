package com.clorabase.clorastore;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Document {
    protected final File document;
    protected Map<String, Object> data;
    public static final int DOCUMENT_MAX_SIZE = 5*1024*1024;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    protected Document(File root) {
        this.document = root;
        data = getData();
    }

    /**
     * Creates field in the existing document. Updates if already exist.
     *
     * @param field The name of the field
     * @param value The value of the field
     * @throws ClorastoreException There are Many possibilities, Some are :-
     *                             1) When value is not a valid datatype
     *                             2) When an IO exception occurred
     *                             3) When document does not exist
     */
    public void put(@NonNull String field, @NonNull Object value) {
        validateDatatype(value);

        if (document.length() > DOCUMENT_MAX_SIZE && document.delete())
            throw new ClorastoreException("Document size exceed 5 MB, it must be less then 5 MB.", Reasons.DOC_SIZE_EXCEED);

        data.put(field,value);
        setData(data);
    }


    /**
     * Creates field in the document. Creates document if not already exist. This will overwrite previously written data the document
     * Use put() to update the data, add() to add element into the list. The method runs on a background thread
     *
     * @param fields The data to store in the document
     * @throws ClorastoreException If fields contain a value that is not a valid datatype or if an IO error occurred.
     */
    public void setData(@NonNull Map<String, Object> fields) {
        fields.values().forEach(this::validateDatatype);

        if (document.length() > DOCUMENT_MAX_SIZE && document.delete())
            throw new ClorastoreException("Document size exceed 5 MB, it must be less then 5 MB.", Reasons.DOC_SIZE_EXCEED);

        var json = gson.toJsonTree(fields).getAsJsonObject();
        data = fields;
        executor.execute(() -> {
            try {
                FileUtils.writeStringToFile(document, json.toString(), Charset.defaultCharset(), false);
            } catch (IOException e) {
                throw new ClorastoreException("An IO error occurred while creating/writing document in " + document.getName() + " .Error details:-\n" + e.getLocalizedMessage(), Reasons.IO_ERROR);
            }
        });
    }


    /**
     * Returns the fields and their values present in the document in the form of <code>Map</code>
     *
     * @return a Map containing fields name as String and values as object,null if document is empty.
     */
    @NonNull
    protected Map<String, ? super Object> getData() {
        try {
            var content = FileUtils.readFileToString(document, Charset.defaultCharset());
            if (content == null || content.isEmpty())
                return new HashMap<>();

            var type = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(content,type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getString(String filed,String defaultValue){
        return (String) data.getOrDefault(filed,defaultValue);
    }

    public Number getNumber(String filed,Number defaultValue){
        return (Number) data.getOrDefault(filed,defaultValue);
    }

    public boolean getBoolean(String filed,boolean defaultValue){
        return (boolean) data.getOrDefault(filed,defaultValue);
    }

    public List getList(String filed,List defaultValue){
        return (List) data.getOrDefault(filed,defaultValue);
    }

    /**
     * Adds a value to the list. If list does not exist, it will be created.
     * @param listName Name of the list
     * @param value Value to be added
     */
    public void addItem(String listName,Object value){
        validateDatatype(value);
        var list = (List) data.getOrDefault(listName,new ArrayList<>());
        try {
            list.add(value);
        } catch (UnsupportedOperationException e){
            var mutable = new ArrayList<>(list);
            mutable.add(value);
            list = mutable;
            put(listName,mutable);
        }
        data.put(listName,list);
        setData(data);
    }

    public void removeItem(String listName,Object value) {
        var list = (List) data.getOrDefault(listName, new ArrayList<>());
        list.remove(value);
        data.put(listName, list);
        setData(data);
    }


    /**
     * Gets the POJO from the document.
     *
     * @return Object that can be casted to your POJO class.
     * @throws ClassCastException If the object is not compatible with the class provided
     */
    public <T> T getAsObject(@NonNull Class<T> clazz) throws IOException, ClassCastException {
        System.out.println(data);
        return gson.fromJson(gson.toJsonTree(data),clazz);
    }


    /**
     * Inserts a POJO into the document.
     *
     * @param object The POJO
     */
    public void setObject(@NonNull Object object) {
        var json = gson.toJsonTree(object);
        var type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String,Object> map =  gson.fromJson(json,type);
        setData(map);
    }


    /**
     * Deletes the document. Any operation performed on this document after deleting this may cause
     * {@link java.io.FileNotFoundException}. This should be last call on this document.
     */
    public void delete() {
        document.delete();
        System.gc();
    }

    /**
     * Gets the name of the document. Usefully when document is returned by the query.
     *
     * @return Name of the document with .doc as extension.
     */
    public @NonNull String getName() {
        return document.getName();
    }

    private void validateDatatype(Object value) {
        var isValid = value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof List;
        if (!isValid)
            throw new ClorastoreException("Datatype not supported. A document can only contain either a 'String','Boolean' or a subclass of 'Number'", Reasons.ERROR_UNKNOWN);
    }

    private void addValueByObject(JsonObject jsonObject, String propertyName, Object value) {
        if (value instanceof String) {
            jsonObject.addProperty(propertyName, (String) value);
            jsonObject.addProperty(propertyName, (String) value);
        } else if (value instanceof Number) {
            jsonObject.addProperty(propertyName, (Number) value);
        } else if (value instanceof Boolean) {
            jsonObject.addProperty(propertyName, (Boolean) value);
        } else if (value instanceof List) {
            JsonArray jsonArray = new JsonArray();
            for (Object item : (List) value) {
                if (item instanceof String || item instanceof Number || item instanceof Boolean) {
                    jsonArray.add(new JsonPrimitive(item.toString()));
                } else {
                    throw new ClorastoreException("Unsupported item found in the list : " + item.getClass().getName(),Reasons.INVALID_DATATYPE);
                }
            }
            jsonObject.add(propertyName, jsonArray);
        } else {
            throw new ClorastoreException("Unsupported data type : " + value.getClass().getName(),Reasons.INVALID_DATATYPE);
        }
    }


    @Override
    public String toString() {
        return gson.toJson(data);
    }
}
