package com.hpe.cloud.management.resources;

import io.dropwizard.jersey.errors.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.PrintWriter;
import java.io.StringWriter;

public class IdpWebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    Logger log = LoggerFactory.getLogger("Response");

    public IdpWebApplicationExceptionMapper() {
    }

    public static String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }

    @Override
    public Response toResponse(WebApplicationException e) {
        log.error(e.getMessage() + "{{{\n" + getTrace(e) + "}}}");

        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),
                e.getMessage()))
            .build();
    }
}


