/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action opens a window which shows information about the current selected
 * tow in play list
 * 
 * @author fleax
 * 
 */
public class ShowPlayListItemInfoAction extends CustomAbstractAction {

    private static final long serialVersionUID = -2006569851431046347L;

    ShowPlayListItemInfoAction() {
        super(I18nUtils.getString("INFO"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("INFO_BUTTON_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        IAudioObject audioObject = PlayListHandler.getInstance().getSelectedAudioObjects().get(0);
        GuiHandler.getInstance().showPropertiesDialog(audioObject);
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return selection.size() == 1;
    }

}
