package pago.com.pago.loginscreen.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import pago.com.pago.InternetConnect.ConnectionDetector;
import pago.com.pago.R;
import pago.com.pago.analytics.GoogleAnaly;
import pago.com.pago.dbconnect.JSONParser;
import pago.com.pago.main.controller.MainActivity;

import com.facebook.FacebookSdk;
import com.facebook.*;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pago.com.pago.analytics.GoogleAnaly.TrackerName;



public class LoginMain extends Activity implements View.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    /*GoogleAnalytics analytics = GoogleAnalytics.getInstance(getApplicationContext());
    Tracker tracker = analytics.newTracker("UA-63906684-1"); // Send hits to tracker id UA-XXXX-Y*/

    private static String url_create_user = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/create_newuser.php";

    private static final String TAG_SUCCESS = "success";

    CallbackManager callbackManager;
    LoginButton loginButton;
    TextView userName,email_id;
    String id,email,name,mobile,birthday,gender,personPhotoUrl;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private AccessToken accessToken;

    private String fbUserID;
    private String fbProfileName;
    private String fbAuthToken;
    int signin_click=0;
    boolean loggedIn=false;

    // G+ related Sign In
    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "LoginMain";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    private ImageView display_banner;
    private TextView txtViewInternet;
    private TextView skipLogin;

    public static final String MyPREFERENCES = "Login Detail" ;


    private FacebookCallback<LoginResult> mcallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();

            GraphRequest.newMeRequest(
                    accessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            if (response.getError() != null) {
                                // handle error
                                Log.e(TAG, response.getError().toString());
                            } else {
                                Profile profile = Profile.getCurrentProfile();

                                if(profile!= null){
                                    fbProfileName = profile.getName();
                                    name = profile.getName();
                                    userName.setVisibility(View.VISIBLE);

                                    fbAuthToken = accessToken.getToken();
                                    fbUserID = accessToken.getUserId();
                                    //personPhotoUrl = profile.getProfilePictureUri();


                                    Log.d(TAG, "User name: " + fbProfileName);
                                    Log.d(TAG, "User id: " + fbUserID);
                                    Log.d(TAG, "Access token is: " + fbAuthToken);
                                    Log.d(TAG, "Name: " + name);
                                }
                                email = me.optString("email");
                                Log.d("Email", email);
                                email_id.setVisibility(View.VISIBLE);
                                userName.setText("Welcome: " + fbProfileName);
                                email_id.setText(email);
                                birthday = me.optString("age_range");
                                gender = me.optString("gender");
                                Log.d("Gender", gender);
                                Log.d("Birthday", birthday);
                                //String id = me.optString("id");
                                btnSignIn.setVisibility(View.GONE);
                                skipLogin.setVisibility(View.GONE);
                                redirectToActivity();
                            }
                        }
                    }).executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.main_login);

        cd = new ConnectionDetector(getApplicationContext());

        File dir = getFilesDir();
        File filexml = new File(dir, "offer_details.xml");
        File file = new File(dir, "saved_offers");
        file.delete();
        filexml.delete();

        // Get Tracker for Google Analytics
        Tracker t = ((GoogleAnaly) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Login");
        t.send(new HitBuilders.ScreenViewBuilder().build());

/*        // Enable Advertising Features.
        t.enableAdvertisingIdCollection(true);*/

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        txtViewInternet = (TextView)findViewById(R.id.txtInternet);

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            txtViewInternet.setVisibility(View.GONE);

            loginButton = (LoginButton) findViewById(R.id.login_button);

            if(AccessToken.getCurrentAccessToken()!=null) {
                loggedIn = true;
                loginButton.setVisibility(View.GONE);
            }

            List<String> permissions = new ArrayList<String>();
            permissions.add("public_profile");
            permissions.add("user_friends");
            permissions.add("email");
           // permissions.add("user_birthday");
            //loginButton.setReadPermissions(Arrays.asList("basic_info", "email"));
            loginButton.setReadPermissions(permissions);
            userName = (TextView) findViewById(R.id.username);
            email_id = (TextView) findViewById(R.id.email_id);

            btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
            btnSignOut = (Button) findViewById(R.id.btn_sign_out);
            btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
            imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
            txtName = (TextView) findViewById(R.id.txtName);
            txtEmail = (TextView) findViewById(R.id.txtEmail);
            llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
            display_banner = (ImageView) findViewById(R.id.display_banner);
            skipLogin = (TextView)findViewById(R.id.skip_login);

            // Callback registration
            callbackManager = CallbackManager.Factory.create();

            skipLogin.setOnClickListener(this);

            // Google+ Button click listeners
            btnSignIn.setOnClickListener(this);
            btnSignOut.setOnClickListener(this);
            btnRevokeAccess.setOnClickListener(this);

            // Initializing google plus api client
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();


            if(loggedIn){
                accessToken = AccessToken.getCurrentAccessToken();
                GraphRequest.newMeRequest(
                    accessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            if (response.getError() != null) {
                                // handle error
                                Log.e(TAG, response.getError().toString());
                            } else {
                                Profile profile = Profile.getCurrentProfile();

                                if(profile!= null){
                                    fbProfileName = profile.getName();
                                    name = profile.getName();
                                    userName.setVisibility(View.VISIBLE);

                                    fbAuthToken = accessToken.getToken();
                                    fbUserID = accessToken.getUserId();
                                    personPhotoUrl = "https://graph.facebook.com/" + fbUserID + "/picture?type=large";
                                    Log.d(TAG, "User name: " + fbProfileName);
                                    Log.d(TAG, "User id: " + fbUserID);
                                    Log.d(TAG, "Access token is: " + fbAuthToken);
                                    Log.d(TAG, "Name: " + name);
                                }
                                email = me.optString("email");
                                Log.d("Email", email);
                                email_id.setVisibility(View.VISIBLE);
                                userName.setText("Welcome: " + fbProfileName);
                                email_id.setText(email);
                                birthday = me.optString("birthday");
                                gender = me.optString("gender");
                                Log.d("Gender", gender);
                                Log.d("Birthday", birthday);
                                //String id = me.optString("id");
                                btnSignIn.setVisibility(View.GONE);
                                skipLogin.setVisibility(View.GONE);
                                redirectToActivity();
                            }
                        }
                    }).executeAsync();
            }
/*
            // For FB Access
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(
                        AccessToken oldAccessToken,
                        AccessToken currentAccessToken) {
                    fbAuthToken = currentAccessToken.getToken();
                    fbUserID = currentAccessToken.getUserId();

                    Log.d(TAG, "User id: " + fbUserID);
                    Log.d(TAG, "Access token is: " + fbAuthToken);


                }
            };

            // For FB Profile Tracking
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(
                        Profile oldProfile,
                        Profile currentProfile) {
                    fbProfileName = currentProfile.getName();
                    name = currentProfile.getName();
                    loginButton.setVisibility(View.GONE);
                    userName.setVisibility(View.VISIBLE);

                    userName.setText("Welcome: " + fbProfileName);

                    Log.d(TAG, "User name: " + fbProfileName);
                }
            };*/

            loginButton.registerCallback(callbackManager, mcallback);

        }
        else{
            txtViewInternet.setVisibility(View.VISIBLE);
            txtViewInternet.setText("No Internet Connection");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
/*
    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }*/

    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(LoginMain.this).reportActivityStart(this);
        if(isInternetPresent) {
            mGoogleApiClient.connect();
        }
    }

    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(LoginMain.this).reportActivityStop(this);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                // Signin button clicked
                signin_click = 1;
                signInWithGplus();
                break;
            case R.id.btn_sign_out:
                // Signout button clicked
                signOutFromGplus();
                break;
            case R.id.btn_revoke_access:
                // Revoke access button clicked
                revokeGplusAccess();
                break;
            case R.id.skip_login:
                Intent intent = new Intent(LoginMain.this, MainActivity.class);

                Bundle extras = new Bundle();
                extras.putString("name", "");

                // add bundle to intent
                intent.putExtras(extras);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_to_center, R.anim.slide_center_to_left);
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if(!loggedIn) {
            btnSignIn.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            skipLogin.setVisibility(View.VISIBLE);
        }
        if(loggedIn) {
            loginButton.setVisibility(View.VISIBLE);
        }
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;

        // Get user's information
        getProfileInformation();

        Toast.makeText(this, name + " connected!", Toast.LENGTH_LONG).show();

        // Update the UI after signin
        //updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }

/*    public static String getDeviceId(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }*/

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     * */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            loginButton.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
            display_banner.setVisibility(View.GONE);

        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
            display_banner.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                name = currentPerson.getDisplayName();
                personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                int gend = currentPerson.getGender();
                if (gend == 0){
                    gender = "Male";
                }
                else{
                    gender = "Female";
                }
                birthday = currentPerson.getBirthday();

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + " , gender: " + gender
                        + " , DOB: " + birthday
                        + ", Image: " + personPhotoUrl);

                txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
                new InsertUserLogin().execute();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("Name", name);
                editor.putString("Gender",gender);
                editor.putString("Email",email);
                editor.putString("ImageURL",personPhotoUrl);
                editor.putString("DOB", birthday);
                // editor.commit();
                editor.apply();


                Intent intent = new Intent(LoginMain.this, MainActivity.class);

                Bundle extras = new Bundle();
                extras.putString("name", name);

                // add bundle to intent
                intent.putExtras(extras);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_to_center, R.anim.slide_center_to_left);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
           // mGoogleApiClient.connect();
            updateUI(false);
        }
    }

    /**
     * Revoking access from google
     * */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                        }

                    });
        }
    }

    class InsertUserLogin extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("mobile", mobile));

                JSONObject json = jsonParser.makeHttpRequest(
                        url_create_user, "GET", params);

                // check your log for json response
                Log.d("Create new user", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    private void redirectToActivity(){
        if(AccessToken.getCurrentAccessToken()!=null){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sp.edit();

            editor.putString("Name", name);
            editor.putString("Gender",gender);
            editor.putString("Email",email);
            editor.putString("ImageURL",personPhotoUrl);
            editor.putString("DOB", birthday);
           // editor.commit();
            editor.apply();


            Intent intent = new Intent(LoginMain.this, MainActivity.class);

            Bundle extras = new Bundle();
            extras.putString("name", name);

            // add bundle to intent
            intent.putExtras(extras);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_center, R.anim.slide_center_to_left);
            finish();
        }
    }

}
