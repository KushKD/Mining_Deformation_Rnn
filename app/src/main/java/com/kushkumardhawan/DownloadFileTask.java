package com.kushkumardhawan;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFileTask extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = "DownloadFileTask";
    private DownloadCallback callback;
    private String destinationDirectory;
    private Context context;


    public DownloadFileTask(DownloadCallback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String fileUrl = params[0];

        // Get the app's internal storage directory
        File internalStorageDir = context.getFilesDir();

        // Create a file object with the destination path
        File destinationFile = new File(internalStorageDir, "downloaded_file.dat");


        // Create the destination directory if it doesn't exist
//        File dir = new File(destinationFile);
//        if (!dir.exists()) {
//            if (!dir.mkdirs()) {
//                Log.e(TAG, "Failed to create directory");
//                return false;
//            }
//        }

        try {
            // Call this before making the HTTPS connection
            TrustAllCertificates.trustAllCertificates();

            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Check for valid response code
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            int fileLength = connection.getContentLength();
            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(destinationFile);

            byte data[] = new byte[1024];
            long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return false;
                }

                total += count;
                if (fileLength > 0) {
                    publishProgress((int) (total * 100 / fileLength));
                }

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error downloading file: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        callback.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            callback.onDownloadComplete();
        } else {
            callback.onDownloadFailed();
        }
    }

    public interface DownloadCallback {
        void onProgressUpdate(int progress);

        void onDownloadComplete();

        void onDownloadFailed();
    }
}
