package com.example.danid.lastapp

import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_cita.*
import kotlinx.android.synthetic.main.add_client_dialog.view.*
import kotlinx.android.synthetic.main.date_picker_dialog.view.*
import kotlinx.android.synthetic.main.time_picker_dialog.view.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap

class AddCita : AppCompatActivity() {
    internal var cita: Cita? = null
    internal var fechaCita = Date()

    internal var newClientName: String = ""
    internal var newClientTelephone: String = ""
    internal var newClientMail: String = ""

    internal var jsonArray: JSONArray? = null

    internal var clients = ArrayList<String>()
    internal var companies = ArrayList<String>()
    internal var empresaIdx = 0
    internal var clienteIdx = 0

    var idAgente = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cita)

        val toolbar = toolbarAddCita as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val b = intent.extras
        if (b != null) {
            idAgente = b.get("IDAGENTE") as Int
        }

        fechaAddCitaBt.text = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())

        AddCitaAsyncTask().execute("getClientesAndCompanies")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_cita_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
                setResult(0)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun selectDate(view: View) {
        val builder = AlertDialog.Builder(this)

        val picker = View.inflate(this, R.layout.date_picker_dialog, null)
        val datePicker = picker.datePickerDialog

        builder.setView(picker)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            val builder1 = AlertDialog.Builder(this@AddCita)
            val time = View.inflate(this@AddCita, R.layout.time_picker_dialog, null)
            val timePicker = time.timePickerDialog

            builder1.setView(time)
            builder1.setPositiveButton("OK") { dialogInterface, i ->
                try {
                    fechaCita = SimpleDateFormat("dd/MM/yyyy HH:mm").parse(datePicker.dayOfMonth.toString() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute())

                    fechaAddCitaBt.text = SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaCita)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            builder1.create().show()
        }

        builder.create().show()
    }


    fun saveCita(view: View) {
        if (cita != null)
            AddCitaAsyncTask().execute("crearCita", cita!!.idCita.toString(), idAgente.toString(), companyAddCitaSp.selectedItem.toString(), clientAddCitaSp.selectedItem.toString(), newClientTelephone, newClientMail, "Direccion", SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaCita))
        else
            AddCitaAsyncTask().execute("crearCita", "-1", idAgente.toString(), companyAddCitaSp.selectedItem.toString(), clientAddCitaSp.selectedItem.toString(), newClientTelephone, newClientMail, "Direccion", SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaCita))
    }


    internal inner class AddCitaAsyncTask : AsyncTask<String, String, JSONArray>() {
        var metodo: String = ""

        override fun doInBackground(vararg parametros: String): JSONArray? {
            val conexionDB = ConexionWeb(this@AddCita)

            val parametrosPost = HashMap<String, String?>()
            if (parametros[0].length > 0 && !parametros[0].isEmpty()) {
                metodo = parametros[0]
                parametrosPost.put("metodo", parametros[0])
                if (parametros[0] == "crearCita") {
                    parametrosPost.put("IDCITA", parametros[1])
                    parametrosPost.put("IDAGENTE", parametros[2])
                    parametrosPost.put("EMPRESA", parametros[3])
                    parametrosPost.put("CLIENTE", parametros[4])
                    parametrosPost.put("TELEFONO", if (parametros[5] != null) parametros[5] else "")
                    parametrosPost.put("MAIL", if (parametros[6] != null) parametros[6] else "")
                    parametrosPost.put("ADDRESS", if (parametros[7] != null) parametros[7] else "")
                    parametrosPost.put("DATE", parametros[8])
                }
            } else
                return null

            jsonArray = conexionDB.sendRequest(parametrosPost)

            return if (jsonArray != null) jsonArray else null

        }

        override fun onPostExecute(jsonArray: JSONArray?) {
            try {
                if (jsonArray != null) {
                    if (metodo == "crearCita") {
                        if (java.lang.Boolean.valueOf(jsonArray.get(0).toString())!!) {
                            Toast.makeText(this@AddCita, "Cita creada", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else if (metodo == "getClientesAndCompanies") {


                        companies = ArrayList()
                        companies.add("Seleccionar empresa")
                        companies.add("Crear empresa")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.get(i) as JSONObject
                            val empresa = jsonObject.getString("EMPRESA")
                            if (!companies.contains(empresa)) {
                                companies.add(empresa)

                                //Si estamos editando una cita
                                if (cita != null)
                                    if (jsonObject.getString("EMPRESA") == cita!!.empresa) {
                                        empresaIdx = i + 2
                                        println("Coge empresa: " + empresaIdx)
                                    }
                            }

                        }
                        companyAddCitaSp.adapter = ArrayAdapter(this@AddCita, android.R.layout.simple_dropdown_item_1line, companies)

                        companyAddCitaSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                                if (i == 1)
                                //Add client
                                {
                                    val builder = AlertDialog.Builder(this@AddCita)

                                    builder.setTitle("Crear empresa")

                                    val newCompany = EditText(this@AddCita)
                                    builder.setView(newCompany)

                                    builder.setPositiveButton("OK") { dialogInterface, i ->
                                        companies.add(newCompany.text.toString())
                                        companyAddCitaSp.adapter = ArrayAdapter(this@AddCita, android.R.layout.simple_dropdown_item_1line, companies)
                                        companyAddCitaSp.setSelection(companies.size - 1)
                                    }

                                    builder.create().show()
                                }


                                clients = ArrayList()
                                clients.add("Seleccionar cliente")
                                clients.add("Crear cliente")

                                try {
                                    for (j in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.get(j) as JSONObject
                                        if (jsonObject.getString("EMPRESA") == companies[i]) {
                                            clients.add(jsonObject.getString("NOMBRE"))

                                            //Si estamos editando una cita
                                            if (cita != null)
                                                if (jsonObject.getString("NOMBRE") == cita!!.cliente) {
                                                    clienteIdx = j + 2
                                                    println("Coge cleinte: " + clienteIdx)
                                                }
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                clientAddCitaSp.adapter = ArrayAdapter(this@AddCita, android.R.layout.simple_dropdown_item_1line, clients)
                                clientAddCitaSp.setSelection(clienteIdx)

                                clientAddCitaSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                                        if (i == 1) {
                                            val builder = AlertDialog.Builder(this@AddCita)

                                            builder.setTitle("Crear cliente")

                                            val newClient = View.inflate(this@AddCita, R.layout.add_client_dialog, null)
                                            builder.setView(newClient)

                                            builder.setPositiveButton("OK") { dialogInterface, i ->
                                                newClientName = (newClient.newClientNameTf as EditText).text.toString()
                                                newClientTelephone = (newClient.newClientTelephoneTf as EditText).text.toString()
                                                newClientMail = (newClient.newClientMailTf as EditText).text.toString()

                                                clients.add(newClientName)
                                                clientAddCitaSp.adapter = ArrayAdapter(this@AddCita, android.R.layout.simple_dropdown_item_1line, clients)
                                                clientAddCitaSp.setSelection(clients.size - 1)
                                            }

                                            builder.create().show()
                                        }
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>) {}
                                }
                            }

                            override fun onNothingSelected(adapterView: AdapterView<*>) {}
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }


            //Si editamos
            if (cita != null) {
                companyAddCitaSp.setSelection(empresaIdx)
                fechaAddCitaBt.text = SimpleDateFormat().format(cita!!.fecha)

                println("Cliente: " + cita!!.cliente!!)
                println("Empresa: " + cita!!.empresa!!)
            }


            super.onPostExecute(jsonArray)
        }
    }
}
