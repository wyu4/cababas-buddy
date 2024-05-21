import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class HighQualityImageIcon extends ImageIcon {
    private boolean mirrored;

    public HighQualityImageIcon(String url) {
        super(url);
        mirrored = false;
    }

    public HighQualityImageIcon(Image image) {
        super(image);
        mirrored = false;
    }

    public void resizeIcon(int width, int height) {
        Image resized = getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        setImage(resized);
    }

    public void resizeIcon(Dimension size) {
        resizeIcon(size.width, size.height);
    }

    public void setMirrored(boolean mirror) {
        boolean actuallyMirror = !(mirrored == mirror);

        mirrored = mirror;
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        setImageObserver(c);

        // Making the image render more smoothly
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Painting the image
        g2d.drawImage(getImage(), 0, 0, c.getWidth(), c.getHeight(), c);
    }
}