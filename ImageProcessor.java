import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;


public class ImageProcessor {
	
	private int[] readRgbArray(Image sourceImage) {
		int rgbData[] = new int[sourceImage.getWidth(null)*sourceImage.getHeight(null)];
		BufferedImage bufferImage = new BufferedImage(sourceImage.getWidth(null), sourceImage.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D bufferContext = bufferImage.createGraphics();
		bufferContext.drawImage(sourceImage, 0, 0, null);
		bufferImage.getRGB(0, 0, sourceImage.getWidth(null), sourceImage.getHeight(null), rgbData, 0, sourceImage.getWidth(null));
		return rgbData;
	}

	//formula: 
	//D(x, y) = S(j, k) *(1-t)*(1-u) + S(j, k+1)*t*(1-u) + S(j+1,k)*(1-t)*u + S(j+1,k+1)*t*u
	public Image scale(Image sourceImage, int destW, int destH) {
		int A = 0;
		int R = 0;
		int G = 0;
		int B = 0;
		int srcW = sourceImage.getWidth(null);
		int srcH = sourceImage.getHeight(null);
		System.out.println("原图片宽和高："+srcW+" "+srcH+"\n目的图片宽和高:"+destW+" "+destH);
		//store source rgbData
		int[] rgbData = readRgbArray(sourceImage);
		
		//store result rgbData
		int[] rgbData2 = new int[destW*destH];
		double rowRatio = ((double)srcH)/((double)destH);
		double colRatio = ((double)srcW)/((double)destW);
		for (int row=0; row<destH; row++) {
			double srcRow = ((double)row)*rowRatio;
			double j = Math.floor(srcRow);
			double t = srcRow - j;
			for (int col=0; col<destW; col++) {
				double srcCol = ((double)col)*colRatio;
				double k = Math.floor(srcCol);
				double u = srcCol - k;
				
				double a = (1.0d-t)*(1.0d-u);
				double b = t*(1.0d-u);
				double c = t*u;
				double d = (1.0d-t)*u;
				
				A = (int)(a*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k,srcW-1,0)] >>24)& 0xFF) +
						b* (double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k,srcW-1,0)] >>24)& 0xFF) +
						c*(double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)]>>24)& 0xFF) +
						d*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)]>>24)& 0xFF));
				
				R = (int)(a*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k,srcW-1,0)] >>16)& 0xFF) +
						b* (double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k,srcW-1,0)] >>16)& 0xFF) +
						c*(double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)] >>16)& 0xFF) +
						d*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)] >>16)& 0xFF));
				
				G = (int)(a*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k,srcW-1,0)] >>8)& 0xFF) +
						b* (double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k,srcW-1,0)] >>8)& 0xFF) +
						c*(double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)]>>8)& 0xFF) +
						d*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)]>>8)& 0xFF));
				
				B = (int)(a*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k,srcW-1,0)] & 0xFF)) +
						b* (double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k,srcW-1,0)]& 0xFF)) +
						c*(double)((rgbData[limit((int)j+1,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)]& 0xFF)) +
						d*(double)((rgbData[limit((int)j,srcH-1,0)*srcW+limit((int)k+1,srcW-1,0)]& 0xFF)));
				A = A&0x000000FF;
				R = R&0x000000FF;
				G = G&0x000000FF;
				B = B&0x000000FF;
				rgbData2[row*destW+col] = A<<24 | R<<16 | G<<8 | B ;
			}
		}
		// use rgbData2 to create Image object
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(destW, destH, rgbData2, 0, destW));
		return image;
	}
	int limit(int x, int max, int min) {
		return x>max? max : x<min? min: x;
	}
	public Image quantize(Image sourceImage, int level) {
		if(level < 0 || level >255) {
			return null;
		}
		float num = 255/level;
		int A=0;
		int R=0;
		int G=0;
		int B=0;
		int rgbData[] = readRgbArray(sourceImage);
		int height = sourceImage.getHeight(null);
		int width = sourceImage.getWidth(null);
		int rgbData2[] = new int[height*width];
		for (int i = 0; i < height * width; i++) {
			/*A = (int)((rgbData[i] & 0xFF000000) >> 24)/num;*/
			R = (int)((float)((rgbData[i] & 0x00FF0000) >> 16)*level/255);
			G = (int)((float)((rgbData[i] & 0x0000FF00) >> 8)*level/255);
			B = (int)((float)((rgbData[i] & 0x000000FF))*level/255);
			/*A = A*num;*/
			R = (int)((float)R*num);
			G = (int)((float)G*num);
			B = (int)((float)B*num);

			rgbData2[i] = 0xFF000000 | R<<16 | G<<8 | B;
		}
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, rgbData2, 0, width));
		return image;
	}
	//transform pic1 to pic2
	public Image transform(int t, Image image1, Image image2) {
		int height = image1.getHeight(null);
		int width = image1.getWidth(null);
		int R=0;
		int G=0;
		int B=0;
		int[] rgbData1 = readRgbArray(image1);
		int[] rgbData2 = readRgbArray(image2);
		int[] rgbData3 = new int[width*height];
		float tf = (float)t/100;
		for(int i=0; i<height*width; i++) {
			R = (int)(((rgbData1[i] & 0x00ff0000) >> 16)* (1-tf) + (((rgbData2[i] & 0x00ff0000) >> 16)*tf));
			G = (int)(((rgbData1[i] & 0x0000ff00) >> 8)* (1-tf) + ((rgbData2[i] & 0x0000ff00) >> 8)*tf);
			B = (int)((rgbData1[i] & 0x000000ff)* (1-tf) + (rgbData2[i] & 0x000000ff)*tf);
			/*R = R&0x000000FF;
			G = G&0x000000FF;
			B = B&0x000000FF;*/
			rgbData3[i] = (0xff000000) + (R<<16) + (G<<8) + B;
		}
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, rgbData3, 0, width));
		return image;
	}
	
	public Image getGray(Image sourceImage) {
		int height = sourceImage.getHeight(null);
		int width = sourceImage.getWidth(null);
		int rgbData[] = readRgbArray(sourceImage);
		for (int i = 0; i < height * width; i++) {
			int a = (int) (((rgbData[i] & 0x00ff0000) >> 16) * 0.3
					+ ((rgbData[i] & 0x0000ff00) >> 8) * 0.59 + (rgbData[i] & 0x000000ff) * 0.11);
			rgbData[i] = (rgbData[i] & 0xff000000) + (a << 16) + (a << 8) + a;
		}
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, rgbData, 0, width));
		return image;
	}

	public Image getBlack(Image sourceImage) {
		int height = sourceImage.getHeight(null);
		int width = sourceImage.getWidth(null);
		int rgbData[] = readRgbArray(sourceImage);
		for (int i = 0; i < height * width; i++) {
			int a = (int) (((rgbData[i] & 0x00ff0000) >> 16) * 0.3
					+ ((rgbData[i] & 0x0000ff00) >> 8) * 0.59 + (rgbData[i] & 0x000000ff) * 0.11);
			//if gray image rgb is greater than 125,then set it to black, else white
			if (a <= 125) {
				rgbData[i] = 0xff000000;
			} else {
				rgbData[i] = 0xffffffff;
			}
		}
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, rgbData, 0, width));
		
		return image;
	}
	
	// method for rotation
	public Image rotate(Image sourceImage, int angle) {
		//get bufferedImage
		BufferedImage bufferedImage = new BufferedImage(sourceImage.getWidth(null), sourceImage.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D bufferContext = bufferedImage.createGraphics();
		bufferContext.drawImage(sourceImage, 0, 0, null);
		
		int w= bufferedImage.getWidth();// 得到图片宽度。
        int h= bufferedImage.getHeight();// 得到图片高度。
        int type= bufferedImage.getColorModel().getTransparency();// 得到图片透明度。
        BufferedImage img;// 空的图片。
        Graphics2D graphics2d;// 空的画笔。
        (graphics2d= (img= new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(angle), w / 2, h / 2);// 旋转，degree是整型，度数，比如垂直90度。
        graphics2d.drawImage(bufferedImage, 0, 0, null);// 从bufferedimagecopy图片至img，0,0是img的坐标。
        graphics2d.dispose();
        
        int rgbData[] = new int[sourceImage.getWidth(null)*sourceImage.getHeight(null)];
        img.getRGB(0, 0, sourceImage.getWidth(null), sourceImage.getHeight(null), rgbData, 0, sourceImage.getWidth(null));
        Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(w, h, rgbData, 0, w));
		return image;
	}
	
	private Image showChanel(Image sourceImage,Color clr) {
		int rgb = 0;
		BufferedImage bufferImage = new BufferedImage(sourceImage.getWidth(null), sourceImage.getHeight(null), BufferedImage.TYPE_INT_RGB);  
        // set the bufferImage to the attributes of the sourceImage
		Graphics2D bufferContext = bufferImage.createGraphics();
		bufferContext.drawImage(sourceImage, 0, 0, null);
        
		int [] rgbData = new int[sourceImage.getWidth(null)*sourceImage.getHeight(null)];
		// use array rgbData to store all the RGB information of the sourceImage 
		bufferImage.getRGB(0, 0, sourceImage.getWidth(null),sourceImage.getHeight(null), rgbData, 0, sourceImage.getWidth(null));
		/* change every pixel's color according to clr */
        for (int i = 0; i<sourceImage.getWidth(null)*sourceImage.getHeight(null); i++) {
        	rgb = rgbData[i];
			if (clr == Color.RED ) {
				rgb &= 0xffff0000;
			} else if (clr == Color.GREEN) {
				rgb &= 0xff00ff00;
			} else if (clr == Color.BLUE) {
				rgb &= 0xff0000ff;
			} else if (clr == Color.GRAY){
				// use the gray scale equation to generate proper gray
				int gray = (int)(((rgb & 0x00ff0000)>>16)*0.3 + ((rgb & 0x0000ff00)>>8)*0.59 + (rgb & 0x000000ff)*0.11);  
				// fix all parts together
				rgb &= 0xff000000;
				rgb += (gray<<16)+(gray<<8)+gray;
			} else if (clr == Color.BLACK){
				int red = (rgb >> 16) & 0xff;
				int green = (rgb >> 8) & 0xff;
				int blue = rgb & 0xff;
				rgb = 0xff000000;
				int temp = red*red +green*green+blue*blue;
				if(temp > 3*128*128){ // 大于，则是白色
		            rgb += 0x00ffffff;
		        }
			}
			rgbData[i] = rgb;
        }
        
		// create image using the rgbData array
        Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(
        		sourceImage.getWidth(null), sourceImage.getHeight(null), rgbData, 0, sourceImage.getWidth(null)));
		return image;
	}
	//show red channel
	public Image showChanelR(Image sourceImage)  
	{  
		return showChanel(sourceImage,Color.RED);
	}  
	//show green channel
	public Image showChanelG(Image sourceImage)  
	{  
		return showChanel(sourceImage,Color.GREEN);
	}  
	//show blue channel  
	public Image showChanelB(Image sourceImage)  
	{  
		return showChanel(sourceImage,Color.BLUE);
	}  
    // show gray picture
 	public Image showGray(Image sourceImage)  
	{  
 		return showChanel(sourceImage,Color.GRAY);
	}

	public Image showBlackAndWhite(Image sourceImage) {
		// TODO Auto-generated method stub
		return showChanel(sourceImage,Color.BLACK);
	}
}