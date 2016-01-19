//package org.kreal.wallpaper;
//
//import android.app.WallpaperManager;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;

//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent,0);
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case 0:{
//                if(resultCode!=RESULT_OK) return;
//                Intent i=WallpaperManager.getInstance(getApplicationContext()).getCropAndSetWallpaperIntent(data.getData());
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(i,1);
//                break;
//            }
//            case 1:{
//                finish();
//                break;
//            }
//
//        }
//    }
//}
