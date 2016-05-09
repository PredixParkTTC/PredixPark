package com.ge.predix.park.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.park.ParkDataAPI;
import com.ge.predix.solsvc.restclient.impl.CxfAwareRestClient;
import com.ge.predix.solsvc.spi.IServiceManagerService;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesRestConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesWSConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;
import com.ge.predix.solsvc.timeseries.bootstrap.websocket.client.TimeseriesWebsocketClient;
import com.ge.predix.timeseries.entity.datapoints.ingestionrequest.Body;
import com.ge.predix.timeseries.entity.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.timeseries.entity.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.timeseries.entity.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.timeseries.entity.datapoints.queryresponse.Results;
import com.ge.predix.timeseries.entity.datapoints.queryresponse.Tag;

/**
 * 
 * @author Betul Turkicin 
 */
@Component
public class ParkDataImpl implements ParkDataAPI {

	public static final Integer PARKING_STATUS_UNKNOWN = -1;
	public static final Integer PARKING_STATUS_FULL = 1;
	public static final Integer PARKING_STATUS_EMPTY = 1;
	
	@Autowired
	private IServiceManagerService serviceManagerService;

	@Autowired
	private TimeseriesRestConfig timeseriesRestConfig;

	@Autowired
	private CxfAwareRestClient restClient;

	@Autowired
	private TimeseriesWSConfig tsInjectionWSConfig;

	@Autowired
	private TimeseriesWebsocketClient timeseriesWebsocketClient;

	@Autowired
	private TimeseriesFactory timeseriesFactory;


	private static Logger log = LoggerFactory.getLogger(ParkDataImpl.class);

	/**
	 * -
	 */
	public ParkDataImpl() {
		super();
	}

	/**
	 * -
	 */
	@PostConstruct
	public void init() {
		this.serviceManagerService.createRestWebService(this, null);
		//createMetrics();
	}

	@Override
	public Response greetings() {
		return handleResult("Greetings from Parking Presence Rest Service " + new Date()); //$NON-NLS-1$
	}


	@Override
	public Response getLatestParkingSlotTimeseriesDataPoints(String id, String authorization) {

		DatapointsResponse response = getLatestDataPointsResponse(id, authorization);

		log.debug(response.toString());

		return handleResult(response);
	}
	
	private DatapointsResponse getLatestDataPointsResponse(String id, String authorization){
		if (id == null) {
			return null;
		}
		List<Header> headers = generateHeaders();
		DatapointsLatestQuery dpQuery = buildLatestDatapointsQueryRequest(id);
		
		DatapointsResponse response = this.timeseriesFactory
				.queryForLatestDatapoint(
						this.timeseriesRestConfig.getBaseUrl(), dpQuery,
						headers);
		return response;
	}
	

	@Override
	public Response getLatestParkingSlotStatus(String id, String authorization) {
		Integer parkingStatus = -1;
		DatapointsResponse response = getLatestDataPointsResponse(id, authorization);
		List<Tag> tags = response.getTags();
		if(!CollectionUtils.isEmpty(tags)) {
			Tag tag = tags.get(0);
			List<Results> results = tag.getResults();
			if(!CollectionUtils.isEmpty(results)){
				Results result = results.get(0);
				List<Object> values = result.getValues();
				log.info("Values:" + values);
				if(!CollectionUtils.isEmpty(values)){
					List valueList = (List)values.get(0);
					if(valueList != null && valueList.size() > 1 ){
						parkingStatus=(Integer)valueList.get(1);
						log.info("Parking status:"+parkingStatus);
					}
					
				}
			}
		}
		return handleResult(parkingStatus);
	}

	@SuppressWarnings({ "unqualified-field-access", "nls" })
	private List<Header> generateHeaders()
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        
		this.restClient.addZoneToHeaders(headers,
				this.timeseriesRestConfig.getZoneId());
        return headers;
    }


	private DatapointsLatestQuery buildLatestDatapointsQueryRequest(String id) {
		DatapointsLatestQuery datapointsLatestQuery = new DatapointsLatestQuery();

		com.ge.predix.timeseries.entity.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.timeseries.entity.datapoints.queryrequest.latest.Tag();
		tag.setName(id);
		List<com.ge.predix.timeseries.entity.datapoints.queryrequest.latest.Tag> tags = new ArrayList<com.ge.predix.timeseries.entity.datapoints.queryrequest.latest.Tag>();
		tags.add(tag);
		datapointsLatestQuery.setTags(tags);
		return datapointsLatestQuery;
	}



	@SuppressWarnings({ "nls", "unchecked" })
	private void createMetrics() {
		for (int i = 0; i < 10; i++) {
			DatapointsIngestion dpIngestion = new DatapointsIngestion();
			dpIngestion
					.setMessageId(String.valueOf(System.currentTimeMillis()));

			Body body = new Body();
			body.setName("Compressor-2015:CompressionRatio"); //$NON-NLS-1$
			List<Object> datapoint1 = new ArrayList<Object>();
			datapoint1.add(generateTimestampsWithinYear(System
					.currentTimeMillis()));
			datapoint1.add(10);
			datapoint1.add(3); // quality

			List<Object> datapoint2 = new ArrayList<Object>();
			datapoint2.add(generateTimestampsWithinYear(System
					.currentTimeMillis()));
			datapoint2.add(9);
			datapoint2.add(1); // quality

			List<Object> datapoint3 = new ArrayList<Object>();
			datapoint3.add(generateTimestampsWithinYear(System
					.currentTimeMillis()));
			datapoint3.add(27);
			datapoint3.add(0); // quality

			List<Object> datapoint4 = new ArrayList<Object>();
			datapoint4.add(generateTimestampsWithinYear(System
					.currentTimeMillis()));
			datapoint4.add(78);
			datapoint4.add(2); // quality

			List<Object> datapoint5 = new ArrayList<Object>();
			datapoint5.add(generateTimestampsWithinYear(System
					.currentTimeMillis()));
			datapoint5.add(2);
			datapoint5.add(3); // quality

			List<Object> datapoint6 = new ArrayList<Object>();
			datapoint6.add(generateTimestampsWithinYear(System
					.currentTimeMillis()));
			datapoint6.add(98);
			datapoint6.add(1); // quality

			List<Object> datapoints = new ArrayList<Object>();
			datapoints.add(datapoint1);
			datapoints.add(datapoint2);
			datapoints.add(datapoint3);
			datapoints.add(datapoint4);
			datapoints.add(datapoint5);
			datapoints.add(datapoint6);

			body.setDatapoints(datapoints);

			com.ge.dsp.pm.ext.entity.util.map.Map map = new com.ge.dsp.pm.ext.entity.util.map.Map();
			map.put("host", "server1"); //$NON-NLS-2$
			map.put("customer", "Acme"); //$NON-NLS-2$

			body.setAttributes(map);

			List<Body> bodies = new ArrayList<Body>();
			bodies.add(body);

			dpIngestion.setBody(bodies);
			this.timeseriesFactory.create(dpIngestion);
		}
	}

	@SuppressWarnings("javadoc")
	protected Response handleResult(Object entity) {
		ResponseBuilder responseBuilder = Response.status(Status.OK);
		responseBuilder.type(MediaType.APPLICATION_JSON);
		responseBuilder.entity(entity);
		return responseBuilder.build();
	}

	private Long generateTimestampsWithinYear(Long current) {
		long yearInMMS = Long.valueOf(31536000000L);
		return ThreadLocalRandom.current().nextLong(current - yearInMMS,
				current + 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.api.ParkDataAPI#getParkDataTags()
	 */
	@Override
	public Response getParkDataTags() {
		List<Header> headers = generateHeaders();
		CloseableHttpResponse httpResponse = null;
		String entity = null;
		try {
			httpResponse = this.restClient
					.get(this.timeseriesRestConfig.getBaseUrl() + "/v1/tags", headers); //$NON-NLS-1$

			if (httpResponse.getEntity() != null) {
				try {
					entity = processHttpResponseEntity(httpResponse.getEntity());
					log.debug("HttpEntity returned from Tags" + httpResponse.getEntity().toString()); //$NON-NLS-1$
				} catch (IOException e) {
					throw new RuntimeException(
							"Error occured calling=" + this.timeseriesRestConfig.getBaseUrl() + "/v1/tags", e); //$NON-NLS-1$//$NON-NLS-2$
				}
			}
		} finally {
			if (httpResponse != null)
				try {
					httpResponse.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}

		return handleResult(entity);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("nls")
	private String processHttpResponseEntity(org.apache.http.HttpEntity entity)
			throws IOException {
		if (entity == null)
			return null;
		if (entity instanceof GzipDecompressingEntity) {
			return IOUtils.toString(
					((GzipDecompressingEntity) entity).getContent(), "UTF-8");
		}
		return EntityUtils.toString(entity);
	}

}
