package org.kreal.vpn_c;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Intent addShortCut;
        if(getIntent().getAction().equals(Intent.ACTION_CREATE_SHORTCUT)){
            addShortCut = new Intent();
            //快捷方式的名称
            addShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME , "VPN");
            //显示的图片
            Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
            addShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            //快捷方式激活的activity，需要执行的intent，自己定义
            addShortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent("android.net.vpn.SETTINGS"));
            setResult(RESULT_OK, addShortCut);
        }else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
