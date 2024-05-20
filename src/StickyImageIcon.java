import javax.swing.*;
import java.awt.*;

public class StickyImageIcon extends ImageIcon {
    public StickyImageIcon(String url) {
        super(url);
    }

    public void resizeIcon(int width, int height) {
        Image resized = getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        setImage(resized);
    }

    public void resizeIcon(Dimension size) {
        resizeIcon(size.width, size.height);
    }
}
