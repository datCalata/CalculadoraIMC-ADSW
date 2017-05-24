package es.upm.dit.adsw.ej6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    //Tags
    private static final String TAG = "MainActivity";


    //RequestCodes
    private final static int REQUESTFROMIMC = 1;
    private final static int REQUESTFROMIGCE = 2;

    private final static String NAMEIMC= "imc";
    private final static String NAMEIGCE= "igce";
    private static final String NAMECATEGORIA = "categoria";

    //Tags shared Preferences

    private final static String SHAREDPREFERENCESKEY = "userinfo";

    //Modders

    private final static float METERS = 100;

    //Varibales con datos
    private int anio;
    private int mes;
    private int dia;
    private int edad;
    private int sexo;
    private float imc;
    private float igce;
    private boolean calculado;
    private String categoria;

    //Campos del formulario

    private EditText campoAltura;
    private EditText campoPeso;
    private TextView resultadoIMC;
    private TextView resultadoIMCTexto;
    private TextView resultadoIGCE;
    private Button calculoIGC;
    private Button calculoIMC;
    private DatePicker picker;
    private RadioGroup grupoRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos campos segun inicia la actividad
        campoAltura = (EditText) findViewById(R.id.editAltura);
        campoPeso = (EditText) findViewById(R.id.editPeso);
        resultadoIMC = (TextView) findViewById(R.id.resultadoIMC);
        resultadoIMCTexto = (TextView) findViewById(R.id.resultadoIMCTexto);
        calculoIMC = (Button) findViewById(R.id.calculoIMC);
        resultadoIGCE = (TextView) findViewById(R.id.resultadoIGCE);
        calculoIGC = (Button) findViewById(R.id.calculoIGCE);
        grupoRadio = (RadioGroup) findViewById(R.id.RadioGroup);
        picker = (DatePicker) findViewById(R.id.thePicker);

        //Declaramos el calendario comun a todos los listeners
        final Calendar mcurrentDate = Calendar.getInstance();

        calculado = false;
        //Establecemos los listeners

        //Listeners de Actualizacion
        picker.init(mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //actualizamos variables
                actualizarFecha(mcurrentDate);
                if (calculado && comprobar()) {
                    imprimirIgce();
                }
            }
        });


        grupoRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                actualizarSexo();
                if (calculado && comprobar()) {
                    imprimirIgce();
                }
            }
        });

        //  Listeners Botnoes

        calculoIMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float peso = 0f;
                float altura = 0f;
                try {
                    peso = Float.valueOf(campoPeso.getText().toString());
                    altura = Float.valueOf(campoAltura.getText().toString()) / METERS;
                } catch (Exception e) {
                    Log.e(TAG, "Error " + e.toString());
                    Toast.makeText(getApplication(), R.string.errorDatos, Toast.LENGTH_SHORT).show();
                }
                //Calculamos los campos
                imc = calcularImc(peso,altura);
                categoria = getCategoria(imc);

                //Actualizamos los campos
                resultadoIMC.setText(String.format("%2.2f", imc));
                resultadoIMCTexto.setText(categoria);
                lanzarIntencion(imc,categoria,igce,REQUESTFROMIMC);

            }
        });


        calculoIGC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                actualizarFecha(mcurrentDate);
                actualizarSexo();
                //Comprobaciones
                if (comprobar()) {
                    imprimirIgce();
                    calculado = true;
                    lanzarIntencion(imc,categoria,igce,REQUESTFROMIGCE);
                }
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    private void lanzarIntencion(float imc,String categoria, float igce, final int REQUESTCODE) {
        Intent i = new Intent(this,Resultados.class);

        i.putExtra(NAMEIGCE,igce);
        i.putExtra(NAMEIMC,imc);
        i.putExtra(NAMECATEGORIA,categoria);

        startActivityForResult(i,REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Log.i(TAG,"Se ha vuelto correctamente de Resultados");
            return;
        }
        Log.e(TAG,"Error volviendo de Resultados (TimeOut)");
        Toast.makeText(getApplication(),R.string.errorTimeOut,Toast.LENGTH_SHORT).show();
    }

    private float calcularImc(float peso, float altura){
        return peso/(altura*altura);
    }

    //Actualiza el calculo del IGCE y cambia el valor del campo
    private void imprimirIgce() {
        igce = calcularIgce(imc, (float) edad, (float) sexo);
        String texto = String.format("%2.2f %%", igce);
        resultadoIGCE.setText(texto);
    }

    //Actualiza las variables de fecha, anio, mes, dia
    private void actualizarFecha(Calendar mCurrentDate) {
        anio = picker.getYear();
        mes = picker.getMonth();
        dia = picker.getDayOfMonth();
        edad = mCurrentDate.get(Calendar.YEAR) - anio;
    }

    //Obtiene la categoria segun el imc
    private String getCategoria(float imc) {
        String categoria = "";
        if (imc < 16.00) {
            categoria = getString(R.string.condicion1);
        } else if (imc >= 16.00 && imc <= 16.99) {
            categoria = getString(R.string.condicion2);
        } else if (imc >= 17.00 && imc <= 18.49) {
            categoria = getString(R.string.condicion3);
        } else if (imc >= 18.5 && imc <= 24.99) {
            categoria = getString(R.string.condicion4);
        } else if (imc >= 25.00 && imc <= 29.99) {
            categoria = getString(R.string.condicion5);
        } else if (imc >= 30.00 && imc <= 34.99) {
            categoria = getString(R.string.condicion6);
        } else if (imc >= 35.00 && imc <= 39.99) {
            categoria = getString(R.string.condicion7);
        } else if (imc >= 40.00) {
            categoria = getString(R.string.condicion8);
        }
        return categoria;
    }

    //Comprueba que las entradas sean validas
    private boolean comprobar() {
        Calendar mcurrentDate = Calendar.getInstance();
        boolean estado = true;
        String error = null;
        if (grupoRadio.getCheckedRadioButtonId() == -1) {
            error = getString(R.string.errorSexo);
            estado = false;
        }
        if (imc == 0) {
            error = getString(R.string.errorIMC);
            estado = false;
        }
        if(dia == 0){
            error = getString(R.string.errorFecha2);
            estado = false;
        }
        if (dia > mcurrentDate.get(Calendar.DAY_OF_MONTH) && mes >= mcurrentDate.get(Calendar.MONTH) && anio >= mcurrentDate.get(Calendar.YEAR)) {
            error = getString(R.string.errorFecha);
            estado = false;
        }
        if (error != null)
            Toast.makeText(getApplication(), error, Toast.LENGTH_SHORT).show();
        return estado;
    }

    //Calcula el sexo a partir de la seleccion
    private void actualizarSexo(){
        sexo =  Math.abs(grupoRadio.getCheckedRadioButtonId() - 1);
    }

    //Calcula el IGCE
    private float calcularIgce(float IMC, float edad, float sexo){
        if(edad < 16){
            //Caso especial de los niños
            return 1.51f * IMC - 0.7f * edad - 3.6f * sexo + 1.4f;
        }
        return 1.20f * IMC + 0.23f * edad - 10.8f * sexo - 5.4f;
    }


}