package com.makeursport;

import com.makeursport.preferences.Settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * Fragment qui nous permet d'afficher notre menu
 * @author Mickael
 *
 */
public class MenuFragment extends ListFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu, null);
	}
	/**
	 * Une fois que l'activité est crée, on paramètre notre adapter
	 * c'est a dire celui qui fournit les infos à notre listView.
	 * 
	 * On met dedans les menu que l'on veut voir
	 * et on l'associe à la liste
	 */
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MyListAdapter adapter = new MyListAdapter(getActivity());
		adapter.add(new ListItem("Course libre", R.drawable.ic_action_course_libre));
		adapter.add(new ListItem("Générer un parcours", R.drawable.ic_action_generer_parcours));
		adapter.add(new ListItem("Consulter l'historique", R.drawable.ic_action_historique));
		adapter.add(new ListItem("Paramètres", R.drawable.ic_action_preferences));
		
		setListAdapter(adapter);
	}
	/**
	 * Lors d'un click sur un élément de notre liste
	 */
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
		case 0://Course en Cours
			if(!(this.getFragmentManager().findFragmentById(R.id.main_layout) instanceof CourseEnCours)) {
		        newContent = new CourseEnCours();
			}
			break;
		case 1:
			newContent = new TestFragment();
			//TODO:newContent = new GenererParcours();
			break;
		case 2:
			//TODO:newContent = new HistoriqueConsultation();
			break;
		case 3:
			Intent prefActivity = new Intent(this.getActivity(), Settings.class);
			startActivity(prefActivity);
			//newContent = new PreferenceFragment();
			//TODO:newContent = new Parametre();
			break;
		}
		if (newContent != null){
			this.switchFragment(newContent);
		}
	}
	/**
	 * Changement de du contenu de notre fenêtre principale
	 * @param f le fragment à afficher
	 */
	void switchFragment(Fragment f) {
		if (getActivity() == null) {
			return;
		}
		((MainActivity) getActivity()).switchContent(f);
	}

	
	
	/**
	 * Classe qui contient un item d'une liste
	 * Il permet de définir avec quel éléments travail notre adapter
	 */
	private class ListItem {
		public String tag;
		public int iconRes;
		/**
		 * Définition d'un item de notre liste
		 * @param tag la chaine a afficher
		 * @param iconRes l'identifiant du drawable a afficher
		 */
		public ListItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}
	/**
	 * Adapter qui fait le lien entre notre ListView et nos infos
	 *
	 */
	public class MyListAdapter extends ArrayAdapter<ListItem> {

		public MyListAdapter(Context context) {
			super(context, 0);
		}
		/**
		 * Définission de la façon dont on veut afficher nos infos
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
}
