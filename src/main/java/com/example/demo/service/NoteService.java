package com.example.demo.service;

import com.example.demo.dto.NoteRequestDto;
import com.example.demo.entity.Note;
import com.example.demo.entity.SubscriptionPlan; // ✅ Correct import for the enum
import com.example.demo.entity.Tenant;
import com.example.demo.entity.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.TenantRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, TenantRepository tenantRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all notes for a given tenant.
     * @param tenantId The ID of the tenant.
     * @return A list of notes belonging to the tenant.
     */
    public List<Note> getNotesForTenant(Long tenantId) {
        return noteRepository.findByTenantId(tenantId);
    }

    /**
     * Creates a new note for a tenant, respecting subscription limits.
     * @param tenantId The ID of the tenant.
     * @param userId The ID of the user creating the note.
     * @param noteRequest The data for the new note.
     * @return The created Note entity.
     * @throws RuntimeException if tenant is not found, or if the Free plan limit is reached.
     */
    @Transactional
    public Note createNote(Long tenantId, Long userId, NoteRequestDto noteRequest) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        // Check subscription limit for FREE plan
        long noteCount = noteRepository.countByTenantId(tenantId);
        // This comparison is now valid because SubscriptionPlan is correctly imported
        if (tenant.getPlan() == SubscriptionPlan.FREE && noteCount >= 3) {
            throw new RuntimeException("Note limit reached for Free plan. Please upgrade.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note newNote = new Note();
        newNote.setTitle(noteRequest.getTitle());
        newNote.setContent(noteRequest.getContent());
        newNote.setTenant(tenant);
        newNote.setUser(user);
        newNote.setCreatedAt(LocalDateTime.now());
        newNote.setUpdatedAt(LocalDateTime.now());

        return noteRepository.save(newNote);
    }

    /**
     * Retrieves a specific note by its ID and verifies it belongs to the tenant.
     * @param userId The ID of the note.
     * @param tenantId The ID of the tenant.
     * @return An Optional containing the Note if found and owned by the tenant, otherwise empty.
     */
    public List<Note> getNoteByIdAndTenantId(Long userId, Long tenantId) {
    return noteRepository.findAllByUserId(userId).stream()
            .filter(note -> note.getTenant() != null && note.getTenant().getId().equals(tenantId))
            .toList();
}

    /**
     
     */
   @Transactional
public Note updateNoteByUser(Long userId, Long tenantId, Long noteId, NoteRequestDto noteRequest) {
    Note note = getNoteByIdAndTenantId(userId, tenantId).stream()
            .filter(n -> n.getId().equals(noteId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Note not found for this user and tenant."));

    note.setTitle(noteRequest.getTitle());
    note.setContent(noteRequest.getContent());
    note.setUpdatedAt(LocalDateTime.now());

    return noteRepository.save(note);
}



   @Transactional
public void deleteNoteByUser(Long userId, Long tenantId, Long noteId) {
    Note note = getNoteByIdAndTenantId(userId, tenantId).stream()
            .filter(n -> n.getId().equals(noteId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Note not found for this user and tenant."));

    noteRepository.delete(note);
}


}