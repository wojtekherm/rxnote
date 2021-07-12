package org.example;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static java.time.Instant.now;

public class ConcatTest {

    private static Observable<String> emitOnePerSecond(String name, int numberOfEvents) {
        return Observable.create(s -> {
            System.out.format("%s subscribed at %s%n", name, now());
            new Thread(() -> {
                for (int i = 0; i < numberOfEvents; i++) {
                    if (s.isDisposed()) {
                        return;
                    }
                    System.out.format("%s emitted %d at %s%n", name, i, now());
                    s.onNext(name + " " + i);
                    try {
                        sleep(1_000);
                    } catch (InterruptedException e) {
                        System.out.format("%s failed at %s%n", name, now());
                        s.onError(e);
                        return;
                    }
                }
                System.out.format("%s completed at %s%n", name, now());
                s.onComplete();
            }).start();
        });
    }

    @Test
    public void simple() throws InterruptedException {
        emitOnePerSecond("Ala", 5)
                .subscribe();
        sleep(10_000);
    }

    @Test
    public void flatMap() throws InterruptedException {
        Observable.fromArray("Ala", "Julka", "Wally")
                .flatMap(name -> emitOnePerSecond(name, 5))
                .subscribe();
        sleep(10_000);
    }

    @Test
    public void concatMap() throws InterruptedException {
        Observable.fromArray("Ala", "Julka", "Wally")
                .concatMap(name -> emitOnePerSecond(name, 5))
                .subscribe();
        sleep(20_000);
    }

    @Test
    public void concatMapEager() throws InterruptedException {
        Observable.fromArray("Ala", "Julka", "Wally")
                .concatMapEager(name -> emitOnePerSecond(name, 5))
                .subscribe(s -> System.out.println("$ " + s + " " + now()));
        sleep(10_000);
    }
}
