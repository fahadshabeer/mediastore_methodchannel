package com.example.status_saver

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.File


class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.whatsapp_status"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getStatusDirectory") {
                FolderAccessHandler(context,result).createDirectoryInExternalStorage("MySaver");
            }
            else if(call.method=="saveFile"){
                val sourcePath=call.argument<String>("path");
                val fileName=call.argument<String>("fileName");
                FolderAccessHandler(context,result).copyFileUsingMediaStore(sourcePath.toString(),fileName.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun openDirectory() {
        val path = Environment.getExternalStorageDirectory()
            .toString() + "/Android/data/com.pubg.krmobile/whatever folder you want to access"
        val file = File(path)
        var startDir="";
        var secondDir: String
        val finalDirPath: String
        if (file.exists()) {
            startDir =
                "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        }
        val sm = getSystemService(STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        Log.d("TAG", "INITIAL_URI scheme: $scheme")
        scheme = scheme.replace("/root/", "/document/")
        finalDirPath = "$scheme%3A$startDir"
        uri = Uri.parse(finalDirPath)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        Log.d("TAG", "uri: $uri")
        try {
            startActivityForResult(intent, 6)
        } catch (ignored: ActivityNotFoundException) {
        }
    }
    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data != null) {
               var uri = data.data
                if (uri != null) {
                    if (uri.path?.endsWith(".Statuses") == true) {
                        Log.d("TAG", "onActivityResult: " + uri.getPath())
                        val takeFlags = (data.flags
                                and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            contentResolver.takePersistableUriPermission(uri, takeFlags)
                        }

                        // these are my SharedPerfernce values for remembering the path
            //                    prefHelper.setIsScopePermissionGranted(true)
            //                    prefHelper.setSavedRoute(uri.toString())

                        // save any boolean in pref if user given the right path so we can use the path
                        // in future and avoid to ask permission more than one time
            //                    startActivity(Intent(this, MainDashboardActivity::class.java))
                        finish()
                    } else {
                        // dialog when user gave wrong path
            //                    showWrongPathDialog()
                    }
                }
            }
        }
    }
}
