package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kushkumardhawan.R;

import java.io.File;
import java.util.ArrayList;

import Presentation.CustomDialog;
import Utilities.AppStatus;


public class DownloadShowFile extends AppCompatActivity implements DownloadFileTask.DownloadCallback {

    private ProgressBar progressBar;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> fileList;


    CustomDialog CD = new CustomDialog();
    String receivedUrl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_show_file);

        // Retrieve the URL from the Intent
        Intent intent = getIntent();
        if (intent != null) {
             receivedUrl = intent.getStringExtra("URL_EXTRA");
        }

        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.listView);
        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);




        // Replace the file URL with the actual Google Drive file URL
        //https://drive.google.com/file/d/1XSQqAqpijfOFd6827p2VztioFItXzZ_A/view?usp
        //=sharing
        //String fileUrl = "https://drive.google.com/uc?export=download&id=1XSQqAqpijfOFd6827p2VztioFItXzZ_A";

        //New URL
        //https://drive.google.com/file/d/168G9sDAQA-ZHFnkzybLc9bl4TpmIRgCV/view?usp=sharing
        //String fileUrl = "https://drive.google.com/uc?export=download&id=168G9sDAQA-ZHFnkzybLc9bl4TpmIRgCV";

        //OLD URL
        //https://drive.google.com/file/d/15zgFa42brcFKZb2H1RVrF_6sARVS2dhS/view?usp=sharing
        //String fileUrl = "https://drive.google.com/uc?export=download&id=15zgFa42brcFKZb2H1RVrF_6sARVS2dhS";

        String destinationDirectory = Environment.getExternalStorageDirectory().getPath() + "/my_directory";

        if(!AppStatus.getInstance(DownloadShowFile.this).isOnline()){
            CD.showDialog(DownloadShowFile.this, "Please connect to Internet First if there is a downloading error. \n \nFile Will be automatically Downloaded form the Server. \n \n After Downloading is Completed, the file name will be shown in the list.  \n \n \n Please click the File and Proceed to view the Deformation Charts. \n \n \n Please enjoy your cup of Tea while the downloading is in process.");
            DownloadFileTask downloadTask = new DownloadFileTask(this, this);
            downloadTask.execute(receivedUrl);
        }else{
            CD.showDialogCloseActivity(DownloadShowFile.this,"Please Connect to Internet and try again.");
        }


        // Set item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected file name
                String selectedFileName = fileList.get(position);

                // Get the file path
                File selectedFile = new File(getFilesDir()+"/downloaded_file.dat");

                // Read the contents of the file
                // String fileContents = readFileContents(selectedFile);

                // Display the file contents
//                Toast.makeText(
//                        DownloadShowFile.this,
//                        "File Path:\n" + selectedFile.getAbsolutePath(),
//                        Toast.LENGTH_LONG
//                ).show();

                Intent mainIntent = new Intent(DownloadShowFile.this, MainActivity.class);
                mainIntent.putExtra("FILE_NAME", selectedFile.getAbsolutePath());
                startActivity(mainIntent);
            }
        });
    }

    @Override
    public void onProgressUpdate(int progress) {
        progressBar.setProgress(progress);

        // Optionally, update a TextView to display the progress percentage
        // TextView progressTextView = findViewById(R.id.progressTextView);
        // progressTextView.setText(getString(R.string.progress, progress));
    }

    @Override
    public void onDownloadComplete() {
        Toast.makeText(this, "Download complete", Toast.LENGTH_SHORT).show();
        hideProgressBarAndShowListView();

        // After download is complete, update the ListView with the downloaded file
        updateFileList();
    }

    @Override
    public void onDownloadFailed() {
        Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
    }

    private void updateFileList() {
        // Update the file list with the downloaded file
        String fileName = "downloaded_file.dat";
        fileList.add(fileName);

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();

        // Optionally, you can open the downloaded file or perform other actions here
        // For example, you can create an Intent to open the file using appropriate apps
        // (e.g., a text file can be opened with a text editor app).
        // Keep in mind that the file type and how it should be opened depend on the file content.
    }

    private void hideProgressBarAndShowListView() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ListView listView = findViewById(R.id.listView);

        // Hide the ProgressBar
        progressBar.setVisibility(View.GONE);

        // Show the ListView and make it fill the screen
        RelativeLayout.LayoutParams listViewParams = (RelativeLayout.LayoutParams) listView.getLayoutParams();
        listViewParams.addRule(RelativeLayout.BELOW, 0);  // Remove the rule that positions it below the ProgressBar
        listView.setLayoutParams(listViewParams);
    }
}