package com.funex.scoutapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.funex.scoutapp.model.Event;
import com.funex.scoutapp.model.Game;
import com.funex.scoutapp.model.Team;
import com.funex.scoutapp.model.Event.ACTION;
import com.funex.scoutapp.model.Team.TEAM;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GameActivity extends Activity {

	private Button btnSafe;
	private Button btnAttack;
	private Button btnGoal;
	private Button btnCorner;
	private Button btnShot;
	private Button btnFreekick;
	private Button btnYellowCard;
	private Button btnRedCard;
	private Button btnOffside;
	private ToggleButton toggleHome;
	private ToggleButton toggleAway;
	private TextView txtHome;
	private TextView txtAway;
	private TextView txtHomeScore;
	private TextView txtAwayScore;
	private Menu menu;
	private Firebase myFirebaseRef;
	private Context context;
	private Game game;

	private NotificationManager notifManager;

	// A list of elements to be written to Firebase on the next push
	private HashMap<String, Event> eventQueue = new HashMap<String, Event>();

	// How often (in ms) we push write updates to Firebase
	private static final int UPDATE_THROTTLE_DELAY = 40;
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss a";
	public static final int NOTIFICATION_ID = 1543; // Unique ID
	protected static final String TAG = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.game);

		Firebase.setAndroidContext(this);
		myFirebaseRef = new Firebase(MainActivity.FIREBASE_URL);
		setFireBase();
		findControls();
		setupGame();

	}

	private void setupGame() {

		// get game id
		Intent intent = getIntent();
		String gameId = intent.getStringExtra("key");
		boolean firstHalfFinished = intent.getBooleanExtra("firstHalfFinished",
				false);
		boolean secondHalfFinished = intent.getBooleanExtra(
				"secondHalfFinished", false);
		boolean gameRunning = intent.getBooleanExtra("gameRunning", false);

		if (gameId == null) {
			// quit if any error
			finish();
		}

		game = new Game();
		game.setId(gameId);
		game.setFirstHalfFinished(firstHalfFinished);
		game.setSecondHalfFinished(secondHalfFinished);
		game.setGameRunning(gameRunning);

		// if game is running turn controls on
		toggleControls(gameRunning);

		Firebase gameRef = myFirebaseRef.child("games/" + game.getId());

		gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {

				SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
				Map<String, Object> gameDetails = (Map<String, Object>) snapshot
						.getValue();

				try {
					game.setScheduledDate(formatter.parse((String) gameDetails
							.get("scheduledDate")));
					Log.w(TAG, game.getScheduledDate().toGMTString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
			}
		});

		// id passed with intent
		gameRef = myFirebaseRef.child("games/" + game.getId() + "/teams/HOME");

		gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Map<String, Object> team = (Map<String, Object>) snapshot
						.getValue();
				Team home = new Team();
				home.setTeam(TEAM.HOME);
				home.setName((String) team.get("name"));
				Integer i = (int) (long) team.get("goals");
				home.setGoals(i);
				i = (int) (long) team.get("yellowCards");
				home.setYellowCards(i);
				i = (int) (long) team.get("redCards");
				home.setRedCards(i);

				game.setHome(home);

				txtHome.setText(home.getName());
				txtHomeScore.setText(String.valueOf(home.getGoals()));

			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
			}
		});

		// id passed with intent
		gameRef = myFirebaseRef.child("games/" + game.getId() + "/teams/AWAY");

		gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Map<String, Object> team = (Map<String, Object>) snapshot
						.getValue();
				Team away = new Team();
				away.setTeam(TEAM.AWAY);
				away.setName((String) team.get("name"));
				Integer i = (int) (long) team.get("goals");
				away.setGoals(i);
				i = (int) (long) team.get("yellowCards");
				away.setYellowCards(i);
				i = (int) (long) team.get("redCards");
				away.setRedCards(i);

				game.setAway(away);

				txtAway.setText(away.getName());
				txtAwayScore.setText(String.valueOf(away.getGoals()));

			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
			}
		});

	}

	// find all controls and add the respective click listeners
	private void findControls() {
		btnSafe = (Button) findViewById(R.id.btnSafe);
		btnSafe.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepareAndSendEvent(ACTION.SAFE);
			}
		});

		btnAttack = (Button) findViewById(R.id.btnAttack);
		btnAttack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepareAndSendEvent(ACTION.ATTACK);
			}
		});
		btnCorner = (Button) findViewById(R.id.btnCorner);
		btnCorner.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepareAndSendEvent(ACTION.CORNER);
			}
		});
		btnFreekick = (Button) findViewById(R.id.btnFreekick);
		btnFreekick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepareAndSendEvent(ACTION.FREEKICK);
			}
		});
		btnShot = (Button) findViewById(R.id.btnShot);
		btnShot.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepareAndSendEvent(ACTION.SHOTONGOAL);
			}
		});
		btnGoal = (Button) findViewById(R.id.btnGoal);
		btnGoal.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Team team;
				if (toggleHome.isChecked()) {
					team = game.getHome();
				} else
					team = game.getAway();

				// increment goal on the local model
				team.setGoals(team.getGoals() + 1);

				// send action and send to server
				prepareAndSendEvent(ACTION.GOAL);

				// update score instead of creating new record
				updateMatchTeam(team.getTeam(), "goals", team.getGoals());

				// update gui
				if (toggleHome.isChecked())
					txtHomeScore.setText(String.valueOf(team.getGoals()));
				else
					txtAwayScore.setText(String.valueOf(team.getGoals()));

			}
		});

		btnYellowCard = (Button) findViewById(R.id.btnYellow);
		btnYellowCard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Team team;
				if (toggleHome.isChecked()) {
					team = game.getHome();
				} else
					team = game.getAway();

				// increment card on the local model
				team.setYellowCards(team.getYellowCards() + 1);

				// send action and send to server
				prepareAndSendEvent(ACTION.YELLOWCARD);

				// update score instead of creating new record
				updateMatchTeam(team.getTeam(), "yellowCards",
						team.getYellowCards());

			}
		});

		btnRedCard = (Button) findViewById(R.id.btnRed);
		btnRedCard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Team team;
				if (toggleHome.isChecked()) {
					team = game.getHome();
				} else
					team = game.getAway();

				// increment card on the local model
				team.setRedCards(team.getRedCards() + 1);

				// send action and send to server
				prepareAndSendEvent(ACTION.REDCARD);

				// update score instead of creating new record
				updateMatchTeam(team.getTeam(), "redCards", team.getRedCards());
			}
		});

		btnOffside = (Button) findViewById(R.id.btnOffside);
		btnOffside.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepareAndSendEvent(ACTION.OFFSIDE);
			}
		});

		toggleHome = (ToggleButton) findViewById(R.id.toggleHome);
		toggleHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleAway.setChecked(!toggleHome.isChecked());
			}
		});
		toggleAway = (ToggleButton) findViewById(R.id.toggleAway);
		toggleAway.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleHome.setChecked(!toggleAway.isChecked());
			}
		});
		txtHome = (TextView) findViewById(R.id.txtHome);
		txtAway = (TextView) findViewById(R.id.txtAway);
		txtAwayScore = (TextView) findViewById(R.id.txtAwaySscore);
		txtHomeScore = (TextView) findViewById(R.id.txtHomeSscore);

	}

	// send a team command
	private void prepareAndSendEvent(ACTION action) {
		Event event = new Event();
		event.setKey("events");
		if (toggleAway.isChecked()) {
			event.setTeam(TEAM.AWAY);
		} else {
			event.setTeam(TEAM.HOME);
		}
		event.setAction(action);
		eventQueue.put(event.getKey(), event);
	}

	// send event to firebase
	private void sendEvent(String key, Event command) {
		if (null == key || null == command)
			throw new IllegalArgumentException();

		// re-apply the cached key, just in case
		command.setKey(key);

		myFirebaseRef.child(key).child(game.getId()).push()
				.setValue(command, new Firebase.CompletionListener() {
					@Override
					public void onComplete(FirebaseError firebaseError,
							Firebase firebase) {
						if (firebaseError != null) {
							Log.w(TAG,
									"Update failed! "
											+ firebaseError.getMessage());
						} else {
							Log.w(TAG, "Update successfull! ");
						}
					}
				});
	}

	// queue for events
	private void setFireBase() {
		ScheduledExecutorService firebaseUpdateScheduler = Executors
				.newScheduledThreadPool(1);
		firebaseUpdateScheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (eventQueue != null && eventQueue.size() > 0) {
					for (Event event : eventQueue.values()) {
						sendEvent(event.getKey(), event);
						System.out.println(event.getKey());
						eventQueue.remove(event.getKey());
					}
				}
			}
		}, UPDATE_THROTTLE_DELAY, UPDATE_THROTTLE_DELAY, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		this.menu = menu;

		// get the correct action menu
		toggleMenu();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		// start first half and show button to end
		if (id == R.id.game_start_first_half) {
			prepareAndSendEvent(ACTION.STARTFIRSTHALF);
			item.setVisible(false);
			toggleControls(true);
			menu.findItem(R.id.game_end_first_half).setVisible(true);

			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

			updateMatch("startDate", formatter.format(new Date()));
			updateMatch("gameRunning", true);

			return true;
		}
		// end first half and show button to start second half
		if (id == R.id.game_end_first_half) {
			prepareAndSendEvent(ACTION.ENDFIRSTHALF);
			item.setVisible(false);
			toggleControls(false);
			menu.findItem(R.id.game_start_second_half).setVisible(true);

			// update match status
			updateMatch("firstHalfFinished", true);
			updateMatch("gameRunning", false);

			return true;
		}
		// start second half and show button to end
		if (id == R.id.game_start_second_half) {
			prepareAndSendEvent(ACTION.STARTSECONDHALF);
			item.setVisible(false);
			toggleControls(true);
			menu.findItem(R.id.game_end_second_half).setVisible(true);
			updateMatch("gameRunning", true);
			return true;
		}
		// end second half and show button to exit
		if (id == R.id.game_end_second_half) {
			prepareAndSendEvent(ACTION.ENDSECONDHALF);
			item.setVisible(false);
			toggleControls(false);
			menu.findItem(R.id.game_end).setVisible(true);

			updateMatch("secondHalfFinished", true);
			updateMatch("gameRunning", false);

			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

			updateMatch("endDate", formatter.format(new Date()));

			return true;
		}
		if (id == R.id.game_end) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateMatch(String key, Object value) {
		// update match status
		Firebase postRef = myFirebaseRef.child("games/" + game.getId());
		Map<String, Object> gameFirebase = new HashMap<String, Object>();
		gameFirebase.put(key, value);
		postRef.updateChildren(gameFirebase);
	}

	private void updateMatchTeam(TEAM team, String key, Object value) {
		// update match status
		Firebase postRef = myFirebaseRef.child("games/" + game.getId()
				+ "/teams/" + team);
		Map<String, Object> gameFirebase = new HashMap<String, Object>();
		gameFirebase.put(key, value);
		postRef.updateChildren(gameFirebase);
	}

	// enable/disable controls
	private void toggleControls(boolean bool) {
		btnSafe.setEnabled(bool);
		btnAttack.setEnabled(bool);
		btnGoal.setEnabled(bool);
		btnCorner.setEnabled(bool);
		btnShot.setEnabled(bool);
		btnFreekick.setEnabled(bool);
		btnYellowCard.setEnabled(bool);
		btnRedCard.setEnabled(bool);
		btnOffside.setEnabled(bool);
		toggleHome.setEnabled(bool);
		toggleAway.setEnabled(bool);

	}

	// spaghetti code but does the work
	private void toggleMenu() {
		// Show the correct action menus depending on the current game status
		if (game.isSecondHalfFinished()) {
			Log.w(TAG, "2 finished");
			menu.findItem(R.id.game_start_first_half).setVisible(false);
			menu.findItem(R.id.game_end_first_half).setVisible(false);
			menu.findItem(R.id.game_start_second_half).setVisible(false);
			menu.findItem(R.id.game_end_second_half).setVisible(false);
			menu.findItem(R.id.game_end).setVisible(true);
		} else if (game.isFirstHalfFinished()) {
			Log.w(TAG, "1 finished");
			menu.findItem(R.id.game_start_first_half).setVisible(false);
			menu.findItem(R.id.game_end_first_half).setVisible(false);
			if (game.isGameRunning()) {
				Log.w(TAG, "running");
				menu.findItem(R.id.game_start_second_half).setVisible(false);
				menu.findItem(R.id.game_end_second_half).setVisible(true);
			} else {
				menu.findItem(R.id.game_start_second_half).setVisible(true);
				menu.findItem(R.id.game_end_second_half).setVisible(false);
			}
			menu.findItem(R.id.game_end).setVisible(false);
		} else {
			if (game.isGameRunning()) {
				Log.w(TAG, "1 running");
				menu.findItem(R.id.game_start_first_half).setVisible(false);
				menu.findItem(R.id.game_end_first_half).setVisible(true);
			} else {
				Log.w(TAG, "not started");
				menu.findItem(R.id.game_start_first_half).setVisible(true);
				menu.findItem(R.id.game_end_first_half).setVisible(false);
			}
			menu.findItem(R.id.game_start_second_half).setVisible(false);
			menu.findItem(R.id.game_end_second_half).setVisible(false);
			menu.findItem(R.id.game_end).setVisible(false);
		}
	}

	// if we press the back button popup dialog
	@Override
	public void onBackPressed() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Are you sure?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
	}

	// create notification if we leave the activity and the game is running
	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first

		Notification.Builder mBuilder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.soccer_icon)
				.setContentTitle("Livescore Agent")
				.setContentText("Game is still running").setAutoCancel(true);

		Intent resultIntent = new Intent(this, GameActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		String ns = Context.NOTIFICATION_SERVICE;
		notifManager = (NotificationManager) getSystemService(ns);
		notifManager.notify(NOTIFICATION_ID, notification);

	}

	@Override
	public void onResume() {
		super.onResume();
		// cancel notification
		if (notifManager != null) {
			notifManager.cancel(NOTIFICATION_ID);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// cancel notification
		if (notifManager != null) {
			notifManager.cancel(NOTIFICATION_ID);
		}
	}
}
