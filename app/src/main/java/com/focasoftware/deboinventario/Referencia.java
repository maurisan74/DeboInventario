package com.focasoftware.deboinventario;

class Referencia {

	private int sector;
	private int articulo;
	private String codigo_barra;
	private String descripcion;
	private double precio_venta;
	private double precio_costo;
	private String foto;

	private double exis_venta;
	private double exis_deposito;
	private int depsn;
	
	/*variables para balanza*/
	private int balanza;
	private int decimales;
	private String codigo_barra_completo;
	
	public Referencia() {
	}

	public Referencia(int sector, int articulo, int balanza, int decimales, String codigo_barra,
			String codigo_barra_completo,
			String descripcion, double precio_venta, double precio_costo,
			String foto, double exis_venta, double exis_deposito, int depsn) {
		this.sector = sector;
		this.articulo = articulo;this.balanza = balanza;
		this.decimales = decimales;
		this.codigo_barra = codigo_barra;
		this.codigo_barra_completo = codigo_barra_completo;
		this.descripcion = descripcion;
		this.precio_venta = precio_venta;
		this.precio_costo = precio_costo;
		this.foto = foto;

		this.exis_venta = exis_venta;
		this.exis_deposito = exis_deposito;
		this.depsn = depsn;
	}


	public double getExis_venta () {
		return exis_venta;
	}

	public void setExis_venta(double exis_venta) {
		this.exis_venta = exis_venta;
	}
	
	public int getDepsn () {
		return depsn;
	}

	public void setDepsn(int depsn) {
		this.depsn = depsn;
	}
	
	
	public double getExis_deposito () {
		return exis_deposito;
	}

	public void setExis_deposito(double exis_deposito) {
		this.exis_deposito = exis_deposito;
	}
	
	public int getSector() {
		return sector;
	}

	public void setSector(int sector) {
		this.sector = sector;
	}
	

	public int getBalanza() {
		return balanza;
	}

	public void setBalanza(int balanza) {
		this.balanza = balanza;
	}

	public int getDecimales() {
		return decimales;
	}

	public void setDecimales(int decimales) {
		this.decimales = decimales;
	}


	public int getArticulo() {
		return articulo;
	}

	public void setArticulo(int articulo) {
		this.articulo = articulo;
	}

	public String getCodigo_barra() {
		return codigo_barra;
	}

	public void setCodigo_barra(String codigo_barra) {
		this.codigo_barra = codigo_barra;
	}
	
	public String getCodigo_barra_completo() {
		return codigo_barra_completo;
	}

	public void setCodigo_barra_completo(String codigo_barra_completo) {
		this.codigo_barra_completo = codigo_barra_completo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecio_venta() {
		return precio_venta;
	}

	public void setPrecio_venta(double precio_venta) {
		this.precio_venta = precio_venta;
	}

	public double getPrecio_costo() {
		return precio_costo;
	}

	public void setPrecio_costo(double precio_costo) {
		this.precio_costo = precio_costo;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

}
