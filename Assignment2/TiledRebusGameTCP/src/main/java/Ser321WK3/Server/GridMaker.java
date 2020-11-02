package Ser321WK3.Server;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Creates the square image grid used in Assignment 3
 *
 * Usage: gradle Maker --args="<image to slice> <size>"
 */
public class GridMaker {

    private static final List<BufferedImage> croppedImages = new ArrayList<>();

    /**
     * Crops an image to the specified region. Creates a single image cell.
     *
     * @param bufferedImage the image that will be cropped
     * @param x             the upper left x coordinate that this region will start
     * @param y             the upper left y coordinate that this region will start
     * @param width         the width of the region that will be crop
     * @param height        the height of the region that will be crop
     * @return the image that was cropped
     *
     * https://javapointers.com/java/java-core/crop-image-java/
     */
    public static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height) throws IOException {
        return bufferedImage.getSubimage(x, y, width, height);
    }

    /**
     * Resize a buffered image to a specific size
     *
     * @param image  to resize
     * @param width  of result image
     * @param height of result image
     * @return resized image
     */
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        if (width < 1 || height < 1) {
            return null;
        }
        // create output buffer
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        // draw at new size
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static List<BufferedImage> getCroppedImages() {
        return croppedImages;
    }

    public static List<BufferedImage> main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Maker --args\"<path to image to slice> <size : int>\"");
            System.exit(0);
        }
        String filename = args[0];
        File file = new File(filename);
        //extract the different path pieces - see below for definition
        FancyPath path = new FancyPath(file);
        if (!file.exists()) {
            System.err.println("Cannot find file: " + file.getAbsolutePath());
            System.exit(-1);
        }

        int dimension = Integer.parseInt(args[1]);

        // Read in image and adjust
        BufferedImage img = ImageIO.read(file);
        int divisibleHeight = img.getHeight() - (img.getHeight() % dimension);
        int divisibleWidth = img.getWidth() - (img.getWidth() % dimension);
        img = resize(img, divisibleWidth, divisibleHeight);

        // calculate crop size
        int cellHeight = divisibleHeight / dimension;
        int cellWidth = divisibleWidth / dimension;

        // for each crop section
        for (int r = 0; r < dimension; ++r) {
            for (int c = 0; c < dimension; ++c) {
                // crop and output
                BufferedImage output = cropImage(img, c * cellWidth, r * cellHeight, cellWidth, cellHeight);
                croppedImages.add(output);
            }
        }
        return croppedImages;
    }

    /**
     * Tokenizes and analyzes a file path to allow for manipulation
     */
    public static class FancyPath {
        // determine *nix vs Windows
        private String delimiter;
        // whole original path
        private String absolutePath;
        // path up to file
        private String folderPath;
        // specific file name
        private String filename;
        // specific file extension
        private String extension;

        public FancyPath(File file) {
            absolutePath = file.getAbsolutePath();
            // *nix or windows?
            delimiter = absolutePath.startsWith("/") ? "/" : "\\";

            folderPath = absolutePath.substring(0, absolutePath.lastIndexOf(delimiter) + 1);

            String filenameWithExt = absolutePath.substring(absolutePath.lastIndexOf(delimiter) + 1);

            int lastPeriod = filenameWithExt.lastIndexOf('.');
            if (lastPeriod > 0) { // has extension?
                // separate extension from filename
                filename = filenameWithExt.substring(0, lastPeriod);
                extension = filenameWithExt.substring(lastPeriod + 1);
            } else {
                // no extension
                filename = filenameWithExt;
                extension = "";
            }
        }

        /**
         * Gets filename
         *
         * @return filename
         */
        public String getFilename() {
            return filename;
        }

        /**
         * Sets filename
         *
         * @param newFilename to set to filename
         */
        public void setFilename(String newFilename) {
            filename = newFilename;
        }

        /**
         * Gets file extension
         *
         * @return file extension
         */
        public String getExtension() {
            return extension;
        }

        /**
         * Sets file extension
         *
         * @param newExtension to set file extension
         */
        public void setExtension(String newExtension) {
            extension = newExtension;
        }

        /**
         * Get built file and folder path
         */
        @Override
        public String toString() {
            return folderPath + filename + "." + extension;
        }
    }
}
