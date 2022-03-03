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
    protected final File root;

    protected Collection(File root) {
        this.root = root;
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
            throw new ClorastoreException("Unknown error occurred while creating document.", Reasons.ERROR_UNKNOWN);
        return new Collection(file);
    }


    public @NonNull Document document(@NonNull String name){
        File file = new File(root, name + ".doc");
        try {
            file.createNewFile();
            return new Document(file);
        } catch (IOException e) {
            throw new ClorastoreException("Unknown error occurred while creating document.", Reasons.ERROR_UNKNOWN);
        }
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
     * Deletes a document or a collection. This do not delete to current collection
     * in which you are currently in.
     * @param name The name of the doc or collection
     * @return true if delete succeed, false otherwise
     */
    public boolean delete(String name){
        return new File(root,name).delete();
    }


    /**
     * Constructs a query starting from this collection.
     */
    public Query query(){
        return new Query(this);
    }
}
