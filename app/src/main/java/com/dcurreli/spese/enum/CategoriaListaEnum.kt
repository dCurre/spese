package com.dcurreli.spese.enum

enum class CategoriaListaEnum(val value: String) {
    PERIODICA("Periodica"),
    EVENTO("Evento");


}

object CategoriaListaEnumUtils{
    fun getEnums(): ArrayList<String> {
        var enumList = ArrayList<String>()
        enumList.add(CategoriaListaEnum.PERIODICA.value)
        enumList.add(CategoriaListaEnum.EVENTO.value)
        return enumList
    }
}
