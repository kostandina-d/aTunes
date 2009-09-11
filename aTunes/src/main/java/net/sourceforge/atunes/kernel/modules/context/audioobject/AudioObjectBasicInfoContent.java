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
package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jdesktop.swingx.border.DropShadowBorder;

public class AudioObjectBasicInfoContent extends ContextPanelContent {

    private static final long serialVersionUID = 996227362636450601L;

    /**
     * Image for Audio Object
     */
    private JLabel audioObjectImage;

    /**
     * Title of audio object
     */
    private JLabel audioObjectTitle;

    /**
     * Artist of audio object
     */
    private JLabel audioObjectArtist;

    /**
     * Last date played this audio object
     */
    private JLabel audioObjectLastPlayDate;

    /**
     * Mark song as loved track in last.fm
     */
    private JMenuItem lovedSong;

    /**
     * Mark song as banned track in last.fm
     */
    private JMenuItem bannedSong;

    /**
     * Default constructor
     */
    public AudioObjectBasicInfoContent() {
        super(new AudioObjectBasicInfoDataSource());
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        audioObjectImage.setIcon(null);
        audioObjectImage.setBorder(null);
        audioObjectTitle.setText(null);
        audioObjectArtist.setText(null);
        audioObjectLastPlayDate.setText(null);
        lovedSong.setEnabled(false);
        bannedSong.setEnabled(false);
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, AudioObject> parameters = new HashMap<String, AudioObject>();
        parameters.put(AudioObjectBasicInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        return parameters;
    }

    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        ImageIcon image = (ImageIcon) result.get(AudioObjectBasicInfoDataSource.OUTPUT_IMAGE);
        if (image != null) {
            audioObjectImage.setIcon(image);
        }
        if (result.containsKey(AudioObjectBasicInfoDataSource.OUTPUT_AUDIO_OBJECT)) {
            if (result.get(AudioObjectBasicInfoDataSource.OUTPUT_AUDIO_OBJECT) instanceof AudioFile && image != null) {
                audioObjectImage.setBorder(new DropShadowBorder());
            } else {
                audioObjectImage.setBorder(null);
            }
        }

        if (result.containsKey(AudioObjectBasicInfoDataSource.OUTPUT_TITLE)) {
            audioObjectTitle.setText((String) result.get(AudioObjectBasicInfoDataSource.OUTPUT_TITLE));
        }
        if (result.containsKey(AudioObjectBasicInfoDataSource.OUTPUT_ARTIST)) {
            audioObjectArtist.setText((String) result.get(AudioObjectBasicInfoDataSource.OUTPUT_ARTIST));
        }
        if (result.containsKey(AudioObjectBasicInfoDataSource.OUTPUT_LASTPLAYDATE)) {
            audioObjectLastPlayDate.setText((String) result.get(AudioObjectBasicInfoDataSource.OUTPUT_LASTPLAYDATE));
        }

        // TODO: Allow these options for radios where song information is available
        lovedSong.setEnabled(ApplicationState.getInstance().isLastFmEnabled() && result.get(AudioObjectBasicInfoDataSource.OUTPUT_AUDIO_OBJECT) instanceof AudioFile);
        bannedSong.setEnabled(ApplicationState.getInstance().isLastFmEnabled() && result.get(AudioObjectBasicInfoDataSource.OUTPUT_AUDIO_OBJECT) instanceof AudioFile);
    }

    @Override
    protected String getContentName() {
        return LanguageTool.getString("INFO");
    }

    @Override
    protected Component getComponent() {
        // Create components
        audioObjectImage = new JLabel();
        audioObjectTitle = new JLabel();
        audioObjectTitle.setHorizontalAlignment(SwingConstants.CENTER);
        audioObjectTitle.setFont(Fonts.CONTEXT_INFORMATION_BIG_FONT);
        audioObjectArtist = new JLabel();
        audioObjectArtist.setHorizontalAlignment(SwingConstants.CENTER);
        audioObjectLastPlayDate = new JLabel();
        audioObjectLastPlayDate.setHorizontalAlignment(SwingConstants.CENTER);

        // Add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 0, 0, 0);
        panel.add(audioObjectImage, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 10);
        panel.add(audioObjectTitle, c);
        c.gridy = 2;
        c.insets = new Insets(5, 10, 10, 10);
        panel.add(audioObjectArtist, c);
        c.gridy = 3;
        panel.add(audioObjectLastPlayDate, c);

        return panel;
    }

    @Override
    protected List<Component> getOptions() {
        List<Component> options = new ArrayList<Component>();
        lovedSong = new JMenuItem(LanguageTool.getString("ADD_LOVED_SONG_IN_LASTFM"));
        lovedSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LastFmService.getInstance().addLovedSong(ContextHandler.getInstance().getCurrentAudioObject());
            }
        });
        bannedSong = new JMenuItem(LanguageTool.getString("ADD_BANNED_SONG_IN_LASTFM"));
        bannedSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LastFmService.getInstance().addBannedSong(ContextHandler.getInstance().getCurrentAudioObject());
            }
        });
        options.add(lovedSong);
        options.add(bannedSong);
        return options;
    }

}
