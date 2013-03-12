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
import android.widget.ProgressBar;
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
	/**
	 * La progressBar qui s'affiche avant de recuperer la position
	 */
	private ProgressBar loading = null;
	/**
	 * Le bouton confirmer
	 */
	private Button confirmBT = null;
	/**
	 * Le bouton annulé
	 */
	private Button cancelBT = null;
	/**
	 * Le contexte de l'activity en cours
	 */
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.parcours_dialog);
		this.distET = (EditText) this.findViewById(R.id.ET_distance);
		this.loading = (ProgressBar) this.findViewById(R.id.PB_gen_parcours);
		confirmBT = (Button) this.findViewById(R.id.BT_confirm);
		cancelBT = (Button) this.findViewById(R.id.BT_cancel);
		confirmBT.setOnClickListener(this);
		cancelBT.setOnClickListener(this);
		
		this.mContext=this;
		
		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	public void onLocationChanged(Location location) {
		this.loc = location;
		//Si l'utilisateur a cliqué sur confirmé et attend la génération de parcours on ferme le dialogue pour générer le parcours
		if (this.loading.getVisibility() == View.VISIBLE) {
			fermerDialog();
		}
	}

	public void onProviderDisabled(String provider) {}

	public void onProviderEnabled(String provider) {}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	/**
	 * Lors d'un click sur un des boutons du Dialog. setBackgroundDrawable est deprecié uniquement à partir de l'API 16,
	 * mais on l'utilise quand même avant
	 */
	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.BT_confirm :
			if(this.distET.getText().length() < 1 || this.distET.getText().toString().equalsIgnoreCase("0")) {
				Toast.makeText(mContext, mContext.getString(R.string.mauvaise_distance), Toast.LENGTH_LONG).show();
			}
			else if (this.loc != null){
				if (this.loading.getVisibility() == View.VISIBLE) {
					this.loading.setVisibility(View.GONE);
				}
				fermerDialog();
			}
			else {
				if (this.loading.getVisibility() != View.VISIBLE) {
					this.loading.setVisibility(View.VISIBLE);
					this.confirmBT.setEnabled(false);
					this.confirmBT.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_selector_disabled_holo_light));
				}
			}
			break;
		case R.id.BT_cancel :
			finish();
			break;
		}
	}
	
	/**
	 * Ferme le dialogue en transmettant les informations nécessaires à la génération de parcours
	 */
	private void fermerDialog() {
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

}
