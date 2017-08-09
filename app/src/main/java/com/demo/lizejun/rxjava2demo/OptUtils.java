package com.demo.lizejun.rxjava2demo;


import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class OptUtils {

    private static final String TAG = "OptUtils";

    public static void classicalSample() {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext("1");
                observableEmitter.onNext("2");
                observableEmitter.onNext("3");
                observableEmitter.onNext("4");

            }
        }).subscribe(new Observer<String>() {

            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "onSubscribe");
                mDisposable = disposable;
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext=" + s);
                if ("2".equals(s)) {
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError");

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");

            }
        });
    }

    static void mapSample() {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                Log.d(TAG, "observableEmitter's thread=" + Thread.currentThread().getId() + ",string=true");
                observableEmitter.onNext("true");
                Log.d(TAG, "observableEmitter's thread=" + Thread.currentThread().getId() + ",string=false");
                observableEmitter.onNext("false");
                Log.d(TAG, "observableEmitter's thread=" + Thread.currentThread().getId() + ",onComplete");
                observableEmitter.onComplete();
            }
        //1.指定了subscribe方法执行的线程，并进行第一次下游线程的切换，将其切换到新的子线程。
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).map(new Function<String, Boolean>() {

            @Override
            public Boolean apply(String s) throws Exception {
                Log.d(TAG, "apply's thread=" + Thread.currentThread().getId() + ",s=" + s);
                return "true".equals(s);
            }
        //2.进行第二次下游线程的切换，将其切换到主线程。
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {

            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.d(TAG, "Observer's thread=" + Thread.currentThread().getId() + ",boolean=" + aBoolean);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Observer's thread=" + Thread.currentThread().getId() + ",onComplete");
            }
        });
    }
}
