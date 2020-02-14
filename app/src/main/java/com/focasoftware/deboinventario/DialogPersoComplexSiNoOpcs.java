package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Dialogo personalizado que permite al usuario elegir entre realizar o no una acci�n
 * proveyendole adem�s la posibilidad de elegir una opcion de un radio group
 * @author GuillermoR
 *
 */
public class DialogPersoComplexSiNoOpcs extends Dialog {
	
	/**
	 * ACtivity que lo abrio, aparanetmente en desuso
	 */
	private Activity owner;
	/**
	 * Indice de la opcion que se seleccion� mediante el radiobutton
	 */
	private int opcionSeleccionada=0;
	
	/**
	 * Constructor para inicializar la UI y los handlers correspondientes
	 * <p>1� Construcci�n del t�tulo
	 * <p>2� Cargamos el layout y main layout
	 * <p>3� Construcci�n de la imagen
	 * <p>4� Mensaje
	 * <p>5� Setear los textos en las opciones a los radio 
	 * 			button y agregarlos al radiogroup
	 * <p>6� Botones
	 * 
	 * @param context
	 * @param titulo
	 * @param mensaje
	 * @param opc
	 * @param categoria_alerta
	 * @param listenerPositivo
	 * @param listenerNegativo
	 */
	public DialogPersoComplexSiNoOpcs (@NonNull Context context,
                                       @NonNull String titulo, String mensaje, @NonNull ArrayList<String> opc, int categoria_alerta,
                                       View.OnClickListener listenerPositivo, @Nullable View.OnClickListener listenerNegativo) {
		
		super(context);
		final Activity owner = (Activity) context;
		
		//1� Construcci�n del t�tulo:
			if (titulo.length() > 0) {
				super.setTitle(titulo);
			}
			
		//2� Cargamos el layout y main layout:
			super.setContentView(R.layout.z_dialogpersocomplex_sino_opc);
			
		//3� Construcci�n de la imagen:
			ImageView imagen = (ImageView) super.findViewById(R.id.DIALOG_imagen);
			
			switch (categoria_alerta) {
				case DialogPerso.DEFAULT:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_alertar));
					break;
					
				case DialogPerso.VALIDAR:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_validar));
					break;
					
				case DialogPerso.ALERTAR:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_alertar));
					break;
					
				case DialogPerso.PROHIBIR:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_prohibir));
					break;
				
				default:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_alertar));
					break;
			}
			
		//4� Mensaje:
			TextView texto = (TextView) super.findViewById(R.id.DIALOG_texto);
			texto.setText(mensaje);
			
				
		//5� Setear los textos en las opciones a los radio button y agregarlos al radiogroup
			RadioGroup grupo=(RadioGroup) super.findViewById(R.id.DIALOG_opciones);
			
			for (int i=0;i< opc.size();i++){
				RadioButton opcion=new RadioButton(context);
				opcion.setText(opc.get(i));
				opcion.setId(i);
				opcion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					
					public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
						if(isChecked==true){
							opcionSeleccionada=buttonView.getId();
						}
						
					}
				});
				if(i==0){
					opcion.setChecked(true);
				}
				grupo.addView(opcion);
				
			}
			
//			RadioButton opcion1=(RadioButton)super.findViewById(R.id.DIALOG_radio_opc1);
//			RadioButton opcion2=(RadioButton)super.findViewById(R.id.DIALOG_radio_opc2);
			
//			opcion1.setText(opc.get(0));
//			opcion1.setText(opc.get(1));
			
			
		//6� Botones:
			Button boton_validar = (Button) super.findViewById(R.id.DIALOG_boton_validar);
			Button boton_cancelar = (Button) super.findViewById(R.id.DIALOG_boton_cancelar);
			
			if (listenerNegativo != null) {
				boton_validar.setOnClickListener(listenerPositivo);
				boton_cancelar.setOnClickListener(listenerNegativo);
			} else {
				boton_validar.setOnClickListener(listenerPositivo);
				boton_validar.setText("OK");
				boton_cancelar.setVisibility(View.GONE);
			}
			
			
			
			
			
	}


	public int getOpcionSeleccionada() {
		return opcionSeleccionada;
	}


	public void setOpcionSeleccionada(int opcionSeleccionada) {
		this.opcionSeleccionada = opcionSeleccionada;
	}
	
	
	

}
