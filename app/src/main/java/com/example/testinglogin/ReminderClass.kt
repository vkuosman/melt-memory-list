package com.example.testinglogin

class ReminderClass {

    var name: String = ""
    var mess: String = ""
    var id: String = ""
    // Other attributes are currently not used.
    var creid: Int? = 0
    var locx: String? = ""
    var locy: String? = ""
    var remtim: String? = ""
    var cretim: String? = ""
    var remsee: String? = ""

    constructor(nameIn: String, messIn: String, idIn: String, creidIn: Int? = null, locxIn: String? = null, locyIn: String? = null, remtimIn: String? = null, cretimIn: String? = null, remseeIn: String? = null) {
        this.name = nameIn
        this.mess = messIn
        this.id = idIn
        this.creid = creidIn
        this.locx = locxIn
        this.locy = locyIn
        this.remtim = remtimIn
        this.cretim = cretimIn
        this.remsee = remseeIn
    }

    constructor(){}
}
