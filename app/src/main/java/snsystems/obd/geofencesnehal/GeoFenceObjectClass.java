package snsystems.obd.geofencesnehal;

/**
 * Created by SNSSnehal on 3/15/2017.
 */

public class GeoFenceObjectClass {

    public String geo_fence_id;
    public String geo_fence_name;
    public String geo_fence_radius;
    public String getGeo_fence_lat_long;
    public String getGeo_fence_arrive_alert;
    public String getGeo_fence_depart_alert;

    public String getGeo_fence_id() {
        return geo_fence_id;
    }

    public void setGeo_fence_id(String geo_fence_id) {
        this.geo_fence_id = geo_fence_id;
    }

    public String getGeo_fence_name() {
        return geo_fence_name;
    }

    public void setGeo_fence_name(String geo_fence_name) {
        this.geo_fence_name = geo_fence_name;
    }

    public String getGeo_fence_radius() {
        return geo_fence_radius;
    }

    public void setGeo_fence_radius(String geo_fence_radius) {
        this.geo_fence_radius = geo_fence_radius;
    }

    public String getGetGeo_fence_lat_long() {
        return getGeo_fence_lat_long;
    }

    public void setGetGeo_fence_lat_long(String getGeo_fence_lat_long) {
        this.getGeo_fence_lat_long = getGeo_fence_lat_long;
    }

    public String getGetGeo_fence_arrive_alert() {
        return getGeo_fence_arrive_alert;
    }

    public void setGetGeo_fence_arrive_alert(String getGeo_fence_arrive_alert) {
        this.getGeo_fence_arrive_alert = getGeo_fence_arrive_alert;
    }

    public String getGetGeo_fence_depart_alert() {
        return getGeo_fence_depart_alert;
    }

    public void setGetGeo_fence_depart_alert(String getGeo_fence_depart_alert) {
        this.getGeo_fence_depart_alert = getGeo_fence_depart_alert;
    }

    public GeoFenceObjectClass() {
    }

    public GeoFenceObjectClass(String geo_fence_id, String geo_fence_name, String geo_fence_radius, String getGeo_fence_lat_long
            , String getGeo_fence_arrive_alert, String getGeo_fence_depart_alert) {
        this.geo_fence_name = geo_fence_name;
        this.geo_fence_radius = geo_fence_radius;
        this.getGeo_fence_lat_long = getGeo_fence_lat_long;
        this.geo_fence_id = geo_fence_id;
        this.getGeo_fence_arrive_alert = getGeo_fence_arrive_alert;
        this.getGeo_fence_depart_alert = getGeo_fence_depart_alert;
    }
}