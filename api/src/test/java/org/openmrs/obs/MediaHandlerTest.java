/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.obs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Obs;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.obs.handler.AbstractHandler;
import org.openmrs.obs.handler.MediaHandler;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractHandler.class, OpenmrsUtil.class, Context.class })

public class MediaHandlerTest {
		
	@Mock
	private AdministrationService administrationService;
	
    @Rule
    public TemporaryFolder complexObsTestFolder = new TemporaryFolder();

    @Test
    public void shouldReturnSupportedViews() {
        MediaHandler handler = new MediaHandler();
        String[] actualViews = handler.getSupportedViews();
        String[] expectedViews = { ComplexObsHandler.RAW_VIEW };

        assertArrayEquals(actualViews, expectedViews);
    }

    @Test
    public void shouldSupportRawView() {
        MediaHandler handler = new MediaHandler();

        assertTrue(handler.supportsView(ComplexObsHandler.RAW_VIEW));
    }

    @Test
    public void shouldNotSupportOtherViews() {
        MediaHandler handler = new MediaHandler();

        assertFalse(handler.supportsView(ComplexObsHandler.HTML_VIEW));
        assertFalse(handler.supportsView(ComplexObsHandler.PREVIEW_VIEW));
        assertFalse(handler.supportsView(ComplexObsHandler.TEXT_VIEW));
        assertFalse(handler.supportsView(ComplexObsHandler.TITLE_VIEW));
        assertFalse(handler.supportsView(ComplexObsHandler.URI_VIEW));
        assertFalse(handler.supportsView(""));
        assertFalse(handler.supportsView((String) null));
    }
     
	@Test
	public void saveObs_shouldRetrieveCorrectMimetype() throws IOException {
		String mimetype = "audio/mpeg";
		String filename = "TestingComplexObsSaving.mp3";
		File sourceFile = new File(
	        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "ComplexObsTestAudio.mp3");
		
		FileInputStream in1 = new FileInputStream(sourceFile);
		FileInputStream in2 = new FileInputStream(sourceFile);
		
		ComplexData complexData1 = new ComplexData(filename, in1);
		ComplexData complexData2 = new ComplexData(filename, in2);
		
		// Construct 2 Obs to also cover the case where the filename exists already
		Obs obs1 = new Obs();
		obs1.setComplexData(complexData1);
		
		Obs obs2 = new Obs();
		obs2.setComplexData(complexData2);
		
		// Mocked methods
		mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(administrationService);
		when(administrationService.getGlobalProperty(any())).thenReturn(complexObsTestFolder.newFolder().getAbsolutePath());
		
		MediaHandler handler = new MediaHandler();
		
		// Execute save
		handler.saveObs(obs1);
		handler.saveObs(obs2);
		
		// Get observation
		Obs complexObs1 = handler.getObs(obs1, "RAW_VIEW");
		Obs complexObs2 = handler.getObs(obs2, "RAW_VIEW");
		
		assertEquals(complexObs1.getComplexData().getMimeType(), mimetype);
		assertEquals(complexObs2.getComplexData().getMimeType(), mimetype);
	}
	
}
