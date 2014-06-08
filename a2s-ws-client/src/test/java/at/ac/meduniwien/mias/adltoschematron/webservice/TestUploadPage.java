package at.ac.meduniwien.mias.adltoschematron.webservice;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple test using the WicketTester
 */
public class TestUploadPage
{
	private WicketTester tester;

	@Before
	public void setUp()
	{
		tester = new WicketTester(new WicketApplication());
	}

	@Test
	public void homepageRendersSuccessfully()
	{
		//start and render the test page
		tester.startPage(UploadPage.class);

		//assert rendered page class
		tester.assertRenderedPage(UploadPage.class);
	}
}
