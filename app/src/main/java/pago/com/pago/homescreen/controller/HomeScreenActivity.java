package pago.com.pago.homescreen.controller;

import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;

import pago.com.pago.InternetConnect.ConnectionDetector;
import pago.com.pago.OffersActivityNew;
import pago.com.pago.SupportFragment;
import pago.com.pago.filters.controller.MainFilter;
import pago.com.pago.offers.controller.OffersFragment;
import pago.com.pago.profile.controller.ProfileFragment;
import pago.com.pago.settingsdrawer.controller.NavigationDrawerFragment;
import pago.com.pago.R;
import pago.com.pago.settingsdrawer.model.SettingsDrawerInterface;
import pago.com.pago.preferences.controller.PreferenceFragment;


public class HomeScreenActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, SettingsDrawerInterface {

    private static final String PROFILE_SCREEN_FRAGMENT = "PROFILE_SCREEN_FRAGMENT";
    private static final String RESTRAUNTS_SCREEN_FRAGMENT = "RESTRAUNTS_SCREEN_FRAGMENT";
    private static final String PREFERENCES_SCREEN_FRAGMENT = "PREFERENCES_SCREEN_FRAGMENT";
    private static final String OFFERS_SCREEN_FRAGMENT = "OFFERS_SCREEN_FRAGMENT";
    private static final String SUPPORT_SCREEN_FRAGMENT = "SUPPORTS_SCREEN_FRAGMENT";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private boolean isRestrauntListVisible;
    private boolean isProfileScreenVisible = false;
    private boolean isRestrauntScreenVisible = false;
    private boolean isFriendsScreenVisible = false;
    private boolean isOffersScreenVisible = false;
    private boolean isPreferencesScreenVisible = false;
    private boolean isSupportScreenVisible = false;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */

    private CharSequence mTitle;
    String cat;
    int typeAct;

    //Internet Check
    ConnectionDetector cd;
    private TextView txtViewInternet;
    Boolean isInternetPresent = false;


    @Override
    public void onBackPressed() {
        /*if(isProfileScreenVisible){
            isProfileScreenVisible = false;
        }
        if(isAccountsScreenVisible){
            isAccountsScreenVisible = false;
        }*/
        if(mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.closeDrawer();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setSettingsDrawerInterfaceListner(this);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        // Intent for Particular Offers
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b !=null) {
            typeAct = b.getInt("activity");
            if (typeAct == 1) {
                cat = b.getString("category");

            }
        }


    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(HomeScreenActivity.this).reportActivityStart(this);
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(HomeScreenActivity.this).reportActivityStop(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, OffersActivityNew.newInstance(),RESTRAUNTS_SCREEN_FRAGMENT)
                .commit();
        isRestrauntListVisible = true;
        isRestrauntScreenVisible = true;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.login, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);*/
        getMenuInflater().inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
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
/*        if(id == R.id.action_second){
            Intent intent = new Intent(this, QrCodeScannerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_center,R.anim.slide_center_to_left);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    //////////////------------Settings drawer listner interface methods-----------------///////////////////////////////////////////////

    @Override
    public void profileButtonPressed() {
        mNavigationDrawerFragment.closeDrawer();
        if(!isProfileScreenVisible){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            removeAllFragments(fragmentManager,fragmentTransaction);
            fragmentTransaction.replace(R.id.container, ProfileFragment.newInstance(), PROFILE_SCREEN_FRAGMENT);
            fragmentTransaction.commit();
            isProfileScreenVisible = true;
        }
    }



    @Override
    public void homeButtonPressed() {
        mNavigationDrawerFragment.closeDrawer();
        if(!isRestrauntScreenVisible) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            removeAllFragments(fragmentManager,fragmentTransaction);
            fragmentTransaction.replace(R.id.container, OffersActivityNew.newInstance(), RESTRAUNTS_SCREEN_FRAGMENT);
            fragmentTransaction.commit();
            isRestrauntScreenVisible = true;
        }
    }

    @Override
    public void offersButtonPressed() {
        mNavigationDrawerFragment.closeDrawer();
        if(!isOffersScreenVisible) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            removeAllFragments(fragmentManager,fragmentTransaction);
            fragmentTransaction.replace(R.id.container, OffersFragment.newInstance(), OFFERS_SCREEN_FRAGMENT);
            fragmentTransaction.commit();
            isOffersScreenVisible = true;
        }
    }

    @Override
    public void PreferenceButtonPressed() {
        mNavigationDrawerFragment.closeDrawer();
        if(!isPreferencesScreenVisible){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            removeAllFragments(fragmentManager,fragmentTransaction);
            fragmentTransaction.replace(R.id.container, PreferenceFragment.newInstance(), PREFERENCES_SCREEN_FRAGMENT);
            fragmentTransaction.commit();
            isPreferencesScreenVisible = true;
        }
    }

    @Override
    public void SupportButtonPressed() {
        mNavigationDrawerFragment.closeDrawer();
        if(!isSupportScreenVisible){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            removeAllFragments(fragmentManager,fragmentTransaction);
            fragmentTransaction.replace(R.id.container, SupportFragment.newInstance(), SUPPORT_SCREEN_FRAGMENT);
            fragmentTransaction.commit();
            isSupportScreenVisible = true;
        }
    }

    private void removeAllFragments(FragmentManager fragmentManager,FragmentTransaction fragmentTransaction){

        if(isRestrauntScreenVisible){
            isRestrauntScreenVisible = false;
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(RESTRAUNTS_SCREEN_FRAGMENT));
                    //.commit();
        }
        if(isProfileScreenVisible){
            isProfileScreenVisible = false;
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(PROFILE_SCREEN_FRAGMENT));
                    //.commit();
            //fragmentManager.popBackStack();
        }

        if(isFriendsScreenVisible){
            isFriendsScreenVisible = false;
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(PREFERENCES_SCREEN_FRAGMENT));
            //.commit();
            //fragmentManager.popBackStack();
        }

        if(isOffersScreenVisible){
            isOffersScreenVisible = false;
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(OFFERS_SCREEN_FRAGMENT));
            //.commit();
            //fragmentManager.popBackStack();
        }

    }
}
