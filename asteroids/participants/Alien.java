package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import asteroids.game.ParticipantState;

public class Alien extends Participant implements ShipDestroyer, AsteroidDestroyer
{
    /** Outline of the ship */
    private Shape outline;
    
    /** Game controller */
    private Controller controller;
    
    public Alien (int x, int y, Controller controller)
    {
        //Create outline
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
        outline = poly;
        
        //Set position and rotation
        setPosition(x, y);
        setRotation(Math.PI);
        setDirection(0);
        setSpeed(0);
        
        //Set controller
        this.controller = controller;
    }
    
    /**
     * Returns the outline of the ship
     */
    @Override
    protected Shape getOutline () {
        return outline;
    }
    
    /**
     * Destroys the ship if it collides with an asteroid or player ship
     */
    public void collidedWith (Participant p) 
    {
        if (p instanceof ShipDestroyer || p instanceof AsteroidDestroyer) {
            //Create debris
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), true));
            controller.addParticipant(new Debris(getX(), getY(), false));
            controller.addParticipant(new Debris(getX(), getY(), false));
            controller.addParticipant(new Debris(getX(), getY(), false));
            controller.addParticipant(new Debris(getX(), getY(), false));
            
            //Expire the ship
            Participant.expire(this);
        }
    }

}
