/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrecognition;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author anter_000
 */
public class Processing {
    
    private int background = Color.WHITE.getRGB();
    private int segments = 1;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private int segmentWidth = imageWidth/segments;
    private int segmentHeight = imageHeight/segments;
    
    public Processing(int seg){
        segments = seg;
    }
    
    public BufferedImage BitColor(BufferedImage image){
	for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
		Color color = new Color(image.getRGB(x, y));
		int red = (int)(color.getRed());
		int green = (int)(color.getGreen());
		int blue = (int)(color.getBlue());
		int RGB = red+green+blue;
		RGB = (RGB/3 > 75)? 255 : 0 ;
		image.setRGB(x, y, new Color(RGB, RGB, RGB).getRGB() );
            }
	}	
		return image;
    }
    
    public BufferedImage CropImage(BufferedImage image){
			
        int Xmin = image.getWidth();
	int Xmax = 0;
        int Ymin = image.getHeight();
        int Ymax = 0;
		
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
		if(image.getRGB(x, y) != background){
                    if(Xmin > x){
			Xmin = x;
                    }
                    if(Xmax < x){
                        Xmax = x;
                    }
					
                    if(Ymin > y){
			Ymin = y;
                    }
                    if(Ymax < y){
			Ymax = y;
                    }
		}
				
            }
	}
		
	BufferedImage newImage = new BufferedImage(Xmax - Xmin + 1, Ymax - Ymin + 1, BufferedImage.TYPE_INT_RGB);
	for(int x = Xmin; x <= Xmax; x++){
            for(int y = Ymin; y <= Ymax; y++){
		newImage.setRGB(x - Xmin, y - Ymin, image.getRGB(x, y));
            }
	}
	while(newImage.getWidth() < 200 || newImage.getHeight() < 200){
            newImage = ZoomIn(newImage);
	}
		
		
	return newImage;
    }
    
    public BufferedImage ZoomIn(BufferedImage image){
	BufferedImage newImage = new BufferedImage ( image.getWidth() * 2, image.getHeight() * 2, BufferedImage.TYPE_INT_RGB );
	for( int x = 0 ; x < image.getWidth() ; x++){
            for( int y = 0 ; y < image.getHeight() ; y++){
		newImage.setRGB(x*2, y*2, image.getRGB(x, y));
		newImage.setRGB(x*2, y*2+1, image.getRGB(x, y));
		newImage.setRGB(x*2+1, y*2, image.getRGB(x, y));
		newImage.setRGB(x*2+1, y*2+1, image.getRGB(x, y));
				
            }
	}
        return newImage;
		
    }
    
    private int BlackOrWhiteSegment(Color color){
	
        int red = (int)(color.getRed());
	int green = (int)(color.getGreen());
	int blue = (int)(color.getBlue());
	int RGB = red+green+blue;
	RGB = (RGB/3 > 127)? 0 : 1 ;
        
	return RGB;
    
    }
    
    public BufferedImage Rotate(BufferedImage image, double deg){
        
        AffineTransform tx = new AffineTransform();
        tx.rotate(deg * (Math.PI/180), image.getWidth()/2, image.getHeight()/2);
        
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        image = op.filter(image, null);
        
        return image;
        
    }
    
    private BufferedImage drawSegments(BufferedImage image){
        
        int width = image.getWidth();
        int height = image.getHeight();
        int segmentWidth = width/segments;
        int segmentHeight = height/segments;
        
        int x = segmentWidth;
        int y = segmentHeight;
        for(int i=0; i < height; i++){
            image.setRGB(x,i,Color.red.getRGB());
        }
        x += segmentWidth;
        for(int i=0; i < height; i++){
            image.setRGB(x,i,Color.red.getRGB());
        }
        
        for(int j=0; j < width; j++){
            image.setRGB(j,y,Color.red.getRGB());
        }
        y+= segmentHeight;
        for(int j=0; j < width; j++){
            image.setRGB(j,y,Color.red.getRGB());
        }
        
        return image;
    
    }
    
    public void OCRTrain(BufferedImage image){
		
	String pattern = "";
	
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        segmentWidth = imageWidth/segments;
        segmentHeight = imageHeight/segments;
        
        int x;
        int y;
        
        //Identify string pattern
        for(int i = 0; i < segments; i++){
            x = segmentWidth * i;
            y = segmentHeight * i;
            pattern  += BlackOrWhiteSegment(new Color(image.getRGB(x, y)));
        }
        
    }
    
    private void println(String string){
	System.out.println(string);
    }
		
    private void println(char character){
	System.out.println(character);
    }
		
    private void println(int integer){
	System.out.println(integer);
    }
	
    private void println(float decimal){
	System.out.println(decimal);
    }
    private void println(){
        System.out.println();
    }

}
