package com.example.billy.googlemap_test;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import sqlite.Databasehelper;

public class login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    Button button;
    EditText tendangnhap,matkhau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        setTitle("Login");
        button=findViewById(R.id.dangnhap);
        tendangnhap=findViewById(R.id.tendn);
        matkhau=findViewById(R.id.mk);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(login.this,"Successful.", Toast.LENGTH_LONG).show();
              ResultLogin();
            }

            @Override
            public void onCancel() {
                Toast.makeText(login.this,"Login attempt canceled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(login.this,"Login attempt failed.", Toast.LENGTH_LONG).show();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             String a=   tendangnhap.getText().toString().trim(); String b= matkhau.getText().toString().trim();

                if(a.equals("admin")&&b.equals("123456"))
                {
                    Intent intent=new Intent(login.this,detail_user.class);
                    startActivity(intent);
               }else
                {
                    Intent intent =new Intent(login.this, profile.class);
                    intent.putExtra("Name","Guess");
                    //intent.putExtra("image",img.toString());
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.btnSignIn).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [END build_client]
        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        SignInButton signInButton = (SignInButton) findViewById(R.id.btnSignIn);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        LoginManager.getInstance().logOut();


        // [END customize_button]
    }

    private void ResultLogin() {
        GraphRequest graphRequest=GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("json",response.getJSONObject().toString());
                try {
                    Name=object.getString("name").toString();
                    img= Profile.getCurrentProfile().getProfilePictureUri(100,100);
                    //txtNameFB.setText(txtNameFB.getText()+": cac "+object.getString("name").toString());
                    new LoadImage().execute(img.toString());//InsertInfo(img);
                    Toast.makeText(login.this,"You had login with " + Name,Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(login.this, profile.class);
                    intent.putExtra("Name",Name);
                    intent.putExtra("image",img.toString());
                    startActivity(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }


    @Override
    public void onStart() {

        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            try {
//                handleSignInResult(result);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    try {
//                        handleSignInResult(googleSignInResult);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        }

    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            try {
                handleSignInResult(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // [END onActivityResult]

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [START handleSignInResult]
    String Name;
    Uri img;
    private void handleSignInResult(GoogleSignInResult result) throws IOException {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Name=acct.getDisplayName();
            img=acct.getPhotoUrl();
         
            //InsertInfo(img);
            updateUI(true);


        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    Bitmap bitmapIMG;
  class LoadImage extends AsyncTask<String,Void,Bitmap>
  {
      Bitmap bitmap=null;
      @Override
      protected Bitmap doInBackground(String... strings) {
          try {
              URL url=new URL(strings[0]);
              InputStream inputStream=url.openConnection().getInputStream();
              bitmap= BitmapFactory.decodeStream(inputStream);
          } catch (MalformedURLException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
              e.printStackTrace();
          }
          return bitmap;
      }

      @Override
      protected void onPostExecute(Bitmap bitmap) {
          super.onPostExecute(bitmap);
          try {
              InsertInfo(bitmap);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
    void InsertInfo(Bitmap bitmap) throws IOException {

        Databasehelper myDatabase = new Databasehelper(this);
        SQLiteDatabase database;
        myDatabase.Khoitai();
        database = myDatabase.getMyDatabase();


        Log.d("img",img.toString());
        ArrayList<String> arrName = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select name from user", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                arrName.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){};

        int flag=1;
        for(String s:arrName)
        {
            if(s.equals(Name)) {flag=0; break;}
        }

        if(flag==1) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] array=byteArrayOutputStream.toByteArray();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", Name);
            contentValues.put("Password", "");
            contentValues.put("Image", array);
            database.insert("USER", null, contentValues);
        }

    }
        // [END handleSignInResult]
    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {

          //signOut();
            new LoadImage().execute(img.toString());//InsertInfo(img);
            Toast.makeText(this,"You had login with " + Name,Toast.LENGTH_LONG).show();
            Intent intent =new Intent(this, profile.class);
            intent.putExtra("Name",Name);
            intent.putExtra("image",img.toString());
            startActivity(intent);
        } else {
            findViewById(R.id.btnSignIn).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resource,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                signIn();
                mGoogleApiClient.clearDefaultAccountAndReconnect();
                break;
        }
    }
}