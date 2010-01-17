package net.sourceforge.atunes.plugins.webinterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import javax.activation.MimetypesFileTypeMap;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public abstract class AbstractHttpHandler implements HttpRequestHandler {
	
	private static MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	
    public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
    	System.out.println(request.getRequestLine().getUri());
        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported"); 
        }

        if (!isResourceFound(request)) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            EntityTemplate body = new EntityTemplate(new ContentProducer() {
                
                public void writeTo(final OutputStream outstream) throws IOException {
                    OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
                    writer.write("<html><body><h1>");
                    writer.write("Not found");
                    writer.write("</h1></body></html>");
                    writer.flush();
                }
                
            });
            body.setContentType("text/html; charset=UTF-8");
            response.setEntity(body);
        } else {
        	try {
        		process(request, response, context);
        	} catch (final Exception e) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                EntityTemplate body = new EntityTemplate(new ContentProducer() {
                    public void writeTo(final OutputStream outstream) throws IOException {
                        OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
                        writer.append("Server error: ");
                        writer.append(e.getClass().getName());
                        writer.append(": ");
                        writer.append(e.getMessage());
        				for (StackTraceElement ste : e.getStackTrace()) {
        					writer.append(ste.toString());
        					writer.append(System.getProperty("line.separator"));
        				}
                        writer.flush();
                    }
                });
                body.setContentType("text/html; charset=UTF-8");
                response.setEntity(body);

        	}
        }
    }
    
	protected final String getMimeType(File file) {
		// TODO: 
		if (file.getName().toLowerCase().endsWith("css")) {
			return "text/css";
		}
		return mimeTypesMap.getContentType(file);
	}
	
	protected abstract boolean isResourceFound(HttpRequest request);
	
	
	protected abstract void process(final HttpRequest request, final HttpResponse response, final HttpContext context) throws Exception;
}
