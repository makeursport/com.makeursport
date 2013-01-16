package com.makeursport;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
/**
 * Dialog proposant de séléctionner une distance pour la génération de parcours
 *
 */
public class ParcoursDialog extends SherlockFragmentActivity implements OnClickListener,LocationListener{

	public final static String DISTANCE = "com.makeursport.DISTANCE";
	public final static String LATITUDE = "com.makeursport.LATITUDE";
	public final static String LONGITUDE = "com.makeursport.LONGITUDE";
	
	private float dist;
	private LocationManager lm;
	private Criteria critere;
	private Location loc = null;
	private EditText distET = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.parcours_dialog);
		distET = (EditText) this.findViewById(R.id.ET_distance);
		Button confirmBT = (Button) this.findViewById(R.id.BT_confirm);
		Button cancelBT = (Button) this.findViewById(R.id.BT_cancel);
		confirmBT.setOnClickListener(this);
		cancelBT.setOnClickListener(this);
		
		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		critere = new Criteria();
		
		critere.setAccuracy(Criteria.ACCURACY_FINE);
		critere.setAltitudeRequired(false);
		critere.setBearingRequired(true);
		critere.setCostAllowed(false);
		critere.setPowerRequirement(Criteria.POWER_MEDIUM);
		critere.setSpeedRequired(false);
		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		this.loc = location;
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.BT_confirm :
			if (this.loc != null){
				this.dist = Float.valueOf(this.distET.getText().toString());
				Intent data = new Intent();
				data.putExtra(ParcoursDialog.DISTANCE, this.dist);
				data.putExtra(ParcoursDialog.LATITUDE, loc.getLatitude());
				data.putExtra(ParcoursDialog.LONGITUDE, loc.getLongitude());
				//setResult(RESULT_OK, i);
				//finish();
				if (getParent() == null) {
				    setResult(RESULT_OK, data);
				} else {
				    getParent().setResult(RESULT_OK, data);
				}
				finish();
			}
			else {
				Toast.makeText(this, "Signal GPS invalide", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.BT_cancel :
			finish();
			break;
		}
	}

}
