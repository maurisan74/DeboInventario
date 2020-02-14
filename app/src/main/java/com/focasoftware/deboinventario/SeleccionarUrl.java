package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SeleccionarUrl extends Activity {

	private Button cerrar;
	private Button agregar;
	private Button actualizar;
	private EditText local;
	private EditText urlWebservice;
	@NonNull
    private Context ctx = this;
	private Spinner spinnerLocales;
	private int idlocalActualizar;
	private ArrayAdapter<CharSequence> adapterLocales;
	private boolean bloquearspinner = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seleccionar_url);

		// CREACION DE LA TABLA LOCAL
		BaseDatos data = new BaseDatos(this);
		data.crearTablaLocales();

		try {
			System.out.println("::: SeleccionarURL 49 Busca para mostrar las opciones");
			ArrayList<Local> LocalesIniciales = data.ObtenerTodosLocales();
			if (LocalesIniciales.size() == 0) {
				data.guardarLocal(new Local("POS Retail",
						"http://192.168.0.0/webservice_balanza/deboinventario/webservice.php"));
				data.guardarLocal(new Local("BO Estaciones",
						"http://192.168.0.0/wsestaciones/webservice.php"));
			}
		} catch (Exception e) {
			Toast.makeText(ctx, "Error al obtener locales: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.local = (EditText) findViewById(R.id.nombre_local);
		this.urlWebservice = (EditText) findViewById(R.id.urlWebservice);
		this.spinnerLocales = (Spinner) findViewById(R.id.spinnerPreferencias);

		try {
			ArrayList<Local> Locales = data.ObtenerTodosLocales();
			String[] ParaAdapter = new String[Locales.size()];

			int cont = 0;
			for (Local local : Locales) {
				ParaAdapter[cont] = local.getNombre();
				cont++;
			}

			this.adapterLocales = new ArrayAdapter<CharSequence>(this,
					android.R.layout.simple_spinner_item, ParaAdapter);
			this.adapterLocales
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			this.spinnerLocales.setAdapter(this.adapterLocales);

			this.spinnerLocales
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						public void onItemSelected(@NonNull AdapterView<?> parentView,
                                                   View selectedItemView, int position, long id) {
							if (bloquearspinner == true) {
								bloquearspinner = false;
							} else {

								BaseDatos data = new BaseDatos(ctx);
								try {
									actualizar.setVisibility(View.VISIBLE);
									Local localTemp = data
											.ObtenerLocal_x_Id((position + 1));
									local.setText(localTemp.getNombre());
									idlocalActualizar = localTemp.getIdLocal();
									urlWebservice.setText(localTemp
											.getDescripcion());
								} catch (ExceptionBDD e) {
									Toast.makeText(ctx,
											"no se pudo obtener el local",
											Toast.LENGTH_LONG).show();
								}
								Toast.makeText(
										parentView.getContext(),
										"Has seleccionado "
												+ parentView.getItemAtPosition(
														position).toString(),
										Toast.LENGTH_LONG).show();
							}
						}

						public void onNothingSelected(AdapterView<?> parentView) {
						}
					});

		} catch (ExceptionBDD e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.actualizar = (Button) findViewById(R.id.actualizar_local);
		this.actualizar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String nombreLocal = local.getText().toString();
				String urWebserviceLocal = urlWebservice.getText().toString();

				ArrayList<String> error = new ArrayList<String>();
				if (nombreLocal.length() == 0) {
					local.setError("Ingrese el nombre del local");
					error.add("1");
				}
				if (urWebserviceLocal.length() == 0) {
					urlWebservice
							.setError("Ingrese la direccion del webservice");
					error.add("1");
				}

				if (error.size() > 0) {
					Toast.makeText(ctx, "Revise los campos", Toast.LENGTH_LONG)
							.show();

				} else {
					Local local = new Local(nombreLocal, urWebserviceLocal,
							idlocalActualizar);

					BaseDatos bd = new BaseDatos(ctx);
					bd.actualizarLocal(local);
					Toast.makeText(ctx, "Local actualizado", Toast.LENGTH_LONG)
							.show();
					setResult(RESULT_OK);
					finish();
				}
			}
		});

		this.cerrar = (Button) findViewById(R.id.cerrar);
		this.cerrar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});

		this.agregar = (Button) findViewById(R.id.guardar_local);
		this.agregar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String nombreLocal = local.getText().toString();
				String urWebserviceLocal = urlWebservice.getText().toString();

				ArrayList<String> error = new ArrayList<String>();
				if (nombreLocal.length() == 0) {
					local.setError("Ingrese el nombre del local");
					error.add("1");
				}
				if (urWebserviceLocal.length() == 0) {
					urlWebservice.setError("Ingrese la url del webservice");
					error.add("1");
				}
				if (error.size() > 0) {
					Toast.makeText(ctx, "Revise los campos", Toast.LENGTH_LONG)
							.show();
				} else {
					Local local = new Local(nombreLocal, urWebserviceLocal);

					BaseDatos bdd = new BaseDatos(ctx);
					bdd.guardarLocal(local);
					Toast.makeText(ctx, "Local agregado", Toast.LENGTH_LONG)
							.show();
					setResult(RESULT_OK);
					finish();
				}
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

}
