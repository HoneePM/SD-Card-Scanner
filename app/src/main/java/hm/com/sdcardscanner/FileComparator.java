package hm.com.sdcardscanner;
import java.util.Comparator;


public class FileComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof FileData && o2 instanceof FileData) {
            return (((FileData) o1).getSize()).compareTo(((FileData) o2).getSize());
        }
        return 0;
    }
}