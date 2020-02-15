package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class ComprasMainBoard extends Activity implements DialogPersoSimple,
		Wifi {
	@NonNull
    private Context ctxt = this;
	private CheckBox CheckBorrar;
	private boolean borrar;
	private BaseDatos bdd;
	@NonNull
    BaseDatos bd = new BaseDatos(ctxt);
	private TableLayout tablaPrincipal;
	private ImageView botonSalir;
	private ProgressDialog popupCarga, popupEspera;

	private Button busquedaProveedoresNombre;
	private EditText nomProveedor;
	@NonNull
    GestorLogEventos log = new GestorLogEventos();

	private DialogPersoComplexBusqueda dialogoBusqueda;
	@Nullable
    private EditText edittextBusqueda = null;
	//private HashMap<Integer, Integer> proveedor_resultado_busqueda = null;
	private DialogProveComplexResultados dialogoResultados;

	private ListView list;

	View.OnClickListener listenerBuscarPro;
	/*** Botones para importar y exportar los inventarios*/
	private Button botonExportar, botonImportar, Exportar_BD, Importar_BD, boton_nuevo_inv;
	private RadioButton CheckedInventariosVentas;
	private RadioButton CheckedInventariosDeposito;
	// Parametro para mostrar inventarios de deposito, ventas o compras -3
	private int condR = 0;
/*** Dialogo de aviso para borrar inventarios comunes, se muestra cuando presionamos de forma prolongada un boton de un inventario com�n*/
	private DialogPersoComplexSiNo dialogoBorrarInventario;
	private DialogPersoComplexSiNoInvComp dialogoNuevoInventario;
	/*** Para manejo especial de inventario dinamico*/
	boolean hayDinamicos = false;

	@Nullable
    HashMap<String, String> hashmapInventarioCompra;
	/*** Dialogo que se muestra para preguntar si desea seguir trabajando con el
	 * inventario dinamico actual o empezar de cero*/
	private DialogPersoComplexSiNo dialogoContinuarInventario;
	/*** Dialogo para confirmar la decisi�n de borrar el inventario din�mico
	 * actual y crear uno nuevo desde cero*/
	private DialogPersoComplexSiNo dialogoBorrarInventarioDinamico;
	/*** Lista para almacenar cuales son los inventarios seleccionados con los que se trabajara */
	@NonNull
    private ArrayList<Integer> listaInventariosSeleccionados = new ArrayList<Integer>();
	/**
	 * Dialog donde se informa que se han realizado todas las mediciones para
	 * que proceda a exportar
	 */
	private AlertDialog.Builder dialogoFin;
	private Button elegirProveedor;
	private ArrayList<Proveedor> listaProveedorCompleta;
	@Nullable
    private HashMap<Integer, Integer> proveedor_resultado_busqueda = null;
	private TextView nombreProveedorV;
	private int numero_proveedor;

	private boolean fueCanceladoDialogoResultados = false;

	private int respuestaSeleccionada = -99;
	private int indice_on_focus = -1;
	private int inventarios_elegidos = 0;
	/*** Variable para saber si hay que borrar despues de exportar*/
	boolean borrarDespues = false;
	/*** Dialogos para mostrar opciones de importacion y exportacion para elegir los medios*/
	private DialogPersoComplexExportCompra dialogoPrincipioExport,dialogoPrincipioImport;
	/** Variables de control para saber que tipo de unidad se tiene conectada tanto para esta clase como para la UsbProvider*/
	@NonNull
    public String unidad_final_import = "Dispositivo";
	@NonNull
    public String unidad_final_export = "Dispositivo";
	// se lanza al principio de la clase, para que quede confirurado para la clase usbProvider
	// se verifica el inicio de la ruta configurada sea la correcta /udisk/, /flash/,/sdcard/
	@NonNull
    public String TipoDispositivoImport() {
		// se retorna el resultado y se seteea para que este disponible en cualquier momento
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_mainboard_compras);
		// Recuperamos tabla:
		tablaPrincipal = (TableLayout) findViewById(R.id.IMB_tabla);
		// Recuperamos los botones:
		botonExportar = (Button) findViewById(R.id.IMB_boton_exportar);
		botonSalir = (ImageView) findViewById(R.id.IMB_boton_salir);
	//	botonImportar = (Button) findViewById(R.id.IMB_boton_importar);
		boton_nuevo_inv = (Button) findViewById(R.id.ADD_boton_nuevo_invc);
		Exportar_BD = (Button) findViewById(R.id.Exportar_BD);
		Importar_BD = (Button) findViewById(R.id.Importar_BD);
		CheckedInventariosVentas = (RadioButton) findViewById(R.id.CheckInventariosVentas);
		CheckedInventariosDeposito = (RadioButton) findViewById(R.id.CheckInventariosDepositos);

		elegirProveedor = (Button) findViewById(R.id.id_proveedor_buscar);
		//nombreProveedorV = (TextView) findViewById(R.id.nombreProveedor);

		Exportar_BD.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				log.log("Se presiono el boton Exportar BD", 3);
				String titulo = "EXPORTACION DE BASE DE DATOS";
				String mensaje = "Se exporto la base de datos";
				showSimpleDialogOK(titulo, mensaje).show();
				try {
					File sourceFile = new File(
							ParametrosInventario.URL_CARPETA_DATABASES
									+ "DB_INVENT");
					File destFile = new File(
							ParametrosInventario.CARPETA_LOGDATOS
									+ "DB_INVENT.sqlite");
					copyFile(sourceFile, destFile);
					Log.e("mensaje, sourceFile", sourceFile.toString());
					Log.e("mensaje, destFile", destFile.toString());
				} catch (Exception e) {
					log.log(e.toString(), 4);
					showSimpleDialogOK(
							"EXPORTACION DE BASE DE DATOS",
							"Se interrumpio, intentelo nuevamente.\n"
									+ "Si el error persiste, reporte el archivo log a Servicio Tecnico")
							.show();
				}

				log.log("Exportacion Realizada con exito", 3);
				return false;
			}

		});
		// Apagamos el Wifi:
		//desactivarWifi();
		// 2� REFRESH DE LA PAGINA PRINCIPAL:
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

		botonExportar.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				log.log("[-- 276 --]" + "Presiono Exportar", 0);
				BaseDatos bdd = new BaseDatos(ctxt);
				int numero_inventarios_cerrados = 0;
				ArrayList<Integer> inventarios_a_exportar ;
				try {
					inventarios_a_exportar = bdd
							.selectInventariosCerradosEnBddCompras();
					//inventarios_a_exportar = bdd
					//		.selectInventariosNumerosEnBddCompras();
					numero_inventarios_cerrados = inventarios_a_exportar.size();
					if(numero_inventarios_cerrados>=1) {
						inventarios_elegidos = inventarios_a_exportar.get(0);
					}
				} catch (ExceptionBDD e) {
					log.log("[-- 285 --]" + e.toString(), 4);
					log.log("[-- 286 --]" + "Error, la exportacion se cancelo",
							3);
					e.printStackTrace();
					Toast.makeText(ctxt, "Error, la exportacion se cancelo",
							Toast.LENGTH_LONG).show();
					return false;
				}
				if (numero_inventarios_cerrados <= 0) {
					log.log("[-- 297 --]"
									+ "Ningun inventario cerrado, la exportacion se cancelo",3);
					Toast.makeText(
							ctxt,
							"Ningun inventario cerrado, la exportacion se cancelo",
							Toast.LENGTH_LONG).show();
					return false;
				}else if(numero_inventarios_cerrados>=2){
					log.log("[-- 297 --]"
									+ "Debe seleccionar un solo inventario, la exportacion se cancelo",3);
					Toast.makeText(
							ctxt,
							"No puede realizarse la exportacion, solamente debe haber un inventario cerrado para exportar.",
							Toast.LENGTH_LONG).show();
					return false;
				}

				lanzarMenuEspera();

				View.OnClickListener listenerWifi = new View.OnClickListener() {

					public void onClick(View v) {

						log.log("[-- 311 --]" + "Se presiono el boton Wifi ", 0);
						borrarDespues = dialogoPrincipioExport.isBorrar_luego();

						dialogoPrincipioExport.dismiss();

						Intent intentWifi = new Intent(
								ComprasMainBoard.this, WiFiControlador.class);
						startActivityForResult(intentWifi,
								Parametros.REQUEST_WIFI_EXPORT);
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
							} while (control_buena_exportacion() == false
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
						if ((control_buena_exportacion() == false)
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
					dialogoPrincipioExport = new DialogPersoComplexExportCompra(
							ctxt,
							"MEDIO DE EXPORTACION",
							"Usted esta a punto de exportar los datos de COMPRAS.\n"
									+ "La opcion de  AJUSTAR PRODUCTOS NO INCLUIDOS puede demorar varios minutos.\n\n"
									+ "Al finalizar la Compra sera borrada.",
							true, listenerWifi, listenerNegativo);
					dialogoPrincipioExport.show();
				return true;
			}
		});

		boton_nuevo_inv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
/******************************************************************************************
*******************************************************************************************/
				//dialogoNuevoInventario
				// 1.1.1 En caso positivo pasa al inventario actual
				View.OnClickListener listenerPositivo = new View.OnClickListener() {
					public void onClick(View v) {
//						log.log("[-- --]"
//								+ "Presiono para ir a los inventarios dinamicos", 0);
						BaseDatos bdd = new BaseDatos(ctxt);
						// Lo que hace con el boton del si
						dialogoNuevoInventario.dismiss();
						// Creamos los inventarios dinamicos
						Inventario inventarioDinamicoCompra = new Inventario(
								ParametrosInventario.ID_INV_COMPRAS,
								"Inv. Comp ",
										//+ String.valueOf(ParametrosInventario.ID_INV_COMPRAS)
										//+ "Compras",
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(new Date()),
								"",
								ParametrosInventario.INVENTARIO_ABIERTO,
								ParametrosInventario.COD_LUGAR_INVENTARIO_VENTA);
						try {

							if(bdd.verificaBaseNueva()==true){
								bdd.verComprasExistentes(inventarioDinamicoCompra);
								Toast.makeText(
										ctxt,
										"Se creo un nuevo inventario de compras.",
										Toast.LENGTH_LONG).show();
								Intent intentInventario = new Intent(ctxt,
										ComprasMainBoard.class);
								startActivityForResult(intentInventario,
										ParametrosInventario.REQUEST_INVENTARIO_COMPRAS);
							}else{
								Toast.makeText(
										ctxt,
										"Debe crear el primero antes de agregar más Inv..",
										Toast.LENGTH_LONG).show();
							}
						} catch (ExceptionBDD exceptionBDD) {
							exceptionBDD.printStackTrace();
						}
					}
				};
				// 1.1.2 En caso negativo muestra otro cartel para verificar si
				// borra
				View.OnClickListener listenerNegativo = new View.OnClickListener() {
					public void onClick(View v) {
						// Lo que hace con el no Debe preguntar nuevamente para eliminar el inventario
						// guardado Abrir otra ventana y preguntar si realmente quiere eliminar los datos
						dialogoNuevoInventario.dismiss();
						// 1.1.2.1 En caso de que quiera borrar se elimina y genera uno nuevo pasando a la Pagina correspondiente
						/*View.OnClickListener listenerPositivo = new View.OnClickListener() {
							public void onClick(View v) {
								log.log("[-- 1617 --]" + "Se presiono el boton si",
										0);
								BaseDatos bdd = new BaseDatos(ctxt);
								// Lo que hace con el boton del si
								dialogoBorrarInventarioDinamico.dismiss();
								// Creamos los inventarios dinamicos
								Inventario inventarioDinamicoCompra = new Inventario(
										ParametrosInventario.ID_INV_COMPRAS,
										"Inv. dinamico "
												+ String.valueOf(ParametrosInventario.ID_INV_COMPRAS)
												+ " de compra",
										new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
												.format(new Date()),
										"",
										ParametrosInventario.INVENTARIO_ABIERTO,
										ParametrosInventario.COD_LUGAR_INVENTARIO_VENTA);
								try {
									// Borrar datos del inventario
									bdd.borrarInventarioConArticulos(ParametrosInventario.ID_INV_COMPRAS);
									// Crearlo de nuevo
									bdd.insertInventarioComprasEnBdd(inventarioDinamicoCompra);
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
										PaginaCompras.class);
								intentInventario
										.putExtra(
												ParametrosInventario.extra_numeroInventarioCompra,
												ParametrosInventario.ID_INV_COMPRAS);
								startActivityForResult(
										intentInventario,
										ParametrosInventario.REQUEST_INVENTARIO_COMPRAS);
							}
						};*/
						// 1.1.2.2 En caso de que no quiera borrar, se vuelve a la
						// pantalla principal
					}
				};
				// 1.1 Genera un dialog que pregunta si se continua con el
				// inventario o se
				// borra y genera algo nuevo
				dialogoNuevoInventario = new DialogPersoComplexSiNoInvComp(
						ctxt,
						"Agregar Inventario Compras",
						"Desea agregar un inventario de compras?",
						DialogPerso.VALIDAR, listenerPositivo, listenerNegativo);

				dialogoNuevoInventario.show();


				/******************************************************************************************
				 * ***************************************************************************************
				 */
				log.log("[-- 396 --]" + "Se presiono Agregar Inv C", 0);
				//	try {
				bdd = new BaseDatos(ctxt);
				//	} catch (ExceptionBDD e) {
				//		log.log("[-- 409 --]" + e.toString(), 4);
				//	}
			}
		});

		botonSalir.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intentDebo = new Intent(ctxt,
						DeboInventario.class);
				startActivity(intentDebo);
			/*	try {
					if (indice_on_focus >= 0) {
						TableRow linea = (TableRow) tabla_articulos
								.getChildAt(indice_on_focus);
						EditText edittext = (EditText) linea.getChildAt(3);
						InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						mgr.hideSoftInputFromWindow(edittext.getWindowToken(),
								0);

						deseleccionarLineaParticular(indice_on_focus);
					}
				} catch (Exception e) {
				log.log("[-- 2461 --]" + e.toString(), 4);
					e.printStackTrace();
				} finally {
					setResult(RESULT_OK, intentPadre);
					finish();
				}*/
			}
		});
	/*	botonSalir.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
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
		});*/

	}
	public void elegir(View view, int id_inventario) {
		String convertir_id = String.valueOf(id_inventario);
		Intent i = new Intent(ComprasMainBoard.this, ProveedorBusqueda.class);
		i.putExtra("proveedor", convertir_id);
		startActivity(i);
	}

	private void export_usb(boolean borrar_despues) throws ExceptionBDD,
			Exception {
		// Para prueba descomentar la linea siguiente para que encuentre los archivos ParametrosInventario.PREF_USB_EXPORT =
		// "data/data/com.foca.deboInventario/test/"; 1 Vaciar la carpeta de exportacion
		vaciarCarpetaExportacion();
		bdd = new BaseDatos(ctxt);
		String texto_error = "";
		boolean estado_exportacion = true;
		Dispositivo_Export = TipoDispositivoExport();
		Dispositivo_Export = TipoDispositivoImport();

		// 2 Fabricamos la lista de todos los inventarios que estn cerrados:
		ArrayList<Integer> listaInventariosCerrados = bdd
				.selectInventariosCerradosEnBddCompras();
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
	/*** Funcin accesoria para vaciar la carpeta de exportacion del pendrive*/
	private void vaciarCarpetaExportacion() {
		File carpeta_destino = new File(
				ParametrosInventario.CARPETA_DESDETABLET);
		for (File archivo : carpeta_destino.listFiles()) {
			// No funciona esto con archivos de menos de 1 byte por lo menos en el emulador
			archivo.delete();
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

	/*** Muestra un dialog de OK*/
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
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}
	//@Override
	//public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje, Class<?> clase) {
	//	return null;
	//}

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
								} while (control_buena_exportacion() == false
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
									ComprasMainBoard.this, UsbProvider.class);
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
	 * Funcion accesoria para copiar un archivo de origen en uno de destino
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

	private void refreshTablaPrincipal() throws ExceptionBDD, Exception {
		HashMap<Integer, HashMap<String, String>> matrizInventarios = new HashMap<Integer, HashMap<String, String>>();
		HashMap<Integer, HashMap<String, String>> matrizInventariosDinamicos = new HashMap<Integer, HashMap<String, String>>();
		tablaPrincipal.removeAllViews();
		control_integridad_tablas();
		BaseDatos bdd = new BaseDatos(ctxt);
		bdd = new BaseDatos(ctxt);
		try {
			matrizInventarios = bdd.selectInventariosCompraEnBdd();
		} catch (ExceptionBDD e2) {
			log.log("[-- 882 --]" + e2.toString() + " NO HAY INVENTARIOS", 4);
			e2.printStackTrace();
		}
		// 4 Configuracin de los botones:
		if (matrizInventarios.size() <= 0) {
			botonExportar.setEnabled(false);
			//botonImportar.setEnabled(true);
			boton_nuevo_inv.setEnabled(true);

			// return;
		} else if (matrizInventarios.size() >= ParametrosInventario.PREF_MAX_COMPRA_ABIERTAS) {
			botonExportar.setEnabled(true);
			//botonImportar.setEnabled(false);
			boton_nuevo_inv.setEnabled(false);
		} else {
			botonExportar.setEnabled(true);
			//botonImportar.setEnabled(true);
			boton_nuevo_inv.setEnabled(true);
		}
		// 5 Si tenemos elementos, construccin de todos los botones de inventarios comunes de la pagina:
		ArrayList<Integer> lista_claves = new ArrayList<Integer>();
		for (int i : matrizInventarios.keySet()) {
			lista_claves.add(i);
		}


		Collections.sort(lista_claves);
		Iterator<Integer> iterator_inventarios_id = lista_claves.iterator();
		while (iterator_inventarios_id.hasNext() == true) {
			// Recupermos los datos:
			final int id_inventario = iterator_inventarios_id.next();
			if (id_inventario < -3) {
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
						R.layout.z_lineaprogressbar_mainboard_compras, null);
				nuevaLinea
						.setId(ParametrosInventario.ID_LINEAS + id_inventario);
				// Elemento 1 = boton:
				Button b = (Button) nuevaLinea.findViewById(R.id.LPB2_boton);
				//if (id_inventario >= 0) {
				//	b.setText("Inventario " + String.valueOf(id_inventario));
				b.setText("Inventario Compras");
			//	} else {
			//		b.setText("Inv. dinamico "
			//				+ String.valueOf(Math.abs(id_inventario)));
			//	}
				b.setId(ParametrosInventario.ID_BOTONES + id_inventario);
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
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_ab));
				} else {
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_cer));
				}
				System.out.println("::: 700 COMPRAS MAIN BOAR VER SI LLEGA ACCCCCCCCCCCCCCCAAAAAAAA");
				// Elemento 6 = nombre del Proveedor:
				//TextView textoNombreProve = (TextView) nuevaLinea
				//		.findViewById(R.id.id_proveedor_buscar);
				//textoNombreProve.setText("NOMBRE");
				//nombreProveedorV.setText("NOMBRE");

				String cod_prov_string = bdd.proveedorAsignado(id_inventario);
				// Elemento 6 = nombre del Proveedor:
				//Asi estaba con el        android:onClick="elegir"
			//	TextView textoNombreProve = (TextView) nuevaLinea
			//			.findViewById(R.id.id_proveedor_buscar);
			//	textoNombreProve.setText(cod_prov_string);


				Button textoNombreProve = (Button) nuevaLinea.findViewById(R.id.id_proveedor_buscar);
				textoNombreProve .setText(cod_prov_string);
				textoNombreProve .setId(ParametrosInventario.ID_BOTONES + id_inventario);
/****************
 * ****************************************
						 * ********************************/
				textoNombreProve.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						log.log("[-- 994 --]" + "Presion un inventario ", 0);
						elegir(v,id_inventario);
						System.out.println("::: SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII V");
					}
				});
/********************************************************
 * ********************************/

				// Creacin del handler:
				b.setOnClickListener(new View.OnClickListener() {
					public void onClick(@NonNull View v) {
						log.log("[-- 994 --]" + "Presion un inventario ", 0);
						BaseDatos bdd = new BaseDatos(ctxt);
						System.out.println("::: 1 ComprasMainBoard CUAL ABREEEE " + v.getId());
						try {
							if (bdd.estaAbiertoInventarioComprasConId(v.getId()
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
							System.out.println("::: 2 ComprasMainBoard CUAL ABREEEE " + v.getId());
							if (bdd.estaAbiertoInventarioComprasConId(id_invent_con_boton) == true) {
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
						//	System.out.println("::: -------!!!!!!!________!!!!!!--------");
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
			/*ESTO Impide que YO vea los otros inventarios*/
				/*
				elegirProveedor.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					System.out.println("::: ESTO CREO Q GENERA EL ERROR");
					}
				});
*/

				// AGREGAMOS LA LINEA A LA TABLA:
				tablaPrincipal.addView(nuevaLinea);

			} else {
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

		// Verificamos que no hayan inventarios dinamicos para crear una linea
		// vacia
		if (matrizInventariosDinamicos.size() == 0) {
			hayDinamicos = false;
		} else {
			// Caso en el que hay inventarios dinamicos de antes
			hayDinamicos = true;
		}

		hashmapInventarioCompra = matrizInventariosDinamicos
				.get(ParametrosInventario.ID_INV_COMPRAS);

		int id_inv_ficticio = -3;

		ArrayList<Integer> listaEstadisticas = new ArrayList<Integer>();
		int articulosTotalesQueInventariar = 0, articulosNonInventariados = 0;

		int cantidadArticulosEnInventario = 0, articulosYaContadosEnInventario = 0;
		// Si es ficticio es 0
		if (hayDinamicos) {
			ArrayList<Integer> listaEstadisticasCompra;
			// Buscar info en la base de datos y completar las variables
			listaEstadisticasCompra = bdd
						.selectEstadisticasConIdInventario(ParametrosInventario.ID_INV_COMPRAS);
				cantidadArticulosEnInventario = listaEstadisticasCompra.get(0);
				articulosYaContadosEnInventario = listaEstadisticasCompra.get(1);
		} else {
			listaEstadisticas.add(articulosTotalesQueInventariar);
			listaEstadisticas.add(articulosTotalesQueInventariar
					- articulosNonInventariados);
			listaEstadisticas.add(articulosNonInventariados);
			cantidadArticulosEnInventario = listaEstadisticas.get(0);
			articulosYaContadosEnInventario = listaEstadisticas.get(1);
		}
		// CREACIN DE LA LINEA:
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		TableRow nuevaLinea = (TableRow) inflater.inflate(
				R.layout.z_lineaprogressbar_mainboard_compras, null);
		nuevaLinea.setId(ParametrosInventario.ID_LINEAS + id_inv_ficticio);
		// nuevaLinea.setId(ParametrosInventario.ID_LINEA_DINAMICO);
		System.out.println("::: INVDIN 1347 Creacion de la linea id_inv_ficticio " + id_inv_ficticio);
		// Elemento 1 = boton:
		Button b = (Button) nuevaLinea.findViewById(R.id.LPB2_boton);
		b.setText("Inventario Compras");
		b.setId(ParametrosInventario.ID_BOTONES + id_inv_ficticio);
		// Elemento 2 = texto del NOMBRE:
		TextView textoNombre = (TextView) nuevaLinea
				.findViewById(R.id.LPB2_nombre);
		textoNombre.setText("Inv. Comp");
		// Elemento 3 = texto de la FECHA de CREACION:
		TextView textoFecha = (TextView) nuevaLinea
				.findViewById(R.id.LPB2_inicio);
		if (hayDinamicos) {
			String fecha="";
				condR=-3;
				String fechaIVta = hashmapInventarioCompra.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio).trim();
				fecha = hashmapInventarioCompra.get(
						ParametrosInventario.bal_bdd_inventario_fechaInicio)
						.trim();
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
		System.out.println("::: 700 2 COMPRAS MAIN BOAR VER SI LLEGA ACCCCCCCCCCCCCCCAAAAAAAA");


		String cod_prov_string = bdd.proveedorAsignado(-3);
		// Elemento 6 = nombre del Proveedor:
		//Asi estaba con el        android:onClick="elegir"
//		TextView textoNombreProve = (TextView) nuevaLinea
//				.findViewById(R.id.id_proveedor_buscar);
//		textoNombreProve.setText(cod_prov_string);

		Button textoNombreProve = (Button) nuevaLinea.findViewById(R.id.id_proveedor_buscar);
		textoNombreProve .setText(cod_prov_string);
		textoNombreProve .setId(ParametrosInventario.ID_BOTONES + -3);
/****************
 * ****************************************
 * ********************************/
		textoNombreProve.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				log.log("[-- 994 --]" + "Presion un inventario ", 0);
				elegir(v , -3);
				System.out.println("::: SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII V");
			}
		});
/********************************************************
 * ********************************/


		if (hayDinamicos) {
			// Se verifica que esten los dos cerrados
			int estadoCandadoCom = 0;
			estadoCandadoCom = Integer.parseInt(hashmapInventarioCompra
						.get(ParametrosInventario.bal_bdd_inventario_estado));
				if (estadoCandadoCom == 1){
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_ab));
				} else {
					candado.setImageDrawable(getResources().getDrawable(
							R.drawable.candado_cer));
				}
		} else {
			// Se dibuja abierto
			candado.setImageDrawable(getResources().getDrawable(
					R.drawable.candado_ab));
		}
		// Creacin del handler del boton:
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(@NonNull View v) {
				BaseDatos bdd = new BaseDatos(ctxt);
				System.out.println("::: 3 ComprasMainBoard CUAL ABREEEE " + v.getId());
				if (hayDinamicos) {
					try {
							if (bdd.estaAbiertoInventarioConId(v.getId()
									- ParametrosInventario.ID_BOTONES) == true) {
								ClicBotonDinamico((Button) v);
								//ClicBoton((Button) v);
							} else {
								log.log("[-- --]"
										+ "Inventario cerrado con candado", 3);
								Toast.makeText(ctxt,
										"Inventario cerrado con candado",
										Toast.LENGTH_LONG).show();
							}
					} catch (ExceptionBDD e) {
						log.log("[-- 1372 --]" + e.toString(), 3);
						e.printStackTrace();
						Toast.makeText(ctxt, "Inventario cerrado con candado",
								Toast.LENGTH_LONG).show();
					}
				} else {
					ClicBotonDinamico((Button) v);
					//ClicBoton((Button) v);
				}
			}
		});

		// Creacin de los handlers:
		candado.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try {
					ImageView iv = (ImageView) v;
					int estadoCandadoCom = 0;
					BaseDatos bdd = new BaseDatos(ctxt);
					if (hayDinamicos) {
						// Se verifica que esten los dos cerrados
						//if(condR == -1){
						estadoCandadoCom = Integer.parseInt(hashmapInventarioCompra
								.get(ParametrosInventario.bal_bdd_inventario_estado));
					} else {
					}
					if (estadoCandadoCom == ParametrosInventario.INVENTARIO_ABIERTO) {
						// Actualizamos, cerrando el inventario dinamico de
						// venta
						bdd.updateInventario(
								ParametrosInventario.ID_INV_COMPRAS,
								ParametrosInventario.INVENTARIO_CERRADO);
						hashmapInventarioCompra
								.put(ParametrosInventario.bal_bdd_inventario_estado,
										String.valueOf(ParametrosInventario.INVENTARIO_CERRADO));
						iv.setImageDrawable(getResources().getDrawable(
								R.drawable.candado_cer));
					} else {
						// Los guardo como abiertos
						bdd.updateInventario(
								ParametrosInventario.ID_INV_COMPRAS,
								ParametrosInventario.INVENTARIO_ABIERTO);
						hashmapInventarioCompra
								.put(ParametrosInventario.bal_bdd_inventario_estado,
										String.valueOf(ParametrosInventario.INVENTARIO_ABIERTO));
						iv.setImageDrawable(getResources().getDrawable(
								R.drawable.candado_ab));
					}

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
	// desactivarWifi();
	}// Fin de la funcin
	/*** Verifica que los inventarios tengan articulos*/
	private void control_integridad_tablas() {
		try {
			BaseDatos bdd = new BaseDatos(ctxt);
				condR=-3;
		ArrayList<Integer> lista_numeros_inventarios = bdd
					.selectInventariosNumerosEnBddCompras();
			if (lista_numeros_inventarios != null) {
				for (int id_inv : lista_numeros_inventarios) {
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
	 * Metodo que manejea el funcionamiento de los botones de la tabla al
	 * hacerles click en el caso comun, entra a la activity
	 * PaginaInventario.java, pasando como parmetro el valor del numero de
	 * inventario asociado al boton
	 *
	 * @param boton
	 */
	private void ClicBoton(@NonNull Button boton) {
		int numeroInventarioAsociadoAlBoton = (boton.getId() - ParametrosInventario.ID_BOTONES);
		Intent intentInventario = new Intent(ctxt, PaginaCompras.class);
		intentInventario.putExtra(ParametrosInventario.extra_numeroInventarioCompra,
				numeroInventarioAsociadoAlBoton);
		// intentInventario.putExtra(ParametrosInventario.extra_bandera_invs_dinamicos,
		// ParametrosInventario.extra_valor_bandera_invs_dinamicos_no);
		startActivityForResult(intentInventario,
				ParametrosInventario.REQUEST_INVENTARIO_COMPRAS);
	}

	private void ClicBotonDinamico(Button boton) {
		// 1 Si hay inventarios creados debe preguntar por eliminar o seguir con el anterior
		if (hayDinamicos) {
			// 1.1.1 En caso positivo pasa al inventario actual
			View.OnClickListener listenerPositivo = new View.OnClickListener() {

				public void onClick(View v) {
					// Lo que hace con el boton del si
					log.log("[-- --]"
							+ "Presiono para ir a los inventarios dinamicos", 0);
					dialogoContinuarInventario.dismiss();
					Intent intentInventario = new Intent(ctxt,
							PaginaCompras.class);
					intentInventario.putExtra(
							ParametrosInventario.extra_numeroInventarioCompra,
							ParametrosInventario.ID_INV_COMPRAS);
					 intentInventario.putExtra(ParametrosInventario.extra_bandera_invs_dinamicos,
					 ParametrosInventario.extra_valor_bandera_invs_dinamicos_si);
					startActivityForResult(intentInventario,
							ParametrosInventario.REQUEST_INVENTARIO_COMPRAS);
				}
			};
			// 1.1.2 En caso negativo muestra otro cartel para verificar si
			// borra
			View.OnClickListener listenerNegativo = new View.OnClickListener() {

				public void onClick(View v) {
					// Lo que hace con el no Debe preguntar nuevamente para eliminar el inventario
					// guardado Abrir otra ventana y preguntar si realmente quiere eliminar los datos
					dialogoContinuarInventario.dismiss();
					// 1.1.2.1 En caso de que quiera borrar se elimina y genera uno nuevo pasando a la Pagina correspondiente
					View.OnClickListener listenerPositivo = new View.OnClickListener() {

						public void onClick(View v) {
							log.log("[-- 1617 --]" + "Se presiono el boton si",
									0);
							BaseDatos bdd = new BaseDatos(ctxt);
							// Lo que hace con el boton del si
							dialogoBorrarInventarioDinamico.dismiss();
							// Creamos los inventarios dinamicos
							Inventario inventarioDinamicoCompra = new Inventario(
									ParametrosInventario.ID_INV_COMPRAS,
									"Inv. dinamico "
											+ String.valueOf(ParametrosInventario.ID_INV_COMPRAS)
											+ " de compra",
									new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Date()),
									"",
									ParametrosInventario.INVENTARIO_ABIERTO,
									ParametrosInventario.COD_LUGAR_INVENTARIO_VENTA);
							try {
								// Borrar datos del inventario
								bdd.borrarInventarioConArticulos(ParametrosInventario.ID_INV_COMPRAS);
								// Crearlo de nuevo
								bdd.insertInventarioComprasEnBdd(inventarioDinamicoCompra);
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
									PaginaCompras.class);
							intentInventario
									.putExtra(
											ParametrosInventario.extra_numeroInventarioCompra,
											ParametrosInventario.ID_INV_COMPRAS);
							startActivityForResult(
									intentInventario,
									ParametrosInventario.REQUEST_INVENTARIO_COMPRAS);
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
							"Nueva Compra",
							"Si genera una nueva compra se borrara cualquier " +
									"compra en curso que se estuviera realizando aun no exportada.\nCUIDADO:"
									+ "Los datos no han sido exportados al sistema DEBO BackOffice para ser "
									+ "procesados para el correcto control de stock y se borraran",
							DialogPerso.ALERTAR, listenerPositivo,
							listenerNegativo);
					dialogoBorrarInventarioDinamico.show();
				}
			};
			// 1.1 Genera un dialog que pregunta si se continua con el
			// inventario o se
			// borra y genera algo nuevo
			dialogoContinuarInventario = new DialogPersoComplexSiNo(
					ctxt,
					"Continuar Compras",
					"Desea continuar trabajando con la recepcion de compra actual?",
					DialogPerso.VALIDAR, listenerPositivo, listenerNegativo);

			dialogoContinuarInventario.show();

		} else {
			// 2 Si no hay inventarios , Se deben crear 2 inventarios (uno para
			// venta y otro para deposito)
			// Logica de creacion de los inventarios dinamicos, las ponemos aca
			// Proceso de creacin del inventario nuevo:

			bdd = new BaseDatos(ctxt);

			// 2.1 Creo los objetos para los dos inventarios nuevos
			final Inventario inventarioDinamicoCompra = new Inventario(
					ParametrosInventario.ID_INV_COMPRAS,
					"Inv. dinamico "
							+ String.valueOf(ParametrosInventario.ID_INV_COMPRAS)
							+ " de venta", new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss").format(new Date()), "",
					ParametrosInventario.INVENTARIO_ABIERTO,
					ParametrosInventario.COD_LUGAR_INVENTARIO_VENTA);
			// 2.2 Insertamos en la base de datos:
			try {
//				if (ParametrosInventario.InventariosVentas == true) {
				bdd.insertInventarioComprasEnBdd(inventarioDinamicoCompra);
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
					PaginaCompras.class);
			intentInventario.putExtra(
					ParametrosInventario.extra_numeroInventarioCompra,
					ParametrosInventario.ID_INV_COMPRAS);
			startActivityForResult(intentInventario,
					ParametrosInventario.REQUEST_INVENTARIO_COMPRAS);
		}

	}

	public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent intentRespondido) {
		try {
			super.onActivityResult(requestCode, resultCode, intentRespondido);
			Bundle bundle = null;
			if (intentRespondido != null) {
				bundle = intentRespondido.getExtras();
			}
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
			} else if (requestCode == ParametrosInventario.REQUEST_INVENTARIO_COMPRAS) {
				try {
					refreshTablaPrincipal();

					controlFin();
				} catch (ExceptionBDD e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			/*
			} else if (requestCode == Parametros.REQUEST_WIFI_IMPORT
					&& resultCode == RESULT_OK) {
				// cerrarMenuEspera();
				// 2 Si volvemos de WIFI import todo bien, vamos a seleccionar
				// Inventario
				Intent intentInventario = new Intent(ctxt,
						SeleccionInventarios.class);
				startActivity(intentInventario);
				finish();
			} else if (requestCode == Parametros.REQUEST_WIFI_IMPORT
					&& resultCode != RESULT_OK) {
				// cerrarMenuEspera();
				// 3 Si hubo un error con WIFI import da la opcin de cargar
				// por USB
				desactivarWifi();
				showSimpleDialogSiNo(
						"Error de conexion a la red",
						"La red hasta el servidor no ha podido ser establecida (1).\n\nUsted desea importar sus datos por medio de un Dispositivo (conectelo en aquel caso)?",
						UsbProvider.class).show();
						*/
			} else if (requestCode == Parametros.REQUEST_WIFI_EXPORT
					&& resultCode == RESULT_OK) {
				// 4 Si volvemos de WIFI Export todo OK vamos a hacer
				// exportacion por WIFI
				ExportarDatos unaExportacion = new ExportarDatos();
				unaExportacion.execute(ctxt);

			} else if (requestCode == Parametros.REQUEST_WIFI_EXPORT
					&& resultCode != RESULT_OK) {
				// 5 Si volvemos de WIFI export

				cerrarMenuEspera();
				desactivarWifi();
				dialogoFin = new AlertDialog.Builder(this);
				dialogoFin
						.setTitle("Error de conexion a la red")
						.setMessage(
								"La red hasta el servidor no ha podido ser establecida (2).\n (Intente conectarse nuevamente a la red)")
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

		} catch (Exception e) {

			log.log("[-- 492 --]" + e.toString(), 4);
			e.printStackTrace();
			showSimpleDialogOK("Error", e.toString()).show();
		}
	}

	private void controlFin() throws ExceptionBDD {
		if (estanTerminadosTodosLosInventarios() == true) {
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
	 * Verifica si estan todos los inventarios terminados en la BD para avisar
	 * que se exporten posteriormente
	 *
	 * @return
	 * @throws ExceptionBDD
	 */
	private boolean estanTerminadosTodosLosInventarios() throws ExceptionBDD {
		boolean result = true;
		BaseDatos bdd = new BaseDatos(ctxt);
		listaInventariosSeleccionados = bdd.selectInventariosNumerosEnBddCompras();
		for (int numInventario : listaInventariosSeleccionados) {
			if (bdd.selectEstadisticasConIdInventario(numInventario).get(2) > 0) {
				result = false;
			}
		}
		return result;
	}
	void mostrarMensaje(int valorRecibido){
			if(valorRecibido == 0){
				Toast.makeText(ctxt,
						"El articulo no tiene habilitado el deposito",
						Toast.LENGTH_LONG).show();
			}
	}

	/**
	 * Tarea asincronica de exportacion de los datos
	 * @author DamianC
	 */
	protected class ExportarDatos extends
			AsyncTask<Context, Integer, RespuestasExportar> {
		private static final boolean Referencia = false;
		@NonNull
        protected RespuestasExportar doInBackground(Context... arg0) {
			boolean result = true;
			if (ParametrosInventario.ProductosNoContabilizados == 2) {
				ArrayList<Referencia> Referencias = new ArrayList<Referencia>();
				BaseDatos bd = new BaseDatos(ctxt);
				String fechaInicioInventario  = "";
				int numero_inventario_elegido = inventarios_elegidos;
				if(inventarios_elegidos==-3 ){
					try {
						Inventario inven = bd.selectInventarioConNumeroCompra(-3);
					} catch (ExceptionBDD e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
								Articulo art = new Articulo(ref.getSector(),
										ref.getArticulo(), ref.getBalanza(), ref.getDecimales(), codbar,
										codbarcompleto , -1,
										ref.getDescripcion(),
										ref.getPrecio_venta(),
										ref.getPrecio_costo(), "", 0,
										ref.getExis_venta(),
										ref.getExis_deposito(),
										ref.getDepsn(),
										fechaInicioInventario,fechaYA);
								bd.insertArticuloEnBdd_conFechaFin(art);
							} else {
								System.out.println("::: InventarioMainBoard ");
								Log.e("Referencia con articulo", "No agregar "+ articulo.getDescripcion().toString());
							}
						} catch (ExceptionBDD e) {
							Toast.makeText(ctxt,
									"Error al recorrer las referencias",
									Toast.LENGTH_LONG).show();
							result = false;
						}
					}
				}else if(inventarios_elegidos >0){
				}
			}else{
				try {
					System.out.println("::: 1688 ComprasMainBoard " + inventarios_elegidos);
					//Inventario inven = bd.selectInventarioConNumeroParametro(-3,ParametrosInventario.ProductosNoContabilizados);
					Inventario inven = bd.selectInventarioConNumeroParametro(inventarios_elegidos,ParametrosInventario.ProductosNoContabilizados);
				} catch (ExceptionBDD e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG,
						new Date(), "MAIN BOARD", "0", "Export: 0 %");
			} catch (Exception e) {
				log.log("[-- 1943 --]" + e.toString(), 4);
				e.printStackTrace();
			}
			try {
				bdd = new BaseDatos(ctxt);
				// 1 Fabricamos la lista de todos los inventarios que estn cerrados:
				ArrayList<Integer> listaInventariosCerrados = null;
				try {
					listaInventariosCerrados = bdd
							.selectInventariosCerradosEnBddCompras();
				} catch (ExceptionBDD e4) {
					log.log("[-- 1959 --]" + e4.toString(), 4);
					e4.printStackTrace();
				}
				if (listaInventariosCerrados.size() <= 0) {
					Toast.makeText(ctxt,
							"Debe haber por lo menos un inventario cerrado",
							Toast.LENGTH_LONG).show();
					result = false;
				}
				try {
					// 2 se realiza la exportacin de los datos en las BD
					result = bdd.exportarTodasBaseDatosSQLiteCompras(listaInventariosCerrados);
				} catch (ExceptionHttpExchange e2) {

					log.log("[-- 1961 --]" + e2.toString(), 4);
					e2.printStackTrace();
					return new RespuestasExportar(
							RespuestasExportar.CODIGO_ERROR,
							"Error Export - Export a la BDD - Articulos - "
									+ e2.toString());
				} catch (ExceptionBDD e) {

					log.log("[-- 1969 --]" + e.toString(), 0);
					e.printStackTrace();
					return new RespuestasExportar(
							RespuestasExportar.CODIGO_ERROR,
							"Error Export - Export a la BDD - Articulos - "
									+ e.toString());
				}

				// 3 Exportamos los estados de los inventarios:
				try {
					if (result == true) {
						HttpSender httpSender = new HttpSender(
								Parametros.CODIGO_SOFT_DEBOINVENTARIO);
						for (int inventario : listaInventariosCerrados) {
							if (bdd.selectArticulosConNumeroInventarioCompra(
									inventario).size() <= 0) {
								result &= httpSender.send_liberacion(
										inventario, 0);
							} else {
								result &= httpSender.send_liberacion(
										inventario, 1);
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
							RespuestasExportar.CODIGO_ERROR,
							"Error Export - Export a la BDD - Inventarios - "
									+ e2.toString());
				} catch (ExceptionBDD e) {
					log.log("[-- 2022 --]" + e.toString(), 4);

				} catch (Exception e3) {
					log.log("[-- 2024 --]" + e3.toString(), 4);
					e3.printStackTrace();
					return new RespuestasExportar(
							RespuestasExportar.CODIGO_ERROR,
							"Error Export - Export a la BDD - Inventarios - "
									+ e3.toString());
				}

				int i = 1;

				// 5 Comprobamos que todo paso bien
				if (result == false) {
					return new RespuestasExportar(
							RespuestasExportar.CODIGO_ERROR,
							"Error Export - Export a la BDD - Verifique la conexion a la red");
				}

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG,
							new Date(), "MAIN BOARD", "0", "Export: 20 %");
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
						senderFotos = new HttpSender(
								Parametros.CODIGO_SOFT_DEBOINVENTARIO);
					} catch (ExceptionHttpExchange e) {

						log.log("[-- 2046 --]" + e.toString(), 4);
						e.printStackTrace();
						return new RespuestasExportar(
								RespuestasExportar.CODIGO_WARNING,
								"Error Export - Export imagenes - "
										+ e.toString());
					}

					for (File unaFoto : carpetaFotos.listFiles()) {
						result &= senderFotos
								.send_foto(ParametrosInventario.URL_CARPETA_FOTOS
										+ unaFoto.getName());
					}
				}
				i = 5;
				// 6.1 Comprobar que todo esta bien
				if (result == false) {
					return new RespuestasExportar(
							RespuestasExportar.CODIGO_WARNING,
							"Error Export - Export imagenes");
				}
				i = 6;

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG,
							new Date(), "MAIN BOARD", "0", "Export: 40 %");
				} catch (Exception e) {
					e.printStackTrace();

					log.log("[-- 2075 --]" + e.toString(), 4);
				}

				// 7 Exportamos los logs:
				File archivoLOG = new File(ParametrosInventario.URL_ARCHIVO_LOG);
				i = 7;
				if (archivoLOG.exists() == true) {
					HttpSender senderLOG;
					try {
						senderLOG = new HttpSender(
								Parametros.CODIGO_SOFT_DEBOINVENTARIO);
						result = senderLOG
								.send_txt(ParametrosInventario.URL_ARCHIVO_LOG);
					} catch (ExceptionHttpExchange e) {

						e.printStackTrace();
						return new RespuestasExportar(
								RespuestasExportar.CODIGO_WARNING,
								"Error Export - Export logs - " + e.toString());
					}

					i = 8;

					// 7.1 Comprobar que todo esta bien:
					if (result == false) {
						return new RespuestasExportar(
								RespuestasExportar.CODIGO_WARNING,
								"Error Export - Export logs");
					}
					i = 9;
					// archivoLOG.delete();
					i = 10;
				}

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG,
							new Date(), "MAIN BOARD", "0", "Export: 60 %");
				} catch (Exception e) {

					log.log("[-- 2114 --]" + e.toString(), 4);
					e.printStackTrace();
				}

				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG,
							new Date(), "MAIN BOARD", "0", "Export: 90 %");
				} catch (Exception e) {
					log.log("[-- 2136 --]" + e.toString(), 4);
					e.printStackTrace();
				}

			} catch (Exception e) {
				return new RespuestasExportar(RespuestasExportar.CODIGO_ERROR,
						"Error Export - " + " - Error fatal - "
								+ e.toString());
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
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG,
							new Date(), "MAIN BOARD", "0",
							"Export: 100 % --- EXITOSO");
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
					showSimpleDialogOK("Exportacion Cancelada",
							"Explicacion: \n\n" + result.getMensaje()).show();
				} else if (result.getCodigoError() == RespuestasExportar.CODIGO_WARNING) {
					showSimpleDialogOK(
							"Exportacion Finalizada con Advertencias",
							"Explicacion: \n\n" + result.getMensaje()).show();
				}
				try {
					RegistroLog.log(ParametrosInventario.URL_ARCHIVO_LOG,
							new Date(), "MAIN BOARD", "0", "Export: 100 % --- "
									+ result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/** Devuelve una informacin sobre el tamao de los archivos (aqui los archivos de exportacin). Si los archivos son de tamaos inferiores a 1
	 * bytes, se considera que el archivo esta vacio.
	 * @return (boolean) TRUE si los archivos tienen un tamao aceptable, FALSE	sino.*/
	private boolean control_buena_exportacion() {
		File carpeta_destino = new File(
				ParametrosInventario.CARPETA_DESDETABLET);
		boolean respuesta = true;
		// Controlamos la integridad de todos los archivos de export
		for (File archivo : carpeta_destino.listFiles()) {
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
	 * Cierra el menu de espera
	 */
	private void cerrarMenuEspera() {
		popupEspera.dismiss();
	}

	@Override
	public void activarWifi() {

	}

	@Override
	public void desactivarWifi() {

	}

	@Override
	public boolean estaEnModoAvion() {
		return false;
	}
}