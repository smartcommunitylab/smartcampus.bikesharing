package smartcampus.activity;

import org.osmdroid.util.GeoPoint;

import smartcampus.activity.MainActivity.OnPositionAquiredListener;
import smartcampus.activity.MainActivity.onBackListener;
import smartcampus.asynctask.GetStationsTask;
import smartcampus.asynctask.GetStationsTask.AsyncStationResponse;
import smartcampus.model.Station;
import smartcampus.util.StationsAdapter;
import smartcampus.util.StationsHelper;
import smartcampus.util.Tools;
import smartcampus.util.StationsAdapter.OnFavouritesChanged;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import eu.trentorise.smartcampus.bikesharing.R;

public class FavouriteFragment extends ListFragment implements onBackListener, OnFavouritesChanged {
	private StationsAdapter adapter;
	private View empty;

	private OnStationSelectListener mCallback;

	// Container Activity must implement this interface
	public interface OnStationSelectListener {
		public void onStationSelected(Station station, boolean animation);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnStationSelectListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString()
							+ " must implement FavouriteFragment.OnStationSelectListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		getActivity().getActionBar().setTitle(getString(R.string.favourites));

		((MainActivity) getActivity())
				.setOnPositionAquiredListener(new OnPositionAquiredListener() {

					@Override
					public void onPositionAquired() {
						adapter.notifyDataSetChanged();
					}
				});
		/*
		 * //If the distance is already defined the list is sorted by distance,
		 * otherwise //is sorted by available bikes if
		 * (mStations.get(0).getDistance()==Station.DISTANCE_NOT_VALID)
		 * sortByAvailableBikes(false); else sortByDistance(false);
		 */
		// refreshDatas();

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Station s = (Station) l.getItemAtPosition(position);
		GeoPoint p = ((MainActivity) getActivity()).getCurrentLocation();
		Intent i = new Intent(getActivity(), DetailsActivity.class);
		i.putExtra(DetailsActivity.EXTRA_STATION, s);
		if (p != null) {
			i.putExtra(DetailsActivity.EXTRA_POSITION,
					new double[] { p.getLatitude(), p.getLongitude() });
		}
		startActivity(i);
		getActivity().overridePendingTransition(R.anim.alpha_in,
				R.anim.alpha_out);
	}

	@Override
	public void changedFavourite(Station station) {
		adapter = new StationsAdapter(getActivity(), 0, StationsHelper.getFavourites(), this);
		setListAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.favourites);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adapter = new StationsAdapter(getActivity(), 0,
				StationsHelper.getFavourites(), this);
		setListAdapter(adapter);
		View rootView = inflater.inflate(R.layout.fav_stations, null);
		empty = rootView.findViewById(android.R.id.empty);

		return rootView;
	}

	private void refreshDatas() {
		GetStationsTask getStationsTask = new GetStationsTask(getActivity());
		getStationsTask.delegate = new AsyncStationResponse() {

			@Override
			public void processFinish(int status) {
				adapter.notifyDataSetChanged();
				if (((MainActivity) getActivity()).getCurrentLocation() != null)
					((MainActivity) getActivity()).updateDistances();
				onRefreshComplete();
				if (status != GetStationsTask.NO_ERROR) {
					Toast.makeText(getActivity(), getString(R.string.error),
							Toast.LENGTH_SHORT).show();
				}
				Log.d("Server call finished", "status code: " + status);
			}
		};
		getStationsTask.execute("");
	}

	private void onRefreshComplete() {
		Log.i("STR", "onRefreshComplete");
		adapter.notifyDataSetChanged();
		// Stop the refreshing indicator
	}

	@Override
	public void onStart() {
		super.onStart();
		getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
		getListView()
				.setDividerHeight(Tools.convertDpToPixel(getActivity(), 5));
		getListView().setEmptyView(empty);
//		getListView().setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				mCallback.onStationSelected(
//						StationsHelper.sStations.get(position), true);
//			}
//		});
	}

	@Override
	public void onBackPressed() {
		getFragmentManager().popBackStack();
	}

}
