import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Desktop;

public class SlideShow extends JFrame {

	//Declare Variables
	private JPanel slidePane = new JPanel();
	private JPanel textPane = new JPanel();
	private JPanel buttonPane = new JPanel();
	private JPanel southPane  = new JPanel(); // new code
	private CardLayout card = new CardLayout();
	private CardLayout cardText = new CardLayout();
	private JButton btnPrev = new JButton();
	private JButton btnNext = new JButton();

	// Created a new arrays list for each slide with description, image and url link to redirect to the website.
	private List<Slide> slides = Arrays.asList(
			new Slide(
					"<font size='5'>#1 Esalen Institute in California.</font> <br>Take this journey embark on a personal journey to nourish the mind, body and soul.",
					"/resources/Slide1.jpg",
					"https://www.esalen.org/visit/campus-features#meditation-hut"),
			new Slide(
					"<font size='5'>#2 Kamalaya Koh Samui in Thailand. </font> <br>Come and relieve any stress, anxiety and other issues through a spirited journey to find balance in your life.",
					"/resources/SLide2.jpg",
					"https://kamalaya.com/"),
			new Slide(
					"<font size='5'>#3 Mandarin Oriental, Marrakech in Morocco. </font> <br>Come and explore the beauty that Morocco has to offer and visit the spa, detox and wellness portion.",
					"/resources/Slide3.jpeg",
					"https://www.mandarinoriental.com/en/marrakech/la-medina/offers"),
			new Slide(
					"<font size='5'>#4 Sha Wellness in Spain </font> <br> Come visit this therapeutic and beautiful experience in Spain.",
					"/resources/Slide4.jpg",
					"https://shawellness.com/en/locations/sha-spain/"),
			new Slide(
					"<font size='5'>#5 The Pearl Laguna in California. </font> <br>Beautiful beach views and therapeutic healing journey that you will find at Laguna Beach in California.",
					"/resources/Slide5.jpeg",
					"https://www.thepearllaguna.com/")
	);
	
	/**
	 * Create the application.
	 */
	public SlideShow() throws HeadlessException {
		initComponent();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initComponent() {
		//Moved previous empty objects into the declare variables section to clean the code.
		textPane.setBackground(Color.BLUE);
		textPane.setBounds(5, 470, 790, 50);
		textPane.setVisible(true);
		southPane.setLayout(new BoxLayout(southPane, BoxLayout.Y_AXIS)); // new code that runs the title above the buttons

		//Setup frame attributes
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Top 5 Destinations SlideShow");
		getContentPane().setLayout(new BorderLayout(10, 50));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Setting the layouts for the panels
		slidePane.setLayout(card);
		textPane.setLayout(cardText);
		
		//logic to add each of the slides and text. Deleted the for loop and implemented this structure instead. 
		slides.forEach(s -> {
			final JLabel imageLabel = new JLabel(s.getImage()); 
			// This new section helps click on the link to the slide show and redirect to the url.
			imageLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent evt) {
					try {
						Desktop.getDesktop().browse(URI.create(s.getUrl()));
					} catch (IOException e){
						e.printStackTrace();
					}
				}
			});
			slidePane.add(imageLabel);
			textPane.add(new JLabel(s.getDescription()));
		});

		getContentPane().add(slidePane, BorderLayout.CENTER);
		southPane.add(textPane); // new code

		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

		btnPrev.setText("Previous");
		btnPrev.addActionListener((ActionEvent e) -> goPrevious()); // removed the override and used a lambda expression.
		buttonPane.add(btnPrev);

		btnNext.setText("Next");
		btnNext.addActionListener((ActionEvent e) -> goNext()); // removed the override and used a lambda expression.
		buttonPane.add(btnNext);

		southPane.add(buttonPane);
		
		getContentPane().add(southPane, BorderLayout.SOUTH); // new code
	}

	/**
	 * Previous Button Functionality
	 */
	private void goPrevious() {
		card.previous(slidePane);
		cardText.previous(textPane);
	}
	
	/**
	 * Next Button Functionality
	 */
	private void goNext() {
		card.next(slidePane);
		cardText.next(textPane);
	}
	
	/**
	 * Launch the application. Removed the else-if loop because it is declared in the arrays list
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> (new SlideShow()).setVisible(true)); // removed the override and used a lambda expression.
	}
	
	// Created a new class for the slides
	class Slide {
		private final String description;
		private final String image;
		private final String url;
		
		public Slide(final String description, final String image, final String url) {
			super();
			this.description = "<html><body>" + description + "</body></html>";
			this.image =  "<html><body><img width= '800' height='500' src='" + getClass().getResource(image) + "'</body></html>";
			this.url = url;
		}

		public String getDescription() {
			return description;
		}

		public String getImage() {
			return image;
		}

		public String getUrl() {
			return url;
		}
		
		
	}
	
}