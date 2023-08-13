import 'dart:developer';
import 'dart:io';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:saf/saf.dart';
import 'package:shared_preferences/shared_preferences.dart';



class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  final MethodChannel _methodChannel = MethodChannel('com.example.whatsapp_status');
  List<String> _statusFilePaths = [];
  bool _isPermissionGranted = false;

  @override
  void initState() {
    super.initState();
  }

  Future<List<Map<String, dynamic>>> requestFilesFromFolder() async {
    try {
      Permission.storage.request();
     await _methodChannel.invokeMethod('getStatusDirectory');
      var saf=Saf("Android/media/com.whatsapp/WhatsApp/Media/.Statuses");

      await saf.getDirectoryPermission();
      await saf.sync();
      _isPermissionGranted=true;
      var files=await saf.getCachedFilesPath();
      // for (var element in files!) {
      //   log(element);
      // }
      _statusFilePaths=files?.where((element) => element.endsWith(".jpg")).toList()??[];
      setState(() {

      });
      return [];
    } on Exception catch (e) {
      // Handle platform exceptions, if any.
      print('Error: ${e.toString()}');
      return [];
    }
  }

  void saveFile(String path)async{
    try{
      var fileName=path.split("/").last;
      var filePath=path.split(fileName).first;
      await _methodChannel.invokeMethod("saveFile",{"path":filePath,"fileName":fileName});
    }catch(e){
      log(e.toString());
    }
  }





  @override
  Widget build(BuildContext context) {
    return Container(child: Scaffold(
        appBar: AppBar(
          title: Text('WhatsApp Status Images'),
        ),
        body: _isPermissionGranted
            ? ListView.builder(
          itemCount: _statusFilePaths.length,
          itemBuilder: (context, index) {
            final url = _statusFilePaths[index];
            return GestureDetector(
              onTap: (){
                saveFile(_statusFilePaths[index]);
              },
                child: Image.file(File(url)));
          },
        )
            : Center(
          child: ElevatedButton(
            onPressed:()async{
             await requestFilesFromFolder();
            },
            child: const Text('Grant Permission'),
          ),
        ),
      ),
    );
  }
}
