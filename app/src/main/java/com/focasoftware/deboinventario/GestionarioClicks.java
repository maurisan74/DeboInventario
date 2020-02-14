package com.focasoftware.deboinventario;

/**
 * Clase que almacena valores parametrizados para administrar los toques y clicks
 * en la pantalla
 * @author GuillermoR
 *
 */
public class GestionarioClicks {
	/**
	 * El usuario a tocado la pantalla
	 */
	public final static int TOUCH_TRIGGERED = 2801;
	
	/**
	 * El usuario a soltado rapidamente despues de tocar la pantalla sin desplazamiento,
	 * por lo que cuenta como click 
	 */
	public final static int CLICK_TRIGGERED = 2802;
	
	/**
	 * El usuario mantiene el dedo apretado sin desplazamiento, por lo que cuenta como longclick
	 */
	public final static int LONGCLICK_TRIGGERED = 2803;
	
	/**
	 * El usuario mantiene el dedo apretado y lo ha desplazado, por lo que cuenta como un slip
	 */
	public final static int SLIP_TRIGGERED = 2804;
}
