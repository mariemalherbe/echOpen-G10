package com.echopen.asso.echopen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.echopen.asso.echopen.echography_image_streaming.EchographyImageStreamingService;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationContract;
import com.echopen.asso.echopen.echography_image_visualisation.EchographyImageVisualisationPresenter;

import java.util.UUID;

/**
 * This class handle the displaying of the echography results
 */
public class EchographyActivity extends Activity implements EchographyImageVisualisationContract.View {


    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    private void saveImageToGallery(Bitmap bitmap){
        ImageView imageView = (ImageView) findViewById(R.id.image);
        //bitmap = imageView.getDrawingCache();

        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "/screenshot" + UUID.randomUUID().toString() +".png", null);
    }

//    public void saveBitmap(Bitmap bitmap) {
//
//        String screenshot = "/screenshot" + UUID.randomUUID().toString() +".png";
//        File imagePath = new File(Environment.getExternalStorageDirectory() + screenshot);
//
//        FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(imagePath);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            Log.e("bitmap", String.valueOf(imagePath));
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            Log.e("bitmap", e.getMessage(), e);
//        }
//    }

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
                saveImageToGallery(bitmap);

            }
        });

        final ImageButton buttonmenu = (ImageButton) findViewById(R.id.buttonmenu);
        buttonmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EchographyActivity.this);

                View view = getLayoutInflater().inflate(R.layout.dialog, null);
                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        EchographyImageStreamingService mEchographyImageStreamingService = ((EchOpenApplication) this.getApplication()).getEchographyImageStreamingService();
        // instanciate the streaming service passing by the contextController
        EchographyImageVisualisationContract.Presenter mEchographyImageVisualisationPresenter = new EchographyImageVisualisationPresenter(mEchographyImageStreamingService, this);
        // subscribe to the observable stream
        this.setPresenter(mEchographyImageVisualisationPresenter);
    }

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
}
