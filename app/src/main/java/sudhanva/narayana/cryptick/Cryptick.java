package sudhanva.narayana.cryptick;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by nsudh on 19-02-2016.
 */
public class Cryptick extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
