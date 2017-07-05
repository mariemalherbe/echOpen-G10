package com.echopen.asso.echopen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.echopen.asso.echopen.echography_image_streaming.EchographyImageStreamingService;
import com.echopen.asso.echopen.echography_image_streaming.modes.EchographyImageStreamingTCPMode;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationContract;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationPresenter;
import com.echopen.asso.echopen.ui.RenderingContextController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.echopen.asso.echopen.utils.Constants.Http.REDPITAYA_IP;
import static com.echopen.asso.echopen.utils.Constants.Http.REDPITAYA_PORT;

/**
 * This class handle the displaying of the echography results
 */
public class EchographyActivity extends Activity {


    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.e("bitmap", String.valueOf(imagePath));
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("bitmap", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("bitmap", e.getMessage(), e);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echography);


        final ImageButton buttonscreenshot = (ImageButton) findViewById(R.id.buttonscreenshot);
        buttonscreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Bitmap bitmap = takeScreenshot();
            saveBitmap(bitmap);

            }
        });

        final ImageButton buttonmenu = (ImageButton) findViewById(R.id.buttonmenu);
        buttonmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EchographyActivity.this);

                View view = getLayoutInflater().inflate(R.layout.dialog,null);
                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });




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
        Bundle bundle = getIntent().getExtras();
        final String TAG = "MyActivity";
        Log.d(TAG,bundle.getString("probe"));

    }

}
