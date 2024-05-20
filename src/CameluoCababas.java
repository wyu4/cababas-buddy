import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CameluoCababas extends JButton implements ActionListener {
    private static final String CABABAS_STICKER_LEFT = "src/Resources/CababasLeft.png";
    private static final String CABABAS_STICKER_RIGHT = "src/Resources/CababasRight.png";
    private static final int FPS = 240; // FPS of animation
    private static final int JUMP_LENGTH = (Toolkit.getDefaultToolkit().getScreenSize().width/10); // Pixels to jump by
    private static final long JUMP_DURATION = 1000; // Duration of animation in milliseconds
    private static final long TOTAL_FRAMES = (long)(JUMP_DURATION / (1000.0/FPS));
    private static final double X_INCREMENT = (JUMP_LENGTH / (double) TOTAL_FRAMES);

    private final Dimension frameSize;
    private final StickyImageIcon imageIconL, imageIconR;
    private final Timer clock;
    private boolean jumping, leave;
    private double absoluteX, absoluteY, startingX, startingY;
    private long lastTime;
    private int jumpMod;
    private Point goal;

    public CameluoCababas(Dimension frameSize) {
        this.frameSize = frameSize;

        leave = false;
        jumping = false;
        jumpMod = 0;
        lastTime = System.currentTimeMillis();

        imageIconL = new StickyImageIcon(CABABAS_STICKER_LEFT);
        imageIconR = new StickyImageIcon(CABABAS_STICKER_RIGHT);

        setName("Cameluo Cababas");
        setSize((int)(frameSize.getHeight()/2), (int)(frameSize.getHeight()/2));
        setLocation(
                (int)(frameSize.getWidth() + (JUMP_LENGTH/2.0)),
                (int)(frameSize.getHeight() - (frameSize.getHeight()/2)));
        setBackground(new Color(0, 0, 0, 0));
        setForeground(new Color(0, 0, 0, 0));
        setFocusable(false);
        setBorder(null);
        setLayout(null);
        setIcon(imageIconL);
        setContentAreaFilled(false);

        goal = getLocation();
        absoluteX = getX();
        absoluteY = getY();
        startingX = getX();
        startingY = getY();

        repaint();
        revalidate();

        imageIconL.resizeIcon(getWidth(), getHeight());
        imageIconR.resizeIcon(getWidth(), getHeight());

        clock = new Timer(1000/FPS, this);
        jump();
    }

    public void jump() {
        if (!jumping && !leave) {
            jumping = true;

            jumpMod = calculateJumpMod();

            if (jumpMod > 0) {
                setIcon(imageIconR);
            } else {
                setIcon(imageIconL);
            }

            goal = new Point(
                  getX() + (jumpMod * JUMP_LENGTH),
                    (int)(frameSize.getHeight() - (frameSize.getHeight()/2))
            );

            startingX = getX();
            startingY = getY();

            lastTime = System.currentTimeMillis();
            clock.restart();
        }
    }

    public void leave() {
        if (!leave) {
            jump();
            leave = true;
        }
    }

    public int calculateJumpMod() {
        int result = 0;
        int leftX = getX();
        int rightX = getX() + getWidth();

        if ((rightX + JUMP_LENGTH) <= frameSize.getWidth()) { // Check if Cababas wil lgo out of bounds by jumping right
            result++;
        }
        if ((leftX - JUMP_LENGTH) >= 0) { // Check if Cababas will go out of bounds by jumping left
            result--;
        }

        if (result == 0) { // Cababas can jump both ways. Choose random (-1 or 1)
            double rng = Math.random();
            if (rng > 0.5) {
                return 1;
            } else {
                return -1;
            }
        }
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();

        if (e.getSource().equals(clock)) {
            long delta = (currentTime - lastTime);
            long deltaRate = (delta / (1000/FPS));
            lastTime = currentTime;

            if (jumping) {
                double pos = Math.abs(absoluteX - startingX);
                double midpoint = (JUMP_LENGTH/2.0);
                double aValue = (goal.getY()/(midpoint*midpoint));

                absoluteX += ((jumpMod * X_INCREMENT) * deltaRate);
                absoluteY = aValue * ((pos-midpoint)*(pos-midpoint));

                setLocation((int) absoluteX, (int) absoluteY);
                if (leave) {
                    if (pos >= (JUMP_LENGTH*2)) {
                        jumping = false;
                        setVisible(false);
                        clock.stop();
                        System.exit(0);
                    }
                } else {
                    if (pos >= JUMP_LENGTH) {
                        jumping = false;
                        setLocation(goal);
                        clock.stop();
                    }
                }
                repaint();
            }
        }
    }
}
