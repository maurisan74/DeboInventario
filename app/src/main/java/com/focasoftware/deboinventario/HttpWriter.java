package com.focasoftware.deboinventario;



import org.w3c.dom.Document;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
 * Clase para convertir un documento en forma de DOM a un archivo XML para su posterior
 * uso o envo
 * @author GuillermoR
 *
 */
public class HttpWriter{



    /*
     * Convierte un documento DOM en un archivo XML a la direccion y nombre
     * indicados en el url
     * <p>1 Creacion del fuente del DOM
     * <p>2 Creacion del fichero de salida
     * <p>3 Configuracion del transformador de documento
     * <p>4 Realizamos la transformacion
     *
     */
    public static void transformerXml(Document document, String urlArchivo) {
        try {
            //1 Creacion del fuente del DOM
            Source source = new DOMSource(document);

            //2 Creacion del fichero de salida
            File archivo = new File(urlArchivo);
            File parent = new File(archivo.getParent());
            if (!parent.exists()) {
                archivo.mkdirs();
            }
            Result resultat = new StreamResult(archivo);

            //3 Configuracion del transformador de documento
            TransformerFactory fabricaTransformacion = TransformerFactory.newInstance();
            Transformer transformador = fabricaTransformacion.newTransformer();
            transformador.setOutputProperty(OutputKeys.INDENT, "yes");
            transformador.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            //4 Realizamos la transformacion
            transformador.transform(source, resultat);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}











//
//import androidx.annotation.NonNull;
//import org.w3c.dom.Document;
//import java.io.File;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Result;
//import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import java.util.Objects;
//
//import static android.content.Context.MODE_PRIVATE;
//
///*
// * Clase para convertir un documento en forma de DOM a un archivo XML para su posterior
// * uso o env�o
// * @author GuillermoR
// *
// */
//public class HttpWriter{
//    /*
//	 * Convierte un documento DOM en un archivo XML a la direccion y nombre
//	 * indicados en el url
//	 * <p>1� Creacion del fuente del DOM
//	 * <p>2� Creacion del fichero de salida
//	 * <p>3� Configuracion del transformador de documento
//	 * <p>4� Realizamos la transformacion
//	 *
//	 */
//	public static void transformerXml(Document document, @NonNull String urlArchivo) {
//        try {
//            //1� Creacion del fuente del DOM
//            Source source = new DOMSource(document);
//
//            //2� Creacion del fichero de salida
//            File archivo = new File(urlArchivo);
//            File parent = new File(Objects.requireNonNull(archivo.getParent()));
//            if (!parent.exists()) {
//            	archivo.mkdirs();
//            }
//
////            FileInputStream fIS = new FileInputStream (new File("/Dir/data.txt"));
//
//
//
//            Result resultat = new StreamResult(archivo);
//
//            //3� Configuracion del transformador de documento
//            TransformerFactory fabricaTransformacion = TransformerFactory.newInstance();
//            Transformer transformador = fabricaTransformacion.newTransformer();
//            transformador.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformador.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
//
//            //4� Realizamos la transformacion
//            transformador.transform(source, resultat);
////            osw.close();
//        }catch(Exception e){
//        	e.printStackTrace();
//        }
//    }
//
//
//	/*
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//    {
//		// Por fin creamos la p�gina:
//		super.onCreate(savedInstanceState);
//
//		try{
//			hash1.put("ID_MED","1");
//			hash1.put("PER","2011/07");
//			hash1.put("LEAN", "1000");
//			hash1.put("LEAC","2000");
//			hash1.put("VAL","123456");
//			hash1.put("FECHA_TOMA", "2011/08/15 11:30:25");
//			hash1.put("ID_ERROR","0");
//			hash1.put("OBSERVACION","");
//			hash1.put("ID_OPE", "1");
//
//			listaTodasMediciones.put("1",hash1);
//			//listaTodasMediciones.put("2",hash2);
//			//listaTodasMediciones.put("3",hash3);
//
//			// Cr�ation d'un nouveau DOM
//			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
//			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
//			Document document = constructeur.newDocument();
//
//			// Propri�t�s du DOM
//			document.setXmlVersion("1.0");
//			document.setXmlStandalone(true);
//
//			// Cr�ation de l'arborescence du DOM
//			//Element raiz = document.createElement("WEBs");
//
//			Element titulo = document.createElement("VI_AGUA_FROM_TABLET");
//			//raiz.appendChild(titulo);
//
//			Enumeration<String> enumeration = listaTodasMediciones.keys();
//			while (enumeration.hasMoreElements() == true) {
//				String key = enumeration.nextElement();
//				Hashtable<String,String> hash = listaTodasMediciones.get(key);
//
//				Element medicion = document.createElement("MEDICION");
//				Enumeration<String> e = hash.keys();
//				while (e.hasMoreElements() == true) {
//					String key2 = e.nextElement();
//					String value2 = hash.get(key2);
//					Element elemento = document.createElement(key2);
//					elemento.setTextContent(value2);
//					medicion.appendChild(elemento);
//				}
//				titulo.appendChild(medicion);
//			}
//
//			//raiz.appendChild(titulo);
//			document.appendChild(titulo); //raiz);
//
//			//Sauvegarde du DOM dans un fichier XML
//			transformerXml(document, ParametrosAgua.URL_DOCUMENTO_XML);
//
//			HttpSender sender = new HttpSender(Parametros.CODIGO_SOFT_DEBOAGUA);
//			sender.send_xml(ParametrosAgua.URL_DOCUMENTO_XML);
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	*/
//}
