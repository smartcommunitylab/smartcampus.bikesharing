package eu.trentorise.smartcampus.bikesharing.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eu.trentorise.smartcampus.bikesharing.exceptions.InvalidCityIDException;
import eu.trentorise.smartcampus.bikesharing.exceptions.InvalidStationIDException;
import eu.trentorise.smartcampus.bikesharing.exceptions.WebServiceErrorException;
import eu.trentorise.smartcampus.bikesharing.feedback.Feedback;
import eu.trentorise.smartcampus.bikesharing.feedback.FeedbackManager;
import eu.trentorise.smartcampus.bikesharing.managers.Container;
import eu.trentorise.smartcampus.bikesharing.managers.DataManager;
import eu.trentorise.smartcampus.bikesharing.model.AnarchicBike;
import eu.trentorise.smartcampus.bikesharing.model.Station;
 
@Controller
public class WebServiceController
{
	@Autowired
	private DataManager dataManager;
	
	@Autowired
	private FeedbackManager feedBackManager;
	
	@RequestMapping(value = "/stations/{cityId:.*}/{stationId:.*}", method = RequestMethod.GET)
    public @ResponseBody Container<Station> stationService(HttpServletRequest request, @PathVariable String cityId, @PathVariable String stationId)
    {
		printRequest(request);
		
		int httpStatus = HttpStatus.OK.value();
		String errorString =  "";
		Station station = null;
		
		try
		{
			station = dataManager.getStation(cityId, stationId);
		}
		catch (InvalidCityIDException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.BAD_REQUEST.value();
			errorString =   HttpStatus.BAD_REQUEST.getReasonPhrase() + ": " + e.getMessage();
		}
		catch (InvalidStationIDException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.BAD_REQUEST.value();
			errorString =  HttpStatus.BAD_REQUEST.getReasonPhrase() + ": " + e.getMessage();
		}
		catch (WebServiceErrorException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
			errorString =  HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ": " + e.getMessage();
		}
		
        return new Container<Station>(httpStatus, errorString, station);
    }
	
	@RequestMapping(value = "/stations/{cityId:.*}", method = RequestMethod.GET)
    public @ResponseBody Container<Object[]> stationsService(HttpServletRequest request, @PathVariable String cityId)
    {
    	printRequest(request);
		
		int httpStatus = HttpStatus.OK.value();
		String errorString = "";
		Map<String, Station> stations = null;
		
		try
		{
			stations = dataManager.getStations(cityId);
		}
		catch (InvalidCityIDException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.BAD_REQUEST.value();
			errorString =  HttpStatus.BAD_REQUEST.getReasonPhrase() + ": " + e.getMessage();
		}
		catch (WebServiceErrorException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
			errorString =  HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ": " + e.getMessage();
		}
		
        return new Container<Object[]>(httpStatus, errorString, stations.values().toArray());
    }
	
	@RequestMapping(value = "/bikes/{cityId:.*}", method = RequestMethod.GET)
    public @ResponseBody Container<Object[]> anarchicBikesService(HttpServletRequest request, @PathVariable String cityId)
    {
    	printRequest(request);
		
		int httpStatus = HttpStatus.OK.value();
		String errorString =  "";
		Map<String, AnarchicBike> bikes = null;
		
		try
		{
			bikes = dataManager.getAnarchicBikes(cityId);
		}
		catch (InvalidCityIDException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.BAD_REQUEST.value();
			errorString =   HttpStatus.BAD_REQUEST.getReasonPhrase() + ": " + e.getMessage();
		}
		catch (WebServiceErrorException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
			errorString =  HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ": " + e.getMessage();
		}
		
		return new Container<Object[]>(httpStatus, errorString, bikes.values().toArray());
    }
    
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    public @ResponseBody Container<Integer> reportService(HttpServletRequest request, @RequestParam(value = "body") String feedback, @RequestParam(required=false,value="file") MultipartFile file)
    {
    	printRequest(request);
		
		int httpStatus = HttpStatus.OK.value();
		String errorString = "";
		
		byte[] byteArray = null;
		
		if(file != null)
		{
			try
			{
				byteArray = file.getBytes();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				httpStatus = HttpStatus.NOT_ACCEPTABLE.value();
				errorString = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase() + ": Cannot read image, " + e.getMessage();
			}
		}
		
		try
		{
			feedBackManager.addNewFeedback(feedback, byteArray);
		}
		catch (DataAccessResourceFailureException e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
			errorString = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ": Disconnected database, " + e.getMessage();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			httpStatus = HttpStatus.NOT_ACCEPTABLE.value();
			errorString = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase() + ": Wrong json format, " + e.getMessage();
		}
		
		return new Container<Integer>(httpStatus, errorString, 0);
    }
    
    @RequestMapping(value = "/stations/{cityId:.*}/{stationId:.*}/reports")
    public @ResponseBody Container<Feedback[]> stationsReportService(HttpServletRequest request, @PathVariable String cityId, @PathVariable String stationId)
    {
    	printRequest(request);
		
		int httpStatus = HttpStatus.OK.value();
		String errorString = "";
		
		List<Feedback> res = feedBackManager.getStationFeedbacks(cityId, stationId);
		
        return new Container<Feedback[]>(httpStatus, errorString, res.toArray(new Feedback[res.size()]));
    }
    
    @RequestMapping(value = "/bikes/{cityId:.*}/{bikeId:.*}/reports")
    public @ResponseBody Container<Feedback[]> anarchicBikesReportService(HttpServletRequest request, @PathVariable String cityId, @PathVariable String bikeId)
    {
    	printRequest(request);
		
		int httpStatus = HttpStatus.OK.value();
		String errorString = "";
		
		List<Feedback> res = feedBackManager.getBikeFeedbacks(cityId, bikeId);

        return new Container<Feedback[]>(httpStatus, errorString, res.toArray(new Feedback[res.size()]));
    }
    
    private void printRequest(HttpServletRequest request)
    {
    	System.out.println("IP: " + request.getRemoteAddr());
		System.out.println("HOST: " + request.getRemoteHost());
		System.out.println("PORT: " + request.getRemotePort());
		System.out.println("URI: " + request.getRequestURI());
		System.out.println();
    }
}