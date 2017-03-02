package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(1110, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 210, 50, 880, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 210, 50, 880, 500, new OpenStreetMap.OpenStreetMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
		    // (1) all of the relevant properties in the features
	    	System.out.println(f.getProperties());
		    // (2) how to get one property and use it
//	    	Object magObj = f.getProperty("magnitude");
//	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    }

	    //Create markers for each earthquake, and set properties based on earthquake magnitude
	    for (PointFeature eq: earthquakes) {
	    	SimplePointMarker newMarker = new SimplePointMarker(eq.getLocation(), eq.getProperties());
	    	Object magObj = eq.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	if (mag > 5.0) {
	    		newMarker.setColor(color(255, 0, 0)); // Red
	    		newMarker.setRadius(20);
	    	} else if (mag > 4.0) {
	    		newMarker.setColor(color(255, 255, 0));	// Yellow
	    		newMarker.setRadius(10);
	    	} else {
	    		newMarker.setColor(color(0, 0, 255)); // Blue
	    		newMarker.setRadius(5);
	    	}

	    	markers.add(newMarker);
	    }
	    map.addMarkers(markers);
	}
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// TODO: Implement this method and call it from setUp, if it helps
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// finish implementing and use this method, if it helps.
		return new SimplePointMarker(feature.getLocation());
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		int keyX = 20;
		int keyY = 50;
		int keyWidth = 170;
		int keyHeight = 180;
		int margin = 25;
		int spacingY = 50;

		// Symbols
		fill(255, 255, 255); // White
		rect(keyX, keyY, keyWidth, keyHeight);
		
		fill(255, 0, 0); // Red
		ellipse(keyX+margin, keyY+margin*2, 20, 20);

		fill(255, 255, 0); // Yellow
		ellipse(keyX+margin, keyY+margin*2+spacingY*1, 10, 10);
		
		fill(0, 0, 255); // Blue
		ellipse(keyX+margin, keyY+margin*2+spacingY*2, 5, 5);
		
		// Labels
		fill(0);
		textSize(12);
		textAlign(LEFT);
		text("Magnitude > 5.0", keyX+margin*2, 5+keyY+margin*2);
		text("Magnitude > 4.0", keyX+margin*2, 5+keyY+margin*2+spacingY*1);
		text("Magnitude < 4.0", keyX+margin*2, 5+keyY+margin*2+spacingY*2);

		// Title
		fill(0);
		textSize(16);
		textAlign(CENTER);
		text("KEY", keyX+(keyWidth/2), keyY+margin);
	}
}
