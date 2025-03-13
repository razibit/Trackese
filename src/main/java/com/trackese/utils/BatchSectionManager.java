package com.trackese.utils;

import com.trackese.models.BatchSection;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing batch sections.
 */
public class BatchSectionManager {
    private static final String BATCH_SECTIONS_FILE = "batch_sections.dat";
    private static List<BatchSection> batchSections = null;

    /**
     * Get all batch sections.
     *
     * @return List of batch sections
     */
    public static List<BatchSection> getAllBatchSections() {
        if (batchSections == null) {
            loadBatchSections();
        }
        return batchSections;
    }

    /**
     * Load batch sections from file.
     */
    private static void loadBatchSections() {
        batchSections = new ArrayList<>();
        
        // Add default batch sections if no file exists
        if (!Files.exists(Paths.get(BATCH_SECTIONS_FILE))) {
            batchSections.add(new BatchSection("58", "C"));
            batchSections.add(new BatchSection("58", "D"));
            batchSections.add(new BatchSection("58", "E"));
            saveBatchSections();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BATCH_SECTIONS_FILE))) {
            batchSections = (List<BatchSection>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Add default batch sections if loading fails
            batchSections.add(new BatchSection("58", "C"));
            batchSections.add(new BatchSection("58", "D"));
            batchSections.add(new BatchSection("58", "E"));
            saveBatchSections();
        }
    }

    /**
     * Save batch sections to file.
     */
    private static void saveBatchSections() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BATCH_SECTIONS_FILE))) {
            oos.writeObject(batchSections);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new batch section.
     *
     * @param batchName Batch name
     * @param section   Section
     * @return The newly created batch section
     */
    public static BatchSection addBatchSection(String batchName, String section) {
        if (batchSections == null) {
            loadBatchSections();
        }
        
        // Check if the batch section already exists
        for (BatchSection bs : batchSections) {
            if (bs.getBatchName().equalsIgnoreCase(batchName) && bs.getSection().equalsIgnoreCase(section)) {
                return bs; // Already exists
            }
        }
        
        BatchSection newBatchSection = new BatchSection(batchName, section);
        batchSections.add(newBatchSection);
        saveBatchSections();
        
        return newBatchSection;
    }

    /**
     * Delete a batch section.
     *
     * @param batchSection The batch section to delete
     */
    public static void deleteBatchSection(BatchSection batchSection) {
        if (batchSections == null) {
            loadBatchSections();
        }
        
        batchSections.remove(batchSection);
        saveBatchSections();
    }
} 