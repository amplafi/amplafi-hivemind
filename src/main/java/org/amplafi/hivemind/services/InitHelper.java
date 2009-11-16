package org.amplafi.hivemind.services;

import javax.servlet.http.HttpServlet;

import org.apache.hivemind.Registry;

public class InitHelper {
	
	public static void startAppInitializers(Registry registry, HttpServlet servlet) {
        AppInitializer ai = (AppInitializer) registry.getService(
                "amplafi.init.AppInitializer", AppInitializer.class);

        ai.initialize(servlet);		
	}

}
