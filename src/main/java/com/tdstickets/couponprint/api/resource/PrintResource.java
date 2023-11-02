package com.tdstickets.couponprint.api.resource;

import com.tdstickets.couponprint.api.printer.ITicketPrinter;
import com.tdstickets.couponprint.api.service.CouponPrintService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/print")
public class PrintResource
{
  @Inject
  private CouponPrintService printService;

  @POST
  @Path("{carrier}")
  @Produces({MediaType.APPLICATION_OCTET_STREAM})
  public Response post(@PathParam("carrier") String carrier, byte[] coupons)
  {
    try {
      return Response.ok().entity(printService.getCoupons(carrier, coupons)).build();
    }
    catch(Exception e) {
      return Response.serverError().build();
    }
  }

  @POST
  @Path("{carrier}/{locale}")
  @Produces({MediaType.APPLICATION_OCTET_STREAM})
  public Response post(@QueryParam("terms") boolean terms, @PathParam("carrier") String carrier, @PathParam("locale") String locale, byte[] coupons)
  {
    try {
      return Response.ok().entity(printService.getCoupons(carrier, terms, locale, coupons)).build();
    }
    catch(Exception e) {
      return Response.serverError().build();
    }
  }

  @POST
  @Path("{carrier}/{locale}/{type}")
  @Produces({MediaType.APPLICATION_OCTET_STREAM})
  public Response post(@QueryParam("terms") boolean terms, @QueryParam("tickets") boolean tickets, @PathParam("carrier") String carrier, @PathParam("locale") String locale, @PathParam("type") ITicketPrinter.Type type, byte[] coupons)
  {
    try {
      return Response.ok().entity(printService.getCoupons(carrier, terms, locale, tickets, type, coupons)).build();
    }
    catch(Exception e) {
      return Response.serverError().build();
    }
  }
}
