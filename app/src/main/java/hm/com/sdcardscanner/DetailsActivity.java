package hm.com.sdcardscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**

 Shows the 10 biggest file , average size of files and the most used extension and their count.
 */


public class DetailsActivity extends AppCompatActivity {

    private String TAG = DetailsActivity.class.getSimpleName();
    private File file;
    private RadioGroup radioGroup;
    private RadioButton name_size, average, frequency;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    String extFrqStr;
    private double averageValue;
    private String biggestFilesList;
    TextView avgText ;
    Context context ;
    TextView nameSizeList ;
    TextView extFrequencyData;
    ProgressBar pb;
    private Menu menu;
    private Intent shareIntent=new Intent(Intent.ACTION_SEND);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        context = this;
        final ArrayList<FileData> listFiles;
        Bundle bundle  = getIntent().getExtras();
        if(bundle != null){
            listFiles = bundle.getParcelableArrayList("dataList");
            averageValue = getAverageSize(listFiles);
            extFrqStr = getFrequentExtension(listFiles);
            biggestFilesList = findTopTenBiggestFiles(listFiles);
        }else{
            listFiles = DataLoader.getInstance().getFiles(file,false);
            averageValue = getAverageSize(listFiles);
            extFrqStr = getFrequentExtension(listFiles);
            biggestFilesList = findTopTenBiggestFiles(listFiles);
        }
        radioGroup = (RadioGroup) findViewById(R.id.raido_btn_grp);
        avgText = (TextView)findViewById(R.id.avdData);
        nameSizeList = (TextView) findViewById(R.id.nameSizeList);
        extFrequencyData = (TextView)findViewById(R.id.extensionFrequencydata);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.nameSize) {
                    avgText.setVisibility(View.GONE);
                    nameSizeList.setVisibility(View.VISIBLE);
                    extFrequencyData.setVisibility(View.GONE);
                    nameSizeList.setText(biggestFilesList);

                }else if(checkedId == R.id.average) {
                    avgText.setVisibility(View.VISIBLE);
                    extFrequencyData.setVisibility(View.GONE);
                    nameSizeList.setVisibility(View.GONE);
                    DecimalFormat dec = new DecimalFormat("0.00");
                    String value = dec.format(averageValue).concat(" KB");
                    avgText.setText(value);

                } else {
                    avgText.setVisibility(View.GONE);
                    nameSizeList.setVisibility(View.GONE);
                    extFrequencyData.setVisibility(View.VISIBLE);
                    extFrequencyData.setText(extFrqStr);

                }

            }

        });

        name_size = (RadioButton) findViewById(R.id.nameSize);

        average = (RadioButton) findViewById(R.id.average);

        frequency = (RadioButton) findViewById(R.id.frequent);

    }

    /**
     * Find the top 10 biggest file  from the scan result.
     * @param filedataList
     * @return string
     */
    public String findTopTenBiggestFiles(List<FileData> filedataList){

        List<FileData> topTen = new ArrayList<FileData>();
        Collections.sort(filedataList,Collections.reverseOrder(new FileComparator()));
        int listSize = filedataList.size() > 10 ? 10 : filedataList.size();
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < listSize ; i ++){
            sb.append(filedataList.get(i).toString());
        }
        Log.i(TAG,sb.toString());
        return  sb.toString();

    }

    /**
     * Get the average size of all the files in sd card.
     * @param fileDataList
     * @return
     */
    public double getAverageSize(List<FileData> fileDataList){

        double sum = 0;
        double avg = 0 ;
        if(fileDataList != null) {
            for (FileData fd : fileDataList) {
                sum = sum + fd.getSize();
            }
            avg = sum / fileDataList.size();
            Log.i(TAG,Double.toString(avg));
            System.out.println("Average = " + avg);
        }
        return avg;
    }

    /**
     * Gets the most commnly used file extensions with the extension name and count.
     * @param fileDataList
     * @return
     */
    public String getFrequentExtension(List<FileData> fileDataList){
        Map<String, Integer> frequencyMap = new TreeMap<String, Integer>();
        if(fileDataList != null) {
            for (FileData fd : fileDataList) {
                if (fd.getFileExtension().equalsIgnoreCase("NoExtn")) {
                    continue;
                }
                if (frequencyMap.containsKey(fd.getFileExtension())) {

                    frequencyMap.put(fd.getFileExtension(), frequencyMap.get(fd.getFileExtension()) + 1);
                } else {
                    frequencyMap.put(fd.getFileExtension(), 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(frequencyMap.entrySet());
        Collections.sort(entryList, Collections.reverseOrder(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        }));

        int size = entryList.size() > 5 ? 5 : entryList.size();
        List<ExtensionData> frequentList = new ArrayList<ExtensionData>();
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < size  ; i ++){
            sb.append(entryList.get(i).getKey());
            sb.append("  (Count  " + entryList.get(i).getValue() + ")"+"\n");
            Log.i(TAG,"Extension = " +  entryList.get(i).getKey() +  " Frequency = " + entryList.get(i).getValue());

        }
        System.out.println();
        return sb.toString();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        enableOptionMenu(false);
        return super.onPrepareOptionsMenu(menu);
    }

    private void enableOptionMenu(boolean flag){
        MenuItem item= menu.findItem(R.id.menu_item_share);
        item.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        }
        if(id == R.id.menu_item_share){
            String shareContent = getString(R.string.ten_big_files)+biggestFilesList+getString(R.string.frequent_file_extensions)+extFrqStr+getString(R.string.average_file_size)+
                    Double.toString(averageValue)+ getString(R.string.kb);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
            ShareScanDetails(shareContent);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Share the scan details.
     * @param shareContent
     */
    private void ShareScanDetails(String shareContent){
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.sdcard_scan_details));
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
    }


}