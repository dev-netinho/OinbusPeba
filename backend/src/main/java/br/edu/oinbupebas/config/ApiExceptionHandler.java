package br.edu.oinbupebas.config;

import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import br.edu.oinbupebas.identity.application.RecoveryUnavailableException;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail invalid(MethodArgumentNotValidException ex) {
    var p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Revise os campos informados.");
    p.setType(URI.create("urn:oinbupebas:validation"));
    p.setProperty("errors", ex.getFieldErrors().stream().map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage())).toList());
    return p;
  }
  @ExceptionHandler(IllegalArgumentException.class)
  ProblemDetail illegal(IllegalArgumentException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()); }
  @ExceptionHandler(RecoveryUnavailableException.class)
  ProblemDetail recoveryUnavailable(RecoveryUnavailableException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()); }
  @ExceptionHandler(OptimisticConflictException.class)
  ProblemDetail conflict(OptimisticConflictException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()); }
}
