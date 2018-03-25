package application.dependencyInjection;


@FunctionalInterface
public interface IVarArgFunc<T, R> {
    R apply(T... args);
}


