package pago.com.pago.loginscreen.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pago.com.pago.R;
import pago.com.pago.homescreen.controller.HomeScreenActivity;
import pago.com.pago.register.controller.RegistrationActivity;

public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText passwordEditText;
    private EditText usernameEditText;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();

        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Freestyle.ttf");
        ((TextView)findViewById(R.id.welcome_text_view)).setTypeface(tf);
        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                if (usernameEditText.getText().toString().trim().equalsIgnoreCase(""))
                    usernameEditText.setError("Please enter username");
                else if (passwordEditText.getText().toString().trim().equalsIgnoreCase(""))
                    passwordEditText.setError("Please enter your password");
                else {
                    Intent intent = new Intent(this, HomeScreenActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_to_center, R.anim.slide_center_to_left);
                    finish();
                }
                break;
            case R.id.register_button:
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_to_center, R.anim.slide_center_to_left);

                break;
        }
    }
}
