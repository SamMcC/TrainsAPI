package com.bjss.trainsapi.model.persistence;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class CoordinatesConverterTest {

    private final CoordinatesConverter coordinatesConverter = new CoordinatesConverter();

    @Test
    public void testConvertToDatabaseColumn_shouldReturnCommaSeparatedString_whenCoordinatesValid(){
        Coordinates coordinates = Coordinates.builder().build();
        String convertedValue = coordinatesConverter.convertToDatabaseColumn(coordinates);
        assertThat(convertedValue).containsOnlyOnce(",");
        assertThat(convertedValue.split(",")).extracting(Short::valueOf).containsExactly(coordinates.getX(), coordinates.getY());
    }

    @Test
    public void testConvertToEntityAttribute_shouldReturnCoordinates_whenStringValid() {
        short x = (short) RandomUtils.nextInt();
        short y = (short) RandomUtils.nextInt();
        String coordinateString = x + "," + y;
        Coordinates actualValue = coordinatesConverter.convertToEntityAttribute(coordinateString);
        assertThat(actualValue).isEqualTo(Coordinates.builder().x(x).y(y).build());
    }

    @ParameterizedTest
    @ValueSource(strings = {"111", "111,111,111", "", "abc,123", "1.1,1.2"})
    public void testConvertToEntityAttribute_shouldThrowRuntimeException_whenStringInvalid(String coordinateString) {
        RuntimeException exception = catchThrowableOfType(() -> coordinatesConverter.convertToEntityAttribute(coordinateString), RuntimeException.class);
        assertThat(exception).isNotNull().hasMessage("Attempted to map bad coordinates");
    }
}
