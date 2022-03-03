package com.clorabase.clorastore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Document {
    protected final File document;

    protected Document(File root){
        this.document = root;
    }

    /**
     * Creates field in the existing document. Updates if already exist.
     * @param field     The name of the field
     * @param value    The value of the field
     * @throws ClorastoreException There are Many possibilities, Some are :-
     *                             1) When value is not a valid datatype
     *                             2) When an IO exception occurred
     *                             3) When document does not exist
     */
    public void update(@NonNull String field,@NonNull Object value) {
        if (!(value instanceof String || value instanceof Number))
            throw new ClorastoreException("Datatype not supported. A document can only contain either a 'String' or a subclass of 'Number'", Reasons.ERROR_UNKNOWN);

        try {
            JSONObject json = new JSONObject(FileUtils.readFileToString(document, Charset.defaultCharset()));
            json.put(field, value);
            FileUtils.writeStringToFile(document, json.toString(4), Charset.defaultCharset(), false);
            if (document.length() > 1024L * 1024 * 1024 * 8 && document.delete())
                throw new ClorastoreException("Document size exceed 10 MB, it must be less then 10 MB.", Reasons.DOC_SIZE_EXCEED);
        } catch (IOException | JSONException e) {
            if (e instanceof IOException)
                throw new ClorastoreException("An IO error occurred while creating/writing document in " + document.getName() + ". Error details:-\n" + e.getLocalizedMessage(), Reasons.IO_ERROR);
            else
                throw new ClorastoreException("Updating document that does not exist ?", Reasons.NO_DOC_EXIST);
        }
    }


    /**
     * Creates field in the document. Creates document if not already exist. This will overwrite previously written data the document
     * @param fields The data to store in the document
     * @throws ClorastoreException If fields contain a value that is not a valid datatype or if an IO error occurred.
     */
    public void setData(@NonNull Map<String,?> fields) {
        try {
            fields.forEach((s, o) -> {
                if (!(o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof List))
                    throw new ClorastoreException("Datatype not supported ! Please see supported datatype in documentation", Reasons.INVALID_DATATYPE);
            });
            FileUtils.writeStringToFile(document, new JSONObject(fields).toString(4), Charset.defaultCharset(), false);
            if (document.length() > 1024L * 1024 * 1024 * 8 && document.delete())
                throw new ClorastoreException("Document size exceed 10 MB, it must be less then 10 MB.", Reasons.DOC_SIZE_EXCEED);
        } catch (IOException | JSONException e) {
            if (e instanceof IOException)
                throw new ClorastoreException("An IO error occurred while creating/writing document in " + document.getName() + " .Error details:-\n" + ((IOException) e).getLocalizedMessage(), Reasons.IO_ERROR);
            else
                throw new ClorastoreException("An unexpected error occurred", Reasons.ERROR_UNKNOWN);
        }
    }


    /**
     * Returns the fields and their values present in the document in the form of <code>Map</code>
     * @return a Map containing fields name as String and values as object,null if document is empty.
     */
    @Nullable
    public Map<String,? super Object> getData() {
        try {
            Map<String, Object> map = new HashMap<>();
            JSONObject json = new JSONObject(FileUtils.readFileToString(document, Charset.defaultCharset()));
            Iterator<String> it = json.keys();
            while (it.hasNext()) {
                String key = it.next();
                map.put(key, json.get(key));
            }
            return map;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Gets the POJO from the document.
     * @param name The name of the POJO
     * @return Object that can be casted to your POJO class.
     */
    public Object getObject(@NonNull String name,Class<?> clazz){
        return new Gson().fromJson((String) getData().get(name),clazz);
    }


    /**
     * Inserts a POJO into the document.
     * @param name Name of the pojo
     * @param object The POJO
     */
    public void setObject(@NonNull String name,@NonNull Object object){
        Map<String,Object> map = new HashMap<>();
        map.put(name,new Gson().toJson(object));
        setData(map);
    }


    /**
     * Deletes the document. Any operation performed on this document after deleting this may cause
     * {@link java.io.FileNotFoundException}. This should be last call on this document.
     */
    public void delete(){
        document.delete();
        System.gc();
    }

    /**
     * Gets the name of the document. Usefully when document is returned by the query.
     * @return Name of the document with .doc as extension.
     */
    public String getName(){
        return document.getName();
    }
}
