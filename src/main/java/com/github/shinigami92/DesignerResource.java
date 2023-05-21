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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

class DesignElement {
    public float x;
    public float y;
    public String fontFamily;
    public float fontSize;
    public String text;
    public String color;
}

@Path("/api/v1/designer")
public class DesignerResource {
    @Path("/example.png")
    @GET
    public Response exampleImage() throws IOException {
        List<DesignElement> elements = new ArrayList<>();

        elements.add(new DesignElement() {
            {
                x = 0.1f;
                y = 0.2f;
                fontFamily = "Arial";
                fontSize = 0.15f;
                text = "Jane";
                color = "#0000FF";
            }
        });
        elements.add(new DesignElement() {
            {
                x = 0.1f;
                y = 0.4f;
                fontFamily = "Arial";
                fontSize = 0.15f;
                text = "Doe";
                color = "#0000FF";
            }
        });

        // https://en.wikipedia.org/wiki/ISO/IEC_7810
        double widthInInch = 3.370; // 85.60 mm
        double heightInInch = 2.125; // 53.98 mm
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

        for (DesignElement element : elements) {
            Font font = new Font(element.fontFamily, Font.PLAIN, Math.round(element.fontSize * dpi));

            g2d.setColor(Color.decode(element.color));
            g2d.setFont(font);
            g2d.drawString(element.text, Math.round(element.x * dpi), Math.round(element.y * dpi));
        }

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageData = baos.toByteArray();

        return Response.ok(imageData, "image/png").build();
    }
}
