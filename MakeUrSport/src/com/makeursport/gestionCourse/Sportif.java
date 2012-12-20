package com.makeursport.gestionCourse;

import java.util.Calendar;

public class Sportif {
	
	private int anneeNaissance;
	private int taille;
	private float poids;
	public Sportif () {

	}
	public Sportif(int anneeNaissance, int taille, float poids) {
		this.anneeNaissance = anneeNaissance;
		this.setTaille(taille);
		this.setPoids(poids);
		
	}
	
	public int getAge() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR) - this.anneeNaissance;//age this.anneeNaissance
	}

	public int getTaille() {
		return taille;
	}

	public void setTaille(int taille) {
		this.taille = taille;
	}

	public float getPoids() {
		return poids;
	}

	public void setPoids(float poids) {
		this.poids = poids;
	}
	
	public String toString() {
		return "Age : " + this.getAge() + "; Poids" + this.getPoids() + "; Taille :" + this.getTaille();
	}
	
}
