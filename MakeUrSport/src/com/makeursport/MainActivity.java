package com.makeursport;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.makeursport.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
/*
 * TODO:
 *	   -> TTS
 *	   -> Partage Stats
 *	   -> Problème d'unité/d'affichage avec la distance et les calories ? TOTEST
 *
DAVID  -> Historique
 */
/*
 * DONE :
 * OK -> Page de parametre
 * OK -> Gestion de la pause
 * OK -> Affichage de ces infos
 * OK -> Afficher sur la map le tracé de la course.
 * OK 	Regler bug :
 * OK		- Quand on met en pause et qu'on reprend, ca trace une ligne
 * OK	depuis l'endroit ou t'as mis en pause jusu'a l'endroit ou t'es.
 * OK	Il faut faire un nouveau polyline quand tu sort de pause.
 * OK		- Quand on recoit pas le signal GPS desuite, on a le chrono
 * OK	qui commence quand même a compter alors qu'on a aucune infos.
 * OK -> Gestion des infos de la course (caloriesbrulée, et distance)
 * OK -> Generation Parcours
 * OK -> Gestion du sport (course vs velo vs roller...)
 */
/**
 * Activité principale de MakeUrsport qui permet de gérer
 * les différents fragments de l'application
 *
 */
public class MainActivity extends SlidingFragmentActivity {
	public static final int PARCOURSDIALOG_REQUESTCODE = 11;
	/**
	 * Tag utilisé pour le LOGCAT (affichage de message quand on debug)
	 */
	private final String LOGCAT_TAG=this.getClass().getCanonicalName();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        
        FragmentManager fm = this.getSupportFragmentManager();//Récupération d'un FragmentManager, pour gerer les fragments
        
        //Affichage du "home as up" de l'actionbar (petite flèche qui indique que l'on peut clicker sur l'icone de l'appli
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        //On met la vue de "derrière" (ce qu'affiche le menu) comme étant menu_frame
        setBehindContentView(R.layout.menu_frame);
        
        
        //On remplace ce que contient menu_frame par un nouveau MenuFragment
        //qui affiche toutes les infos que l'on veut, c'est a dire
        //notre menu
        fm.beginTransaction()
		.replace(R.id.menu_frame, new MenuFragment())
		.commit();

		//Paramètrage du SlidingMenu
        SlidingMenu menu = getSlidingMenu();//On recupere le sliding menu que l'on vient de mettre
        menu.setMode(SlidingMenu.LEFT);//On dit qu'il est sur la gauche
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setFadeDegree(0.35f);
        menu.setFadeEnabled(true);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//Qu'on glisse en fonction des marges
        menu.setBehindOffsetRes(R.dimen.menu_behind_offset);//Et que l'on veut afficher un bout de notre vrai view
        													//même quand on slide
        
        //On affiche le contenu que l'on veut à l'ouverture de l'application, ici CourseEnCours()
        switchContent(new CourseEnCours());
        ViewServer.get(this).addWindow(this);

	}
	public void onDestroy() {
		super.onDestroy();
		ViewServer.get(this).removeWindow(this);
	}
	public void onResume() {
		super.onResume();
		ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOGCAT_TAG, "onActivityResult : mainActivity " + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==PARCOURSDIALOG_REQUESTCODE) {
			switch(resultCode) {
			case Activity.RESULT_OK:
				float dist = data.getFloatExtra(ParcoursDialog.DISTANCE, 0);
				double lat = data.getDoubleExtra(ParcoursDialog.LATITUDE, 0);
				double lon = data.getDoubleExtra(ParcoursDialog.LONGITUDE, 0);
				LatLng ptDepart = new LatLng(lat,lon);
				Log.d("DIST", dist+"");
				Log.d("MA LATLNG", "lat:"+ptDepart.latitude + " lon:" + ptDepart.longitude);
				
				GenerationParcoursATask parcours = new GenerationParcoursATask(dist,this);
				parcours.execute(ptDepart);
				
				break;
			case Activity.RESULT_CANCELED:
				break;
				default:
			}		
		} else {
			this.getSupportFragmentManager().findFragmentById(R.id.main_layout).onActivityResult(requestCode, resultCode, data);
		}

		
	}
	
	/**
	 * Permet d'ouvrir le fragment de courseEnCours, avec les bonnes infos
	 
	public void ouvrirFragmentCourseEnCours(int anneeNaissance, int taille, float poids) {
        //On définit nos arguments pour la CourseEnCours
        Bundle args = new Bundle();
        args.putInt(this.getString(R.string.SP_taille),taille);
        args.putInt(this.getString(R.string.SP_annee_naissance), anneeNaissance);
        args.putFloat(this.getString(R.string.SP_poids), poids);
        
        
        //On créer notre nouvelle CourseEnCours()
        CourseEnCours course = new CourseEnCours();
        course.setArguments(args);//On lui rajoute ces arguments
        this.getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.main_layout, course)
        .commitAllowingStateLoss();//Et on affiche ce fragment.
	}*/
	
	public void switchContent(Fragment f) {
		this.getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.main_layout, f)
		.commit();
		
		getSlidingMenu().showContent();
	}
}
