package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseFile extends AppCompatActivity {


    Button n, old;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);

        n = findViewById(R.id.n);
        old = findViewById(R.id.old);

        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //New URL
                //https://drive.google.com/file/d/168G9sDAQA-ZHFnkzybLc9bl4TpmIRgCV/view?usp=sharing
                String fileUrl = "https://drive.google.com/uc?export=download&id=168G9sDAQA-ZHFnkzybLc9bl4TpmIRgCV";

                // Create an Intent
                Intent intent = new Intent(ChooseFile.this, DownloadShowFile.class);
                intent.putExtra("URL_EXTRA", fileUrl);
                startActivity(intent);
            }
        });


        old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //OLD URL
                //https://drive.google.com/file/d/15zgFa42brcFKZb2H1RVrF_6sARVS2dhS/view?usp=sharing
                String fileUrl = "https://drive.google.com/uc?export=download&id=15zgFa42brcFKZb2H1RVrF_6sARVS2dhS";

                // Create an Intent
                Intent intent = new Intent(ChooseFile.this, DownloadShowFile.class);
                intent.putExtra("URL_EXTRA", fileUrl);
                startActivity(intent);
            }
        });
    }
}