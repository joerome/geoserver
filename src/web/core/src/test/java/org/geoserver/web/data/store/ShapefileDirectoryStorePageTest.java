/* Copyright (c) 2001 - 2009 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.workspace.WorkspacesModel;
import org.geotools.data.shapefile.ShapefileDirectoryFactory;

/**
 * Test for the shapefile directory ppanel
 * 
 * @author Andrea Aime
 */
public class ShapefileDirectoryStorePageTest extends GeoServerWicketTestSupport {

    /**
     * print page structure?
     */
    private static final boolean debugMode = false;

    private AbstractDataAccessPage startPage() {
        final String dataStoreFactoryDisplayName = new ShapefileDirectoryFactory().getDisplayName();

        final AbstractDataAccessPage page = new DataAccessNewPage(dataStoreFactoryDisplayName);
        login();
        tester.startPage(page);

        if (debugMode) {
            print(page, true, true, true);
        }

        return page;
    }

    public void testChangeWorkspaceNamespace() throws Exception {
        startPage();

        WorkspaceInfo defaultWs = getCatalog().getDefaultWorkspace();

        tester.assertModelValue("dataStoreForm:workspacePanel:border:border_body:paramValue", defaultWs);
        
        print(tester.getLastRenderedPage(), true, true);

        // configure the store
        FormTester ft = tester.newFormTester("dataStoreForm");
        ft.setValue("dataStoreNamePanel:border:border_body:paramValue", "testStore");
        ft.setValue("parametersPanel:url:border:border_body:paramValue", "file://" + new File("./target").getCanonicalPath());
        ft.select("workspacePanel:border:border_body:paramValue", 2);
        ft.submit();
        tester.executeAjaxEvent("dataStoreForm:workspacePanel:border:border_body:paramValue", "onchange");
        tester.executeAjaxEvent("dataStoreForm:save", "onclick");
        
        tester.assertErrorMessages();
        
        
        // get the workspace we have just configured in the GUI
        WorkspacesModel wm = new WorkspacesModel();
        List<WorkspaceInfo> wl = (List<WorkspaceInfo>) wm.getObject();
        WorkspaceInfo ws = wl.get(2);

        // check it's the same
        StoreInfo store = getCatalog().getStoreByName("testStore", DataStoreInfo.class);
        assertNotNull(store);
        String uri = getCatalog().getNamespaceByPrefix(ws.getName()).getURI();
        Map<String, Serializable> connParams = store.getConnectionParameters();
        Serializable storeNS = connParams.get("namespace");
        assertEquals(uri, storeNS);
    }

   

}
