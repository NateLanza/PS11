package asteroids.game;

import javax.swing.*;
import static asteroids.game.Constants.*;
import java.awt.*;

/**
 * Defines the top-level appearance of an Asteroids game.
 */
@SuppressWarnings("serial")
public class Display extends JFrame
{
    /** The area where the action takes place */
    private Screen screen;

    /**
     * Lays out the game and creates the controller
     */
    public Display (Controller controller)
    {
        // Title at the top
        setTitle(TITLE);

        // Default behavior on closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main playing area and the controller
        screen = new Screen(controller);
        
        // This panel shows the amount of lives left with ships
        JPanel livesPanel = new JPanel();
        ImageIcon icon = new ImageIcon("/Users/admin 1/Desktop/NOAH.png");
        ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() / 12,
                icon.getIconHeight() / 12, Image.SCALE_SMOOTH));
        JLabel Icon = new JLabel(scaledIcon);
        JLabel Icon2 = new JLabel(scaledIcon);
        JLabel Icon3 = new JLabel(scaledIcon);
        livesPanel.add(Icon);
        livesPanel.add(Icon2);
        livesPanel.add(Icon3);
        
        // This panel shows the level int
        JPanel levelPanel = new JPanel();
        JLabel levelLabel = new JLabel("1"/*Controller.level*/);
        levelLabel.setForeground(Color.white);
        levelLabel.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        levelPanel.add(levelLabel);
        
        // This panel shows the Score int
        JPanel scorePanel = new JPanel();
        JLabel scoreLabel = new JLabel("69420");
        scoreLabel.setForeground(Color.white);
        scoreLabel.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        scorePanel.add(scoreLabel);

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
        livesPanel.setBounds(0, 50, 200, 50);
        
        levelPanel.setOpaque(false);
        scorePanel.setOpaque(false);
        livesPanel.setOpaque(false);

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
     * Called when it is time to update the screen display. This is what drives the animation.
     */
    public void refresh ()
    {
        screen.repaint();
    }

    /**
     * Sets the large legend
     */
    public void setLegend (String s)
    {
        screen.setLegend(s);
    }
}
