package com.example.musicapp_kotlin.models

data class SongModel(
    val id : String,
    val title : String,
    val artist :String,
    val url : String,
    val coverUrl : String,

){
    constructor():this("","","","","")
}
