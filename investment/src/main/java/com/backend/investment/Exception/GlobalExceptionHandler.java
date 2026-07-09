package com.backend.investment.Exception;

import com.backend.investment.common.ApiResponse;
import com.backend.investment.common.ConstantResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex) {

        ApiResponse<Object> response = new ApiResponse<>();

        response.setSuccess(false);
        response.setCode(ConstantResponse.BAD_REQUEST);
        response.setMessage(ex.getMessage());
        response.setData(null);

        return ResponseEntity.badRequest().body(response);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex) {

        ApiResponse<Object> response = new ApiResponse<>();

        response.setSuccess(false);
        response.setCode(ConstantResponse.NOT_FOUND);
        response.setMessage(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception ex) {

        ApiResponse<Object> response = new ApiResponse<>();

        response.setSuccess(false);
        response.setCode(ConstantResponse.INTERNAL_SERVER_ERROR);
        response.setMessage(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);

    }

}