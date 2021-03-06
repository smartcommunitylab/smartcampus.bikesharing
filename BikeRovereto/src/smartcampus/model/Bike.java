package smartcampus.model;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import android.os.Parcel;
import android.os.Parcelable;

public class Bike implements Parcelable, Reportable
{

	private GeoPoint position;

	private String id;

	private ArrayList<Report> reports;
	public static final int DISTANCE_NOT_VALID = -1;
	private int distance = DISTANCE_NOT_VALID; // >=0 only when distance is

	private boolean areThereReports = false;

	// initialized

	public Bike(GeoPoint position, String id)
	{
		this.position = position;
		this.id = id;

		reports = new ArrayList<Report>();
	}

	// parcelable stuff
	public Bike(Parcel source)
	{
		position = new GeoPoint(source.readInt(), source.readInt());
		id = source.readString();
		source.readList(reports, List.class.getClassLoader());
		areThereReports = source.readInt() > 0;
	}

	public static final Parcelable.Creator<Bike> CREATOR = new Creator<Bike>()
	{

		@Override
		public Bike[] newArray(int size)
		{
			return new Bike[size];
		}

		@Override
		public Bike createFromParcel(Parcel source)
		{
			return new Bike(source);
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{

		dest.writeInt(position.getLatitudeE6());
		dest.writeInt(position.getLongitudeE6());
		dest.writeString(id);
		dest.writeList(reports);
		dest.writeInt(areThereReports ? 1 : 0);
	}

	// getters and setters
	public GeoPoint getPosition()
	{
		return new GeoPoint(position.getLatitudeE6(), position.getLongitudeE6());
	}

	public String getId()
	{
		return id;
	}

	public static BoundingBoxE6 getBoundingBox(ArrayList<Bike> bikes)
	{
		// the four edges of the bounding box

		int north = Integer.MIN_VALUE;
		int south = Integer.MAX_VALUE;
		int west = Integer.MAX_VALUE;
		int east = Integer.MIN_VALUE;

		for (Bike b : bikes)
		{
			if (b.getPosition().getLatitudeE6() > north)
			{
				north = b.getPosition().getLatitudeE6();
			}
			else if (b.getPosition().getLatitudeE6() < south)
			{
				south = b.getPosition().getLatitudeE6();
			}

			if (b.getPosition().getLongitudeE6() > east)
			{
				east = b.getPosition().getLongitudeE6();
			}
			else if (b.getPosition().getLongitudeE6() < west)
			{
				west = b.getPosition().getLongitudeE6();
			}
		}
		return new BoundingBoxE6(north, east, south, west);
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}

	public double getLatitudeDegree()
	{
		return position.getLatitudeE6() / 1E6;
	}

	public double getLongitudeDegree()
	{
		return position.getLongitudeE6() / 1E6;
	}

	@Override
	public String getType()
	{
		return Report.BIKE;
	}

	@Override
	public void addReport(Report report)
	{
		reports.add(report);
		areThereReports = true;
	}

	@Override
	public int getNReports()
	{
		return reports.size();
	}

	@Override
	public ArrayList<Report> getReports()
	{

		if (reports == null)
			return new ArrayList<Report>(); // for safety!
		return reports;
	}

	@Override
	public Report getReport(int index)
	{
		return reports.get(index);
	}

	@Override
	public String getName()
	{
		return id;
	}

	public boolean areThereReports()
	{
		return areThereReports;
	}

	public void thereAreReports(boolean b)
	{
		areThereReports = b;
	}
}
