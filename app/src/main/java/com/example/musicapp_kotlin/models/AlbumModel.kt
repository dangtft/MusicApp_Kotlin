package com.example.musicapp_kotlin.models


data class AlbumModel(
    val name : String,
    val coverUrl : String,
    var songs : List<String>
){
    constructor(): this("","", listOf())
}
