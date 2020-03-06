package com.focasoftware.deboinventario;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.RadioGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// ABALEGNO 17/10/2014
//import android.provider.Telephony.Mms.Part;

/*
 * Clase que maneja las conexiones y todas las operaciones de las Bases de
 * Datos.
 *
 * @author GuillermoR
 *
 */

public class BaseDatos extends SQLiteOpenHelper {

	// *************************
	// *************************
	// **** ATRIBUTOS ****
	// *************************
	// *************************
	//

	/*
	 * Nombres de las Tablas de la base de datos parametrizadas
	 */

	private String tabla_articulos_nombre = ParametrosInventario.tabla_articulos;
	private String tabla_inventarios_nombre = ParametrosInventario.tabla_inventarios;
	private String tabla_referencias_nombre = ParametrosInventario.tabla_referencias;

	private String tabla_proveedores_nombre = ParametrosInventario.tabla_proveedores;
	private String tabla_compraproveedor_nombre = ParametrosInventario.tabla_compra_proveedor;

	private RadioGroup RadioGroupProductosNoContabilizados;
	private int ProductosNoContabilizados;

	private int condR =0;

	/*Variable para cuando esta activada la balanza*/
	private String codcompleto = "";
	private String pesoObtenido = "";

	/*
	 * Variables para almacenar las SQLs de Creaci�n de las tablas:
	 */
	private String sqlCreateTablaArticulos = "CREATE TABLE IF NOT EXISTS "
			+ tabla_articulos_nombre + " ("
			+ ParametrosInventario.bal_bdd_articulo_sector + " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_articulo_codigo + " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_articulo_balanza + " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_articulo_decimales + " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_articulo_codigo_barra + " VARCHAR(150) DEFAULT NULL " + ", "
			+ ParametrosInventario.bal_bdd_articulo_codigo_barra_completo + " VARCHAR(150) DEFAULT NULL " + ", "
			+ ParametrosInventario.bal_bdd_articulo_inventario + " INTEGER"
			+ ", " + ParametrosInventario.bal_bdd_articulo_descripcion + " VARCHAR(50)" + ", "
			+ ParametrosInventario.bal_bdd_articulo_precio_venta + " REAL"
			+ ", " + ParametrosInventario.bal_bdd_articulo_precio_costo
			+ " REAL" + ", " + ParametrosInventario.bal_bdd_articulo_foto + " VARCHAR(100)" + ", "
			+ ParametrosInventario.bal_bdd_articulo_cantidad + " FLOAT"
			+ ", "
			+ ParametrosInventario.bal_bdd_articulo_subtotal + " FLOAT"
			+ ", "
			+ ParametrosInventario.bal_bdd_articulo_pesaje + " FLOAT"
			+ ", "
			+ ParametrosInventario.bal_bdd_articulo_existencia_venta + " REAL"
			+ ", "
			+ ParametrosInventario.bal_bdd_articulo_existencia_deposito + " REAL"
			+ ", "
			+ ParametrosInventario.bal_bdd_articulo_depsn
			+ " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_articulo_fechaInicio
			+ " VARCHAR(24)" + ", "
			+ ParametrosInventario.bal_bdd_articulo_fechaFin + " VARCHAR(24)"
			+ ", " + "PRIMARY KEY ("
			+ ParametrosInventario.bal_bdd_articulo_sector + ", "
			+ ParametrosInventario.bal_bdd_articulo_codigo + ", "
			+ ParametrosInventario.bal_bdd_articulo_inventario + ")" + " )";

	/*
	 * Creacion de la tabla proveedores:
	 */
	private String sqlCreateTablaProveedores = "CREATE TABLE IF NOT EXISTS "
			+ tabla_proveedores_nombre + " ("
			+ ParametrosInventario.bal_bdd_proveedores_codigo + " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_proveedores_descripcion
			+ " VARCHAR(150)  DEFAULT NULL " +   ", " + "PRIMARY KEY ("
			+ ParametrosInventario.bal_bdd_proveedores_codigo + ")" + " )";

	/*
	 * Creacion de la tabla COMPRA_PROVEEDOR para relacionar la compra con un proveedor:
	 */
	private String sqlCreateTablaCompraProveedor = "CREATE TABLE IF NOT EXISTS "
			+ tabla_compraproveedor_nombre + " ("
			+ ParametrosInventario.bal_bdd_compraproveedor_inventario + " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_compraproveedor_codigo
			+ " INTEGER )";


	private String sqlCreateTablaInventarios = "CREATE TABLE IF NOT EXISTS "
			+ tabla_inventarios_nombre + " ("
			+ ParametrosInventario.bal_bdd_inventario_numero + " INTEGER"
			+ ", " + ParametrosInventario.bal_bdd_inventario_descripcion
			+ " VARCHAR(50)" + ", "
			+ ParametrosInventario.bal_bdd_inventario_fechaInicio
			+ " VARCHAR(24)" + ", "
			+ ParametrosInventario.bal_bdd_inventario_fechaFin + " VARCHAR(24)"
			+ ", " + ParametrosInventario.bal_bdd_inventario_estado
			+ " INTEGER" + ", " + ParametrosInventario.bal_bdd_inventario_lugar
			+ " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_inventario_prodcont
			+ " INTEGER" + ", "+ "PRIMARY KEY ("
			+ ParametrosInventario.bal_bdd_inventario_numero + ")" + " )";

	private String sqlCreateTablaReferencias = "CREATE TABLE IF NOT EXISTS "
			+ tabla_referencias_nombre + " ("
			+ ParametrosInventario.bal_bdd_referencia_sector + " INTEGER"
			+ ", " + ParametrosInventario.bal_bdd_referencia_codigo
			+ " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_referencia_balanza
			+ " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_referencia_decimales
			+ " INTEGER" + ", "
			+  ParametrosInventario.bal_bdd_referencia_existencia_venta
			+ " REAL" + ", "
			+ ParametrosInventario.bal_bdd_referencia_existencia_deposito
			+ " REAL" + ", "
			+ ParametrosInventario.bal_bdd_referencia_codigo_barra
			+ " VARCHAR(150)" + ", "
			+ ParametrosInventario.bal_bdd_referencia_codigo_barra_completo
			+ " VARCHAR(150)" + ", "
			+ ParametrosInventario.bal_bdd_referencia_descripcion
			+ " VARCHAR(50)" + ", "
			+ ParametrosInventario.bal_bdd_referencia_precio_venta
			+ " REAL"
			+ ", "
			+ ParametrosInventario.bal_bdd_referencia_precio_costo
			+ " REAL" + ", "
			+ ParametrosInventario.bal_bdd_referencia_depsn
			+ " INTEGER" + ", "
			+ ParametrosInventario.bal_bdd_referencia_foto
			+ " VARCHAR(100)" + ", " + "PRIMARY KEY ("
			+ ParametrosInventario.bal_bdd_referencia_codigo_barra + ")" + " )";

	private String sqlCreateTableLocales = "CREATE TABLE IF NOT EXISTS ["
			+ ParametrosInventario.tabla_local + "] ( " + "["
			+ ParametrosInventario.bal_bdd_local_idLocal
			+ "] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," + "["
			+ ParametrosInventario.bal_bdd_local_nombre
			+ "] VARCHAR(25) DEFAULT NULL," + "["
			+ ParametrosInventario.bal_bdd_local_descripcion
			+ "] VARCHAR(512) DEFAULT NULL" + ")";

	// *****************************
	// *****************************
	// **** CONSTRUCTORES ****
	// *****************************
	// *****************************

	/*
	 * Constructor de la clase BaseDatos
	 *
	 * @param contexto
	 * @param nombre
	 * @param factory
	 * @param version
	 */
	public BaseDatos(Context contexto, String nombre, CursorFactory factory, int version) {
		super(contexto, ParametrosInventario.BDD_NOMBRE, null, ParametrosInventario.BDD_VERSION);
	}

	/*
	 * Constructor de la clase BaseDatos con 2 parametros
	 */
	public BaseDatos(Context contexto) {
		this(contexto, ParametrosInventario.BDD_NOMBRE, null, ParametrosInventario.BDD_VERSION);
		System.out.println("::: BaseDatos 177 Version en BD " + ParametrosInventario.VERSION);
	}

	// ***********************
	// ***********************
	// **** METODOS ****
	// ***********************
	// ***********************
	/*
	 * Al llamarse este metodo se crean las tablas de nuevo?
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Se ejecuta la sentencia SQL de creaci�n de la tabla
		db.execSQL(sqlCreateTablaArticulos);
		db.execSQL(sqlCreateTablaInventarios);
		db.execSQL(sqlCreateTablaReferencias);
		db.execSQL(sqlCreateTableLocales);
		db.execSQL(sqlCreateTablaProveedores);
		db.execSQL(sqlCreateTablaCompraProveedor);
	}

	/*
	 * Funcion para crear desde cero y vaciar las tablas de articulos e
	 * inventarios
	 * <p>
	 * 1� Las borramos
	 * 2� Las creamos de nuevo
	 */
	public void reiniciarArticulosInventarios() {
		System.out.println("::: BaseDatos 206 reiniciarArticulosInventarios");
		SQLiteDatabase dtb = this.getWritableDatabase();

		// 1� Las borramos
		dtb.execSQL("DROP TABLE IF EXISTS " + tabla_articulos_nombre);
		dtb.execSQL("DROP TABLE IF EXISTS " + tabla_inventarios_nombre);

		// 2� Las creamos de nuevo
		dtb.execSQL(sqlCreateTablaArticulos);
		dtb.execSQL(sqlCreateTablaInventarios);

		dtb.close();

	}

	public void crearTablaLocales() {
		System.out.println("::: BaseDatos 222 crearTablaLocales");
		SQLiteDatabase dtb = this.getWritableDatabase();
		dtb.execSQL(sqlCreateTableLocales);
		dtb.close();
	}

	public void guardarLocal(Local pLocal) {
		try {
			System.out.println("::: BaseDatos 230 guardarLocal");
			SQLiteDatabase dtb = this.getWritableDatabase();
			if (dtb != null) {

				ContentValues nuevoRegistro = new ContentValues();
				nuevoRegistro.put(ParametrosInventario.bal_bdd_local_nombre,
						pLocal.getNombre());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_local_descripcion,
						pLocal.getDescripcion());

				// 4� Insertamos el registro en la base de datos
				long resultado = dtb.insert(ParametrosInventario.tabla_local,
						null, nuevoRegistro);

				// Test resultado INSERT:
				if (resultado < 0) {
				}
			}

			// 5� Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

		}

	}

	public void actualizarLocal(Local pLocal) {
		try {
			System.out.println("::: BaseDatos 266 actualizarLocal");
			SQLiteDatabase dtb = this.getWritableDatabase();

			ContentValues registro = new ContentValues();
			registro.put(ParametrosInventario.bal_bdd_local_nombre,
					pLocal.getNombre());
			registro.put(ParametrosInventario.bal_bdd_local_descripcion,
					pLocal.getDescripcion());

			// 4� Insertamos el registro en la base de datos
			long resultado = dtb.update(ParametrosInventario.tabla_local,
					registro, ParametrosInventario.bal_bdd_local_idLocal
							+ " = " + pLocal.getIdLocal(), null);
			// 5� Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

		}

	}

	public Local ObtenerLocal_x_Id(int pIdLocal) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 291 ObtenerLocal_x_Id");
			// 1� Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();

			String sql = "SELECT * FROM " + ParametrosInventario.tabla_local
					+ " WHERE " + ParametrosInventario.bal_bdd_local_idLocal
					+ " = " + pIdLocal;
			Cursor c = dtb.rawQuery(sql, null);
			Local local = new Local(null, null);
			// Result:
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					// 3� Agregamos cada numero a la lista
					int idLocal = c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_local_idLocal));
					String nombreLocal = c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_local_nombre));
					String descripcionLocal = c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_local_descripcion));

					local.setIdLocal(idLocal);
					local.setDescripcion(descripcionLocal);
					local.setNombre(nombreLocal);

					c.moveToNext();
				}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
						"Imposible obtener el local.");
			}

			// 4� Cierre de BD
			dtb.close();
			return local;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.log("[-- 2976 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT, "Imposible obtener los locales");
		}
	}

	public ArrayList<Local> ObtenerTodosLocales() throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 341 ObtenerTodosLocales");
			// Variable de respuesta:
			ArrayList<Local> LocalesRetornables = new ArrayList<Local>();

			// 1� Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();

			// Request:
			String[] col = new String[] {
					ParametrosInventario.bal_bdd_local_idLocal,
					ParametrosInventario.bal_bdd_local_nombre,
					ParametrosInventario.bal_bdd_local_descripcion };
			// 2� Ejecutamos la consulta
			Cursor c = dtb.query(ParametrosInventario.tabla_local, col, null, null, null, null, null);

			// Result:
			if (c.moveToFirst()) {
				do {
					// 3� Agregamos cada numero a la lista
					int idLocal = c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_local_idLocal));
					String nombreLocal = c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_local_nombre));
					String descripcionLocal = c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_local_descripcion));
					Local loc = new Local(nombreLocal, descripcionLocal, idLocal);
					LocalesRetornables.add(loc);
				} while (c.moveToNext());
			}
			dtb.close();
			return LocalesRetornables;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.log("[-- 2976 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT, "Imposible obtener los locales");
		}
	}

	/*
	 * Al actualizar la version se regeneran las tablas.�Se guarda la
	 * informacion anterior?
	 * <p>
	 * 1� Iniciamos la transaccion:
	 * <p>
	 * 2� Cambiamos los nombres de las tablas que existen hacia temp
	 * <p>
	 * 3� Creamos las tablas
	 * <p>
	 * 4� Se eliminan las tablas temporarias
	 * <p>
	 * 5� Se concluye el upgrade
	 */

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
		System.out.println("::: BaseDatos 408 onUpgrade");
		// 1� Iniciamos la transaccion:
		db.beginTransaction();

		// 2� Cambiamos los nombres de las tablas que existen hacia temp:
		db.execSQL("ALTER TABLE " + tabla_articulos_nombre + " RENAME TO temp_"
				+ tabla_articulos_nombre);
		db.execSQL("ALTER TABLE " + tabla_inventarios_nombre
				+ " RENAME TO temp_" + tabla_inventarios_nombre);

		// 3� Creamos las tablas:
		db.execSQL(sqlCreateTablaArticulos);
		db.execSQL(sqlCreateTablaInventarios);

		//lo de abajo estaba descomentado 999
		db.execSQL(sqlCreateTablaReferencias);
		db.execSQL(sqlCreateTablaProveedores);
		db.execSQL(sqlCreateTablaCompraProveedor);
		// Recopiamos los datos de las tablas:
		/*
		 * db.execSQL("INSERT INTO " + tabla_articulos_nombre + " " +
		 * "SELECT * from temp_" + tabla_articulos_nombre );
		 */
		/*
		 * db.execSQL("INSERT INTO " + tabla_inventarios_nombre + " " +
		 * "SELECT * from temp_" + tabla_inventarios_nombre );
		 */
		// 4� Se elimina las tablas temporarias:
		db.execSQL("DROP TABLE IF EXISTS temp_" + tabla_articulos_nombre);
		db.execSQL("DROP TABLE IF EXISTS temp_" + tabla_inventarios_nombre);

		// 5� Se concluye el upgrade:
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/*
	 * Borra los datos de una cierta tabla pasada como parametro Hardcodeado
	 * para que si es la de referencias y no se desea borrar todo se borre solo
	 * los articulos no nuevos (codigo y sector > 0)
	 * <p>
	 * 1� Abrimos la base
	 * <p>
	 * 2�Suprimimos todas (o algunas de) las entradas de las tablas, sin
	 * suprimir las tablas
	 * <p>
	 *
	 * @param nombre_tabla
	 * @param borrarTodo
	 * @throws ExceptionBDD
	 */
	public void borrarDatosBDD(String nombre_tabla, boolean borrarTodo) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 461 borrarDatosBDD");
			// 1� Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			String sentencia;

			if (nombre_tabla.compareTo(tabla_referencias_nombre) == 0
					&& !borrarTodo) {

				sentencia = "DELETE FROM " + nombre_tabla + " WHERE "
						+ ParametrosInventario.bal_bdd_referencia_codigo
						+ " >= 0 AND "
						+ ParametrosInventario.bal_bdd_referencia_sector
						+ " >= 0 ";

			} else {

				sentencia = "DELETE FROM " + nombre_tabla;

			}
			// 2�Suprimimos todas las entradas de las tablas, sin suprimir las
			// tablas:
			dtb.execSQL(sentencia);
			//
			// dtb.execSQL("DROP TABLE IF EXISTS " + nombre_tabla);
			//
			// if (nombre_tabla.compareTo(tabla_articulos_nombre) == 0) {
			// dtb.execSQL(sqlCreateTablaArticulos);
			// }
			// else if (nombre_tabla.compareTo(tabla_inventarios_nombre) == 0) {
			// dtb.execSQL(sqlCreateTablaInventarios);
			// }
			// else if (nombre_tabla.compareTo(tabla_referencias_nombre) == 0) {
			// dtb.execSQL(sqlCreateTablaReferencias);
			// }

			// Cierre:
			dtb.close();

		} catch (Exception e) {

			// ///////////GESTOR DE LOG///////////////////
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 301--]"
					+ "Imposible suprimir el contenido de las tablas: "
					+ e.toString(), 4);
			// ///////////////////////////////////////////

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE,
					"Imposible suprimir el contenido de las tablas: "
							+ e.toString());
		}
	}

	/*
	 * Borra el articulo sector-codigo del inventario con nro de inventario = a
	 * inventario
	 * <p>
	 * 1� Abrimos la base:
	 * <p>
	 * 2� Suprimimos la entrada correspondiente los articulos
	 * <p>
	 * 3� Cierre de la bd
	 *
	 * @param sector
	 * @param codigo
	 * @param inventario
	 * @throws ExceptionBDD
	 */
	public void borrarArcticuloInventario(int sector, int codigo, int inventario) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 535 borrarArcticuloInventario");
			// 1� Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			// 2� Suprimimos la entrada correspondiente a los articulos
			dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
					+ ParametrosInventario.bal_bdd_articulo_codigo + "="
					+ String.valueOf(codigo) + " AND "
					+ ParametrosInventario.bal_bdd_articulo_sector + "="
					+ String.valueOf(sector) + " AND "
					+ ParametrosInventario.bal_bdd_articulo_inventario + "="
					+ String.valueOf(inventario));

			// 3� Cierre de la bd
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 349 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE, "Imposible suprimir el articulo del inventario");

		}

	}

	/*
	 * Elimina el inventario con id_inventario y sus articulos
	 * <p>
	 * 1 Abrimos la base
	 * <p>
	 * 2 Suprimimos la entrada correspondiente los articulos y luego el
	 * inventario
	 * <p>
	 * 3 Cierre de la BD
	 *
	 * @param id_inventario
	 * @throws ExceptionBDD
	 */
	public void borrarInventarioConArticulos(int id_inventario) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 581 borrarInventarioConArticulos");
			boolean condicionRadio = ParametrosInventario.InventariosVentas;
			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			if(condicionRadio && id_inventario==-1){
				dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_inventario_numero + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_articulo_inventario + "="
						+ String.valueOf(id_inventario));
			}else if(condicionRadio==false && id_inventario==-2){
				dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_inventario_numero + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_articulo_inventario + "="
						+ String.valueOf(id_inventario));
			}else if(id_inventario==-3){
				dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_inventario_numero + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_articulo_inventario + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_compraproveedor_nombre+ " WHERE COMPRA_INV_COD="
						+ String.valueOf(id_inventario));
			}else if(id_inventario<-3){
				dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_inventario_numero + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_articulo_inventario + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_compraproveedor_nombre+ " WHERE COMPRA_INV_COD="
						+ String.valueOf(id_inventario));
			}else if(id_inventario>0){
				dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_inventario_numero + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_articulo_inventario + "="
						+ String.valueOf(id_inventario));
			}
//			// 2 Suprimimos la entrada correspondiente los articulos y luego el
//			// inventario
//			dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre + " WHERE "
//					+ ParametrosInventario.bal_bdd_inventario_numero + "="
//					+ String.valueOf(id_inventario));
//			dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
//					+ ParametrosInventario.bal_bdd_articulo_inventario + "="
//					+ String.valueOf(id_inventario));
			// 3 Cierre de la BD
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 394 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE,
					"Imposible suprimir el contenido de las tablas");
		}

	}

	public void borrarInventarioConArticulosCompras(int id_inventario) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 581 borrarInventarioConArticulos");
			boolean condicionRadio = ParametrosInventario.InventariosVentas;
			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			if(id_inventario==-3){
				dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_inventario_numero + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_articulos_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_articulo_inventario + "="
						+ String.valueOf(id_inventario));
				dtb.execSQL("DELETE FROM " + tabla_compraproveedor_nombre + " WHERE "
						+ ParametrosInventario.bal_bdd_inventario_numero + "="
						+ String.valueOf(id_inventario));
				System.out.println("::: BORRO SEGURO");
			}

			// 3 Cierre de la BD
			dtb.close();
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 394 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE,
					"Imposible suprimir el contenido de las tablas");
		}

	}

	/*
	 * Borra los inventarios de la lista con sus articulos
	 * <p>
	 * 1 Abrimos la base
	 * <p>
	 * 2 Para cada inventario : Suprimimos la entrada correspondiente al
	 * inventario y articulos
	 * <p>
	 * 3 Cierre
	 *
	 * @param lista_inventarios
	 * @throws ExceptionBDD
	 */
	public void borrarInventarioConArticulos(ArrayList<Integer> lista_inventarios) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 626 borrarInventarioConArticulos");
			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			// 2 Para cada inventario : Suprimimos la entrada correspondiente
			// al inventario y articulos
			for (int numero : lista_inventarios) {
				try {
					dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre
							+ " WHERE "
							+ ParametrosInventario.bal_bdd_inventario_numero
							+ "=" + String.valueOf(numero));
					dtb.execSQL("DELETE FROM " + tabla_articulos_nombre
							+ " WHERE "
							+ ParametrosInventario.bal_bdd_articulo_inventario
							+ "=" + String.valueOf(numero));
				} catch (Exception e) {

					GestorLogEventos log = new GestorLogEventos();
					log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
					log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
					log.log("[-- 438 --]" + e.toString(), 4);

				}
			}

			// 3 Cierre:
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 451 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE,
					"Imposible suprimir el contenido de las tablas");
		}

	}

	/*
	 * Limpia los inventarios y articulos de la BD
	 * <p>
	 * 1 Abrimos la base
	 * <p>
	 * 2 Suprimimos todas las entradas de las tablas, sin suprimir las tablas
	 * <p>
	 * 3 Cierre
	 *
	 * @throws ExceptionBDD
	 */
	public void borrarInventariosYArticulosEnBDD_y_tambien_locales() throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 682 borrarInventariosYArticulosEnBDD_y_tambien_locales");
			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			// 2 Suprimimos todas las entradas de las tablas, sin suprimir las tablas:
			dtb.execSQL("DELETE FROM " + tabla_articulos_nombre);
			dtb.execSQL("DELETE FROM " + tabla_inventarios_nombre);
			dtb.execSQL("DROP TABLE IF EXISTS "+ ParametrosInventario.tabla_local);
			dtb.execSQL(sqlCreateTableLocales);
			dtb.execSQL("DROP DATABASE DB_INVENT");
			// 3 Cierre:
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 488 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE,
					"Tabla no encontrada en la base de datos");
		}
	}

	/*
	 * Busca en las referencias articulos con descripciones parecidas a las
	 * pasadas en la cadena de parametro
	 * <p>
	 * 1 Abrimos la base
	 * <p>
	 * 2Procesar la cadena de busqueda (un espacio blanco cuenta como un &&):
	 * Reemplazamos los multiples espacios blancos por 1 solo
	 * <p>
	 * 3 Partimos el string en una tabla segun el caracter ' '
	 * <p>
	 * 4 Construimos la consulta SQL
	 * <p>
	 * 5 Buscar en las referencias el articulo
	 * <p>
	 * 6 Control respuestas
	 * <p>
	 * 7 Lectura de los resultados
	 * <p>
	 * 8 Cerramos la BD
	 *
	 * @param busqueda
	 * @return
	 * @throws ExceptionBDD
	 */
	public ArrayList<HashMap<Integer, Object>> buscarEnReferencias(String busqueda) throws ExceptionBDD {
		// toModify;
		try {
			System.out.println("::: BaseDatos 738 buscarEnReferencias");
			// Variable de retorno:Revisar esto
			ArrayList<HashMap<Integer, Object>> lista_resultado = new ArrayList<HashMap<Integer, Object>>();

			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			// 2Procesar la cadena de busqueda (un espacio blanco cuenta com un &&):
			// Reemplazamos los multiples espacios blancos por 1 solo:
			busqueda = busqueda.replaceAll("\\s+", " ");

			// 3 Partimos el string en una tabla segun el caracter ' ':
			String tabla_busqueda[] = busqueda.split("\\s");

			// 4 Construimos la consulta SQL:
			String consulta_SQL = "";
			for (String s : tabla_busqueda) {
				consulta_SQL += ParametrosInventario.bal_bdd_referencia_descripcion + " LIKE '%" + s + "%' AND ";
			}
			consulta_SQL = consulta_SQL.substring(0, consulta_SQL.length() - 4);
			// consulta_SQL += "AND " +
			// ParametrosInventario.bal_bdd_articulo_inventario + "=" +
			// num_inventario;

			// 5 Buscar en las referencias el articulo
			// Mas columnas, o todo
			String[] col = new String[] {
					ParametrosInventario.bal_bdd_referencia_sector,
					ParametrosInventario.bal_bdd_referencia_codigo,
					ParametrosInventario.bal_bdd_referencia_descripcion
					// ParametrosInventario.bal_bdd_referencia_codigo_barra,
					// ParametrosInventario.bal_bdd_referencia_precio_venta,
					// ParametrosInventario.bal_bdd_referencia_precio_costo
			};

			Cursor c = dtb.query(tabla_referencias_nombre, col, consulta_SQL,
					null, ParametrosInventario.bal_bdd_referencia_codigo + ","
							+ ParametrosInventario.bal_bdd_referencia_sector,
					null, ParametrosInventario.bal_bdd_referencia_descripcion);

			// 6 Control respuestas:
			if (c.getCount() <= 0) {
				dtb.close();
				return lista_resultado;
			} else if (c.getCount() > ParametrosInventario.MAX_SQL_RESPUESTAS) {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_TOO_MANY_RESULTS);
			}
			// ArticuloVisible nvoArt;
			// int codigo,sector;
			// String desc,codigoBarra;
			// double precioVenta, precioCosto;
			// ArrayList<String> losCodigosBarras;
			// 7 Lectura de los resultados:
			if (c.moveToFirst()) {
				//		System.out.println(":::ACA SI ENTROOOOOOO");
				while (c.isAfterLast() == false) {
					HashMap<Integer, Object> hashmap = new HashMap<Integer, Object>();
					// Crear un articulo con los datos extraidos
					// codigo=c.getInt(0);
					// sector=c.getInt(1);
					// desc=c.getString(2);
					// codigoBarra=c.getString(3);
					// precioVenta=c.getDouble(4);
					// precioCosto=c.getDouble(5);
					// losCodigosBarras= new ArrayList<String>();
					// losCodigosBarras.add(codigoBarra);
					// nvoArt=new ArticuloVisible(sector, codigo,
					// losCodigosBarras, 0, desc, precioVenta, precioCosto, "",
					// -1, "", true);
					// Meterlo en el hash map
					hashmap.put(ParametrosInventario.clave_art_sector,
							c.getInt(0));
					hashmap.put(ParametrosInventario.clave_art_codigo,
							c.getInt(1));
					hashmap.put(ParametrosInventario.clave_art_nombre,
							c.getString(2));
					lista_resultado.add(hashmap);
					//	System.out.println(":::"+lista_resultado);
					c.moveToNext();
				}
				// 8 Cerramos la BD
				dtb.close();
				return lista_resultado;
			} else {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
			}
		} catch (Exception e) {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Error en la busqueda: " + busqueda);
		}

	}

	/*
	 * Busca en las referencias articulos con descripciones parecidas a las
	 * pasadas en la cadena de parametro
	 * <p>
	 * 1 Abrimos la base
	 * <p>
	 * 2Procesar la cadena de busqueda (un espacio blanco cuenta como un &&):
	 * Reemplazamos los multiples espacios blancos por 1 solo
	 * <p>
	 * 3 Partimos el string en una tabla segun el caracter ' '
	 * <p>
	 * 4 Construimos la consulta SQL
	 * <p>
	 * 5 Buscar en las referencias el articulo
	 * <p>
	 * 6 Control respuestas
	 * <p>
	 * 7 Lectura de los resultados
	 * <p>
	 * 8 Cerramos la BD
	 *
	 * @param busqueda
	 * @return
	 * @throws ExceptionBDD
	 */
	public ArrayList<HashMap<Integer, Object>> buscarEnReferenciasPorCodigoODescripcion(String busqueda) throws ExceptionBDD {
		// toModify;
		try {
			System.out.println("::: BaseDatos 738 buscarEnReferencias");
			// Variable de retorno:Revisar esto
			ArrayList<HashMap<Integer, Object>> lista_resultado = new ArrayList<HashMap<Integer, Object>>();

			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			// 2Procesar la cadena de busqueda (un espacio blanco cuenta como
			// un
			// &&):
			// Reemplazamos los multiples espacios blancos por 1 solo:

			StringBuilder consulta_SQL = new StringBuilder();
			if(busqueda.matches("^\\d{1,5}$")){
				consulta_SQL = new StringBuilder(ParametrosInventario.bal_bdd_referencia_codigo + " = '" + busqueda + "'");
			} else {
				busqueda = busqueda.replaceAll("\\s+", " ");

				// 3 Partimos el string en una tabla segun el caracter ' ':
				String[] tabla_busqueda = busqueda.split("\\s");

				// 4 Construimos la consulta SQL:
				for (String s : tabla_busqueda) {
					consulta_SQL.append(ParametrosInventario.bal_bdd_referencia_descripcion + " LIKE '%").append(s).append("%' AND ");
				}
				consulta_SQL = new StringBuilder(consulta_SQL.substring(0, consulta_SQL.length() - 4));
				// consulta_SQL += "AND " +
				// ParametrosInventario.bal_bdd_articulo_inventario + "=" +
				// num_inventario;
			}

			// 5 Buscar en las referencias el articulo
			// Mas columnas, o todo
			String[] col = new String[] {
					ParametrosInventario.bal_bdd_referencia_sector,
					ParametrosInventario.bal_bdd_referencia_codigo,
					ParametrosInventario.bal_bdd_referencia_descripcion
					// ParametrosInventario.bal_bdd_referencia_codigo_barra,
					// ParametrosInventario.bal_bdd_referencia_precio_venta,
					// ParametrosInventario.bal_bdd_referencia_precio_costo
			};

			Cursor c = dtb.query(tabla_referencias_nombre, col, consulta_SQL.toString(),
					null, ParametrosInventario.bal_bdd_referencia_codigo + ","
							+ ParametrosInventario.bal_bdd_referencia_sector,
					null, ParametrosInventario.bal_bdd_referencia_descripcion);

			// 6 Control respuestas:
			if (c.getCount() <= 0) {
				dtb.close();
				return lista_resultado;
			} else if (c.getCount() > ParametrosInventario.MAX_SQL_RESPUESTAS) {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_TOO_MANY_RESULTS);
			}
			// ArticuloVisible nvoArt;
			// int codigo,sector;
			// String desc,codigoBarra;
			// double precioVenta, precioCosto;
			// ArrayList<String> losCodigosBarras;
			// 7 Lectura de los resultados:
			if (c.moveToFirst()) {
				//		System.out.println(":::ACA SI ENTROOOOOOO");
				while (c.isAfterLast() == false) {
					HashMap<Integer, Object> hashmap = new HashMap<Integer, Object>();
					// Crear un articulo con los datos extraidos
					// codigo=c.getInt(0);
					// sector=c.getInt(1);
					// desc=c.getString(2);
					// codigoBarra=c.getString(3);
					// precioVenta=c.getDouble(4);
					// precioCosto=c.getDouble(5);
					// losCodigosBarras= new ArrayList<String>();
					// losCodigosBarras.add(codigoBarra);
					// nvoArt=new ArticuloVisible(sector, codigo,
					// losCodigosBarras, 0, desc, precioVenta, precioCosto, "",
					// -1, "", true);
					// Meterlo en el hash map
					hashmap.put(ParametrosInventario.clave_art_sector, c.getInt(0));
					hashmap.put(ParametrosInventario.clave_art_codigo, c.getInt(1));
					hashmap.put(ParametrosInventario.clave_art_nombre, c.getString(2));
					lista_resultado.add(hashmap);
					//	System.out.println(":::"+lista_resultado);
					c.moveToNext();
				}
				// 8 Cerramos la BD
				dtb.close();
				return lista_resultado;
			} else {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
			}
		} catch (Exception e) {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Error en la busqueda: " + busqueda);
		}

	}

	public boolean buscarArticulosNoTomadosBD(int inventario_curso)throws ExceptionBDD{
		SQLiteDatabase dtb = this.getWritableDatabase();
		// 2 Construimos la consulta SQL:
		Cursor c;
		ArrayList<HashMap<Integer, Object>> art_no_tomados = new ArrayList<HashMap<Integer, Object>>();
		c= dtb.rawQuery("SELECT * FROM ARTICULOS WHERE ART_I="+inventario_curso + " AND ART_Q IN (-1,0)", null);
		if (c.moveToFirst()) {
			System.out.println("::: BD HAY ARTICULOS NO TOMADOS");
			return true;
		} else {
			System.out.println("::: BD TODOS LOS ARTICULOS TIENEN COSAS");
			return false;
		}
	}

	/*
	 *
	 * Busca los proveedores existentes
	 * */
	public ArrayList<String> buscarEnProveedores(String valor) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 738 buscarEnProveedores");
			//ArrayList<HashMap<Integer, Object>> lista_resultado = new ArrayList<HashMap<Integer, Object>>();
			ArrayList<String> lista_resultado = new ArrayList<String>();
			SQLiteDatabase dtb = this.getWritableDatabase();
			if(valor.equals("")){
				valor = valor.replaceAll("\\s+", " ");
				String[] tabla_busqueda = valor.split("\\s");
				String consulta_SQL = "";
				for (String s : tabla_busqueda) {
					consulta_SQL += ParametrosInventario.bal_bdd_proveedores_descripcion
							+ " LIKE '%" + s + "%' AND ";
				}
				//consulta_SQL = consulta_SQL.substring(0, consulta_SQL.length() - 4);
				consulta_SQL = "";
				String[] col = new String[] {
						ParametrosInventario.bal_bdd_proveedores_descripcion
				};
				Cursor c = dtb.query(tabla_proveedores_nombre, col, consulta_SQL,
						null, ParametrosInventario.bal_bdd_proveedores_codigo + ","
								+ ParametrosInventario.bal_bdd_proveedores_descripcion,
						null, ParametrosInventario.bal_bdd_proveedores_descripcion);

				// 6 Control respuestas:
				if (c.getCount() <= 0) {
					dtb.close();
					return lista_resultado;
				} else if (c.getCount() > ParametrosInventario.MAX_SQL_RESPUESTAS) {
					dtb.close();
					throw new ExceptionBDD(ExceptionBDD.ERROR_TOO_MANY_RESULTS);
				}
				// 7 Lectura de los resultados:
				if (c.moveToFirst()) {
					while (!c.isAfterLast()) {

//					lista_resultado.add(ParametrosInventario.clave_prov_cod,
//							c.getString(0));
						lista_resultado.add(c.getString(0));
						System.out.println("::: listaresultado " +lista_resultado );

						c.moveToNext();
					}
					// 8 Cerramos la BD
					dtb.close();
					return lista_resultado;
				} else {
					dtb.close();
					throw new ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
				}
			}else{
				valor = valor.replaceAll("\\s+", " ");
				String tabla_busqueda[] = valor.split("\\s");
				String consulta_SQL = "";
				for (String s : tabla_busqueda) {
					consulta_SQL += ParametrosInventario.bal_bdd_proveedores_descripcion
							+ " LIKE '%" + s + "%' AND ";
				}
				consulta_SQL = consulta_SQL.substring(0, consulta_SQL.length() - 4);
				String col[] = new String[] {
						ParametrosInventario.bal_bdd_proveedores_descripcion
				};
				Cursor c = dtb.query(tabla_proveedores_nombre, col, consulta_SQL,
						null, ParametrosInventario.bal_bdd_proveedores_codigo + ","
								+ ParametrosInventario.bal_bdd_proveedores_descripcion,
						null, ParametrosInventario.bal_bdd_proveedores_descripcion);


				// 6 Control respuestas:
				if (c.getCount() <= 0) {
					dtb.close();
					return lista_resultado;
				} else if (c.getCount() > ParametrosInventario.MAX_SQL_RESPUESTAS) {
					dtb.close();
					//throw new ExceptionBDD(ExceptionBDD.ERROR_TOO_MANY_RESULTS);
					return lista_resultado;
				}
				// 7 Lectura de los resultados:
				if (c.moveToFirst()) {
					while (c.isAfterLast() == false) {
//					lista_resultado.add(ParametrosInventario.clave_prov_cod,
//							c.getString(0));
						lista_resultado.add(c.getString(0));
						System.out.println("::: listaresultado " +lista_resultado );
						c.moveToNext();
					}
					// 8 Cerramos la BD
					dtb.close();
					return lista_resultado;
				} else {
					dtb.close();
					throw new ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
				}
			}
		} catch (Exception e) {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Error en la busqueda: " + valor);
		}
	}

	/*
	 *
	 * Busca el codigo del proveedor mediante el nombre, y guarda el codigo en una tabla que relaciona el inventario de compra
	 * */
	public ArrayList<String> cargarProveedor(String valor,String proveedor_id) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 738 buscarEnProveedores");
			ArrayList<String> lista_resultado = new ArrayList<String>();
			SQLiteDatabase dtb = this.getWritableDatabase();
			valor = valor.replaceAll("\\s+", " ");
			int codigoProve = 0;
			String tabla_busqueda[] = valor.split("\\s");
			String consulta_SQL = "";
			for (String s : tabla_busqueda) {
				consulta_SQL += ParametrosInventario.bal_bdd_proveedores_descripcion
						+ " LIKE '%" + s + "%' AND ";
			}
			consulta_SQL = consulta_SQL.substring(0, consulta_SQL.length() - 4);
			String col[] = new String[] {
					ParametrosInventario.bal_bdd_proveedores_codigo
			};
			Cursor c = dtb.query(tabla_proveedores_nombre, col, consulta_SQL,
					null, ParametrosInventario.bal_bdd_proveedores_codigo + ","
							+ ParametrosInventario.bal_bdd_proveedores_descripcion,
					null, ParametrosInventario.bal_bdd_proveedores_descripcion);
			// 6 Control respuestas:
			if (c.getCount() <= 0) {
				dtb.close();
				return lista_resultado;
			} else if (c.getCount() > ParametrosInventario.MAX_SQL_RESPUESTAS) {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_TOO_MANY_RESULTS);
			}
			// 7 Lectura de los resultados:
			if (c.moveToFirst()) {
				while (c.isAfterLast() == false) {
//					lista_resultado.add(ParametrosInventario.clave_prov_cod,c.getString(0));
					lista_resultado.add(c.getString(0));
					codigoProve = c.getInt(0);
					dtb.execSQL("DELETE FROM COMPRA_PROVEEDOR WHERE COMPRA_INV_COD="+proveedor_id);
					dtb.execSQL("INSERT INTO COMPRA_PROVEEDOR(COMPRA_INV_COD,COMPRA_PROVE_COD) VALUES ("+proveedor_id+","+codigoProve+") ");
					c.moveToNext();
				}
				// 8 Cerramos la BD
				dtb.close();
				return lista_resultado;
			} else {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
			}
		} catch (Exception e) {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Error en la busqueda: " + valor);
		}
	}

	/*
	 * Busca el articulo unSector-unCodigo en las referencias
	 * <p>
	 * 1 Abrimos la base
	 * <p>
	 * 2 Construimos la consulta SQL
	 * <p>
	 * 3 Buscar en las referencias el articulo
	 * <p>
	 * 4 Control respuestas
	 * <p>
	 * 5 Lectura de los resultados
	 * <p>
	 * 6 Crear un articulo con los datos extraidos
	 * <p>
	 * 7 Cerramos la BD
	 *
	 * @param unCodigo
	 * @param unSector
	 * @return el articulo encontrado
	 * @throws ExceptionBDD
	 *             si no encuentra
	 */
	public ArticuloVisible buscarArticuloEnReferencias(int unCodigo,int unSector) throws ExceptionBDD {
		ArticuloVisible result = null;
		System.out.println("::: BaseDatos 866 buscarArticuloEnReferencias");
		try {
			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();
			// 2 Construimos la consulta SQL:
			String consulta_SQL = "";

			consulta_SQL = ParametrosInventario.bal_bdd_referencia_codigo + " = " + String.valueOf(unCodigo) + " AND " + ParametrosInventario.bal_bdd_referencia_sector + " = " + String.valueOf(unSector);

			// 3 Buscar en las referencias el articulo
			// Mas columnas, o todo
			String[] col = new String[] {
					ParametrosInventario.bal_bdd_referencia_sector,
					ParametrosInventario.bal_bdd_referencia_codigo,
					ParametrosInventario.bal_bdd_referencia_balanza,
					ParametrosInventario.bal_bdd_referencia_decimales,
					ParametrosInventario.bal_bdd_referencia_descripcion,
					ParametrosInventario.bal_bdd_referencia_codigo_barra,
					ParametrosInventario.bal_bdd_referencia_codigo_barra_completo,
					ParametrosInventario.bal_bdd_referencia_precio_venta,
					ParametrosInventario.bal_bdd_referencia_precio_costo ,
					ParametrosInventario.bal_bdd_referencia_existencia_venta,
					ParametrosInventario.bal_bdd_referencia_existencia_deposito,
					ParametrosInventario.bal_bdd_referencia_depsn,};
			System.out.println("::: BD 873 valor depsn " + ParametrosInventario.bal_bdd_referencia_depsn);

			Cursor c = dtb.query(tabla_referencias_nombre, col, consulta_SQL,
					null, null, null, null);

			// 4 Control respuestas:
			if (c.getCount() <= 0) {
				dtb.close();
				return result;
			}

			// ArticuloVisible nvoArt;
			int codigo, sector, depsn,balanza,decimales;
			String desc, codigoBarra,codigoBarraCompleto;
			double precioVenta, precioCosto, exisventa, exisdeposito;
			ArrayList<String> losCodigosBarras;
			ArrayList<String> losCodigosBarrasCompleto;
			// 5 Lectura de los resultados:
			if (c.moveToFirst()) {
				// Deberia ser uno solo
				// while (c.isAfterLast() == false) {
				HashMap<Integer, Object> hashmap = new HashMap<Integer, Object>();
				// 6 Crear un articulo con los datos extraidos
				sector = c.getInt(0);
				codigo = c.getInt(1);
				balanza = c.getInt(2);
				decimales = c.getInt(3);
				desc = c.getString(4);
				codigoBarra = c.getString(5);
				codigoBarraCompleto = c.getString(6);
				precioVenta = c.getDouble(7);
				precioCosto = c.getDouble(8);
				exisventa = c.getDouble(9);
				exisdeposito = c.getDouble(10);
				depsn = c.getInt(11);
				System.out.println("::: BD 907 hasta aca llega el valor depsn " + depsn);
				losCodigosBarras = new ArrayList<String>();
				losCodigosBarrasCompleto = new ArrayList<String>();
				losCodigosBarras.add(codigoBarra);
				result = new ArticuloVisible(sector, codigo, balanza,decimales, losCodigosBarras,losCodigosBarrasCompleto,
						0, desc, precioVenta, precioCosto, "", -1,-1,exisventa,exisdeposito,depsn, "", true);
///////////////////////////////////////////
				// c.moveToNext();
				// }
				// 7 Cerramos la BD
				dtb.close();
				return result;
			} else {
				dtb.close();
				return result;
				// throw new
				// ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
			}
		} catch (Exception e) {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Error en la busqueda: " + unSector + "-" + unCodigo);
		}

	}


	public Proveedor buscarProveedor(int unCodigo) throws ExceptionBDD {
		Proveedor result = null;
		System.out.println("::: BaseDatos 866 buscarProveedor");
		try {
			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();
			// 2 Construimos la consulta SQL:
			String consulta_SQL = "";

			consulta_SQL = ParametrosInventario.bal_bdd_proveedores_codigo
					+ " = " + String.valueOf(unCodigo);

			// 3 Buscar en las referencias el articulo
			// Mas columnas, o todo
			String[] col = new String[] {
					ParametrosInventario.bal_bdd_proveedores_codigo,
					ParametrosInventario.bal_bdd_proveedores_descripcion
			};
			Cursor c = dtb.query(tabla_proveedores_nombre, col, consulta_SQL,
					null, null, null, null);

			// 4 Control respuestas:
			if (c.getCount() <= 0) {
				dtb.close();
				return result;
			}

			int codigo;
			String nombre;
			// 5 Lectura de los resultados:
			if (c.moveToFirst()) {
				// Deberia ser uno solo
				// while (c.isAfterLast() == false) {
				HashMap<Integer, Object> hashmap = new HashMap<Integer, Object>();
				// 6 Crear un articulo con los datos extraidos
				codigo = c.getInt(0);
				nombre = c.getString(1);
				System.out.println("::: BD 907 hasta aca llega el valor depsn ");
				result = new Proveedor(codigo,nombre);

				dtb.close();
				return result;
			} else {
				dtb.close();
				return result;
				// throw new
				// ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
			}
		} catch (Exception e) {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Error en la busqueda: " + unCodigo);
		}

	}

	/*
	 * Busca por parecido de descripcion en el num_inventario
	 * <p>
	 * 1 Abrimos la base
	 * <p>
	 * 2 Procesar la cadena de busqueda (un espacio blanco cuenta como un
	 * &&:Reemplazamos los multiples espacios blancos por 1 solo
	 * <p>
	 * 3 Partimos el string en una tabla segun el caracter ' '
	 * <p>
	 * 4 Construimos la consulta SQL
	 * <p>
	 * 5 Buscar en el inventario especificado el articulo
	 * <p>
	 * 6 Control respuestas
	 * <p>
	 * 7 Lectura de los resultados
	 * <p>
	 * 8 Cerramos la BD
	 *
	 * @param num_inventario
	 * @param busqueda
	 * @return
	 * @throws ExceptionBDD
	 */
	public ArrayList<HashMap<Integer, Object>> buscar(int num_inventario, String busqueda) throws ExceptionBDD {
		System.out.println("::: BaseDatos 972 buscar");
		try {
			// Variable de retorno:
			ArrayList<HashMap<Integer, Object>> lista_resultado = new ArrayList<HashMap<Integer, Object>>();

			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			// 2 Procesar la cadena de busqueda (un espacio blanco cuenta como
			// un
			// &&):
			// Reemplazamos los multiples espacios blancos por 1 solo:
			busqueda = busqueda.replaceAll("\\s+", " ");

			// 3 Partimos el string en una tabla segn el caracter ' ':
			String[] tabla_busqueda = busqueda.split("\\s");

			// 4 Construimos la consulta SQL:
			String consulta_SQL = "";
			for (String s : tabla_busqueda) {
				consulta_SQL += ParametrosInventario.bal_bdd_articulo_descripcion + " LIKE '%" + s + "%' AND ";
			}
			consulta_SQL = consulta_SQL.substring(0, consulta_SQL.length() - 4);
			consulta_SQL += "AND " + ParametrosInventario.bal_bdd_articulo_inventario + "=" + num_inventario;

			// 5 Buscar en el inventario especificado el articulo:
			String[] col = new String[] {
					ParametrosInventario.bal_bdd_articulo_sector,
					ParametrosInventario.bal_bdd_articulo_codigo,
					ParametrosInventario.bal_bdd_articulo_descripcion };

			Cursor c = dtb.query(tabla_articulos_nombre, col, consulta_SQL, null, null, null, ParametrosInventario.bal_bdd_articulo_descripcion);

			// 6 Control respuestas:
			if (c.getCount() <= 0) {
				dtb.close();
				return lista_resultado;
			} else if (c.getCount() > ParametrosInventario.MAX_SQL_RESPUESTAS) {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_TOO_MANY_RESULTS);
			}

			// 7 Lectura de los resultados:
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					HashMap<Integer, Object> hashmap = new HashMap<Integer, Object>();

					hashmap.put(ParametrosInventario.clave_art_sector, c.getInt(0));
					hashmap.put(ParametrosInventario.clave_art_codigo, c.getInt(1));
					hashmap.put(ParametrosInventario.clave_art_nombre, c.getString(2));

					lista_resultado.add(hashmap);

					c.moveToNext();
				}
				// 8 Cerramos la BD
				dtb.close();
				return lista_resultado;
			} else {
				dtb.close();
				throw new ExceptionBDD(ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED);
			}
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 818 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Error en la busqueda: " + busqueda);
		}
	}

	/*
	 * Regenera las tablas de articulos e inventarios. Si se ha cambiado la
	 * sentencia de creacion, se creana segun la nueva estriuctura 1 Abrimos la
	 * base 2 Suprimimos todas las entradas de las tablas, sin suprimir las 3
	 * Se elimina la version anterior de la tabla 4 Se crea la nueva version de
	 * la tabla 5 Cierre
	 *
	 * @throws ExceptionBDD
	 */
	public void destruirYReconstruir() throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 1065 destruirYReconstruir");
			// 1 Abrimos la base:
			SQLiteDatabase db = this.getWritableDatabase();

			// 2 Suprimimos todas las entradas de las tablas, sin suprimir las
			// tablas:
			db.execSQL("DELETE FROM " + tabla_articulos_nombre);
			db.execSQL("DELETE FROM " + tabla_inventarios_nombre);

			// 3 Se elimina la versin anterior de la tabla
			db.execSQL("DROP TABLE IF EXISTS " + tabla_articulos_nombre);
			db.execSQL("DROP TABLE IF EXISTS " + tabla_inventarios_nombre);

			// 4 Se crea la nueva version de la tabla
			db.execSQL(sqlCreateTablaArticulos);
			db.execSQL(sqlCreateTablaInventarios);

			// 5 Cierre:
			db.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 859 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE, "Imposible de hacer RESET");
		}
	}

	public void borrarArticuloCompra(int cod_sector, int cod_art, int inventario_numero_en_curso){
		try {
			System.out.println("::: BaseDatos 1459 borrarArticuloCompra");
			// 1 Abrimos la base:
			SQLiteDatabase db = this.getWritableDatabase();
			// 2 Suprimimos todas las entradas de las tablas, sin suprimir las
			// tablas:
			db.execSQL("DELETE FROM ARTICULOS WHERE ART_SEC="+ cod_sector
					+ " AND ART_COD="+ cod_art
					+ " AND ART_I= "+inventario_numero_en_curso);
			// 3 Se elimina la versin anterior de la tabla
			// 5 Cierre:
			db.close();
		} catch (Exception e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 859 --]" + e.toString(), 4);
			//throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE,
			//		"Imposible de hacer RESET");
		}
	}

	/*
	 * Regenera la tabla que se le pasa como parametro 1 Abrimos la base 2
	 * Suprimimos todas las entradas de la tabla 3 Suprimimos la tabla que se
	 * paso 4 Se crea la nueva versin de la tabla 5 Cierre
	 *
	 * @param tabla
	 * @throws ExceptionBDD
	 */
	public void destruirYReconstruir(String tabla) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 1107 destruirYReconstruir");
			// 1 Abrimos la base:
			SQLiteDatabase dtb = this.getWritableDatabase();

			// 2 Suprimimos todas las entradas de la tabla
			dtb.execSQL("DELETE FROM " + tabla);

			// 3 Suprimimos la tabla que se paso
			dtb.execSQL("DROP TABLE IF EXISTS " + tabla);

			// 4 Se crea la nueva versin de la tabla
			if (tabla.compareTo(tabla_articulos_nombre) == 0) {
				dtb.execSQL(sqlCreateTablaArticulos);
			} else if (tabla.compareTo(tabla_inventarios_nombre) == 0) {
				dtb.execSQL(sqlCreateTablaInventarios);
			}

			// 5 Cierre:
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 900 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE, "Imposible de hacer RESET");
		}

	}

	/*
	 * Verifica el estado del inventario por su id para ver si esta abierto 1
	 * Abrimos la base de datos en modo lectura 2 Buscamos el inventario con
	 * ese id en la tabla de inventarios 3 Evaluamos el resultado y si es 1
	 * devolvemos true
	 *
	 * @param id
	 * @return true si el inventario esta abierto
	 * @throws ExceptionBDD
	 *             si el inventario no existe
	 */
	public boolean estaAbiertoInventarioConId(int id) throws ExceptionBDD {
		System.out.println("::: BaseDatos 1152 estaAbiertoInventarioConId");
		// 1 Abrimos la base de datos en modo lectura
		SQLiteDatabase dtb = this.getReadableDatabase();
		System.out.println("::: BaseDatos 1155 que id es " + String.valueOf(id));


		/*hardcode mas o menos*/
		/*COMO NO PUEDO TRAER EL VALOR DE -2 DEL INVENTARIO POR DEPOSITO Y SIEMPRE
		 * VIENE -1, HAGO UN IF PREGUNTANDO DE SI EL CHECK DE VENTAS ES FALSO Y EL ID
		 * PASADO ES -1 LE ASIGNE A LO BRUTO UN -2 PARA PODER TRABAJAR*/
		boolean condicionRadio = ParametrosInventario.InventariosVentas;
		Cursor c;
		System.out.println("::: BaseDatos condicionRadio " + condicionRadio + " " + String.valueOf(id));
		int valorapasar = Integer.parseInt(String.valueOf(id));
		if(!condicionRadio && valorapasar==-1){
			//deposito
			System.out.println("DEPOSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
			c = dtb
					.query(tabla_inventarios_nombre,
							new String[] { ParametrosInventario.bal_bdd_inventario_estado },
							ParametrosInventario.bal_bdd_inventario_numero + "=?",
							new String[] {"-2"}, null, null, null);
		}else{
			//ventas
			System.out.println("VENTASSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
			c = dtb
					.query(tabla_inventarios_nombre,
							new String[] { ParametrosInventario.bal_bdd_inventario_estado },
							ParametrosInventario.bal_bdd_inventario_numero + "=?",
							new String[] { String.valueOf(id) }, null, null, null);
		}


		/**/
		// 2 Buscamos el inventario con ese id en la tabla de inventarios
//		Cursor c = dtb
//				.query(tabla_inventarios_nombre,
//						new String[] { ParametrosInventario.bal_bdd_inventario_estado },
//						ParametrosInventario.bal_bdd_inventario_numero + "=?",
//						new String[] { String.valueOf(id) }, null, null, null);

		// 3 Evaluamos el resultado y si es 1 devolvemos true
		if (c.moveToFirst()) {
			if (c.getInt(0) == 1) {
				return true;
			} else {
				return false;
			}

		} else {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"El INVENTARIO con NUMERO=" + String.valueOf(id)
							+ " no existe");
		}
	}

	public boolean estaAbiertoInventarioComprasConId(int id) throws ExceptionBDD {
		System.out.println("::: BaseDatos 1152 estaAbiertoInventarioComprasConId");
		// 1 Abrimos la base de datos en modo lectura

		SQLiteDatabase dtb = this.getReadableDatabase();
		Cursor c;
		//int valorapasar = Integer.parseInt(String.valueOf(id));
		//if(valorapasar==-3){
		//deposito
		System.out.println("DEPOSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
		//c = dtb.query(tabla_inventarios_nombre,
		//				new String[] { ParametrosInventario.bal_bdd_inventario_estado },
		//				ParametrosInventario.bal_bdd_inventario_numero + "="+ id,
		//				new String[] {"-2"}, null, null, null);}
		//select count(INV_NUM) from INVENTARIOS  WHERE INV_NUM=-6
		//c= dtb.rawQuery("select * from "+tabla_inventarios_nombre+"  WHERE "+ParametrosInventario.bal_bdd_inventario_numero
		//		+"="+id, null);

		c = dtb.query(tabla_inventarios_nombre,
						new String[] { ParametrosInventario.bal_bdd_inventario_estado },
						ParametrosInventario.bal_bdd_inventario_numero + "=?",
						new String[] { String.valueOf(id) }, null, null, null);
		System.out.println("::: BaseDatos c.getInt(0) que traeeee ");

		// 3 Evaluamos el resultado y si es 1 devolvemos true


		// 3 Evaluamos el resultado y si es 1 devolvemos true
		if (c.moveToFirst()) {
			if (c.getInt(0) == 1) {
				return true;
			} else {
				return false;
			}
//return true;
		} else {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"El INVENTARIO con NUMERO=" + String.valueOf(id) + " no existe");
		}
	}


	public void SelectTotal() throws IOException {
		System.out.println("::: BaseDatos 1179 SelectTotal");

		/*
		 * EN ESTA FUNCIN SE CREAR UNA EXPORTACIN DE SEGURIDAD DE LA BASE DE DATOS
		 */

		/*
		 * COMIENZO LA CONFIGURACIN DE DONDE IR LOS DATOS
		 */

		String query = "Select * from " + ParametrosInventario.tabla_articulos;

		// se llama en el gestor que se encargar de dibujar el txt con las
		// etiquetas de xml
		GestorLogEventos logDatos = new GestorLogEventos();
		logDatos.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		logDatos.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		logDatos.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		logDatos.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		logDatos.setUbicacion(ParametrosInventario.CARPETA_LOGDATOS);

		GestorLogEventos logErrores = new GestorLogEventos();
		logErrores.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		logErrores.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		logErrores.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		logErrores.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		logErrores.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;

		String ruta = ParametrosInventario.CARPETA_LOGDATOS + "log.txt";
		logErrores.log("La ruta del logDatos es: " + ruta, 3);
		File LogDatosAnterior = new File(ruta);
		LogDatosAnterior.delete();
		LogDatosAnterior.deleteOnExit();

		logErrores.log("Comienza la exportacin de artculos", 2);

		SQLiteDatabase dtb = this.getReadableDatabase();
		Cursor c = dtb.rawQuery(query, null);

		/*
		 * COMIENZA LA EXPORTACIN DE LA LA TABLA ARTCULOS
		 */

		try {
			if (c.moveToFirst()) {


				System.out.println("*** DATOS="+ParametrosInventario.tabla_articulos);
				logDatos.Logdatos(ParametrosInventario.tabla_articulos, null, 1);
				int contador = 1;
				do {

					String sector = c.getString(0);
					String codigo = c.getString(1);
					String codigoBarra = c.getString(2);
					String inventario = c.getString(3);
					String descripcion = c.getString(4);
					String precioVenta = c.getString(5);
					String precioCosto = c.getString(6);
					String foto = c.getString(7);
					String cantidad = c.getString(8);
					String fechaInicio = c.getString(9);
					String fechaFin = c.getString(10);
					String exisVenta = c.getString(11);
					String exisDeposito = c.getString(12);
					String subtotal = c.getString(13);

					String contadorString = String.valueOf(contador);

					logDatos.Logdatos(contadorString, null, 2);
					logDatos.Logdatos(sector, ParametrosInventario.bal_bdd_articulo_sector, 3);
					logDatos.Logdatos(codigo, ParametrosInventario.bal_bdd_articulo_codigo, 3);
					logDatos.Logdatos(codigoBarra, ParametrosInventario.bal_bdd_articulo_codigo_barra, 3);
					logDatos.Logdatos(inventario, ParametrosInventario.bal_bdd_articulo_inventario, 3);
					logDatos.Logdatos(descripcion, ParametrosInventario.bal_bdd_articulo_descripcion, 3);
					logDatos.Logdatos(precioVenta, ParametrosInventario.bal_bdd_articulo_precio_venta, 3);
					logDatos.Logdatos(precioCosto, ParametrosInventario.bal_bdd_articulo_precio_costo, 3);
					logDatos.Logdatos(foto, ParametrosInventario.bal_bdd_articulo_foto, 3);
					logDatos.Logdatos(cantidad, ParametrosInventario.bal_bdd_articulo_cantidad, 3);
					logDatos.Logdatos(fechaInicio, ParametrosInventario.bal_bdd_articulo_fechaInicio, 3);
					logDatos.Logdatos(fechaFin, ParametrosInventario.bal_bdd_articulo_fechaFin, 3);


					logDatos.Logdatos(exisVenta, ParametrosInventario.bal_bdd_articulo_existencia_venta, 3);
					logDatos.Logdatos(exisDeposito, ParametrosInventario.bal_bdd_articulo_existencia_deposito, 3);


					logDatos.Logdatos(null, null, 21);
					contador++;

				} while (c.moveToNext());

				logDatos.Logdatos(null, null, 11);

			} else {
				logErrores.log("La tabla no tiene datos", 3);
			}

		} catch (Exception e1) {
			logErrores.log(
					"[-- 1046 --] ERROR EN LA EXPORTACION" + e1.toString(), 4);
			logDatos.Logdatos(null, null, 11);
		}

		dtb.close();

		/*
		 * EXPORTACIN DE ARTCULO TERMINAD
		 */

		/*
		 * EXPORTACIN DE REFERENCIAS FINALIZA
		 */

		logErrores.log("Comienza la exportacin de Referencias", 2);

		String query_1 = "Select * from " + ParametrosInventario.tabla_referencias;
		SQLiteDatabase dtb_1 = this.getReadableDatabase();
		Cursor c_1 = dtb_1.rawQuery(query_1, null);

		try {
			if (c_1.moveToFirst()) {

				logDatos.Logdatos(ParametrosInventario.tabla_referencias, null, 1);
				int contador = 1;
				do {

					String sector = c_1.getString(0);
					String codigo = c_1.getString(1);
					String codigoBarra = c_1.getString(2);
					String descripcion = c_1.getString(3);
					String precioVenta = c_1.getString(4);
					String precioCosto = c_1.getString(5);
					String foto = c_1.getString(6);

					String exisVenta = c_1.getString(7);
					String exisDeposito = c_1.getString(8);

					String contadorString = String.valueOf(contador);

					logDatos.Logdatos(contadorString, null, 2);
					logDatos.Logdatos(sector, ParametrosInventario.bal_bdd_referencia_sector, 3);
					logDatos.Logdatos(codigo, ParametrosInventario.bal_bdd_referencia_codigo, 3);
					logDatos.Logdatos(codigoBarra, ParametrosInventario.bal_bdd_referencia_codigo_barra, 3);
					logDatos.Logdatos(descripcion, ParametrosInventario.bal_bdd_referencia_descripcion, 3);
					logDatos.Logdatos(precioVenta, ParametrosInventario.bal_bdd_referencia_precio_venta, 3);
					logDatos.Logdatos(precioCosto, ParametrosInventario.bal_bdd_referencia_precio_costo, 3);
					logDatos.Logdatos(foto, ParametrosInventario.bal_bdd_referencia_foto, 3);

					logDatos.Logdatos(exisVenta, ParametrosInventario.bal_bdd_referencia_existencia_venta, 3);
					logDatos.Logdatos(exisDeposito, ParametrosInventario.bal_bdd_referencia_existencia_deposito, 3);

					logDatos.Logdatos(null, null, 21);
					contador++;

				} while (c_1.moveToNext());

				logDatos.Logdatos(null, null, 11);

			} else {
				logErrores.log("La tabla no tiene datos", 3);
			}

		} catch (Exception e1) {
			logErrores.log(
					"[-- 1102 --]ERROR EN LA EXPORTACION" + e1.toString(), 4);
			logDatos.Logdatos(null, null, 11);
		}
		dtb_1.close();

		/*
		 * EXPORTACIN DE REFERENCIAS FINALIZADA
		 */

		/*
		 * COMIENZA LA EXPORTACIN DE INVENTARIOS
		 */

		logErrores.log("Comienza la exportacin de Inventarios", 2);

		String query_2 = "Select * from " + ParametrosInventario.tabla_inventarios;
		SQLiteDatabase dtb_2 = this.getReadableDatabase();
		Cursor c_2 = dtb_2.rawQuery(query_2, null);

		try {
			if (c_2.moveToFirst()) {

				logDatos.Logdatos(ParametrosInventario.tabla_inventarios, null,
						1);
				int contador = 1;
				do {

					String numero = c_2.getString(0);
					String descripcion = c_2.getString(1);
					String fechaInicio = c_2.getString(2);
					String fechaFin = c_2.getString(3);
					String estado = c_2.getString(4);
					String cantidad = c_2.getString(5);
					String subtotal = c_2.getString(6);

					String contadorString = String.valueOf(contador);

					logDatos.Logdatos(contadorString, null, 2);
					logDatos.Logdatos(numero, ParametrosInventario.bal_bdd_inventario_numero, 3);
					logDatos.Logdatos(descripcion, ParametrosInventario.bal_bdd_inventario_descripcion, 3);
					logDatos.Logdatos(fechaInicio, ParametrosInventario.bal_bdd_inventario_fechaInicio, 3);
					logDatos.Logdatos(fechaFin, ParametrosInventario.bal_bdd_inventario_fechaFin, 3);
					logDatos.Logdatos(estado, ParametrosInventario.bal_bdd_inventario_estado, 3);
					logDatos.Logdatos(cantidad, ParametrosInventario.bal_bdd_inventario_cantidad, 3);

					logDatos.Logdatos(null, null, 21);
					contador++;

				} while (c_2.moveToNext());

				logDatos.Logdatos(null, null, 11);

			} else {
				logErrores.log("La tabla no tiene datos", 3);
			}

		} catch (Exception e1) {
			logErrores.log(
					"[-- 1169 --]ERROR EN LA EXPORTACION" + e1.toString(), 4);
			logDatos.Logdatos(null, null, 11);
		}
		dtb_2.close();

		/*
		 * EXPORTACIN DE INVENTARIOS FINALIZADA
		 */

	}

	/*
	 * Exporta la base de datos que contiene los valores medidos para los
	 * inventarios realizados
	 * <p>
	 * 1 Creacion del nuevo documento
	 * <p>
	 * 2 Creacion del elemento de cabecera
	 * <p>
	 * 3 Abrimos la base de datos en modo lectura
	 * <p>
	 * 4 Para cada inventario de la lista
	 * <p>
	 * &nbsp; &nbsp;4.1 Creamos un elemento para el inventario
	 * <p>
	 * &nbsp; &nbsp;4.2 Recorro las columnas y creo los elementos de los datos
	 * <p>
	 * &nbsp; &nbsp;4.3 Buscamos los datos de los articulos
	 * <p>
	 * &nbsp; &nbsp;4.4 Creamos los elementos para cada artculo
	 * <p>
	 * 5 Guardamos el DOM como archivo XML
	 * <p>
	 * 6 Mandamos el archivo en POST hacia el servidor
	 *
	 * @return TRUE si el export se realiza con xito
	 * @throws ExceptionBDD
	 *             En caso de fracaso, el error ser generado
	 * @throws ExceptionHttpExchange
	 */
	// Tiene en cuenta el nuevo formato para datos de inventario tambien
	public boolean exportarTodasBaseDatosSQLite(ArrayList<Integer> listaInventariosSeleccionados) throws ExceptionBDD, ExceptionHttpExchange {
		System.out.println("::: BaseDatos 1473 exportarBDSQLite");
		try {
			// Chequear
			boolean hayAlMenosUno = false;
//			 StringBuilder string_lista_inventarios = new StringBuilder();
//			 for (int inv : listaInventariosSeleccionados) {
//			 string_lista_inventarios.append(String.valueOf(inv)).append(",");
//			 }
//			 string_lista_inventarios = new StringBuilder(string_lista_inventarios.substring(0, string_lista_inventarios.length() - 1));

			// 1 Creacion del nuevo documento
			DocumentBuilderFactory fabricaDocumentos = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructorDocumentos = fabricaDocumentos.newDocumentBuilder();
			Document documento = constructorDocumentos.newDocument();

			// Propiedades del DOM:
			documento.setXmlVersion("1.0");
			documento.setXmlStandalone(true);

			// 2 Creacion del elemento de cabecera
			Element titulo = documento.createElement(ParametrosInventario.bal_xml_export_cabecera);

			// 3 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();
			System.out.println("::: BaseDatos 1501 ");
			//estaba comdado
//			int radioButtonID = RadioGroupProductosNoContabilizados.getCheckedRadioButtonId();
//			View radioButton = RadioGroupProductosNoContabilizados.findViewById(radioButtonID);
//			int idx = RadioGroupProductosNoContabilizados.indexOfChild(radioButton);
//			if (idx == 0) {
//				System.out.println("-----!!!!!!---!!!!!!!------------!!!!!--------------");
//				System.out.println("BASE DE DATOS SALE EN 1");
//				ProductosNoContabilizados = 1;
//			} else if (idx == 1) {
//				System.out.println("-----!!!!!!---!!!!!!!------------!!!!!--------------");
//				System.out.println("BASE DE DATOS SALE EN 2");
//				ProductosNoContabilizados = 2;
//			}
			//hasta aca

			//Actualizar un registro
			System.out.println("PROD CONT SI O NO "+ParametrosInventario.ProductosNoContabilizados);
			//	int radioButtonID = RadioGroupProductosNoContabilizados
			//			.getCheckedRadioButtonId();
			//System.out.println("::: BD Prod. no contabilizados "+radioButtonID);
//			View radioButton = RadioGroupProductosNoContabilizados
//					.findViewById(radioButtonID);
//			int idx = RadioGroupProductosNoContabilizados
//					.indexOfChild(radioButton);
//			System.out.println("LALALALALALSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
//			System.out.println(idx);

			// 4 Para cada inventario de la lista
			for (Integer inv : listaInventariosSeleccionados) {
				String[] columnasDeseadasInventario = new String[] {
						ParametrosInventario.bal_bdd_inventario_numero,
						ParametrosInventario.bal_bdd_inventario_prodcont,
						ParametrosInventario.bal_bdd_inventario_fechaInicio,
						ParametrosInventario.bal_bdd_inventario_fechaFin,
						ParametrosInventario.bal_bdd_inventario_lugar,};

				// Recuperamos los resultados:
				 //Cursor cInventarios = dtb.query(tabla_inventarios_nombre, columnasDeseadasInventario, null, null, null, null, null);
				System.out.println("::: BaseDatos 1501 columna " + columnasDeseadasInventario);
				System.out.println("::: BaseDatos 1514 ");

				Cursor cInventarios = dtb.query(tabla_inventarios_nombre, columnasDeseadasInventario, ParametrosInventario.bal_bdd_inventario_numero + " = "
								+ String.valueOf(inv), null, null, null, null);

				int cantidadColumnasInv = cInventarios.getColumnCount();
				System.out.println("::: BaseDatos 1575 cantidadColumnasInv " + cantidadColumnasInv);
				Element inventario;// ,articulo;

				// Inicia el bucle
				if (cInventarios.moveToFirst()) {

					// int nroInvActual=cInventarios.getInt(0);
					int nroInvActual = inv;

					// No seria necesario este while
					// while (cInventarios.isAfterLast() == false) {

					// Para cada inventario
					 inventario = documento.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2xml(tabla_inventarios_nombre));
					// 4.1 Creamos un elemento para el inventario
					inventario = documento.createElement(Parametros.bal_xml_inventario_root);

					// 4.2 Recorro las columnas y creo los elementos de los datos
					for (int i = 0; i < cantidadColumnasInv; i++) {
						Element elemento = documento.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2xml(cInventarios.getColumnName(i)));
						elemento.setTextContent(cInventarios.getString(i));
						inventario.appendChild(elemento);
					}

					// Deberia ir aqui o mas abajo?
					// titulo.appendChild(inventario);

					// Agregar los articulos

					// Abrimos la base de datos en modo lectura:
					// SQLiteDatabase db = this.getReadableDatabase(); se comentoa*****************************************************

					// 4.3 Buscamos los datos de los articulos
					String[] columnasDeseadas = new String[] {
							ParametrosInventario.bal_bdd_articulo_sector,
							ParametrosInventario.bal_bdd_articulo_codigo,
							// ParametrosInventario.bal_bdd_articulo_inventario,
							ParametrosInventario.bal_bdd_articulo_cantidad,
							ParametrosInventario.bal_bdd_articulo_subtotal,
							ParametrosInventario.bal_bdd_articulo_fechaInicio,
							ParametrosInventario.bal_bdd_articulo_fechaFin,
							// ParametrosInventario.bal_bdd_articulo_foto,
							ParametrosInventario.bal_bdd_articulo_descripcion,
							ParametrosInventario.bal_bdd_articulo_codigo_barra,


							ParametrosInventario.bal_bdd_articulo_existencia_venta,
							ParametrosInventario.bal_bdd_articulo_existencia_deposito};
					System.out.println("::: BaseDatos 1627");
					// Recuperamos los resultados:
					Cursor c = dtb.query(tabla_articulos_nombre,
							columnasDeseadas,
							ParametrosInventario.bal_bdd_articulo_inventario
									+ " = " + String.valueOf(nroInvActual),
							null, null, null, null);

					int cantidadColumnas = c.getColumnCount();
					// Chequear
					boolean tieneArticulos = false;

					// Para cada articulo
					if (c.moveToFirst()) {
						tieneArticulos = true;
						hayAlMenosUno = true;
						while (!c.isAfterLast()) {
							// Creamos el hijo ART
							// Element medicion = documento.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2xml(tabla_articulos_nombre));

							// 4.4 Creamos los elementos para el articulo
							Element medicion = documento.createElement(Parametros.bal_xml_articulo_root);

								for (int i = 0; i < cantidadColumnas; i++) {
								Element elemento = documento.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2xml(c.getColumnName(i)));
								elemento.setTextContent(c.getString(i));
								medicion.appendChild(elemento);
							}
							inventario.appendChild(medicion);
							c.moveToNext();
						}
					}
					// Verificar
					if (tieneArticulos) titulo.appendChild(inventario);
					// cInventarios.moveToNext();
					// Cierra el while, no seria necesario
					// }
				}
			}

			dtb.close();
			// Verificar
			if (hayAlMenosUno) {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Element fec_ope = documento.createElement(ParametrosInventario.bal_xml_export_fec_ope);
				System.out.println(" Pasar fecha ");
				System.out.println(simpleDateFormat.format(cal.getTime()).toString());
				fec_ope.appendChild(documento.createTextNode(simpleDateFormat.format(cal.getTime())));
				titulo.appendChild(fec_ope);

				documento.appendChild(titulo);

				// 5 Guardamos el DOM como archivo XML

				HttpWriter.transformerXml(documento, ParametrosInventario.URL_COPIA_XML_EXPORT);

				// 6 Mandamos el archivo en POST hacia el servidor:
				HttpSender httpSender = new HttpSender(Parametros.CODIGO_SOFT_DEBOINVENTARIO);
				System.out.println("::: el system :;:;:;");
				return httpSender.send_xml(ParametrosInventario.URL_COPIA_XML_EXPORT);
			} else {
				return false;
			}

		} catch (Exception e) {
			System.out.println("::: entro al catch :;:;:;");
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1141 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
					"La exportacion de los datos de la Base De Datos fracaso");
		}
	}

	public boolean exportarTodasBaseDatosSQLiteCompras(ArrayList<Integer> listaInventariosSeleccionados) throws ExceptionBDD, ExceptionHttpExchange {
		System.out.println("::: BaseDatos 2263 exportarBDSQLiteCompras");
		System.out.println("ACA********************");
		try {
			boolean hayAlMenosUno = false;
			// 1 Creacion del nuevo documento
			DocumentBuilderFactory fabricaDocumentos = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder constructorDocumentos = fabricaDocumentos
					.newDocumentBuilder();
			Document documento = constructorDocumentos.newDocument();
			// Propiedades del DOM:
			documento.setXmlVersion("1.0");
			documento.setXmlStandalone(true);
			// 2 Creacion del elemento de cabecera
			Element titulo = documento.createElement(ParametrosInventario.bal_xml_export_cabecera);
			// 3 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();
			//Actualizar un registro
			// 4 Para cada inventario de la lista
			for (Integer inv : listaInventariosSeleccionados) {
				String[] columnasDeseadasInventario = new String[] {
						ParametrosInventario.bal_bdd_inventario_numero,
						ParametrosInventario.bal_bdd_inventario_prodcont,
						ParametrosInventario.bal_bdd_inventario_fechaInicio,
						ParametrosInventario.bal_bdd_inventario_fechaFin,
						ParametrosInventario.bal_bdd_inventario_lugar,
				};
				System.out.println("::: BaseDatos 2315 exportarBDSQLiteCompras por recuperar resultados");
				System.out.println("::: BaseDatos 2315 exportarBDSQLiteCompras por recuperar resultados inv "+inv);
				// Recuperamos los resultados:
				Cursor cInventarios = dtb.query(tabla_inventarios_nombre, columnasDeseadasInventario,
						ParametrosInventario.bal_bdd_inventario_numero + " = "
								+ String.valueOf(inv), null, null, null, null);



				int cantidadColumnasInv = cInventarios.getColumnCount();
				Element inventario;// ,articulo;
				// Inicia el bucle
				if (cInventarios.moveToFirst()) {
					// int nroInvActual=cInventarios.getInt(0);
					int nroInvActual = inv;
					// 4.1 Creamos un elemento para el inventario
					inventario = documento.createElement(Parametros.bal_xml_inventario_root);
					// 4.2 Recorro las columnas y creo los elementos de los
					// datos
					for (int i = 0; i < cantidadColumnasInv; i++) {
						Element elemento = documento.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2xml(cInventarios.getColumnName(i)));
						elemento.setTextContent(cInventarios.getString(i));
						inventario.appendChild(elemento);
					}
					// 4.3 Buscamos los datos de los articulos
					String[] columnasDeseadas = new String[] {
							ParametrosInventario.bal_bdd_articulo_sector,
							ParametrosInventario.bal_bdd_articulo_codigo,
							// ParametrosInventario.bal_bdd_articulo_inventario,
							ParametrosInventario.bal_bdd_articulo_cantidad,
							//ParametrosInventario.bal_bdd_articulo_subtotal,
							ParametrosInventario.bal_bdd_articulo_fechaInicio,
							ParametrosInventario.bal_bdd_articulo_fechaFin,
							// ParametrosInventario.bal_bdd_articulo_foto,
							ParametrosInventario.bal_bdd_articulo_descripcion,
							ParametrosInventario.bal_bdd_articulo_codigo_barra,
							ParametrosInventario.bal_bdd_articulo_existencia_venta,
							ParametrosInventario.bal_bdd_articulo_existencia_deposito//,
							//ParametrosInventario.bal_bdd_compraproveedor_codigo
					};

					Cursor cValidarProve = dtb.rawQuery("SELECT * FROM COMPRA_PROVEEDOR WHERE COMPRA_INV_COD ="+inv,null);
					if (cValidarProve.moveToFirst()) {
						Cursor c = dtb.rawQuery("SELECT a.ART_SEC, a.ART_COD,a.ART_Q,a.ART_SUBTOT, a.ART_FEI,a.ART_FEF,a.ART_DESC," +
								"a.ART_CB,a.ART_EV,a.ART_ED,b.COMPRA_PROVE_COD FROM ARTICULOS a " +
								"INNER JOIN COMPRA_PROVEEDOR b ON a.ART_I=b.COMPRA_INV_COD WHERE a.ART_I="+inv,null); //a.ART_SUBTOT,
						int cantidadColumnas = c.getColumnCount();
						// Chequear
						boolean tieneArticulos = false;
						// Para cada articulo
						if (c.moveToFirst()) {
							tieneArticulos = true;
							hayAlMenosUno = true;
							while (!c.isAfterLast()) {
								// Creamos el hijo ART
								// Element medicion =
								// 4.4 Creamos los elementos para el articulo
								Element medicion = documento.createElement(Parametros.bal_xml_articulo_root);
								for (int i = 0; i < cantidadColumnas; i++) {
									Element elemento = documento
											.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2xml(c.getColumnName(i)));
									System.out.println("::: BD VER QUE TRAE ESTO getColumnName(i)== " + c.getColumnName(i));
									System.out.println("::: BD VER QUE TRAE ESTO getString(i)== " + c.getString(i));
									elemento.setTextContent(c.getString(i));
									medicion.appendChild(elemento);
								}
								inventario.appendChild(medicion);
								c.moveToNext();
							}
						}else{
							throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
									"No se encuentran articulos cargados para la exportacion.");//return false;
						}
						// Verificar
						if (tieneArticulos) titulo.appendChild(inventario);
					}else{
						throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
								"No se ha seleccionado un proveedor para la exportacion.");//return false;
					}
				}
			}
			dtb.close();
			// Verificar
			if (hayAlMenosUno) {
				documento.appendChild(titulo);
				// 5 Guardamos el DOM como archivo XML
				HttpWriter.transformerXml(documento, ParametrosInventario.URL_COPIA_XML_EXPORT);
				// 6 Mandamos el archivo en POST hacia el servidor:
				HttpSender httpSender = new HttpSender(Parametros.CODIGO_SOFT_DEBOINVENTARIO);
				return httpSender.send_compra_xml(ParametrosInventario.URL_COPIA_XML_EXPORT);
			} else {
				return false;
			}

		} catch (Exception e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1141 --]" + e.toString(), 4);
			System.out.println("ERRORRR" + e.toString());
			throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
					"La exportacion de los datos de la Base De Datos fracaso");
		}
	}

	/*
	 * Genera los archivos XML de los inventarios a exportar por USB
	 * <p>
	 * 1 Para cada inventario
	 * <p>
	 * &nbsp; &nbsp;1.1 Creacion del DOM
	 * <p>
	 * &nbsp; &nbsp;1.2 Creacion del elemento raiz del DOM
	 * <p>
	 * &nbsp; &nbsp;1.3 Abrimos la base de datos en modo lectura
	 * <p>
	 * &nbsp; &nbsp;1.4 Recuperamos los datos del inventario
	 * <p>
	 * &nbsp; &nbsp;1.5 Buscamos los articulos
	 * <p>
	 * &nbsp; &nbsp;1.6 Para cada articulo
	 * <p>
	 * &nbsp; &nbsp;&nbsp; &nbsp;1.6.1 Creamos el elmento de datos
	 * <p>
	 * &nbsp; &nbsp;1.7 Generacion del Arcchivo XML local
	 * <p>
	 * &nbsp; &nbsp;1.8 Si estan creados todos los archivos, transformamos el
	 * DOM a un documento
	 *
	 * @param listaInventariosSeleccionados
	 * @throws ExceptionBDD
	 */
	// Nuevo formato desde 28/4/12
	public void exportarTodasBaseDatosSQLite_HaciaUsb(ArrayList<Integer> listaInventariosSeleccionados) throws ExceptionBDD {
		System.out.println("::: BaseDatos 1741 exportarTodasBaseDatosSQLite_HaciaUsb");
		try {
			boolean hayAlMenosUno = false;
			// 1 Para cada inventario
			for (int inv : listaInventariosSeleccionados) {

				// 1.1 Creacion del DOM
				DocumentBuilderFactory fabricaDocumentos = DocumentBuilderFactory
						.newInstance();
				// Arroja ParserConfigurationException
				DocumentBuilder constructorDocumentos = fabricaDocumentos
						.newDocumentBuilder();
				Document documento = constructorDocumentos.newDocument();

				// Propris du DOM:
				documento.setXmlVersion("1.0");
				documento.setXmlStandalone(true);

				// 1.2 Creacion del elemento raiz del DOM
				Element titulo = documento
						.createElement(ParametrosInventario.bal_xml_export_cabecera);

				// 1.3 Abrimos la base de datos en modo lectura:
				SQLiteDatabase dtb = this.getReadableDatabase();

				String[] columnasDeseadasInventario = new String[] {
						ParametrosInventario.bal_bdd_inventario_numero,
						ParametrosInventario.bal_bdd_inventario_fechaInicio,
						ParametrosInventario.bal_bdd_inventario_fechaFin,
						ParametrosInventario.bal_bdd_inventario_lugar };

				// 1.4 Recuperamos los datos del inventario:
				Cursor cInventarios = dtb.query(tabla_inventarios_nombre,
						columnasDeseadasInventario,
						ParametrosInventario.bal_bdd_inventario_numero + " = "
								+ String.valueOf(inv), null, null, null, null);

				int cantidadColumnasInv = cInventarios.getColumnCount();

				Element inventarioActual;// ,articulo;
				boolean tieneArticulos = false;
				if (cInventarios.moveToFirst()) {
					// Element inventario =
					// documento.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2usb(ParametrosInventario.bal_bdd_inventario_numero));
					inventarioActual = documento
							.createElement(Parametros.bal_xml_inventario_root);

					// Recorro las columnas
					for (int i = 0; i < cantidadColumnasInv; i++) {
						// bdd2xml arroja Exception
						Element elemento = documento
								.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2xml(cInventarios.getColumnName(i)));
						elemento.setTextContent(cInventarios.getString(i));
						inventarioActual.appendChild(elemento);
					}

					String[] columnasDeseadas = new String[] {
							ParametrosInventario.bal_bdd_articulo_sector,
							ParametrosInventario.bal_bdd_articulo_codigo,
							// ParametrosInventario.bal_bdd_articulo_inventario,
							ParametrosInventario.bal_bdd_articulo_cantidad,
							ParametrosInventario.bal_bdd_articulo_subtotal,
							ParametrosInventario.bal_bdd_articulo_fechaInicio,
							ParametrosInventario.bal_bdd_articulo_fechaFin,
							// ParametrosInventario.bal_bdd_articulo_foto,
							ParametrosInventario.bal_bdd_articulo_descripcion,
							ParametrosInventario.bal_bdd_articulo_codigo_barra,

							ParametrosInventario.bal_bdd_articulo_existencia_venta,
							ParametrosInventario.bal_bdd_articulo_existencia_deposito,
					};
					Cursor c = dtb.query(tabla_articulos_nombre,
							columnasDeseadas,
							ParametrosInventario.bal_bdd_articulo_inventario
									+ "=" + String.valueOf(inv), null, null,
							null, null);

					// Recuperamos los resultados:
					int cantidadColumnas = c.getColumnCount();
					// boolean tieneArticulos=false;
					// 1.5 Buscamos los articulos
					if (c.moveToFirst()) {
						tieneArticulos = true;
						hayAlMenosUno = true;
						// Luego el detalle de los articulos:
						// 1.6 Para cada articulo
						while (c.isAfterLast() == false) {
							// 1.6.1 Creamos el elmento de datos
							Element medicion = documento
									.createElement(Parametros.bal_xml_articulo_root);
							// Element medicion =
							// documento.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2usb(tabla_articulos_nombre));

							for (int i = 0; i < cantidadColumnas; i++) {
								Element elemento = documento
										.createElement(ParametrosInventario.CONVERSOR_BALIZAS.bdd2usb(c.getColumnName(i)));
								elemento.setTextContent(c.getString(i));
								medicion.appendChild(elemento);
							}
							inventarioActual.appendChild(medicion);

							c.moveToNext();
						}
					}
					// Verificar
					if (tieneArticulos) {
						titulo.appendChild(inventarioActual);
					}
				}
				//
				dtb.close();

				// Verificar esto
				// if(hayAlMenosUno) {
				if (tieneArticulos) {
					documento.appendChild(titulo);
				}

				// Sauvegarde du DOM dans un fichier XML
				// Construccion del nombre del archivode exportacion: n del
				// inventario sobre 4 digitos:
				// 1.7 Generacion del Arcchivo XML local
				String titulo_final;

				if (inv < 0) {
					String titulo_inicial = String.valueOf(Math.abs(inv));
					titulo_final = "D";// +titulo_inicial;
					for (int i = titulo_inicial.length(); i < 4; i++) {
						titulo_final = titulo_final + "0";// ; + titulo_final;

					}
					titulo_final = titulo_final + titulo_inicial;
				} else {
					String titulo_inicial = String.valueOf(inv);
					titulo_final = titulo_inicial;
					for (int i = titulo_inicial.length(); i < 4; i++) {
						titulo_final = "0" + titulo_final;
					}
				}

				File carpeta_usb_export = new File(
						ParametrosInventario.URL_CARPETA_USB_EXPORT + "/");
				File archivo_destino = new File(carpeta_usb_export.getPath()
						+ "/" + titulo_final + ".xml");

				// OLD
				// Si la carpeta no existe:
				// if (archivo_destino.exists() == false) {
				// archivo_destino.mkdirs();
				// archivo_destino.createNewFile();
				// }
				// else {
				// archivo_destino.delete();
				// archivo_destino.createNewFile();
				// }
				//

				// Si la carpeta no existe:
				if (archivo_destino.exists() == false) {
					//
					if (tieneArticulos) {
						// archivo_destino.mkdirs();
						if (carpeta_usb_export.exists() == false) {
							carpeta_usb_export.mkdirs();
						}
						// Arroja IOException
						archivo_destino.createNewFile();
					}
				} else {
					archivo_destino.delete();
					// Verificar esto
					if (tieneArticulos) {
						if (carpeta_usb_export.exists() == false) {
							carpeta_usb_export.mkdirs();
						}

						archivo_destino.createNewFile();
					}
				}
				// Verificar esto
				if (tieneArticulos) {
					// 1.8 Si estan creados todos los archivos, transformamos el
					// DOM a un documento
					HttpWriter.transformerXml(documento, archivo_destino.getAbsolutePath());
				} else {
					// throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
					// "No todos los inventarios tienen artculos");
				}
			} // end for
			if (!hayAlMenosUno) {
				throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
						"No hay inventarios con articulos");
			}
			// Agarramos la excepcion de IO

		} catch (IOException ioEx) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1378 --]" + ioEx.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
					"Error de IO al crear los archivos: " + ioEx.getMessage());
		}
		// Agarramos la ParserConfigurationException
		catch (ParserConfigurationException pcEx) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1389 --]" + pcEx.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
					"Error al crear la fabrica de documentos XML"
							+ pcEx.getMessage());
			// Finalmente agarramos la excepcion ms general
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1400 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.EXPORT_FRACASADO,
					"Error en la conversion de balizas: " + e.getMessage());
		}
	}

	/*
	 * Agregar un articulo nuevo o actualiza la base de datos en la tabla de
	 * articulos
	 * <p>
	 * 1 Test de la preexistencia de la entrada
	 * <p>
	 * &nbsp; &nbsp;1.1 Si la entrada ya existe, actualizamos los datos
	 * <p>
	 * 2 Sino, abrimos la base de datos en modo escritura
	 * <p>
	 * 3 Creamos el registro a insertar como objeto ContentValues
	 * <p>
	 * 4 Insertamos el registro en la base de datos
	 * <p>
	 * 5 Cierre
	 *
	 * @param articulo
	 * @throws ExceptionBDD
	 */
	public void insertArticuloEnBdd(Articulo articulo) throws ExceptionBDD {
		try {
			System.out.println("::: BD 2119 Insert con cb");
			// 1 Test de la preexistencia de la entrada:

			/*Comprueba si esta marcado el parametro de balanza*/
			boolean condicionBalanza = ParametrosInventario.balanza;

			System.out.println("::: BaseDatos 2253 condicionBalanza " + condicionBalanza );

			if (selectArticuloConCodigos(articulo.getSector(),
					articulo.getCodigo(), articulo.getInventario()) != null) // Si
			// existe...
			{
				// 1.1 Si la entrada ya existe, actualizamos los datos:
				updateArticulo(articulo);
				return;
			}

			SQLiteDatabase dtb = this.getWritableDatabase();
			System.out.println("::: BD 2119 Insert con cb " + ParametrosInventario.InventariosVentas);

			if(condicionBalanza == true){
				if (ParametrosInventario.InventariosVentas == true) {
					int valorDepo = articulo.getDepsn();
					System.out.println("::: BD 2139 ACA ENTRA SI ES VENTAS TRUE");
					// 2 Sino, abrimos la base de datos en modo escritura
					// SQLiteDatabase dtb = this.getWritableDatabase();
					// Si hemos abierto correctamente la base de datos
					if (dtb != null) {
						// 3 Creamos el registro a insertar como objeto ContentValues

						System.out.println("::: BaseDatos 2814 Sector " + articulo.getSector());
						System.out.println("::: BaseDatos 2815 Codigo " + articulo.getCodigo());

						ContentValues nuevoRegistro = new ContentValues();
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
								articulo.getSector());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
								articulo.getCodigo());
						String codigo_a_modificar = "";
						String cod1;
						String codigoDeBarras ="";
						String obtenerPesaje ="";
						String corregirDecimales="";
						String descBarra;
						if(articulo.getInventario()==-1 || articulo.getInventario()==-2 || articulo.getInventario()==-3) {
							codigo_a_modificar = articulo.getCodigos_barras_string();
							if(articulo.getCodigos_barras_completo().isEmpty()){
								if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()!=3){
									codigoDeBarras = String.valueOf(articulo.getCantidad());
								}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()!=3){
									codigoDeBarras = String.valueOf(articulo.getCantidad());
								}else if(articulo.getBalanza()!=8){
									codigoDeBarras = String.valueOf(articulo.getCantidad());
								}else if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()==3){
//								corregirDecimales = articulo.getCodigos_barras_completo().get(0).substring(7,12);
//								String parte1 = articulo.getCodigos_barras_completo().get(0).substring(7,9);
//								String parte2 = articulo.getCodigos_barras_completo().get(0).substring(9,12);
//								codigoDeBarras = String.valueOf(parte1 +"."+ parte2);
									codigoDeBarras = String.valueOf(articulo.getCantidad());
								}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()==3){
									codigoDeBarras = String.valueOf(articulo.getCantidad());
								}
							}else{
								if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()!=3){
									codigoDeBarras = articulo.getCodigos_barras_completo().get(0).substring(7,12);
								}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()!=3){
									codigoDeBarras = String.valueOf(articulo.getCantidad());
								}else if(articulo.getBalanza()!=8){
									codigoDeBarras = String.valueOf(articulo.getCodigos_barras());
								}else if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()==3){
									corregirDecimales = articulo.getCodigos_barras_completo().get(0).substring(7,12);
									String parte1 = articulo.getCodigos_barras_completo().get(0).substring(7,9);
									String parte2 = articulo.getCodigos_barras_completo().get(0).substring(9,12);
									codigoDeBarras = String.valueOf(parte1 +"."+ parte2);
								}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()==3){
									codigoDeBarras = String.valueOf(articulo.getCodigos_barras());
								}
							}
							if(articulo.getBalanza()==8){
								descBarra = articulo.getCodigos_barras_completo_string();
							}else{
								descBarra = String.valueOf(articulo.getCodigos_barras().get(0));
							}
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra,
									articulo.getCodigos_barras_string());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra_completo,
									articulo.getCodigos_barras_completo_string());
						}else if(articulo.getInventario()>0){
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra,
									articulo.getCodigos_barras_string_inv());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra_completo,
									articulo.getCodigos_barras_string_inv());
						}else if(articulo.getInventario()<-3){
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra,
									articulo.getCodigos_barras_string_inv());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra_completo,
									articulo.getCodigos_barras_string_inv());
						}
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_inventario,
								articulo.getInventario());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_descripcion,
								articulo.getDescripcion());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_precio_venta,
								articulo.getPrecio_venta());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_precio_costo,
								articulo.getPrecio_costo());
						/*Damian*/
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_existencia_venta,
								articulo.getExis_venta());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_existencia_deposito,
								articulo.getExis_deposito());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
								articulo.getFoto());
						if(articulo.getInventario()==-1 || articulo.getInventario()==-2) {
							if(articulo.getCodigos_barras_completo().isEmpty()){
								obtenerPesaje = String.valueOf(articulo.getCantidad());
							}else{
								if(articulo.getBalanza()==8){
									obtenerPesaje = articulo.getCodigos_barras_completo().get(0).substring(7,12);
								}else{
									obtenerPesaje = String.valueOf(articulo.getCantidad());
								}
							}
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_pesaje,
									obtenerPesaje);

							System.out.println("::: QUIERO VER Q TRAE articulo.getCantidad() "  + articulo.getCantidad());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_cantidad,
									codigoDeBarras);
						}else if(articulo.getInventario()>0){
							obtenerPesaje = "0";
							codigoDeBarras = "-1";

							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_pesaje,
									obtenerPesaje);
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_cantidad,
									codigoDeBarras);
						}else if(articulo.getInventario()<-3){
							obtenerPesaje = "0";
							codigoDeBarras = "-1";

							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_pesaje,
									obtenerPesaje);
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_cantidad,
									codigoDeBarras);
						}

						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_balanza, articulo.getBalanza());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_decimales, articulo.getDecimales());

						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());

						// 4 Insertamos el registro en la base de datos
						long resultado = dtb.insert(tabla_articulos_nombre, null, nuevoRegistro);

						// Test resultado INSERT:
						if (resultado < 0) {
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT, "Imposible agregar el articulo nuevo a la Base De Datos");
						}
					} else {
						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT, "Imposible agregar el articulo nuevo a la Base De Datos");
					}


				}else {
					// 2 Sino, abrimos la base de datos en modo escritura
					// SQLiteDatabase dtb = this.getWritableDatabase();
					int valorDepo = articulo.getDepsn();
					System.out.println("::::: BD 2209 valor depsn " + valorDepo);
					if(valorDepo == 1){
						// 3 Creamos el registro a insertar como objeto ContentValues
						ContentValues nuevoRegistro = new ContentValues();
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
								articulo.getSector());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
								articulo.getCodigo());
						String codigo_a_modificar = articulo.getCodigos_barras_string();
						String codigoDeBarras ="";
						String obtenerPesaje ="";
						String corregirDecimales="";
						String descBarra;
						if(articulo.getCodigos_barras_completo().isEmpty()){
							if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()!=3){
								codigoDeBarras = String.valueOf(articulo.getCantidad());
							}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()!=3){
								codigoDeBarras = String.valueOf(articulo.getCantidad());
							}else if(articulo.getBalanza()!=8){
								codigoDeBarras = String.valueOf(articulo.getCantidad());
							}else if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()==3){
								codigoDeBarras = String.valueOf(articulo.getCantidad());
							}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()==3){
								codigoDeBarras = String.valueOf(articulo.getCantidad());
							}
						}else{
							if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()!=3){
								codigoDeBarras = articulo.getCodigos_barras_completo().get(0).substring(7,12);
							}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()!=3){
								codigoDeBarras = String.valueOf(articulo.getCantidad());
							}else if(articulo.getBalanza()!=8){
								codigoDeBarras = String.valueOf(articulo.getCodigos_barras());
							}else if(articulo.getBalanza()==8 && articulo.getCantidad()== -1.0 && articulo.getDecimales()==3){
								corregirDecimales = articulo.getCodigos_barras_completo().get(0).substring(7,12);
								String parte1 = articulo.getCodigos_barras_completo().get(0).substring(7,9);
								String parte2 = articulo.getCodigos_barras_completo().get(0).substring(9,12);
								codigoDeBarras = String.valueOf(parte1 +"."+ parte2);
							}else if(articulo.getBalanza()==8 && articulo.getCantidad()!= -1.0 && articulo.getDecimales()==3){
								codigoDeBarras = String.valueOf(articulo.getCodigos_barras());
							}


						}

						if(articulo.getBalanza()==8){
							descBarra = articulo.getCodigos_barras_completo_string();
							System.out.println("::: BaseDatos 2351 Cargando cb si es 8 " + descBarra);
						}else{

							descBarra = String.valueOf(articulo.getCodigos_barras().get(0));
							//descBarra = String.valueOf(articulo.getCodigos_barras());
							System.out.println("::: BaseDatos 2355 Cargando cb si no es 8 " + descBarra);
						}
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo_barra, articulo.getCodigos_barras_string());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo_barra_completo, articulo.getCodigos_barras_string());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_inventario, articulo.getInventario());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_descripcion, articulo.getDescripcion());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_precio_venta, articulo.getPrecio_venta());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_precio_costo, articulo.getPrecio_costo());
						/*Damian*/
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_existencia_venta, articulo.getExis_venta());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_existencia_deposito, articulo.getExis_deposito());

						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto, articulo.getFoto());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_cantidad,
//								articulo.getCantidad());
//

						System.out.println("::: BaseDatos 2301 pudo pasar");
						if(articulo.getCodigos_barras_completo().isEmpty()){
							obtenerPesaje = String.valueOf(articulo.getCantidad());
						}else{

							if(articulo.getBalanza()==8){
								obtenerPesaje = articulo.getCodigos_barras_completo().get(0).substring(7,12);
							}else{
								obtenerPesaje = String.valueOf(articulo.getCantidad());
							}

						}

						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_pesaje, obtenerPesaje);
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_cantidad, codigoDeBarras);
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_balanza, articulo.getBalanza());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_decimales, articulo.getDecimales());

						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());

						// 4 Insertamos el registro en la base de datos
						long resultado = dtb.insert(tabla_articulos_nombre, null, nuevoRegistro);

						// Test resultado INSERT:
						if (resultado < 0) {
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT, "Imposible agregar el articulo nuevo a la Base De Datos");
						}
					}else{

						System.out.println("::: BD 2271 NO TIENE QUE ENTRAR CONDICION PERO ENTROOOOOOOOOOOOOOOOOOO");
						// Si hemos abierto correctamente la base de datos
						if (dtb != null) {
							System.out.println("::: BD 2271 NO ");
							// 3 Creamos el registro a insertar como objeto ContentValues
							ContentValues nuevoRegistro = new ContentValues();
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector, articulo.getSector());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo, articulo.getCodigo());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo_barra, articulo.getCodigos_barras_string());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_inventario, articulo.getInventario());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_descripcion, articulo.getDescripcion());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_precio_venta, articulo.getPrecio_venta());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_precio_costo, articulo.getPrecio_costo());
							/*Damian*/
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_existencia_venta,
									articulo.getExis_venta());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_existencia_deposito,
									articulo.getExis_deposito());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_depsn,
									articulo.getDepsn());

							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
									articulo.getFoto());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_cantidad,
									articulo.getCantidad());


							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());

							// 4 Insertamos el registro en la base de datos
							long resultado = dtb.insert(tabla_articulos_nombre, null, nuevoRegistro);

							// Test resultado INSERT:
							if (resultado < 0) {
								throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT, "Imposible agregar el articulo nuevo a la Base De Datos");
							}

						} else {
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
									"Imposible agregar el articulo nuevo a la Base De Datos");
						}
//						System.out.println("::: BD 2271 NO TIENE QUE GUARDAR");
//						dtb.close();
//						return;
					}

					dtb.close();
					return;

				}

			}else{

				if (ParametrosInventario.InventariosVentas == true) {

					int valorDepo = articulo.getDepsn();
					System.out.println("::::: BD 2186 en ventas valor depsn " + valorDepo);

					System.out.println("::: BD 2139 ACA ENTRA SI ES VENTAS TRUE");
					// 2 Sino, abrimos la base de datos en modo escritura
//					SQLiteDatabase dtb = this.getWritableDatabase();

					// Si hemos abierto correctamente la base de datos
					if (dtb != null) {
						System.out.println("::: BD 2139 ACA ENTRA SI ABRIO BIEN LA DB");
						// 3 Creamos el registro a insertar como objeto ContentValues
						ContentValues nuevoRegistro = new ContentValues();
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
								articulo.getSector());
						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
								articulo.getCodigo());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_codigo_barra,
								articulo.getCodigos_barras_string());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_inventario,
								articulo.getInventario());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_descripcion,
								articulo.getDescripcion());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_precio_venta,
								articulo.getPrecio_venta());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_precio_costo,
								articulo.getPrecio_costo());
						/*Damian*/
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_existencia_venta,
								articulo.getExis_venta());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_existencia_deposito,
								articulo.getExis_deposito());

						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
								articulo.getFoto());
						nuevoRegistro.put(
								ParametrosInventario.bal_bdd_articulo_cantidad,
								articulo.getCantidad());


						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());

						// 4 Insertamos el registro en la base de datos
						long resultado = dtb.insert(tabla_articulos_nombre, null, nuevoRegistro);

						// Test resultado INSERT:
						if (resultado < 0) {
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
									"Imposible agregar el articulo nuevo a la Base De Datos");
						}

					} else {
						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
								"Imposible agregar el articulo nuevo a la Base De Datos");
					}

				}else {

					// 2 Sino, abrimos la base de datos en modo escritura
//					SQLiteDatabase dtb = this.getWritableDatabase();

					int valorDepo = articulo.getDepsn();
					System.out.println("::::: BD 2209 valor depsn " + valorDepo);

					if(valorDepo == 1){
						System.out.println("::: BD 2271 NO TIENE QUE ENTRAR CONDICION");
						// Si hemos abierto correctamente la base de datos
						if (dtb != null) {
							System.out.println("::: BD 2271 NO ");
							// 3 Creamos el registro a insertar como objeto ContentValues
							ContentValues nuevoRegistro = new ContentValues();
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
									articulo.getSector());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
									articulo.getCodigo());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra,
									articulo.getCodigos_barras_string());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_inventario,
									articulo.getInventario());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_descripcion,
									articulo.getDescripcion());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_precio_venta,
									articulo.getPrecio_venta());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_precio_costo,
									articulo.getPrecio_costo());
							/*Damian*/
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_existencia_venta,
									articulo.getExis_venta());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_existencia_deposito,
									articulo.getExis_deposito());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_depsn,
									articulo.getDepsn());

							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
									articulo.getFoto());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_cantidad,
									articulo.getCantidad());

							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());

							// 4 Insertamos el registro en la base de datos
							long resultado = dtb.insert(tabla_articulos_nombre, null,
									nuevoRegistro);

							// Test resultado INSERT:
							if (resultado < 0) {
								throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
										"Imposible agregar el articulo nuevo a la Base De Datos");
							}

						} else {
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
									"Imposible agregar el articulo nuevo a la Base De Datos");
						}
					}else{

						System.out.println("::: BD 2271 NO TIENE QUE ENTRAR CONDICION PERO ENTROOOOOOOOOOOOOOOOOOO");
						// Si hemos abierto correctamente la base de datos
						if (dtb != null) {
							System.out.println("::: BD 2271 NO ");
							// 3 Creamos el registro a insertar como objeto ContentValues
							ContentValues nuevoRegistro = new ContentValues();
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
									articulo.getSector());
							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
									articulo.getCodigo());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_codigo_barra,
									articulo.getCodigos_barras_string());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_inventario,
									articulo.getInventario());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_descripcion,
									articulo.getDescripcion());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_precio_venta,
									articulo.getPrecio_venta());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_precio_costo,
									articulo.getPrecio_costo());
							/*Damian*/
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_existencia_venta,
									articulo.getExis_venta());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_existencia_deposito,
									articulo.getExis_deposito());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_depsn,
									articulo.getDepsn());

							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
									articulo.getFoto());
							nuevoRegistro.put(
									ParametrosInventario.bal_bdd_articulo_cantidad,
									articulo.getCantidad());


							nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());

							// 4 Insertamos el registro en la base de datos
							long resultado = dtb.insert(tabla_articulos_nombre, null,
									nuevoRegistro);

							// Test resultado INSERT:
							if (resultado < 0) {
								throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
										"Imposible agregar el articulo nuevo a la Base De Datos");
							}

						} else {
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
									"Imposible agregar el articulo nuevo a la Base De Datos");
						}
//						System.out.println("::: BD 2271 NO TIENE QUE GUARDAR");
//						dtb.close();
//						return;
					}

					dtb.close();
					return;


				}





			}


//			if (ParametrosInventario.InventariosVentas == true) {
//
//				int valorDepo = articulo.getDepsn();
//				System.out.println("::::: BD 2186 en ventas valor depsn " + valorDepo);
//
//				System.out.println("::: BD 2139 ACA ENTRA SI ES VENTAS TRUE");
//				// 2 Sino, abrimos la base de datos en modo escritura
////				SQLiteDatabase dtb = this.getWritableDatabase();
//
//				// Si hemos abierto correctamente la base de datos
//				if (dtb != null) {
//					System.out.println("::: BD 2139 ACA ENTRA SI ABRIO BIEN LA DB");
//					// 3 Creamos el registro a insertar como objeto ContentValues
//					ContentValues nuevoRegistro = new ContentValues();
//					nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
//							articulo.getSector());
//					nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
//							articulo.getCodigo());
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_codigo_barra,
//							articulo.getCodigos_barras_string());
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_inventario,
//							articulo.getInventario());
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_descripcion,
//							articulo.getDescripcion());
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_precio_venta,
//							articulo.getPrecio_venta());
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_precio_costo,
//							articulo.getPrecio_costo());
//					/*Damian*/
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_existencia_venta,
//							articulo.getExis_venta());
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_existencia_deposito,
//							articulo.getExis_deposito());
//
//					nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
//							articulo.getFoto());
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_cantidad,
//							articulo.getCantidad());
//
//
//					nuevoRegistro.put(
//							ParametrosInventario.bal_bdd_articulo_fechaInicio,
//							articulo.getFechaInicio());
//
//					// 4 Insertamos el registro en la base de datos
//					long resultado = dtb.insert(tabla_articulos_nombre, null,
//							nuevoRegistro);
//
//					// Test resultado INSERT:
//					if (resultado < 0) {
//						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
//								"Imposible agregar el articulo nuevo a la Base De Datos");
//					}
//
//				} else {
//					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
//							"Imposible agregar el articulo nuevo a la Base De Datos");
//				}
//
//
//
//
//			}else{
//
//				// 2 Sino, abrimos la base de datos en modo escritura
////				SQLiteDatabase dtb = this.getWritableDatabase();
//
//				int valorDepo = articulo.getDepsn();
//				System.out.println("::::: BD 2209 valor depsn " + valorDepo);
//
//
//
//				if(valorDepo == 1){
//					System.out.println("::: BD 2271 NO TIENE QUE ENTRAR CONDICION");
//					// Si hemos abierto correctamente la base de datos
//					if (dtb != null) {
//						System.out.println("::: BD 2271 NO ");
//						// 3 Creamos el registro a insertar como objeto ContentValues
//						ContentValues nuevoRegistro = new ContentValues();
//						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
//								articulo.getSector());
//						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
//								articulo.getCodigo());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_codigo_barra,
//								articulo.getCodigos_barras_string());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_inventario,
//								articulo.getInventario());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_descripcion,
//								articulo.getDescripcion());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_precio_venta,
//								articulo.getPrecio_venta());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_precio_costo,
//								articulo.getPrecio_costo());
//						/*Damian*/
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_existencia_venta,
//								articulo.getExis_venta());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_existencia_deposito,
//								articulo.getExis_deposito());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_depsn,
//								articulo.getDepsn());
//
//						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
//								articulo.getFoto());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_cantidad,
//								articulo.getCantidad());
//
//
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_fechaInicio,
//								articulo.getFechaInicio());
//
//						// 4 Insertamos el registro en la base de datos
//						long resultado = dtb.insert(tabla_articulos_nombre, null,
//								nuevoRegistro);
//
//						// Test resultado INSERT:
//						if (resultado < 0) {
//							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
//									"Imposible agregar el articulo nuevo a la Base De Datos");
//						}
//
//					} else {
//						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
//								"Imposible agregar el articulo nuevo a la Base De Datos");
//					}
//				}else{
//
//					System.out.println("::: BD 2271 NO TIENE QUE ENTRAR CONDICION PERO ENTROOOOOOOOOOOOOOOOOOO");
//					// Si hemos abierto correctamente la base de datos
//					if (dtb != null) {
//						System.out.println("::: BD 2271 NO ");
//						// 3 Creamos el registro a insertar como objeto ContentValues
//						ContentValues nuevoRegistro = new ContentValues();
//						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
//								articulo.getSector());
//						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
//								articulo.getCodigo());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_codigo_barra,
//								articulo.getCodigos_barras_string());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_inventario,
//								articulo.getInventario());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_descripcion,
//								articulo.getDescripcion());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_precio_venta,
//								articulo.getPrecio_venta());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_precio_costo,
//								articulo.getPrecio_costo());
//						/*Damian*/
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_existencia_venta,
//								articulo.getExis_venta());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_existencia_deposito,
//								articulo.getExis_deposito());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_depsn,
//								articulo.getDepsn());
//
//						nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
//								articulo.getFoto());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_cantidad,
//								articulo.getCantidad());
//
//
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_fechaInicio,
//								articulo.getFechaInicio());
//
//						// 4 Insertamos el registro en la base de datos
//						long resultado = dtb.insert(tabla_articulos_nombre, null,
//								nuevoRegistro);
//
//						// Test resultado INSERT:
//						if (resultado < 0) {
//							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
//									"Imposible agregar el articulo nuevo a la Base De Datos");
//						}
//
//					} else {
//						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
//								"Imposible agregar el articulo nuevo a la Base De Datos");
//					}
////					System.out.println("::: BD 2271 NO TIENE QUE GUARDAR");
////					dtb.close();
////					return;
//				}
//
//				dtb.close();
//				return;
//
//				////////////////////////////////////////
//			}

			// 5 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1688 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el articulo nuevo a la Base De Datos");
		}

	}

	public void insertProveedorEnBdd(Proveedor proveedor) throws ExceptionBDD {
		try {
			System.out.println("::: BD 2119 Insert proveedor con cb");
			if (selectProveedorConCodigos(proveedor.getCodigo()) != null) // Si
			// existe...
			{
				// 1.1 Si la entrada ya existe, actualizamos los datos:
				updateProveedor(proveedor);
				return;
			}
			SQLiteDatabase dtb = this.getWritableDatabase();

			System.out.println("::: BD 2139 ACA ENTRA SI ES VENTAS TRUE");
			if (dtb != null) {
				// 3 Creamos el registro a insertar como objeto ContentValues
				ContentValues nuevoRegistro = new ContentValues();
				nuevoRegistro.put(ParametrosInventario.bal_bdd_proveedores_codigo,
						proveedor.getCodigo());
				//nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
				//		articulo.getCodigo());
				//String codigo_a_modificar = "";
				//String cod1;
				String codigo;

				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_proveedores_codigo,
						proveedor.getCodigo());
//						nuevoRegistro.put(
//								ParametrosInventario.bal_bdd_articulo_descripcion,
//								articulo.getDescripcion());

				// 4 Insertamos el registro en la base de datos
				long resultado = dtb.insert(tabla_proveedores_nombre, null,
						nuevoRegistro);

				// Test resultado INSERT:
				if (resultado < 0) {
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
							"Imposible agregar el proveedor nuevo a la Base De Datos");
				}

			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
						"Imposible agregar el articulo nuevo a la Base De Datos");
			}

			// 5 Cierre:
			dtb.close();
			return;
		} catch (Exception e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1688 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el proveedor nuevo a la Base De Datos");
		}
	}

	public void insertArticuloEnBdd_conFechaFin(Articulo articulo) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 2435 insertArticuloEnBdd_conFechaFin");
			// 1 Test de la preexistencia de la entrada:

			if (selectArticuloConCodigos(articulo.getSector(),
					articulo.getCodigo(), articulo.getInventario()) != null)
			// Si existe...
			{
				// 1.1 Si la entrada ya existe, actualizamos los datos:
				System.out.println("::: BaseDatos 2435 insertArticuloEnBdd existe");

				updateArticulo(articulo);
				return;
			}
			System.out.println("::: BaseDatos 2435 insertArticuloEnBdd NO EXISTE");

			// 2 Sino, abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// 3 Creamos el registro a insertar como objeto ContentValues
				ContentValues nuevoRegistro = new ContentValues();
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_sector,
						articulo.getSector());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo,
						articulo.getCodigo());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_codigo_barra,
						articulo.getCodigos_barras_string());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_codigo_barra_completo,
						articulo.getCodigos_barras_completo_string());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_inventario,
						articulo.getInventario());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_descripcion,
						articulo.getDescripcion());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_precio_venta,
						articulo.getPrecio_venta());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_precio_costo,
						articulo.getPrecio_costo());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
						articulo.getFoto());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_cantidad,
						articulo.getCantidad());
				/*Damian*/
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_existencia_venta,
						articulo.getExis_venta());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_existencia_deposito,
						articulo.getExis_deposito());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_balanza,
						articulo.getBalanza());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_decimales,
						articulo.getDecimales());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaFin, articulo.getFechaFin());
				System.out.println("::: BaseDatos articulo.getCodigo() " + articulo.getCodigo());
				// 4 Insertamos el registro en la base de datos
				long resultado = dtb.insert(tabla_articulos_nombre, null,
						nuevoRegistro);

				// Test resultado INSERT:
				if (resultado < 0) {
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
							"Imposible agregar el articulo nuevo a la Base De Datos");
				}

			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
						"Imposible agregar el articulo nuevo a la Base De Datos");
			}

			// 5 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1688 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el articulo nuevo a la Base De Datos");
		}

	}

	/*
	 * Agrega un articulo a las referencias
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Creamos el registro a insertar como objeto ContentValues
	 * <p>
	 * 3 Insertamos el registro en la base de datos
	 * <p>
	 * 4 Cierre
	 *
	 * @param referencia
	 * @throws ExceptionBDD
	 */
	public void insertReferenciaEnBdd(Articulo referencia) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 2541 insertReferenciaEnBdd");
			// Test de la preexistencia de la entrada:
			// Si la entrada ya existe, actualizamos los datos:
			// No deberia ser necesario por que se supone que es un articulo
			// nuevo
			// if (selectArticuloConCodigos(referencia.getSector(),
			// referencia.getCodigo(), referencia.getInventario()) != null) //
			// Si existe...
			// {
			// updateArticulo(referencia);
			// return;
			// }

			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// 2 Creamos el registro a insertar como objeto ContentValues
				ContentValues nuevoRegistro = new ContentValues();
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_sector,
						referencia.getSector());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_codigo,
						referencia.getCodigo());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_balanza,
						referencia.getBalanza());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_decimales,
						referencia.getDecimales());
				/*Damian*/
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_existencia_venta,
						referencia.getExis_venta());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_existencia_deposito,
						referencia.getExis_deposito());

				/*21/11 Se agrega para que cuando se agrega un articulo que no existe en la base, le agregue al codigo de barras el mismo
				 * numero que de codigo y sector, para que al guardar un segundo articulo o mas que no esten en la base, no se produsca error o conflicto
				 * en la tabla por uno o mas codigos de barras vacios y se genere problemas de primera key.*/
				if(referencia.getSector()<=-1 && referencia.getCodigo()<=-1 && referencia.getCodigos_barras_string()==""){
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_referencia_codigo_barra,
							referencia.getCodigo());
				}else{
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_referencia_codigo_barra,
							referencia.getCodigos_barras_string());
				}

				/*nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_codigo_barra,
						referencia.getCodigos_barras_string());
				*/

				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_descripcion,
						referencia.getDescripcion());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_precio_venta,
						referencia.getPrecio_venta());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_referencia_precio_costo,
						referencia.getPrecio_costo());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_referencia_foto,
						referencia.getFoto());

				// 3 Insertamos el registro en la base de datos
				long resultado = dtb.insert(tabla_referencias_nombre, null,
						nuevoRegistro);

				// Test resultado INSERT:
				if (resultado < 0) {
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
							"Imposible agregar el referencia nuevo a la Base De Datos");
				}

			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
						"Imposible agregar el referencia nuevo a la Base De Datos");
			}

			// 4 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1775 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el referencia nuevo a la Base De Datos");
		}
	}

	/*
	 * Inserta un articulo nuevo en la BD (genera el sec-cod)
	 * <p>
	 * 1 Buscamos nueva clave primaria {sector;codigo}
	 * <p>
	 * 2 La seteamos al nuevo articulo
	 * <p>
	 * 3 Lo insertamos en la BD
	 *
	 * @param articulo
	 * @throws ExceptionBDD
	 */
	public void insertArticuloNuevoEnBdd(Articulo articulo) throws ExceptionBDD {
		System.out.println("::: BaseDatos 2634 insertArticuloNuevoEnBdd");
		// 1 Buscamos nueva clave primaria {sector;codigo}
		SQLiteDatabase dtb = this.getReadableDatabase();

		// En el caso de articulos nuevos creados por el usuario, el CODIGO ser
		// negativo.
		// Buscamos el ms negativo de todos:
		String seleccion[] = new String[] { ParametrosInventario.bal_bdd_articulo_codigo };
		Cursor c = dtb.query(tabla_articulos_nombre, seleccion, null, null,
				null, null, ParametrosInventario.bal_bdd_articulo_codigo
						+ " ASC", "1");

		int nuevoIndice = -1;

		if (c.moveToFirst() == true) {

			nuevoIndice = Math.min(-1, c.getInt(0) - 1);
		}

		// Cierre:
		dtb.close();
		// 2 La seteamos al nuevo articulo
		articulo.setCodigo(nuevoIndice);
		articulo.setSector(nuevoIndice);

		// 3 Lo insertamos en la BD
		insertArticuloEnBdd(articulo);
		return;
	}

	/*
	 * Inserta una referencia nueva en la tabla (genera su sec-cod)
	 * <p>
	 * 1 Buscamos nueva clave primaria {sector;codigo}
	 * <p>
	 * 2 Seteamos los valores de la nueva clave
	 * <p>
	 * 3 Insertamos en la BD
	 *
	 * @param referencia
	 * @throws ExceptionBDD
	 */
	public void insertReferenciaNuevaEnBdd(Articulo referencia)
			throws ExceptionBDD {
		System.out.println("::: BD INSERT REFERENCIA");
		SQLiteDatabase dtb = this.getReadableDatabase();

		// 1 Buscamos nueva clave primaria {sector;codigo}
		// En el caso de articulos nuevos creados por el usuario, el CODIGO ser
		// negativo.
		// Buscamos el ms negativo de todos:
		String seleccion[] = new String[] { ParametrosInventario.bal_bdd_referencia_codigo };
		Cursor c = dtb.query(tabla_referencias_nombre, seleccion, null, null,
				null, null, ParametrosInventario.bal_bdd_referencia_codigo
						+ " ASC", "1");

		int nuevoIndice = -1;

		if (c.moveToFirst() == true) {

			System.out.println("::: BaseDatos 3960 nuevoIndice " + nuevoIndice);
			System.out.println("::: BaseDatos 3960 c.getInt(0) " + c.getInt(0));

			nuevoIndice = Math.min(-1, c.getInt(0) - 1);

			System.out.println("::: BaseDatos 3966 nuevoIndice despues del math " + nuevoIndice);
		}

		// Cierre:
		dtb.close();

		// 2 Seteamos los valores de la nueva clave
		referencia.setCodigo(nuevoIndice);
		referencia.setSector(nuevoIndice);

		// 3 Insertamos en la BD
		insertReferenciaEnBdd(referencia);
		return;

	}

	/*
	 * Ejecuta las sentencias sql de la lista
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Ejecutamos las sentencias entre un "BEGIN" y un "COMMIT"
	 * <p>
	 * 3 Cierre
	 *
	 * @param listaSQL
	 * @throws ExceptionBDD
	 */
	public void insertReferenciasConSQLEnBdd(ArrayList<String> listaSQL)
			throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 2725 insertReferenciasConSQLEnBdd");
			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			// 2 Ejecutamos las sentencias entre un "BEGIN" y un "COMMIT"
			dtb.execSQL("BEGIN");
			for (String requete : listaSQL) {
				try {
					dtb.execSQL(requete);
				} catch (Exception e) {

					GestorLogEventos log = new GestorLogEventos();
					log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
					log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
					log.log("[-- 1899 --]" + e.toString(), 4);

				}
			}
			dtb.execSQL("COMMIT");

			// 3 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1913 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el articulo nuevo a la Base De Datos: "
							+ e.toString());
		}
	}



	/*
	 * Ejecuta las sentencias sql de la lista
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Ejecutamos las sentencias entre un "BEGIN" y un "COMMIT"
	 * <p>
	 * 3 Cierre
	 *
	 * @param listaSQL
	 * @throws ExceptionBDD
	 */
	public void insertProveedoresConSQLEnBdd(ArrayList<String> listaSQL)
			throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 2725 insertProveedoresConSQLEnBdd");
			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			// 2 Ejecutamos las sentencias entre un "BEGIN" y un "COMMIT"
			dtb.execSQL("BEGIN");
			for (String requete : listaSQL) {
				try {
					dtb.execSQL(requete);
				} catch (Exception e) {

					GestorLogEventos log = new GestorLogEventos();
					log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
					log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
					log.log("[-- 1899 --]" + e.toString(), 4);

				}
			}
			dtb.execSQL("COMMIT");

			// 3 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1913 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el articulo nuevo a la Base De Datos: "
							+ e.toString());
		}
	}


	/*
	 * Elimina todos los registros que se cargaron vacios
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Cierre
	 *
	 * @throws ExceptionBDD
	 */
	public void deleteProveedoresVacios()
			throws ExceptionBDD {
		try {
			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			dtb.execSQL("DELETE FROM PROVEEDORES WHERE PROV_DESC IS NULL");
			// 2 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1913 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_DELETE,
					"No se pudo ejecutar el comando en la base de datos: "
							+ e.toString());
		}
	}




	/*
	 * Ejecuta la consulta en la BD
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Ejecutamos la consulta
	 * <p>
	 * 3 Cierre
	 *
	 * @param consulta
	 * @throws ExceptionBDD
	 */
	public void insertDesdeUSBEnBdd(String consulta) throws ExceptionBDD {
		try {
			System.out.println("::: BD 2647 Inserta articulo desde usb");
			// Test de la preexistencia de la entrada:
			// Si la entrada ya existe, actualizamos los datos:
			/*
			 * if (selectArticuloConCodigos(articulo.getSector(),
			 * articulo.getCodigo(), articulo.getInventario()) != null) // Si
			 * existe... { updateArticulo(articulo); return; }
			 */

			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			try {
				// 2 Ejecutamos la consulta
				dtb.execSQL(consulta);
			} catch (Exception e) {

				GestorLogEventos log = new GestorLogEventos();
				log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
				log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
				log.log("[-- 1955 --]" + e.toString(), 4);

			}

			// 3 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1967 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el articulo nuevo a la Base De Datos: "
							+ e.toString());
		}
	}

	/**
	 * Ejecuta todas las sentencias de la lista en la BD
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Ejecutamos las consultas
	 * <p>
	 * 3 Cierre
	 *
	 * @param listaSQL
	 * @throws ExceptionBDD
	 */
	public void insertDesdeUSBEnBdd(ArrayList<String> listaSQL)
			throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 2834 insertDesdeUSBEnBdd");
			// Test de la preexistencia de la entrada:
			// Si la entrada ya existe, actualizamos los datos:
			/*
			 * if (selectArticuloConCodigos(articulo.getSector(),
			 * articulo.getCodigo(), articulo.getInventario()) != null) // Si
			 * existe... { updateArticulo(articulo); return; }
			 */

			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			dtb.execSQL("BEGIN");
			for (String requete : listaSQL) {
				try {
					System.out.println("::: BD 2834  execsql");
					// 2 Ejecutamos las consultas
					dtb.execSQL(requete);
				} catch (Exception e) {

					GestorLogEventos log = new GestorLogEventos();
					log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
					log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
					log.log("[-- 2013 --]" + e.toString(), 4);

				}
			}
			dtb.execSQL("COMMIT");

			// 3 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2058 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el articulo nuevo a la Base De Datos: "
							+ e.toString());
		}
	}

	/*
	 * Agregar un INVENTARIO a la base de datos (o lo actualiza si ya existe)
	 * <p>
	 * 1 Si la entrada ya existe, actualizamos los datos
	 * <p>
	 * 2 Sino, abrimos la base de datos en modo escritura
	 * <p>
	 * 3 Insertamos el registro en la base de dato
	 * <p>
	 * 4 Cierre
	 *
	 * @param inventario
	 *            Objeto que agregar en la base
	 * @throws ExceptionBDD
	 */
	public void insertInventarioEnBdd(Inventario inventario)
			throws ExceptionBDD {
		System.out.println("::: BD 2754 Inserta inventario ");
		try {
			boolean condicionRadio = ParametrosInventario.InventariosVentas;

			// Test de la preexistencia de la entrada:
			// 1 Si la entrada ya existe, actualizamos los datos:
			if (selectInventarioConNumero(inventario.getNumero()) != null) // Si
			// existe...
			{
				System.out.println("::: BD 2754  updateInventario");
				updateInventario(inventario);
				return;
			}

			// 2 Sino, abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// Creamos el registro a insertar como objeto ContentValues
				// ContentValues nuevoRegistro = new ContentValues();
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_numero,
				// inventario.getNumero());
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_descripcion,
				// inventario.getDescripcion());
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_fecha,
				// inventario.getFecha());
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_estado,
				// inventario.getEstado());
				// 3 Insertamos el registro en la base de datos
				// long resultado = dtb.insert(tabla_inventarios_nombre, null,
				// nuevoRegistro);
				String descripcionCompleta = inventario.getDescripcion().trim();
				int n = descripcionCompleta.trim().length();
				String obtenerDescripcion = descripcionCompleta.substring(0, (n - 3));
				//		char car=descripcionCompleta.charAt(n-1);
				int variable_dep = 0;
//				if(car=='0'){
//					variable_dep= -1;
//				}else if(car=='1'){
//					variable_dep= -2;
//				}else{
				variable_dep =inventario.getLugar();
				String str = "";
				if(condicionRadio == true && inventario.getNumero()==-1){
					// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
//					condR=-1;
					System.out.println("::: BD 2754  prepara el string para insertar inventario");
					str = "INSERT INTO " + tabla_inventarios_nombre
							+ " VALUES(" + inventario.getNumero() + "," + "'"
							+ obtenerDescripcion + "'," + "'"
//							+ inventario.getDescripcion() + "'," + "'"
							+ inventario.getFechaInicio() + "'," + "'"
							+ inventario.getFechaFin() + "',"
							+ inventario.getEstado() + "," + variable_dep+ "," +0
							+ ")";
					System.out.println("::: D.V. QUIERO VER VARIABLE_DEP "+variable_dep);
					dtb.execSQL(str);

				}else if(condicionRadio == false && inventario.getNumero()==-2){
					// Esta seleccionado deposito, esto debe continuar sin los campos de ventas
//					condR=-2;
					System.out.println("::: BD 2754  prepara el string para insertar inventario 2");
					str = "INSERT INTO " + tabla_inventarios_nombre
							+ " VALUES(" + inventario.getNumero() + "," + "'"
							+ obtenerDescripcion + "'," + "'"
//							+ inventario.getDescripcion() + "'," + "'"
							+ inventario.getFechaInicio() + "'," + "'"
							+ inventario.getFechaFin() + "',"
							+ inventario.getEstado() + "," + variable_dep+ "," +0
							+ ")";
					System.out.println("::: D.D. QUIERO VER VARIABLE_DEP "+variable_dep);
					dtb.execSQL(str);

				}
				else if(condicionRadio && inventario.getNumero()>0){
					System.out.println("::: BD 2754  prepara el string para insertar inventario 3");
					str = "INSERT INTO " + tabla_inventarios_nombre
							+ " VALUES(" + inventario.getNumero() + "," + "'"
							+ obtenerDescripcion + "'," + "'"
//							+ inventario.getDescripcion() + "'," + "'"
							+ inventario.getFechaInicio() + "'," + "'"
							+ inventario.getFechaFin() + "',"
							+ inventario.getEstado() + "," + -1+ "," +0
							+ ")";
					System.out.println("::: D.O. QUIERO VER VARIABLE_DEP "+variable_dep);
					dtb.execSQL(str);
				}
				else if(!condicionRadio && inventario.getNumero()>0){
					System.out.println("::: BD 2754  prepara el string para insertar inventario 3");
					str = "INSERT INTO " + tabla_inventarios_nombre
							+ " VALUES(" + inventario.getNumero() + "," + "'"
							+ obtenerDescripcion + "'," + "'"
//							+ inventario.getDescripcion() + "'," + "'"
							+ inventario.getFechaInicio() + "'," + "'"
							+ inventario.getFechaFin() + "',"
							+ inventario.getEstado() + "," + -2 + "," +0
							+ ")";
					System.out.println("::: D.O. QUIERO VER VARIABLE_DEP "+variable_dep);
					dtb.execSQL(str);
				}
//				System.out.println("::: BaseDatos 2929 variable_dep " + variable_dep);
//				System.out.println("::: BaseDatos 2929 inventario.getNumero() " + inventario.getNumero());
//				String str = "INSERT INTO " + tabla_inventarios_nombre
//						+ " VALUES(" + inventario.getNumero() + "," + "'"
//						+ obtenerDescripcion + "'," + "'"
////						+ inventario.getDescripcion() + "'," + "'"
//						+ inventario.getFechaInicio() + "'," + "'"
//						+ inventario.getFechaFin() + "',"
//						+ inventario.getEstado() + "," + variable_dep+ "," +0
//						+ ")";
//				System.out.println("::: BD 2791 inserta string inv "+ str);
//				dtb.execSQL(str);
				// Test resultado INSERT:
				// if (resultado < 0) {
				// throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
				// "Imposible agregar el INVENTARIO nuevo a la Base De Datos");
				// }
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT, "Imposible agregar el INVENTARIO nuevo a la Base De Datos");
			}
			// 4 Cierre:
			dtb.close();

			return;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2112 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT, "Imposible agregar el INVENTARIO nuevo a la Base De Datos");
		}
	}

	/*
	 * Agregar un INVENTARIO a la base de datos (o lo actualiza si ya existe)
	 * <p>
	 * 1 Si la entrada ya existe, actualizamos los datos
	 * <p>
	 * 2 Sino, abrimos la base de datos en modo escritura
	 * <p>
	 * 3 Insertamos el registro en la base de dato
	 * <p>
	 * 4 Cierre
	 *
	 * @param inventario
	 *            Objeto que agregar en la base
	 * @throws ExceptionBDD
	 */
	public void insertInventarioComprasEnBdd(Inventario inventario)
			throws ExceptionBDD {
		System.out.println("::: BD 2754 Inserta inventario Compras");
		try {
			// Test de la preexistencia de la entrada:
			// 1 Si la entrada ya existe, actualizamos los datos:
			if (selectInventarioConNumero(inventario.getNumero()) != null) // Si
			// existe...
			{
				updateInventarioCompras(inventario);
				return;
			}
			// 2 Sino, abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();
			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				String descripcionCompleta = inventario.getDescripcion().trim();
				int n = descripcionCompleta.trim().length();
				String obtenerDescripcion = descripcionCompleta.substring(0,(n-3));
				char car=descripcionCompleta.charAt(n-1);
				int variable_dep = 3;
				String str = "";

				if(inventario.getNumero()==-3){
					// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
					str = "INSERT INTO " + tabla_inventarios_nombre
							+ " VALUES(" + inventario.getNumero() + "," + "'"
							+ obtenerDescripcion + "'," + "'"
							+ inventario.getFechaInicio() + "'," + "'"
							+ inventario.getFechaFin() + "',"
							+ inventario.getEstado() + "," + variable_dep+ "," +0
							+ ")";
					System.out.println("::: D.C. QUIERO VER VARIABLE_DEP "+variable_dep);
					dtb.execSQL(str);
				}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
						"Imposible agregar el INVENTARIO nuevo a la Base De Datos");
			}
			// 4 Cierre:
			dtb.close();
			return;
		} catch (Exception e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2112 --]" + e.toString(), 4);
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el INVENTARIO nuevo a la Base De Datos");
		}
	}

	/*
	 * Recupera el objeto ARTICULO del inventario correspondiente con el codigo
	 * del mismo
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Busqueda en la base
	 * <p>
	 * 3 Recorremos el resultado (que debe ser nico normalmente) y creamos el
	 * articulo
	 * <p>
	 * 4 Cerramos la BD
	 * <p>
	 * 5 Devolvemos el articulo
	 *
	 * @param articulo_cod
	 * @return ARTICULO
	 * @throws ExceptionBDD
	 */
	public Articulo selectArticuloConCodigos(int articulo_sector,
											 int articulo_cod, int articulo_inv) throws ExceptionBDD {
		System.out.println("::: BaseDatos 3010 selectArticuloConCodigos");
		try {
			// Salida:
			Articulo articulo;
			System.out.println("::: BaseDatos selectArt " + articulo_sector +" "+ articulo_cod + " " + articulo_inv);
			// 1 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();
			// 2 Busqueda en la base:
			String[] args = new String[] { String.valueOf(articulo_sector),
					String.valueOf(articulo_cod), String.valueOf(articulo_inv) };
			Cursor c = dtb.query(tabla_articulos_nombre, null,
					ParametrosInventario.bal_bdd_articulo_sector + "=? AND "
							+ ParametrosInventario.bal_bdd_articulo_codigo
							+ "=? AND "
							+ ParametrosInventario.bal_bdd_articulo_inventario
							+ "=?", args, null, null, null);
			if (c.moveToFirst()) {
				articulo = new Articulo(
						articulo_sector,
						articulo_cod,
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_balanza)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_decimales)),
						new ArrayList<String>(
								Arrays.asList(c
										.getString(
												c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo_barra))
										.split(","))),
						new ArrayList<String>(
								Arrays.asList("0")),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_inventario)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_descripcion)),
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_venta)),
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_costo)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_foto)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_cantidad)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_subtotal)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_depsn)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_fechaInicio)));


			} else {
				articulo = null;
				System.out.println("::: BaseDatos VACIOOOOO 000000000000000000000000000000000000000");
			}
			// 4 Cerramos la BD
			//		System.out.println(":::CIERRA");
			dtb.close();
			// 5 Devolvemos el articulo
			return articulo;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2194 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible recuperar el ARTICULO cuyo codigo es: "
							+ articulo_cod);
		}
	}

	public Proveedor selectProveedorConCodigos(int proveedor_cod) throws ExceptionBDD {
		System.out.println("::: BaseDatos 3010 selectProveedorConCodigos");
		try {
			// Salida:
			Proveedor proveedor;
			System.out.println("::: BaseDatos selectArt " + proveedor_cod);
			// 1 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();
			// 2 Busqueda en la base:
			String[] args = new String[] { String.valueOf(proveedor_cod)};
			Cursor c = dtb.query(tabla_proveedores_nombre, null,
					ParametrosInventario.bal_bdd_proveedores_codigo + "=? ", args, null, null, null);
			if (c.moveToFirst()) {
				proveedor = new Proveedor(
						proveedor_cod,
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_proveedores_descripcion)));
			} else {
				proveedor = null;
				System.out.println("::: BaseDatos VACIOOOOO 000000000000000000000000000000000000000");
			}
			// 4 Cerramos la BD
			//		System.out.println(":::CIERRA");
			dtb.close();

			return proveedor;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2194 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible recuperar el ARTICULO cuyo codigo es: "
							+ proveedor_cod);
		}
	}

	/*
	 * Recupera el Articulo con el codigo de barra de la tabla articulos
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Busqueda en la base
	 * <p>
	 * 3 Recorremos el resultado (que debe ser nico normalmente) y creamos el
	 * articulo
	 *
	 * @param codigo_barra
	 * @return
	 * @throws ExceptionBDD
	 */
	public Articulo selectArticuloConCodigoBarra(String codigo_barra)
			throws ExceptionBDD {
		System.out.println("::: BaseDatos 3108 selectArticuloConCodigoBarra");
		try {
			// Salida:
			Articulo articulo;

			// 1 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();

			// 2 Busqueda en la base:
			String[] args = new String[] { codigo_barra };
			Cursor c = dtb.query(tabla_articulos_nombre, null,
					ParametrosInventario.bal_bdd_articulo_codigo_barra + "=?",
					args, null, null, null);

			// Nos aseguramos de que existe al menos un registro
			if (c.moveToFirst()) {
				// 3 Recorremos el resultado (que debe ser nico normalmente) y
				// creamos el articulo
				articulo = new Articulo(
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_sector)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_balanza)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_decimales)),
						new ArrayList<String>(
								Arrays.asList(c
										.getString(
												c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo_barra))
										.split(","))),
						new ArrayList<String>(
								Arrays.asList(c
										.getString(
												c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo_barra_completo))
										.split(","))),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_inventario)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_descripcion)),
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_venta)),
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_costo)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_foto)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_cantidad)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_depsn)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_fechaInicio)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_articulo_fechaFin)));
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
						"Imposible recuperar el ARTICULO cuyo codigo de barra es: "
								+ codigo_barra);
			}
			dtb.close();
			return articulo;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2273 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible recuperar el ARTICULO cuyo codigo de barra es: "
							+ codigo_barra);
		}
	}

	/*
	 * Busca en la tabla de referencias una referencia con codigo de barras
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Busqueda en la base
	 * <p>
	 * 3 Recorremos el resultado (que debe ser nico normalmente) y creamos el
	 * articulo de referencia a devolver
	 * <p>
	 * 4 Cerramos y devolvemos
	 * @param codigo_barra
	 * @return
	 * @throws ExceptionBDD
	 */
	public Articulo selectReferenciaConCodigoBarra(String codigo_barra)
			throws ExceptionBDD {
		System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra");
		// Salida:
		Articulo referencia = null;
		// 1 Abrimos la base de datos en modo lectura:
		SQLiteDatabase dtb = this.getReadableDatabase();
		// 2 Busqueda en la base:
		String[] args = new String[] { codigo_barra };
// Cursor c = dtb.query(tabla_referencias_nombre,// null,// ParametrosInventario.bal_bdd_referencia_codigo_barra// + "%%", args, null, null, null);
		codcompleto = codigo_barra;
		Cursor c;
		boolean condicionBalanza = ParametrosInventario.balanza;
		String consulta = "";
		/**
		 * Se valida el largo del codigo de barras para aquellos que tienen menor cantidad de digitos
		 **/
		if(codigo_barra.length()== 13){
			if(condicionBalanza == true){
				String sSubCadena = codigo_barra.substring(0,2);
				int subCadena = Integer.parseInt(sSubCadena);
				String sCodigo = codigo_barra.substring(2,7);
				String peso = codigo_barra.substring(7,12);
				System.out.println("::: BaseDatos selectReferencia 3282 Articulo codigo_barra  valor a pasar ");
				pesoObtenido = peso;
				String valor;
				String codCompleto="";
				switch (subCadena) {
					case 20:
						System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 2");
						valor = sCodigo;
						dtb.execSQL("UPDATE REFERENCIAS SET REF_CBC='"+codcompleto+"' WHERE REF_CB='"+valor+"'");
						consulta = "select * from " + tabla_referencias_nombre + " where "
								+ ParametrosInventario.bal_bdd_referencia_codigo_barra + " = '" + valor + "'";
						c = dtb.rawQuery(consulta, null);
						// Nos aseguramos de que existe al menos un registro
						if (c.moveToFirst()) {
							System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 3");
							// 3 Recorremos el resultado (que debe ser nico normalmente) y// creamos el articulo de// referencia a devolver
							referencia = new Articulo(
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_sector)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_balanza)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_decimales)),
									new ArrayList<String>(
											Arrays.asList(c
													.getString(
															c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra))
													.split(","))),
									new ArrayList<String>(
											Arrays.asList(c
													.getString(
															c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra_completo))
													.split(","))),
									-1,
									c.getString(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_descripcion)),
									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_venta)),
									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_costo)),
									c.getString(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_foto)),
									-1,-1,
									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_venta)),
									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_deposito)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_depsn)), "");
						}
						else {
							System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 4 ");
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
									"Imposible recuperar la REFERENCIA cuyo codigo de barra es: "
											+ codigo_barra);
						}
						break;
					default:
						System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 5");
						valor = codigo_barra;
						dtb.execSQL("UPDATE REFERENCIAS SET REF_CBC='"+codcompleto+"' WHERE REF_CB='"+valor+"'");
						consulta = "select * from " + tabla_referencias_nombre
								+ " where "
								+ ParametrosInventario.bal_bdd_referencia_codigo_barra + " = '"
								+ codigo_barra + "'";
						System.out.println("::: BaseDatos 4163 consulta2 " + consulta);
						c = dtb.rawQuery(consulta, null);
						System.out.println("::: BaseDatos 4163 c.moveToFirst() " + c.moveToFirst());
						// Nos aseguramos de que existe al menos un registro
						if (c.moveToFirst()) {
							System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 6");
							// 3 Recorremos el resultado (que debe ser nico normalmente) y
							// creamos el articulo de	// referencia a devolver
							referencia = new Articulo(
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_sector)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_balanza)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_decimales)),
									new ArrayList<String>(
											Arrays.asList(c
													.getString(
															c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra))
													.split(","))),
									new ArrayList<String>(
											Arrays.asList(c
													.getString(
															c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra_completo))
													.split(","))),
									-1,
									c.getString(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_descripcion)),

									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_venta)),
									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_costo)),
									c.getString(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_foto)),
									-1,-1,
									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_venta)),
									c.getDouble(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_deposito)),
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_referencia_depsn)), "");
						} else {
							System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 7");
							//	System.out.println("::: BaseDatos 3040 codigo_barra " + codigo_barra);
							throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
									"Imposible recuperar la REFERENCIA cuyo codigo de barra es: "
											+ codigo_barra);
						}
						break;
				}
			}else{
				System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 8");
				consulta = "select * from " + tabla_referencias_nombre + " where "
						+ ParametrosInventario.bal_bdd_referencia_codigo_barra + " = '" + codigo_barra + "'";
				System.out.println("consulta " + consulta);
				c = dtb.rawQuery(consulta, null);
				// Nos aseguramos de que existe al menos un registro
				if (c.moveToFirst()) {
					System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 9");
					// 3 Recorremos el resultado (que debe ser nico normalmente) y creamos el articulo de referencia a devolver
					referencia = new Articulo(
							c.getInt(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_sector)),
							c.getInt(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo)),
							c.getInt(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_balanza)),
							c.getInt(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_decimales)),
							new ArrayList<String>(
									Arrays.asList(c
											.getString(
													c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra))
											.split(","))),
							new ArrayList<String>(
									Arrays.asList(c
											.getString(
													c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra_completo))
											.split(","))),
							-1,
							c.getString(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_descripcion)),

							c.getDouble(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_venta)),
							c.getDouble(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_costo)),
							c.getString(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_foto)),
							-1,-1,
							c.getDouble(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_venta)),
							c.getDouble(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_deposito)),
							c.getInt(c
									.getColumnIndex(ParametrosInventario.bal_bdd_referencia_depsn)),"");
					System.out.println(c
							.getColumnIndex(ParametrosInventario.bal_bdd_referencia_sector));
					System.out.println(c
							.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo));
				} else {
					System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 10");
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
							"Imposible recuperar la REFERENCIA cuyo codigo de barra es: "
									+ codigo_barra);
				}
			}
		}else if(codigo_barra.length()!=13){
			System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 8");
			consulta = "select * from " + tabla_referencias_nombre
					+ " where "
					+ ParametrosInventario.bal_bdd_referencia_codigo_barra + " = '"
					+ codigo_barra + "'";
//			System.out.println("consulta " + consulta);
			c = dtb.rawQuery(consulta, null);
			// Nos aseguramos de que existe al menos un registro
			if (c.moveToFirst()) {
				System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 9");
				// 3 Recorremos el resultado (que debe ser nico normalmente) y
				// creamos el articulo de
				// referencia a devolver
				referencia = new Articulo(
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_sector)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_balanza)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_decimales)),
						new ArrayList<String>(
								Arrays.asList(c
										.getString(
												c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra))
										.split(","))),
						new ArrayList<String>(
								Arrays.asList(c
										.getString(
												c.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo_barra_completo))
										.split(","))),
						-1,
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_descripcion)),
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_venta)),
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_precio_costo)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_foto)),
						-1,-1,
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_venta)),
						c.getDouble(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_existencia_deposito)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_referencia_depsn)),"");
				System.out.println(c
						.getColumnIndex(ParametrosInventario.bal_bdd_referencia_sector));
				System.out.println(c
						.getColumnIndex(ParametrosInventario.bal_bdd_referencia_codigo));
			} else {
				System.out.println("::: BaseDatos 3040 selectReferenciaConCodigoBarra 10");
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
						"Imposible recuperar la REFERENCIA cuyo codigo de barra es: "
								+ codigo_barra);
			}
		}
		// 4 Cerramos y devolvemos
		dtb.close();
		return referencia;
	}
	public boolean FijarsesiEsta_o_nelArticulo(String codigo_barra) {
		System.out.println("::: BaseDatos 3264 FijarsesiEsta_o_nelArticulo");
		SQLiteDatabase dtb = this.getReadableDatabase();

		// 2 Busqueda en la base:
		String[] args = new String[] { codigo_barra };
		Cursor c = dtb.query(tabla_referencias_nombre, null,
				ParametrosInventario.bal_bdd_referencia_codigo_barra + "=?",
				args, null, null, null);
		if (c.moveToFirst()) {
			return true;
		} else {
			return false;
		}
	}
	/*
	 * Funcion para buscar un articulo en las bases de datos locales, tanto de
	 * referencias como de articulos en el caso de que se haya cargado nuevo en
	 * este momento
	 * <p>
	 * 1 Busca en las referencias
	 * <p>
	 * 2 Si no se encontro en las referencias, lo buscamos en la tabla de
	 * Articulos por codigo de barra
	 *
	 * @param cod_barra
	 * @return
	 * @throws ExceptionBDD
	 */
	public Articulo selectReferenciaArticuloConCodigoBarra(String cod_barra)
			throws ExceptionBDD {
		System.out.println("::: BaseDatos 3296 selectReferenciaArticuloConCodigoBarra");
		Articulo articuloEncontrado;
		try {
			// 1 Busca en las referencias
			articuloEncontrado = this.selectReferenciaConCodigoBarra(cod_barra);
		} catch (ExceptionBDD e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2387 --]" + e.toString(), 4);
			// No se encontro en la tabla de referencias
			try {
				// 2 Si no se encontro en las referencias, lo buscamos en la
				// tabla de Articulos por codigo de barra
				articuloEncontrado = this.selectArticuloConCodigoBarra(cod_barra);
			} catch (ExceptionBDD ex) {
				GestorLogEventos log1 = new GestorLogEventos();
				log1.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
				log1.log(e.toString(), 4);
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
						"Imposible recuperar la REFERENCIA cuyo codigo de barra es: "
								+ cod_barra);
			}
		}
		return articuloEncontrado;
	}

	/*
	 * Obtiene los ARTICULOS del inventario indicado en parametro
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Sacamos todos los ARTICULOS que tienen el dicho numero de INVENTARIO
	 * <p>
	 * 3 Para cada articlo lo Creamos
	 * <p>
	 * 4 Lo agregamos a la lista de resultados
	 * <p>
	 * 5 Cerramos la BD
	 *
	 * @param numero_inventario
	 * @return
	 * @throws ExceptionBDD
	 */
	public ArrayList<ArticuloVisible> selectArticulosConNumeroInventario(int numero_inventario) throws ExceptionBDD {
		System.out.println("::: BaseDatos 3349 selectArticulosConNumeroInventario " + numero_inventario);
		try {
			// Variable de retorno:
			ArrayList<ArticuloVisible> result = new ArrayList<ArticuloVisible>();
			// 1 Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();
			// 2 Sacamos todos los ARTICULOS que tienen el dicho numero de
			// INVENTARIO
			String[] args = new String[] { String.valueOf(numero_inventario) };
			Cursor c = dtb.query(tabla_articulos_nombre, null,
					ParametrosInventario.bal_bdd_articulo_inventario + "=?",
					args, null, null, null);
			int contador = 0;
			// Combinamos el sector y el codigo bajo formato: [sector]-[codigo]
			if (c.moveToFirst()) {
				while (c.isAfterLast() == false) {
					// 3 Para cada articlo lo Creamos
					String codigo_barra = c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo_barra));ArticuloVisible articulo = new ArticuloVisible(
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_sector)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_balanza)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_decimales)), new ArrayList<String>(Arrays.asList(codigo_barra.split(","))), new ArrayList<String>(Arrays.asList(codigo_barra.split(","))),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_inventario)),
							c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_descripcion)),
							c.getDouble(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_venta)),
							c.getDouble(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_costo)),
							c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_foto)),
							c.getFloat(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_cantidad)),
							c.getFloat(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_subtotal)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_depsn)),
							c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_fechaInicio)));

					// 4 Lo agregamos a la lista de resultados
					contador = contador + 1;
//					if(c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_deposito))==0){
////						c.moveToNext();
//						System.out.println(":::: No quiero q haga nada");
//					}else{
//						result.add(articulo);
//						c.moveToNext();	}
					result.add(articulo);
					c.moveToNext();
				}
			}

			// 5 Cerramos la BD
			dtb.close();
			return result;

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2490 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible obtener los ARTICULOS del INVENTARIO n"
							+ numero_inventario);
		}

	}

	/*COMPRA*/

	public ArrayList<ArticuloVisible> selectArticulosConNumeroInventarioCompra(
			int numero_inventario) throws ExceptionBDD {
		System.out.println("::: BaseDatos 5246 selectArticulosConNumeroInventarioCompra " + numero_inventario);
		try {
			// Variable de retorno:
			ArrayList<ArticuloVisible> result = new ArrayList<ArticuloVisible>();
			// 1 Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();
			// 2 Sacamos todos los ARTICULOS que tienen el dicho numero de
			// INVENTARIO
			String[] args = new String[] { String.valueOf(numero_inventario) };
			Cursor c = dtb.query(tabla_articulos_nombre, null,
					ParametrosInventario.bal_bdd_articulo_inventario + "=?",
					args, null, null, null);
			int contador = 0;
			// Combinamos el sector y el codigo bajo formato: [sector]-[codigo]
			if (c.moveToFirst()) {
				while (c.isAfterLast() == false) {
					// 3 Para cada articlo lo Creamos
					String codigo_barra = c
							.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo_barra));
					ArticuloVisible articulo = new ArticuloVisible(
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_sector)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_balanza)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_decimales)),
							new ArrayList<String>(Arrays.asList(codigo_barra.split(","))),
							new ArrayList<String>(Arrays.asList(codigo_barra.split(","))),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_inventario)),
							c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_descripcion)),
							c.getDouble(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_venta)),
							c.getDouble(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_precio_costo)),
							c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_foto)),
							c.getFloat(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_cantidad)),
							c.getFloat(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_subtotal)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_venta)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_deposito)),
							c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_depsn)),
							c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_fechaInicio)));
					// 4 Lo agregamos a la lista de resultados
					contador = contador + 1;
//					if(c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_articulo_existencia_deposito))==0){
////						c.moveToNext();
//						System.out.println(":::: No quiero q haga nada");
//					}else{
//						result.add(articulo);
//						c.moveToNext();	}
					result.add(articulo);
					c.moveToNext();
				}
			}

			// 5 Cerramos la BD
			dtb.close();
			return result;

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2490 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT, "Imposible obtener los ARTICULOS del INVENTARIO n" + numero_inventario);
		}

	}

	/*
	 * Obtiene la lista de los pares (sector,codigo) de todos los ARTICULOS en
	 * curso que se encuentran en la base
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Ejecutamos la busqueda
	 * <p>
	 * 3 Para cada articulo encontrado generamos la combinacion de sector y
	 * codigo
	 * <p>
	 * 4 Cierre
	 *
	 * @return Lista de los pares de codigos de cada articulo {sector ; codigo}
	 * @throws ExceptionBDD
	 */
	public ArrayList<HashMap<String, Integer>> selectArticulosCodigosEnBdd()
			throws ExceptionBDD {
		System.out.println("::: BaseDatos 3452 ");
		try {
			// Variable de respuesta:
			ArrayList<HashMap<String, Integer>> result = new ArrayList<HashMap<String, Integer>>();

			// 1 Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();

			// Request:
			String[] cols = new String[] {
					ParametrosInventario.bal_bdd_articulo_sector,
					ParametrosInventario.bal_bdd_articulo_codigo };
			// 2 Ejecutamos la busqueda
			Cursor c = dtb.query(tabla_articulos_nombre, cols, null, null,
					null, null, null);

			// Result:
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					// 3 Para cada articulo encontrado generamos la combinacion
					// de sector y codigo
					HashMap<String, Integer> hashmapArticulo = new HashMap<String, Integer>();
					hashmapArticulo
							.put(ParametrosInventario.bal_bdd_articulo_sector,
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_articulo_sector)));
					hashmapArticulo
							.put(ParametrosInventario.bal_bdd_articulo_codigo,
									c.getInt(c
											.getColumnIndex(ParametrosInventario.bal_bdd_articulo_codigo)));
					result.add(hashmapArticulo);
					c.moveToNext();
				}
			} else {
				result = null;
			}
			// 4 Cierre:
			dtb.close();
			return result;
		} catch (Exception e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2563 --]" + e.toString(), 4);
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible inventariar todos los ARTICULOS de la Base De Datos");
		}
	}

	/*
	 * Obtiene la lista de los codigos de todos los ARTICULOS que pertenecen a
	 * un INVENTARIO en particular
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Sacamos todos los ARTICULOS que tienen el dicho numero de INVENTARIO
	 * <p>
	 * 3 Combinamos el sector y el codigo bajo formato: [sector]-[codigo]
	 * <p>
	 * 4 Agregamos el hashmap a la lista
	 * <p>
	 * 5 Cerramos la BD
	 *
	 * @param numero_inventario
	 * @return ArrayList<(sector,codigo)> con cada respuesta bajo forma
	 *         [sector]-[codigo] en String
	 * @throws ExceptionBDD
	 */
	public ArrayList<HashMap<String, Integer>> selectArticulosCodigosConNumeroInventario(
			int numero_inventario) throws ExceptionBDD {
		System.out.println("::: BaseDatos 3527");
		try {
			// Variable de retorno:
			ArrayList<HashMap<String, Integer>> result = new ArrayList<HashMap<String, Integer>>();
			// 1 Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();
			// 2 Sacamos todos los ARTICULOS que tienen el dicho numero de
			// INVENTARIO:
			String[] cols = new String[] {
					ParametrosInventario.bal_bdd_articulo_sector,
					ParametrosInventario.bal_bdd_articulo_codigo };
			String[] args = new String[] { String.valueOf(numero_inventario) };
			Cursor c = dtb.query(tabla_articulos_nombre, cols,
					ParametrosInventario.bal_bdd_articulo_inventario + "=?",
					args, null, null, null);
			// 3 Combinamos el sector y el codigo bajo formato:
			// [sector]-[codigo]
			if (c.moveToFirst()) {
				while (c.isAfterLast() == false) {
					HashMap<String, Integer> hashmapArticulo = new HashMap<String, Integer>();
					hashmapArticulo.put(
							ParametrosInventario.bal_bdd_articulo_sector,
							c.getInt(0));
					hashmapArticulo.put(
							ParametrosInventario.bal_bdd_articulo_codigo,
							c.getInt(1));
					// 4 Agregamos el hashmap a la lista
					result.add(hashmapArticulo);
					c.moveToNext();
				}
			}
			// 5 Cerramos la BD
			dtb.close();
			return result;
		} catch (Exception e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2634 --]" + e.toString(), 4);
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible obtener los articulos del inventario n"
							+ numero_inventario);
		}

	}

	/*
	 * Calcula cuantos articulos contiene cada inventario, los que ya han sido
	 * contado y los que queda por contar
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Clculo de cuantos articulos no han sido inventariado en este
	 * inventario
	 * <p>
	 * 3 Clculo de todos los articulos que inventariar
	 * <p>
	 * 4 Construccin del ArrayList de resultado
	 * <p>
	 * 5 Cerramos la BD
	 *
	 * @param numero_inventario
	 *            Numero del inventario
	 * @return ArrayList<Integer> segn el esquema: [narticulos en el
	 *         inventario ; narticulos ya contados ; n articulos faltantes]
	 * @throws ExceptionBDD
	 *             En caso de fracaso
	 */
	public ArrayList<Integer> selectEstadisticasConIdInventario(
			int numero_inventario) throws ExceptionBDD {
		System.out.println("::: BaseDatos 3604");
		try {
			// Variable de retorno:
			ArrayList<Integer> result = new ArrayList<Integer>();

			// 1 Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();

			// 2 Clculo de cuantos articulos no han sido inventariado en este
			// inventario:
			// (Se admite que un articulo que todavia no ha sido inventariado
			// tiene una cantidad de -1)
			String consultaArticulosSinContar = "SELECT COUNT(*) " + "FROM "
					+ tabla_articulos_nombre + " " + "WHERE "
					+ ParametrosInventario.bal_bdd_articulo_cantidad + "<0 "
					+ "AND " + ParametrosInventario.bal_bdd_articulo_inventario
					+ "=" + numero_inventario + " ";

			Cursor c1 = dtb.rawQuery(consultaArticulosSinContar, null);

			int articulosNonInventariados = 0;
			if (c1.moveToFirst()) {
				articulosNonInventariados = c1.getInt(0);
			}

			// 3 Clculo de todos los articulos que inventariar:
			String consultaTodosArticulosInventario = "SELECT COUNT(*) "
					+ "FROM " + tabla_articulos_nombre + " " + "WHERE "
					+ ParametrosInventario.bal_bdd_articulo_inventario + "="
					+ numero_inventario + " ";

			Cursor c2 = dtb.rawQuery(consultaTodosArticulosInventario, null);

			int articulosTotalesQueInventariar = 0;
			if (c2.moveToFirst()) {
				articulosTotalesQueInventariar = c2.getInt(0);
			}

			// 4 Construccin del ArrayList de resultado:
			result.add(articulosTotalesQueInventariar);
			result.add(articulosTotalesQueInventariar
					- articulosNonInventariados);
			result.add(articulosNonInventariados);

			// 5 Cerramos la BD
			dtb.close();

			return result;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2719 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Generacion de las estadisticas imposible");
		}

	}

	/*
	 * Recupera el objeto INVENTARIO con el numero del mismo
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Busqueda en la base
	 * <p>
	 * 3 Recorremos el resultado (que debe ser unico normalmente) y creamos el
	 * Inventario
	 * <p>
	 * 4 Cerramos la BD
	 *
	 * @return INVENTARIO
	 * @throws ExceptionBDD
	 */
	public Inventario selectInventarioConNumero(int inventario_num)
			throws ExceptionBDD {
		System.out.println("::: BaseDatos 3395 Selecciona inventario con numero");
		try {
			// Salida:
			Inventario inventario = null;

			// 1 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();
		/*	String name = null;
				Cursor c = null;
				c = dtb.rawQuery("select name from person where id="+id, null);
				c.moveToFirst();
				name = c.getString(c.getColumnIndex("name"));
				c.close();
*/System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::>> "+ inventario_num);
			dtb.execSQL("UPDATE INVENTARIOS SET INV_PRODCONT=0");
			// 2 Busqueda en la base:
			String[] args = new String[] { String.valueOf(inventario_num) };
			System.out.println("::: BaseDatos 3579 ");
			Cursor c = dtb.query(tabla_inventarios_nombre, null,
					ParametrosInventario.bal_bdd_inventario_numero + "=?",
					args, null, null, null);
			System.out.println("::: BaseDatos 3583 " + c);
			// Nos aseguramos de que existe al menos un registro
			if (c.moveToFirst()) {
				// 3 Recorremos el resultado (que debe ser unico normalmente) y
				// creamos el Inventario
				System.out.println("::: BaseDatos 3519 va a seleccionar el inventario ");
				inventario = new Inventario(
						c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)),
						c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_descripcion)),
						c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaInicio)),
						c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaFin)),
						c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_estado)),
						c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_lugar)));
				System.out.println("::: BaseDatos 3519 Listo");
			} else {
				inventario = null;
			}

			// 4 Cerramos la BD
			dtb.close();
			return inventario;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2787 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible recuperar el INVENTARIO cuyo numero es: "
							+ inventario_num);
		}
	}
	public Inventario selectInventarioConNumeroCompra(int inventario_num)
			throws ExceptionBDD {
		System.out.println("::: BaseDatos 3395 Selecciona inventario con numero");
		try {
			// Salida:
			Inventario inventario = null;

			// 1 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();
		/*	String name = null;
				Cursor c = null;
				c = dtb.rawQuery("select name from person where id="+id, null);
				c.moveToFirst();
				name = c.getString(c.getColumnIndex("name"));
				c.close();
*/
			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::>> "+ inventario_num);
			dtb.execSQL("UPDATE INVENTARIOS SET INV_PRODCONT=0");
			// 2 Busqueda en la base:
			String[] args = new String[] { String.valueOf(inventario_num) };
			System.out.println("::: BaseDatos 3579 ");
			Cursor c = dtb.query(tabla_inventarios_nombre, null,
					ParametrosInventario.bal_bdd_inventario_numero + "=?",
					args, null, null, null);
			System.out.println("::: BaseDatos 3583 " + c);
			// Nos aseguramos de que existe al menos un registro
			if (c.moveToFirst()) {
				// 3 Recorremos el resultado (que debe ser unico normalmente) y
				// creamos el Inventario
				System.out.println("::: BaseDatos 3519 va a seleccionar el inventario ");
				inventario = new Inventario(
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_inventario_descripcion)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaInicio)),
						c.getString(c
								.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaFin)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_inventario_estado)),
						c.getInt(c
								.getColumnIndex(ParametrosInventario.bal_bdd_inventario_lugar)));
				System.out.println("::: BaseDatos 3519 Listo");
			} else {
				inventario = null;
			}

			// 4 Cerramos la BD
			dtb.close();
			return inventario;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2787 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible recuperar el INVENTARIO cuyo numero es: "
							+ inventario_num);
		}
	}
	public Inventario selectInventarioConNumeroParametro(int inventario_num, int productos_conta)
			throws ExceptionBDD {
		System.out.println("::: BaseDatos 3622 Selecciona inventario con numero y parametro");
		try {
			System.out.println("////////////////////////////////////////////////////////////////////");
			System.out.println("Productos conta "+ productos_conta );
			// Salida:
			Inventario inventario = null;

			// 1 Abrimos la base de datos en modo lectura:
			SQLiteDatabase dtb = this.getReadableDatabase();
			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::>>");
			dtb.execSQL("UPDATE INVENTARIOS SET INV_PRODCONT=1");
			// 2 Busqueda en la base:
			String[] args = new String[] { String.valueOf(inventario_num) };
			Cursor c = dtb.query(tabla_inventarios_nombre, null, ParametrosInventario.bal_bdd_inventario_numero + "=?", args, null, null, null);

			// Nos aseguramos de que existe al menos un registro
			if (c.moveToFirst()) {
				// 3 Recorremos el resultado (que debe ser unico normalmente) y
				// creamos el Inventario
				System.out.println("::: BaseDatos 3519 va a seleccionar el inventario ");
				inventario = new Inventario(
						c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)),
						c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_descripcion)),
						c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaInicio)),
						c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaFin)),
						c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_estado)),
						c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_lugar)));
				System.out.println("::: BaseDatos 3519 Listo");
			} else {
				inventario = null;
			}

			// 4 Cerramos la BD
			dtb.close();
			return inventario;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2787 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible recuperar el INVENTARIO cuyo numero es: "
							+ inventario_num);
		}
	}


	/*
	 * Busca los inventarios en la bd
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Buscamos todos los inventarios
	 * <p>
	 * 3 Genera el hashmap, uno por inventario
	 * <p>
	 * 4 Agrega el hashmap en la entrada correspondiente a ese inventario
	 * <p>
	 * 5 Cerramos conexiones
	 *
	 * @return un HashMap que guarda los inventarios por id de inventario
	 * @throws ExceptionBDD
	 *             si no encuentra inventarios
	 */
	public HashMap<Integer, HashMap<String, String>> selectInventariosEnBdd() throws ExceptionBDD {
		System.out.println("::: BaseDAtos Busca inventario con numero en la bdd");
		System.out.println("::: BaseDAtos 3772 selectInventariosEnBdd");
		HashMap<Integer, HashMap<String, String>> tablaResultados = new HashMap<Integer, HashMap<String, String>>();

		// 1 Abrimos la base de datos en modo lectura
		SQLiteDatabase dtb = this.getReadableDatabase();

		boolean condicionRadio = ParametrosInventario.InventariosVentas;
		Cursor c;

		System.out.println("::: BaseDatos 3861 condicionRadio " + condicionRadio);

		if(condicionRadio){
			// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
//			condR=-1;
			String whereClause = "inv_num=-1 or inv_lug=-1";// or inv_lug=-2

			c = dtb.query(tabla_inventarios_nombre, null , whereClause, null, null, null, null + " ASC" );
			System.out.println("::: BaseDatos 3861 consulta " + c);
		}else{
			// Esta seleccionado deposito, esto debe continuar sin los campos de ventas
//			condR=-2;
			String whereClause = "inv_num=-2 or inv_lug=-2";// or inv_lug=-2
			c = dtb.query(tabla_inventarios_nombre, null , whereClause, null, null, null, null + " ASC" );
			System.out.println("::: BaseDatos 3861 consulta " + c);
		}


		// 2 Buscamos todos los inventarios
		//Cursor c = dtb.query(tabla_inventarios_nombre, null, null, null, null,
				//null, ParametrosInventario.bal_bdd_inventario_numero + " ASC");
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				HashMap<String, String> tablaUnInventario = new HashMap<String, String>();
				/*
				 * 3 Genera el hashmap, uno por inventario
				 */
				tablaUnInventario.put(ParametrosInventario.bal_bdd_inventario_numero, c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)));
				tablaUnInventario.put(ParametrosInventario.bal_bdd_inventario_descripcion, c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_descripcion)));
				tablaUnInventario.put(ParametrosInventario.bal_bdd_inventario_fechaInicio, c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaInicio)));
				tablaUnInventario.put(ParametrosInventario.bal_bdd_inventario_fechaFin, c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaFin)));
				tablaUnInventario.put(ParametrosInventario.bal_bdd_inventario_estado, String.valueOf(c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_estado))));
				/*
				 * 4 Agrega el hashmap en la entrada correspondiente a ese
				 * inventario
				 */
				System.out.println("::: BaseDatos 3838 "+tablaUnInventario.put(ParametrosInventario.bal_bdd_inventario_lugar, String.valueOf(c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_estado)))));
				tablaResultados.put(c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)), tablaUnInventario);

				System.out.println("::: BaseDatos 3847 ver q traeeeeee" + tablaResultados);
				System.out.println("::: BaseDatos 3848 ver q traeeeeee 2" + c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero));
				c.moveToNext();
			}
		} else {
			throw new ExceptionBDD("INVENTARIO", 0);
		}
		// 5 Cerramos conexiones
		c.close();
		dtb.close();
		return tablaResultados;
	}

	/*
	 * Devuelve la lista de los IDs de todos los inventarios cerrados y listos
	 * para ser exportados.
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Buscamos los inventarios con estado = a 0
	 * <p>
	 * 3 Lo agregamos a la tabla de resultados
	 * <p>
	 * 4 Cerramos la BD
	 *
	 * @return ArrayList<Integer> lista de los IDs, lista vaca si no hay
	 *         reultados
	 * @throws ExceptionBDD
	 *             lanzada si no se encuentra la tabla en la base de datos
	 */
	public ArrayList<Integer> selectInventariosCerradosEnBddCompras() throws ExceptionBDD {
		System.out.println("::: BaseDatos 3921 selectInventariosCerradosEnBdd");
		ArrayList<Integer> tablaResultado = new ArrayList<Integer>();
		// 1 Abrimos la base de datos en modo lectura
		SQLiteDatabase dtb = this.getReadableDatabase();

		String consul_where="";
		System.out.println("::: BaseDatos 3928 esta marcado inventario venta");
		//consul_where =ParametrosInventario.bal_bdd_inventario_estado + "=0 and (inv_num=-3 or inv_lug=-3) ";
		consul_where =ParametrosInventario.bal_bdd_inventario_estado + "=0 and (inv_num<=-3) ";

		// 2 Buscamos los inventarios con estado = a 0
		try {
//			Cursor c = dtb.query(tabla_inventarios_nombre, null,
//					ParametrosInventario.bal_bdd_inventario_estado + "=0",
//					null, null, null,
//					ParametrosInventario.bal_bdd_inventario_numero + " ASC");
			Cursor c = dtb.query(tabla_inventarios_nombre, null,
					consul_where,
					null, null, null,
					ParametrosInventario.bal_bdd_inventario_numero + " ASC");
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					// 3 Lo agregamos a la tabla de resultados
					tablaResultado.add(c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)));
					c.moveToNext();
				}
			} else {

				return tablaResultado;
			}
			// 4 Cerramos la BD
			c.close();
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2916 --]" + e.toString(), 4);

			throw new ExceptionBDD(tabla_inventarios_nombre, 0);
		}


		System.out.println("::: BaseDatos 3942 tablaResultado " + tablaResultado);
//esto corrobora el ajuste que esta marcado en preferencias, si es contabilizado o no, y actualiza la tabla con ese valor
// haciendo un update
		System.out.println("::: BD ACTUALIZA INV DEPENDIENDO AJUSTE SELECCIONADO CONTABLILIZADO"
				+ ParametrosInventario.ProductosNoContabilizados);
		if(ParametrosInventario.ProductosNoContabilizados == 1){
			dtb.execSQL("UPDATE INVENTARIOS SET INV_PRODCONT=1");
		}else{
			dtb.execSQL("UPDATE INVENTARIOS SET INV_PRODCONT=0");
		}
		dtb.close();
		return tablaResultado;
	}

	public ArrayList<Integer> selectInventariosCerradosEnBdd() throws ExceptionBDD {
		System.out.println("::: BaseDatos 3921 selectInventariosCerradosEnBdd");
		ArrayList<Integer> tablaResultado = new ArrayList<Integer>();

		// 1 Abrimos la base de datos en modo lectura
		SQLiteDatabase dtb = this.getReadableDatabase();

		boolean condicionRadio = ParametrosInventario.InventariosVentas;

		String consul_where="";
		int condR = 0;
		if(condicionRadio){
			condR=-1;
		}else{
			condR=-2;
		}
		if(condR == -1){
			System.out.println("::: BaseDatos 3928 esta marcado inventario venta");
			consul_where =ParametrosInventario.bal_bdd_inventario_estado + "=0 and (inv_num=-1 or inv_lug=-1) ";
		}else if(condR == -2){
			System.out.println("::: BaseDatos 3928 esta marcado inventario por deposito");
			consul_where =ParametrosInventario.bal_bdd_inventario_estado + "=0 and (inv_num=-2 or inv_lug=-2) ";
		}

		// 2 Buscamos los inventarios con estado = a 0
		try {
//			Cursor c = dtb.query(tabla_inventarios_nombre, null,
//					ParametrosInventario.bal_bdd_inventario_estado + "=0",
//					null, null, null,
//					ParametrosInventario.bal_bdd_inventario_numero + " ASC");
			Cursor c = dtb.query(tabla_inventarios_nombre, null,
					consul_where,
					null, null, null,
					ParametrosInventario.bal_bdd_inventario_numero + " ASC");
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					// 3 Lo agregamos a la tabla de resultados
					tablaResultado.add(c.getInt(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)));
					c.moveToNext();
				}
			} else {

				return tablaResultado;
			}
			// 4 Cerramos la BD
			c.close();
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 2916 --]" + e.toString(), 4);

			throw new ExceptionBDD(tabla_inventarios_nombre, 0);
		}


		System.out.println("::: BaseDatos 3942 tablaResultado " + tablaResultado);
//esto corrobora el ajuste que esta marcado en preferencias, si es contabilizado o no, y actualiza la tabla con ese valor
// haciendo un update
		System.out.println("::: BD ACTUALIZA INV DEPENDIENDO AJUSTE SELECCIONADO CONTABLILIZADO" + ParametrosInventario.ProductosNoContabilizados);
		if(ParametrosInventario.ProductosNoContabilizados == 1){
			dtb.execSQL("UPDATE INVENTARIOS SET INV_PRODCONT=1");
		}else{
			dtb.execSQL("UPDATE INVENTARIOS SET INV_PRODCONT=0");
		}
		dtb.close();
		return tablaResultado;
	}

	/*
	 * Obtiene la lista de los numeros de todos los inventarios en curso que se
	 * encuentran en la base
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Ejecutamos la consulta
	 * <p>
	 * 3 Agregamos cada numero a la lista
	 * <p>
	 * 4 Cierre de BD
	 *
	 * @return ArrayList<Integer>
	 * @throws ExceptionBDD
	 */
	public ArrayList<Integer> selectInventariosNumerosEnBddCompras() throws ExceptionBDD {
		System.out.println("::: BaseDatos 3977 selectInventariosNumerosEnBddCompras");
		boolean condicionRadioSelect = ParametrosInventario.InventariosVentas;
		int condVtaDep = -3;
		try {
			// Variable de respuesta:
			ArrayList<Integer> result = new ArrayList<Integer>();
			// 1 Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();
			// Request:
			String[] col = new String[] { ParametrosInventario.bal_bdd_inventario_numero };
			// 2 Ejecutamos la consulta
			String tipoInventario = "INV_NUM=-3";
			Cursor c = dtb.query(tabla_inventarios_nombre, col, tipoInventario , null,
					null, null, null);
			// Result:
			if (c.moveToFirst()) {
				while (c.isAfterLast() == false) {
					result.add(c.getInt(0));
					c.moveToNext();
				}
			} else {
				return result;
			}
			// 4 Cierre de BD
			dtb.close();
			return result;
		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.log("[-- 2976 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible inventariar todos los INVENTARIOS en curso");
		}
	}

	public ArrayList<Integer> selectInventariosNumerosEnBdd() throws ExceptionBDD {
		System.out.println("::: BaseDatos 3977 selectInventariosNumerosEnBdd");
		boolean condicionRadioSelect = ParametrosInventario.InventariosVentas;
		int condVtaDep = 0;
//		System.out.println("::: BaseDatos 4032 condicionRadioSelect "+ condicionRadioSelect);
		if(condicionRadioSelect==true){
			System.out.println("BD 4043 VENTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			condVtaDep = -1;
		}else if (condicionRadioSelect==false){
			condVtaDep = -2;
			System.out.println("BD 4043 DEPOSITOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		}
		System.out.println("BD 4043 condVtaDep " + condVtaDep);

		//		if(condicionRadio==false && valorapasar==-1){
//			//deposito
//			System.out.println("DEPOSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
//				c = dtb
//				.query(tabla_inventarios_nombre,
//						new String[] { ParametrosInventario.bal_bdd_inventario_estado },
//						ParametrosInventario.bal_bdd_inventario_numero + "=?",
//						new String[] {"-2"}, null, null, null);
//		}else{
//			//ventas
//			System.out.println("VENTASSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
//				c = dtb
//					.query(tabla_inventarios_nombre,
//							new String[] { ParametrosInventario.bal_bdd_inventario_estado },
//							ParametrosInventario.bal_bdd_inventario_numero + "=?",
//							new String[] { String.valueOf(id) }, null, null, null);
//		}
		try {
			// Variable de respuesta:
			ArrayList<Integer> result = new ArrayList<Integer>();

			// 1 Abrimos la base de datos en modo lectura
			SQLiteDatabase dtb = this.getReadableDatabase();

			// Request:
			String[] col = new String[] { ParametrosInventario.bal_bdd_inventario_numero };
			// 2 Ejecutamos la consulta
			System.out.println("::: BaseDatos 4043 col ===== tabla_inventarios_nombre " + tabla_inventarios_nombre);
//		Cursor c = ;
			String tipoInventario = "";
			if(condVtaDep==-1){
				tipoInventario = "INV_NUM!=-2";
//	 c = dtb.query(tabla_inventarios_nombre, col, "INV_NUM!=-2", null,
//			null, null, null);

			}else if(condVtaDep==-2){
//	c = dtb.query(tabla_inventarios_nombre, col, "INV_NUM!=-1", null,
//			null, null, null);
				tipoInventario = "INV_NUM!=-1";

			}
			Cursor c = dtb.query(tabla_inventarios_nombre, col, tipoInventario , null,
					null, null, null);
//			Cursor c = dtb.query(tabla_inventarios_nombre, col, null, null,
//					null, null, null);

			// Result:
			if (c.moveToFirst()) {

				if(condVtaDep==-1){
					while (c.isAfterLast() == false) {
						// 3 Agregamos cada numero a la lista
						if(condVtaDep!=-2){
							result.add(c.getInt(0));
						}
						c.moveToNext();
					}
				}else if(condVtaDep==-2){
					while (c.isAfterLast() == false) {
						// 3 Agregamos cada numero a la lista
						if(condVtaDep!=-1){
							result.add(c.getInt(0));
						}
						c.moveToNext();
					}
				}

//				while (c.isAfterLast() == false) {
//					// 3 Agregamos cada numero a la lista
//					System.out.println("::: BaseDatos 4043 Campo inv_lug " + c.getInt(0));
//					if(condVtaDep==-1){
//						System.out.println("::: BaseDatos 4043 entro en el -1 que guarda ");
//					}
//					result.add(c.getInt(0));
//					System.out.println("::: BaseDatos 4043 result: " + result);
//					c.moveToNext();
//				}
//				System.out.println("::: BaseDatos 3717 result: " + result);
			} else {
				return result;
			}

			// 4 Cierre de BD
			dtb.close();

			return result;

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.log("[-- 2976 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_SELECT,
					"Imposible inventariar todos los INVENTARIOS en curso");
		}
	}

	/*
	 * Actualiza los datos de un ARTICULO pasado como parametro
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Creamos el registro a insertar como objeto ContentValues con los
	 * valores correspondientes
	 * <p>
	 * 3 Insertamos el registro en la base de datos en modo actualizacion
	 * <p>
	 * 4 Cerramos la conexion
	 *
	 * @param articulo
	 * @throws ExceptionBDD
	 */
	public void updateArticulo(Articulo articulo) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 3649 updateArticulo ");
			// Aca solamente hacemos update de fecha o cantidades o foto:
			// ATENCION ATENCION ATENCION:
			// El update implica la concatenacion de los codigos de barras de
			// los articulos identicos
			// con multiples codigos de barras SI LOS DOS ARTICULOS TIENEN CB
			// DIFERENTES:
			// Articulo articuloViejo =
			// selectArticuloConCodigos(articulo.getSector(),
			// articulo.getCodigo(), articulo.getInventario());
			// String nueva_cadena_codigos_barras =
			// articuloViejo.getCodigos_barras_string() + "," +
			// articulo.getCodigos_barras_string();

			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// 2 Creamos el registro a insertar como objeto ContentValues con los valores correspondientes
				ContentValues nuevoRegistro = new ContentValues();

				// !!!! En caso de update, reemplazamos los valores de CB con
				// los nuevos:
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_codigo_barra,
				// nueva_cadena_codigos_barras);
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_inventario,
				// articulo.getInventario());
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_descripcion,
				// articulo.getDescripcion());
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_precio_venta,
				// articulo.getPrecio_venta());
				// nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_precio_costo,
				// articulo.getPrecio_costo());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto, articulo.getFoto());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_cantidad, articulo.getCantidad());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_subtotal, articulo.getSubtotal());

				// Cuidado, si el nuevo valor es "No Tomado" (cantidad Q = -1),
				// hay que suprimir la fecha en la BDD:

				// MODIFICACION DE FECHAS:
				// Fecha inicio: si no tiene valor en fecha inicio, le ponemos
				// una:
				//SE COMENTA PARA QUE LA FECHA DE INICIO Y FIN SEAN LAS DEL ULTIMO ARTICULO CARGADO.
//				String fechaI = articulo.getFechaInicio();
//				if (fechaI.length() == 0) {
					articulo.setFechaInicio(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//				}
				// Fecha fin: se modifica cada vez:
				articulo.setFechaFin(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

				if (articulo.getCantidad() < 0) {
					// Es no tomado
					nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, "");
					nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaFin, "");
				} else {
					nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaFin());
					nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaFin, articulo.getFechaFin());
				}

				// 3 Insertamos el registro en la base de datos en modo
				// actualizacion
				int resultado = dtb.update(tabla_articulos_nombre,
								nuevoRegistro,
								ParametrosInventario.bal_bdd_articulo_sector
										+ "=? AND "
										+ ParametrosInventario.bal_bdd_articulo_codigo
										+ "=? AND "
										+ ParametrosInventario.bal_bdd_articulo_inventario
										+ "=?",
								new String[] {
										String.valueOf(articulo.getSector()),
										String.valueOf(articulo.getCodigo()),
										String.valueOf(articulo.getInventario()) });
				System.out.println("****************GUARDA****************");
				// Test del resultado:
				if (resultado <= 0) {
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
							"Imposible actualizar los datos del ARTICULO (1) cuyo codigo es: "
									+ articulo.getSector() + "-"
									+ articulo.getCodigo());
				}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
						"Imposible actualizar los datos del ARTICULO (2) cuyo codigo es: "
								+ articulo.getSector() + "-"
								+ articulo.getCodigo());
			}

			// 4 Cerramos la conexion
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 3110 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
					"Imposible actualizar los datos del ARTICULO cuyo codigo es: "
							+ articulo.getSector() + "-" + articulo.getCodigo());
		}
	}

	public void updateProveedor(Proveedor proveedor) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 3649 updateArticulo ");
			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();
			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// 2 Creamos el registro a insertar como objeto ContentValues
				// con los valores correspondientes
				ContentValues nuevoRegistro = new ContentValues();
				// !!!! En caso de update, reemplazamos los valores de CB con
				// los nuevos:
				nuevoRegistro.put(ParametrosInventario.bal_bdd_proveedores_codigo,
						proveedor.getCodigo());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_proveedores_descripcion,
						proveedor.getNombre());
				// Cuidado, si el nuevo valor es "No Tomado" (cantidad Q = -1),
				// hay que suprimir la fecha en la BDD:
				// MODIFICACION DE FECHAS:
				// Fecha inicio: si no tiene valor en fecha inicio, le ponemos
				// una:
				// 3 Insertamos el registro en la base de datos en modo
				// actualizacion
				int resultado = dtb
						.update(tabla_proveedores_nombre,
								nuevoRegistro,
								ParametrosInventario.bal_bdd_proveedores_codigo
										+ "=?",
								new String[] {
										String.valueOf(proveedor.getCodigo())});
				// Test del resultado:
				if (resultado <= 0) {
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
							"Imposible actualizar los datos del Proveedor(1) cuyo codigo es: "
									+ proveedor.getCodigo());
				}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
						"Imposible actualizar los datos del Proveedor (2) cuyo codigo es: "
								+ proveedor.getCodigo());
			}
			// 4 Cerramos la conexion
			dtb.close();

		} catch (Exception e) {
			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 3110 --]" + e.toString(), 4);
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
					"Imposible actualizar los datos del Proveedor cuyo codigo es: "
							+ proveedor.getCodigo());
		}
	}

	/*
	 * Actualiza los Codigos de barra de un articulo dado
	 * <p>
	 * 1 Busca el articulo en la BD
	 * <p>
	 * 2 Agrega los codigos de barras del articulo pasado al de la BD
	 * <p>
	 * 3 Abrimos la base de datos en modo escritura
	 * <p>
	 * 4 Creamos el registro a insertar como objeto ContentValues
	 * <p>
	 * 5 Insertamos el registro en la base de datos
	 * <p>
	 * 6 Cerramos la BD
	 *
	 * @param articulo
	 * @throws ExceptionBDD
	 */
	public void updateCbArticulo(Articulo articulo) throws ExceptionBDD {
		System.out.println("::: BaseDatos 4177 updateCbArticulo");
		try {
			// ATENCION ATENCION ATENCION:
			// El update implica la concatenacion de los codigos de barras de
			// los
			// articulos identicos
			// con multiples codigos de barras SI LOS DOS ARTICULOS TIENEN CB
			// DIFERENTES:
			// 1 Busca el articulo en la BD
			Articulo articuloViejo = selectArticuloConCodigos(
					articulo.getSector(), articulo.getCodigo(),
					articulo.getInventario());
			// 2 Agrega los 2001167019254codigos de barras del articulo pasado al de la BD
			String nueva_cadena_codigos_barras = articuloViejo
					.getCodigos_barras_string()
					+ ","
					+ articulo.getCodigos_barras_string();

			// 3 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// 4 Creamos el registro a insertar como objeto ContentValues
				ContentValues nuevoRegistro = new ContentValues();

				// !!!! En caso de update, reemplazamos los valores de CB con
				// los nuevos:
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_codigo_barra,
						nueva_cadena_codigos_barras);
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_inventario,
						articulo.getInventario());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_descripcion,
						articulo.getDescripcion());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_precio_venta,
						articulo.getPrecio_venta());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_precio_costo,
						articulo.getPrecio_costo());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_foto,
						articulo.getFoto());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_cantidad,
						articulo.getCantidad());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());
				nuevoRegistro.put(ParametrosInventario.bal_bdd_articulo_fechaInicio, articulo.getFechaInicio());
				/*Damian*/
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_existencia_venta,
						articulo.getExis_venta());
				nuevoRegistro.put(
						ParametrosInventario.bal_bdd_articulo_existencia_deposito,
						articulo.getExis_deposito());

				// 5 Insertamos el registro en la base de datos
				int resultado = dtb
						.update(tabla_articulos_nombre,
								nuevoRegistro,
								ParametrosInventario.bal_bdd_articulo_sector
										+ "=? AND "
										+ ParametrosInventario.bal_bdd_articulo_codigo
										+ "=? AND "
										+ ParametrosInventario.bal_bdd_articulo_inventario
										+ "=?",
								new String[] {
										String.valueOf(articulo.getSector()),
										String.valueOf(articulo.getCodigo()),
										String.valueOf(articulo.getInventario()) });

				// Test del resultado:
				if (resultado <= 0) {
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
							"Imposible actualizar los datos del ARTICULO (1) cuyo codigo es: "
									+ articulo.getSector() + "-"
									+ articulo.getCodigo());
				}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
						"Imposible actualizar los datos del ARTICULO (2) cuyo codigo es: "
								+ articulo.getSector() + "-"
								+ articulo.getCodigo());
			}
			// 6 Cerramos la BD
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 3228 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
					"Imposible actualizar los datos del ARTICULO cuyo codigo es: "
							+ articulo.getSector() + "-" + articulo.getCodigo());

		}
	}

	/*
	 * Actualiza los datos de un INVENTARIO
	 * <p>
	 * 1 Abrimos la base de datos en modo escritura
	 * <p>
	 * 2 Creamos el registro a actualizar como objeto ContentValues
	 * <p>
	 * 3 Insertamos el registro para actualizar en la base de datos
	 * <p>
	 * 4 Cerramos la BD
	 *
	 * @param inventario
	 * @throws ExceptionBDD
	 */
	public void updateInventario(Inventario inventario) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 4300 updateInventario");
			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();

			boolean condicionRadio = ParametrosInventario.InventariosVentas;

			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// 2 Creamos el registro a actualizar como objeto ContentValues

				ContentValues nuevoRegistro = new ContentValues();

				System.out.println("::: BaseDatos 4300 aca siiii condicionRadio " +condicionRadio +" inventario.getNumero() " +inventario.getNumero());

				if(condicionRadio == true && inventario.getNumero()==-1){
					// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
//					condR=-1;

					System.out.println("::: BaseDatos 4468 inventario.getNumero() " + inventario.getNumero());
//					ContentValues nuevoRegistro = new ContentValues();
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_numero,
							inventario.getNumero());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_descripcion,
							inventario.getDescripcion());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_fechaInicio, inventario.getFechaInicio());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_fechaFin,
							inventario.getFechaFin());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_estado,
							inventario.getEstado());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_lugar,
							inventario.getLugar());

					int resultado = dtb
							.update(tabla_inventarios_nombre, nuevoRegistro,
									ParametrosInventario.bal_bdd_inventario_numero
											+ "=?", new String[] { String
											.valueOf(inventario.getNumero()) });

					// Test del resultado:
					if (resultado <= 0) {
						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
								"Imposible actualizar los datos del INVENTARIO cuyo numero es: "
										+ String.valueOf(inventario.getNumero()));
					}
				}else if(condicionRadio == false && inventario.getNumero()==-2){
					// Esta seleccionado deposito, esto debe continuar sin los campos de ventas
//					condR=-2;

					System.out.println("::: BaseDatos 4468 inventario.getNumero() " + inventario.getNumero());
//					ContentValues nuevoRegistro = new ContentValues();
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_numero,
							inventario.getNumero());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_descripcion,
							inventario.getDescripcion());
					nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_fechaInicio, inventario.getFechaInicio());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_fechaFin,
							inventario.getFechaFin());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_estado,
							inventario.getEstado());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_lugar,
							inventario.getLugar());

					int resultado = dtb
							.update(tabla_inventarios_nombre, nuevoRegistro,
									ParametrosInventario.bal_bdd_inventario_numero
											+ "=?", new String[] { String
											.valueOf(inventario.getNumero()) });

					// Test del resultado:
					if (resultado <= 0) {
						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
								"Imposible actualizar los datos del INVENTARIO cuyo numero es: "
										+ String.valueOf(inventario.getNumero()));
					}
				}

				// 3 Insertamos el registro para actualizar en la base de datos
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE, "Imposible actualizar los datos del INVENTARIO cuyo numero es: " + String.valueOf(inventario.getNumero()));
			}
			// 4 Cerramos la BD
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 3305 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
					"Imposible actualizar los datos del INVENTARIO cuyo numero es: "
							+ String.valueOf(inventario.getNumero()));

		}
	}

	public void updateInventarioCompras(Inventario inventario) throws ExceptionBDD {
		try {
			System.out.println("::: BaseDatos 4300 updateInventarioCompras");
			SQLiteDatabase dtb = this.getWritableDatabase();
			//boolean condicionRadio = ParametrosInventario.InventariosVentas;

			if (dtb != null) {
				ContentValues nuevoRegistro = new ContentValues();
				//if(condicionRadio == true && inventario.getNumero()==-1){
				if(inventario.getNumero()==-3){

					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_numero,
							inventario.getNumero());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_descripcion,
							inventario.getDescripcion());
					nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_fechaInicio, inventario.getFechaInicio());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_fechaFin,
							inventario.getFechaFin());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_estado,
							inventario.getEstado());
					nuevoRegistro.put(
							ParametrosInventario.bal_bdd_inventario_lugar,
							inventario.getLugar());

					int resultado = dtb
							.update(tabla_inventarios_nombre, nuevoRegistro,
									ParametrosInventario.bal_bdd_inventario_numero
											+ "=?", new String[] { String
											.valueOf(inventario.getNumero()) });

					// Test del resultado:
					if (resultado <= 0) {
						throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
								"Imposible actualizar los datos del INVENTARIO cuyo numero es: "
										+ String.valueOf(inventario.getNumero()));
					}
				}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
						"Imposible actualizar los datos del INVENTARIO cuyo numero es: "
								+ String.valueOf(inventario.getNumero()));
			}
			// 4 Cerramos la BD
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 3305 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE,
					"Imposible actualizar los datos del INVENTARIO cuyo numero es: "
							+ String.valueOf(inventario.getNumero()));

		}
	}

	public void updateInventario(int num_inventario, int estado_nuevo) throws ExceptionBDD {
		System.out.println("::: BaseDatos 4378 updateInventario");
		try {
			// 1 Abrimos la base de datos en modo escritura
			SQLiteDatabase dtb = this.getWritableDatabase();
			System.out.println("::: BaseDatos 4378 estado_nuevo " + estado_nuevo);
			System.out.println("::: BaseDatos 4378 num_inventario " + num_inventario);
			// Si hemos abierto correctamente la base de datos
			if (dtb != null) {
				// 2 Creamos el registro a actualizar como objeto ContentValues
				ContentValues nuevoRegistro = new ContentValues();
				nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_estado, estado_nuevo);
				nuevoRegistro.put(ParametrosInventario.bal_bdd_inventario_fechaFin, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				// 3 Actualizamos el registro en la base de datos
				int resultado = dtb.update(tabla_inventarios_nombre, nuevoRegistro, ParametrosInventario.bal_bdd_inventario_numero + "=?", new String[] { String.valueOf(num_inventario) });

				// Test del resultado:
				if (resultado <= 0) {
					throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE, "(1) Imposible actualizar los datos del INVENTARIO cuyo numero es: " + String.valueOf(num_inventario));
				}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE, "(2) Imposible actualizar los datos del INVENTARIO cuyo numero es: "+ String.valueOf(num_inventario));
			}
			// 4 Cerramos la BD
			dtb.close();

		} catch (Exception e) {

			GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 3370 --]" + e.toString(), 4);

			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_UPDATE, "(3) Imposible actualizar los datos del INVENTARIO cuyo numero es: " + String.valueOf(num_inventario));

		}
	}

	public ArrayList<Referencia> getArticulosAll() {
		System.out.println("::: BaseDatos 4429 getArticulosAll");
		ArrayList Referencias = new ArrayList<Referencia>();
		String query_1 = "Select * from "
				+ ParametrosInventario.tabla_referencias;
		SQLiteDatabase dtb_1 = this.getReadableDatabase();
		Cursor c_1 = dtb_1.rawQuery(query_1, null);

		try {
			if (c_1.moveToFirst()) {

				do {

					/*String sector = c_1.getString(0);
					String codigo = c_1.getString(1);
					String codigoBarra = c_1.getString(2);
					String descripcion = c_1.getString(3);
					String precioVenta = c_1.getString(4);
					String precioCosto = c_1.getString(5);
					String foto = c_1.getString(6);
*/
					String sector = c_1.getString(0);
					String codigo = c_1.getString(1);
					String balanza = c_1.getString(2);
					String decimales = c_1.getString(3);
					String exisventa = c_1.getString(4);
					String exisdep = c_1.getString(5);
					String codigoBarra = c_1.getString(6);
					String codigoBarraC = c_1.getString(7);
					String descripcion = c_1.getString(8);
					String precioVenta = c_1.getString(9);
					String precioCosto = c_1.getString(10);
					String depsn = c_1.getString(11);
					//String foto = c_1.getString(12);
					String foto = "";

	/*				System.out.println("::: BaseDatos getArticulosAll do sector " + sector);
					System.out.println("::: BaseDatos getArticulosAll do codigo " + codigo);
					System.out.println("::: BaseDatos getArticulosAll do balanza " + balanza);
					System.out.println("::: BaseDatos getArticulosAll do decimales " + decimales);
					System.out.println("::: BaseDatos getArticulosAll do exisventa " + exisventa);
					System.out.println("::: BaseDatos getArticulosAll do exisdep " + exisdep);
					System.out.println("::: BaseDatos getArticulosAll do codigoBarra " + codigoBarra);
					System.out.println("::: BaseDatos getArticulosAll do descripcion " + descripcion);
					System.out.println("::: BaseDatos getArticulosAll do precioVenta " + precioVenta);
					System.out.println("::: BaseDatos getArticulosAll do precioCosto " + precioCosto);
					System.out.println("::: BaseDatos getArticulosAll do depsn " + depsn);
					//		System.out.println("::: BaseDatos getArticulosAll do foto " + foto);
*/
					//		String exisVenta = c_1.getString(7);
					//		String exisDeposito = c_1.getString(8);

					Referencia referencia = new Referencia();
					referencia.setSector(Integer.parseInt(sector));
					referencia.setArticulo(Integer.parseInt(codigo));
					referencia.setBalanza(Integer.parseInt(balanza));
					referencia.setDecimales(Integer.parseInt(decimales));
					referencia.setCodigo_barra(codigoBarra);
					referencia.setDescripcion(descripcion);
					referencia.setPrecio_venta(Double.parseDouble(precioVenta));
					referencia.setPrecio_costo(Double.parseDouble(precioCosto));

					referencia.setExis_venta(Double.parseDouble(exisventa));
					referencia.setExis_deposito(Double.parseDouble(exisdep));
					referencia.setFoto(foto);
					//System.out.println("::: BaseDatos Referencia  " + referencia);
					Referencias.add(referencia);
				} while (c_1.moveToNext());
			}

		} catch (Exception e1) {

		}
		dtb_1.close();

		return Referencias;

	}

	/*
	 * Busca los inventarios en la bd
	 * <p>
	 * 1 Abrimos la base de datos en modo lectura
	 * <p>
	 * 2 Buscamos todos los inventarios
	 * <p>
	 * 3 Genera el hashmap, uno por inventario
	 * <p>
	 * 4 Agrega el hashmap en la entrada correspondiente a ese inventario
	 * <p>
	 * 5 Cerramos conexiones
	 *
	 * @return un HashMap que guarda los inventarios por id de inventario
	 * @throws ExceptionBDD
	 *             si no encuentra inventarios
	 */
	public HashMap<Integer, HashMap<String, String>> selectInventariosCompraEnBdd() throws ExceptionBDD {
		System.out.println("::: BaseDAtos 5956 selectInventariosCompraEnBdd");
		HashMap<Integer, HashMap<String, String>> tablaResultados = new HashMap<Integer, HashMap<String, String>>();
		// 1 Abrimos la base de datos en modo lectura
		SQLiteDatabase dtb = this.getReadableDatabase();
		Cursor c;
		String whereClause = "inv_lug=3";//
		c = dtb.query(tabla_inventarios_nombre, null , whereClause, null, null,
				null, "INV_NUM"+ " ASC" );
		// 2 Buscamos todos los inventarios
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				HashMap<String, String> tablaUnInventario = new HashMap<String, String>();
				/*
				 * 3 Genera el hashmap, uno por inventario
				 */
				tablaUnInventario
						.put(ParametrosInventario.bal_bdd_inventario_numero,
								c.getString(c
										.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)));
				tablaUnInventario
						.put(ParametrosInventario.bal_bdd_inventario_descripcion,
								c.getString(c
										.getColumnIndex(ParametrosInventario.bal_bdd_inventario_descripcion)));
				tablaUnInventario.put(ParametrosInventario.bal_bdd_inventario_fechaInicio, c.getString(c.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaInicio)));
				tablaUnInventario
						.put(ParametrosInventario.bal_bdd_inventario_fechaFin,
								c.getString(c
										.getColumnIndex(ParametrosInventario.bal_bdd_inventario_fechaFin)));
				tablaUnInventario
						.put(ParametrosInventario.bal_bdd_inventario_estado,
								String.valueOf(c.getInt(c
										.getColumnIndex(ParametrosInventario.bal_bdd_inventario_estado))));
				/*
				 * 4 Agrega el hashmap en la entrada correspondiente a ese
				 * inventario
				 */
				tablaResultados
						.put(c.getInt(c
										.getColumnIndex(ParametrosInventario.bal_bdd_inventario_numero)),
								tablaUnInventario);
				c.moveToNext();
			}
		} else {
			throw new ExceptionBDD("INVENTARIO", 0);
		}
		// 5 Cerramos conexiones
		c.close();
		dtb.close();
		return tablaResultados;
	}

	public String proveedorAsignado(int id_inventario) throws ExceptionBDD {
		System.out.println("::: BaseDatos 6764 corroborar si tiene un proveedor asignado");
		// 1 Abrimos la base de datos en modo lectura
		SQLiteDatabase dtb = this.getReadableDatabase();
		Cursor c;
		Cursor cc;
		String codigo_devolver = "";
		String nombre_devolver = "";
		c= dtb.rawQuery("SELECT COMPRA_PROVE_COD FROM COMPRA_PROVEEDOR WHERE COMPRA_INV_COD="+id_inventario, null);
		// 3 Evaluamos el resultado y si es 1 devolvemos true
		if (c.moveToFirst() == true) {
			do {
				codigo_devolver= c.getString(0);
			} while(c.moveToNext());
			cc= dtb.rawQuery("SELECT PROV_DESC FROM PROVEEDORES WHERE PROV_COD="+codigo_devolver, null);

			if (cc.moveToFirst()) {
				do {
					nombre_devolver= cc.getString(0);
				} while(cc.moveToNext());
				return nombre_devolver;
			}else{
				nombre_devolver = "ELEGIR PROVEEDOR";
				return nombre_devolver;
			}
		} else {
			nombre_devolver = "ELEGIR PROVEEDOR";
			return nombre_devolver;
		}
	}

	public boolean verificaBaseNueva(){
		/*SE REALIZA UNA CONSULTA SOLAMENTE PARA VERIFICAR QUE SI LA BASE ES NUEVA, NO CREE UN INVENTARIO SIN
		 * ANTES QUE EL USUARIO USE EL INVENTARIO DINAMICO DE COMPRAS. VERIFICANDO QUE NO HAYA UN SOLO INVENTARIO
		 * EN LA PARTE DE COMPRAS PREGUNTANDO DESDE EL -3 PARA ABAJO*/
		SQLiteDatabase dtb = this.getReadableDatabase();
		Cursor cvalidar;
		cvalidar= dtb.rawQuery("SELECT * FROM INVENTARIOS WHERE INV_NUM <=-3", null);
		if (cvalidar.moveToFirst() == true) {
			return true;
		}else{
			return false;
		}
	}

	public void verComprasExistentes (Inventario inventario)throws ExceptionBDD{
		System.out.println("::: BaseDatos 6782");
		SQLiteDatabase dtb = this.getReadableDatabase();
		Cursor c;
		Cursor cc;
		int inventario_encontrado = 0;
		c= dtb.rawQuery("select * from INVENTARIOS  WHERE INV_LUG=3 ORDER BY INV_NUM ASC LIMIT 1", null);
		if (c.moveToFirst() == true) {
			do {
				inventario_encontrado= Integer.parseInt(c.getString(0));
			} while(c.moveToNext());
			inventario_encontrado = inventario_encontrado - 1;

			SQLiteDatabase dtbw = this.getWritableDatabase();
			// Si hemos abierto correctamente la base de datos
			if (dtbw != null) {
				String descripcionCompleta = inventario.getDescripcion().trim();
				int n = descripcionCompleta.trim().length();
				String obtenerDescripcion = descripcionCompleta.substring(0,(n-3));
				char car=descripcionCompleta.charAt(n-1);
				int variable_dep = 3;
				String str = "";
				// Esta seleccionado ventas, esto debe continuar sin los campos de deposito
				str = "INSERT INTO " + tabla_inventarios_nombre
						+ " VALUES(" + inventario_encontrado + "," + "'"
						+ obtenerDescripcion + "'," + "'"
						+ inventario.getFechaInicio() + "'," + "'"
						+ inventario.getFechaFin() + "',"
						+ inventario.getEstado() + "," + variable_dep+ "," +0
						+ ")";
				System.out.println("::: D.C. QUIERO VER VARIABLE_DEP "+variable_dep);
				dtb.execSQL(str);
				//}
			} else {
				throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
						"Imposible agregar el INVENTARIO nuevo a la Base De Datos");
			}
		} else {
			throw new ExceptionBDD(ExceptionBDD.ERROR_TIPO_INSERT,
					"Imposible agregar el INVENTARIO nuevo a la Base De Datos");
		}

	}
}