package com.makeursport.gestionCourse;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.makeursport.R;

/**
 * Stockage d'un sportif
 * @author L'équipe MakeUrSport
 * 
 */
public class Sportif {
	
	private static final String LOGCAT_TAG = "Sportif";
	private int anneeNaissance;
	private int taille;
	private float poids;
	public Sportif () {

	}
	public Sportif(int anneeNaissance, int taille, float poids) {
		this.anneeNaissance = anneeNaissance;
		this.setTaille(taille);
		this.setPoids(poids);
		
	}
	public static Sportif fromPrefs(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
	     int anneeNaissance=prefs.getInt(c.getString(R.string.SP_annee_naissance), 0);
	     int taille=prefs.getInt(c.getString(R.string.SP_taille), 0);
	     float poids=prefs.getFloat(c.getString(R.string.SP_poids), 0f);
    	Sportif userSportif = new Sportif(anneeNaissance,
        			taille,
        			poids);
    	Log.d(LOGCAT_TAG,"Selection de l'utilisateur depuis les prefs : " + anneeNaissance + ", " + taille + "cm, " + poids + "kg");
    	return userSportif;
	}
	
	public int getAge() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR) - this.anneeNaissance;//age this.anneeNaissance
	}

	public int getTaille() {
		return taille;
	}

	public void setTaille(int taille) {
		this.taille = taille;
	}

	public float getPoids() {
		return poids;
	}

	public void setPoids(float poids) {
		this.poids = poids;
	}
	
	public String toString() {
		return "Age : " + this.getAge() + "; Poids" + this.getPoids() + "; Taille :" + this.getTaille();
	}
	
}
