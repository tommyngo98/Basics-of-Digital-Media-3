import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
public class GLDM_3 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Rot-Kanal", "Negativ", "Graustufen", "Fehlerdiffusion", "Bin‰rbild", "Sepia", "6-Farben"};


	public static void main(String args[]) {

		IJ.open("C:\\Users\\tommy\\eclipse-workspace\\GLDM_3\\Bear.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GLDM_3 pw = new GLDM_3();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 

		}


		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zur√ºckschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Rot-Kanal")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("Negativ")) {
				
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

				
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
			
						int rn = 255 - r;
						int gn = 255 - g;
						int bn = 255 - b;
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("Graustufen")) {
		
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

				
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
			
						int t = (r+g+b)/3;
						
						int rn = t;
						int gn = t;
						int bn = t;
						
//						int rn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
//						int gn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
//						int bn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("Fehlerdiffusion")) {
				 int border = 255;
		         int error = 0;
		             
		            for (int y=0; y<height; y++) {
		                for (int x=0; x<width; x++) {
		                    int pos = y*width + x;
		                    int argb = origPixels[pos];
		                    
		                    int r = (argb >> 16) & 0xff;
		                    int g = (argb >>  8) & 0xff;
		                    int b =  argb        & 0xff;
		                     
		                    int rn = 255;
		                    int gn = 255;
		                    int bn = 255;
		                     
		                    if((r+g+b+error)<border){
		                        rn = 0;
		                        gn = 0;
		                        bn = 0; 
		                    }
		                     
		                    error=((r+g+b+error)-(rn+gn+bn));
		                    pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
		                }
		                error=0;
		            }   
			}
			
			if (method.equals("Sepia")) {
				
				int sepiaSt‰rke = 20;
				
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

				
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
//						
//						int rn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
//						int gn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
//						int bn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
//						
//						 int rNew = farbgrenze (rn + (sepiaSt‰rke * 2));
//		                 int gNew = farbgrenze (gn + sepiaSt‰rke);
//		                 int bNew = farbgrenze (bn - sepiaSt‰rke);
//						
						int rn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
						int gn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
						int bn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
						
						int rNew = (int) ((0.439 * rn));
						int gNew = (int) ((0.258 * gn));
						int bNew = (int) ((0.078 * bn));
					
					pixels[pos] = (0xFF<<24) | (rNew<<16) | (gNew<<8) | bNew;
					}
				}
			}
			
			if (method.equals("Bin‰rbild")) {
				
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

				
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
			
						int rn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
						int gn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
						int bn = (int) (0.299 * r + 0.587 * g + 0.114 * b);
						
						if(rn <= 128) {
							rn = 0;
						}
						else {
							rn = 255;
						}
		
						if(gn <= 128) {
							gn = 0;
						}
						else {
							gn = 255;
						}
						
						if(bn <= 128) {
							bn = 0;
						}
						else {
							bn = 255;
						}
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("6-Farben")) {
				
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

				
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						if(r <= 130) {
							r = 0;
						}
						else {
							r = 255;
						}
		
						if(g <= 130) {
							g = 0;
						}
						else {
							g = 255;
						}
						
						if(b <= 130) {
							b = 0;
						}
						else {
							b = 255;
						}
					
					pixels[pos] = (0xFF<<24) | (r<<16) | (g<<8) | b;
					}
				}
			}
			
			
			
		}


	} // CustomWindow inner class
} 