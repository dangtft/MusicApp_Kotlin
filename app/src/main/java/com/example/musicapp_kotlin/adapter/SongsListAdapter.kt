package com.example.musicapp_kotlin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp_kotlin.MyExoplayer
import com.example.musicapp_kotlin.PlayerActivity
import com.example.musicapp_kotlin.databinding.SongListItemRecyclerRowBinding
import com.example.musicapp_kotlin.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore

class SongsListAdapter(private val songIdList : List<String>):
    RecyclerView.Adapter<SongsListAdapter.MyViewHolder>(){

    class MyViewHolder(private val binding :  SongListItemRecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

        fun binData(songId : String){

            FirebaseFirestore.getInstance().collection("Songs")
                .document(songId).get()
                .addOnSuccessListener {
                    val songs = it.toObject(SongModel::class.java)
                    songs?.apply {
                        binding.songTitleTextView.text = title
                        binding.songArtistTextView.text = artist
                        Glide.with(binding.songCoverImageView).load(coverUrl)
                            .apply(
                                RequestOptions().transform(RoundedCorners(32))
                            )
                            .into(binding.songCoverImageView)
                        binding.root.setOnClickListener{
                            MyExoplayer.startPlaying(binding.root.context,songs)
                            it.context.startActivity(Intent(it.context, PlayerActivity::class.java))
                        }
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SongListItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songIdList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binData(songIdList[position])
    }
}