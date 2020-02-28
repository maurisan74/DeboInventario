package com.focasoftware.deboinventario;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Clase accesoria que permite las conversiones entre balizas de XML,USB y de BDD
 * @author GuillermoR
 *
 */
public class BalizasConversor {


	public final static int BDD = 1;
	public final static int XML = 2;
	public final static int USB = 3;

	/*
	 * Almacena HashMaps con informacin de las Balizas
	 */
	private ArrayList<HashMap<Integer, String>> todos_vectores;


	/*
	 * Constructor
	 */
	public BalizasConversor(){
		todos_vectores = new ArrayList<HashMap<Integer,String>>();
	}


	/*
	 * Constructor con BDD y XML solos
	 * @param baliza_BDD
	 * @param baliza_XML
	 * <p>1 Mete en un hashMap las valizas BDD, XML y "" para USB
	 * <p>2 Agrega ese hashMap al arreglo
	 */
	public void put(String baliza_BDD, String baliza_XML) {

		//1 Mete en un hashMap las valizas BDD, XML y "" para USB
		HashMap<Integer, String> vector = new HashMap<Integer, String>();
		vector.put(BDD, baliza_BDD);
		vector.put(XML, baliza_XML);
		vector.put(USB, "");
		//2 Agrega ese hashMap al arreglo
		todos_vectores.add(vector);
	}

	/*
	 * Constructor con BDD, XML y USB
	 * @param baliza_BDD
	 * @param baliza_XML
	 * @param baliza_USB
	 * <p>1 Mete en un hashMap las valizas BDD, XML y USB pasadas
	 * <p>2 Agrega ese hashMap al arreglo
	 */
	public void put(String baliza_BDD, String baliza_XML, String baliza_USB) {
		Log.e("balizasConversotPut", baliza_BDD + ", " + baliza_XML  + ", " + baliza_USB);
		//1 Mete en un hashMap las valizas BDD, XML y USB pasadas
		HashMap<Integer, String> vector = new HashMap<Integer, String>();
		vector.put(BDD, baliza_BDD);
		vector.put(XML, baliza_XML);
		vector.put(USB, baliza_USB);
		//2 Agrega ese hashMap al arreglo
		todos_vectores.add(vector);
	 }

	 /*
	 * Convierte una baliza xml en una de tabla de bdd
	 * @param bal_xml
	 * @param tabla_bdd
	 * @return
	 * @throws Exception
	 *
	 * <p>1 Obtiene la baliza XML para la bdd pasada
	 * <p>2 Busca esa baliza en todos los HashMaps y lo devuelve
	 * <p>3 Si no encontramos, disparamos un error
	 */
	public String xml2bdd(String bal_xml, String tabla_bdd)   {
		try {
			int indice = -1;
			//1 Obtiene la baliza XML para la bdd pasada
			String codigo_xml_tabla = this.bdd2xml(tabla_bdd);
			Log.e("baliza", codigo_xml_tabla);
			Log.e("bal_xml", bal_xml);
			//2 Busca esa baliza en todos los HashMaps y lo devuelve
			for (HashMap<Integer, String> vector : todos_vectores) {
				if ( (vector.get(XML)).compareTo(bal_xml) == 0 ) {
					indice = todos_vectores.indexOf(vector);
					String posible_resultado = (String) todos_vectores.get(indice).get(BDD);
					if (posible_resultado.contains(codigo_xml_tabla)) {

						Log.e("posible_resultado", posible_resultado);
						return posible_resultado;
					}
				}
			}


		} catch (Exception e) {
			Log.d("prueba",  e.toString());

		}
		return tabla_bdd;

	}

	/*
	 * Convierte una baliza xml en una de tabla de bdd
	 * @param bal_xml
	 * @param tabla_bdd
	 * @return
	 * @throws Exception
	 *
	 * <p>1 Obtiene la baliza XML para la bdd pasada
	 * <p>2 Busca esa baliza en todos los HashMaps y lo devuelve
	 * <p>3 Si no encontramos, disparamos un error
	 */
	public String xml2bddProv(String bal_xml, String tabla_bdd)   {
		try {
			int indice = -1;
			//1 Obtiene la baliza XML para la bdd pasada
			String codigo_xml_tabla = this.bdd2xmlProv(tabla_bdd);
			Log.e("baliza", codigo_xml_tabla);
			Log.e("bal_xml", bal_xml);
			//2 Busca esa baliza en todos los HashMaps y lo devuelve
			for (HashMap<Integer, String> vector : todos_vectores) {

				if ( (vector.get(XML)).compareTo(bal_xml) == 0 ) {
					indice = todos_vectores.indexOf(vector);
					String posible_resultado = (String) todos_vectores.get(indice).get(BDD);
					//if (posible_resultado.contains(codigo_xml_tabla) == true) {

					Log.e("posible_resultado", posible_resultado);
					return posible_resultado;
					//	}
				}
			}


		} catch (Exception e) {
			Log.d("prueba",  e.toString());

		}
		return tabla_bdd;

	}


	/*
	 * Convierte una baliza bdd pasada como parametro en una xml devuelta
	 * <p>1 Busca en todos los vectores la informacion de XML correspondiente a esa
	 *		baliza bdd
	 * <p>2 Si no encontramos, disparamos un error
	 * @param bal_bdd
	 * @return la baliza XML
	 * @throws Exception
	 */
	public String bdd2xml(String bal_bdd) throws Exception {
		int indice = -1;
		//1 Busca en todos los vectores la informacion de XML correspondiente a esa
		//baliza bdd
		for (HashMap<Integer, String> vector : todos_vectores) {
			if ( (vector.get(BDD)).compareTo(bal_bdd) == 0 ) {
				indice = todos_vectores.indexOf(vector);
				return (String) todos_vectores.get(indice).get(XML);
			}
		}

		//2 Si no encontramos, disparamos un error:
		throw new Exception("ERROR EN LA CONVERSIN DE BALIZAS: (BDD) " + bal_bdd + " to XML");
	}

	/*
	 * Convierte una baliza bdd pasada como parametro en una xml devuelta
	 * <p>1 Busca en todos los vectores la informacion de XML correspondiente a esa
	 *		baliza bdd
	 * <p>2 Si no encontramos, disparamos un error
	 * @param bal_bdd
	 * @return la baliza XML
	 * @throws Exception
	 */
	public String bdd2xmlProv(String bal_bdd) throws Exception {
		int indice = -1;
		//1 Busca en todos los vectores la informacion de XML correspondiente a esa
		//baliza bdd
		for (HashMap<Integer, String> vector : todos_vectores) {
			if ( (vector.get(BDD)).compareTo(bal_bdd) == 0 ) {
				indice = todos_vectores.indexOf(vector);
				return (String) todos_vectores.get(indice).get(XML);
			}
		}

		//2 Si no encontramos, disparamos un error:
		throw new Exception("ERROR EN LA CONVERSIN DE BALIZAS: (BDD) " + bal_bdd + " to XML");
	}





	/*
	 * Convierte una baliza usb en una baliza bdd
	 * <p>1 Trasnforma la baliza bdd a codigo usb
	 * <p>2 Busca ese codigo en los vectores
	 * <p>3 Si no encontramos, disparamos un error
	 * @param bal_usb
	 * @param tabla_bdd
	 * @return
	 * @throws Exception
	 */
	public String usb2bdd(String bal_usb, String tabla_bdd) throws Exception {
		try {
			int indice = -1;
			//1 Trasnforma la baliza bdd a codigo usb
			String codigo_usb_tabla = this.bdd2usb(tabla_bdd);
			//2� Busca ese codigo en los vectores
			for (HashMap<Integer, String> vector : todos_vectores) {
				if ( (vector.get(USB)).compareTo(bal_usb) == 0 ) {
					indice = todos_vectores.indexOf(vector);
					String posible_resultado = (String) todos_vectores.get(indice).get(BDD);

					if (posible_resultado.contains(codigo_usb_tabla) == true) {
						return posible_resultado;
					}
				}
			}

			//3� Si no encontramos, disparamos un error:
			throw new Exception("ERROR EN LA CONVERSI�N DE BALIZAS: (USB) " + bal_usb + " to BDD" );

		} catch (Exception e) {

			throw new Exception("ERROR EN LA CONVERSI�N DE BALIZAS: (USB) " + bal_usb + " to BDD");
		}
	}

	/*
	 * Devuelve la baliza usb correspondiente ala baliza bdd pasada como parametro
	 * <p>1� Buscar en todos los vectores la baliza usb de la bdd pasada
	 * <p>2� Si no encontramos, disparamos un error
	 * @param bal_bdd
	 * @return
	 * @throws Exception
	 */
	public String bdd2usb(String bal_bdd) throws Exception {
		int indice = -1;
		//1� Buscar en todos los vectores la baliza usb de la bdd pasada
		for (HashMap<Integer, String> vector : todos_vectores) {
			if ( (vector.get(BDD)).compareTo(bal_bdd) == 0 ) {
				indice = todos_vectores.indexOf(vector);
				return (String) todos_vectores.get(indice).get(USB);
			}
		}

		//2� Si no encontramos, disparamos un error:
		throw new Exception("ERROR EN LA CONVERSI�N DE BALIZAS: (BDD) " + bal_bdd + " to USB");
	}



}






//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///*
// * Clase accesoria que permite las conversiones entre balizas de XML,USB y de BDD
// * @author GuillermoR
// *
// */
//public class BalizasConversor {
//
//
//	public final static int BDD = 1;
//	public final static int XML = 2;
//	public final static int USB = 3;
//
//	/*
//	 * Almacena HashMaps con informaci�n de las Balizas
//	 */
//	private ArrayList<HashMap<Integer, String>> todos_vectores;
//
//
//	/**
//	 * Constructor
//	 */
//	public BalizasConversor(){
//		todos_vectores = new ArrayList<HashMap<Integer,String>>();
//	}
//
//
//	/*
//	 * Constructor con BDD y XML solos
//	 * @param baliza_BDD
//	 * @param baliza_XML
//	 * <p>1� Mete en un hashMap las valizas BDD, XML y "" para USB
//	 * <p>2� Agrega ese hashMap al arreglo
//	 */
//	public void put(String baliza_BDD, String baliza_XML) {
//
//		//1� Mete en un hashMap las valizas BDD, XML y "" para USB
//		HashMap<Integer, String> vector = new HashMap<Integer, String>();
//		vector.put(BDD, baliza_BDD);
//		vector.put(XML, baliza_XML);
//		vector.put(USB, "");
//		//2� Agrega ese hashMap al arreglo
//		todos_vectores.add(vector);
//	}
//
//	/*
//	 * Constructor con BDD, XML y USB
//	 * @param baliza_BDD
//	 * @param baliza_XML
//	 * @param baliza_USB
//	 * <p>1� Mete en un hashMap las valizas BDD, XML y USB pasadas
//	 * <p>2� Agrega ese hashMap al arreglo
//	 */
//	public void put(String baliza_BDD, String baliza_XML, String baliza_USB) {
//		Log.e("balizasConversotPut", baliza_BDD + ", " + baliza_XML  + ", " + baliza_USB);
//		//1� Mete en un hashMap las valizas BDD, XML y USB pasadas
//		HashMap<Integer, String> vector = new HashMap<Integer, String>();
//		vector.put(BDD, baliza_BDD);
//		vector.put(XML, baliza_XML);
//		vector.put(USB, baliza_USB);
//		//2� Agrega ese hashMap al arreglo
//		todos_vectores.add(vector);
//	}
//
//	/*
//	 * Convierte una baliza xml en una de tabla de bdd
//	 * @param bal_xml
//	 * @param tabla_bdd
//	 * @return
//	 * @throws Exception�
//	 *
//	 * <p>1� Obtiene la baliza XML para la bdd pasada
//	 * <p>2� Busca esa baliza en todos los HashMaps y lo devuelve
//	 * <p>3� Si no encontramos, disparamos un error
//	 */
//	@Nullable
//    public String xml2bdd(@NonNull String bal_xml, @NonNull String tabla_bdd)   {
//		try {
//			int indice = -1;
//			//1� Obtiene la baliza XML para la bdd pasada
//			String codigo_xml_tabla = this.bdd2xml(tabla_bdd);
//			Log.e("baliza", codigo_xml_tabla);
//			Log.e("bal_xml", bal_xml);
//			//2� Busca esa baliza en todos los HashMaps y lo devuelve
//			for (HashMap<Integer, String> vector : todos_vectores) {
//				if ( (vector.get(XML)).compareTo(bal_xml) == 0 ) {
//					indice = todos_vectores.indexOf(vector);
//					String posible_resultado = (String) todos_vectores.get(indice).get(BDD);
//
//					if (posible_resultado.contains(codigo_xml_tabla)) {
//
//						Log.e("posible_resultado", posible_resultado);
//						return posible_resultado;
//					}
//				}
//			}
//
//
//		} catch (Exception e) {
//			Log.d("prueba",  e.toString());
//
//		}
//		return tabla_bdd;
//
//	}
//
//	/*
//	 * Convierte una baliza bdd pasada como parametro en una xml devuelta
//	 * <p>1� Busca en todos los vectores la informacion de XML correspondiente a esa
//	 *		baliza bdd
//	 * <p>2� Si no encontramos, disparamos un error
//	 * @param bal_bdd
//	 * @return la baliza XML
//	 * @throws Exception
//	 */
//	@Nullable
//    public String bdd2xml(@NonNull String bal_bdd) throws Exception {
//		int indice = -1;
//		//1� Busca en todos los vectores la informacion de XML correspondiente a esa
//		//baliza bdd
//		for (HashMap<Integer, String> vector : todos_vectores) {
//
//			if ( (vector.get(BDD)).compareTo(bal_bdd) == 0 ) {
//				indice = todos_vectores.indexOf(vector);
//				return (String) todos_vectores.get(indice).get(XML);
//			}
//		}
//
//		//2� Si no encontramos, disparamos un error:
//		throw new Exception("ERROR EN LA CONVERSI�N DE BALIZAS: (BDD) " + bal_bdd + " to XML");
//	}
//
//	/*
//	 * Convierte una baliza usb en una baliza bdd
//	 * <p>1� Trasnforma la baliza bdd a codigo usb
//	 * <p>2� Busca ese codigo en los vectores
//	 * <p>3� Si no encontramos, disparamos un error
//	 * @param bal_usb
//	 * @param tabla_bdd
//	 * @return
//	 * @throws Exception
//	 */
//	@Nullable
//    public String usb2bdd(@NonNull String bal_usb, @NonNull String tabla_bdd) throws Exception {
//		try {
//			int indice = -1;
//			//1� Trasnforma la baliza bdd a codigo usb
//			String codigo_usb_tabla = this.bdd2usb(tabla_bdd);
//			//2� Busca ese codigo en los vectores
//			for (HashMap<Integer, String> vector : todos_vectores) {
//				if ( (vector.get(USB)).compareTo(bal_usb) == 0 ) {
//					indice = todos_vectores.indexOf(vector);
//					String posible_resultado = (String) todos_vectores.get(indice).get(BDD);
//
//					if (posible_resultado.contains(codigo_usb_tabla) == true) {
//						return posible_resultado;
//					}
//				}
//			}
//
//			//3� Si no encontramos, disparamos un error:
//			throw new Exception("ERROR EN LA CONVERSI�N DE BALIZAS: (USB) " + bal_usb + " to BDD" );
//
//		} catch (Exception e) {
//
//			throw new Exception("ERROR EN LA CONVERSI�N DE BALIZAS: (USB) " + bal_usb + " to BDD");
//		}
//	}
//
//	/*
//	 * Devuelve la baliza usb correspondiente ala baliza bdd pasada como parametro
//	 * <p>1� Buscar en todos los vectores la baliza usb de la bdd pasada
//	 * <p>2� Si no encontramos, disparamos un error
//	 * @param bal_bdd
//	 * @return
//	 * @throws Exception
//	 */
//	@Nullable
//    public String bdd2usb(@NonNull String bal_bdd) throws Exception {
//		int indice = -1;
//		//1� Buscar en todos los vectores la baliza usb de la bdd pasada
//		for (HashMap<Integer, String> vector : todos_vectores) {
//			if ( (vector.get(BDD)).compareTo(bal_bdd) == 0 ) {
//				indice = todos_vectores.indexOf(vector);
//				return (String) todos_vectores.get(indice).get(USB);
//			}
//		}
//
//		//2� Si no encontramos, disparamos un error:
//		throw new Exception("ERROR EN LA CONVERSI�N DE BALIZAS: (BDD) " + bal_bdd + " to USB");
//	}
//}
