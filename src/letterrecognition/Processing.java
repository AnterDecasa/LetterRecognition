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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author anter_000
 */
public class Processing {
    
    private String url = "jdbc:mysql://localhost/imageproc";
    private String username = "root";
    private String password = "";
    private Connection connect = null;
    
    private int background = Color.WHITE.getRGB();
    private int segments = 1;
    
    public Processing(int seg){
        segments = seg;
        System.out.println("Connecting to database...");
        
        try{
            connect = DriverManager.getConnection(url,username,password);
            println("Database connected.");
        }
        catch(SQLException e){
            println("Not able to connect!!");
        }
        
    }
    
    public BufferedImage BitColor(BufferedImage image){
	println("Turning image to black and white");
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
	println("Cropping image");		
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
    
    private int BlackOrWhiteSegment(BufferedImage image,int x, int y, int segmentWidth, int segmentHeight){
        
        int cnt = 0;
        int segmentArea = segmentWidth * segmentHeight;
        for(int i = 0; i < segmentHeight; i++){
            for(int j = 0; j < segmentWidth; j++){
                if(image.getRGB(x+j, y+i) != background){
                    cnt++;
                }
            }
        }
        int RGB = (cnt > (int)(segmentArea * .35))? 1:0;
        return RGB;
        
    }
    
    public BufferedImage Rotate(BufferedImage image, double deg){
        
        AffineTransform tx = new AffineTransform();
        tx.rotate(deg * (Math.PI/180), image.getWidth()/2, image.getHeight()/2);
        
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        image = op.filter(image, null);
        
        return image;
        
    }
    
    public BufferedImage drawSegments(BufferedImage image){
        
        int x = 0;
        int y = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        int segmentWidth = width/segments;
        int segmentHeight = height/segments;
        
        println("Draw sample segmented image");
        
        for(int i = 1; i < segments; i++){
            x = segmentWidth * i;
            y = segmentHeight * i;
            for(int n = 0; n < width; n++){
                image.setRGB(n, y, Color.red.getRGB());
            }
            for(int m = 0; m < height; m++){
                image.setRGB(x,m,Color.red.getRGB());
            }
        }
        
        return image;
    
    }
    
    public BufferedImage removeDarkEdges(BufferedImage image){
        
        Queue queue = new LinkedList();
        
        int width = image.getWidth();
        int height = image.getHeight();
        int x = 0;
        int y = 0;
        
        println("Removing dark edges");
        
        //Check top edge
        for(int j = 0; j < width; j++){
            int curRGB = image.getRGB(j, y);
            if (background != curRGB){
                image.setRGB(j, y, background);
                queue.add(new int[]{j,y});
                    
                do{
                    int[] wh = (int[]) queue.poll();
                    if(null!=wh){
                        int qw = wh[0];
                        int qh = wh[1];
                            
                        //Check Straight Neighbors
                        checkPixelLabel(image, queue,qw+1,qh);
                        checkPixelLabel(image, queue,qw-1,qh);
                        checkPixelLabel(image, queue,qw,qh+1);
                        checkPixelLabel(image, queue,qw,qh-1);
                            
                        //Check Slant Neighbors
                        checkPixelLabel(image, queue,qw+1,qh+1);
                        checkPixelLabel(image, queue,qw-1,qh-1);
                        checkPixelLabel(image, queue,qw+1,qh-1);
                        checkPixelLabel(image, queue,qw-1,qh+1);
                    }
                }while(!queue.isEmpty());
            }
        }
        //check left edge
        for(int i = 0; i < height; i++){
            int curRGB = image.getRGB(x, i);
            if (background != curRGB){
                image.setRGB(x, i, background);
                queue.add(new int[]{x,i});
                    
                do{
                    int[] wh = (int[]) queue.poll();
                    if(null!=wh){
                        int qw = wh[0];
                        int qh = wh[1];
                            
                        //Check Straight Neighbors
                        checkPixelLabel(image, queue,qw+1,qh);
                        checkPixelLabel(image, queue,qw-1,qh);
                        checkPixelLabel(image, queue,qw,qh+1);
                        checkPixelLabel(image, queue,qw,qh-1);
                            
                        //Check Slant Neighbors
                        checkPixelLabel(image, queue,qw+1,qh+1);
                        checkPixelLabel(image, queue,qw-1,qh-1);
                        checkPixelLabel(image, queue,qw+1,qh-1);
                        checkPixelLabel(image, queue,qw-1,qh+1);
                    }
                }while(!queue.isEmpty());
            }
        }
        
        //check bottom edge
        y = height-1;
        for(int j = 0; j < width; j++){
            int curRGB = image.getRGB(j, y);
            if (background != curRGB){
                image.setRGB(j, y, background);
                queue.add(new int[]{j,y});
                    
                do{
                    int[] wh = (int[]) queue.poll();
                    if(null!=wh){
                        int qw = wh[0];
                        int qh = wh[1];
                            
                        //Check Straight Neighbors
                        checkPixelLabel(image, queue,qw+1,qh);
                        checkPixelLabel(image, queue,qw-1,qh);
                        checkPixelLabel(image, queue,qw,qh+1);
                        checkPixelLabel(image, queue,qw,qh-1);
                            
                        //Check Slant Neighbors
                        checkPixelLabel(image, queue,qw+1,qh+1);
                        checkPixelLabel(image, queue,qw-1,qh-1);
                        checkPixelLabel(image, queue,qw+1,qh-1);
                        checkPixelLabel(image, queue,qw-1,qh+1);
                    }
                }while(!queue.isEmpty());
            }
        }
        
        //check right edge
        x = width-1;
        for(int i = 0; i < height; i++){
            int curRGB = image.getRGB(x, i);
            if (background != curRGB){
                image.setRGB(x, i, background);
                queue.add(new int[]{x,i});
                    
                do{
                    int[] wh = (int[]) queue.poll();
                    if(null!=wh){
                        int qw = wh[0];
                        int qh = wh[1];
                            
                        //Check Straight Neighbors
                        checkPixelLabel(image, queue,qw+1,qh);
                        checkPixelLabel(image, queue,qw-1,qh);
                        checkPixelLabel(image, queue,qw,qh+1);
                        checkPixelLabel(image, queue,qw,qh-1);
                            
                        //Check Slant Neighbors
                        checkPixelLabel(image, queue,qw+1,qh+1);
                        checkPixelLabel(image, queue,qw-1,qh-1);
                        checkPixelLabel(image, queue,qw+1,qh-1);
                        checkPixelLabel(image, queue,qw-1,qh+1);
                    }
                }while(!queue.isEmpty());
            }
        }
        
        return image;
        
    }
        
    private void checkPixelLabel(BufferedImage image, Queue queue,int w, int h){
        int height = image.getHeight();
        int width = image.getWidth();
        
        if (w!=-1 && h!=-1 
                && w<width && h<height){
            int curRGB = image.getRGB(w, h);
            if (background != curRGB){
                image.setRGB(w, h, background);
                queue.add(new int[]{w,h});
            }
        }
    }
    
    public void OCRTrain(BufferedImage image){
		
	String pattern = "";
	
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int segmentWidth = imageWidth/segments;
        int segmentHeight = imageHeight/segments;
        
        int x;
        int y;
        println("Getting string pattern");
        //Identify string pattern
        for(int i = 0; i < segments; i++){
            y = segmentHeight * i;
            for(int j = 0; j < segments; j++){
                x = segmentWidth * j;
                //pattern  += BlackOrWhiteSegment(new Color(image.getRGB(x, y)));
                pattern += BlackOrWhiteSegment(image,x,y,segmentWidth,segmentHeight);
            }
        }
        println(pattern);
        
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
