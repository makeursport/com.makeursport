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
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
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
	private List<LatLng> pointsParcours;
	@SuppressWarnings("unused")
	private Polyline parcoursPolyline;
	/**
	 * Liste de tout les polyline de notre carte
	 */
	private LinkedList<Polyline> traces = null;
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
    	carte = this.getMap();
    	carte.setMyLocationEnabled(true);//On souhaite afficher la position de l'utilisateur
    	
    	if(pointsParcours!=null) {
    		this.dessinnerParcours();
    	}
    	return view;
    }
    /**
     * Initialise la carte (met sur la position de l'utilisateur,
     * zoom, et rajoute la première position dans la liste des points
     * @param longitude La longitude du sportif
     * @param latitude La latitude du sportif
     */
    private void initialiserCarte(double latitude, double longitude) {
    	Log.v(LOGCAT_TAG, "Initialisation de la carte ("+latitude+","+longitude+")");
    	LatLng pos = new LatLng(latitude,longitude);
    	traces = new LinkedList<Polyline>();
    	if(this.carte==null) {
    		Log.e(LOGCAT_TAG+"_mapnull", "Map is null...");
    	}
    	carte.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
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
    	if(traces==null || traces.isEmpty()) {
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
        carte.animateCamera(CameraUpdateFactory.newLatLng(pos));
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
    /**
     * définit le parcours d'une course
     * @param pointsParcours les points du parcours
     */
    public void setParcours(List<LatLng> pointsParcours) {
    	this.pointsParcours=pointsParcours;
    }
    /**
     * Dessine le parcours précedemment définis
     */
    private void dessinnerParcours() {
		//Il ne reste plus qu'à afficher l'itinéraire sur la carte
		PolylineOptions traceOptions = new PolylineOptions().color(Color.BLUE).visible(true);
		for (LatLng geoPoint : pointsParcours){
			traceOptions.add(geoPoint);
		}
		traceOptions.color(Color.parseColor("#33b5e5"));
		if(this.carte==null) {
			try {
				MapsInitializer.initialize(this.getActivity().getApplicationContext());
				this.carte = this.getMap();
			} catch (GooglePlayServicesNotAvailableException e) {
				Toast.makeText(this.getActivity(), this.getString(R.string.play_service_not_available), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
		this.parcoursPolyline = this.carte.addPolyline(traceOptions);
		//this.initialiserCarte(this.pointsParcours.get(0).latitude, this.pointsParcours.get(0).longitude);
		//this.parcours.setPoints(pointsParcours);
    }
    /**
     * Supprime les gestures sur la carte 
     */
    public void supprimerMapGesture() {
    	//this.carte.getUiSettings().setAllGesturesEnabled(false);
    	this.carte.getUiSettings().setScrollGesturesEnabled(false);
    }
    /**
     * Ré-active les gestres sur la carte
     */
    public void activerMapGesture() {
    	this.carte.getUiSettings().setScrollGesturesEnabled(true);
    	//this.carte.getUiSettings().setAllGesturesEnabled(true);
    }

}
