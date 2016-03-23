import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JFlashPlayer;*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


@SuppressWarnings("serial")
public class UI extends JFrame {
	private JFileChooser fileChooser;
	private JPanel contentPane;
	private Image myImage1;
	private Image myImage2;
	//use myImage3 to store the modified image
	private Image myImage3;
	private JSlider slider;
	/*private static String flashPath;
	public static JComponent createContent() {
	    JFlashPlayer flashPlayer = new JFlashPlayer();
	    flashPlayer.load(SimpleFlashExample.class, flashPath);
	    return flashPlayer;
	  }*/
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		/*NativeInterface.open();
	    UIUtils.setPreferredLookAndFeel();*/
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		fileChooser = new JFileChooser();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 457, 552);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JLabel label = new JLabel();
		
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem itemOpen1 = new JMenuItem("Choose Picture");
		menuFile.add(itemOpen1);
		itemOpen1.addActionListener(new ActionListener() {

			// @Override
			public void actionPerformed(ActionEvent a) {
				// TODO Auto-generated method stub
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getPath();
					System.out.println("Path:\n"+path);
					File imagefile = new File(path);
					try {
						slider.setVisible(false);
						myImage1 = ImageIO.read(imagefile);
						//change frame size according to the image
						setSize(myImage1.getWidth(null),myImage1.getHeight(null));
					} catch (IOException e) {
						System.out.println("Error occured while reading image.");
					}
					// set image to lable icon
					label.setIcon(new ImageIcon(myImage1));
				}
			}
		});
		JMenuItem itemOpen2 = new JMenuItem("Choose Another Picture");
		menuFile.add(itemOpen2);
		itemOpen2.addActionListener(new ActionListener() {

			// @Override
			public void actionPerformed(ActionEvent a) {
				// TODO Auto-generated method stub
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getPath();
					
					File imagefile = new File(path);
					try {
						myImage2 = ImageIO.read(imagefile);
						//change frame size according to the image
						//setSize(myImage2.getWidth(null),myImage2.getHeight(null));
						slider.setVisible(true);
					} catch (IOException e) {
						System.out.println("Error occured while reading image.");
					}
					// set image to lable icon
					//label.setIcon(new ImageIcon(myImage2));
				}
			}
		});
		
		/*JMenuItem mntmOpenFlashfile = new JMenuItem("Open FlashFile");
		menuFile.add(mntmOpenFlashfile);
		//choose a flash file
		mntmOpenFlashfile.addActionListener(new ActionListener() {

			// @Override
			public void actionPerformed(ActionEvent a) {
				// TODO Auto-generated method stub
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					flashPath = fileChooser.getSelectedFile().getPath();
					
					File imagefile = new File(flashPath);
					slider.setVisible(false);
					getContentPane().add(createContent(), BorderLayout.CENTER);
					NativeInterface.runEventPump();
					//change frame size according to the image
					//setSize(myImage1.getWidth(null),myImage1.getHeight(null));
					// set image to lable icon
					//label.setIcon(new ImageIcon(myImage1));
				}
			}
		});*/
		JMenuItem itemSave = new JMenuItem("Save");
		menuFile.add(itemSave);
		itemSave.addActionListener(new ActionListener() {
			// TODO Auto-generated method stub
			public void actionPerformed(ActionEvent a) {
				slider.setVisible(false);
				int result = fileChooser.showSaveDialog(null);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getPath();
					try {
						File imgFile = new File(path);
						BufferedImage buffer = new BufferedImage(myImage3.getWidth(null),
								myImage3.getHeight(null), BufferedImage.TYPE_INT_RGB);
						Graphics2D g2 = buffer.createGraphics();
						g2.drawImage(myImage3, 0, 0, null);
						g2.dispose();
						ImageIO.write(buffer, "jpg", imgFile);
						//ImageIO.write((RenderedImage) myImage, "jpg", imgFile);
					} catch (IOException e) {
						System.out.println("Error occured while saving image.");
					}

					label.setIcon(new ImageIcon(myImage3));
				}
			}
		});
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmScale = new JMenuItem("Scale");
		mnEdit.add(mntmScale);
		mntmScale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				slider.setVisible(false);
				// TODO Auto-generated method stub
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				String message=JOptionPane.showInputDialog(null,"Input the new size: \"width height\" (seperate them with space)","Scale",JOptionPane.PLAIN_MESSAGE);
				String[] size = (message.trim()).split("\\s+|\\*|&");
				int width = Integer.parseInt(size[0]);
				int height = Integer.parseInt(size[1]);
				System.out.println("w,h:"+width+ " " + height);
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.scale(myImage1, width, height);
				if(myImage3.getWidth(null)*myImage3.getHeight(null)>200*400) {
					System.out.println("setsize to fit rescaled image");
					setSize(myImage3.getWidth(null),(int)((myImage3.getHeight(null))*1.1));
				} else {
					setSize(400,400);
				}
				label.setIcon(new ImageIcon(myImage3));
			}
			
		});
		
		JMenuItem mntmQuantize = new JMenuItem("Quantize");
		mnEdit.add(mntmQuantize);
		mntmQuantize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				slider.setVisible(false);
				// TODO Auto-generated method stub
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				String message=JOptionPane.showInputDialog(null,"Input the level for new image.  (0-255)","Quantize",JOptionPane.PLAIN_MESSAGE);
				int level = Integer.parseInt(message.trim());
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.quantize(myImage1, level);
				if(myImage3 == null) {
					System.out.println("null image!!");
				}
				label.setIcon(new ImageIcon(myImage3));
			}
		});
		JMenuItem mntmRotate = new JMenuItem("Rotate");
		mnEdit.add(mntmRotate);
		mntmRotate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				slider.setVisible(false);
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				String message=JOptionPane.showInputDialog(null,"Input rotation angle. (0-359)","Rotation",JOptionPane.PLAIN_MESSAGE);
				int angle = Integer.parseInt(message.trim());
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.rotate(myImage1, angle);
				if(myImage3 == null) {
					System.out.println("null image!!");
				}
				label.setIcon(new ImageIcon(myImage3));
			}
			
		});
		JMenu menuChanel = new JMenu("Chanel");
		menuBar.add(menuChanel);
		
		JMenuItem mntmChanelRed = new JMenuItem("Red");
		menuChanel.add(mntmChanelRed);
		mntmChanelRed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				slider.setVisible(false);
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.showChanelR(myImage1);
				if(myImage3 == null) {
					System.out.println("null image!!");
				}
				label.setIcon(new ImageIcon(myImage3));
			}
			
		});
		
		JMenuItem mntmChanelBlue = new JMenuItem("Blue");
		menuChanel.add(mntmChanelBlue);
		mntmChanelBlue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				slider.setVisible(false);
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.showChanelB(myImage1);
				if(myImage3 == null) {
					System.out.println("null image!!");
				}
				label.setIcon(new ImageIcon(myImage3));
			}
			
		});
		
		JMenuItem mntmChanelGreen = new JMenuItem("Green");
		menuChanel.add(mntmChanelGreen);
		mntmChanelGreen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				slider.setVisible(false);
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.showChanelG(myImage1);
				if(myImage3 == null) {
					System.out.println("null image!!");
				}
				label.setIcon(new ImageIcon(myImage3));
			}
			
		});
		
		JMenuItem mntmChanelGray = new JMenuItem("Gray");
		menuChanel.add(mntmChanelGray);
		mntmChanelGray.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				slider.setVisible(false);
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.showGray(myImage1);
				if(myImage3 == null) {
					System.out.println("null image!!");
				}
				label.setIcon(new ImageIcon(myImage3));
			}
		});
		
		JMenuItem mntmChanelBlackAndWhite = new JMenuItem("Black&White");
		menuChanel.add(mntmChanelBlackAndWhite);
		mntmChanelBlackAndWhite.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				slider.setVisible(false);
				if(myImage1 ==null) {
					JOptionPane.showMessageDialog(null,"No image selected yet!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				ImageProcessor ip = new ImageProcessor();
				myImage3 = ip.showBlackAndWhite(myImage1);
				if(myImage3 == null) {
					System.out.println("null image!!");
				}
				label.setIcon(new ImageIcon(myImage3));
			}
			
		});
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(label);
		
		slider = new JSlider(0,100);
		slider.setVisible(false);
		slider.setToolTipText("Drag the slider to transform");
		slider.setValue(0);
		
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				ImageProcessor myImageProcessor = new ImageProcessor();
				myImage3 = myImageProcessor.transform(slider.getValue(),myImage1,myImage2);
				label.setIcon(new ImageIcon(myImage3));
			}
		});
		contentPane.add(slider, BorderLayout.SOUTH);
	}

}
