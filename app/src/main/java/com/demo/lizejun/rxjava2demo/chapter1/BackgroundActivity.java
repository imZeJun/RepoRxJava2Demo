package com.demo.lizejun.rxjava2demo.chapter1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.lizejun.rxjava2demo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BackgroundActivity extends AppCompatActivity {

    private TextView mTvDownload;
    private int downloadIndex = 0;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        mTvDownload = (TextView) findViewById(R.id.tv_download);
        mTvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload(downloadIndex++);
            }
        });
    }

    private void startDownload(final int downloadIndex) {
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i % 20 == 0) {
                        try {
                            Thread.sleep(500); //模拟下载的操作。
                        } catch (InterruptedException exception) {
                            if (!e.isDisposed()) {
                                e.onError(exception);
                            }
                        }
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }

        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                Log.d("BackgroundActivity", "downloadIndex=" + downloadIndex + ",onNext=" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("BackgroundActivity", "downloadIndex=" + downloadIndex + ",onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d("BackgroundActivity", "downloadIndex=" + downloadIndex + ",onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
