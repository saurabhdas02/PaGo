package pago.com.pago;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by Saurabh on 6/3/2015.
 */

public class SupportFragment extends Fragment  {


    public static SupportFragment newInstance()
    {
        return new SupportFragment();
    }


    String email,body;
    EditText et;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        TextView tv = (TextView)view.findViewById(R.id.txtSupport);
        et = (EditText)view.findViewById(R.id.userFeedback);
        email = "contact@thinkanalytics.in";

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                body = et.getText().toString();
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vito FeedBack");
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
                Log.v("Mailer",body);
                startActivity(Intent.createChooser(emailIntent, "Send email via:"));

            }
        });


        return view;
    }





}

