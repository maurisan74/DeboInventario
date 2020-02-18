package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
 * Permite ver el detalle de un artculo de un inventario dinmico seleccionado.
 * Llamada desde PaginaInventarioDinamico.java. 
 * Pensada para en un futuro eliminar  el articulo del Inventario Dinmico actual.
 * @author GuillermoR
 *
 */
public class DetallesArticuloDinamico extends Activity implements DialogPersoSimple {

	/**
	 * Variable para almacenar los datos de contexto de la actividad
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Variable de instancia para manejar las conexiones y operaciones con la BD
	 */
	private BaseDatos bdd;
	/**
	 * Botn que permite volver a la activity anterior cerrando la actual
	 */
	private Button boton_volver;
	/**
	 * Almacena los datos del articulo cuyos detalles se estan mostrando
	 */
	@Nullable
    private Articulo articulo_on_focus;
	/**
	 * Variable para mostrar el nombre del articulo
	 */
	private TextView textview_nombre; 
	/**
	 * Variable para mostrar el sector
	 */
	private TextView textview_sector; 
	/**
	 * Variable para mostrar el codigo del articulo
	 */
	private TextView textview_codigo;
	/**
	 * Variable que muestra los codigos de barra concatenados por comas
	 */
	private TextView textview_codbar; 
	/**
	 * Variable que muestra el precio del articulo
	 */
	private TextView textview_precio;
	/**
	 * Variable para moestrar el costo del articulo
	 */
	private TextView textview_costo;
	/**
	 * Variable para mostrar la cantidad actual inventariada
	 */
	private TextView textview_cantidad;

	/*123456*/
	private TextView textview_exis;
	/**
	 * Variable accesoria donde se almacena el nombre de la imagen
	 */	
	@NonNull
    private String nombreImagen = "";
	/**
	 * Arreglo de bytes  accesorio para leer la foto o imagen tomada
	 */
	@Nullable
    private byte[] ba = null;
	/**
	 * Dialogo para confirmar la correctitud de la foto
	 */
	private DialogPersoComplexFotoSiNo dialogo;
	/**
	 * Boton para permitir eliminar el articulo del inventario actual, temporalmente
	 * inhabilitado hasta que se apruebe esa mejora
	 */
	private Button botonEliminar;
	/**
	 * Dialogo que pregunta por la confirmacin del borrado del articulo del inventario
	 */
	private DialogPersoComplexSiNo dialogoContinuarBorrar;
	/**
	 * Variable para almacenar los datos del intent que llama a esta activity
	 */
	private Intent intentPadre;
	/**
	 * Variable acceesoria Para almacenar el sector del articulo actual
	 */
	private int sector;
	/**
	 * Variable accesoria para guardar el codigo del articulo
	 */
	private int codigo;
	/**
	 * Variable para almacenar el numero de inventario del articulo actual
	 */
	private int inventario;
	/**
	 * Variable para mostrar en caso que no se pueda abrir la cmara
	 */
	private ProgressDialog PopUps;
	private boolean estadoCamara;
	
	/**
	 * Se inicializa la UI y se cargan los handlers
	 * <p>1 Recuperamos los extras
	 * <p>2 Recuperamos la lista de todos los datos que mostrar en pantalla
	 * <p>3 Recuperamos los elementos de la parte grfica
	 * <p>4 Cargado de handlers sobre la imagen y botones
	 * <p>5 Configuracin del valor
	 * <p>6 Chequeamos si ya tenemos una foto en la tablet para mostrar,
	 * sino buscamos en la red
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_detallesarticulo_dinamico);

		System.out.println(":::llega para llamar a detalles articulos");

		final GestorLogEventos log = new GestorLogEventos();
		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 149 --]" + "DetalleArticuloDinamico", 2);
		
		
		intentPadre=getIntent();
		// BUNDLES:
			Bundle bundle = intentPadre.getExtras();
			
			//1 Recuperamos los extras:
				sector = bundle.getInt(ParametrosInventario.extra_sector);
				codigo = bundle.getInt(ParametrosInventario.extra_codigo);
				inventario = bundle.getInt(ParametrosInventario.extra_inventario);
				//Parametros.PREF_URL_CONEXION_SERVIDOR = "http://192.167.1.156/Dalvian/webservice.php";
				
		// BASE DE DATOS:
			bdd = new BaseDatos(ctxt);	
				
			//2 Recuperamos la lista de todos los datos que mostrar en pantalla:
				try {
					articulo_on_focus = bdd.selectArticuloConCodigos(sector, codigo, inventario);
				} catch (ExceptionBDD e) {
					e.printStackTrace();
					showSimpleDialogOK("Error en el articulo", e.toString()).show();
					
					log.log("[-- 172 --]" + e.toString(), 4);
				}
		
				
		// INTERFAZ:
			//3 Recuperamos los elementos de la parte grfica:
				textview_nombre = (TextView) findViewById(R.id.DART_art_nombre);
				textview_sector = (TextView) findViewById(R.id.DART_art_sector);
				textview_codigo = (TextView) findViewById(R.id.DART_art_codigo);
				textview_codbar = (TextView) findViewById(R.id.DART_art_codbar);
				textview_precio = (TextView) findViewById(R.id.DART_art_precio);
				textview_costo = (TextView) findViewById(R.id.DART_art_costo);
				textview_cantidad = (TextView) findViewById(R.id.DART_art_cantidad);
				textview_exis = (TextView) findViewById(R.id.DART_art_exisventa);
				
				
				boton_volver = (Button) findViewById(R.id.DART_boton_salir);
				
				
			//4 Cargado de handlers sobre la imagen y botones
				
				
			
					
				boton_volver.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						//No hubo eliminacion del articulo
						setResult(RESULT_OK,intentPadre);
						finish();
						
						log.log("[-- 203 --]" + "Volvi hacia atras", 0);
					}
				});
				
				
			//5 Configuracin del valor:
				textview_nombre.setText(articulo_on_focus.getDescripcion());
				textview_sector.setText(String.valueOf(articulo_on_focus.getSector()));
				textview_codigo.setText(String.valueOf(articulo_on_focus.getCodigo()));
				textview_codbar.setText(articulo_on_focus.getCodigos_barras_string());
				textview_precio.setText(String.valueOf(articulo_on_focus.getPrecio_venta()));
				textview_costo.setText(String.valueOf(articulo_on_focus.getPrecio_costo()));
				textview_cantidad.setText(String.valueOf(articulo_on_focus.getCantidad()));
				
				/*getDescripcion
				 * */
				double exis = articulo_on_focus.getExis_venta();
				double dep = articulo_on_focus.getExis_deposito();
				double resutado = exis + dep;

			//	textview_exis.setText(String.valueOf(resultado));

			String existencia;
			SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(ctxt);
			boolean mostrarExistencia = setting.getBoolean(ParametrosInventario.tablet_mostrar_existencia, ParametrosInventario.mostrar_existencia);
			if(mostrarExistencia) {
				existencia = String.valueOf(articulo_on_focus.getExis_venta() + articulo_on_focus.getExis_deposito());
			}else{
				existencia = ParametrosInventario.no_disponible;
			}
			textview_exis.setText(existencia);
				
				
				/*123456*/
			//	textview_exis.setText(String.valueOf(articulo_on_focus.getExis_venta()));
				
		
				
				/**
				 * Boton para eliminar articulos: en el caso de necesitar esta
				 * funcionalidad, descomentar esta parte del codigo, esto
				 * perimitira dar la posibilidad de eliminar el articulo actual
				 * del inventario.
				 */
//				botonEliminar= (Button) findViewById(R.id.DART_eliminar);
//				
//				botonEliminar.setOnClickListener(new View.OnClickListener() {
//					
//					
//					public void onClick(View v) {
//						/**
//						 * Generamos los listeners
//						 */
//						View.OnClickListener listenerPositivo=new View.OnClickListener() {
//							
//							
//							public void onClick(View v) {
//								
//								BaseDatos bdd=new BaseDatos(ctxt);
//								int resultado=ParametrosInventario.RETURN_ART_ELIM_FALLO;
//								try {
//									//Eliminamos los datos del articulo del inventario actual
//									bdd.borrarArcticuloInventario(sector,codigo,inventario);
//									//Pasamos los datos del articulo eliminado
//									intentPadre.putExtra(ParametrosInventario.extra_sector, sector);
//									intentPadre.putExtra(ParametrosInventario.extra_codigo, codigo);
//									intentPadre.putExtra(ParametrosInventario.extra_codBar, articulo_on_focus.getCodigos_barras_string());
//									resultado=ParametrosInventario.RETURN_ART_ELIM;
//									setResult(resultado, intentPadre);
//									dialogoContinuarBorrar.dismiss();
//									finish();
//								} catch (ExceptionBDD e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//									Toast.makeText(ctxt, "Error al intentar borrar el articulo",
//											Toast.LENGTH_LONG).show();
////									resultado=ParametrosInventario.RETURN_ART_ELIM_FALLO;
//									dialogoContinuarBorrar.dismiss();
//								}
//							}
//						};
//						
//						View.OnClickListener listenerNegativo=new View.OnClickListener() {
//							
//							
//							public void onClick(View v) {
//								//Cerramos el dialog
//								dialogoContinuarBorrar.dismiss();
//								
//							}
//						};
//						
//						dialogoContinuarBorrar=new DialogPersoComplexSiNo(ctxt, "Eliminar " +
//								"Artculo", "Esta seguro que desea eliminar el artculo del inventario?",
//								DialogPerso.ALERTAR, 
//								listenerPositivo, 
//								listenerNegativo);
//						
//						dialogoContinuarBorrar.show();
//						
//						
//					}
//				});
				/**
				 * Hasta aqui hay que descomentar
				 */
				
	}

	
	
	/**
	 * Al presionar back, volvemos cerrando la activity actual
	 */
	
	public void onBackPressed() {
		finish();
	}


	/**
	 * Muestra un dialogo con la opcion de presionar "Ok" para cerrarlo
	 */
	
	public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {
		
		final GestorLogEventos log = new GestorLogEventos();
		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 314 --]" + "titulo: " + titulo + "; \n mensaje: " + mensaje , 0);
		
		AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
		dialogoSimple.setCancelable(false)
				 	 .setTitle(titulo)
				 	 .setMessage(mensaje)
				 	 .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        		   	 public void onClick(@NonNull DialogInterface dialog, int id) {
	        		   	     dialog.dismiss();
	        		   	 }
    	   		 	 });
		AlertDialog alert = dialogoSimple.create();
		return alert;
	}

	/**
	 * No implementado
	 */
	
	@Nullable
    public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje, Class<?> clase) {
		return null;
	}






	
	
	
}
