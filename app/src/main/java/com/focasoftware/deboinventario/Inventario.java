
package com.focasoftware.deboinventario;

/**
 * Entidad que modela un inventario realizado o a realizarse.
 * @author GuillermoR
 *
 */
public class Inventario {
	
	/**
	 * Parametros de los Inventarios Dinamicos
	 */
	public final static int INVENTARIO_DINAMICO_DEST_VENTA = 0;
	public final static int INVENTARIO_DINAMICO_DEST_DEPOSITO = 1;
	
	
	public final static int INVENTARIO_CLASE_PLANEADO = 0;
	public final static int INVENTARIO_CLASE_DINAMICO = 1;
	

	//    				**************************
	//  				**************************
	//   				****    CONSTANTES    ****
	//    				**************************
	//    				**************************
	

	//    				*************************
	//  				*************************
	//   				****    ATRIBUTOS    ****
	//    				*************************
	//    				*************************
	//

	/**
	 * Numero del inventario, positivo si viene importado, negativo si es dinamico
	 */
	private int numero;
	/**
	 * Una descripcion del inventario, nombre
	 */
	private String descripcion;
	/**
	 * Fecha en que se creo o se incio el mismo
	 */
	private String fechaInicio;
	/**
	 * Fecha en que se conto el ultimo numero o se cerro y se exporto
	 */
	private String fechaFin;
	/**
	 * Estado posible abierto o cerrado para su exportacion
	 */
	private int estado;
	/**
	 * Destino puede ser venta, o deposito por el momento (27/4/2012)
	 * Le agrego Compras como valor en 3 -> Damian(16/06/2016)
	 */
	private int lugar;
	

	//    				*****************************
	//  				*****************************
	//   				****    CONSTRUCTORES    ****
	//    				*****************************
	//    				*****************************
	
	/**
	 * Constructor completo
	 */
	public Inventario (int unNumero, String unaDescripcion, String unaFecha,
			String unafechaFin, int unEstado,int unLugar) {
		numero = unNumero;
		descripcion = unaDescripcion;
		fechaInicio = unaFecha;
		fechaFin=unafechaFin;
		estado = unEstado;
		lugar = unLugar;
	}
	
	
	//    				***********************
	//  				***********************
	//   				****    METODOS    ****
	//    				***********************
	//    				***********************
	
	public int getNumero() {
		return numero;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public String getFechaInicio() {
		return fechaInicio;
	}
	
	public int getEstado() {
		return estado;
	}

	public int getLugar() {
		return lugar;
	}

	public void setLugar(int destino) {
		this.lugar = destino;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}


	
	
	
	
}
