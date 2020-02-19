package com.focasoftware.deboinventario;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Activity que muestra los artculos a inventariar en un inventario dado y
 * permite administrarlos. Trabaja con inventarios normales. Permite ver los
 * detalles de algn artculo y modificar sus cantidades.
 * 
 * @author GuillermoR
 * 
 */
@SuppressLint("ResourceAsColor")
public class PaginaInventario extends Activity implements DialogPersoSimple {

	private final static int NUMERO_LINEAS_EN_TABLA = 8;
	/**
	 * Longitud de numero debajo (o igual) del cual se interpreta el numero como
	 * un valor, y por arriba del cual se considerar como un codigo de barra
	 */
	// private final static int NUMERO_CARACTERES_VALOR_CODIGO = 3;
	private static final int SCAN_BARCODE = 0;
	/**
	 * Datos de gestion de TOUCH
	 */
	private int event_triggered = -1;
	private float y_delta = 0;
	private float y_inicial = 0;
	private float UMBRAL_DESPLAZAMIENTO_MINIMO = 10;
	/**
	 * Variable para almamcenar los datos del contexto de la activity
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Instancia de administrador de la BD
	 */
	private BaseDatos bdd;
	/**
	 * Intent que llamo a esta activity, generalmente es el MainBoard
	 */
	private Intent intentPadre;
	/**
	 * Numero del inventario que se esta administrando
	 */
	private int inventario_numero_en_curso;
	/**
	 * Lista de todos los articulos que tiene el inventario
	 */
	private ArrayList<ArticuloVisible> listaArticulosCompleta;
	/**
	 * ArrayList que contiene las tablas de los codigos de barras. El orden se
	 * preserva como a la insersin. Esta tabla se tiene que ordenar al igual
	 * que la listaArticulosCompleta.
	 */
	private ArrayList<ArrayList<String>> listaCodigosDeBarrasOrdenados;
	/**
	 * Variable para almacenar los sectores por los que se filtra
	 */
	@NonNull
    private ArrayList<Integer> listaSectoresFiltrados = new ArrayList<Integer>();
	@NonNull
    private ArrayList<Integer> copia_listaSectoresFiltrados = new ArrayList<Integer>();
	/**
	 * Precio por el cual filtrar
	 */
	private double precioFiltro = (double) 0;
	private double copia_precioFiltro = 0;
	private Dialog dialogo;
	/**
	 * Variable de filtro : +1 => articulos ya inventariados // 0 => no ver nada
	 * // -1 => articulos no inventariados
	 */
	private int inventarioFiltro = 0;
	private int copia_inventarioFiltro = 0;
	private int modo_mas_1 = 0;
	/**
	 * Boton para salir vuelve a la main board y cierra la actual
	 */
	private Button boton_salir;
	/**
	 * Imagen de boton de busqueda (Lupa)
	 */
	private ImageView boton_busqueda;
	/**
	 * Boton de la lectora para activar el modo mas 1
	 */
	private ImageView boton_lectora;
	/**
	 * TextView del titulo
	 */
	private TextView textview_titulo;
	/**
	 * TextViews de los encabezados de la tabla
	 */
	private TextView encabezado_codigo, encabezado_descripcion,
			encabezado_precio_venta, encabezado_cantidad;
	/**
	 * Imagenes de los distintos filtros aplicables
	 */
	private ImageView filtro_codigo, filtro_precio, filtro_cantidad;
	/**
	 * Imagenes para flechas del ascensor lateral
	 */
	private ImageView flecha_arriba, flecha_abajo;
	/**
	 * Layout del ascensor
	 */
	private LinearLayout asensor_layout;
	/**
	 * Table layout para mostrar los articulos
	 */
	private TableLayout tabla_articulos;
	/**
	 * LoadingBar de espera
	 */
	private ProgressBar loadingBar;
	/**
	 * EditTexts varios
	 */
	@Nullable
    private EditText editTT = null;
	@Nullable
    private EditText edittextBusqueda = null;
	/**
	 * Una alerta
	 */
	private AlertDialog alert;
	/**
	 * Dialogo para permitir que se sume o se reste a la cantidad actual
	 */
	private DialogPersoComplexCantidadMasMenos dialogoMasMenos;
	/**
	 * Dialogo para permitir la modificacin de una cantidad
	 */
	@Nullable
    private DialogPersoComplexCantidadModificacion dialogoModificacion;
	/**
	 * Dialogo para realizar la busqueda
	 */
	private DialogPersoComplexBusqueda dialogoBusqueda;
	/**
	 * Variable accesoria en desuso aparentemente
	 */
	private boolean fueCanceladoDialogoBusqueda = false;
	/**
	 * Dialogo donde se muestran los resultados
	 */
	@Nullable
    private DialogPersoComplexResultados dialogoResultados;
	/**
	 * Variable accesoria
	 */
	private boolean fueCanceladoDialogoResultados = false;
	/**
	 * Variable auxiliar para marcar la respuesta que se selecciono
	 */
	private int respuestaSeleccionada = -99;
	/**
	 * Dialog para pedir los datos del articulo nuevo
	 */
	private DialogPersoComplexEditTextOkCancel dialogoNombreArticuloNuevo;

	private int columna_ordonante = -1;
	@NonNull
    private String bufferLectoraCB = "";
	@Nullable
    private HashMap<Integer, Integer> articulo_resultado_busqueda = null;

	/**
	 * Indice al cual apunta la primera linea de la tabla visual de los 8
	 * articulos. Puede ser diferente de la linea del articulo enfocado.
	 */
	private int indice_primera_linea = 0;

	/**
	 * Indice de la tablaArticulosCompleta donde se encuentra el articulo
	 * enfocado.
	 */
	private int indice_on_focus = -1;
	/**
	 * Variable auxiliar para saber la cantidad de articulos visibles
	 */
	private int numero_articulos_visibles = 0;
	/**
	 * Variables auxiliares para saber la gestion del uso del dedo en pantalla
	 */
	private boolean dedoEnContacto = false;
	private float dedoInicialX, dedoFinalX, dedoInicialY, dedoFinalY;

	private int altura_linea_asensor = 300;
	private int numero_lineas_asensor_text = 0;
	// private float umbralUnitario = (float)10.0;
	/**
	 * Valor de cantidad antes de modificar incial
	 */
	private float valor_antes_modificar = -1;
	/**
	 * Columnas de datos
	 */
	private int columna_codigo = 0;
	private int columna_descripcion = 1;
	private int columna_precio = 2;
	private int columna_cantidad = 3;
	private int columna_secreta = 5;

	
	private int cont;
	
	private int cantVent;
	private int cantDep;
	
	private ProgressDialog aguarde;
	
	private ImageView Ic_Lectora;
	
	@NonNull
    public String[] cortarCadenaPorPuntos(@NonNull String cadena) {
		  return cadena.split("\\.");
		}
	/**
	 * Considera la creacion de inventarios
	 * <p>
	 * 1 Seteamos el inventario en curso al numero elegido
	 * <p>
	 * 2 Recuperamos la lista de todos los datos que mostrar en pantalla:
	 * <p>
	 * 3 Si no hay nada que mostrar avisamos
	 * <p>
	 * 4 Iniciamos la UI
	 * <p>
	 * 5 Cargar los HANDLERS:
	 * <p>
	 * 6 Aviso por si no sabe que hacer para que lea con CB
	 * <p>
	 * 7 Carga los articulos en la tabla
	 */
    @NonNull
    GestorLogEventos log = new GestorLogEventos();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_paginainventario);

		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 278 --]" + "Inicia Pagina Inventarios", 2);
		log.log("[-- 280 --]"
				+ "Lea un articulo con la lectora de codigo de barra para modificar"
				+ " su cantidad en el inventario", 3);
		Toast.makeText(
				ctxt,
				"Lea un articulo con la lectora de codigo de barra para modificar"
						+ " su cantidad en el inventario", Toast.LENGTH_LONG)
				.show();
		// BUNDLES:
		intentPadre = getIntent();
		Bundle bundle = getIntent().getExtras();
		// Hay que ver si vamos a trabajar con inventarios dinamicos o normales
		// Recuperamos todos los articulos de este inventario:
		if (bundle != null) {
			inventario_numero_en_curso = bundle
					.getInt(ParametrosInventario.extra_numeroInventario);
		} else {
			inventario_numero_en_curso = 1;
		}
		// BASE DE DATOS:
		bdd = new BaseDatos(ctxt);

		// Recuperamos la lista de todos los datos que mostrar en pantalla:
		try {
			listaArticulosCompleta = bdd
					.selectArticulosConNumeroInventario(inventario_numero_en_curso);
		} catch (ExceptionBDD e) {

			log.log("[-- 313 --]" + e.toString(), 4);
			e.printStackTrace();
			showSimpleDialogOK("Error", e.toString()).show();
		}

		// Si no hay nada que mostrar y que no estamos en el caso del inventario
		// sorpresa:
		if (listaArticulosCompleta.size() <= 0
				&& inventario_numero_en_curso > 0) {
			showSimpleDialogSiNo(
					"ERROR",
					"El inventario no contiene ningun articulo\n\n Desea continuar?",
					InventarioMainBoard.class).show();
		}
		Ic_Lectora = (ImageView) findViewById(R.id.Ic_Lectora);

		textview_titulo = (TextView) findViewById(R.id.PI_tit_pagina);
		encabezado_codigo = (TextView) findViewById(R.id.PI_encabezado_codigo);
		encabezado_descripcion = (TextView) findViewById(R.id.PI_encabezado_descripcion);
		encabezado_precio_venta = (TextView) findViewById(R.id.PI_encabezado_precio);
		encabezado_cantidad = (TextView) findViewById(R.id.PI_encabezado_cantidad);

		filtro_cantidad = (ImageView) findViewById(R.id.PI_encab_cantidad);
		filtro_codigo = (ImageView) findViewById(R.id.PI_encab_codigo);
		filtro_precio = (ImageView) findViewById(R.id.PI_encab_precio);

		flecha_abajo = (ImageView) findViewById(R.id.PI_boton_down);
		flecha_arriba = (ImageView) findViewById(R.id.PI_boton_up);

		boton_salir = (Button) findViewById(R.id.PI_boton_salir);
		boton_busqueda = (ImageView) findViewById(R.id.PI_boton_busqueda);
		boton_lectora = (ImageView) findViewById(R.id.PI_boton_lectora);

		asensor_layout = (LinearLayout) findViewById(R.id.PI_layout_asensor);
		tabla_articulos = (TableLayout) findViewById(R.id.PI_tabla_articulos);
		
		
		loadingBar = (ProgressBar) findViewById(R.id.PI_loadingbar);

		// Actualizamos estos elementos:
		if (inventario_numero_en_curso < 0) {
			textview_titulo.setText("Inventario Dinamico n "
					+ String.valueOf(Math.abs(inventario_numero_en_curso)));
		} else {
			textview_titulo.setText("Inventario n "
					+ String.valueOf(inventario_numero_en_curso));
		}
		loadingBar.setVisibility(View.GONE);

		// Cargar los HANDLERS:
		cargar_handlers();
		aguarde = ProgressDialog.show(ctxt, "Aguarde",
				"Aguarde que se carguen los articulos");

		log.log("[-- 363 --]" + "Aguarde que se carguen los articulos", 3);
		// Aviso por si no sabe que hacer
		log.log("[-- 371 --]"
				+ "Lea un articulo con la lectora de codigo de barra para modificar"
				+ " su cantidad en el inventario", 3);
		Toast.makeText(
				ctxt,
				"Lea un articulo con la lectora de codigo de barra para modificar"
						+ " su cantidad en el inventario", Toast.LENGTH_LONG)
				.show();
		// Cargamos la pagina de recepcin:
		cargarArticulosEnTabla();
		aguarde.dismiss();
		if(!ParametrosInventario.CamHabScanner){
			Ic_Lectora.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Funcion que carga los articulos del inventario actual en la tabla
	 * principal
	 * <p>
	 * 1 Construimos la tabla - Linea por linea
	 * <p>
	 * &nbsp; &nbsp;1.1 Recuperamos la linea y la cargamos con el layout
	 * inflater
	 * <p>
	 * &nbsp; &nbsp;1.2 Recuperamos los detalles del articulo que va a figurar
	 * en la linea
	 * <p>
	 * &nbsp; &nbsp;1.3 Configuramos los elementos (4 casillas) de la linea
	 * <p>
	 * &nbsp; &nbsp;1.4 Calculo de la cantidad de elementos visibles:
	 * <p>
	 * &nbsp; &nbsp;1.5 Configuramos los valores de casillas
	 * <p>
	 * &nbsp; &nbsp;1.6 Pegamos LISTENERS a la linea
	 * <p>
	 * &nbsp; &nbsp;1.7 Modificamos la lista de codigos de barras, agregando los
	 * CB del articulo en la linea
	 * <p>
	 * 2 Seguimos las otras listas fuera de la tabla visible de 8 lineas
	 */
	private void cargarArticulosEnTabla() {
		int con = 0;
		log.log("[-- 498 --]" + "Se cargan los articulos", 2);
		try {
			indice_primera_linea = 0;
			int paridad = 0; // numeroLinea % 2

			listaCodigosDeBarrasOrdenados = new ArrayList<ArrayList<String>>();
			numero_articulos_visibles = 0;

			// Construimos la tabla - Linea por linea:
			for (int numLinea = 0; numLinea < NUMERO_LINEAS_EN_TABLA; numLinea++) {
				con = numLinea;
	
				// Recuperamos la linea y la cargamos con el layout inflater:
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				// ViewFlipper vf = (ViewFlipper) findViewById(idflipper);
				TableRow linea = (TableRow) inflater.inflate(
						R.layout.z_lineaarticulo_inventario, null);
				tabla_articulos.addView(linea);
				System.out.println("::: PaginaInv linea ="+ linea);	
				// Recuperamos los detalles del articulo que va a figurar en
				// esta linea:
				// Aca, si hay menos de 8 articulos, paramos, y no mostramos la
				// barra de asensor:
				ArticuloVisible a = null;
				try {
					a = listaArticulosCompleta.get(numLinea);
				} catch (Exception e) {

					log.log("[-- 526 --]" + e.toString(), 4);
					a = new ArticuloVisible(false);
					linea.setVisibility(View.INVISIBLE);
				}
				// Configuramos los elementos (4 casillas) de la linea:
				TextView casilla_codigo = (TextView) linea
						.findViewById(R.id.ZLIN_codigo);
				TextView casilla_descripcion = (TextView) linea
						.findViewById(R.id.ZLIN_descripcion);
				TextView casilla_precio = (TextView) linea
						.findViewById(R.id.ZLIN_precio);
				EditText casilla_cantidad = (EditText) linea
						.findViewById(R.id.ZLIN_cantidad);
				TextView casilla_exisventa = (TextView) linea
						.findViewById(R.id.ZLIN_existente);
				TextView casilla_secreta = (TextView) linea
						.findViewById(R.id.ZLIN_secreto);
				// Calculo de la cantidad de elementos visibles:
				if (a.esVisible() == true) {
					numero_articulos_visibles++;
				}
				// Configuramos los valores de casillas:
				/* Primera casilla: CODIGO del ARTICULO */
				if (a.getSector() >= 0) {
					casilla_codigo.setText(String.valueOf(a.getSector()) + "-"
							+ String.valueOf(a.getCodigo()));
				} else {
					casilla_codigo.setText("Nvo. "
							+ String.valueOf(Math.abs(a.getSector())));
				}
				/* Segunda casilla: DESCRIPCION del ARTICULO */
				casilla_descripcion.setText(a.getDescripcion());
				/* Tercera casilla: PRECIO DE VENTA del ARTICULO */
				casilla_precio
						.setText("$ "
								+ new DecimalFormat("0.00").format(a
										.getPrecio_venta()));
				/* Cuarta casilla: CANTIDAD del ARTICULO */
				if (a.getCantidad() >= 0) {
					casilla_cantidad.setText(String.valueOf(a.getCantidad()));
				} else {
					casilla_cantidad.setText("No Tomado");
				}
				/* Quinta casilla: Existencia del ARTICULO */
				/*Damian*/
				double valor1 =a.getExis_venta();
				double valor2= a.getExis_deposito();
				double resultado= valor1 + valor2;
			//	String resultadoS = String.valueOf(resultado);
			//	String[] arreglo=resultadoS.split("\\.");
				casilla_exisventa.setText(String.valueOf(resultado));
				SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(ctxt);
				boolean mostrarExistencia = setting.getBoolean(ParametrosInventario.tablet_mostrar_existencia, ParametrosInventario.mostrar_existencia);
				if(!mostrarExistencia){
					casilla_exisventa.setText(ParametrosInventario.no_disponible);
				}
				
				/* Sexta casilla: INDICE ARTICULO */
				System.out.println("::: numeLinea = " + numLinea);
				casilla_secreta.setText(String.valueOf(numLinea));
				// Colorear la linea:
				colorearLinea(linea, paridad);
				System.out.println("::: Veamos los num linea "+ String.valueOf(numLinea));
				// Pegamos LISTENERS a la linea.
				casilla_cantidad.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						System.out.println("::: PASO NO Dinamico 1 ingresa al onclick");
						bufferLectoraCB = "";

						if (indice_on_focus >= 0) {
							deseleccionarLineaParticular(indice_on_focus);
						} else if (indice_on_focus < 0) {
							EditText editT = (EditText) v;
							TableRow tableR = (TableRow) editT.getParent();
							TextView textV = (TextView) tableR.getChildAt(5);
			//				System.out.println("::: ver valor del TextView ="+textV);
							int indice_articulo = Integer.parseInt(String
									.valueOf(textV.getText()));
			//				System.out.println("::: 511ver valor del indice_articulo ="+indice_articulo);
							seleccionarMostrarIndiceArticulo(indice_articulo);
						}
					}
				});

				linea.setOnTouchListener(new View.OnTouchListener() {

					public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							event_triggered = GestionarioClicks.TOUCH_TRIGGERED;
							y_delta = 0;
							y_inicial = event.getRawY();
							// Log.v("on move","Y INICIAL = " + y_inicial);
							return false;
						} else if (event.getAction() == MotionEvent.ACTION_MOVE
								&& indice_on_focus < 0
								&& (event_triggered == GestionarioClicks.TOUCH_TRIGGERED || event_triggered == GestionarioClicks.SLIP_TRIGGERED)) {

							// Si el movimiento es muy pequeo, tenemos que
							// considerar que no hay movimiento.
							// Con el mouse no se nota, pero con el dedo puede
							// haber una pequea variacin de
							// la posicin de +- 2 pixeles aunque uno deje su
							// dedo bien fijo en la pantalla:
							if (Math.abs(event.getRawY() - y_inicial) < 5) {
								return true;
							}

							event_triggered = GestionarioClicks.SLIP_TRIGGERED;
							float y_actual = event.getRawY();
							y_delta += -1 * (y_actual - y_inicial);
							y_inicial = y_actual;
							// Log.v("on move","getY = " + y_actual);
							// Log.v("on move","delta Y = " + y_delta);

							// Obtener el signo de y_delta y el movimiento de
							// lineas:
							int y_move;
							if (y_delta >= 0) {
								y_move = (int) Math.floor((float) y_delta
										/ (float) UMBRAL_DESPLAZAMIENTO_MINIMO);
								y_delta = Math.abs(y_delta)
										% UMBRAL_DESPLAZAMIENTO_MINIMO;
							} else {
								y_move = (int) Math.ceil((float) y_delta
										/ (float) UMBRAL_DESPLAZAMIENTO_MINIMO);
								y_delta = -1
										* (Math.abs(y_delta) % UMBRAL_DESPLAZAMIENTO_MINIMO);
							}
							// Log.v("on move","mouvement = " + y_move);
							moverTablaArticulos(y_move);

							SystemClock.sleep(20);
							return true;
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							if (event_triggered == GestionarioClicks.TOUCH_TRIGGERED) {
								// Si el evento de soltar el boton ocurre sin
								// desplazamiento, es un click:
								event_triggered = GestionarioClicks.CLICK_TRIGGERED;
								bufferLectoraCB = "";
								if (indice_on_focus >= 0) {
									deseleccionarLineaParticular(indice_on_focus);
								} else if (indice_on_focus < 0) {
									seleccionarMostrarIndiceArticulo(Integer.parseInt(String.valueOf(((TextView) ((TableRow) v)
											.getChildAt(columna_secreta))
											.getText())));
								}
							} else {
								event_triggered = GestionarioClicks.CLICK_TRIGGERED;
							}
							event_triggered = -1;
							y_delta = 0;
							return true;
						} else {
							return false;
						}
					}
				});

				// Click <=> seleccionar la linea:
				// Desactivado, ahora integrado a OnTouchListener
				/*
				 * linea.setOnClickListener(new View.OnClickListener() {
				 * 
				 * public void onClick(View v) { Toast.makeText(ctxt, "Clic",
				 * Toast.LENGTH_SHORT).show();
				 * 
				 * bufferLectoraCB = "";
				 * 
				 * if (indice_on_focus >= 0) {
				 * deseleccionarLineaParticular(indice_on_focus); } else if
				 * (indice_on_focus < 0) { seleccionarMostrarIndiceArticulo(
				 * Integer.parseInt( String.valueOf( ((TextView)
				 * ((TableRow)v).getChildAt(columna_secreta) ).getText() ) ) ,
				 * true, false); } } });
				 */

				// Long Click <=> pide para suprimir el articulo:
				linea.setOnLongClickListener(new View.OnLongClickListener() {

					public boolean onLongClick(@NonNull View linea) {
						// Si otro evento se dispar antes del long click, no se
						// procesa el LongClick:
						if (event_triggered != GestionarioClicks.TOUCH_TRIGGERED) {
							return true;
						} else {
							event_triggered = GestionarioClicks.LONGCLICK_TRIGGERED;
						}

						// Sino:
						bufferLectoraCB = "";
		//				
						
						int indice_articulo_apuntado = Integer.parseInt(String.valueOf(((TextView) ((TableRow) linea)
								.getChildAt(columna_secreta)).getText()));
						System.out.println("::: PaginaInventario 628 indice_articulo_apuntado" + indice_articulo_apuntado);
				
						ArticuloVisible a = listaArticulosCompleta
								.get(indice_articulo_apuntado);
						System.out.println("::: PaginaInventario 632 a " +a);
						Intent intentDetallesArticulo = new Intent(
								PaginaInventario.this,
								DetallesArticulo.class)
								.putExtra(ParametrosInventario.extra_sector,
										a.getSector())
								.putExtra(ParametrosInventario.extra_codigo,
										a.getCodigo())
								.putExtra(
										ParametrosInventario.extra_inventario,
										inventario_numero_en_curso);
						//System.out.println("::: PaginaInventario 643 intent " + intentDetallesArticulo);
						 startActivity(intentDetallesArticulo);
						// Llamamos al detalle, puede devolver result de
						// articulo eliminado
						startActivityForResult(intentDetallesArticulo,
								ParametrosInventario.REQUEST_DETALLES_ART);
						return true;
					}
				});
				// 1.7 Modificamos la lista de codigos de barras, agregando los
				// CB del articulo en la linea
				listaCodigosDeBarrasOrdenados.add(a.getCodigos_barras());

				paridad = (paridad + 1) % 2;
			} // end for

			// Seguimos las otras listas fuera de la tabla visible de 8 lineas:
			for (int i2 = NUMERO_LINEAS_EN_TABLA; i2 < listaArticulosCompleta
					.size(); i2++) {
				con = i2;
				ArticuloVisible a = listaArticulosCompleta.get(i2);
				// Calculo de la cantidad de elementos visibles:
				if (a.esVisible() == true) {
					numero_articulos_visibles++;
				}
				listaCodigosDeBarrasOrdenados.add(a.getCodigos_barras());

			}

		} catch (Exception e) {

			log.log("[-- 712 --]" + e.toString(), 4);
			e.printStackTrace();
			loadingBar.setVisibility(View.GONE);
			showSimpleDialogOK(
					"ERROR de carga",
					e.toString()
							+ "\n\nUn error occurio en la carga del presente inventario, articulo "
							+ String.valueOf(con)
							+ ".\n\n Por favor, quite y reintente carga los datos.")
					.show();
			// aguarde.dismiss();
		}
	}

	/**
	 * Funcin para refrezcar la tabla central
	 * <p>
	 * 1 Actualizamos la lista de los codigos de barras:
	 * <p>
	 * 2 Modificamos la tabla visual de 8 lineas (recuperamos los proximos
	 * indices desde el actual)
	 * <p>
	 * 3 Para cada indice proximo
	 * <p>
	 * &nbsp; &nbsp;3.1 Recuperamos el id de cada TableRow de cada linea, y la
	 * linea asociada:
	 * <p>
	 * &nbsp; &nbsp;3.2 Modificamos los datos
	 * <p>
	 * &nbsp; &nbsp;3.3 Recuperamos los detalles del articulo que va a figurar
	 * en esta linea
	 * <p>
	 * &nbsp; &nbsp;3.4 Configuramos los valores de casillas
	 * <p>
	 * 4 Si no tenemos las 8 lineas necesarias, escondemos las ultimas:
	 */
	private void refreshTablaCentral() {
		// Al momento del refresh, hace falta que el foco se desactive CUANDO se
		// origin en el
		// ordenamiento de la lista de los articulos o por un filtro.
		//
		// En ambos casos seguimos enfocando los mismos indices aunque el
		// contenido cambie!
		//
		// CUANDO se trata de un toma de foco, hay que traer el objeto
		// seleccionado en primero fila.

		try {
			// Parametros:
			int paridad;

			// Cargamos la barra de carga:
			loadingBar.setVisibility(View.VISIBLE);
			loadingBar.bringToFront();

			// Primero, actualizamos la lista de los codigos de barras:
			listaCodigosDeBarrasOrdenados = new ArrayList<ArrayList<String>>();
			numero_articulos_visibles = 0;

			for (int i = 0; i < listaArticulosCompleta.size(); i++) {
				ArticuloVisible a = listaArticulosCompleta.get(i);
				listaCodigosDeBarrasOrdenados.add(a.getCodigos_barras());
				if (a.esVisible() == true) {
					numero_articulos_visibles++;
				}
			}

			// Modificamos la tabla visual de 8 lineas:
			ArrayList<Integer> listaProximosIndex = null;
			// if (listaArticulosCompleta.size() > 8) {
			if (indice_on_focus < 0) {
				listaProximosIndex = getProximosIndicesArticulosVisibles(
						indice_primera_linea, false);
			} else {
				listaProximosIndex = getProximosIndicesArticulosVisibles(
						indice_on_focus, true);
			}

			paridad = (listaProximosIndex.get(0)) % 2;

			for (int index : listaProximosIndex) {
				// Recuperamos el id de cada TableRow de cada linea, y la linea
				// asociada:
				TableRow linea = (TableRow) tabla_articulos
						.getChildAt(listaProximosIndex.indexOf((int) index));
				System.out.println("::: PaginaInventario 760 tablerow linea = "+linea);
				linea.setVisibility(View.VISIBLE);

				// Modificamos los datos:
				TextView casilla_codigo = (TextView) linea.getChildAt(0);
				TextView casilla_descripcion = (TextView) linea.getChildAt(1);
				TextView casilla_precio = (TextView) linea.getChildAt(2);
				EditText casilla_cantidad = (EditText) linea.getChildAt(3);
				TextView casilla_exisventa = (TextView) linea.getChildAt(4);
				TextView casilla_secreta = (TextView) linea.getChildAt(5);

				// Recuperamos los detalles del articulo que va a figurar en
				// esta linea:
				ArticuloVisible a = listaArticulosCompleta.get(index);

				// Configuramos los valores de casillas:
				/* Primera casilla: CODIGO del ARTICULO */
				if (a.getSector() >= 0) {
					casilla_codigo.setText(String.valueOf(a.getSector()) + "-"
							+ String.valueOf(a.getCodigo()));
				} else {
					casilla_codigo.setText("Nvo. "
							+ String.valueOf(Math.abs(a.getSector())));
				}

				/* Segunda casilla: DESCRIPCION del ARTICULO */
				casilla_descripcion.setText(a.getDescripcion());

				/* Tercera casilla: PRECIO DE VENTA del ARTICULO */
				casilla_precio
						.setText("$ "
								+ new DecimalFormat("0.00").format(a
										.getPrecio_venta()));
					
				/* Quinta casilla: CANTIDAD del ARTICULO */
				if (a.getCantidad() >= 0) {
					casilla_cantidad.setText(String.valueOf(a.getCantidad()));
				} else {
					casilla_cantidad.setText("No Tomado");
				}
				
				
				/* Cuarta casilla: Existencia del ARTICULO */
					double valor1 =a.getExis_venta();
					double valor2= a.getExis_deposito();
					double resultado= valor1 + valor2;
			//		String resultadoS = String.valueOf(resultado);
					// String[] arreglo=resultadoS.split("\\.");
			//	 System.out.println("::: arreglo = " + arreglo[0]);
					casilla_exisventa.setText(String.valueOf(resultado));

                SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(ctxt);
                boolean mostrarExistencia = setting.getBoolean(ParametrosInventario.tablet_mostrar_existencia, ParametrosInventario.mostrar_existencia);
                if(!mostrarExistencia){
                    casilla_exisventa.setText(ParametrosInventario.no_disponible);
                }

				/* Sexta casilla: INDICE ARTICULO */
						System.out.println("::: index = " + index);
				casilla_secreta.setText(String.valueOf(index));
				casilla_secreta.setVisibility(View.GONE);

				// Colorear la linea:
				colorearLinea(linea, paridad);

				paridad = (paridad + 1) % 2;
			} // end for

			// Si no tenemos las 8 lineas necesarias, escondemos las ultimas:
			for (int j = listaProximosIndex.size(); j < NUMERO_LINEAS_EN_TABLA; j++) {
				// Recuperamos el id de cada TableRow de cada linea, y la linea
				// asociada:
				try {
					TableRow linea = (TableRow) tabla_articulos
							.getChildAt((int) j);
					linea.setVisibility(View.GONE);
				} catch (Exception e) {

					log.log("[-- 900 --]" + e.toString(), 4);
				}
			}

			// Parametros globales:
			loadingBar.setVisibility(View.GONE);

			refreshAsensor();

		} catch (Exception e) {

			log.log("[-- 911 --]" + e.toString(), 4);
			e.printStackTrace();
			loadingBar.setVisibility(View.GONE);
			// showSimpleDialogOK("ERROR de carga", e.toString() +
			// "\n\nUn error occuri en la carga del presente inventario, articulo "+
			// String.valueOf(con)
			// +".\n\n Por favor, quite y reintente carga los datos.").show();
		}
	}

	/**
	 * Refrezca el ascensor del costado
	 */
	private void refreshAsensor() {
		numero_lineas_asensor_text = (int) ((float) getPosicionRelativa(
				indice_primera_linea, false)
				* (float) (altura_linea_asensor - 30) / (float) (numero_articulos_visibles - 1));

		if (numero_lineas_asensor_text < 0) {
			numero_lineas_asensor_text = 0;
		} else if (numero_lineas_asensor_text > altura_linea_asensor) {
			numero_lineas_asensor_text = altura_linea_asensor;
		}

		TextView txv = (TextView) (asensor_layout.getChildAt(0));
		txv.setLines(numero_lineas_asensor_text);
	}

	/**
	 * Refrezca el ascensor del costado
	 * 
	 * @param delta
	 */
	private void refreshAsensor(float delta) {
		int indice_anterior = (int) ((float) (numero_lineas_asensor_text * numero_articulos_visibles) / (float) (altura_linea_asensor));

		numero_lineas_asensor_text = (int) delta;

		int indice_nuevo = (int) ((float) (numero_lineas_asensor_text * numero_articulos_visibles) / (float) (altura_linea_asensor));

		moverTablaArticulos(indice_nuevo - indice_anterior);

		// if (numero_lineas_asensor_text < 0) {
		// numero_lineas_asensor_text = 0;
		// } else if (numero_lineas_asensor_text > altura_linea_asensor){
		// numero_lineas_asensor_text = altura_linea_asensor;
		// }

		// int aaa = (int)( (float)(numero_lineas_asensor_text *
		// (altura_linea_asensor-30)) / (float)(altura_linea_asensor) );
		// TextView txv = (TextView) (asensor_layout.getChildAt(0));
		// txv.setLines(aaa);

	}

	/**
	 * Pinta la linea segun la paridad que se le pase
	 * 
	 * @param linea
	 * @param paridad
	 */
	private void colorearLinea(@NonNull TableRow linea, int paridad) {
		// Cortamos la linea en sus 4 casillas:
		TextView casilla_codigo = (TextView) linea.getChildAt(0);
		TextView casilla_descripcion = (TextView) linea.getChildAt(1);
		TextView casilla_precio = (TextView) linea.getChildAt(2);
		EditText casilla_cantidad = (EditText) linea.getChildAt(3);
		TextView casilla_exisventa = (TextView) linea.getChildAt(4);

		// 1ra CASILLA - CODIGO:
		if (paridad == 1) {
			casilla_codigo.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro_mas));
		} else {
			casilla_codigo.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro));
		}

		// 2da CASILLA - DESCRIPCIN:
		if (paridad == 1) {
			casilla_descripcion.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro_mas));
		} else {
			casilla_descripcion.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro));
		}

		// 3ra CASILLA - PRECIO DE VENTA:
		if (paridad == 1) {
			casilla_precio.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro_mas));
		} else {
			casilla_precio.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro));
		}

		// 4ta CASILLA - ESTADO INVENTARIO:
		if (paridad == 1) {
			casilla_cantidad.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro_mas));
		} else {
			casilla_cantidad.setBackgroundColor(getResources().getColor(
					R.color.verde_oscuro));
		}
		// 5ta CASILLA - EXISTENCIA INVENTARIO:
				if (paridad == 1) {
					casilla_exisventa.setBackgroundColor(getResources().getColor(
							R.color.verde_oscuro_mas));
				} else {
					casilla_exisventa.setBackgroundColor(getResources().getColor(
							R.color.verde_oscuro));
				}
	}

	/**
	 * Busca el indice de el articulo en la tabla cuyo Codigo de barra es cb
	 * 
	 * @param cb
	 */
	private void getIndiceArticuloConCB(final String cb) {
		// Toast.makeText(ctxt, cb, Toast.LENGTH_SHORT).show();
		loadingBar.setVisibility(View.VISIBLE);
		loadingBar.bringToFront();

		int indice_articulo_encontrado = -1;

		// Buscamos el codigo de barras:
		for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
			if (tablaCodigosUnArticulo.contains((String) cb)) {
				// Obtenemos el indice de la linea seleccionada:
				indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
						.indexOf(tablaCodigosUnArticulo);
				break;
			} // end if
		} // end for

		// Tenemos el indice de la posicion del articulo, y luego su visibilidad
		// :
		if (indice_articulo_encontrado < 0) {
			// No encontramos nada, el codigo de barra no referencia ningun
			// articulo del inventario:
			showSimpleDialogTimer(
					"Articulo no encontrado: " + cb,
					"El codigo de barra no referencia ningun articulo en el presente inventario!\n\n("
							+ cb + ")", 4000).show();
			loadingBar.setVisibility(View.GONE);
			return;
		}

		// A ver si el articulo es visible:
		if (listaArticulosCompleta.get(indice_articulo_encontrado).esVisible() == false) {
			showSimpleDialogOK(
					"Articulo oculto",
					"El codigo de barras leido hace referencia a un articulo bloqueado por un filtro!")
					.show();
			loadingBar.setVisibility(View.GONE);
			return;
		} else { // Si visible...
			seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
			loadingBar.setVisibility(View.GONE);
		}
	} // end funcion

	/**
	 * Funcion que se ejecuta para seleccionar el articulo de la tabla cuyo
	 * indice es indice_articulo y poder modificar el valor de la cantidad
	 * 
	 * @param indice_articulo
	 *            <p>
	 *            1 Seteamos el indice_on_focus al indice pasado
	 *            <p>
	 *            2 Refresh de las tablas
	 *            <p>
	 *            3 Buscamos la linea que modificar
	 *            <p>
	 *            &nbsp; &nbsp;3.1 Modificamos la cantidad:si es modo ms uno le
	 *            sumamos uno, si no creamos el dialog para modificar, sumar o
	 *            restar
	 * 
	 */
	private void seleccionarMostrarIndiceArticulo(int indice_articulo) {
		// Configuramos las variables:
		// if (listaArticulosCompleta.size() <= 8) {
		// indice_primera_linea = 0;
		// }
		// else {
		System.out.println("::: PaginaInventarioDinamico 1031 indice_articulo: " 
				+ indice_articulo);
		if (indice_articulo < 0) {
			return;
		} else if (indice_articulo == 0) {
			indice_primera_linea = indice_articulo;
		} else {
			indice_primera_linea = indice_articulo - 1;
		}
		// }
				// 1 Seteamos el indice_on_focus al indice pasado
				indice_on_focus = indice_articulo;
				System.out.println(":::ACA EENTRO 1 : " + indice_on_focus);
				// 2 Refresh de las tablas:
				refreshTablaCentral();

				System.out.println(":::ACA EENTRO 1");
				// 3 Buscamos la linea que modificar:
				for (int j = 0; j < NUMERO_LINEAS_EN_TABLA; j++) {
					TableRow linea = (TableRow) tabla_articulos.getChildAt(j);
			System.out.println("::: Dentro del for + " + linea);
			if (Float.parseFloat(String.valueOf(((TextView) (linea
					.getChildAt(5))).getText())) == indice_articulo) {
				System.out.println(":::ACA EENTRO 2");
				linea.setBackgroundColor(getResources().getColor(
						R.color.anaranjado_verde));
				final EditText edittext = (EditText) linea.getChildAt(3);
				// edittext.setFocusable(true);
				edittext.requestFocus();
				edittext.setFocusableInTouchMode(true);
				// InputMethodManager mgr = (InputMethodManager)
				// getSystemService(Context.INPUT_METHOD_SERVICE);
				// mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
				//
				edittext.setBackgroundColor(getResources().getColor(
						R.color.anaranjado_verde));
				edittext.setTextColor(getResources().getColor(R.color.white));
				System.out.println(":::ACA EENTRO 3");
				try {
					final float cantidad_producto = Float.parseFloat(String
							.valueOf(edittext.getText())); // Ac nos aseguramos
															// que la linea es
															// un nombre, si es
															// otra cosa lo
															// reemplazamos por
															// vacio ""

					// Aca, el articulo est y tiene cantidad.
					// Si estamos en modo +1, no mostramos le menu 3M:
					if (modo_mas_1 == 1) {
						return;
					}

					// Si la linea enfocada ya contiene un valor numrico,
					// abrimos un dialog para preguntar si queremos sumar,
					// restar, modificar y cancelar:
					System.out.println("::: PaginaInventario 1088 aca deberia abrir el dialogo");
					final View.OnClickListener listenerNegativo = new View.OnClickListener() {

						public void onClick(View v) {

							log.log("[-- 1143 --]"
									+ "Se cancelo Dialogo de Modificacion", 0);
							/*
							 * editTT = (EditText)
							 * dialogoModificacion.findViewById
							 * (R.id.Z_DIALOG_cantidad_nueva);
							 * InputMethodManager mgr = (InputMethodManager)
							 * getSystemService(Context.INPUT_METHOD_SERVICE);
							 * mgr
							 * .hideSoftInputFromWindow(editTT.getWindowToken(),
							 * 0);
							 */

							dialogoModificacion.cancel();
						}
					};

					// ***************
					// * SUMAR *
					// ***************
					View.OnClickListener listener_sumar = new View.OnClickListener() {

						public void onClick(View v) {

							log.log("[-- 1166--]"
									+ "Se abre el cuador de dialogo para la suma",
									0);
							// Abrir un dialogo para sumar valores:
							View.OnClickListener listenerPositivo = new View.OnClickListener() {

								public void onClick(View v) {

									log.log("[-- 1173 --]"
											+ "Se presiono para editar texto",
											2);
									editTT = (EditText) dialogoModificacion
											.findViewById(R.id.Z_DIALOG_cantidad_nueva);
									System.out.println("::: Que tiene edittext " + editTT);
									InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									mgr.hideSoftInputFromWindow(
											editTT.getWindowToken(), 0);

									SystemClock.sleep(20);

									String valor_a_sumar = dialogoModificacion
											.get_nuevo_valor();
									System.out.println("::: Que tiene valor_a_sumar " + valor_a_sumar);
									edittext.setText(String.valueOf(cantidad_producto
											+ Float.parseFloat(valor_a_sumar)));

									dialogoModificacion.dismiss();
								}
							};

							dialogoModificacion = new DialogPersoComplexCantidadModificacion(
									ctxt, DialogPerso.OPERACION_SUMAR,
									cantidad_producto, listenerPositivo,
									listenerNegativo, null);
							dialogoModificacion
									.setOnShowListener(new DialogInterface.OnShowListener() {
										public void onShow(
												DialogInterface dialog) {
											System.out.println("::: Entro en el siguiente");
											editTT = (EditText) dialogoModificacion
													.findViewById(R.id.Z_DIALOG_cantidad_nueva);
											InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
											mgr.showSoftInput(
													editTT,
													InputMethodManager.SHOW_FORCED);

										}
									});
							dialogoModificacion
									.setOnDismissListener(new OnDismissListener() {

										public void onDismiss(
												DialogInterface dialog) {
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
											dialogoMasMenos.dismiss();
										}
									});
							dialogoModificacion
									.setOnCancelListener(new OnCancelListener() {

										public void onCancel(
												DialogInterface dialog) {
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
										}
									});
							dialogoModificacion.show();
						}
					};

					// ****************
					// * RESTAR *
					// ****************
					View.OnClickListener listener_restar = new View.OnClickListener() {

						public void onClick(View v) {
							// Abrir un dialogo para restar valores:
							View.OnClickListener listenerPositivo = new View.OnClickListener() {

								public void onClick(View v) {
									editTT = (EditText) dialogoModificacion
											.findViewById(R.id.Z_DIALOG_cantidad_nueva);
									InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									mgr.hideSoftInputFromWindow(
											editTT.getWindowToken(), 0);

									SystemClock.sleep(20);

									String valor_a_restar = dialogoModificacion
											.get_nuevo_valor();
									float nuevo_valor = Math
											.max(0,
													cantidad_producto
															- Float
																	.parseFloat(valor_a_restar));
									edittext.setText(String
											.valueOf(nuevo_valor));

									dialogoModificacion.dismiss();
								}
							};

							dialogoModificacion = new DialogPersoComplexCantidadModificacion(
									ctxt, DialogPerso.OPERACION_RESTAR,
									cantidad_producto, listenerPositivo,
									listenerNegativo, null);
							dialogoModificacion
									.setOnShowListener(new DialogInterface.OnShowListener() {

										public void onShow(
												DialogInterface dialog) {
											editTT = (EditText) dialogoModificacion
													.findViewById(R.id.Z_DIALOG_cantidad_nueva);
											InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
											mgr.showSoftInput(
													editTT,
													InputMethodManager.SHOW_FORCED);

										}
									});
							dialogoModificacion
									.setOnDismissListener(new OnDismissListener() {

										public void onDismiss(
												DialogInterface dialog) {
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
											dialogoMasMenos.dismiss();
										}
									});
							dialogoModificacion
									.setOnCancelListener(new OnCancelListener() {

										public void onCancel(
												DialogInterface dialog) {
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
										}
									});
							dialogoModificacion.show();
						}
					};

					// *******************
					// * MODIFICAR *
					// *******************
					View.OnClickListener listener_modificar = new View.OnClickListener() {

						public void onClick(View v) {

							log.log("[-- 1356 --]"
									+ "Se presiona para ingresar un nuevo valor",
									0);
							// Abrir dialogo para ingresar nuevo valor:
							View.OnClickListener listenerPositivo = new View.OnClickListener() {

								public void onClick(View v) {
									editTT = (EditText) dialogoModificacion
											.findViewById(R.id.Z_DIALOG_cantidad_nueva);
									InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									mgr.hideSoftInputFromWindow(
											editTT.getWindowToken(), 0);

									SystemClock.sleep(20);

									String valor_string = (dialogoModificacion)
											.get_nuevo_valor();
									edittext.setText(String
											.valueOf(valor_string));

									dialogoModificacion.dismiss();
								}
							};

							View.OnClickListener listenerReset = new View.OnClickListener() {

								public void onClick(View v) {
									editTT = (EditText) dialogoModificacion
											.findViewById(R.id.Z_DIALOG_cantidad_nueva);
									InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									mgr.hideSoftInputFromWindow(
											editTT.getWindowToken(), 0);

									SystemClock.sleep(20);

									// Caso de reset: se escribe "No tomado":
									log.log("[-- 1391 --]" + "Valor reseteado",
											0);
									edittext.setText("");

									dialogoModificacion.dismiss();
								}
							};

							dialogoModificacion = new DialogPersoComplexCantidadModificacion(
									ctxt, DialogPerso.OPERACION_MODIFICAR,
									cantidad_producto, listenerPositivo,
									listenerNegativo, listenerReset);
							dialogoModificacion
									.setOnShowListener(new DialogInterface.OnShowListener() {

										public void onShow(
												DialogInterface dialog) {
											editTT = (EditText) dialogoModificacion
													.findViewById(R.id.Z_DIALOG_cantidad_nueva);
											InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
											mgr.showSoftInput(
													editTT,
													InputMethodManager.SHOW_FORCED);
										}
									});
							dialogoModificacion
									.setOnDismissListener(new OnDismissListener() {

										public void onDismiss(
												DialogInterface dialog) {
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
											dialogoMasMenos.dismiss();
										}
									});
							dialogoModificacion
									.setOnCancelListener(new OnCancelListener() {

										public void onCancel(
												DialogInterface dialog) {

											log.log("[-- 1442 --]"
													+ "Se cancela la accion", 0);
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
										}
									});
							dialogoModificacion.show();
						}
					};

					View.OnClickListener listener_cancelar = new View.OnClickListener() {

						public void onClick(View v) {
							// Es un simple cancel:
							dialogoMasMenos.cancel();
						}
					};

					dialogoMasMenos = new DialogPersoComplexCantidadMasMenos(
							ctxt, "Articulo ya inventariado",
							(float) cantidad_producto, listener_sumar, listener_restar,
							listener_modificar, listener_cancelar);
					dialogoMasMenos
							.setOnDismissListener(new OnDismissListener() {

								public void onDismiss(DialogInterface dialog) {
									/*
									 * editTT = (EditText)
									 * dialogoModificacion.findViewById
									 * (R.id.Z_DIALOG_cantidad_nueva);
									 * InputMethodManager mgr =
									 * (InputMethodManager)
									 * getSystemService(Context
									 * .INPUT_METHOD_SERVICE);
									 * mgr.hideSoftInputFromWindow
									 * (editTT.getWindowToken(), 0);
									 */

									deseleccionarLineaParticular(indice_on_focus);
								}
							});
					dialogoMasMenos
							.setOnCancelListener(new OnCancelListener() {

								public void onCancel(DialogInterface dialog) {
									deseleccionarLineaParticular(indice_on_focus);
								}
							});

					dialogoMasMenos.show();

				} catch (Exception e) {
					edittext.setText("");
					edittext.requestFocus();

					// Toast.makeText(ctxt, "Clavier apparait maintenant",
					// Toast.LENGTH_LONG).show();

					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
				}

				break;
			}
		}
	}

	/**
	 * Deselecciona una linea indicada por el indice, al deseleccionar debemos
	 * grabar la informacion actualizada en la BD
	 * 
	 * @param indice_focused
	 */
	private void deseleccionarLineaParticular(int indice_focused) {
		deseleccionarLineaParticular(indice_focused, true);
	}

	/**
	 * Deselecciona la linea indicada por el indice y avisa si hay que grabar la
	 * informacion o no, al deseleccionar debemos,si se pide, grabar la
	 * informacion actualizada en la BD
	 * <p>
	 * 1 Buscamos la linea que modificar
	 * <p>
	 * 2 Cambiamos el color
	 * <p>
	 * 3 Modificamos el objeto ARTICULO y su valor en la base de datos (si el
	 * argumento booleano "grabar_si_no" lo permite y si no es un valor erroneo,
	 * muy grande o invalido. Si el valor es invalido se pone -1 o el valor
	 * anterior
	 * 
	 * @param indice_focused
	 * @param grabar_si_no
	 */
	private void deseleccionarLineaParticular(int indice_focused,
			boolean grabar_si_no) {
		if (indice_focused >= 0) {
			// 1 Buscamos la linea que modificar:
			TableRow lineaFocused = null;
			int numeroLineaFocused = 0;

			for (int j = 0; j < tabla_articulos.getChildCount(); j++) {
				lineaFocused = (TableRow) tabla_articulos.getChildAt(j);

				if (Integer.parseInt(String.valueOf(((TextView) (lineaFocused
						.getChildAt(5))).getText())) == indice_focused) {
					numeroLineaFocused = j;
					break;
				}
			}

			// 2 Cambiamos el color
			lineaFocused.setBackgroundColor(getResources().getColor(
					R.color.verde_claro));

			EditText edittext = (EditText) lineaFocused.getChildAt(3);
			edittext.setFocusable(false);
			if ((indice_primera_linea % 2) == (numeroLineaFocused % 2)) {
				edittext.setBackgroundColor(getResources().getColor(
						R.color.verde_oscuro));
			} else {
				edittext.setBackgroundColor(getResources().getColor(
						R.color.verde_oscuro_mas));
			}
			edittext.setTextColor(getResources().getColor(
					R.color.anaranjado_verde));

			tabla_articulos.requestFocus();

			indice_on_focus = -1;

			// se esconde el teclado? para que
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(edittext.getWindowToken(), 0);

			// 3 Modificamos el objeto ARTICULO y su valor en la base de datos
			// (si
			// el argumento booleano "grabar_si_no" lo permite):
			if (grabar_si_no == true) {
				String valorDelEdittext = "";
				try {
					valorDelEdittext = String.valueOf(edittext.getText())
							.trim();
					System.out.println(":::No tomado, toma el valor con trim "
							+ valorDelEdittext);
				} catch (Exception e) {

					log.log("[-- 1588 --]" + e.toString(), 4);
					// Si excepcion levantada, es que el contenido del Edittext
					// es malo, ponemos valor del articulo a -1 (sin hacer):
					listaArticulosCompleta.get(indice_focused).setCantidad(-1);
					showSimpleDialogOK("Error", "Valor incorrecto (1)").show();
					edittext.setText("No Tomado");
					return;
				}
				System.out.println(":::Ingresa al no tomado");

				if (valorDelEdittext.length() == 0) {
					listaArticulosCompleta.get(indice_focused).setCantidad(-1);
					edittext.setText("No Tomado");
					// return;
				} else {
					try {
						System.out.println("::: Aca hace le proceso de guardado");
						float cantidad = Float.parseFloat(valorDelEdittext);
						listaArticulosCompleta.get(indice_focused).setCantidad(
								cantidad);
						if (listaArticulosCompleta.get(indice_focused)
								.getFechaInicio().length() == 0) {
							listaArticulosCompleta.get(indice_focused)
									.setFechaInicio(
											new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new Date()));
						}
						listaArticulosCompleta.get(indice_focused).setFechaFin(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(new Date()));
					} catch (Exception e) {

						log.log("[-- 1619 --]" + e.toString(), 4);
						// Si excepcion levantada, es que el contenido del
						// Edittext no es un Integer, en este caso: -1
						listaArticulosCompleta.get(indice_focused).setCantidad(
								-1);
						showSimpleDialogOK("Error",
								"Valor incorrecto, ingrese un entero").show();
						edittext.setText("No Tomado");
						return;
					}
				}

				bdd = new BaseDatos(ctxt);
				try {
					// Cuidado, si el nuevo valor es "No Tomado" (cantidad Q =
					// -1), hay que suprimir la fecha en la BDD:
					bdd.updateArticulo(listaArticulosCompleta
							.get(indice_focused));
				} catch (ExceptionBDD e) {

					log.log("[-- 1639 --]" + e.toString(), 4);
					e.printStackTrace();
					showSimpleDialogOK("Error", e.toString()).show();
				}
			} else {
				// Si decidimos no grabar el valor, entonces reponemos el viejo
				// valor del edittext como antes:
				edittext.setText(String.valueOf(valor_antes_modificar));
				valor_antes_modificar = -1;
			}
		}
	}

	/**
	 * Muestra un dialog con la opcion de OK
	 */

	public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {

		log.log("[-- 1663 --]" + "titulo: " + titulo + ", \n mensaje: "
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

	/**
	 * Muestra un dialogo por un tiempo determinado
	 * 
	 * @param titulo
	 * @param mensaje
	 * @param timer_ms
	 * @return
	 */
	public AlertDialog showSimpleDialogTimer(String titulo, String mensaje,
			final int timer_ms) {

		log.log("[-- 1686 --]" + "titulo: " + titulo + ", \n mensaje: "
				+ mensaje, 0);
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		alert = dialogoSimple.create();

		ShowDialogTimer unDialogoTimer = new ShowDialogTimer();
		unDialogoTimer.execute(ctxt);

		return alert;
	}

	/**
	 * Muestra un dialog con opcion Si o No, en el caso afirmativo procedemos a
	 * la Activity definida por "clase"
	 */

	public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje,
			final Class<?> clase) {
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						Intent i = new Intent(PaginaInventario.this, clase);
						startActivity(i);
						dialog.dismiss();
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						Intent i = new Intent(PaginaInventario.this, clase);
						startActivity(i);
						dialog.dismiss();
						finish();
					}
				});
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}

	/**
	 * Funcion para filtrar los articulos a mostrar, evalua los valores de
	 * banderas segun el filtro que se aplique
	 * <p>
	 * 1 Configuramos la visibilidad de todos los articulos
	 * <p>
	 * &nbsp; &nbsp;1.1 SI hay filtro por sector Y SI el sector del articulo no
	 * est en la lista, visibilidad = false
	 * <p>
	 * &nbsp; &nbsp;1.2 SI hay filtro por precio Y SI el precio no corresponde a
	 * los criterios, visibilidad = false
	 * <p>
	 * &nbsp; &nbsp;1.3 SI hay filtro por inventario hecho o no Y SI el estado
	 * no corresponde a los criterios, visibilidad = false
	 * <p>
	 * 2 Deseleccionamos la linea en focus
	 */
	private void filtrar() {
		numero_articulos_visibles = 0;
		// Configuramos la visibilidad de todos los articulos:
		for (ArticuloVisible a : listaArticulosCompleta) {

			// SI hay filtro por sector Y SI el sector del articulo no est en
			// la lista, visibilidad = false:
			if (listaSectoresFiltrados.size() > 0
					&& listaSectoresFiltrados.contains((Object) a.getSector()) == false) {
				a.setVisibilidad(false);
				continue;
			}

			// SI hay filtro por precio Y SI el precio no corresponde a los
			// criterios, visibilidad = false:
			if (precioFiltro != 0) {
				if (precioFiltro > 0 && a.getPrecio_venta() < precioFiltro) {
					a.setVisibilidad(false);
					continue;
				} else if (precioFiltro < 0
						&& a.getPrecio_venta() > Math.abs(precioFiltro)) {
					a.setVisibilidad(false);
					continue;
				}
			}

			// SI hay filtro por inventario hecho o no Y SI el estado no
			// corresponde a los criterios, visibilidad = false:
			if (inventarioFiltro != 0) {
				if (inventarioFiltro > 0 && a.getCantidad() < 0) {
					a.setVisibilidad(false);
					continue;
				} else if (inventarioFiltro < 0 && a.getCantidad() >= 0) {
					a.setVisibilidad(false);
					continue;
				}
			}
			a.setVisibilidad(true);
			numero_articulos_visibles++;
		} // end for
		deseleccionarLineaParticular(indice_on_focus);
		try {
			indice_primera_linea = getIndiceArticuloVisibleMasCercano(0);
		} catch (Exception e) {

			log.log("[-- 1793 --]" + e.toString(), 4);
			indice_primera_linea = 0;
			loadingBar.setVisibility(View.GONE);
			showSimpleDialogOK("ERROR de filtraje", "Imposible filtrar").show();
			return;
		}
		refreshTablaCentral();
		loadingBar.setVisibility(View.GONE);
	}

	/**
	 * Muestra el menu del filtro a aplicar, actualiza las banderas
	 * correspondientes?
	 * <p>
	 * 1 Trabajamos con copias de las 3 variables de filtro, asi en caso de
	 * "CANCEL" no modificamos nada
	 * <p>
	 * 2 Segun el tipo de filtro guardamos la informacion y las banderas del
	 * mismo
	 * <p>
	 * 3 Aplicar modificaciones desde las variables filtro hasta las
	 * <p>
	 * 4 Mostrar el icono de filtro
	 * <p>
	 * 5 Actualizar el filtrado en la UI llamando a filtrar()
	 * 
	 * @param tipoFiltro
	 */
	public void showMenuFiltro(int tipoFiltro) {

		log.log("[-- 1823 --]" + "Inicio el filtro de sector", 2);
		loadingBar.setVisibility(View.VISIBLE);
		loadingBar.bringToFront();
		dialogo = new Dialog(ctxt);
		LinearLayout layoutMenu = new LinearLayout(ctxt);
		layoutMenu.setOrientation(LinearLayout.VERTICAL);

		// Trabajamos con copias de las 3 variables de filtro, asi en caso de
		// "CANCEL" no modificamos nada:
		copia_listaSectoresFiltrados.clear();
		for (int i : listaSectoresFiltrados) {
			copia_listaSectoresFiltrados.add(i);
		}
		copia_precioFiltro = precioFiltro;
		copia_inventarioFiltro = inventarioFiltro;

		if (tipoFiltro == ParametrosInventario.filtro_sector) {
			dialogo.setTitle("Filtrar por sector");
			for (int sector : getListaTodosSectores()) {
				CheckBox chkb = new CheckBox(ctxt);
				chkb.setText("Sector " + String.valueOf(sector));
				chkb.setTextSize(ParametrosInventario.TALLA_TEXTO);
				chkb.setId(Parametros.ID_CHECKBOX + sector);
				if (listaSectoresFiltrados.contains(sector) == true) {
					chkb.setChecked(true);
				}
				chkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(@NonNull CompoundButton checkbox,
                                                 boolean isChecked) {

						log.log("[-- 1854 --]" + "Cambio de estado chkb", 2);
						if (isChecked == true) {
							copia_listaSectoresFiltrados.add(checkbox.getId()
									- Parametros.ID_CHECKBOX);
						} else {
							copia_listaSectoresFiltrados
									.remove((Object) (checkbox.getId() - Parametros.ID_CHECKBOX));
						}
					}
				});
				layoutMenu.addView(chkb);
			}
		} else if (tipoFiltro == ParametrosInventario.filtro_precio) {
			dialogo.setTitle("Filtrar por precio");

			RadioButton rdioSup = new RadioButton(ctxt);
			rdioSup.setText("Precios superiores a: ");
			rdioSup.setTextSize(ParametrosInventario.TALLA_TEXTO);
			rdioSup.setId(1 + Parametros.ID_RADIOBUTTON);

			RadioButton rdioInf = new RadioButton(ctxt);
			rdioInf.setText("Precios inferiores a: ");
			rdioInf.setTextSize(ParametrosInventario.TALLA_TEXTO);
			rdioInf.setId(-1 + Parametros.ID_RADIOBUTTON);

			RadioButton rdioSin = new RadioButton(ctxt);
			rdioSin.setText("Sin filtrar");
			rdioSin.setTextSize(ParametrosInventario.TALLA_TEXTO);
			rdioSin.setId(Parametros.ID_RADIOBUTTON);

			EditText valorUmbral = new EditText(ctxt);
			valorUmbral.setSingleLine(true);
			valorUmbral.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
			valorUmbral.setId(Parametros.ID_EDITTEXT);
			valorUmbral.setTextSize(ParametrosInventario.TALLA_TEXTO);

			if (precioFiltro < 0) {
				rdioInf.setChecked(true);
				valorUmbral.setEnabled(true);
				valorUmbral
						.setText(String.valueOf(Math.abs((int) precioFiltro)));
				copia_precioFiltro = -1;
			} else if (precioFiltro > 0) {
				rdioSup.setChecked(true);
				valorUmbral.setEnabled(true);
				valorUmbral
						.setText(String.valueOf(Math.abs((int) precioFiltro)));
				copia_precioFiltro = 1;
			} else if (precioFiltro == 0) {
				rdioSin.setChecked(true);
				valorUmbral.setEnabled(false);
				valorUmbral.setText("");
				copia_precioFiltro = 0;
			}

			rdioInf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton view,
						boolean isChecked) {

					log.log("[-- 1914 --]"
							+ "Cambi ode estado de Radio Button Inferior", 2);
					if (isChecked == true) {
						copia_precioFiltro = -1;

						RadioButton rbSup = (RadioButton) dialogo
								.findViewById(1 + Parametros.ID_RADIOBUTTON);
						rbSup.setChecked(false);

						RadioButton rbSin = (RadioButton) dialogo
								.findViewById(0 + Parametros.ID_RADIOBUTTON);
						rbSin.setChecked(false);

						EditText valorUmbral = (EditText) dialogo
								.findViewById(Parametros.ID_EDITTEXT);
						valorUmbral.setEnabled(true);
					}
				}
			});

			rdioSup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton view,
						boolean isChecked) {
					if (isChecked == true) {

						log.log("[-- 1939 --]"
								+ "Cambio de estado de Radio Superior", 2);
						copia_precioFiltro = 1;

						RadioButton rbInf = (RadioButton) dialogo
								.findViewById(-1 + Parametros.ID_RADIOBUTTON);
						rbInf.setChecked(false);

						RadioButton rbSin = (RadioButton) dialogo
								.findViewById(0 + Parametros.ID_RADIOBUTTON);
						rbSin.setChecked(false);

						EditText valorUmbral = (EditText) dialogo
								.findViewById(Parametros.ID_EDITTEXT);
						valorUmbral.setEnabled(true);
					}
				}
			});

			rdioSin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton view,
						boolean isChecked) {

					log.log("[-- 1962 --]" + "Cambio de estado de Radio Sin ",
							2);
					if (isChecked == true) {
						copia_precioFiltro = 0;

						RadioButton rbSup = (RadioButton) dialogo
								.findViewById(1 + Parametros.ID_RADIOBUTTON);
						rbSup.setChecked(false);

						RadioButton rbInf = (RadioButton) dialogo
								.findViewById(-1 + Parametros.ID_RADIOBUTTON);
						rbInf.setChecked(false);

						EditText valorUmbral = (EditText) dialogo
								.findViewById(Parametros.ID_EDITTEXT);
						valorUmbral.setEnabled(false);
						copia_precioFiltro = 0;
					}
				}
			});

			layoutMenu.addView(rdioSin);
			layoutMenu.addView(rdioSup);
			layoutMenu.addView(rdioInf);
			layoutMenu.addView(valorUmbral);
		} else if (tipoFiltro == ParametrosInventario.filtro_inventario) {
			dialogo.setTitle("Filtrar por estado del inventario");

			RadioButton rdioSin = new RadioButton(ctxt);
			rdioSin.setText("Sin filtrar");
			rdioSin.setTextSize(ParametrosInventario.TALLA_TEXTO);
			rdioSin.setId(Parametros.ID_RADIOBUTTON);

			RadioButton rdioHecho = new RadioButton(ctxt);
			rdioHecho.setText("Articulos ya inventariados");
			rdioHecho.setTextSize(ParametrosInventario.TALLA_TEXTO);
			rdioHecho.setId(Parametros.ID_RADIOBUTTON + 2);

			RadioButton rdioVirgen = new RadioButton(ctxt);
			rdioVirgen.setText("Articulos NO inventariados");
			rdioVirgen.setTextSize(ParametrosInventario.TALLA_TEXTO);
			rdioVirgen.setId(Parametros.ID_RADIOBUTTON - 2);

			if (copia_inventarioFiltro == 1) {
				rdioHecho.setChecked(true);
			} else if (copia_inventarioFiltro == -1) {
				rdioVirgen.setChecked(true);
			} else if (copia_inventarioFiltro == 0) {
				rdioSin.setChecked(true);
			}

			rdioVirgen
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton view,
								boolean isChecked) {
							log.log("[-- 2017 --]"
									+ "Cambio de estado de Radio Virgen", 2);
							if (isChecked == true) {
								RadioButton rbSin = (RadioButton) dialogo
										.findViewById(0 + Parametros.ID_RADIOBUTTON);
								rbSin.setChecked(false);
								RadioButton rbHecho = (RadioButton) dialogo
										.findViewById(2 + Parametros.ID_RADIOBUTTON);
								rbHecho.setChecked(false);

								copia_inventarioFiltro = -1;
							}
						}
					});

			rdioHecho
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton view,
								boolean isChecked) {
							log.log("[-- 2035 --]"
									+ "Cambio de estado de Radio Hecho", 2);
							if (isChecked == true) {
								RadioButton rbSin = (RadioButton) dialogo
										.findViewById(0 + Parametros.ID_RADIOBUTTON);
								rbSin.setChecked(false);
								RadioButton rbVirgen = (RadioButton) dialogo
										.findViewById(-2
												+ Parametros.ID_RADIOBUTTON);
								rbVirgen.setChecked(false);

								copia_inventarioFiltro = 1;
							}
						}
					});

			rdioSin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton view,
						boolean isChecked) {
					if (isChecked == true) {

						log.log("[-- 2055 --]"
								+ "Cambio de estado de Radio sin", 2);
						RadioButton rbHecho = (RadioButton) dialogo
								.findViewById(2 + Parametros.ID_RADIOBUTTON);
						rbHecho.setChecked(false);
						RadioButton rbVirgen = (RadioButton) dialogo
								.findViewById(-2 + Parametros.ID_RADIOBUTTON);
						rbVirgen.setChecked(false);

						copia_inventarioFiltro = 0;
					}
				}
			});

			layoutMenu.addView(rdioSin);
			layoutMenu.addView(rdioHecho);
			layoutMenu.addView(rdioVirgen);
		}

		LinearLayout layoutBotones = new LinearLayout(ctxt);
		Button boton_cancel = new Button(ctxt);
		boton_cancel.setText("Cancel");
		boton_cancel.setTextSize(ParametrosInventario.TALLA_TEXTO);
		Button boton_validar = new Button(ctxt);
		boton_validar.setText("Validar");
		boton_validar.setTextSize(ParametrosInventario.TALLA_TEXTO);
		boton_cancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dialogo.cancel();
				loadingBar.setVisibility(View.GONE);
			}
		});
		boton_validar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				log.log("[-- 2091 --]" + "Se presiona el boton validar", 0);
				dialogo.dismiss();
				loadingBar.setVisibility(View.VISIBLE);
				loadingBar.bringToFront();

				// Aplicar modificaciones desde las variables filtro hasta las
				// variables reales:
				listaSectoresFiltrados.clear();
				for (int j : copia_listaSectoresFiltrados) {
					listaSectoresFiltrados.add(j);
				}
				EditText valorUmbral = (EditText) dialogo
						.findViewById(Parametros.ID_EDITTEXT);
				if (valorUmbral != null) {
					precioFiltro = copia_precioFiltro
							* Double.parseDouble(String.valueOf(valorUmbral
									.getText()));
				}
				inventarioFiltro = copia_inventarioFiltro;

				// Mostrar el icono de filtro:
				if (listaSectoresFiltrados.size() > 0) {
					filtro_codigo.setVisibility(View.VISIBLE);
				} else {
					filtro_codigo.setVisibility(View.INVISIBLE);
				}

				if (precioFiltro != 0) {
					filtro_precio.setVisibility(View.VISIBLE);
				} else {
					filtro_precio.setVisibility(View.INVISIBLE);
				}

				if (inventarioFiltro != 0) {
					filtro_cantidad.setVisibility(View.VISIBLE);
				} else {
					filtro_cantidad.setVisibility(View.INVISIBLE);
				}

				// Aplicar el filtro visualmente:
				filtrar();

			}
		});
		layoutBotones.addView(boton_validar);
		layoutBotones.addView(boton_cancel);
		layoutBotones.setGravity(Gravity.CENTER_VERTICAL);
		layoutMenu.addView(layoutBotones);
		dialogo.setContentView(layoutMenu);
		dialogo.show();
	}

	/**
	 * Devuelve todos los sectores posibles para el filtrado
	 * 
	 * @return
	 */
	@NonNull
    private ArrayList<Integer> getListaTodosSectores() {
		ArrayList<Integer> listaRespuesta = new ArrayList<Integer>();

		for (Articulo articulo : listaArticulosCompleta) {
			if (listaRespuesta.contains((int) articulo.getSector()) == false) {
				listaRespuesta.add(articulo.getSector());
			}
		}

		return listaRespuesta;
	}

	/**
	 * Esta foncion se usa despues de un cambio de orden o la aplicacin de un
	 * filtro para encontrar el articulo VISIBLE mas cercano al indice anterior,
	 * dado que el articulo que se presenta en el indice enfocado anteriormente
	 * puede no ser visible como su predecesor.
	 * 
	 * Hacemos una busqueda al indice. Si no esta visible, miramos el siguiente.
	 * Si no esta visible, buscamos a la posicion anterior de la posicion
	 * inicial. El indice varia segun +1, -2, +3, -4, +5, -6, etc...
	 * 
	 * <p>
	 * 1 Verifica que haya al menos un articulo visible
	 * <p>
	 * 2 Mientras no se encuentre el articulo visible en la lista de todos los
	 * articulos
	 * <p>
	 * &nbsp; &nbsp;2.1 Actualiza el indice segun el paso
	 * <p>
	 * &nbsp; &nbsp;3.1 Verifica que este el articulo visible en la lista con
	 * ese indice
	 * 
	 * @param indice_anterior
	 * @return indice del articulo visible ms cercano por arriba o abajo
	 * @throws Exception
	 */
	private int getIndiceArticuloVisibleMasCercano(int indice_anterior)
			throws Exception {
		ArticuloVisible a = new ArticuloVisible(false);
		int indice = indice_anterior;
		int paso = -1;

		// Hacemos una busqueda al indice. Si no esta visible, miramos el
		// siguiente. Si no esta visible,
		// buscamos a la posicion anterior de la posicion inicial.
		// El indice varia segun +1, -2, +3, -4, +5, -6, etc...
		//
		// ATENCIN!!!!
		// Corremos el riesgo que el indice sea <0 o >listaArticulos.size().
		// Solucin: try / catch, para matar el error y seguir al indice
		// siguiente.
		//
		// ATENCIN!!!!
		// Si el filtro ha escondido todos los articulos, esta funcion no para
		// nunca...
		// Solucin: hace un control antes:
		boolean hayMinimoUnArticuloVisible = false;
		for (ArticuloVisible av : listaArticulosCompleta) {
			if (av.esVisible() == true) {
				hayMinimoUnArticuloVisible = true;
				break;
			}
		}

		if (hayMinimoUnArticuloVisible == false) {
			throw new Exception("Ningun articulo visible en la lista");
		}

		do {
			paso++;
			if ((paso % 2) == 1) {
				indice = indice + paso;
			} else {
				indice = indice - paso;
			}

			try {
				a = listaArticulosCompleta.get(indice);
			} catch (Exception e) {
				log.log("[-- 2228 --]" + e.toString(), 4);
			}

		} while (a.esVisible() == false);

		return indice;
	}

	/**
	 * Idem anterior pero busca para arriba Buscamos al articulo visible
	 * anterior al nuestro en el cual el operador ha clicado.Es decir que
	 * hacemos una busqueda al indice anterior, de -1 en -1 hasta topar con un
	 * indice correspondiendo a un articulo visible. Si topamos con 0 antes de
	 * encontrar un articulo visible, es que indice_anterior es el primer
	 * articulo visible de la lista.
	 * 
	 * @param indice_anterior
	 * @return
	 * @throws Exception
	 */
	private int getIndiceArticuloVisibleMasCercanoPorArriba(int indice_anterior)
			throws Exception {
		ArticuloVisible a = new ArticuloVisible(false);
		int indice = indice_anterior;

		// Buscamos al articulo visible anterior al nuestro en el cual el
		// operador ha clicado.
		// Es decir que hacemos una busqueda al indice anterior, de -1 en -1
		// hasta topar con un indice
		// correspondiendo a un articulo visible.
		// Si topamos con 0 antes de encontrar un articulo visible, es aue
		// indice_anterior es el primer
		// articulo visible de la lista.
		if (indice == 0) {
			return getIndiceArticuloVisibleMasCercano(0);
		}

		indice--;
		a = listaArticulosCompleta.get(indice);

		while (a.esVisible() == false) {
			indice--;

			try {
				a = listaArticulosCompleta.get(indice);
			} catch (Exception e) {

				log.log("[-- 2275 --]" + e.toString(), 4);
				return indice_anterior;
			}
		}

		return indice;
	}

	/**
	 * Busca los indices de los proximos articulos visibles a partir del indice
	 * dado
	 * <p>
	 * 1 Buscamos el articulo visible mas cercano a nuestra incice-posicion
	 * actual
	 * <p>
	 * 2 Buscar los 7 elementos que siguen. Atencin, puede no haber 7, puede
	 * haber menos
	 * <p>
	 * 3 Actualizamos el indice de la primera linea
	 * 
	 * @param indice_actual
	 * @param hay_indice_on_focus
	 * @return una lista con indices de los articulos visibles en la lista
	 * @throws Exception
	 */
	@NonNull
    private ArrayList<Integer> getProximosIndicesArticulosVisibles(
			int indice_actual, boolean hay_indice_on_focus) throws Exception {
		// Primero buscamos el articulo visible mas cercano a nuestra
		// incice-posicion actual:
		int indice_inicial = 0;
		if (hay_indice_on_focus == false) {
			indice_inicial = getIndiceArticuloVisibleMasCercano(indice_actual);
		} else {
			indice_inicial = getIndiceArticuloVisibleMasCercanoPorArriba(indice_actual);
		}

		// Buscar los 7 elementos que siguen:
		// Atencin, puede no haber 7, puede haber menos:
		ArrayList<Integer> listaRespuesta = new ArrayList<Integer>();
		listaRespuesta.add(indice_inicial);
		int index = indice_inicial + 1;

		while (listaRespuesta.size() < NUMERO_LINEAS_EN_TABLA) {
			try {
				ArticuloVisible av = listaArticulosCompleta.get(index);
				if (av.esVisible() == true) {
					listaRespuesta.add(index);
				}
				index++;

			} catch (Exception e) {
				log.log("[-- 2326 --]" + e.toString(), 4);
				// Error: si llegamos aca, es que estamos al fin de
				// listaArticulosCompleta, entonces paramos.
				break;
			}
		}

		indice_primera_linea = listaRespuesta.get(0);
		// Fin: que sea el break o que hayamos alcanzado los 8 elementos,
		// devolvemos resultado:
		return listaRespuesta;
	}

	/**
	 * Funcion para mover la tabla de articulos tantas unidades como lo indica
	 * su argumento
	 * 
	 * @param unidades
	 */
	private void moverTablaArticulos(int unidades) {
		if (unidades != 0) {
			deseleccionarLineaParticular(indice_on_focus);

			int contador = Math.abs(unidades);
			int paso = 0;
			if (unidades > 0) {
				paso = 1;
			} else {
				paso = -1;
			}
			int indice_busqueda = indice_primera_linea;

			try {
				while (contador > 0) {
					indice_busqueda += paso;
					ArticuloVisible av = listaArticulosCompleta
							.get(indice_busqueda);
					if (av.esVisible() == true) {
						contador--;
						indice_primera_linea = indice_busqueda;
					}
				}
			} catch (Exception e) {

				log.log("[-- 2370 --]" + e.toString(), 4);
				// Bloc catch: hemos sobrepasado el indice mas grande de la
				// tabla (si unidades>0),
				// o hemos alcanzado el indice -1 de la tabla y seguimos
				// buscando
				// => NO HAY POSIBILIDAD DE BAJAR O SUBIR MAS, NOS PARAMOS AL
				// EXTREMO VISIBLE:
				// (indice_primera_linea ya est actualizado de todos modos)
			}

			refreshTablaCentral();
		}
	}

	/**
	 * A partir de un indice de un elemento de la tabla_articulos_completa
	 * VISIBLE, devuelve su posicion relativa con respecto a los solos elementos
	 * VISIBLES de la tabla (EN BASE 0!). <br/>
	 * Nota: esta funcin puede actualizar el numero_articulos_visibles si
	 * pedido en parametro <br/>
	 * <br/>
	 * Cuidado: la tabla debe tener como minimo 1 elemento visible: el
	 * parametro.
	 * 
	 * //@param indice_tabla_completa
	 **/
	private int getPosicionRelativa(int indice_elemento, boolean actualizarFull) {
		int resultado = 0;

		// Actualizacin parcial:
		if (actualizarFull == false) {
			for (int k = 0; k < indice_elemento; k++) {
				ArticuloVisible av = listaArticulosCompleta.get(k);
				if (av.esVisible() == true) {
					resultado++;
				}
			}
			resultado++; // 1 vez ms para llegar hasta el elemento buscado
		}
		// Actualizacin completa:
		else {
			numero_articulos_visibles = 0;
			for (int k = 0; k < listaArticulosCompleta.size(); k++) {
				ArticuloVisible av = listaArticulosCompleta.get(k);
				if (av.esVisible() == true) {
					numero_articulos_visibles++;
					if (k <= indice_elemento) {
						resultado++;
					}
				}
			}
		} // end if

		return resultado - 1;
	}

	/**
	 * Funcion accesoria para cargar los handlers de todos los elementos que lo
	 * necesiten
	 */
	@SuppressLint({ "ResourceAsColor", "ResourceAsColor" })
	private void cargar_handlers() {
		
		Ic_Lectora.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new IntentIntegrator((Activity) ctxt).initiateScan();
//				scanBarcode(Ic_Lectora);
			}
		});
		// HANDLERS:
		// Boton de SALIDA:
		boton_salir.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try {
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

					log.log("[-- 2451 --]" + e.toString(), 4);
					e.printStackTrace();
				} finally {
					setResult(RESULT_OK, intentPadre);
					finish();
				}
			}
		});

		// Boton de BUSQUEDA:
		boton_busqueda.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Deseleccionamos la linea actual:
				deseleccionarLineaParticular(indice_on_focus);

				// Construccin de la lista de los nombre de todos los
				// productos:
				ArrayList<String> lista_todos_nombres = new ArrayList<String>();

				for (ArticuloVisible articulo : listaArticulosCompleta) {
					if (articulo.esVisible() == true) {
						lista_todos_nombres.add(articulo.getDescripcion());
					}
				}

				// ////////////////////////
				// BOTON CANCELAR //
				// ////////////////////////
				View.OnClickListener listenerCancelar = new View.OnClickListener() {

					public void onClick(View v) {

						log.log("[-- 2484 --]"
								+ "Se presiono el boton cancelar", 0);
						edittextBusqueda = (EditText) dialogoBusqueda
								.findViewById(R.id.Z_DIALOG_editext);
						InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						mgr.hideSoftInputFromWindow(
								edittextBusqueda.getWindowToken(), 0);

						SystemClock.sleep(20);

						dialogoBusqueda.cancel();
					}
				};

				// //////////////////////
				// BOTON BUSCAR //
				// //////////////////////
				View.OnClickListener listenerBusqueda = new View.OnClickListener() {

					public void onClick(View v) {

						log.log("[-- 2504--]" + "Se presiono en boton buscar",
								2);
						// 1) Recuperamos el string buscado:
						String busqueda = dialogoBusqueda.get_busqueda();

						// 2) Corremos la busqueda en la base de datos:
						ArrayList<HashMap<Integer, Object>> lista_resultados = null;
						bdd = new BaseDatos(ctxt);
						try {
							lista_resultados = bdd.buscar(
									inventario_numero_en_curso, busqueda);
						} catch (ExceptionBDD e) {

							log.log("[-- 2517 --]" + e.toString(), 4);
							e.printStackTrace();

							if (e.getCodigo() == ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED) {
								Toast.makeText(ctxt,
										"El proceso de busqueda ha fracasado",
										Toast.LENGTH_LONG).show();
								log.log("[-- 2523 --]"
										+ "El proceso de busqueda ha fracasado",
										3);
								return;
							} else if (e.getCodigo() == ExceptionBDD.ERROR_TOO_MANY_RESULTS) {
								Toast.makeText(
										ctxt,
										"La busqueda devolvio demasiado articulos, por favor afine",
										Toast.LENGTH_LONG).show();
								log.log("[-- 2531 --]"
										+ "La busqueda devolvio demasiado articulos, por favor afine",
										3);
								return;

							} else if (e.getCodigo() == ExceptionBDD.ERROR_TIPO_SELECT) {
								Toast.makeText(
										ctxt,
										"El proceso de busqueda ha fracasado con el siguiente error: "
												+ e.toString(),
										Toast.LENGTH_LONG).show();
								log.log("[-- 2541 --]"
										+ "El proceso de busqueda ha fracasado con el siguiente error: ",
										3);
								return;

							}
						}

						// 3) Escondemos el teclado virtual:
						edittextBusqueda = (EditText) dialogoBusqueda
								.findViewById(R.id.Z_DIALOG_editext);
						InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						mgr.hideSoftInputFromWindow(
								edittextBusqueda.getWindowToken(), 0);

						// 4) Cerrar el menu anterior:
						dialogoBusqueda.cancel();

						// 5) Mostramos un nuevo dialog con solamente las
						// respuestas:
						/*
						 * View.OnLongClickListener listenerEleccionRespuesta =
						 * new View.OnLongClickListener() {
						 * 
						 * public boolean onLongClick(View v) {
						 * //edittextBusqueda = (EditText)
						 * dialogoBusqueda.findViewById(R.id.Z_DIALOG_editext);
						 * //InputMethodManager mgr = (InputMethodManager)
						 * getSystemService(Context.INPUT_METHOD_SERVICE);
						 * //mgr.
						 * hideSoftInputFromWindow(edittextBusqueda.getWindowToken
						 * (), 0);
						 * 
						 * //SystemClock.sleep(20);
						 * 
						 * articulo_resultado_busqueda =
						 * dialogoResultados.get_codigos_articulo_seleccionado
						 * ();
						 * 
						 * int indice =
						 * get_indice_con_articulos(articulo_resultado_busqueda
						 * ); indice_on_focus = indice; // Segun el indice
						 * devuelto, hacemos: //if (indice >= 0) { //
						 * seleccionarMostrarIndiceArticulo(indice); //} else {
						 * // deseleccionarLineaParticular(indice_on_focus); //
						 * // showSimpleDialogTimer( // "Articulo oculto", //
						 * "El resultado de su bsqueda es un artculo bloqueado por un filtro!"
						 * , // 5000 // ).show(); //}
						 * 
						 * dialogoResultados.dismiss();
						 * 
						 * return true; } };
						 */

						View.OnClickListener listenerEleccionRespuesta = new View.OnClickListener() {

							public void onClick(View v) {

								log.log("[-- 2598 --]"
										+ "Se presiono listener Eleccion Respuesta ",
										0);
								LinearLayout tableR = (LinearLayout) v;
								articulo_resultado_busqueda = dialogoResultados
										.get_codigos_articulo_seleccionado();
								int indice = get_indice_con_articulos(articulo_resultado_busqueda);

								if (indice >= 0
										&& indice == respuestaSeleccionada) {
									indice_on_focus = indice;
									dialogoResultados.dismiss();
								} else if (indice < 0
										&& indice == respuestaSeleccionada) {
									indice_on_focus = -1;
									dialogoResultados.dismiss();
								} else {
									respuestaSeleccionada = indice;

									LinearLayout padre = (LinearLayout) tableR
											.getParent();
									for (int k = 0; k < padre.getChildCount(); k++) {
										LinearLayout tr = (LinearLayout) padre
												.getChildAt(k);
										tr.setBackgroundColor(getResources()
												.getColor(R.color.verde_oscuro));
									}
									tableR.setBackgroundColor(getResources()
											.getColor(R.color.anaranjado_oscuro));
								}
							}
						};

						View.OnClickListener listenerCancel = new View.OnClickListener() {

							public void onClick(View v) {
								dialogoResultados.cancel();

								dialogoBusqueda.show();

								/*
								 * edittextBusqueda = (EditText)
								 * dialogoBusqueda.
								 * findViewById(R.id.Z_DIALOG_editext);
								 * InputMethodManager mgr = (InputMethodManager)
								 * getSystemService
								 * (Context.INPUT_METHOD_SERVICE);
								 * mgr.showSoftInput(edittextBusqueda,
								 * InputMethodManager.SHOW_FORCED);
								 */
							}
						};

						dialogoResultados = new DialogPersoComplexResultados(
								ctxt, lista_resultados,
								listenerEleccionRespuesta, listenerCancel);
						dialogoResultados
								.setOnShowListener(new DialogInterface.OnShowListener() {

									public void onShow(DialogInterface dialog) {
										fueCanceladoDialogoResultados = false;
									}
								});
						dialogoResultados
								.setOnDismissListener(new OnDismissListener() {

									public void onDismiss(DialogInterface dialog) {

										log.log("[-- 2665 --]"
												+ "Dialogo Respuesta  Activado",
												2);
										respuestaSeleccionada = -99;
										if (fueCanceladoDialogoResultados == false) {
											// Segun el indice devuelto,
											// hacemos:
											int indice_nuevo = indice_on_focus;

											if (indice_on_focus >= 0) {
												seleccionarMostrarIndiceArticulo(indice_nuevo);
											} else {
												showSimpleDialogTimer(
														"Articulo oculto",
														"El resultado de su busqueda es un articulo bloqueado por un filtro!",
														5000).show();
											}
										}
									}
								});
						dialogoResultados
								.setOnCancelListener(new OnCancelListener() {

									public void onCancel(DialogInterface dialog) {

										log.log("[-- 2689--]"
												+ "Respuesta Dialogo Cancelada",
												2);
										fueCanceladoDialogoResultados = true;
									}
								});
						dialogoResultados.show();
					}
				};

				dialogoBusqueda = new DialogPersoComplexBusqueda(
						ctxt,
						"Buscar un articulo",
						"Esta herramienta de busqueda le permitira encontrar un articulo en particular. \n\n"
								+ "Por favor, ingrese el nombre del articulo deseado en el campo situado mas abajo. \n\n",
						inventario_numero_en_curso, listenerBusqueda,
						listenerCancelar);
				dialogoBusqueda
						.setOnShowListener(new DialogInterface.OnShowListener() {

							public void onShow(DialogInterface dialog) {
								log.log("[-- 2709--]"
										+ "Se muestra el dialogo de busqueda",
										0);
								edittextBusqueda = (EditText) dialogoBusqueda
										.findViewById(R.id.Z_DIALOG_editext);
								InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								mgr.showSoftInput(edittextBusqueda,
										InputMethodManager.SHOW_FORCED);
							}
						});
				dialogoBusqueda.show();

			}
		});

		boton_busqueda.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, @NonNull MotionEvent event) {

				log.log("[-- 2726 --]" + "Se presiona boton busqueda", 4);
				ImageView imgV = (ImageView) v;

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					imgV.setBackgroundColor(getResources().getColor(
							R.color.amarillo_oscuro_mas));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					imgV.setBackgroundColor(getResources().getColor(
							android.R.color.transparent));
				}
				return false;
			}
		});

		// Click on boton lectora (activacion de modo +1):
		boton_lectora.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				log.log("[-- 2744 --]"
						+ "Se presiona un clic largo sobre la lectora", 0);
				ImageView imageV = (ImageView) v;
				if (modo_mas_1 == 0) {
					modo_mas_1 = 1;
					log.log("[-- 2478 --]" + "se activa el modo mas 1", 2);
					imageV.setBackgroundColor(getResources().getColor(
							R.color.anaranjado_oscuro));
				} else {
					log.log("[-- 2752 --]" + "se desactiva el modo mas 1", 2);
					modo_mas_1 = 0;
					imageV.setBackgroundColor(android.R.color.transparent);
				}
				return true;
			}
		});

		// Touch on flecha:
		flecha_arriba.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, @NonNull MotionEvent event) {
				ImageView imgV = (ImageView) v;

				log.log("[-- 2766 --]" + "Se presiono flecha arriba", 0);
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					imgV.setImageDrawable(getResources().getDrawable(
							R.drawable.boton_up_select));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					imgV.setImageDrawable(getResources().getDrawable(
							R.drawable.boton_up));
				}
				return false;
			}
		});

		flecha_arriba.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				moverTablaArticulos(-1);
			}
		});

		flecha_abajo.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, @NonNull MotionEvent event) {
				ImageView imgV = (ImageView) v;
				log.log("[-- 2789 --]" + "Se presiono flecha abajo", 0);
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					imgV.setImageDrawable(getResources().getDrawable(
							R.drawable.boton_down_select));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					imgV.setImageDrawable(getResources().getDrawable(
							R.drawable.boton_down));
				}
				return false;
			}
		});

		flecha_abajo.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				moverTablaArticulos(1);
			}
		});

		// Touch on asensor:
		asensor_layout.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, @NonNull MotionEvent event) {

				log.log("[-- 2813 --]" + "Se produjo un ontouch, el scroll", 0);
				// Caso cuando el dedo toca la pantalla:
				if (event.getAction() == MotionEvent.ACTION_DOWN
						&& dedoEnContacto == false) {
					dedoEnContacto = true;
					dedoInicialY = event.getY();
				}

				// Caso de soltar el touch:
				else if (event.getAction() == MotionEvent.ACTION_UP) {
					dedoEnContacto = false;
				}

				else if (event.getAction() == MotionEvent.ACTION_MOVE
						&& dedoEnContacto == true) {
					dedoFinalY = event.getY();
					float deltaY = dedoFinalY;

					refreshAsensor(deltaY);

					SystemClock.sleep(20);
				}
				return true;
			}
		});

		// Ordenar por C�DIGO:
		encabezado_codigo.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {

				log.log("[-- 2844 --]" + "Se ordena por codigo", 0);
				bufferLectoraCB = "";
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					view.setBackgroundColor(getResources().getColor(
							R.color.anaranjado_verde));
					deseleccionarLineaParticular(indice_on_focus);
				}
				return false;
			}
		});

		encabezado_codigo.setOnClickListener(new View.OnClickListener() {

			public void onClick(@NonNull View view) {

				view.setBackgroundColor(getResources().getColor(
						R.color.verde_claro));

				if (columna_ordonante != 0) {
					Collections.sort(listaArticulosCompleta,
							Articulo.ORDEN_CODIGO);
				} else {
					Collections.reverse(listaArticulosCompleta);
				}
				columna_ordonante = 0;
				indice_primera_linea = 0;
				refreshTablaCentral();
			}
		});

		encabezado_codigo
				.setOnLongClickListener(new View.OnLongClickListener() {

					public boolean onLongClick(View v) {

						log.log("[-- 2879 --]"
								+ "Se filtra por sector, onclick largo sobre codigo",
								0);
						showMenuFiltro(ParametrosInventario.filtro_sector);
						encabezado_codigo.setBackgroundColor(getResources()
								.getColor(R.color.verde_claro));
						return true;
					}
				});

		// Ordenar por DESCRIPCION:
		encabezado_descripcion.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
				bufferLectoraCB = "";
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					view.setBackgroundColor(getResources().getColor(
							R.color.anaranjado_verde));
					deseleccionarLineaParticular(indice_on_focus);
				}
				return false;
			}
		});

		encabezado_descripcion.setOnClickListener(new View.OnClickListener() {

			public void onClick(@NonNull View view) {
				view.setBackgroundColor(getResources().getColor(
						R.color.verde_claro));

				if (columna_ordonante != 1) {
					Collections
							.sort(listaArticulosCompleta, Articulo.ORDEN_NOM);
				} else {
					Collections.reverse(listaArticulosCompleta);
				}
				columna_ordonante = 1;
				indice_primera_linea = 0;
				refreshTablaCentral();
			}
		});

		// Ordenar por PRECIO DE VENTA:
		encabezado_precio_venta.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {

				log.log("[-- 2925 --]"
						+ "Se presioan ordenar encabezado_precio_venta", 0);
				bufferLectoraCB = "";
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					view.setBackgroundColor(getResources().getColor(
							R.color.anaranjado_verde));
					deseleccionarLineaParticular(indice_on_focus);
				}
				return false;
			}
		});

		encabezado_precio_venta.setOnClickListener(new View.OnClickListener() {

			public void onClick(@NonNull View view) {

				log.log("[-- 2940 --]" + "Se presiona encabezado precio venta",
						0);
				view.setBackgroundColor(getResources().getColor(
						R.color.verde_claro));

				if (columna_ordonante != 2) {
					Collections.sort(listaArticulosCompleta,
							Articulo.ORDEN_PRECIO_VENTA);
				} else {
					Collections.reverse(listaArticulosCompleta);
				}
				columna_ordonante = 2;
				indice_primera_linea = 0;
				refreshTablaCentral();
			}
		});

		encabezado_precio_venta
				.setOnLongClickListener(new View.OnLongClickListener() {

					public boolean onLongClick(View v) {

						log.log("[-- 2962 --]"
								+ "Se presiona un clic largo para abrir el menu de filtro, encabezado precio venta",
								0);
						showMenuFiltro(ParametrosInventario.filtro_precio);
						encabezado_precio_venta
								.setBackgroundColor(getResources().getColor(
										R.color.verde_claro));
						return true;
					}
				});

		// Ordenar por CANTIDADES:
		encabezado_cantidad.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {

				log.log("[-- 2976 --]" + "Se presiona ordenar por cantidad", 0);
				bufferLectoraCB = "";
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					view.setBackgroundColor(getResources().getColor(
							R.color.anaranjado_verde));
					deseleccionarLineaParticular(indice_on_focus);
				}
				return false;
			}
		});

		encabezado_cantidad.setOnClickListener(new View.OnClickListener() {

			public void onClick(@NonNull View view) {

				log.log("[-- 2991 --]"
						+ "Se presiono cantidad Ordena por cantida", 0);
				view.setBackgroundColor(getResources().getColor(
						R.color.verde_claro));

				if (columna_ordonante != 3) {
					Collections.sort(listaArticulosCompleta,
							Articulo.ORDEN_CANTIDAD);
				} else {
					Collections.reverse(listaArticulosCompleta);
				}
				columna_ordonante = 3;
				indice_primera_linea = 0;
				refreshTablaCentral();
			}
		});

		encabezado_cantidad
				.setOnLongClickListener(new View.OnLongClickListener() {

					public boolean onLongClick(View v) {
						log.log("[-- 3011 --]"
								+ "Se presiono un clic largo en cantidad, se abre el menu de filtro",
								0);
						deseleccionarLineaParticular(indice_on_focus);
						showMenuFiltro(ParametrosInventario.filtro_inventario);
						encabezado_cantidad.setBackgroundColor(getResources()
								.getColor(R.color.verde_claro));
						return true;
					}
				});
	}

	/**
	 * Creamos el thread que va mostrar y destruir el menu
	 * 
	 * @author GuillermoR
	 * 
	 */
	protected class ShowDialogTimer extends AsyncTask<Context, Integer, String> {

		@Nullable
        protected String doInBackground(Context... arg0) {
			SystemClock.sleep(3000);
			return null;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				alert.dismiss();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Funcin para buscar el indice de un articulo en la lista por codigo y
	 * sector
	 * <p>
	 * Iteramos la lista hasta encontrar la coincidencia
	 * 
	 * @param hashmap
	 *            : contiene el codigo y sector de un articulo
	 * @return Devuelve el indice de el articulo cuyo codigo y sector se pasan
	 *         en el parametro
	 */
	private int get_indice_con_articulos(@NonNull HashMap<Integer, Integer> hashmap) {
		int posicion = -1;
		int index = 0;

		for (ArticuloVisible av : listaArticulosCompleta) {
			if (av.esVisible() == true
					&& av.getCodigo() == hashmap
							.get(ParametrosInventario.clave_art_codigo)
					&& av.getSector() == hashmap
							.get(ParametrosInventario.clave_art_sector)) {
				posicion = index;
				break;
			}
			index++;
		}

		if (posicion < 0) {
			return -1;
		} else {
			return posicion;
		}
	}

		/**
	 * Funcion que se llama luego de que se levanta la tecla.Esto procesa todos
	 * los eventos de teclado y codigo de barra.
	 * 
	 */

	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {

		log.log("[-- 3108 --]" + "Valor de key: " + keyCode, 3);
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
			bufferLectoraCB = "";
			moverTablaArticulos(1);
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
			bufferLectoraCB = "";
			moverTablaArticulos(-1);
		} else if (indice_on_focus < 0) { // Si ninguna linea tiene el focus
			char car = event.getNumber();
			if (Character.isDigit(car) == true) {
				bufferLectoraCB += String.valueOf(car);
			} else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				processArticuloConCB(bufferLectoraCB, true);
				bufferLectoraCB = "";
			} else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				bufferLectoraCB = "";
				deseleccionarLineaParticular(indice_on_focus);
				setResult(RESULT_OK, intentPadre);
				finish();
			}
		} else if (indice_on_focus >= 0) { // Si una linea tiene foco
			// bufferLectoraCB = "";

			char car = event.getNumber();
			if (Character.isDigit(car) == true) {
				// Toast.makeText(ctxt, String.valueOf(car),
				// Toast.LENGTH_SHORT).show();
				if (bufferLectoraCB.length() <= 0) {
					valor_antes_modificar = listaArticulosCompleta.get(
							indice_on_focus).getCantidad();
				}
				bufferLectoraCB += String.valueOf(car);
			} else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				bufferLectoraCB = "";
				deseleccionarLineaParticular(indice_on_focus);
			} else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				if (bufferLectoraCB.length() <= 0) {
					deseleccionarLineaParticular(indice_on_focus);
				} else if (bufferLectoraCB.length() > ParametrosInventario.TAMANO_MAX_CANTIDAD) {
					deseleccionarLineaParticular(indice_on_focus, false);
				} else {
					deseleccionarLineaParticular(indice_on_focus);
					// processArticuloConCB(bufferLectoraCB, true);
					bufferLectoraCB = "";
				}
				bufferLectoraCB = "";
			} else {
				bufferLectoraCB = "";
				super.onKeyUp(keyCode, null);
			}
		}
		return true;
	}

	/**
	 * Procesa el CB leido para buscarlo en la lista o aadirlo si no esta o
	 * crear uno nuevo si no existe en las referencias
	 * <p>
	 * Al leer un cdigo de barras, buscamos el articulo en el listado de los
	 * articulos del inventario, es el caso cuando un articulo ya estuvo
	 * cargado, y vuelve a pasar por la lectora...
	 * </p>
	 * <p>
	 * Si todava no est el articulo en la tabla, lo agregamos.
	 * 
	 * <p>
	 * 1 Buscamos el codigo de barras en la tabla de los articulos con su
	 * codigo de barra
	 * <p>
	 * 2 Si el articulo no ha sido escaneado todava, entonces agregamos el
	 * articulo que no est en la tabla
	 * <p>
	 * &nbsp; &nbsp;2.1 Vemos si el codigo de barras referencia un articulo
	 * conocido en la tabla de los articulos
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.1.1 Agregamos este articulo en el listado de
	 * los articulos
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.1.2 Agregar este articulo a la base, tabla
	 * ARTICULOS
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.1.3 Refrescar el encabezado
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.1.4 Enfocamos el articulo (es el ltimo dado
	 * que no lo hemos encontrado ya en la lista)
	 * <p>
	 * &nbsp; &nbsp;2.2 Si el articulo es nuevo / desconocido
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.2.1 Abrimos un dialogo para pedir nombre del
	 * articulo nuevo
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.2.2 Si se acepta la creacion -> Crear
	 * articulo visible nuevo no existnte en las referencias de la BDD
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.2.3 Modificar el nombre/descripcin del
	 * artculo
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.2.4 Agregamos este articulo en el listado de
	 * los articulos
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.2.5 Agregar este articulo a las tablas de
	 * referncia y articulos
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.2.6 Refrescamos el encabezado
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;2.2.7 Enfocamos el articulo (es el ltimo dado
	 * que no lo hemos encontrado en la lista)
	 * <p>
	 * 3 Si el articulo ya existe
	 * <p>
	 * &nbsp; &nbsp;3.1 Si hemos leido con la lectora en modo NO "+1", abrimos
	 * abrimos el cartel "MasMenosModif"
	 * <p>
	 * &nbsp; &nbsp;3.2 Si hemos leido con la lectora en modo "+1", agregamos +1
	 * a la cantidad
	 * 
	 * 
	 * @param cb
	 * @param from_lectoraCB
	 */
	private void processArticuloConCB(@NonNull final String cb, boolean from_lectoraCB) {
		// Mostrar el codigo de barra en pantalla:
		// Toast.makeText(ctxt, cb, Toast.LENGTH_SHORT).show();
		loadingBar.setVisibility(View.VISIBLE);
		loadingBar.bringToFront();

		boolean condicionBalanza = ParametrosInventario.balanza;
		Articulo artic = null;
		BaseDatos bdd = new BaseDatos(ctxt);

		System.out.println("::: PaginaInventario 3214");
		// Se considera que aqui el orden de clasificacion puede ser violado,
		// desactivamos el eventual
		// ordenamiento corriente:
		columna_ordonante = -1;

		/*
		 * _________ \ TEORIA: |_________/
		 *
		 * Al leer un cdigo de barras, buscamos el articulo en el listado de
		 * los articulos de la compra, es el caso cuando un articulo ya estuvo
		 * cargado, y vuelve a pasar por la lectora...
		 *
		 * Si todava no est se lo advierte que no pertenece al inventario o no est en los maestros
		 * pero no se lo agrega
		 */

		int indice_articulo_encontrado = -1;

		// Buscamos el codigo de barras en la tabla de los articulos con su
		// codigo de barra:
		if(condicionBalanza==true){
			int sSubCadena = Integer.parseInt(cb.substring(0,2));
			if(sSubCadena == 20){
				String buscarBD = cb.substring(2,7);
				for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
					if (tablaCodigosUnArticulo.contains((String) buscarBD)) {
						// Obtenemos el indice de la linea seleccionada:
						indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
								.indexOf(tablaCodigosUnArticulo);
						break;
					} // end if
				} // end for
			}else{
				for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
					if (tablaCodigosUnArticulo.contains((String) cb)) {
						// Obtenemos el indice de la linea seleccionada:
						indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
								.indexOf(tablaCodigosUnArticulo);
						break;
					} // end if
				} // end for
			}

		}else{

			for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
				if (tablaCodigosUnArticulo.contains((String) cb)) {
					// Obtenemos el indice de la linea seleccionada:
					indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
							.indexOf(tablaCodigosUnArticulo);
					break;
				} // end if
			} // end for
		}



		// Si el articulo no ha sido escaneado todava:
		if (indice_articulo_encontrado < 0) {
			// Entonces agregamos el articulo que no est en la tabla:

			// Vemos si el codigo de barras referencia un articulo conocido
			// en la tabla de los articulos:

if(condicionBalanza==true){

	int sSubCadena = Integer.parseInt(cb.substring(0,2));
	if(sSubCadena == 20){
		String buscarBD = cb.substring(2,7);
		ArticuloVisible a = null;
		//int valorCortado1 = Integer.parseInt(cb.substring(0, 2));
		String valorCortado2 = cb.substring(2,7);
		System.out.println("::: PaginaInventarioDinamico esta dentro de balanza");
		System.out.println("::: VALOR " + valorCortado2);
		boolean esta = bdd.FijarsesiEsta_o_nelArticulo(valorCortado2);
		System.out.println("::: PaginaInventario 3266 "+ esta);
		if (!esta) {
			loadingBar.setVisibility(View.GONE);
			String titulo = "Articulo no Correspondiente";
			String mensaje  = "El articulo solicitado no se encuentra en el inventario";
			showSimpleDialogOK(titulo, mensaje).show();
		} else {
			loadingBar.setVisibility(View.GONE);
			String titulo = "Articulo no Correspondiente";
			String mensaje  = "El articulo se encuentra en los maestros, pero no en el inventario. No se agregara dicho articulo";
			showSimpleDialogOK(titulo, mensaje).show();


		}

	}else{
		System.out.println("::: PaginaInventarioDinamico 3887 que antes 2");
	//	artic = bdd.selectReferenciaConCodigoBarra(cb);
		ArticuloVisible a = null;
		boolean esta = bdd.FijarsesiEsta_o_nelArticulo(cb);
		if (!esta) {
			loadingBar.setVisibility(View.GONE);
			String titulo = "Articulo no Correspondiente";
			String mensaje  = "El articulo solicitado no se encuentra en el inventario";
			showSimpleDialogOK(titulo, mensaje).show();
		} else {
			loadingBar.setVisibility(View.GONE);
			String titulo = "Articulo no Correspondiente";
			String mensaje  = "El articulo se encuentra en los maestros, pero no en el inventario. No se agregara dicho articulo";
			showSimpleDialogOK(titulo, mensaje).show();


		}
	}
}



		}

		// Si el articulo ya existe:
		// Enfocamos y sumamos 1 unidad
		else {
			// Estratgia: (3 casos)
			// 1) Si hemos leido el CB con boton "+Nuevo", abrimos el cartel
			// "MasMenosModif"
			// 2) Si hemos leido con la lectora en modo NO "+1", abrimos abrimos
			// el cartel "MasMenosModif"
			// 3) Si hemos leido con la lectora en modo "+1", agregamos +1 a la
			// cantidad sin dar foco
			//
			loadingBar.setVisibility(View.GONE);
			ArticuloVisible a_v = listaArticulosCompleta
					.get(indice_articulo_encontrado);
			if (a_v.esVisible() == false) {
				showSimpleDialogOK(
						"Articulo Oculto",
						"El articulo a inventariar esta "
								+ "oculto por un filtro").show();
			} else {
	System.out.println("::: PaginaInventario 3357 cb "+ cb);
			//float cantidad_a_sumar = 0;

				// En el caso 3, agregamos + 1 a la cantidad de
				// listaArticuloCompleta, damos y sacamos el foco:

				if (modo_mas_1 == 1) {

					if(condicionBalanza==true){

						if(a_v.getBalanza()==8 && a_v.getDecimales()==3){
							if (a_v.getCantidad() >= 0) {
								String sumar1 = cb.substring(7,9);
								String sumar2 = cb.substring(9,12);
								String numSumar = sumar1 +"."+ sumar2;
								float sumarD = Float.parseFloat(numSumar);
								a_v.setCantidad(a_v.getCantidad() + sumarD);
							} else if(a_v.getCantidad() == -1){
								String sumar1 = cb.substring(7,9);
								String sumar2 = cb.substring(9,12);
								String numSumar = sumar1 +"."+ sumar2;
								float sumarD = Float.parseFloat(numSumar);
								a_v.setCantidad(sumarD);
							}else{
								a_v.setCantidad(1);
							}
							seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
							deseleccionarLineaParticular(indice_articulo_encontrado);
						}if(a_v.getBalanza()==8 && a_v.getDecimales()!=3){
							if (a_v.getCantidad() >= 0) {
								float sumar = Integer.parseInt(cb.substring(7,12));
								a_v.setCantidad(a_v.getCantidad() + sumar);
							} else if(a_v.getCantidad() == -1){
								float sumar = Integer.parseInt(cb.substring(7,12));
								a_v.setCantidad(sumar);
							} else {
								a_v.setCantidad(1);
							}
							seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
							deseleccionarLineaParticular(indice_articulo_encontrado);
						}else if(a_v.getBalanza()!=8){
							if (a_v.getCantidad() >= 0) {
								a_v.setCantidad(a_v.getCantidad() + 1);
							} else {
								a_v.setCantidad(1);
							}
							seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
							deseleccionarLineaParticular(indice_articulo_encontrado);
						}
					}else{
						if (a_v.getCantidad() >= 0) {
							a_v.setCantidad(a_v.getCantidad() + 1);
						} else {
							a_v.setCantidad(1);
						}
						seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
						deseleccionarLineaParticular(indice_articulo_encontrado);
					}
				}
				// En los casos 1 y 2 damos el foco a la linea:
				else {
					seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
				}

/*				if (modo_mas_1 == 1) {

					if (a_v.getCantidad() >= 0) {
						//a_v.setCantidad(a_v.getCantidad() + 1);
						a_v.setCantidad(a_v.getCantidad() + cantidad_a_sumar);
					} else {
						a_v.setCantidad(1);
					}
					seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
					deseleccionarLineaParticular(indice_articulo_encontrado);
				}
				// En los casos 1 y 2 damos el foco a la linea:
				else {
					seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
				}
*/
			}
		}
	} // end funcion

//	public void scanBarcode(View button) {
//		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//		startActivityForResult(intent, SCAN_BARCODE);


//		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//		intent.setPackage("com.google.zxing.client.android");
//		intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE",
//				"ONE_D_MODE");
//		startActivityForResult(intent, SCAN_BARCODE);
//	}





//	public void onActivityResult(int requestCode, int resultCode,Intent intent) {
//		if (requestCode == SCAN_BARCODE) {
//			if (resultCode == RESULT_OK) {
//				String contents = intent.getStringExtra("SCAN_RESULT");
//				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//				processArticuloConCB(contents.trim(), true);processArticuloConCB(contents.trim(), true);
//			} else if (resultCode == RESULT_CANCELED) {
//				// Handle cancel
//			}
//		}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null) {
			if (result.getContents() == null) {
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}


//		if (requestCode == SCAN_BARCODE) {
//			if (resultCode == RESULT_OK) {
//				String contents = intent.getStringExtra("SCAN_RESULT");
//
//				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//
//				processArticuloConCB(contents.trim(), true);processArticuloConCB(contents.trim(), true);
//								// ... Usar o mostrar el cdigo de barras
//			} else if (resultCode == RESULT_CANCELED) {
//				// El escaneo ha fallado. Volver a probar
//				// ...
//			}
//		}
//	}

} // END OF CLASS

