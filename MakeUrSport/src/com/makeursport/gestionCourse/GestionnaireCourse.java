package com.makeursport.gestionCourse;

public class GestionnaireCourse {
	
	private Course c;
	
	public GestionnaireCourse(){
		this.c = new Course();
	}
	
	public void lancerCourse() {
		this.c.setEtatCourse(EtatCourse.CourseLancee);
	}
	
	public void mettreEnPause () {
		this.c.setEtatCourse(EtatCourse.CourseEnPause);
	}
	
	public void arreterCourse() {
		this.c.setEtatCourse(EtatCourse.CourseArretee);
	}

	public Course getC() {
		return this.c;
	}

	public void setC(Course c) {
		this.c = c;
	}
}
