package com.makeursport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SportifDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_SPORTIF="sportif";
		public static final String COLUMN_SPORTIF_ID="id";
		public static final String COLUMN_SPORTIF_DATENAISSANCE="datenaissance";
		public static final String COLUMN_SPORTIF_TAILLE = "taille";
		public static final String COLUMN_SPORTIF_POIDS = "poids";
	
		public static final int NUM_COLUMN_SPORTIF_ID=0;
		public static final int NUM_COLUMN_SPORTIF_DATENAISSANCE=1;
		public static final int NUM_SPORTIF_TAILLE =2;
		public static final int NUM_COLUMN_SPORTIF_POIDS =3;
		
	private static final String DB_NAME="Sportif.db";
	private static final int DB_VERSION=01;
	
	private static final String reqCreerSportif="create table " + TABLE_SPORTIF +
			"( " + COLUMN_SPORTIF_ID + " integer primary key autoincrement, " 
			+ COLUMN_SPORTIF_DATENAISSANCE + " integer ,"
			+ COLUMN_SPORTIF_TAILLE + " integer , "
			+ COLUMN_SPORTIF_POIDS + " float);";
	
	/**
	 * Constructeur de la classe
	 * @param context : contexte de l'appli
	 * @param name : nom de la bdd
	 * @param factory 
	 * @param version : version de la bdd
	 */
	public SportifDBHelper (Context context, String name, CursorFactory factory,int version) {
	super(context, name, factory, version);
	}
	
	public SportifDBHelper(Context context) {
	super(context, DB_NAME,null,DB_VERSION);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(reqCreerSportif);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	
	

}
