package com.makeursport.gestionCourse;
/**
 * Enum des trois état de la course
 * @author l'équipe MakeUrSport
 * 
 */
public enum EtatCourse {
	CourseLancee(0),
	CourseArretee(1),
	CourseEnPause(2);
	private final int statut;
	EtatCourse(int i) {
		this.statut=i;
	}
	double getStatusInt() {
		return statut;
	}
	
	
	}
