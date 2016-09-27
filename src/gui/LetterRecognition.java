/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import letterrecognition.Processing;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;

/**
 *
 * @author anter_000
 */
public class LetterRecognition {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        
        Processing p = new Processing(5);
        int ctr = 0;
        
        JFrame window = new JFrame("Test webcam panel");
        
        JPanel mainPanel = new JPanel();
        JPanel captureControlPanel = new JPanel();
        JPanel trainPanel = new JPanel();
        
        //Access the webCam
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel liveFeedPanel = new WebcamPanel(webcam);
        liveFeedPanel.setFPSDisplayed(true);
        liveFeedPanel.setDisplayDebugInfo(true);
        liveFeedPanel.setImageSizeDisplayed(true);
        liveFeedPanel.setMirrored(true);
        
        //Create Training Panel
        //Clean image display
        JLabel cleanImageSample = new JLabel();
        
        //Create traing control panel
        JPanel trainControlPanel = new JPanel();
        trainControlPanel.setLayout(new BoxLayout(trainControlPanel,BoxLayout.X_AXIS));
        
        //Create ABCDE,none buttons to be put in train control panel
        JButton aBtn = new JButton("A");
        JButton bBtn = new JButton("B");
        JButton cBtn = new JButton("C");
        JButton dBtn = new JButton("D");
        JButton eBtn = new JButton("E");
        JButton noneBtn = new JButton("None");
        
        //Add buttons to train Control panel
        trainControlPanel.add(aBtn);
        trainControlPanel.add(bBtn);
        trainControlPanel.add(cBtn);
        trainControlPanel.add(dBtn);
        trainControlPanel.add(eBtn);
        trainControlPanel.add(noneBtn);
        
        //End of create Training Panel
        
        //Create Capture Control Panel
        //Capture Button
        JButton captureBtn = new JButton("Capture");
        captureBtn.setSize(40, 20);
        captureBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                // get image
		//BufferedImage image = webcam.getImage();
                BufferedImage image = null;
                BufferedImage displayImage = null;
                try{
                    image = ImageIO.read(new File("C:\\Users\\anter_000\\Desktop\\image processing\\Letters\\letter1 - Copy.png"));
                }
                catch(IOException ex){
                    System.out.println("Cannot find image");
                }
                
                image = p.BitColor(image);
                image = p.removeDarkEdges(image);
                image = p.CropImage(image);
                p.OCRTrain(image);
                displayImage = image;
                cleanImageSample.setIcon(new ImageIcon(p.drawSegments(displayImage)));
            }
            
        });
        
        //End of Create the Control Panel
        
        //Add all elements of Capture Control Panel
        captureControlPanel.setLayout(new BoxLayout(captureControlPanel,BoxLayout.X_AXIS));
        captureControlPanel.add(captureBtn);
        
        //Add all elements of Training Panel
        trainPanel.setLayout(new BoxLayout(trainPanel,BoxLayout.Y_AXIS));
        trainPanel.add(cleanImageSample);
        trainPanel.add(trainControlPanel);
        
        //Add all sub panels to Main Panel
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.add(liveFeedPanel);
        mainPanel.add(captureControlPanel);
        mainPanel.add(trainPanel);
        mainPanel.setVisible(true);
        
        //Set Window
        window.add(mainPanel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }
    
}
