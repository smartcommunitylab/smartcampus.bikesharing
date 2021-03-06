package smartcampus.util;

import java.util.ArrayList;
import java.util.Iterator;

import smartcampus.model.Station;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import eu.trentorise.smartcampus.bikesharing.R;

public class StationsAdapter extends ArrayAdapter<Station> {

	private ArrayList<Station> mStations;
	private SharedPreferences pref;
	private boolean mFavourite;
	private OnFavouritesChanged mListener;

	public interface OnFavouritesChanged {
		public void changedFavourite(Station station);
	}
	public StationsAdapter(Context context, int resource, ArrayList<Station> stations, OnFavouritesChanged listener) {
		super(context, resource, stations);
		mStations = stations;
		pref = context
				.getSharedPreferences("favStations", Context.MODE_PRIVATE);
		this.mListener = listener;
	}
	
	public StationsAdapter(Context context, int resource,
			ArrayList<Station> stations) {
		this(context,resource,stations, null);
	}

	public StationsAdapter(Context context, int resource,
			ArrayList<Station> stations, boolean fav) {
		super(context, resource, stations);
		mStations = stations;
		this.mFavourite = fav;
		pref = context
				.getSharedPreferences("favStations", Context.MODE_PRIVATE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) getContext())
					.getLayoutInflater();
			convertView = inflater.inflate(R.layout.stations_model, parent,
					false);

			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.street = (TextView) convertView
					.findViewById(R.id.street);
			viewHolder.availableBike = (TextView) convertView
					.findViewById(R.id.available_bikes);
			viewHolder.availableSlots = (TextView) convertView
					.findViewById(R.id.available_slots);
			viewHolder.distance = (TextView) convertView
					.findViewById(R.id.distance);
			viewHolder.favouriteBtn = (CheckBox) convertView
					.findViewById(R.id.favourites_btn);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Station thisStation = mStations.get(position);

		viewHolder.name.setText(thisStation.getName());
		viewHolder.street.setText(thisStation.getStreet());
		viewHolder.availableBike.setText(thisStation.getNBikesPresent() + "");
		viewHolder.availableSlots.setText(thisStation.getNSlotsEmpty() + "");
		viewHolder.distance.setText(Tools.formatDistance(thisStation
				.getDistance()));
		viewHolder.favouriteBtn.setChecked(thisStation.getFavourite());
		viewHolder.favouriteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				thisStation.setFavourite(!thisStation.getFavourite());
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(Tools.STATION_PREFIX + thisStation.getId(),
						thisStation.getFavourite());
				editor.apply();
				if (mFavourite) {
					notifyDataSetChanged();
					StationsHelper.updateStation(thisStation);
				}
				if (StationsAdapter.this.mListener != null) {
					StationsAdapter.this.mListener.changedFavourite(thisStation);
				}
			}
		});
		return convertView;

	}

	private static class ViewHolder {
		TextView name;
		TextView street;
		TextView availableBike, availableSlots;
		TextView distance;
		CheckBox favouriteBtn;

	}

}
