package com.funex.scoutapp;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Firebase myFirebaseRef;
	private Context context;
	private FrameLayout spinner;
	private EditText editUser;
	private EditText editPassword;
	private Button btnLogin;
	private Button btnCancel;
	
	// Insert your firebase URL here
	public static final String FIREBASE_URL = "INSER_URL_HERE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);
		myFirebaseRef = new Firebase(FIREBASE_URL);
		
		context = this;
		
		editUser = (EditText) findViewById(R.id.editUser);
		editPassword = (EditText) findViewById(R.id.editPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(btnSubmitListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(btnCancelListener);
		spinner = (FrameLayout)findViewById(R.id.spinnerLayout);
		
		toggleSpinner(false);
		
	}
	

	private OnClickListener btnSubmitListener = new OnClickListener() {
		public void onClick(View v) {
					
			toggleSpinner(true);
			
			String user = ((EditText) findViewById(R.id.editUser)).getText().toString();
			String pass = ((EditText) findViewById(R.id.editPassword)).getText().toString();
						
			myFirebaseRef.authWithPassword(user, pass,
				    new Firebase.AuthResultHandler() {
				    @Override
				    public void onAuthenticated(AuthData authData) {
				        // Authentication just completed successfully :)
				    				    	
					    Toast.makeText(context, "Success, Loading Data...", Toast.LENGTH_SHORT).show();
					    Intent intent = new Intent(MainActivity.this, GameListActivity.class);
					    startActivity(intent);
					    finish();
				    }
				    @Override
				    public void onAuthenticationError(FirebaseError error) {
				        // Something went wrong :(
					    Toast.makeText(context, "Oops..Something went wrong.", Toast.LENGTH_SHORT).show();
					    toggleSpinner(false);
				    }
				});
		}

	};

	private OnClickListener btnCancelListener = new OnClickListener() {
		public void onClick(View v) {
			((EditText) findViewById(R.id.editUser)).setText("");
			((EditText) findViewById(R.id.editPassword)).setText("");			
		}

	};
	// toggle the loading screen
	private void toggleSpinner(boolean bool){
		editUser.setEnabled(!bool);
		editPassword.setEnabled(!bool);
		btnLogin.setEnabled(!bool);
		btnCancel.setEnabled(!bool);
        if (!bool) spinner.setVisibility((View.GONE));
        else spinner.setVisibility((View.VISIBLE));
	}
}
