package ch.ost.clde.ods.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymbolMovingAverages {

    private final MovingAverageCalculator ma5 = new MovingAverageCalculator(5);
    private final MovingAverageCalculator ma7 = new MovingAverageCalculator(7);

    private CrossState lastState = CrossState.NONE;
}
