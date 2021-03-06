package org.altbeacon.beaconreference;

import java.util.Collection;

import android.app.Activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        beaconManager.bind(this);
    }

    @Override 
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override 
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override 
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
           @Override
           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
              if (beacons.size() > 0) {
                  //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                  Beacon firstBeacon = beacons.iterator().next();
                 //logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getRssi() + " meters away.");
                  String s = "goob";
                  s = firstBeacon.toString().substring(7,11);

                  String digits = "0123456789ABCDEF";
                  s = s.toUpperCase();
                  int val = 0;
                  float ADCVal = 0;
                  for (int i = 0; i < s.length(); i++) {
                      char c = s.charAt(i);
                      int d = digits.indexOf(c);
                      val = 16*val + d;
                      ADCVal = val;
              }
               double UVVal = 0.00;
                    UVVal = ((0.00161133 * ADCVal) - 1)/0.1427096636866752;



                 logToDisplay(Integer.toString(val) + "\n" + Double.toString(UVVal) +  "\n" + " rssi: " + firstBeacon.getRssi());
                 //logToDisplay(firstBeacon.toString() + " rssi: " + firstBeacon.getRssi());
                 //logToDisplay(firstBeacon.getParserIdentifier()  + " rssi: " + firstBeacon.getRssi());

              }
           }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                editText.setText(line+"\n");
            }
        });
    }
}
