package com.flowapps.soundify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flowapps.soundify.R
import com.flowapps.soundify.models.Song

class SongListAdapter(private val songs: ArrayList<Song>, val onClickList: OnClickListener) :
    RecyclerView.Adapter<SongListAdapter.SongViewHolder>() {
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songNameTextView: TextView = itemView.findViewById(R.id.tv_song_name)
        val songArtistTextView: TextView = itemView.findViewById(R.id.tv_song_artist)
        val songArtImageView: ImageView = itemView.findViewById(R.id.iv_song_art)
    }

    class OnClickListener(val clickListener: (song: Song) -> Unit) {
        fun onClick(song: Song) = clickListener(song)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val rowView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_row_list, parent, false)

        return SongViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        holder.songArtImageView.setImageURI(song.getAlbumCoverURI())
        holder.songNameTextView.text = song.title
        holder.songArtistTextView.text = song.artist
        holder.itemView.setOnClickListener {
            onClickList.onClick(song)
        }
    }
}