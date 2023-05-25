package dev.battlesweeper.event;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class MutableEventHandler implements EventHandler {

    private final PublishSubject<Event> eventPublisher;

    public MutableEventHandler() {
        eventPublisher = PublishSubject.create();
    }

    public <T extends Event> void fireEvent(T event) {
        //callbacks.get(eventClass).forEach(v -> v.call(event));
        eventPublisher.onNext(event);
    }

    @Override
    public <T extends Event> Observable<T> listenFor(Class<T> event) {
        return eventPublisher
                .observeOn(Schedulers.computation())
                .ofType(event);
    }
}
