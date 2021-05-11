///*
// * @References:
// * Global variables: Vishal Yidav - https://stackoverflow.com/questions/1944656/android-global-variable
// * Facebook Login: Facebook, Inc. - https://developers.facebook.com/apps/927107168075416/fb-login/quickstart
// * Facebook Login: ProgrammingKnowledge - https://www.youtube.com/watch?v=qAN9KYhOSec
// * Remove Header Bar: Boris Strandjev, Andriy D. - https://stackoverflow.com/questions/62835053/how-to-set-fullscreen-in-android-r
// *
// */
//
//package ncirl.x15015556.detect_diabetic_reinopathy;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.SignInButton;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.OptionalPendingResult;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//
//public class SignInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
//
//    private LoginButton btnFbLogin;
//    private TextView status;
//    public CallbackManager callbackManager;
//
//    private static final String TAG = SignInActivity.class.getSimpleName();
//    private static final int RC_SIGN_IN = 007;
//
//    private GoogleApiClient mGoogleApiClient;
//    private ProgressDialog mProgressDialog;
//
//    private SignInButton btnGooSignIn;
//    private Button btnSignOut, btnRevokeAccess;
//    private LinearLayout llProfileLayout;
//    private ImageView imgProfilePic;
//    private TextView txtStatus, txtName, txtEmail;
//
//
//    @Override
//    @SuppressWarnings("DEPRECATION")
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        setContentView(R.layout.activity_main);
//
//        //allocation variables to their respective buttons/images/etc
//        btnFbLogin = (LoginButton) findViewById(R.id.btn_facebook_login);
//        btnGooSignIn = (SignInButton) findViewById(R.id.btn_google_sign_in);
//        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
//        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
//        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
//        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
//        txtName = (TextView) findViewById(R.id.txtName);
//        txtEmail = (TextView) findViewById(R.id.txtEmail);
//
//        // when button clicked, stay on this activity
//        btnGooSignIn.setOnClickListener(this);
//        btnSignOut.setOnClickListener(this);
//        btnRevokeAccess.setOnClickListener(this);
//        initiallizeControls();
//
//        //Declaring GoogleSignInOptions to sign in via email
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        //Connecting the GoogleSignInOptions to the API client
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        // Customizing Google Sign In button
//        btnGooSignIn.setSize(SignInButton.SIZE_STANDARD);
//        btnGooSignIn.setScopes(gso.getScopeArray());
//
//        try {
//            //Creating a hash code for the Google Sign In with exceptions for different scenarios.
//            PackageInfo info = getPackageManager().getPackageInfo("ncirl.x15015556.detect_diabetic_reinopathy", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("hash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }
//
//    }
//
//    //Declaring the callbackManager, what the txtStatus and Facebook login buttons are
//    private void initiallizeControls() {
//        callbackManager = CallbackManager.Factory.create();
//        txtStatus = (TextView) findViewById(R.id.txt_status);
//        btnFbLogin = (LoginButton) findViewById(R.id.btn_facebook_login);
//    }
//
//    // Specifying logging into Facebook (Google sign in bypassed), ans what happens if successful, user cancels the action, or if it fails.
//    private void loginWithFb() {
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                txtStatus.setText("Login Success\n" + loginResult.getAccessToken());
//                Intent a = new Intent(SignInActivity.this, PatientActivity.class);
//                startActivity(a);
//
//            }
//
//            @Override
//            public void onCancel() {
//                txtStatus.setText("Login Cancelled");
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                txtStatus.setText("Login Error: " + error.getMessage());
//
//            }
//        });
//    }
//
//    //For Google Sign in, when the Google sign in is completed, this handles its result by sending the result to the handleSignInResult();
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }
//
//    //Signing in with Google and sending the user to a new intent/page when finished
//    private void signInWithGoogle() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//        Intent gmail = new Intent(SignInActivity.this, PatientActivity.class);
//
//    }
//
//    // To sign out the user from Google
//    private void signOut() {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        updateUI(false);
//                    }
//                });
//    }
//
//    // To allow the user to revoke access to Google Sign in.
//    private void revokeAccess() {
//        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        updateUI(false);
//                    }
//                });
//    }
//
//    //This sets the user details from Google and inserts them into text or image views. This was just used for testing originally.
//    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
//        if (result.isSuccess()) {
//            // Signed in successfully, show authenticated UI.
//            GoogleSignInAccount acct = result.getSignInAccount();
//
//            Log.e(TAG, "display name: " + acct.getDisplayName());
//
//            String personName = acct.getDisplayName();
//            String personPhotoUrl = acct.getPhotoUrl().toString();
//            String email = acct.getEmail();
//
//            Log.e(TAG, "Name: " + personName + ", email: " + email
//                    + ", Image: " + personPhotoUrl);
//
//            txtName.setText(personName);
//            txtEmail.setText(email);
//
//            //External directory that assists in inserting images into ImageViews.
//            Glide.with(getApplicationContext()).load(personPhotoUrl)
//            .into(imgProfilePic);
//
//            updateUI(true);
//        } else {
//            // Signed out, show unauthenticated UI.
//            updateUI(false);
//        }
//    }
//
//    //This resolves the question, "what happens when user clicks Button X, Y or Z?"
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//
//        switch (id) {
//            case R.id.btn_google_sign_in:
//                signInWithGoogle();
//                break;
//
//            case R.id.btn_facebook_login:
//                loginWithFb();
//                break;
//
//            case R.id.btn_sign_out:
//                signOut();
//                break;
//
//            case R.id.btn_revoke_access:
//                revokeAccess();
//                break;
//        }
//    }
//
//    //This is code directly from the Google Tutorial that wasn't coded correctly for this activity.
//    /*@Override
//public void onActivityResult(int requestCode, int resultCode, Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//
//    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//    if (requestCode == RC_SIGN_IN) {
//        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//        handleSignInResult(result);
//    }
//}*/
//
//    //Google's Sign in that deals with the result of the Google Sign in and updates the buttons to be hidden/visible.
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
//    }
//
//    // If the connection fails to sign in with Google, it is logged.
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
//        // be available.
//        Log.d(TAG, "onConnectionFailed: " + connectionResult);
//    }
//
//    //Shows thw progress of the Google sign in.
//    private void showProgressDialog() {
//        if (mProgressDialog == null){
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage("Loading...");
//            mProgressDialog.setIndeterminate(true);
//        }
//
//        mProgressDialog.show();
//    }
//
//    //When the Google Sign N progress is finished (regardless of result), the progress bar is hidden.
//    private void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.hide();
//        }
//    }
//
//    // This changes the visibility of certain buttons, depending on what buttons are needed
//    private void updateUI(boolean isSignedIn) {
//        if (isSignedIn) {
//            btnGooSignIn.setVisibility(View.GONE);
//            btnSignOut.setVisibility(View.VISIBLE);
//            btnRevokeAccess.setVisibility(View.VISIBLE);
//            llProfileLayout.setVisibility(View.VISIBLE);
//        }
//        else {
//            btnGooSignIn.setVisibility(View.VISIBLE);
//            btnSignOut.setVisibility(View.GONE);
//            btnRevokeAccess.setVisibility(View.GONE);
//            llProfileLayout.setVisibility(View.GONE);
//        }
//    }
//}
