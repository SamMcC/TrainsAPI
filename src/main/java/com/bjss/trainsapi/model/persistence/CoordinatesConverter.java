package com.bjss.trainsapi.model.persistence;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class CoordinatesConverter implements AttributeConverter<Coordinates, String> {

    @Override
    public String convertToDatabaseColumn(Coordinates coordinates) {
        return coordinates.getX() + "," + coordinates.getY();
    }

    @Override
    public Coordinates convertToEntityAttribute(String string) {
        try {
            List<Short> coordinates = Arrays.stream(string.split(","))
                .map(Short::valueOf)
                .collect(Collectors.toList());
            if (coordinates.size() != 2) {
                throw new RuntimeException("Attempted to map bad coordinates");
            }
            return Coordinates.builder()
                    .x(coordinates.get(0))
                    .y(coordinates.get(1))
                    .build();
        } catch (NumberFormatException ignored){
            throw new RuntimeException("Attempted to map bad coordinates");
        }
    }
}
