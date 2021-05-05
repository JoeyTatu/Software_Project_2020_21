package ncirl.x15015556.detect_diabetic_reinopathy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Intent mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        setContentView(R.layout.activity_second);

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

        //Declare items in XML
        TextView tvGoogleID = findViewById(R.id.tvGoogleID);
        TextView tvGoogleName = findViewById(R.id.tvGoogleName);
        mainActivity = new Intent(SecondActivity.this, MainActivity.class);

        // Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Get Google Info from file
        String googleID = ReadWriteToFile.readFromFileGoogleID(this);
        String googleName = ReadWriteToFile.readFromFileGoogleName(this);

        // Set Google info in XML
        tvGoogleID.setText("(" + googleID + ")");
        tvGoogleName.setText(googleName);

        // Buttons
        Button btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        Button btnRevokeAccess = findViewById(R.id.btnRevokeAccess);
        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
            }
        });
    }

    // To stop the app from going back to the login screen when already logged in
    @Override
    public void onBackPressed() {
        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
//        finish(); // Finishes current indent and opens Log in screen.
//        System.exit(0); // Restarts same intent.
    }


    // To sign out the user from Google
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        ReadWriteToFile.writeToFileGoogleID("", SecondActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", SecondActivity.this);
                        finish();
                    }
                });
    }

    // To allow the user to revoke access to Google Sign in.
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        ReadWriteToFile.writeToFileGoogleID("",SecondActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", SecondActivity.this);
                        finish();
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

    // Google Sign in failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Google", "Google Sign In failed");
    }
}
