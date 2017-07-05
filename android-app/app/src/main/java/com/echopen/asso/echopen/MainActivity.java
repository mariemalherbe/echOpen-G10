package com.echopen.asso.echopen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.echopen.asso.echopen.echography_image_streaming.EchographyImageStreamingService;
import com.echopen.asso.echopen.echography_image_streaming.modes.EchographyImageStreamingTCPMode;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationContract;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationPresenter;
import com.echopen.asso.echopen.ui.AbstractActionActivity;
import com.echopen.asso.echopen.ui.RenderingContextController;
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

public class MainActivity extends Activity implements AbstractActionActivity, EchographyImageVisualisationContract.View {

    private EchographyImageStreamingService mEchographyImageStreamingService;
    private EchographyImageStreamingTCPMode lTCPMode = new EchographyImageStreamingTCPMode(Constants.Http.REDPITAYA_IP, Constants.Http.REDPITAYA_PORT);
    private boolean isProbeConnected=true;
    private ImageView activityButton;

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
        activityButton = (ImageView) findViewById(R.id.button);
        //activityButton.setImageResource(R.drawable.button_active);
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
        toggleButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEchographyImageStreamingService.disconnect();
        toggleButton();
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

    private void toggleButton() {
        isProbeConnected = !isProbeConnected;
        if (!isProbeConnected) {
            Log.e("probe", "active");
            //activityButton.setImageResource(R.drawable.button_active);
        } else {
            //activityButton.setImageResource(R.drawable.button_iddle);
            Log.e("probe", "inactive");
        }
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
}