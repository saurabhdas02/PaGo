package pago.com.pago.register.controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pago.com.pago.R;
import pago.com.pago.homescreen.controller.HomeScreenActivity;

public class RegistrationActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private EditText dateOfBirthEditText;
    private EditText cityEditText;
    private EditText numberEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private TextView usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getActionBar().hide();
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "Exo-SemiBoldItalic.otf");
        ((TextView)findViewById(R.id.welcome_text_view)).setTypeface(tf);
        dateOfBirthEditText = (EditText)findViewById(R.id.date_of_birth_edit_text);
        //dateOfBirthEditText.setOnClickListener(this);
        dateOfBirthEditText.setOnTouchListener(this);

        findViewById(R.id.signup_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);

        usernameEditText = (TextView) findViewById(R.id.username_edit_text);

    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };



    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateOfBirthEditText.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_to_center, R.anim.slide_center_to_right);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registration, menu);
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
            case R.id.date_of_birth_edit_text:
                /*new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
                break;
            case R.id.signup_button:
                /*if (usernameEditText.getText().toString().trim().equalsIgnoreCase(""))
                    usernameEditText.setError("Please enter username");
                else if (passwordEditText.getText().toString().trim().equalsIgnoreCase(""))
                    passwordEditText.setError("Please enter your password");
                else if (dateOfBirthEditText.getText().toString().trim().equalsIgnoreCase(""))
                    dateOfBirthEditText.setError("Please enter your date of birth");
                else if (nameEditText.getText().toString().trim().equalsIgnoreCase(""))
                    nameEditText.setError("Please enter your name");
                else if (numberEditText.getText().toString().trim().equalsIgnoreCase(""))
                    numberEditText.setError("Please enter your number");
                else if (cityEditText.getText().toString().trim().equalsIgnoreCase(""))
                    cityEditText.setError("Please enter your city");
                else {*/
                    Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_to_center, R.anim.slide_center_to_left);
                    finish();
                //}
                break;
            case R.id.cancel_button:
                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.date_of_birth_edit_text){
            new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            return true;
        }
        return false;
    }
}
