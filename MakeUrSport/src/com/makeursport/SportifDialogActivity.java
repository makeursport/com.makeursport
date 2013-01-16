package com.makeursport;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
/**
 * Classe pour le dialog proposant a
 * l'utilisateur d'entrer les infos du sportif
 * Cette classe hérite de {@link SherlockFragmentActivity} car
 * c'est une activitée
 * Et elle implémente {@link OnClickListener} de façon à pouvoir
 * traiter le retour du click sur les boutons valider/cancel
 * @author L'équipe MakeUrSport
 * 
 */
public class SportifDialogActivity extends SherlockFragmentActivity implements OnClickListener {
	/**
	 * Tag utilisé pour le LOGCAT (affichage de message quand on debug)
	 */
	@SuppressWarnings("unused")
	private final String LOGCAT_TAG=this.getClass().getCanonicalName();

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sportifdialog);
		Button confirmBT = (Button) this.findViewById(R.id.BT_confirm);
		Button cancelBT = (Button) this.findViewById(R.id.BT_cancel);
		confirmBT.setOnClickListener(this);
		cancelBT.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.BT_confirm:
			Log.v("", "Click sur le bouton valider!");
			String anneeNaissance=
					((EditText)this.findViewById(R.id.ET_date_naissance)).getText().toString();
			String taille=
					((EditText)this.findViewById(R.id.ET_taille)).getText().toString();
			String poids=
					((EditText)this.findViewById(R.id.ET_poids)).getText().toString();
			if(anneeNaissance!=null && taille!=null && poids !=null
					&& !anneeNaissance.contentEquals("") && !taille.contentEquals("") && !poids.contentEquals("")) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editPref = pref.edit();
				editPref.putInt(this.getString(R.string.SP_annee_naissance), Integer.parseInt(anneeNaissance));
				editPref.putInt(this.getString(R.string.SP_taille), Integer.parseInt(taille));
				editPref.putFloat(this.getString(R.string.SP_poids), Float.parseFloat(poids));
				editPref.commit();
				setResult(RESULT_OK);
				finish();

			}
			else {
				Toast.makeText(this, this.getString(R.string.infos_pas_rempli), Toast.LENGTH_LONG);
			}
			break;
		case R.id.BT_cancel:
			Log.v("","Click sur le bouton cancel");
			Toast.makeText(this, this.getString(R.string.cancel_string), Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}

}
