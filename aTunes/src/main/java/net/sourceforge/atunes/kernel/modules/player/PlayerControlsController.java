/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IStatePodcast;

final class PlayerControlsController extends AbstractSimpleController<PlayerControlsPanel> {

	private IPlayerControlsPanel playerControls;
	
    private IPlayerHandler playerHandler;
    
    private VolumeSlider volumeSlider;
    
    private VolumeSliderMouseWheelListener volumeSliderMouseWheelListener;
    
    private VolumeSliderChangeListener volumeSliderChangeListener;
    
    private IStatePodcast statePodcast;
    
    /**
     * @param statePodcast
     */
    public void setStatePodcast(IStatePodcast statePodcast) {
		this.statePodcast = statePodcast;
	}
    
    /**
     * @param volumeSliderChangeListener
     */
    public void setVolumeSliderChangeListener(VolumeSliderChangeListener volumeSliderChangeListener) {
		this.volumeSliderChangeListener = volumeSliderChangeListener;
	}
    
    /**
     * @param volumeSliderMouseWheelListener
     */
    public void setVolumeSliderMouseWheelListener(VolumeSliderMouseWheelListener volumeSliderMouseWheelListener) {
		this.volumeSliderMouseWheelListener = volumeSliderMouseWheelListener;
	}
    
    /**
     * @param volumeSlider
     */
    public void setVolumeSlider(VolumeSlider volumeSlider) {
		this.volumeSlider = volumeSlider;
	}
    
    /**
     * @param playerControls
     */
    public void setPlayerControls(IPlayerControlsPanel playerControls) {
		this.playerControls = playerControls;
	}

    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
    
    /**
     * Instantiates a new player controls controller.
     * 
     * @param panel
     * @param state
     * @param playerHandler
     */
    public void initialize() {
        setComponentControlled((PlayerControlsPanel) playerControls.getSwingComponent());
        addBindings();
        addStateBindings();
    }

    @Override
	public void addBindings() {
        ProgressBarSeekListener seekListener = new ProgressBarSeekListener(getComponentControlled().getProgressSlider(), playerHandler);        
        getComponentControlled().getProgressSlider().addMouseListener(seekListener);
        // Add volume behavior
        volumeSlider.addMouseWheelListener(volumeSliderMouseWheelListener);
        volumeSlider.addChangeListener(volumeSliderChangeListener);
    }

    /**
     * Sets the max duration.
     * 
     * @param length
     *            the new length
     */
    void setAudioObjectLength(final long length) {
    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    	        getComponentControlled().getProgressSlider().setMaximum((int) length);
    		}
    	});
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    void setPlaying(boolean playing) {
        getComponentControlled().setPlaying(playing);
    }

    /**
     * Sets the slidable.
     * 
     * @param slidable
     *            the new slidable
     */
    void setSlidable(boolean slidable) {
        getComponentControlled().getProgressSlider().setEnabled(slidable);
    }

    /**
     * Sets the time.
     * 
     * @param timePlayed
     *            the time played
     * @param totalTime
     *            the total time
     */
    void setCurrentAudioObjectTimePlayed(final long timePlayed, final long totalTime) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setCurrentAudioObjectTimePlayedEDT(timePlayed, totalTime);
                }
            });
        } else {
            setCurrentAudioObjectTimePlayedEDT(timePlayed, totalTime);
        }
    }

    private void setCurrentAudioObjectTimePlayedEDT(long timePlayed, long totalTime) {
        long remainingTime = totalTime - timePlayed;
        getComponentControlled().setProgress(timePlayed, timePlayed == 0 ? timePlayed : remainingTime);
        getComponentControlled().getProgressSlider().setValue((int) timePlayed);
    }

    /**
     * Sets the volume.
     * 
     * @param value
     *            the new volume
     */
    void setVolume(int value) {
    	getComponentControlled().setVolume(value);
    }

    /**
     * Gets the position in percent.
     */
    float getPostionInPercent() {
        int max = getComponentControlled().getProgressSlider().getMaximum();
        int pos = getComponentControlled().getProgressSlider().getValue();

        float floatPercent = 0;

        if (max > 0 && pos >= 0) {
            int intPercent = pos * 100 / max;
            floatPercent = intPercent / 100f;
        }
        return floatPercent;
    }

    /**
     * Updates controls when playing given audio object
     * @param audioObject
     */
    void updatePlayerControls(IAudioObject audioObject) {
        // Disable slider if audio object is a radio or podcast feed entry
        boolean b = audioObject.isSeekable();
        if (b && audioObject instanceof IPodcastFeedEntry) {
            b = statePodcast.isUseDownloadedPodcastFeedEntries();
        }
        setSlidable(b);
    }
}
