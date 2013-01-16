package com.makeursport.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
/**
 * Classe qui decrit la structure et qui nous aide à interagir avec la db
 * @author l'équipe MakeUrSport
 * 
 */
public class CourseDBHelper extends SQLiteOpenHelper {
	/**
	 * La table course
	 */
	public static final String TABLE_COURSE="course";
		public static final String COLUMN_COURSE_ID="id";
		public static final String COLUMN_COURSE_DATE="date";
		public static final String COLUMN_COURSE_DISTANCE="distance";
		public static final String COLUMN_COURSE_DUREE="duree";
		public static final String COLUMN_COURSE_SPORT="sport";
		
		public static final int NUM_COLUMN_COURSE_ID=0;
		public static final int NUM_COLUMN_COURSE_DATE=1;
		public static final int NUM_COLUMN_COURSE_DISTANCE=2;
		public static final int NUM_COLUMN_COURSE_DUREE=3;
		public static final int NUM_COLUMN_COURSE_SPORT=4;
	/**
	 * Nom du fichier de la course
	 */
	private static final String DB_NAME="Course.db";
	/**
	 * Version de notre DB
	 */
	private static final int DB_VERSION=1;
	
	/**
	 * Requete de création de la table Course
	 */
	private static final String reqCreerCourse="create table " + TABLE_COURSE +
			"( " + COLUMN_COURSE_ID + " integer primary key autoincrement, " 
			+ COLUMN_COURSE_DATE + " integer ," 
			+ COLUMN_COURSE_DISTANCE + " float , "
			+ COLUMN_COURSE_DUREE + " long , "
			+ COLUMN_COURSE_SPORT + " integer);";
	
	/**
	 * Constructeur de la classe
	 * @param context : contexte de l'appli
	 * @param name : nom de la bdd
	 * @param factory 
	 * @param version : version de la bdd
	 */
	public CourseDBHelper (Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}
	
	public CourseDBHelper(Context context) {
		super(context, DB_NAME,null,DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(reqCreerCourse);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("BDD ","Bien mis a jour");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE );
		onCreate(db);

	}
		
	
	
		
}
