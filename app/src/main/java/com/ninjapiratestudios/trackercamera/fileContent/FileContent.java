package com.ninjapiratestudios.trackercamera.fileContent;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by benjamin on 3/15/16.
 */
public class FileContent {
    private ArrayList<FileHolder> fileContentList;

    /**
     * Creates an empty File Content Object
     */
    public FileContent() {
        fileContentList = new ArrayList<>();
    }


    /**
     * Creates a File Content Object from a list of files.
     *
     * @param files - list of files to create the FileContent object from
     */
    public FileContent(File[] files) {
        assert files != null;
        fileContentList = new ArrayList<>();
        Log.d("FileLength", Integer.valueOf(files.length).toString());
        for (File file : files) {
            Log.d("Exists", Boolean.toString(file.exists()));
            Log.d("FileName", file.getName());
            Log.d("FileContents", file.toString());
            fileContentList.add(new FileHolder(file));
        }
        Collections.sort(fileContentList);
    }

    /**
     * Retrieves the fileHolder object being stored at the position passed in by the caller
     * and returns that item to the caller.
     *
     * @param position - the position of the FileHolder object in the FileContent Object.
     * @return the FileHolder object at that position.
     */
    public FileHolder getItem(int position) {
        return fileContentList.get(position);
    }

    /**
     * Adds A FileHolderObject to the FileContent object
     *
     * @param fH - The file holder object being added.
     */
    public void addItem(FileHolder fH) {
        this.fileContentList.add(fH);
        Collections.sort(fileContentList);
    }

    /**
     * Returns an ArrayList of the FileHolder object the FileContent object currently contains.
     *
     * @return the list of FileHolder Objects.
     */
    public ArrayList<FileHolder> getItems() {
        return fileContentList;
    }

    /**
     * Returns the number of file content objects stored in the FileContent object.
     *
     * @return - the number of FileHolder Object in the FileContent Object.
     */
    public int size() {
        return fileContentList.size();
    }

    /**
     * Created by benjamin on 3/15/16.
     */
    public class FileHolder implements Comparable {
        /**
         * The Video file the FileHolder object is holding
         */
        private final File videoFile;

        /**
         * Creates a FileHolder object to hold the file being passed in.
         *
         * @param f - the file the FileHolder is holding.
         */
        public FileHolder(File f) {
            this.videoFile = f;
        }

        /**
         * Retrieves the File the FileHolder object is holding.
         *
         * @return the videofile the FileHolder object is holding.
         */
        public File getVideoFile() {
            return this.videoFile;
        }

        /**
         * Compares the names of video files, this is the primary purpose of creating the FileHolder
         * object and is used by FileContent to sort the FileHolder objects it contains.
         *
         * @param another - The Object Being compared
         * @return the result of the comparison
         */
        @Override
        public int compareTo(Object another) {
            FileHolder fileToCompare = null;
            try {
                fileToCompare = (FileHolder) another;
            } catch (Exception e) {
                System.out.println("The object passed in wasn't able to be converted to a FileHolder object. \n" +
                        "Are you sure that you provided an object of type FileHolder?");
                System.exit(1);
            }
            String name1 = this.getVideoFile().getName();
            String name2 = fileToCompare.getVideoFile().getName();
            return name1.compareToIgnoreCase(name2);
        }

    }
}
