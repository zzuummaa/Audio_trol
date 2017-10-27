package ru.zuma;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class DisplayVideoFrame extends JFrame {
    private JPanel contentPane;
    private BufferedImage image;

    /**
     * Create the frame.
     */
    public DisplayVideoFrame() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 650, 490);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new FlowLayout());
        setVisible(true);
    }

    public void paint(Graphics g){
        g = contentPane.getGraphics();
        if (image != null) {
            double scaleCoef = this.getWidth() * this.getHeight() / ( image.getWidth() * image.getHeight() );
            int newWidth = (int)(image.getWidth() * scaleCoef);
            int newHeight = (int)(this.getHeight() * scaleCoef);

            g.drawImage(image, 0, 0, newWidth, newHeight, this);
        }
    }

    public void showImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
}
