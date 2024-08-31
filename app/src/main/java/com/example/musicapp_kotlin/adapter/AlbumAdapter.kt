package com.example.musicapp_kotlin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp_kotlin.SongListActivity
import com.example.musicapp_kotlin.databinding.AlbumItemRecyclerRowBinding
import com.example.musicapp_kotlin.models.AlbumModel

class AlbumAdapter (private val albumList:List<AlbumModel>) :
    RecyclerView.Adapter<AlbumAdapter.MyViewHolder>()
{
    class MyViewHolder(private val binding: AlbumItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun binData(album : AlbumModel){
                binding.nameTextView.text = album.name
                Glide.with(binding.coverImageView).load(album.coverUrl)
                    .apply(
                        RequestOptions().transform(RoundedCorners(32))
                    )
                    .into(binding.coverImageView)

                val context = binding.root.context
                binding.root.setOnClickListener{
                    SongListActivity.album = album
                    context.startActivity(Intent(context,SongListActivity::class.java))
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AlbumItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return albumList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binData(albumList[position])
    }
}