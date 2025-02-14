    package com.library.online_library.exception;

    import java.util.HashMap;
    import java.util.Map;

    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.FieldError;
    import org.springframework.web.bind.MethodArgumentNotValidException;
    import org.springframework.web.bind.annotation.ControllerAdvice;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.ResponseStatus;

    import io.swagger.v3.oas.annotations.Hidden;

    @Hidden // needed for swagger to accessible 
    @ControllerAdvice
    public class GlobalExceptionHandler {

        public GlobalExceptionHandler() {
        }

        //  1. Handle resource not found (404)
        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Resource Not Found");
            response.put("message", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        //  2. Handle validation errors (400 bad request)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        //  3. Handle AI API errors
        @ExceptionHandler(AiApiException.class)
        @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
        public ResponseEntity<Map<String, String>> handleAiApiException(AiApiException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "AI Service   Unavailable");
            response.put("message", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 4. Handle generic exceptions (500 internal server error)
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
