package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.Utils;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = ImageActivity.class.getName();
    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView fullImageView = (ImageView) findViewById(R.id.fullImageid);
        String fullImageString = getIntent().getExtras().getString("imageuri");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_indicator_main_grid);

        Glide
                .with(this)
                .load(fullImageString)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        //Error in gif
                        prepareShareIntent(((GlideBitmapDrawable) resource).getBitmap());
                        return false;
                    }
                })
                .error(R.drawable.ic_image_error)
                .into(fullImageView);

        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.checkImageResource(ImageActivity.this, (ImageView) view, R.drawable.ic_image_error)) {
                    Toast.makeText(ImageActivity.this, getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                } else if (Utils.checkImageResource(ImageActivity.this, (ImageView) view, R.drawable.ic_loading)) {
                    Toast.makeText(ImageActivity.this, getResources().getString(R.string.image_loading), Toast.LENGTH_SHORT).show();
                }
            }
        });

        fullImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch() ");
                ImageView view = (ImageView) v;

                // Handle touch events here...
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        Log.d(TAG, "mode=DRAG");
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        Log.d(TAG, "oldDist=" + oldDist);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                            Log.d(TAG, "mode=ZOOM");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        Log.d(TAG, "mode=NONE");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            // ...
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - start.x, event.getY()
                                    - start.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            Log.d(TAG, "newDist=" + newDist);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                        break;
                }

                view.setImageMatrix(matrix);
                return true;
            }
        });
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void prepareShareIntent(Bitmap drawableImage) {
        Uri bmpUri = getBitmapFromDrawable(drawableImage);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");

    }

    public Uri getBitmapFromDrawable(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getCacheDir(), "images" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(ImageActivity.this, "com.example.imnobody.photosearch.fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}