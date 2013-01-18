package com.makeursport.preferences;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.makeursport.R;
/**
 * Activity des paramètre. Utilise l'ancienne API de paramètre
 * car la librairie de compatibilité n'importe pas les nouvelles méthodes
 * @author L'équipe MakeUrSport
 */
public class Settings extends SherlockPreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
	}

}
