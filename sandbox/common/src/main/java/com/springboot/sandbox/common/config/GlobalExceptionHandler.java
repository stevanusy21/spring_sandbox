package com.springboot.sandbox.common.config;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.sandbox.common.dto.ErrorResponse;
import com.springboot.sandbox.common.exception.BadRequestException;
import com.springboot.sandbox.common.exception.InternalServerException;
import com.springboot.sandbox.common.exception.NotFoundException;

import feign.FeignException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
        private final ObjectMapper objectMapper;

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(InternalServerException.class)
        public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                // Menggabungkan semua pesan error per field, contoh: "username: must not be
                // blank, email: must not be blank"
                String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining(", "));
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                errorMessage,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Catch invalid Query/Path parameters
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                String.format("The parameter '%s' has an invalid value.", ex.getName()),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Catch invalid JSON request body fields
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "Invalid input provided. Please check your data fields.",
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(FeignException.class)
        public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex, HttpServletRequest request) {
                HttpStatus status = HttpStatus.resolve(ex.status());
                if (status == null) {
                        status = HttpStatus.INTERNAL_SERVER_ERROR;
                }

                String message = ex.getMessage();
                String responseBody = ex.contentUTF8();

                if(responseBody != null && !responseBody.isBlank()){
                        try{
                                JsonNode rootNode = objectMapper.readTree(responseBody);
                                if (rootNode.has("message") && !rootNode.get("message").isNull()){
                                        message = rootNode.get("message").asText();
                                } else {
                                        message = responseBody;
                                }
                        }catch(Exception e){
                                message = responseBody;
                        }
                }

                ErrorResponse errorResponse = new ErrorResponse(
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, status);
        }
}