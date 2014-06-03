/*
 * Copyright (C) 2014 Mukesh Y authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.androiddeveloper.googlemapsv2;

import java.util.ArrayList;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * @author Mukesh Y
 */
public class MainActivity extends Activity implements OnMapClickListener {

	// Google Map
	private GoogleMap googleMap;
	ArrayList<LatLng> latLang = new ArrayList<LatLng>();
	ArrayList<IGeoPoint> listPoints = new ArrayList<IGeoPoint>();
	boolean isGeometryClosed = false;
	Polygon polygon;
	Context context = MainActivity.this;
	boolean isStartGeometry = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			initilizeMap();
			ActionBar actionBar = this.getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setIcon(R.drawable.ic_launcher);
		} catch (Exception e) {
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.map_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_normal:
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			return true;

		case R.id.action_hybrid:
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			return true;

		case R.id.action_satellite:
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {

			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(this, "Sorry! unable to create maps",
						Toast.LENGTH_SHORT).show();
				return;
			}

			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			// set my location
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(false);
			googleMap.getUiSettings().setRotateGesturesEnabled(false);
			// set zooming controll
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			googleMap.setOnMapClickListener(this);
		}

	}

	public void Draw_Map() {
		PolygonOptions rectOptions = new PolygonOptions();
		rectOptions.addAll(latLang);
		rectOptions.strokeColor(Color.BLUE);
		rectOptions.fillColor(Color.CYAN);
		rectOptions.strokeWidth(7);
		polygon = googleMap.addPolygon(rectOptions);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	/**
	 * Close the Polygon / join last point to first point
	 * 
	 * @param view
	 */
	public void closePolygon(View view) {
		if (latLang.size() > 0) {
			Draw_Map();
			isGeometryClosed = true;
			isStartGeometry = false;
		}
	}

	/**
	 * Close the Polygon / join last point to first point
	 * 
	 * @param view
	 */
	public void startDrawing(View view) {
		isStartGeometry = true;
	}

	@Override
	public void onMapClick(LatLng latlan) {
		if (!isGeometryClosed && isStartGeometry) {
			latLang.add(latlan);
			GeoPoint point = new GeoPoint(latlan.latitude, latlan.longitude);
			listPoints.add((IGeoPoint) point);
			MarkerOptions marker = new MarkerOptions().position(latlan);
			googleMap.addMarker(marker);
			if (latLang.size() > 1) {
				PolylineOptions polyLine = new PolylineOptions().color(
						Color.BLUE).width((float) 7.0);
				polyLine.add(latlan);
				LatLng previousPoint = latLang.get(latLang.size() - 2);
				polyLine.add(previousPoint);
				googleMap.addPolyline(polyLine);
			}
		}
	}

	/**
	 * Clear the all draw lines
	 * 
	 * @param view
	 *            Current view of activity
	 */
	public void clearCanvas(View view) {

		try {
			AlertDialog.Builder alertdalogBuilder = new AlertDialog.Builder(
					context);

			alertdalogBuilder.setTitle("Clear");
			alertdalogBuilder
					.setMessage(
							"Do you really want to clear the geometry? This action can't be undone!")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// polygon.remove();
									googleMap.clear();
									latLang = new ArrayList<LatLng>();
									listPoints = new ArrayList<IGeoPoint>();
									isGeometryClosed = false;
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

			// Create Alert Dialog
			AlertDialog alertdialog = alertdalogBuilder.create();
			// show the alert dialog
			alertdialog.show();

		} catch (Exception e) {
		}

	}

	/**
	 * Method for Save Property
	 * 
	 * @param view
	 */
	public void savePolygon(View view) {
		try {
			if (isGeometryClosed) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setTitle("Save");
				alertDialogBuilder
						.setMessage(
								"Do you really want to save? This action can't be undone!")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										// Prop.this.finish();
										savePolygonAfterAlert();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			} else {
				showDialog(context, "Alert", "Close geometry before saving");
			}
		} catch (Exception e) {

		}

	}

	/**
	 * Save the Polygon made by user
	 * 
	 * @param view
	 */

	public void savePolygonAfterAlert() {
		// save geometry of polygon
	}

	/**
	 * Method to show the Dialog box
	 * 
	 * @param ctx
	 * @param title
	 * @param msg
	 */
	public void showDialog(Context ctx, String title, String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
		dialog.setTitle(title).setMessage(msg).setCancelable(true);
		dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = dialog.create();
		alertDialog.show();
	}
}