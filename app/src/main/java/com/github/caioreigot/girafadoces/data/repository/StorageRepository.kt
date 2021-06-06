package com.github.caioreigot.girafadoces.data.repository

import com.github.caioreigot.girafadoces.data.model.FirebaseResult

interface StorageRepository {
    fun downloadAllImages(
        callback: (List<Pair<String, ByteArray>>?, result: FirebaseResult) -> Unit
    )

    fun downloadImage(
        uid: String,
        callback: (byteArray: ByteArray?, result: FirebaseResult) -> Unit
    )

    fun uploadImage(
        imageInByte: ByteArray,
        uid: String,
        progressCallback: (progress: Int) -> Unit,
        callback: (result: FirebaseResult) -> Unit
    )

    fun deleteImage(
        uid: String,
        callback: (result: FirebaseResult) -> Unit
    )
}