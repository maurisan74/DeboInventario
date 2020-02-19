package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Activity que muestra una pantalla con las preferencias de funcionamiento del
 * sistema. URL del web service, SSID de la conexin WIFI, id de Tablet, URI de
 * importacin y exportacin de los inventarios.
 * 
 * @author GuillermoR
 * 
 */
public class PreferenciasInventario extends Activity {

	private ArrayAdapter<CharSequence> adapterLocales;
	@NonNull
    private Context ctxt = this;
	/**
	 * Variable para almacenar la URL del webService
	 */
	@Nullable
    private String url_webservice;
	/**
	 * Variable para almacenar el SSID de la red predeterminada
	 */
	@Nullable
    private String nombre_wifi_priveligiado;
	/**
	 * Variable para almacenar el id de la tablet
	 */
	@Nullable
    private String id_tablet;
	/**
	 * Variables para almacenar las URI de exportacion e importacion de los
	 * inventarios
	 */
	@Nullable
    private String usb_uri_export, usb_uri_import, carpetaMaestros,
			carpetaLogEventos, carpetaLogDatos;
	/**
	 * Edit texts de los valores a almacenar
	 */
	private EditText edit_webservice, edit_wifi, edit_idtablet;
	private EditText edit_usb_export, edit_usb_import, edit_carpeta_maestros,
			edit_carpeta_LogEventos, edit_carpeta_LogDatos;
	/**
	 * Boton para guardar los datos
	 */
	private Button botonGuardar;
	private Button btnCheckURL;
	private Button botonCancelar;
	private Button botonAgregarUrl;
	private SharedPreferences settings;

	private CheckBox CheckedlogEventos;
	private CheckBox CheckedlogProcesos;
	private CheckBox CheckedlogMensajes;
	private CheckBox CheckedlogExcepciones;

	private RadioButton CheckedInventariosVentas;
	private RadioButton CheckedInventariosDeposito;
		
	
	private CheckBox CheckedHabCamScanner;
	private CheckBox CheckedLecturaEntrada;
	
	private CheckBox CheckedToma;
	private CheckBox CheckedBalanza;
	
	private RadioGroup RadioGroupProductosNoContabilizados;
	private RadioButton radio1;
	private RadioButton radio2;
	
	
	private RadioGroup RadioGroupVD;
	private RadioButton radioV;
	private RadioButton radioD;
	

	private boolean BooleanlogEventos;
	private boolean BooleanlogProcesos;
	private boolean BooleanlogMensajes;
	private boolean BooleanlogExcepciones;
	private boolean InventariosVentas;
	private boolean InventariosDeposito;
	private boolean HabCamScanner;
	private boolean LecturaEntrada;
	
	private boolean BooleanToma;
	private int StockalaToma;
	
	private boolean BooleanBalanza;
	private int balanzaActivada;
	
	private int ProductosNoContabilizados;
	private Spinner SpinnerUrls;
	private boolean bloquearSpinner = true;
	private int idLocalActual;

	private int URL;
	
	public CheckBox checkboxToma;
	public CheckBox checkboxBalanza;

	/**
	 * Metodo llamado al crear la activity
	 * <p>
	 * 1 Restaurar las preferencias
	 * <p>
	 * 2 Creamos los textBox correspondientes
	 * <p>
	 * 3 Configuramos los botones
	 * <p>
	 * 4 Configurar los HANDLERS
	 */

	@NonNull
    static GestorLogEventos log = new GestorLogEventos();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_preferenciasinventario);

		//if (android.os.Build.VERSION.SDK_INT ==23) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		//}


		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 92 --]" + "Comienzo de Preferencias Inventario", 2);

		try {
			// 1 Restaurar las preferencias:
			settings = PreferenceManager.getDefaultSharedPreferences(ctxt);
			url_webservice = settings.getString(
					Parametros.preferencia_servidor,
					Parametros.PREF_URL_CONEXION_SERVIDOR);
			idLocalActual = settings.getInt(
					Parametros.preferencia_LOCAL,
					Parametros.Pref_id_Local);
			nombre_wifi_priveligiado = settings.getString(
					Parametros.preferencia_wifi,
					Parametros.PREF_WIFI_PRIVILEGIADO);
			id_tablet = settings.getString(Parametros.preferencia_idtablet,
					Parametros.PREF_NUMERO_DE_TERMINAL);
			usb_uri_export = settings.getString(
					Parametros.preferencia_usb_uri_export,
					ParametrosInventario.CARPETA_DESDETABLET);
			usb_uri_import = settings.getString(
					Parametros.preferencia_usb_uri_import,
					ParametrosInventario.CARPETA_ATABLET);
			carpetaMaestros = settings.getString(
					Parametros.preferencia_usb_uri_maestros,
					ParametrosInventario.CARPETA_MAETABLET);
			carpetaLogEventos = settings.getString(
					Parametros.preferencia_usb_uri_log_eventos,
					ParametrosInventario.CARPETA_LOGTABLET);
			carpetaLogDatos = settings.getString(
					Parametros.preferencia_usb_uri_log_datos,
					ParametrosInventario.CARPETA_LOGDATOS);

			BooleanlogEventos = settings.getBoolean(
					Parametros.preferencias_logEvento,
					Parametros.PREF_LOG_EVENTOS);
			BooleanlogProcesos = settings.getBoolean(
					Parametros.preferencias_logProcesos,
					Parametros.PREF_LOG_PROCESOS);
			BooleanlogMensajes = settings.getBoolean(
					Parametros.preferencias_logMensajes,
					Parametros.PREF_LOG_MENSAJES);
			BooleanlogExcepciones = settings.getBoolean(
					Parametros.preferencias_logExcepciones,
					Parametros.PREF_LOG_EXCEPCIONES);
			InventariosVentas = settings.getBoolean(
					Parametros.preferencias_inventario_venta,
					ParametrosInventario.InventariosVentas);
			InventariosDeposito = settings.getBoolean(
					Parametros.preferencias_inventario_deposito,
					ParametrosInventario.InventariosDeposito);
			
			/*checkbox stock damian*/
			BooleanToma = settings.getBoolean(
					Parametros.preferencias_stockToma,
					Parametros.PREF_BAL);
		//	System.out.println("::: PreferenciasInventario 197 +"+ BooleanToma);

			BooleanBalanza = settings.getBoolean(
					Parametros.preferencias_balanza,
					Parametros.PREF_STOCK);
			System.out.println("::: PreferenciasInventarios booleanBalanza " + BooleanBalanza);
			
			HabCamScanner = settings.getBoolean(
					ParametrosInventario.preferencias_camara_scanner,
					ParametrosInventario.CamHabScanner);

			LecturaEntrada = settings.getBoolean(
					Parametros.preferencia_lectura_entrada,
					ParametrosInventario.LecturaEntrada);

			ProductosNoContabilizados = settings.getInt(
					Parametros.preferencia_productos_no_contabilizados,
					ParametrosInventario.ProductosNoContabilizados);

		} catch (Exception e) {
			log.log("[-- 130 --]" + e.toString(), 4);
		}

		// 2 Creamos los textBox correspondientes y chekbox:
		try {
			edit_webservice = (EditText) findViewById(R.id.PREF_URL_webservice);
			edit_wifi = (EditText) findViewById(R.id.PREF_wifi_preferido);
			edit_idtablet = (EditText) findViewById(R.id.PREF_id_tablet);

			edit_webservice.setText(url_webservice);
			edit_wifi.setText(nombre_wifi_priveligiado);
			edit_idtablet.setText(id_tablet);

			edit_usb_export = (EditText) findViewById(R.id.PREF_usb_export);
			edit_usb_import = (EditText) findViewById(R.id.PREF_usb_import);
			edit_carpeta_maestros = (EditText) findViewById(R.id.Carpeta_Maestros);
			edit_carpeta_LogEventos = (EditText) findViewById(R.id.Carpeta_LogEventos);
			edit_carpeta_LogDatos = (EditText) findViewById(R.id.Carpeta_LogDatos);

			edit_usb_export.setText(usb_uri_export);
			edit_usb_import.setText(usb_uri_import);
			edit_carpeta_maestros.setText(carpetaMaestros);
			edit_carpeta_LogEventos.setText(carpetaLogEventos);
			edit_carpeta_LogDatos.setText(carpetaLogDatos);

			CheckedlogEventos = (CheckBox) findViewById(R.id.checkBoxEventos);
			CheckedlogProcesos = (CheckBox) findViewById(R.id.checkBoxProcesos);
			CheckedlogMensajes = (CheckBox) findViewById(R.id.checkBoxMensajes);
			CheckedlogExcepciones = (CheckBox) findViewById(R.id.checkBoxExcepciones);
			CheckedHabCamScanner = (CheckBox) findViewById(R.id.CheckHabcamScanner);
			CheckedLecturaEntrada = (CheckBox) findViewById(R.id.CheckLecturaEntrada);

			CheckedToma = (CheckBox) findViewById(R.id.checkBoxToma);
			CheckedBalanza = (CheckBox) findViewById(R.id.checkBoxBalanza);
			System.out.println("::: PreferenciasInventario 247 CheckedBalanza " + CheckedBalanza);
			CheckedInventariosVentas = (RadioButton) findViewById(R.id.CheckInventariosVentas);
			CheckedInventariosDeposito = (RadioButton) findViewById(R.id.CheckInventariosDepositos);
			System.out.println("::: PreferenciasInventario 247");
	
			
			RadioGroupProductosNoContabilizados = (RadioGroup) findViewById(R.id.gruporb);
			radio1 = (RadioButton) findViewById(R.id.radio1);
			radio2 = (RadioButton) findViewById(R.id.radio2);
			if (ProductosNoContabilizados == 1) {
				radio1.setChecked(true);
			} else if (ProductosNoContabilizados == 2) {
				radio2.setChecked(true);
			}
			

			CheckedlogEventos.setChecked(BooleanlogEventos);
			CheckedlogProcesos.setChecked(BooleanlogProcesos);
			CheckedlogMensajes.setChecked(BooleanlogMensajes);
			CheckedlogExcepciones.setChecked(BooleanlogExcepciones);
			CheckedInventariosVentas.setChecked(InventariosVentas);
			CheckedInventariosDeposito.setChecked(InventariosDeposito);
			CheckedHabCamScanner.setChecked(HabCamScanner);
			CheckedLecturaEntrada.setChecked(ParametrosInventario.LecturaEntrada);
			
			CheckedToma.setChecked(BooleanToma);
			CheckedBalanza.setChecked(BooleanBalanza);
			System.out.println("::: PreferenciasInventario 285 CheckedBalanza " + CheckedBalanza);
			
		} catch (Exception e) {
			log.log(e.toString(), 4);
		}

		// 3 Configuramos los botones:
		botonCancelar = (Button) findViewById(R.id.PREF_boton_cancelar);
		botonGuardar = (Button) findViewById(R.id.PREF_boton_guardar);
		botonAgregarUrl = (Button) findViewById(R.id.PREF_agregarUrl);
		btnCheckURL = (Button) findViewById(R.id.PREF_checkUrl);

		// 4 Configurar los HANDLERS:
		botonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				log.log("[-- 178 --]" + "Se presiono cancelar", 0);
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		botonAgregarUrl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(ctxt, SeleccionarUrl.class);
				i.putExtra("algo", "algo");
				startActivityForResult(i, URL);

			}
		});

		btnCheckURL.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Parametros.PREF_URL_CONEXION_SERVIDOR = String.valueOf(edit_webservice.getText()).trim();
				String result = checkURL(Parametros.PREF_URL_CONEXION_SERVIDOR);
				Toast.makeText(ctxt, result, Toast.LENGTH_LONG).show();
			}
		});

		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("::: Preferencia 320");
				boolean salir = false;
				try {
					log.log("[-- 166 --]" + "Se presiono guardar", 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(Parametros.preferencia_servidor, String
							.valueOf(edit_webservice.getText()).trim());
					editor.putInt(Parametros.preferencia_LOCAL, idLocalActual );
					editor.putString(Parametros.preferencia_wifi, String
							.valueOf(edit_wifi.getText()).trim());
					editor.putString(Parametros.preferencia_idtablet, String
							.valueOf(edit_idtablet.getText()).trim());
					editor.putString(Parametros.preferencia_usb_uri_export,
							String.valueOf(edit_usb_export.getText()).trim());
					editor.putString(Parametros.preferencia_usb_uri_import,
							String.valueOf(edit_usb_import.getText()).trim());
					editor.putString(Parametros.preferencia_usb_uri_maestros,
							String.valueOf(edit_carpeta_maestros.getText())
									.trim());
					editor.putString(
							Parametros.preferencia_usb_uri_log_eventos, String
									.valueOf(edit_carpeta_LogEventos.getText())
									.trim());
					editor.putString(Parametros.preferencia_usb_uri_log_datos,
							String.valueOf(edit_carpeta_LogDatos.getText())
									.trim());

					editor.putBoolean(Parametros.preferencias_logEvento,
							Boolean.valueOf(CheckedlogEventos.isChecked()));
					
					editor.putBoolean(Parametros.preferencias_logProcesos,
							Boolean.valueOf(CheckedlogProcesos.isChecked()));
					editor.putBoolean(Parametros.preferencias_logMensajes,
							Boolean.valueOf(CheckedlogMensajes.isChecked()));
					editor.putBoolean(Parametros.preferencias_logExcepciones,
							Boolean.valueOf(CheckedlogExcepciones.isChecked()));
					if (Boolean.valueOf(CheckedInventariosVentas.isChecked()) == false
							&& Boolean.valueOf(CheckedInventariosDeposito
									.isChecked()) == false) {
						Toast.makeText(ctxt,
								"Algun tipo de Inventario debe ser utilizado",
								Toast.LENGTH_LONG).show();

						salir = false;
					} else {
						salir = true;
					}
					
//					if (Boolean.valueOf(CheckedInventariosVentas.isChecked()) == true) {
//						System.out.println("::: Preferencia 369");
//						Toast.makeText(ctxt,
//								"Ventas",
//								Toast.LENGTH_LONG).show();
//					} else if (Boolean.valueOf(CheckedInventariosDeposito.isChecked()) == true) {
//						Toast.makeText(ctxt,
//								"Ventas",
//								Toast.LENGTH_LONG).show();
//					}
//					
					editor.putBoolean(Parametros.preferencias_inventario_venta,
							CheckedInventariosVentas
									.isChecked());
					
					
					/*este seria el checkbox de stock pero dejo referencia camaras*/
					editor.putBoolean(
							Parametros.preferencias_stockToma,
							CheckedToma.isChecked());
					
					/* checkbox para habilitar balanza*/
					editor.putBoolean(
							Parametros.preferencias_balanza,
							Boolean.valueOf(CheckedBalanza.isChecked()));
					
					System.out.println("::: Preferencia 3 :::" + CheckedToma);
					System.out.println("::: Preferencia 3 :::" + ParametrosInventario.preferencias_stock_alatoma);
					
					editor.putBoolean(
							Parametros.preferencias_inventario_deposito,
							Boolean.valueOf(CheckedInventariosDeposito
									.isChecked()));

					editor.putBoolean(
							ParametrosInventario.preferencias_camara_scanner,
							Boolean.valueOf(CheckedHabCamScanner.isChecked()));
					editor.putBoolean(Parametros.preferencia_lectura_entrada,
							Boolean.valueOf(CheckedLecturaEntrada.isChecked()));

					int radioButtonID = RadioGroupProductosNoContabilizados
							.getCheckedRadioButtonId();
					View radioButton = RadioGroupProductosNoContabilizados
							.findViewById(radioButtonID);
					int idx = RadioGroupProductosNoContabilizados
							.indexOfChild(radioButton);

					if (idx == 0) {
						System.out.println("-----!!!!!!---!!!!!!!------------!!!!!--------------");
						System.out.println("SALE EN 1");
						ProductosNoContabilizados = 1;
					} else if (idx == 1) {
						System.out.println("-----!!!!!!---!!!!!!!------------!!!!!--------------");
						System.out.println("SALE EN 2");
						
						ProductosNoContabilizados = 2;
					}
					editor.putInt(
							Parametros.preferencia_productos_no_contabilizados,
							ProductosNoContabilizados);

					editor.commit();
				} catch (Exception e) {
					log.log("[-- 202 --]" + e.toString() + " " + e.getMessage(),
							4);
				}

				try {
					Parametros.PREF_URL_CONEXION_SERVIDOR = String.valueOf(
							edit_webservice.getText()).trim();
					Parametros.Pref_id_Local = idLocalActual;
					Parametros.PREF_WIFI_PRIVILEGIADO = String.valueOf(
							edit_wifi.getText()).trim();
					Parametros.PREF_NUMERO_DE_TERMINAL = String.valueOf(
							edit_idtablet.getText()).trim();
					ParametrosInventario.CARPETA_DESDETABLET = String.valueOf(
							edit_usb_export.getText()).trim();
					ParametrosInventario.CARPETA_ATABLET = String.valueOf(
							edit_usb_import.getText()).trim();
					ParametrosInventario.CARPETA_MAETABLET = String.valueOf(
							edit_carpeta_maestros.getText()).trim();
					ParametrosInventario.CARPETA_LOGTABLET = String.valueOf(
							edit_carpeta_LogEventos.getText()).trim();
					ParametrosInventario.CARPETA_LOGDATOS = String.valueOf(
							edit_carpeta_LogEventos.getText()).trim();
					Parametros.PREF_LOG_EVENTOS = Boolean
							.valueOf(CheckedlogEventos.isChecked());
					Parametros.PREF_LOG_PROCESOS = Boolean
							.valueOf(CheckedlogProcesos.isChecked());
					Parametros.PREF_LOG_MENSAJES = Boolean
							.valueOf(CheckedlogMensajes.isChecked());
					Parametros.PREF_LOG_EXCEPCIONES = Boolean
							.valueOf(CheckedlogExcepciones.isChecked());
					ParametrosInventario.InventariosVentas = Boolean
							.valueOf(CheckedInventariosVentas.isChecked());
//					System.out.println("::: Preferencia 452");
					ParametrosInventario.InventariosDeposito = Boolean
							.valueOf(CheckedInventariosDeposito.isChecked());
					
					/*Toma valor del checkbox true o false*/
					ParametrosInventario.StockToma = Boolean
							.valueOf(CheckedToma.isChecked());
					
					System.out.println("::: Preferencias checkboxToma 33:"+ Boolean.valueOf(CheckedToma.isChecked()));
					
					ParametrosInventario.balanza = Boolean
							.valueOf(CheckedBalanza.isChecked());
					
					System.out.println("::: Preferencias checkboxBalanza 33:"+ Boolean.valueOf(CheckedBalanza.isChecked()));
					
					ParametrosInventario.CamHabScanner = Boolean
							.valueOf(CheckedHabCamScanner.isChecked());
					ParametrosInventario.LecturaEntrada = Boolean
							.valueOf(CheckedLecturaEntrada.isChecked());
					ParametrosInventario.ProductosNoContabilizados = ProductosNoContabilizados;

					log.log("SERVIDOR: "
							+ Parametros.PREF_URL_CONEXION_SERVIDOR, 3);
					log.log("LOCAL: "
							+ Parametros.Pref_id_Local, 3);
					log.log("WI FI: " + Parametros.PREF_WIFI_PRIVILEGIADO, 3);
					log.log("TERMINAL: " + Parametros.PREF_NUMERO_DE_TERMINAL,
							3);
					log.log("ATABLET: " + ParametrosInventario.CARPETA_ATABLET,
							3);
					log.log("DESDETABLET: "
							+ ParametrosInventario.CARPETA_DESDETABLET, 3);
					log.log("MAETABLET: "
							+ ParametrosInventario.CARPETA_MAETABLET, 3);
					log.log("LOGTABLET: "
							+ ParametrosInventario.CARPETA_LOGTABLET, 3);
					log.log("LOGDATOS: "
							+ ParametrosInventario.CARPETA_LOGDATOS, 3);
					log.log("EVENTOS: " + Parametros.PREF_LOG_EVENTOS, 3);
					log.log("MENSAJES: " + Parametros.PREF_LOG_MENSAJES, 3);
					log.log("PROCESOS: " + Parametros.PREF_LOG_PROCESOS, 3);
					log.log("EXCEPCIONES: " + Parametros.PREF_LOG_EXCEPCIONES,
							3);
					log.log("VENTAS: " + ParametrosInventario.InventariosVentas,
							3);
					log.log("DEPOSITOS: "
							+ ParametrosInventario.InventariosDeposito, 3);
					log.log("Scanner Camara: "
							+ ParametrosInventario.CamHabScanner, 3);
					log.log("Lectura Entrada: "
							+ ParametrosInventario.LecturaEntrada, 3);
					log.log("Productos no contabilizados: "
							+ ParametrosInventario.ProductosNoContabilizados, 3);
					
					log.log("Stock habilitado: "
							+ ParametrosInventario.StockalaToma, 3);

					String result = checkURL(Parametros.PREF_URL_CONEXION_SERVIDOR);
					Toast.makeText(ctxt,
							result,
							Toast.LENGTH_LONG).show();

				} catch (Exception e) {
					log.log("[-- 218 --]" + e.toString() + " " + e.getMessage(),
							4);

				}

				if (salir == true) {

					setResult(RESULT_OK);
					finish();

				}
			}
		});
		mostrarLocalesSpinner();
		this.SpinnerUrls
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(@NonNull AdapterView<?> parentView,
                                               View selectedItemView, int position, long id) {
						if (bloquearSpinner == true) {
							bloquearSpinner = false;
						} else {
							try {
								BaseDatos data = new BaseDatos(ctxt);
								Local localTemp = data
										.ObtenerLocal_x_Id((position + 1));
								edit_webservice.setText(localTemp
										.getDescripcion());
								idLocalActual = localTemp.getIdLocal();
								Toast.makeText(
										parentView.getContext(),
										"Has seleccionado "
												+ parentView.getItemAtPosition(
														position).toString(),
										Toast.LENGTH_LONG).show();
							} catch (ExceptionBDD e) {
								e.getStackTrace();
							}
						}
					}

					public void onNothingSelected(AdapterView<?> parentView) {
					}
				});
	}


	@NonNull
    public String checkURL(String u){
		try{
			URL myUrl = new URL(u);
			HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
			int algo = connection.getResponseCode();
			String message = connection.getResponseMessage();
			if (algo==200){
				return "URL WebService correcta";
			}else{
				return "URL WebService Incorrecta ("+algo+" "+message+")";
			}
		} catch (Exception e) {
			return "URL WebService Incorrecta ("+e+")";
		}
/*	public static int getHTTPResponseStatusCode(String u) throws IOException {
		java.net.URL url = new URL("www.google.com");
		System.out.println("LLEGA :::::::::: " + url);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		int code = connection.getResponseCode();
		System.out.println("HHHHHHHHHHHHHHHHHHHHHHH "+code);
		return code;
*/
	}

	public static void cargarPreferencias(Context ctxtx) {
		try {
			SharedPreferences setting = PreferenceManager
					.getDefaultSharedPreferences(ctxtx);
			Parametros.PREF_URL_CONEXION_SERVIDOR = setting.getString(
					Parametros.preferencia_servidor,
					Parametros.PREF_URL_CONEXION_SERVIDOR);
			Parametros.Pref_id_Local = setting.getInt(
					Parametros.preferencia_LOCAL,
					Parametros.Pref_id_Local);
			Parametros.PREF_WIFI_PRIVILEGIADO = setting.getString(
					Parametros.preferencia_wifi,
					Parametros.PREF_WIFI_PRIVILEGIADO);
			Parametros.PREF_NUMERO_DE_TERMINAL = setting.getString(
					Parametros.preferencia_idtablet,
					Parametros.PREF_NUMERO_DE_TERMINAL);
			ParametrosInventario.CARPETA_ATABLET = setting.getString(
					Parametros.preferencia_usb_uri_import,
					ParametrosInventario.CARPETA_ATABLET);
			ParametrosInventario.CARPETA_DESDETABLET = setting.getString(
					Parametros.preferencia_usb_uri_export,
					ParametrosInventario.CARPETA_DESDETABLET);
			ParametrosInventario.CARPETA_MAETABLET = setting.getString(
					Parametros.preferencia_usb_uri_maestros,
					ParametrosInventario.CARPETA_MAETABLET);
			ParametrosInventario.CARPETA_LOGTABLET = setting.getString(
					Parametros.preferencia_usb_uri_log_eventos,
					ParametrosInventario.CARPETA_LOGTABLET);
			ParametrosInventario.CARPETA_LOGDATOS = setting.getString(
					Parametros.preferencia_usb_uri_log_datos,
					ParametrosInventario.CARPETA_LOGDATOS);

			Parametros.PREF_CAMARA = setting.getBoolean(
					Parametros.preferencia_admin_log, Parametros.PREF_CAMARA);

			Parametros.PREF_LOG_EVENTOS = setting.getBoolean(
					Parametros.preferencias_logEvento,
					Parametros.PREF_LOG_EVENTOS);
			Parametros.PREF_LOG_MENSAJES = setting.getBoolean(
					Parametros.preferencias_logProcesos,
					Parametros.PREF_LOG_PROCESOS);
			Parametros.PREF_LOG_PROCESOS = setting.getBoolean(
					Parametros.preferencias_logMensajes,
					Parametros.PREF_LOG_MENSAJES);
			Parametros.PREF_LOG_EXCEPCIONES = setting.getBoolean(
					Parametros.preferencias_logExcepciones,
					Parametros.PREF_LOG_EXCEPCIONES);
			ParametrosInventario.InventariosVentas = setting.getBoolean(
					Parametros.preferencias_inventario_venta,
					ParametrosInventario.InventariosVentas);
			ParametrosInventario.InventariosDeposito = setting.getBoolean(
					Parametros.preferencias_inventario_deposito,
					ParametrosInventario.InventariosDeposito);
			ParametrosInventario.CamHabScanner = setting.getBoolean(
					ParametrosInventario.preferencias_camara_scanner,
					ParametrosInventario.CamHabScanner);
			ParametrosInventario.LecturaEntrada = setting.getBoolean(
					Parametros.preferencia_lectura_entrada,
					ParametrosInventario.LecturaEntrada);
			ParametrosInventario.ProductosNoContabilizados = setting.getInt(
					Parametros.preferencia_productos_no_contabilizados,
					ParametrosInventario.ProductosNoContabilizados);
			
			ParametrosInventario.StockToma = setting.getBoolean(
					Parametros.preferencias_stockToma,
					ParametrosInventario.StockToma);
			
			ParametrosInventario.balanza = setting.getBoolean(Parametros.preferencias_balanza, ParametrosInventario.balanza);

			log.log("SERVIDOR: " + Parametros.PREF_URL_CONEXION_SERVIDOR, 3);
			log.log("WI FI: " + Parametros.PREF_WIFI_PRIVILEGIADO, 3);
			log.log("TERMINAL: " + Parametros.PREF_NUMERO_DE_TERMINAL, 3);
			log.log("ATABLET: " + ParametrosInventario.CARPETA_ATABLET, 3);
			log.log("DESDETABLET: " + ParametrosInventario.CARPETA_DESDETABLET,
					3);
			log.log("MAETABLET: " + ParametrosInventario.CARPETA_MAETABLET, 3);
			log.log("LOGTABLET: " + ParametrosInventario.CARPETA_LOGTABLET, 3);
			log.log("LOGDATOS: " + ParametrosInventario.CARPETA_LOGDATOS, 3);
			log.log("EVENTOS: " + Parametros.PREF_LOG_EVENTOS, 3);
			log.log("MENSAJES: " + Parametros.PREF_LOG_MENSAJES, 3);
			log.log("PROCESOS: " + Parametros.PREF_LOG_PROCESOS, 3);
			log.log("EXCEPCIONES: " + Parametros.PREF_LOG_EXCEPCIONES, 3);
			log.log("VENTAS: " + ParametrosInventario.InventariosVentas, 3);
			log.log("DEPOSITOS: " + ParametrosInventario.InventariosDeposito, 3);
			log.log("Scanner: " + ParametrosInventario.CamHabScanner, 3);
			
			log.log("Stock: " + ParametrosInventario.StockalaToma, 3);
			
			log.log("Lectura por Entrada: "
					+ ParametrosInventario.LecturaEntrada, 3);
			log.log("Productos no contabilizados: "
					+ ParametrosInventario.ProductosNoContabilizados, 3);

		} catch (Exception e) {
			log.log("[-- 266 --]" + e.toString() + " || " + e.getMessage(), 4);

		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == URL) {
				if (resultCode == RESULT_OK) {
					mostrarLocalesSpinner();
				} else if (resultCode == RESULT_CANCELED) {
					Toast.makeText(ctxt, "Error al recuperar los locales",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mostrarLocalesSpinner() {
		System.out.println("::: PreferenciasInventario 726 mostrarLocalesSpinner");
		SpinnerUrls = (Spinner) findViewById(R.id.spinner1);
		try {
			BaseDatos data = new BaseDatos(ctxt);

			ArrayList<Local> verificar_datos = data.ObtenerTodosLocales();
			if (verificar_datos.size() > 0) {
				//No debe hacer nada
			}else{
				data.guardarLocal(new Local("POS Retail",
						"http://192.168.0.0/webservice_balanza/deboinventario/webservice.php"));
				data.guardarLocal(new Local("BO Estaciones",
						"http://192.168.0.0/wsestaciones/webservice.php"));
			}

			ArrayList<Local> Locales = data.ObtenerTodosLocales();

			if (Locales.size() > 0) {
				String[] paraAdapter = new String[Locales.size()];
				int posotionSelected = 0;
				int cont = 0;
				for (Local local : Locales) {
					paraAdapter[cont] = local.getNombre();

					if (idLocalActual == local.getIdLocal()) {
						posotionSelected = cont;
					}
					cont++;
				}
				ArrayAdapter<String> adapterLocales = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item, paraAdapter);
				adapterLocales
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				SpinnerUrls.setAdapter(adapterLocales);
				SpinnerUrls.setSelection(posotionSelected);
			}
		} catch (ExceptionBDD e) {
			Toast.makeText(ctxt,
					"Error al recuperar los locales" + e.getMessage(),
					Toast.LENGTH_LONG).show();
			;
		}
	}
}