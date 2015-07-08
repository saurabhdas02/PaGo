package pago.com.pago;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import pago.com.pago.dbconnect.JSONParser;
import pago.com.pago.filters.controller.MainFilter;
import pago.com.pago.webview.GetWebView;


public class OffersActivity extends ListActivity {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    JSONParser jsonParser = new JSONParser();

    ArrayList<String> catList = new ArrayList<String>();
    ArrayList<String> brandList = new ArrayList<String>();
    ArrayList<String> merchantList = new ArrayList<String>();
    ArrayList<String> couponTypeList = new ArrayList<String>();
    ArrayList<String> bankList = new ArrayList<String>();
    String loc;


    ArrayList<HashMap<String, String>> offerslist;
    String[] pro = new String[8];

    // url to get all offers list
    private static String url_all_offers = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_all_products_new.php";
    private static String url_all_filter_offers = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_all_filtered_products.php";
    private static final String url_offer_details = "http://ec2-52-6-60-173.compute-1.amazonaws.com/vito_db/get_product_details_new.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_OFFERS = "offers";
    private static final String TAG_OFFER = "offer";
    private static final String TAG_OFFER_ID = "offer_id";
    private static final String TAG_DESC = "description";
    private static final String TAG_TITLE = "title";
    private static final String TAG_NAME = "name";
    private static final String TAG_TYPE = "coupon_type";
    private static final String TAG_BRAND = "brand";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_URL = "url";
    private static final String TAG_VALIDITY = "val";
    private static final String TAG_IMG = "image_url";
    private static final String TAG_COUPON_CODE = "coupon_code";

    // offers JSONArray
    JSONArray offers = null;
    String offerid;

    private Dialog dialog;
    String cat;
    int typeAct;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // Get listview
        ListView lv = getListView();


        // Intent for All Offers Button
        Intent button = getIntent();
        Bundle but = button.getExtras();
        if(but !=null) {
            typeAct = but.getInt("activity");
            if (typeAct == 2)
            {
                offerslist = new ArrayList<HashMap<String, String>>();
                new LoadFilteredOffers().execute();
            }

        }

        // Intent for Particular Offers
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b !=null) {
            typeAct = b.getInt("activity");
            if (typeAct == 1) {
                cat = b.getString("category");

                // Hashmap for ListView
                offerslist = new ArrayList<HashMap<String, String>>();

                // Loading offers in Background Thread
                new LoadAllOffers().execute();
//
//                if(TAG_VALIDITY.isEmpty()){
//                    TextView val = (TextView) findViewById(R.id.offer_val);
//                    TextView validity = (TextView) findViewById(R.id.offer_validity);
//                    val.setVisibility(View.GONE);
//                    validity.setVisibility(View.GONE);
//
//                }
            }
        }


        // Intent for Filter
        Intent p = getIntent();
        Bundle f = p.getExtras();
        if(f !=null) {
            typeAct = f.getInt("activity");
            if (typeAct == 3) {

                offerslist = new ArrayList<HashMap<String, String>>();

                loc = f.getString("location");
                catList = f.getStringArrayList("category");
                brandList = f.getStringArrayList("brand");
                merchantList = f.getStringArrayList("merchant");
                couponTypeList = f.getStringArrayList("coupon type");
                bankList = f.getStringArrayList("bank");

                new LoadFilteredOffers().execute();
            }

        }

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String proid = ((TextView) view.findViewById(R.id.offerid)).getText()
                        .toString();
                Log.d("TAG", "CLICKED row number: " + proid);
                offerid = proid;

                dialog = new Dialog(OffersActivity.this);
                // Setting view for custom Dialogue
                dialog.setContentView(R.layout.filter_dialogue);


                new GetOfferDetails().execute();
                // Set Title

                //Set Cancelable
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_title);
                dialog.getWindow().setTitleColor(getResources().getColor(R.color.white_color));


                ImageView img1 = (ImageView) dialog.findViewById(R.id.imv1);
                img1.setImageResource(R.drawable.ic_launcher);

                Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit1);

//                Button btnClose = (Button) dialog.findViewById(R.id.button_close);



                dialog.show();

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = pro[7];
                        Intent second = new Intent(OffersActivity.this, GetWebView.class);
                        Bundle extras = new Bundle();
                        extras.putString("offer_url", url);

                        // add bundle to intent
                        second.putExtras(extras);
                        startActivity(second);
                        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);

                    }
                });


            }
        });


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
        if(id == R.id.action_filter){
            // Start Filter Intent
            Intent intent = new Intent(this, MainFilter.class);
            Bundle extras = new Bundle();
            extras.putString("category", cat);

            // add bundle to intent
            intent.putExtras(extras);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
            return true;

        }
        /*if(id == R.id.action_second){
            Intent intent = new Intent(this, QrCodeScannerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }


    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllOffers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OffersActivity.this);
            pDialog.setMessage("Loading Offers. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All offers from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("category", cat));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_offers, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Offers: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // offers found
                    // Getting Array of Products
                    offers = json.getJSONArray(TAG_OFFERS);

                    // looping through All Products
                    for (int i = 0; i < offers.length(); i++) {
                        JSONObject c = offers.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_OFFER_ID);
                        String title = c.getString(TAG_TITLE);
//                        String desc = c.getString(TAG_DESC);
                        String name = c.getString(TAG_NAME);
//                        String image = c.getString(TAG_IMG);
                        String val = c.getString(TAG_VALIDITY);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_OFFER_ID, id);
                        map.put(TAG_TITLE, title);
                        map.put(TAG_NAME, name);
                        map.put(TAG_VALIDITY,val);
//                        map.put(TAG_DESC,desc);
//                        map.put(TAG_IMG,image);

                        // adding HashList to ArrayList
                        offerslist.add(map);
                    }
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
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */


                      ListAdapter adapter = new SimpleAdapter(
                            OffersActivity.this, offerslist,
                            R.layout.product_list, new String[]{TAG_OFFER_ID,
                            TAG_TITLE,TAG_NAME,TAG_VALIDITY},
                            new int[]{R.id.offerid, R.id.title, R.id.name, R.id.offer_validity});
                    // updating listview
                    setListAdapter(adapter);

                }
            });

        }

    }


    class LoadFilteredOffers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OffersActivity.this);
            pDialog.setMessage("Loading Fresh Offers. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All offers from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if(typeAct==3)
            {
                StringBuilder sb = new StringBuilder();

                // Convert ArrayList catList to String
                for(String str : catList){
                    sb.append('"').append(str).append('"').append(','); //separating contents using comma
                }

                String catFromArrayList = sb.toString();

                int len = catFromArrayList.length();
                if (len > 0) {
                    String catToParam = catFromArrayList.substring(0, len - 1);
                    params.add(new BasicNameValuePair("category", catToParam));

                }


                // Convert ArrayList brandList to String
                StringBuilder sb1 = new StringBuilder();
                for(String str : brandList)
                {
                    sb1.append('"').append(str).append('"').append(','); //separating contents using comma
                }

                 String brandFromArrayList = sb1.toString();

                len = brandFromArrayList.length();
                if (len >0) {
                   String brandToParam = brandFromArrayList.substring(0, len - 1);
                    params.add(new BasicNameValuePair("brand", brandToParam));

                }


                // Convert ArrayList merchantList to String
                StringBuilder sb2 = new StringBuilder();
                for(String str : merchantList){
                    sb2.append('"').append(str).append('"').append(','); //separating contents using comma
                }

                String merFromArrayList = sb2.toString();

                len = merFromArrayList.length();
                if (len >0) {
                    String merchantToParam = merFromArrayList.substring(0, len - 1);
                    params.add(new BasicNameValuePair("merchant", merchantToParam));
                }

                // Convert ArrayList couponTypeList to String
                StringBuilder sb3 = new StringBuilder();
                for(String str : couponTypeList){
                    sb3.append('"').append(str).append('"').append(','); //separating contents using comma
                }

                String couponFromArrayList = sb3.toString();

                len = couponFromArrayList.length();
                if (len>0) {
                   String couponTypeToParam = couponFromArrayList.substring(0, len - 1);
                    params.add(new BasicNameValuePair("coupon type", couponTypeToParam));

                }

                // Convert ArrayList bankList to String
                StringBuilder sb4 = new StringBuilder();
                for(String str : bankList){
                    sb4.append('"').append(str).append('"').append(','); //separating contents using comma
                }

                String bankFromArrayList = sb4.toString();

                len = bankFromArrayList.length();
                if (len >0) {
                    String bankToParam = bankFromArrayList.substring(0, len - 1);
                    params.add(new BasicNameValuePair("bank", bankToParam));
                }

                if (!loc.isEmpty()){
                    StringBuilder sb5 = new StringBuilder();
                        sb5.append('"').append(loc).append('"'); //separating contents using comma

                    String location = sb5.toString();
                    params.add(new BasicNameValuePair("location", location));
                }


            }

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_filter_offers, "GET", params);

            // Check your log cat for JSON reponse
//            Log.d("All Offers: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // offers found
                    // Getting Array of Products
                    offers = json.getJSONArray(TAG_OFFERS);

                    // looping through All Products
                    for (int i = 0; i < offers.length(); i++) {
                        JSONObject c = offers.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_OFFER_ID);
                        String title = c.getString(TAG_TITLE);
                        String val = c.getString(TAG_VALIDITY);
                        String name = c.getString(TAG_NAME);
//                        String desc = c.getString(TAG_DESC);
//                        String image = c.getString(TAG_IMG);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_OFFER_ID, id);
                        map.put(TAG_TITLE, title);
//                        map.put(TAG_DESC, desc);
                        map.put(TAG_NAME, name);
                        map.put(TAG_VALIDITY,val);
                       // map.put(TAG_IMG,image);

                        // adding HashList to ArrayList
                        offerslist.add(map);
                    }
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
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            OffersActivity.this, offerslist,
                            R.layout.product_list, new String[]{TAG_OFFER_ID,
                            TAG_TITLE,TAG_NAME,TAG_VALIDITY},
                            new int[]{R.id.offerid, R.id.title, R.id.name,R.id.offer_validity});
                    // updating listview
                    setListAdapter(adapter);

                    /*ImageView img_url = (ImageView)findViewById(R.id.img);
                    if(TAG_IMG.isEmpty()){

                    }
                    else {
                        img_url.setImageBitmap(getBitmapFromURL(TAG_IMG));
                    }*/
                }
            });

        }

    }

    /**
     * Background Async Task to Get complete product details
     * */
    class GetOfferDetails extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

                // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("offer_id", offerid));

                        JSONObject json = jsonParser.makeHttpRequest(
                                url_offer_details, "GET", params);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray offerObj = json
                                    .getJSONArray(TAG_OFFER); // JSON Array

                            // get first product object from JSON Array
                            JSONObject offer = offerObj.getJSONObject(0);

                            pro[0] = offer.getString(TAG_TITLE);
                            pro[1] = offer.getString(TAG_NAME);
                            pro[2] = offer.getString(TAG_DESC);
                            pro[3] = offer.getString(TAG_VALIDITY);
                            pro[4] = offer.getString(TAG_TYPE);
                            pro[5] = offer.getString(TAG_BRAND);
                            pro[6] = offer.getString(TAG_ADDRESS);
                            pro[7] = offer.getString(TAG_URL);



                        }else{
                            // product with offerid not found
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
            // dismiss the dialog once got all details
            runOnUiThread(new Runnable() {
                public void run() {
                    // product with this offerid found
                    TextView txtDesc = (TextView) dialog.findViewById(R.id.description);
                    TextView txtValidity = (TextView) dialog.findViewById(R.id.validity);
                    TextView txtCoupon = (TextView) dialog.findViewById(R.id.coupon_type);
                    TextView txtBrand = (TextView) dialog.findViewById(R.id.brand);
                    TextView txtAddress = (TextView) dialog.findViewById(R.id.address);


                    // String brand = product.getString(TAG_BRAND);

//                        if (brand.isEmpty()){
//
//                            txtBrand.setVisibility();
//                        }
                    // display product data in ViewText
                    dialog.setTitle(pro[1]);
                    txtDesc.setText(pro[2]);
                    txtValidity.setText(pro[3]);
                    txtCoupon.setText(pro[4]);
                    txtBrand.setText(pro[5]);
                    txtAddress.setText(pro[6]);

                    if (!TAG_VALIDITY.isEmpty() || !TAG_VALIDITY.equalsIgnoreCase(null)){
                       TextView val = (TextView) dialog.findViewById(R.id.val);
                        val.setText("Ends On: ");
                    }
                }
            });
        }
    }

}
