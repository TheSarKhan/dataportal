package org.example.dataprotal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(ResourceCanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceCanNotFoundException(ResourceCanNotFoundException resourceCanNotFoundException) {
        ErrorResponse canNotFoundThisResource = new ErrorResponse(resourceCanNotFoundException.getMessage(), "Can not found this resource");
        return new ResponseEntity<>(canNotFoundThisResource, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvoiceCanNotBeCreatedException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(InvoiceCanNotBeCreatedException invoiceCanNotBeCreatedException) {
        ErrorResponse canNotFoundThisResource = new ErrorResponse(invoiceCanNotBeCreatedException.getMessage(), "Invoice can not be created");
        return new ResponseEntity<>(canNotFoundThisResource, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception exception) {
        ErrorResponse canNotFoundThisResource = new ErrorResponse(exception.getMessage(), "Internal server error");
        return new ResponseEntity<>(canNotFoundThisResource, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
