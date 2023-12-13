package com.healthapp_av

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 *  Makes queries to calorieninjas.com to get nutritional information about a food.
 *
 *  The following lines are added to the project manifest for internet access:
 *      <uses-permission android:name="android.permission.INTERNET" />
 *      <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *
 *  The following dependencies are added to build.gradle.kts for JSON serialization:
 *      implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
 *      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
 *
 *  The following line is added to plugins in build.gradle.kts for JSON serialization:
 *      id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
 *
 *  Networking code, such as data retrieval, should never be done on the main thread.
 *  Use withContext with dispatcher to execute code on the main thread, an I/O thread, or a default thread.
 **/
class WebData {

    private val apiKey = "a0m9FBw5/m3K+TfcPC2e1A==vcGHQ00C2yr9s228"

    fun search(query: String, doWithResult: (Food?) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.IO).async { getData(query) }.await()
            data?.also { data ->
                doWithResult(foodFromData(-1, data.items[0], ArrayList(), -1))
            } ?: run {
                doWithResult(null)
            }
        }
    }

    private suspend fun getData(query: String): FoodDataList? {
        return withContext(Dispatchers.IO) {
            var items: FoodDataList? = null
            val url = URL("https://api.calorieninjas.com/v1/nutrition?query=$query")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("X-Api-Key", apiKey)
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = Json { ignoreUnknownKeys = true }
                items = json.decodeFromString<FoodDataList>(response)
            }
            items
        }
    }
}