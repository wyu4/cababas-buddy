import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CameluoCababas extends JButton implements ActionListener {
    private static final int FPS = 1000; // FPS of animation
    private static final int JUMP_LENGTH = (Toolkit.getDefaultToolkit().getScreenSize().width/10); // Pixels to jump by
    private static final long JUMP_DURATION = 1000; // Duration of animation in milliseconds
    private static final long TOTAL_FRAMES = (long)(JUMP_DURATION / (1000.0/FPS));
    private static final double X_INCREMENT = (JUMP_LENGTH / (double) TOTAL_FRAMES);

    private final Dimension frameSize;
    private final HighQualityImageIcon imageIconL, imageIconR;
    private final Timer clock;
    private boolean jumping, leave, squishing, landing;
    private double absoluteX, absoluteY, absoluteWidth, absoluteHeight, startingX, startingWidth, startingHeight, squishPos, landPos;
    private long lastTime;
    private int jumpMod;
    private Point goal;

    public CameluoCababas(Dimension frameSize) {
        this.frameSize = frameSize;

        leave = false;
        jumping = false;
        squishing = false;
        landing = false;
        jumpMod = 0;
        squishPos = 0;
        landPos = 0;
        lastTime = System.currentTimeMillis();

        imageIconL = new HighQualityImageIcon("src\\Resources\\CababasLeft.png");
        imageIconR = new HighQualityImageIcon("src\\Resources\\CababasRight.png");

        setName("Cameluo Cababas");
        setSize((int)calculateDimension(), (int)calculateDimension());
        setLocation(
                (int)(frameSize.getWidth() + (JUMP_LENGTH/2.0)),
                (int)(frameSize.getHeight() - getHeight())
        );
//        setLocation(
//                (int)(Math.random() * (frameSize.getWidth() - getWidth())),
//                (int)(frameSize.getHeight() - (frameSize.getHeight()/2)));
        setBackground(new Color(0, 0, 0));
        setForeground(new Color(0, 0, 0));
        setFocusable(false);
        setBorder(null);
        setLayout(null);
        setIcon(imageIconL);
         setContentAreaFilled(false);

        goal = getLocation();
        absoluteX = getX();
        absoluteY = getY();
        absoluteWidth = getWidth();
        absoluteHeight = getHeight();
        saveAbsoluteValues();

        repaint();
        revalidate();

        imageIconL.resizeIcon(getWidth(), getHeight());
        imageIconR.resizeIcon(getWidth(), getHeight());

        clock = new Timer(1000/FPS, this);
        jump();
    }

    public void jump() {
        if (!jumping && !leave && !squishing) {
            squishing = true;

            jumpMod = calculateJumpMod();

            if (jumpMod > 0) {
//                imageIcon.setMirrored(true);
                setIcon(imageIconR);
            } else {
//                imageIcon.setMirrored(false);
                setIcon(imageIconL);
            }
            setSize((int)calculateDimension(), (int)calculateDimension());
            setLocation(getX(), goal.y);

            goal = new Point(
                  getX() + (jumpMod * JUMP_LENGTH),
                    (int)(frameSize.getHeight() - getHeight())
            );

            absoluteX = getX();
            absoluteY = getY();
            absoluteWidth = getWidth();
            absoluteHeight = getHeight();
            saveAbsoluteValues();

            lastTime = System.currentTimeMillis();
            clock.restart();
        }
    }

    private void saveAbsoluteValues() {
        startingX = getX();
        startingWidth = getWidth();
        startingHeight = getHeight();
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

    private double calculateQuadraticY(double currentX, double startingX, double goalX, double startingY, double goalY, boolean log) {
        double h = (startingX + goalX)/2.0;
        double k = goalY;
        double a = (startingY - k) / Math.pow((startingX - h), 2);

        double result = ( (a * Math.pow((currentX-h), 2)) + k );

        if (log) {
            System.out.println(result + " = " + a + "(" + currentX + " - " + h + ")^2 + " + k);
        }

        return result;
    }

    private double calculateDimension() {
        return (frameSize.getWidth() * 0.12);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // System.out.println(squishing + ", " + jumping + ", " + landing);
        long currentTime = System.currentTimeMillis();

        if (e.getSource().equals(clock)) {
            long delta = (currentTime - lastTime);
            long deltaRate = (delta / (1000/FPS));
            lastTime = currentTime;

            if (jumping) {
                // Calculating dimensions
                double pos = Math.abs(absoluteX - startingX);

                absoluteX += ((jumpMod * X_INCREMENT) * deltaRate);
                absoluteY = calculateQuadraticY(
                        pos,
                        0,
                        JUMP_LENGTH,
                        goal.getY(),
                        goal.getY() - (calculateDimension() * 0.75),
                        false
                );
                absoluteHeight = calculateQuadraticY(
                        (pos <= JUMP_LENGTH ? pos : JUMP_LENGTH), // Cap pos to jump length
                        0,
                        JUMP_LENGTH,
                        startingHeight,
                        (calculateDimension()*1.1),
                        false
                );

                setBounds(
                        (int) absoluteX,
                        (int) absoluteY,
                        getWidth(),
                        (int) absoluteHeight
                );
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
                        landing = true;
                        setBounds(
                                goal.x,
                                goal.y,
                                getWidth(),
                                (int) startingHeight
                        );
                        // clock.stop();
                    }
                }
                repaint();
            } else if (squishing) {
                double pos = squishPos;

                squishPos += ((X_INCREMENT*5) * deltaRate);

                absoluteHeight = calculateQuadraticY(
                        pos,
                        0,
                        startingHeight * 0.9,
                        startingHeight,
                        startingHeight * 0.9,
                        false
                );

                absoluteY = frameSize.getHeight() - absoluteHeight;

                setBounds(
                        (int) Math.round(absoluteX),
                        (int) Math.round(absoluteY),
                        (int) Math.round(absoluteWidth),
                        (int) Math.round(absoluteHeight)
                );
                if (pos >= startingHeight * 0.9) {
                    jumping = true;
                    squishing = false;
                    squishPos = 0;
                }
            } else if (landing) {
                double pos = landPos;

                landPos += ((X_INCREMENT*3) * deltaRate);

                absoluteHeight = calculateQuadraticY(
                        pos,
                        0,
                        JUMP_LENGTH,
                        startingHeight,
                        startingHeight * 0.9,
                        false
                );
                // System.out.println(startingHeight / absoluteHeight);

                //absoluteX = startingX - (absoluteWidth/2.0);
                absoluteY = frameSize.getHeight() - absoluteHeight;

//                System.out.println(absoluteX / startingX);

                setBounds(
                        (int) Math.round(absoluteX),
                        (int) Math.round(absoluteY),
                        (int) Math.round(absoluteWidth),
                        (int) Math.round(absoluteHeight)
                );
                if (leave) {
                    // Dont bother actually running the landing animation, since Cababas should be out of bounds
                    jumping = true; // Make Cababas jump again
                } else {
                    if (pos >= JUMP_LENGTH) {
                        landing = false;
                        jumping = false;
                        squishing = false;

                        landPos = 0;
                        squishPos = 0;
                        clock.stop();
                    }
                }
            }
        }
    }
}