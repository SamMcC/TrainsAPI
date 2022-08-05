package com.bjss.trainsapi.model.persistence;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;

@Getter
@Builder
@EqualsAndHashCode
public class Coordinates {
    @Builder.Default
    private short x = (short) RandomUtils.nextInt();

    @Builder.Default
    private short y = (short) RandomUtils.nextInt();

    public double distanceToCoordinate(Coordinates coordinates) {
        int maxX = Integer.max(x, coordinates.getX());
        int minX = Integer.min(x, coordinates.getX());
        int maxY = Integer.max(y, coordinates.getY());
        int minY = Integer.min(y, coordinates.getY());
        return Math.sqrt((maxX - minX) ^ 2) + ((maxY - minY) ^ 2);
    }
}
