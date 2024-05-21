import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Launcher extends JFrame implements ActionListener {
    private static final int MAX_CABABAS = 10;
    private static final List<CameluoCababas> cababasCollection = new ArrayList<CameluoCababas>();
    private static TrayIcon loadedTrayIcon;

    private final CameluoCababas cababas;
    public Launcher() {
        long startTime = System.currentTimeMillis();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setIconImage(Toolkit.getDefaultToolkit().getImage("src\\Resources\\ImageIcon.png"));
        setUndecorated(true);
        setType(Type.UTILITY);
        setLayout(null);
        setBackground(new Color(0, 0, 0, 0));
        getContentPane().setBackground(new Color(0, 0, 0, 0));
        setSize((int)(screenSize.getWidth()), (int)(screenSize.getHeight()*0.4));
        setLocation(0, (int)(screenSize.getHeight() - getHeight()));
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusable(true);
        setResizable(false);
        requestFocus();

        cababas = new CameluoCababas(getSize());
        cababas.addActionListener(this);

        cababasCollection.add(cababas);

        add(cababas);

        System.out.println("Cababas Buddy took " + (((double)(System.currentTimeMillis()-startTime))/1000) + " seconds to load.");

        // Attempt to add tray icon to the tray.
        if (loadedTrayIcon == null) {
            try {
                TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("src\\Resources\\ImageIcon.png"), "Close Cababas Buddy");
                trayIcon.setImageAutoSize(true);
                SystemTray.getSystemTray().add(trayIcon);

                trayIcon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Closing Cababas Buddy via tray icon.");
                        SystemTray.getSystemTray().remove(trayIcon);

                        for (CameluoCababas c : cababasCollection) {
                            c.leave();
                        }

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                System.exit(0); // Close after waiting (if Cababas hasn't closed the program)
                            }
                        }.start();
                    }
                });
                loadedTrayIcon = trayIcon;
            } catch (AWTException e) { // If it fails, the program exits. The user should have a way of closing the program!
                System.out.println("Could not dock icon to tray.");
                setType(Type.NORMAL);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage());
                e.printStackTrace();
                setType(Type.NORMAL);
            }
        }

        new Thread() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep((long) (Math.random() * 2000) + 2000);
                        cababas.jump();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();

        setVisible(true);
        repaint();
        revalidate();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void launch() {
        if (cababasCollection.size() < MAX_CABABAS) {
            System.out.println("Loading Cababas Buddy #" + cababasCollection.size() + "...");
            new Launcher();
        } else {
            System.out.println("Cababas limit exceeded.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(cababas)) {
            cababas.jump();
        }
    }
}