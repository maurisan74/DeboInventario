package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Activity que permite ver los detalles de un artculo seleccionado. Es llamada
 * desde la clase PaginaInventario.java.
 * 
 * @author GuillermoR
 * 
 */
public class DetallesArticulo extends Activity implements DialogPersoSimple {

	/**
	 * Variable para almacentar informacin de contexto de la activity,almacena
	 * la instancia de esta activity
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Instancia de un objeto BaseDatos para manejar las sentencias contra la BD
	 */
	private BaseDatos bdd;
	/**
	 * Boton que cierra lal activity y regresa a la pagina inventario
	 * correspondiente
	 */
	private Button boton_volver;
	/**
	 * Variable para almacenar el artculo cuyos detalles se estan viendo
	 */
	@Nullable
    private Articulo articulo_on_focus;
	/**
	 * Variable para moestrar el nombre
	 */
	private TextView textview_nombre;
	/**
	 * Variable para mostrar el sector del articulo
	 */
	private TextView textview_sector;
	/**
	 * Variable para mostrar el codigo del articulo
	 */
	private TextView textview_codigo;
	/**
	 * Variable para mostrar los codigos de barra concatenados?
	 */
	private TextView textview_codbar;
	/**
	 * Variable para mostrar el precio del articulo
	 */
	private TextView textview_precio;
	/**
	 * Variable para mostrar el costo del articulo
	 */
	private TextView textview_costo;
	/**
	 * Variable para mostrar la cantidad actial
	 */
	private TextView textview_cantidad;
	
	
	
	private TextView textview_existencia;
	
	
	
	/**
	 * Variable que muestra una posible imagen o foto del articlo
	 */

	/**
	 * Variable donde se almacena el nombre de la imagen
	 */
	@NonNull
    private String nombreImagen = "";
	/**
	 * Variable auxiliar donde se almacena la foto tomada al articulo en caso de
	 * haberlo hecho
	 */
	@Nullable
    private byte[] ba = null;
	/**
	 * Dialogo para confirmar la satisfaccin de la foto
	 */
	private DialogPersoComplexFotoSiNo dialogo;

	/**
	 * Variable para almacenar el intent llamo a esta activity
	 */
	private Intent intentPadre;
	/**
	 * Variable auxiliar para almacenat el sector del articulo en cuestin
	 */
	private int sector;
	/**
	 * Variable auxiliar para almacenar el codigo del articulo en cuestin
	 */
	private int codigo;
	/**
	 * Variable auxiliar que almacena el numero de inventario del articulo
	 */
	private int inventario;

	/**
	 * Inicializacion de UI y recuperacion de datos pasados por extras
	 * <p>
	 * 1 Recuperamos los extras
	 * <p>
	 * 2 Recuperamos la lista de todos los datos que mostrar en pantalla
	 * <p>
	 * 3 Recuperamos los elementos de la parte grfica
	 * <p>
	 * 4 Cargamos los handlers
	 * <p>
	 * 5 Configuracin del valor
	 * <p>
	 * 6 Recuperamos la foto formateando el nombre de la imagen
	 * <p>
	 * 7 Chequeamos si ya tenemos una foto en la tablet para mostrar, sino
	 * buscamos en la red
	 */

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_detallesarticulo);

		System.out.println("::: Entra en detalles articulo");
		
		final GestorLogEventos log = new GestorLogEventos();
		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("Inicia Detalle Articulo ", 2);

		intentPadre = getIntent();
		// BUNDLES:
		Bundle bundle = intentPadre.getExtras();

		// 1 Recuperamos los extras:
		sector = bundle.getInt(ParametrosInventario.extra_sector);
		codigo = bundle.getInt(ParametrosInventario.extra_codigo);
		inventario = bundle.getInt(ParametrosInventario.extra_inventario);
		// Parametros.PREF_URL_CONEXION_SERVIDOR =
		// "http://192.167.1.156/Dalvian/webservice.php";

		// BASE DE DATOS:
		bdd = new BaseDatos(ctxt);
		System.out.println("::: Entra en detalles articulo aqui 1");
		// 2 Recuperamos la lista de todos los datos que mostrar en pantalla:
		try {
			articulo_on_focus = bdd.selectArticuloConCodigos(sector, codigo,
					inventario);
			System.out.println("::: Entra en detalles articulo aqui 2");
		} catch (ExceptionBDD e) {

			log.log(e.toString(), 4);
			e.printStackTrace();
			showSimpleDialogOK("Error en el articulo", e.toString()).show();
		}

		// INTERFAZ:
		// 3 Recuperamos los elementos de la parte grfica:
		textview_nombre = (TextView) findViewById(R.id.DART_art_nombre);
		textview_sector = (TextView) findViewById(R.id.DART_art_sector);
		textview_codigo = (TextView) findViewById(R.id.DART_art_codigo);
		textview_codbar = (TextView) findViewById(R.id.DART_art_codbar);
		textview_precio = (TextView) findViewById(R.id.DART_art_precio);
		textview_costo = (TextView) findViewById(R.id.DART_art_costo);
		textview_cantidad = (TextView) findViewById(R.id.DART_art_cantidad);
		textview_existencia = (TextView) findViewById(R.id.DART_art_exisventa);

		boton_volver = (Button) findViewById(R.id.DART_boton_salir);

		// 4 Cargamos los handlers

		boton_volver.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// No hubo eliminacion del articulo
				
				log.log("Inicia Detalle Articulo ", 2);
				setResult(RESULT_OK, intentPadre);
				finish();
			}
		});

		// 5 Configuracin del valor:
		textview_nombre.setText(articulo_on_focus.getDescripcion());
		textview_sector.setText(String.valueOf(articulo_on_focus.getSector()));
		textview_codigo.setText(String.valueOf(articulo_on_focus.getCodigo()));
		textview_codbar.setText(articulo_on_focus.getCodigos_barras_string());
		textview_precio.setText(String.valueOf(articulo_on_focus
				.getPrecio_venta()));
		textview_costo.setText(String.valueOf(articulo_on_focus
				.getPrecio_costo()));
		textview_cantidad.setText(String.valueOf(articulo_on_focus
				.getCantidad()));
		String existencia;
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(ctxt);
		boolean mostrarExistencia = setting.getBoolean(ParametrosInventario.tablet_mostrar_existencia, ParametrosInventario.mostrar_existencia);
		if(mostrarExistencia) {
			existencia = String.valueOf(articulo_on_focus.getExis_venta());
		}else{
			existencia = ParametrosInventario.no_disponible;
		}
		textview_existencia.setText(existencia);
		System.out.println("::: Entra en detalles articulo aqui configura los valores");
//		textview_exis.setText(String.valueOf(articulo_on_focus.getExis_venta()));
	}

	/**
	 * Al presionar Back , se cierra la aplicacion
	 */

	public void onBackPressed() {
		finish();
	}

	/**
	 * Muestra un dialogo simple con la opcion de presionar Ok
	 */

	public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {

		GestorLogEventos log = new GestorLogEventos();
		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("titulo: " + titulo + ",\n mensaje: " + mensaje, 3);

		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(@NonNull DialogInterface dialog, int id) {
						dialog.dismiss();

						GestorLogEventos log = new GestorLogEventos();
						log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
						log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
						log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
						log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
						log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
						log.log("Acepto", 0);
					}
				});
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}

	/**
	 * Dialogo simple que muestra un mensaje, aparentemente no esta implementado
	 */

	@Nullable
    public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje,
                                            Class<?> clase) {
		return null;
	}

}
