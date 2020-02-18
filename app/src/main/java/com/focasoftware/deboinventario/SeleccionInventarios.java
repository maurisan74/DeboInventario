package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Activity que muestra una lista con los inventarios disponibles para importar
 * y permite seleccionar los inventarios a de la misma.
 * 
 * @author GuillermoR
 * 
 */
public class SeleccionInventarios extends Activity implements DialogPersoSimple {
	//
	//
	//
	// *********************************************************************************************
	//
	// *************************
	// *************************
	// **** ATRIBUTOS ****
	// *************************
	// *************************
	//

	@NonNull
    private Context ctxt = this;
	private Thread threadBackground;

	@NonNull
    private BaseDatos bdd = new BaseDatos(ctxt);

	// private ScrollView scrollview;
	private TableLayout tablaMenuScrollable;
	private Button botonNext;
	private Button botonPrevious;
	private Button botonUsb;
	private Button botonRefresh;
	private ProgressBar progressbar;

	@Nullable
    private LinkedHashMap<String,String> inventariosDetalles = new LinkedHashMap<String,String>();
	@NonNull
    private ArrayList<Integer> inventarios_seleccionados = new ArrayList<Integer>();
	// private HashMap<Integer,ArrayList<HashMap<String,Integer>>>
	// matrizArticulosCadaInventario = new
	// HashMap<Integer,ArrayList<HashMap<String,Integer>>>();

	private ProgressDialog popupCarga;
	private int pasoInventario;
	private double pasoArticulo;
	private double carga = (double) 0;

	/*
	 * Variables para obtener los check de preferencias
	 * Inventario Ventas / Deposito
	 * */
	private SharedPreferences settings;
	
	private RadioButton CheckedInventariosVentas;
	private RadioButton CheckedInventariosDeposito;
	
	private boolean InventariosVentas;
	private boolean InventariosDeposito;
	private int condR = 0;
	//
	//
	//
	// *********************************************************************************************
	//
	// *****************************
	// *****************************
	// **** CONSTRUCTORES ****
	// *****************************
	// *****************************

	@NonNull
    GestorLogEventos log = new GestorLogEventos();

	public void onCreate(Bundle savedInstanceState) {
		// Creamos la pgina:
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_seleccioninventarios);

System.out.println("::: SeleccionInventario 99 ");
		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 101 --]" + "Inicia Seleccion deInventarios", 2);
		// Parametros.PREF_URL_CONEXION_SERVIDOR =
		// "http://192.168.1.156/Dalvian/webservice.php";
		System.out.println("::: SeleccionInventario 108 ");
		// Lanzamos un thread que nos va a recuperar los datos de todas las
		// rutas via HTTP:
		threadBackground = new Thread(new Runnable() {
			public void run() {
				try {
					// Recuperar todas las rutas posibles bajo la forma de un
					// hashtable de hashtable
					// {indice ; {numero ruta ; nombre ; ultimo periodo ; numero
					// tablet que lo tiene (sino 0)}}:
					inventariosDetalles = getTodosElementos();
					System.out.println("::: SeleccionInventario 119 inventariosDetalles " + inventariosDetalles);
					// wait(500);

				} catch (Exception e) {
					
					log.log(e.toString(),4);
					e.printStackTrace();
				} catch (ExceptionHttpExchange e) {
					
					log.log(e.toString(),4);
					e.printStackTrace();
				}
			}
		});
		threadBackground.start();

		// Recuperamos los elementos grficos necesarios:
		tablaMenuScrollable = (TableLayout) findViewById(R.id.SI_tablaMenuScrollable);
		botonNext = (Button) findViewById(R.id.SI_boton_siguiente);
		botonPrevious = (Button) findViewById(R.id.SI_boton_precedente);
		botonUsb = (Button) findViewById(R.id.SI_boton_usb);
		botonRefresh = (Button) findViewById(R.id.SI_boton_refresh);
		progressbar = (ProgressBar) findViewById(R.id.SI_progressbar);
		
//		System.out.println("::: SeleccionInventarios 142 " +inventariosDetalles);
		
		System.out.println("::: SeleccionInventarios 142 aca llama al llenarMenuScrollableConOpciones");
		llenarMenuScrollableConOpciones();

		
		
		/*
		 * Corroboramos que inventario esta seleccionado. Ventas / Deposito
		 * 
		 * */
//		SharedPreferences.Editor editor = settings.edit();
		System.out.println("::: SeleccionInventarios 170 seleccion inventarios checkkkkkk");
		
		
		
		//Lo de arriba deberia ir en algun boton
		
		
		// Listeners de los botones:
		botonNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				log.log("[-- 144 --]" + "Se presiono el boton siguiente",0);

				try {
					// Insertar los Inventarios seleccionados en la base:
					bdd = new BaseDatos(ctxt);
					// bdd.destruirYReconstruir(ParametrosInventario.tabla_inventarios);
					// bdd.destruirYReconstruir(ParametrosInventario.tabla_articulos);

					// Rellenar con los articulos correspondientes:
					popup_carga_start();
					CargarDatosArticulos unaCarga = new CargarDatosArticulos();
					unaCarga.execute(ctxt);

				} catch (Exception e) {
					
					log.log(e.toString(),4);
					e.printStackTrace();
					showSimpleDialogOK("Error", e.toString()).show();
					return;
				} /*
				 * catch (ExceptionBDD e) { e.printStackTrace();
				 * showSimpleDialogOK("Error", e.toString()).show(); return; }
				 */
				/*
				 * // Llamar a la pgina siguiente (identificacin del
				 * operador): Intent nextIntent = new
				 * Intent(SeleccionInventarios.this, InventarioMainBoard.class);
				 * 
				 * // Pasamos la lista de las rutas seleccionadas en Extra
				 * despues de haberlo ordenado: startActivity(nextIntent);
				 * 
				 * finish();
				 */
			}
		});

		botonUsb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				log.log("[-- 183 --]" + "Se presiono el boton usb",0);
				Intent intentUSB = new Intent(SeleccionInventarios.this,
						UsbProvider.class);
				intentUSB.putExtra(Parametros.extra_uri_usb,
						ParametrosInventario.CARPETA_ATABLET);
				startActivity(intentUSB);
				finish();
			}
		});

		botonPrevious.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				log.log("[-- 196 --]" + "Se presiono el boton anterior",0);
				// Llamamos a la pgina anterior y cerramos:
				Intent volverPaginaAnterior = new Intent(
						SeleccionInventarios.this, InventarioMainBoard.class);
				startActivity(volverPaginaAnterior);
				finish();
			}
		});

		botonRefresh.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				log.log("[-- 209 --]" + "Se presiono el obton refrescar",0);
				Intent intentWifi = new Intent(SeleccionInventarios.this,
						WiFiControlador.class);
				startActivityForResult(intentWifi, Parametros.REQUEST_WIFI);
			}
		});

		// Esperemos que se termine el thread y mostramos los resultados:
		try {
			threadBackground.join(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Toast.makeText(ctxt, "El thread ha sido interrumpido!",
					Toast.LENGTH_LONG).show();
			SystemClock.sleep(2000);

			Intent volverPaginaAnterior = new Intent(SeleccionInventarios.this,
					DeboInventario.class);
			startActivity(volverPaginaAnterior);
			finish();
		} catch (Exception e) {
			
			log.log(e.toString(),4);
			e.printStackTrace();
		}

		// Escondemos la barra de carga:
		progressbar.setVisibility(View.GONE);

		// Despues del join (por xito o vencimiento del tiempo):
		if (threadBackground.isAlive() == true) {
			Toast.makeText(ctxt, "El thread se quedo bloqueado en ejecucion!",
					Toast.LENGTH_LONG).show();
			
			log.log("[-- 243 --]" + "El thread se quedo bloqueado en ejecucion!",3);

			Intent volverPaginaAnterior = new Intent(SeleccionInventarios.this,
					DeboInventario.class);
			startActivity(volverPaginaAnterior);

			finish();
			return;
		} else if (inventariosDetalles == null) {
			Toast.makeText(
					ctxt,
					"Los datos de inventarios no han podido ser recuperados!\n\n Revise su conexion WiFi",
					Toast.LENGTH_LONG).show();
			log.log("[-- 256 --]" + "Los datos de inventarios no han podido ser recuperados!\n\n Revise su conexion WiFi",3);
			Intent volverPaginaAnterior = new Intent(SeleccionInventarios.this,
					DeboInventario.class);
			startActivity(volverPaginaAnterior);

			finish();
			return;
		} else {
			llenarMenuScrollableConOpciones();
			System.out.println("::: SeleccionInventarios 275");
		}
	}

	//
	//
	//
	// *********************************************************************************************
	//
	// ***********************
	// ***********************
	// **** METODOS ****
	// ***********************
	// ***********************
	//

	/**
	 * Devuelve la lista de todas las rutas conocidas en el servidor:
	 * 
	 * @return matriz detallada de las rutas
	 * @throws ExceptionHttpExchange
	 */
	@Nullable
    public LinkedHashMap<String, String> getTodosElementos()
			throws ExceptionHttpExchange {
		System.out.println("::: SeleccionInventarios 292 getTodosElementos");
		// Variable de return:
		LinkedHashMap<String, String> todosElementos = new LinkedHashMap<String,String>();

///*Lo siguiente es para saber con que inventario se esta trabajando*/		
//		boolean condicionRadio = ParametrosInventario.InventariosVentas;
//	
//		if(condicionRadio == true){
//			condR=-1;
//			
//		}else{
//			condR=-2;
//		}
//
//		System.out.println("::: SeleccionInventario 340 condR == " + condR);

		/*Lo anterior es para saber con que inventario se trabaja*/
		
		HttpReader httpReader = new HttpReader(
				Parametros.PREF_URL_CONEXION_SERVIDOR,
				ParametrosInventario.FONCION_CARGAR_INVENTARIOS);
		try {
//			System.out.println("::: SeleccionInventarios 300 getTodosElementos");
			todosElementos = httpReader.readInventarios(condR);
//			System.out.println("::: SeleccionInventarios 302 TodosElementos "+ todosElementos);
		} catch (ExceptionHttpExchange e) {
			
			log.log(e.toString(),4);
			// Toast.makeText(ctxt, e.print(), Toast.LENGTH_LONG).show();
			Intent volverPaginaAnterior = new Intent(SeleccionInventarios.this,
					DeboInventario.class);
			ctxt.startActivity(volverPaginaAnterior);
			finish();
		}
//		System.out.println("::: SeleccionInventarios 311 getTodosElementos " + todosElementos);
		return todosElementos;
	}

	/**
	 * Llena la tabla scrollable con todas las rutas identificadas
	 * 
	 */
	public void llenarMenuScrollableConOpciones() {
		System.out.println("::: SeleccionInventarios 322 ");
//		System.out.println("::: SeleccionInventarios 322 con get " + inventariosDetalles.get("294"));

		String[] Hlist = inventariosDetalles.keySet().toArray(new String[0]);
		 for(int z = 0; z < Hlist.length; z++){
			 System.out.println("::: SeleccionInventarios 332 ");
	            System.out.println(Arrays.toString(Hlist));
	        }
		 System.out.println("::: SeleccionInventarios 335 ");
		 String strhash = inventariosDetalles.toString();
		 
		 String[] arrayStr = strhash.split(",");
		 System.out.println("::: SeleccionInventarios 343 ");
		// En este momento tenemos un array en el que cada elemento es un color.
//		 for (int i = 0; i < arrayStr.length; i++) {
//			 System.out.println("::: SeleccionInventarios 347 ");
//		}
		//		HashMap<String, String > h = new HashMap<String, String>(){{
//	        put("a","b");
//	        put("c","d");
//	        put("e","f");
//	    }};
//	    String[] harr1 = new String[h.size()];
//		String[] harr2 = new String[h.size()];
//		  Set hentries = h.entrySet();
//	        Iterator hentriesIterator = hentries.iterator();
//	        int j = 0;
//	        while(hentriesIterator.hasNext()){
//	            Map.Entry mapping = (Map.Entry) hentriesIterator.next();
//	            harr1[j] = mapping.getKey().toString();
//	            harr2[j] = mapping.getValue().toString();
//	            j++;
//	        }
        String[] arr1 = new String[inventariosDetalles.size()];
		String[] arr2 = new String[inventariosDetalles.size()];
		System.out.println("::: SeleccionInventarios 375 ");
		  Set entries = inventariosDetalles.entrySet();
	        Iterator entriesIterator = entries.iterator();
	        int i = 0;
	        System.out.println("::: SeleccionInventarios 379 ");
	        while(entriesIterator.hasNext()){
	        	System.out.println("::: SeleccionInventarios 381 ");
	            Map.Entry mapping = (Map.Entry) entriesIterator.next();
	            arr1[i] = mapping.getKey().toString();
	            arr2[i] = mapping.getValue().toString();
//	            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//	            System.out.println( Arrays.toString(arr2));
	            i++;
	        }
	        System.out.println("::: SeleccionInventarios 389 ");

		// Primero borramos todo elcontenido de la tabla:
		tablaMenuScrollable.removeAllViews();
		//List<String> list = new ArrayList<String>(map.values());
		//List<String> list = new ArrayList<String>(inventariosDetalles.toString());
		// Recorremos la matriz de todas las rutas encontradas en el servidor, y
		// lo mostramos bajo forma de checkboxes:
		Set<String> listaId = inventariosDetalles.keySet();
//		Set<String> nombreInv = inventariosDetalles.get(listaId).keySet();
//		System.out.println("::::: SeleccionInventarios 335 " + nombreInv);
//		System.out.println("::: SeleccionInventario 329 listaId" +  listaId);
		Iterator<String> itr = listaId.iterator();
		String key;
		
int cp = 0;
System.out.println("::: SeleccionInventarios 410 ");
String str = inventariosDetalles.toString();
String delimiter = ",";
String[] temp;
temp = str.split(delimiter);

String cadena = "";
int n =0;
int dep = 0;
for(int d =0; d < temp.length ; d++)

System.out.println("::: SeleccionInventarios 417 temp[d] "+temp[d]);


System.out.println("::: SeleccionInventarios iiiiiiiiiii " + inventariosDetalles.toString());
		while (itr.hasNext() == true) {
			System.out.println("WHILE 1 ");
			System.out.println("WHILE 1 cp " + cp);
			key = itr.next();
//			HashMap<String, String> tabla = inventariosDetalles.get(key);
			System.out.println("WHILE 2 temp[cp] " + temp[cp]);

			String strDos = temp[cp];
			if(cp==0){
				strDos = strDos.substring(1,strDos.length());
			}
			String delimiterDos = "=";
			String[] tempDos;
			tempDos = strDos.split("=");

			

			System.out.println("WHILE 3 tempDos "+ tempDos[0]);

//			cp = cp+1;
//			System.out.println("WHILE 4  cp " + cp + " tempDos[cp] " + tempDos[cp]);
//			String strTres = tempDos[cp];
			String strTres = tempDos[0];
			String delimiterTres = "-";
			String[] tempTres;
			tempTres = strTres.split(delimiterTres);
			
			System.out.println("WHILE 4");
			System.out.println("ESTE SERIA EL NOMBRE " + tempTres[0]);
		
			System.out.println(temp[cp]);

			String tabla = inventariosDetalles.get(key);
			// CREACION DE LAS LINEAS DE LA TABLA SCROLLABLE:
			TableRow lineaTabla = new TableRow(ctxt);
			CheckBox nuevoChkBox = new CheckBox(ctxt);
			TextView textView = new TextView(ctxt);
			/* Creacion de las checkboxes: */
			nuevoChkBox.setTextSize(20);
			nuevoChkBox.setClickable(false);
			nuevoChkBox.setGravity(Gravity.CENTER_VERTICAL);
			nuevoChkBox.setId(ParametrosInventario.ID_CHECKBOXES
					+ Integer.parseInt(key));
			nuevoChkBox.setText(" Inventario n" + key + " ");
			nuevoChkBox.setTextColor(Color.WHITE);

			/* Creacin de los scroll y del texto que contienen: */
			textView.setTextSize(20);
			textView.setTextColor(Color.WHITE);
//			textView.setText("        "
//					+ tabla.get(ParametrosInventario.bal_bdd_inventario_descripcion));
			textView.setText("        "
					+ tempTres[0]);
//			System.out.println("::: SeleccionInventarios 356 inv desc " + 
//					tabla.get(ParametrosInventario.bal_bdd_inventario_descripcion));
//			System.out.println("::: SeleccionInventarios 356 inv desc " + inventariosDetalles);
			/* Juntamos todos los elementos en la linea: */
			lineaTabla.addView(nuevoChkBox);
			lineaTabla.addView(textView);
			lineaTabla.setGravity(Gravity.CENTER_VERTICAL);
		
			System.out.println("::: SeleccionInventario seleccionar inventarios 467");

			/* Handler en la linea */
			lineaTabla.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					System.out.println("::: SeleccionInventario seleccionar inventarios 472");
					TableRow lineaSeleccionada = (TableRow) view;
					CheckBox chkb = (CheckBox) lineaSeleccionada.getChildAt(0);

					if (chkb.isChecked() == false) {
						
						
						if(inventarios_seleccionados.isEmpty()){
							System.out.println("::: SeleccionInventarios es null");
						
							chkb.setChecked(true);
							inventarios_seleccionados.add(chkb.getId()
							- ParametrosInventario.ID_CHECKBOXES);
							lineaSeleccionada.setBackgroundColor(Color.YELLOW);
							chkb.setTextColor(Color.BLACK);
							((TextView) lineaSeleccionada.getChildAt(1))
									.setTextColor(Color.BLACK);
						
						}else{
							System.out.println("::: SeleccionInventarios no es null");
	
							Toast.makeText(ctxt,
									"Solo se puede importar de a un solo inventario.",
									Toast.LENGTH_LONG).show();
							
						}
						
						
//						inventarios_seleccionados.add(chkb.getId()
//								- ParametrosInventario.ID_CHECKBOXES);
//						
//						
//						lineaSeleccionada.setBackgroundColor(Color.YELLOW);
//						chkb.setTextColor(Color.BLACK);
//						((TextView) lineaSeleccionada.getChildAt(1))
//								.setTextColor(Color.BLACK);
					} else {
						chkb.setChecked(false);
						inventarios_seleccionados.remove((Object) (chkb.getId() - ParametrosInventario.ID_CHECKBOXES));
						lineaSeleccionada.setBackgroundColor(Color.TRANSPARENT);
						System.out.println("::: SeleccionInventario 537 desmarcar inventarios_seleccionados ");

						
						chkb.setTextColor(Color.WHITE);
						((TextView) lineaSeleccionada.getChildAt(1))
								.setTextColor(Color.WHITE);
					}

					if (inventarios_seleccionados.size() == 0) {
						botonNext.setVisibility(View.INVISIBLE);
					} else {
						botonNext.setVisibility(View.VISIBLE);
					}
				}
			});
//			System.out.println("500");
			cp= cp +1;
//			System.out.println("502");
			System.out.println("50 3 lineaTabla " + lineaTabla);
			// AGREGAMOS LA LINEA A LA TABLA PRINCIPAL:
			tablaMenuScrollable.addView(lineaTabla);

		} // endwhile
	} // endfunction

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Parametros.REQUEST_WIFI && resultCode == RESULT_OK) {
			Intent i = new Intent(SeleccionInventarios.this,
					SeleccionInventarios.class);
			startActivity(i);
			finish();
		}

		else if (requestCode == Parametros.REQUEST_WIFI
				&& resultCode == RESULT_CANCELED) {
			showSimpleDialogOK("Error de conexion a la red WiFi",
					"No se pudo establecer una conexion con la red WiFi.")
					.show();
		}

	}

	public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {
		
		log.log("[-- 411 --]" + "titulo: " + titulo + ", \n mensaje: " + mensaje,3);
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.dismiss();

						Intent intent = new Intent(SeleccionInventarios.this,
								InventarioMainBoard.class);
						startActivity(intent);
						finish();
					}
				});
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}

	@Nullable
    public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje,
                                            Class<?> clase) {
		log.log("[-- 430 --]" + "titulo: " + titulo + ", \n mensaje: " + mensaje,3);
		return null;
	}

	// Creamos el thread que va a ejecutar el trabajo pesado (en una nueva
	// clase):
	protected class CargarDatosArticulos extends
			AsyncTask<Context, Integer, String> {

		@Nullable
        protected String doInBackground(Context... arg0) {
			try {
System.out.println("::: SeleccionInventario 451 doInBackground");
				popup_carga_subir(5);

				// Cargamos la base de datos:
				BaseDatos bdd = new BaseDatos(ctxt);
				bdd = new BaseDatos(ctxt);

				popup_carga_subir(10);

				// Para cada inventario vemos si tiene articulos ya cargados o
				// no.
				// Los inventarios que figuran sin articulos => hay que
				// rellenarlos.
				ArrayList<Integer> inventarios_por_cargar = new ArrayList<Integer>();
				for (int num_inventario : inventarios_seleccionados) {
					// if
					// (bdd.selectArticulosCodigosConNumeroInventario(num_inventario).size()
					// <= 0) {
					inventarios_por_cargar.add(num_inventario);
					// }
				}

				popup_carga_subir(15);

				// Para cada inventario que no tenga articulos cargados,
				// recuperamos los datos de los articulos con HTTP:
				HttpReader readerHttp = null;
				try {
					readerHttp = new HttpReader(
							Parametros.PREF_URL_CONEXION_SERVIDOR,
							ParametrosInventario.FONCION_CARGAR_ARTICULOS,
							inventarios_por_cargar);
				} catch (ExceptionHttpExchange e) {
					e.printStackTrace();
				}

				popup_carga_subir(20);

				// Descargamos los detalles de todas las rutas seleccionadas:
				// Para cada ruta, guardamos sus detalles en una super matriz de
				// matriz de matriz ( matriz^3 ):
				int countSeguridad = 3;
				LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> hashmapDetallesTodosInventarios = readerHttp
						.readDetallesInventarios();
System.out.println("::: SeleccionInventario 495 ");
				while (countSeguridad > 0
						&& hashmapDetallesTodosInventarios.size() <= 0) {
					hashmapDetallesTodosInventarios = readerHttp
							.readDetallesInventarios();
					System.out.println("::: SeleccionInventario 500 ");
					countSeguridad--;
					popup_carga_subir(20 + 4 - countSeguridad);
				}
	
				popup_carga_subir(30);
				SystemClock.sleep(2000);
				System.out.println("::: SeleccionInventario 507 ");
				// Empezamos la lectura de la MEGA-MATRIZ:
				Set<Integer> setNumerosInventarios = hashmapDetallesTodosInventarios
						.keySet();
				Iterator<Integer> ite = setNumerosInventarios.iterator();

				pasoInventario = (int) Math.floor((95 - 30)
						/ setNumerosInventarios.size());
				carga = 30;

				popup_carga_subir(30);
				System.out.println("::: SeleccionInventario 518 ");
				// Iteracin la enumeracin de las rutas:
				while (ite.hasNext() == true) {
					System.out.println("::: SeleccionInventario 521 ");
					// Recuperamos el numero de la ruta:
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
					System.out.println("::: SeleccionInventario 536 ");
					// Iteracin la enumeracin de los medidores de esta ruta:
					while (ite2.hasNext() == true) {
						HashMap<String, Integer> codigosArticulo = ite2.next();
						listaCodigosArticulos.add(codigosArticulo);

						// Recuperamos los datos del ARTICULO con su par de
						// codigos de identificacion (sector, codigo):
						HashMap<String, String> datosUnArticulo = hashmapDetallesTodosInventarios
								.get(numeroInventario).get(codigosArticulo);
						System.out.println("::: SeleccionInventario 546 Carga ARticulos parece ");
//						String COD_BAR = datosUnArticulo
//								.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
//						if (COD_BAR == "") {
//							COD_BAR = "sin cdigo";
//						}

//boolean condicionBalanza = ParametrosInventario.balanza;
//
//Articulo articulo;						
//						if(condicionBalanza == true){
//							String COD_BAR = datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
//							String COD_BAR_COMP = datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_codigo_barra_completo);
//							if (COD_BAR == "") {
//								COD_BAR = "sin cdigo";
//							}
//							String pventaS = datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_precio_venta);
//							Double pventa = Double.parseDouble(pventaS);
//							String pcostoS = datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_precio_costo);
//							Double pcosto = Double.parseDouble(pcostoS);
//							// A partir de este hashmap recuperamos el objeto
//							// ARTICULO:
//							articulo = new Articulo(
//									Integer.parseInt(datosUnArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_sector)),
//									Integer.parseInt(datosUnArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_codigo)),
//											Integer.parseInt(datosUnArticulo
//													.get(ParametrosInventario.bal_bdd_articulo_balanza)),
//													Integer.parseInt(datosUnArticulo
//															.get(ParametrosInventario.bal_bdd_articulo_decimales)),
//									new ArrayList<String>(Arrays.asList(COD_BAR
//											.split(","))),
//											new ArrayList<String>(Arrays.asList(COD_BAR_COMP
//													.split(","))),		
//									Integer.parseInt(datosUnArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_inventario)),
//									datosUnArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_descripcion),
//									Double.parseDouble(datosUnArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
////											0,
//									Double.parseDouble(datosUnArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
////											0,
////											Integer.parseInt(datosUnArticulo
////													.get(ParametrosInventario.bal_bdd_articulo_depsn)),
//											0,
////									Double.parseDouble(datosUnArticulo
////											.get(ParametrosInventario.bal_bdd_articulo_precio_venta)),
//											pventa,
////									Double.parseDouble(datosUnArticulo
////											.get(ParametrosInventario.bal_bdd_articulo_precio_costo))
//											pcosto);
//							System.out.println("::: SeleccionInventario 576 " + articulo);
//							bdd.insertArticuloEnBdd(articulo);
//				System.out.println("::: SeleccionInventario 577 ");
//							// Aumentamos la barra del popup:
//							carga += pasoArticulo;
//							popup_carga_subir((int) carga);
//						}else{
//					String COD_BAR = datosUnArticulo
//							.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
//					String COD_BAR_COMP = COD_BAR;
//					if (COD_BAR == "") {
//						COD_BAR = "sin cdigo";
//					}
//					String pventaS = datosUnArticulo
//							.get(ParametrosInventario.bal_bdd_articulo_precio_venta);
//					Double pventa = Double.parseDouble(pventaS);
//					String pcostoS = datosUnArticulo
//							.get(ParametrosInventario.bal_bdd_articulo_precio_costo);
//					Double pcosto = Double.parseDouble(pcostoS);
//					// A partir de este hashmap recuperamos el objeto
//					// ARTICULO:
//				articulo = new Articulo(
//							Integer.parseInt(datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_sector)),
//							Integer.parseInt(datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_codigo)),
//									Integer.parseInt(datosUnArticulo
//											.get(ParametrosInventario.bal_bdd_articulo_balanza)),
//											Integer.parseInt(datosUnArticulo
//													.get(ParametrosInventario.bal_bdd_articulo_decimales)),
//							new ArrayList<String>(Arrays.asList(COD_BAR
//									.split(","))),
//									new ArrayList<String>(Arrays.asList(COD_BAR_COMP
//											.split(","))),		
//							Integer.parseInt(datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_inventario)),
//							datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_descripcion),
//							Double.parseDouble(datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
//							Double.parseDouble(datosUnArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
//									0,
//									pventa,
//									pcosto);
//						}
						String COD_BAR = datosUnArticulo
								.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
//						String COD_BAR_COMP = datosUnArticulo
//								.get(ParametrosInventario.bal_bdd_articulo_codigo_barra_completo);
						String COD_BAR_COMP = COD_BAR;
						if (COD_BAR == "") {
							COD_BAR = "sin codigo";
						}
						String pventaS = datosUnArticulo
								.get(ParametrosInventario.bal_bdd_articulo_precio_venta);
						Double pventa = Double.parseDouble(pventaS);
						String pcostoS = datosUnArticulo
								.get(ParametrosInventario.bal_bdd_articulo_precio_costo);
						Double pcosto = Double.parseDouble(pcostoS);
						
						// A partir de este hashmap recuperamos el objeto
						// ARTICULO:

if(datosUnArticulo.get(ParametrosInventario.bal_bdd_articulo_balanza)!=null &&
	datosUnArticulo.get(ParametrosInventario.bal_bdd_articulo_decimales)!=null){

	Articulo articulo = new Articulo(
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_sector)),
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_codigo)),
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_balanza)),
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_decimales)),
//										0,0, //balanza y decimal
			new ArrayList<String>(Arrays.asList(COD_BAR
					.split(","))),
			new ArrayList<String>(Arrays.asList(COD_BAR_COMP
					.split(","))),
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_inventario)),
			datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_descripcion),
			Double.parseDouble(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
			Double.parseDouble(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
			0,
			pventa,
			pcosto);

	System.out.println("::: SeleccionInventario 576 " + articulo);
	bdd.insertArticuloEnBdd(articulo);
	System.out.println("::: SeleccionInventario 1 577 ");

	// Aumentamos la barra del popup:
	carga += pasoArticulo;
	popup_carga_subir((int) carga);


}else{

	Articulo articulo = new Articulo(
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_sector)),
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_codigo)),
			//Integer.parseInt(datosUnArticulo
			//		.get(ParametrosInventario.bal_bdd_articulo_balanza)),
			//Integer.parseInt(datosUnArticulo
			//		.get(ParametrosInventario.bal_bdd_articulo_decimales)),

			0,0, //balanza y decimal
			new ArrayList<String>(Arrays.asList(COD_BAR
					.split(","))),
			new ArrayList<String>(Arrays.asList(COD_BAR_COMP
					.split(","))),
			Integer.parseInt(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_inventario)),
			datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_descripcion),
			Double.parseDouble(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
			Double.parseDouble(datosUnArticulo
					.get(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
			0,
			pventa,
			pcosto);

	System.out.println("::: SeleccionInventario 576 " + articulo);
	bdd.insertArticuloEnBdd(articulo);
	System.out.println("::: SeleccionInventario 2 577 ");

	// Aumentamos la barra del popup:
	carga += pasoArticulo;
	popup_carga_subir((int) carga);

}


					} // end while (ite2.hasNext() == true)

					// Guardamos en memoria la lista de los articulos de este
					// inventario:
					// matrizArticulosCadaInventario.put(numeroInventario,
					// listaCodigosArticulos);
					// carga += pasoArticulo;
					// popup_carga_subir(proximoPaso);

				} // end while (ite.hasNext() == true)

				// Borramos la memoria:
				hashmapDetallesTodosInventarios.clear();

				// Terminar el thead:
				popup_carga_subir(99);

			} catch (Exception e) {
				
				log.log(e.toString(), 4);
				e.printStackTrace();
				popup_carga_end();
				return e.toString();
			} catch (ExceptionBDD e) {
				
				log.log(e.toString(),4);
				e.printStackTrace();
				popup_carga_end();
				return e.toString();
			}

			popup_carga_end();
			return null;
		}

		protected void onPostExecute(@Nullable String result) {
			super.onPostExecute(result);
			if (result == null) {
				showSimpleDialogOK(
						"CARGA DE LOS INVENTARIOS",
						"Los datos de los articulos que partenecen a los inventarios seleccionados se cargaron con exito.")
						.show();
						log.log("[-- 608 --]" + "Los datos de los articulos que partenecen a los inventarios seleccionados se cargaron con exito.",2);

				// Al ULTIMO CARGAMOS LOS INVENTARIOS:
//						int cp = 0;
				for (int numeroInventario : inventarios_seleccionados) {
					System.out.println("::: ACA INSERTA INVENTARIOSSSSSSSSSSS");
					String str = inventariosDetalles
							.get(String.valueOf(numeroInventario));
					String delimiter = "-";
					String[] temp;

                    temp = str.split(delimiter);
					String temp_d;
					String temp_f;
					if (temp.length > 2) {
						temp_f = temp[temp.length - 1];
                        temp_d = "";
						for (int it = 0; it < temp.length - 1; it++) {
                            if (it > 0) {
                                temp_d += "-";
                            }
                            temp_d += String.valueOf(temp[it]);
                        }
					} else {
						temp_d = temp[0];
						temp_f = temp[1];
					}

					try {
						System.out.print("::: SeleccionInventarios 894 " + numeroInventario);
						bdd.insertInventarioEnBdd(new Inventario(
								numeroInventario,
								temp_d
//								inventariosDetalles
//										.get(String.valueOf(numeroInventario))
//										.get(ParametrosInventario.bal_bdd_inventario_descripcion)
										,
								temp_f
//								inventariosDetalles
//										.get(String.valueOf(numeroInventario))
//										.get(ParametrosInventario.bal_bdd_inventario_fechaInicio)
										,
								inventariosDetalles
										.get(String.valueOf(numeroInventario))
//										.get(ParametrosInventario.bal_bdd_inventario_fechaFin)
										,
								1,// Integer.parseInt(inventariosDetalles.get(String.valueOf(numeroInventario)).get(ParametrosInventario.bal_bdd_inventario_estado))
								-1// inventariosDetalles.get(String.valueOf(numeroInventario)).get(ParametrosInventario.bal_bdd_inventario_lugar)
						));
					} catch (ExceptionBDD e) {
						
						log.log(e.toString(),4);
						e.printStackTrace();						
					}
				}
			} else {
				showSimpleDialogOK("CARGA DE LOS INVENTARIOS",
						"EL FORMATO DEL XML NO ES VALIDO: " + result).show();
			}
		}

	} // fin de "protected class CargarDatosArticulos"

public  void popup_carga_start() {
		popupCarga = new ProgressDialog(ctxt);
		popupCarga.setCancelable(false);
		popupCarga.setMessage("Importando los datos de inventarios...");
		popupCarga.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		popupCarga.setProgress(0);
		popupCarga.setMax(100);
		popupCarga.show();
	}

	private void popup_carga_subir(int hastaXPorciento) {
		popupCarga.setProgress(hastaXPorciento);
	}

	private void popup_carga_end() {
		popupCarga.dismiss();
	}

} // end class