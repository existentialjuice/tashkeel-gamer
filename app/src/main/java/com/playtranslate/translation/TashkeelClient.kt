package com.playtranslate.translation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

internal object TashkeelClient {
    suspend fun process(text: String): String =
        withContext(Dispatchers.IO) {
            try {
                val connection = URL(
                    "http://127.0.0.1:8765/tashkeel"
                ).openConnection() as HttpURLConnection

                try {
                    connection.requestMethod = "POST"
                    connection.connectTimeout = 5000
                    connection.readTimeout = 30000
                    connection.doOutput = true
                    connection.setRequestProperty(
                        "Content-Type", "application/json"
                    )

                    val body = JSONObject()
                        .put("text", text)
                        .toString()

                    connection.outputStream.use {
                        it.write(body.toByteArray(Charsets.UTF_8))
                    }

                    if (connection.responseCode != 200) {
                        return@withContext text
                    }

                    val response = connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                    JSONObject(response).optString("text", text)
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                text
            }
        }
}
