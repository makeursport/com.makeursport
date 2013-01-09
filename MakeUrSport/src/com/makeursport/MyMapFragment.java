package com.makeursport;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
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
	/**
	 * Liste de tout les polyline de notre carte
	 */
	private LinkedList<Polyline> traces;
	/**
	 * Booleen qui vaut vrai dès qu'il faut creer une polyline
	 */
	private boolean besoinDunNouveauPolyline=true;
	
	/**
	 * Créer une nouvelle instance de MyMapFragment
	 * en lui passant des options (GoogleMapOptions)
	 * @param opt Les options a passé
	 * @return le MyMapFragment crée
	 */
	public static MyMapFragment newInstance(GoogleMapOptions opt) {
		MyMapFragment fragment = new MyMapFragment();
        Bundle args = new Bundle();
        args.putParcelable("MapOptions", opt);
        fragment.setArguments(args);
		return fragment;
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = super.onCreateView(inflater, container, savedInstanceState);
    	setMapTransparent((ViewGroup) view);
    	traces = new LinkedList<Polyline>();
    	carte = this.getMap();
    	carte.setMyLocationEnabled(true);//On souhaite afficher la position de l'utilisateur
    	return view;
    }
    /**
     * Initialise la carte (met sur la position de l'utilisateur,
     * zoom, et rajoute la première position dans la liste des points
     * @param longitude La longitude du sportif
     * @param latitude La latitude du sportif
     */
    private void initialiserCarte(double latitude, double longitude) {
    	Log.v(LOGCAT_TAG, "Initialisation de la carte");
    	LatLng pos = new LatLng(latitude,longitude);
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
    	if(traces!=null && traces.isEmpty()) {
    		this.initialiserCarte(latitude, longitude);
    	}
    	if(besoinDunNouveauPolyline) {
    		this.creerNouveauPolyline(latitude, longitude);
    		this.besoinDunNouveauPolyline=false;
    	}
		Polyline lastTrace = traces.getLast();
    	LatLng pos = new LatLng(latitude,longitude);
    	List<LatLng> points = lastTrace.getPoints();
    	points.add(pos);
        //carte.animateCamera(CameraUpdateFactory.newLatLng(pos));
        lastTrace.setPoints(points);
        //traces.set(traces.size(), lastTrace);
    }
    /**
     * Methode qui permet de demander la création d'un nouveau polyline.
     * Elle est appelé dans {@link com.makeursport.CourseEnCours} une fois
     * que l'utilisateur sort de pause
     */
    public void demanderNouveauPolyline() {
    	this.besoinDunNouveauPolyline=true;
    }
    
    /**
     * Methode qui creer un nouveau Polyline dans la liste {@link #traces}
     * @param lat la latitude du point d'origine
     * @param lng la longitude du point d'origine
     */
    private void creerNouveauPolyline(double lat, double lng) {
    	LatLng pos = new LatLng(lat, lng);
    	traces.add(carte.addPolyline(new PolylineOptions().add(pos).color(Color.BLUE).visible(true)));
    }
    /**
     * Methode qui met la carte transparente. Permet de regler un problème de GoogleMaps
     * @param group la vu qui contient la carte que l'on doit mettre transparente
     */
    private void setMapTransparent(ViewGroup group) {
    	int childCount = group.getChildCount();
    	for (int i = 0; i < childCount; i++) {
	    	View child = group.getChildAt(i);
	        if (child instanceof ViewGroup) {
	            setMapTransparent((ViewGroup) child);
	        } else if (child instanceof SurfaceView) {
	            child.setBackgroundColor(0x00000000);
	        }
	    }
	}
}
