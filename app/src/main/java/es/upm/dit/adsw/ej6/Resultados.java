package es.upm.dit.adsw.ej6;

import android.content.Intent;
import android.nfc.Tag;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Resultados extends AppCompatActivity {


    private static final String TAG = "ResultadosActivity";

    private final static String NAMEIMC= "imc";
    private final static String NAMEIGCE= "igce";
    private static final String NAMECATEGORIA = "categoria";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        Bundle mainActivityData = getIntent().getExtras();
        if(mainActivityData == null){
            return;
        }

        float imc = mainActivityData.getFloat(NAMEIMC);
        float igce = mainActivityData.getFloat(NAMEIGCE);
        String categoria = mainActivityData.getString(NAMECATEGORIA);

        TextView imcDisplay = (TextView)findViewById(R.id.IMCDisplay);
        TextView igceDisplay = (TextView)findViewById(R.id.IGCEDisplay);
        TextView categoriaDisplay = (TextView)findViewById(R.id.CategoriaDisplay);
        LinearLayout layout = (LinearLayout)findViewById(R.id.resultadosLayout);

        imcDisplay.setText(String.format("%2.2f", imc));
        igceDisplay.setText(String.format("%2.2f", igce));
        categoriaDisplay.setText(categoria);

        layout.setOnClickListener(new LinearLayout.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_OK,i);
                finish();
            }
        });

        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i(TAG,""+millisUntilFinished);
            }

            public void onFinish() {
                Intent i = new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }
        }.start();

    }

}
