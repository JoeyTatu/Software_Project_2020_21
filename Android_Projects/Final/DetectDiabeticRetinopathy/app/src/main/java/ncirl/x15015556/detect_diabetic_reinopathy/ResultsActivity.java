package ncirl.x15015556.detect_diabetic_reinopathy;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ResultsActivity extends AppCompatActivity {

    Button btnSubmit, btnSendEmail;
    Bitmap image;
    ImageView ivImage;
    TextView tvResults;
    Interpreter tflite;
    boolean diabRet = false;
    Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title in Android R/10+
        setContentView(R.layout.activity_results);

        //App runs full screen, no header or status bar.
        // Version R is Android 10+ - if/else for compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Removes the header/title bar in app
            // Android R is version 10 - if/else for versions older versions
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(

                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        ivImage = findViewById(R.id.ivImage);
        tvResults = findViewById(R.id.tvResults);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSendEmail = findViewById(R.id.btnSendEmail);

//        Intent receiveIntent;
//        receiveIntent = new Intent(ResultsActivity.this, ImageActivity.class);
        Intent imageActivityIntent = getIntent();
        String uriImageString = imageActivityIntent.getStringExtra("uriImage");
        Log.d("URI", uriImageString);

        uriImage = Uri.parse(uriImageString);
      Picasso.get().load(uriImage).into(ivImage);

//        if (Build.VERSION.SDK_INT < 28) {
//            try {
//                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImage);
//                ivImage.setImageBitmap(image);
//
//                Picasso.get().
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        } else {
//            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),uriImage);
//            try {
//                image = ImageDecoder.decodeBitmap(source);
//                ivImage.setImageBitmap(image);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        Button btnSendEmail = findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitToFile();
            }
        });

        loadFile();
    }

    private void classifyImage(Bitmap bitmap) {
        int imageSizeX = 224;
        int imageSizeY = 224;

        float[] inputVal;
        inputVal = new float[1];

        bitmap = getResizedBitmap(bitmap, imageSizeX, imageSizeY);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 1 *
                224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[][] intValues;
        intValues = new int[224][224];

        bitmap.getPixels(intValues[0], 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i<224; i++){
            for(int j = 0; j<224; j++){
                int val = intValues[pixel++][pixel++];

                //Kotlin:
//                byteBuffer.putFloat((val shr 16 and 0xFF) * (1f / 255f));
//                byteBuffer.putFloat((val shr 8 and 0xFF) * (1f / 255f));
//                byteBuffer.putFloat((val and 0xFF) * (1f / 255f));

                //Java:
                byteBuffer.putFloat((((val >> 16) & 0xFF)*(1f / 255f)));
                byteBuffer.putFloat((((val >> 8) & 0xFF)*(1f / 255f)));
                byteBuffer.putFloat(((val & 0xFF)*(1f / 255f)));
            }
        }

        if (tflite != null){
            tflite.run(byteBuffer, inputVal);
            if (inputVal[0] >= 0.5) {
                if (inputVal[0] > 0.05){
                    if(tvResults != null){
                        tvResults.setText("Your eye result may be positive for retinopathy.\nConsider consulting a doctor.");

                    } else {
                        tvResults.setText("You may have retinopathy.\nConsider consulting a doctor.");
                        diabRet = true;
                    }

                } else {
                    tvResults.setText("Your eye is healthy!");
                }
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaledWidth = newWidth / width;
        float scaledHeight = newHeight / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaledWidth, scaledHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public void loadFile() {
        try{
            tflite = new Interpreter(loadModelFile());
            classifyImage(image);

        } catch (Exception e){
            Toast.makeText(this, "Error getting model", Toast.LENGTH_LONG).show();
            Log.d("Model", e.toString());
        }
    }

    public MappedByteBuffer loadModelFile() throws IOException {
        try (AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite")) {
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getDeclaredLength();
            long declaredLength = fileDescriptor.getDeclaredLength();

            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

//    public void setImage() {
//        try {
//            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImage);
//            if (image != null) {
//                ivImage.setImageBitmap(image);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error getting image", Toast.LENGTH_LONG).show();
//            Log.d("SetImage", e.toString());
//        }
//    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void submitToFile() {
        // Send to database.
    }

    @SuppressLint("IntentReset")
    public void sendEmail() {
        String to = "";
        String cc = "";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("application/image");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Detect Diabetic Retinopathy Results");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, \n This is the results of the Detect Diabetic Retinopathy app.");
        try{
            startActivity(Intent.createChooser(emailIntent, "Share..."));
            finish();
            Log.i("Shared", "Email sent to doctor");

        } catch (ActivityNotFoundException e){
            Toast.makeText(this, "There are no email apps installed", Toast.LENGTH_LONG).show();
            Log.i("Shared", "No email app installed on device");
        }

    }

}
