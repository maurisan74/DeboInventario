package com.focasoftware.deboinventario;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Dialogo personalizado que permite aceptar una foto tomada, o volver a tomar la misma
 * @author GuillermoR
 *
 */
public class DialogPersoComplexFotoSiNo extends Dialog {

	/**
	 * Constructor, carga la UI y handlers
	 * <p>1� Llamamos al super
	 * <p>2� Obtenemos el layout
	 * <p>3� Recuperamos La imagen
	 * <p>4� Recuperamos El TextView
	 * <p>5� Recuperamos los botones
	 * <p>6� Seteamos el titulo al padre
	 * <p>7� Llenamos la imagen y TextView
	 * <p>8� Handlers a los botonoes
	 * 
	 * @param context
	 * @param titulo
	 * @param mensaje
	 * @param foto
	 * @param listenerPositivo
	 * @param listenerNegativo
	 */
	public DialogPersoComplexFotoSiNo(@NonNull Context context, String titulo, String mensaje, Bitmap foto,
                                      View.OnClickListener listenerPositivo, View.OnClickListener listenerNegativo) {
		//1� Llamamos al super
		super(context);
		//2� Obtenemos el layout
		super.setContentView(R.layout.z_dialogpersocomplexfoto_sino);
		//3� Recuperamos La imagen
		ImageView imagen = (ImageView) super.findViewById(R.id.DIALOG_imagen);
		//4� Recuperamos El TextView
		TextView texto_mensaje = (TextView) super.findViewById(R.id.DIALOG_texto);
		//5� Recuperamos los botones
		Button boton_validar = (Button) super.findViewById(R.id.DIALOG_boton_validar);
		Button boton_cancelar = (Button) super.findViewById(R.id.DIALOG_boton_cancelar);
		//6� Seteamos el titulo al padre
		super.setTitle(titulo);
		//7� Llenamos la imagen y TextView
		imagen.setImageBitmap(foto);
		texto_mensaje.setText(mensaje);
		//8� Handlers a los botonoes
		boton_validar.setOnClickListener(listenerPositivo);
		boton_cancelar.setOnClickListener(listenerNegativo);
	}

	

}
