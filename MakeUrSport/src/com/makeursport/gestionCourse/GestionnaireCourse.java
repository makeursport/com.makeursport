package com.makeursport.gestionCourse;

import java.util.Date;

import android.location.Location;
import android.util.Log;
/**
 * Gestionnaire d'une course précise
 * @author l'équipe MakeUrSport
 * 
 */
public class GestionnaireCourse {
	private final static String LOGCAT_TAG="GestionnaireCourse";
	private Course maCourse;
	
	public GestionnaireCourse(){
		this.maCourse = new Course();
	}
	
	/**
     * Met a jour les infos de la course
     * @param vitesseReelle la vitesse de l'utilisateur en m/s
     * @param loc1 le premier point (pour la distance)
     * @parma loc2 le deuxieme point
     */
    public void mettreAJourCourse(float vitesseReelle, Location loc1, Location loc2) {
    	Log.v(LOGCAT_TAG, "Mise à jour de la course : v:" + (vitesseReelle*3.6f) + " - lat:" + loc2.getLatitude());
    	this.maCourse.setVitesseReelle(vitesseReelle * 3.6f);
    	//this.maCourse.setDistance(distance);
    	this.maCourse.addDistance(this.calculerDistance(loc1, loc2));
    }
    /**
     * Demarre la course
     */
    public void demarrerCourse() {
    	Log.d(LOGCAT_TAG, "Course démarrée");
    	this.maCourse.setDebutCourse(new Date().getTime());
    	this.maCourse.setDate(new Date());
    	this.maCourse.setDistance(0);
    	this.maCourse.setVitesseReelle(0.0f);
    	this.maCourse.setEtatCourse(EtatCourse.CourseLancee);
    }
    /**
     * Met une course en pause
     */
    public void mettreEnPauseCourse() {
    	Log.v(LOGCAT_TAG,"Course mise en pause...");
    	this.maCourse.setEtatCourse(EtatCourse.CourseEnPause);
    }
    /**
     * Sort une course de pause
     */
    public void reprendreCourse() {
    	Log.v(LOGCAT_TAG, "Course reprise !");
    	this.maCourse.setEtatCourse(EtatCourse.CourseLancee);
    }
    /**
     * Arrete une course
     */
    public void stopperCourse() {
    	Log.v(LOGCAT_TAG, "Course arrétée.");
		this.maCourse.setEtatCourse(EtatCourse.CourseArretee);
    }
    /**
     * Recupere l'état de la course en cours
     * @return l'etat de la course
     */
    public EtatCourse getEtatCourse() {
    	return this.maCourse.getEtatCourse();
    }
    /**
     * Modifie l'état de la course
     * @param etatCourse 
     */
    public void setEtatCourse(EtatCourse etatCourse) {
    	this.maCourse.setEtatCourse(etatCourse);
    }
    /**
     * recupere la course en cours
     * @return la course
     */
	public Course getCourse() {
		return this.maCourse;
	}
	/**
	 * Remplace la course en cours par une nouvelle course
	 * @param c la nouvelle course
	 */
	public void setCourse(Course c) {
		this.maCourse = c;
	}
	
	/**
	 * Calcule la distance entre deux points
	 * @param loc1 le premier point
	 * @param loc2 le deuxieme point
	 * @return distance parcouru entre les points, en km
	 */
	private float calculerDistance(Location loc1, Location loc2) {
	   double earthRadius = 6371;
	   double dLat = Math.toRadians(loc2.getLatitude()-loc1.getLatitude());
	   double dLng = Math.toRadians(loc2.getLongitude()-loc1.getLongitude());
	   double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	              Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(Math.toRadians(loc2.getLatitude())) *
	              Math.sin(dLng/2) * Math.sin(dLng/2);
	   double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	   double dist = earthRadius * c;
	   Log.v(LOGCAT_TAG + "_calculerDistance", dist + "");
	   return Double.valueOf(dist).floatValue();
	}
	
	
}
