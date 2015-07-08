package pago.com.pago.offers.controller;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import pago.com.pago.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class OffersFragment extends Fragment {


    public OffersFragment() {
        // Required empty public constructor
    }

    ListView lv;
    ArrayList<HashMap<String, String>> offerslist;

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

    String text = null;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_offer_new, container, false);
        lv = (ListView) view.findViewById(R.id.listView1);
        TextView txtViewInternet = (TextView) view.findViewById(R.id.txtInternet);
        txtViewInternet.setVisibility(View.GONE);

        offerslist = new ArrayList<HashMap<String, String>>();

        String fileName = "offer_details.xml";
        FileInputStream fis;
        InputStreamReader isr;

        // XMLPull Parser
        try {
            fis = getActivity().openFileInput(fileName);
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();

            parser.setInput(fis, null);

            Log.d(fileName,parser.toString());
            int event = parser.getEventType();
            HashMap<String,String> map = null;

            while (event != XmlPullParser.END_DOCUMENT)
            {
                // get tag name
                String tagName = parser.getName();

                switch (event){

                    // at start of document: START_DOCUMENT
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if(tagName.equalsIgnoreCase("Offers")){
                            map =  new HashMap<String, String>();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagName.equalsIgnoreCase(TAG_OFFER_ID)) {
                            map.put(TAG_OFFER_ID, text);
                        }
                        else  if(tagName.equalsIgnoreCase(TAG_TITLE)) {
                            map.put(TAG_TITLE, text);
                        }
                        else  if(tagName.equalsIgnoreCase(TAG_NAME)) {
                            map.put(TAG_NAME, text);
                        }
                        else  if(tagName.equalsIgnoreCase(TAG_VALIDITY)) {
                            map.put(TAG_VALIDITY,text);
                            offerslist.add(map);
                        }

                }
                event = parser.next();
            }

        /*    if(map!=null){
                offerslist.add(map);
            }*/
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // On selecting single product
        ListAdapter adapter = new SimpleAdapter(
                getActivity(), offerslist,
                R.layout.product_list, new String[]{TAG_OFFER_ID,
                TAG_TITLE,TAG_NAME,TAG_VALIDITY},
                new int[]{R.id.offerid, R.id.title, R.id.name,R.id.offer_validity});
        lv.setAdapter(adapter);
        return view;
    }

    public static OffersFragment newInstance() {
        return new OffersFragment();
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
}
