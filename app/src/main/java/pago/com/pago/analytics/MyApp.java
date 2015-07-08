package pago.com.pago.analytics;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Saurabh on 6/9/2015.
 */
public class MyApp extends Application {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    public void onCreate() {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-63906684-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

    }
}