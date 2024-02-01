package com.onlinelibrary.OnlineLibrary.service;

import com.onlinelibrary.OnlineLibrary.domain.Comment;
import com.onlinelibrary.OnlineLibrary.repository.CommentRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@Service
public class CommentService {
    private final CommentRepository repository;

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public List<Comment> getAllComments() {
        return this.repository.findAll();
    }

    public void addComment(@Valid Comment comment) {
        this.repository.save(comment);
    }

    public Comment getCommentById(Long id) {
        return this.repository.findById(id).orElse(null);
    }

    public void deleteCommentById(Long id) {
        this.repository.deleteById(id);
    }
}
