package com.focasoftware.deboinventario;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Dialogo que permite mostrar una UI para modificar la cantidad de un articulo
 * en las BD
 * @author GuillermoR
 *
 */
public  class DialogPersoComplexCantidadModificacion extends Dialog implements GestionarioTecladoVirtual{	
	
	/**
	 * TextView para mostrar el valor incical
	 */
	private TextView textV_valor_inicial;
	/**
	 * Text view para mostrar el valor previsional luego de modificar su cantidad
	 */
	private TextView textV_valor_previsional;
	/**
	 * EditText para almacenar el nuevo valor a ingresar
	 */
	private EditText editT_nuevo_valor;
	/**
	 * Boton para resetear la cantidad
	 */
	private Button boton_reset;
	/**
	 * Variables accesorias
	 */
	private float VALOR_INICIO = 0;
	private int TIPO_OPERACION = -1;
	
	/**
	 * Constructor completo, setea las UI y carga los handlers
	 * <p>1� Construcci�n del t�tulo
	 * <p>2� Cargamos el layout y main layout
	 * <p>3� Actualizaci�n de los textos
	 * <p>4� Si estamos en el modo "MODIFICAR",
	 * 		se habilita la posibilidad de reestablecer el valor a No Tomado
	 * <p>5� EditTexts y handlers de los mismos
	 * <p>6� Botones y sus handlers
	 * 
	 * @param context
	 * @param tipo_operacion
	 * @param valor_inicial
	 * @param listenerValidar
	 * @param listenerCancelar
	 * @param listenerReset
	 */
	public DialogPersoComplexCantidadModificacion (@NonNull Context context, int tipo_operacion, float valor_inicial,
                                                   View.OnClickListener listenerValidar,
                                                   View.OnClickListener listenerCancelar,
                                                   View.OnClickListener listenerReset) {
		
		super(context);		
		System.out.println("::: Modificacion ");
		//1� Construcci�n del t�tulo:
			switch (tipo_operacion) {
				case DialogPerso.OPERACION_SUMAR:
					super.setTitle("Sumar");
					break;
	
				case DialogPerso.OPERACION_RESTAR:
					super.setTitle("Restar");
					break;
				
				case DialogPerso.OPERACION_MODIFICAR:
					super.setTitle("Nuevo valor");
					break;
			}

	//		System.out.println("::: DialogComple modificacion antes del switch");
	//		int ventana = 1;
	//		switch (ventana) {
	 //       case 1:
	  //     	System.out.println("::: DialogComple modificacion case 1 ");
		//2� Cargamos el layout y main layout:
			//super.setContentView(R.layout.z_dialogpersocomplexcantidad_masmenos);
	        	super.setContentView(R.layout.z_dialogpersocomplexcantidad_modificacion);
	//		break;
	//		default:
	//			super.setContentView(R.layout.z_dialogpersocomplexcantidad_modificacion_balanza);
	//		break;
		//	}
		//2� Cargamos el layout y main layout:
//			super.setContentView(R.layout.z_dialogpersocomplexcantidad_modificacion);
			
			
		//3� Actualizaci�n de los textos:
			textV_valor_inicial = (TextView) super.findViewById(R.id.Z_DIALOG_cantidad_actual);
			textV_valor_inicial.setText(String.valueOf(valor_inicial));
			VALOR_INICIO = valor_inicial;
			TIPO_OPERACION = tipo_operacion;
			
			textV_valor_previsional = (TextView) super.findViewById(R.id.Z_DIALOG_cantidad_final);
			textV_valor_previsional.setText(String.valueOf(valor_inicial));
			
		//4� Si estamos en el modo "MODIFICAR", se habilita la posibilidad de reestablecer el valor a No Tomado:
			if (TIPO_OPERACION == DialogPerso.OPERACION_MODIFICAR) {
				boton_reset = (Button) super.findViewById(R.id.Z_DIALOG_boton_reset);
				boton_reset.setVisibility(View.VISIBLE);
			}
		
			//5� EditTexts y handlers de los mismos
			editT_nuevo_valor = (EditText) super.findViewById(R.id.Z_DIALOG_cantidad_nueva);
			editT_nuevo_valor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				public void onFocusChange(View v, boolean hasFocus) {
					showKeyboard(editT_nuevo_valor);
					
				}
			});
			editT_nuevo_valor.addTextChangedListener(new TextWatcher() {
				
				
				public void onTextChanged(CharSequence s, int start, int before, int count) {}
				
				
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				
				
				public void afterTextChanged(Editable s) {
					float new_val = 0;
					int val_temp = 0;
					try {
						val_temp = Integer.parseInt(String.valueOf(s));
					}
					catch (Exception ex) {}
					
					switch (TIPO_OPERACION) {
						case DialogPerso.OPERACION_SUMAR:
							new_val = VALOR_INICIO + val_temp;
							break;
			
						case DialogPerso.OPERACION_RESTAR:
							new_val = Math.max(VALOR_INICIO - val_temp, 0);
							break;
						
						case DialogPerso.OPERACION_MODIFICAR:
							new_val = val_temp;
							break;
					}
					textV_valor_previsional.setText(String.valueOf(new_val));
				}
			});
			
			TextView textV_operacion = (TextView) super.findViewById(R.id.Z_DIALOG_tipo_operacion);
			String miTexto = "";
			switch (tipo_operacion) {
				case DialogPerso.OPERACION_SUMAR:
					miTexto = "SUMAR: ";
					break;
	
				case DialogPerso.OPERACION_RESTAR:
					miTexto = "RESTAR: ";
					break;
				
				case DialogPerso.OPERACION_MODIFICAR:
					miTexto = "NUEVO VALOR: ";
					break;
			}
			textV_operacion.setText(miTexto);
			
		//6� Botones y sus handlers:
			ImageView boton_validar = (ImageView) super.findViewById(R.id.Z_DIALOG_validar);
			ImageView boton_cancelar = (ImageView) super.findViewById(R.id.Z_DIALOG_cancelar);
			
			boton_validar.setOnClickListener(listenerValidar);
			boton_cancelar.setOnClickListener(listenerCancelar);
			if (TIPO_OPERACION == DialogPerso.OPERACION_MODIFICAR) {
				boton_reset.setOnClickListener(listenerReset);
			}
	}
	
	/**
	 * Devuleve el nuevo valor
	 * <p>1� Test para ver si el valor tiene formato a nombre, sino 0
	 * <p>2� Para provocar la excepcion si s no es del tipo Integer
	 * <p>3� En caso de error, devolvemos 0 si se trata de "sumar" o "restar".
	 * 		En el caso del "modificar", devolvemos "" para poder 
	 *  	restablecer un "No Tomado", o valor inicial sino
	 * 
	 * @return
	 */
	@NonNull
    public String get_nuevo_valor() {
		//1� Test para ver si el valor tiene formato a nombre, sino 0:
		String s = String.valueOf(editT_nuevo_valor.getText());
		
		try {
			//2� Para provocar la excepcion si s no es del tipo Integer
			Integer.parseInt(s); 
			return s;
		}
		catch (Exception ex) {
			//3� En caso de error, devolvemos 0 si se trata de "sumar" o "restar".
			// En el caso del "modificar", devolvemos "" para poder restablecer un "No Tomado", o valor inicial sino
			if (s.length() <= 0 && TIPO_OPERACION == DialogPerso.OPERACION_MODIFICAR) {
				return "";
			}
			else if (s.length() > 0 && TIPO_OPERACION == DialogPerso.OPERACION_MODIFICAR) {
				return String.valueOf(VALOR_INICIO);
			}
			else {
				return "0";
			}
		}
	}

	public void showKeyboard(EditText edit_text) {
		// TODO Auto-generated method stub
		
	}

	public void hideKeyboard(EditText edit_text) {
		// TODO Auto-generated method stub
		
	}
}