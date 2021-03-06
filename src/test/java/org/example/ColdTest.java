package org.example;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

public class ColdTest {

    private static Observable<String> cold(int count) {
        return Observable.create(s -> new Thread(() -> {
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
        }).start());
    }

    @Test
    public void testCold() throws InterruptedException {
        cold(20).subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("completed")
        );
        System.out.println("subscribe method ended");

        Thread.sleep(15_000);
    }
}
