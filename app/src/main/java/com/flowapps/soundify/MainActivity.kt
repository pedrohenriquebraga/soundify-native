package com.flowapps.soundify

import android.R.*
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.flowapps.soundify.adapters.SongListAdapter
import com.flowapps.soundify.databinding.ActivityMainBinding
import com.flowapps.soundify.models.Song

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var AUDIO_PERMISSION_CODE = 1
    private val readAudioFilesPermissionsOld = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private lateinit var adapter: SongListAdapter
    private lateinit var mediaPlayer: MediaPlayer


    @SuppressLint("InlinedApi")
    private val readAudioFilesPermissionsNew = android.Manifest.permission.READ_MEDIA_AUDIO
    private val permission: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        readAudioFilesPermissionsNew
    } else {
        readAudioFilesPermissionsOld
    }

    private lateinit var songs: ArrayList<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestPermissionsLauncher()

        binding.rvMusics.layoutManager = LinearLayoutManager(this)
        adapter = SongListAdapter(songs, SongListAdapter.OnClickListener { song ->

            binding.tvSongName.text = song.title
            binding.tvSongArtist.text = song.artist
            binding.ivSongArt.setImageURI(song.getAlbumCoverURI())

            mediaPlayer = MediaPlayer.create(this, song.uri)
            playPauseSong()
        })
        binding.rvMusics.adapter = adapter

        binding.btPlayPause.setOnClickListener {
            playPauseSong()
        }
    }

    private fun playPauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.btPlayPause.setImageResource(drawable.ic_media_play)
        } else {
            mediaPlayer.start()
            binding.btPlayPause.setImageResource(drawable.ic_media_pause)
        }
    }

    private fun getAllSongs(): ArrayList<Song> {
        val songs = ArrayList<Song>()

        val songsAttributes = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val musicURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        this.contentResolver.query(musicURI, songsAttributes, selection, null, null)
            ?.use { cursor ->
                val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn).toString()
                    val artist = cursor.getString(artistColumn).toString()
                    val albumId: Long = cursor.getString(albumIdColumn).toLong()
                    val uri = ContentUris.withAppendedId(musicURI, id)

                    songs.add(Song(uri, title, artist, albumId))
                }
            }

        return songs
    }

    private fun requestPermissionsLauncher() {
        if (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission), AUDIO_PERMISSION_CODE
            )
        } else {
            songs = getAllSongs()
            Log.d("Songs", songs.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Tudo certo para começarmos", Toast.LENGTH_SHORT).show()
                    songs = getAllSongs()
                    Log.d("Songs", songs.toString())
                } else {
                    Toast.makeText(
                        this,
                        "Preciso da sua permissão para acessar suas músicas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}