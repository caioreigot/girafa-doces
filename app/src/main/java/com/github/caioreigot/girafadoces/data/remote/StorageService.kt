package com.github.caioreigot.girafadoces.data.remote

import com.github.caioreigot.girafadoces.data.model.ServiceResult
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.repository.StorageRepository

class StorageService : StorageRepository {

    override fun downloadAllImages(
        callback: (List<Pair<String, ByteArray>>?, serviceResult: ServiceResult) -> Unit
    ) {
        val mutableListImages = mutableListOf<Pair<String, ByteArray>>()

        Singleton.STORAGE_MENU_ITEMS_REF.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    downloadImage(item.name) { byteArray, result ->
                        when (result) {
                            is ServiceResult.Success ->
                                byteArray?.let { mutableListImages.add(Pair(item.name, it)) }

                            is ServiceResult.Error ->
                                callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                        }

                        // Just "callback" when all images are downloaded
                        if (mutableListImages.size == listResult.items.size)
                            callback(mutableListImages, ServiceResult.Success)
                    }
                }
            }

            .addOnFailureListener {
                callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
            }
    }

    override fun downloadImage(
        uid: String,
        callback: (data: ByteArray?, serviceResult: ServiceResult) -> Unit
    ) {
        val TEN_MEGABYTES: Long = (1024 * 1024) * 10

        Singleton.STORAGE_MENU_ITEMS_REF.child(uid).getBytes(TEN_MEGABYTES)
            .addOnSuccessListener { byteArray ->
                callback(byteArray, ServiceResult.Success)
            }

            .addOnFailureListener {
                callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
            }
    }

    override fun uploadImage(
        imageInByte: ByteArray,
        uid: String,
        progressCallback: (progress: Int) -> Unit,
        callback: (serviceResult: ServiceResult) -> Unit
    ) {
        Singleton.STORAGE_MENU_ITEMS_REF.child(uid).putBytes(imageInByte)
            .addOnProgressListener { uploadTask ->
                val percentage = ((uploadTask.bytesTransferred / imageInByte.size) * 100).toInt()
                progressCallback(percentage)
            }

            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    callback(ServiceResult.Success)
                else if (task.isCanceled)
                    callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
            }
    }

    override fun deleteImage(
        uid: String,
        callback: (serviceResult: ServiceResult) -> Unit
    ) {
        Singleton.STORAGE_MENU_ITEMS_REF.child(uid).delete()
            .addOnSuccessListener { callback(ServiceResult.Success) }
            .addOnFailureListener { callback(ServiceResult.Error(ErrorType.SERVER_ERROR)) }
    }
}