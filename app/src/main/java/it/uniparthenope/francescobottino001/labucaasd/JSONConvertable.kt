package it.uniparthenope.francescobottino001.labucaasd

import com.google.gson.Gson
import org.json.JSONArray

inline fun <reified T> String.toObject(): T = Gson().fromJson(this, T::class.java)

inline fun <reified T> T.toGson(): String = Gson().toJson(this)

inline fun <reified T> String.toObjectArray(): Array<T> = Gson().fromJson(this, Array<T>::class.java)

inline fun <reified T> JSONArray.toArrayList(): ArrayList<T> {
    val array: ArrayList<T> = arrayListOf()
    for (i in 0 until this.length()) {
        array.add(this.getJSONObject(i).toString().toObject())
    }
    return array
}