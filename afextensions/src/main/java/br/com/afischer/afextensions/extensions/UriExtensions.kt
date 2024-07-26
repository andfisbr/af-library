package br.com.afischer.afextensions.extensions

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


fun Uri.isContentUri() = this.scheme?.lowercase() == "content"

fun Uri.isDocumentUri(context: Context) = DocumentsContract.isDocumentUri(context, this)

fun Uri.isFileUri() = this.scheme?.lowercase() == "file"

fun Uri.isAuthorityExternalStoreDoc() = this.authority?.lowercase() == "com.android.externalstorage.documents"

fun Uri.isAuthorityDownloadDoc() = this.authority?.lowercase() == "com.android.providers.downloads.documents"

fun Uri.isAuthorityMediaDoc() = this.authority?.lowercase() == "com.android.providers.media.documents"

fun Uri.isAuthorityGooglePhotoDoc() = this.authority?.lowercase() == "com.google.android.apps.photos.content"

fun Uri.isAuthorityGoogleDoc() = this.authority?.lowercase() == "com.google.android.apps.docs.storage"



fun Uri.getUriRealPah(context: Context) = when {

        isContentUri() -> if (isAuthorityGooglePhotoDoc() || isAuthorityGoogleDoc()) {
                this.lastPathSegment!!

        } else if (isAuthorityDownloadDoc()) {
                doWhenAuthorityDownloadDoc(context)

        } else if (isAuthorityExternalStoreDoc()) {
                doWhenAuthorityExternalStorageDoc()

        } else if (isAuthorityMediaDoc()) {
                doWhenAuthorityMediaDoc(context)

        } else {
                context.contentResolver.getImageRealPath(this, "")
        }



        isFileUri() -> this.path!!



        isDocumentUri(context) -> when {
                this.isAuthorityMediaDoc() -> doWhenAuthorityMediaDoc(context)
                this.isAuthorityDownloadDoc() -> doWhenAuthorityDownloadDoc(context)
                this.isAuthorityExternalStoreDoc() -> doWhenAuthorityExternalStorageDoc()
                else -> ""
        }



        else -> ""
}




/* Return uri represented document file real local path.*/
fun ContentResolver.getImageRealPath(uri: Uri, whereClause: String): String {
        // Query the URI with the condition.
        val cursor = this.query(uri, null, whereClause, null, null)

        return if (cursor == null) {
                ""
        } else {
                val moveToFirst = cursor.moveToFirst()
                if (moveToFirst) {

                        // Get columns name by URI type.
                        val columnName = when (uri) {
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> MediaStore.Images.Media.DATA
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI -> MediaStore.Audio.Media.DATA
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> MediaStore.Video.Media.DATA
                                else -> MediaStore.Images.Media.DATA
                        }

                        // Get column index.
                        val imageColumnIndex = cursor.getColumnIndex(columnName)

                        // Get column value which is the uri related file local path.
                        cursor.getString(imageColumnIndex)

                } else {
                        ""
                }
        }
}







fun Uri.doWhenAuthorityExternalStorageDoc(): String {
        val documentId = DocumentsContract.getDocumentId(this)
        val idArr = documentId.split(":")

        return if (idArr.size != 2) {
                ""

        } else {
                val type = idArr[0]
                val realDocId = idArr[1]

                if (type.lowercase() == "primary") {
                        Environment.getExternalStorageDirectory().path + "/" + realDocId
                } else {
                        ""
                }
        }
}

fun Uri.doWhenAuthorityDownloadDoc(context: Context): String {
        val documentId = DocumentsContract.getDocumentId(this)

        // Build download URI.
        val downloadUri = Uri.parse("content://downloads/public_downloads")

        // Append download document id at URI end.
        val downloadUriAppendId = ContentUris.withAppendedId(downloadUri, documentId.toLong())

        return context.contentResolver.getImageRealPath(downloadUriAppendId, "")
}

fun Uri.doWhenAuthorityMediaDoc(context: Context): String {
        val documentId = DocumentsContract.getDocumentId(this)
        val idArr = documentId.split(":")

        return if (idArr.size != 2) {
                ""

        } else {
                // First item is document type.
                val docType = idArr[0]

                // Second item is document real id.
                val realDocId = idArr[1]

                // Get content uri by document type.
                val mediaContentUri = when (docType) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                // Get where clause with real document id.
                val whereClause = MediaStore.Images.Media._ID + " = " + realDocId

                context.contentResolver.getImageRealPath(mediaContentUri, whereClause)
        }
}
