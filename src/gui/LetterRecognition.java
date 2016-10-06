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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

/**
 *
 * @author anter_000
 */
public class LetterRecognition {

    /**
     * @param args the command line arguments
     */
    //private static Processing p = new Processing(11);
    private static Processing p = new Processing(5);
    public static void main(String[] args) throws InterruptedException {
        
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
        //liveFeedPanel.setMirrored(true);
        
        //Create Training Panel
        //Clean image display
        JLabel cleanImageSample = new JLabel();
        //Containter for guess letter
        JLabel guessLetter = new JLabel();
        
        //Create training control panel
        JPanel trainControlPanel = new JPanel();
        trainControlPanel.setLayout(new BoxLayout(trainControlPanel,BoxLayout.X_AXIS));
        
        //Create ABCDE,none buttons to be put in train control panel
        JButton aBtn = new JButton("A");
        aBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addToDb(e,"A");
            }
        });
        JButton bBtn = new JButton("B");
        bBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addToDb(e,"B");
            }
        });
        JButton cBtn = new JButton("C");
        cBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addToDb(e,"C");
            }
        });
        JButton dBtn = new JButton("D");
        dBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToDb(e,"D");
            }
        });
        JButton eBtn = new JButton("E");
        eBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addToDb(e,"E");
            }
        });
        JButton noneBtn = new JButton("None");
        noneBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addToDb(e,"None");
            }
        });
        
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
		BufferedImage image = webcam.getImage();
                //BufferedImage image = null;
                BufferedImage displayImage = null;
                /*try{
                    image = ImageIO.read(new File("C:\\Users\\anter_000\\Desktop\\image processing\\Letters\\underline_A.png"));
                }
                catch(IOException ex){
                    System.out.println("Cannot find image");
                }*/
                
                //image = p.BitColor(image);
                //image = p.removeDarkEdges(image);
                //image = p.CropImage(image);
                
                image = p.Preprocess(image);
                
                displayImage = image;
                cleanImageSample.setIcon(new ImageIcon(p.drawSegments(displayImage)));
                String segmentPattern = p.createSegmentPattern(image);
                String columnPattern = p.createColumnPattern(image);
                String rowPattern = p.createRowPattern(image);
                float blackToWhiteRatio = p.getBlackToWhiteRatio(image);
                Processing.currImage = image;
                Processing.segmentPattern = segmentPattern;
                Processing.columnPercent = columnPattern;
                Processing.rowPercent = rowPattern;
                Processing.areaPercent = blackToWhiteRatio;
                guessLetter.setText(p.guessLetter(segmentPattern));
                
            }
            
        });
        captureBtn.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if(c == KeyEvent.VK_ENTER || c == KeyEvent.VK_SPACE){
                    // get image
                    BufferedImage image = webcam.getImage();

                    BufferedImage displayImage = null;
                
                    //image = p.BitColor(image);
                    //image = p.removeDarkEdges(image);
                    //image = p.CropImage(image);
                
                    image = p.Preprocess(image);
                
                    displayImage = image;
                    cleanImageSample.setIcon(new ImageIcon(p.drawSegments(displayImage)));
                    String segmentPattern = p.createSegmentPattern(image);
                    String columnPattern = p.createColumnPattern(image);
                    String rowPattern = p.createRowPattern(image);
                    float blackToWhiteRatio = p.getBlackToWhiteRatio(image);
                    Processing.currImage = image;
                    Processing.segmentPattern = segmentPattern;
                    Processing.columnPercent = columnPattern;
                    Processing.rowPercent = rowPattern;
                    Processing.areaPercent = blackToWhiteRatio;
                    guessLetter.setText(p.guessLetter(segmentPattern));
                }
                else{
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                e.consume();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                e.consume();
            }
        });
     
        
        //End of Create the Control Panel
        
        //Add all elements of Capture Control Panel
        captureControlPanel.setLayout(new BoxLayout(captureControlPanel,BoxLayout.X_AXIS));
        captureControlPanel.add(captureBtn);
        
        //Add all elements of Training Panel
        trainPanel.setLayout(new BoxLayout(trainPanel,BoxLayout.Y_AXIS));
        trainPanel.add(cleanImageSample);
        trainPanel.add(guessLetter);
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
    
    //action for ABCDE buttons
    private static void addToDb(ActionEvent evt, String letter){
        
        if(!letter.equalsIgnoreCase("none")){
            p.storeToDB(Processing.segmentPattern, letter);
        }
        
    }
    
}
