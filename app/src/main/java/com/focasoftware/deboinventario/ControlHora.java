package com.focasoftware.deboinventario;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase que permite realizar un control sobre la fecha de la tablet, informando si la
 * misma se ha reseteado a un valor muy antiguo
 * @author GuillermoR
 *
 */
public class ControlHora {

	
	/**
	 * Funci�n que controla que la hora del sistema no haya cambiado y vuelto a un 
	 * valor equivocado
	 * <p>1� Si la el a�o es menor a 2011, se devuelve false
	 * @return TRUE si la hora de la tablet parece correcta, FALSE sino. 
	 */
	public static boolean control_horario() {
		Date fecha = new Date();
		//1� Si la el a�o es menor a 2011, se devuelve false
		if ((fecha.getYear() + 1900) < 2011) {
			return false;
		} else {
			return true;
		}
		
	}
	
	/**
	 * Funcion que devuelve el mensaje indicando la hora actual del sistema
	 * @return
	 */
	@NonNull
    public static String mensaje() {
		return "El sistema indica la fecha siguiente: \n\n" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) +
				"\n\n�Le parece correcto este valor?";
	}
	
	/**
	 * Verifica la fecha actual contra la fecha de la utlima operacion del sistem
	 * para evaluar si es posterior a la misma
	 * <p>1� Obtener la fecha actual
	 * <p>2� Obtener la ultima fecha de uso del sistema
	 * <p>3� Si la hora actual es anterior a la de la ultima operacion,Devuelve false
	 * <p>4� Si no, seteamos la nueva fecha
	 * @param context
	 * @return
	 */
	public static boolean autorizar(Context context) {
		//1� Obtener la fecha actual
		Date fecha_now = new Date();
		//2� Obtener la ultima fecha de uso del sistema
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String fecha_ultima_operacion = settings.getString(Parametros.preferencia_fecha_ultima_op, Parametros.PREF_ULTIMA_FECHA_OPERACION);
		Date fecha_ult_ope = new Date(Date.parse(fecha_ultima_operacion));
		//3� Si la hora actual es anterior a la de la ultima operacion,
		//Devuelve false
		if (fecha_now.before(fecha_ult_ope)) { 
			return false;
		}
		else {
			//4� Si no, seteamos la nueva fecha
			SharedPreferences.Editor editor = settings.edit();
    		editor.putString(Parametros.preferencia_fecha_ultima_op, new SimpleDateFormat("yyyyMMdd").format(fecha_now));
			editor.commit();
    		return true;
		}
	}
	
	
}
