package sudhanva.narayana.cryptick.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import sudhanva.narayana.cryptick.MainActivity;
import sudhanva.narayana.cryptick.R;
import sudhanva.narayana.cryptick.utils.Constants;

/**
 * Created by nsudh on 19-02-2016.
 */
public class LoginActivity extends AppCompatActivity {

    // Create a handler to handle the result of the authentication
    // See Firebase doc: https://www.firebase.com/docs/android/guide/user-auth.html
    // Remember this: Tokens issued to the authenticated users are
    // valid for 24 hours by default. You can change this from
    // the Login & Auth tab on your App Dashboard.
    Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
        @Override
        public void onAuthenticated(AuthData authData) {
            // Authenticated successfully with payload authData
            // Go to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            // Authenticated failed, show Firebase error to user
            showErrorMessageToUser(firebaseError.getMessage());
        }
    };
    private EditText mUserEmail;
    private EditText mUserPassWord;
    private Button mLoginToMChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView textViewX = (TextView) findViewById(R.id.signUpTextView);

        textViewX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Initialize
        mUserEmail = (EditText) findViewById(R.id.input_email);
        mUserPassWord = (EditText) findViewById(R.id.input_password);
        ;
        mLoginToMChat = (Button) findViewById(R.id.signInButton);

        // Log In click listener
        mLoginToMChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Validate input text */

                // Get user email and password
                String userName = mUserEmail.getText().toString();
                String passWord = mUserPassWord.getText().toString();

                // Omit space
                userName = userName.trim();
                passWord = passWord.trim();

                // validate fields
                if (userName.isEmpty() || passWord.isEmpty()) {
                    // show message when field is empty
                    showErrorMessageToUser(getString(R.string.dialog_title_error));

                } else {
                    // Log in
                    Firebase authenticateUser = new Firebase(Constants.FIREBASE_URL); // Get app main firebase url
                    authenticateUser.authWithPassword(userName, passWord, authResultHandler);
                }


            }
        });
    }

    private void showErrorMessageToUser(String errorMessage) {
        // Create an AlertDialog to show error message
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(errorMessage)
                .setTitle(getString(R.string.dialog_title_error))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
