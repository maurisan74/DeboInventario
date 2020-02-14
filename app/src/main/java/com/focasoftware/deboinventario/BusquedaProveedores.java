package com.focasoftware.deboinventario;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Usuario on 22/04/2016.
 */
public class BusquedaProveedores extends Activity {    private ListView list;
    private BaseDatos bdd;
    @NonNull
    private Context ctxt = this;
    private ImageView botonSalir;
    @NonNull
    BaseDatos bd = new BaseDatos(ctxt);
    @Nullable
    Bundle b;
    private TableLayout tablaPrincipal;
    /*** Datos del contexto de la actividad*/
    @NonNull
    GestorLogEventos log = new GestorLogEventos();
    private Intent intentPadre;
    private Button cancelar_busqueda;
    @Nullable
    private String proveedor_id;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_busqueda_proveedores);
        b= getIntent().getExtras();
        intentPadre = getIntent();
        Bundle bundle = getIntent().getExtras();
        ArrayList<String[]> listado = (ArrayList<String[]>) getIntent().getSerializableExtra("lista");
        proveedor_id = (String) getIntent().getSerializableExtra("proveedor");
        String[] mStringArray = new String[listado.size()];
        mStringArray = listado.toArray(mStringArray);
        if(listado.size()!=0){
            list = (ListView)findViewById(R.id.ListProveedores);
            list = (ListView)findViewById(R.id.ListProveedores);
            botonSalir = (ImageView) findViewById(R.id.CMB_boton_salir);
            cancelar_busqueda = (Button) findViewById(R.id.ID_CANCELAR_PROVE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStringArray);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                            Toast.LENGTH_SHORT).show();
                    String valor = (String) parent.getAdapter().getItem(position);
                    ArrayList<String> lista_resultados = new ArrayList<String>();
                    bdd = new BaseDatos(ctxt);
                    try {
                        lista_resultados = bdd.cargarProveedor(valor, proveedor_id);
                    } catch (ExceptionBDD exceptionBDD) {
                        exceptionBDD.printStackTrace();
                    }
                    Intent intentCompras = new Intent(ctxt,
                            ComprasMainBoard.class);
                    startActivity(intentCompras);
                }
            });
            botonSalir.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    /*Intent intentMainCompras = new Intent(ctxt,
                            ComprasMainBoard.class);
                    startActivity(intentMainCompras);*/
                    Intent intentProveedorBusqueda = new Intent(ctxt,
                            ProveedorBusqueda.class);
                    startActivity(intentProveedorBusqueda);
                }
            });
            cancelar_busqueda.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intentProveedorBusqueda = new Intent(ctxt,
                            ProveedorBusqueda.class);
                    startActivity(intentProveedorBusqueda);
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"No se encontro coincidencia en la busqueda o los resultados devueltos son demasiados.",
                    Toast.LENGTH_SHORT).show();
            Intent intentBusqueda = new Intent(ctxt,
                   ProveedorBusqueda.class);
            startActivity(intentBusqueda);
        }
    }
}