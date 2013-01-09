package com.makeursport;

import java.util.Date;
import java.util.Iterator;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.makeursport.gestionCourse.Course;
import com.makeursport.gestionCourse.EtatCourse;
import com.makeursport.gestionCourse.Sport;
import com.makeursport.gestionCourse.Sportif;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/*
 * TODO :
 * 	-> Page de parametre
 *  -> Gestion du sport (course vs velo vs roller...)
 *  -> Gestion des infos de la course (caloriesbrulée, et distance)
 *  		distance : calcul entre deux positions géoloc : http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
 *  					du coup, sauvegarde de la dernierePositionConnu dans "Course"
 *  		calories : http://www.ehow.com/how_5021922_measure-calories-burned.html
 *  				   http://en.wikipedia.org/wiki/Metabolic_equivalent
 *  -> Gestion de la pause (ok ?)
 *  -> Affichage de ces infos (presque fait)
 *  -> Afficher sur la map le tracé de la course. (a prioris fait ? Semble bon...)
 */
import android.widget.Toast;

/**
 * Activité de la CourseEnCours
 * affiche les infos, la carte etc. d'une course en cours
 * gère la mise ç jour de ces infos en temps réel<br/>
 * Implémente {@link LocationListener} de manière à être informer lorsque
 * la position de l'utilisateur change, et {@link GpsStatus.Listener} pour être informer
 * de l'état du GPS en temps réel
 * @author L'équipe MakeUrSport
 *
 */
public class CourseEnCours extends SherlockFragment implements LocationListener,Listener {
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
	 * Fragment contenant la carte Google Maps
	 */
	private MyMapFragment mapFragment;

	/**
	 * Valeur pour savoir si on reçoit bien les infos du GPS ou pas
	 */
	private boolean gpsActif=false;
	/**
	 * Contient le handler qui met a jour la vue toute les xms
	 */
	private Handler updateHandler=new Handler();
	/**
	 * Runnable executée toute les x ms par le Handler
	 */
	private final Runnable runnableMiseAJour = new Runnable() {
		public void run() {
			if(gpsActif) {
				mettreAJourView();
				updateHandler.postDelayed(this, 490);
			} else {
				Log.d(LOGCAT_TAG,"GPS inactif, pas de mise a jour");
			}
		}
	};
	/**
	 * Le menu (dans l'actionbar) de notre activity
	 */
	private Menu menu;
	/**
	 * La vue de notre fragment. Permet d'interragir avec elle après le onCreateView
	 */
	private RelativeLayout vuePrincipale;
	
	/**
	 * On enregistre ici la derniere locatlisation connu.
	 */
	private Location dernierePosition = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.maCourse=new Course();
	    
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
	      //  pref.edit().clear().commit();

        //On verifie si le sportif existe, si il existe on le recupere des SharedPreferences
        if(pref.contains(this.getString(R.string.SP_annee_naissance))
        		&& pref.contains(this.getString(R.string.SP_poids))
        		&& pref.contains(this.getString(R.string.SP_taille))) {
        	associerSportifDepuisPrefs();
    	}
        else {//Sinon on ouvre un dialog pour demander d'entrer des infos
        	Intent demarrerDialogSportif = new Intent(this.getActivity(),SportifDialogActivity.class);
        	//Le startActivityForResult demarre le dialog mais se met en attente d'une réponse
        	//De façon à pouvoir traiter si l'utilisateur rentre les infos comme prévu
        	this.startActivityForResult(demarrerDialogSportif, 7);
        }
	
		//On met dans sa place réservé, le MapFragment
		FragmentManager fm = getChildFragmentManager(); 
		//Pour creer un nouveau MyMapFragment, on utilise newInstance
		//Plutôt qu'un constructeur. Conseillé par la doc
		this.mapFragment = MyMapFragment.newInstance(new GoogleMapOptions().zoomControlsEnabled(false));
		fm.beginTransaction()
		.replace(R.id.mapfragment_location, mapFragment)
		.commit();
		
		//Initialisation du LocationManager
        this.locationManager = (LocationManager)this.getActivity()
        		.getSystemService(Context.LOCATION_SERVICE);
        //On lance le GPS Status Listener, pour qu'on nous dise quand le GPS marche
   		this.locationManager.addGpsStatusListener(this);
		this.setHasOptionsMenu(true);//On signal que l'on veut recevoir les appels concernant le menu de l'action bar
		this.vuePrincipale= (RelativeLayout) inflater.inflate(R.layout.activity_course_en_cours, container, false);	
        return this.vuePrincipale;
	}

	
	/**
	 * Associe le sportif à la course depuis les infos des préférences.
	 */
    private void associerSportifDepuisPrefs() {
	     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
	     int anneeNaissance=prefs.getInt(this.getString(R.string.SP_annee_naissance), 0);
	     int taille=prefs.getInt(this.getString(R.string.SP_taille), 0);
	     float poids=prefs.getFloat(this.getString(R.string.SP_poids), 0f);
    	Sportif userSportif = new Sportif(anneeNaissance,
    			taille,
    			poids);
    	Log.d(LOGCAT_TAG,"Utilisateur est née le " + anneeNaissance + ", mesure " + taille + " et pèse " + poids);
    	this.maCourse.setUser(userSportif);
		
	}
	/**
     * Met a jour les infos de la course
     * @param caloriesBrulees le nombre de calorie brulées
     * @param vitesseReelle la vitesse de l'utilisateur
     * @param distance la distance parcouru
     */
    private void mettreAJourCourse(int caloriesBrulees, float vitesseReelle, int distance) {
    	this.maCourse.setCaloriesBrulees(caloriesBrulees);
    	this.maCourse.setVitesseReelle(vitesseReelle);
    	this.maCourse.setDistance(distance);
    }
    /**
     * Demarre la course
     */
    private void demarrerCourse() {
    	Log.d(LOGCAT_TAG, "Course démarrée");
    	this.maCourse.setDebutCourse(new Date().getTime());
    	this.maCourse.setDate(new Date());
    	this.maCourse.setDistance(0);
    	this.maCourse.setVitesseReelle(0.0f);
    	this.maCourse.setCaloriesBrulees(0);
    	this.maCourse.setEtatCourse(EtatCourse.CourseLancee);
    	menu.findItem(com.makeursport.R.id.BT_playpause)
				.setIcon(this.getResources().getDrawable(R.drawable.ic_action_pause));
    }
    /**
     * Met une course en pause
     */
    private void mettreEnPauseCourse() {
    	Log.v(LOGCAT_TAG,"Course mise en pause...");
    	this.maCourse.setEtatCourse(EtatCourse.CourseEnPause);
    	this.updateHandler.removeCallbacks(runnableMiseAJour);
		menu.findItem(com.makeursport.R.id.BT_playpause)
				.setIcon(this.getResources().getDrawable(R.drawable.ic_action_play));
    }
    /**
     * Sort une course de pause
     */
    private void reprendreCourse() {
    	Log.v(LOGCAT_TAG, "Course reprise !");
    	this.maCourse.setEtatCourse(EtatCourse.CourseLancee);
    	updateHandler.post(runnableMiseAJour);
    	menu.findItem(com.makeursport.R.id.BT_playpause)
				.setIcon(this.getResources().getDrawable(R.drawable.ic_action_pause));
    	this.mapFragment.demanderNouveauPolyline();
    }
    /**
     * Arrete une course
     */
    private void stopperCourse() {
    	Log.v(LOGCAT_TAG, "Course arrétée.");
		this.stopperLocationListener();
		this.maCourse.setEtatCourse(EtatCourse.CourseArretee);
    	this.updateHandler.removeCallbacks(runnableMiseAJour);
		menu.findItem(com.makeursport.R.id.BT_playpause)
				.setIcon(this.getResources().getDrawable(R.drawable.ic_action_play));
    }
    /**
     * Met à jour la vue, en affichant les infos de la course en cours.
     */
    private void mettreAJourView() {
    	TextView vitMoy_tv = (TextView) vuePrincipale.findViewById(R.id.TV_vit_moyenne_valeur);
    	vitMoy_tv.setText(maCourse.calculerVitesseMoyenne() + "m/s");
    	TextView vitReel_tv = (TextView) vuePrincipale.findViewById(R.id.TV_vit_reel_valeur);
    	vitReel_tv.setText(maCourse.getVitesseReelle() + "m/s");
    	TextView calories_tv = (TextView) vuePrincipale.findViewById(R.id.TV_calories_valeur);
    	calories_tv.setText(maCourse.getCaloriesBrulees() + "");
    	TextView distance_tv = (TextView) vuePrincipale.findViewById(R.id.TV_distance_valeur);
    	distance_tv.setText(maCourse.getDistance() + "km");
    	TextView duree_tv = (TextView) vuePrincipale.findViewById(R.id.TV_duree);
    	long duree = maCourse.getDuree()/1000;//Temps en ms
    	duree_tv.setText(String.format("%d:%02d:%02d", duree/(3600), (duree%3600)/(60), (duree%(60))));
    }
    /**
     * Demande la mise à jour de la localistion.
     * 
     * @see CourseEnCours#onLocationChanged()
     */
    private void demarrerLocationListener() {
   		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);//GPS, des qu'on peut (tte les 0s, 0m), on previent this
   		if(!this.gpsActif) {
   			this.signalerGPSInactif();
   		}
    }
    /**
     * Appelé quand on sait que le signal GPS est inactif
     */
    public void signalerGPSInactif() {
    	Log.d(LOGCAT_TAG,"En attente du signal GPS...");
    	View voile = this.vuePrincipale.findViewById(R.id.voileInactif);
    	AlphaAnimation animTransparence = new AlphaAnimation(0.0f,1.0f);
    	animTransparence.setDuration(500);
    	voile.setAnimation(animTransparence);
    	animTransparence.startNow();
    	voile.setVisibility(View.VISIBLE);
    	this.gpsActif=false;
    	this.vuePrincipale.findViewById(R.id.TV_messageInactif).setVisibility(TextView.VISIBLE);
    }
    /**
     * Appelé quand le signal GPS devient actif
     */
    public void signalerGPSActif() {
    	//Le GPS vient d'être déclaré actif, on démarre la course.
    	if(this.maCourse.getEtatCourse() == EtatCourse.CourseLancee) {//Si jamais on est en courselancee et qu'on avait pas le GPS activé
    		//Ca veut dire qu'on avait lancé la course et qu'on attendait l'activation du signal GPS
    		this.demarrerCourse();
    	}
    	this.gpsActif=true;
    	this.vuePrincipale.findViewById(R.id.voileInactif).setVisibility(View.GONE);
    	this.vuePrincipale.findViewById(R.id.TV_messageInactif).setVisibility(TextView.GONE);

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
		if(provider.equals(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this.getSherlockActivity(), this.getString(R.string.message_erreur_desactivation_GPS), Toast.LENGTH_LONG).show();
			this.stopperCourse();
		}
	}
	public void onProviderEnabled(String provider) {
		// Rien a faire ici
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(LOGCAT_TAG+"_onStatusChanged", "Provider:"+provider + " status:" + status);
	}

	public void onGpsStatusChanged(int event) {
		String sEvent="";
		switch(event) {
		case GpsStatus.GPS_EVENT_FIRST_FIX :
			this.signalerGPSActif();
			sEvent="GPS_EVENT_FIRST_FIX";
			break;
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			sEvent="GPS_EVENT_SATELLITE_STATUS";
			Iterator<GpsSatellite> it = this.locationManager.getGpsStatus(null).getSatellites().iterator();
			int i=0;
			while(it.hasNext()) {
				i++;it.next();	
			}
			sEvent+=" (" + i + " satellites";
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			sEvent="GPS_EVENT_STARTED";
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			sEvent="GPS_EVENT_STOPPED";
			break;
		default:
			sEvent="" + event;
		}
		Log.v(LOGCAT_TAG+"_onGPSStatusChanged", "Received : " + sEvent);
		
		
	}
	/**
	 * Lors d'un clique sur les boutons du menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.BT_playpause:
			if(!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(this.getSherlockActivity(), this.getString(R.string.GPS_desactive), Toast.LENGTH_LONG).show();
			}
			if(this.maCourse.getEtatCourse()==EtatCourse.CourseArretee) {
				this.demarrerCourse();
		    	this.demarrerLocationListener();
			}
			else if(this.maCourse.getEtatCourse()==EtatCourse.CourseEnPause){
				this.reprendreCourse();
			}
			else {
				this.mettreEnPauseCourse();
			}
			break;
		case R.id.BT_stop:
			if(this.maCourse.getEtatCourse() != EtatCourse.CourseArretee) {
				this.stopperCourse();
			}
			break;
		//Si l'on appuie sur l'icone de l'appli
		case android.R.id.home:
			((SlidingFragmentActivity) getActivity()).toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
	
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_course_en_cours, menu);
    	this.menu=menu;
    }
	
	/**
	 * Est appelé dès que la dialog SportifDialogActivity est fermé.
	 * 
	 * Plus précisement, onActivityResult est appelé dès qu'une activité démarré par
	 * startActivityForResult (cf {@link CourseEnCours#onCreate()}) est fermé.
	 * Elle sert à traiter le résultat retourné par cette activité.
	 * Ici le click sur le bouton cancel, ou le click sur le bouton confirm
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(this.LOGCAT_TAG, "Starting OnActivityResult from requestCode");
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode) {
			case Activity.RESULT_CANCELED:
				Log.d(LOGCAT_TAG, "Le sportif ne veut pas rentrer d'info : fermeture de l'appli");
				this.getActivity().finish();
				break;
			case Activity.RESULT_OK://Si on a un retour ok
				this.associerSportifDepuisPrefs();
				break;

		}
	}

	public float calculerDistance(Location loc1, Location loc2) {
	   double earthRadius = 3958.75;
	   double dLat = Math.toRadians(loc2.getLatitude()-loc1.getLatitude());
	   double dLng = Math.toRadians(loc2.getLongitude()-loc1.getLongitude());
	   double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	              Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(Math.toRadians(loc2.getLatitude())) *
	              Math.sin(dLng/2) * Math.sin(dLng/2);
	   double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	   double dist = earthRadius * c;
	
	   int meterConversion = 1609;
	
	   return new Float(dist * meterConversion).floatValue();
	}



	public double calculerCaloriesBrulees(float poids, long duree, float met) {
		double nbCaloriesBrulees = 0;	
		nbCaloriesBrulees = duree*(met*3.5*poids)/200;
		return nbCaloriesBrulees;
	}
	
	public float getMet(Sport sport, float vitesse){
		float vitKMH = vitesse * 0.277778F;
		float met = 0;
		if(sport==Sport.COURSE) {
			if(vitKMH<4){
				met = 2.3F; 
			}
			if(vitKMH==4.8){
				met = 3.3F;
			}
			if(vitKMH==5.5){
				met = 3.6F;
			}
			if(vitKMH==10){
				met = 7.0F;
			}
		}
		else if(sport==Sport.VELO){
			if(vitKMH<16){
				met = 4.0F;
			}
			if(vitKMH>16){
				met = 5.5F;
			}
		}
		else if(sport==Sport.ROLLER) {
				if(vitKMH<10){
					met = 6;
				}
				if(vitKMH>10){
					met = 7;
				}
		}
		return met;
	}
}
	