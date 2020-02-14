package com.focasoftware.deboinventario;

/**
 * Interface que deberian implementar quienes vayana a acitvar, desactivar el WIFI
 * o poner en modo avion la aplicacion
 * @author GuillermoR
 *
 */
public interface Wifi {

	/**
	 * Activar el WIFI
	 */
	public void activarWifi();
	/**
	 * Desactivar el WIFI
	 */
	public void desactivarWifi();
	/**
	 * Verificar si estamos en modo avion
	 * @return
	 */
	public boolean estaEnModoAvion();
	
}
