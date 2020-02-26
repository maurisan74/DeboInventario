package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
/*
 * Dialogo personalizado preparado para realizar bsquedas.
 * @author GuillermoR
 *
 */
public class DialogPersoComplexBusqueda extends Dialog{


	/**
	 * Variable para almacenar la busqueda que se quiere realizar
	 */
	private EditText campoBusqueda = null;


	/*
	 * Constructor
	 * <p>1 Llamada al super
	 * <p>3 Cargamos el layout y main layout
	 * <p>4 Mensaje
	 * <p>5 Edittext
	 * <p>6 Botones y handlers
	 *
	 * @param context
	 * @param titulo
	 * @param mensaje
	 * @param numeroInventario datos para la busqueda
	 * @param listenerBuscar que hace con el boton buscar
	 * @param listenerCancelar que hace con el boton cancelar
	 */
	public DialogPersoComplexBusqueda (
			Context context,
			String titulo,
			String mensaje,
			int numeroInventario,
			View.OnClickListener listenerBuscar,
			View.OnClickListener listenerCancelar
	) {
		//1 Llamada al super
		super(context);
		final Activity owner = (Activity) context;

		//2 Construccin del ttulo:
		if (titulo.length() > 0) {
			super.setTitle(titulo);
		}


		//3 Cargamos el layout y main layout:
		super.setContentView(R.layout.z_dialogpersocomplexbusqueda);


		//4 Mensaje:
		TextView tv_mensaje = (TextView) super.findViewById(R.id.Z_DIALOG_mensaje);
		tv_mensaje.setText(mensaje);


		//5 Edittext:
		campoBusqueda = (EditText) super.findViewById(R.id.Z_DIALOG_editext);


		//6 Botones y handlers:
		Button boton_cancelar = (Button) super.findViewById(R.id.Z_DIALOG_boton_cancelar);
		ImageView boton_buscar = (ImageView) super.findViewById(R.id.Z_DIALOG_boton_buscar);

		final GestorLogEventos log = new GestorLogEventos();
		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 83 --]" + "Inicio de Dialog Person Comple Bsqueda", 2 );

		boton_cancelar.setOnClickListener(listenerCancelar);

		boton_buscar.setOnClickListener(listenerBuscar);
		boton_buscar.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				ImageView imageV = (ImageView)v;

				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					imageV.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_buscar_down));
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					imageV.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_buscar_up));
				}

				log.log("[-- 99 --]" + "Click en el boton buscar", 0 );

				return false;
			}
		});
	}



	public String get_busqueda() {
		return String.valueOf(campoBusqueda.getText());
	}

}

//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
///*
// * Dialogo personalizado preparado para realizar bsquedas.
// * @author GuillermoR
// *
// */
//public class DialogPersoComplexBusqueda extends Dialog{
//
//	/*
//	 * Variable para almacenar la busqueda que se quiere realizar
//	 */
//	@Nullable
//    private EditText campoBusqueda = null;
//
//	/*
//	 * Constructor
//	 * <p>1 Llamada al super
//	 * <p>3 Cargamos el layout y main layout
//	 * <p>4 Mensaje
//	 * <p>5 Edittext
//	 * <p>6 Botones y handlers
//	 *
//	 * @param context
//	 * @param titulo
//	 * @param mensaje
//	 * @param numeroInventario datos para la busqueda
//	 * @param listenerBuscar que hace con el boton buscar
//	 * @param listenerCancelar que hace con el boton cancelar
//	 */
//	@SuppressLint("ClickableViewAccessibility")
//	public DialogPersoComplexBusqueda (@NonNull Context context, @NonNull String titulo, String mensaje, int numeroInventario, View.OnClickListener listenerBuscar, View.OnClickListener listenerCancelar) {
//		//1 Llamada al super
//		super(context);
//		final Activity owner = (Activity) context;
//
//		//2 Construccin del ttulo:
//			if (titulo.length() > 0) super.setTitle(titulo);
//		//3 Cargamos el layout y main layout:
//			super.setContentView(R.layout.z_dialogpersocomplexbusqueda);
//		//4 Mensaje:
//			TextView tv_mensaje = (TextView) super.findViewById(R.id.Z_DIALOG_mensaje);
//			tv_mensaje.setText(mensaje);
//		//5 Edittext:
//			campoBusqueda = (EditText) super.findViewById(R.id.Z_DIALOG_editext);
//		//6 Botones y handlers:
//		    Button boton_cancelar = (Button) super.findViewById(R.id.Z_DIALOG_boton_cancelar);
//		    ImageView boton_buscar = (ImageView) super.findViewById(R.id.Z_DIALOG_boton_buscar);
//
//		    final GestorLogEventos log = new GestorLogEventos();
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 83 --]" + "Inicio de Dialog Person Comple Bsqueda", 2 );
//			boton_cancelar.setOnClickListener(listenerCancelar);
//			boton_buscar.setOnClickListener(listenerBuscar);
//			boton_buscar.setOnTouchListener(new View.OnTouchListener() {
//				public boolean onTouch(View v, @NonNull MotionEvent event) {
//					ImageView imageV = (ImageView)v;
//
//					if (event.getAction() == KeyEvent.ACTION_DOWN) {
//						imageV.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_buscar_down));
//					} else if (event.getAction() == KeyEvent.ACTION_UP) {
//						imageV.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_buscar_up));
//					}
//
//					log.log("[-- 99 --]" + "Click en el boton buscar", 0 );
//
//					return false;
//				}
//			});
//	}
//
//
//
//	@NonNull
//    public String get_busqueda() {
//		assert campoBusqueda != null;
//		return String.valueOf(campoBusqueda.getText());
//	}
//
//}
