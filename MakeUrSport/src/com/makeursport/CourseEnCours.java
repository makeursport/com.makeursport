package com.makeursport;
/**
 * Normalement, j'ai tout modifié, et c'est le fichiers le plus à jour (20:05)
 */
import java.util.Date;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.makeursport.gestionCourse.Course;
import com.makeursport.gestionCourse.EtatCourse;
import com.makeursport.gestionCourse.Sportif;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;
/*
 * TODO : 
 * 	-> Gestion de l'utilisateur (sportif), gestion de la première ouverture (enregistrer infos dans db)
 *  -> Gestion du sport (course vs velo vs roller...)
 *  -> Gestion des infos de la course (caloriesbrulée, et distance)
 *  		distance : calcul entre deux positions géoloc : http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
 *  					du coup, sauvegarde de la dernierePositionConnu dans "Course"
 *  		calories : http://www.ehow.com/how_5021922_measure-calories-burned.html
 *  					http://en.wikipedia.org/wiki/Metabolic_equivalent
 *  -> Gestion de la pause (bouton mettre en pause)
 *  -> Affichage de ces infos
 *  -> Afficher sur la map le tracé de la course. (a prioris fait ?)
 *  
 */

/**
 * Activité de la CourseEnCours
 * affiche les infos, la carte etc. d'une course en cours
 * gère la mise ç jour de ces infos en temps réel
 * @author L'équipe MakeUrSport
 *
 */
public class CourseEnCours extends SherlockFragmentActivity implements LocationListener {
	/**
	 * Tag utilisé pour le LOGCAT (affichage de message quand on debug)
	 */
	private final String LOGCAT_TAG=this.getClass().getCanonicalName();
	/**
	 * La course en cours
	 */
	private Course maCourse;
	/**
	 * Le location manager gérant la localisation
	 */
	private LocationManager locationManager;
	/**
	 * Temps (en ms) depuis lequel on mets la course en pause.
	 */
	private long tempsDebutPause;
	/**
	 * Fragment contenant la carte Google Maps
	 */
	MyMapFragment mapFragment;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_en_cours);
        FragmentManager fm = this.getSupportFragmentManager();//Récupération d'un FragmentManager, pour gerer les fragments
        this.maCourse = new Course();// Création d'une nouvelle course.
        this.locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);//Initialisation du LocationManager
        this.mapFragment = (MyMapFragment) fm.findFragmentById(R.id.mapfragment);//Récupération du fragment gérant la carte
        //Ceci vient d'être mis à jour
        Log.d("", "modifiŽ par Paul");
        Log.d("COUCOU", "modifié par Micky");
        this.demarrerCourse(new Sportif());//On démarre la course avec un nouveau sportif...
        ((TextView)this.findViewById(R.id.infosCourse)).setText(this.maCourse.getDate().toLocaleString());
        /*SportifDS sds = new SportifDS(this.getApplicationContext());
        sds.open();
        Sportif user = sds.selectSportif();
        Log.v("Appli",user.toString());
        sds.close();
        FragmentManager fm = this.getSupportFragmentManager();
        SupportMapFragment mapf = (SupportMapFragment) fm.findFragmentById(R.id.mapfragment);
        mapf.getMap().setMyLocationEnabled(true);*/
        
    }
    /**
     * Met a jour les infos de la course
     * @param caloriesBrulees le nombre de calorie brulées
     * @param vitesseReelle la vitesse de l'utilisateur
     * @param distance la distance parcouru
     */
    public void mettreAJourCourse(int caloriesBrulees, float vitesseReelle, int distance) {
    	this.maCourse.setCaloriesBrulees(caloriesBrulees);
    	this.maCourse.setVitesseReelle(vitesseReelle);
    	this.maCourse.setDistance(distance);
    }
    /**
     * Demarre la course
     * @param user l'utilisateur de l'application
     */
    public void demarrerCourse(Sportif user) {
    	this.maCourse.setDebutCourse(new Date().getTime());
    	this.maCourse.setDate(new Date());
    	this.maCourse.setDistance(0);
    	this.maCourse.setVitesseReelle(0.0f);
    	this.maCourse.setUser(user);
    	this.maCourse.setCaloriesBrulees(0);
    	this.maCourse.setEtatCourse(EtatCourse.CourseLancee);
    	
    	this.demarrerLocationListener();
    }
    /**
     * Met une course en pause
     */
    public void mettreEnPauseCourse() {
    	//TODO:
    	this.maCourse.setEtatCourse(EtatCourse.CourseEnPause);
    	tempsDebutPause = new Date().getTime();
    }
    /**
     * Sort une course de pause
     */
    public void reprendreCourse() {
    	//TODO:
    	this.maCourse.addTempsPause(new Date().getTime() - tempsDebutPause);
    	this.maCourse.setEtatCourse(EtatCourse.CourseLancee);
    }
    /**
     * Met à jour la vue, en affichant les infos voulus.
     */
    public void mettreAJourView() {
    	//TODO
    	TextView tv = (TextView) this.findViewById(R.id.infosCourse);
    	tv.setText(this.maCourse.getEtatCourse().toString() + " : Vitesse:" + this.maCourse.getVitesseReelle()
    			+"; Duree:" +this.maCourse.getDuree() );
    }
    /**
     * Demande la mise à jour de la localistion.
     * 
     * @see CourseEnCours#onLocationChanged()
     */
    private void demarrerLocationListener() {
    	Criteria critere = new Criteria();
    	critere.setAccuracy(Criteria.ACCURACY_FINE);// On peut mettre ACCURACY_FINE pour une haute précision
    	critere.setAltitudeRequired(false);    // On ne recupere pas l'altitude
    	critere.setBearingRequired(true);	// On recupere la direction
    	critere.setCostAllowed(false);    	// On ne veut pas de providers payant
    	// Pour indiquer la consommation d'énergie demandée
    	// Criteria.POWER_HIGH pour une haute consommation, Criteria.POWER_MEDIUM pour une consommation moyenne et Criteria.POWER_LOW pour une basse consommation
    	critere.setPowerRequirement(Criteria.POWER_HIGH);
    	critere.setSpeedRequired(true); // On récupere la vitesses aussi 
   		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50,this);
   		Location l = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
   		if(l!=null) {
   	   		this.mapFragment.initialiserCarte(l.getLatitude(), l.getLongitude());//Centrer la carte sur notre position...
   		}
    }
    /**
     * Met fin à la mise à jour du location listener
     * (démarré à l'aide du {@link CourseEnCours#demarrerLocationListener})
     */
    public void stopperLocationListener() {
    	locationManager.removeUpdates(this);
    }
    /**
     * Méthode appelée quand la position de l'utilisateur change
     * @param loc la position de l'utilisateur
     */
   	public void onLocationChanged(Location loc) {
   		if(this.maCourse.getEtatCourse()== EtatCourse.CourseLancee) {
   			this.mettreAJourCourse(0, loc.getSpeed(), 0);
   			Log.v(LOGCAT_TAG, "Mise à jour des coordonnées (calories: 0 (TODO), speed:" + maCourse.getVitesseReelle() + ", distance 0 (TODO) )");
   			this.mapFragment.mettreAJourCarte(loc.getLatitude(),loc.getLongitude());
   			this.mettreAJourView();
   		}
   		else {
   			Log.d(LOGCAT_TAG, "Course en pause... Les infos ne sont pas mise à jour...");
   		}
   	}
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}
