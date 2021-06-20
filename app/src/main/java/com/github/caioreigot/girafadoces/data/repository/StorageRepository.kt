package com.github.caioreigot.girafadoces.data.repository

import com.github.caioreigot.girafadoces.data.model.ServiceResult

interface StorageRepository {
    fun downloadAllImages(
        callback: (List<Pair<String, ByteArray>>?, serviceResult: ServiceResult) -> Unit
    )

    fun downloadImage(
        uid: String,
        callback: (byteArray: ByteArray?, serviceResult: ServiceResult) -> Unit
    )

    fun uploadImage(
        imageInByte: ByteArray,
        uid: String,
        progressCallback: (progress: Int) -> Unit,
        callback: (serviceResult: ServiceResult) -> Unit
    )

    fun deleteImage(
        uid: String,
        callback: (serviceResult: ServiceResult) -> Unit
    )
}