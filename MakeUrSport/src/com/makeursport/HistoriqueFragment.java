package com.makeursport;

import java.util.ArrayList;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;
import com.makeursport.gestionCourse.Course;
import com.makeursport.gestionCourse.EtatCourse;
import com.makeursport.gestionCourse.GestionnaireHistorique;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
/**
 * Le {@link SherlockListFragment} permettant l'affichage des donn�es contenus dans l'historique.
 * Il r�cup�re toute les courses de l'historique dans le {@link #onResume)
 * @author L'�quipe MakeUrSport
 */
public class HistoriqueFragment extends SherlockListFragment{
	
	private static final String LOGCAT_TAG = "HistoriqueActivity";
	/**
	 * Le gestionnaire qui nous permet d'int�ragir avec notre historique
	 */
	private GestionnaireHistorique historique;
	/**
	 * L'adapter qui nous aide � interagir avec le ListView
	 */
	private HistoriqueAdapter adapter;
	/**
	 * Le Listener qui attends le click sur la course
	 */
	private OnItemClickListener courseListener= new OnItemClickListener(){
		/**
		 * L'action que l'on execute lors du click
		 * @param adapter l'adapter sur lequel a eu lieu le click
		 * @param vue la vue dans l'adapter sur laquelle a eu lieu le click
		 * @param position la position de la vue click�e
		 * @paaram id la ligne de l'item click� dans la listView
		 */
		public void onItemClick(AdapterView<?> adapter, View vue, int position,
				long id) {

			if(getSherlockActivity() instanceof MainActivity) {
				Fragment f = new CourseFragment();
				Bundle b = new Bundle();
				Course c = (Course) adapter.getItemAtPosition(position);
				b.putInt(CourseFragment.ID, c.getId());
				f.setArguments(b);
			((MainActivity)getSherlockActivity()).addFragment(f);
			}
		}
	};
	
	/*public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
	    View monLayout=inflater.inflate(R.layout.activity_historique, container,false);

        //listCourse=(ListView) monLayout.findViewById(R.id.listCourse);
        //adapter = new HistoriqueAdapter(this.getSherlockActivity());
        //listCourse.setAdapter(adapter);
        
        /**
	     * Boucle de repeuplage de la bd
	      for(int i=0;i<5;i++){
        	historique.setCourse(new Course(0,new Date().getTime(),456f,45,Sport.COURSE));
	        historique.setCourse(new Course(0,new Date().getTime(),456f,45,Sport.ROLLER));
	        historique.setCourse(new Course(0,new Date().getTime(),456f,45,Sport.VELO));
	    }* /
   
        //historique.getArray();
        //Log.d("BLABLA",historique.getCourse(14).getCaloriesBrulees()+"");
        this.getListView().setOnItemClickListener(courseListener);
        return monLayout;
	 
    }*/
	@Override
	public void onResume(){
		super.onResume();
		Log.v(LOGCAT_TAG,"Generation de la liste de courses...");
        this.getListView().setOnItemClickListener(courseListener);
        this.adapter = new HistoriqueAdapter(this.getSherlockActivity());
	    this.setListAdapter(adapter);
        historique=new GestionnaireHistorique(this);
		historique.selectToutesLesCourses();
		this.setHasOptionsMenu(true);
	}
	/**
	 * Modifie l'adapter courant et lui dit de mettre � jour sa vue
	 * @param courses
	 */
	public void modifierAdapter(ArrayList<Course> courses) {
		Log.v(LOGCAT_TAG, "Mise � jour de l'adapter avec " + courses.size() + " courses");
		adapter.setCourses(courses);
	}
	/**
	 * Lors d'un clique sur les boutons du menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			((SlidingFragmentActivity) getActivity()).toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
}

