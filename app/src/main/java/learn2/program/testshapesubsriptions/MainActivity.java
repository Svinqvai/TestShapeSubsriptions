package learn2.program.testshapesubsriptions;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import learn2.program.billing_util.IabHelper;

public class MainActivity extends AppCompatActivity {
    Button buttonOne;
    Button buttonTwo;

    private static final int RC_REQUEST = 10001;
    private IabHelper mHelper;
    String base64EncodedPublicKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonOne = (Button) findViewById(R.id.buttonOne);
        buttonTwo = (Button) findViewById(R.id.buttonTwo);

        buttonOne.setOnClickListener(btnOneCL);
        buttonTwo.setOnClickListener(btnTwoCL);
        mHelper = new IabHelper(this, base64EncodedPublicKey);


    }
    private final View.OnClickListener btnOneCL = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Btn One", Toast.LENGTH_SHORT).show();
        }
    };

    private final View.OnClickListener btnTwoCL = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this,"Btn Two",Toast.LENGTH_SHORT).show();
        }
    };
}
