package pago.com.pago;


import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import pago.com.pago.InternetConnect.ConnectionDetector;
import pago.com.pago.analytics.GoogleAnaly;
import pago.com.pago.dbconnect.JSONParser;
import pago.com.pago.filters.controller.MainFilter;
import pago.com.pago.webview.GetWebView;

public class OffersActivityNew extends Fragment {

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
    File newxmlfile;

    private final HashSet<Integer> imageFav = new HashSet<Integer>();
    private final HashSet<Integer> imageLike = new HashSet<Integer>();
    private final HashSet<Integer> imageDislike = new HashSet<Integer>();

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


    // Google Tracker
    Tracker t;
    // offers JSONArray
    JSONArray offers = null;
    String offerid;

    ListView lv;
    private Dialog dialog;
    String cat;
    int typeAct;
    //Internet Check
    ConnectionDetector cd;
    private TextView txtViewInternet;
    Boolean isInternetPresent = false;
    String email;

    OnClickListener imageClickListener;
    public static OffersActivityNew newInstance(){
        return new OffersActivityNew();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_offer_new, container, false);

        // Get listview
        newxmlfile = getActivity().getFileStreamPath("offer_details.xml");

        if (!newxmlfile.exists()){
            try{
                newxmlfile.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        lv = (ListView)view.findViewById(R.id.listView1);

        cd = new ConnectionDetector(getActivity().getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        txtViewInternet = (TextView)view.findViewById(R.id.txtInternet);

        if(isInternetPresent) {

            SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            email = prfs.getString("Email", "");

            // Get Tracker for Google Analytics
            t = ((GoogleAnaly) getActivity().getApplication()).getTracker(GoogleAnaly.TrackerName.APP_TRACKER);
            t.setScreenName("Offers");
            t.send(new HitBuilders.ScreenViewBuilder().build());

/*            // Enable Advertising Features.
            t.enableAdvertisingIdCollection(true);*/

            txtViewInternet.setVisibility(View.GONE);

            // Intent for All Offers Button
            Intent button = getActivity().getIntent();
            Bundle but = button.getExtras();
            if (but != null) {
                typeAct = but.getInt("activity");
                if (typeAct == 2) {
                    offerslist = new ArrayList<HashMap<String, String>>();
                    new LoadFilteredOffers().execute();
                }

            }

            // Intent for Particular Offers
            Intent i = getActivity().getIntent();
            Bundle b = i.getExtras();
            if (b != null) {
                typeAct = b.getInt("activity");
                if (typeAct == 1) {
                    cat = b.getString("category");

                    // Hashmap for ListView
                    offerslist = new ArrayList<HashMap<String, String>>();

                    // Loading offers in Background Thread
                    new LoadAllOffers().execute();
                }
            }


            // Intent for Filter
            Intent p = getActivity().getIntent();
            Bundle f = p.getExtras();
            if (f != null) {
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

            // On selecting single product
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // getting values from selected ListItem
                    String proid = ((TextView) view.findViewById(R.id.offerid)).getText()
                            .toString();
                    offerid = proid;

                    dialog = new Dialog(getActivity());
                    // Setting view for custom Dialogue
                    dialog.setContentView(R.layout.filter_dialogue);


                    new GetOfferDetails().execute();
                    // Set Title


                    //Google Analytics Event
                    t.setScreenName("Offers Dialogue");
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Offer clicked")
                            .setAction(email)
                            .setLabel(proid)
                            .build());

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
                            Intent second = new Intent(getActivity(), GetWebView.class);
                            Bundle extras = new Bundle();
                            extras.putString("offer_url", url);

                            // add bundle to intent
                            second.putExtras(extras);
                            startActivity(second);
                            getActivity().overridePendingTransition(R.anim.slide_right_to_center, R.anim.slide_center_to_left);

                        }
                    });


                }
            });
        }
        else{
            txtViewInternet.setVisibility(View.VISIBLE);
            txtViewInternet.setText("No Internet Connection");
        }
        return view;
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }*/

    @Override
    public void onResume(){
        super.onResume();
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
            Intent intent = new Intent(getActivity(), MainFilter.class);
            Bundle extras = new Bundle();
            extras.putString("category", cat);

            // add bundle to intent
            intent.putExtras(extras);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
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
            pDialog = new ProgressDialog(getActivity());
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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), offerslist,
                            R.layout.product_list, new String[]{TAG_OFFER_ID,
                                    TAG_TITLE, TAG_NAME, TAG_VALIDITY},
                            new int[]{R.id.offerid, R.id.title, R.id.name, R.id.offer_validity}){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {

                          //  Log.d("Convert View", "getView " + position + " " + convertView);

                            String fileName = "saved_offers";
                            StringBuilder sb = new StringBuilder();
                            try {
                                FileInputStream fis = getActivity().openFileInput(fileName);
                                InputStreamReader isr = new InputStreamReader(fis);
                                BufferedReader bufferedReader = new BufferedReader(isr);
                                String line;
                                while ((line = bufferedReader.readLine()) != null) {
                                    sb.append(line);
                                }
                                isr.close();
                                Log.d("File Read",String.valueOf(sb));
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                            ViewHolder holder;
                            if (convertView == null) {

                                LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                convertView = mInflater.inflate(R.layout.product_list, null);
                                holder = new ViewHolder();

                                holder.imgFavButton = (ImageView)convertView.findViewById(R.id.imgfavorite);
                                holder.imgShareButton = (ImageView)convertView.findViewById(R.id.imgShare);
                                holder.imgLikeButton = (ImageView)convertView.findViewById(R.id.imgLike);
                                holder.imgDislikeButton = (ImageView)convertView.findViewById(R.id.imgDislike);
                                holder.txtName = (TextView)convertView.findViewById(R.id.name);
                                holder.txtTitle = (TextView)convertView.findViewById(R.id.title);
                                holder.txtValidity = (TextView)convertView.findViewById(R.id.offer_validity);
                                holder.txtOfferID = (TextView)convertView.findViewById(R.id.offerid);
                                convertView.setTag(holder);
                            }
                            else {
                                holder = (ViewHolder)convertView.getTag();
                            }

                            holder.imgFavButton.setTag(position);
                            holder.imgLikeButton.setTag(position);
                            holder.imgDislikeButton.setTag(position);

                            //Check for Fav button
                            if(imageFav.contains(Integer.valueOf(position)) || sb.toString().contains(offerslist.get(position).get(TAG_OFFER_ID)))
                            {
                                holder.imgFavButton.setImageResource(R.drawable.ic_action_favorite_pinkfill);
                            }
                            else{
                                holder.imgFavButton.setImageResource(R.drawable.ic_action_favorite);
                            }

                            //Check for Like button
                            if(imageLike.contains(Integer.valueOf(position))){
                                holder.imgLikeButton.setImageResource(R.drawable.ic_action_good_bluefill);
                            }
                            else{
                                holder.imgLikeButton.setImageResource(R.drawable.ic_action_good);
                            }

                            //Check for Dislike button
                            if(imageDislike.contains(Integer.valueOf(position))){
                                holder.imgDislikeButton.setImageResource(R.drawable.ic_action_bad_greenfill);
                            }
                            else{
                                holder.imgDislikeButton.setImageResource(R.drawable.ic_action_bad);
                            }

                            holder.txtName.setText(offerslist.get(position).get(TAG_NAME));
                            holder.txtOfferID.setText(offerslist.get(position).get(TAG_OFFER_ID));
                            holder.txtTitle.setText(offerslist.get(position).get(TAG_TITLE));
                            holder.txtValidity.setText(offerslist.get(position).get(TAG_VALIDITY));

                            holder.imgFavButton.setOnClickListener(imageClickListener);
                            holder.imgShareButton.setOnClickListener(imageClickListener);
                            holder.imgLikeButton.setOnClickListener(imageClickListener);
                            holder.imgDislikeButton.setOnClickListener(imageClickListener);
                            return convertView;
                        }
                    };
                    // updating listview
                    lv.setAdapter(adapter);

                    imageClickListener =new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        switch (v.getId()){
                            case R.id.imgShare:
                                // your button is pressed on this view, position is stored in tag.
                                //Integer positionPressed=(Integer)v.getTag();
                                String shareBody = "For more amazing offers, Click here http://bit.ly/1SuMkYc";
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vito-");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                break;

                            case R.id.imgfavorite:
                                int position = (Integer)v.getTag();
                                String offer_id = offerslist.get(position).get(TAG_OFFER_ID).concat(",");

                                String fileName = "saved_offers";
                                int flag=0;
                                try {
                                    FileInputStream fis = getActivity().openFileInput(fileName);
                                    InputStreamReader isr = new InputStreamReader(fis);
                                    BufferedReader bufferedReader = new BufferedReader(isr);
                                    String line;
                                    while ((line = bufferedReader.readLine()) != null) {
                                        if(line.contains(offer_id)){
                                            flag=1;
                                            break;
                                        }
                                    }
                                    isr.close();
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                if(flag==0) {
                                    ImageView imgFav = (ImageView)v;
                                    imgFav.setImageResource(R.drawable.ic_action_favorite_pinkfill);
                                    imageFav.add(position);
                                    FileOutputStream outputStream;
                                    try
                                    {
                                        outputStream = getActivity().openFileOutput(fileName, Context.MODE_APPEND);
                                        outputStream.write(offer_id.getBytes());
                                        Log.d("Write File", "Saved_Offers written");
                                        outputStream.close();
                                        Toast.makeText(getActivity(), offerslist.get(position).get(TAG_NAME) + " offer added to favourites ", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try
                                    {
                                        String xmlFile = "offer_details.xml";
                                        boolean fileExist = fileExistance(xmlFile);

                                        //Fill Offers class variables
                                        Offers obj_offers = new Offers(offerslist.get(position).get(TAG_OFFER_ID),
                                                offerslist.get(position).get(TAG_TITLE),
                                                offerslist.get(position).get(TAG_NAME),
                                                offerslist.get(position).get(TAG_VALIDITY));
                                        updateFile(obj_offers);
/*
                                        if(!fileExist) {

                                            outputStream = getActivity().openFileOutput(xmlFile, Context.MODE_APPEND);
                                            XmlSerializer serializer = Xml.newSerializer();
                                            serializer.setOutput(outputStream, "UTF-8");
                                            serializer.startDocument(null, Boolean.valueOf(true));
                                            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                                            serializer.startTag(null, "Record");
                                            serializer.startTag(null, "Offers");
                                            serializer.attribute(null, "ID", offerslist.get(position).get(TAG_OFFER_ID));
                                            serializer.startTag(null, "Offer_id");
                                            serializer.text(obj_offers.getOfferid());
                                            serializer.endTag(null, "Offer_id");
                                            serializer.startTag(null, "Title");
                                            serializer.text(obj_offers.getTitle());
                                            serializer.endTag(null, "Title");
                                            serializer.startTag(null, "Name");
                                            serializer.text(obj_offers.getName());
                                            serializer.endTag(null, "Name");
                                            serializer.startTag(null, "Validity");
                                            serializer.text(obj_offers.getValidity());
                                            serializer.endTag(null, "Validity");
                                            serializer.endTag(null, "Offers");
                                            serializer.endTag(null,"Record");
                                            serializer.endDocument();
                                            serializer.flush();
                                            outputStream.close();
                                        }
                                        else {

                                            String data;
                                            FileInputStream fis;
                                            InputStreamReader isr;

                                                fis = getActivity().openFileInput(xmlFile);
                                                isr = new InputStreamReader(fis);
                                                char[] inputBuffer = new char[fis.available()];
                                                isr.read(inputBuffer);
                                                data = new String(inputBuffer);
                                                isr.close();
                                                fis.close();
                                                Log.d("XML",data);


                                                // converting the String data to XML format
                                                // so that the DOM parser understand it as an XML input.

                                            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
                                            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                                            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                                            Document document = documentBuilder.parse(is);
                                            Element root = document.getDocumentElement();

                                            Collection<Offers> offerses = new ArrayList<Offers>();
                                            offerses.add(obj_offers);

                                            for (Offers offer : offerses) {
                                                // server elements
                                                Element newServer = document.createElement("Offers");
                                                root.appendChild(newServer);

                                                Element offerId = document.createElement("Offer_id");
                                                offerId.appendChild(document.createTextNode(offer.getOfferid()));
                                                newServer.appendChild(offerId);

                                                Element Title = document.createElement("Title");
                                                Title.appendChild(document.createTextNode(offer.getTitle()));
                                                newServer.appendChild(Title);

                                                Element name = document.createElement("Name");
                                                name.appendChild(document.createTextNode(offer.getName()));
                                                newServer.appendChild(name);

                                                Element validity = document.createElement("Validity");
                                                validity.appendChild(document.createTextNode(offer.getValidity()));
                                                newServer.appendChild(validity);

                                                root.appendChild(newServer);
                                            }

                                            DOMSource source = new DOMSource(document);

                                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                            Transformer transformer = transformerFactory.newTransformer();
                                            StreamResult result = new StreamResult(xmlFile);
                                            transformer.transform(source, result);
                                        }*/
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    Toast.makeText(getActivity(), "Offer already in favourites!, Not Added", Toast.LENGTH_SHORT).show();
                                }

                                break;

                            case R.id.imgLike:
                                position = (Integer)v.getTag();
                                ImageView imgLike = (ImageView)v;
                                imgLike.setImageResource(R.drawable.ic_action_good_bluefill);
                                imageLike.add(position);
                                // Toast.makeText(getActivity(),  + " connected!", Toast.LENGTH_LONG).show();
                                break;

                            case R.id.imgDislike:
                                position = (Integer)v.getTag();
                                ImageView imgDislike = (ImageView)v;
                                imgDislike.setImageResource(R.drawable.ic_action_bad_greenfill);
                                imageDislike.add(position);
                                // Toast.makeText(getActivity(),  + " connected!", Toast.LENGTH_LONG).show();
                                break;
                        }

                        }
                    };
                }
            });

        }

    }

    public static class ViewHolder {
        public ImageView imgShareButton;
        public ImageView imgFavButton;
        public ImageView imgLikeButton;
        public ImageView imgDislikeButton;
        public TextView txtName;
        public TextView txtTitle;
        public TextView txtValidity;
        public TextView txtOfferID;
    }


    class LoadFilteredOffers extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Fresh Offers. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


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

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all offers
            pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), offerslist,
                            R.layout.product_list, new String[]{TAG_OFFER_ID,
                            TAG_TITLE,TAG_NAME,TAG_VALIDITY},
                            new int[]{R.id.offerid, R.id.title, R.id.name,R.id.offer_validity});
                    // updating listview
                    lv.setAdapter(adapter);

                }
            });

        }

    }

    /**
     * Background Async Task to Get complete Offer details
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
            getActivity().runOnUiThread(new Runnable() {
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

    public boolean fileExistance(String fname){
        File file = getActivity().getFileStreamPath(fname);
        return file.exists();
    }

    public static class Offers {

        String Offer_id,Title,Name,Validity;

        public Offers(String _id, String title, String name, String validity){
            Offer_id = _id;
            Title = title;
            Name = name;
            Validity = validity;
        }
        public String getOfferid() { return Offer_id;};
        public String getTitle() {return Title;};
        public String getName(){return Name;};
        public String getValidity() {return Validity;};
    }

    /**
     * Appends new data to the file
     *
     * @param obj_offers
     */
    private void updateFile(Offers obj_offers) throws IOException {
        RandomAccessFile randomAccessFile = null;
        String closingLine = null;
        boolean fileExists = true;

        if (newxmlfile.length() == 0) {
            fileExists = false;

            try {
                randomAccessFile = new RandomAccessFile(newxmlfile, "rw");
            } catch(FileNotFoundException e) {
                Log.e("FileNotFoundException", "can't create FileOutputStream");
            }
        } else {
            try {
                randomAccessFile = new RandomAccessFile(newxmlfile, "rw");
                randomAccessFile.seek(0);

                String lastLine;
                long lastLineOffset = 0;
                int lastLineLength = 0;

                lastLine = randomAccessFile.readLine();

                while (lastLine != null) {
                    // +1 is for end line symbol
                    lastLineLength = lastLine.length();
                    lastLineOffset = randomAccessFile.getFilePointer();

                    closingLine = lastLine;
                    lastLine = randomAccessFile.readLine();
                }

                lastLineOffset -= lastLineLength;
                // got to string before last
                randomAccessFile.seek(lastLineOffset);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        // Now random access file is positioned properly, we can append new xml data
        //we create a XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();

        if (randomAccessFile == null) {
            return;
        }

        try {
            final StringWriter writer = new StringWriter();

            serializer.setOutput(writer);

            if (!fileExists) {
                serializer.startDocument(null, true);
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                serializer.startTag(null, "Record");
            } else {
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            }

            serializer.startTag(null, "Offers");

            serializer.startTag(null, "Offer_id");
            serializer.text(obj_offers.getOfferid());
            serializer.endTag(null, "Offer_id");

            serializer.startTag(null, "Title");
            serializer.text(obj_offers.getTitle());
            serializer.endTag(null, "Title");

            serializer.startTag(null, "Name");
            serializer.text(obj_offers.getName());
            serializer.endTag(null, "Name");

            serializer.startTag(null, "Val");
            serializer.text(obj_offers.getValidity());
            serializer.endTag(null, "Val");

            serializer.endTag(null, "Offers");

            if (!fileExists) {
                serializer.endTag(null, "Record");
            }

            serializer.flush();

            if (closingLine != null) {
                serializer.endDocument();
                writer.append("\n");
                writer.append(closingLine);
            }

            // Add \n just for better output in console
            randomAccessFile.writeBytes(writer.toString());
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            randomAccessFile.close();
        }
    }
}

