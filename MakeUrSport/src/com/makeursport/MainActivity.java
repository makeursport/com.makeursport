package com.makeursport;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.makeursport.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
/*
 * TODO:
 *  -> Gestion du sport (course vs velo vs roller...)
PAUL  -> Gestion des infos de la course (caloriesbrulée, et distance)
PAUL  		distance : calcul entre deux positions géoloc : http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
PAUL  					du coup, sauvegarde de la dernierePositionConnu dans "Course"
PAUL  		calories : http://www.ehow.com/how_5021922_measure-calories-burned.html
PAUL  				   http://en.wikipedia.org/wiki/Metabolic_equivalent
DAVID  -> Historique
FLORENT-> Generation Parcours
 */
/*
 * DONE :
 * OK -> Page de parametre OK
 * OK -> Gestion de la pause (ok ?)
 * OK -> Affichage de ces infos (presque fait)
 * OK -> Afficher sur la map le tracé de la course. (a prioris fait ? Semble bon...)
 * OK 	Regler bug :
 * OK		- Quand on met en pause et qu'on reprend, ca trace une ligne
 * OK	depuis l'endroit ou t'as mis en pause jusu'a l'endroit ou t'es.
 * OK	Il faut faire un nouveau polyline quand tu sort de pause.
 * OK		- Quand on recoit pas le signal GPS desuite, on a le chrono
 * OK	qui commence quand même a compter alors qu'on a aucune infos.
 */
public class MainActivity extends SlidingFragmentActivity {
	/**
	 * Tag utilisé pour le LOGCAT (affichage de message quand on debug)
	 */
	@SuppressWarnings("unused")
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
