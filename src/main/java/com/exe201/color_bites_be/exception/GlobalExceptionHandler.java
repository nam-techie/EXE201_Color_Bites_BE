package com.exe201.color_bites_be.exception;

import com.exe201.color_bites_be.dto.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
// Xóa import jakarta.persistence.EntityNotFoundException
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Xử lý ngoại lệ MethodArgumentNotValidException (lỗi validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException exception) {
        StringBuilder message = new StringBuilder("Lỗi xác thực: ");

        // Lấy thông báo lỗi từ exception và xây dựng chuỗi thông báo
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getDefaultMessage()).append(". ");
        }
        return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
    }

    // Xóa hoặc comment EntityNotFoundException handler
    // @ExceptionHandler(EntityNotFoundException.class)
    // public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
    //     return new ResponseEntity<>("Không tìm thấy dữ liệu: " + exception.getMessage(), HttpStatus.NOT_FOUND);
    // }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        return new ResponseEntity<>("Lỗi hệ thống: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Xử lý ngoại lệ ExpiredJwtException (lỗi JWT hết hạn)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException exception) {
        return new ResponseEntity<>("Token đã hết hạn, vui lòng đăng nhập lại.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(FuncErrorException.class)
    public ResponseEntity<String> handleFuncErrorException(FuncErrorException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
