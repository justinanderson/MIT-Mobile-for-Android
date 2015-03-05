package edu.mit.mitmobile2.maps;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.graphics.*;
import android.content.res.Resources;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.maps.android.ui.IconGenerator;


import edu.mit.mitmobile2.R;

public class MITMapView {

    private GoogleMap mMap;
    private MapFragment mapFragment;
    private MapItem  mItem;
    public static String MAP_ITEMS = "MAP_ITEMS";
    private FragmentManager mFm;
    private int mapResourceId;
    private Marker lastClickedMarker;

    //set initial latlng for zoom in MIT area
    final LatLng initialLatLng = new LatLng(42.359858, -71.09913);
    public static final int INITIAL_ZOOM = 14;
    private Context mContext;

    public MITMapView(Context mContext, FragmentManager fm, int mapResourceId) {
        this.mContext = mContext;
        this.mFm = fm;
        this.mapResourceId = mapResourceId;
        this.mapFragment = (MapFragment) fm.findFragmentById(mapResourceId);
        mMap = this.mapFragment.getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, MITMapView.INITIAL_ZOOM));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private ArrayList<MapItem> mapItems;

    public void show() {
       Fragment f = mFm.findFragmentById(mapResourceId);
        mFm.beginTransaction()
                .show(f)
                .commit();
    };

    public void hide() {
        Fragment f = mFm.findFragmentById(mapResourceId);
        mFm.beginTransaction()
                .hide(f)
                .commit();
    }

    public void addMapItem(MapItem mItem) {
        if (mMap != null) {
            if (MapItem.class.isAssignableFrom(mItem.getClass())) {
                int type = mItem.getMapItemType();

                switch (type) {
                    case 0:
                        break;

                    case MapItem.MARKERTYPE:
                        if (mItem.getMarkerText() != null) {
                            IconGenerator iconGenerator = new IconGenerator(mContext);
                            iconGenerator.setBackground(mContext.getResources().getDrawable(R.drawable.usermarker));

                            iconGenerator.setTextAppearance(10); //set font size?
                            Bitmap bitmap = iconGenerator.makeIcon(mItem.getMarkerText());
                            mMap.addMarker(mItem.getMarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        } else {
                            mMap.addMarker(mItem.getMarkerOptions());
                        }
                        break;

                    case MapItem.POLYGONTYPE:
                        mMap.addPolyline(mItem.getPolylineOptions());
                        break;


                    case MapItem.POLYLINETYPE:
                        mMap.addPolygon(mItem.getPolygonOptions());
                        break;

                }
            }
        }
    }

    public void addMapItemList(ArrayList<MapItem> mapItems, Boolean clear,Boolean fit) {
        this.mapItems = mapItems;
        if (clear) {
            mMap.clear();
        }
        if (mMap != null && mapItems != null && mapItems.size()>0) {
            Iterator<MapItem> iterator = mapItems.iterator();
            while (iterator.hasNext()) {
                MapItem item = (MapItem) iterator.next();
                addMapItem(item);
            }

            if (fit) {
                this.fitMapItems();
            }
        }
    }

    public void addMapItemList(ArrayList<MapItem> mapItems) {
        addMapItemList(mapItems, true,true);
    }

    public void fitMapItems() {

        //Calculate the markers to get their position
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (int i = 0; i < mapItems.size(); i++) {
            MapItem mItem = mapItems.get(i);
            if (mItem.getMapItemType() == MapItem.MARKERTYPE) {
                b.include(mItem.getMarkerOptions().getPosition());
            }
        }
        LatLngBounds bounds = b.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25,25,5);
        mMap.moveCamera(cu);
    }

    public GoogleMap getMap() {
        return this.mMap;
    }

    public void toggle() {
        if (isExpanded()) {
            float map_height = mContext.getResources().getDimension(R.dimen.map_height);
            mapFragment.getView().setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)map_height));
        }
        else {
            mapFragment.getView().setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    public Boolean isExpanded() {
        // return true if the current map height != the collapsed map height defined at R.dimen.map_height
        float h = mapFragment.getView().getHeight();
        float map_height = mContext.getResources().getDimension(R.dimen.map_height);
        return (h != map_height);
    }

    public void showLocation() {
        Location location = mMap.getMyLocation();
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(MITMapView.INITIAL_ZOOM).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public Marker getLastClickedMarker() {
        return lastClickedMarker;
    }

    public void setLastClickedMarker(Marker lastClickedMarker) {
        this.lastClickedMarker = lastClickedMarker;
    }
}
