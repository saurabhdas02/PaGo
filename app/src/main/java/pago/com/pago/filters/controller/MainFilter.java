package pago.com.pago.filters.controller;

/**
 * Created by Saurabh on 3/19/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pago.com.pago.OffersActivity;
import pago.com.pago.OffersActivityNew;
import pago.com.pago.R;
import pago.com.pago.analytics.GoogleAnaly;
import pago.com.pago.dbconnect.JSONParser;
import pago.com.pago.homescreen.controller.HomeScreenActivity;

public class MainFilter extends Activity {

    private ProgressDialog pDialog;
    String cat;

    JSONParser jParser = new JSONParser();
    // Brands JSONArray
    JSONArray brands = null;
    JSONArray merchants = null;
    JSONArray locations = null;

    Spinner spinnerDropDown;
    String[] area;
    String location;

    List<String> catList;
    private static final String url_get_brands = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_brands.php";
    private static final String url_get_brands_new = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_brands_new.php";
    private static final String url_get_merchants = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_merchants.php";
    private static final String url_get_locations = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_location.php";
    private static final String url_get_merchants_new = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_merchants_new.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_BRANDS = "brands";
    private static final String TAG_BRAND_NAME = "brand_name";
    private static final String TAG_MERCHANTS = "merchants";
    private static final String TAG_MERCHANT_NAME = "merchant_name";
    private static final String TAG_LOCATIONS = "locations";
    private static final String TAG_LOCATION = "location";


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();

    // Google Tracker
    Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_filter);

        // Get Tracker for Google Analytics
        Tracker t = ((GoogleAnaly) getApplication()).getTracker(GoogleAnaly.TrackerName.APP_TRACKER);
        t.setScreenName("Login");
        t.send(new HitBuilders.ScreenViewBuilder().build());

/*
        // Enable Advertising Features.
        t.enableAdvertisingIdCollection(true);
*/

        Intent i = getIntent();
        Bundle b = i.getExtras();
        cat = b.getString("category");

        // Get reference of SpinnerView from layout/main_activity.xml
        spinnerDropDown =(Spinner)findViewById(R.id.spinner1);

        new prepareSpinnerData().execute();


        spinnerDropDown.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // Get select item
                int sid=spinnerDropDown.getSelectedItemPosition();
                if (area[sid].isEmpty()){
                    location = area[sid];
                }
                else {
                    Toast.makeText(getBaseContext(), "Selected Area: " + area[sid],
                            Toast.LENGTH_SHORT).show();
                    location = area[sid];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        new prepareListData().execute();



        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(MainFilter.this,
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT).show();


                return false;
            }

        });


        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                int len = listAdapter.getGroupCount();
                for (int i = 0; i < len; i++) {
                    if (i != groupPosition) {
                        expListView.collapseGroup(i);
                    }
                }
            }
        });

    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(MainFilter.this).reportActivityStart(this);
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(MainFilter.this).reportActivityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.offers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_apply) {
            Intent intent = new Intent(MainFilter.this, HomeScreenActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("activity", 3);
            extras.putString("location",location);
            extras.putStringArrayList("category", (ArrayList<String>)listAdapter.catList);
            extras.putStringArrayList("brand", (ArrayList<String>)listAdapter.brandList);
            extras.putStringArrayList("merchant", (ArrayList<String>)listAdapter.merchantList);
            extras.putStringArrayList("coupon type", (ArrayList<String>)listAdapter.couponTypeList);
            extras.putStringArrayList("bank", (ArrayList<String>)listAdapter.bankList);

            // add bundle to intent
            intent.putExtras(extras);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_to_center,R.anim.slide_right_to_center);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    /*
     * Preparing the list data
     */

    class prepareSpinnerData extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_locations, "GET", params);

            List<String> location = new ArrayList<String>();

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // offers found
                    // Getting Array of Products
                    locations = json.getJSONArray(TAG_LOCATIONS);

                    area = new String[locations.length()];
                    // looping through All Products
                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject c = locations.getJSONObject(i);

                        // Storing each json item in variable
                        location.add(c.getString(TAG_LOCATION));
                        area[i] = c.getString(TAG_LOCATION);
                        Log.d("Locations",area[i].toString());


                    }
                }


            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all offers
            //pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */

                    ArrayAdapter<String> adapter= new ArrayAdapter<String>(MainFilter.this,android.
                            R.layout.simple_spinner_item ,area);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerDropDown.setAdapter(adapter);
                }
            });

        }
    }


    class prepareListData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainFilter.this);
            pDialog.setMessage("Loading Brands & Merchant.. Just a sec...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All Brands from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("category", cat));

            List<String> newBrand = new ArrayList<String>();
            List<String> newMerchant = new ArrayList<String>();

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_merchants, "GET", params);


            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // offers found
                    // Getting Array of Products
                    merchants = json.getJSONArray(TAG_MERCHANTS);

                    // looping through All Products
                    for (int i = 0; i < merchants.length(); i++) {
                        JSONObject c = merchants.getJSONObject(i);

                        // Storing each json item in variable
                        newMerchant.add(c.getString(TAG_MERCHANT_NAME));
                    }

                }

                // getting JSON string from URL
                json = jParser.makeHttpRequest(url_get_brands, "GET", params);


                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of Products
                    brands = json.getJSONArray(TAG_BRANDS);


                    // looping through All Products
                    for (int i = 0; i < brands.length(); i++) {
                        JSONObject c = brands.getJSONObject(i);

                        // Storing each json item in variable
                        newBrand.add(c.getString(TAG_BRAND_NAME));
                    }
                }


                // Adding child data
                listDataHeader.add("Category");
                listDataHeader.add("Merchant");
                listDataHeader.add("Brand");
                listDataHeader.add("Coupon Type");
                listDataHeader.add("Bank");

                // Adding child data
                List<String> Category = new ArrayList<String>();
                Category.add("Clothing & Accessories");
                Category.add("Electronics");
                Category.add("Home & Kitchen");
                Category.add("Sports Fitness & Outdoors");
                Category.add("Books & Media");
                Category.add("Watches & Jewellery");
                Category.add("Restaurant");
                Category.add("Travel");
                Category.add("Handbags & Luggage");
                Category.add("Shoes");
                Category.add("Beauty Health & Gourmet");
                Category.add("Toys & Baby Products");
                Category.add("Pet Supplies");
                Category.add("Stores Experience & Other");

    //            List<String> Merchant = new ArrayList<String>();
    //            Merchant.add("Flipkart");
    //            Merchant.add("Myntra");
    //            Merchant.add("SnapDeal");
    //            Merchant.add("Jabong");
    //            Merchant.add("Amazon");
    //            Merchant.add("e-bay");
    //
    //            List<String> Brand = new ArrayList<String>();
    //            Brand.add("Adidas");
    //            Brand.add("Nike");
    //            Brand.add("Puma");
    //            Brand.add("Samsung");
    //            Brand.add("Apple");
    //            Brand.add("LG");

                List<String> CouponType = new ArrayList<String>();
                CouponType.add("Top 10");
                CouponType.add("New");
                CouponType.add("Cash Back");
                CouponType.add("Exchange");
                CouponType.add("Discount");
                CouponType.add("Exclusive");


                List<String> Bank = new ArrayList<String>();
                Bank.add("SBI");
                Bank.add("HDFC");
                Bank.add("ICICI");
                Bank.add("American Express");
                Bank.add("Citi Bank");
                Bank.add("Axis Bank");


                listDataChild.put(listDataHeader.get(0), Category); // Header, Child data
                listDataChild.put(listDataHeader.get(1), newMerchant);
                listDataChild.put(listDataHeader.get(2), newBrand);
                listDataChild.put(listDataHeader.get(3), CouponType); // Header, Child data
                listDataChild.put(listDataHeader.get(4), Bank);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all offers
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */

                    listAdapter = new ExpandableListAdapter(MainFilter.this, listDataHeader, listDataChild, cat);

                    // setting list adapter
                    expListView.setAdapter(listAdapter);

                }
            });

        }
    }

    public void GetBrands(List _GroupStrings, HashMap<String, List<String>> _ChildStrings,List<String> _catlist){

        listDataHeader = (ArrayList<String>) _GroupStrings;
        listDataChild = _ChildStrings;
        catList = _catlist;

        new LoadAllBrands().execute();
    }
    class LoadAllBrands extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(MainFilter.this);
//            pDialog.setMessage("Loading Brands.. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }

        /**
         * getting All Brands from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            listDataChild.get(listDataHeader.get(2)).clear();

            StringBuilder sb = new StringBuilder();
            //catList = listAdapter.catList;

            for(String str : catList){
                sb.append('"').append(str).append('"').append(','); //separating contents using comma
            }

            String strFromArrayList = sb.toString();

            int len = strFromArrayList.length();
            String strToParam = strFromArrayList.substring(0,len-1);

            params.add(new BasicNameValuePair("category",strToParam));

            // getting JSON string from URL
            JSONObject j = jParser.makeHttpRequest(url_get_brands_new, "GET", params);


            try {
                // Checking for SUCCESS TAG
                int success = j.getInt(TAG_SUCCESS);
                brands = null;

                if (success == 1) {
                    // offers found
                    // Getting Array of Products
                    brands = j.getJSONArray(TAG_BRANDS);

                    List<String> newBrand = new ArrayList<String>();

                    for (int i = 0; i < brands.length(); i++) {
                        JSONObject c = brands.getJSONObject(i);

                        // Storing each json item in variable
                        newBrand.add(c.getString(TAG_BRAND_NAME));

                    }

                    listDataChild.put(listDataHeader.get(2),newBrand);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            listDataChild.get(listDataHeader.get(1)).clear();

            j = jParser.makeHttpRequest(url_get_merchants_new, "GET", params);

            try {
                // Checking for SUCCESS TAG
                int success = j.getInt(TAG_SUCCESS);
                merchants = null;

                if (success == 1) {
                    merchants = j.getJSONArray(TAG_MERCHANTS);

                    List<String> newMerchant = new ArrayList<String>();

                    // looping through All Products
                    for (int i = 0; i < merchants.length(); i++) {
                        JSONObject c = merchants.getJSONObject(i);

                        // Storing each json item in variable
                        newMerchant.add(c.getString(TAG_MERCHANT_NAME));

                    }

                    listDataChild.put(listDataHeader.get(1),newMerchant);
//                    listAdapter.update(listDataHeader,listDataChild);

                }
                } catch (JSONException e) {
                    e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all offers
            //pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    listAdapter = new ExpandableListAdapter(MainFilter.this, listDataHeader, listDataChild);
                    listAdapter.notifyDataSetChanged();

                }
            });
        }

    }

}

