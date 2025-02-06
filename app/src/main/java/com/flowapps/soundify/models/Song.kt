package com.flowapps.soundify.models

import android.net.Uri

class Song(val uri: Uri, val title: String?, val artist: String?, private val albumId: Long?) {

    private val albumInitialPath = "content://media/external/audio/albumart/"

    fun getAlbumCoverURI(): Uri? = Uri.parse("$albumInitialPath${albumId}")
    override fun toString(): String {
        return "Song(title=$title, artist=$artist, albumId=$albumId, albumInitialPath='$albumInitialPath')"
    }
}