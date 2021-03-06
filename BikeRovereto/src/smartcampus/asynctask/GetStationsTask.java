package smartcampus.asynctask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import smartcampus.model.Report;
import smartcampus.model.Station;
import smartcampus.util.StationsHelper;
import smartcampus.util.Tools;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class GetStationsTask extends AsyncTask<String, Void, Void> {

	private static final String STATION_NAME = "name";
	private static final String STATION_STREET = "street";
	private static final String STATION_LATITUDE = "latitude";
	private static final String STATION_LONGITUDE = "longitude";
	private static final String AVAILABLE_BIKES = "nBikes";
	private static final String MAX_SLOTS = "maxSlots";
	private static final String BROKEN_SLOTS = "nBrokenBikes";
	private static final String STATION_ID = "id";
	private static final String REPORTS_NUMBER = "reportsNumber";

	public static final int NO_ERROR = 0;
	public static final int ERROR_SERVER = 1;
	public static final int ERROR_CLIENT = 2;

	private int currentStatus;

	private Context context;

	public GetStationsTask(Context context) {
		this.context = context;
	}

	public interface AsyncStationResponse {
		void processFinish(int status);
	}

	public AsyncStationResponse delegate = null;

	@Override
	protected Void doInBackground(String... data) {

		String responseJSON;
		if(StationsHelper.sStations==null){
			StationsHelper.sStations=new ArrayList<Station>();
		}
		else{
			StationsHelper.sStations.clear();
		}
	
		try {
			HttpGet httpg;
			httpg = new HttpGet(Tools.SERVICE_URL + Tools.STATIONS_REQUEST
					+ Tools.CITY_CODE + "/"
					+ URLEncoder.encode(data[0], "utf-8"));
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

			HttpResponse response = httpClient.execute(httpg);
			responseJSON = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			currentStatus = ERROR_CLIENT;
			return null;
		} catch (IOException e) {
			currentStatus = ERROR_CLIENT;
			return null;
		}
		try {
			SharedPreferences pref = context.getSharedPreferences(
					"favStations", Context.MODE_PRIVATE);
			JSONObject container = new JSONObject(responseJSON);
			int httpStatus = container.getInt("httpStatus");
			if (httpStatus == 200)
				currentStatus = NO_ERROR;
			else
				currentStatus = ERROR_SERVER;
			String errorString = container.getString("errorString");
			if (data[0] == "") {
				JSONArray stationsArrayJSON = container.getJSONArray("data");
				for (int i = 0; i < stationsArrayJSON.length(); i++) {
					JSONObject stationJSON = stationsArrayJSON.getJSONObject(i);
					String name = stationJSON.getString(STATION_NAME);
					String street = stationJSON.getString(STATION_STREET);
					Double latitude = stationJSON.getDouble(STATION_LATITUDE);
					Double longitude = stationJSON.getDouble(STATION_LONGITUDE);
					int availableBikes = stationJSON.getInt(AVAILABLE_BIKES);
					int maxSlots = stationJSON.getInt(MAX_SLOTS);
					int brokenSlots = stationJSON.getInt(BROKEN_SLOTS);
					int reportsNumber = stationJSON.getInt(REPORTS_NUMBER);
					String id = stationJSON.getString(STATION_ID);
					Station station = new Station(new GeoPoint(latitude,
							longitude), name, street, maxSlots, availableBikes,
							brokenSlots, id);
					boolean fav = pref.getBoolean(Tools.STATION_PREFIX + id,
							false);
					station.setFavourite(fav);
					station.setUsedSlots(availableBikes);
					station.thereAreReports(reportsNumber > 0);
					StationsHelper.sStations.add(station);
				}
			} else {
				JSONObject stationJSON = container.getJSONObject("data");
				String name = stationJSON.getString(STATION_NAME);
				String street = stationJSON.getString(STATION_STREET);
				Double latitude = stationJSON.getDouble(STATION_LATITUDE);
				Double longitude = stationJSON.getDouble(STATION_LONGITUDE);
				int availableBikes = stationJSON.getInt(AVAILABLE_BIKES);
				int maxSlots = stationJSON.getInt(MAX_SLOTS);
				int brokenSlots = stationJSON.getInt(BROKEN_SLOTS);
				int reportsNumber = stationJSON.getInt(REPORTS_NUMBER);
				String id = stationJSON.getString(STATION_ID);
				Station station = new Station(
						new GeoPoint(latitude, longitude), name, street,
						maxSlots, availableBikes, brokenSlots, id);
				boolean fav = pref.getBoolean(Tools.STATION_PREFIX + id, false);
				station.setFavourite(fav);
				station.setUsedSlots(availableBikes);
				station.thereAreReports(reportsNumber > 0);
				StationsHelper.sStations.add(station);
			}

		} catch (JSONException e) {
			Log.e(this.getClass().getCanonicalName(), e.toString());
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (delegate != null)
			delegate.processFinish(currentStatus);

	}

}
