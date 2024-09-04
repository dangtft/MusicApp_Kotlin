package com.example.musicapp_kotlin

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp_kotlin.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore

object MyExoplayer {

    private var exoPlayer : ExoPlayer? = null
    private var currentSongs:SongModel? =null

    fun getCurrentSong():SongModel?{
        return currentSongs
    }

    fun getInstance():ExoPlayer?{
        return exoPlayer
    }

    fun startPlaying(context: Context,song : SongModel){
        if(exoPlayer == null)
            exoPlayer = ExoPlayer.Builder(context).build()

        if(currentSongs != song){

            currentSongs = song
            updateCount()
            currentSongs?.url?.apply {
                val mediaItem = MediaItem.fromUri(this)
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
        }
    }

    fun updateCount(){
        currentSongs?.id?.let {id ->
            FirebaseFirestore.getInstance().collection("Songs")
                .document(id)
                .get().addOnSuccessListener {
                    var latesCount = it.getLong("count")
                    if(latesCount == null){
                        latesCount = 1L
                    }else{
                        latesCount = latesCount + 1
                    }
                    FirebaseFirestore.getInstance().collection("Songs")
                        .document(id)
                        .update(mapOf("count" to latesCount))
                }
        }
    }

}