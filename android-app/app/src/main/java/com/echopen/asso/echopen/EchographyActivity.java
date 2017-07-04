package com.echopen.asso.echopen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.echopen.asso.echopen.echography_image_streaming.EchographyImageStreamingService;
import com.echopen.asso.echopen.echography_image_streaming.modes.EchographyImageStreamingTCPMode;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationContract;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationPresenter;
import com.echopen.asso.echopen.ui.RenderingContextController;

import static com.echopen.asso.echopen.utils.Constants.Http.REDPITAYA_IP;
import static com.echopen.asso.echopen.utils.Constants.Http.REDPITAYA_PORT;

/**
 * This class handle the displaying of the echography results
 */
public class EchographyActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echography);
        RenderingContextController controller = new RenderingContextController();
        // instanciate the streaming service passing by the contextController
        EchographyImageStreamingService streamingService = new EchographyImageStreamingService(controller);
        EchographyImageVisualisationPresenter presenter = new EchographyImageVisualisationPresenter(streamingService, new EchographyImageVisualisationContract.View() {

            @Override
            public void refreshImage(final Bitmap iBitmap) {
                try {
                    // on probe output refresh the image view
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView echoImage = (ImageView) findViewById(R.id.image);
                            echoImage.setImageBitmap(iBitmap);
                            Log.e("image", iBitmap.getWidth() + "," + iBitmap.getHeight());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void setPresenter(EchographyImageVisualisationContract.Presenter presenter) {
                presenter.start();
            }

        });


        // pass the network ip and port
        EchographyImageStreamingTCPMode mode = new EchographyImageStreamingTCPMode(REDPITAYA_IP, REDPITAYA_PORT);
        streamingService.connect(mode, this);
        // subscribe to the observable stream
        presenter.listenEchographyImageStreaming();

    }

}
