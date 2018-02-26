package com.example.danid.lastapp


import java.io.Serializable
import java.util.Date

/**
 * Created by danid on 19/01/2018.
 */

class Cita : Serializable {
    var idCita: Int = 0
    var empresa: String? = null
    var cliente: String? = null
    var telefono: String? = null
    var email: String? = null
    var direccion: String? = null
    var fecha: Date? = null

    constructor() {
        this.idCita = -1
        this.empresa = ""
        this.cliente = ""
        this.telefono = ""
        this.email = ""
        this.direccion = ""
        this.fecha = Date()
    }

    constructor(idCita: Int, empresa: String, cliente: String, telefono: String, email: String, direccion: String, fecha: Date) {
        this.idCita = idCita
        this.empresa = empresa
        this.cliente = cliente
        this.telefono = telefono
        this.email = email
        this.direccion = direccion
        this.fecha = fecha
    }
}
