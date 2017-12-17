package org.stratum0.statuswidget.interactors

import android.net.Uri
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.stratum0.statuswidget.BuildConfig
import org.stratum0.statuswidget.Constants
import java.io.IOException
import java.util.concurrent.ExecutionException

class StatusUpdater {
    fun update(name: String?) {
        val queryString = if (name != null) {
            UPDATE_URI.buildUpon().appendQueryParameter("open", "true").appendQueryParameter("by", name)
        } else {
            UPDATE_URI.buildUpon().appendQueryParameter("open", "false")
        }.build().toString()

        if (BuildConfig.DEBUG) {
            Thread.sleep(500)
            Log.d(Constants.TAG, "Skipping actual status update in debug build")
            return
        }

        try {
            val okHttpClient = OkHttpClient()

            val response = okHttpClient.newCall(Request.Builder().url(queryString).build()).execute()

            if(response.code() != 200) {
                Log.e(Constants.TAG, "Could not update space status!")
            }
        } catch (e: IOException) {
            Log.e(Constants.TAG, "IOException " + e.message, e)
        } catch (e: InterruptedException) {
            Log.e(Constants.TAG, "Wait for new status didn't finish:", e)
        } catch (e: ExecutionException) {
            Log.e(Constants.TAG, "Error executing update task inside change task:", e)
        }
    }

    companion object {
        private val UPDATE_URI = Uri.parse("https://status.stratum0.org/update")
    }
}