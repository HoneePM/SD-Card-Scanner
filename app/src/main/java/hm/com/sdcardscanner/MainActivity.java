package hm.com.sdcardscanner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.util.List;

/**
 * Launcher class . Displays the start and stop button.
 */

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private File file;
    private Handler handler = new Handler();
    Integer count = 1;
    Button stopScanBtn;
    Button statScanBtn;
    LoadData task;
    private NotificationCompat.Builder notification;
    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statScanBtn = (Button) findViewById(R.id.button_start);
        stopScanBtn = (Button) findViewById(R.id.button_stop);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setMax(10);

        statScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScanBtn.setEnabled(true);
                count = 1;
                List<FileData> sdcarddata;
                progressBar.setVisibility(View.VISIBLE);
                startNotification();
                progressBar.setProgress(0);
                task = new LoadData();
                task.execute(10);
            }
        });
        stopScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                task.cancel(true);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
                notificationManager.cancel(0);

            }
        });
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    public class LoadData extends AsyncTask<Integer, Integer, String> {
        ProgressDialog progressDialog;


        @Override
        protected String doInBackground(Integer... params) {
            int start = params[0];
            publish(start);
            String root_sd = Environment.getExternalStorageDirectory().toString();
            file = new File(root_sd);

            if (isCancelled()) {
                return null;
            }
            DataLoader.getInstance().getFiles(file, true);
            if (isCancelled()) {
                return null;
            }
            publish(start + 50);
            return "Task Completed.";
        }


        public void publish(int start) {
            for (int i = start; i <= 50; i += 5) {
                try {
                    boolean isCancelled = isCancelled();
                    if (!isCancelled) {
                        publishProgress(i);
                    }
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
            stopScanBtn.setEnabled(false);
            notificationManager.cancel(0);
            Intent i = new Intent(MainActivity.this, DetailsActivity.class);
            i.putParcelableArrayListExtra("dataList", DataLoader.getInstance().getFiles(null, false));
            startActivity(i);
        }

        ;

        @Override
        protected void onPreExecute() {
            progressBar.setMax(100);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setProgress(10);
            progressBar.setProgress(progressBar.getProgress() + 10);
        }

        @Override
        protected void onCancelled() {
            progressBar.setMax(0);
            notificationManager.cancel(0);

        }
    }

    /**
     * Notification
     */
    protected void startNotification() {
        notification = new NotificationCompat.Builder(MainActivity.this);
        notification.setContentTitle(getString(R.string.card_scan));
        notification.setContentText(getString(R.string.scan_description));
        notification.setTicker(getString(R.string.scan_title));
        notification.setSmallIcon(R.drawable.scan);

        Intent notificationIntent = new Intent(this, DetailsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(contentIntent);
        notificationManager.notify(0, notification.build());
    }

    private void cancelScan() {

        notificationManager.cancel(0);
        statScanBtn.setEnabled(true);
        stopScanBtn.setEnabled(false);
        progressBar.setProgress(0);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelScan();
        }
        return super.onKeyDown(keyCode, event);
    }


}