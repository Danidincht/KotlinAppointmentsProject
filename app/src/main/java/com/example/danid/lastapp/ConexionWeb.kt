package com.example.danid.lastapp

import android.content.Context
import android.preference.PreferenceManager

import org.json.JSONArray
import org.json.JSONException

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.HashMap

/**
 * Created by danid on 13/01/2018.
 */

class ConexionWeb(internal var context: Context) {
    private var connection: HttpURLConnection? = null
    internal var ip = "192.168.1.10"
    internal var serverIP: String? = null
    private val link = "http://192.168.1.198:880/AgentesAyala/index.php"

    fun sendRequest(values: HashMap<String, String?>): JSONArray? {

        var jsonArray: JSONArray? = null
        try {
            val url = URL(link)
            println("HOST: " + link)

            connection = url.openConnection() as HttpURLConnection
            connection!!.readTimeout = CONNECTION_TIMEOUT
            connection!!.connectTimeout = CONNECTION_TIMEOUT
            connection!!.requestMethod = "POST"
            connection!!.doInput = true
            connection!!.doOutput = true
            connection!!.connect()

            if (values != null) {
                val outputStream = connection!!.outputStream
                val outputStreamWriter = OutputStreamWriter(outputStream, "utf-8")
                val writer = BufferedWriter(outputStreamWriter)
                writer.write(getPostData(values))
                writer.flush()
                writer.close()
                outputStream.close()
            }

            if (connection!!.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection!!.inputStream
                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)

                var result = ""
                var line: String? = null
                val stringBuilder = StringBuilder()

                line = bufferedReader.readLine()
                while (line != null) {
                    stringBuilder.append(line!! + "\n")
                    line = bufferedReader.readLine()
                }

                inputStream.close()
                result = stringBuilder.toString()
                println("Result: " + result)

                try {
                    jsonArray = JSONArray(result)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonArray
    }

    fun getPostData(values: HashMap<String, String?>): String {
        val builder = StringBuilder()
        var first = true
        for ((key, value) in values) {
            if (first)
                first = false
            else
                builder.append("&")

            try {
                builder.append(URLEncoder.encode(key, "UTF-8"))
                builder.append("=")
                builder.append(URLEncoder.encode(value, "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
            }

        }
        return builder.toString()
    }

    companion object {
        val CONNECTION_TIMEOUT = 15 * 1000
    }
}
