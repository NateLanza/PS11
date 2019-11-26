package asteroids.participants;

import asteroids.destroyers.AsteroidDestroyer;
import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Bullet extends Participant implements AsteroidDestroyer
{
    
    public static int bulletCount = 0;
    
    /** The outline of the bullet*/
    private Shape outline;
    
    /**
     * Creates a new Bullet
     */
    public Bullet (double x, double y, double speed, double direction)
    {
        outline = new Ellipse2D.Double(x, y, 2., 2.);
        this.setSpeed(speed);
        this.setDirection(direction);
        bulletCount++;
        new ParticipantCountdownTimer(this, BULLET_DURATION);
    }
    
    /**
     * Returns the outline for the bullet
     */
    protected Shape getOutline () {
        return outline;
    }
    
    /**
     * Destroys the bullet if it collides with an asteroid
     */
    public void collidedWith (Participant p) {
        if (p instanceof ShipDestroyer) {
            --bulletCount;
            Participant.expire(this);
        }
    }
    
    /**
     * Called when the Bullet's timer runs out, destroys the bullet
     */
    @Override
    public void countdownComplete (Object payload) {
        --bulletCount;
        Participant.expire(this);
    }
}
