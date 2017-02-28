package demos;

/** 
 * A class to illustrate class design and use.
 * Used in module 2 of the UC San Diego MOOC Object Oriented Programming in Java
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * 
 *
 */
public class SimpleLocation
{
    private double latitude;		// Member variables: data the objects need to store
    private double longitude;
    
    public double getLatitude() {
    	return this.latitude;
    }
    public void setLatitude(double lat) {
    	if (lat < -180 || lat > 180) {
    		System.out.println("Illegal value for latitude.");
    	}
    	else {
        	this.latitude = lat;    		
    	}
    }
    
    public double getLongitude() {
    	return this.longitude;
    }
    public void setLongitude(double lng) {
    	if (lng < -45 || lng > 45) {
    		System.out.println("Illegal value for longitude.");
    	}
    	else {
        	this.longitude = lng;    		
    	}
    }
    
    public SimpleLocation()	// Default Constructor
    {
        this.latitude = 32.9;
        this.longitude = -117.2;
    }
   
    public SimpleLocation(double latIn, double lonIn)	// Param Constructor: Method to create new object
    {
        this.latitude = latIn;
        this.longitude = lonIn;
    }
    
    // Method to return the distance in km between this SimpleLocation and the 
    // parameter other
    public double distance(SimpleLocation other)
    {
        return getDist(this.latitude, this.longitude,
                       other.latitude, other.longitude);          
    }

    public double distance(double otherLat, double otherLon)	// In case user doesn't want to create a SimpleLocation object
    {
        return getDist(this.latitude, this.longitude,
                       otherLat, otherLon);          
    }
    
    private double getDist(double lat1, double lon1, double lat2, double lon2)
    {
    	int R = 6373; // radius of the earth in kilometres
    	double lat1rad = Math.toRadians(lat1);
    	double lat2rad = Math.toRadians(lat2);
    	double deltaLat = Math.toRadians(lat2-lat1);
    	double deltaLon = Math.toRadians(lon2-lon1);

    	double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
    	        Math.cos(lat1rad) * Math.cos(lat2rad) *
    	        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    	double d = R * c;
    	return d;
    }
   
}
