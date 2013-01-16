package com.makeursport;

import java.util.ArrayList;
import java.util.Iterator;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.makeursport.gestionCourse.Course;
import com.makeursport.gestionCourse.EtatCourse;
import com.makeursport.gestionCourse.GestionnaireCourse;
import com.makeursport.gestionCourse.Sport;
import com.makeursport.gestionCourse.Sportif;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activité de la CourseEnCours
 * Gère l'affichage des infos d'une course, gérer par un @{link GestionnaireCourse}
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
	 * Le gestionnaire de la course en cours
	 */
	private GestionnaireCourse gestCourse;
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
	
	/**
	 * String utilisé pour partagé un parcours dans un Bundle
	 */
	public static final String PARCOURS = "com.makeursport.PARCOURS";
	/**
	 * String utilisé pour partagé un Sport dans un intent
	 */
	public static final String SPORT_INTENT = "com.makeusrport.SPORTINTENT";
	/**
	 * Numéro de requete utilisé lors du lancement de {@link SportifDialogActivity}
	 */
	public static final int DIALOG_SPORTIF_REQUEST_CODE =7;
	/**
	 * Numéro de requete utilisé lors du lancement de {@link SportDialog}
	 */
	public static final int DIALOG_SPORT_REQUEST_CODE = 8;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.gestCourse=new GestionnaireCourse();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
	      //  pref.edit().clear().commit();

        //On verifie si le sportif existe, si il existe on le recupere des SharedPreferences
        if(pref.contains(this.getString(R.string.SP_annee_naissance))
        		&& pref.contains(this.getString(R.string.SP_poids))
        		&& pref.contains(this.getString(R.string.SP_taille))) {
        	this.gestCourse.getCourse().setUser(Sportif.fromPrefs(this.getSherlockActivity()));
    	}
        else {//Sinon on ouvre un dialog pour demander d'entrer des infos
        	Intent demarrerDialogSportif = new Intent(this.getActivity(),SportifDialogActivity.class);
        	//Le startActivityForResult demarre le dialog mais se met en attente d'une réponse
        	//De façon à pouvoir traiter si l'utilisateur rentre les infos comme prévu
        	this.startActivityForResult(demarrerDialogSportif, DIALOG_SPORTIF_REQUEST_CODE);
        }
	
		//On met dans sa place réservé, le MapFragment
		FragmentManager fm = getChildFragmentManager(); 
		//Pour creer un nouveau MyMapFragment, on utilise newInstance
		//Plutôt qu'un constructeur. Conseillé par la doc
		this.mapFragment = MyMapFragment.newInstance(new GoogleMapOptions().zoomControlsEnabled(false));
		fm.beginTransaction()
		.replace(R.id.mapfragment_location, mapFragment)
		.commit();
		
	    if(this.getArguments() != null && this.getArguments().containsKey(PARCOURS)) {
			ArrayList<LatLng> parcours = this.getArguments().getParcelableArrayList(PARCOURS);
	    	this.mapFragment.setParcours(parcours);
	    }
		
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
     * Met à jour la vue, en affichant les infos de la course en cours.
     */
    private void mettreAJourView() {
    	Log.d(LOGCAT_TAG, "Mise à jour de la vue");
    	Course course = gestCourse.getCourse();
    	TextView vitMoy_tv = (TextView) vuePrincipale.findViewById(R.id.TV_vit_moyenne_valeur);
    	vitMoy_tv.setText(course.getVitesseMoyenne() + this.getString(R.string.unite_vitesse));
    	TextView vitReel_tv = (TextView) vuePrincipale.findViewById(R.id.TV_vit_reel_valeur);
    	vitReel_tv.setText(course.getVitesseReelle() + this.getString(R.string.unite_vitesse));
    	TextView calories_tv = (TextView) vuePrincipale.findViewById(R.id.TV_calories_valeur);
    	calories_tv.setText(course.getCaloriesBrulees() + "");
    	TextView distance_tv = (TextView) vuePrincipale.findViewById(R.id.TV_distance_valeur);
    	distance_tv.setText(course.getDistanceArrondi() + this.getString(R.string.unite_distance));
    	TextView duree_tv = (TextView) vuePrincipale.findViewById(R.id.TV_duree);
    	long duree = course.getDuree();
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
    private void signalerGPSInactif() {
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
    private void signalerGPSActif() {
    	//Le GPS vient d'être déclaré actif, on démarre la course.
    	if(this.gestCourse.getEtatCourse() == EtatCourse.CourseLancee) {//Si jamais on est en courselancee et qu'on avait pas le GPS activé
    		//Ca veut dire qu'on avait lancé la course et qu'on attendait l'activation du signal GPS
    		this.gestCourse.demarrerCourse();
    	}
    	this.gpsActif=true;
    	cacherVoile();
    }
    /**
     * Cache le "voile" du GPS inactif
     */
    private void cacherVoile() {
    	View voile = this.vuePrincipale.findViewById(R.id.voileInactif);
    	if(voile.getVisibility()==View.VISIBLE){
    		voile.setVisibility(View.GONE);
    		this.vuePrincipale.findViewById(R.id.TV_messageInactif).setVisibility(View.GONE);
    	}
    }
    /**
     * Met fin à la mise à jour du location listener
     * (démarré à l'aide du {@link CourseEnCours#demarrerLocationListener})
     */
    private void stopperLocationListener() {
    	locationManager.removeUpdates(this);
    }


    /**
     * Méthode appelée quand la position de l'utilisateur change
     * @param loc la position de l'utilisateur
     */
   	public void onLocationChanged(Location loc) {
   		if(this.gestCourse.getEtatCourse()== EtatCourse.CourseLancee) {
   			if(this.dernierePosition!=null) {
   				Log.d(LOGCAT_TAG + "_OnLocationChanged", "dernierePosition!=null");
   				float vitesseReelle = loc.getSpeed();
   				this.gestCourse.mettreAJourCourse(vitesseReelle, this.dernierePosition, loc);
   			}
			this.dernierePosition=loc;
   			this.mapFragment.mettreAJourCarte(loc.getLatitude(),loc.getLongitude());
   			if(!this.gpsActif) {
   				this.signalerGPSActif();
   			}
   		}
   		else {
   			Log.d(LOGCAT_TAG, "Course en pause... Les infos ne sont pas mise à jour...");
   		}
   	}
	public void onProviderDisabled(String provider) {
		if(provider.equals(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this.getSherlockActivity(), this.getString(R.string.message_erreur_desactivation_GPS), Toast.LENGTH_LONG).show();
			this.gestCourse.stopperCourse();
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
			
			if(this.gestCourse.getEtatCourse()==EtatCourse.CourseArretee) {//Si la course est arrétée : demarrage
				this.gestCourse.demarrerCourse();
				this.mapFragment.supprimerMapGesture();
		    	this.updateHandler.post(runnableMiseAJour);
		    	this.swapIcons(this.gestCourse.getEtatCourse());
		    	this.demarrerLocationListener();
			}
			else if(this.gestCourse.getEtatCourse()==EtatCourse.CourseEnPause){//Si la course est en pause : démarrage
				this.gestCourse.reprendreCourse();
				this.mapFragment.activerMapGesture();
		    	this.swapIcons(this.gestCourse.getEtatCourse());
		    	this.updateHandler.post(runnableMiseAJour);
		    	this.mapFragment.demanderNouveauPolyline();
			}
			else {//Sinon : la course est en cours, mettre en pause.
				this.gestCourse.mettreEnPauseCourse();
				this.mapFragment.activerMapGesture();
				this.swapIcons(this.gestCourse.getEtatCourse());
		    	this.updateHandler.removeCallbacks(runnableMiseAJour);
			}
			break;
		case R.id.BT_stop:
			if(this.gestCourse.getEtatCourse() != EtatCourse.CourseArretee) {
				this.gestCourse.stopperCourse();
		    	this.swapIcons(this.gestCourse.getEtatCourse());
		    	this.cacherVoile();
				this.mapFragment.activerMapGesture();
		    	this.stopperLocationListener();
		    	this.updateHandler.removeCallbacks(runnableMiseAJour);
			}
			break;
		case R.id.BT_sport:
			Intent i = new Intent(this.getSherlockActivity(), SportDialog.class);
			getActivity().startActivityForResult(i,DIALOG_SPORT_REQUEST_CODE);
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
	/**
	 * Echange l'icone play/pause en fonction de l'état de la course
	 * @param etat l'état dans lequel vient tout juste de rentrer de la course
	 */
	private void swapIcons(EtatCourse etat) {
		switch(etat) {
		case CourseArretee:
		case CourseEnPause:
			menu.findItem(com.makeursport.R.id.BT_playpause)
			.setIcon(this.getResources().getDrawable(R.drawable.ic_action_play));
			break;
		case CourseLancee:
			menu.findItem(com.makeursport.R.id.BT_playpause)
			.setIcon(this.getResources().getDrawable(R.drawable.ic_action_pause));
			break;
			
		}
	}
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_course_en_cours, menu);
    	this.menu=menu;
		changeSportIcone(gestCourse.getCourse().getSport());
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
		Log.v(this.LOGCAT_TAG, "Starting OnActivityResult from requestCode" + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==DIALOG_SPORTIF_REQUEST_CODE) {
			switch(resultCode) {
				case Activity.RESULT_CANCELED:
					Log.d(LOGCAT_TAG, "Le sportif ne veut pas rentrer d'info : fermeture de l'appli");
					this.getActivity().finish();
					break;
				case Activity.RESULT_OK://Si on a un retour ok
		        	this.gestCourse.getCourse().setUser(Sportif.fromPrefs(this.getSherlockActivity()));
					break;
			}	
		} else if (requestCode == DIALOG_SPORT_REQUEST_CODE){
			switch(resultCode) {
			case Activity.RESULT_OK:
				int sport =data.getExtras().getInt(SPORT_INTENT);
				this.gestCourse.getCourse().setSport(Sport.getSport(sport));
				this.changeSportIcone(Sport.getSport(sport));
				break;
			}
		}
	}

	/**
	 * Change l'icone du sport
	 * @param sport le sport a afficher
	 */
	private void changeSportIcone(Sport sport) {
		int res=R.drawable.ic_action_course;
		switch(sport) {
		case COURSE:
			break;
		case ROLLER:
			res=R.drawable.ic_action_roller;
			break;
		case VELO:
			res=R.drawable.ic_action_velo;
			break;
		}
		menu.findItem(com.makeursport.R.id.BT_sport)
		.setIcon(res);
	}
}
	