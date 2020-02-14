package com.focasoftware.deboinventario;

/**
 * Entidad utilizada en varias aplicacones que contiene informacion de operadores de 
 * las tablets
 * @author GuillermoR
 *
 */
public class Operador {
	//
	//
	//
	//*********************************************************************************************
	//
	//    				**************************
	//  				**************************
	//   				****    CONSTANTES    ****
	//    				**************************
	//    				**************************
	/**
	 * Operadores por defecto en las aplicaciones
	 */
	public final static Operador ope1 = new Operador(1, "111", "Se�or Medidor", "foca");
	public final static Operador ope2 = new Operador(2, "111", "foca", "foca");
	public final static Operador ope3 = new Operador(3, "111", "1", "2");
	
	public final static Operador[] listaOperadores = {ope1, ope2, ope3};
	
	//
	//
	//
	//*********************************************************************************************
	//
	//    				*************************
	//  				*************************
	//   				****    ATRIBUTOS    ****
	//    				*************************
	//    				*************************
	//
	
	/**
	 * identificador del operador
	 */
	private int id;
	/**
	 * Dni del operador 
	 */
	private String dni;
	/**
	 * Variable para el nombre del operador 
	 */
	private String nombre;
	/**
	 * Variable para almacenar el password
	 */
	private String pwd;
	
	//
	//
	//
	//*********************************************************************************************
	//
	//    				*****************************
	//  				*****************************
	//   				****    CONSTRUCTORES    ****
	//    				*****************************
	//    				*****************************

	/**Constructor completo de la entidad OPERADOR
	 * 
	 * @param unId
	 * @param unDni
	 * @param unNombre
	 * @param unaContrasena
	 */
	public Operador(int unId, String unDni, String unNombre, String unaContrasena) {
		id = unId;
		dni = unDni;
		nombre = unNombre;
		pwd = unaContrasena;
	}

	
	//
	//
	//
	//*********************************************************************************************
	//
	//    				***********************
	//  				***********************
	//   				****    METODOS    ****
	//    				***********************
	//    				***********************
	
	public int getId() {
		return id;
	}

	public String getDni() {
		return dni;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String getPwd() {
		return pwd;
	}

}