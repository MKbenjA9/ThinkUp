package com.example.thinkup.repository

import android.content.Context
import com.example.thinkup.model.Idea
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class IdeasRepository(context: Context) {
    private val sp = context.getSharedPreferences("ideas_prefs", Context.MODE_PRIVATE)
    private val KEY = "ideas_json"

    /* ===== CRUD ===== */

    fun saveIdea(idea: Idea) {
        val all = getAll().toMutableList()
        all.add(idea)
        sp.edit().putString(KEY, serialize(all)).apply()
    }

    fun getAll(): List<Idea> {
        val raw = sp.getString(KEY, "[]") ?: "[]"
        return deserialize(raw)
    }

    fun getByAuthor(author: String): List<Idea> =
        getAll().filter { it.author.equals(author, ignoreCase = true) }

    fun getRandom(): Idea? {
        val list = getAll()
        if (list.isEmpty()) return null
        return list[Random.nextInt(list.size)]
    }

    fun deleteIdea(id: Long) {
        val current = getAll()
        val updated = current.filterNot { it.id == id }
        if (updated.size != current.size) {
            sp.edit().putString(KEY, serialize(updated)).apply()
        }
        // si no cambió el tamaño, no había una idea con ese ID; no hacemos nada
    }

    fun deleteAllByAuthor(author: String) {
        val updated = getAll().filterNot { it.author.equals(author, ignoreCase = true) }
        sp.edit().putString(KEY, serialize(updated)).apply()
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
            val o = arr.optJSONObject(i) ?: continue
            val id = o.optLong("id", System.currentTimeMillis())
            val title = o.optString("title", "")
            val description = o.optString("description", "")
            val category = o.optString("category", "")
            val lat = o.optDouble("lat", .0)
            val lng = o.optDouble("lng", .0)
            val author = o.optString("author", "Desconocido")
            val createdAt = o.optLong("createdAt", id) // fallback razonable

            out.add(
                Idea(
                    id = id,
                    title = title,
                    description = description,
                    category = category,
                    lat = lat,
                    lng = lng,
                    author = author,
                    createdAt = createdAt
                )
            )
        }
        return out
    }
}
