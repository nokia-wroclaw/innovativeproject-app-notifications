package com.robinsonprogramming.www.loginrestapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddService extends AppCompatActivity {

    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        imageButton1 = (ImageButton) findViewById(R.id.imageButtonAdd1);
        imageButton2 = (ImageButton) findViewById(R.id.imageButtonAdd2);
        imageButton3 = (ImageButton) findViewById(R.id.imageButtonAdd3);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(getApplicationContext(), "Success 1", Toast.LENGTH_LONG).show();
                goToSecond(bundle);
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(getApplicationContext(), "Success 2", Toast.LENGTH_LONG).show();
                goToWebsiteActivity(bundle);
            }
        });

        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Work in progress", Toast.LENGTH_LONG).show();
            }
        });
    }

    void goToSecond(Bundle bundle)
    {
        Intent intent = new Intent(this, AddServiceSecond.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    void goToWebsiteActivity(Bundle bundle)
    {
        Intent intent = new Intent(this, AddServiceWebsite.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
