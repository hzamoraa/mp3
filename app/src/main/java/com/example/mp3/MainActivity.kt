package com.example.mp3

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player

import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), Player.Listener {


        lateinit var player: ExoPlayer
        lateinit var playerView: PlayerControlView

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("songs")
        lateinit var songItems: ArrayList<MediaItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.player)
        songItems = ArrayList()
        getSongs()


    }

    private fun getSongs() {
        myRef.get().addOnSuccessListener {

            for(data in it.children){
                var songAux = data.getValue(Song::class.java)
                if(songAux!= null){
                    var metaData = MediaMetadata.Builder()
                        .setTitle(songAux.title)
                        .setArtist(songAux.artist)
                        .setAlbumTitle(songAux.Album)
                        .setArtworkUri(Uri.parse(songAux.img))
                        .build()

                    var item:MediaItem = MediaItem.Builder()
                        .setUri(songAux.song_url)
                        .setMediaMetadata(metaData)
                        .build()

                    songItems.add(item)
                }
            }
            initPlayer()



        }
    }

    private fun initPlayer() {
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.addMediaItems(songItems)
        player.addListener(this)
        player.prepare()
        playerView.player= player
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        var title:TextView =findViewById(R.id.title)
        var artist:TextView =findViewById(R.id.artist)
        var album:TextView =findViewById(R.id.album)
        var album_image:ImageView =findViewById(R.id.album_image)

        if(mediaItem!=null){
           title.text = mediaItem.mediaMetadata.title
            artist.text = mediaItem.mediaMetadata.artist
            album.text = mediaItem.mediaMetadata.albumTitle
           Picasso.get().load(mediaItem.mediaMetadata.artworkUri).into(album_image)

        }
    }
}