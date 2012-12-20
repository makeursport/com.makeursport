package com.makeursport;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
/**
 * Map Fragment utilisé pour afficher une carte, et tracé le parcours de l'utilisateur
 * @author l'équipe MakeUrSport
 * @see SupportMapFragment
 */
public class MyMapFragment extends SupportMapFragment  {
	private final String LOGCAT_TAG = this.getClass().getCanonicalName();
	private GoogleMap carte;
	private Polyline trace;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	carte = this.getMap();
    	carte.setMyLocationEnabled(true);//On souhaite afficher la position de l'utilisateur
    	View view = super.onCreateView(inflater, container, savedInstanceState);
    	return view;
    }
    /**
     * Initialise la carte (met sur la position de l'utilisateur,
     * zoom, et rajoute la première position dans la liste des points
     * @param longitude La longitude du sportif
     * @param latitude La latitude du sportif
     */
    public void initialiserCarte(double latitude, double longitude) {
    	Log.v(LOGCAT_TAG, "Initialisation de la carte");
    	LatLng pos = new LatLng(latitude,longitude);
    	trace = carte.addPolyline(new PolylineOptions().add(pos).color(Color.BLUE).visible(true));
        carte.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,15));
    }
    
    /**
     * Met a jour la carte a partir des coordonnées données
     * (Affiche le tracé de l'utilisateur sur la carte)
     * @param longitude La longitude du sportif
     * @param latitude La latitude du sportif
     */
    public void mettreAJourCarte(double latitude, double longitude) {
    	//TODO
    	Log.v(LOGCAT_TAG, "Mise à jour de la carte");
    	if(trace!=null && carte != null) {
        	LatLng pos = new LatLng(latitude,longitude);
	    	List<LatLng> points = trace.getPoints();
	    	points.add(pos);
	        carte.animateCamera(CameraUpdateFactory.newLatLng(pos));
	        trace.setPoints(points);
    	}
    	else {
    		Log.v(LOGCAT_TAG, "Carte non initialisé. Init...");
    		initialiserCarte(latitude,longitude);
    	}
    }
}
