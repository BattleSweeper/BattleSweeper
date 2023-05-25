package dev.battlesweeper.event;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public interface EventHandler {

    <T extends Event> Observable<T> listenFor(Class<T> event);
}
