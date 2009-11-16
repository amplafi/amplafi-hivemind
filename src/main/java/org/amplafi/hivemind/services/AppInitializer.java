package org.amplafi.hivemind.services;

import javax.servlet.http.HttpServlet;

/**
 * Marks services that may perform startup initialization during servlet init.
 * <p/>
 * Bootstrapping this initialization does NOT happen automatically - you'll have to
 * do it yourself, as part of the servlet's {@link HttpServlet#init(javax.servlet.ServletConfig)}.
 * 
 * @see InitHelper#startAppInitializers(org.apache.hivemind.Registry, HttpServlet)
 * @author andyhot
 */
public interface AppInitializer {
	/**
	 * TODO: perhaps servlet parameter could go away (makes modules depend on servlet-api) 
	 */
	void initialize(HttpServlet servlet);

}
