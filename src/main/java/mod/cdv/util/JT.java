package mod.cdv.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class JT {
    public static <T> T apply(T t, Consumer<T> c) {
        c.accept(t);
        return t;
    }
    public static <T, E> E castOrNull(Class<E> castTo, T value) {
        E cast = null;
        try {
            cast = castTo.cast(value);
        } catch (ClassCastException ignored) {}

        return cast;
    }
    public static <T, E> E castOrDefault(Class<E> castTo, T value, E defaultValue) {
        E cast = defaultValue;
        try {
            cast = castTo.cast(value);
        } catch (ClassCastException ignored) {}

        return cast;
    }
    public static <E> boolean orNull(E value, Predicate<E> predicate) {
        return value != null && predicate.test(value);
    }
}
