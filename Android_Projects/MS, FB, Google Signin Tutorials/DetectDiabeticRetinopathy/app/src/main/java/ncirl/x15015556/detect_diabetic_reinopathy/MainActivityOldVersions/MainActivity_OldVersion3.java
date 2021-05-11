/*
 * @References:
 * Global variables: Vishal Yidav - https://stackoverflow.com/questions/1944656/android-global-variable
 *
 * Facebook Login: Facebook, Inc. - https://developers.facebook.com/apps/927107168075416/fb-login/quickstart
 * Facebook Login: ProgrammingKnowledge - https://www.youtube.com/watch?v=qAN9KYhOSec
 *
 * Remove Header Bar: Boris Strandjev, Andriy D. - https://stackoverflow.com/questions/62835053/how-to-set-fullscreen-in-android-r
 *
 * Google Sign In: Google, Inc - https://developers.google.com/identity/sign-in/android/start-integrating
 * Google Sign In: ProgrammingKnowledge - https://www.youtube.com/watch?v=uPg1ydmnzpk
 *
 * OnSavedInstanceState/OnRestoreInstanceState: Zhar & Reto Meier - https://stackoverflow.com/questions/151777/how-to-save-an-activity-state-using-save-instance-state
 */
//
//package ncirl.x15015556.detect_diabetic_reinopathy;
//
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowInsets;
//import android.view.WindowInsetsController;
//import android.view.WindowManager;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleID;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.SignInButton;
//import com.google.android.gms.common.api.GoogleApiClient;
//
//public class SignInActivity<savedInstanceState> extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
//
//    private TextView textStatus;
//    private TextView status; // This will show any errors with Login to the user
//    private String username, facebookID, googleID;
//    private Intent secondActivity;
//
//    // Facebook
//    private LoginButton btnFacebookLogin;
//    public CallbackManager callbackManager;
//    private AccessToken accessToken;
//
//    // Google
//    SignInButton btnGoogleSignIn;
//    private GoogleApiClient mGoogleApiClient;
//
////    private static final String TAG = SignInActivity.class.getSimpleName();
////    private static final int FB_SIGN_IN = 10;
//    private static final int GOOGLE_SIGN_IN = 20;
////    private static final String KEY_FACEBOOK_ID = "facebookID_key";
////    private static final String KEY_GOOGLE_ID = "googleID_key";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        setContentView(R.layout.activity_main);
//
//        //App runs full screen, no header or status bar.
//        // Version R is Android 10+ - if/else for compatibility
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
//            // Removes the header/title bar in app
//            // Android R is version 10 - if/else for versions older versions
//            final WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                insetsController.hide(WindowInsets.Type.statusBars());
//            }
//        } else {
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//            );
//        }
//
////        if (savedInstanceState != null){
////            facebookID = savedInstanceState.getString(KEY_FACEBOOK_ID);
////            googleID = savedInstanceState.getString(KEY_GOOGLE_ID);
//
////            if ( (facebookID != null) || (googleID != null)) {
////                startActivity(secondActivity);
////            }
////
////        } else {
//
////        if (savedInstanceState != null){
////            onRestoreInstanceState(savedInstanceState);
////            Log.d("savedIns", facebookID + " / " + googleID);
////            startActivity(secondActivity);
////        }
//
//        //Google
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestId()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            startActivity(secondActivity);
//        }
//
//
//        btnGoogleSignIn = (SignInButton) findViewById(R.id.btn_google_sign_in);
//        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                googleSignIn();
//            }
//        });
//
//
////        //Facebook
////        callbackManager = CallbackManager.Factory.create();
////        btnFacebookLogin = (LoginButton) findViewById(R.id.btn_facebook_login);
////        textStatus = (TextView) findViewById(R.id.txt_status);
////        facebookLogin();
//
//    }
////    }
//
////    private void setUI() {
////        btnFacebookLogin = (LoginButton) findViewById(R.id.btn_facebook_login);
////        btnFacebookLogin.setOnClickListener(this);
//////        btnFacebookLogin.setReadPermissions("email", "public_profile");
////    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
////            case R.id.btn_google_sign_in: //Already integrated into Facebook Login button, can't access it from here
////                facebookLogin();
//
//            case R.id.btn_google_sign_in:
//                googleSignIn();
//
//        }
//    }
//
//    private void googleSignIn() {
//        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(intent, GOOGLE_SIGN_IN);
//    }
//
//    // Facebook only keeps the API code active for around 72 hours before it expires.
//    // The code itself works, but I cannot guarantee the app will work with the examiner, with the API code changing.
////    private void facebookLogin() {
////        if (accessToken != null) {
////            Log.d("LoginResult", "accesst:" + accessToken);
////            startActivity(secondActivity);
////            finish();
////        }
////        else {
////            //Callback registration
////            btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
////                @Override
////                public void onSuccess(LoginResult loginResult) {
////                    accessToken = loginResult.getAccessToken();
////                    String facebookID = accessToken.getUserId();
////                    Log.d("LoginResult", "User ID: " + facebookID);
////
////                    // Sets the Facebook ID and signedin boolean in Global variables.
////
////                    Global.setFacebookID(facebookID);
////                    Global.setGoogleID(null);
////                    Global.setSignedIn(true);
////
////                    Toast.makeText(SignInActivity.this, "Successfully logged in with Facebook", Toast.LENGTH_LONG).show();
////                    startActivity(secondActivity);
////                }
////
////                @Override
////                public void onCancel() {
////                    Global.setFacebookID(null);
////                    Global.setGoogleID(null);
////                    Global.setSignedIn(false);
////                    Toast.makeText(SignInActivity.this, "Facebook login cancelled", Toast.LENGTH_LONG).show();
////                }
////
////                @Override
////                public void onError(FacebookException error) {
////                    Global.setFacebookID(null);
////                    Global.setGoogleID(null);
////                    Global.setSignedIn(false);
////                    textStatus.setText("Facebook Login error: " + error.getMessage());
////                }
////            });
////        }
////    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
////        if (requestCode == FB_SIGN_IN) {
////            callbackManager.onActivityResult(requestCode, resultCode, data);
////            startActivity(secondActivity);
////        }
//
//        if (requestCode == GOOGLE_SIGN_IN){
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if (result.isSuccess()){
//                GoogleSignInAccount acct = GoogleID.getLastSignedInAccount(this);
//                String googleID = acct.getId();
//
//                Global.setFacebookID(null);
//                Global.setGoogleID(googleID);
//                Global.setSignedIn(true);
//
//                startActivity(secondActivity);
//            }
//        }
//    }
//
//    // Google Sign in failed
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Global.setFacebookID(null);
//        Global.setGoogleID(null);
//        Global.setSignedIn(false);
//        Log.d("LoginResult", "Google Sign In failed");
//    }
//
////    // To keep the data when the user kills the app
////    // This was only keeping data while the app is active. If one closes and reopens the app, this gets reset.
////    @Override
////    public void onSaveInstanceState(Bundle savedInstanceState) {
////        super.onSaveInstanceState(savedInstanceState);
////        savedInstanceState.putString(KEY_FACEBOOK_ID, facebookID);
////        savedInstanceState.putString(KEY_GOOGLE_ID, googleID);
////    }
////
////    @Override
////    public void onRestoreInstanceState(Bundle savedInstanceState) {
////        super.onRestoreInstanceState(savedInstanceState);
////        // Restore UI state from the savedInstanceState.
////        // This bundle has also been passed to onCreate.
////        facebookID = savedInstanceState.getString(KEY_FACEBOOK_ID);
////        googleID = savedInstanceState.getString(KEY_GOOGLE_ID);
////    }
//
//}