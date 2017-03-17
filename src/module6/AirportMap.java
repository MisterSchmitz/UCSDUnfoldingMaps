package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	private UnfoldingMap map;
	private List<Marker> airportList;
	private List<Marker> routeList;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public static void printMap(HashMap mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry pair = (HashMap.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public void setup() {
		// setting up PApplet
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// hashmap for quicker access when matching with routes
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// put airports in hashmap with OpenFlights unique id for key
		for(PointFeature feature : features) {
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		}
		
		// TODO: Only show airports with routes
		HashMap<Integer, Location> airportsWithRoutes = new HashMap<Integer, Location>();
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
//			System.out.println(airports.containsKey(source));
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
			
			airportsWithRoutes.put(source, airports.get(source));
			airportsWithRoutes.put(dest, airports.get(dest));
			
			sl.setHidden(true);
			
			routeList.add(sl);		
		}
		
//		printMap(airportsWithRoutes);
		
		// List for markers
		airportList = new ArrayList<Marker>();

		// create markers from features
		for(PointFeature feature : features) {
			int featureId = Integer.parseInt(feature.getId());
//			System.out.println(feature.getId());
//			System.out.println(airportsWithRoutes.containsKey(featureId));

			if(airportsWithRoutes.containsKey(featureId)) {
				AirportMarker m = new AirportMarker(feature);
				m.setId(feature.getId());
				m.setRadius(5);
				airportList.add(m);
			}
			
		}
		
//		int featureId = 8130;
//		System.out.println(airportsWithRoutes.get(featureId));
		
		map.addMarkers(routeList);
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(airportList);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an airport, its routes, and associated airports
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
			hideRouteMarkers();
		}
		else if (lastClicked == null) 
		{
			checkAirportsForClick();
		}
	}
	
	// Helper method that will check if an airport marker was clicked on
	// and respond appropriately
	private void checkAirportsForClick()
	{
		if (lastClicked != null) return;
		// Loop over the airport markers to see if one of them is selected
		for (Marker m : airportList) {
			AirportMarker marker = (AirportMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other airports
				for (Marker mhide : airportList) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
//				System.out.println(lastClicked.getProperties());
				System.out.println(lastClicked.getId());
				// Hide all routes where airport is neither origin nor destination
				for (Marker mhide : routeList) {
//					mhide.setHidden(true);
					System.out.print(lastClicked.getId()+" : ");
					System.out.println(mhide.getProperty("source")+" , "+mhide.getProperty("destination"));
					if (mhide.getProperty("source").equals(lastClicked.getId()) || mhide.getProperty("destination").equals(lastClicked.getId())) {
						mhide.setHidden(false);
					}
				}
				return;
			}
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : airportList) {
			marker.setHidden(false);
		}
//		for(Marker marker : routeList) {
//			marker.setHidden(false);
//		}
	}
	
	private void hideRouteMarkers() {
		for(Marker marker : routeList) {
			marker.setHidden(true);
		}
	}	

}
