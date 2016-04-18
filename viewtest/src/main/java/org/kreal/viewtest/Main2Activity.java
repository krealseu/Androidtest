package org.kreal.viewtest;

import android.content.Context;
import android.os.Environment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class Main2Activity extends AppCompatActivity {

    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        gridView=(GridView)findViewById(R.id.gridView);
        final String[] list;

        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.layoutarray);
        adapter.add("dsf");
        adapter.add("1dsf");adapter.add("2dsf");
        adapter.add("3dsf");
        adapter.add("4dsf");

        File file=Environment.getExternalStorageDirectory();
        final File[] files=file.listFiles();
        final df dd=new df(getApplicationContext(),R.layout.layoutarray);
        dd.addAll(files);
        dd.sort(new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if(lhs.isFile()==rhs.isFile())
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                else return (lhs.isFile()?1:-1);
            }
        });
        gridView.setAdapter(dd);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("qwe", position + "asd" + id + "hhh");
                dd.clear();
                dd.addAll(files);
                dd.sort(new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        if (lhs.isFile() == rhs.isFile())
                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                        else return (lhs.isFile() ? 1 : -1);
                    }
                });
            }
        });
    }

    private class ad extends BaseAdapter{

        @Override
        public int getCount() {
            return 1000;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if(convertView==null)
            {
                textView=new TextView(getApplicationContext());
                textView.setGravity(Gravity.CENTER);
                textView.setMinHeight(80);
                //textView.setText("qw"+position);
            }
            else textView=(TextView)convertView;
            textView.setText("qw"+position);
            return textView;
        }
    }

    private class df extends ArrayAdapter<File>{
        public df(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView=new TextView(getApplicationContext());
            textView.setText(getItem(position).getName());
            return textView;
        }
    }
}
