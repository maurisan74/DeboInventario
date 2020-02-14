package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Dialogo personalizado que presenta al usuario la opcion de elegir entre una accion
 * positiva y otra negativa
 * @author GuillermoR
 *
 */
public class DialogPersoComplexSiNo extends Dialog {

	
	/**
	 * Quien llamo a este dialog, aparentemente en desuso
	 */
	private Activity owner;
	
	
	/**
	 * Constructor que inicializa la UI y los handlers
	 * <p>1� Construcci�n del t�tulo
	 * <p>2� Cargamos el layout y main layout
	 * <p>3� Construcci�n de la imagen
	 * <p>4� Mensaje
	 * <p>5� Botones y handlers
	 * 
	 * @param context
	 * @param titulo
	 * @param mensaje
	 * @param categoria_alerta
	 * @param listenerPositivo
	 * @param listenerNegativo
	 */
	public DialogPersoComplexSiNo (@NonNull Context context,
                                   @NonNull String titulo, String mensaje, int categoria_alerta,
                                   View.OnClickListener listenerPositivo, @Nullable View.OnClickListener listenerNegativo) {
		
		super(context);
		final Activity owner = (Activity) context;
		
		//1� Construcci�n del t�tulo:
			if (titulo.length() > 0) {
				super.setTitle(titulo);
			}
			
		//2� Cargamos el layout y main layout:
			super.setContentView(R.layout.z_dialogpersocomplex_sino);
			
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
			
					
		//5� Botones y handlers:
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
	
	
	
}
