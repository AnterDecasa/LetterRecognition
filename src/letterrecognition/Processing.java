/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrecognition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author anter_000
 */
public class Processing {
    
    public static BufferedImage currImage = null;
    public static String segmentPattern = "";
    public static String columnPercent = "";
    public static String rowPercent = "";
    public static float areaPercent = 0f;
    
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
		RGB = (RGB/3 > 60)? 255 : 0 ;
		image.setRGB(x, y, new Color(RGB, RGB, RGB).getRGB() );
            }
	}	
		return image;
    }
    
    public BufferedImage grayScale(BufferedImage image){
        
        BufferedImage grayScaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        println("Turning image to grayscale");
        
        for (int i = 0; i < image.getHeight(); i++){
            for(int j = 0; j < image.getWidth(); j++){
                Color c = new Color(image.getRGB(j,i));
                int red = (int)(c.getRed());
                int green = (int)(c.getGreen());
                int blue = (int)(c.getBlue());
                Color newColor = new Color((red+green+blue)/3, (red+green+blue)/3, (red+green+blue)/3);
                grayScaleImage.setRGB(j, i, newColor.getRGB());
            }
        }
        
        return grayScaleImage;
               
    }
    
    public BufferedImage HistogramEqualizationImage(BufferedImage image){
		
	int len = 256;
	double array[] = new double[len];
	int cp[] = new int[len];
	int max = 0;
	int total = image.getWidth() * image.getHeight();
	int intensity = 255;
		
	//Checks if image is gray scale
	if(!IsGrayscale(image)){
            //image get the gray scale of colored image
            image = Luminosity(image);
	}
		
	//initializes array;
	for(int i = 0 ; i < len ; i++){
            array[i] = 0;
	}
	//fill array with data
	for(int x = 0 ; x < image.getWidth() ; x++ ){ 
            for(int y = 0 ; y < image.getHeight() ; y++){
		Color color = new Color(image.getRGB(x, y));
		array[color.getBlue()]++;
            }
	}
	//probability
	for(int i = 0 ; i < len ; i++){
            array[i] = array[i] / total;
	}
	//cumulative probability
	for(int i = 1 ; i < len ; i++){
            array[i] = array[i - 1] + array[i];
	}
	//cumulative probability * intensity
	for(int i = 1 ; i < len ; i++){
            cp[i] = (int) Math.floor(array[i] * intensity);
	}
		
        //takes the highest possible graph height
	for(int i = 0 ; i < 256; i++ ){
            if(array[i] > max){
		max = (int) array[i];
            }
        }
		
	BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight() ,BufferedImage.TYPE_INT_RGB);
	//graphs the histogram
	for(int x = 0 ; x < image.getWidth() ; x++ ){ 
            for(int y = 0 ; y < image.getHeight() ; y++){
		Color color = new Color(image.getRGB(x, y));
		newImage.setRGB(x, y, new Color(cp[color.getBlue()],cp[color.getBlue()],cp[color.getBlue()]).getRGB());
		//System.out.println(color.getBlue());
            }
	}
	return newImage;
    }
    
    private BufferedImage Luminosity(BufferedImage image){
	for( int x = 0 ; x < image.getWidth() ; x++ ){
            for( int y = 0 ; y < image.getHeight() ; y++){
		Color color = new Color(image.getRGB(x, y));
                int red = (int)(color.getRed()     * 0.21);
		int green = (int)(color.getGreen() * 0.72);
		int blue = (int)(color.getBlue()   * 0.07);
		int RGB = red+green+blue;
		Color newColor = new Color(RGB , RGB, RGB);
		image.setRGB(x, y, newColor.getRGB());
            }
	}
		
	return image;	
    }
    
    private boolean IsGrayscale(BufferedImage image){
	int count = 0;
	int total = image.getWidth() * image.getHeight();
	for(int x = 0 ; x < image.getWidth() ; x++ ){ 
            for(int y = 0 ; y < image.getHeight() ; y++){
		Color color = new Color(image.getRGB(x, y));
		//if red, green, and blue has the same value then its a shade of gray
		if(color.getRed() == color.getGreen() && color.getGreen() == color.getBlue()){
                    count++;
		}
            }
	}
	return (count == total)? true : false;
		
    }
    
    public BufferedImage getNoise(BufferedImage image){
         
        println("Removing noise");
        for( int x = 0 ; x < image.getWidth() - 50 ; x++){
            for(int y = 0  ; y < image.getHeight() - 50 ; y++){
                int tr = 0; 
                for(int ctr = 0; tr!=1 && ctr < 50; ctr++){
                    if(image.getRGB(x+ctr, y) != Color.black.getRGB()){
                        tr = 1;
                    }
                    if(image.getRGB(x+ctr, y+50) != Color.black.getRGB()){
                        tr = 1;
                    }
                    if(image.getRGB(x, y+ctr) != Color.black.getRGB()){
                        tr = 1;
                    }
                    if(image.getRGB(x+50, y+ctr) != Color.black.getRGB()){
                        tr = 1;
                    }
                }
                            
                if(tr!=1){
                    for( int x0 = x ; x0 < x+50 ; x0++){
                        for(int y0 = y  ; y0 < y+50 ; y0++){
                            image.setRGB(x0, y0, 0);
                        }
                    }
                }
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
    
    public BufferedImage Rotate(BufferedImage image, double angle){
        
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage newImage = new BufferedImage(w, h, image.getType());
        setWhiteBackground(newImage);
        Graphics2D g = newImage.createGraphics();
        g.rotate(Math.toRadians(angle), w / 2, h / 2);
        g.drawImage(image, null, 0, 0);
        return newImage;
        
    }
    
    private BufferedImage setWhiteBackground(BufferedImage image){
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                image.setRGB(j, i, Color.white.getRGB());
            }
        }
            
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
    
    public String createSegmentPattern(BufferedImage image){
		
	String pattern = "";
	
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int segmentWidth = imageWidth/segments;
        int segmentHeight = imageHeight/segments;
        
        int x;
        int y;
        println("Getting segment string pattern");
        //Identify string segmentPattern
        for(int i = 0; i < segments; i++){
            y = segmentHeight * i;
            for(int j = 0; j < segments; j++){
                x = segmentWidth * j;
                //pattern  += BlackOrWhiteSegment(new Color(image.getRGB(x, y)));
                pattern += BlackOrWhiteSegment(image,x,y,segmentWidth,segmentHeight);
            }
        }
        println(pattern);
        
        return pattern;
        
    }
    
    public float getBlackToWhiteRatio(BufferedImage image){
        
        double tempRetVal = 0f;
        float retVal = 0f;
        
        int width = image.getWidth();
        int height = image.getHeight();
        double totalArea = width * height;
        double count = 0;
        
        println("Getting total black to white ratio");
        
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(image.getRGB(j,i) != background){
                    count++;
                }
            }
        }
        tempRetVal = count/totalArea*100;
        retVal = (float) tempRetVal;
        
        BigDecimal bd = new BigDecimal(Float.toString(retVal));
        bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
        retVal = bd.floatValue();
        
        println(retVal);
        
        return retVal;
    
    }    
    
    public String createRowPattern(BufferedImage image){
        String pattern = "";
        
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int segmentHeight = imageHeight/segments;
        int y = 0;
        double blackCnt = 0;
        double columnArea = segmentHeight * imageWidth;
        double blackToWhiteRatio = 0f;
        
        println("Creating row black to white pixel ratio string pattern");
        
        for(int cnt = 0; cnt < segments; cnt++){
            y  = segmentHeight * cnt;
            blackCnt = 0;
            
            for(int j = 0; j < imageWidth; j++){
                for(int i = 0; i < segmentHeight; i++){
                    if(image.getRGB(j,y+i) != background){
                        blackCnt++;
                    }
                }
            }
            blackToWhiteRatio = blackCnt/columnArea*100;
            pattern += String.format("%.2f",blackToWhiteRatio) + "";
            if(cnt < segments-1){
                pattern += ",";
            }
        }
        println(pattern);
        
        return pattern;
    }
    
    public String createColumnPattern(BufferedImage image){
        String pattern = "";
        
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int segmentWidth = imageWidth/segments;
        int x = 0;
        double blackCnt = 0;
        double columnArea = segmentWidth * imageHeight;
        double blackToWhiteRatio = 0f;
        
        println("Creating column black to white pixel ratio string pattern");
        
        for(int cnt = 0; cnt < segments; cnt++){
            x  = segmentWidth * cnt;
            blackCnt = 0;
            for(int i = 0; i < imageHeight; i++){
                for(int j = 0; j < segmentWidth; j++){
                    if(image.getRGB(x+j,i) != background){
                        blackCnt++;
                    }
                }
            }
            blackToWhiteRatio = blackCnt/columnArea*100;
            pattern += String.format("%.2f",blackToWhiteRatio) + "";
            if(cnt < segments-1){
                pattern += ",";
            }
        }
        println(pattern);
        
        return pattern;
    }
    
    public void storeToDB(String pattern, String letter){
        println("Storing to database: " + pattern + " " + letter);
        try{
            Statement stmt = connect.createStatement();
            String sql;
            sql = "INSERT into lettersegment (letter, pattern, columnPercentPattern, rowPercentPattern, totalPercent)";
            sql += "VALUES ('"+ letter + "','" + pattern +"','"+ columnPercent +"','"+ rowPercent +"',"+ areaPercent +")";
            stmt.executeUpdate(sql);
            println("Data successfully stored");
        }
        catch(SQLException ex){
            println("Not able to add data!");
        }    
        
    }
    
    public String guessLetter(String pattern){
        
        String letterGuess = "None";         
        println("Guessing....");
        try{
            Statement stmnt = connect.createStatement();
            String sql;
            sql = "SELECT * FROM lettersegment WHERE pattern = " + pattern;
            ResultSet results = stmnt.executeQuery(sql);
            int cnt = 0;
            while(results.next()){
                cnt++;
                println(cnt);
            }
            if(cnt != 0){
                
                println("Results are available");
                results.first();
                String guessLetter = results.getString("letter");
                println(guessLetter + "");
                letterGuess = guessLetter;
                
            }
        }
        catch(SQLException ex){
            println();
        }
        
        return letterGuess;
        
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
    
    private void println(double decimal){
        System.out.println(decimal);
    }
    
    private void println(){
        System.out.println();
    }
    
    
    
    public BufferedImage StandUp2(BufferedImage image){

            image = BlackWhite2(image);
            image = BlueWhite2(image);
            image = CropImage2(image);

            if(IsVertical(image)){
                    image = Rotate2(image,90);
            }else if(IsHorizantal(image)){
                    image = Rotate2(image,0); // i'm not kidding
            }else{
                    removeBlack(image);
                    image = NinetyDegrees(image);
                    image = CropImage2(image);
                    }
            image = IsUpSideDown(image);	
            image = NIggaWhite(image);

            image = RemoveLine(image);
            return image;
    }

    public boolean IsHorizantal(BufferedImage image){	
            boolean retval = false;
            int maxX = 0;
            for(int y = 0; y < image.getHeight(); y++){
                    for(int x = 0; x < image.getWidth(); x++){
                            if(image.getRGB(x, y) == Color.WHITE.getRGB()){
                                    maxX = (maxX < x)? x : maxX;
                                    image.setRGB(x, y, 0);
                                    if(x == image.getWidth() -1){
                                            break;
                                    }
                            }else{
                                    break;
                            }
                    }
            }

            if(maxX == image.getWidth() -1){
                    retval = true;
            }
            System.out.println("Horizantal : " + retval);
            return retval;
    }


    public boolean IsVertical(BufferedImage image){	
            boolean retval = false;
            int maxY = 0;
            for(int x = 0; x < image.getWidth(); x++){	
                    for(int y = 0; y < image.getHeight(); y++){
                            if(image.getRGB(x, y) == Color.WHITE.getRGB()){
                                    maxY = (maxY < y)? y : maxY;
                                    image.setRGB(x, y, 0);
                                    if(x == image.getHeight() -1){
                                            break;
                                    }
                            }else{
                                    break;
                            }
                    }
            }

            if(maxY == image.getHeight() -1){
                    retval = true;
            }
            System.out.println("Vertical : " + retval);
            return retval;
    }


    public BufferedImage IsUpSideDown(BufferedImage image){

            int upDown = image.getHeight()/2;
            int maxX = 0;
            int maxY = 0; // height of white space from above

            for(int y = 0; y < image.getHeight(); y++){
                    for(int x = 0; x < image.getWidth(); x++){
                            if(image.getRGB(x, y) == Color.WHITE.getRGB()){
                                    maxX = (maxX < x)? x : maxX;
                                    if(x == image.getWidth() -2){
                                            maxY = y;
                                            break;
                                    }
                            }else{
                                    break;
                            }
                    }
            }

            System.out.println("UpSideDown :" + ((maxY <= upDown)? "flipped" : "yup"));
            if(maxY <= upDown){	
                    image = Rotate2(image, 180);
            }

            return image;
    }



    public boolean IsDiagonal(BufferedImage image){
            boolean retval = false;
            int blue = Color.BLUE.getRGB();	
            int maxX = 0;

            for(int y = 0; y < image.getHeight(); y++){
                    for(int x = 0; x < image.getWidth(); x++){
                            if(image.getRGB(x, y) == blue){
                                    maxX = (maxX < x)? x : maxX;
                                    break;
                            }else if( x == image.getWidth() - 1){
                                    image.setRGB(x, y,0);
                                    maxX = x;
                                    break;
                            }else{
                                    image.setRGB(x, y,0);
                            }
                    }
            }
            if(maxX == image.getWidth()-1){
                    retval = false;
            }else{
                    retval = true;
            }
            System.out.println("diagonal : " + retval);

            return retval;
    }


    public BufferedImage NinetyDegrees(BufferedImage image){
            double angle;
            int LX = 0;
            int LY = 0;
            int FX = 0;
            int FY = 0;
            int i = 0;
            do{
                    i++;
                    image = Rotate2(image, 90);
                    LX = 0;
                    LY = 0;
                    FX = 0;
                    FY = 0;

                    for(int x = 0; x < image.getWidth(); x++){
                            for(int y = 0; y < image.getHeight(); y++){
                                    if(image.getRGB(x, y) == Color.BLUE.getRGB()){
                                            if(FX< x){
                                                    FX = x;
                                                    FY = y;
                                            }
                                            if(LY< y){
                                                    LX = x;
                                                    LY = y;
                                            }
                                    }
                            }
                    }

                    double adjacent = FX - LX;
                    double opposite = LY - FY;
                    boolean greater = false;
                    if(adjacent >= opposite){
                             greater = true;
                    }else{
                            opposite = FX - LX;
                             adjacent = LY - FY;
                    }
                    angle = Math.toDegrees(Math.atan2(adjacent, opposite));

                    if(greater){
                            angle = -angle ;
                    }	
            }while(CheckSpace(image,LX, LY, FX, FY) && i < 4);
            image = Rotate2(image, angle);
            return image;
    }


    public boolean CheckSpace(BufferedImage image, int x, int y, int x2, int y2 ){
            boolean retval = false;

             int w = x2 - x ;
                int h = y2 - y ;
                int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
                if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
                if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
                if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
                int longest = Math.abs(w) ;
                int shortest = Math.abs(h) ;
                if (!(longest>shortest)) {
                    longest = Math.abs(h) ;
                    shortest = Math.abs(w) ;
                    if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
                    dx2 = 0 ;            
                }
                int numerator = longest >> 1 ;
                for (int i=0;i<=longest;i++) {
                    if(image.getRGB(x, y) == Color.WHITE.getRGB()){
                            retval = true;
                            break;
                    }
                    numerator += shortest ;
                    if (!(numerator<longest)) {
                        numerator -= longest ;
                        x += dx1 ;
                        y += dy1 ;
                    } else {
                        x += dx2 ;
                        y += dy2 ;
                    }
                }
                System.out.println("Wrong Side : " + retval + ((retval)? " - pls rotate 90 deg" : ""));
            return retval;
    }



    public BufferedImage CropImage2(BufferedImage image){

            int Xmin = image.getWidth();
            int Xmax = 0;
            int Ymin = image.getHeight();
            int Ymax = 0;

            int background = Color.WHITE.getRGB();
            for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                            if(image.getRGB(x, y)  != background ){
                                    Xmin = (Xmin > x)? x : Xmin ;
                                    Xmax = (Xmax < x)? x : Xmax ;
                                    Ymin = (Ymin > y)? y : Ymin ;
                                    Ymax = (Ymax < y)? y : Ymax;
                            }
                    }
            }

            BufferedImage newImage = new BufferedImage(Xmax - Xmin + 1, Ymax - Ymin + 1, BufferedImage.TYPE_INT_RGB);
            for(int x = Xmin; x <= Xmax; x++){
                    for(int y = Ymin; y <= Ymax; y++){
                            newImage.setRGB(x - Xmin, y - Ymin, image.getRGB(x, y));
                    }
            }
            return newImage;
    }


    public BufferedImage Rotate2(BufferedImage image, double angle){
        BufferedImage newImage = new BufferedImage(image.getWidth() * 2, image.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
        int addX = image.getWidth() / 2 ; 
        int addY = image.getHeight() / 2;
        for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                            newImage.setRGB(x + addX, y + addY, image.getRGB(x, y));
                    }
            }

            AffineTransform tx = new AffineTransform();
        tx.rotate(angle * (Math.PI / 180), newImage.getWidth() / 2,newImage.getHeight() / 2);

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        newImage = op.filter(newImage, null);
        newImage = removeBlack(newImage);
        newImage = CropImage2(newImage);

        return newImage;
    }


    public BufferedImage BlackWhite2(BufferedImage image){
            for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                                    Color color = new Color(image.getRGB(x, y));
                                    int red = (int)(color.getRed());
                                    int green = (int)(color.getGreen());
                                    int blue = (int)(color.getBlue());
                                    int RGB = red+green+blue;

                                    RGB = (RGB/3 > 75)? 255 : 0 ;
                                    image.setRGB(x, y, new Color(RGB, RGB, RGB).getRGB());
                    }
            }	
            return image;
    }	

    public BufferedImage BlueWhite2(BufferedImage image){
            for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                            Color color = new Color(image.getRGB(x, y));
                            if(color.getBlue() == 0){
                                    image.setRGB(x, y, 255);
                            }
                    }
            }	
            return image;
    }
    public BufferedImage removeBlack(BufferedImage image){
            for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                            Color color = new Color(image.getRGB(x, y));
                            if(color.getRed() == color.getGreen() && color.getGreen() == color.getBlue()){
                                    image.setRGB(x, y, Color.WHITE.getRGB());
                            }
                    }
            }	
            return image;
    }

    public BufferedImage NIggaWhite(BufferedImage image){
            for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                            Color color = new Color(image.getRGB(x, y));
                            if(color.getRed() == color.getGreen() && color.getGreen() == color.getBlue()){
                                    image.setRGB(x, y, Color.WHITE.getRGB());
                            }else{
                                    image.setRGB(x, y, Color.BLACK.getRGB());
                            }
                    }
            }	
            return image;
    }

    public BufferedImage RemoveLine(BufferedImage image){

            int maxX = 0;
            int maxY = 0; // height of white space from above

            for(int y = 0; y < image.getHeight() && maxX < image.getWidth() -1; y++){
                    for(int x = 0; x < image.getWidth() && maxX < image.getWidth() -1; x++){
                            if(image.getRGB(x, y) == Color.WHITE.getRGB()){
                                    maxX = (maxX < x)? x : maxX;
                                    if(x == image.getWidth() -1){
                                            maxY = y;
                                            break;
                                    }
                            }else{
                                    break;
                            }
                    }
            }


            if(maxX == image.getWidth() -1){
                    for(int x = 0 ; x < image.getWidth(); x++){
                            for(int y = maxY; y < image.getHeight(); y++){
                                    image.setRGB(x, y, Color.WHITE.getRGB());
                            }
                    }
            }

            image = CropImage2(image);

            return image;
    }

}
