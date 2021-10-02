package com.tangem.tap.domain

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.tangem.common.services.Result

/**
 * Created by Anton Zhilenkov on 17/09/2021.
 */
class UrlBitmapLoader {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun loadBitmap(url: String, callback: (Result<Bitmap>) -> Unit) {
        val target = DownloadTarget(callback)
        protectedFromGarbageCollectorTargets.add(target)
        mainHandler.post { Picasso.get().load(url).into(target) }
    }

    fun loadBitmap(url: String, target: DownloadTarget) {
        protectedFromGarbageCollectorTargets.add(target)
        Picasso.get().load(url).into(target)
    }

}

private val protectedFromGarbageCollectorTargets = mutableListOf<Target>()

// It adds the ability to trigger multiple downloads with a unique callback.
open class DownloadTarget(
    val callback: (Result<Bitmap>) -> Unit,
) : Target {

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        callback(Result.Success(bitmap))
        protectedFromGarbageCollectorTargets.remove(this)
    }

    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
        callback.invoke(Result.Failure(e))
        protectedFromGarbageCollectorTargets.remove(this)
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
    }
}