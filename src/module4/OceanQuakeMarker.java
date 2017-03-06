package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		// Drawing a centered square for Ocean earthquakes
		// DO NOT set the fill color.  That will be set in the EarthquakeMarker
		// class to indicate the depth of the earthquake.
		// Simply draw a centered square.
		
		// HINT: Notice the radius variable in the EarthquakeMarker class
		// and how it is set in the EarthquakeMarker constructor
		
		// TODO: Implement this method
		pg.rect(x-this.getRadius(),y-this.getRadius(),2*this.getRadius(),2*this.getRadius());
		
		// If earthquake occurred in past day, add an X through the marker:
		String age = (String) getProperty("age"); 
		if ( age.equals("Past Day") ) {
			pg.line(x-this.getRadius(), y-this.getRadius(), x+this.getRadius(), y+this.getRadius());
			pg.line(x-this.getRadius(), y+this.getRadius(), x+this.getRadius(), y-this.getRadius());
		}
	}

}
