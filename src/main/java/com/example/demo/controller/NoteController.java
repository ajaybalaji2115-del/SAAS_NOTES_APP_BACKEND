package com.example.demo.controller;

import com.example.demo.dto.NoteRequestDto;
import com.example.demo.dto.NoteResponseDto;
import com.example.demo.entity.Note;
import com.example.demo.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    // Helper method to get the tenantId from the request
    private Long getTenantId(HttpServletRequest request) {
        return (Long) request.getAttribute("tenantId");
    }
     private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getNotes(HttpServletRequest request) {
        Long tenantId = getTenantId(request);
        List<Note> notes = noteService.getNotesForTenant(tenantId);
        List<NoteResponseDto> dtos = notes.stream()
                .map(note -> {
                    NoteResponseDto dto = new NoteResponseDto();
                    dto.setId(note.getId());
                    dto.setTitle(note.getTitle());
                    dto.setContent(note.getContent());
                    dto.setCreatedAt(note.getCreatedAt());
                    dto.setUpdatedAt(note.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(@RequestBody NoteRequestDto noteRequest, HttpServletRequest request) {
        Long tenantId = getTenantId(request);
      Long userId = getUserId(request);


        try {
            Note createdNote = noteService.createNote(tenantId, userId, noteRequest);
            // Convert Note entity to NoteResponseDto before sending
            NoteResponseDto dto = new NoteResponseDto();
            dto.setId(createdNote.getId());
            dto.setTitle(createdNote.getTitle());
            dto.setContent(createdNote.getContent());
            dto.setCreatedAt(createdNote.getCreatedAt());
            dto.setUpdatedAt(createdNote.getUpdatedAt());
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

@GetMapping("/{userId}")
public ResponseEntity<List<NoteResponseDto>> getNotesByUserId(
        @PathVariable Long userId, HttpServletRequest request) {

    Long tenantId = getTenantId(request);

    List<NoteResponseDto> notes = noteService.getNoteByIdAndTenantId(userId, tenantId)
            .stream()
            .map(note -> {
                NoteResponseDto dto = new NoteResponseDto();
                dto.setId(note.getId());
                dto.setTitle(note.getTitle());
                dto.setContent(note.getContent());
                dto.setCreatedAt(note.getCreatedAt());
                dto.setUpdatedAt(note.getUpdatedAt());
                return dto;
            })
            .toList();

    if (notes.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(notes);
}

   @PutMapping("/user/{userId}/note/{noteId}")
public ResponseEntity<?> updateNote(
        @PathVariable Long userId,
        @PathVariable Long noteId,
        @RequestBody NoteRequestDto noteRequest,
        HttpServletRequest request) {

    Long tenantId = getTenantId(request);

    try {
        Note updatedNote = noteService.updateNoteByUser(userId, tenantId, noteId, noteRequest);

        NoteResponseDto dto = new NoteResponseDto();
        dto.setId(updatedNote.getId());
        dto.setTitle(updatedNote.getTitle());
        dto.setContent(updatedNote.getContent());
        dto.setCreatedAt(updatedNote.getCreatedAt());
        dto.setUpdatedAt(updatedNote.getUpdatedAt());

        return ResponseEntity.ok(dto);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}



@DeleteMapping("/user/{userId}/note/{noteId}")
public ResponseEntity<?> deleteNote(
        @PathVariable Long userId,
        @PathVariable Long noteId,
        HttpServletRequest request) {

    Long tenantId = getTenantId(request);

    try {
        noteService.deleteNoteByUser(userId, tenantId, noteId);
        return ResponseEntity.noContent().build();

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

}