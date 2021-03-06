package it.smartcommunitylab.bikesharing.rovereto.activity;

import it.smartcommunitylab.bikesharing.rovereto.R;
import smartcampus.activity.MainActivity;
import android.os.Bundle;

public class MainRoveretoActivity extends MainActivity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainClass = MainRoveretoActivity.class;
		getSupportActionBar().setTitle(R.string.app_name);
	}

	@Override
	protected int getAboutLayout() {
		return R.layout.fragment_about;
	}

	@Override
	protected int getMainTitle() {
		return R.string.app_name;
	}
}
