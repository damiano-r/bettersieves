package bet.ter.sieves;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0.0D;

    public RandomCollection() { this(new Random()); }

    public RandomCollection(Random random) { this.random = random; }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0.0D) return this;
        this.total += weight;
        this.map.put(Double.valueOf(this.total), result);
        return this;
    }

    public E next() {
        double value = this.random.nextDouble() * this.total;
        return (E)this.map.higherEntry(Double.valueOf(value)).getValue();
    }
}