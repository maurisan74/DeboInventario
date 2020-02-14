package com.focasoftware.deboinventario;
class Local {

	public  int id_local;
	public String nombre;
	public String descripcion;

	public Local(String pNombre, String pDescripcion) {
		this.nombre = pNombre;
		this.descripcion = pDescripcion;
	}
	
	public Local(String pNombre, String pDescripcion, int pIdLocal) {
		this.nombre = pNombre;
		this.descripcion = pDescripcion;
		this.id_local = pIdLocal;
	}
	
	public void setIdLocal(int pIdLocal){
		this.id_local = pIdLocal;
	}
	
	public int getIdLocal(){
		return this.id_local;
	}

	public void setNombre(String pNombre) {
		this.nombre = pNombre;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setDescripcion(String pDescripcion) {
		this.descripcion = pDescripcion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}
}
