package com.brainx.zytrack_demo.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.brainx.zytrack_demo.User
import java.io.InputStream
import java.io.OutputStream


object UserSerializer : Serializer<User> {
    override val defaultValue: User = User.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): User {
        try {
            return User.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: User, output: OutputStream) = t.writeTo(output)
}