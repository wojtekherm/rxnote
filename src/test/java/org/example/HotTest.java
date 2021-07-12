package org.example;

import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class HotTest {

    private static Observable<String> hot(int count) {
        // dirty code without thread synchronization and any unsubscribe handling

        List<Emitter<String>> emitters = new ArrayList<>();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                final int x = i;
                emitters.forEach(em -> em.onNext("ping " + x));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    emitters.forEach(em -> em.onError(e));
                    return;
                }
            }
            emitters.forEach(Emitter::onComplete);
        }).start();

        return Observable.create(emitters::add);
    }

    @Test
    public void testHot() throws InterruptedException {
        Observable<String> hot = hot(20);

        Thread.sleep(5_000);

        hot.subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("completed")
        );

        Thread.sleep(15_000);
    }
}
