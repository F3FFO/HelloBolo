package com.f3ffo.hellobusbologna.output.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;

import java.util.List;

public class Maps {

    private Map map;
    private MapRoute mapRoute;

    public Maps() {
        map = null;
        mapRoute = null;
    }

    public void loadMap(Context context, SupportMapFragment mapFragment, double latitude, double longitude) {
        //mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init((OnEngineInitListener.Error error) -> {
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapFragment.getMap();
                map.setCenter(new GeoCoordinate(latitude, longitude), Map.Animation.NONE);
                map.setZoomLevel(map.getMaxZoomLevel() / 1.3);
                MapMarker myMapMarker = new MapMarker(new GeoCoordinate(latitude, longitude));
                map.addMapObject(myMapMarker);
            }
        });
        takeRealCordinates(context, latitude, longitude);
    }

    private void takeRealCordinates(Context context, double latitude, double longitude) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        LocationListener locationListener = new MapsListener(context, latitude, longitude);
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        }
        getDirections(context, latitude, longitude, location.getLatitude(), location.getLongitude());
    }

    private void getDirections(Context context, double latitude, double longitude, double latitudeReal, double longitudeReal) {
        if (map != null && mapRoute != null) {
            map.removeMapObject(mapRoute);
            mapRoute = null;
        }
        RouteManager routeManager = new RouteManager();
        RoutePlan routePlan = new RoutePlan();
        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.UNDEFINED);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);
        routePlan.addWaypoint(new GeoCoordinate(latitude, longitude));
        routePlan.addWaypoint(new GeoCoordinate(latitudeReal, longitudeReal));
        RouteManager.Error error = routeManager.calculateRoute(routePlan, routeManagerListener);
        if (error != RouteManager.Error.NONE) {
            Toast.makeText(context.getApplicationContext(), "Route calculation failed with: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private RouteManager.Listener routeManagerListener = new RouteManager.Listener() {
        public void onCalculateRouteFinished(RouteManager.Error errorCode, List<RouteResult> result) {
            if (errorCode == RouteManager.Error.NONE && result.get(0).getRoute() != null) {
                mapRoute = new MapRoute(result.get(0).getRoute());
                map.addMapObject(mapRoute);
                GeoBoundingBox gbb = result.get(0).getRoute().getBoundingBox();
                map.zoomTo(gbb, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);
            }
        }

        public void onProgress(int percentage) {
        }
    };

    public class MapsListener implements LocationListener {

        private double latitude;
        private double longitude;
        private Context context;

        public MapsListener(Context context, double latitude, double longitude) {
            this.context = context;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public void onLocationChanged(Location location) {
            double longitudeReal = location.getLongitude();
            double latitudeReal = location.getLatitude();
            getDirections(context, latitude, longitude, latitudeReal, longitudeReal);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
