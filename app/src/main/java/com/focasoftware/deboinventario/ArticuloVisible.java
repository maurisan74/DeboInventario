package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Entidad que modela un Artculo a mostrar en la lista de artculos de los inventarios
 * @author GuillermoR
 *
 */
public class ArticuloVisible extends Articulo {

	//    				*************************
	//  				*************************
	//   				****    ATRIBUTOS    ****
	//    				*************************
	//    				*************************
	//
	/**
	 * Especifica si el articulo es visible o no en la lista
	 */
	private boolean visible;
	

	//    				*****************************
	//  				*****************************
	//   				****    CONSTRUCTORES    ****
	//    				*****************************
	//    				*****************************
	//
	/**
	 * Constructor de ARTICULO VISIBLE completo, especificando la visibilidad del 
	 * articulo
	 * @param unSector
	 * @param unCodigo
	 * @param losCodigosBarras
	 * @param unInventario
	 * @param unaDescripcion
	 * @param precioDeVenta
	 * @param precioDeCosto
	 * @param unaFoto
	 * @param unaCantidad
	 * @param unaFecha
	 * @param unExisventa
	 * @param unExisdeposito
	 * @param unDepsn
	 * @param unaVisibilidad
	 */
	public ArticuloVisible(int unSector, int unCodigo, int unaBalanza,int unDecimal, ArrayList<String> losCodigosBarras,
			ArrayList<String> losCodigosBarrasCompleto,
			int unInventario, String unaDescripcion, double precioDeVenta, double precioDeCosto, 
			String unaFoto, float unaCantidad, float unSubtotal, double unExisventa, double unExisdeposito, int unDepsn,
			String unaFecha, Boolean unaVisibilidad) {
		
		super(unSector, unCodigo,unaBalanza, unDecimal , losCodigosBarras,losCodigosBarrasCompleto, 
								unInventario, unaDescripcion, precioDeVenta,
								precioDeCosto, unaFoto, unaCantidad, unSubtotal, unExisventa, unExisdeposito, unDepsn, unaFecha);
		
		visible = unaVisibilidad;
	}
	


	
	/**
	 * Constructor de ARTICULO VISIBLE nuevo (sin especificar la visibilidad, 
	 * TRUE por defecto)
	 * @param unSector
	 * @param unCodigo
	 * @param losCodigosBarras
	 * @param unInventario
	 * @param unaDescripcion
	 * @param precioDeVenta
	 * @param precioDeCosto
//	 * @param unaUriFoto //msanchez
	 * @param unaCantidad
	 * @param unExisventa
	 * @param unExisdeposito
	 * @param unaFecha
	 */
	public ArticuloVisible(int unSector, int unCodigo, int unaBalanza, int unDecimal, ArrayList<String> losCodigosBarras,
			ArrayList<String> losCodigosBarrasCompleto,
			int unInventario, String unaDescripcion, double precioDeVenta, double precioDeCosto, 
			String unaFoto, float unaCantidad,float unSubtotal, double unExisventa,double unExisdeposito,int unDepsn, String unaFecha) {
		
		super(unSector, unCodigo, unaBalanza, unDecimal, losCodigosBarras,losCodigosBarrasCompleto, 
								unInventario, unaDescripcion, precioDeVenta,
								precioDeCosto, unaFoto, unaCantidad, unSubtotal, unExisventa, unExisdeposito, unDepsn, unaFecha);
		
		visible = true;
	}
	
	/**
	 * Constructor de ARTICULO VISIBLE nuevo (sin especificar la visibilidad
	 * pero que especifica fecha de inicio y fecha de fin al crearlo
	 * @param unSector
	 * @param unCodigo
	 * @param losCodigosBarras
	 * @param unInventario
	 * @param unaDescripcion
	 * @param precioDeVenta
	 * @param precioDeCosto
	 * @param unaFoto
	 * @param unaCantidad
	 * @param unExisventa
	 * @param unExisdeposito
	 * @param unaFecha
	 * @param fechaFin
	 */
	public ArticuloVisible(int unSector, int unCodigo, int unaBalanza, int unDecimal, ArrayList<String> losCodigosBarras,
			ArrayList<String> losCodigosBarrasCompleto,
			int unInventario, String unaDescripcion, double precioDeVenta, double precioDeCosto, 
			String unaFoto, float unaCantidad, double unExisventa, double unExisdeposito,
			int unDepsn, String unaFecha,String fechaFin) {
		
		super(unSector, unCodigo,  unaBalanza, unDecimal, losCodigosBarras, losCodigosBarrasCompleto, 
				unInventario, unaDescripcion, precioDeVenta,
				precioDeCosto, unaFoto, unaCantidad, unExisventa, unExisdeposito, unDepsn, unaFecha,fechaFin);
		
		visible = true;
	}
	
	/**
	 * Constructor a partir de otro articulo
	 * @param articulo
	 */
	public ArticuloVisible(@NonNull Articulo articulo) {
		super(articulo);
		this.visible = true;
	}
	
	
	
	/**
	 * Constructor de ARTICULO VISIBLE, solo por su visibilidad y gestin de articulos
	 * @param visibilidad
	 */
	public ArticuloVisible(boolean visibilidad) {
		this(0,0,0, 0, new ArrayList<String>(),new ArrayList<String>(), 0, "", (double)0, (double)0, "", -1, -1,(double)0,(double)0,0,
				"", visibilidad);
	
	}


	//    				***********************
	//  				***********************
	//   				****    MTODOS    ****
	//    				***********************
	//    				***********************
	//
	
	public boolean esVisible() {
		return visible;
	}

	public void setVisibilidad(boolean visibilidad) {
		this.visible = visibilidad;
	}
	
	
	
	
	
}
