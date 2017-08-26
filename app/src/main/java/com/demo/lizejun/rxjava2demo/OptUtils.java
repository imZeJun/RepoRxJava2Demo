package com.demo.lizejun.rxjava2demo;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class OptUtils {

    private static final String TAG = "OptUtils";

    private static Subscription sSubscription;

    public static void classicalSample() {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext("1");
                observableEmitter.onNext("2");
                observableEmitter.onNext("3");
                observableEmitter.onNext("4");
                observableEmitter.onComplete();

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

    static void flatMapSample() {
        Observable<Integer> sourceObservable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(1);
                observableEmitter.onNext(2);
                observableEmitter.onNext(3);
            }
        });
        Observable<String> flatObservable = sourceObservable.flatMap(new Function<Integer, ObservableSource<String>>() {

            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.fromArray("a value of " + integer + ",b value of " + integer);
            }
        });
        flatObservable.subscribe(new Consumer<String>() {

            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    static void flatMapOrderSample() {
        Observable<Integer> sourceObservable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                Log.d(TAG, "flatMapOrderSample emit 1");
                observableEmitter.onNext(1);
                Log.d(TAG, "flatMapOrderSample emit 2");
                observableEmitter.onNext(2);
                Log.d(TAG, "flatMapOrderSample emit 3");
                observableEmitter.onNext(3);
            }
        });
        Observable<String> flatObservable = sourceObservable.flatMap(new Function<Integer, ObservableSource<String>>() {

            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                Log.d(TAG, "flatMapOrderSample apply=" + integer);
                long delay = (3 - integer) * 100;
                return Observable.fromArray("a value of " + integer, "b value of " + integer).delay(delay, TimeUnit.MILLISECONDS);
            }
        });
        flatObservable.subscribe(new Consumer<String>() {

            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    static void contactMapOrderSample() {
        Observable<Integer> sourceObservable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                Log.d(TAG, "contactMapOrderSample emit 1");
                observableEmitter.onNext(1);
                Log.d(TAG, "contactMapOrderSample emit 1");
                observableEmitter.onNext(2);
                Log.d(TAG, "contactMapOrderSample emit 1");
                observableEmitter.onNext(3);
            }
        });
        Observable<String> flatObservable = sourceObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).concatMap(new Function<Integer, ObservableSource<String>>() {

            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                Log.d(TAG, "contactMapOrderSample apply=" + integer);
                long delay = (3 - integer) * 100;
                return Observable.fromArray("a value of " + integer, "b value of " + integer).delay(delay, TimeUnit.MILLISECONDS);
            }
        });
        flatObservable.subscribe(new Consumer<String>() {

            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    static void zipSample() {
        Observable<Integer> sourceObservable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                Log.d(TAG, "sourceObservable emit 1");
                observableEmitter.onNext(1);
                Thread.sleep(1000);
                Log.d(TAG, "sourceObservable emit 2");
                observableEmitter.onNext(2);
                Log.d(TAG, "sourceObservable emit 3");
                observableEmitter.onNext(3);
                Log.d(TAG, "sourceObservable emit 4");
                observableEmitter.onNext(4);
            }
        }).subscribeOn(Schedulers.newThread());
        Observable<Integer> otherObservable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                Log.d(TAG, "otherObservable emit 1");
                observableEmitter.onNext(1);
                Log.d(TAG, "otherObservable emit 2");
                observableEmitter.onNext(2);
                Log.d(TAG, "otherObservable emit 3");
                observableEmitter.onNext(3);
            }
        }).subscribeOn(Schedulers.newThread());
        Observable.zip(sourceObservable, otherObservable, new BiFunction<Integer, Integer, Integer>() {

            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }

        }).subscribe(new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "resultObservable onSubscribe");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "resultObservable onNext=" + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "resultObservable onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "resultObservable onComplete");

            }
        });
    }

    static void oomSample() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    Log.d(TAG, "observableEmitter=" + i);
                    observableEmitter.onNext(i);
                }
            }
        }).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Thread.sleep(2000);
                Log.d(TAG, "accept=" + integer);
            }

        });
    }

    static void filterSample() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    observableEmitter.onNext(i);
                }
            }

        }).filter(new Predicate<Integer>() {

            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 60 == 0;
            }
        }).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "sample accept=" + integer);
            }
        });
    }

    static void sample() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    Thread.sleep(200);
                    Log.d(TAG, "sample, observableEmitter=" + i);
                    observableEmitter.onNext(i);
                }

            }
        }).sample(2, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "sample, accept=" + integer);
            }
        });
    }

    static void flowErrorSample() {
        final Flowable<Integer> sourceFlow = Flowable.create(new FlowableOnSubscribe<Integer>() {

            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 200; i++) {
                    Thread.sleep(500);
                    Log.d(TAG, "requestCount=" + emitter.requested() + ",emitter=" + i);
                    emitter.onNext(i);
                }

                emitter.onComplete();
            }

        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.newThread());

        sourceFlow.observeOn(Schedulers.io()).subscribe(new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                Log.d(TAG, "onSubscribe");
                sSubscription = subscription;
                sSubscription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sSubscription.request(1);
                Log.d(TAG, "onNext=" + integer);
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

    static void clickSubscription() {
        if (sSubscription != null) {
            sSubscription.request(64);
        }
    }

    static void flowBufferSample() {
        Flowable<Integer> sourceFlow = Flowable.create(new FlowableOnSubscribe<Integer>() {

            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 10000;i ++) {
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }

        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io());

        sourceFlow.observeOn(Schedulers.newThread()).subscribe(new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                Log.d(TAG, "onSubscribe");
                sSubscription = subscription;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext=" + integer);
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

    static void flowDropSample() {
        Flowable<Integer> sourceFlow = Flowable.create(new FlowableOnSubscribe<Integer>() {

            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 130; i++) {
                    emitter.onNext(i);
                }
            }

        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io());

        sourceFlow.observeOn(Schedulers.io()).subscribe(new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                Log.d(TAG, "onSubscribe");
                sSubscription = subscription;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext=" + integer);
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

    static void flowLatestSample() {
        Flowable<Integer> sourceFlow = Flowable.create(new FlowableOnSubscribe<Integer>() {

            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 130; i++) {
                    emitter.onNext(i);
                }
            }

        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io());

        sourceFlow.observeOn(Schedulers.io()).subscribe(new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                Log.d(TAG, "onSubscribe");
                sSubscription = subscription;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext=" + integer);
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

    static void startActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
    }
}
