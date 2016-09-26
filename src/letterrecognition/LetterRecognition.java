/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrecognition;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
        JButton captureBtn = new JButton("Capture");
        captureBtn.setSize(40, 20);
        captureBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                // get image
		BufferedImage image = webcam.getImage();

		// save image to PNG file
                try{
                    ImageIO.write(image, "PNG", new File("test.png"));
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
            
        });
        
        //Add all elements of Control Panel
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
