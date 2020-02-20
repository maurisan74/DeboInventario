package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Activity que muestra los inventarios din�micos y comunes en curso para
 * llevarlos a cabo, exportarlos o importar nuevos.
 * 
 * @author GuillermoR
 * 
 */
public class InventarioMainBoard extends Activity implements DialogPersoSimple,
		Wifi {

	// *************************
	// *************************
	// **** ATRIBUTOS ****
	// *************************
	// *************************
	//
	/**
	 * Datos del contexto de la actividad
	 */
	@NonNull
    private Context ctxt = this;

	private CheckBox CheckBorrar;

	private boolean borrar;
	/**
	 * Instancia de un administrador de Base de datos
	 */
	private BaseDatos bdd;

	@NonNull
    BaseDatos bd = new BaseDatos(ctxt);
	/**
	 * Lista para almacenar cuales son los inventarios seleccionados con los que
	 * se trabajara
	 */
	@NonNull
    private ArrayList<Integer> listaInventariosSeleccionados = new ArrayList<Integer>();
	/**
	 * 
	 */
	@NonNull
    private HashMap<Integer, ArrayList<HashMap<String, Integer>>> matrizArticulosCadaInventario = new HashMap<Integer, ArrayList<HashMap<String, Integer>>>();
	/**
	 * Tabla prinicipal donde se muestran los inventarios actuales
	 */
	private TableLayout tablaPrincipal;
	/**
	 * Botones para importar y exportar los inventarios
	 */
	private Button botonExportar, botonImportar, Exportar_BD, Importar_BD;
	// private Button botonInvDinamico;
	/**
	 * Boton para volver a la pantalla inicial (icono : X )
	 */
	private ImageView botonSalir;
	/**
	 * Dialogos para mostrar opciones de importacion y exportacion para elegir
	 * los medios
	 */
	private DialogPersoComplexExport dialogoPrincipioExport,
			dialogoPrincipioImport;
	/**
	 * Dialogo de aviso para borrar inventarios comunes, se muestra cuando
	 * presionamos de forma prolongada un boton de un inventario com�n
	 */
	private DialogPersoComplexSiNo dialogoBorrarInventario;// ,
															// dialogoCreacionInvDinamico;
	/**
	 * Dialogo que se muestra para preguntar si desea seguir trabajando con el
	 * inventario dinamico actual o empezar de cero
	 */
	private DialogPersoComplexSiNo dialogoContinuarInventario;
	/**
	 * Dialogo para confirmar la decisi�n de borrar el inventario din�mico
	 * actual y crear uno nuevo desde cero
	 */
	private DialogPersoComplexSiNo dialogoBorrarInventarioDinamico;
	// private DialogPersoComplexSiNoOpcs dialogoCreacionInvDinamico;
	/**
	 * ProgresDialogs para mostrar progresos de procesos como exportacion e
	 * importacion
	 */
	private ProgressDialog popupCarga, popupEspera;

	/**
	 * Variables auxiliares
	 */
	private int pasoInventario;
	private double pasoArticulo;
	private double carga = (double) 0;

	private int inventarios_elegidos = 0;

	/**
	 * Dialog donde se informa que se han realizado todas las mediciones para
	 * que proceda a exportar
	 */
	private AlertDialog.Builder dialogoFin;

	boolean isEnabled;
	/**
	 * Variable para saber si hay que borrar despues de exportar
	 */
	boolean borrarDespues = false;

	// Agregados 3/5/2012
	/**
	 * Para manejo especial de inventario dinamico
	 */
	boolean hayDinamicos = false;
	/**
	 * HashMaps para manejo especial de los inventarios din�micos se guarda la
	 * informacion de los mismos ahi
	 */

	/**
	 * Variables de control para saber que tipo de unidad se tiene conectada
	 * tanto para esta clase como para la UsbProvider
	 */

	@NonNull
    public String unidad_final_import = "Dispositivo";

	@NonNull
    public String unidad_final_export = "Dispositivo";

	// se lanza al principio de la clase, para que quede confirurado para la
	// clase usbProvider
	// se verifica el inicio de la ruta configurada sea la correcta /udisk/,
	// /flash/,/sdcard/
	@NonNull
    public String TipoDispositivoImport() {

		// se retorna el resultado y se seteea para que este disponible en
		// cualquier momento
		ParametrosInventario.Dispositivo_Import = unidad_final_import;
		return unidad_final_import;
	}

	// idem anterior
	@NonNull
    public String TipoDispositivoExport() {

		ParametrosInventario.Dispositivo_Export = unidad_final_export;
		return unidad_final_export;
	}

	@NonNull
    private String Dispositivo_Import = TipoDispositivoImport();
	@NonNull
    private String Dispositivo_Export = TipoDispositivoExport();

	@Nullable
    HashMap<String, String> hashmapInventarioVenta;
	@Nullable
    HashMap<String, String> hashmapInventarioDeposito;
	
	// Parametro para mostrar inventarios de deposito o ventas
	private int condR = 0;

	/* Variables para saber si esta marcado Inventario por ventas o Inventario deposito */
	private RadioButton CheckedInventariosVentas;
	private RadioButton CheckedInventariosDeposito;

	// *****************************
	// *****************************
	// **** CONSTRUCTORES **********
	// *****************************
	// *****************************

	/**
	 * Se ejecuta al iniciar la activity
	 * <p>
	 * 1� Carga de la UI
	 * <p>
	 * 2� Refresh de la tabla principal
	 * <p>
	 * 3� HANDLERS de los botones
	 */

    @NonNull
    GestorLogEventos log = new GestorLogEventos();

	public void onCreate(Bundle savedInstanceState) {
		// 1� Carga de la UI
		// Creaci�n p�gina desde el documento XML
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_mainboard);

		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 240 --]" + "Inicia _Inventario Main Board", 2);

		// Recuperamos tabla:
		tablaPrincipal = (TableLayout) findViewById(R.id.IMB_tabla);

	    // Recuperamos los botones:
		botonExportar = (Button) findViewById(R.id.IMB_boton_exportar);
		botonSalir = (ImageView) findViewById(R.id.IMB_boton_salir);
		botonImportar = (Button) findViewById(R.id.IMB_boton_importar);
		Exportar_BD = (Button) findViewById(R.id.Exportar_BD);
		Importar_BD = (Button) findViewById(R.id.Importar_BD);
		CheckedInventariosVentas = (RadioButton) findViewById(R.id.CheckInventariosVentas);
		CheckedInventariosDeposito = (RadioButton) findViewById(R.id.CheckInventariosDepositos);

		Exportar_BD.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				log.log("Se presiono el boton Exportar BD", 3);
				String titulo = "EXPORTACION DE BASE DE DATOS";
				String mensaje = "Se exporto la base de datos";
				showSimpleDialogOK(titulo, mensaje).show();
				try {
					File sourceFile = new File(ParametrosInventario.URL_CARPETA_DATABASES + "DB_INVENT");
					File destFile = new File(ParametrosInventario.CARPETA_LOGDATOS + "DB_INVENT.sqlite");
					copyFile(sourceFile, destFile);
					Log.e("mensaje, sourceFile", sourceFile.toString());
					Log.e("mensaje, destFile", destFile.toString());
				} catch (Exception e) {
					log.log(e.toString(), 4);
					showSimpleDialogOK("EXPORTACION DE BASE DE DATOS", "Se interrumpio, intentelo nuevamente.\nSi el error persiste, reporte el archivo log a Servicio Tecnico").show();
				}
				log.log("Exportacion Realizada con exito", 3);
				return false;
			}

		});

		Importar_BD.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				log.log("Se presiono el boton Importar BD", 3);
				String titulo = "IMPORTACION DE BASE DE DATOS";
				String mensaje = "Se importo la base de datos";
				showSimpleDialogOK(titulo, mensaje).show();
				try {
					File destFile = new File(
							ParametrosInventario.URL_CARPETA_DATABASES
									+ "DB_INVENT");
					File sourceFile = new File(
							ParametrosInventario.CARPETA_LOGDATOS
									+ "DB_INVENT.sqlite");
					copyFile(sourceFile, destFile);
					Log.e("mensaje, sourceFile", sourceFile.toString());
					Log.e("mensaje, destFile", destFile.toString());
				} catch (Exception e) {
					log.log(e.toString(), 4);
					showSimpleDialogOK(
							"IMPORTACION DE BASE DE DATOS",
							"Se interrumpio, intentelo nuevamente.\n"
									+ "Si el error persiste, reporte el archivo log a Servicio Tecnico")
							.show();
				}

				log.log("Importacion Realizada con exito", 3);
				return false;
			}

		});

		log.log("Fin exportar ", 3);

		// Apagamos el Wifi:
		desactivarWifi();

		// 2 REFRESH DE LA PAGINA PRINCIPAL:
		try {
			refreshTablaPrincipal();
		} catch (ExceptionBDD e1) {

			log.log("[-- 260 --]" + e1.toString(), 2);
			e1.printStackTrace();
			Toast.makeText(ctxt, e1.toString(), Toast.LENGTH_LONG).show();
		} catch (Exception e1) {

			log.log("[-- 265 --]" + e1.toString(), 4);
			e1.printStackTrace();
			Toast.makeText(ctxt, e1.toString(), Toast.LENGTH_LONG).show();
		}

		// 3 HANDLERS de los botones:
		botonExportar.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {

				log.log("[-- 276 --]" + "Presiono Exportar", 0);
				BaseDatos bdd = new BaseDatos(ctxt);
				int numero_inventarios_cerrados = 0;
				ArrayList<Integer> inventarios_a_exportar ;
//String prueba = "";
				try {
				//	numero_inventarios_cerrados = bdd
				//			.selectInventariosCerradosEnBdd().size();

					inventarios_a_exportar = bdd.selectInventariosCerradosEnBdd();

					//	inventarios_elegidos = inventarios_a_exportar.get(0);
					numero_inventarios_cerrados = inventarios_a_exportar.size();
					if(numero_inventarios_cerrados>=1) {
						inventarios_elegidos = inventarios_a_exportar.get(0);
					}
					System.out.println("::: InventarioMainBoard 351 numero_inventarios_cerrados " + numero_inventarios_cerrados);
				} catch (ExceptionBDD e) {
					log.log("[-- 285 --]" + e.toString(), 4);log.log("[-- 286 --]" + "Error, la exportacion se cancelo", 3);
					e.printStackTrace();
					Toast.makeText(ctxt, "Error, la exportacion se cancelo", Toast.LENGTH_LONG).show();
					return false;
				}

				if (numero_inventarios_cerrados <= 0) {
					log.log("[-- 297 --]" + "Ningun inventario cerrado, la exportacion se cancelo", 3);
					Toast.makeText(ctxt, "Ningun inventario cerrado, la exportacion se cancelo", Toast.LENGTH_LONG).show();
					return false;
				}else if(numero_inventarios_cerrados>=2){
					log.log("[-- 297 --]" + "Debe seleccionar un solo inventario, la exportacion se cancelo", 3);
					Toast.makeText(ctxt, "No puede realizarse la exportacion, solamente debe haber un inventario cerrado para exportar.", Toast.LENGTH_LONG).show();
					return false;
				}

				lanzarMenuEspera();

				View.OnClickListener listenerWifi = new View.OnClickListener() {

					public void onClick(View v) {
						log.log("[-- 311 --]" + "Se presiono el boton Wifi ", 0);
						borrarDespues = dialogoPrincipioExport.isBorrar_luego();
						dialogoPrincipioExport.dismiss();
						Intent intentWifi = new Intent(InventarioMainBoard.this, WiFiControlador.class);
						startActivityForResult(intentWifi, Parametros.REQUEST_WIFI_EXPORT);
					}
				};

				View.OnClickListener listenerUsb = new View.OnClickListener() {

					public void onClick(View v) {

						log.log("[-- 327 --]" + "Se presiono el boton USB ", 0);
						// dialogoPrincipioExport.cancel();
						// dialogoPrincipioExport.hide();
						dialogoPrincipioExport.dismiss();

						int contador = 3;

						try {
							do {
								export_usb(dialogoPrincipioExport.isBorrar_luego());
								contador--;
							} while (!control_buena_exportacion()
									&& contador >= 0);
						} catch (ExceptionBDD e) {
							showSimpleDialogOK(
									"Error",
									"Generacion del documento XML imposible: "
											+ e.getComentario()).show();
							cerrarMenuEspera();
							return;
						} catch (Exception e) {
							showSimpleDialogOK(
									"Error",
									"Imposible exportar en el "
											+ Dispositivo_Export
											+ ". Verifique que este correcamente "
											+ "conectado").show();
							cerrarMenuEspera();
							return;
						}

						// Si hubo vencimiento del contador:
						if ((!control_buena_exportacion())
								|| contador < 0) {
							// Entra aca, por que?
							showSimpleDialogOK(
									"ERROR DE EXPORTACION",
									"Los archivos XML de exportacion del "
											+ Dispositivo_Export
											+ ", no han podido ser creados con exito.")
									.show();
							return;
						}

						cerrarMenuEspera();
					}
				};

				View.OnClickListener listenerNegativo = new View.OnClickListener() {

					public void onClick(View v) {
						dialogoPrincipioExport.cancel();
						cerrarMenuEspera();
					}
				};

				int radioProductosContabilizados = ParametrosInventario.ProductosNoContabilizados;
				System.out.println("::: InventarioMainBoard 453 Seleccion invetario cerrado radio valor " +radioProductosContabilizados );
if(radioProductosContabilizados==2){
	dialogoPrincipioExport = new DialogPersoComplexExport(
			ctxt,
			"MEDIO DE EXPORTACION",
			"Usted esta a punto de exportar los datos de INVENTARIOS.\n"
					+ "La opcion de  AJUSTAR PRODUCTOS NO INCLUIDOS puede demorar varios minutos.\n\n"
					+ "Por favor, elija el medio con el cual usted desea exportar los datos:\n"
					+ "Los inventarios dinamicos se borraran",
			true, listenerWifi, listenerUsb, listenerNegativo);
	dialogoPrincipioExport.show();
}else{
	dialogoPrincipioExport = new DialogPersoComplexExport(
			ctxt,
			"MEDIO DE EXPORTACION",
			"Usted esta a punto de exportar los datos de INVENTARIOS.\n"
					+ "Este proceso puede requerir algunos minutos.\n\n"
					+ "Por favor, elija el medio con el cual usted desea exportar los datos:\n"
					+ "Los inventarios dinamicos se se borraran",
			true, listenerWifi, listenerUsb, listenerNegativo);
	dialogoPrincipioExport.show();

}

				/*dialogoPrincipioExport = new DialogPersoComplexExport(
						ctxt,
						"MEDIO DE EXPORTACION",
						"Usted esta a punto de exportar los datos de INVENTARIOS.\n"
								+ "Este proceso puede requerir algunos minutos.\n\n"
								+ "Por favor, elija el medio con el cual usted desea exportar los datos:\n"
								+ "Los inventarios dinamicos se se borraran",
						true, listenerWifi, listenerUsb, listenerNegativo);
				dialogoPrincipioExport.show();
*/
				return true;
			}
		});

		botonImportar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				log.log("[-- 396 --]" + "Se presiono Importar", 0);
				// Control de la cantidad de inventario ya en curso:
				try {
					bdd = new BaseDatos(ctxt);
					System.out.println("::: Inventariomainboard 467 antes del wifi sector 1");
					if (bdd.selectInventariosNumerosEnBdd().size() > ParametrosInventario.PREF_MAX_COMPRA_ABIERTAS) {
						showSimpleDialogOK(
								"Alerta",
								"La cantidad de inventarios cargados es muy importante y puede perjudicar el buen funcionamiento de su tablet Android.\n\n"
										+ "Por favor, se recomiende proceder a la exportacion de los inventarios terminados y borrar los inventarios vencidos."
										+ "que no superen los 10 inventarios");
					}
				} catch (ExceptionBDD e) {

					log.log("[-- 409 --]" + e.toString(), 4);

				}

				View.OnClickListener listenerWifi = new View.OnClickListener() {

					public void onClick(View v) {
						System.out.println("::: Inventario mainboard antes del wifi sector 2");
						log.log("[-- 417 --]" + "Se presiono wifi", 0);
						dialogoPrincipioImport.dismiss();

						Intent intentWifi = new Intent(
								InventarioMainBoard.this, WiFiControlador.class);
						startActivityForResult(intentWifi,
								Parametros.REQUEST_WIFI_IMPORT);
					}
				};

				View.OnClickListener listenerUsb = new View.OnClickListener() {

					public void onClick(View v) {
						dialogoPrincipioImport.dismiss();

						log.log("[-- 432 --]" + "Se presiono exportar por usb",
								0);
						System.out.println("InventarioMainBoard 499 ExportarUSB");
						Intent intentUSB = new Intent(InventarioMainBoard.this,
								UsbProvider.class);
						// intentUSB.putExtra(Parametros.extra_uri_usb,
						// Parametros.PREF_USB_IMPORT);
						intentUSB.putExtra(Parametros.extra_uri_usb,
								ParametrosInventario.CARPETA_ATABLET);
						// intentUSB.putExtra(Parametros.extra_uri_usb,
						// "/data/data/com.foca.deboInventario/test/");
						startActivityForResult(intentUSB,
								Parametros.REQUEST_USB);
						finish();
					}
				};

				View.OnClickListener listenerNegativo = new View.OnClickListener() {

					public void onClick(View v) {
						log.log("[-- 450 --]" + "Se presiono cancelar", 0);
						dialogoPrincipioImport.cancel();
						// cerrarMenuEspera();
					}
				};

				dialogoPrincipioImport = new DialogPersoComplexExport(
						ctxt,
						"CARGAR UN NUEVO INVENTARIO",
						"Usted esta a punto de cargar un nuevo INVENTARIOS.\n\n"
								+ "Por favor, elija el medio con el cual usted desea importar " +
								"los datos del nuevo inventario:\n",
						false, listenerWifi, listenerUsb, listenerNegativo);
				dialogoPrincipioImport.show();

				log.log("[-- 464 --]"
						+ "Se mu8estra un pop up con las opcioners de importacion",
						3);
			}
		});

		botonSalir.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, @NonNull MotionEvent event) {
				log.log("[-- 473 --]" + "Se presiono salir", 0);
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					ImageView imageV = (ImageView) v;
					imageV.setBackgroundColor(getResources().getColor(
							R.color.orange));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					ImageView imageV = (ImageView) v;
					imageV.setBackgroundColor(Color.TRANSPARENT);

					finish();
				}
				return true;
			}
		});
	}

	/**
	 * Podemos regresar desde: Exportacion, importacion, inventario normal o
	 * dinmico
	 *
	 * 1 Volvemos de Pagina inventario o Dinmico :refrescamos la tabla y
	 * controlamos si hay alguno para cerrar
	 *
	 * 2 Si volvemos de WIFI import todo bien, vamos a seleccionar Inventario
	 *
	 * 3 Si hubo un error con WIFI import da la opcin de cargar por USB
	 * <p>
	 * 4 Si volvemos de WIFI Export todo OK vamos a hacer exportacion por WIFI
	 * <p>
	 * 5 Si volvemos de WIFI export con error damos la opcion de hacerlo por
	 * USB
	 */

	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intentRespondido) {
		try {
			super.onActivityResult(requestCode, resultCode, intentRespondido);
			Bundle bundle = null;
			if (intentRespondido != null) {
				bundle = intentRespondido.getExtras();
			}

			/*
			 * if (requestCode == ParametrosInventario.REQUEST_INVENTARIO &&
			 * resultCode == RESULT_OK) { int numeroInventario =
			 * bundle.getInt(ParametrosInventario.extra_numeroInventario); int
			 * idLineaSeleccionada = ParametrosInventario.ID_LINEAS +
			 * numeroInventario;
			 * TableRow estaLinea = (TableRow)findViewById(idLineaSeleccionada);
			 * estaLinea.setBackgroundColor(Color.GREEN);
			 * refreshLinea(estaLinea);
			 * controlFin();
			 * } else if (requestCode == ParametrosInventario.REQUEST_INVENTARIO
			 * && resultCode == RESULT_CANCELED) { int numeroInventario =
			 * bundle.getInt(ParametrosInventario.extra_numeroInventario); int
			 * idLineaSeleccionada = ParametrosInventario.ID_LINEAS +
			 * numeroInventario;
			 * TableRow estaLinea = (TableRow)findViewById(idLineaSeleccionada);
			 * refreshLinea(estaLinea);
			 * controlFin();
			 */
			// 1 Volvemos de Pagina inventario o Dinmico:refrescamos la tabla
			// y
			// controlamos si hay alguno para cerrar
			if (requestCode == ParametrosInventario.REQUEST_INVENTARIO) {
				try {

					refreshTablaPrincipal();

					controlFin();
				} catch (ExceptionBDD e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == ParametrosInventario.REQUEST_INVENTARIO_DINAMICO) {
				try {
					refreshTablaPrincipal();

					controlFin();
				} catch (ExceptionBDD e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == Parametros.REQUEST_WIFI_IMPORT
					&& resultCode == RESULT_OK) {
				// cerrarMenuEspera();
				// 2 Si volvemos de WIFI import todo bien, vamos a seleccionar
				// Inventario
				Intent intentInventario = new Intent(ctxt,
						SeleccionInventarios.class);
				startActivity(intentInventario);
				finish();

			} else if (requestCode == Parametros.REQUEST_WIFI_IMPORT) {
				// cerrarMenuEspera();
				// 3 Si hubo un error con WIFI import da la opcin de cargar
				// por USB
				desactivarWifi();
				showSimpleDialogSiNo("Error de conexion a la red", "La red hasta el servidor no ha podido ser establecida (1).\n\nUsted desea importar sus datos por medio de un Dispositivo (conectelo en aquel caso)?", UsbProvider.class).show();
			} else if (requestCode == Parametros.REQUEST_WIFI_EXPORT && resultCode == RESULT_OK) {
				// 4 Si volvemos de WIFI Export todo OK vamos a hacer
				// exportacion por WIFI
				ExportarDatos unaExportacion = new ExportarDatos();
				unaExportacion.execute(ctxt);

			} else if (requestCode == Parametros.REQUEST_WIFI_EXPORT) {
				// 5 Si volvemos de WIFI export con error damos la opcion de
				// hacerlo por USB
				cerrarMenuEspera();
				desactivarWifi();
				showSimpleDialogSiNo("Error de conexion a la red", "La red hasta el servidor no ha podido ser establecida (2).\n\nUsted desea exportar sus datos por medio de un Dispositivo (conectelo en aquel caso)?", null).show();
			}

		} catch (Exception e) {

			log.log("[-- 492 --]" + e.toString(), 4);
			e.printStackTrace();
			showSimpleDialogOK("Error", e.toString()).show();
		}
	}

	// ***********************
	// ***********************
	// **** METODOS ****
	// ***********************
	// ***********************
	//

	/**
	 * Creamos el thread que va a ejecutar el trabajo pesado (en una nueva clase
	 * ) No se usa aparentemente en esta activity
	 */
	protected class CargarDatosArticulos extends AsyncTask<Context, Integer, String> {

		/**
		 * Tarea a realizar en background
		 */
		@Nullable
        protected String doInBackground(Context... arg0) {
			System.out.println("::: InventarioMainboard 700 CargarDatosArticulos doInBackground");
			popupSubir(2);

			// Cargamos la base de datos:
			BaseDatos bdd = new BaseDatos(ctxt);
			bdd = new BaseDatos(ctxt);

			popupStart();

			// Recuperamos los inventarios cargados:
			try {
				listaInventariosSeleccionados = bdd
						.selectInventariosNumerosEnBdd();
			} catch (ExceptionBDD e2) {

				log.log("[-- 633 --]" + e2.toString(), 0);
				e2.printStackTrace();
			}

			// Para cada inventario vemos si tiene articulos ya cargados o no.
			// Los inventarios que figuran sin articulos => hay que rellenarlos.

			// A ver si continuamos datos ya presentes en bdd o si empezamos de
			// nuevo:
			boolean bddYaCargada = true;
			try {
				if (bdd.selectArticulosCodigosEnBdd() == null) {
					bddYaCargada = false;
				}
			} catch (ExceptionBDD e1) {

				e1.printStackTrace();
				bddYaCargada = false;
			}

			popupSubir(5);

			// Carga de los datos si y solamente si la base est vaca:
			if (bddYaCargada == false) {

				try {
					popupSubir(10);

					// Primero se actualizan las panreferencias en la tablet
					// Android:
					/*
					 * SharedPreferences settings =
					 * PreferenceManager.getDefaultSharedPreferences(ctxt);
					 * SharedPreferences.Editor editor = settings.edit();
					 * editor.
					 * putBoolean(ParametrosInventario.preferencias_en_curso,
					 * true); editor.commit();
					 */
					popupSubir(15);

					// Descargamos los detalles de todas las inventarios
					// seleccionadas:
					HttpReader readerHttp = null;
					try {
						readerHttp = new HttpReader(
								Parametros.PREF_URL_CONEXION_SERVIDOR,
								ParametrosInventario.FONCION_CARGAR_ARTICULOS,
								listaInventariosSeleccionados);
					} catch (ExceptionHttpExchange e) {

						log.log("[-- 686 --]" + e.toString(), 4);
						e.printStackTrace();
					}

					popupSubir(20);
					// Para cada inventario, guardamos sus detalles en una super
					// matriz de matriz de matriz ( matriz^3 ):
					int countSeguridad = 3;
					LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> hashmapDetallesTodosInventarios = readerHttp
							.readDetallesInventarios();


					while (countSeguridad > 0
							&& hashmapDetallesTodosInventarios.size() <= 0) {
						hashmapDetallesTodosInventarios = readerHttp
								.readDetallesInventarios();
						System.out.println("::: InventarioMainboard 780");
						countSeguridad--;
						popupSubir(20 + 4 - countSeguridad);
					}

					popupSubir(25);

					// Empezamos la lectura de la base de datos:
					Set<Integer> setNumerosInventarios = hashmapDetallesTodosInventarios
							.keySet();
					Iterator<Integer> ite = setNumerosInventarios.iterator();

					pasoInventario = (int) Math.floor((95 - 30)
							/ setNumerosInventarios.size());
					carga = 30;

					popupSubir(30);

					// Iteracin la enumeracin de las inventarios:
					while (ite.hasNext() == true) {

						// Recuperamos el numero de la inventario:
						int numeroInventario = ite.next();

						// Para este inventario, leemos todos los articulos que
						// contiene:
						ArrayList<HashMap<String, Integer>> listaCodigosArticulos = new ArrayList<HashMap<String, Integer>>();

						Set<HashMap<String, Integer>> setCodigosArticulos = hashmapDetallesTodosInventarios
								.get(numeroInventario).keySet();
						Iterator<HashMap<String, Integer>> ite2 = setCodigosArticulos
								.iterator();
						long cantidadArticulosEsteInventario = setCodigosArticulos
								.size();
						pasoArticulo = (double) ((double) pasoInventario / (double) cantidadArticulosEsteInventario);

						// Iteracin la enumeracin de los medidores de esta
						// inventario:
						while (ite2.hasNext() == true) {
							HashMap<String, Integer> codigosArticulo = ite2
									.next();
							listaCodigosArticulos.add(codigosArticulo);

							// Recuperamos los datos del ARTICULO con su par de
							// codigos de identificacion (sector, codigo):
							HashMap<String, String> datosUnArticulo = hashmapDetallesTodosInventarios
									.get(numeroInventario).get(codigosArticulo);

							// A partir de este hashmap recuperamos el objeto
							// ARTICULO:
							System.out.println("::: InventarioMainboard 830 recupera objeto");
							Articulo articulo = new Articulo(
									Integer.parseInt(datosUnArticulo
											.get(ParametrosInventario.bal_bdd_articulo_sector)),
									Integer.parseInt(datosUnArticulo
											.get(ParametrosInventario.bal_bdd_articulo_codigo)),
											Integer.parseInt(datosUnArticulo
													.get(ParametrosInventario.bal_bdd_articulo_balanza)),
													Integer.parseInt(datosUnArticulo
															.get(ParametrosInventario.bal_bdd_articulo_decimales)),
									new ArrayList<String>(
											Arrays.asList(datosUnArticulo
													.get(ParametrosInventario.bal_bdd_articulo_codigo_barra)
													.split(","))),
													new ArrayList<String>(
															Arrays.asList(datosUnArticulo
																	.get(ParametrosInventario.bal_bdd_articulo_codigo_barra_completo)
																	.split(","))),
									Integer.parseInt(datosUnArticulo
											.get(ParametrosInventario.bal_bdd_articulo_inventario)),
									datosUnArticulo
											.get(ParametrosInventario.bal_bdd_articulo_descripcion),
									Double.parseDouble(datosUnArticulo
										.get(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
									Double.parseDouble(datosUnArticulo
											.get(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
											Integer.parseInt(datosUnArticulo
													.get(ParametrosInventario.bal_bdd_articulo_depsn)),
									Double.parseDouble(datosUnArticulo
											.get(ParametrosInventario.bal_bdd_articulo_precio_venta)),
									Double.parseDouble(datosUnArticulo
											.get(ParametrosInventario.bal_bdd_articulo_precio_costo)));
							bdd.insertArticuloEnBdd(articulo);

							// Aumentamos la barra del popup:
							carga += pasoArticulo;
							popupSubir((int) carga);

						} // end while (ite2.hasNext() == true)

						// Guardamos en memoria la lista de los articulos de
						// este inventario:
						matrizArticulosCadaInventario.put(numeroInventario,
								listaCodigosArticulos);
						// carga += pasoArticulo;
						// popupSubir(proximoPaso);

					} // end while (ite.hasNext() == true)

					// Borramos la memoria:
					hashmapDetallesTodosInventarios.clear();

					// Terminar el thead:
					popupSubir(99);
					wait(500);

				} catch (Exception e) {

					log.log("[-- 794 --]" + e.toString(), 4);
					e.printStackTrace();
				} catch (ExceptionBDD e) {

					log.log("[-- 798 --]" + e.toString(), 4);
					e.printStackTrace();
				} finally {
					popupEnd();
				}
			} else {
				// En el caso en el cual la base ya esta cargada con datos:
				matrizArticulosCadaInventario.clear();

				for (int numInventario : listaInventariosSeleccionados) {
					try {
					
						matrizArticulosCadaInventario
								.put(numInventario,
										bdd.selectArticulosCodigosConNumeroInventario(numInventario));
					} catch (ExceptionBDD e) {

						log.log("[-- 814 --]" + e.toString(), 4);
						e.printStackTrace();
					}
				}
				popupEnd();
			}

			return null;
		}

		protected void onPostExecute(String result) {

			super.onPostExecute(result);

			// Cargamos la tabla principal con los datos eventualmente ya
			// cargados:
			try {
				refreshTablaPrincipal();

				controlFin();
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (ExceptionBDD e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	} // fin de "protected class CargarDatosinventarios"

	/**
	 * Metodo que llena la tabla de inventarios principal Trata de manera
	 * especial a los inventarios dinamicos de los otros ya que muestra siempre
	 * el boton de inventario dinmico por que siempre esta o se puede crear,
	 * pero es uno solo
	 * <p>
	 * 1 Limpiamos la vista
	 * <p>
	 * 2 Control de la integridad de cada inventario
	 * <p>
	 * 3 Cargamos los datos presentes (eventualmente) en la base de datos
	 * <p>
	 * 4 Configuracin de los botones
	 * <p>
	 * 5 Si tenemos elementos, construccin de todos los botones de inventarios
	 * comunes de la pagina
	 * <p>
	 * 6 Creacion de estructuras y la linea del inventario dinmico
	 * 
	 * @throws ExceptionBDD
	 * @throws Exception
	 */
	private void refreshTablaPrincipal() throws ExceptionBDD, Exception {
		// Variables:
		System.out.println("::: InventarioMain RefresTablaPrincipal 951");
		HashMap<Integer, HashMap<String, String>> matrizInventarios = new HashMap<Integer, HashMap<String, String>>();
		HashMap<Integer, HashMap<String, String>> matrizInventariosDinamicos = new HashMap<Integer, HashMap<String, String>>();

		// 1 Limpiamos la vista:
		tablaPrincipal.removeAllViews();

		// 2 Control de la integridad de cada inventario:
		control_integridad_tablas();

		// 3 Cargamos los datos presentes (eventualmente) en la base de datos:
		BaseDatos bdd = new BaseDatos(ctxt);
		// Por que lo creamos dos veces?
		bdd = new BaseDatos(ctxt);
		try {
			matrizInventarios = bdd.selectInventariosEnBdd();
		} catch (ExceptionBDD e2) {

			log.log("[-- 882 --]" + e2.toString() + " NO HAY INVENTARIOS", 4);
			e2.printStackTrace();
		}
		// 4 Configuracin de los botones:
		if (matrizInventarios.size() <= 0) {
			botonExportar.setEnabled(false);
			botonImportar.setEnabled(true);
			// return;
		} else if (matrizInventarios.size() >= ParametrosInventario.PREF_MAX_COMPRA_ABIERTAS) {
			botonExportar.setEnabled(true);
			botonImportar.setEnabled(false);
		} else {
			botonExportar.setEnabled(true);
			botonImportar.setEnabled(true);
		}

		// 5 Si tenemos elementos, construccin de todos los botones de
		// inventarios
		// comunes de la pagina:
		ArrayList<Integer> lista_claves = new ArrayList<Integer>();
		for (int i : matrizInventarios.keySet()) {
			lista_claves.add(i);
		}
		Collections.sort(lista_claves);
		Iterator<Integer> iterator_inventarios_id = lista_claves.iterator();
		System.out.println("::::: InventarioMainboard 1010 ");
		while (iterator_inventarios_id.hasNext() == true) {

			// Recupermos los datos:
			final int id_inventario = iterator_inventarios_id.next();
			
			if (id_inventario > 0) {
				final HashMap<String, String> hashmapUnInventario = matrizInventarios
						.get(id_inventario);

				// Consulta de la completud del inventario:
				ArrayList<Integer> listaEstadisticas = bdd
						.selectEstadisticasConIdInventario(id_inventario);
				int cantidadArticulosEnInventario = listaEstadisticas.get(0);
				int articulosYaContadosEnInventario = listaEstadisticas.get(1);

				// CREACIN DE LA LINEA:
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				TableRow nuevaLinea = (TableRow) inflater.inflate(
						R.layout.z_lineaprogressbar_mainboard, null);
				nuevaLinea
						.setId(ParametrosInventario.ID_LINEAS + id_inventario);

				// Elemento 1 = boton:
				Button b = (Button) nuevaLinea.findViewById(R.id.LPB2_boton);
				if (id_inventario >= 0) {
					b.setText("Inventario " + String.valueOf(id_inventario));
				} else {
					b.setText("Inv. dinamico "
							+ String.valueOf(Math.abs(id_inventario)));
				}
				b.setId(ParametrosInventario.ID_BOTONES + id_inventario);
				
				System.out.println("::: InventarioMainboard 1044 Ver id_inventario " + id_inventario);

				// Elemento 2 = texto del NOMBRE:
				TextView textoNombre = (TextView) nuevaLinea
						.findViewById(R.id.LPB2_nombre);
				textoNombre.setText(hashmapUnInventario.get(
						ParametrosInventario.bal_bdd_inventario_descripcion)
						.trim());

				// Elemento 3 = texto de la FECHA de CREACION:
				TextView textoFecha = (TextView) nuevaLinea
						.findViewById(R.id.LPB2_inicio);
				textoFecha.setText(hashmapUnInventario.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio)
						.trim());

				// Elemento 4 = PROGRESS-BAR:
				TextView tv_progressbar = (TextView) nuevaLinea
						.findViewById(R.id.LPB2_texto_progressbar);
				String texto_estadisticas_progresion = String
						.valueOf(articulosYaContadosEnInventario)
						+ " de "
						+ String.valueOf(cantidadArticulosEnInventario);
				tv_progressbar.setText(texto_estadisticas_progresion);

				try {
					ProgressBar pb = (ProgressBar) nuevaLinea
							.findViewById(R.id.LPB2_progressbar);
					int newValue = 0;
					if (cantidadArticulosEnInventario != 0) {
						newValue = (int) Math
								.floor((double) articulosYaContadosEnInventario
										/ (double) cantidadArticulosEnInventario
										* (double) 100);
					}
				
					pb.setProgress(newValue);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Elemento 5 = CANDADO:
				ImageView candado = (ImageView) nuevaLinea
						.findViewById(R.id.LPB2_estado);
				if (Integer.parseInt(hashmapUnInventario
						.get(ParametrosInventario.bal_bdd_inventario_estado)) == 1) {
					System.out.println("::: InventarioMainboard 1080 candado");
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_ab));
				} else {
					System.out.println("::: InventarioMainboard 1084 candado");
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_cer));
				}

				// Creacin del handler:
				b.setOnClickListener(new View.OnClickListener() {
					public void onClick(@NonNull View v) {

						log.log("[-- 994 --]" + "Presion un inventario ", 0);
						BaseDatos bdd = new BaseDatos(ctxt);

						try {
							if (bdd.estaAbiertoInventarioConId(v.getId()
									- ParametrosInventario.ID_BOTONES) == true) {
								ClicBoton((Button) v);
							} else {
								log.log("[-- 1002 --]"
										+ "Inventario cerrado con candado", 3);
								Toast.makeText(ctxt,
										"Inventario cerrado con candado",
										Toast.LENGTH_LONG).show();
							}
						} catch (ExceptionBDD e) {

							log.log("[-- 1009 --]" + e.toString(), 4);
							e.printStackTrace();
							Toast.makeText(ctxt,
									"Inventario cerrado con candado",
									Toast.LENGTH_LONG).show();
						}

					}
				});

				b.setOnLongClickListener(new View.OnLongClickListener() {

					public boolean onLongClick(@NonNull View v) {

						log.log("[-- 1043 --]"
								+ "Se hizo un clic largo sobre un ionventario",
								0);
						try {
							final int id_invent_con_boton = v.getId()
									- ParametrosInventario.ID_BOTONES;
							BaseDatos bdd = new BaseDatos(ctxt);

							if (bdd.estaAbiertoInventarioConId(id_invent_con_boton) == true) {
								View.OnClickListener listenerPositivo = new View.OnClickListener() {

									public void onClick(View v) {

										log.log("[-- --]"
												+ "Se presion el boton para borrar el inventario",
												0);
										// Aqui borramos el inventario:
										BaseDatos bdd = new BaseDatos(ctxt);
										try {
											bdd.borrarInventarioConArticulos(id_invent_con_boton);
										} catch (ExceptionBDD e) {

											log.log("[-- 1065 --]"
													+ e.toString(), 4);
											e.printStackTrace();
										}

										try {
											refreshTablaPrincipal();
										} catch (ExceptionBDD e) {

											log.log("[-- 1073 --]"
													+ e.toString(), 4);
											e.printStackTrace();
										} catch (Exception e) {

											log.log("[-- 1077 --]"
													+ e.toString(), 4);
											e.printStackTrace();
										}

										dialogoBorrarInventario.dismiss();
									}
								};

								View.OnClickListener listenerNegativo = new View.OnClickListener() {

									public void onClick(View v) {

										log.log("[-- --]"
												+ "Se presiono cancelar", 0);
										dialogoBorrarInventario.cancel();
									}
								};

								dialogoBorrarInventario = new DialogPersoComplexSiNo(
										ctxt,
										"SUPRIMIR INVENTARIO",
										"Usted esta a punto de suprimir el inventario n"
												+ String.valueOf(id_invent_con_boton)
												+ "\n\n"
												+ "Esta seguro de querer suprimir este inventario?",
										DialogPerso.ALERTAR, listenerPositivo,
										listenerNegativo);
								dialogoBorrarInventario.show();
							} else {
								Toast.makeText(
										ctxt,
										"Supresion imposible: inventario cerrado con candado",
										Toast.LENGTH_LONG).show();
							}

						} catch (ExceptionBDD e) {

							log.log("[-- 1114 --]" + e.toString(), 4);
							e.printStackTrace();
							Toast.makeText(ctxt,
									"Inventario cerrado con candado",
									Toast.LENGTH_LONG).show();

							log.log("[-- 1120 --]"
									+ "Inventario cerrado con candado", 3);
						}

						return true;
					}

				});

				// Creacin de los handlers:
				candado.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						log.log("[-- 1133 --]" + "Se presiono el candado", 0);

						try {
							ImageView iv = (ImageView) v;

							BaseDatos bdd = new BaseDatos(ctxt);

							if (Integer.parseInt(hashmapUnInventario
									.get(ParametrosInventario.bal_bdd_inventario_estado)) == ParametrosInventario.INVENTARIO_ABIERTO) {

								bdd.updateInventario(
										Integer.parseInt(hashmapUnInventario
												.get(ParametrosInventario.bal_bdd_inventario_numero)),
										ParametrosInventario.INVENTARIO_CERRADO);
								hashmapUnInventario
										.put(ParametrosInventario.bal_bdd_inventario_estado,
												String.valueOf(ParametrosInventario.INVENTARIO_CERRADO));
								iv.setImageDrawable(getResources().getDrawable(
										R.drawable.candado_cer));
							} else {
								bdd.updateInventario(
										Integer.parseInt(hashmapUnInventario
												.get(ParametrosInventario.bal_bdd_inventario_numero)),
										ParametrosInventario.INVENTARIO_ABIERTO);
								hashmapUnInventario
										.put(ParametrosInventario.bal_bdd_inventario_estado,
												String.valueOf(ParametrosInventario.INVENTARIO_ABIERTO));
								iv.setImageDrawable(getResources().getDrawable(
										R.drawable.candado_ab));
							}
						} catch (Exception e) {

							log.log("[-- 1164 --]" + e.toString(), 4);
							e.printStackTrace();
						} catch (ExceptionBDD e) {

							log.log("[-- 1168 --]" + e.toString(), 4);
							e.printStackTrace();
						}
					}
				});

				// AGREGAMOS LA LINEA A LA TABLA:
				tablaPrincipal.addView(nuevaLinea);

			} else {
				
				System.out.println("::::: InvMain 1253 " + matrizInventarios);
				// Meter los dinamicos en un arreglo
				matrizInventariosDinamicos.put(id_inventario,
						matrizInventarios.get(id_inventario));
			}  

		} // end while

		/**
		 * Trabajo que se hace para los inventarios dinmicos similar a los
		 * otros pero con un tratamiento especial por que es un boton que esta
		 * siempre y fijo y tiene una funcionalidad definida
		 */
		// 6 Creacion de estructuras y la linea del inventario dinmico
		// HashMap<String,String>
		// hashmapInventarioVenta,hashmapInventarioDeposito;

		// Verificamos que no hayan inventarios dinamicos para crear una linea
		// vacia
		if (matrizInventariosDinamicos.size() == 0) {
			hayDinamicos = false;
		} else {
			// Caso en el que hay inventarios dinamicos de antes
			hayDinamicos = true;
		}
		System.out.println("::: INVDIN 1300 //////////////////////");
		
		hashmapInventarioVenta = matrizInventariosDinamicos
				.get(ParametrosInventario.ID_INV_DIN_VTA);
		hashmapInventarioDeposito = matrizInventariosDinamicos
				.get(ParametrosInventario.ID_INV_DIN_DEP);

		// ArrayList<Integer> lista_claves_dinamicos = new ArrayList<Integer>();
		//
		// if(hayDinamicos) {
		// //Variable para los id de los inventarios dinamicos
		//
		// for (int i : matrizInventariosDinamicos.keySet()){
		// lista_claves_dinamicos.add(i);
		// }
		// Collections.sort(lista_claves_dinamicos);
		// }
		boolean condicionRadio = ParametrosInventario.InventariosVentas;
		
		int id_inv_ficticio = 0;
		if(condicionRadio){
//			// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
			id_inv_ficticio = -1;
		}else{
//			// Esta seleccionado deposito, esto debe continuar sin los campos de ventas
			id_inv_ficticio = -2;
		}
		
		//Se agrega para que lea el parametro y se pase el valor del inventario correcto o seleccinado
		// No deberia tener un numero generico
//		int id_inv_ficticio = -1;
		
		// Crear la linea del inventario dinamico ficticio
		// Consulta de la completud del inventario, como es una linea ficticia,
		// es 0
		ArrayList<Integer> listaEstadisticas = new ArrayList<Integer>();
		int articulosTotalesQueInventariar = 0, articulosNonInventariados = 0;

		int cantidadArticulosEnInventario = 0, articulosYaContadosEnInventario = 0;
		// Si es ficticio es 0
		if (hayDinamicos) {
			
			ArrayList<Integer> listaEstadisticasVenta;
			ArrayList<Integer> listaEstadisticasDepo;
			
			// Buscar info en la base de datos y completar las variables
			if(condicionRadio == true){
				
				listaEstadisticasVenta = bdd
						.selectEstadisticasConIdInventario(ParametrosInventario.ID_INV_DIN_VTA);

				cantidadArticulosEnInventario = listaEstadisticasVenta.get(0);
				articulosYaContadosEnInventario = listaEstadisticasVenta.get(1);
			}else{
				listaEstadisticasDepo = bdd
						.selectEstadisticasConIdInventario(ParametrosInventario.ID_INV_DIN_DEP);
				cantidadArticulosEnInventario = listaEstadisticasDepo.get(0);
				articulosYaContadosEnInventario = listaEstadisticasDepo.get(1);
			}
//			ArrayList<Integer> listaEstadisticasVenta = bdd
//					.selectEstadisticasConIdInventario(ParametrosInventario.ID_INV_DIN_VTA);
//
//			ArrayList<Integer> listaEstadisticasDepo = bdd
//					.selectEstadisticasConIdInventario(ParametrosInventario.ID_INV_DIN_DEP);
//
//			cantidadArticulosEnInventario = listaEstadisticasVenta.get(0)
//					+ listaEstadisticasDepo.get(0);
//
//			articulosYaContadosEnInventario = listaEstadisticasVenta.get(1)
//					+ listaEstadisticasDepo.get(1);

			
		} else {
			listaEstadisticas.add(articulosTotalesQueInventariar);
			listaEstadisticas.add(articulosTotalesQueInventariar
					- articulosNonInventariados);
			listaEstadisticas.add(articulosNonInventariados);
			cantidadArticulosEnInventario = listaEstadisticas.get(0);
			articulosYaContadosEnInventario = listaEstadisticas.get(1);
		}
		System.out.println("::: INVDIN 1347 cantidadArticulosEnInventario "+ cantidadArticulosEnInventario);
		System.out.println("::: INVDIN 1347 articulosYaContadosEnInventario "+ articulosYaContadosEnInventario);
		
		
		
		System.out.println("::: INVDIN 1347 Creacion de la linea ");
		// CREACIN DE LA LINEA:
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		TableRow nuevaLinea = (TableRow) inflater.inflate(
				R.layout.z_lineaprogressbar_mainboard, null);
		nuevaLinea.setId(ParametrosInventario.ID_LINEAS + id_inv_ficticio);
		// nuevaLinea.setId(ParametrosInventario.ID_LINEA_DINAMICO);
		System.out.println("::: INVDIN 1347 Creacion de la linea id_inv_ficticio " + id_inv_ficticio);
		// Elemento 1 = boton:
		Button b = (Button) nuevaLinea.findViewById(R.id.LPB2_boton);
		b.setText("Inventarios Dinamicos ");
		b.setId(ParametrosInventario.ID_BOTONES + id_inv_ficticio);

		// Elemento 2 = texto del NOMBRE:
		TextView textoNombre = (TextView) nuevaLinea
				.findViewById(R.id.LPB2_nombre);
		textoNombre.setText("Inventario Dinmico");

		// Elemento 3 = texto de la FECHA de CREACION:
		TextView textoFecha = (TextView) nuevaLinea
				.findViewById(R.id.LPB2_inicio);
		if (hayDinamicos) {
			// Buscar la fecha menor de los dos inventarios
			String fecha="";
//			boolean condicionRadio = ParametrosInventario.InventariosVentas;
			if(condicionRadio == true){
				// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
				condR=-1;
			}else{
				// Esta seleccionado deposito, esto debe continuar sin los campos de ventas
				condR=-2;
			}
			System.out.println("::: InventarioMainBoard 1367 condR == " + condR);
			if(condR == -1){
				String fechaIVta = hashmapInventarioVenta.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio).trim();
				fecha = hashmapInventarioVenta.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio)
						.trim();
			}else if(condR == -2){
				String fechaIDep = hashmapInventarioDeposito.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio).trim();
				fecha = hashmapInventarioDeposito.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio)
						.trim();
			}
			
			// SimpleDateFormat sdf= new
			// SimpleDateFormat("yyyy-MM-ddm hh:mm:ss");

			// Date fechaVenta=sdf.parse(fechaIVta);
			// Date fechaDepo=sdf.parse(fechaIDep);

			// if(fechaVenta.compareTo(fechaDepo)<0) {
			// fecha=hashmapInventarioVenta.get(ParametrosInventario.bal_bdd_inventario_fechaInicio).trim();
			// }else {
			// fecha=hashmapInventarioDeposito.get(ParametrosInventario.bal_bdd_inventario_fechaInicio).trim();
			// }

			
/*
 * Esto estaba asi pero se arma de otra forma para separar los inventarios
 * Damian 10/11/2015
 * 			
			if (fechaIVta.compareTo(fechaIDep) < 0) {
				fecha = hashmapInventarioVenta.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio)
						.trim();
			} else {
				fecha = hashmapInventarioDeposito.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio)
						.trim();
			}
*
*
*
*/

			textoFecha.setText(fecha);
		} else {
			textoFecha.setText("");
		}

		// Elemento 4 = PROGRESS-BAR:
		TextView tv_progressbar = (TextView) nuevaLinea
				.findViewById(R.id.LPB2_texto_progressbar);
		String texto_estadisticas_progresion = String
				.valueOf(articulosYaContadosEnInventario)
				+ " de "
				+ String.valueOf(cantidadArticulosEnInventario);
		tv_progressbar.setText(texto_estadisticas_progresion);

		try {
			ProgressBar pb = (ProgressBar) nuevaLinea
					.findViewById(R.id.LPB2_progressbar);
			int newValue = 0;
			if (cantidadArticulosEnInventario != 0) {
				newValue = (int) Math
						.floor((double) articulosYaContadosEnInventario
								/ (double) cantidadArticulosEnInventario
								* (double) 100);
			}
			pb.setProgress(newValue);
		} catch (Exception e) {

			log.log("[-- 1326 --]" + e.toString(), 4);
			e.printStackTrace();
		}

		// Elemento 5 = CANDADO:
		ImageView candado = (ImageView) nuevaLinea
				.findViewById(R.id.LPB2_estado);
		if (hayDinamicos) {
			// Se verifica que esten los dos cerrados
			int estadoCandadoDepo = 0;
			int estadoCandadoVta = 0;
			System.out.println("::: InventarioMainBoard 1451 Antes de ir a bd");
			if(condR == -1){
				
				estadoCandadoVta = Integer.parseInt(hashmapInventarioVenta
						.get(ParametrosInventario.bal_bdd_inventario_estado));
				
				if (estadoCandadoVta == 1){
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_ab));
				} else {
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_cer));
				}
				
			}else if(condR == -2){
				
				estadoCandadoDepo = Integer.parseInt(hashmapInventarioDeposito
						.get(ParametrosInventario.bal_bdd_inventario_estado));
				
				if (estadoCandadoDepo == 1){
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_ab));
				} else {
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_cer));
				}
				
			}
			
//			if (estadoCandadoVta == 1){
//				candado.setImageDrawable(getResources().getDrawable(
//						R.drawable.candado_ab));
//			}if (estadoCandadoDepo == 1){
//				candado.setImageDrawable(getResources().getDrawable(
//						R.drawable.candado_ab));
//			} else {
//				candado.setImageDrawable(getResources().getDrawable(
//						R.drawable.candado_cer));
//			}
/*
 * 
 * Esto se comenta y modifica para la division de los inventarios ventas y depositos
 * Damian 10/11/2015			
			
			int estadoCandadoVta = Integer.parseInt(hashmapInventarioVenta
					.get(ParametrosInventario.bal_bdd_inventario_estado));
			int estadoCandadoDepo = Integer.parseInt(hashmapInventarioDeposito
					.get(ParametrosInventario.bal_bdd_inventario_estado));
			if ((estadoCandadoVta == 1) && (estadoCandadoDepo == 1)) {
				candado.setImageDrawable(getResources().getDrawable(
						R.drawable.candado_ab));
			} else {
				candado.setImageDrawable(getResources().getDrawable(
						R.drawable.candado_cer));
			}
*
*
*
*/			
		} else {
			// Se dibuja abierto
			candado.setImageDrawable(getResources().getDrawable(
					R.drawable.candado_ab));
		}
		// Creacin del handler del boton:
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(@NonNull View v) {
				BaseDatos bdd = new BaseDatos(ctxt);

				if (hayDinamicos) {
					
//					if(condR==-1){
						try {
//System.out.println("::: InventarioMainboard 1524 vta que id es el q pasa -1 " + v.getId() );
							if(condR==-1){
								if (bdd.estaAbiertoInventarioConId(v.getId()
										- ParametrosInventario.ID_BOTONES) == true) {
									ClicBotonDinamico((Button) v);
								} else {

									log.log("[-- --]"
											+ "Inventario cerrado con candado", 3);
									Toast.makeText(ctxt,
											"Inventario cerrado con candado",
											Toast.LENGTH_LONG).show();
								}
							}else if(condR==-2){
								if (bdd.estaAbiertoInventarioConId(v.getId()
										- ParametrosInventario.ID_BOTONES) == true) {
									ClicBotonDinamico((Button) v);
								} else {

									log.log("[-- --]"
											+ "Inventario cerrado con candado", 3);
									Toast.makeText(ctxt,
											"Inventario cerrado con candado",
											Toast.LENGTH_LONG).show();
								}
							}
							
//							if (bdd.estaAbiertoInventarioConId(v.getId()
//									- ParametrosInventario.ID_BOTONES) == true) {
//								ClicBotonDinamico((Button) v);
//							} else {
//
//								log.log("[-- --]"
//										+ "Inventario cerrado con candado", 3);
//								Toast.makeText(ctxt,
//										"Inventario cerrado con candado",
//										Toast.LENGTH_LONG).show();
//							}
							
							
						} catch (ExceptionBDD e) {

							log.log("[-- 1372 --]" + e.toString(), 3);
							e.printStackTrace();
							Toast.makeText(ctxt, "Inventario cerrado con candado",
									Toast.LENGTH_LONG).show();
						}
//					}else if(condR==-2){
//						try {
//							System.out.println("::: InventarioMainboard 1545 dep que id es el q pasa -2 " + v.getId() );
//							if (bdd.estaAbiertoInventarioConId(v.getId()
//									- ParametrosInventario.ID_BOTONES) == true) {
//								ClicBotonDinamico((Button) v);
//							} else {
//
//								log.log("[-- --]"
//										+ "Inventario cerrado con candado", 3);
//								Toast.makeText(ctxt,
//										"Inventario cerrado con candado",
//										Toast.LENGTH_LONG).show();
//							}
//						} catch (ExceptionBDD e) {
//
//							log.log("[-- 1372 --]" + e.toString(), 3);
//							e.printStackTrace();
//							Toast.makeText(ctxt, "Inventario cerrado con candado",
//									Toast.LENGTH_LONG).show();
//						}
//					}
//					try {
//						if (bdd.estaAbiertoInventarioConId(v.getId()
//								- ParametrosInventario.ID_BOTONES) == true) {
//							ClicBotonDinamico((Button) v);
//						} else {
//
//							log.log("[-- --]"
//									+ "Inventario cerrado con candado", 3);
//							Toast.makeText(ctxt,
//									"Inventario cerrado con candado",
//									Toast.LENGTH_LONG).show();
//						}
//					} catch (ExceptionBDD e) {
//
//						log.log("[-- 1372 --]" + e.toString(), 3);
//						e.printStackTrace();
//						Toast.makeText(ctxt, "Inventario cerrado con candado",
//								Toast.LENGTH_LONG).show();
//					}
				} else {
					ClicBotonDinamico((Button) v);
				}

			}
		});

		// Creacin de los handlers:
		candado.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try {
					ImageView iv = (ImageView) v;

					int estadoCandadoVta = 0;
					int estadoCandadoDepo = 0;

					BaseDatos bdd = new BaseDatos(ctxt);

					if (hayDinamicos) {
						// Se verifica que esten los dos cerrados
if(condR == -1){
	
	estadoCandadoVta = Integer.parseInt(hashmapInventarioVenta
			.get(ParametrosInventario.bal_bdd_inventario_estado));
	
}else if(condR == -2){
	
	estadoCandadoDepo = Integer.parseInt(hashmapInventarioDeposito
			.get(ParametrosInventario.bal_bdd_inventario_estado));
	
}

/*
 * 
 * Se modifico para separar los inventarios 
 * Damian 10/11/2015
						estadoCandadoVta = Integer.parseInt(hashmapInventarioVenta
								.get(ParametrosInventario.bal_bdd_inventario_estado));
						estadoCandadoDepo = Integer.parseInt(hashmapInventarioDeposito
								.get(ParametrosInventario.bal_bdd_inventario_estado));
 * 
 * 
 * 
 */
						// estadoCandadoVta=bdd.getEstadoInventarioDinamico(ParametrosInventario.ID_INV_DIN_VTA);
						// estadoCandadoDepo=bdd.getEstadoInventarioDinamico(ParametrosInventario.ID_INV_DIN_DEP);
					} else {
						// Se dibuja abierto
					System.out.println("::: InventarioMainBoard 1604 aca no hace nada!!!!");
						/*
						 * ACA NO HAY NADA
						 * NOSE SI AGREGAR
						 */
					}
System.out.println("::: InventarioMainBoard 1667 estadoCandadoVta " + estadoCandadoVta);
System.out.println("::: InventarioMainBoard 1667 estadoCandadoDepo " + estadoCandadoDepo);
//System.out.println("::: InventarioMainBoard 1667 abierto " + ParametrosInventario.INVENTARIO_ABIERTO);
//System.out.println("::: InventarioMainBoard 1667 cerrado " + ParametrosInventario.INVENTARIO_CERRADO);
System.out.println("::: InventarioMainBoard 1667 inventario_estado " +ParametrosInventario.bal_bdd_inventario_estado);					



if(condR == -1){
	System.out.println("::: InventarioMainboard 1640");
	System.out.println("::: InventarioMainboard 1640 " + ParametrosInventario.INVENTARIO_ABIERTO);
	System.out.println("::: InventarioMainboard 1640 " + estadoCandadoVta);
	if (estadoCandadoVta == ParametrosInventario.INVENTARIO_ABIERTO){
		// Actualizamos, cerrando el inventario dinamico de
		// venta
		System.out.println("::: InventarioMainboard 1645");
		bdd.updateInventario(
				ParametrosInventario.ID_INV_DIN_VTA,
				ParametrosInventario.INVENTARIO_CERRADO);
		hashmapInventarioVenta
				.put(ParametrosInventario.bal_bdd_inventario_estado,
						String.valueOf(ParametrosInventario.INVENTARIO_CERRADO));
		iv.setImageDrawable(getResources().getDrawable(
				R.drawable.candado_cer));
		System.out.println("::: InventarioMainboard 1654");
	} else {
		// Los guardo como abiertos
		System.out.println("::: InventarioMainboard 1657");
		bdd.updateInventario(
				ParametrosInventario.ID_INV_DIN_VTA,
				ParametrosInventario.INVENTARIO_ABIERTO);
		hashmapInventarioVenta
				.put(ParametrosInventario.bal_bdd_inventario_estado,
						String.valueOf(ParametrosInventario.INVENTARIO_ABIERTO));
		iv.setImageDrawable(getResources().getDrawable(
				R.drawable.candado_ab));
		System.out.println("::: InventarioMainboard 1666");
	}
	
}else if(condR == -2){
	System.out.println("::: InventarioMainboard 1670");
	System.out.println("::: InventarioMainboard 1670 " + ParametrosInventario.INVENTARIO_ABIERTO);
	System.out.println("::: InventarioMainboard 1670 " + estadoCandadoDepo);
	if (estadoCandadoDepo == ParametrosInventario.INVENTARIO_ABIERTO) {
		// Actualizamos, cerrando el inventario dinamico de
		// deposito
		System.out.println("::: InventarioMainboard 1674");
		bdd.updateInventario(
				ParametrosInventario.ID_INV_DIN_DEP,
				ParametrosInventario.INVENTARIO_CERRADO);
		hashmapInventarioDeposito
				.put(ParametrosInventario.bal_bdd_inventario_estado,
						String.valueOf(ParametrosInventario.INVENTARIO_CERRADO));
		System.out.println("::: InventarioMainboard 1681");
		iv.setImageDrawable(getResources().getDrawable(
				R.drawable.candado_cer));
		
	}else {
		System.out.println("::: InventarioMainboard 1686");
		bdd.updateInventario(
				ParametrosInventario.ID_INV_DIN_DEP,
				ParametrosInventario.INVENTARIO_ABIERTO);
		hashmapInventarioDeposito
				.put(ParametrosInventario.bal_bdd_inventario_estado,
						String.valueOf(ParametrosInventario.INVENTARIO_ABIERTO));
		iv.setImageDrawable(getResources().getDrawable(
				R.drawable.candado_ab));
	}
	
	
}
/*
 * 
 * Se modifico para la division de inventarios
 * Damian 10/11/2015
					if ((estadoCandadoVta == ParametrosInventario.INVENTARIO_ABIERTO)
							&& (estadoCandadoDepo == ParametrosInventario.INVENTARIO_ABIERTO)) {
						// Actualizamos, cerrando el inventario dinamico de
						// venta y el de deposito
						bdd.updateInventario(
								ParametrosInventario.ID_INV_DIN_VTA,
								ParametrosInventario.INVENTARIO_CERRADO);
						bdd.updateInventario(
								ParametrosInventario.ID_INV_DIN_DEP,
								ParametrosInventario.INVENTARIO_CERRADO);
						hashmapInventarioVenta
								.put(ParametrosInventario.bal_bdd_inventario_estado,
										String.valueOf(ParametrosInventario.INVENTARIO_CERRADO));
						hashmapInventarioDeposito
								.put(ParametrosInventario.bal_bdd_inventario_estado,
										String.valueOf(ParametrosInventario.INVENTARIO_CERRADO));
						iv.setImageDrawable(getResources().getDrawable(
								R.drawable.candado_cer));
					} else {
						// Los guardo como abiertos
						bdd.updateInventario(
								ParametrosInventario.ID_INV_DIN_VTA,
								ParametrosInventario.INVENTARIO_ABIERTO);
						bdd.updateInventario(
								ParametrosInventario.ID_INV_DIN_DEP,
								ParametrosInventario.INVENTARIO_ABIERTO);
						hashmapInventarioVenta
								.put(ParametrosInventario.bal_bdd_inventario_estado,
										String.valueOf(ParametrosInventario.INVENTARIO_ABIERTO));
						hashmapInventarioDeposito
								.put(ParametrosInventario.bal_bdd_inventario_estado,
										String.valueOf(ParametrosInventario.INVENTARIO_ABIERTO));
						iv.setImageDrawable(getResources().getDrawable(
								R.drawable.candado_ab));
					}
*
*
*
*/
					
				} catch (Exception e) {

					log.log("[-- 1449 --]" + e.toString(), 4);

					e.printStackTrace();
				} catch (ExceptionBDD e) {

					log.log("[-- 1454 --]" + e.toString(), 4);
					e.printStackTrace();
				}
			}
		});

		// AGREGAMOS LA LINEA A LA TABLA:
		tablaPrincipal.addView(nuevaLinea);

		desactivarWifi();

	}// Fin de la funcin

	/**
	 * Cuando se presione el boton "back" se cierra
	 */

	public void onBackPressed() {
		finish();
	}

	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

		log.log("[-- 1477 --]" + "Se presiono la tecla: " + keyCode, 1);
		if (keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0) {
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Metodo para mostrar un popup cuando se importan los inventarios
	 */
	private void popupStart() {
		popupCarga = new ProgressDialog(ctxt);
		popupCarga.setCancelable(false);
		popupCarga.setMessage("Importando los datos de inventarios...");
		popupCarga.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		popupCarga.setProgress(0);
		popupCarga.setMax(100);
		popupCarga.show();

		log.log("[-- 1479 --]" + "Inicia pop up cuando termina la importacion",
				2);
	}

	/**
	 * Sube un porcentaje el popUp de progreso
	 * 
	 * @param hastaXPorciento
	 */
	private void popupSubir(int hastaXPorciento) {
		popupCarga.setProgress(hastaXPorciento);
	}

	private void popupEnd() {
		popupCarga.dismiss();
	}

	/**
	 * Metodo que manejea el funcionamiento de los botones de la tabla al
	 * hacerles click en el caso comun, entra a la activity
	 * PaginaInventario.java, pasando como parmetro el valor del numero de
	 * inventario asociado al boton
	 * 
	 * @param boton
	 */
	private void ClicBoton(@NonNull Button boton) {
		int numeroInventarioAsociadoAlBoton = (boton.getId() - ParametrosInventario.ID_BOTONES);
		Intent intentInventario = new Intent(ctxt, PaginaInventario.class);
		intentInventario.putExtra(ParametrosInventario.extra_numeroInventario,
				numeroInventarioAsociadoAlBoton);
		// intentInventario.putExtra(ParametrosInventario.extra_bandera_invs_dinamicos,
		// ParametrosInventario.extra_valor_bandera_invs_dinamicos_no);
		startActivityForResult(intentInventario,
				ParametrosInventario.REQUEST_INVENTARIO);
	}

	/**
	 * El boton de inventario dinmico entra a la activity
	 * PaginaInventarioDinamico.java adems se agrega la funcionalidad de que se
	 * cargue la pantalla de confirmacin de continuar con el inventario o
	 * borrar y empezar con uno nuevo
	 * <p>
	 * 1 Si hay inventarios creados debe preguntar por eliminar o seguir con el
	 * anterior
	 * <p>
	 * &nbsp; &nbsp;1.1 Genera un dialog que pregunta si se continua con el
	 * inventario o se borra y genera algo nuevo
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;1.1.1 En caso positivo pasa al inventario
	 * actual
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;1.1.2 En caso negativo muestra otro cartel para
	 * verificar si borra
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;1.1.2.1 En caso de que quiera
	 * borrar se elimina y genera uno nuevo pasando a la Pagina correspondiente
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;1.1.2.2 En caso de que no quiera
	 * borrar, se vuelve a la pantalla principal
	 * <p>
	 * 2 Si no hay inventarios , Se deben crear 2 inventarios (uno para venta y
	 * otro para deposito)
	 * <p>
	 * &nbsp; &nbsp;2.1 Creo los objetos para los dos inventarios nuevos
	 * <p>
	 * &nbsp; &nbsp;2.2 Insertamos en la base de datos
	 * <p>
	 * &nbsp; &nbsp;2.3 Pasamos a la pantalla de administracin de inventario
	 * dinamico
	 * 
	 * @param boton
	 */
	private void ClicBotonDinamico(Button boton) {
		// 1 Si hay inventarios creados debe preguntar por eliminar o seguir
		// con
		// el anterior
		if (hayDinamicos) {
			// 1.1.1 En caso positivo pasa al inventario actual
			View.OnClickListener listenerPositivo = new View.OnClickListener() {

				public void onClick(View v) {
					// Lo que hace con el boton del si

					log.log("[-- --]"
							+ "Presiono para ir a los inventarios dinamicos", 0);
					dialogoContinuarInventario.dismiss();

					Intent intentInventario = new Intent(ctxt,
							PaginaInventarioDinamico.class);
					intentInventario.putExtra(
							ParametrosInventario.extra_numeroInventario,
							ParametrosInventario.ID_INV_DIN_VTA);
					// intentInventario.putExtra(ParametrosInventario.extra_numeroInventarioDinDepo,
					// ParametrosInventario.ID_INV_DIN_DEP);
					// intentInventario.putExtra(ParametrosInventario.extra_bandera_invs_dinamicos,
					// ParametrosInventario.extra_valor_bandera_invs_dinamicos_si);

					startActivityForResult(intentInventario,
							ParametrosInventario.REQUEST_INVENTARIO_DINAMICO);
				}
			};
			// 1.1.2 En caso negativo muestra otro cartel para verificar si
			// borra
			View.OnClickListener listenerNegativo = new View.OnClickListener() {

				public void onClick(View v) {
					// Lo que hace con el no
					// Debe preguntar nuevamente para eliminar el inventario
					// guardado
					// Abrir otra ventana y preguntar si realmente quiere
					// eliminar los datos

					dialogoContinuarInventario.dismiss();

					// 1.1.2.1 En caso de que quiera borrar se elimina y genera
					// uno nuevo pasando a la
					// Pagina correspondiente
					View.OnClickListener listenerPositivo = new View.OnClickListener() {

						public void onClick(View v) {

							log.log("[-- 1617 --]" + "Se presiono el boton si",
									0);
							BaseDatos bdd = new BaseDatos(ctxt);

							// Lo que hace con el boton del si

							dialogoBorrarInventarioDinamico.dismiss();

							// Creamos los inventarios dinamicos
							Inventario inventarioDinamicoVenta = new Inventario(
									ParametrosInventario.ID_INV_DIN_VTA,
									"Inv. dinamico "
											+ String.valueOf(ParametrosInventario.ID_INV_DIN_VTA)
											+ " de venta",
									new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Date()),
									"",
									ParametrosInventario.INVENTARIO_ABIERTO,
									ParametrosInventario.COD_LUGAR_INVENTARIO_VENTA);

							Inventario inventarioDinamicoDepo = new Inventario(
									ParametrosInventario.ID_INV_DIN_DEP,
									"Inv. dinamico "
											+ String.valueOf(ParametrosInventario.ID_INV_DIN_DEP)
											+ " de deposito",
									new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Date()),
									"",
									ParametrosInventario.INVENTARIO_ABIERTO,
									ParametrosInventario.COD_LUGAR_INVENTARIO_DEPO);

							try {
								// Borrar datos del inventario
								bdd.borrarInventarioConArticulos(ParametrosInventario.ID_INV_DIN_VTA);
								bdd.borrarInventarioConArticulos(ParametrosInventario.ID_INV_DIN_DEP);
								// Crearlo de nuevo
								bdd.insertInventarioEnBdd(inventarioDinamicoVenta);
								bdd.insertInventarioEnBdd(inventarioDinamicoDepo);
								Toast.makeText(
										ctxt,
										"Se crearon los inventarios dinamicos nuevos",
										Toast.LENGTH_LONG).show();
							} catch (ExceptionBDD e) {

								log.log("[-- 1660 --]" + e.toString(), 4);
								// TODO Auto-generated catch block
								e.printStackTrace();
								Toast.makeText(
										ctxt,
										"Problema al borrar los inventarios de la BD"
												+ e.getMessage(),
										Toast.LENGTH_LONG).show();
							}

							Intent intentInventario = new Intent(ctxt,
									PaginaInventarioDinamico.class);
							intentInventario
									.putExtra(
											ParametrosInventario.extra_numeroInventario,
											ParametrosInventario.ID_INV_DIN_VTA);
							// intentInventario.putExtra(ParametrosInventario.extra_numeroInventarioDinDepo,
							// ParametrosInventario.ID_INV_DIN_DEP);
							// intentInventario.putExtra(ParametrosInventario.extra_bandera_invs_dinamicos,
							// ParametrosInventario.extra_valor_bandera_invs_dinamicos_si);

							startActivityForResult(
									intentInventario,
									ParametrosInventario.REQUEST_INVENTARIO_DINAMICO);
						}
					};

					// 1.1.2.2 En caso de que no quiera borrar, se vuelve a la
					// pantalla principal
					View.OnClickListener listenerNegativo = new View.OnClickListener() {

						public void onClick(View v) {
							log.log("[-- 1692 --]" + "Se presiono el boton no",
									0);
							// Lo que hace con el no
							// Debe preguntar nuevamente para eliminar el
							// inventario guardado
							// Abrir otra ventana y preguntar si realmente
							// quiere eliminar los datos

							dialogoBorrarInventarioDinamico.dismiss();

						}
					};

					dialogoBorrarInventarioDinamico = new DialogPersoComplexSiNo(
							ctxt,
							"Nuevo Inventario Dinamico",
							"Si genera un nuevo inventario se borrara cualquier " +
							"inventario dinamico en curso que se estuviera realizando aun no exportado.\nCUIDADO:"
									+ "Los datos no han sido exportados al sistema DEBO BackOffice para ser "
									+ "procesados para el correcto control de stock y se borraran",
							DialogPerso.ALERTAR, listenerPositivo,
							listenerNegativo);
//					dialogoBorrarInventarioDinamico = new DialogPersoComplexSiNo(
//							ctxt,
//							"Nuevo Inventario Dinmico",
//							"Esta seguro que desea generar un inventario dinmico nuevo?\nCUIDADO:"
//									+ "Los datos no han sido exportados al sistema DEBO BackOffice para ser "
//									+ "procesados para el correcto control de stock y se borraran",
//							DialogPerso.ALERTAR, listenerPositivo,
//							listenerNegativo);
					dialogoBorrarInventarioDinamico.show();
				}
			};
			// 1.1 Genera un dialog que pregunta si se continua con el
			// inventario o se
			// borra y genera algo nuevo
			dialogoContinuarInventario = new DialogPersoComplexSiNo(
					ctxt,
					"Continuar Inventario Dinamico",
					"Desea continuar trabajando con el inventario dinamico actual?",
					DialogPerso.VALIDAR, listenerPositivo, listenerNegativo);

			dialogoContinuarInventario.show();

		} else {
			// 2 Si no hay inventarios , Se deben crear 2 inventarios (uno para
			// venta y otro para deposito)
			// Logica de creacion de los inventarios dinamicos, las ponemos aca

			// Proceso de creacin del inventario nuevo:

			bdd = new BaseDatos(ctxt);

			// 2.1 Creo los objetos para los dos inventarios nuevos
			final Inventario inventarioDinamicoVenta = new Inventario(
					ParametrosInventario.ID_INV_DIN_VTA,
					"Inv. dinamico "
							+ String.valueOf(ParametrosInventario.ID_INV_DIN_VTA)
							+ " de venta", new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date()), "",
					ParametrosInventario.INVENTARIO_ABIERTO,
					ParametrosInventario.COD_LUGAR_INVENTARIO_VENTA);

			final Inventario inventarioDinamicoDepo = new Inventario(
					ParametrosInventario.ID_INV_DIN_DEP,
					"Inv. dinamico "
							+ String.valueOf(ParametrosInventario.ID_INV_DIN_DEP)
							+ " de deposito", new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date()), "",
					ParametrosInventario.INVENTARIO_ABIERTO,
					ParametrosInventario.COD_LUGAR_INVENTARIO_DEPO);

			// 2.2 Insertamos en la base de datos:
			try {
//				if (ParametrosInventario.InventariosVentas == true) {
				bdd.insertInventarioEnBdd(inventarioDinamicoVenta);
				bdd.insertInventarioEnBdd(inventarioDinamicoDepo);
				Toast.makeText(ctxt,
						"Se crearon los inventarios dinamicos nuevos",
						Toast.LENGTH_LONG).show();
				try {
					refreshTablaPrincipal();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ExceptionBDD e) {
				log.log("[-- 1769 --]" + e.toString(), 4);
				showSimpleDialogOK("Error",
						"La creacion del inventario fue cancelada").show();
			}

			// 2.3 Pasamos a la pantalla de administracion de inventario
			// dinamico
			Intent intentInventario = new Intent(ctxt,
					PaginaInventarioDinamico.class);
			intentInventario.putExtra(
					ParametrosInventario.extra_numeroInventarioDinVta,
					ParametrosInventario.ID_INV_DIN_VTA);
			// intentInventario.putExtra(ParametrosInventario.extra_numeroInventarioDinDepo,
			// ParametrosInventario.ID_INV_DIN_DEP);
			// intentInventario.putExtra(ParametrosInventario.extra_bandera_invs_dinamicos,
			// ParametrosInventario.extra_valor_bandera_invs_dinamicos_si);
			startActivityForResult(intentInventario,
					ParametrosInventario.REQUEST_INVENTARIO_DINAMICO);
		}

	}

	/**
	 * Refresca una linea de la tabla
	 */
	public void refreshLinea(@NonNull TableRow unaLinea) throws ExceptionBDD {
		try {
			int numeroInventario = unaLinea.getId()
					- ParametrosInventario.ID_LINEAS;
			// Consulta de la completud de la inventario:
			ArrayList<Integer> listaEstadisticas = bdd
					.selectEstadisticasConIdInventario(numeroInventario);
			int cantidadArticulosEnInventario = listaEstadisticas.get(0);
			int articulosYaContadosEnInventario = listaEstadisticas.get(1);
			int articulosNoContadosTodavia = listaEstadisticas.get(2);

			RelativeLayout unRelativeLayout = (RelativeLayout) unaLinea
					.getChildAt(1);
			ProgressBar unaProgressBar = (ProgressBar) unRelativeLayout
					.getChildAt(0);
			TextView unTextoDeProgressBar = (TextView) unRelativeLayout
					.getChildAt(1);
			TextView unTextoDeProgresion = (TextView) unaLinea.getChildAt(2);

			int newValue = (int) Math
					.floor((double) articulosYaContadosEnInventario
							/ (double) cantidadArticulosEnInventario
							* (double) 100);
			unaProgressBar.setProgress(newValue);

			unTextoDeProgressBar.setText(String.valueOf(newValue) + " %");

			unTextoDeProgresion.setText(String
					.valueOf(articulosYaContadosEnInventario)
					+ " / "
					+ String.valueOf(cantidadArticulosEnInventario));

			if (articulosNoContadosTodavia == 0) {
				unaLinea.setBackgroundColor(Color.GREEN);
			} else {
				unaLinea.setBackgroundColor(Color.TRANSPARENT);
			}
		} catch (Exception e) {

			log.log("[-- 1834 --]" + e.toString(), 0);
			e.printStackTrace();
		}
	}

	/**
	 * Verifica si estan todos los inventarios terminados en la BD para avisar
	 * que se exporten posteriormente
	 * 
	 * @return
	 * @throws ExceptionBDD
	 */
	private boolean estanTerminadosTodosLosInventarios() throws ExceptionBDD {
		boolean result = true;
		BaseDatos bdd = new BaseDatos(ctxt);
		listaInventariosSeleccionados = bdd.selectInventariosNumerosEnBdd();

		for (int numInventario : listaInventariosSeleccionados) {
			if (bdd.selectEstadisticasConIdInventario(numInventario).get(2) > 0) {
				result = false;
			}
		}

		return result;
	}

	/**
	 * Controla si se han medido todos los inventarios y si es necesario
	 * exportarlos
	 * 
	 * @throws ExceptionBDD
	 */
	private void controlFin() throws ExceptionBDD {
		if (estanTerminadosTodosLosInventarios()) {
			botonExportar.setEnabled(true);

			dialogoFin = new AlertDialog.Builder(this);
			dialogoFin
					.setTitle("Fin de las mediciones")
					.setMessage(
							"Todas los inventarios han sido procesados exitosamente.\n"
									+ "Por favor, dirijase hacia el central de control para descargar los datos de los inventarios al servidor.")
					.setCancelable(false)
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(@NonNull DialogInterface dialog,
                                                    int which) {
									dialog.dismiss();
								}
							});
			AlertDialog alert = dialogoFin.create();
			alert.show();
		}
	}

	/**
	 * Tarea asincronica de exportacion de los datos
	 * 
	 * @author GuillermoR
	 * 
	 */
	protected class ExportarDatos extends
			AsyncTask<Context, Integer, RespuestasExportar> {

		private static final boolean Referencia = false;

		/**
		 * Devuelve un objeto Respuestas exportar con info de la exportacion
		 * realiza la tarea de exportar los datos por WIFI
		 * <p>
		 * 1 Fabricamos la lista de todos los inventarios que estn cerrados
		 * <p>
		 * 2 se realiza la exportacin de los datos en las BD
		 * <p>
		 * 3 Exportamos los estados de los inventarios
		 * <p>
		 * 4 Si borrar estuvo activado se borra o si es dinamico el inventario
		 * <p>
		 * 5 Comprobamos que todo paso bien
		 * <p>
		 * 6 Exportamos las fotos
		 * <p>
		 * &nbsp; &nbsp;6.1 Verificamos que todo se hiso bien
		 * <p>
		 * 7 Exportamos los logs
		 * <p>
		 * &nbsp; &nbsp; 7.1 Verificamos que todo salio bien
		 */	

		@NonNull
        protected RespuestasExportar doInBackground(Context... arg0) {
			boolean result = true;
			System.out.println("::: PASO por InventarioM ProductosNoContabilizados " + ParametrosInventario.ProductosNoContabilizados);

			if (ParametrosInventario.ProductosNoContabilizados == 2) {
				ArrayList<Referencia> Referencias = new ArrayList<Referencia>();
				BaseDatos bd = new BaseDatos(ctxt);
				String fechaInicioInventario  = "";
				System.out.println("::: InventariosMainBoard 2367 RespuestasExportar " + inventarios_elegidos);
				int numero_inventario_elegido = inventarios_elegidos;
				if(inventarios_elegidos==-1 || inventarios_elegidos == -2){
					try {
						Inventario inven = bd.selectInventarioConNumero(-1);
					} catch (ExceptionBDD e1) {
					// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println("::: PASO por InventarioM 3 catch");
					}
					System.out.println("::: PASO por InventarioM 4");
					Referencias = bd.getArticulosAll();
					for (Referencia ref : Referencias) {
						try {
							System.out.println("::: PASO por InventarioM 5");
							Articulo articulo = bd.selectArticuloConCodigos(
									ref.getSector(), ref.getArticulo(), -1);
							if (articulo == null) {
								Date fechaHoy = new Date();
								ArrayList<String> codbar = new ArrayList<String>();
								ArrayList<String> codbarcompleto = new ArrayList<String>();
								String fechaYA = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
								System.out.println("::: InventarioMain sector 1");
								Articulo art = new Articulo(ref.getSector(),
								ref.getArticulo(), ref.getBalanza(), ref.getDecimales(), codbar, codbarcompleto , -1, ref.getDescripcion(), ref.getPrecio_venta(), ref.getPrecio_costo(), "", 0, ref.getExis_venta(), ref.getExis_deposito(), ref.getDepsn(), fechaInicioInventario,fechaYA);
								bd.insertArticuloEnBdd_conFechaFin(art);
							} else {
								System.out.println("::: InventarioMainBoard ");
								Log.e("Referencia con articulo", "No agregar "+ articulo.getDescripcion().toString());
							}

						} catch (ExceptionBDD e) {
							Toast.makeText(ctxt, "Error al recorrer las referencias", Toast.LENGTH_LONG).show();
							result = false;
						}
					}
				}else if(inventarios_elegidos >0){

				}
			}else{
				try {
					Inventario inven = bd.selectInventarioConNumeroParametro(-1,ParametrosInventario.ProductosNoContabilizados);
				} catch (ExceptionBDD e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("::: InventarioM PASO por  6");
			try {
				System.out.println("::: InventarioM 7 PASO ");
				RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG, new Date(), "MAIN BOARD", "0", "Export: 0 %");
			} catch (Exception e) {
		//		System.out.println("::: PASO por InventarioM 8");
				log.log("[-- 1943 --]" + e.toString(), 4);
				e.printStackTrace();
			}

			try {
				bdd = new BaseDatos(ctxt);
				// 1 Fabricamos la lista de todos los inventarios que estan cerrados:
				ArrayList<Integer> listaInventariosCerrados = null;
				try {
					listaInventariosCerrados = bdd.selectInventariosCerradosEnBdd();
				} catch (ExceptionBDD e4) {
					log.log("[-- 1959 --]" + e4.toString(), 4);
					e4.printStackTrace();
				}
				if (listaInventariosCerrados.size() <= 0) {
					Toast.makeText(ctxt, "Debe haber por lo menos un inventario cerrado", Toast.LENGTH_LONG).show();
					result = false;
				}
				try {
					// 2 se realiza la exportacin de los datos en las BD
					result = bdd.exportarTodasBaseDatosSQLite(listaInventariosCerrados);
				} catch (ExceptionHttpExchange e2) {
					log.log("[-- 1961 --]" + e2.toString(), 4);
					e2.printStackTrace();
					return new RespuestasExportar(RespuestasExportar.CODIGO_ERROR, "Error Export - Export a la BDD - Articulos - " + e2.toString());
				} catch (ExceptionBDD e) {
					log.log("[-- 1969 --]" + e.toString(), 0);
					e.printStackTrace();
					return new RespuestasExportar(RespuestasExportar.CODIGO_ERROR, "Error Export - Export a la BDD - Articulos - " + e.toString());
				}

				// 3 Exportamos los estados de los inventarios:
				try {
					if (result) {
						HttpSender httpSender = new HttpSender(Parametros.CODIGO_SOFT_DEBOINVENTARIO);
						for (int inventario : listaInventariosCerrados) {
							if (bdd.selectArticulosConNumeroInventario(
									inventario).size() <= 0) {
								result &= httpSender.send_liberacion(
										inventario, 0);
							} else {
								result &= httpSender.send_liberacion(inventario, 1);
							}

							// 4 A ver si borrar estuvo activado o no,o si es
							// dinamico el inventario:
							if (borrarDespues == true || inventario < 0) {
								bdd.borrarInventarioConArticulos(inventario);
							}
						}
					}
				} catch (ExceptionHttpExchange e2) {

					log.log("[-- 2001 --]" + e2.toString(), 4);
					e2.printStackTrace();
					return new RespuestasExportar(
							RespuestasExportar.CODIGO_ERROR, "Error Export - Export a la BDD - Inventarios - " + e2.toString());
				} catch (ExceptionBDD e) {
					log.log("[-- 2022 --]" + e.toString(), 4);

				} catch (Exception e3) {
					log.log("[-- 2024 --]" + e3.toString(), 4);
					e3.printStackTrace();
					return new RespuestasExportar(
							RespuestasExportar.CODIGO_ERROR, "Error Export - Export a la BDD - Inventarios - " + e3.toString());
				}
				int i = 1;

				// 5 Comprobamos que todo paso bien
				if (!result) {
					return new RespuestasExportar(RespuestasExportar.CODIGO_ERROR, "Error Export - Export a la BDD - Verifique la conexion a la red");
				}

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG, new Date(), "MAIN BOARD", "0", "Export: 20 %");
				} catch (Exception e) {
					log.log("[-- 2054 --]" + e.toString(), 4);
					e.printStackTrace();
				}

				i = 2;
				// 6 Exportamos las fotos:
				File carpetaFotos = new File(
						ParametrosInventario.URL_CARPETA_FOTOS);
				i = 3;
				if (carpetaFotos.listFiles().length > 0) {
					i = 4;
					HttpSender senderFotos;
					try {
						senderFotos = new HttpSender(Parametros.CODIGO_SOFT_DEBOINVENTARIO);
					} catch (ExceptionHttpExchange e) {

						log.log("[-- 2046 --]" + e.toString(), 4);
						e.printStackTrace();
						return new RespuestasExportar(RespuestasExportar.CODIGO_WARNING, "Error Export - Export imagenes - " + e.toString());
					}

					for (File unaFoto : carpetaFotos.listFiles()) {
						result &= senderFotos.send_foto(ParametrosInventario.URL_CARPETA_FOTOS + unaFoto.getName());
					}
				}
				i = 5;
				// 6.1 Comprobar que todo esta bien
				if (result == false) {
					return new RespuestasExportar(RespuestasExportar.CODIGO_WARNING, "Error Export - Export imagenes");
				}
				i = 6;

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG, new Date(), "MAIN BOARD", "0", "Export: 40 %");
				} catch (Exception e) {
					e.printStackTrace();

					log.log("[-- 2075 --]" + e.toString(), 4);
				}

				// 7 Exportamos los logs:
				File archivoLOG = new File(ParametrosInventario.URL_ARCHIVO_LOG);
				i = 7;
				if (archivoLOG.exists()) {
					HttpSender senderLOG;
					try {
						senderLOG = new HttpSender(Parametros.CODIGO_SOFT_DEBOINVENTARIO);
						result = senderLOG.send_txt(ParametrosInventario.URL_ARCHIVO_LOG);
					} catch (ExceptionHttpExchange e) {

						e.printStackTrace();
						return new RespuestasExportar(RespuestasExportar.CODIGO_WARNING, "Error Export - Export logs - " + e.toString());
					}
					i = 8;

					// 7.1 Comprobar que todo esta bien:
					if (result == false) {
						return new RespuestasExportar(RespuestasExportar.CODIGO_WARNING, "Error Export - Export logs");
					}
					i = 9;
					// archivoLOG.delete();
					i = 10;
				}

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG, new Date(), "MAIN BOARD", "0", "Export: 60 %");
				} catch (Exception e) {

					log.log("[-- 2114 --]" + e.toString(), 4);
					e.printStackTrace();
				}

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG, new Date(), "MAIN BOARD", "0", "Export: 90 %");
				} catch (Exception e) {
					log.log("[-- 2136 --]" + e.toString(), 4);
					e.printStackTrace();
				}

			} catch (Exception e) {
				return new RespuestasExportar(RespuestasExportar.CODIGO_ERROR, "Error Export - " + " - Error fatal - " + e.toString());
			}

			return new RespuestasExportar(RespuestasExportar.CODIGO_OK,
					"Operacion Realizada con Exito");
		} // end doInBackground

		/**
		 * Tareas de aviso cuando se finalizo la tarea asincronica, segun
		 * resultados
		 */

		protected void onPostExecute(@NonNull RespuestasExportar result) {
			super.onPostExecute(result);

			// Terminamos la exportacion con un mesaje + refresh:
			cerrarMenuEspera();
			desactivarWifi();

			if (result.getCodigoError() == RespuestasExportar.CODIGO_OK) {
				showSimpleDialogOK("Exportacion Exitosa",
						"La exportacion se realizo con exito.").show();

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG, new Date(), "MAIN BOARD", "0", "Export: 100 % --- EXITOSO");
				} catch (Exception e) {
					log.log("[-- 2170 --]" + e.toString(), 4);
					e.printStackTrace();
				}

				try {
					refreshTablaPrincipal();
				} catch (ExceptionBDD e) {
					log.log("[-- 2176 --]" + e.toString(), 4);
					e.printStackTrace();
				} catch (Exception e) {
					log.log("[-- 2178 --]" + e.toString(), 4);
					e.printStackTrace();
				}

				// Registro.log(ParametrosSancion.URL_ARCHIVO_LOG, new Date(),
				// nombreClase, String.valueOf(operadorId),
				// "Exportar datos 100%");
				// finish();
			} else {
				if (result.getCodigoError() == RespuestasExportar.CODIGO_ERROR) {
					showSimpleDialogOK("Exportacion Cancelada", "Explicacion: \n\n" + result.getMensaje()).show();
				} else if (result.getCodigoError() == RespuestasExportar.CODIGO_WARNING) {
					showSimpleDialogOK("Exportacion Finalizada con Advertencias", "Explicacion: \n\n" + result.getMensaje()).show();
				}
				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG, new Date(), "MAIN BOARD", "0", "Export: 100 % --- " + result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Lanza un progres dialog de espera
	 */
	private void lanzarMenuEspera() {
		popupEspera = new ProgressDialog(ctxt);
		popupEspera.setCancelable(false);
		popupEspera.setMessage("Exportando los datos...");
		popupEspera.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		popupEspera.show();
	}

	/**
	 * Cierra el menu de espera
	 */
	private void cerrarMenuEspera() {
		popupEspera.dismiss();
	}

	/**
	 * Muestra un dialog de OK
	 */

	public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {
		log.log("[-- 2215 --]" + "titulo: " + titulo + ", \n mensaje: "
				+ mensaje, 3);
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		return dialogoSimple.create();
	}

	/**
	 * Muestra un dialog Si o No
	 */

	public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje,
                                            @Nullable final Class<?> clase) {
		log.log("[-- 2233 --]" + "titulo: " + titulo + ", \n mensaje: "
				+ mensaje, 3);
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple
				.setCancelable(false)
				.setTitle(titulo)
				.setMessage(mensaje)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						bdd = new BaseDatos(ctxt);

						log.log("[-- 2243 --]" + "Acepto sino", 2);

						// Si CLASE = null, es un EXPORT
						if (clase == null) {
							int contador = 3;

							try {
								do {
									export_usb(false);
									contador--;
								} while (!control_buena_exportacion()
										&& contador >= 0);
							} catch (ExceptionBDD e) {

								log.log("[-- 2257 --]" + e.toString(), 4);
								showSimpleDialogOK(
										"ERROR DE EXPORTACION",
										"Generacion del documento XML imposible: "
												+ e.getComentario()).show();
								return;
							} catch (Exception e) {
								log.log("[-- 2264 --]" + e.toString(), 4);
								showSimpleDialogOK(
										"ERROR DE EXPORTACION",
										"Imposible exportar en el "
												+ Dispositivo_Export
												+ ". Verifique que este "
												+ "correctamente conectado")
										.show();
								return;
							}

							if (contador < 0) {
								showSimpleDialogOK(
										"ERROR DE EXPORTACION",
										"Los archivos XML de exportacion no han podido ser creados con exito. "
												+ "Verifique su correctitud")
										.show();
								return;
							}

							cerrarMenuEspera();

						}
						// Si CLASE es not NULL, es import:
						else {
							Intent intentUSB = new Intent(
									InventarioMainBoard.this, UsbProvider.class);
							intentUSB.putExtra(Parametros.extra_uri_usb,
									Parametros.PREF_USB_IMPORT);
							startActivity(intentUSB);
							finish();
						}
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.cancel();
						log.log("[-- 2301 --]" + "Cancelo sino", 2);
					}
				});
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}

	/**
	 * Activa el WIFI para usarlo en caso de ser necesario
	 */

	public void activarWifi() {
		WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		ConnectivityManager wifiConexion = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		//NetworkInfo wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		assert wifiConexion != null;
		wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo wifiInfo;
		//wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!wifiInfo.isConnected()) {

			Settings.System.putInt(ctxt.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", false);
			sendBroadcast(intent);

			wifiManager.setWifiEnabled(true);
		}
		log.log("[-- 2329 --]" + "Se activo el Wifi", 2);
	}

	/**
	 * Desactiva el WIFI
	 */

	public void desactivarWifi() {
		// if (estaEnModoAvion() == false) {
		// // toggle airplane mode
		// Settings.System.putInt(ctxt.getContentResolver(),
		// Settings.System.AIRPLANE_MODE_ON, 1);
		//
		// // Post an intent to reload
		// Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		// intent.putExtra("state", true);
		// sendBroadcast(intent);
		//
		// WifiManager wifiManager = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		// wifiManager.setWifiEnabled(false);
		//
		// log.log("[-- 2350 --]" + "Se dessactiva el Wi fi", 4);
		// }
	}

	/**
	 * Verificacion de modo Avion
	 */

	public boolean estaEnModoAvion() {
		return (Settings.System.getInt(ctxt.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) == 1);
	}

	/**
	 * Funcion accesoria para copiar un archivo de origen en uno de destino
	 * 
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	private void copyFile(@NonNull File sourceFile, @NonNull File destFile) throws IOException {
		if (!sourceFile.exists()) {
			return;
		}
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}
	}

	/**
	 * Funcion principal de exportacion de datos por USB (MUY IMPORTANTE)
	 * 
	 * @param borrar_despues
	 *            <p>
	 *            1 Vaciar la carpeta de exportacion
	 *            <p>
	 *            2 Fabricamos la lista de todos los inventarios que estn
	 *            cerrados
	 *            <p>
	 *            3 Llama al proceso de exportacion de las BD al pendrive, esto
	 *            genera un archivo o varios XML de export en la carpeta
	 *            data/data/com.foca.deboInventario/usb/export
	 *            <p>
	 *            4 Copiamos al pen drive todos los archivos XML presentes en
	 *            la carpeta de EXPORT, al pendrive y verifica el proceso
	 *            <p>
	 *            5 Se verifica la correcta exportacin, si es positiva se
	 *            borran los inventarios
	 * @throws ExceptionBDD
	 * @throws Exception
	 */
	private void export_usb(boolean borrar_despues) throws ExceptionBDD,

	Exception {
		// Para prueba descomentar la linea siguiente para que encuentre los
		// archivos
		// ParametrosInventario.PREF_USB_EXPORT =
		// "data/data/com.foca.deboInventario/test/";
		// 1 Vaciar la carpeta de exportacion
		vaciarCarpetaExportacion();
		bdd = new BaseDatos(ctxt);
		String texto_error = "";
		boolean estado_exportacion = true;
		Dispositivo_Export = TipoDispositivoExport();
		Dispositivo_Export = TipoDispositivoImport();

		// 2 Fabricamos la lista de todos los inventarios que estn cerrados:
		ArrayList<Integer> listaInventariosCerrados = bdd
				.selectInventariosCerradosEnBdd();
		if (listaInventariosCerrados.size() <= 0) {
			log.log("[-- 2433 --]"
					+ "Debe haber por lo menos un inventario cerrado", 3);
			Toast.makeText(ctxt,
					"Debe haber por lo menos un inventario cerrado",
					Toast.LENGTH_LONG).show();
			return;
		}

		/**
		 * 3 Llama al proceso de exportacion de las BD al pendrive, esto genera
		 * un archivo o varios XML de export en la carpeta
		 * data/data/com.foca.deboInventario/usb/export
		 */
		
		
		if (ParametrosInventario.ProductosNoContabilizados == 2) {


			ArrayList<Referencia> Referencias = new ArrayList<Referencia>();
			BaseDatos bd = new BaseDatos(ctxt);
			Referencias = bd.getArticulosAll();
			for (Referencia ref : Referencias) {
				try {
					Articulo articulo = bd.selectArticuloConCodigos(
							ref.getSector(), ref.getArticulo(), -1);

					if (articulo == null) {
						ArrayList<String> codbar = new ArrayList<String>();
						ArrayList<String> codbarcompleto = new ArrayList<String>();
						codbar.add(ref.getCodigo_barra());
						codbarcompleto.add(ref.getCodigo_barra_completo());
						Articulo art = new Articulo(ref.getSector(),
								ref.getArticulo(),
								ref.getBalanza(), 
								ref.getDecimales(), 
								codbar,
								codbarcompleto, -1,
								ref.getDescripcion(),
								ref.getPrecio_venta(),
								ref.getPrecio_costo(), "", 0,0,
								ref.getExis_venta(),
								ref.getExis_deposito(),
								ref.getDepsn(),
								"");
						bd.insertArticuloEnBdd(art);
					} else {
					//	Log.e("Referencias con articulos", "No agregar "
				//				+ articulo.getDescripcion().toString());
					}

				} catch (ExceptionBDD e) {
					Toast.makeText(ctxt,
							"Error al recorrer las referencias",
							Toast.LENGTH_LONG).show();
					
				}

			}
		}
		
		
		
		
		bdd.exportarTodasBaseDatosSQLite_HaciaUsb(listaInventariosCerrados);

		/**
		 * 4 Copiamos al pen drive todos los archivos XML presentes en la
		 * carpeta de EXPORT, al pendrive y verifica el proceso
		 */

		File carpeta_fuente = new File(
				ParametrosInventario.URL_CARPETA_USB_EXPORT);
		for (File archivo : carpeta_fuente.listFiles()) {
			File carpeta_destino = new File(
					ParametrosInventario.CARPETA_ATABLET);
			File archivo_destino = new File(
					ParametrosInventario.CARPETA_DESDETABLET
							+ archivo.getName());
			// boolean existecarpetaDest=carpeta_destino.exists();
			// boolean existearchivo=archivo.exists();
			try {
				if (carpeta_destino.exists() == true
						&& archivo.exists() == true) {
					// Puede arrojar la IOExc
					archivo_destino.createNewFile();
					// Puede arrojar la IOExc
					copyFile(archivo, archivo_destino);
					archivo.delete();
				} else {
					texto_error = "Imposible encontrar la carpeta de destino: "
							+ carpeta_destino.getPath();
					estado_exportacion = false;
					// Deberiamos crear la carpeta de destino y hacer todo lo
					// anterior
					// carpeta_destino.mkdirs();
					// archivo_destino.createNewFile();
					// copyFile(archivo, archivo_destino);
					archivo.delete();
				}
			} catch (IOException e) {
				// Genera este error
				texto_error = "Un error occurio al momento de copiar los archivos al"
						+ Dispositivo_Export;
				estado_exportacion = false;
			}

			// Control directo sobre el archivo generado:
			if (carpeta_destino.exists() == false) {
				estado_exportacion = false;
			}
		}

		// 5 Se verifica la correcta exportacion, si es positiva se borran los
		// inventarios
		if (estado_exportacion == true && control_buena_exportacion() == true) {
			// Los borramos si nos han marcado o si son dinamicos
			for (int num_inv : listaInventariosCerrados) {
				if (borrar_despues == true || num_inv < 0) {

					bdd.borrarInventarioConArticulos(num_inv);
				}
			}

			showSimpleDialogOK(
					"Exportacion exitosa",
					"Datos correctamente exportados en el "
							+ Dispositivo_Export).show();
		}// Solucion temporaria 08/05/2012
			// else if ()
		else {
			// Se saca para que no moleste despues
			// showSimpleDialogOK("Error", texto_error +
			// "\n Es posible que los archivos de exportacin hayan sido mal generados.\n\n"
			// +
			// "Por favor reintente.").show();
			// Borrar los archivos de la carpeta desdeTablet
			// File carpeta_destino = new
			// File(ParametrosInventario.PREF_USB_EXPORT);
			// for(File archivoDest:carpeta_destino.listFiles()) {
			// archivoDest.delete();
			// }
		}

		cerrarMenuEspera();
		refreshTablaPrincipal();
	}

	/**
	 * Devuelve una informacin sobre el tamao de los archivos (aqui los
	 * archivos de exportacin). Si los archivos son de tamaos inferiores a 1
	 * bytes, se considera que el archivo esta vacio.
	 * 
	 * @return (boolean) TRUE si los archivos tienen un tamao aceptable, FALSE
	 *         sino.
	 */
	private boolean control_buena_exportacion() {

		File carpeta_destino = new File(
				ParametrosInventario.CARPETA_DESDETABLET);
		boolean respuesta = true;

		// Controlamos la integridad de todos los archivos de export
		for (File archivo : carpeta_destino.listFiles()) {
			// No funciona esto con archivos de menos de 1 byte por lo menos en
			// el emulador
			Long tamaoArchivo = new Long(archivo.length());
			int resultadoComparacion = tamaoArchivo.compareTo(new Long(1L));
			int comparativa = -1;
			boolean esMenor = (resultadoComparacion == comparativa);
			if (esMenor) {
				respuesta = false;
				break;

			}
		}

		return respuesta;
	}

	/**
	 * Funcin accesoria para vaciar la carpeta de exportacion del pendrive
	 */
	private void vaciarCarpetaExportacion() {
		File carpeta_destino = new File(
				ParametrosInventario.CARPETA_DESDETABLET);

		for (File archivo : carpeta_destino.listFiles()) {
			// No funciona esto con archivos de menos de 1 byte por lo menos en
			// el emulador
			archivo.delete();
		}

	}

	/**
	 * Verifica que los inventarios tengan articulos
	 */
	private void control_integridad_tablas() {
		try {
			System.out.println("::: InventarioMainBoard 2769 verifica inventario con articulos");
			BaseDatos bdd = new BaseDatos(ctxt);
			
			boolean condicionRadio = ParametrosInventario.InventariosVentas;
			System.out.println("::: InventarioMainBoard 3062 condicionRadio " + condicionRadio);
			
			if(condicionRadio == true){
				// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
				condR=-1;
			}else{
				// Esta seleccionado deposito, esto debe continuar sin los campos de ventas
				condR=-2;
			}
			System.out.println("::: InventarioMainBoard 3062 condR == " + condR);
			
			

			ArrayList<Integer> lista_numeros_inventarios = bdd
					.selectInventariosNumerosEnBdd();

			if (lista_numeros_inventarios != null) {
				for (int id_inv : lista_numeros_inventarios) {
					System.out.println("::: InventarioMainBoard 2769 " + lista_numeros_inventarios);
					System.out.println("::: InventarioMainBoard 2769 " + 
							bdd.selectArticulosCodigosConNumeroInventario(
									id_inv));
					if (id_inv >= 0
							&& bdd.selectArticulosCodigosConNumeroInventario(
									id_inv).size() <= 0) {
						bdd.borrarInventarioConArticulos(id_inv);
					}
				}
			}
		} catch (ExceptionBDD e) {
			e.printStackTrace();
			log.log("[-- 2594 --]" + e.toString(), 4);
		}
	}

	/**
	 * Funcion accesoria que devuelve el minimo indice de una lista?
	 * 
	 * @param lista
	 * @return
	 */
	private int min(@NonNull ArrayList<Integer> lista) {
		int mini = lista.get(0);
		for (int i : lista) {
			if (i < mini) {
				mini = i;
			}
		}
		return mini;
	}

} // end class

