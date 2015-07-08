package pago.com.pago.preferences.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pago.com.pago.R;

/**
 * Created by Saurabh on 6/3/2015.
 */

public class PreferenceFragment extends Fragment {


    public static PreferenceFragment newInstance()
    {
        return new PreferenceFragment();
    }

    // url to get all products list
    private static final String url_all_offers = "http://192.168.1.12/android_connect/get_all_products.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "Offers";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESC = "description";
    private static final String TAG_TITLE = "title";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restraunts_list, container, false);
        //productList.setAdapter(new RestrauntAdapter());
        return view;
    }





}

