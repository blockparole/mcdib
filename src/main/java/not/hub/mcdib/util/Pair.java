package not.hub.mcdib.util;

import javax.annotation.Nonnull;

/**
 * A pair of 2 objects of the same type.
 *
 * @param <T> object
 */
public class Pair<T> {

    private T a;
    private T b;

    public Pair(@Nonnull T a, @Nonnull T b) {
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public void setA(T a) {
        this.a = a;
    }

    public T getB() {
        return b;
    }

    public void setB(T b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?> pair = (Pair<?>) o;
        if (!a.equals(pair.a)) return false;
        return b.equals(pair.b);
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a.toString() +
                ", b=" + b.toString() +
                '}';
    }

}
