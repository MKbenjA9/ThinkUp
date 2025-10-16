package com.example.thinkup.repository

import android.content.Context
import com.example.thinkup.model.Idea
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class IdeasRepository(context: Context) {
    private val sp = context.getSharedPreferences("ideas_prefs", Context.MODE_PRIVATE)
    private val KEY = "ideas_json"

    fun saveIdea(idea: Idea) {
        val all = getAll().toMutableList()
        all.add(idea)
        sp.edit().putString(KEY, serialize(all)).apply()
    }

    fun getAll(): List<Idea> {
        val raw = sp.getString(KEY, "[]") ?: "[]"
        return deserialize(raw)
    }

    fun getRandom(): Idea? = getAll().ifEmpty { return null }.let { list ->
        list[Random.nextInt(list.size)]
    }

    fun clearAll() {
        sp.edit().putString(KEY, "[]").apply()
    }

    private fun serialize(list: List<Idea>): String {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().apply {
                put("id", it.id)
                put("title", it.title)
                put("description", it.description)
                put("category", it.category)
                put("lat", it.lat)
                put("lng", it.lng)
                put("author", it.author)
                put("createdAt", it.createdAt)
            })
        }
        return arr.toString()
    }

    private fun deserialize(raw: String): List<Idea> {
        val arr = JSONArray(raw)
        val out = mutableListOf<Idea>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(
                Idea(
                    id = o.getLong("id"),
                    title = o.getString("title"),
                    description = o.getString("description"),
                    category = o.getString("category"),
                    lat = o.getDouble("lat"),
                    lng = o.getDouble("lng"),
                    author = o.getString("author"),
                    createdAt = o.getLong("createdAt")
                )
            )
        }
        return out
    }
}
