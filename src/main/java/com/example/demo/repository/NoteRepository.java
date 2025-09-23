package com.example.demo.repository;

import com.example.demo.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByTenantId(Long tenantId);
     List<Note> findAllByUserId(Long userId);
    int countByTenantId(Long tenantId);
}