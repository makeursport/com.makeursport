package com.makeursport.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.makeursport.gestionCourse.*;
import com.makeursport.database.CourseDBHelper;
import com.makeursport.gestionCourse.Course;
/**
 * Classe permettant d'intéragir depuis une {@link GestionnaireHistorique} avec la DB<br/>
 * <strong>ATTENTION:</strong> cette classe ne doit pas être utilisé directement pour intéragir avec la db.
 * En effect, ces requêtes ne doivent pas être faites depuis le thread principal sinon ca bloquera l'interface
 * d' l'utilisateurs
 * @author Mickael
 *
 */
public class CourseDS {
	private static final String LOGCAT_TAG = "CourseDS";
	/**
	 * Le dbHelper qui nous aide à interagir avec la db
	 */
	private CourseDBHelper dbHelper;
	/**
	 * La database avec laquel on souhaite intéragir
	 */
	private SQLiteDatabase database;
	/**
	 * La listes des colonnes de la table Courses
	 * (utilisé pour la selections des courses)
	 */
	private String[] colTableCourse = {CourseDBHelper.COLUMN_COURSE_ID,CourseDBHelper.COLUMN_COURSE_DATE,CourseDBHelper.COLUMN_COURSE_DISTANCE,CourseDBHelper.COLUMN_COURSE_DUREE,CourseDBHelper.COLUMN_COURSE_SPORT};
	
	/**
	 * Constructeur de la classe
	 * @param context le context de l'activité en cours
	 */
	public CourseDS(Context context){
		dbHelper = new CourseDBHelper(context);
	}
	
	/**
	 * Ouverture de la base de données
	 */
	public void open(){
		Log.v(LOGCAT_TAG,"Ouverture DBCourse...");
		try {
			database = dbHelper.getWritableDatabase();
		} catch(SQLException e) {
			Log.e(LOGCAT_TAG, "Erreur lors de l'ouverture de la base de données Course ...");
			 e.getStackTrace();
		}
	}
	
	/**
	 * Fermeture de la base de données
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
	public long insertInfoCourse(Course c) {
		long err;
		ContentValues values = new ContentValues();
		values.put(CourseDBHelper.COLUMN_COURSE_DATE, c.getDate().getTime());
		values.put(CourseDBHelper.COLUMN_COURSE_DISTANCE, c.getDistanceArrondi());
		values.put(CourseDBHelper.COLUMN_COURSE_DUREE, c.getDuree());
		values.put(CourseDBHelper.COLUMN_COURSE_SPORT, c.getSport().getSportInt());
		err = database.insert(CourseDBHelper.TABLE_COURSE, null, values);
		if(err==-1) {
			Log.d(LOGCAT_TAG, "An error occured while inserting values (Course of " + c.getDistanceArrondi() + "km)");
		}
		return err;
	}
	
	/**
	 * Selection d'une course dans la base de données
	 * @return la course
	 */
	public Course selectCourse(int id){
		Cursor c = database.query(CourseDBHelper.TABLE_COURSE, colTableCourse,CourseDBHelper.COLUMN_COURSE_ID + " = " + id ,null, null, null, null);
		Course maCourse = this.transformCursorToCourse(c);
		Log.d(LOGCAT_TAG,"Selecting course " + id);
		return maCourse;
		
	}
	/**
	 * Selection de toute la liste des courses enregistrées
	 * @return la liste de toutes les courses enregistrées
	 */
	public ArrayList<Course> selectListesCourses() {
		Cursor c = database.query(CourseDBHelper.TABLE_COURSE, colTableCourse,null, null, null, null,CourseDBHelper.COLUMN_COURSE_ID + " desc",null);
		ArrayList<Course> maListe = this.transformCursorToListCourse(c);
		Log.d(LOGCAT_TAG, "Selecting all courses");
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
	 * @param c le curseur à transformer
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
		maCourse = new Course(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_ID),c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_DATE), c.getFloat(CourseDBHelper.NUM_COLUMN_COURSE_DISTANCE), c.getLong(CourseDBHelper.NUM_COLUMN_COURSE_DUREE),Sport.getSport(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_SPORT)));
		return maCourse;
	}
	/**
	 * Transformation d'un cursor en liste de course
	 * @param c le curseur à traiter
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
			maListe.add(new Course(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_ID),c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_DATE), c.getFloat(CourseDBHelper.NUM_COLUMN_COURSE_DISTANCE), c.getLong(CourseDBHelper.NUM_COLUMN_COURSE_DUREE),Sport.getSport(c.getInt(CourseDBHelper.NUM_COLUMN_COURSE_SPORT))));
			c.moveToNext();
		}
		return maListe;
	}
}


