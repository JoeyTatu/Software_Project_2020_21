package ncirl.x15015556.detect_diabetic_reinopathy;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    Button btnSubmit, btnSendEmail;
    Bitmap image;
    ImageView ivImage;
    TextView tvResults, tvGoogle, tvName, tvPatientID;
    Interpreter tflite;
    boolean diabRet = false;
    Uri uriImage;
    String resultText, resultPercent, patientID, googleName, googleID;
    Button btnSignOut, btnRevokeAccess;
    GoogleApiClient mGoogleApiClient;
    Intent mainActivity, patientActivity;

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
        TextView tvGoogleID = findViewById(R.id.tvGoogleID);
        TextView tvGoogleName = findViewById(R.id.tvGoogleName);
        ivImage = findViewById(R.id.ivImage);
        tvResults = findViewById(R.id.tvResults);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSendEmail = findViewById(R.id.btnSendEmail);

        // Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mainActivity = new Intent(ResultsActivity.this, SignInActivity.class);
        patientActivity = new Intent(ResultsActivity.this, PatientActivity.class);

        btnSignOut = findViewById(R.id.btnSignOut);
        btnRevokeAccess = findViewById(R.id.btnRevokeAccess);

        // Buttons
        btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                startActivity(mainActivity);
            }
        });

        btnRevokeAccess = findViewById(R.id.btnRevokeAccess);
        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
                startActivity(mainActivity);
            }
        });

        //Declaring patientID textview, and getting relevant values from storage, if present.
        tvPatientID = findViewById(R.id.tvPatientID);
        patientID = ReadWriteToFile.readFromFilePatientID(this);
        // if no PatientID stored, go to PatientActivity, else populate patientID
        if ((patientID == null) || (patientID.equals(""))) {
            startActivity(patientActivity);

        } else {
            tvPatientID.setText(patientID);
        }

        //Declaring GoogleID and Name textview, and getting relevant values from storage, if present.
        tvGoogleID = findViewById(R.id.tvGoogleID);
        tvGoogleName = findViewById(R.id.tvGoogleName);
        googleName = ReadWriteToFile.readFromFileGoogleName(this);
        googleID = ReadWriteToFile.readFromFileGoogleID(this);
        //if no Google Name or ID, go back to SignInActivity to sign in, else populate with GoogleID and GoogleName
        if ((googleID == null) || (googleID.equals("")) || (googleName == null) || (googleName.equals(""))) {
            startActivity(mainActivity);

        } else {
            tvGoogleName.setText(googleName);
            tvGoogleID.setText(googleID);
        }

//        Intent receiveIntent;
//        receiveIntent = new Intent(ResultsActivity.this, ImageActivity.class);
        Intent imageActivityIntent = getIntent();
        String uriImageString = imageActivityIntent.getStringExtra("uriImage");
        Log.d("URI", uriImageString);

        uriImage = Uri.parse(uriImageString);
        Picasso.get()
                .load(uriImage)
                .into(ivImage);

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                submitToFile();
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loadFile();
            }
        }, 2500);   //5 seconds

    }

    @SuppressLint("SetTextI18n")
    private void classifyImage(Bitmap bitmap) {
        int imageSizeX = 224;
        int imageSizeY = 224;

        float[] inputVal;
        inputVal = new float[1];

        try{
            //Try Method 1
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

        } catch (Exception e) { //Try Method 2
            Log.d("Result", e.getMessage());

            resultText = ReadWriteToFile.getResult();
            resultPercent = String.valueOf(ReadWriteToFile.getPercentage());

            tvResults.setText("There is a " + resultPercent + "% chance there is " + resultText + " diabetic retinopathy.");
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
            Log.d("Model", e.toString());
        }
//
//        String modelFilePath = "//assets/model.tflite";
//
//        File fileModel = new File(modelFilePath);
//        fileModel.getAbsoluteFile();

    }

//    public MappedByteBuffer loadModelFile() throws IOException {
//        try (AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite")) {
//            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//            FileChannel fileChannel = inputStream.getChannel();
//            long startOffset = fileDescriptor.getDeclaredLength();
//            long declaredLength = fileDescriptor.getDeclaredLength();
//
//            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
//        }
//    }

    private MappedByteBuffer loadModelFile() throws IOException {

        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        MappedByteBuffer ret = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

        return ret;


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

        String datetime;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        datetime = dft.format(now);

        } else {
            SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd");
            datetime = stf.format((new Date()));

        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference scan_dt_resultText = database.getReference("patients/" + patientID + "/dr_scan/" + datetime + "/stage");
        scan_dt_resultText.setValue(resultText);

        DatabaseReference scan_dt_resultPercent = database.getReference("patients/" + patientID + "/dr_scan/" + datetime + "/percent_chance");
        scan_dt_resultPercent.setValue(resultPercent + "%");



        tvResults.append("\n\nSent to file!");

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

    // To sign out the user from Google
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // These ensure the saved info is deleted upon exit.
                        ReadWriteToFile.writeToFileGoogleID("", ResultsActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", ResultsActivity.this);
                        startActivity(mainActivity);
                    }
                });
    }

    // To allow the user to revoke access to Google Sign in.
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // These ensure the saved info is deleted upon exit.
                        ReadWriteToFile.writeToFileGoogleID("", ResultsActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", ResultsActivity.this);
                        startActivity(mainActivity);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSignOut:
                signOut();
                startActivity(mainActivity);

            case R.id.btnRevokeAccess:
                revokeAccess();
                startActivity(mainActivity);
        }
    }

    // To stop the app from going back to the login screen when already logged in
    @Override
    public void onBackPressed() {
        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
//        finish(); // Finishes current indent and opens Log in screen.
//        System.exit(0); // Restarts same intent.
    }

    // Google Sign in failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Google", "Google Sign In failed");
    }
}
