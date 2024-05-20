import javax.swing.*;
import java.awt.*;

public class HighQualityImageIcon extends ImageIcon {
    public HighQualityImageIcon(String url) {
        super(url);
    }

    public void resizeIcon(int width, int height) {
        Image resized = getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        setImage(resized);
    }

    public void resizeIcon(Dimension size) {
        resizeIcon(size.width, size.height);
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        // Making the image render more smoothly
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Painting the image
        if (getImageObserver() == null) {
            g2d.drawImage(getImage(), x, y, c);
        } else {
            g2d.drawImage(getImage(), x, y, getImageObserver());
        }
    }
}
