package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Activity para gestionar la importaci�n de inventarios por USB.
 * 
 * @author GuillermoR
 * 
 */
public class UsbProvider extends Activity implements DialogPersoSimple {

	@NonNull
    private Context ctxt = this;

	@Nullable
    private String uri_usb;
	private TableLayout tabla;
	private Button boton_volver, boton_seguir, boton_refresh;
	private TextView titulo;

	@NonNull
    private ArrayList<Integer> inventarios_seleccionados = new ArrayList<Integer>();
	// private LinkedList<String> colaSQL = new LinkedList<String>();
	// private final static Object locker = new Object();
	// private boolean terminado = false;

	private ProgressDialog popupEspera;

	private final static int COLUMNA_ID = 1;
	private final static int COLUMNA_CANTIDAD = 4;
	private final static int COLUMNA_PATH = 5;

	@NonNull
    GestorLogEventos log = new GestorLogEventos();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_usbstream);

		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("Inicio de UsbProvider", 2);
		LinearLayout ll = (LinearLayout) findViewById(R.id.USB_root);
		ll.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.fondo_recepcion_inventario));

		// BUNDLES:
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			uri_usb = bundle.getString(Parametros.extra_uri_usb);
			// uri_usb = "data/data/com.foca.deboInventario/test/";
		} else {
			uri_usb = "/udisk/deboInventario/aTablet/";
			// uri_usb = "data/data/com.foca.deboInventario/test/";
		}

		// if(ParametrosInventario.MODO_DEBUG) {
		// uri_usb = "data/data/com.foca.deboInventario/test/";
		// }

		// CARGA ELEMENTOS GRAFICOS:
		tabla = (TableLayout) findViewById(R.id.USB_tabla);

		boton_volver = (Button) findViewById(R.id.USB_boton_precedente);
		boton_seguir = (Button) findViewById(R.id.USB_boton_siguiente);
		boton_refresh = (Button) findViewById(R.id.USB_boton_refrescar);

		titulo = (TextView) findViewById(R.id.USB_titulo);
		titulo.setText("Lista de Inventarios");

		// Construir la interfaz visual:
		refreshUI();

		// SET HANDLERS AND LISTENERS ON BUTTONS:
		boton_seguir.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				log.log("[-- 122 --]" + "Se presiono boton seguir", 0);
				System.out.println("USBProvider 123 Lanzar menu en espera");
				lanzarMenuEspera();

				// Puede ser necesario llamar a un metodo secuencial en vez de
				// esto en background
				// cargarDatosEnBdd(inventarios_seleccionados);
				CargarDatosFromUSBtoBDD unaCargaFromUSBtoBDD = new CargarDatosFromUSBtoBDD();
		//		System.out.println("USBProvider 129 unaCargaFromUSB: " 
	//					+ unaCargaFromUSBtoBDD);

				unaCargaFromUSBtoBDD.execute(ctxt);
				
			}
		});

		boton_volver.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				log.log("[-- 138 --]" + "Se presiono boton volver", 0);
				Intent intentMB = new Intent(UsbProvider.this,
						InventarioMainBoard.class);
				startActivity(intentMB);
				finish();
			}
		});

		boton_refresh.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
	//			System.out.println("::: USBProvider 153 boton refrescar");
				log.log("[-- 150 --]" + "Se presiono boton refrescar", 0);
				refreshUI();
				inventarios_seleccionados = new ArrayList<Integer>();
				boton_seguir.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void refreshUI() {
		try {
			for (int l = tabla.getChildCount() - 1; l > 0; l--) {
				tabla.removeViewAt(l);
			}

			File fileUSB = new File(uri_usb);

			if (fileUSB.exists() == true) {
				if (fileUSB.isDirectory() == true) {

					File[] listaTodosArchivos = fileUSB.listFiles();

					for (int i = 0; i < listaTodosArchivos.length; i++) {
						File archivo = listaTodosArchivos[i];

						PreScanXML preScanner = new PreScanXML();
						preScanner.scan(archivo);

						TableRow tr = new TableRow(ctxt);
						CheckBox chkbox = new CheckBox(ctxt);
						TextView tv1 = new TextView(ctxt);
						TextView tv2 = new TextView(ctxt);
						TextView tv3 = new TextView(ctxt);
						TextView tv4 = new TextView(ctxt);
						TextView tv5 = new TextView(ctxt); // path del archivo
															// (VISIBILITY ==
															// GONE)

						chkbox.setClickable(false);
						tr.addView(chkbox);

						tv1.setText(preScanner.numero);
						tv1.setTextColor(Color.BLACK);
						tv1.setGravity(Gravity.CENTER_HORIZONTAL);
						tr.addView(tv1);

						tv2.setText(preScanner.descripcion);
						tv2.setTextColor(Color.BLACK);
						tv2.setGravity(Gravity.CENTER_HORIZONTAL);
						tr.addView(tv2);

						tv3.setText(preScanner.fecha);
						tv3.setTextColor(Color.BLACK);
						tv3.setGravity(Gravity.CENTER_HORIZONTAL);
						tr.addView(tv3);

						tv4.setText(preScanner.cantidad);
						tv4.setTextColor(Color.BLACK);
						tv4.setGravity(Gravity.CENTER_HORIZONTAL);
						tr.addView(tv4);

						tv5.setText(archivo.getAbsolutePath());
						tv5.setVisibility(View.GONE);
						tr.addView(tv5);

						tr.setOnClickListener(new View.OnClickListener() {

							public void onClick(View view) {
								TableRow linea = (TableRow) view;
								CheckBox checkBox = (CheckBox) linea
										.getChildAt(0);

								if (checkBox.isChecked() == false) {
									checkBox.setChecked(true);
									TextView ttvv = (TextView) linea
											.getChildAt(COLUMNA_ID);
									inventarios_seleccionados.add(Integer
											.parseInt(String.valueOf(ttvv
													.getText())));
									boton_seguir.setVisibility(View.VISIBLE);
								} else {
									checkBox.setChecked(false);
									TextView ttvv = (TextView) linea
											.getChildAt(COLUMNA_ID);
									inventarios_seleccionados.remove((Object) Integer
											.parseInt(String.valueOf(ttvv
													.getText())));
									if (inventarios_seleccionados.size() <= 0) {
										boton_seguir
												.setVisibility(View.INVISIBLE);
									}
								}
							}
						});

						tabla.addView(tr);
					} // end for
				} // end if
			} else {
				showSimpleDialogOK(
						"Error",
						"No se encuentra el dispositivo "
								+ ParametrosInventario.Dispositivo_Import
								+ " para la importacion").show();
			}

		} catch (Exception e) {

			log.log("[-- 257 --]" + e.toString(), 4);
			showSimpleDialogOK("Error", e.toString()).show();
		}
	}

	public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {

		log.log("[-- 264 --]" + "titulo: " + titulo + ", \n mensaje: "
				+ mensaje, 3);
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}

	@Nullable
    public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje,
                                            Class<?> clase) {
		log.log("[-- 278 --]" + "titulo: " + titulo + ", \n mensaje: "
				+ mensaje, 3);
		return null;
	}

	// Podria ser necesario reactivarlo?
	/*
	 * private void cargarDatosEnBdd(ArrayList<Integer> numeros_inventarios)
	 * throws ExceptionBDD { // Recuperamos el pedazo que corresponde a este
	 * inventario: String path = ""; lanzarMenuEspera();
	 * 
	 * for (int n = 1 ; n < tabla.getChildCount() ; n++) { TableRow linea =
	 * (TableRow) tabla.getChildAt(n); int num_invent =
	 * Integer.parseInt(String.valueOf
	 * (((TextView)linea.getChildAt(1)).getText()));
	 * 
	 * if (numeros_inventarios.contains(num_invent) == true) { path =
	 * String.valueOf(((TextView)linea.getChildAt(COLUMNA_PATH)).getText());
	 * 
	 * File archivo = new File(path);
	 * 
	 * if (archivo.exists() == true && archivo.isFile() == true) { try { //
	 * Copiamos el archivo desde el Pen Drive hasta la tablet: (para limitar los
	 * accesos USB): File carpeta_import = new
	 * File(ParametrosInventario.URL_CARPETA_USB_IMPORT); File archivo_destino =
	 * new File(ParametrosInventario.URL_CARPETA_USB_IMPORT +
	 * archivo.getName());
	 * 
	 * if (carpeta_import.exists() == false) { archivo.mkdirs();
	 * archivo.createNewFile(); } else { if (archivo_destino.exists() == true) {
	 * archivo_destino.delete(); } archivo_destino.createNewFile(); }
	 * 
	 * copyFile(archivo, archivo_destino);
	 * 
	 * 
	 * // Analisamos y partimos el archivo XML: ParserXML parseador = new
	 * ParserXML(); parseador.parser(archivo);
	 * 
	 * // Borramos memoria BDD: BaseDatos bdd = new BaseDatos(ctxt); //try { //
	 * bdd.destruirYReconstruir(); //} catch (ExceptionBDD e) { //
	 * e.printStackTrace(); //}
	 * 
	 * // Agregamos inventario: Inventario inventario = new Inventario(
	 * Integer.parseInt
	 * (parseador.cabecera.get(ParametrosInventario.bal_bdd_inventario_numero)),
	 * parseador
	 * .cabecera.get(ParametrosInventario.bal_bdd_inventario_descripcion),
	 * parseador.cabecera.get(ParametrosInventario.bal_bdd_inventario_fecha),
	 * 1//Integer.parseInt(parseador.cabecera.get(ParametrosInventario.
	 * bal_bdd_inventario_estado)) ); bdd.insertInventarioEnBdd(inventario);
	 * 
	 * // Agregamos los articulos:
	 * //
	 * // WARNING
	 * //// Algunos
	 * articulos pueden figurar 2 veces o mas en la lista de los articulos //
	 * por tener codigos de barras diferentes. Hay que fusionarlos! // Eso se
	 * hace al nivel de la base de datos: cuando agregamos un articulo, //
	 * controlamos si uno ya existe, y si es el caso, le actualizamos el campo
	 * de // codigo de barra solamente. for (HashMap<String,String>
	 * articulo_cabeza: parseador.detallesArticulos.keySet()) {
	 * HashMap<String,String> articulo_cuerpo =
	 * parseador.detallesArticulos.get(articulo_cabeza);
	 * 
	 * Articulo articulo = new Articulo(
	 * Integer.parseInt(articulo_cabeza.get(ParametrosInventario
	 * .bal_bdd_articulo_sector)),
	 * Integer.parseInt(articulo_cabeza.get(ParametrosInventario
	 * .bal_bdd_articulo_codigo)), new
	 * ArrayList<String>(Arrays.asList(articulo_cuerpo
	 * .get(ParametrosInventario.bal_bdd_articulo_codigo_barra))),
	 * Integer.parseInt
	 * (articulo_cabeza.get(ParametrosInventario.bal_bdd_articulo_inventario)),
	 * articulo_cuerpo.get(ParametrosInventario.bal_bdd_articulo_descripcion),
	 * Double.parseDouble(articulo_cuerpo.get(ParametrosInventario.
	 * bal_bdd_articulo_precio_venta)),
	 * Double.parseDouble(articulo_cuerpo.get(ParametrosInventario
	 * .bal_bdd_articulo_precio_costo)),
	 * articulo_cuerpo.get(ParametrosInventario.bal_bdd_articulo_foto),
	 * Integer.parseInt
	 * (articulo_cuerpo.get(ParametrosInventario.bal_bdd_articulo_cantidad)),
	 * articulo_cuerpo.get(ParametrosInventario.bal_bdd_articulo_fecha) );
	 * 
	 * bdd.insertArticuloEnBdd(articulo); }
	 * 
	 * // Al final borramos el archivo: archivo.delete(); } catch (Exception e)
	 * { e.printStackTrace(); } } // END of IF (file exists) }// END of IF (num
	 * del inventario en la lista) }// END of FOR cerrarMenuEspera(); }// END of
	 * CARGAR DATOS EN BDD
	 */

	// Creamos el thread que va a ejecutar el trabajo pesado (en una nueva
	// clase):
	protected class CargarDatosFromUSBtoBDD extends
			AsyncTask<Context, Integer, String> {
		
		
		// Puede ser necesario meterlo en un solo metodo de clase para que no
		// haga lio, por que a veces da error

		@NonNull
        protected String doInBackground(Context... arg0) {
		//	db.execSQL("UPDATE Usuarios SET nombre='usunuevo' WHERE codigo=6 ");
			int step = 0;
			String error = "";
			boolean todosCorrectos = true;
			System.out.println("::: USBProvider 388 doInBackground ");
			try {
				// Recuperamos el pedazo que corresponde a este inventario:
				String path = "";

				for (int n = 1; n < tabla.getChildCount(); n++) {
					TableRow linea = (TableRow) tabla.getChildAt(n);
					int num_invent = Integer
							.parseInt(String.valueOf(((TextView) linea
									.getChildAt(1)).getText()));

					if (inventarios_seleccionados.contains(num_invent) == true) {
						path = String.valueOf(((TextView) linea
								.getChildAt(COLUMNA_PATH)).getText());

						File archivo = new File(path);

						if (archivo.exists() == true
								&& archivo.isFile() == true) {
							try {
								// Copiamos el archivo desde el Pen Drive hasta
								// la tablet: (para limitar los accesos USB):
								File carpeta_import = new File(
										ParametrosInventario.URL_CARPETA_USB_IMPORT);
								if (carpeta_import.exists() == false) {
									carpeta_import.mkdir();
									log.log("[-- 411 --]"
											+ "carpeta import creada", 3);
								}
								log.log("carpeta_import = "
										+ carpeta_import.getPath(), 3);
								log.log("carpeta_import = " + archivo.getName(),
										3);
								File archivo_destino = new File(

								carpeta_import.getPath() + "/"
										+ archivo.getName());

//								if (archivo_destino.exists() == false) {
//									archivo_destino.mkdir();
//									
//								} else {
//									archivo_destino.delete();
//								}
								log.log("archivo = " + archivo.toString(), 3);
								log.log("archivo_destino = "
										+ archivo_destino.toString(), 3);
								if (archivo_destino.exists() == false) {
									log.log("destino antes del copy = false", 3);
								} else {
									log.log("destino antes del copy = true", 3);
								}

								copyFile(archivo, archivo_destino);

								if (archivo_destino.exists() == false) {
									log.log("destino despues= false", 3);
								} else {
									log.log("destino = true", 3);
								}

								// Nos dormimos para dejar el tiempo la tablet
								// de copiar bien el archivo a la memoria
								// interna:
								SystemClock.sleep(300);

								// Creacion parseador:
								ParserImportarXML parseador = new ParserImportarXML();

								/*
								 * // Creamos un thread de trabajo: Thread
								 * thread = new Thread(new Runnable() {
								 * 
								 * public void run() { while (terminado ==
								 * false) { boolean b = true; BaseDatos bdd =
								 * new BaseDatos(ctxt);
								 * 
								 * 
								 * synchronized (locker) { b =
								 * colaSQL.isEmpty(); }
								 * 
								 * if (b == true) { try { synchronized (this) {
								 * this.wait(100); } } catch
								 * (InterruptedException e) {} } else { String
								 * unaConsultaSQL = ""; synchronized (locker) {
								 * unaConsultaSQL = colaSQL.poll(); } try {
								 * bdd.insertDesdeUSBEnBdd(unaConsultaSQL); }
								 * catch (ExceptionBDD e) {} } } } });
								 * thread.start();
								 */

								// Analisamos y partimos el archivo XML:
								// ParserImportarXML parseador = new
								// ParserImportarXML();
								parseador
										.parser_importer(
												archivo_destino,
												Integer.parseInt(String
														.valueOf(((TextView) linea
																.getChildAt(COLUMNA_CANTIDAD))
																.getText())));

								try {
									BaseDatos bdd = new BaseDatos(ctxt);
									ArrayList<String> sentenciaSQL = parseador.listaSQLprocesar;
									System.out.println("::: UsbProvider 492 sentenciaSQL = "
											+ sentenciaSQL);
									System.out.println("::: UsbProvider antes de insertarnUsb");
									bdd.insertDesdeUSBEnBdd(sentenciaSQL);
									System.out.println("::: UsbProvider 495 bdd = "+ bdd);
									bdd.close();
								} catch (ExceptionBDD exc) {
									return exc.toString();
								}

								setMenuValue(100);

								/*
								 * // Agregamos inventario: Inventario
								 * inventario = new Inventario(
								 * Integer.parseInt(
								 * parseador.cabecera.get(ParametrosInventario
								 * .bal_bdd_inventario_numero)),
								 * parseador.cabecera.get(ParametrosInventario.
								 * bal_bdd_inventario_descripcion),
								 * parseador.cabecera.get(ParametrosInventario.
								 * bal_bdd_inventario_fecha),
								 * 1//Integer.parseInt
								 * (parseador.cabecera.get(ParametrosInventario
								 * .bal_bdd_inventario_estado)) );
								 * bdd.insertInventarioEnBdd(inventario);
								 * 
								 * 
								 * // Agregamos los articulos:
								 * //
								 *
								 * // WARNING
								 *
								 * //
								 * //
								 * Algunos articulos pueden figurar 2 veces o
								 * mas en la lista de los articulos // por tener
								 * codigos de barras diferentes. Hay que
								 * fusionarlos! // Eso se hace al nivel de la
								 * base de datos: cuando agregamos un articulo,
								 * // controlamos si uno ya existe, y si es el
								 * caso, le actualizamos el campo de // codigo
								 * de barra solamente. int index = 0;
								 * 
								 * for (HashMap<String,String> articulo_cabeza:
								 * parseador.detallesArticulos.keySet()) {
								 * HashMap<String,String> articulo_cuerpo =
								 * parseador
								 * .detallesArticulos.get(articulo_cabeza);
								 * 
								 * Articulo articulo = new Articulo(
								 * Integer.parseInt
								 * (articulo_cabeza.get(ParametrosInventario
								 * .bal_bdd_articulo_sector)),
								 * Integer.parseInt(articulo_cabeza
								 * .get(ParametrosInventario
								 * .bal_bdd_articulo_codigo)), new
								 * ArrayList<String
								 * >(Arrays.asList(articulo_cuerpo
								 * .get(ParametrosInventario
								 * .bal_bdd_articulo_codigo_barra))),
								 * Integer.parseInt
								 * (articulo_cabeza.get(ParametrosInventario
								 * .bal_bdd_articulo_inventario)),
								 * articulo_cuerpo.get(ParametrosInventario.
								 * bal_bdd_articulo_descripcion),
								 * Double.parseDouble
								 * (articulo_cuerpo.get(ParametrosInventario
								 * .bal_bdd_articulo_precio_venta)),
								 * Double.parseDouble
								 * (articulo_cuerpo.get(ParametrosInventario
								 * .bal_bdd_articulo_precio_costo)),
								 * articulo_cuerpo
								 * .get(ParametrosInventario.bal_bdd_articulo_foto
								 * ), Integer.parseInt(articulo_cuerpo.get(
								 * ParametrosInventario
								 * .bal_bdd_articulo_cantidad)),
								 * articulo_cuerpo.
								 * get(ParametrosInventario.bal_bdd_articulo_fecha
								 * ) );
								 * 
								 * bdd.insertArticuloEnBdd(articulo);
								 * 
								 * index++; popupEspera.setProgress((int) (50 +
								 * Math.floor((double)index * (double)50/
								 * (double)parseador.contador)));
								 * 
								 * }
								 */

								// Al final borramos el archivo si se inserto
								// correctamente en la bd
								BaseDatos bdd = new BaseDatos(ctxt);
								boolean excepcion = false;
								Inventario actual = null;
								try {
									actual = bdd
											.selectInventarioConNumero(num_invent);
								} catch (ExceptionBDD e) {

									log.log("[-- 556 --]" + e.toString(), 4);
									// TODO Auto-generated catch block
									e.printStackTrace();
									excepcion = true;
								}

								if (actual != null /* && !excepcion */) {
									archivo.delete();

								} else {
									// Marcar que hubo un problema
									todosCorrectos = false;
								}
							} catch (Exception e) {

								log.log("[-- 571 --]" + e.toString(), 4);
								return e.toString();
								// e.printStackTrace();
								// error += "(" + e.toString() + ")";
							}
						} // END of IF (file exists)
					}// END of IF (num del inventario en la lista)
				}// END of FOR

			} catch (Exception ex) {

				log.log("[-- 582 --]" + ex.toString(), 4);

				return "Step: " + String.valueOf(step) + " // " + "Error: "
						+ error + " // " + ex.toString();
			}
			if (todosCorrectos) {
				return "Importacion exitosa";
			} else {
				return "Error en el formato del xml";
			}
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			cerrarMenuEspera();

			TextView tv = (TextView) findViewById(R.id.aaaa);
			tv.setText(result);

			// Mensaje de aviso de resultado
			AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
			builder.setMessage(result)
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(@NonNull DialogInterface dialog,
                                                    int id) {
									dialog.dismiss();
								}
							})
					.setNegativeButton("Salir",
							new DialogInterface.OnClickListener() {
								public void onClick(@NonNull DialogInterface dialog,
                                                    int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();

			alert.show();

			// Toast.makeText(ctxt, "Resultado obtenido: " + result,
			// Toast.LENGTH_LONG).show();

			// SystemClock.sleep(3000);

			Intent intentSeleccion = new Intent(UsbProvider.this,
					InventarioMainBoard.class);
			startActivity(intentSeleccion);
			finish();

		}

	} // fin de "protected class CargarDatosRutas"

	protected class PreScanXML {

		protected String descripcion;
		protected String numero;
		protected String fecha;
		protected String cantidad;
		
		protected String existencia;
		protected String deposito;
		

		protected PreScanXML() {
			descripcion = "";
			numero = "";
			fecha = "";
			cantidad = "";
			existencia = "";
		}

		private void scan(@NonNull File archivo) {

			try {
				XmlCleaner.xml_cleaning_spechar(archivo);
			} catch (IOException e1) {

				log.log("[-- 658 --]" + e1.toString(), 4);
				e1.printStackTrace();
			}

			// PARSER el archivo XML:
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				DefaultHandler handler = new DefaultHandler() {

					// Balizas para los 3 datos que necesitamos.
					// 0 = dato no parseado ;
					// 1 = baliza leida, dato todavia no parseado ;
					// 2 = dato recuperado
					int bal_descripcion = 0;
					int bal_numero = 0;
					int bal_fechaInicio = 0;
					int bal_cantidad = 0;
					double bal_existencia = 0;
					double bal_deposito = 0;
					
					public void startElement(String uri, String localName,
                                             @NonNull String qName, Attributes attributes)
							throws SAXException {
						if (bal_descripcion == 0
								&& qName.equalsIgnoreCase(
										Parametros.bal_usb_inventario_descripcion)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
							bal_descripcion = 1;
						} else if (bal_numero == 0
								&& qName.equalsIgnoreCase(
										Parametros.bal_usb_inventario_numero)) {
							bal_numero = 1;
						} else if (bal_fechaInicio == 0
								&& qName.equalsIgnoreCase(
										Parametros.bal_usb_inventario_fechaInicio)) {
							bal_fechaInicio = 1;
						} else if (bal_cantidad == 0
								&& qName.equalsIgnoreCase(
										Parametros.bal_usb_inventario_cantidad)) {
							bal_cantidad = 1;
						} else if(bal_existencia == 0
								&& qName.equalsIgnoreCase(
										Parametros.bal_usb_articulo_existencia_venta)){
							bal_existencia = 1 ;
					}else if(bal_deposito == 0
							&& qName.equalsIgnoreCase(
									Parametros.bal_usb_articulo_existencia_deposito)){
							bal_deposito = 1 ;
					}}

					public void characters(@NonNull char ch[], int start, int length)
							throws SAXException {
						if (bal_descripcion == 1) {
							descripcion = String.valueOf(ch).substring(0,
									length);
							bal_descripcion = 2;
						} else if (bal_numero == 1) {
							numero = String.valueOf(ch).substring(0, length);
							bal_numero = 2;
						} else if (bal_fechaInicio == 1) {
							fecha = String.valueOf(ch).substring(0, length);
							bal_fechaInicio = 2;
						} else if (bal_cantidad == 1) {
							cantidad = String.valueOf(ch).substring(0, length);
							bal_cantidad = 2;
						} else if (bal_existencia == 1) {
							existencia = String.valueOf(ch).substring(0, length);
							bal_existencia = 2;
						}else if (bal_deposito == 1) {
							deposito = String.valueOf(ch).substring(0, length);
							bal_deposito = 2;
						}
						if (bal_cantidad == 2 && bal_descripcion == 2
								&& bal_fechaInicio == 2 && bal_numero == 2
								&& bal_existencia == 2 && bal_deposito == 2) {
							throw new SAXException("fin");
						}
					}
				};

				InputStream inputStream = new FileInputStream(archivo);
				Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
				InputSource is = new InputSource(reader);
				is.setEncoding("ISO-8859-1");

				saxParser.parse(is, handler);

			} catch (Exception e) {

				log.log("[-- 730 --]" + e.toString() + "marcando", 4);
				e.printStackTrace();
				return;
			}

		} // end foncion SCAN

	} // end CLASS PreScanXML

	protected class ParserImportarXML {

		protected HashMap<String, String> unArticulo;
		protected HashMap<String, String> unInventario;
		protected int contador;
		protected BaseDatos bdd;

		protected ArrayList<String> listaSQLprocesar;

		protected long temps1;
		protected long temps2;

		protected ParserImportarXML() {
			unArticulo = new HashMap<String, String>();
			unInventario = new HashMap<String, String>();
			contador = 0;
			bdd = new BaseDatos(ctxt);

			listaSQLprocesar = new ArrayList<String>();
		}

		private void parser_importer(@NonNull File archivo, int cantidad) {
			System.out.println("::: USBProvider 820 parser_importer");
			final int cantidad_articulos_en_archivo = cantidad;

			try {
				XmlCleaner.xml_cleaning_spechar(archivo);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// PARSER el archivo XML:
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				DefaultHandler handler = new DefaultHandler() {

					// Balizas para los datos que necesitamos.
					// 0 = baliza no leida
					// 1 = baliza leida
					int flag_descripcion = 0;
					int flag_numero = 0;
					int flag_fecha = 0;

					int flag_articulo = 0;
					int flag_sector = 0;
					int flag_codigo = 0;
					int flag_codigobarra = 0;
					int flag_cantidad = 0;
					int flag_nom = 0;
					int flag_preciocosto = 0;
					int flag_precioventa = 0;
                    
					double flag_exisventa = 0;
                    double flag_exisdeposito = 0;
                    int flag_depsn = 0;
					
                    int flag_foto = 0;

					public void startElement(String uri, String localName,
                                             @NonNull String qName, Attributes attributes)
							throws SAXException {
					System.out.println("::: USBProvider 831 ");
						/*
						 * if
						 * (qName.equalsIgnoreCase(Parametros.bal_usb_articulo_root
						 * )) { flag_articulo = 1; temps1 = new
						 * Date().getTime(); setMenuValue((int)
						 * Math.floor((double)contador * (double)50 /
						 * (double)cantidad_articulos_en_archivo)); } else
						 */if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_sector)) {
							flag_sector = 1;
						} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_codigo)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
						flag_codigo = 1;
						} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_codigo_barra)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
							flag_codigobarra = 1;
						} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_cantidad)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
							flag_cantidad = 1;
						} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_descripcion)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
							flag_nom = 1;
						} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_precio_costo)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
							flag_preciocosto = 1;
						} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_precio_venta)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
							flag_precioventa = 1;
						} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_existencia_venta)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
					flag_exisventa = 1;
						}else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_existencia_deposito)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
						//	System.out.println("::: USBProvider 912 flagdep ="
					//				+ qName.equalsIgnoreCase(
					//						Parametros.bal_usb_articulo_existencia_deposito));
							flag_exisdeposito = 1;
						}else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_depsn)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
					flag_depsn = 1;
					} else if (flag_articulo == 1
								&& qName.equalsIgnoreCase(Parametros.bal_usb_articulo_foto)) {
							// Si vemos la baliza de la descripcion, ponemos el
							// estado a "1":
							flag_foto = 1;
						} else if (qName
								.equalsIgnoreCase(Parametros.bal_usb_inventario_descripcion)) {
							flag_descripcion = 1;
						} else if (qName
								.equalsIgnoreCase(Parametros.bal_usb_inventario_numero)) {
							flag_numero = 1;
						} else if (qName
								.equalsIgnoreCase(Parametros.bal_usb_inventario_fechaInicio)) {
							flag_fecha = 1;
						}
					}

					public void characters(@NonNull char ch[], int start, int length)
							throws SAXException {
						System.out.println("::: USBProvider 901 ");
						if (flag_sector == 1) {
							unArticulo
								.put(ParametrosInventario.bal_bdd_articulo_sector,
									String.valueOf(ch).substring(0,
										length));
							flag_sector = 0;
						} else if (flag_codigo == 1) {
							unArticulo
								.put(ParametrosInventario.bal_bdd_articulo_codigo,
									String.valueOf(ch).substring(0,
										length));
//							System.out.println("::: USBProvider 951 unArticulo codigo= "
//													+unArticulo);
							flag_codigo = 0;
						} else if (flag_codigobarra == 1) {
							if (length == 0) {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_codigo_barra,
												"0");
							} else {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_codigo_barra,
												String.valueOf(ch).substring(0,
														length));
							}
							
							flag_codigobarra = 0;
						} else if (flag_cantidad == 1) {
							if (length == 0) {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_cantidad,
												"-1");
							} else {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_cantidad,
												String.valueOf(ch).substring(0,
														length));
							}
							flag_cantidad = 0;
						} else if (flag_nom == 1) {
							if (length == 0) {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_descripcion,
												"Art. sin descripcion");
							} else {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_descripcion,
												String.valueOf(ch).substring(0,
														length));
							}
							flag_nom = 0;
						} else if (flag_preciocosto == 1) {
							unArticulo
									.put(ParametrosInventario.bal_bdd_articulo_precio_costo,
											String.valueOf(ch).substring(0,
													length));
							flag_preciocosto = 0;
						} else if (flag_precioventa == 1) {
							unArticulo
									.put(ParametrosInventario.bal_bdd_articulo_precio_venta,
											String.valueOf(ch).substring(0,
													length));
							flag_precioventa = 0;
						} else if (flag_exisventa == 1) {
							unArticulo
									.put(ParametrosInventario.bal_bdd_articulo_existencia_venta,
											String.valueOf(ch).substring(0,
													length));
			//				System.out.println("::: USBProvider 1007 unArticulo exis= "
			//						+unArticulo);
			//				System.out.println("::: USBProvider 1008 exis "+ unArticulo
			//						.put(ParametrosInventario.bal_bdd_articulo_existencia_venta,
			//								String.valueOf(ch).substring(0,
			//										length)));
							flag_exisventa = 0;
						}else if (flag_exisdeposito == 1) {
							unArticulo
									.put(ParametrosInventario.bal_bdd_articulo_existencia_deposito,
											String.valueOf(ch).substring(0,
													length));
			//				System.out.println("::: USBProvider 1014 dep "+ unArticulo);
			//				System.out.println("::: USBProvider 1015 dep "+ unArticulo
			//						.put(ParametrosInventario.bal_bdd_articulo_existencia_deposito,
			//								String.valueOf(ch).substring(0,
			//										length)));
							
							flag_exisdeposito = 0;
						}else if (flag_foto == 1) {
							if (length == 0) {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_foto,
												"''");
							} else {
								unArticulo
										.put(ParametrosInventario.bal_bdd_articulo_foto,
												String.valueOf(ch).substring(0,
														length));
							}
							flag_foto = 0;
						} else if (flag_descripcion == 1) {
							unInventario
									.put(ParametrosInventario.bal_bdd_inventario_descripcion,
											String.valueOf(ch).substring(0,
													length));
							flag_descripcion = 2;
						} else if (flag_numero == 1) {
							unInventario
									.put(ParametrosInventario.bal_bdd_inventario_numero,
											String.valueOf(ch).substring(0,
													length));
							flag_numero = 2;
						} else if (flag_fecha == 1) {
							unInventario
									.put(ParametrosInventario.bal_bdd_inventario_fechaInicio,
											String.valueOf(ch).substring(0,
													length));
							flag_fecha = 2;
						}
						System.out.println("::: USBProvider 1059 ");
						System.out.println("::: USBProvider 1061 "+ flag_numero);
						System.out.println("::: USBProvider 1061 "+ flag_descripcion);
						System.out.println("::: USBProvider 1061 "+ flag_fecha);
						//				System.out.println("::: USBProvider 1006 ");
						if (flag_descripcion == 2 && flag_numero == 2
								&& flag_fecha == 2) {
							// Tenemos todos los campos del inventario, lo
							// grabamos en la BDD:
							// Posiblemente haya que modificar la forma en que
							// se guarda la fecha de inicio por que la guarda
							// con otro formato
							// 07-05-2012 15:53 --> 2012-05-08 13:08:56
							// Guardamos la fecha de inicio en una cadena tal
							// como viene en el XML de importacin dd-MM-yyyy
							// hh:mm
							// String
							// fechaDoc=unInventario.get(ParametrosInventario.bal_bdd_inventario_fechaInicio);
							// //Generamos una date de esa cadena
							// SimpleDateFormat sdf=new
							// SimpleDateFormat("dd-MM-yyyy hh:mm");
							// Date fechaInicioDoc=sdf.parse(fechaDoc);
							// //Generamos otra cadena con el formato nuevo
							// yyyy-MM-dd hh:mm:ss
							// sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							// String
							// nuevaFechaInicio=sdf.format(fechaInicioDoc);
							//

							Inventario inventario = new Inventario(
									Integer.parseInt(unInventario
											.get(ParametrosInventario.bal_bdd_inventario_numero)),
									unInventario
											.get(ParametrosInventario.bal_bdd_inventario_descripcion),
									unInventario
											.get(ParametrosInventario.bal_bdd_inventario_fechaInicio),
									unInventario
											.get(ParametrosInventario.bal_bdd_inventario_fechaFin),
									1, -1);
							

							try {
							
								bdd.insertInventarioEnBdd(inventario);
							} catch (ExceptionBDD e) {

								log.log("[-- 983 --]" + e.toString(), 4);
								e.printStackTrace();
							}

							flag_descripcion = 0;
							flag_numero = 0;
							flag_fecha = 0;
							flag_articulo = 1;
						}
					}

					public void endElement(String uri, String localName,
                                           @NonNull String qName) throws SAXException {
						System.out.println("::: UsbProvider 1118");
						if (qName
								.equalsIgnoreCase(Parametros.bal_usb_articulo_root)) {
							/*
							 * Articulo articulo = null; try { articulo = new
							 * Articulo(
							 * Integer.parseInt(unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_sector)),
							 * Integer.parseInt(unArticulo
							 * .get(ParametrosInventario
							 * .bal_bdd_articulo_codigo)), new
							 * ArrayList<String>(
							 * Arrays.asList(unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_codigo_barra))),
							 * Integer.parseInt
							 * (unInventario.get(ParametrosInventario
							 * .bal_bdd_inventario_numero)),
							 * unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_descripcion),
							 * Double.parseDouble
							 * (unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_precio_venta)),
							 * Double.parseDouble
							 * (unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_precio_costo)),
							 * unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_foto),
							 * Integer.parseInt(unArticulo
							 * .get(ParametrosInventario
							 * .bal_bdd_articulo_cantidad)), "" // fecha vaca
							 * todava );
							 * 
							 * bdd.insertArticuloEnBdd(articulo); } catch
							 * (ExceptionBDD e) { } catch (Exception ex) { }
							 */
		//					System.out.println("::: USBProvider 1092 " + unArticulo);
							System.out.println("::: UsbProvider 1149");
							
							listaSQLprocesar
									.add("INSERT OR REPLACE INTO "
											+ ParametrosInventario.tabla_articulos
											+ " "
											+ "VALUES ("
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_sector)
											+ ","
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_codigo)+ ","
											+ "0"+ ","
											+ "0"+ ","
											+ "'"
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_codigo_barra)
											+ "' "+ ","
											+ "'"
											+ "' "
											+ "|| coalesce("
											+ "',' || (SELECT "
											+ ParametrosInventario.bal_bdd_articulo_codigo_barra
											+ " "
											+ "FROM "
											+ ParametrosInventario.tabla_articulos
											+ " "
											+ "WHERE "
											+ ParametrosInventario.bal_bdd_articulo_sector
											+ "="
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_sector)
											+ " AND "
											+ ParametrosInventario.bal_bdd_articulo_codigo
											+ "="
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_codigo)
											+ " AND "
											+ ParametrosInventario.bal_bdd_articulo_inventario
											+ "="
											+ unInventario
													.get(ParametrosInventario.bal_bdd_inventario_numero)
											+ "),''),"
											+ unInventario
													.get(ParametrosInventario.bal_bdd_inventario_numero)
											+ ","
											
											+ "'" +
											 unArticulo
											.get(ParametrosInventario.bal_bdd_articulo_cantidad)//Trae la descripcion
											+"'"+ ","
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_descripcion)//Trae Pre_vent
											+ ","
//											+ unArticulo
//													.get(ParametrosInventario.bal_bdd_articulo_precio_venta)
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_precio_costo)//Trae pre_cost
											+ ","
											+ unArticulo
													.get(ParametrosInventario.bal_bdd_articulo_foto)//foto creo
											+ ","
											+ "0"//cantidad
											+ ",0"
											//peso
											+ ",0" 
//											+ unArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_existencia_venta)
									+ ",0"
//									+ unArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_existencia_deposito)
									+ ",''"
//									+ unArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_depsn)
									+ ","
											+ "'',''" + ")");
			//				System.out.println("::: USBProvider 1160 ExisVenta" + unArticulo
			//						.get(ParametrosInventario.bal_bdd_articulo_existencia_venta));
			
							
			//				System.out.println("::: USBProvider 1162 " + listaSQLprocesar);
							/*
							 * synchronized (locker) {
							 * colaSQL.add("INSERT into "+
							 * ParametrosInventario.tabla_articulos +
							 * " VALUES (" +
							 * unArticulo.get(ParametrosInventario.
							 * bal_bdd_articulo_sector) + "," +
							 * unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_codigo) + "," + "'" +
							 * unArticulo.get(ParametrosInventario.
							 * bal_bdd_articulo_codigo_barra) + "'," +
							 * unInventario
							 * .get(ParametrosInventario.bal_bdd_inventario_numero
							 * ) + "," + "'" +
							 * unArticulo.get(ParametrosInventario
							 * .bal_bdd_articulo_descripcion) + "'," +
							 * unArticulo.get(ParametrosInventario.
							 * bal_bdd_articulo_precio_venta) + "," +
							 * unArticulo.get(ParametrosInventario.
							 * bal_bdd_articulo_precio_costo) + "," + "'" +
							 * unArticulo
							 * .get(ParametrosInventario.bal_bdd_articulo_foto)
							 * + "'," + unArticulo.get(ParametrosInventario.
							 * bal_bdd_articulo_cantidad) + "," + "''" + ")"); }
							 */
							contador++;
							setMenuValue((int) Math.floor((double) contador
									* (double) 98
									/ (double) cantidad_articulos_en_archivo));
							unArticulo.clear();
						}
					}
				};
				System.out.println("::: USBProvider 1267 ");
				InputStream inputStream = new FileInputStream(archivo);
				Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
				InputSource is = new InputSource(reader);
				is.setEncoding("ISO-8859-1");

				saxParser.parse(is, handler);

				// terminado = true;

			} catch (Exception e) {

				log.log("[-- 1134 --]" + e.toString(), 4);
				e.printStackTrace();
			}

			/*
			 * DocumentBuilderFactory docFactory = null; DocumentBuilder
			 * docBuilder = null; Document doc = null;
			 * 
			 * try { XmlCleaner.xml_cleaning_spechar(archivo); } catch
			 * (IOException e1) { e1.printStackTrace(); }
			 * 
			 * try { docFactory = DocumentBuilderFactory.newInstance();
			 * docBuilder = docFactory.newDocumentBuilder(); doc =
			 * docBuilder.parse(archivo); } catch (ParserConfigurationException
			 * e) { e.printStackTrace(); } catch (SAXException e) {
			 * e.printStackTrace(); } catch (IOException e) {
			 * e.printStackTrace(); }
			 * 
			 * // Recorrido del arbol de los datos XML: try { // Primero
			 * recuperamos los datos del encabezado: // Detalles Inventario :
			 * numero, descripcion y fecha NodeList listaDescripcion =
			 * doc.getElementsByTagName
			 * (Parametros.bal_usb_inventario_descripcion);
			 * this.cabecera.put(ParametrosInventario
			 * .CONVERSOR_BALIZAS.usb2bdd(Parametros
			 * .bal_usb_inventario_descripcion,
			 * ParametrosInventario.tabla_inventarios),
			 * String.valueOf(listaDescripcion
			 * .item(0).getTextContent()).trim());
			 * 
			 * NodeList listaNumero =
			 * doc.getElementsByTagName(Parametros.bal_usb_inventario_numero);
			 * this.cabecera.put(ParametrosInventario.CONVERSOR_BALIZAS.usb2bdd(
			 * Parametros.bal_usb_inventario_numero,
			 * ParametrosInventario.tabla_inventarios),
			 * String.valueOf(listaNumero.item(0).getTextContent()).trim());
			 * 
			 * NodeList listaFecha =
			 * doc.getElementsByTagName(Parametros.bal_usb_inventario_fecha);
			 * this.cabecera.put(ParametrosInventario.CONVERSOR_BALIZAS.usb2bdd(
			 * Parametros.bal_usb_inventario_fecha,
			 * ParametrosInventario.tabla_inventarios),
			 * String.valueOf(listaFecha.item(0).getTextContent()).trim());
			 * 
			 * // Despues recuperamos todos los datos de los articulos: NodeList
			 * listaArticulos =
			 * doc.getElementsByTagName(Parametros.bal_usb_articulo_root);
			 * 
			 * for (int i = 0 ; i < listaArticulos.getLength() ; i++) {
			 * setMenuValue((int) Math.floor((double)i * (double)50 /
			 * (double)listaArticulos.getLength()));
			 * 
			 * NodeList listaHijos = listaArticulos.item(i).getChildNodes();
			 * //NodeList listaHijos = listaArticulos.item(i).getChildNodes();
			 * HashMap<String, String> cabeza = new HashMap<String, String>();
			 * HashMap<String, String> cuerpo = new HashMap<String, String>();
			 * 
			 * // Llenamos el CUERPO con todos los datos disponibles: for (int j
			 * = 0 ; j < listaHijos.getLength() ; j++) { try { String nombre =
			 * listaHijos.item(j).getNodeName(); String valor =
			 * listaHijos.item(j).getTextContent().trim().replace(",", ".");
			 * cuerpo.put(ParametrosInventario.CONVERSOR_BALIZAS.usb2bdd(nombre,
			 * ParametrosInventario.tabla_articulos) , valor); } catch
			 * (Exception e) {} }
			 * 
			 * // Extraemos unos campos de identificacin para fabricar la
			 * CABEZA: cabeza.put(ParametrosInventario.bal_bdd_articulo_codigo,
			 * cuerpo.get(ParametrosInventario.bal_bdd_articulo_codigo));
			 * cabeza.put(ParametrosInventario.bal_bdd_articulo_sector,
			 * cuerpo.get(ParametrosInventario.bal_bdd_articulo_sector));
			 * cabeza.put(ParametrosInventario.bal_bdd_articulo_inventario,
			 * this.
			 * cabecera.get(ParametrosInventario.bal_bdd_inventario_numero));
			 * 
			 * // Control de los nmeros, que si no hay nada le ponemos 0: if
			 * (cuerpo
			 * .get(ParametrosInventario.bal_bdd_articulo_codigo_barra).trim
			 * ().length() <= 0) {
			 * cuerpo.put(ParametrosInventario.bal_bdd_articulo_codigo_barra,
			 * "0"); }
			 * 
			 * if (Integer.parseInt(cuerpo.get(ParametrosInventario.
			 * bal_bdd_articulo_cantidad).trim()) <= 0) {
			 * cuerpo.put(ParametrosInventario.bal_bdd_articulo_cantidad, "-1");
			 * }
			 * 
			 * try { double db =
			 * Double.parseDouble(cuerpo.get(ParametrosInventario
			 * .bal_bdd_articulo_precio_venta).trim().replace(",", "."));
			 * cuerpo.put(ParametrosInventario.bal_bdd_articulo_precio_venta,
			 * String.valueOf(db)); } catch (Exception e) {
			 * cuerpo.put(ParametrosInventario.bal_bdd_articulo_precio_venta,
			 * "0"); }
			 * 
			 * try { double db =
			 * Double.parseDouble(cuerpo.get(ParametrosInventario
			 * .bal_bdd_articulo_precio_costo).trim().replace(",", "."));
			 * cuerpo.put(ParametrosInventario.bal_bdd_articulo_precio_costo,
			 * String.valueOf(db)); } catch (Exception e) {
			 * cuerpo.put(ParametrosInventario.bal_bdd_articulo_precio_costo,
			 * "0"); }
			 * 
			 * this.detallesArticulos.put(cabeza,cuerpo);
			 * 
			 * } // end for
			 * 
			 * } catch (Exception e) { e.printStackTrace(); } // end try
			 */
		} // end foncion PARSER

	} // end CLASS ParserXML

	private void lanzarMenuEspera() {
		popupEspera = new ProgressDialog(ctxt);
		popupEspera.setCancelable(true);
		popupEspera
				.setMessage("Por favor, aguarde mientras se cargan los datos de los INVENTARIOS...");
		log.log("[-- 1251 --]"
				+ "Por favor, aguarde mientras se acargan los datos de los INVENTARIOS...",
				3);
		popupEspera.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		popupEspera.setMax(100);
		popupEspera.setProgress(0);

		DialogInterface.OnClickListener listenerClic = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				try {
					cerrarMenuEspera();
					BaseDatos bdd = new BaseDatos(ctxt);
					bdd.borrarInventarioConArticulos(inventarios_seleccionados);
					Intent intentMB = new Intent(UsbProvider.this,
							InventarioMainBoard.class);
					startActivity(intentMB);
					finish();
				} catch (ExceptionBDD e) {

					log.log("[-- 1269 --]" + e.toString(), 4);

				}
			}
		};

		popupEspera.setButton(Dialog.BUTTON_NEGATIVE, "Cancelar", listenerClic);
		popupEspera.show();

	}

	private void setMenuValue(int valor) {
		popupEspera.setProgress(valor);
	}

	private void cerrarMenuEspera() {
		popupEspera.dismiss();
	}

	private void copyFile(@NonNull File sourceFile, @NonNull File destFile) throws IOException {
		try {
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

			while (destFile.exists() == false) {
				SystemClock.sleep(100);
			}
		} catch (Exception e) {
			log.log(e.toString(), 3);

		}
	}

} // END CLASS

