package com.makeursport;

import com.makeursport.gestionCourse.Sportif;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SportifDS {
	private SportifDBHelper dbHelper;
	private SQLiteDatabase database;
	private String[] colTableSportif = {SportifDBHelper.COLUMN_SPORTIF_ID,SportifDBHelper.COLUMN_SPORTIF_DATENAISSANCE,SportifDBHelper.COLUMN_SPORTIF_TAILLE,SportifDBHelper.COLUMN_SPORTIF_POIDS};

	private static final String LOGCAT_TAG = "SportifDS";
	
	/**
	 * Constructeur de la classe
	 * @param context
	 */
	public SportifDS(Context context){
		dbHelper = new SportifDBHelper(context);
	}
	
	/**
	 * Ouverture de la base de données
	 */
	public void open(){
		Log.v(LOGCAT_TAG,"Ouverture DB...");
		try {
			database = dbHelper.getWritableDatabase();
		} catch(SQLException e) {
			Log.e(LOGCAT_TAG, "Erreur lors de l'ouverture de la base de données...");
			 e.getStackTrace();
		}
	}
	
	/**
	 * Fermeture de la base de données
	 */
	public void close(){
		Log.v(LOGCAT_TAG,"Fermeture DB...");
		dbHelper.close();
	}
	/**
	 * Insertion d'un sportif dans la base de donnée
	 * @param anneeNaissance l'année de naissance du sportif
	 * @param taille la taille du sportif
	 * @param poids le poids du sportif
	 * @return -1 si il y a une erreur, l'identifiant de la nouvelle colonne sinon
	 */
	public long insertInfoSportif(int anneeNaissance, int taille, float poids) {
		long err;
		ContentValues values = new ContentValues();
		values.put(SportifDBHelper.COLUMN_SPORTIF_DATENAISSANCE, anneeNaissance);
		values.put(SportifDBHelper.COLUMN_SPORTIF_POIDS, poids);
		values.put(SportifDBHelper.COLUMN_SPORTIF_TAILLE, taille);
		err = database.insert(SportifDBHelper.TABLE_SPORTIF, null, values);
		return err;
	}
	/**
	 * Selection du sportif dans la base de données
	 * @return le sportif et ses infos
	 */
	public Sportif selectSportif(){
		Cursor c = database.query(SportifDBHelper.TABLE_SPORTIF, colTableSportif , null, null, null, null, null);
		Sportif monSportif = this.transformCursorToSportif(c);
		return monSportif;
	}

	

	/**
	 * Transformation d'un curseur en sportif
	 * @param c le curseur à transformer
	 * @return le sportif
	 */
	private Sportif transformCursorToSportif(Cursor c) {
		if( c == null || c.getCount() == 0) {
			Log.w(LOGCAT_TAG, "ERREUR : Le curseur est vide. Fermeture du curseur");
			c.close();
			return null;
		}
		Sportif monSportif;
		c.moveToFirst();
		monSportif = new Sportif(c.getInt(SportifDBHelper.NUM_COLUMN_SPORTIF_DATENAISSANCE), c.getInt(SportifDBHelper.NUM_SPORTIF_TAILLE), c.getFloat(SportifDBHelper.NUM_COLUMN_SPORTIF_POIDS));
		return monSportif;
	}
}
