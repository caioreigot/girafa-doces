package com.github.caioreigot.girafadoces.data.remote.storage

import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.repository.StorageRepository

class StorageDataSource : StorageRepository {

    override fun downloadAllImages(
        callback: (List<Pair<String, ByteArray>>?, result: FirebaseResult) -> Unit
    ) {
        val mutableListImages = mutableListOf<Pair<String, ByteArray>>()

        Singleton.mStorageMenuImagesReference.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    downloadImage(item.name) { byteArray, result ->
                        when (result) {
                            is FirebaseResult.Success ->
                                byteArray?.let { mutableListImages.add(Pair(item.name, it)) }

                            is FirebaseResult.Error ->
                                callback(null, FirebaseResult.Error(ErrorType.SERVER_ERROR))
                        }

                        // Just "callback" when all images are downloaded
                        if (mutableListImages.size == listResult.items.size)
                            callback(mutableListImages, FirebaseResult.Success)
                    }
                }
            }

            .addOnFailureListener {
                callback(null, FirebaseResult.Error(ErrorType.SERVER_ERROR))
            }
    }

    override fun downloadImage(
        uid: String,
        callback: (data: ByteArray?, result: FirebaseResult) -> Unit
    ) {
        val TEN_MEGABYTES: Long = (1024 * 1024) * 10

        Singleton.mStorageMenuImagesReference.child(uid).getBytes(TEN_MEGABYTES)
            .addOnSuccessListener { byteArray ->
                callback(byteArray, FirebaseResult.Success)
            }

            .addOnFailureListener {
                callback(null, FirebaseResult.Error(ErrorType.SERVER_ERROR))
            }
    }

    override fun uploadImage(
        imageInByte: ByteArray,
        uid: String,
        progressCallback: (progress: Int) -> Unit,
        callback: (result: FirebaseResult) -> Unit
    ) {
        Singleton.mStorageMenuImagesReference.child(uid).putBytes(imageInByte)
            .addOnProgressListener { uploadTask ->
                val percentage = ((uploadTask.bytesTransferred / imageInByte.size) * 100).toInt()
                progressCallback(percentage)
            }

            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    callback(FirebaseResult.Success)
                else if (task.isCanceled)
                    callback(FirebaseResult.Error(ErrorType.UNEXPECTED_ERROR))
            }
    }

    override fun deleteImage(
        uid: String,
        callback: (result: FirebaseResult) -> Unit
    ) {
        Singleton.mStorageMenuImagesReference.child(uid).delete()
            .addOnSuccessListener { callback(FirebaseResult.Success) }
            .addOnFailureListener { callback(FirebaseResult.Error(ErrorType.SERVER_ERROR)) }
    }
}