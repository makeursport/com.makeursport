package com.makeursport.gestionCourse;

public enum Sport {
	COURSE(0),
	VELO(1),
	ROLLER(2);
	
	private final int sport;
	
	Sport(int i) {
		this.sport=i;
	}
	
	public int getSportInt() {
		return sport;
	}
}
