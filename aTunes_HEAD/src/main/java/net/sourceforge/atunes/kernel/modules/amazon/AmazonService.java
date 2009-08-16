/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.amazon;

import java.awt.Image;
import java.io.IOException;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateChangeListener;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;

/**
 * The Class AmazonService.
 */
public class AmazonService implements ApplicationStateChangeListener {

    /** The logger. */
    private Logger logger;

    /** The Constant SUBSCRIPTION_ID. */
    private static final String SUBSCRIPTION_ID = "06SX9FYP905XDBHBCZR2";

    /** The Constant ARTIST_WILDCARD. */
    private static final String ARTIST_WILDCARD = "(%ARTIST%)";

    /** The Constant ALBUM_WILDCARD. */
    private static final String ALBUM_WILDCARD = "(%ALBUM%)";

    /** The search url. */
    private static String searchURL = StringUtils.getString("http://ecs.amazonaws.com/onca/xml?Service=AWSECommerceService&Operation=ItemSearch&SubscriptionId=", SUBSCRIPTION_ID,
            "&SearchIndex=Music&Artist=", ARTIST_WILDCARD, "&Title=", ALBUM_WILDCARD, "&ResponseGroup=Images,Tracks");

    /** The proxy. */
    private Proxy proxy;
    
    /**
     * Singleton instance
     */
    private static AmazonService instance;
    
    /**
     * Default private constructor
     */
    private AmazonService() {
    	
    }
    
    /**
     * Getter of the singleton object
     * @return
     */
    public static AmazonService getInstance() {
    	if (instance == null) {
    		instance = new AmazonService();
    	}
    	return instance;
    }

    /**
     * Gets the amazon album.
     * 
     * @param artist
     *            the artist
     * @param album
     *            the album
     * 
     * @return the amazon album
     */
    public AmazonAlbum getAlbum(String artist, String album) {
        try {
            // build url
            String urlString = searchURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist)).replace(ALBUM_WILDCARD, NetworkUtils.encodeString(album));
            // read xml
            Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(urlString, proxy)));
            return AmazonAlbum.getAlbum(xml);
        } catch (Exception e) {
            getLogger().internalError(e);
        }
        return null;
    }

    /**
     * Gets the image.
     * 
     * @param url
     *            the url
     * 
     * @return the image
     */
    public Image getImage(String url) {
        try {
            return NetworkUtils.getImage(NetworkUtils.getConnection(url, proxy));
        } catch (IOException e) {
        	getLogger().error(LogCategories.NETWORK, e);
        }
        return null;
    }

    /**
     * @param proxy the proxy to set
     */
    public void setProxyBean(ProxyBean proxyBean) {
        try {
            proxy = ProxyBean.getProxy(proxyBean);
        } catch (IOException e) {
            getLogger().error(LogCategories.NETWORK, e);
        }
    }
    
    /**
     * Returns the internal logger of this class
     * @return
     */
    private Logger getLogger() {
    	if (logger == null) {
    		logger = new Logger();
    	}
    	return logger;
    }
    
    @Override
    public void applicationStateChanged(ApplicationState newState) {
        setProxyBean(newState.getProxy());
    }
}
