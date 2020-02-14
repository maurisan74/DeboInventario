package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;

/**
 * Clase accesorioa que valida una cadena para ver si tiene un formato aceptable
 * @author GuillermoR
 *
 */
public class URLValidator {
	
	/**
	 * Funcion que verifica si la URL es valida siguiendo algunos patrones
	 * <p>1� Si no contiene http:// devuelve mal formado
	 * <p>2� Si contiene .. devuelve mal formado
	 * <p>3� Si contiene /. devuelve mal formado
	 * <p>4� Si Contiene ./ devuelve mal formado
	 * <p>5� Si no contiene .php devulevle mal formado
	 * 
	 * @param url
	 * @throws ExceptionHttpExchange
	 */
	public static void esValidaEstaURL(@NonNull String url) throws ExceptionHttpExchange {
		//1� Si no contiene http:// devuelve mal formado
		if (url.contains("http://") == false) {
			throw new ExceptionHttpExchange("URL", "Mal formado: \"http://\" ausente");
		}
		//2� Si contiene .. devuelve mal formado
		if (url.contains("..") == true) {
			throw new ExceptionHttpExchange("URL", "Mal formado: \"..\" prohibido");
		}
		//3� Si contiene /. devuelve mal formado
		if (url.contains("/.") == true) {
			throw new ExceptionHttpExchange("URL", "Mal formado: \"/.\" prohibido");
		}
		//4� Si Contiene ./ devuelve mal formado
		if (url.contains("./") == true) {
			throw new ExceptionHttpExchange("URL", "Mal formado: \"./\" prohibido");
		}
		//5� Si no contiene .php devulevle mal formado
		if (url.contains(".php") == false) {
			throw new ExceptionHttpExchange("URL", "Mal formado: \".php\" ausente");
		}

	}
	
}
