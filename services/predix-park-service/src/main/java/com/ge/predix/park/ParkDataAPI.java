package com.ge.predix.park;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Betul Turkicin  
 */
@Consumes(
{
        "application/json", "application/xml"
})
@Produces(
{
        "application/json", "application/xml"
})
@Path("/parkingservices")
public interface ParkDataAPI
{
	/**
	 * @return -
	 */
	@GET
	@Path("/ping")
	public Response greetings();


	/**
	 * @param id
	 *            -
	 * @param authorization
	 *            -
	 * @return -
	 */
	@GET
	@Path("/data/slotid/{id}")
	public Response getParkingSlotStatus(@PathParam("id") String id,
			@HeaderParam(value = "Authorization") String authorization);

	/**
	 * 
	 * @return List of tags
	 */
	@GET
	@Path("/tags")
	public Response getParkDataTags();
}

