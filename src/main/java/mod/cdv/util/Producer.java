package mod.cdv.util;

@FunctionalInterface
public interface Producer<A> {
    void apply
            (
                    A a
            );
    @FunctionalInterface
    interface Producer2<A, B> {
        void apply
                (
                        A a,
                        B b
                );
    }
    @FunctionalInterface
    interface Producer3<A, B, C> {
        void apply
                (
                        A a,
                        B b,
                        C c
                );
    }
    @FunctionalInterface
    interface Producer4<A, B, C, D> {
        void apply
                (
                        A a,
                        B b,
                        C c,
                        D d
                );
    }
}
