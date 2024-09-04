package com.example.musicapp_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp_kotlin.adapter.SongAdapter
import com.example.musicapp_kotlin.databinding.ActivitySearchBinding
import com.example.musicapp_kotlin.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {
    lateinit var binding : ActivitySearchBinding

    private lateinit var searchView: SearchView
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var adapter: SongAdapter
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        searchView = findViewById(R.id.search_view)
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view)
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SongAdapter(emptyList()) { song ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("SONG_ID", song.id)
                putExtra("SONG_TITLE", song.title)
                putExtra("SONG_ARTIST", song.artist)
                putExtra("SONG_URL", song.url)
                putExtra("SONG_COVER_URL", song.coverUrl)
            }
            startActivity(intent)
        }
        searchResultsRecyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    fetchSongs(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        fetchSongs(it)
                    } else {
                        adapter = SongAdapter(emptyList()) { }
                        searchResultsRecyclerView.adapter = adapter
                    }
                }
                return true
            }
        })
    }
    fun normalizeString(str: String): String {
        return str.lowercase().replace("[^\\p{L}\\p{M}]".toRegex(), "")
    }

    private fun fetchSongs(query: String) {
        val normalizedQuery = normalizeString(query)
        db.collection("Songs")
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + '\uf8ff')
            .get()
            .addOnSuccessListener { result ->
                val songs = result.mapNotNull { document ->
                    document.toObject(SongModel::class.java)
                }
                adapter = SongAdapter(songs) { song ->
                    val intent = Intent(this, PlayerActivity::class.java).apply {
                        putExtra("SONG_ID", song.id)
                        putExtra("SONG_TITLE", song.title)
                        putExtra("SONG_ARTIST", song.artist)
                        putExtra("SONG_URL", song.url)
                        putExtra("SONG_COVER_URL", song.coverUrl)
                    }
                    startActivity(intent)
                }
                searchResultsRecyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting songs: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}