package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProveedorBusqueda extends Activity {
	@NonNull
    private Context ctxt = this;
	private boolean borrar;
	private ImageView botonSalir;
	private BaseDatos bdd;
	@Nullable
    Bundle b;
	@NonNull
    BaseDatos bd = new BaseDatos(ctxt);
	private Intent intentPadre;
	private EditText nomProveedor;
	@Nullable
    private String proveedor_id;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_busqueda_proveedor);

		b= getIntent().getExtras();
		intentPadre = getIntent();
		Bundle bundle = getIntent().getExtras();
		proveedor_id = (String) getIntent().getSerializableExtra("proveedor");
		nomProveedor = (EditText)findViewById(R.id.editTextProv);
		botonSalir = (ImageView) findViewById(R.id.CMB_boton_salir);
		botonSalir.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intentMainCompras = new Intent(ctxt,
						ComprasMainBoard.class);
				startActivity(intentMainCompras);
			}
		});
	}

	public void buscarProveedor(View view) throws ExceptionBDD {
		String valor = "";
		valor = nomProveedor.getText().toString();
		if(valor.trim().length()==0){
			Toast.makeText(getApplicationContext(), "Debe ingresar una descripcion ", Toast.LENGTH_SHORT).show();
		}else{
			ArrayList<String> lista_resultados = new ArrayList<String>();
			bdd = new BaseDatos(ctxt);
			lista_resultados = bdd.buscarEnProveedores(valor);
			ArrayList<String> listado_prov = new ArrayList<String>();
			Intent i = new Intent(ProveedorBusqueda.this, BusquedaProveedores.class);
			i.putExtra("lista", lista_resultados);
			i.putExtra("proveedor", proveedor_id);
			startActivity(i);
		}
	}

	public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent intentRespondido) {
		try {
			super.onActivityResult(requestCode, resultCode, intentRespondido);
			Bundle bundle = null;
			if (intentRespondido != null) {
				bundle = intentRespondido.getExtras();
			}
		} catch (Exception e) {
		}
	}
}