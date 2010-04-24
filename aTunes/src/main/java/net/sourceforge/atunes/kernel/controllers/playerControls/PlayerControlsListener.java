/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.controllers.playerControls;

import java.awt.event.MouseAdapter;

import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;

/**
 * The listener interface for receiving playerControls events.
 */
public final class PlayerControlsListener extends MouseAdapter {

    private PlayerControlsPanel panel;

    /**
     * Instantiates a new player controls listener.
     * 
     * @param panel
     *            the panel
     * @param controller
     *            the controller
     */
    protected PlayerControlsListener(PlayerControlsPanel panel, PlayerControlsController controller) {
        this.panel = panel;
    }
}
