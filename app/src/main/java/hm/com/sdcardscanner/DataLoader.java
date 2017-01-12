package hm.com.sdcardscanner;

import java.io.File;
import java.util.ArrayList;

/**
 * Singleton class to get the scan data.
 */

public class DataLoader {

    private static final DataLoader instance = new DataLoader();
    private ArrayList<FileData> fileDataList = null;
    private DataLoader(){

    }

    public static DataLoader getInstance() {
        return instance;
    }


    public  ArrayList<FileData> getFiles(File parentDir , boolean reload) {
        if(reload) {
            fileDataList = getListFiles(parentDir);
        }
        return fileDataList;
    }

    ArrayList<FileData> getListFiles(File parentDir) {
        ArrayList<FileData> inFiles = new ArrayList<FileData>();

        File[] files = parentDir.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    FileData fd = new FileData();

                    Double file_size = Double.parseDouble(String.valueOf(file.length()/1024));
                    fd.setSize(file_size);
                    fd.setFileName(file.getName());
                    int lastIndexdOfComma = file.getName().lastIndexOf('.');
                    if(lastIndexdOfComma != -1){
                        fd.setFileExtension(file.getName().substring(lastIndexdOfComma));
                    } else {
                        fd.setFileExtension("NoExtn");
                    }

                    inFiles.add(fd);
                }
            }
        }
        return inFiles;
    }
}