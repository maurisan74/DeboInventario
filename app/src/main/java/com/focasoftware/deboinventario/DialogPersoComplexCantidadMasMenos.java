package com.focasoftware.deboinventario;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Dialogo personalizado que permite modificar la cantidad de un articulo, sumar
 * o restar sobre la actual
 * @author GuillermoR
 *
 */
public class DialogPersoComplexCantidadMasMenos extends Dialog {	
	
	/**
	 * Variable para almamcenar la cantidad incial
	 */
	private float cantidad = 0;
	
	
	
	/**
	 * Variable para almacenar la informaci�n de contexto de la actividad
	 */
	private Context ctxt;
	
	/*
	 * Variables para realizar identificacion y calcular balanza
	 */
	private int cod;
	private Articulo art;
	
	
	/**
	 * Constructor del dialogo que recibe la informaci�n a mostrar y arma la UI
	 * <p>1� Construcci�n del t�tulo
	 * <p>2� Cargamos el layout y main layout
	 * <p>3� Actualizacion de los valores en pantalla
	 * <p>4� Botones y sus handlers
	 * 
	 * @param context
	 * @param titulo
	 * @param cantidad_inicial
	 * @param listenerSumar
	 * @param listenerRestar
	 * @param listenerModificar
	 * @param listenerCancelar
	 */
	public DialogPersoComplexCantidadMasMenos (@NonNull Context context, @NonNull String titulo, float cantidad_inicial,
                                               View.OnClickListener listenerSumar, View.OnClickListener listenerRestar,
                                               View.OnClickListener listenerModificar, View.OnClickListener listenerCancelar) {
		
		super(context);
		this.cantidad = cantidad_inicial;
		ctxt = context;
		
		//1� Construcci�n del t�tulo:
			if (titulo.length() > 0) {
				super.setTitle(titulo);
			} else {
				super.setTitle("Informaci�n");
			}
	
		//2� Cargamos el layout y main layout:
			System.out.println("::: DialogComple cantidad inicial case 1");
			super.setContentView(R.layout.z_dialogpersocomplexcantidad_masmenos);
		
		//3� Actualizacion de los valores en pantalla:
			TextView texto_cantidad_articulo = (TextView) super.findViewById(R.id.Z_DIALOG_articulo_cantidad);
			texto_cantidad_articulo.setText(String.valueOf(cantidad_inicial));
			
			
		//4� Botones y sus handlers:
			ImageView boton_sumar = (ImageView) super.findViewById(R.id.Z_DIALOG_boton_mas);
			ImageView boton_modificar = (ImageView) super.findViewById(R.id.Z_DIALOG_boton_modificar);
			ImageView boton_restar = (ImageView) super.findViewById(R.id.Z_DIALOG_boton_menos);
			Button boton_cancelar = (Button) super.findViewById(R.id.Z_DIALOG_boton_cancelar);
			
			boton_sumar.setOnClickListener(listenerSumar);
			boton_modificar.setOnClickListener(listenerModificar);
			boton_restar.setOnClickListener(listenerRestar);
			boton_cancelar.setOnClickListener(listenerCancelar);
			
			boton_sumar.setOnTouchListener(new View.OnTouchListener() {
				
				public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
					RelativeLayout rlvL = (RelativeLayout) v.getParent();
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						rlvL.setBackgroundColor(ContextCompat.getColor(ctxt,R.color.amarillo));
					}
					else if (event.getAction() == KeyEvent.ACTION_UP){
						rlvL.setBackgroundColor(ContextCompat.getColor(ctxt,android.R.color.transparent));
					}
					return false;
				}
			});
			
			boton_modificar.setOnTouchListener(new View.OnTouchListener() {
				
				public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
					RelativeLayout rlvL = (RelativeLayout) v.getParent();
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						rlvL.setBackgroundColor(ContextCompat.getColor(ctxt,R.color.amarillo));
					}
					else if (event.getAction() == KeyEvent.ACTION_UP){
						rlvL.setBackgroundColor(ContextCompat.getColor(ctxt,android.R.color.transparent));
					}
					return false;
				}
			});
			
			boton_restar.setOnTouchListener(new View.OnTouchListener() {
				
				public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
					RelativeLayout rlvL = (RelativeLayout) v.getParent();
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						rlvL.setBackgroundColor(ContextCompat.getColor(ctxt,R.color.amarillo));
					}
					else if (event.getAction() == KeyEvent.ACTION_UP){
						rlvL.setBackgroundColor(ContextCompat.getColor(ctxt,android.R.color.transparent));
					}
					return false;
				}
			});
	}
	
	
	public float get_cantidad() {
		return this.cantidad;
	}
}