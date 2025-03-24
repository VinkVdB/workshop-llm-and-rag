package infosupport.be.plot;

import infosupport.be.util.EmbeddingManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a 2D plot of word embeddings on custom axes (X and Y)
 * and saves it as a PNG image. Uses midpoint-based centering for each axis.
 */
public class EmbeddingPlotter {

    /**
     * Renders a 2D scatter plot of the given words onto the provided anchors
     * (xLabelLeft→xLabelRight, yLabelBottom→yLabelTop) and saves the result
     * as a PNG. The "axisX" and "axisY" parameters are not used directly here;
     * we recalculate axis vectors from the anchor terms for clarity.
     *
     * @param embeddingManager manager to fetch embeddings
     * @param words            words to be plotted
     * @param width            image width (pixels)
     * @param height           image height (pixels)
     * @param xLabelLeft       left anchor for X-axis
     * @param xLabelRight      right anchor for X-axis
     * @param yLabelBottom     bottom anchor for Y-axis
     * @param yLabelTop        top anchor for Y-axis
     * @param outputFile       path to the PNG output
     */
    public void plot2DToImage(
            EmbeddingManager embeddingManager,
            List<String> words,
            int width,
            int height,
            String xLabelLeft,
            String xLabelRight,
            String yLabelBottom,
            String yLabelTop,
            Path outputFile
    ) {
        // 1) Compute anchor vectors & midpoints for each axis
        float[] xLeftVec = embeddingManager.valueOf(xLabelLeft).vector();
        float[] xRightVec = embeddingManager.valueOf(xLabelRight).vector();
        float[] xMid = averageVectors(xLeftVec, xRightVec);
        float[] xDir = normalize(subtract(xRightVec, xLeftVec));

        float[] yBottomVec = embeddingManager.valueOf(yLabelBottom).vector();
        float[] yTopVec    = embeddingManager.valueOf(yLabelTop).vector();
        float[] yMid = averageVectors(yBottomVec, yTopVec);
        float[] yDir = normalize(subtract(yTopVec, yBottomVec));

        // 2) Project each word by subtracting midpoints and dotting with each axis
        List<Point2D> points = new ArrayList<>();
        for (String word : words) {
            float[] wVec = embeddingManager.valueOf(word).vector();
            double xVal = dot(subtract(wVec, xMid), xDir);
            double yVal = dot(subtract(wVec, yMid), yDir);
            points.add(new Point2D(word, xVal, yVal));
        }

        // 3) Find min/max for plotting
        double minX = points.stream().mapToDouble(Point2D::x).min().orElse(0.0);
        double maxX = points.stream().mapToDouble(Point2D::x).max().orElse(1.0);
        double minY = points.stream().mapToDouble(Point2D::y).min().orElse(0.0);
        double maxY = points.stream().mapToDouble(Point2D::y).max().orElse(1.0);

        // Expand slightly so points near edges aren't clipped
        double xRange = Math.max(1.0, maxX - minX);
        double yRange = Math.max(1.0, maxY - minY);
        double padding = 0.05;

        minX -= xRange * padding;
        maxX += xRange * padding;
        minY -= yRange * padding;
        maxY += yRange * padding;

        // 4) Create image & draw background
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        final int MARGIN = 50;
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        String title = String.format("Word Embeddings Plot: %s-%s vs %s-%s",
                xLabelLeft, xLabelRight, yLabelBottom, yLabelTop);
        g.drawString(title, MARGIN, MARGIN - 10);

        // 5) Draw numeric axes with ticks
        drawAxesAndTicks(g, width, height, minX, maxX, minY, maxY);

        // 6) Plot the points
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (Point2D p : points) {
            int xPix = transformXToPixel(p.x(), width, minX, maxX);
            int yPix = transformYToPixel(p.y(), height, minY, maxY);
            g.setColor(new Color(0, 100, 0)); // dark green
            g.fillOval(xPix - 4, yPix - 4, 8, 8);
            g.drawString(p.label(), xPix - 4, yPix - 9);
        }

        // 7) Add axis labels at the margins
        drawAxisLabels(g, width, height, minX, maxX, minY, maxY,
                xLabelLeft, xLabelRight, yLabelBottom, yLabelTop);

        g.dispose();

        // 8) Save to PNG
        try {
            ImageIO.write(image, "png", outputFile.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save plot image: " + outputFile, e);
        }
    }

    /**
     * Draws X and Y axes (with numeric ticks) according to the data range.
     */
    private void drawAxesAndTicks(
            Graphics2D g,
            int width,
            int height,
            double minX,
            double maxX,
            double minY,
            double maxY
    ) {
        g.setColor(Color.BLACK);
        final int MARGIN = 50;

        // Find where x=0, y=0 map in the final coordinate space
        int yAxisX = transformXToPixel(0.0, width, minX, maxX);
        if (yAxisX < MARGIN || yAxisX > (width - MARGIN)) {
            yAxisX = MARGIN;
        }
        int xAxisY = transformYToPixel(0.0, height, minY, maxY);
        if (xAxisY < MARGIN || xAxisY > (height - MARGIN)) {
            xAxisY = height - MARGIN;
        }

        // Draw horizontal & vertical axis lines
        g.drawLine(MARGIN, xAxisY, width - MARGIN, xAxisY);
        g.drawLine(yAxisX, MARGIN, yAxisX, height - MARGIN);

        // Add numeric ticks
        int numTicks = 5;
        for (int i = 0; i <= numTicks; i++) {
            double fraction = i / (double) numTicks;

            // X ticks
            double xVal = minX + fraction * (maxX - minX);
            int xPix = transformXToPixel(xVal, width, minX, maxX);
            g.drawLine(xPix, xAxisY - 3, xPix, xAxisY + 3);
            g.drawString(String.format("%.2f", xVal), xPix - 10, xAxisY + 20);

            // Y ticks
            double yVal = minY + fraction * (maxY - minY);
            int yPix = transformYToPixel(yVal, height, minY, maxY);
            g.drawLine(yAxisX - 3, yPix, yAxisX + 3, yPix);
            g.drawString(String.format("%.2f", yVal), yAxisX - 40, yPix + 5);
        }
    }

    /**
     * Places text labels for the axes near the margins, rotating the X-axis labels vertically.
     */
    private void drawAxisLabels(
            Graphics2D g,
            int width,
            int height,
            double minX,
            double maxX,
            double minY,
            double maxY,
            String xLabelLeft,
            String xLabelRight,
            String yLabelBottom,
            String yLabelTop
    ) {
        final int MARGIN = 50;
        g.setColor(Color.BLACK);

        int xAxisY = transformYToPixel(0.0, height, minY, maxY);
        if (xAxisY < MARGIN || xAxisY > (height - MARGIN)) {
            xAxisY = height - MARGIN;
        }
        int yAxisX = transformXToPixel(0.0, width, minX, maxX);
        if (yAxisX < MARGIN || yAxisX > (width - MARGIN)) {
            yAxisX = MARGIN;
        }

        AffineTransform old = g.getTransform();

        // Left X-axis label
        g.translate(MARGIN / 2, xAxisY);
        g.rotate(Math.toRadians(-90));
        g.drawString(xLabelLeft, 0, 0);
        g.setTransform(old);

        // Right X-axis label
        g.translate(width - (MARGIN / 3), xAxisY);
        g.rotate(Math.toRadians(-90));
        g.drawString(xLabelRight, 0, 0);
        g.setTransform(old);

        // Bottom Y-axis label
        int yBottom = height - (MARGIN / 3);
        g.drawString(yLabelBottom, yAxisX, yBottom);

        // Top Y-axis label
        int yTop = MARGIN / 2;
        g.drawString(yLabelTop, yAxisX, yTop);
    }

    // --- Coordinate transforms ---
    private int transformXToPixel(double xVal, int width, double minX, double maxX) {
        final int MARGIN = 50;
        double scale = (width - 2.0 * MARGIN) / (maxX - minX);
        return (int) (MARGIN + (xVal - minX) * scale);
    }

    private int transformYToPixel(double yVal, int height, double minY, double maxY) {
        final int MARGIN = 50;
        double scale = (height - 2.0 * MARGIN) / (maxY - minY);
        return (int) (height - MARGIN - (yVal - minY) * scale);
    }

    // --- Vector math helpers ---
    private float[] subtract(float[] a, float[] b) {
        float[] out = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] - b[i];
        }
        return out;
    }

    private float[] averageVectors(float[] a, float[] b) {
        float[] out = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = 0.5f * (a[i] + b[i]);
        }
        return out;
    }

    private float[] normalize(float[] v) {
        double normSq = 0;
        for (float val : v) {
            normSq += val * val;
        }
        double norm = Math.sqrt(normSq);
        if (norm < 1e-12) {
            return v; // avoid divide-by-zero
        }
        float[] out = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            out[i] = (float) (v[i] / norm);
        }
        return out;
    }

    private double dot(float[] a, float[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    // Data structure to hold each plotted point
    private record Point2D(String label, double x, double y) {}
}
