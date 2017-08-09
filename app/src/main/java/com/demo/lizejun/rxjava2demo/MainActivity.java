package com.demo.lizejun.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mTitles.add("classicalSample()");
        mTitles.add("mapSample()");
        ListView listView = (ListView) findViewById(R.id.list);
        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public Object getItem(int position) {
            return mTitles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            TextView textView = (TextView) item.findViewById(R.id.title);
            textView.setText(mTitles.get(position));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        OptUtils.classicalSample();
                    } else if (position == 1) {
                        OptUtils.mapSample();
                    }
                }
            });
            return item;
        }
    }
}
