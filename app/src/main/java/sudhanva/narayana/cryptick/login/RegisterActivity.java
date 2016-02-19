package sudhanva.narayana.cryptick.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import sudhanva.narayana.cryptick.R;

/**
 * Created by nsudh on 19-02-2016.
 */
public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        setContentView(R.layout.activity_register);
    }
}
