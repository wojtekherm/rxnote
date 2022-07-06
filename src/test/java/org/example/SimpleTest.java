package org.example;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

public class SimpleTest {

    private static Observable<String> simpleObservable(int count) {
        return Observable.create(s -> {
            for (int i = 0; i < count; i++) {
                if (s.isDisposed()) {
                    return;
                }
                s.onNext("ping " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    s.onError(e);
                    return;
                }
            }
            s.onComplete();
        });
    }

    @Test
    public void testSimple() {
        simpleObservable(20).subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("completed")
        );
        System.out.println("subscribe method ended");
    }
}
