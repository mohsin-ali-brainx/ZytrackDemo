package com.brainx.androidext.ext


import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.io.*
import java.lang.reflect.Modifier


fun createGSon(): Gson {
    val builder = GsonBuilder()
    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
    return builder.create()
}

fun Any.toJson(cls: Class<*>): String? {
    return GsonBuilder().create().toJson(this, cls);
}

fun Any.toJson(): String {
    val builder = GsonBuilder()
    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
    val gSon = builder.create()
    return gSon.toJson(this)
}

fun String.toObject(activityClass: Class<*>): Any? {
    return GsonBuilder().create().fromJson(this, activityClass)

}

fun Any.objectToMap(): MutableMap<String, Any> {
    return Gson().fromJson(
        toJson(),
        MutableMap::class.java
    ) as MutableMap<String, Any>
}

fun Context.readJsonFromRaw(JsonFileId: Int): String {
    val inputStream: InputStream = resources.openRawResource(JsonFileId)
    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    inputStream.use { iStream ->
        val reader: Reader = BufferedReader(InputStreamReader(iStream, "UTF-8"))
        var lenght: Int
        while (reader.read(buffer).also { lenght = it } != -1) {
            writer.write(buffer, 0, lenght)
        }
    }

    return writer.toString()
}


fun String.toJsonObject(): JSONObject {
    return JSONObject(this)
}




