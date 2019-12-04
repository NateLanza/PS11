package asteroids.game;

import javax.swing.*;
import static asteroids.game.Constants.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.*;

/**
 * Defines the top-level appearance of an Asteroids game.
 */
@SuppressWarnings("serial")
public class Display extends JFrame
{
    /** The area where the action takes place */
    private Screen screen;

    JPanel scorePanel = new JPanel();
    
    JLabel scoreLabel;
    
    JLabel levelLabel;
    
    Controller controllerCopy;
    
    JPanel livesPanel = new JPanel();

    private Shape outline;
    
    Path2D.Double poly = new Path2D.Double();
    
    /**
     * Lays out the game and creates the controller
     */
    public Display (Controller controller)
    {
        controllerCopy = controller;
        
        // Title at the top
        setTitle(TITLE);

        // Default behavior on closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main playing area and the controller
         screen = new Screen(controller);
        
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
        
        // This panel shows the amount of lives left with ships
        ImageIcon icon1 = new ImageIcon("src/images/SHIP.jpg");
        ImageIcon scaledIcon = new ImageIcon(icon1.getImage().getScaledInstance(icon1.getIconWidth() / 3,
                icon1.getIconHeight() / 2, Image.SCALE_SMOOTH));
        JLabel Icon1 = new JLabel(scaledIcon);
        JLabel Icon2 = new JLabel(scaledIcon);
        Icon2.setOpaque(false);
        JLabel Icon3 = new JLabel(scaledIcon);
        Icon1.setOpaque(true);
        livesPanel.add(Icon3);
        livesPanel.add(Icon2);
        livesPanel.add(Icon1);

        JPanel livesPanel = new JPanel();
        
        //ImageIcon icon = new ImageIcon("/Users/admin 1/Desktop/NOAH.png");
        //ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() / 12,
        //        icon.getIconHeight() / 12, Image.SCALE_SMOOTH));
        ScorePanel Point1 = new ScorePanel();
        ScorePanel Point2 = new ScorePanel();
        ScorePanel Point3 = new ScorePanel();
        Point1.setSize(100, 100);
        Point2.setSize(100, 100);
        Point3.setSize(100, 100);
        //Point1.
        
        /*JLabel Icon = new JLabel(outline);
        JLabel Icon2 = new JLabel(scaledIcon);*/
        //JLabel Icon3 = new JLabel(scaledIcon);
        livesPanel.add(Point1);
        livesPanel.add(Point2);
        livesPanel.add(Point3);
        
        // This panel shows the level int
        JPanel levelPanel = new JPanel();
        levelLabel = new JLabel(controllerCopy.getLevel() + "");
        levelLabel.setForeground(Color.white);
        levelLabel.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        levelPanel.add(levelLabel);
        makeScorePanel(controller);

        // This panel contains the screen to prevent the screen from being
        // resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);
        
        JLayeredPane fullScreenPanel = new JLayeredPane();
        fullScreenPanel.setPreferredSize(new Dimension(750, 750));
        levelPanel.setSize(1450, 50);
        scorePanel.setSize(150, 50);
        screenPanel.setSize(750, 750);
        livesPanel.setBounds(0, 50, 200, 500);
        
        levelPanel.setOpaque(false);
        scorePanel.setOpaque(false);
        //livesPanel.setOpaque(false);

        fullScreenPanel.add(levelPanel, new Integer(101));
        fullScreenPanel.add(scorePanel, new Integer(100));
        fullScreenPanel.add(livesPanel, new Integer(99));
        fullScreenPanel.add(screenPanel, -1);

        // This panel contains buttons and labels
        JPanel controls = new JPanel();

        // The button that starts the game
        JButton startGame = new JButton(START_LABEL);
        controls.add(startGame);

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(fullScreenPanel, "Center");
        mainPanel.add(controls, "North");
        setContentPane(mainPanel);
        pack();

        // Connect the controller to the start button
        startGame.addActionListener(controller);
    }
    
    /**
     * creates score Panel
     */
    public void makeScorePanel(Controller controller){
        // This panel shows the Score int
        scoreLabel = new JLabel(controller.getScore() + "");
        scoreLabel.setForeground(Color.white);
        scoreLabel.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        scorePanel.add(scoreLabel);
    }

    /**
     * Called when it is time to update the screen display. This is what drives the animation.
     */
    public void refresh ()
    {
        livesPanel.setLocation(-40-((3 - controllerCopy.getLives()) * 40), 50);
        levelLabel.setText(controllerCopy.getLevel() + "");
        scoreLabel.setText(controllerCopy.getScore() + "");
        screen.repaint();
    }

    /**
     * Sets the large legend
     */
    public void setLegend (String s)
    {
        screen.setLegend(s);
    }
    
    private class ScorePanel extends JPanel {
        int[] xPoints = {420, -21, -14, -14, -21};
        int[] yPoints = {0, 12, 10, -10, -12};
        
        @Override
        public void paintComponent(Graphics g) {
            //g.fillPolygon(xPoints, yPoints, 5);
            Graphics2D g2d = (Graphics2D) g;
            g2d.fill(outline);
        }
        
    }
    
}
