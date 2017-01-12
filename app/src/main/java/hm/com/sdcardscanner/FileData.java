package hm.com.sdcardscanner;


import android.os.Parcel;
import android.os.Parcelable;


public class FileData implements Parcelable {


    public FileData() {

    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }


    private Double size;
    private String fileName;
    private String fileExtension;

    public String toString() {
        return this.getFileName() + "(" + this.getSize() + " kb)\n";

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(size);
        dest.writeString(fileName);
        dest.writeString(fileExtension);

    }


    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public FileData createFromParcel(Parcel in) {
            return new FileData(in);
        }

        public FileData[] newArray(int size) {
            return new FileData[size];
        }
    };

    // "De-parcel object
    public FileData(Parcel in) {
        size = in.readDouble();
        fileName = in.readString();
        fileExtension = in.readString();

    }
}