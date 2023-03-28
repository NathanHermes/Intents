package br.edu.ifsp.ads.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.ifsp.ads.intents.MainActivity.Constantes.EXTRA_PARAMS
import br.edu.ifsp.ads.intents.databinding.ActivityParametroBinding

class ParametroActivity : AppCompatActivity() {
    private val apb: ActivityParametroBinding by lazy {
        ActivityParametroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(apb.root)

        intent.getStringExtra(EXTRA_PARAMS)?.let { parametro ->
            apb.parametroEt.setText(parametro)
        }

        apb.enviarParametroBt.setOnClickListener {
            val parametro: String = apb.parametroEt.text.toString()
            val retornoIntent = Intent()
            retornoIntent.putExtra(EXTRA_PARAMS, parametro)
            setResult(RESULT_OK, retornoIntent)
            finish()
        }
    }
}