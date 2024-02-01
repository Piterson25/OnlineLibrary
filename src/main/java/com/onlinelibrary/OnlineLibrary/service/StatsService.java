package com.onlinelibrary.OnlineLibrary.service;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.repository.BookRepository;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private final BookRepository repository;

    @Autowired
    public StatsService(BookRepository repository) {
        this.repository = repository;
    }

    public Map<String, Long> findTopAuthors(int numberOfAuthors) {
        return castTupleToSortedMap(this.repository.findTopAuthors())
                .entrySet().stream()
                .limit(numberOfAuthors)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Long> findTopGenres(int numberOfGenres) {
        return castTupleToSortedMap(this.repository.findTopGenres())
                .entrySet().stream()
                .limit(numberOfGenres)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Book findOldestBook() {
        return this.repository.findOldestBook();
    }

    public Book findNewestBook() {
        return this.repository.findNewestBook();
    }

    public List<Book> getTopRatedBooks(int numberOfBooks) {
        return this.repository.findAll().stream()
                .sorted(Comparator.comparingDouble(Book::getAverageRating).reversed())
                .limit(numberOfBooks)
                .collect(Collectors.toList());
    }

    public Double findAverageRating() {
        return this.repository.findAverageRating();
    }

    private Map<String, Long> castTupleToSortedMap(List<Tuple> tuples) {
        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, String.class),
                        tuple -> tuple.get(1, Long.class),
                        (a, b) -> a,
                        LinkedHashMap::new
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
