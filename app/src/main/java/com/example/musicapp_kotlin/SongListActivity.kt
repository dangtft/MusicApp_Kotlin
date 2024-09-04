package com.example.musicapp_kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp_kotlin.adapter.SongsListAdapter
import com.example.musicapp_kotlin.databinding.ActivitySongListBinding
import com.example.musicapp_kotlin.models.AlbumModel

class SongListActivity : AppCompatActivity() {

    companion object{
        lateinit var album : AlbumModel
    }

    lateinit var binding : ActivitySongListBinding
    lateinit var songsListAdapter : SongsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameTextView.text = album.name
        Glide.with(binding.coverImageView).load(album.coverUrl)
            .apply(
                RequestOptions().transform(RoundedCorners(32))
            )
            .into(binding.coverImageView)

        setupSongsListRecyclerView()
    }

    fun setupSongsListRecyclerView(){
        songsListAdapter = SongsListAdapter(album.songs)
        binding.songsListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.songsListRecyclerView.adapter = songsListAdapter
    }
}