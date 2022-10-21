package eu.dulag.buffer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Blocking<T> {

    private final Map<T, Boolean> objects = new ConcurrentHashMap<>();

    private int using;

    private T hold;

    @SafeVarargs
    public final Blocking<T> add(T... objects) {
        for (T object : objects) {
            this.objects.put(object, false);
            this.hold = object;
        }
        return this;
    }

    @SafeVarargs
    public final void remove(T... objects) {
        for (T object : objects) this.objects.remove(object);
    }

    public T hold() {
        if (hold == null) return null;
        if (objects.get(hold)) return null;
        using++;
        objects.put(hold, true);
        return hold;
    }

    public T poll() {
        for (T object : objects.keySet()) {
            if (!objects.get(object)) {
                using++;
                objects.put(object, true);
                return object;
            }
        }
        return null;
    }

    public T await(long interrupter) throws InterruptedException {
        while (true) {
            T poll = poll();
            if (poll != null) return poll;
            Thread.sleep(interrupter);
        }
    }

    public void detach(T object) {
        if (objects.containsKey(object)) {
            objects.put(object, false);
            using--;
        }
    }

    public boolean isFree(T object) {
        if (!objects.containsKey(object)) return true;
        return !objects.get(object);
    }

    public int free() {
        return objects.size() - using;
    }
}