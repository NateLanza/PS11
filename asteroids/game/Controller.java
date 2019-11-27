package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.util.Iterator;
import javax.swing.*;
import java.util.TreeMap;
import asteroids.participants.*;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener, Iterable<Participant>
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;
    
    /** The state of all keys of interest */
    private KeyStates keyStates;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;
    
    private Sounds sound;
    
    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** Number of lives left */
    private int lives = 3;

    /** Current level */
    private int level = 1;
    
    private int score = 0;

    /** The game display */
    private Display display;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Initialize the ParticipantState
        pstate = new ParticipantState();
        
        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;
        
        //Set current level
        level = 1;
        
        //Initialize sounds
        sound = new Sounds();
        
        //Create an object for the key states
        keyStates = new KeyStates();
        
        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
    }

    /**
     * This makes it possible to use an enhanced for loop to iterate through the Participants being managed by a
     * Controller.
     */
    @Override
    public Iterator<Participant> iterator ()
    {
        return pstate.iterator();
    }

    /**
     * creates next level
     */
    public void nextLevel() {
        clear();
        level++;
        display.refresh();
        placeShip();
        placeAsteroids();
        
    }
    
    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * @returns the score as an int
     */
    public int getScore() {
        return score;
    }
    
    public void updateScore(int size) {
        if (size == 2) score += 20;
        if (size == 1) score += 50;
        if (size == 0) score += 100;
    }
    
    /*
     * returns the level
     */
    public int getLevel() {
        return level;
    }
    
    
    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids ()
    {
        int j;
        int k;
        for (int i = 0; i < level + 3; i++) {
            if(i % 2 == 0) j = 150;
            else j = 600;
            if(i % 4 == 0 || i % 4 == 1) k = 600;
            else k = 150;
        addParticipant(new Asteroid((int)(Math.random()*4), 2, j, k, 3, this));
        }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        clear();
        
        level = 1;
        score = 0;
        lives = 3;

        // Place asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        //Play sounds
        sound.play("bangShip");
        
        // Null out the ship
        ship = null;

        // Display a legend
        display.setLegend("Ouch!");

        // Decrement lives
        lives--;

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed (int size)
    {
        //Play the correct sound based on asteroid size
        if (size == 0) sound.play("bangSmall");
        else if (size == 1) sound.play("bangMedium");
        else sound.play("bangLarge");
        
        // If all the asteroids are gone, schedule a transition
        if (countAsteroids() == 0)
        {
            scheduleTransition(END_DELAY);
            nextLevel();
        }
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
            keyStates.offAll();
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();
            
            //Perform ship actions if not already doing so
            if (ship != null) {
                // Move the ship
                if (keyStates.thrust()) {
                    ship.accelerate();
                    sound.play("thrust");
                }
                if (!(keyStates.left() && keyStates.right())) {
                    if (keyStates.left()) ship.turnLeft();
                    if (keyStates.right()) ship.turnRight();
                }
                
                //Fire bullet
                if (keyStates.fire() && Bullet.bulletCount <= BULLET_LIMIT) {
                    sound.play("fire");
                    double rotation = ship.getRotation();
                    addParticipant(new Bullet(ship.getXNose(), ship.getYNose(), BULLET_SPEED, rotation));
                }
            }

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Refresh screen
            display.refresh();
        }
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen.
            if (lives <= 0)
            {
                finalScreen();
            }
        }
    }

    /**
     * Returns the number of asteroids that are active participants
     */
    private int countAsteroids ()
    {
        int count = 0;
        for (Participant p : this)
        {
            if (p instanceof Asteroid)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if (ship == null) return;
        keyStates.on(e.getKeyCode());
    }

    @Override
    public void keyTyped (KeyEvent e)
    {
    }
    
    /**
     * If a key of interest is released, record that it is up.
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        if (ship == null) return;
        keyStates.off(e.getKeyCode());
        
        //Turn off ship flame if necessary
        if (!keyStates.thrust()) ship.flameOff();
    }
    
    /**
     * Represents the state of all relevant keys and returns values for the controller
     */
    private class KeyStates {
        
        /** Represents the state of all relevant keys */
        private TreeMap<Integer, Boolean> keyState;
        
        public KeyStates () {
            //Initialize all keys to false (not pressed)
            keyState = new TreeMap<Integer, Boolean>();
            keyState.put(KeyEvent.VK_W, false);
            keyState.put(KeyEvent.VK_A, false);
            keyState.put(KeyEvent.VK_S, false);
            keyState.put(KeyEvent.VK_D, false);
            keyState.put(KeyEvent.VK_UP, false);
            keyState.put(KeyEvent.VK_DOWN, false);
            keyState.put(KeyEvent.VK_LEFT, false);
            keyState.put(KeyEvent.VK_RIGHT, false);
            keyState.put(KeyEvent.VK_SPACE, false);
        }
        
        /**
         * Turns all keys off
         */
        public void offAll () {
            keyState.put(KeyEvent.VK_W, false);
            keyState.put(KeyEvent.VK_A, false);
            keyState.put(KeyEvent.VK_S, false);
            keyState.put(KeyEvent.VK_D, false);
            keyState.put(KeyEvent.VK_UP, false);
            keyState.put(KeyEvent.VK_DOWN, false);
            keyState.put(KeyEvent.VK_LEFT, false);
            keyState.put(KeyEvent.VK_RIGHT, false);
            keyState.put(KeyEvent.VK_SPACE, false); 
        }
        
        /**
         * Turns a key off 
         */
        public void off (int key) {
            keyState.put(key, false);
        }
        
        /**
         * Turns a key on
         */
        public void on (int key) {
            keyState.put(key, true);
        }
        
        /**
         * @return Whether a thrust key is on
         */
        public boolean thrust () {
            return keyState.get(KeyEvent.VK_W) || keyState.get(KeyEvent.VK_UP);
        }
        
        /**
         * @return Whether a fire key is on
         */
        public boolean fire () {
            return keyState.get(KeyEvent.VK_S) || keyState.get(KeyEvent.VK_DOWN) || keyState.get(KeyEvent.VK_SPACE);
        }
        
        /**
         * @return Whether a turn left key is on
         */
        public boolean left () {
            return keyState.get(KeyEvent.VK_A) || keyState.get(KeyEvent.VK_LEFT);
        }
        
        /**
         * @return Whether a turn right key is on
         */
        public boolean right () {
            return keyState.get(KeyEvent.VK_D) || keyState.get(KeyEvent.VK_RIGHT);
        }
        
    }
    
}
