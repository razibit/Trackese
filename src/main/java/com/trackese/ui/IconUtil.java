package com.trackese.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for creating and loading icons.
 */
public class IconUtil {
    private static final String ICONS_DIRECTORY = "icons";
    
    /**
     * Initialize icons for the application.
     * This method creates icon files if they don't exist.
     */
    public static void initializeIcons() {
        // Create icons directory if it doesn't exist
        File iconsDir = new File(ICONS_DIRECTORY);
        if (!iconsDir.exists()) {
            iconsDir.mkdirs();
        }
        
        // Create check icon
        createCheckIcon();
        
        // Create X icon
        createXIcon();
    }
    
    /**
     * Create a check mark icon.
     */
    private static void createCheckIcon() {
        File checkFile = new File(ICONS_DIRECTORY + File.separator + "check.png");
        if (checkFile.exists()) {
            return;
        }
        
        // Create a simple check icon
        ImageIcon checkIcon = createSimpleCheckIcon();
        saveImageIconToFile(checkIcon, checkFile);
    }
    
    /**
     * Create an X icon.
     */
    private static void createXIcon() {
        File xFile = new File(ICONS_DIRECTORY + File.separator + "x.png");
        if (xFile.exists()) {
            return;
        }
        
        // Create a simple X icon
        ImageIcon xIcon = createSimpleXIcon();
        saveImageIconToFile(xIcon, xFile);
    }
    
    /**
     * Create a simple check mark icon.
     * 
     * @return The check mark icon
     */
    private static ImageIcon createSimpleCheckIcon() {
        int size = 24;
        Image image = createEmptyImage(size);
        
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a green checkmark
        g2d.setStroke(new BasicStroke(3f));
        g2d.setColor(new Color(0, 150, 0));
        
        int[] xPoints = {4, 10, 20};
        int[] yPoints = {12, 20, 5};
        g2d.drawPolyline(xPoints, yPoints, 3);
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Create a simple X icon.
     * 
     * @return The X icon
     */
    private static ImageIcon createSimpleXIcon() {
        int size = 24;
        Image image = createEmptyImage(size);
        
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a red X
        g2d.setStroke(new BasicStroke(3f));
        g2d.setColor(new Color(200, 0, 0));
        
        g2d.drawLine(4, 4, size - 4, size - 4);
        g2d.drawLine(size - 4, 4, 4, size - 4);
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Create an empty image with a transparent background.
     * 
     * @param size The size of the image
     * @return The empty image
     */
    private static Image createEmptyImage(int size) {
        Image image = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(new Color(0, 0, 0, 0)); // Transparent
        g2d.fillRect(0, 0, size, size);
        g2d.dispose();
        return image;
    }
    
    /**
     * Save an image icon to a file.
     * 
     * @param icon The image icon
     * @param file The file
     */
    private static void saveImageIconToFile(ImageIcon icon, File file) {
        try {
            Image image = icon.getImage();
            
            // Convert to buffered image
            java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
                    image.getWidth(null), 
                    image.getHeight(null), 
                    java.awt.image.BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            
            // Save image
            javax.imageio.ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the check icon.
     * 
     * @return The check icon or null if it doesn't exist
     */
    public static ImageIcon getCheckIcon() {
        File checkFile = new File(ICONS_DIRECTORY + File.separator + "check.png");
        if (!checkFile.exists()) {
            return createSimpleCheckIcon();
        }
        
        return new ImageIcon(checkFile.getAbsolutePath());
    }
    
    /**
     * Get the X icon.
     * 
     * @return The X icon or null if it doesn't exist
     */
    public static ImageIcon getXIcon() {
        File xFile = new File(ICONS_DIRECTORY + File.separator + "x.png");
        if (!xFile.exists()) {
            return createSimpleXIcon();
        }
        
        return new ImageIcon(xFile.getAbsolutePath());
    }
} 