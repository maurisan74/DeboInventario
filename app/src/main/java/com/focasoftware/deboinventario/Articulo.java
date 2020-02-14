package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Entidad que modela un articulo cuya cantidad se desa inventariar,
 * en la BD los mismos se asocian a los inventarios y adem�s se encuentran
 * almacenados como referencias en la tabla referencias
 * @author GuillermoR
 *
 */
public class Articulo {
	
	
	//    				**************************
	//  				**************************
	//   				****    CONSTANTES    ****
	//    				**************************
	//    				**************************
	
	/**
	 * Permite comparar y ordenar artilcos en base al codigo, si son iguales los sectores los
	 * compara en base al codigo, si no en base al sector directamente
	 * <p>1� Si tienen el mismo sector compara por codigo
	 * <p>2� Si no compara por sector
	 *
	 */
	public static final Comparator<Articulo> ORDEN_CODIGO = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						if (articulo0.getSector() == articulo1.getSector()) {
							//1� Si tienen el mismo sector compara por codigo
							return ((Integer)articulo0.getCodigo()).compareTo((Integer)articulo1.getCodigo());
						} else {
							//2� Si no compara por sector
							return ((Integer)articulo0.getSector()).compareTo((Integer)articulo1.getSector());
						}
					}
				};
				
	/**
	 * Permite comparar y ordenar articulos en base a las descripciones de los mismos
	 * <p>1� Compara las cadenas de descripciones			
	 */
	public static final Comparator<Articulo> ORDEN_NOM = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						//1� Compara las cadenas de descripciones
						return ((String)articulo0.getDescripcion()).compareTo((String)articulo1.getDescripcion());
					}
				};
				
	/**
	 * Permite comparar articulos y ordenarlos en base al n�mero de inventario	
	 * <p>1� Si los inventario son iguales
	 * <p>&nbsp;&nbsp;1.1 Si los sectores son iguales compara por codigo	
	 * <p>&nbsp;&nbsp;1.2 Sino, compara por sector
	 * <p>2� Sino, compara por nro de inventario
	 */
	public static final Comparator<Articulo> ORDEN_INVENTARIO = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						if (articulo0.getInventario() == articulo1.getInventario()) {
							//1� Si los inventario son iguales
							if (articulo0.getSector() == articulo1.getSector()) {
								//1.1 Si los sectores son iguales compara por codigo
								return ((Integer)articulo0.getCodigo()).compareTo((Integer)articulo1.getCodigo());
							} else {
								//1.2 Sino, compara por sector
								return ((Integer)articulo0.getSector()).compareTo((Integer)articulo1.getSector());
							}
						} else {
							//2� Sino, compara por nro de inventario
							return ((Integer)articulo0.getInventario()).compareTo((Integer)articulo1.getInventario());
						}
					}
				};
				
	/**
	 * Permite el ordenamiento y comparaci�n de los articulos en base a su precio de 
	 * venta
	 * <p>1� Si los precios son iguales compara por costo
	 * <p>2� Sino, compara por precio de venta
	 */
	public static final Comparator<Articulo> ORDEN_PRECIO_VENTA = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						if (articulo0.getPrecio_venta() == articulo1.getPrecio_venta()) {
							//1� Si los precios son iguales compara por costo
							return ((Double)articulo0.getPrecio_costo()).compareTo((Double)articulo1.getPrecio_costo());
						} else {
							//2� Sino, compara por precio de venta
							return ((Double)articulo0.getPrecio_venta()).compareTo((Double)articulo1.getPrecio_venta());
						}
					}
				};
				
	/**
	 * Los articulos se ordenan y comparan en base al precio de costo
	 * <p>1� Si los costos son iguales, compara por precio de costo
	 * <p>2� Si no por precio de costo directamente	
	 */
	public static final Comparator<Articulo> ORDEN_PRECIO_COSTO = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						if (articulo0.getPrecio_costo() == articulo1.getPrecio_costo()) {
							//1� Si los costos son iguales, compara por precio de costo
							return ((Double)articulo0.getPrecio_venta()).compareTo((Double)articulo1.getPrecio_venta());
						} else {
							//2� Si no por precio de costo directamente
							return ((Double)articulo0.getPrecio_costo()).compareTo((Double)articulo1.getPrecio_costo());
						}
					}
				};			
	
	/**
	 * Se realiza el ordenamiento y comparacion basados en el nombre del archivo de la 
	 * foto		
	 * <p>1� Compara por la cadena del nombre de la foto	
	 */
	public static final Comparator<Articulo> ORDEN_FOTO = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						//1� Compara por la cadena del nombre de la foto
						return ((String)articulo0.getFoto()).compareTo((String)articulo1.getFoto());
					}
				};
	
	/**
	 * La comparacion y ordenamiento se realia en base a la cantidad inventariado
	 * <p>1� Se compara en base a la cantidad			
	 */
	public static final Comparator<Articulo> ORDEN_CANTIDAD = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						System.out.println("::: Articulo entra a comparar datos" + articulo0.getCantidad());
						System.out.println("::: Articulo entra a comparar datos "+ articulo1.getCantidad());
						//1� Se compara en base a la cantidad
						return ((Float)articulo0.getCantidad()).compareTo((Float)articulo1.getCantidad());
					}
				};
				
				
				
	public static final Comparator<Articulo> ORDEN_EXIS_VENTA = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						if (articulo0.getExis_venta() == articulo1.getExis_venta()) {
							//1� Si los costos son iguales, compara por precio de costo
							return ((Double)articulo0.getExis_venta()).compareTo((Double)articulo1.getExis_venta());
						} else {
							//2� Si no por precio de costo directamente
							return ((Double)articulo0.getExis_venta()).compareTo((Double)articulo1.getExis_venta());
						}
					}
				};						
				
	public static final Comparator<Articulo> ORDEN_EXIS_DEPOSITO = new Comparator<Articulo>() {
					
					public int compare(@NonNull Articulo articulo0, @NonNull Articulo articulo1) {
						if (articulo0.getExis_deposito() == articulo1.getExis_deposito()) {
							//1� Si los costos son iguales, compara por precio de costo
							return ((Double)articulo0.getExis_deposito()).compareTo((Double)articulo1.getExis_deposito());
						} else {
							//2� Si no por precio de costo directamente
							return ((Double)articulo0.getExis_deposito()).compareTo((Double)articulo1.getExis_deposito());
						}
					}
				};						
								
	//    				*************************
	//  				*************************
	//   				****    ATRIBUTOS    ****
	//    				*************************
	//    				*************************
	//
	/**
	 * Numero de sector del articulo			
	 */
	private int sector;
	/**
	 * Numero de codigo del articulo
	 */
	private int codigo;
	/**
	 * Lista de codigos de barra 
	 */
	private ArrayList<String> codigos_barras;
	/**
	 * Numero del inventario al que esta asociado
	 */
	private int inventario;
	/**
	 * Descripcion del articulo
	 */
	private String descripcion;
	/**
	 * Precio al que se vende el articulo
	 */
	private double precio_venta;
	/**
	 * Precio de costo o compra del articulo
	 */
	private double precio_costo;
	/**
	 * Almacena la direcci�n o nombre del archivo de la foto
	 */
	private String foto;
	/**
	 * Cantidad contada, invetariada , segun la aplicacion
	 */
	private float cantidad;
	/**
	 * Fecha de inicio del articulo
	 */
	private String fechaInicio;
	/**
	 * Fecha de fin de edici�n del articulo
	 */
	private String fechaFin;
	
	private double exis_venta;
	private double exis_deposito;
	
	private int balanza;
	private int decimales;
	
	private float pesaje;
	
	/*
	 * Guarda el codigo de barra con los 13 numeros para obtener los datos
	 * por separado
	 */
	@Nullable
    private ArrayList<String> codigos_barras_completo = null;
	
	//
	//
	//*********************************************************************************************
	//
	//    				*****************************
	//  				*****************************
	//   				****    CONSTRUCTORES    ****
	//    				*****************************
	//    				*****************************

	/**
	 * Constructor completo en base a los parametros
	 * 
	 */
	public Articulo(@NonNull Articulo articulo) {
		sector = articulo.sector;
		codigo = articulo.codigo;
		balanza = articulo.balanza;
		decimales = articulo.decimales;
		codigos_barras = articulo.codigos_barras;
		codigos_barras_completo = articulo.codigos_barras_completo;
		inventario = articulo.inventario;
		descripcion = articulo.descripcion;
		precio_venta = articulo.precio_venta;
		precio_costo = articulo.precio_costo;
		foto = articulo.foto;
		cantidad = articulo.cantidad;
		
		pesaje = articulo.pesaje;
		
		exis_venta=articulo.exis_venta;
		exis_deposito=articulo.exis_deposito;
		
		fechaInicio = articulo.fechaInicio;
		fechaFin=articulo.fechaFin;
		
		System.out.println("::: Articulo return cantidad -- 1 --");
		System.out.println(":::: Articulo 269 ");
	}
	
	
	
	/**
	 * Constructor que NO provee la fecha de fin
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
	 */
	public Articulo(int unSector,
					 int unCodigo,
					 int unaBalanza,
					 int unDecimal,
					 ArrayList<String> losCodigosBarras, 
					 ArrayList<String> losCodigosBarrasCompleto, 
					 int unInventario, 
					 String unaDescripcion, 
					 double precioDeVenta, 
					 double precioDeCosto,
					 String unaFoto, 
					 float unaCantidad,
					 double unExisventa,
					 double unExisdeposito,
					 String unaFecha) 
										{
		
		sector = unSector;
		codigo = unCodigo;
		balanza = unaBalanza;
		decimales = unDecimal;
		codigos_barras = losCodigosBarras;
		codigos_barras_completo = losCodigosBarrasCompleto;
		inventario = unInventario;
		descripcion = unaDescripcion;
		precio_venta = precioDeVenta;
		precio_costo = precioDeCosto;
		foto = unaFoto;
		cantidad = unaCantidad;
		exis_venta = unExisventa;
		exis_deposito = unExisdeposito;
		
		fechaInicio = unaFecha;
		
		System.out.println(":::: Articulo 321 ");
	}
	
	/**
	 * Constructor de ARTICULO completo, por defecto
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
     * @param unDepsn
     * @param unaFecha
     */
	public Articulo(int unSector,
                    int unCodigo,
                    int unaBalanza,
                    int unDecimal,
                    ArrayList<String> losCodigosBarras,
                    ArrayList<String> losCodigosBarrasCompleto,
                    int unInventario,
                    String unaDescripcion,
                    double precioDeVenta,
                    double precioDeCosto,
                    String unaFoto,
                    float unaCantidad,
                    double unExisventa,
                    double unExisdeposito,
                    int unDepsn, String unaFecha,
                    String unaFechaFin)
										{
		sector = unSector;
		codigo = unCodigo;
		balanza = unaBalanza;
		decimales = unDecimal;
		codigos_barras = losCodigosBarras;
		codigos_barras_completo = losCodigosBarrasCompleto;
		inventario = unInventario;
		descripcion = unaDescripcion;
		precio_venta = precioDeVenta;
		precio_costo = precioDeCosto;
		foto = unaFoto;
		cantidad = unaCantidad;
		exis_venta = unExisventa;
		exis_deposito = unExisdeposito;
		fechaInicio = unaFecha;
		fechaFin = unaFechaFin;
		
		System.out.println(":::: Articulo 375 ");
	}
	
	/**
	 * Constructor ARTICULO nuevo (sin conocer ni foto, ni cantidad medida, ni 
	 * fecha de inicio de la medicion)
	 * @param unSector
	 * @param unCodigo
	 * @param losCodigosBarras
	 * @param unInventario
	 * @param descripcion
	 * @param precio_venta
	 * @param precio_costo
	 * @param unaDescripcion
	 * @param i
	 * @param unExisventa
	 * @param unExisdeposito
	 * @param precioDeVenta
	 * @param precioDeCosto
*
* <p>1� Llamada al padre con los datos pasados y "" en la foto y fechaInicio y -1
	 * @param s
	 */
	public Articulo(int unSector,
					int unCodigo,
					int unaBalanza,
					int unDecimal,
					ArrayList<String> losCodigosBarras,
					ArrayList<String> losCodigosBarrasCompleto,
					int unInventario,
					String descripcion,
					double precio_venta,
					double precio_costo,
					String unaDescripcion,
					float i,
					double unExisventa,
					double unExisdeposito,
					double precioDeVenta,
					int precioDeCosto,
					String s)
											{
		/*
		 * 1 Llamada al padre con los datos pasados y "" en la foto y fechaInicio y -1
		 * en la cantidad 
		 */
		 
		this(unSector,
				unCodigo,
				unaBalanza,
				unDecimal,
				losCodigosBarras,
				losCodigosBarrasCompleto,
				unInventario, 
				unaDescripcion,
				precioDeVenta, 
				precioDeCosto, 
				"", // FOTO
				-1, // CANTIDAD
				unExisventa,
				unExisdeposito,
				""  // FECHA
				);
	}

	/**
	 * Constructor de ARTICULO como entidad de referencia (con sus datos importantes
	 * solamente)
	 * @param unCodigoBarra
	 * @param unaDescripcion
	 * //msanchez
//	 * @param unaCantidad
//	 * @param unaUriFoto
	 * //msanchez
	 *  Llamado al constructor padre con -1 en la cantidad y "" en la fechaInicio
	 */
	public Articulo(int sector, int articulo, int balanza, int decimales, ArrayList<String> codbar, ArrayList<String> codbarcompleto, int unInventario, String unCodigoBarra, double precio_venta, double precio_costo, String unCodigoBarraCompleto, int i, double exis_venta, double exis_deposito, int depsn, String unaDescripcion, String unaFoto) {
		//1� Llamado al constructor padre con -1 en la cantidad y "" en la fecha de Inicio
	/*	this(0, 0, new ArrayList<String>(Arrays.asList(unCodigoBarra)), 0, unaDescripcion, 
				(double)0, (double)0, unaFoto, -1,0,0, "");
*/
		this(0, 0,0,0, new ArrayList<>(Arrays.asList(unCodigoBarra)),
				new ArrayList<>(Arrays.asList(unCodigoBarraCompleto)), 0, unaDescripcion,
				(double)0, (double)0, unaFoto, 
				-1, (double)0, (double)0, "");
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
	
	public int getCodigo() {
		return codigo;
	}
	public int getBalanza() {
		return balanza;
	}
	public void setBalanza(int balanza) {
		this.balanza = balanza;
	}
	public float getPesaje() {
		return pesaje;
	}
	public void setPesaje(float pesaje) {
		this.pesaje = pesaje;
	}
	public int getDecimales() {
		return decimales;
	}
	public void setDecimales(int decimales) {
		this.decimales = decimales;
	}
	public double getExis_venta() {
		return exis_venta;
	}
	public double getExis_deposito() {
		return exis_deposito;
	}
	public int getInventario() {
		return inventario;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public double getPrecio_venta() {
		return precio_venta;
	}
	public double getPrecio_costo() {
		return precio_costo;
	}
	public float getCantidad() {
		return cantidad;
	}

	public String getFechaInicio() {
		return fechaInicio;
	}

	public String getFoto() {
		return foto;
	}

	public int getSector() {
		return sector;
	}

	public ArrayList<String> getCodigos_barras() {
		return codigos_barras;
	}
	
	@Nullable
    public ArrayList<String> getCodigos_barras_completo() {
		return codigos_barras_completo;
	}
	/**
	 * Devuelve los codigos de barrra como una cadena que concatena los cb separados
	 * por coma
	 * @return
	 */
	public String getCodigos_barras_string() {
		System.out.println("::: Articulo 543 getCodigosBarrasString");
		// Preparacion String = lista todos los codigos de barra del articulo
		
		String codigos_barras = "";
	
		
		if (getCodigos_barras().size() <= 0) {
//			System.out.println("::: Articulo 559");
			codigos_barras = "0";
//			System.out.println("::: Articulo 543 paso3");
		}		
		else if (getCodigos_barras().size() == 1) {
		
	if(getCodigos_barras_completo().isEmpty()){
		System.out.print("::: Articulo 549 el codigo de barra completo viene vacio");
		
		if(getBalanza()==8 && getCantidad()== -1.0 && getDecimales()!=3){

			codigos_barras = String.valueOf(getCantidad());
//			System.out.println("::: Articulo 572 codigos_barras " + codigos_barras);
			
			}else if(getBalanza()==8 && getCantidad()!= -1.0 && getDecimales()!=3){
//				System.out.println("::: Articulo 613 Entra dentro de balanza == 8 && pero en la seg y resto---");
				codigos_barras = String.valueOf(getCantidad());
//				System.out.println("::: Articulo 613 codigos_barras " + codigos_barras);

			}else if(getBalanza()!=8){
//				System.out.println("::: Articulo getBalanza()!=8  entro ");
				codigos_barras = String.valueOf(getCantidad());
			
			}else if(getBalanza()==8 && getCantidad()== -1.0 && getDecimales()==3){
				
				codigos_barras = String.valueOf(getCantidad());
			}else if(getBalanza()==8 && getCantidad()!= -1.0 && getDecimales()!=3){
		
				codigos_barras = String.valueOf(getCantidad());
		}

	}else{

		if(getBalanza()==8 && getCantidad()== -1.0){
			codigos_barras = getCodigos_barras_completo().get(0).substring(7,12);

			}else if(getBalanza()==8 && getCantidad()!= -1.0){

				codigos_barras = String.valueOf(getCantidad());

			}else if(getBalanza()!=8){
				System.out.println("::: Articulo getBalanza()!=8  entro ");
				System.out.println("::: Articulo getBalanza()!=8  cantidad "+ getCantidad());
				System.out.println("::: Articulo getBalanza()!=8  codigoBarras "+ getCodigos_barras().get(0));
				codigos_barras = String.valueOf(getCodigos_barras().get(0));
		}

	}
			

	System.out.println("::: Articulo 618  " +codigos_barras);

		} 
		else {
			System.out.println("::: Articulo 636 ");
			codigos_barras = getCodigos_barras().get(0);
//		
			for (int i = 1 ; i < getCodigos_barras().size() ; i++) {
				codigos_barras += "," + getCodigos_barras().get(i);
		System.out.println("::: Articulo 636 codigos_barras " + codigos_barras);

			}
		}
		return codigos_barras;
	}
	
	public String getCodigos_barras_completo_string() {
		// Preparacion String = lista todos los codigos de barra del articulo
		String codigos_barras_completo = "";
		if (getCodigos_barras_completo().size() <= 0) {
			codigos_barras_completo = "0";
		}
		else if (getCodigos_barras_completo().size() == 1) {

			codigos_barras_completo = getCodigos_barras().get(0);
				
			System.out.println("::: Articulo 3 Metodo 2 cod comp 1 " + codigos_barras_completo);
		} 
		else {
			codigos_barras_completo = getCodigos_barras_completo().get(0);
			for (int i = 1 ; i < getCodigos_barras_completo().size() ; i++) {
				codigos_barras_completo += "," + getCodigos_barras_completo().get(i);

				System.out.println("::: Articulo 3 codi comp2 "+ codigos_barras);
			}
		}
		return codigos_barras_completo;	
		}
	

	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public void setCantidad(float cantidad) {
		this.cantidad = cantidad;
	}
	
	public void setFechaInicio(String fecha) {
		this.fechaInicio = fecha;
	}
	
	public void setDescripcion (@NonNull String nueva_descripcion) {
		if (nueva_descripcion.length() > 0) {
			this.descripcion = nueva_descripcion;
		}
	}

	public void setCodigos_barras(ArrayList<String> lista_codigos_barras) {
		this.codigos_barras = lista_codigos_barras;
	}


	public void setInventario(int inventario) {
		this.inventario = inventario;
	}


	public void setSector(int sector) {
		this.sector = sector;
	}


	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}


	public String getFechaFin() {
		return fechaFin;
	}


	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	
	public void setExis_venta(double exis_venta) {
		this.exis_venta = exis_venta;
	}
	
	
	public void setExis_deposito(double exis_deposito) {
		this.exis_deposito = exis_deposito;
	}

	public int getDepsn() {
		//msanchez
		return 0;
	}

	public String getSubtotal() {
		//msanchez
		return "";
	}
}
