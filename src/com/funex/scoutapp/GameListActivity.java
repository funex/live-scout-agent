package com.funex.scoutapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.funex.scoutapp.model.Game;
import com.funex.scoutapp.model.Team;
import com.funex.scoutapp.model.Team.TEAM;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.shaded.fasterxml.jackson.databind.ObjectWriter;

public class GameListActivity extends ListActivity{
	
	MultiAdapter adapt;
	ArrayList<Game> games = new ArrayList<>();
	private Firebase myFirebaseRef;
	private Context context;
	private ListView listView;
	
	protected static final String TAG = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		context = this;

		// connect to firebase
		Firebase.setAndroidContext(this);
		myFirebaseRef = new Firebase(MainActivity.FIREBASE_URL);
		
		getGames();

		listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				  
				  // send information about the game selected
				    Intent intent = new Intent(GameListActivity.this, GameActivity.class);
				    intent.putExtra("key", games.get(position).getId());
				    intent.putExtra("firstHalfFinished", games.get(position).isFirstHalfFinished());
				    intent.putExtra("secondHalfFinished", games.get(position).isSecondHalfFinished());
				    intent.putExtra("gameRunning", games.get(position).isGameRunning());
				    startActivity(intent);
			  }
			}); 

	}
	
	// get games from firebase
	private void getGames(){

		Firebase gameRef = myFirebaseRef.child("games");
		
		gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	
				Map<String, Object> gamesFirebase = (Map<String, Object>) snapshot.getValue();				
				games = new ArrayList<>();
				
				// for each game
				for (Map.Entry<String, Object> entry : gamesFirebase.entrySet()){
					Game game = new Game();
					game.setId(entry.getKey());
					Log.w(TAG, (String) entry.getKey());

					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					String json;
					
					try {
						// extract game
						json = ow.writeValueAsString(entry.getValue());
						HashMap<String,Object> result = new ObjectMapper().readValue(json, HashMap.class);
						
						SimpleDateFormat formatter = new SimpleDateFormat(GameActivity.DATE_FORMAT);
				    	if (result.get("scheduledDate") != null)
				    		game.setScheduledDate(formatter.parse(result.get("scheduledDate").toString()));
				    	else {
				    		game.setScheduledDate(new Date());
				    	}
				    	
				    	if (result.get("gameRunning") != null){
				    		if ((boolean) result.get("gameRunning") == true){
				    			game.setGameRunning(true);
				    		}
				    		else {
				    			game.setGameRunning(false);
				    		}
				    	}
				    	
				    	if (result.get("firstHalfFinished") != null){
				    		if ((boolean) result.get("firstHalfFinished") == true){
				    			game.setFirstHalfFinished(true);
				    		}
				    		else {
				    			game.setFirstHalfFinished(false);
				    		}
				    	}
						
				    	if (result.get("secondHalfFinished") != null){
				    		if ((boolean) result.get("secondHalfFinished") == true){
				    			game.setSecondHalfFinished(true);
				    		}
				    		else {
				    			game.setSecondHalfFinished(false);
				    		}
				    	}
				    	
				    	
						// extract teams
						json = ow.writeValueAsString(result.get("teams"));
						result = new ObjectMapper().readValue(json, HashMap.class);
												
				    	Log.w(TAG, result.get("HOME").toString());
						JSONObject obj = new JSONObject(ow.writeValueAsString(result.get("HOME")));
				    	Team team = new Team();
				    	team.setTeam(TEAM.HOME);
				    	team.setName(obj.getString("name"));
				    	game.setHome(team);
				    	
				    	Log.w(TAG, result.get("AWAY").toString());
						obj = new JSONObject(ow.writeValueAsString(result.get("AWAY")));
				    	team = new Team();
				    	team.setTeam(TEAM.AWAY);
				    	team.setName(obj.getString("name"));
				    	game.setAway(team);
				    		
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					games.add(game);
				}
				
				adapt = new MultiAdapter(context);
				setListAdapter(adapt);

		    }
		    @Override
		    public void onCancelled(FirebaseError firebaseError) {
		    }
		});
	}
	
	
	class MultiAdapter extends ArrayAdapter<Game> {
		public MultiAdapter(Context context) {
			super(context, R.layout.listview_item, games);
		}

		@Override
		// Called when updating the ListView
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) { // Create new row view object
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.listview_item, parent, false);
			} else
				// reuse old row view to save time/battery
				row = convertView;

			TextView reusableTextView = (TextView) row
					.findViewById(R.id.listText);
			reusableTextView.setText(games.get(position).getHome().getName() + " vs " + games.get(position).getAway().getName());
			reusableTextView = (TextView) row.findViewById(R.id.listText2);
			
			// extra information about the game
			if (games.get(position).isSecondHalfFinished())
				reusableTextView.setText(games.get(position).getScheduledDate().toGMTString() + " - Game Finished");
			else if  (games.get(position).isFirstHalfFinished())
				reusableTextView.setText(games.get(position).getScheduledDate().toGMTString() + " - In progress");
			else 
				reusableTextView.setText(games.get(position).getScheduledDate().toGMTString() + " - Not started");
			
			return row;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// if we leave the screen, rebind the list when we get back
		getGames();
		
	}

	@Override
	protected void onPause() {
		super.onPause();

	}
}
