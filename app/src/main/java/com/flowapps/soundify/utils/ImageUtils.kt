package com.flowapps.soundify.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.ImageInfo
import android.net.Uri
import androidx.palette.graphics.Palette

class ImageUtils(private val context: Context, val uri: Uri) {

    private fun getBitmap(): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(
            source
        ) { imageDecoder: ImageDecoder, imageInfo: ImageInfo?, source1: ImageDecoder.Source? ->
            imageDecoder.isMutableRequired =
                true
        };

        return bitmap;
    }

    fun getPaletteColors(cache: Boolean): Palette {
        return Palette.from(getBitmap()).generate();
    }

    fun rgbToHex(rgb: Int): String {
        return String.format("%06x", 0xFFFFFF and rgb)
    }
}