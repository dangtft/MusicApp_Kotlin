package com.example.musicapp_kotlin

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp_kotlin.adapter.AlbumAdapter
import com.example.musicapp_kotlin.adapter.SectionSongListAdapter
import com.example.musicapp_kotlin.databinding.ActivityMainBinding
import com.example.musicapp_kotlin.models.AlbumModel
import com.example.musicapp_kotlin.models.SongModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var albumAdapter : AlbumAdapter

    private lateinit var scrollView: ScrollView
    private lateinit var playerView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAlbum()
        getSection("section_1",binding.section1MainLayout,binding.section1Title,binding.section1RecycleView)
        getSection("section_2",binding.section2MainLayout,binding.section2Title,binding.section2RecycleView)
        getSection("us-uk",binding.section2MainLayout,binding.section2Title,binding.section2RecycleView)
        getSection("taylor-swift",binding.section2MainLayout,binding.section2Title,binding.section2RecycleView)
        getSection("charlie-puth",binding.section2MainLayout,binding.section2Title,binding.section2RecycleView)
        setupMostlyPlayed("mostly_played",binding.mostlyPlayedMainLayout,binding.mostlyPlayedTitle,binding.mostlyPlayedRecycleView)

        binding.optionBtn.setOnClickListener(){
            showPopupMenu()
        }
        scrollView = findViewById(R.id.scroll_view)
        playerView = findViewById(R.id.player_view)
        if (intent.getBooleanExtra("scroll_to_player_view", false)) {
            scrollToPlayerViewWithAnimation()
        }


    }
    private fun scrollToPlayerViewWithAnimation() {
        val scrollViewHeight = scrollView.height
        val playerViewTop = playerView.top
        val scrollY = playerViewTop - scrollViewHeight

        val animator = ValueAnimator.ofInt(scrollView.scrollY, scrollY)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            scrollView.scrollTo(0, animatedValue)
        }
        animator.start()
    }
    fun showPopupMenu(){
        val popupMenu = PopupMenu(this,binding.optionBtn)
        val inflate = popupMenu.menuInflater
        inflate.inflate(R.menu.option_menu,popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.logout -> {
                    logout()
                    true
                }
            }
            false
        }
    }

    fun logout(){
        MyExoplayer.getInstance()?.release()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
    }

    fun showPlayerView(){
        binding.playerView.setOnClickListener{
            startActivity(Intent(this,PlayerActivity::class.java))
        }
        MyExoplayer.getCurrentSong()?.let {
            binding.playerView.visibility = View.VISIBLE
            binding.songTitleTextView.text = it.title
            Glide.with(binding.songCoverImageView).load(it.coverUrl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                ).into(binding.songCoverImageView)

        } ?: run {
            binding.playerView.visibility = View.GONE
        }
    }

    //Albums
    fun getAlbum(){
        FirebaseFirestore.getInstance().collection("Album")
            .get().addOnSuccessListener {
                val albumList = it.toObjects(AlbumModel::class.java)
                setupAlbumRecyclerView(albumList)
            }
    }

    fun setupAlbumRecyclerView(albumList : List<AlbumModel>){
        albumAdapter = AlbumAdapter(albumList)
        binding.albumRecycleView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.albumRecycleView.adapter = albumAdapter
    }

    //Sections
    fun getSection(id :String,mainLayout : RelativeLayout,titleView:TextView,recyclerView: RecyclerView){
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                val section = it.toObject(AlbumModel::class.java)
                section?.apply {
                    mainLayout.visibility = View.VISIBLE
                    titleView.text = name
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                    recyclerView.adapter = SectionSongListAdapter(songs)
                    mainLayout.setOnClickListener{
                        SongListActivity.album = section
                        startActivity(Intent(this@MainActivity,SongListActivity::class.java))
                    }
                }
            }
    }

    fun setupMostlyPlayed(id :String,mainLayout : RelativeLayout,titleView:TextView,recyclerView: RecyclerView){
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {

                //get most played songs
                FirebaseFirestore.getInstance().collection("Songs")
                    .orderBy("count",Query.Direction.DESCENDING)
                    .limit(5)
                    .get().addOnSuccessListener {songListSnapshot ->
                       val songsModelList = songListSnapshot.toObjects<SongModel>()
                        val songsIdList = songsModelList.map {
                            it.id
                        }.toList()
                        val section = it.toObject(AlbumModel::class.java)
                        section?.apply {
                            section.songs = songsIdList
                            mainLayout.visibility = View.VISIBLE
                            titleView.text = name
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                            recyclerView.adapter = SectionSongListAdapter(songsIdList)
                            mainLayout.setOnClickListener{
                                SongListActivity.album = section
                                startActivity(Intent(this@MainActivity,SongListActivity::class.java))
                            }
                        }
                    }
            }
    }

}