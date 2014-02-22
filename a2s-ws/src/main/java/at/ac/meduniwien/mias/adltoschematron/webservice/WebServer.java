package at.ac.meduniwien.mias.adltoschematron.webservice;

import javax.xml.ws.Endpoint;

public class WebServer {
	
	private static Endpoint endpoint;

	public static void main(final String[] args) {
		endpoint = Endpoint.publish("http://localhost:8080/",
                new WebService() );
	}
	
	@Override
	protected void finalize() throws Throwable {
		endpoint.stop();
		super.finalize();
	}

}
