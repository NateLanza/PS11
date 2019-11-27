package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.geom.Path2D.Double;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Debris extends Participant
{
    
    /** Outline of this debris */
    private Shape outline;
    
    /**
     * Creates a new Debris object at x, y with random direction & rotation
     * @param type: true for ship debris, false for asteroid debris
     */
    public Debris (double x, double y, boolean type)
    {
        //Create outline & set position based on object type
        if (type) {
            Path2D.Double line = new Path2D.Double();
            line.moveTo(0, 0);
            line.lineTo(0, 10 + RANDOM.nextInt(10));
            outline = line;
            setPosition(x, y);
            setRotation(2. * Math.PI * RANDOM.nextDouble());
        } else {
            outline = new Ellipse2D.Double(x, y, 1., 1.);
        }
        
        //Set direction and speed randomly
        setSpeed(3 * RANDOM.nextDouble());
        setDirection(2. * Math.PI * RANDOM.nextDouble());
        
        //Debris lasts 1-3s
        new ParticipantCountdownTimer(this, 1000 + RANDOM.nextInt(2000));
    }
    
    /**
     * Returns this object's outline
     */
    protected Shape getOutline () {
        return outline;
    }
    
    /**
     * Unnecessary inherited abstract method
     */
    public void collidedWith (Participant p) {
        
    }
    
    /**
     * Called when this object's timer runs out, destroys the object
     */
    @Override
    public void countdownComplete (Object payload) {
        Participant.expire(this);
    }

}
