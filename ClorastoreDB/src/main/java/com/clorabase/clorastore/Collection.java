package com.clorabase.clorastore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * This class represent a collection in the database. A collection is the collection of many documents.
 * A collections can have sub-collections in it.
 */
public class Collection {
    private final File root;

    protected Collection(File root) {
        this.root = root;
    }

    /**
     * Creates field in the document. Creates document if not already exist. This will overwrite previously written values in the document, So Don't use this for updating values.
     *
     * @param name   The name of the document
     * @param fields The values to store in the document
     * @throws ClorastoreException If fields contain a value that is not a datatype or if an IO error occured.
     */
    public void document(String name, Map<String, Object> fields) {
        File file = new File(root, name + ".doc");
        try {
            fields.forEach(new BiConsumer<String, Object>() {
                @Override
                public void accept(String s, Object o) {
                    if (!(o instanceof String || o instanceof Number))
                        throw new ClorastoreException("Only datatype are not supported. A document can only contain either a 'string' or a subclass of 'Number'", Reasons.INVALID_DATATYPE);
                }
            });

            FileUtils.writeStringToFile(file, new JSONObject(fields).toString(4), Charset.defaultCharset(), false);

            if (file.length() > 1024L * 1024 * 1024 * 8 && file.delete())
                throw new ClorastoreException("Document size exceed 10 MB, it must be less then 10 MB.", Reasons.DOC_SIZE_EXCEED);
        } catch (IOException | JSONException e) {
            if (e instanceof IOException)
                throw new ClorastoreException("An IO error occured while creating/writing document in " + file.getName() + " .Error details:-\n" + ((IOException) e).getLocalizedMessage(), Reasons.IO_ERROR);
            else
                throw new ClorastoreException("An unexpected error occured. Error details:-\n" + e.getLocalizedMessage(), Reasons.ERROR_UNKNOWN);
        }
    }

    /**
     * Creates field creating the document if not exist. Updates if already exist.
     * @param document The name of the document
     * @param name     The name of the field
     * @param value    The value of the field
     * @throws ClorastoreException There are Many possibilities, Some are :-
     *                             1) When value is not a datatype
     *                             2) When an IO exception occured
     *                             3) When document can't be created for some reason.
     */
    public void document(String document, String name, Object value) {
        if (!(value instanceof String || value instanceof Number))
            throw new ClorastoreException("Only datatype are not supported. A document can only contain either a 'string' or a subclass of 'Number'", Reasons.ERROR_UNKNOWN);

        File file = new File(root, document + ".doc");
        try {
            JSONObject json;
            if (file.exists())
                json = new JSONObject(FileUtils.readFileToString(file, Charset.defaultCharset()));
            else if (file.createNewFile())
                json = new JSONObject();
            else
                throw new ClorastoreException("Document can't be created for some reason.", Reasons.ERROR_UNKNOWN);

            json.put(name, value);
            FileUtils.writeStringToFile(file, json.toString(4), Charset.defaultCharset(), false);

            if (file.length() > 1024L * 1024 * 1024 * 8 && file.delete())
                throw new ClorastoreException("Document size exceed 10 MB, it must be less then 10 MB.", Reasons.DOC_SIZE_EXCEED);
        } catch (IOException | JSONException e) {
            if (e instanceof IOException)
                throw new ClorastoreException("An IO error occured while creating/writing document in " + file.getName() + ". Error details:-\n" + e.getLocalizedMessage(), Reasons.IO_ERROR);
            else
                throw new ClorastoreException("An unexpected error occured. Error details:-\n" + e.getLocalizedMessage(), Reasons.ERROR_UNKNOWN);
        }
    }

    /**
     * Goes into the specified collection or creates if does not exist.
     * @param name Name of the collection
     * @return That new collection
     * @throws ClorastoreException If collection cannot be created for some reason.
     */
    public @NonNull Collection collection(String name) {
        File file = new File(root, name);
        if (!file.isDirectory() && !file.mkdir())
            throw new ClorastoreException("Unknown error occured while creating document.", Reasons.ERROR_UNKNOWN);
        return new Collection(file);
    }

    /**
     * Returns collections present in the current collection.
     *
     * @return {@link Collection}. A list of collections. May be empty, but never null.
     */
    public @NonNull List<Collection> getCollections() {
        List<Collection> collections = new ArrayList<>();
        String[] files = root.list(FileFilterUtils.directoryFileFilter());
        if (files != null) {
            for (String file : files)
                collections.add(new Collection(new File(root, file)));
        }
        return collections;
    }

    /**
     * Returns document names present in the current collection.
     *
     * @return {@link List<String>}. May be empty, but never null
     */
    public List<String> getDocuments() {
        String[] files = root.list(FileFilterUtils.suffixFileFilter(".doc"));
        if (files == null)
            return new ArrayList<>();
        else {
            for (int i = 0; i < files.length; i++) {
                files[i] = files[i].replace(".doc","");
            }
            return Arrays.asList(files);
        }
    }

    /**
     * Returns the name of this collection.
     *
     * @return {@link String}
     */
    public String getName() {
        return root.getName();
    }

    /**
     * Returns the fields and their values present in the document in the form of <code>Map</code>
     *
     * @param name The name of the document.
     * @return If succeed,Map containing fields name as String and values as object, otherwise null.
     */
    public @Nullable Map<String, Object> getDocumentFields(String name) {
        try {
            Map<String, Object> map = new HashMap<>();
            JSONObject json = new JSONObject(FileUtils.readFileToString(new File(root, name + ".doc"), Charset.defaultCharset()));
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
     * Deletes a document or a collection.
     * @param name The name of the doc or collection
     * @return true if delete succeed, false otherwise
     */
    public boolean delete(String name){
        return new File(root,name).delete();
    }
    
}
