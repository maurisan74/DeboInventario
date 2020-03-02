package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Activity que muestra una UI para administracion de preferencias b�sicas de
 * algunas aplicaciones.
 * 
 * @author GuillermoR
 * 
 */
public class Preferencias extends Activity {
	/**
	 * Variable para almacenar informacion del contexto de desarrollo de la
	 * activity
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Variable para almacenar la URL del WebService
	 */
	@Nullable
    private String url_webservice;
	/**
	 * Variable para guardar el SSID del WIFI predeterminado
	 */
	@Nullable
    private String nombre_wifi_priveligiado;
	/**
	 * Variable para almacenar el ID del tablet que esta trabajando
	 */
	@Nullable
    private String id_tablet;
	/**
	 * Almacena el usuario que hara login
	 */
	@Nullable
    private String admin_login;
	/**
	 * Variable que almacena el password del usuario u operador
	 */
	@Nullable
    private String admin_password;
	/**
	 * Almacena el valor de logDatos
	 */
	@Nullable
    private String logDatos;
	/**
	 * Almacena el valor de logTablet
	 */
	@Nullable
    private String logTablet;
	@Nullable
    private String maximoFotos;
	/**
	 * Almacena el valor de log eventos
	 */
	private boolean logEventos;
	/**
	 * Almacena el valor de log procesos
	 */
	private boolean logProcesos;
	/**
	 * Almacena el valor de log mensajes
	 */
	private boolean logMensajes;
	/**
	 * Almacena el valor de log excepciones
	 */
	private boolean logExcepciones;
	private boolean habImpresion;

	/**
	 * contador para pass
	 */
	private int contadorPass = 0;
	/**
	 * contador para login
	 */
	private int contadorLogin = 0;

	/**
	 * EditTexts
	 */
	/**
	 * EditText para leer la URL del webservice
	 */
	private EditText textbox_webservice;
	/**
	 * EditText para leer de el la SSID del WIFI predeterminado
	 */
	private EditText textbox_wifi;
	/**
	 * EditText para leer el ID de la tablet
	 */
	private EditText textbox_idtablet;
	/**
	 * EdtiText para leer los datos de preferencias del login
	 */
	private EditText edit_admin_log;
	/**
	 * EditText para leer los datos de preferencia s aguardar del Password
	 */
	private EditText edit_admin_pas;
	/**
	 * EditText para la carpeta de log Tablet
	 */
	private EditText edit_log_Tablet;
	/**
	 * EditText para la carpeta de log Datos
	 */
	private EditText edit_log_Datos;
	private EditText edit_can_fotos;
	/**
	 * Checkbox para el log de eventos
	 */
	private CheckBox checkbox_eventos;
	/**
	 * Checkbox para el log de procesos
	 */
	private CheckBox checkbox_procesos;
	/**
	 * Checkbox para el log de mensajes
	 */
	private CheckBox checkbox_mensajes;
	/**
	 * Checkbox para el log de excepciones
	 */
	private CheckBox checkbox_excepciones;
	private CheckBox checkbox_impresion;

	/**
	 * Botones
	 * 
	 */
	/**
	 * Boton para guardar los datos
	 */
	private Button botonGuardar;
	/**
	 * Boton para cancelar los datos guardados
	 */
	private Button botonCancelar;
	/**
		 * 
		 */
	private Button boton_logpas;
	/**
	 * Variable para obtener las preferencias del sistema
	 */
	private SharedPreferences settings;

	/**
	 * Constructor de la clase, inicializa las UI, preferencias, handlers
	 * <p>
	 * 1� Restaurar las preferencias
	 * <p>
	 * 2� Creamos los textBox correspondientes
	 * <p>
	 * 3� Configuramos los botones
	 * <p>
	 * 4� Configurar los HANDLERS
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_preferencias);

		// 1� Restaurar las preferencias:
		settings = PreferenceManager.getDefaultSharedPreferences(ctxt);
		url_webservice = settings.getString(Parametros.preferencia_servidor, Parametros.PREF_URL_CONEXION_SERVIDOR);
		nombre_wifi_priveligiado = settings.getString(Parametros.preferencia_wifi, Parametros.PREF_WIFI_PRIVILEGIADO);
		id_tablet = settings.getString(Parametros.preferencia_idtablet, Parametros.PREF_NUMERO_DE_TERMINAL);
		admin_login = settings.getString(Parametros.preferencia_admin_log, Parametros.ADMIN_LOGIN);
		admin_password = settings.getString(Parametros.preferencia_admin_pwd, Parametros.ADMIN_PASSWORD);
		maximoFotos = settings.getString(Parametros.preferencia_max_cant_fotos, Parametros.PREF_CANT_FOTOS);
		logTablet = settings.getString(Parametros.preferencias_logTablet, Parametros.carpeta_sancion_log_tablet);
		logDatos = settings.getString(Parametros.preferencias_logDatos, Parametros.carpeta_sancion_log_Datos);
		logEventos = settings.getBoolean(Parametros.preferencias_logEvento, Parametros.PREF_LOG_EVENTOS);
		logProcesos = settings.getBoolean(Parametros.preferencias_logProcesos, Parametros.PREF_LOG_PROCESOS);
		logMensajes = settings.getBoolean(Parametros.preferencias_logMensajes, Parametros.PREF_LOG_MENSAJES);
		logExcepciones = settings.getBoolean(Parametros.preferencias_logExcepciones, Parametros.PREF_LOG_EXCEPCIONES);

		habImpresion = settings.getBoolean(Parametros.preferencias_habilitar_impresion, Parametros.PREF_HAB_IMPRESION);

		// 2� Creamos los textBox y Checkbox correspondientes:
		textbox_webservice = (EditText) findViewById(R.id.PREF_URL_webservice);
		textbox_wifi = (EditText) findViewById(R.id.PREF_wifi_preferido);
		textbox_idtablet = (EditText) findViewById(R.id.PREF_id_tablet);

		textbox_webservice.setText(url_webservice);
		textbox_wifi.setText(nombre_wifi_priveligiado);
		textbox_idtablet.setText(id_tablet);

		edit_admin_log = (EditText) findViewById(R.id.PREF_admin_log);
		edit_admin_pas = (EditText) findViewById(R.id.PREF_admin_pass);
		edit_log_Tablet = (EditText) findViewById(R.id.IDlogTablet);
		edit_log_Datos = (EditText) findViewById(R.id.IDlogDatos);
		edit_can_fotos = (EditText) findViewById(R.id.maxima_cantidad_fotos_x_sancion);

		edit_log_Tablet.setText(logTablet);
		edit_log_Datos.setText(logDatos);
		edit_can_fotos.setText(maximoFotos);

		checkbox_eventos = (CheckBox) findViewById(R.id.checkBoxEventos);
		checkbox_procesos = (CheckBox) findViewById(R.id.checkBoxProcesos);
		checkbox_mensajes = (CheckBox) findViewById(R.id.checkBoxMensajes);
		checkbox_excepciones = (CheckBox) findViewById(R.id.checkBoxExcepciones);
		checkbox_impresion = (CheckBox) findViewById(R.id.checkBoxHamImpresion);

		checkbox_eventos.setChecked(logEventos);
		checkbox_procesos.setChecked(logProcesos);
		checkbox_mensajes.setChecked(logMensajes);
		checkbox_excepciones.setChecked(logExcepciones);
		checkbox_impresion.setChecked(habImpresion);

		// 3� Configuramos los botones:
		edit_admin_log.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (contadorLogin == 0) {
					edit_admin_log.setText("");
					contadorLogin++;
					return true;
				} else {
					return false;
				}
			}
		});

		edit_admin_pas.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (contadorPass == 0) {
					edit_admin_pas.setText("");
					contadorPass++;

					return true;
				} else {
					return false;
				}
			}
		});

		botonCancelar = (Button) findViewById(R.id.PREF_boton_cancelar);
		botonGuardar = (Button) findViewById(R.id.PREF_boton_guardar);

		boton_logpas = (Button) findViewById(R.id.PREF_admin_boton);

		// 4� Configurar los HANDLERS:
		botonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(Parametros.preferencia_servidor, String
						.valueOf(textbox_webservice.getText()).trim());
				editor.putString(Parametros.preferencia_wifi,
						String.valueOf(textbox_wifi.getText()).trim());
				editor.putString(Parametros.preferencia_idtablet, String
						.valueOf(textbox_idtablet.getText()).trim());
				editor.putString(Parametros.preferencias_logTablet, String
						.valueOf(edit_log_Tablet.getText()).trim());
				editor.putString(Parametros.preferencias_logDatos, String
						.valueOf(edit_log_Datos.getText()).trim());
				editor.putString(Parametros.preferencia_max_cant_fotos, String
						.valueOf(edit_can_fotos.getText()).trim());
				editor.putBoolean(Parametros.preferencias_logEvento,
						Boolean.valueOf(checkbox_eventos.isChecked()));
				editor.putBoolean(Parametros.preferencias_logProcesos,
						Boolean.valueOf(checkbox_procesos.isChecked()));
				editor.putBoolean(Parametros.preferencias_logMensajes,
						Boolean.valueOf(checkbox_mensajes.isChecked()));
				editor.putBoolean(Parametros.preferencias_logExcepciones,
						Boolean.valueOf(checkbox_excepciones.isChecked()));
				editor.putBoolean(Parametros.preferencias_logExcepciones,
						Boolean.valueOf(checkbox_excepciones.isChecked()));
				editor.putBoolean(Parametros.preferencias_habilitar_impresion,
						Boolean.valueOf(checkbox_impresion.isChecked()));
				editor.commit();

				Parametros.PREF_URL_CONEXION_SERVIDOR = String.valueOf(
						textbox_webservice.getText()).trim();
				Parametros.PREF_WIFI_PRIVILEGIADO = String.valueOf(
						textbox_wifi.getText()).trim();
				Parametros.PREF_NUMERO_DE_TERMINAL = String.valueOf(
						textbox_idtablet.getText()).trim();
				Parametros.carpeta_sancion_log_tablet = String.valueOf(
						edit_log_Tablet.getText()).trim();
				Parametros.carpeta_sancion_log_Datos = String.valueOf(
						edit_log_Datos.getText()).trim();
				Parametros.PREF_CANT_FOTOS = String.valueOf(
						edit_can_fotos.getText()).trim();
				Parametros.PREF_LOG_EVENTOS = Boolean.valueOf(checkbox_eventos
						.isChecked());
				Parametros.PREF_LOG_PROCESOS = Boolean
						.valueOf(checkbox_procesos.isChecked());
				Parametros.PREF_LOG_MENSAJES = Boolean
						.valueOf(checkbox_mensajes.isChecked());
				Parametros.PREF_LOG_EXCEPCIONES = Boolean
						.valueOf(checkbox_excepciones.isChecked());
				Parametros.PREF_HAB_IMPRESION = Boolean
						.valueOf(checkbox_impresion.isChecked());

				setResult(RESULT_OK);
				finish();
			}
		});

		boton_logpas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Datos leidos:
				String loginLeido = String.valueOf(edit_admin_log.getText())
						.trim().toUpperCase();
				String contrasenaLeida = String
						.valueOf(edit_admin_pas.getText()).trim().toUpperCase();

				if (loginLeido.compareTo(Parametros.ADMIN_LOGIN.toUpperCase()) == 0
						&& contrasenaLeida.compareTo(Parametros.ADMIN_PASSWORD
								.toUpperCase()) == 0) {

					Intent intentLogPas = new Intent(Preferencias.this,
							CambioContrasena.class);
					startActivity(intentLogPas);

				} else {
					edit_admin_log.setText("");
					edit_admin_pas.setText("");
					edit_admin_log.requestFocus();
					Toast.makeText(ctxt, "�Identificaci�n incorrecta!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	/*
	 * Carga las preferencias almacenadas en el sistema en los valores de las
	 * variables de parametros
	 *
	 * @param ctxtx
	 */
	public static void cargarPreferencias(Context ctxtx) {
		SharedPreferences setting = PreferenceManager
				.getDefaultSharedPreferences(ctxtx);
		Parametros.PREF_URL_CONEXION_SERVIDOR = setting.getString(
				Parametros.preferencia_servidor,
				Parametros.PREF_URL_CONEXION_SERVIDOR);
		Parametros.PREF_WIFI_PRIVILEGIADO = setting.getString(
				Parametros.preferencia_wifi, Parametros.PREF_WIFI_PRIVILEGIADO);
		Parametros.PREF_NUMERO_DE_TERMINAL = setting.getString(
				Parametros.preferencia_idtablet,
				Parametros.PREF_NUMERO_DE_TERMINAL);
		Parametros.ADMIN_LOGIN = setting.getString(
				Parametros.preferencia_admin_log, Parametros.ADMIN_LOGIN);
		Parametros.ADMIN_PASSWORD = setting.getString(
				Parametros.preferencia_admin_pwd, Parametros.ADMIN_PASSWORD);
		Parametros.carpeta_sancion_log_tablet = setting.getString(
				Parametros.preferencias_logTablet,
				Parametros.carpeta_sancion_log_tablet);
		Parametros.carpeta_sancion_log_Datos = setting.getString(
				Parametros.preferencias_logDatos,
				Parametros.carpeta_sancion_log_Datos);
		Parametros.PREF_CANT_FOTOS = setting.getString(
				Parametros.preferencia_max_cant_fotos,
				Parametros.PREF_CANT_FOTOS);
		Parametros.PREF_LOG_EVENTOS = setting.getBoolean(
				Parametros.preferencias_logEvento, Parametros.PREF_LOG_EVENTOS);
		Parametros.PREF_LOG_PROCESOS = setting.getBoolean(
				Parametros.preferencias_logProcesos,
				Parametros.PREF_LOG_PROCESOS);
		Parametros.PREF_LOG_MENSAJES = setting.getBoolean(
				Parametros.preferencias_logMensajes,
				Parametros.PREF_LOG_MENSAJES);
		Parametros.PREF_LOG_EXCEPCIONES = setting.getBoolean(
				Parametros.preferencias_logExcepciones,
				Parametros.PREF_LOG_EXCEPCIONES);
		Parametros.PREF_HAB_IMPRESION = setting.getBoolean(
				Parametros.preferencias_habilitar_impresion,
				Parametros.PREF_HAB_IMPRESION);
		Parametros.MAC_BLUETOOH = setting.getString(
				Parametros.preferencias_mac_bluetooth, Parametros.MAC_BLUETOOH);
	}

}
