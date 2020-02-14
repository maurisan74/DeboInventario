package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity principal que se llama cuando se inicia la aplicaci�n, es la
 * pantalla de inicio al sistema. A partir de aqu� se dispara la ejecuci�n de
 * las otras clases.
 * 
 * @author GuillermoR
 * 
 */
public class DeboInventario extends Activity implements DialogPersoSimple, Wifi {

	/**
	 * Datos de la interfaz
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Boton para pasar a la pantalla de InventarioMainBoard.java
	 */
	private Button boton_continuar;
	/**
	 * 
	 */
	private Button boton_empezar;
	// private Button botonBD;
	/**
	 * Informaci�n de la versi�n del programa y de la tablet
	 */
	private TextView version, idtablet;

	/**
	 * Boton para pasar a la pantalla de CompraMainBoard.java
	 */
	private Button boton_compras;

	/**
	 * Boton para buscar proveedor por nombre
	 */

	private Button boton_busqueda_nombre;



	/**
	 * Boton para cerrar la aplicacin (icono : X)
	 */
	private ImageView boton_salir;
	/**
	 * Boton para configurar los parametros, abra las preferencias(icono:
	 * herramientas)
	 * 
	 */
	private ImageView boton_config;
	/**
	 * Boton para actualizar el maestro de articulos, permite actualizar la
	 * tabla de referencias (icono: flecha)
	 */
	private ImageView boton_actualizar;
	/**
	 * Logo de Debo Inventario, posee una funcionalidad secreta, al presionar 3
	 * veces de forma prolongada se resetean las tablas de articulos e
	 * inventarios
	 */
	private ImageView image_debo;

	/**
	 * Dialogo para mostrar el progreso de las actualizaciones
	 */

	private ProgressDialog popupCarga;
	/**
	 * Dialogo para dar opcion de medio desde donde actualizar
	 */
	private DialogPersoComplexActualizacionWifiUsb dialogoActualizacion;
	/**
	 * Variable para almacenar que se va a actualizar, la unica opcion por el
	 * momento son las referencias o maestro de articulos
	 */
	@NonNull
    private ArrayList<String> lista_actualizaciones_programadas = new ArrayList<String>();

	/**
	 * Dialogo para permitir acceder a las opciones del menu para cambiar la
	 * hora VERIFICAR
	 */
	private DialogPersoComplexSiNo dialogConfigurarHora;

	// private ProgressDialog dialogAguarde;

	/**
	 * Contador para verificar el reseto de la BD, hay que presionar 3 veces el
	 * logo
	 */
	private int contador_reset_BDD = 3;
	private String unidad_almacenamiento = "";// para comprobar que tipo de
												// unidad est� conectada, se
												// compara
	private int unidad_numerica = 0;// variable de control para un switch, a la
									// hora de comprobar la unidad de
									// almacenamiento

	/*
	 * Metodo que se ejecuta al crearse la activity, inicializaciones,
	 * comprobaciones, etc
	 * <p>
	 * 1� Mensaje de verificaci�n de hora
	 * <p>
	 * 2� Control de preexistencia de los archivos necesarios
	 * <p>
	 * 3� Otro control horario en caso que la fecha sea muy incoherente
	 * <p>
	 * 4� Control URL de conexi�n al servidor
	 * <p>
	 * 5� Creaci�n de los elementos graficos de la p�gina
	 * 
	 */

	/*
	 * Se crea y se instancia la clase de log
	 */

	final GestorLogEventos log = new GestorLogEventos();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("DEBOOOOOOOOOOOOOOOOOOOOOO ACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		System.out.println(R.layout.xml_deboinventario);
		setContentView(R.layout.xml_deboinventario);

		PreferenciasInventario.cargarPreferencias(ctxt);

		/**
		 * Se crea y se instancia la clase de log
		 */

		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.logInicial("DeboInventario Version: "
				+ ParametrosInventario.VERSION + "IdTablet: "
				+ Parametros.PREF_NUMERO_DE_TERMINAL);
		log.log("[-- 167 --]" + "Inicio de la aplicacion", 2);
		System.out.println("::: Version en DI " + ParametrosInventario.VERSION);
		// 1 Mensaje de verificacin de hora
		// showSimpleDialogOK(
		// "Verificacion de horario",
		// "Por favor VERIFIQUE que la HORA de la tablet este sincronizada con la del"
		// + " BackOffice para el correcto "
		// + "funcionamiento del sistema, Gracias!").show();

		View.OnClickListener listenerPositivoHora = new View.OnClickListener() {

			public void onClick(View v) {
				// Abre el menu de opciones
				// openOptionsMenu();
				Intent intentHora = new Intent(
						Settings.ACTION_DATE_SETTINGS);
				startActivityForResult(intentHora, 0);
				dialogConfigurarHora.dismiss();

				log.log("[-- 186 --]" + "Activa Modificacion de hora", 0);
			}
		};

		View.OnClickListener listenerNegativoHora = new View.OnClickListener() {

			public void onClick(View v) {
				// Cierra el dialgo
				dialogConfigurarHora.dismiss();

				log.log("[-- 196 --]" + "Cancela Modificacion de Hora", 0);
			}
		};

		dialogConfigurarHora = new DialogPersoComplexSiNo(ctxt,
				"Verificacion de horario",
				"Por favor VERIFIQUE que la HORA de la tablet este sincronizada con la del"
						+ " BackOffice para el correcto "
						+ "funcionamiento del sistema.\n"
						+ "Desea configurarla en este momento?",
				DialogPerso.VALIDAR, listenerPositivoHora, listenerNegativoHora);
		dialogConfigurarHora.show();

		log.log("[-- 207 --]" + "Acepta aviso de Verificacion de Hora", 2);
		// Toast.makeText(ctxt,
		// "Por favor VERIFIQUE que la HORA de la tablet este sincronizada con la del"
		// +
		// " BackOffice para el correcto " +
		// "funcionamiento del sistema, Gracias!" , Toast.LENGTH_LONG).show();
		// 2 Control de preexistencia de los archivos necesarios:
		// CARPETA DE LAS FOTOS: //
		File carpeta_de_fotos = new File(ParametrosInventario.URL_CARPETA_FOTOS);
		if (carpeta_de_fotos.exists() == false) {
			carpeta_de_fotos.mkdir();
		}

		// CREACIN Y VERIFICACIN DE CARPETAS tanto en flash como sdcard //

		try {
			
			File Flash_deboInventario = new File(ParametrosInventario.CARPETA_DEBOINVENTARIO);
			File Flash_deboInventario_aTablet = new File(ParametrosInventario.CARPETA_ATABLET);
			File Flash_deboInventario_desdeTablet = new File(ParametrosInventario.CARPETA_DESDETABLET);
			File Flash_deboInventario_maeTablet = new File(ParametrosInventario.CARPETA_MAETABLET);
			File Flash_deboInventario_logTablet = new File(ParametrosInventario.CARPETA_LOGTABLET);
			File Flash_deboInventario_logDatos = new File(ParametrosInventario.CARPETA_LOGDATOS);
			File carpetaInternaImportacion = new File(ParametrosInventario.URL_CARPETA_USB_EXPORT); 
			File url_carpeta_usb = new File(ParametrosInventario.URL_CARPETA_USB);
			File url_carpeta_usb_import = new File(ParametrosInventario.URL_CARPETA_USB_IMPORT);
			File url_carpeta_usb_export = new File(ParametrosInventario.URL_CARPETA_USB_EXPORT);
			
			
			if(carpetaInternaImportacion.exists() == false){
				carpetaInternaImportacion.mkdir();
			}
			
			if(Flash_deboInventario.exists() == false)
		    Flash_deboInventario.mkdir();
			
			if(Flash_deboInventario_aTablet.exists() == false)
				Flash_deboInventario_aTablet.mkdir();
			
			if(Flash_deboInventario_desdeTablet.exists() == false)
				Flash_deboInventario_desdeTablet.mkdir();
			
			if(Flash_deboInventario_maeTablet.exists() == false)
				Flash_deboInventario_maeTablet.mkdir();
			
			if(Flash_deboInventario_logTablet.exists() == false)
				Flash_deboInventario_logTablet.mkdir();
			
			if(Flash_deboInventario_logDatos.exists() == false)
				Flash_deboInventario_logDatos.mkdir();
			
			if(url_carpeta_usb.exists() == false)
				url_carpeta_usb.mkdir();
			
			if(url_carpeta_usb_import.exists() == false)
				url_carpeta_usb_import.mkdir();
			
			if(url_carpeta_usb_export.exists() == false)
				url_carpeta_usb_export.mkdir();
			
						
		} catch (Exception e) {
			log.log(e.toString(), 4);
			
		}			
			

		
		// if (carpeta_de_fotos.exists() == false) {
		// carpeta_de_fotos.mkdir();
		// }

		// 3 Otro control horario en caso que la fecha sea muy incoherente
		if (ControlHora.control_horario() == false) {
			showSimpleDialogOK("PROBLEMA DE HORARIO", ControlHora.mensaje())
					.show();

			log.log("[-- 247 --]" + "Segunda Verificacion de hora", 2);
		}
		
		// 4 Control URL de conexin al servidor:
		PreferenciasInventario.cargarPreferencias(ctxt);
		try {
			log.log("[-- 319 --]" + "Se llama a cargar PReferencias, para setear los datos del Preferes en las constantes",3);
			URLValidator.esValidaEstaURL(Parametros.PREF_URL_CONEXION_SERVIDOR);
		} catch (ExceptionHttpExchange e) {
			log.log("[-- 309 --]" + e.toString(),4);
			e.printStackTrace();
			showSimpleDialogOK("ERROR - URL conexion al servidor", e.toString())
					.show();

			log.log("[-- 259 --]" + "ERROR- url conexion con el servidor", 0);
		}

		// 5 Creacin de los elementos graficos de la pgina:
		boton_continuar = (Button) findViewById(R.id.DI_boton_izq);
		boton_empezar = (Button) findViewById(R.id.DI_boton_cen);

		// Creacin de los elementos graficos de la pgina:
		boton_compras = (Button) findViewById(R.id.boton_compras);

		boton_salir = (ImageView) findViewById(R.id.DI_imagen_salir);
		boton_salir.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contador_reset_BDD = 3;

				log.log("[-- 272 --]" + "Salir de la aplicacion", 0);
				finish();
			}
		});

		boton_config = (ImageView) findViewById(R.id.DI_imagen_config);
		boton_config.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contador_reset_BDD = 3;

				Intent intentPreferencias = new Intent(DeboInventario.this,
						PreferenciasInventario.class);
				startActivityForResult(intentPreferencias,
						Parametros.REQUEST_PREFERENCIAS);

				log.log("[-- 288 --]" + "Ingresa a Configuraciones", 0);
			}
		});

		boton_actualizar = (ImageView) findViewById(R.id.DI_imagen_update);
		boton_actualizar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				popupStart();

				log.log("[-- 298 --]"
						+ "Ingresa a la actuzalizacion por maestros", 2);

				ArrayList<String> lista_opciones = new ArrayList<String>();
				lista_opciones.add(ParametrosInventario.tabla_referencias);

				View.OnClickListener listenerPositivoWifi = new View.OnClickListener() {

					public void onClick(View view) {
						if (lista_actualizaciones_programadas.size() > 0) {
							// Arrancamos el thread:
							System.out.println("::: 361 DeboInventario WIFI");
							activarWifi();

							Intent intentActualizacion = new Intent(
									DeboInventario.this, WiFiControlador.class);
							startActivityForResult(intentActualizacion,
									Parametros.REQUEST_WIFI);

							dialogoActualizacion.dismiss();
							log.log("[-- 316 --]" + "Actuzalicacion por Wi fi",
									0);
						

						} else {
							dialogoActualizacion.cancel();
							popupEnd();

							log.log("[-- 322 --]"
									+ "Actualicacion por Wifi cancelada", 2);
						}
					}
				};

				View.OnClickListener listenerPositivoUsb = new View.OnClickListener() {

					public void onClick(View view) {
						if (lista_actualizaciones_programadas.size() > 0) {
							// Control de la presencia de un o ms archivos
							// "update_...":
							// TODO a terminar...
							// popupStart();

							// Actualizacion por Pen Drive USB:
							log.log("[-- 337 --]" + "Actuzalizacion por usb", 2);
							
							CargarDatosUsb unaCargaUsb = new CargarDatosUsb(1);
							unaCargaUsb.execute(ctxt);
							dialogoActualizacion.dismiss();
						} else {
							dialogoActualizacion.cancel();
							popupEnd();
							log.log("[-- 345 --]"
									+ "Actuzalizacion por usb Cancelada", 2);
						}
					}
				};

				View.OnClickListener listenerPositivoFlash = new View.OnClickListener() {

					public void onClick(View view) {
						if (lista_actualizaciones_programadas.size() > 0) {
							// Control de la presencia de un o ms archivos
							// "update_...":
							// TODO a terminar...
							// popupStart();

							// Actualizacion por Pen Drive USB:

							log.log("[-- 361 --]" + "Actuzalizacion por flash",
									2);
							CargarDatosUsb unaCargaUsb = new CargarDatosUsb(2);
							unaCargaUsb.execute(ctxt);
							dialogoActualizacion.dismiss();
						} else {

							log.log("[-- 367 --]"
									+ "Actuzalizacion por flash cancelada", 2);
							dialogoActualizacion.cancel();
							popupEnd();
						}
					}
				};

				View.OnClickListener listenerPositivoSdcard = new View.OnClickListener() {

					public void onClick(View view) {
						if (lista_actualizaciones_programadas.size() > 0) {
							// Control de la presencia de un o ms archivos
							// "update_...":
							// TODO a terminar...
							// popupStart();

							// Actualizacion por Pen Drive USB:

							log.log("[-- 385 --]" + "Actuzalizacion por sdcard",
									2);
							CargarDatosUsb unaCargaUsb = new CargarDatosUsb(3);
							unaCargaUsb.execute(ctxt);
							dialogoActualizacion.dismiss();
						} else {
							log.log("[-- 390 --]"
									+ "Actuzalizacion por sdcard cancelada", 2);
							dialogoActualizacion.cancel();
							popupEnd();
						}
					}
				};

				View.OnClickListener listenerNegativo = new View.OnClickListener() {

					public void onClick(View view) {
						dialogoActualizacion.cancel();
						popupEnd();
					}
				};

				dialogoActualizacion = new DialogPersoComplexActualizacionWifiUsb(
						ctxt,
						lista_actualizaciones_programadas,
						"Actualizacion de Maestros",
						"Usted esta a punto de actualizar el maestro general de articulos.\n"
								+ "(!Cuidado! Los datos guardados se actualizaran)\n",// +
						// "Seleccione los registros que desea actualizar:",
						DialogPerso.ALERTAR, lista_opciones,
						listenerPositivoWifi, listenerPositivoUsb,
						listenerPositivoFlash, listenerPositivoSdcard,
						listenerNegativo);
				CheckBox check = (CheckBox) dialogoActualizacion
						.findViewById(0);
				check.setChecked(true);
				check.setVisibility(View.INVISIBLE);
				dialogoActualizacion.show();
				log.log("[-- 421 --]"
						+ "Inicio de pop up de Actualizacion de maestros", 1);

				// dialogAguarde=new ProgressDialog(ctxt);
				// dialogAguarde.setMessage("Esta operacion puede tardar varios minutos. Aguarde por favor");
				//

			}
		});

		image_debo = (ImageView) findViewById(R.id.DI_imagen_debo);
		image_debo.setImageDrawable(getResources().getDrawable(
				R.drawable.icono_inventario_sub));

		image_debo.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {

				log.log("[-- 438 --]"
						+ "Long clic para resetear la base de datos", 0);
				contador_reset_BDD--;

				if (contador_reset_BDD <= 0) {
					BaseDatos bdd = new BaseDatos(ctxt);
					try {
						bdd.borrarInventariosYArticulosEnBDD_y_tambien_locales();
						bdd.reiniciarArticulosInventarios();

						log.log("[-- 447 --]"
								+ "Inicio del Reseteo de Base de Datos", 2);
					} catch (ExceptionBDD e) {

						e.printStackTrace();
						log.log("[-- 451 --]" + e.toString(), 4);

					}
					Toast.makeText(
							ctxt,
							"(---Recovery---) Base de datos RESETEADA (---Recovery---)",
							Toast.LENGTH_LONG).show();
					contador_reset_BDD = 3;
				}
				return true;
			}
		});

		version = (TextView) findViewById(R.id.DI_version);
		idtablet = (TextView) findViewById(R.id.DI_idtablet);

		boton_continuar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contador_reset_BDD = 3;

				Intent intentInventario = new Intent(ctxt,
						InventarioMainBoard.class);
				startActivity(intentInventario);
				log.log("[-- 475 --]"
						+ "Pasa a la pantalla de InventarioMainBOard", 0);
				// finish();
			}
		});


		boton_compras.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				//contador_reset_BDD = 3;
				Intent intentCompras = new Intent(ctxt,
						ComprasMainBoard.class);
				startActivity(intentCompras);
				log.log("[-- 475C --]"
						+ "Pasa a la pantalla de InventarioMainBOard", 0);
				// finish();
			}
		});

		boton_empezar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contador_reset_BDD = 3;

				Intent intentWifi = new Intent(DeboInventario.this,
						WiFiControlador.class);
				startActivityForResult(intentWifi, Parametros.REQUEST_WIFI);

				log.log("[-- 489 --]" + "Abre la pantalla se wifi controlador",
						0);
			}
		});

				version.setText("Version " + ParametrosInventario.VERSION);
		idtablet.setText("Terminal n " + Parametros.PREF_NUMERO_DE_TERMINAL);

		BaseDatos bdd = new BaseDatos(ctxt);
		try {
			if (bdd.selectArticulosCodigosEnBdd() == null) {
				// boton_continuar.setEnabled(false); TEMPORARIO PARA TEST
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				boton_continuar.setEnabled(true);
			} else {
				boton_empezar.setEnabled(true);
			}
		} catch (ExceptionBDD e) {
			e.printStackTrace();

			log.log("[-- 508 --]" + e.toString(), 4);
		}

	}

	/**
	 * Metodo que se llama automaticamente cuando termina una activity llamada
	 * por esta activity en este caso pueden ser las preferecnias o la pantalla
	 * de WIFI:
	 * <p>
	 * 1 Si volvimos de las preferencias actualiza la version y n tablet
	 * <p>
	 * 2 Si volvimos de WIFI con todo ok procedemos a cargar referenciaas por
	 * WIFI
	 * <p>
	 * 3 Si volvimos de WIFI con errores se desactiva el popUp y el wifi
	 */

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ParametrosInventario.REQUEST_CODIGO_BARRA
				&& resultCode == RESULT_OK) {

		}
		// 1 Si volvimos de las preferencias actualiza la version y n tablet
		else if (requestCode == Parametros.REQUEST_PREFERENCIAS) {
			version.setText("Version n " + ParametrosInventario.VERSION);
			idtablet.setText("Terminal n "
					+ Parametros.PREF_NUMERO_DE_TERMINAL);
		}
		/*
		 * else if (requestCode == Parametros.REQUEST_WIFI && resultCode ==
		 * RESULT_OK) { Intent i = new Intent(DeboInventario.this,
		 * SeleccionInventarios.class); startActivity(i); finish(); }
		 * 
		 * else if (requestCode == Parametros.REQUEST_WIFI && resultCode ==
		 * RESULT_CANCELED) {
		 * showSimpleDialogSiNo("Error de conexin a la red WiFi",
		 * "No se pudo establecer una conexin con la red WiFi.\n\n Usted desea cargar inventarios con un Pen Drive USB?"
		 * , UsbProvider.class).show(); }
		 */
		// 2 Si volvimos de WIFI con todo ok procedemos a cargar referenciaas
		// por WIFI
		else if (requestCode == Parametros.REQUEST_WIFI
				&& resultCode == RESULT_OK) {
			CargarDatosWifi unaCarga = new CargarDatosWifi();
			unaCarga.execute(ctxt);
		}
		// 3 Si volvimos de WIFI con errores se desactiva el popUp y el wifi
		else if (requestCode == Parametros.REQUEST_WIFI
				&& resultCode == RESULT_CANCELED) {
			popupEnd();
			desactivarWifi();

		} else if (requestCode == Parametros.REQUEST_USB) {
			popupEnd();
		}
	}

	/**
	 * Metodo para mostrar un dialog con titulo y mensaje
	 */

	public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {
		log.log("[-- 573 --]" + "titulo: " + titulo + ", \n mensaje: "
				+ mensaje, 3);
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.dismiss();
						// popupCarga.dismiss();

					}
				});
		AlertDialog alert = dialogoSimple.create();
		return alert;

	}

	/**
	 * Muestra un mensaje y da la opcion de responder Si o No
	 */

	public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje,
			Class<?> clase) {

		log.log("[-- 596 --]" + "titulo: " + titulo + ", \n mensaje: "
				+ mensaje, 3);

		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple
				.setCancelable(false)
				.setTitle(titulo)
				.setMessage(mensaje)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {

					public void onClick(@NonNull DialogInterface dialog, int id) {
						Intent intentBegining = new Intent(DeboInventario.this,
								UsbProvider.class);
						intentBegining.putExtra(Parametros.extra_uri_usb,
								ParametrosInventario.CARPETA_ATABLET);
						startActivity(intentBegining);
						dialog.dismiss();
						finish();
						log.log("[-- 613 --]" + "Acepto si no", 3);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.cancel();
						log.log("[-- 620 --]" + "cancelo sino", 3);
					}
				});
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}

	/**
	 * Inicia el un popUp que muestra el progreso de la importacion
	 */
	private void popupStart() {
		popupCarga = new ProgressDialog(ctxt);
		popupCarga.setCancelable(false);
		popupCarga
				.setMessage("Importando los datos de los articulos de referencia...");
		popupCarga.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		popupCarga.setProgress(0);
		popupCarga.setMax(100);
		popupCarga.show();

		log.log("[-- 640 --]" + "Pop up iniciado", 2);
	}

	/**
	 * Sube el porcentaje del popUp hasta un valor porciento
	 * 
	 * @param hastaXPorciento
	 */
	private void popupSubir(int hastaXPorciento) {
		popupCarga.setProgress(hastaXPorciento);
	}

	/**
	 * Cierra el popUp de progreso de la carga
	 */
	private void popupEnd() {
		popupCarga.dismiss();
	}

	/**
	 * Activa el WIFI
	 */

	public void activarWifi() {
//		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//		ConnectivityManager wifiConexion = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//		NetworkInfo wifiInfo = wifiConexion
//				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//		wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//		if (wifiInfo.isConnected() == false) {
//
//			Settings.System.putInt(ctxt.getContentResolver(),
//					Settings.System.AIRPLANE_MODE_ON, 0);
//			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//			intent.putExtra("state", false);
//			sendBroadcast(intent);
//
//			wifiManager.setWifiEnabled(true);
//		}
//
//		log.log("[-- 681 --]" + "Se inicia el Wi fi", 2);
	}

	/**
	 * Desactiva el WIFI
	 */

	public void desactivarWifi() {
//		if (estaEnModoAvion() == false) {
//			// toggle airplane mode
//			Settings.System.putInt(ctxt.getContentResolver(),
//					Settings.System.AIRPLANE_MODE_ON, 1);
//
//			// Post an intent to reload
//			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//			intent.putExtra("state", true);
//			sendBroadcast(intent);
//
//			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//			wifiManager.setWifiEnabled(false);
//		}
//
//		log.log("[-- 703 --]" + "desactiva el Wi fi", 2);
	}

	/**
	 * Verifica si la tablet esta en modo avin
	 */

	public boolean estaEnModoAvion() {
		return (Settings.System.getInt(ctxt.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) == 1);

	}

	/**
	 * Clase interna para cargar los datos por WIFI
	 * 
	 * @author GuillermoR
	 * 
	 */
	protected class CargarDatosWifi extends AsyncTask<Context, Integer, String> {
		/**
		 * Tarea que se ejecuta en background cuando se llama al metodo
		 * execute():
		 * <p>
		 * 1 Borramos los datos de las referencias
		 * <p>
		 * 2 Recuperamos las referencias del webservice por partes
		 * <p>
		 * 3 Download de los subpaquetes
		 * <p>
		 * 4 Cargamos en el hashmap leido el sub hashmap
		 * <p>
		 * 5 Recuperamos los detalles de esta referencia y generamos la SQL
		 * <p>
		 * 6 Insertamos las referencias en la BD
		 * 
		 */

		@Nullable
        protected String doInBackground(Context... arg0) {
			log.log("[-- 762 --]" + "Comienza la carga de Datos por Wifi", 2);
			System.out.println("::: DeboInventario 794 Carga datos por wifi");
			// Registro.log(ParametrosSancion.URL_ARCHIVO_LOG, new Date(),
			// nombreClase, "", "Cargar datos 0%");

			popupSubir(5);

			BaseDatos BDD = new BaseDatos(ctxt);

			popupSubir(10);

			// --------------------------------------------------
			// Recuperamos todos los articulos, y los cargamos:
			// --------------------------------------------------
			if (lista_actualizaciones_programadas
					.contains(ParametrosInventario.tabla_referencias) == true) {
				// 1 Borramos los datos de las referencias
				try {
					BDD.borrarDatosBDD(ParametrosInventario.tabla_referencias,
							false);
				} catch (ExceptionBDD e1) {
					e1.printStackTrace();
					log.log("[-- 764 --]" + e1.toString(), 4);
					Toast.makeText(
							ctxt,
							"Error en BORRAR los datos de ARTICULOS en la base de datos",

							Toast.LENGTH_LONG).show();
				}

				// 2 Recuperamos las referencias del webservice:
				HashMap<String, String> subHashmapConfiguraciones = new HashMap<String, String>();

				ArrayList<HashMap<String, HashMap<String, String>>> lista_sub_hashmap = new ArrayList<HashMap<String, HashMap<String, String>>>();
				HashMap<String, HashMap<String, String>> subHashmapReferencias = new HashMap<String, HashMap<String, String>>();

				ArrayList<HashMap<String, HashMap<String, String>>> lista_sub_prov_hashmap = new ArrayList<HashMap<String, HashMap<String, String>>>();
				HashMap<String, HashMap<String, String>> subHashmapProveedores = new HashMap<String, HashMap<String, String>>();

				int cantidad_referencias = 0;
//				int cantidad_proveedores = 0;
				try {
					subHashmapConfiguraciones = HttpReader.readConfiguraciones();
					boolean mostrarExistencia = Boolean.parseBoolean(subHashmapConfiguraciones.get(ParametrosInventario.tablet_mostrar_existencia));
					SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(ctxt);
					SharedPreferences.Editor edit = setting.edit();
					edit.putBoolean(ParametrosInventario.tablet_mostrar_existencia, mostrarExistencia);
					edit.commit();
					cantidad_referencias = HttpReader.readCantidadReferencias();
//					cantidad_proveedores = HttpReader.readCantidadProveedores();
					Log.v("yo", "Cantidad referencias = "
							+ cantidad_referencias);
				} catch (ExceptionHttpExchange e1) {
					e1.printStackTrace();

					log.log("[-- 784 --]" + e1.toString(), 4);
					return "Error: imposible obtener la cantidad de referencias disponibles: "
							+ e1.getMessage();
				}
				int indice = 1;
				int talla_sub_paquetes = ParametrosInventario.TAMANO_PAQUETES_XML;
				boolean stop = false;

				// Vamos a leer los residentes por paquetes de 100:
				do {
					int contador = 4;

					// 3 Download de los subpaquetes:
					if ((indice + talla_sub_paquetes - 1) < cantidad_referencias) {
						do {
							try {
								subHashmapReferencias = HttpReader
										.readParteReferencias(indice, indice
												+ talla_sub_paquetes - 1);

								subHashmapProveedores = HttpReader
										.readParteProveedores(indice, indice
												+ talla_sub_paquetes - 1);


							} catch (ExceptionHttpExchange e) {
								subHashmapReferencias = new HashMap<String, HashMap<String, String>>();

								log.log("[-- 806 --]" + e.toString(), 4);
							}
							contador--;
						} while (subHashmapReferencias.size() <= 0
								&& contador > 0);
						indice = indice + talla_sub_paquetes;
					} else {
						do {
							try {
								subHashmapReferencias = HttpReader
										.readParteReferencias(indice,
												cantidad_referencias);
								subHashmapProveedores = HttpReader
										.readParteProveedores(indice,
												1185);

							} catch (ExceptionHttpExchange e) {
								subHashmapReferencias = new HashMap<String, HashMap<String, String>>();
								log.log("[-- 820 --]" + e.toString(), 4);
							}
							contador--;
						} while (subHashmapReferencias.size() <= 0
								&& contador > 0);
						stop = true;
					}

					// 4 Cargamos en el hashmap leido el sub hashmap:
					lista_sub_hashmap.add(subHashmapReferencias);
					lista_sub_prov_hashmap.add(subHashmapProveedores);
					popupSubir((10 + ((50 - 10) * indice / cantidad_referencias)));

				} while (stop == false);

				try {
					// En un primer tiempo se parsea el hashmap en un arraylist:
					ArrayList<String> listaSQL = new ArrayList<String>();
					int jndice = 0;

					for (HashMap<String, HashMap<String, String>> sub_hashmap : lista_sub_hashmap) {
						for (String codes : sub_hashmap.keySet()) {
							// 5 Recuperamos los detalles de este residente:
							HashMap<String, String> hashmapDetallesReferencia = sub_hashmap
									.get(codes);
System.out.println("::: DeboInventario 895 aca hace el insert en referencias");
							// Creamos la consulta SQL de insersin:
							String sql = "INSERT OR REPLACE INTO "
									+ ParametrosInventario.tabla_referencias
									+ " VALUES("
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_sector)
									+ ", "
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_codigo)
									+ ", "
										+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_balanza)
									+ ", "
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_decimales)
									+ ", "
                                                                /*Damian*/
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_existencia_venta)
									+ ", "
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_existencia_deposito)
									+ ", "
									+ "'"
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_codigo_barra)
									+ "', "
									+ "'"
									+ hashmapDetallesReferencia
									.get(ParametrosInventario.bal_bdd_referencia_codigo_barra_completo)
									+ "', "
									+ "'"
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_descripcion)
									+ "', "
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_precio_venta)
									+ ", "
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_precio_costo)
									+ ", "
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_depsn)
									+ ", "
									+ "'"
									+ hashmapDetallesReferencia
											.get(ParametrosInventario.bal_bdd_referencia_foto)
									+ "'" + ")";

							listaSQL.add(sql);
							Log.v("Almacena", sql);
							popupSubir(50 + (int) Math
									.floor((double) (100 - 50)
											* (double) jndice
											/ cantidad_referencias));
							jndice++;
							hashmapDetallesReferencia.clear();
						}
					}
					// 6 Insertamos las referencias en la BD
					BDD.insertReferenciasConSQLEnBdd(listaSQL);
					subHashmapReferencias.clear();
				} catch (ExceptionBDD e) {
					return e.toString();

				}

				try {
					// En un primer tiempo se parsea el hashmap en un arraylist:
					ArrayList<String> listaSQL = new ArrayList<String>();
					int jndice = 0;

					for (HashMap<String, HashMap<String, String>> sub_prov_hashmap : lista_sub_prov_hashmap) {
						for (String codes : sub_prov_hashmap.keySet()) {
							// 5 Recuperamos los detalles de este residente:
							HashMap<String, String> hashmapDetallesProveedores = sub_prov_hashmap
									.get(codes);
							System.out.println("::: DeboInventario 895 aca hace el insert en proveedores");

							// Creamos la consulta SQL de insersin:
							String sql = "INSERT OR REPLACE INTO "
									+ ParametrosInventario.tabla_proveedores
									+ " VALUES("
									+ hashmapDetallesProveedores
									.get(ParametrosInventario.bal_bdd_proveedores_codigo)
									+ ",' "
									+ hashmapDetallesProveedores
									.get(ParametrosInventario.bal_bdd_proveedores_descripcion)
									+ "')";

							listaSQL.add(sql);
							Log.v("Almacena", sql);
						/*	popupSubir(50 + (int) Math
									.floor((double) (100 - 50)
											* (double) jndice
											/ cantidad_referencias));
						*/	jndice++;
							hashmapDetallesProveedores.clear();
						}
					}
					// 6 Insertamos los proveedores en la BD
					BDD.insertProveedoresConSQLEnBdd(listaSQL);
					BDD.deleteProveedoresVacios();
					subHashmapProveedores.clear();
				} catch (ExceptionBDD e) {
					return e.toString();

				}

				// BDD.cargarResidentesTest();
			}

			popupSubir(100);
			return null;
		}

		/**
		 * Metodo que se llama automaticamente luego de ejecutarse la tarea
		 * programada Muestra un mensaje de resultado de la transaccin
		 */

		protected void onPostExecute(@Nullable String result) {
			super.onPostExecute(result);
			popupEnd();

			if (result == null) {
				desactivarWifi();
				showSimpleDialogOK("Actualizacion exitosa",
						"La actualizacion se realizo con exito").show();
				// Cierre ventana si es usb
			} else {
				showSimpleDialogOK("Actualizacion cancelada",
						"La actualizacion ha sido cancelada.\n\n" + result)
						.show();
			}
			// Registro.log(ParametrosSancion.URL_ARCHIVO_LOG, new Date(),
			// nombreClase, "", "Cargar datos 100%");
		}
	}

	/**
	 * Tarea asincronica para cargar los datos desde un dispositivo
	 * 
	 * @author GuillermoR
	 * 
	 */
	protected class CargarDatosUsb extends AsyncTask<Context, Integer, String> {
		/**
		 * Metodo principal que se ejecuta en backgground, logica de cargado de
		 * datos 1 Borramos los datos de las referencias 2 zor por cada
		 * archivo en el directorio &nbsp; &nbsp;2.1 Leer las referencias del
		 * archivo &nbsp; &nbsp;2.2 Recuperamos los detalles de este articulo
		 * &nbsp; &nbsp;2.3 Creamos la consulta SQL de insersion &nbsp;
		 * &nbsp;2.4 Insertar las sentencias en la BD
		 */

        @NonNull
        String mensajeMaestros = ""; // variable donde se coloca los mensajes de
										// error o de exito
		String urlMaestro = ""; // variable que se carga con la direccion de
								// almacenamiento

		// Polimorfismo se crea un constructo con paso de parametros, para saber
		// si viene del boton USB(1), SDCARD(2), FLASH(3)
		public CargarDatosUsb(int parametro) {
			// se comprueba que boton ha sido pulsado y se procede a configura
			// rlas variables de control
			switch (parametro) {
//			case 1:
//				urlMaestro = ParametrosInventario.PREF_USB_IMPORT_MAESTRO;
//				unidad_almacenamiento = ParametrosInventario.UNIDAD_ALMACENAMIENTO_UDISK;
//				unidad_numerica = 1;
//				break;
//			case 2:
//				urlMaestro = ParametrosInventario.PREF_FLASH_IMPORT_MAESTRO;
//				unidad_almacenamiento = ParametrosInventario.UNIDAD_ALMACENAMIENTO_FLASH;
//				unidad_numerica = 2;
//				break;
			case 3:
				urlMaestro = ParametrosInventario.CARPETA_MAETABLET;
				unidad_almacenamiento = ParametrosInventario.UNIDAD_ALMACENAMIENTO_SDCARD;
				unidad_numerica = 3;
				break;

			}

		}

		@Nullable
        protected String doInBackground(Context... arg0) {

			log.log("[-- 972 --]"
					+ "Comienza la carga de datos por Dispositivo", 2);
			log.log("[-- 973 --]" + "Dispositivo: " + unidad_almacenamiento, 2);
			log.log("[-- 974 --]" + "UrlMaestro: " + urlMaestro, 2);
			// se crean las variables de control de las rutas
			File carpeta = new File(urlMaestro);
			File unidad = new File(unidad_almacenamiento);
			// si comprueba si existe la unidad antes que nada , sino se informa
			// la unidad no esta conectada y que unidad.
			if (unidad.exists()) {
				// se comprueba si las carpetas estan creadas, si lo estan se
				// procede a borrar maestros y actualizar
				if (carpeta.exists()) {
					// popupSubir(1);

					BaseDatos BDD = new BaseDatos(ctxt);

					// popupSubir(10);

					// --------------------------------------------------
					// Recuperamos todos los articulos, y los cargamos:
					// --------------------------------------------------
					if (lista_actualizaciones_programadas
							.contains(ParametrosInventario.tabla_referencias) == true) {
						// 1 Borramos los datos de las referencias
						try {
							BDD.borrarDatosBDD(
									ParametrosInventario.tabla_referencias,
									false);
						} catch (ExceptionBDD e1) {
							e1.printStackTrace();
							log.log("[-- 1002 --]" + e1.toString(), 4);
							Toast.makeText(
									ctxt,
									"Error en BORRAR los datos de ARTICULOS en la base de datos",
									Toast.LENGTH_LONG).show();
						}
						// Recuperamos los ARTICULOS desde los archivos:
						ArrayList<HashMap<String, HashMap<String, String>>> lista_sub_hashmap = new ArrayList<HashMap<String, HashMap<String, String>>>();
						HashMap<String, HashMap<String, String>> HashmapReferencias = new HashMap<String, HashMap<String, String>>();

						ArrayList<String> listaSQL;// = new ArrayList<String>();

						if (ParametrosInventario.MODO_DEBUG) {
							urlMaestro = "/data/data/com.foca.deboInventario/usb/maeTablet/";
						}

						// 2 For por cada archivo en el directorio
						File carpeta_fuente = new File(urlMaestro);

						try {
							if (carpeta_fuente.exists()) {
								int cantidadArchivos = carpeta_fuente
										.listFiles().length;
								int deltaSubir = 100 / cantidadArchivos;
								if (deltaSubir == 0) {
									deltaSubir = 1;
								}

								int lastValueSubir = 0;

								if (carpeta_fuente.listFiles().length > 0) {
									for (File archivo : carpeta_fuente
											.listFiles()) {

										listaSQL = new ArrayList<String>();
										// 2.1 Leer las referencias del archivo
										// try {
										HashmapReferencias = HttpReader
												.readReferenciasVariosUSB(ctxt,
														archivo);
										// } catch (ExceptionHttpExchange e1) {
										// // TODO Auto-generated catch block
										// return e1.toString();
										// }
										log.log("cantidad de elementos del hasmap", 3);

										for (String codes : HashmapReferencias.keySet()) {
											// 2.2 Recuperamos los detalles de
											// este residente:
											HashMap<String, String> hashmapDetallesReferencia = HashmapReferencias
													.get(codes);

											// 2.3 Creamos la consulta SQL de
											// insersin:
											String sql = "INSERT OR REPLACE INTO "
													+ ParametrosInventario.tabla_referencias
													+ " VALUES("
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_sector)
													+ ", "
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_codigo)
													+ ", "
                                                                                                /*Damian*/
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_existencia_venta)
													+ ", "
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_existencia_deposito)
													+ ", "
													+ "'"
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_codigo_barra)
													+ "', "
													+ "'"
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_descripcion)
													+ "', "
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_precio_venta)
													+ ", "
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_precio_costo)
													+ ", "
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_depsn)
													+ ", "
													+ "'"
													+ hashmapDetallesReferencia
															.get(ParametrosInventario.bal_bdd_referencia_foto)
													+ "'" + ")";
											System.out.println("::: DeboInventario 1155 Inserta Referencia ");
											System.out.println(sql);
											listaSQL.add(sql);
											Log.v("yo y yo", sql);
											
											log.log("[-- 1150 --]" + sql + "hasmap: " + HashmapReferencias.size(),3);
											
											hashmapDetallesReferencia.clear();
										}
										
										HashmapReferencias.clear();

										try {
											// 2.4 Insertar las sentencias en la
											// BD
											BDD.insertReferenciasConSQLEnBdd(listaSQL);
											popupSubir(lastValueSubir
													+ deltaSubir);
											lastValueSubir = lastValueSubir
													+ deltaSubir;
										} catch (ExceptionBDD e) {
											log.log("[-- 1100 --]"
													+ e.toString(), 4);
											return e.toString();
										}
										// popupSubir(lastValueSubir+deltaSubir);
										// lastValueSubir=lastValueSubir+deltaSubir;
										// lista_sub_hashmap.add(HashmapReferencias);
										mensajeMaestros = "La actualizacion se realizo con exito";// error
																									// para
																									// desarrollo
									}
								} else {
									popupEnd();
									showSimpleDialogOK(
											"Problema al Cargar las referencias",
											"else 2").show();// error para
																// desarrollo

								}
							} else {

								popupEnd();
								showSimpleDialogOK(
										"Problema al Cargar las referencias",
										"else 2").show();// error para
															// desarrollo
							}
						} catch (Exception exception) {
							mensajeMaestros = "Compruebe que la carpeta este creada :"
									+ "\n \"/deboInventario/maeTablet/\" \n error de catch";// error
																							// para
																							// desarrollo
							log.log("[-- 1132 --]" + exception.toString(), 4);

						}

						// En un primer tiempo se parsea el hashmap en un
						// arraylist:
						// ArrayList<String> listaSQL = new ArrayList<String>();
						//
						// for (HashMap<String,HashMap<String,String>>
						// sub_hashmap :
						// lista_sub_hashmap) {
						//
						// for (String codes : HashmapReferencias.keySet()) {
						// // Recuperamos los detalles de este residente:
						// HashMap<String,String> hashmapDetallesReferencia =
						// sub_hashmap.get(codes);
						//
						// // Creamos la consulta SQL de insersin:
						// String sql = "INSERT OR REPLACE INTO " +
						// ParametrosInventario.tabla_referencias + " VALUES(" +
						// hashmapDetallesReferencia.get(ParametrosInventario.bal_bdd_referencia_sector)
						// + ", " +
						// hashmapDetallesReferencia.get(ParametrosInventario.bal_bdd_referencia_codigo)
						// + ", " +
						// "'" +
						// hashmapDetallesReferencia.get(ParametrosInventario.bal_bdd_referencia_codigo_barra)
						// + "', " +
						// "'" +
						// hashmapDetallesReferencia.get(ParametrosInventario.bal_bdd_referencia_descripcion)
						// + "', " +
						// hashmapDetallesReferencia.get(ParametrosInventario.bal_bdd_referencia_precio_venta)
						// + ", " +
						// hashmapDetallesReferencia.get(ParametrosInventario.bal_bdd_referencia_precio_costo)
						// + ", " +
						// "'" +
						// hashmapDetallesReferencia.get(ParametrosInventario.bal_bdd_referencia_foto)
						// + "'" +
						// ")";
						//
						// listaSQL.add(sql);
						// Log.v("yo", sql);
						// hashmapDetallesReferencia.clear();
						// }
						// }
						// try {
						// BDD.insertReferenciasConSQLEnBdd(listaSQL);
						// } catch (ExceptionBDD e) {
						// return e.toString();
						// }
						// BDD.cargarResidentesTest();
					}

					popupSubir(100);

					return null;
					// si las carpetas no existen, se le comunica la ruta
				} else {
					mensajeMaestros = "Compruebe que la carpeta este creada :"
							+ "\n \"/deboInventario/maeTablet/\" ";
							
					return null;
				}

			} else {
				// se comprueba que tipo de boton se pulso(la variable
				// "unidad_numerica" quedo configurada anteriormente), y se
				// comunica que dispositivo o memoria no se encuentra conectada.
				switch (unidad_numerica) {
				case 1:
					mensajeMaestros = "EL PEN DRIVE no se encuentra conectado";
					break;
				case 2:
					mensajeMaestros = "No se tiene acceso a la memoria FLASH";
					break;
				case 3:
					mensajeMaestros = "La Tarjeta de Memoria no se encuentra conectada";
					break;

				}

				return null;

			}
		}

		/**
		 * A ejecutarse despues de finalizada la tarea on background Muestra
		 * mensajes de resultado de la actualizacin
		 */

		protected void onPostExecute(@Nullable String result) {
			super.onPostExecute(result);
			// popupEnd();
			popupCarga.dismiss();
			if (result == null) {//
				// Cerrar la ventana
				// dialogAguarde.dismiss();
				// popupEnd();
				showSimpleDialogOK("Resultado de la actualizacion",
						mensajeMaestros).show();
				//
			} else {
				// popupEnd();
				showSimpleDialogOK("Actualizacion cancelada",
						"La actualizacion ha sido cancelada.\n\n" + result)
						.show();
				unidad_numerica = 0;
			}
		}
	}
}
