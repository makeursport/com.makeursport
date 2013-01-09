package com.makeursport.gestionCourse;

import java.util.Date;

public class Course {
	private long debutCourse;
	private long tempsPause;
	private long debutPause;
	private float distance; //en KM
	private float vitesseReelle; //en KM/H
	private int caloriesBrulees; //trouver la formule
	private EtatCourse etatCourse;
	private Date date;
	private Sportif user;
	
	public Course() {
		//debutCourse = new Date().getTime();
		this.setDistance(0);
		this.setVitesseReelle(0);
		this.setCaloriesBrulees(0);
		this.setEtatCourse(EtatCourse.CourseArretee);
		this.setDate(new Date());
		this.tempsPause=0;
	}

	
	/**
	 * @return la durée de la course
	 */
	public long getDuree() {
		return new Date().getTime() - this.getTempsPause() - this.debutCourse ;
	}


	/**
	 * @param debutCourse le moment de début de la course
	 */
	public void setDebutCourse(long debutCourse) {
		this.debutCourse = debutCourse;
	}


	/**
	 * @return la durée pendant laquelle l'utilisateur à été en pause (dans tempsPause)
	 */
	public long getTempsPause() {
		return this.tempsPause;
	}


	/**
	 * @param tempsPause rajoute tempsPause au temps que l'on a passé en pause
	 */
	public void addTempsPause(long tempsPause) {
		this.tempsPause += tempsPause;
	}

	/**
	 * Calcul la vitesse moyenne de l'utilisateur pour cette course
	 * @return vitesse moyenne de la course
	 */
	public float calculerVitesseMoyenne () {
		return this.getDistance() / this.getDuree();
	}


	/**
	 * Retourne la distance effectuée de la course
	 * @return distance de la course
	 */
	public float getDistance() {
		return this.distance;
	}



	public void setDistance(float distance) {
		this.distance = distance;
	}



	public float getVitesseReelle() {
		return this.vitesseReelle;
	}



	public void setVitesseReelle(float vitesseReelle) {
		this.vitesseReelle = vitesseReelle;
	}



	public int getCaloriesBrulees() {
		return this.caloriesBrulees;
	}



	public void setCaloriesBrulees(int caloriesBrulees) {
		this.caloriesBrulees = caloriesBrulees;
	}



	public EtatCourse getEtatCourse() {
		return this.etatCourse;
	}



	public void setEtatCourse(EtatCourse etatCourse) {
		if(this.etatCourse==EtatCourse.CourseEnPause && etatCourse==EtatCourse.CourseLancee) {
			this.addTempsPause(new Date().getTime() - this.debutPause);
		}
		else if(this.etatCourse==EtatCourse.CourseLancee && etatCourse==EtatCourse.CourseEnPause) {
			this.debutPause = new Date().getTime();
		}
		this.etatCourse = etatCourse;
	}



	public Sportif getUser() {
		return this.user;
	}



	public void setUser(Sportif user) {
		this.user = user;
	}



	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	
}