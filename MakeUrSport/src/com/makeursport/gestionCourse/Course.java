package com.makeursport.gestionCourse;

import java.util.Date;

import com.makeursport.R;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
/**
 * Course avec ses différents caractéristiques
 *
 * @author l'équipe MakeUrSport
 */
public class Course {
	private static final String LOGCAT_TAG = "Course";
	private Sport sport;
	private long debutCourse;
	private long duree;
	private long tempsPause;
	private long debutPause;
	private double distance; //en KM
	private float vitesseReelle; //en KM/H
	//private double caloriesBrulees; //trouver la formule
	private EtatCourse etatCourse;
	private Date date;
	private Sportif user;
	private int id;
		
	/**
	 * Création d'une course vide
	 */
	public Course() {
		//debutCourse = new Date().getTime();
		this.setDistance(0);
		this.setVitesseReelle(0);
		//this.setCaloriesBrulees(0);
		this.etatCourse=EtatCourse.CourseArretee;
		this.setDate(new Date());
		this.setTempsPause(0);
		this.setSport(Sport.COURSE);
		this.tempsPause=0;
	}
	/**
	 * Création d'une course à partir de toute ses informations
	 * @param id l'id de la course
	 * @param date la date de debut de la course
	 * @param distance la distance parcouru lors de la course
	 * @param duree la durée de la course
	 * @param sport le sport pratiqué
	 */
	public Course(int id,long date,double distance ,long duree, Sport sport){
		Log.d(LOGCAT_TAG,"Création d'une course : (" + date + ") de " + distance + "km de " + duree + "s");
		this.setDate(new Date(date));
		this.setDistance(distance);
		this.setSport(sport);
		this.setDuree(duree);
		this.etatCourse=EtatCourse.CourseArretee;
		this.id=id;
	}
	/**
	 * Met la durée de la course à jour
	 * @param duree la durée de la course, en millisecondes
	 */
	private void setDuree(long duree) {
		this.duree = duree;
	}
	/**
	 * Recuperation de l'id de la course
	 * @return l'id de la course
	 */
	 public int getId() {
		 return this.id;
	 }
	 
	/**
	 * @return la durée de la course en seconde
	 */
	public long getDuree() {
		if(this.etatCourse==EtatCourse.CourseArretee) {
			return this.duree;
		}
		return (new Date().getTime() - this.getTempsPause() - this.debutCourse)/1000;
	}
	

	/**
	 * Change la date de la course
	 * @param date la date en ms 
	 */
	private void setDate(long date) {
		this.date=new Date(date);
	}
	
	/**
	 * Met a jour le debut de la course
	 * @param debutCourse le moment de début de la course
	 */
	public void setDebutCourse(long debutCourse) {
		this.debutCourse = debutCourse;
	}


	/**
	 * Récuperer la durée de temps en pause pendant cette course
	 * @return la durée pendant laquelle l'utilisateur à été en pause (dans tempsPause)
	 */
	public long getTempsPause() {
		return this.tempsPause;
	}
	
	/**
	 * Modifie la durée de temps en pause de cette course
	 * @param tempsPause le temps en pause 
	 */
	public void setTempsPause(long tempsPause) {
		this.tempsPause=tempsPause;
	}

	/**
	 * rajoute une durée au temps de pause
	 * @param tempsPause rajoute tempsPause au temps que l'on a passé en pause
	 */
	public void addTempsPause(long tempsPause) {
		this.setTempsPause(this.getTempsPause()+tempsPause);
	}

	/**
	 * Calcul la vitesse moyenne de l'utilisateur pour cette course
	 * @return vitesse moyenne de la course en km/h
	 */
	public double getVitesseMoyenne () {
		Log.d(LOGCAT_TAG,"Vitesse Moy : " + (double) this.getDistance() + " / " + (double) this.getDuree()/3600);
		double vitKms = this.getDistance()*1000 / this.getDuree();
		return (double) (Math.floor(vitKms/3.6*100)/100);
	}


	/**
	 * Retourne la distance effectuée de la course
	 * @return distance de la course en km
	 */
	private double getDistance() {
		return this.distance;
	}
	/**
	 * Retourne la distance arrondie effectuée de la course
	 * @return la distance de la course en km, arrondi au centième
	 */
	public float getDistanceArrondi() {
		Log.w(LOGCAT_TAG, this.getDistance() + " : " + (Math.floor(this.getDistance()*100)/100));
		return (float) (Math.floor(this.getDistance()*100)/100);
	}


	/**
	 * Met à jour la distance de la course
	 * @param distance en km
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	/**
	 * Rajoute une distance à la course
	 * @param distance en km
	 */
	public void addDistance(double distance) {
		this.distance+=distance;
	}

	/**
	 * Retourne la vitesse reelle de la course
	 * @return Vitesse reel en ce moment en km/h
	 */
	public float getVitesseReelle() {
		return (float) Math.floor((this.vitesseReelle*100)/100);
	}


	/**
	 * Met à jour la vitesse reel de la course
	 * @param vitesseReelle en km/h
	 */
	public void setVitesseReelle(float vitesseReelle) {
		this.vitesseReelle = vitesseReelle;
	}


	/**
	 * recupere les calories brûlées de la course
	 * @return les calories brûlées
	 */
	public float getCaloriesBrulees() {
		if(this.getUser() == null) {
			Log.e(LOGCAT_TAG,"getUser() == null");
		} else if(this.getSport()==null) {
			Log.e(LOGCAT_TAG,"sport==null");
		}
		float duree = ((float)this.getDuree()) /60.0F;
		double caloriesBrulees = this.calculerCaloriesBrulees(this.getUser().getPoids(),duree,this.getMet(this.getSport(), this.getVitesseMoyenne()) );
		
		return (float) (Math.floor(caloriesBrulees*100)/100);
	}
	/**
	 * Sauvegarde cette course dans l'historique
	 * @param context Le context de l'application
	 */
	public void sauvegarderCourse(Context context) {
		Log.d(LOGCAT_TAG, "Sauvegarde de la course...");
		if(this.getDuree() > 1) {
			GestionnaireHistorique gest = new GestionnaireHistorique(context);
			gest.enregistrerCourse(this);
			Toast.makeText(context, context.getText(R.string.course_sauvegardee), Toast.LENGTH_LONG).show();
		} else {
			Log.w(LOGCAT_TAG, "Ou pas : this.getDuree()=" + this.getDuree());
		}
	}

	/**
	 * recupere l'etat de la course
	 * @return l'etat de la course
	 */
	public EtatCourse getEtatCourse() {
		return this.etatCourse;
	}


	/**
	 * Met a jour l'etat de la course,  en prenant en compte les temps de pause
	 * @param etatCourse le nouvel etat de la course
	 */
	public void setEtatCourse(EtatCourse etatCourse) {
		if(this.etatCourse==EtatCourse.CourseEnPause && etatCourse==EtatCourse.CourseLancee) {
			this.addTempsPause(new Date().getTime() - this.debutPause);
		}
		else if(this.etatCourse==EtatCourse.CourseLancee && etatCourse==EtatCourse.CourseEnPause) {
			this.debutPause = new Date().getTime();
		} else if(this.etatCourse == EtatCourse.CourseArretee && etatCourse == EtatCourse.CourseLancee) {
			this.setDebutCourse(new Date().getTime());
		} else if(etatCourse == EtatCourse.CourseArretee) {
			Log.d(LOGCAT_TAG + "_arretCourse", "On remplace la duree par la duree("+this.getDuree());
			this.duree = this.getDuree();
		}
		this.etatCourse = etatCourse;
	}


	/**
	 * recupere le sportif
	 * @return le sportif
	 */
	public Sportif getUser() {
		return this.user;
	}


	/**
	 * met a jour le sportif de la course
	 * @param user le sportif
	 */
	public void setUser(Sportif user) {
		Log.d(LOGCAT_TAG, "mise à jour de l'user");
		this.user = user;
	}



	/**
	 * Retourne la date de début de la course
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * change la date de début de la course
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Recupere le sport de la course
	 * @return le sport
	 */
	public Sport getSport() {
		return sport;
	}

	/**
	 * met a jour le sport de la course
	 * @param sport 
	 */
	public void setSport(Sport sport) {
		this.sport = sport;
	}

	

	/**
	 * Calcul le nombre de calories brulées 
	 * @param poids le poids du sportif en kg
	 * @param duree la durée de la course en minutes
	 * @param met le MET du sport, 
	 * @return le nombre de calories brûlées par le sportif
	 */
	private double calculerCaloriesBrulees(float poids, double duree, float met) {
		double nbCaloriesBrulees = 0;
		nbCaloriesBrulees = duree*(met*3.5*poids)/200;
		return nbCaloriesBrulees;
	}
	
	/**
	 * Calcul le MET d'un sport a une vitesse particulière
	 * @param sport le sport pratiqué
	 * @param vitesse l'allure du sportif (en m/s)
	 * @return le MET du sport 
	 */
	private float getMet(Sport sport, double vitesse){
		float met = 1.0f;
		if(sport==Sport.COURSE) {
			if(vitesse<4){
				met = 2.3F; 
			}
			else if(vitesse<=4.8){
				met = 3.3F;
			}
			else if(vitesse<=5.5){
				met = 3.6F;
			}
			else if(vitesse<=10){
				met = 7.0F;
			}
		}
		else if(sport==Sport.VELO){
			if(vitesse<16){
				met = 4.0F;
			}
			else if(vitesse>16){
				met = 5.5F;
			}
		}
		else if(sport==Sport.ROLLER) {
				if(vitesse<10){
					met = 6;
				}
				else if(vitesse>10){
					met = 7;
				}
		}
		return met;
	}
}