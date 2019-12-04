package asteroids.participants;

import asteroids.participants.Bullet;
import static asteroids.game.Constants.*;
import asteroids.destroyers.*;
import asteroids.game.Participant;

public class AlienBullet extends Bullet implements ShipDestroyer
{

    /**
     * Create a new bullet from an alien ship
     */
    public AlienBullet (double x, double y, double speed, double direction)
    {
        //Create bullet with superclass constructor
        super(x, y, speed, direction, ALIEN_BULLET_DURATION);
        
        //Make sure that superclass doesn't increment bullet count
        Bullet.bulletCount--;
    }
    
    /**
     * Destroys the bullet if it collides with anything
     */
    @Override
    public void collidedWith (Participant p) {
        if ((p instanceof ShipDestroyer || p instanceof AsteroidDestroyer) && !(p instanceof Alien))
            Participant.expire(this);
    }

}
