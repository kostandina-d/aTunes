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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.ApplicationFinishListener;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.RadioNavigationView;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.XMLUtils;

/**
 * The Class RadioHandler.
 * 
 * @author sylvain
 */
public final class RadioHandler implements ApplicationFinishListener {

    /** The instance. */
    private static RadioHandler instance = new RadioHandler();

    /** The logger. */
    Logger logger = new Logger();

    /** The radios. */
    private List<Radio> radios;

    /** The preset radios. */
    private List<Radio> presetRadios;

    /** The retrieved preset radios. */
    List<Radio> retrievedPresetRadios = new ArrayList<Radio>();

    /** The no new stations. */
    boolean noNewStations = true;

    /**
     * Instantiates a new radio handler.
     */
    private RadioHandler() {
        Kernel.getInstance().addFinishListener(this);
    }

    /**
     * Gets the single instance of RadioHandler.
     * 
     * @return single instance of RadioHandler
     */
    public static RadioHandler getInstance() {
        return instance;
    }

    /**
     * Add the radio station from the add radio dialog.
     */
    public void addRadio() {
        Radio radio = VisualHandler.getInstance().showAddRadioDialog();
        if (radio != null) {
            addRadio(radio);
        }
    }

    /**
     * Add a radio station to the list.
     * 
     * @param radio
     *            Station
     */
    public void addRadio(Radio radio) {
        logger.info(LogCategories.HANDLER, "Adding radio");
        if (radio != null && !radios.contains(radio)) {
            radios.add(radio);
        }
        Collections.sort(radios, Radio.getComparator());
        NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
    }

    /**
     * Write stations to xml files.
     */
    public void applicationFinish() {
        ApplicationStateHandler.getInstance().persistRadioCache(radios);
        // Only write preset list if new stations were added
        if (!noNewStations) {
            ApplicationStateHandler.getInstance().persistPresetRadioCache(presetRadios);
        }
    }

    /**
     * Gets the radios.
     * 
     * @return the radios
     */
    public List<Radio> getRadios() {
        return new ArrayList<Radio>(radios);
    }

    /**
     * Gets the radio presets.
     * 
     * @return the preset radios, minus user maintained radio stations.
     */
    public List<Radio> getRadioPresets() {
        // Check if new stations were added and set false if yes
        if (noNewStations == true) {
            noNewStations = presetRadios.containsAll(retrievedPresetRadios);
            retrievedPresetRadios.removeAll(presetRadios);
        } else {
            // New stations were already found, so leave noNewStations as false
            retrievedPresetRadios.removeAll(presetRadios);
        }
        presetRadios.addAll(retrievedPresetRadios);
        presetRadios.removeAll(radios);
        return new ArrayList<Radio>(presetRadios);
    }

    /**
     * Gets the radios.
     * 
     * @param label
     *            the label
     * 
     * @return the radios
     */
    public List<Radio> getRadios(String label) {
        return new ArrayList<Radio>(radios);
    }

    /**
     * Read radio stations lists. We use different files, one for presets which
     * is not modified by the user and a second one for all the user
     * modifications.
     */
    void readRadios() {
        radios = ApplicationStateHandler.getInstance().retrieveRadioCache();
        presetRadios = ApplicationStateHandler.getInstance().retrieveRadioPreset();
    }

    /**
     * Sorts the labels alphabetically
     * 
     * @return Sorted label list
     */
    public List<String> sortRadioLabels() {
        List<String> result = new ArrayList<String>();
        List<String> newResult = new ArrayList<String>();
        // Read labels from user radios
        for (Radio radio : radios) {
            String label = radio.getLabel();
            if (!result.contains(label)) {
                result.add(label);
            }
        }
        // Read labels from preset radios
        for (Radio radio : presetRadios) {
            String label = radio.getLabel();
            if (!result.contains(label)) {
                result.add(label);
            }
        }

        newResult.add(result.get(0));
        // Sort labels, currently in reversed order
        for (String label : result) {
            int i = 0;
            while (i < newResult.size()) {
                int n = label.compareTo(newResult.get(i));
                if (n < 0) {
                    newResult.add(i, label);
                    i = result.size();
                } else if (i == newResult.size() - 1) {
                    if (newResult.get(i) != label)
                        newResult.add(label);
                    i = result.size();
                } else {
                    i = i + 1;
                }
            }
        }
        return newResult;
    }

    /**
     * Runnable process to read radio cache.
     * 
     * @return the read radios runnable
     */
    public Runnable getReadRadiosRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                readRadios();
            }
        };
    }

    /**
     * Remove stations from the list. Preset stations are not really removed but
     * are marked so they not show up in the navigator
     * 
     * @param radio
     *            Radio to be removed
     */
    public void removeRadios(List<Radio> radios) {
        logger.info(LogCategories.HANDLER, "Removing radios");
        for (Radio radio : radios) {
            if (!presetRadios.contains(radio)) {
                this.radios.remove(radio);
            }
            // Preset radio station, we can not delete from preset file directly but must mark it as removed.
            else {
                presetRadios.remove(radio);
                final Radio newRadio = new Radio(radio.getName(), radio.getUrl(), radio.getLabel());
                newRadio.setRemoved(true);
                this.radios.add(newRadio);
            }
        }
        NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
    }

    /**
     * Convenience method to remove a single radio
     * 
     * @param radio
     */
    public void removeRadio(Radio radio) {
        List<Radio> list = new ArrayList<Radio>();
        list.add(radio);
        removeRadios(list);
    }

    /**
     * Retrieve radios for browser.
     * 
     * @return the list< radio>
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("unchecked")
    public List<Radio> retrieveRadiosForBrowser() throws IOException {
        try {
            String xml = NetworkUtils.readURL(NetworkUtils.getConnection(Constants.RADIO_LIST_DOWNLOAD_COMMON_JUKEBOX, Proxy.getProxy(ApplicationState.getInstance().getProxy())));
            return (List<Radio>) XMLUtils.readObjectFromString(xml);
        } catch (Exception e) {
            String xml = NetworkUtils.readURL(NetworkUtils.getConnection(Constants.RADIO_LIST_DOWNLOAD, Proxy.getProxy(ApplicationState.getInstance().getProxy())));
            return (List<Radio>) XMLUtils.readObjectFromString(xml);
        }
    }

    /**
     * Retrieve radios.
     */
    @SuppressWarnings("unchecked")
    /*
     * Get radios from the internet (update preset list)
     */
    public void retrieveRadios() {
        new SwingWorker<List<Radio>, Void>() {

            final ProxyBean proxy = ApplicationState.getInstance().getProxy();

            @Override
            protected List<Radio> doInBackground() throws Exception {
                String xml = NetworkUtils.readURL(NetworkUtils.getConnection(Constants.RADIO_LIST_DOWNLOAD, Proxy.getProxy(proxy)));
                List<Radio> list = (List<Radio>) XMLUtils.readObjectFromString(xml);
                return list;
            }

            @Override
            protected void done() {
                try {
                    retrievedPresetRadios.clear();
                    retrievedPresetRadios.addAll(get());
                    getRadioPresets();
                    NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
                } catch (InterruptedException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (ExecutionException e) {
                    logger.error(LogCategories.HANDLER, e);
                }

            }

        }.execute();
    }

    /**
     * Change label of radio.
     * 
     * @param radioList
     *            List of radios for which the label should be changed
     * @param label
     *            New label
     */
    public void setLabel(List<Radio> radioList, String label) {
        for (Radio r : radioList) {
            // Write preset stations to user list in order to modify label
            if (presetRadios.contains(r)) {
                addRadio(r);
            }
            r.setLabel(label);
        }
    }

    /**
     * Change radio name. Also permits changing name of preset stations.
     * 
     * @param radio
     *            Radio station
     * @param name
     *            New name
     */
    public void setName(Radio radio, String name) {
        if (presetRadios.contains(radio)) {
            addRadio(radio);
        }
        radio.setName(name);
    }

    /**
     * Returns a Radio object for the given url or null if a Radio object is not
     * available for that url
     * 
     * @param url
     * @return
     */
    public Radio getRadioIfLoaded(String url) {
        // Check in user radios
        for (Radio radio : radios) {
            if (radio.getUrl().equalsIgnoreCase(url)) {
                return radio;
            }
        }
        // Check in preset radios
        for (Radio radio : presetRadios) {
            if (radio.getUrl().equalsIgnoreCase(url)) {
                return radio;
            }
        }
        return null;
    }
}
