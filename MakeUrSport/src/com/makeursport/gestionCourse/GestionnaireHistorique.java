package com.makeursport.gestionCourse;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.makeursport.CourseFragment;
import com.makeursport.HistoriqueFragment;
import com.makeursport.MainActivity;
import com.makeursport.database.CourseDS;
/**
 * La classe de Gestionnaire Historique qui permet d'intéragir avec notre Base de données.
 * C'est grace à elle que des classes comme {@link CourseFragment} ou {@link HistoriqueFragment}
 * ont accès à cette base de données
 * @author L'équipe MakeUrSport
 *
 */
public class GestionnaireHistorique {
	
	public static final String LOGCAT_TAG = "GestionnaireHistorique";
	/**
	 * La base de données avec laquel on veut interagir
	 */
	private CourseDS bdd;
	/**
	 * L'{@link HistoriqueFragment} depuis lequel notre GestionnaireHistorique est lancé
	 */
	private HistoriqueFragment hFragment;
	
	/**
	 * Le {@link CourseFragment} depuis lequel notre GestionnaireCourse est lancé
	 */
	private CourseFragment courseFragment;
	/**
	 * Constructeur par défaut.
	 * @param context
	 */
	public GestionnaireHistorique(Context context) {
		this.bdd = new CourseDS(context);
	}
	/**
	 * Constructeur lorsqu'il est appelé par un HistoriqueFragment
	 * @param f le fragment en question
	 */
	public GestionnaireHistorique(HistoriqueFragment f) {
		this(f.getSherlockActivity());
		this.hFragment=f;		
	}
	/**
	 * Constructeur lorsqu'il est appelé par un CourseFragment
	 * @param f le fragment en question
	 */
	public GestionnaireHistorique(CourseFragment f) {
		this(f.getSherlockActivity());
		this.courseFragment=f;
	}
	
	/**
	 * Enregistre une course dans la base de données, en arrière plan
	 * @param course la base de données à executer
	 */
	public void enregistrerCourse(Course course, LatLng dernierePos) {
		InsertCourseATask asynctask = new InsertCourseATask(dernierePos);
		asynctask.execute(course);
	}
	
	/**
	 * Recupere toutes les courses de la bases de données et mets à jour la vue
	 * Attention, doit être lancer depuis un HistoriqueFragment
	 */
	public void selectToutesLesCourses(){
		SelectTouteCoursesATask asynctask = new SelectTouteCoursesATask();
		asynctask.execute();
	}
	
	/**
	 * Selectionne une course et mets à jour la vue
	 * <strong>Attention</strong>, doit être lancer depuis un CourseFragment
	 * @param id l'id de la course à récuperer
	 */
	public void selectionnerCourse(int id){
		SelectCourseATask asynctask= new SelectCourseATask();
		asynctask.execute(id);
	}
	
	/**
	 * Supprime une course de la base de données, et change le fragment en cours.
	 * <strong>Attention:</strong> cette méthode doit être appelé uniquement depuis un CourseFragment
	 * @param idCourse l'id de la course à supprimer
	 */
	public void supprimerCourse(Integer idCourse){
		DeleteCourseATask asynctask = new DeleteCourseATask();
		asynctask.execute(idCourse);
	}
	 
	 /**
	  * AsyncTask sélectionnant toutes les courses de la base de données, et mettant à jour
	  * le HistoriqueFragment.
	  */
     private class SelectTouteCoursesATask extends AsyncTask<Void, Void,ArrayList<Course>>
      {
      	@Override
      	protected ArrayList<Course> doInBackground(Void... args) {
      		bdd.open();
      		ArrayList<Course> courses = bdd.selectListesCourses();
      		bdd.close();
      		return courses;
      	}

      	@Override
      	protected void onPostExecute(ArrayList<Course> result) {
      		if(result!=null) {
          		hFragment.modifierAdapter(result);
                Log.d(LOGCAT_TAG,"Selection de toutes les Courses effectué");
      		}
      	}
  	}
     /**
      * ASyncTask permettant d'inserer une course dans la base de données. 
      */
     private class InsertCourseATask extends AsyncTask<Course,Void ,Void>
     {
    	 LatLng pos;
    	public InsertCourseATask(LatLng pos) {
    		this.pos=pos;
    	}
     	@Override
     	protected Void doInBackground(Course... course) {
     		bdd.open();
     		bdd.insertInfoCourse(course[0],pos);
     		bdd.close();
     		return null;
     	}
     	
     }
     /**
      * ASyncTask permettant de supprimer une course de la base de données, et
      * qui echange le fragment en cours
      */
     private class DeleteCourseATask extends AsyncTask<Integer, Void,Void>
     {
     	@Override
     	protected Void doInBackground(Integer... course) {
     		bdd.open();
     		bdd.deleteCourse(course[0]);
     		bdd.close();
			return null;
     		
     		
     	}
     	@Override
     	protected void onPostExecute(Void arg) {
     		if (courseFragment.getSherlockActivity() instanceof MainActivity) {
     			courseFragment.retourHistoriqueFragment();
     		}
     	}
     }
     /**
      * AsyncTask chargé de recuperer une course depuis la base de données
      * et qui modifie la courseFragment
      */
     private class SelectCourseATask extends AsyncTask<Integer, Void,Course>
     {
     	@Override
     	protected Course doInBackground(Integer... args) {
     		bdd.open();
     		Course maCourse = bdd.selectCourse(args[0]);
     		bdd.close();
     		return maCourse;
     	}

     	@Override
     	protected void onPostExecute(Course result) {
     		if(result==null){
     			Log.d(LOGCAT_TAG, "PROBLEME selection d'une course");
     		}
     		else{     			
     			result.setUser(Sportif.fromPrefs(courseFragment.getSherlockActivity()));
     			courseFragment.modifView(result);
     		}
     	}
     }
}
