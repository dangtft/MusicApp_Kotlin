package com.example.musicapp_kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp_kotlin.R
import com.example.musicapp_kotlin.models.SongModel

class SongAdapter(var songs: List<SongModel>, private val onItemClick: (SongModel) -> Unit) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.song_title)
        val songArtist: TextView = itemView.findViewById(R.id.song_artist)

        init {
            itemView.setOnClickListener {
                val song = songs[adapterPosition]
                onItemClick(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist
    }

    override fun getItemCount(): Int = songs.size
}
