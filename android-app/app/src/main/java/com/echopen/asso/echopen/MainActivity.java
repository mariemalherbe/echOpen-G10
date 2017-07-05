package com.echopen.asso.echopen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.support.design.widget.BottomNavigationView;
import android.widget.TextView;

import com.echopen.asso.echopen.echography_image_streaming.EchographyImageStreamingService;
import com.echopen.asso.echopen.echography_image_streaming.modes.EchographyImageStreamingTCPMode;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationContract;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationPresenter;
import com.echopen.asso.echopen.ui.AbstractActionActivity;
import com.echopen.asso.echopen.utils.Constants;


/**
 * MainActivity class handles the main screen of the app.
 * Tools are called in the following order :
 * - initSwipeViews() handles the gesture tricks via GestureDetector class
 * - initViewComponents() mainly sets the clickable elements
 * - initActionController() and setupContainer() : in order to separate concerns, View parts are handled by the initActionController()
 * method which calls the MainActionController class that deals with MainActivity Views,
 * especially handles the display of the main screen picture
 * These two methods should be refactored into one
 */

public class MainActivity extends AppCompatActivity implements AbstractActionActivity, EchographyImageVisualisationContract.View {

    private EchographyImageStreamingService mEchographyImageStreamingService;
    private EchographyImageStreamingTCPMode lTCPMode = new EchographyImageStreamingTCPMode(Constants.Http.REDPITAYA_IP, Constants.Http.REDPITAYA_PORT);
    private boolean isProbeConnected=true;
    private ImageView activityStatusView;

    /**
     * This method calls all the UI methods and then gives hand to  UDPToBitmapDisplayer class.
     * UDPToBitmapDisplayer listens to UDP data, processes them with the help of ScanConversion,
     * and then displays them.
     * Also, this method uses the Config singleton class that provides device-specific constants
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        final Button buttonprobe = (Button) findViewById(R.id.buttonprobe);
        //activityStatusView = (ImageView) findViewById(R.id.activity);
        //activityStatusView.setImageResource(R.drawable.button_active);
        buttonprobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EchographyActivity.class);
                startActivity(intent);
            }
        });

        mEchographyImageStreamingService = ((EchOpenApplication) this.getApplication()).getEchographyImageStreamingService();
        EchographyImageVisualisationContract.Presenter mEchographyImageVisualisationPresenter = new EchographyImageVisualisationPresenter(mEchographyImageStreamingService, this);
        this.setPresenter(mEchographyImageVisualisationPresenter);
        mEchographyImageStreamingService.connect(lTCPMode, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEchographyImageStreamingService.connect(lTCPMode, this);
        toggleActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEchographyImageStreamingService.disconnect();
        toggleActivity();
    }


    /**
     * Following the doc https://developer.android.com/intl/ko/training/basics/intents/result.html,
     * onActivityResult is “Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.”,
     * See more here : https://stackoverflow.com/questions/20114485/use-onactivityresult-android
     *
     * @param requestCode, integer argument that identifies your request
     * @param resultCode,  to get its values, check RESULT_CANCELED, RESULT_OK here https://developer.android.com/reference/android/app/Activity.html#RESULT_OK
     * @param data,        Intent instance
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toggleActivity() {
//        isProbeConnected = !isProbeConnected;
//        if (!isProbeConnected) {
//            Log.e("probe", "active");
//            activityStatusView.setImageResource(R.drawable.ic_status_active);
//        } else {
//            activityStatusView.setImageResource(R.drawable.ic_status_active);
//            Log.e("probe", "inactive");
//        }
    }

    @Override
    public void initActionController() {

    }

    @Override
    public void refreshImage(final Bitmap iBitmap) {

    }

    @Override
    public void setPresenter(EchographyImageVisualisationContract.Presenter presenter) {
    }

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };
}