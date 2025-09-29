package ch.ost.clde.ods.utility;

import java.util.ArrayDeque;
import java.util.Deque;

public class MovingAverageCalculator {

    private final int windowSize;
    private final Deque<Double> window = new ArrayDeque<>();
    private double sum = 0.0;

    public MovingAverageCalculator(int windowSize) {
        this.windowSize = windowSize;
    }

    public double add(double value) {
        // neuen Wert hinzufügen
        window.addLast(value);
        sum += value;

        // wenn zu groß -> ältesten rauswerfen
        if (window.size() > windowSize) {
            double removed = window.removeFirst();
            sum -= removed;
        }

        // aktueller Durchschnitt
        return sum / window.size();
    }

    public boolean isFull() {
        return window.size() == windowSize;
    }
}

