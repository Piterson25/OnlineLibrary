package com.onlinelibrary.OnlineLibrary.controllersAPI;

import com.onlinelibrary.OnlineLibrary.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/stats")
public class StatsApiController {

    private final StatsService statsService;

    @Autowired
    public StatsApiController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/get_top_rated/{number}")
    public ResponseEntity<?> getTopRatedBooks(@PathVariable int number) {
        try {
            return new ResponseEntity<>(this.statsService.getTopRatedBooks(number), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get_average_rating")
    public ResponseEntity<?> getAverageRating() {
        try {
            return new ResponseEntity<>(Math.round(this.statsService.findAverageRating() * 10.0) / 10.0, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get_top_authors/{number}")
    public ResponseEntity<?> getTopAuthors(@PathVariable int number) {
        try {
            return new ResponseEntity<>(this.statsService.findTopAuthors(number), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get_top_genres/{number}")
    public ResponseEntity<?> getTopGenres(@PathVariable int number) {
        try {
            return new ResponseEntity<>(this.statsService.findTopGenres(number), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get_oldest_book")
    public ResponseEntity<?> getOldestBook() {
        try {
            return new ResponseEntity<>(this.statsService.findOldestBook(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get_newest_book")
    public ResponseEntity<?> getNewestBook() {
        try {
            return new ResponseEntity<>(this.statsService.findNewestBook(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HashMap<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HashMap<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMsg = error.getDefaultMessage();
            errors.put(fieldName, errorMsg);
        });
        return errors;
    }
}
