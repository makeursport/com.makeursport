package com.makeursport.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.makeursport.gestionCourse.*;
import com.makeursport.database.CourseDBHelper;
import com.makeursport.gestionCourse.Course;
/**
 * Classe permettant d'int�ragir depuis une {@link GestionnaireHistorique} avec la DB<br/>
 * <strong>ATTENTION:</strong> cette classe ne doit pas �tre utilis� directement pour int�ragir avec la db.
 * En effect, ces requ�tes ne doivent pas �tre faites depuis le thread principal sinon ca bloquera l'interface
 * d' l'utilisateurs
 * @author Mickael
 *
 */
public class CourseDS {
	private static final String LOGCAT_TAG = "CourseDS";
	/**
	 * Le dbHelper qui nous aide � interagir avec la db
	 */
	private CourseDBHelper dbHelper;
	/**
	 * La database avec laquel on souhaite int�ragir
	 */
	private SQLiteDatabase database;
	/**
	 * La listes des colonnes de la table Courses
	 * (utilis� pour la selections des courses)
	 */
	private String[] colTableCourse = {CourseDBHelper.COLUMN_COURSE_ID,CourseDBHelper.COLUMN_COURSE_DATE,CourseDBHelper.COLUMN_COURSE_DISTANCE,CourseDBHelper.COLUMN_COURSE_DUREE,CourseDBHelper.COLUMN_COURSE_SPORT, CourseDBHelper.COLUMN_COURSE_LASTPOS_LAT, CourseDBHelper.COLUMN_COURSE_LASTPOS_LNG};
	
	/**
	 * Constructeur de la classe
	 * @param context le context de l'activit� en cours
	 */
	public CourseDS(Context context){
		dbHelper = new CourseDBHelper(context);
	}
	
	/**
	 * Ouverture de la base de donn�es
	 */
	public void open(){
		Log.v(LOGCAT_TAG,"Ouverture DBCourse...");
		try {
			database = dbHelper.getWritableDatabase();
		} catch(SQLException e) {
			Log.e(LOGCAT_TAG, "Erreur lors de l'ouverture de la base de donn�es Course ...");
			 e.getStackTrace();
		}
	}
	
	/**
	 * Fermeture de la base de donn�es
	 */
	public void close(){
		Log.v(LOGCAT_TAG,"Fermeture DBCourse...");
		dbHelper.close();
	}
	
	
	 /**
	  * Insertion d'une course dans la db
	  * @param c la course a inserer
	  * @return -1 si il y a une erreur, l'identifiant de la nouvelle colonne sinon
	  */
	public long insertInfoCourse(Course c,LatLng pos) {
		long err;
		ContentValues values = new ContentValues();
		Log.d(LOGCAT_TAG + "_insertCourse", "Course : date:" + c.getDate().getTime() + " dist:" + c.getDistanceArrondi() + " duree:"+ c.getDuree());
		values.put(CourseDBHelper.COLUMN_COURSE_DATE, c.getDate().getTime());
		values.put(CourseDBHelper.COLUMN_COURSE_DISTANCE, c.getDistanceArrondi());
		values.put(CourseDBHelper.COLUMN_COURSE_DUREE, c.getDuree());
		values.put(CourseDBHelper.COLUMN_COURSE_SPORT, c.getSport().getSportInt());
		values.put(CourseDBHelper.COLUMN_COURSE_LASTPOS_LAT, pos.latitude);
		values.put(CourseDBHelper.COLUMN_COURSE_LASTPOS_LNG, pos.longitude);
		err = database.insert(CourseDBHelper.TABLE_COURSE, null, values);
		if(err==-1) {
			Log.d(LOGCAT_TAG, "An error occured while inserting values (Course of " + c.getDistanceArrondi() + "km)");
		}
		return err;
	}
	
	/**
	 * Selection d'une course dans la base de donn�es
	 * @return la course
	 */
	public Course selectCourse(int id){
		Cursor c = database.query(CourseDBHelper.TABLE_COURSE, colTableCourse,CourseDBHelper.COLUMN_COURSE_ID + " = " + id ,null, null, null, null);
		Course maCourse = this.transformCursorToCourse(c);
		Log.d(LOGCAT_TAG + "selectCourse", "Course : date:" + maCourse.getDate().getTime() + " dist:" + maCourse.getDistanceArrondi() + " duree:"+ maCourse.getDuree());
		return maCourse;
		
	}
	/**
	 * Selection de toute la liste des courses enregistr�es
	 * @return la liste de toutes les courses enregistr�es
	 */
	public ArrayList<Course> selectListesCourses() {
		Cursor c = database.query(CourseDBHelper.TABLE_COURSE, colTableCourse,null, null, null, null,CourseDBHelper.COLUMN_COURSE_ID + " desc",null);
		ArrayList<Course> maListe = this.transformCursorToListCourse(c);
		Log.d(LOGCAT_TAG, "Selection de toutes les courses");
		return maListe;
	}
	/**
	 * Suppression d'une courses de la db
	 * @param id l'id de la course
	 * @return true si pas de pb, false sinon
	 */
	public boolean deleteCourse(int id) {
		Log.d(LOGCAT_TAG,"Deleting course with id=" +id);
		return database.delete(CourseDBHelper.TABLE_COURSE, CourseDBHelper.COLUMN_COURSE_ID + "=" + id, null) >0;
	}
	/**
	 * Transformation d'un curseur en course
	 * @param c le curseur � transformer
	 * @return la course
	 */
	private Course transformCursorToCourse(Cursor c) {
		if( c == null || c.getCount() == 0) {
			Log.w(LOGCAT_TAG, "ERREUR : Le curseur est vide. Fermeture du curseur");
			c.close();
			return null;
		}
		Course maCourse;
		c.moveToFirst();
		Log.d(LOGCAT_TAG, "Dur�e : " + c.getLong(CourseDBHelper.NUM_COLUMN_COURSE_DUREE));
		maCourse = new Course(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_ID), c.getLong(CourseDBHelper.NUM_COLUMN_COURSE_DATE), c.getDouble(CourseDBHelper.NUM_COLUMN_COURSE_DISTANCE), c.getLong(CourseDBHelper.NUM_COLUMN_COURSE_DUREE), Sport.getSport(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_SPORT)));
		LatLng lastPos = new LatLng(c.getDouble(CourseDBHelper.NUM_COLUMN_COURSE_LASTPOS_LAT), c.getDouble(CourseDBHelper.NUM_COLUMN_COURSE_LASTPOS_LNG));
		maCourse.setDernierePos(lastPos);
		Log.d(LOGCAT_TAG + "_transformcursor", "lat:" + lastPos.latitude + " lng:" + lastPos.longitude);
		return maCourse;
	}
	/**
	 * Transformation d'un cursor en liste de course
	 * @param c le curseur � traiter
	 * @return la liste de courses
	 */
	private ArrayList<Course> transformCursorToListCourse(Cursor c) {
		if(c==null| c.getCount()==0) {
			Log.w(LOGCAT_TAG,"ERREUR : CUREUR VIDE , FERMETURE CURSEUR");
			c.close();
			return null;
		}
		ArrayList<Course> maListe = new ArrayList<Course>();
		c.moveToFirst();
		for(int i=0;i<c.getCount();i++){
			maListe.add(new Course(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_ID),c.getLong(CourseDBHelper.NUM_COLUMN_COURSE_DATE), c.getDouble(CourseDBHelper.NUM_COLUMN_COURSE_DISTANCE), c.getLong(CourseDBHelper.NUM_COLUMN_COURSE_DUREE),Sport.getSport(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_SPORT))));
			c.moveToNext();
		}
		return maListe;
	}
}


