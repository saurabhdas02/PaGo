package pago.com.pago.profile.controller;



import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pago.com.pago.R;
import pago.com.pago.homescreen.controller.HomeScreenActivity;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {


    private EditText dateOfBirthEditText;
    private TextView txtViewUserName;
    private TextView txtViewGender;
    private TextView txtViewemail;
    //private TextView txtViewBirthday;
    private ImageView imgProfilePic;

    String email,name,mobile,birthday,gender,imageurl;


    public static final String MyPREFERENCES = "Login Detail" ;

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

        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateOfBirthEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        email = prfs.getString("Email", "");
        name = prfs.getString("Name","");
        gender = prfs.getString("Gender","");
        imageurl = prfs.getString("ImageURL","");
        birthday = prfs.getString("DOB", "");
        birthday = birthday.replace('-', '/');

        txtViewemail = (TextView)view.findViewById(R.id.userEmail);
        txtViewUserName= (TextView)view.findViewById(R.id.Name);
        txtViewGender = (TextView)view.findViewById(R.id.userGender);
        //txtViewBirthday = (TextView)view.findViewById(R.id.userBirthday);
        imgProfilePic = (ImageView)view.findViewById(R.id.imgProfilePic);

        txtViewemail.setText(email);
        txtViewUserName.setText(name);
        txtViewGender.setText(gender);

        dateOfBirthEditText = (EditText)view.findViewById(R.id.date_of_birth_edit_text);
        if(birthday.contains("null") || birthday.isEmpty()) {
            //dateOfBirthEditText.setOnClickListener(this);
            //txtViewBirthday.setVisibility(View.GONE);
            //dateOfBirthEditText.setVisibility(View.VISIBLE);
            dateOfBirthEditText.setEnabled(true);
            dateOfBirthEditText.setOnTouchListener(this);
        }
        else {
            //txtViewBirthday.setText(birthday);
            dateOfBirthEditText.setText(birthday);
            new LoadProfileImage(imgProfilePic).execute(imageurl);
        }


        Log.e("Login", "Name: " + name + ", email: " + email
                + " , gender: " + gender
                + " , DOB: " + birthday
                + ", Image: " + imageurl);

        view.findViewById(R.id.signup_button).setOnClickListener(this);
        return view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.date_of_birth_edit_text){
            new DatePickerDialog(getActivity(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        ((HomeScreenActivity)getActivity()).homeButtonPressed();
    }

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

}
