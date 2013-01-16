package com.makeursport;

import android.content.Context;
import android.content.Intent;
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
 * @author l'équipe MakeUrSport
 * 
 */
public class ParcoursDialog extends SherlockFragmentActivity implements OnClickListener,LocationListener{
	/**
	 * String permettant de gerer les transfert de la distance entre les fragment/activity
	 */
	public final static String DISTANCE = "com.makeursport.DISTANCE";
	/**
	 * String permettant de gerer les transfert de la latitude (de la loc de l'user) entre les fragments/activity
	 */
	public final static String LATITUDE = "com.makeursport.LATITUDE";
	/**
	 * String permettant de gerer les transfert de la longitute (de la loc de l'user) entre les fragments/activity
	 */
	public final static String LONGITUDE = "com.makeursport.LONGITUDE";
	/**
	 * Distance que souhaite parcourir l'user
	 */
	private float dist;
	/**
	 * Le locationManager qui nous aide a recuperer la localisation de l'utilisateur
	 */
	private LocationManager lm;
	/**
	 * La localisation de l'utilisateur 
	 */
	private Location loc = null;
	/**
	 * L'EditText permettant de recuperer la distance
	 */
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
		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	public void onLocationChanged(Location location) {
		this.loc = location;
	}

	public void onProviderDisabled(String provider) {}

	public void onProviderEnabled(String provider) {}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	/**
	 * Lors d'un click sur un des boutons du Dialog
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.BT_confirm :
			if (this.loc != null){
				this.dist = Float.valueOf(this.distET.getText().toString());
				Intent data = new Intent();
				data.putExtra(ParcoursDialog.DISTANCE, this.dist);
				data.putExtra(ParcoursDialog.LATITUDE, loc.getLatitude());
				data.putExtra(ParcoursDialog.LONGITUDE, loc.getLongitude());
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
