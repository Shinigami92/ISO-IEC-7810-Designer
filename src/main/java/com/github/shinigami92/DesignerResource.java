package com.github.shinigami92;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/designer")
public class DesignerResource {
    @Path("/example.png")
    @GET
    public Response exampleImage() throws IOException {
        // https://en.wikipedia.org/wiki/ISO/IEC_7810
        double widthInInch = 3.370;
        double heightInInch = 2.125;
        double cornerRadiusInInch = 0.125;

        // https://myaurochs.com/blogs/news/whats-a-credit-cards-size
        int dpi = 300;

        int x = 0;
        int y = 0;
        int width = (int) Math.round(widthInInch * dpi);
        int height = (int) Math.round(heightInInch * dpi);
        int cornerRadius = (int) Math.round(cornerRadiusInInch * dpi);

        ImagePlus image = IJ.createImage(
                "Example",
                width,
                height,
                1,
                24);

        ImageProcessor ip = image.getProcessor();

        // set background color
        ip.setColor(Color.BLACK);
        ip.fill();

        BufferedImage bufferedImage = ip.getBufferedImage();

        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D roundedRect = new RoundRectangle2D.Double(
                x,
                y,
                width - 1,
                height - 1,
                cornerRadius,
                cornerRadius);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(roundedRect);
        g2d.setColor(Color.WHITE);
        g2d.fill(roundedRect);

        Font fontArial = new Font("Arial", Font.PLAIN, Math.round(0.15f * dpi));

        g2d.setColor(Color.BLUE);
        g2d.setFont(fontArial);
        g2d.drawString("Jane", Math.round(0.1 * dpi), Math.round(0.2 * dpi));

        g2d.setColor(Color.BLUE);
        g2d.setFont(fontArial);
        g2d.drawString("Doe", Math.round(0.1 * dpi), Math.round(0.4 * dpi));

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageData = baos.toByteArray();

        return Response.ok(imageData, "image/png").build();
    }
}
