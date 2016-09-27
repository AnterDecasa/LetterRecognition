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
        JPanel controlPanel = new JPanel();
        
        //Access the webCam
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel liveFeedPanel = new WebcamPanel(webcam);
        liveFeedPanel.setFPSDisplayed(true);
        liveFeedPanel.setDisplayDebugInfo(true);
        liveFeedPanel.setImageSizeDisplayed(true);
        liveFeedPanel.setMirrored(true);
        
        //Create the Control Panel
        //Notification
        JLabel notif = new JLabel();
        notif.setText("Waiting...");
        notif.setVisible(true);
        //Capture Button
        JButton captureBtn = new JButton("Capture");
        captureBtn.setSize(40, 20);
        captureBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                // get image
		BufferedImage image = webcam.getImage();

		// save image to PNG file
                /*try{
                    
                    //ImageIO.write(image, "PNG", new File("test.png"));
                    notif.setText("Image captured");
                    //Thread.sleep(2000);
                    //notif.setText("Waiting...");
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(LetterRecognition.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                
                image = p.BitColor(image);
                image = p.CropImage(image);
                p.OCRTrain(image);
            }
            
        });
        
        //Add all elements of Control Panel
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.X_AXIS));
        controlPanel.add(notif);
        controlPanel.add(captureBtn);
        
        
        //Add all sub panels to Main Panel
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.add(liveFeedPanel);
        mainPanel.add(controlPanel);
        mainPanel.setVisible(true);
        
        //Set Window
        window.add(mainPanel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }
    
}
