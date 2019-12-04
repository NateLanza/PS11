package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import asteroids.participants.AlienBullet;
import java.awt.geom.AffineTransform;

public class Alien extends Participant implements ShipDestroyer, AsteroidDestroyer
{
    /** Outline of the ship */
    private Shape outline;

    /** Game controller */
    private Controller controller;

    /** Whether this alien has been placed */
    private boolean placed;

    /** Current direction of travel- true = right, false = left */
    private boolean direction;

    /** Whether this ship is large */
    private boolean isLarge;

    /** Alien ship size constants */
    public static final boolean LARGE = true;
    public static final boolean SMALL = false;

    /** Represent different actions to take when countdown complete */
    public static final int PLACE_ALIEN = 1;
    public static final int MOVE = 0;
    public static final int FIRE = 2;

    /**
     * Possible directions of motion Indices 0-2 go right, 3-5 go left
     */
    private static final double[] DIRECTIONS = { 0., 1., 2. * Math.PI - 1., Math.PI + 0., Math.PI + 1., Math.PI - 1. };

    /**
     * Creates a new alien
     * 
     * @param controller - this game's controller
     * @param type - true for large, false for small
     * @param delay - delay in ms until ship is placed
     */
    public Alien (int x, int y, Controller controller, boolean type, int delay, boolean direction)
    {
        // Set alien type
        isLarge = type;

        // Alien not yet placed on board
        placed = false;

        // Create outline
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(20, 0);
        poly.lineTo(12, -10);
        poly.lineTo(-12, -10);
        poly.lineTo(-20, 0);
        poly.lineTo(20, 0);
        poly.lineTo(12, 10);
        poly.lineTo(-12, 10);
        poly.lineTo(-20, 0);
        poly.moveTo(9, 10);
        poly.lineTo(7, 15);
        poly.lineTo(-7, 15);
        poly.lineTo(-9, 10);
        poly.moveTo(20, 0);
        poly.closePath();
        // Set outline size based on ship type
        if (isLarge)
        {
            outline = poly;
        }
        else
        {
            AffineTransform transform = new AffineTransform();
            transform.scale(.75, .75);
            outline = transform.createTransformedShape(poly);
        }

        // Set position, rotation, and direction
        setPosition(x, y);
        setRotation(Math.PI);

        // Make sure it doesn't interact with anything for now
        setInert(true);

        // Set direction to opposite because it will be reversed in newDirection() later
        this.direction = !direction;

        // Set controller
        this.controller = controller;

        // Set a timer to place alien ship
        new ParticipantCountdownTimer(this, PLACE_ALIEN, delay);
    }

    /** Changes ship direction */
    private void newDirection ()
    {
        // Switch direction
        direction = !direction;
        if (direction)
            this.setDirection(DIRECTIONS[RANDOM.nextInt(3)]);
        else
            this.setDirection(DIRECTIONS[3 + RANDOM.nextInt(3)]);

        // Determine time until next move based on size
        int delay;
        if (isLarge)
            delay = ALIEN_TURN_DELAY + RANDOM.nextInt(2 * ALIEN_TURN_DELAY);
        else
            delay = ALIEN_TURN_DELAY / 2 + RANDOM.nextInt(ALIEN_TURN_DELAY);
        // Schedule next move
        new ParticipantCountdownTimer(this, MOVE, delay);
    }

    /** Fires a bullet */
    private void fire ()
    {
        if (controller.getShip() == null) return;
        
        double bulletDir;
        if (isLarge)
        {
            // Randomly generate bullet direction
            bulletDir = RANDOM.nextDouble() * 2. * Math.PI;

            // Add bullet
            controller.addParticipant(new AlienBullet(getX(), getY(), BULLET_SPEED, bulletDir));
        }
        else
        {
            // Calculate bullet direction based on ship location, with some randomness
            double xDist = controller.getShip().getX() - getX();
            double yDist = controller.getShip().getY() - getY();
            bulletDir = Math.atan2(yDist, xDist) + 
                    (RANDOM.nextInt(2 * ALIEN_BULLET_DEFLECTION + 1) - ALIEN_BULLET_DEFLECTION) * (Math.PI / 180.);

            // Add bullet
            controller.addParticipant(new AlienBullet(getX(), getY(), ALIEN_BULLET_SPEED, bulletDir));
        }
        
        // Play sound
        controller.playSound("fire");

        new ParticipantCountdownTimer(this, FIRE, 500 + RANDOM.nextInt(1500));
    }

    /**
     * @return The size of the current alien. 0 if not on screen, 1 for small, 2 for large
     */
    public int getSize ()
    {
        if (!placed)
            return 0;
        else if (isLarge)
            return 2;
        else
            return 1;
    }

    /**
     * Returns the outline of the ship
     */
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Destroys the ship if it collides with an asteroid or player ship
     */
    public void collidedWith (Participant p)
    {
        if ((p instanceof ShipDestroyer || p instanceof AsteroidDestroyer) && !(p instanceof AlienBullet))
        {
            // Create debris
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), false));
            controller.addParticipant(new Debris(getX(), getY(), false));
            controller.addParticipant(new Debris(getX(), getY(), false));
            controller.addParticipant(new Debris(getX(), getY(), false));

            // Tell the controller
            controller.alienShipDestroyed(isLarge);

            // Expire the ship
            Participant.expire(this);
        }
    }

    /**
     * Called when timer runs out
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Payload says to place this alien
        if (payload.equals(PLACE_ALIEN))
        {
            controller.addParticipant(this);
            placed = true;

            // Allow interaction
            setInert(false);

            // Set speed based on size
            if (isLarge)
                this.setSpeed(ALIEN_SPEED);
            else
                this.setSpeed(ALIEN_SPEED * ALIEN_SPEED_MODIFIER);

            // Start moving
            newDirection();

            // Fire a bullet
            fire();

            // Stop further execution
            return;
        }

        // Payload says change direction
        if (payload.equals(MOVE))
        {
            newDirection();
            return;
        }

        // Payload says fire
        if (payload.equals(FIRE))
        {
            fire();
        }
    }

}
