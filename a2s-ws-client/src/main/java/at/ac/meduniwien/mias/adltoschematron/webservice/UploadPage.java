package at.ac.meduniwien.mias.adltoschematron.webservice;

import java.nio.charset.Charset;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;

import se.acode.openehr.parser.ParseException;
import at.ac.meduniwien.mias.adltoschematron.AdlToSchematronConverter;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

@Slf4j
@SuppressWarnings("serial")
public class UploadPage extends WebPage {

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

	public Response converter(final String adlInput, final String xmlInput) throws ParseException, Exception {

		Response resp = new Response();

		AdlToSchematronConverter adlToSchematronConverter = new AdlToSchematronConverter();
		String schematronOutput = adlToSchematronConverter.createSchematron(true, null, adlInput);
		resp.setSchematron(schematronOutput);

		if (xmlInput != null && !xmlInput.equals("")) {
			String svrlOutput = Utils.validateXmlWithSchematronString(schematronOutput, xmlInput);
			resp.setSvrlReport(svrlOutput);
			String htmlOutput = Utils.generateHtmlReport(svrlOutput);
			resp.setHtmlReport(htmlOutput);
		}

		return resp;
	}

	private class FileUploadForm extends Form<Void> {
		FileUploadField fileUploadFieldAdl;
		FileUploadField fileUploadFieldXml;

		/**
		 * Construct.
		 * 
		 * @param name
		 *        Component name
		 */
		public FileUploadForm(final String name) {
			super(name);

			// set this form to multipart mode (allways needed for uploads!)
			setMultiPart(true);

			// Add one file input field
			add(fileUploadFieldAdl = new FileUploadField("fileInputAdl"));
			add(fileUploadFieldXml = new FileUploadField("fileInputXml"));

			// Set maximum size to 100K for demo purposes
			setMaxSize(Bytes.kilobytes(1024));
		}

		@Override
		protected void onSubmit() {
			FileUpload uploadAdl = fileUploadFieldAdl.getFileUpload();
			FileUpload uploadXml = fileUploadFieldXml.getFileUpload();

			if (uploadAdl == null) {
				error("Archetype has to be specified!");
				return;
			}

			String adl = new String(uploadAdl.getBytes(), Charset.forName("UTF-8"));
			String xml = null;
			if (uploadXml != null) {
				xml = new String(uploadXml.getBytes(), Charset.forName("UTF-8"));
			}

			// TODO this is the place where the webservice invocation should be processed.
			// This feature is to be added if requested. For simplicity reasons this web
			// application processes the generation on its own for now.

			try {

				Response resp = converter(adl, xml);

				outputSchematron.setVisible(true);
				outputSchematron.setDefaultModel(Model.of(resp.schematron));

				if (StringUtils.isNotBlank(resp.svrlReport)) {
					outputSvrl.setVisible(true);
					outputSvrl.setDefaultModel(Model.of(resp.svrlReport));
				} else {
					outputSvrl.setVisible(false);
				}
				if (StringUtils.isNotBlank(resp.htmlReport)) {
					outputHtml.setVisible(true);
					outputHtml.setEscapeModelStrings(false);
					outputHtml.setDefaultModel(Model.of(resp.htmlReport));
				} else {
					outputHtml.setVisible(false);
				}

			} catch (ParseException e) {
				log.error("ParseException", e);
				error("Error while parsing archetype!");
			} catch (Exception e) {
				log.error("Exception", e);
				error("General exception while generating schematron or validation report!");
			}

		}
	}

	Label outputHtml;
	Label outputSchematron;
	Label outputSvrl;

	public UploadPage(final PageParameters parameters) {
		// Create feedback panels
		final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");

		// Add uploadFeedback to the page itself
		add(uploadFeedback);

		final FileUploadForm progressUploadForm = new FileUploadForm("progressUpload");
		progressUploadForm.add(new UploadProgressBar("progress", progressUploadForm,
														progressUploadForm.fileUploadFieldAdl));

		outputHtml = new Label("outputHtml");
		outputSchematron = new Label("outputSchematron");
		outputSvrl = new Label("outputSvrl");

		outputSchematron.setVisible(false);

		add(progressUploadForm, outputHtml, outputSchematron, outputSvrl);


	}


}
