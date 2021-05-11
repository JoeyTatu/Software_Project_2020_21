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
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.WindowInsets;
//import android.view.WindowInsetsController;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//
//import static ncirl.x15015556.detect_diabetic_reinopathy.Global.facebookID;
//import static ncirl.x15015556.detect_diabetic_reinopathy.Global.googleID;
//import static ncirl.x15015556.detect_diabetic_reinopathy.Global.signedIn;
//
//public class SignInActivity extends AppCompatActivity {
//
//    private LoginButton fbLoginButton;
//    public CallbackManager callbackManager;
//
//
//    @Override
//    @SuppressWarnings("DEPRECATION")
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
//            // Removes the header/title bar in app
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
////        facebookID = "1234"; // Testing to see if when signed in, is SignInActivity bypassed.
//
//        // Checks if user is already signed in
//        // If user is signed in, SignInActivity is skipped.
//        if( (facebookID != null) || (googleID !=null)){
//            signedIn = true;
//            Log.d("Signed In? ", signedIn.toString() );
//            //Ignore this page, move to next
//            Intent intent = new Intent(this, FacebookSignInActivity.class);
//            startActivity(intent);
//        }
//        else{
//
//            callbackManager = CallbackManager.Factory.create();
//
//            fbLoginButton = findViewById(R.id.button_facebook_login);
//
//
//            fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                @Override
//                public void onSuccess(LoginResult loginResult) {
//                    facebookID = loginResult.getAccessToken().getUserId();
//                }
//
//                @Override
//                public void onCancel() {
//                    FacebookException error = new FacebookException("Login cancelled");
//                    onError(error);
//                }
//
//                @Override
//                public void onError(FacebookException error) {
//                    Toast.makeText(SignInActivity.this, "An error occurred: " + error, Toast.LENGTH_LONG).show();
//                }
//            });
//
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        Intent intent = new Intent(SignInActivity.this, FacebookSignInActivity.class);
//        startActivity(intent);
//    }
//}