package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
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

		// Only show airports with routes
		HashMap<Integer, Location> airportsWithRoutes = new HashMap<Integer, Location>();
		
		// put airports in hashmap with OpenFlights unique id for key
		for(PointFeature feature : features) {
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		}
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
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
		
		// List for markers
		airportList = new ArrayList<Marker>();

		// create markers from features
		for(PointFeature feature : features) {
			int featureId = Integer.parseInt(feature.getId());
			if(airportsWithRoutes.containsKey(featureId)) {
				AirportMarker m = new AirportMarker(feature);
				m.setId(feature.getId());
				m.setRadius(5);
				airportList.add(m);
			}
			
		}
		
		map.addMarkers(routeList);
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		displayHeader();
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
		
		// Store other airports linked to clicked airport via routes
		HashSet<String> connectingAirports = new HashSet<String>();
		
		// Loop over the airport markers to see if one of them is selected
		for (Marker m : airportList) {
			AirportMarker marker = (AirportMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all routes where airport is neither origin nor destination
				for (Marker mhide : routeList) {
					if (mhide.getProperty("source").equals(lastClicked.getId())) {
//						mhide.setStrokeColor(color(255,0,0));
						mhide.setHidden(false);
						String dest = mhide.getStringProperty("destination");
						connectingAirports.add(dest);
					}
					if (mhide.getProperty("destination").equals(lastClicked.getId())) {
						mhide.setHidden(false);
						String source = mhide.getStringProperty("source");
						connectingAirports.add(source);						
					}
				}
				break;
			}
		}
		if (lastClicked != null) {
			// Hide all airports that are not connectors
			for (Marker mhide : airportList) {
				if (mhide != lastClicked && !connectingAirports.contains(mhide.getId())) {
					mhide.setHidden(true);
				}
			}			
		}
		return;
	}
	
	// loop over and unhide all airport markers
	private void unhideMarkers() {
		for(Marker marker : airportList) {
			marker.setHidden(false);
		}
	}

	// loop over and unhide all route markers
	private void hideRouteMarkers() {
		for(Marker marker : routeList) {
			marker.setHidden(true);
		}
	}
	
	// helper method to draw Header in GUI
	private void displayHeader() {
		fill(255, 250, 240);
		
		int xbase = 425;
		int ybase = 25;
		
		fill(255);
		textAlign(CENTER, CENTER);
		textSize(24);
		
		String headerText = "";
		if(lastClicked == null)  {
			headerText = "Click Airport to Find Routes";
		} else {
			headerText = "Routes to/from "+((AirportMarker)lastClicked).getName()+" ("+((AirportMarker)lastClicked).getCode()+")";
		}
		text(headerText, xbase, ybase);
		return;
	}
}
