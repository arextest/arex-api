package com.arextest.web.common;


import java.util.Objects;

public class Tuple<X, Y> {
    public final X x;
    public final Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(x, tuple.x) &&
                Objects.equals(y, tuple.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
