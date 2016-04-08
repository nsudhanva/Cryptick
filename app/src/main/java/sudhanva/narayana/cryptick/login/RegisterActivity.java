package sudhanva.narayana.cryptick.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sudhanva.narayana.cryptick.MainActivity;
import sudhanva.narayana.cryptick.R;
import sudhanva.narayana.cryptick.utils.Constants;

/**
 * Created by nsudh on 19-02-2016.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    private EditText mUserFirstNameRegister;
    private EditText mUserEmailRegister;
    private EditText mUserPassWordRegister;
    private Button mRegisterButton;
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Initialize
        mUserFirstNameRegister = (EditText) findViewById(R.id.input_name);
        mUserEmailRegister = (EditText) findViewById(R.id.input_email);
        mUserPassWordRegister = (EditText) findViewById(R.id.input_password);

        mRegisterButton = (Button) findViewById(R.id.signUpButton);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 /* validate input text */
                mAuthProgressDialog = new ProgressDialog(RegisterActivity.this);
                mAuthProgressDialog.setTitle("Signing You Up..");
                mAuthProgressDialog.setCancelable(false);
                mAuthProgressDialog.show();

                // Get name, email and password
                String userFirstName = mUserFirstNameRegister.getText().toString();
                String userEmail = mUserEmailRegister.getText().toString();
                String userPassword = mUserPassWordRegister.getText().toString();

                // Omit space
                userFirstName = userFirstName.trim();
                userEmail = userEmail.trim();
                userPassword = userPassword.trim();

                if (userFirstName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
                    // Show message when field is empty
                    showErrorMessageToUser(getString(R.string.register_error_message));
                } else {

                    /* Create new user and allow user to log in if successfully created*/

                    // note from Firebase: Creating an account will not log that new account in
                    // so you have to log user in automatically when account is successfully created

                    final Firebase registerMChatUser = new Firebase(Constants.FIREBASE_URL);  // Get app main firebase url
                    final String finalUserEmail = userEmail;
                    final String finalUserPassword = userPassword;
                    final String finalUserFirstName = userFirstName;

                    // Create new user
                    registerMChatUser.createUser(userEmail, userPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {

                            /* User registered successfully, so Log in automatically */

                            // Show a toast message for successfully registration
                            Toast.makeText(RegisterActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();

                            // Note from firebase: Creating an account will not log that new account in.
                            // Successfully created user account, and log the user in automatically

                            registerMChatUser.authWithPassword(finalUserEmail, finalUserPassword, new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(AuthData authData) {

                                    // Store user data necessary for the chat app

                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(Constants.KEY_PROVIDER, authData.getProvider()); // The authentication method used
                                    map.put(Constants.KEY_FIRST_NAME, finalUserFirstName);   // User first name
                                    map.put(Constants.KEY_USER_EMAIL, (String) authData.getProviderData().get(Constants.KEY_EMAIL)); // User email address
                                    map.put(Constants.CHILD_CONNECTION, Constants.KEY_ONLINE); // User status
                                    map.put(Constants.USER_TICK, 100);

                                    // Time user date is stored in database
                                    long createTime = new Date().getTime();
                                    map.put(Constants.KEY_TIMESTAMP, String.valueOf(createTime)); // Timestamp is string type

                                    // Store user data in the path https://<YOUR-FIREBASE-APP>.firebaseio.com/users/<uid>,
                                    // where users/ is any arbitrary path to store user data, and <uid> represents the
                                    // unique id obtained from the authentication data
                                    registerMChatUser.child(Constants.CHILD_USERS).child(authData.getUid()).setValue(map);


                                    // After storing, go to main activity
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mAuthProgressDialog.dismiss();
                                    startActivity(intent);

                                }

                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {
                                    // There is an error, and close the screen
                                    Toast.makeText(RegisterActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    mAuthProgressDialog.dismiss();
                                }
                            });

                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // There is an error in creating a user
                            //Log.e(TAG, "error creating user");
                            showErrorMessageToUser(firebaseError.getMessage());
                            mAuthProgressDialog.dismiss();
                        }
                    });


                }


            }
        });
    }

    private void showErrorMessageToUser(String errorMessage) {
        //Create an AlertDialog to show error message
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage(errorMessage)
                .setTitle(getString(R.string.dialog_title_error))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

