package com.example.danid.lastapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View

import org.json.JSONArray
import org.json.JSONException

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.HashMap

import kotlinx.android.synthetic.main.activity_lista_citas.*

class ListaCitasActivity : AppCompatActivity(){
    internal val ADD_CITA = 1
    internal val CHANGE_SETTINGS = 2
    var idAgente = 0
    internal var lista = ArrayList<Cita>()

    internal var jsonArray: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_citas)

        idAgente = (intent.extras.get("IDAGENTE") as String).toInt()

        var conexionDB = ConexionWeb(applicationContext)

        setSupportActionBar(toolbarMainActivity as Toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lista_citas_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val drawer = R.id.mainDrawer as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        lista = ArrayList<Cita>()
        updateRecycler(lista);

        CitasAsynTask().execute("encontrarCitas", idAgente.toString())

    }

    fun reload(item: MenuItem) {
        lista = ArrayList<Cita>()
        CitasAsynTask().execute("encontrarCitas", idAgente.toString())
    }

    fun logout(item: MenuItem) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun addCita(item: MenuItem) {
        var intent = Intent(this, AddCita::class.java)
        intent.putExtra("IDAGENTE", idAgente)
        startActivity(intent)
        println("Add")
    }

    fun openSettings(item: MenuItem) {
        //startActivityForResult(Intent(this, Preferencias::class.java), CHANGE_SETTINGS)
        println("Settings")
    }

    inner class CitasAsynTask : AsyncTask<String, String, JSONArray>() {
        override fun onPreExecute() {
            loadingCitas.visibility = View.VISIBLE
            super.onPreExecute()
        }

        override fun doInBackground(vararg parametros: String): JSONArray? {
            val parametrosPost = HashMap<String, String?>()
            if (parametros[0].length > 0 && !parametros[0].isEmpty()) {
                parametrosPost.put("metodo", parametros[0])
                parametrosPost.put("IDAGENTE", parametros[1])
            } else
                return null

            jsonArray = ConexionWeb(applicationContext).sendRequest(parametrosPost)

            return if (jsonArray != null) jsonArray else null

        }

        override fun onPostExecute(jsonArray: JSONArray?) {
            if (jsonArray != null) {
                try {
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val cita = Cita()

                        val idcita = jsonObject.getString("IDCITA")
                        val empresa = jsonObject.getString("EMPRESA")
                        val cliente = jsonObject.getString("CLIENTE")
                        val telefono = jsonObject.getString("TELEFONO")
                        val email = jsonObject.getString("EMAIL")
                        val direccion = jsonObject.getString("DIRECCION")
                        val fecha = jsonObject.getString("FECHACITA")

                        cita.idCita = Integer.parseInt(idcita)
                        cita.empresa = empresa
                        cita.cliente = cliente
                        cita.telefono = telefono
                        cita.email = email
                        cita.direccion = direccion
                        cita.fecha = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fecha)

                        lista.add(cita)
                    }
                    updateRecycler(lista)

                    loadingCitas.visibility = View.INVISIBLE
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }

            super.onPostExecute(jsonArray)
        }
    }

    fun updateRecycler(listaRecycler: ArrayList<Cita>) {
        recyclerCitas.layoutManager = LinearLayoutManager(applicationContext)
        val adaptadorCitas = AdaptadorCitas(listaRecycler)
        //adaptadorCitas.setRecyclerClick(this@MainActivity)
        recyclerCitas.adapter = adaptadorCitas
    }
}
