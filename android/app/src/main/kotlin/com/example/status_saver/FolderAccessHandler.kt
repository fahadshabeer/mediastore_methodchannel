package com.example.status_saver

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

// Replace with your package name
class FolderAccessHandler(private val context: Context, private val result: MethodChannel.Result) {
    fun createFolder() {
        try {
            val folder = File(context.getExternalFilesDir(null), "Fahad")
            if (!folder.exists()) {
                folder.mkdirs()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
     fun createDirectoryInExternalStorage(directoryName: String) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, directoryName)
            put(MediaStore.MediaColumns.MIME_TYPE, "vnd.android.cursor.dir/$directoryName")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/$directoryName")
            }
        }

        val resolver = context.contentResolver
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val itemUri = resolver.insert(collection, values)

        if (itemUri != null) {
           result.success(true)
        } else {
            result.error("CREATE-ERROR","Failed to Created Dir","NONE");
        }
    }


    private fun readDataFromFile(file: File): ByteArray? {
        // Implement the method to read file data as byte[]
        // Example code: FileInputStream fileInputStream = new FileInputStream(file);
        // byte[] fileData = new byte[fileInputStream.available()];
        // fileInputStream.read(fileData);
        // fileInputStream.close();
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun copyFileUsingMediaStore(
        sourceFilePath: String,
        newFileName: String
    ) {
        val contentResolver=context.contentResolver;
        val sourceFile = File(sourceFilePath,newFileName)
        val sourceInputStream = FileInputStream(sourceFile)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(sourceFile))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/MySaver/")
            }
        }

        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = contentResolver.insert(collection, values)

        if (itemUri != null) {
            val outputStream: OutputStream? = contentResolver.openOutputStream(itemUri)
            outputStream?.use {
                sourceInputStream.copyTo(it)
            }
        }

        sourceInputStream.close()
        result.success(true)
    }

    // Function to get MIME type of a file
    fun getMimeType(file: File): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
    }


    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
        private const val FOLDER_PATH =
            "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
    }
}