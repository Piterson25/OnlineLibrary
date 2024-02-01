package com.onlinelibrary.OnlineLibrary.repository;

import com.onlinelibrary.OnlineLibrary.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
