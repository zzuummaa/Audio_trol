package ru.zuma;

import javafx.scene.transform.Scale;
import sun.awt.image.ToolkitImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class DisplayVideoFrame extends JFrame {
    private JPanel contentPane;
    private ToolkitImage image;
    private int s;

    /**
     * Create the frame.
     */
    public DisplayVideoFrame() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 650, 490);
        s = getWidth() * getHeight();

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new FlowLayout());
        setContentPane(contentPane);

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g = contentPane.getGraphics();
        if (image != null) {

            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), this);
            setSize(image.getWidth(), image.getHeight() + 10);
        }
    }



    public void showImage(BufferedImage image) {
        if (image == null) {
            throw new NullPointerException("Image is null!");
        }

        double imageSideRatio = (double) image.getWidth() / image.getHeight();

        double newHeightDouble = (int) (Math.sqrt(s / imageSideRatio));
        int newHeight = (int) newHeightDouble;
        int newWidth = (int) (imageSideRatio * newHeightDouble);
        //System.out.println(imageSideRatio + "\t" + this.getWidth() + "\t" + this.getHeight() + "\t" + image.getWidth() + "\t" + image.getHeight());

        ToolkitImage rescaledImage = (ToolkitImage) image
                .getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);

        this.image = rescaledImage;
        repaint();
    }
}
