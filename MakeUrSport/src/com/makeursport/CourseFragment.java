package com.makeursport;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMapOptions;
import com.makeursport.gestionCourse.Course;
import com.makeursport.gestionCourse.GestionnaireHistorique;
/**
 * Fragment permettant l'affichage d'une course terminée.
 * @author L'équipe MakeUrSport
 *
 */
public class CourseFragment extends SherlockFragment{
	private static final String LOGCAT_TAG = "CourseFragment";
	/**
	 * Le String permettant de recuperer l'id passé en intent/bundle
	 */
	public static final String ID = "com.makeursport.ID";
	private TextView calories=null;
	private TextView vitesseMoy=null;
	private TextView duree=null;
	private TextView distance=null;
	/**
	 * Le gestionnaire de l'historique avec lequel on communique a la db
	 */
	private GestionnaireHistorique historique;
	/**
	 * L'id de la course en cours
	 */
	private int courseId=-1;
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
	    View monLayout=inflater.inflate(R.layout.activity_course, container,false);
	   
	    /**
	     * On récupère l'id de la course clickée
	     */
	    if(this.getArguments()!=null && this.getArguments().containsKey(ID)){
	    	this.courseId=this.getArguments().getInt(ID);
	    }
	    
		//On met dans sa place réservé, le MapFragment
		FragmentManager fm = getChildFragmentManager(); 
		//Pour creer un nouveau MyMapFragment, on utilise newInstance
		//Plutôt qu'un constructeur. Conseillé par la doc
		MyMapFragment mapFragment = MyMapFragment.newInstance(new GoogleMapOptions().zoomControlsEnabled(false));
		fm.beginTransaction()
		.replace(R.id.mapfragment_location, mapFragment)
		.commit();
	    
	    
	    /**
	     * Requète de récuprération de la course
	     */
	    historique=new GestionnaireHistorique(this);
	    historique.selectionnerCourse(courseId);
	    
	    /**
	     * On associe les vues avec les ressources 
	     */
	    vitesseMoy=(TextView)monLayout.findViewById(R.id.TV_vit_moyenne_valeur);
	    duree=(TextView)monLayout.findViewById(R.id.TV_duree);
	    distance=(TextView)monLayout.findViewById(R.id.TV_distance_valeur);
	    calories=(TextView)monLayout.findViewById(R.id.TV_calories_valeur);
	    
	    
		this.setHasOptionsMenu(true);//On signal que l'on veut recevoir les appels concernant le menu de l'action bar

	    return monLayout;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.w(LOGCAT_TAG, "Passage dans le onCreateOptionsMenu");
		inflater.inflate(R.menu.course_activity, menu);
	}
	
	/**
	 * On met à jour toute les vues avec la course récupérée grace à la requète SQL
	 * @param course la course clickée
	 */
	public void modifView(Course course){
		
		calories.setText(course.getCaloriesBrulees()+"");
		distance.setText(course.getDistanceArrondi()+ getString(R.string.unite_distance));
		vitesseMoy.setText(course.getVitesseMoyenne()+ getString(R.string.unite_vitesse));
		duree.setText(course.getDuree()+ getString(R.string.unite_heure));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.MenuBT_delete:
			historique.supprimerCourse(courseId);
			break;
		case android.R.id.home:
			this.getFragmentManager().beginTransaction().remove(this);
		}
		return true;
	}

}
