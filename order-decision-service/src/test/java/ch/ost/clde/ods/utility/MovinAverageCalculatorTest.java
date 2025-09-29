package ch.ost.clde.ods.utility;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovingAverageCalculatorTest {

    @Test
    void shouldCalculateAverageWithFewerValuesThanWindow() {
        MovingAverageCalculator ma = new MovingAverageCalculator(3);

        assertThat(ma.add(10)).isEqualTo(10.0);
        assertThat(ma.add(20)).isEqualTo((10.0 + 20.0) / 2);
        assertThat(ma.isFull()).isFalse();
    }

    @Test
    void shouldCalculateAverageWithFullWindow() {
        MovingAverageCalculator ma = new MovingAverageCalculator(3);

        ma.add(10);
        ma.add(20);
        double avg = ma.add(30);

        assertThat(avg).isEqualTo((10.0 + 20.0 + 30.0) / 3);
        assertThat(ma.isFull()).isTrue();
    }

    @Test
    void shouldSlideWindowWhenExceedingWindowSize() {
        MovingAverageCalculator ma = new MovingAverageCalculator(3);

        ma.add(10);
        ma.add(20);
        ma.add(30);
        double avg = ma.add(40); // 10 wird rausgeschmissen

        assertThat(avg).isEqualTo((20.0 + 30.0 + 40.0) / 3);
        assertThat(ma.isFull()).isTrue();
    }

    @Test
    void shouldHandleConstantValuesCorrectly() {
        MovingAverageCalculator ma = new MovingAverageCalculator(3);

        ma.add(5);
        ma.add(5);
        double avg = ma.add(5);

        assertThat(avg).isEqualTo(5.0);
    }
}

