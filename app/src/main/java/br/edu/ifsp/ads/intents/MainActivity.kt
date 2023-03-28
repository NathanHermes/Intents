package br.edu.ifsp.ads.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.ads.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object Constantes {
        const val EXTRA_PARAMS = "EXTRA_PARAMS"
    }

    private lateinit var parl: ActivityResultLauncher<Intent>
    private lateinit var callPermissionArl: ActivityResultLauncher<String>
    private lateinit var getImageArl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        parl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == RESULT_OK) {
                val response: String = result.data?.getStringExtra(EXTRA_PARAMS) ?: ""
                amb.paramsTv.text = response
            }
        }

        amb.entrarParametroBt.setOnClickListener {
            val paramIntent = Intent("PALMEIRAS_CAMPEAO_PAULISTA_ACTION")
            paramIntent.putExtra(EXTRA_PARAMS, amb.paramsTv.text.toString())
            parl.launch(paramIntent)
        }

        callPermissionArl = registerForActivityResult(ActivityResultContracts.RequestPermission()) { grantedPermission ->
            if (grantedPermission) callNumber(true)
            else {
                Toast.makeText(this, "Permissão necessária para continuar", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        getImageArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    amb.paramsTv.text = it.toString()
                    val toViewImage = Intent(ACTION_VIEW, it)
                    startActivity(toViewImage)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewMi -> {
                val url: Uri = Uri.parse(amb.paramsTv.text.toString())
                val browserIntent = Intent(ACTION_VIEW, url)
                startActivity(browserIntent)
                true
            }
            R.id.callMi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) callNumber(true)
                    else callPermissionArl.launch(CALL_PHONE)
                } else callNumber(true)
                true
            }
            R.id.dialMi -> {
                callNumber(false)
                true
            }
            R.id.pickMi -> {
                val getImageIntent = Intent(ACTION_PICK)
                val imageDirectory = Environment
                    .getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
                    .path

                getImageIntent.setDataAndType(Uri.parse(imageDirectory), "image/*")
                getImageArl.launch(getImageIntent)
                true
            }
            R.id.chooserMi -> {
                val url: Uri = Uri.parse(amb.paramsTv.text.toString())
                val browserIntent = Intent(ACTION_VIEW, url)

                val selectAppIntent = Intent(ACTION_CHOOSER)
                selectAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador preferido")
                selectAppIntent.putExtra(EXTRA_INTENT, browserIntent)

                startActivity(selectAppIntent)
                true
            }
            else -> false
        }
    }

    private fun callNumber (call: Boolean) {
        val numberUri: Uri = Uri.parse("tel: ${amb.paramsTv.text}")
        val callIntent = Intent(if (call) ACTION_CALL else ACTION_DIAL)
        callIntent.data = numberUri
        startActivity(callIntent)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PARAMETRO_ACTIVITY_REQUEST_CODE && requestCode == RESULT_OK) {
            val retorno: String = data?.getStringExtra(PARAMETRO_EXTRA)?: ""
            amb.parametroTv.text = retorno
        }
    }*/
}