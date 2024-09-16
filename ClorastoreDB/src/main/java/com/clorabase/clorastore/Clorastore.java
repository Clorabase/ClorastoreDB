package com.clorabase.clorastore;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * This is the top-level class of the whole database. Every database operation
 * or any configuration change in it is performed using this class.
 * You can configure your database behaviour from here.
 * The database is located in the path returned by {@link Context#getDataDir()}.
 *
 * @author Rahil khan
 * @since 2021
 * @see <a href="https://github.com/ErrorxCode/ClorastoreDB">Github</a> for more info.
 */
public class Clorastore {
    private static Clorastore instance;
    private static File root;

    private Clorastore(){}

    /**
     * Returns the singleton instance of the class.
     * @param dir The directory of your database
     * @return instance of the class.
     * @throws ClorastoreException - if database was not created successfully.
     */
    public static Clorastore getInstance(File dir,String name){
        if (instance == null)
            instance = new Clorastore();

        root = new File(dir,name);
        if (!root.exists() && !root.mkdir())
            throw new ClorastoreException("There was an error while creating the database.",Reasons.ERROR_CREATING_DATABASE);

        return instance;
    }


    /**
     * Returns the root of the database. This root may or may not contain collection.
     * @return {@link Collection}
     */
    public Collection getDatabase(){
        return new Collection(root);
    }

    /**
     * Returns the collection defined by the relative path from the root of the database.
     * For example, passing "/students/junior" will return collection "junior" which is present in collection "student".
     * @param relativePath Relative path of the connection from the root.
     * @return {@link Collection}
     * @throws ClorastoreException If relativePath does not denotes an existing collection
     */
    public Collection getDatabase(String relativePath){
        if (new File(root,relativePath).isDirectory())
            return new Collection(new File(root,relativePath));
        else
            throw new ClorastoreException("Path is not relative or does not denotes a collection",Reasons.NO_COLLECTION_EXIST);
    }


    /**
     * Deletes the current database.
     * @return true if succeed, false otherwise (IOException occurred)
     */
    public boolean delete(){
        try {
            FileUtils.deleteDirectory(root);
            return true;
        } catch (IOException e) {
           return false;
        }
    }


    /**
     * Cleans the database root without deleting it. The database will be still there after cleaning it.
     * This will just delete all the collection in its root.
     * @return true if succeed, false otherwise (IOException occurred)
     */
    public boolean clean(){
        try {
            FileUtils.cleanDirectory(root);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
