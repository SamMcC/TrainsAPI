package com.bjss.trainsapi.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class HandledExceptionResponseDto {
    private String error;
    private String message;
}
