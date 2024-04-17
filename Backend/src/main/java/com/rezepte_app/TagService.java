package com.rezepte_app;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // Methode zum Hinzufügen eines neuen Tags, falls es noch nicht existiert
    @Transactional
    public Tag addTag(Tag tag) {
        try {
            // Überprüfe, ob ein Tag mit dem gleichen Namen bereits existiert
            Optional<Tag> existingTag = tagRepository.findByLabelAndSeverity(tag.getLabel(), tag.getSeverity());

            if (existingTag.isPresent()) {
                // Ein Tag mit dem gleichen Namen existiert bereits, gib es zurück
                return existingTag.get();
            } else {
                // Speichere den neuen Tag, da er eindeutig ist
                return tagRepository.save(tag);
            }
        } catch (DataAccessException e) {
            // Log the exception, handle it, or rethrow it
            throw new ServiceException("Error adding tag", e);
        }
    }

    public List<Tag> saveTags(List<Tag> tags) {
        List<Tag> savedTags = new ArrayList<>();
        for (Tag tag : tags) {
            // Überprüfen, ob das Tag bereits in der Datenbank vorhanden ist
            Optional<Tag> existingTagOptional = tagRepository.findByLabelAndSeverity(tag.getLabel(), tag.getSeverity());
            if (existingTagOptional.isPresent()) {
                // Wenn das Tag bereits existiert, fügen Sie es zur Liste der gespeicherten Tags hinzu
                savedTags.add(existingTagOptional.get());
            } else {
                // Wenn das Tag neu ist, speichern Sie es in der Datenbank und fügen Sie es zur Liste der gespeicherten Tags hinzu
                savedTags.add(tagRepository.save(tag));
            }
        }
        return savedTags;
    }

    // Methode zum Entfernen eines Tags
    @Transactional
    public void removeTag(Long tagId) {
        try {
            tagRepository.deleteById(Math.toIntExact(tagId));
        } catch (IllegalArgumentException e) {
            // Handle invalid ID exception
            throw new ServiceException("Invalid tag ID", e);
        } catch (DataAccessException e) {
            // Handle data access exception
            throw new ServiceException("Error removing tag", e);
        }
    }



    // Methode zum Suchen eines Tags nach Namen
    public Optional<Tag> findTagByLabelAndSeverity(String label, String severity) {
        try {
            return tagRepository.findByLabelAndSeverity(label, severity);
        } catch (RuntimeException e) {
            // Log the exception, handle it, or rethrow it
            throw new ServiceException("Error finding tag by label and severity", e);
        }
    }

    // Methode zum Aktualisieren eines Tags
    @Transactional
    public Tag updateTag(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (RuntimeException e) {
            // Log the exception, handle it, or rethrow it
            throw new ServiceException("Error updating tag", e);
        }
    }
}