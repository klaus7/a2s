package at.ac.meduniwien.mias.adltoschematron.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import se.acode.openehr.parser.ParseException;
import at.ac.meduniwien.mias.adltoschematron.AdlToSchematronConverter;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

@Log4j
@javax.jws.WebService(name = "a2s-ws")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class WebService {

	/**
	 * Schematron Webservice Response.
	 * 
	 * @author Klaus Pfeiffer
	 */
	@Data
	public static class Response {
		/**
		 * Schematron schema as String.
		 */
		private String schematron;
		/**
		 * SVRL Report as String.
		 */
		private String svrlReport;
		/**
		 * HTML Report as String.
		 */
		private String htmlReport;
	}

	/**
	 * @param adlInput ADL as string
	 * @param xmlInput XML as string
	 * @return Response with Schematron schema, SVRL- and HTML report
	 * @throws ParseException
	 * @throws Exception
	 */
	@WebMethod(operationName = "convert")
	@WebResult(name = "converter-response")
	public Response converter(
			@WebParam(name = "adl") final String adlInput,
			@WebParam(name = "xml") final String xmlInput)
															throws ParseException, Exception {

		Response resp = new Response();

		AdlToSchematronConverter adlToSchematronConverter = new AdlToSchematronConverter();
		String schematronOutput = adlToSchematronConverter.createSchematron(true, null, adlInput);
		resp.setSchematron(schematronOutput);

		if (xmlInput != null && !xmlInput.equals("")) {
			try {
				String svrlOutput = Utils.validateXmlWithSchematronString(schematronOutput, xmlInput);
				resp.setSvrlReport(svrlOutput);
				String htmlOutput = Utils.generateHtmlReport(svrlOutput);
				resp.setHtmlReport(htmlOutput);
			} catch (Exception e) {
				log.error(e);
			}
		}

		return resp;
	}

	//	@WebMethod(operationName = "convert")
	//	@WebResult(name = "converter-response")
	//	public Response converter(
	//			@WebParam(name = "adl") final String adlInput,
	//			@WebParam(name = "xml") final String xmlInput)
	//					throws IOException {
	//		
	//		long ts = System.currentTimeMillis();
	//		String name = "uploaded-" + ts;
	//		String adlFile = name + ".adl";
	//		String xmlFile = name + ".xml";
	//		String schFile = name + ".sch";
	//		
	//		FileWriter fw;
	//		fw = new FileWriter(adlFile);
	//		fw.write((adlInput));
	//		fw.flush();
	//		fw.close();
	//		
	//		if (!StringUtils.isEmpty(xmlInput)) {
	//			fw = new FileWriter(xmlFile);
	//			fw.write((xmlInput));
	//			fw.flush();
	//			fw.close();
	//		} else {
	//			xmlFile = null;
	//		}
	//		
	//		AdlToSchematronConverter.outputFile = schFile;
	//		AdlToSchematronConverter.run(adlFile, xmlFile);
	//		
	//		String output = FileUtils.readFileToString(new File(schFile), "UTF-8");
	//		
	//		Response resp = new Response();
	//		resp.setSchematron(output);
	//		return resp;
	//	}

}
