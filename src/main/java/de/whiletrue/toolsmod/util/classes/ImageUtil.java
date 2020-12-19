package de.whiletrue.toolsmod.util.classes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Optional;

import javax.imageio.ImageIO;

public class ImageUtil {

	private static ImageUtil instance;
	
	private ImageUtil() {}
	
	public static ImageUtil getInstance() {
		if(instance==null)
			instance=new ImageUtil();
		return instance;
	}
	
	/**
	 * Tries to load an image from the given path
	 * @param file
	 * @return optionally the loaded image
	 */
	public Optional<BufferedImage> loadImage(String file){
		
		try {
			return Optional.of(ImageIO.read(new File(file)));
		} catch (Exception e) {}

		try {
			return Optional.of(ImageIO.read(new URL(file)));
		}catch(Exception e) {}
		
		try {
			return Optional.of(ImageIO.read(this.getClass().getResourceAsStream(file)));
		}catch(Exception e) {}
		
		//Could not load image
		return Optional.empty();
	}
	
	/**
	 * Used from: https://stackoverflow.com/questions/9417356/bufferedimage-resize#9417836
	 * 
	 * @param img the image
	 * @param newW the new width
	 * @param newH the new height
	 * @return the resized image
	 */
	public BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_FAST);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
	
}
