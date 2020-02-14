package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * Activity que permite al usuario realizar un cambio en la contrase�a que esta 
 * usando actualmente.
 * Realiza validaci�n entre la vieja y la nueva mayormente
 * @author GuillermoR
 *
 */
public class CambioContrasena extends Activity {

	/**
	 * Variable de contexto que contiene datos de la activity
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Boton para cancelar el cambio de contrase�a
	 */
	private Button boton_cancelar;
	/**
	 * Boton para validar la nueva contrase�a
	 */
	private Button boton_validar;
	/**
	 * EdtiText donde se lee el login
	 */
	private EditText edit_new_login;
	/**
	 * EditText donde se lee el pass anterior
	 */
	private EditText edit_new_pass_1;
	/**
	 * EditText de donde se lee el nuevo pass
	 */
	private EditText  edit_new_pass_2;
	
	/**
	 * Inicializa la UI  y carga los handlers
	 * <p>1� Obtenemos y cargamos datos de la UI
	 * <p>2� Cargamos los handlers
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//1� Obtenemos y cargamos datos de la UI
		setContentView(R.layout.xml_cambiocontrasena);
		
		boton_cancelar = (Button) findViewById(R.id.CAMBIO_boton_cancelar);
		boton_validar = (Button) findViewById(R.id.CAMBIO_boton_validar);
		
		edit_new_login = (EditText) findViewById(R.id.CAMBIO_LOGIN);
		edit_new_pass_1 = (EditText) findViewById(R.id.CAMBIO_pass1);
		edit_new_pass_2 = (EditText) findViewById(R.id.CAMBIO_pass2);
		
		//2� Cargamos los handlers
		boton_cancelar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		boton_validar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				String login = String.valueOf(edit_new_login.getText()).trim().toUpperCase();
				String pass1 = String.valueOf(edit_new_pass_1.getText()).trim().toUpperCase();
				String pass2 = String.valueOf(edit_new_pass_2.getText()).trim().toUpperCase();
				
				if ( pass1.compareTo(pass2) != 0 ) {
					Toast.makeText(ctxt, "Error: las contrase�as ingresadas son diferentes", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ctxt, "Login y contrase�a modificados con �xito", Toast.LENGTH_LONG).show();
					
					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctxt);
					SharedPreferences.Editor editor = settings.edit();
 	       		    editor.putString("admin_login", login);
 	       		    editor.putString("admin_password", pass1);
 	       		    editor.commit();
 	       		    
 	       		    Parametros.ADMIN_LOGIN = login;
 	       		    Parametros.ADMIN_PASSWORD = pass1;
				}
				finish();
			}
		});
		
		
	}

	
	
	
	
}
