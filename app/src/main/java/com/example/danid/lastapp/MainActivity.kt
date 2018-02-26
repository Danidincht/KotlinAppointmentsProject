package com.example.danid.lastapp

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        entrarBt.setOnClickListener()
        {
            var username = userTf.text.toString()
            var password = passTf.text.toString()

            println(username)
            println(password)
            AsyncLogin(this).execute("login",username,password);
        }
    }

    inner class AsyncLogin(var context: Context): AsyncTask<String, String, JSONArray>()
    {
        override fun doInBackground(vararg parametros: String?): JSONArray? {
            var conexion = ConexionWeb(context);

            var parametrosMap = java.util.HashMap<String, String?>()

            parametrosMap.put("metodo", parametros[0]);
            parametrosMap.put("user", parametros[1]);
            parametrosMap.put("pass", parametros[2]);

            var jsonArray = conexion.sendRequest(parametrosMap);
            return jsonArray
        }

        override fun onPostExecute(result: JSONArray?) {
            super.onPostExecute(result)
            println("Post Executing")
            if(result != null)
            {
                var jsonObj = result.getJSONObject(0)
                try {
                    var id = jsonObj.getString("IDAGENTE")
                    println("Getting id")
                    if (id != null)
                    {
                        var intent = Intent(context, ListaCitasActivity::class.java)
                        intent.putExtra("IDAGENTE", id)
                        startActivity(intent)
                        finish()
                    }
                }
                catch(e: Exception)
                {
                    wrongLoginTxt.visibility = View.VISIBLE
                    e.printStackTrace()
                }
            }
            else
                println("El resultado es nulo")
        }
    }
}
