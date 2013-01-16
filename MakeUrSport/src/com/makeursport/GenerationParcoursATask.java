package com.makeursport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.maps.model.LatLng;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * ASyncTask qui permet de gérer la génération de parcours
 * en arrière plan
 * 
 */
public class GenerationParcoursATask extends AsyncTask<LatLng,Void,ArrayList<LatLng>> {
	private final static String LOGCAT_TAG = "GenerationParcoursDialog";
	private MainActivity context;	
	private float dist;
	
	public GenerationParcoursATask(float distance, MainActivity context) {
		this.dist = distance;
		this.context=context;
	}
	
	@Override
	protected ArrayList<LatLng> doInBackground(LatLng... ptDepart) {
		// TODO Auto-generated method stub
		ArrayList<LatLng> geoPoints = null;
		
		//On génère un angle aléatoire dont la mesure est comprise entre 0 et 360°
		Random r = new Random();
		int angleAleatoire = r.nextInt(360);
		
		LatLng arrivee = pointDestination(ptDepart[0], this.dist, angleAleatoire);
		Log.d(LOGCAT_TAG, "ptDpartLAT:" + ptDepart[0].latitude + "ptArriveLAT" + arrivee.latitude);
		//On fabrique maintenant l'url pour envoyer une requête HTTP à Google Maps
		String url = makeUrl(ptDepart[0], arrivee);
		
		//On exécute puis récupère le résultat de la requête HTTP
		DefaultHttpClient httpClient = new DefaultHttpClient();
		@SuppressWarnings("unused")
		HttpClient http = sslClient(httpClient);
		HttpGet requete = new HttpGet(url);
		HttpResponse res;
		try {
			res = httpClient.execute(requete);
			HttpEntity entity = res.getEntity();
			String route = null; //résultat de la requête qui contiendra les points géographiques cryptés
			if (entity != null){
				route = EntityUtils.toString(entity);
			}
			Log.d(LOGCAT_TAG,"Chemin reçu : " + route);
			//On décode les points géographiques cryptés
			geoPoints = decodeEncryptedGeopoints(route);
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return geoPoints;
	}
	
	@Override
	protected void onPostExecute(ArrayList<LatLng> result){
		Fragment f = new CourseEnCours();
		Bundle extras = new Bundle();
		extras.putParcelableArrayList(CourseEnCours.PARCOURS, result);
		f.setArguments(extras);
		context.switchContent(f);
		//this.a.setResult(Activity.RESULT_OK, data);
		//carte.dessinerParcours(result);
	}
	
	// Source : http://stackoverflow.com/questions/7622004/android-making-https-request/13485550#13485550 by rposky
	private HttpClient sslClient(HttpClient client) {
	    try {
	        X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}					
	        };
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        ClientConnectionManager ccm = client.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("https", ssf, 443));
	        return new DefaultHttpClient(ccm, client.getParams());
	    } catch (Exception ex) {
	        return null;
	    }
	}
	
	/**
	 * Génère un point de destination aléatoire
	 * @param pointDepart à partir d'un point de départ
	 * @param dist et d'une distance donnée
	 * @param angleAleatoire angle aléatoire en DEGRES
	 * @return le point de destination généré aléatoirement
	 */
	private LatLng pointDestination(LatLng pointDepart, float dist, double angleAleatoire){
		double lat, latV1, latV2, lon, lonV1, lonV2;
		LatLng pDest;
		
		dist = dist/6371; //POURQUOI ? faut que je cherche
		
		latV1 = Math.toRadians(pointDepart.latitude);
		lonV1 = Math.toRadians(pointDepart.longitude);
		
		//A partir de là mon niveau de math n'est plus assez élevé pour comprendre... mais a priori ça fonctionne
		latV2 = Math.sin(latV1)*Math.cos(dist) + Math.cos(latV1)*Math.sin(dist)*Math.cos(angleAleatoire);
		lat = Math.asin(latV2);
		
		lonV2 = Math.atan2(Math.sin(angleAleatoire) * Math.sin(dist) * Math.cos(latV1), Math.cos(dist) - Math.sin(latV1) *Math.sin(latV2));
		lon = lonV1 + lonV2;
		
		pDest = new LatLng(Math.toDegrees(lat),Math.toDegrees(lon));
		
		return pDest;
	}
	
	/**
	 * Fabrique une url pour envoyer une requête HTTP à Google Maps qui renverra les points géographiques
	 * @param src est le point source
	 * @param dest est le point de destination
	 * @return l'url dont il faut se servir pour exécuter la requête
	 */
	private String makeUrl(LatLng src, LatLng dest){
		 
		StringBuffer url = new StringBuffer();
		 
		url.append("http://maps.google.com/maps?f=d&hl=en");
		url.append("&saddr=");
		url.append(Double.toString(src.latitude));
		url.append(",");
		url.append(Double.toString(src.longitude));
		url.append("&daddr=");
		url.append(Double.toString(dest.latitude));
		url.append(",");
		url.append(Double.toString(dest.longitude));
		url.append("&ie=UTF8&0&om=0&output=dragdir&dirflg=w");
		 
		return url.toString();
	}
	
	/**
	 * Décode des points cryptés (algorithme de http://facstaff.unca.edu/mcmcclur/googlemaps/encodepolyline/)
	 * @param encoded est la chaîne de caractère contenant les points à décoder
	 * @return la liste des points décodés
	 */
	private ArrayList<LatLng> decodeEncryptedGeopoints(String encoded){

	// get only the encoded geopoints
	encoded = encoded.split("points:\"")[1].split("\",")[0];
	// replace two backslashes by one (some error from the transmission)
	encoded = encoded.replace("\\\\", "\\");
	 
	//decoding
	ArrayList<LatLng> poly = new ArrayList<LatLng>();
	        int index = 0, len = encoded.length();
	        int lat = 0, lng = 0;
	 
	        while (index < len) {
	            int b, shift = 0, result = 0;
	            do {
	                b = encoded.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lat += dlat;
	 
	            shift = 0;
	            result = 0;
	            do {
	 
	                b = encoded.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lng += dlng;
	 
	            LatLng p = new LatLng(lat/1E5,lng/1E5);
	            poly.add(p);
	            
	        }
	        return poly;
	}

}
