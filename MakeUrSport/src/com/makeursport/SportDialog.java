package com.makeursport;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.makeursport.gestionCourse.Sport;

/**
 * Dialog permettant de choisir un sport
 * @author L'équipe MakeUrSport
 */
public class SportDialog extends SherlockListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setContentView(android.R.layout.simple_list_item_1);
		this.setListAdapter(new MySimpleAdapter());
	}

	/**
	 * Classe de SimpleAdapter pour pouvoir gerer la liste des Sports
	 */
	private class MySimpleAdapter extends BaseAdapter {
		ArrayList<Sport> sports;
		public MySimpleAdapter() {
			sports = new ArrayList<Sport>();
			sports.add(Sport.COURSE);
			sports.add(Sport.ROLLER);
			sports.add(Sport.VELO);
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return sports.size();
		}
	
		public Sport getItem(int position) {
			// TODO Auto-generated method stub
			return sports.get(position);
		}
	
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return getItem(position).getSportInt();
		}
	
		public View getView(int position, View convertView, ViewGroup parent) {
			Sport s = getItem(position);
			//TextView texte = new TextView(getApplicationContext());*/
			TextView texte = (TextView) getLayoutInflater().inflate(R.layout.list_textview_item, parent, false);
			switch(s) {
			case COURSE:
				texte.setText(getApplicationContext().getString(R.string.sport_course));
				texte.setCompoundDrawablesWithIntrinsicBounds(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_course), null, null, null);
				break;
			case ROLLER:
				texte.setText(getApplicationContext().getString(R.string.sport_roller));
				texte.setCompoundDrawablesWithIntrinsicBounds(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_roller), null, null, null);
				break;
			case VELO:
				texte.setText(getApplicationContext().getString(R.string.sport_velo));
				texte.setCompoundDrawablesWithIntrinsicBounds(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_velo), null, null, null);
				break;
			}
			texte.setCompoundDrawablePadding(10);
			//texte.setPadding(10, 10, 10, 10);
			texte.setId(getItem(position).getSportInt());
			texte.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra(CourseEnCours.SPORT_INTENT, v.getId());
					setResult(RESULT_OK,i);
					finish();
				}
			});
			return texte;
		}
	}
}
