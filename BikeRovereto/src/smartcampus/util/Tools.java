package smartcampus.util;

import org.osmdroid.util.GeoPoint;

import smartcampus.model.Station;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;

public class Tools {
	public static final String METADATA_SERVICE_URL = "eu.trentorise.smartcampus.bikerovereto.SERVICE_URL";
	public static final String METADATA_CITY_CODE = "eu.trentorise.smartcampus.bikerovereto.CITY_CODE";
	public static final String METADATA_BIKE_TYPES = "eu.trentorise.smartcampus.bikerovereto.BIKE_TYPES";

	public static final String METADATA_BIKE_TYPE_EMOTION = "e-motion";
	public static final String METADATA_BIKE_TYPE_ANARCHIC = "anarchic";

	public static final long LOCATION_REFRESH_TIME = 60000;
	public static final float LOCATION_REFRESH_DISTANCE = 100;
	public static final String STATION_PREFIX = "sta";

	public static String SERVICE_URL;
	public static String CITY_CODE;
	public static String[] BIKE_TYPES;
	public static final String STATIONS_REQUEST = "stations/";
	public static final String BIKES_REQUEST = "bikes/";
	public static final String REPORTS_REQUEST = "reports/";
	public static final String REPORT_REQUEST = "report/";

	public static int convertDpToPixel(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	public static Spanned formatDistance(int distance) {
		if (distance == Station.DISTANCE_NOT_VALID) {
			return new SpannableString("N/D");
		} else if (distance < 1000) {
			return Html.fromHtml(distance + " m");
		} else {
			return Html.fromHtml(Math.round(distance / 100) / 10.0 + " Km");
		}
	}

	public static String getPathString(GeoPoint start, GeoPoint end) {
		String toRtn = "http://maps.google.com/maps?";

		if (start != null) {
			toRtn += "saddr=" + start.getLatitudeE6() / 1E6 + "," + start.getLongitudeE6() / 1E6;
			toRtn += "&";
		}
		if (end != null) {
			toRtn += "daddr=" + end.getLatitudeE6() / 1E6 + "," + end.getLongitudeE6() / 1E6;
		}
		toRtn += "&dirflg=w";
		return toRtn;
	}

	public static boolean isRuntimeAfterHoneycomb() {
		return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean bikeTypesContains(String bikeType) {
		for (int i = 0; i < BIKE_TYPES.length; i++) {
			if (bikeType.equalsIgnoreCase(Tools.BIKE_TYPES[i])) {
				return true;
			}
		}
		return false;
	}

}
