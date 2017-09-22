package com.sllibrary.util.menus.rows;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiFunction;

@Getter @AllArgsConstructor
public enum Row {

    ONE(9),
    TWO(18),
    THREE(27),
    FOUR(36),
    FIVE(45),
    SIX(54);

    private final int slotAmount;

    private final BiFunction<Row, Integer, Integer> slotPosition = (row1, slot) -> (row1.getSlotAmount() / 9) * 9 + slot - 9 - 1;

    public int getRelativeSlot(int slot) {
        return this.slotPosition.apply(this, slot);
    }
}
