package com.ldb.lms.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data; // 필요 없다면 null 가능
    
    public static <T> ResponseEntity<ApiResponseDto<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponseDto<>(true, "성공", data));
    }

    public static <T> ResponseEntity<ApiResponseDto<T>> fail(String message) {
        return ResponseEntity.ok(new ApiResponseDto<>(false, message, null));
    }

    public static <T> ResponseEntity<ApiResponseDto<T>> error(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, message, null));
    }
}
