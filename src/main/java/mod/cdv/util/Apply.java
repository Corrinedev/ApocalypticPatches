package mod.cdv.util;

@FunctionalInterface
public interface Apply<T> {
    T apply(T in);
}
