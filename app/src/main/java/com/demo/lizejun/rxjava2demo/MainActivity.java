package com.demo.lizejun.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.lizejun.rxjava2demo.chapter1.BackgroundActivity;

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
        mTitles.add("flatMapSample()");
        mTitles.add("zipSample()");
        mTitles.add("oomSample()");
        mTitles.add("filterSample()");
        mTitles.add("sample()");
        mTitles.add("flowErrorSample()");
        mTitles.add("clickSubscription()");
        mTitles.add("flowBufferSample()");
        mTitles.add("flowDropSample()");
        mTitles.add("flatMapOrderSample()");
        mTitles.add("contactMapOrderSample()");
        mTitles.add("flowLatestSample()");
        mTitles.add("backgroundActivitySample()");
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
                    } else if (position == 2) {
                        OptUtils.flatMapSample();
                    } else if (position == 3) {
                        OptUtils.zipSample();
                    } else if (position == 4) {
                        OptUtils.oomSample();
                    } else if (position == 5) {
                        OptUtils.filterSample();
                    } else if (position == 6) {
                        OptUtils.sample();
                    } else if (position == 7) {
                        OptUtils.flowErrorSample();
                    } else if (position == 8) {
                        OptUtils.clickSubscription();
                    } else if (position == 9) {
                        OptUtils.flowBufferSample();
                    } else if (position == 10) {
                        OptUtils.flowDropSample();
                    } else if (position == 11) {
                        OptUtils.flatMapOrderSample();
                    } else if (position == 12) {
                        OptUtils.contactMapOrderSample();
                    } else if (position == 13) {
                        OptUtils.flowLatestSample();
                    } else if (position == 14) {
                        OptUtils.startActivity(MainActivity.this, BackgroundActivity.class);
                    }
                }
            });
            return item;
        }
    }
}
