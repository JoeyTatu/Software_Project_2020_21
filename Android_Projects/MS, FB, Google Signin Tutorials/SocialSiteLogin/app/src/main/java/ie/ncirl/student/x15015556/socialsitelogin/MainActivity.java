package ie.ncirl.student.x15015556.socialsitelogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {


    private TextView info;
    private ImageView profile;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = ( accessToken != null && !accessToken.isExpired() );

        //User will always need to sign on - This is to avoid other people accessing data.
        if (isLoggedIn == true){
            LoginManager.getInstance().logOut();
        }

        info = findViewById(R.id.info);
        profile = findViewById(R.id.profile);

        loginButton = (LoginButton)findViewById(R.id.loginButton);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    String userID = (String) object.get("id");
                                    String userName = (String) object.get("name");

                                    // image url is in root (object) -> picture -> data -> url
//                                    JSONObject pictureObj = (JSONObject) object.get("picture");
//                                    JSONObject dataObj = (JSONObject) pictureObj.get("data");
//                                    String imageURL = (String) dataObj.get("url");

                                    // above shorted to one line
                                    String imageURL = object.getJSONObject("picture").getJSONObject("data").getString("url");

//                                    imageURL = imageURL.replace("height=50", "height=100");
//                                    imageURL = imageURL.replace("width=50", "width=100");
                                    // Doesn't work. The link is absolute and cannot be changed.

//                                    Log.d("userID", replaced)

                                    Picasso.get()
                                            .load(imageURL)
                                            .into(profile);

                                    Log.d("userID", userID);
                                    Log.d("userID", userName);
                                    Log.d("userID", imageURL);
                                    Log.d("userID", loginResult.toString() );

                                    info.setText("Hello " + userName + ". Your user ID is: " + userID + ".");
                                    Log.d("o/p", "name");
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,birthday,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                info.setText("");
                profile.setImageResource(0);
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("An error occurred. Error: " + error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

//    protected Bitmap loadImage(String ut12) {
//        Log.v("Ut12--", ut12);
//        URL imageURL = null;
//
//        Bitmap bitmap = null;
//        try {
//            imageURL = new URL(ut12);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream inputStream = connection.getInputStream();
//
//            bitmap = BitmapFactory.decodeStream(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return bitmap;
//    }





//    public static Bitmap getFacebookProfilePicture(String userId) {
//        URL imageUrl;
//        Bitmap bitmap = null;
//
//        try {
//            imageUrl = new URL("https://graph.facebook.com/" + userId + "/picture?type=large");
//            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return bitmap;
//    }
}