package com.focasoftware.deboinventario;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Dialogo que presenta la UI para cargar un valor en un edittext y permitir aceptarlo
 * o cancelarlo mediante los botones correspondientes
 * @author GuillermoR
 *
 */
public class DialogPersoComplexEditTextOkCancel extends Dialog {
	
	/**
	 * Variable para almacenar el contexto sobre el que se esta trabajando
	 */
	private Context ctxt;
	/**
	 * EditText para almacenar alguna cadena que se desee capturar
	 */
	private EditText editT;
	
	/**
	 * Constructor completo: Inicializa la UI, carga los handlers
	 * <p>1� Construcci�n del t�tulo
	 * <p>2� Cargamos el layout y main layout
	 * <p>3� Mensaje
	 * <p>4� Imagen
	 * <p>5� EditText
	 * <p>6� Botones
	 * 
	 * @param context
	 * @param titulo
	 * @param mensaje
	 * @param imagen
	 * @param input_type
	 * @param listenerPositivo
	 * @param listenerNegativo
	 */
	public DialogPersoComplexEditTextOkCancel (
            @NonNull Context context, @NonNull String titulo, String mensaje, int imagen, int input_type,
            View.OnClickListener listenerPositivo, View.OnClickListener listenerNegativo) {
		
		super(context);
		ctxt = context;
		
		//1� Construcci�n del t�tulo:
			if (titulo.length() > 0) {
				super.setTitle(titulo);
			}
			
			
		//2� Cargamos el layout y main layout:
			super.setContentView(R.layout.z_dialogpersocomplexedittext_okcancel);
			
		
		//3� Mensaje:
			TextView texto = (TextView) super.findViewById(R.id.DIALOG_mensaje);
			texto.setText(mensaje);
			
			
		//4� Imagen:
			ImageView imagen_dialog = (ImageView) super.findViewById(R.id.DIALOG_imagen);
			switch (imagen) {
				case DialogPerso.IMAGEN_CODIGO_BARRAS:
					imagen_dialog.setImageDrawable(ctxt.getResources().getDrawable(R.drawable.icono_barcode));
					break;
				case DialogPerso.IMAGEN_ARTICULO:
					imagen_dialog.setImageDrawable(ctxt.getResources().getDrawable(R.drawable.icono_articulo));
					break;
				default:
					imagen_dialog.setImageDrawable(ctxt.getResources().getDrawable(R.drawable.icono_text_default));
					break;
			}
			
		//5� EditText:
			editT = (EditText) super.findViewById(R.id.DIALOG_edittext);
			switch (input_type) {
				case DialogPerso.INPUT_NUMEROS:
					editT.setInputType(InputType.TYPE_CLASS_NUMBER);
					break;
				default:
					editT.setInputType(InputType.TYPE_CLASS_TEXT);
					break;
			}
			
			
		//6� Botones:
			Button boton_validar = (Button) super.findViewById(R.id.DIALOG_boton_ok);
			Button boton_cancelar = (Button) super.findViewById(R.id.DIALOG_boton_cancel);
			
			boton_validar.setOnClickListener(listenerPositivo);
			boton_cancelar.setOnClickListener(listenerNegativo);
	}
	
	
	/**
	 * Devuelve el valor del EditText
	 * @return
	 */
	@NonNull
    public String get_text() {
		return String.valueOf(editT.getText());
	}
	
	
}
