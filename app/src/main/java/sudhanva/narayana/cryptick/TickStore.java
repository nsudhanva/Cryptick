package sudhanva.narayana.cryptick;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import sudhanva.narayana.cryptick.utils.Constants;

/**
 * Created by nsudh on 10-04-2016.
 */
public class TickStore extends AppCompatActivity {

    public String recipient;
    public String flag;
    public Firebase updateBal;
    private int radiox;

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton:
                if (checked)
                    radiox = 100;
                break;
            case R.id.radioButton2:
                if (checked)
                    radiox = 500;
                break;
            case R.id.radioButton3:
                if (checked)
                    radiox = 1000;
                break;
            case R.id.radioButton4:
                if (checked)
                    radiox = 10000;
                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tick_store);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();


        Button buyButton = (Button) findViewById(R.id.buyButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipient = extras.getString("EXTRA_RID");
            flag = extras.getString("EXTRA_FLAG");
        }

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        if (flag != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(TickStore.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("₹" + radiox)
                        .setTitle(R.string.are_you_sure);

                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        updateBal = new Firebase(Constants.FIREBASE_URL + "/" + Constants.CHILD_USERS + "/" + recipient);
                        Log.i("Recipient ID", recipient);
                        updateBal.child("tickBal").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                if (currentData.getValue() == null) {
                                    currentData.setValue(1);
                                } else {
                                    currentData.setValue((Long) currentData.getValue() + radiox);
                                }
                                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {                                //This method will be called once with the results of the transaction.

                                finish();
                                Context context = getApplicationContext();
                                CharSequence text = "Congo! You have bought" + radiox + " ticks!";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        });

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Set other dialog properties

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
