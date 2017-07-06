package com.echopen.asso.echopen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by hugo on 06/07/2017.
 */

public class ClientActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        final ImageView clientdetail = (ImageView) findViewById(R.id.clientdetail);
        clientdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent clientdetail = new Intent(getApplicationContext(),ClientDetailActivity.class);
                startActivity(clientdetail);

            }
        });

    }




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent main = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(main);
                    finish();
                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:
                    Intent search = new Intent(getApplicationContext(),ClientActivity.class);
                    startActivity(search);
                    finish();
                    return true;
                case R.id.navigation_help:

                    return true;
            }
            return false;
        }

    };


}
