package pago.com.pago.main.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import pago.com.pago.InternetConnect.ConnectionDetector;
import pago.com.pago.OffersActivity;
import pago.com.pago.R;
import pago.com.pago.analytics.GoogleAnaly;
import pago.com.pago.homescreen.controller.HomeScreenActivity;
import pago.com.pago.webview.GetWebView;


public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = " ";
    String name;

    //Internet Check
    ConnectionDetector cd;
    private TextView txtViewInternet;
    Boolean isInternetPresent = false;

    //Google Tracker Initialize
    Tracker t;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            //if you want to lock screen for always Portrait mode
            setRequestedOrientation(ActivityInfo
                    .SCREEN_ORIENTATION_PORTRAIT);
            cd = new ConnectionDetector(getApplicationContext());

            // get Internet status
            isInternetPresent = cd.isConnectingToInternet();
            txtViewInternet = (TextView)findViewById(R.id.txtInternet);
            if(isInternetPresent) {

                //Google Analytics
                t = ((GoogleAnaly) getApplication()).getTracker(GoogleAnaly.TrackerName.APP_TRACKER);
                t.setScreenName("Category");
                t.send(new HitBuilders.ScreenViewBuilder().build());

 /*               // Enable Advertising Features.
                t.enableAdvertisingIdCollection(true);*/

                txtViewInternet.setVisibility(View.GONE);
                Intent button = getIntent();
                Bundle but = button.getExtras();
                if (but != null) {
                    name = but.getString("name");

                }
                if(!name.isEmpty() ) {
                    TextView userName = (TextView) findViewById(R.id.username);
                    userName.setText("Welcome:" + name);
                }
            }
            else{
                txtViewInternet.setVisibility(View.VISIBLE);
                txtViewInternet.setText("No Internet Connection");
            }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    public void allOffers(View view) {

        //Intent intent = new Intent(MainActivity.this, OffersActivity.class);
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);

        Bundle extras = new Bundle();
        extras.putInt("activity", 2);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);

    }

    public void getOffersClothing(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Clothing & Accessories")
                .build());

        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Clothing & Accessories";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersElectronics(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Electronics")
                .build());
        Intent first = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Electronics";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        first.putExtras(extras);
        startActivity(first);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersHome(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Home & Kitchen")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Home & Kitchen";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersSports(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Sports Fitness & Outdoors")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Sports Fitness & Outdoors";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersBooksAndMedia(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Books & Media")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Books & Media";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersJewellery(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Watches & Jewellery")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Watches & Jewellery";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersDining(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Restaurant")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Restaurant";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersTravel(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Travel")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Travel";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersHandbags(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Handbags & Luggage")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Handbags & Luggage";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersShoes(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Shoes")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Shoes";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersBeauty(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Beauty & Gourmet")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Beauty Health & Gourmet";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersToysAndBaby(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Toys & Baby Products")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Toys & Baby Products";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersPetSupply(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Pet Supplies")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Pet Supplies";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }
    public void getOffersStoreAndOthers(View view) {

        //Google Analytics Event
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("Stores Experience & Other")
                .build());
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String message = "Stores Experience & Other";
        Bundle extras = new Bundle();
        extras.putInt("activity", 1);
        extras.putString("category", message);

        // add bundle to intent
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
    }

}
