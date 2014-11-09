package org.openmrs.module.ezvacimmunization.extension.html;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.Extension.MEDIA_TYPE;
import org.openmrs.module.ezvacimmunization.extension.html.AdminList;

public class AdminListTest {
	
	/**
	 * Check the media type of this extension class
	 */
	@Test
	public void testMediaTypeIsHtml() {
		AdminList ext = new AdminList();
		Assert.assertTrue("The media type of this extension should be html", ext.getMediaType().equals(MEDIA_TYPE.html));
	}
	/**
	 * Get the links for the extension class
	 */
	@Test
	public void testValidatesLinks() {
		AdminList ext = new AdminList();
		
		Map<String, String> links = ext.getLinks();
		
		Assert.assertNotNull("Some links should be returned", links);
		Assert.assertNotNull(links);
		Assert.assertTrue("There should be a positive number of links", links.values().size() > 0);
	}


}
