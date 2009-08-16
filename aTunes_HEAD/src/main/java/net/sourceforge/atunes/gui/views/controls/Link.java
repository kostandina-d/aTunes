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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.kernel.modules.desktop.DesktopHandler;

/**
 * A link for url labels
 */
class Link extends AbstractAction {

    private static final long serialVersionUID = -3784927498125973809L;

    private String url;

    /**
     * Instantiates a new link
     * 
     * @param url
     *            the url
     */
    Link(String url) {
        this.url = url;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DesktopHandler.getInstance().openURL(url);
    }

}
