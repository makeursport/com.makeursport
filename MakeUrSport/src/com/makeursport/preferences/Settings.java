package com.makeursport.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

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

		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        addPreferencesFromResource(R.xml.preferences);
        this.findPreference(this.getString(R.string.pref_link)).setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				String url = "https://github.com/makeursport/com.makeursport";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				return false;
			}
		});
	}

}
