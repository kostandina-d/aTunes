/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to contain all playlists when storing and reading from
 * disk.
 * 
 * @author alex
 */
public class ListOfPlayLists implements Serializable {

    private static final long serialVersionUID = -9098493526495642598L;

    /** The play lists. */
    private List<PlayList> playLists;

    /** The selected play list. */
    private int selectedPlayList;

    /**
     * Returns a list of playlists with an empty playlist.
     * 
     * @return the empty play list
     */
    public static ListOfPlayLists getEmptyPlayList() {
        ListOfPlayLists l = new ListOfPlayLists();
        List<PlayList> list = new ArrayList<PlayList>();
        list.add(new PlayList());
        l.setPlayLists(list);
        l.setSelectedPlayList(0);
        return l;
    }

    /**
     * Gets the play lists.
     * 
     * @return the playLists
     */
    public List<PlayList> getPlayLists() {
        // As some attributes are transient complete some information before return play lists
        for (PlayList pl : playLists) {
            if (pl.getMode() == null) {
                pl.setMode(PlayListMode.getPlayListMode(pl));
            }
        }
        return playLists;
    }

    /**
     * Gets the selected play list.
     * 
     * @return the selectedPlayList
     */
    int getSelectedPlayList() {
        return selectedPlayList;
    }

    /**
     * Sets the play lists.
     * 
     * @param playLists
     *            the playLists to set
     */
    void setPlayLists(List<PlayList> playLists) {
        this.playLists = playLists;
    }

    /**
     * Sets the selected play list.
     * 
     * @param selectedPlayList
     *            the selectedPlayList to set
     */
    void setSelectedPlayList(int selectedPlayList) {
        this.selectedPlayList = selectedPlayList;
    }

}
