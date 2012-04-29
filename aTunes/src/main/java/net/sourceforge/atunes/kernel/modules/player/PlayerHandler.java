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

import java.util.Collection;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.PlaybackStateListeners;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.model.PlayerEngineCapability;
import net.sourceforge.atunes.model.SubmissionState;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

/**
 * This class is responsible for handling the player engine.
 */
public final class PlayerHandler extends AbstractHandler implements PluginListener, IPlayerHandler {

    /**
     * The player engine
     */
    private IPlayerEngine playerEngine = new VoidPlayerEngine();

    /**
     * The current playback state
     */
    private PlaybackState playbackState;

    /**
     * Controller
     */
    private PlayerControlsController playerControlsController;
    
    private IEqualizer equalizer;
    
    private Volume volumeController;
    
    private IStateUI stateUI;
    
    private IStatePlayer statePlayer;
    
    /**
     * @param statePlayer
     */
    public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}
    
    /**
     * @param stateUI
     */
    public void setStateUI(IStateUI stateUI) {
		this.stateUI = stateUI;
	}

    /**
     * @param volumeController
     */
    public void setVolumeController(Volume volumeController) {
		this.volumeController = volumeController;
	}
        
    /**
     * @param playerControlsController
     */
    public void setPlayerControlsController(
			PlayerControlsController playerControlsController) {
		this.playerControlsController = playerControlsController;
	}
    
    /**
     * @param equalizer
     */
    public void setEqualizer(IEqualizer equalizer) {
		this.equalizer = equalizer;
	}
    
    @Override
    public void applicationStateChanged() {
    	// Show advanced controls
    	playerControlsController.getComponentControlled().showAdvancedPlayerControls(stateUI.isShowAdvancedPlayerControls());
    }

    @Override
	public boolean isEnginePlaying() {
        return playerEngine.isEnginePlaying();
    }

    @Override
	public void setVolume(int perCent) {
    	playerEngine.setVolume(perCent);
        playerControlsController.setVolume(perCent);
    }

    @Override
	public void applyMuteState(boolean state) {
    	playerEngine.applyMuteState(state);
    }

    @Override
	public void applyNormalization() {
    	playerEngine.applyNormalization();
    }

    @Override
	public boolean supportsCapability(PlayerEngineCapability capability) {
        return playerEngine.supportsCapability(capability);
    }

    @Override
    public void applyEqualization(float[] values) {
    	playerEngine.applyEqualization(values);
    }

    /**
     * This method must be implemented by player engines. Transform values
     * retrieved from equalizer dialog to values for player engine
     * 
     * @param values
     * @return
     */
    float[] transformEqualizerValues(float[] values) {
        return playerEngine.transformEqualizerValues(values);
    }

    @Override
	public final void playCurrentAudioObject(boolean buttonPressed) {
    	playerEngine.playCurrentAudioObject(buttonPressed);
    }

    @Override
	public final void stopCurrentAudioObject(boolean userStopped) {
    	playerEngine.stopCurrentAudioObject(userStopped);
    }

    @Override
	public final void playPreviousAudioObject() {
    	playerEngine.playPreviousAudioObject();
    }

    @Override
	public final void playNextAudioObject() {
    	playerEngine.playNextAudioObject(false);
    }

    @Override
	public final void seekCurrentAudioObject(long milliseconds) {
    	playerEngine.seekCurrentAudioObject(milliseconds);
    }

    @Override
	public final void volumeDown() {
    	playerEngine.volumeDown();
    }

    @Override
	public final void volumeUp() {
    	playerEngine.volumeUp();
    }

    @Override
    public void applicationFinish() {
        // Stop must be called explicitly to avoid playback after user closed app
        stopCurrentAudioObject(true);
        playerEngine.finishPlayer();
    }

    @Override
	public IEqualizer getEqualizer() {
        return equalizer;
    }

    @Override
	public IAudioObject getAudioObject() {
    	return playerEngine.getAudioObject();
    }
    
    @Override
    public void allHandlersInitialized() {
    	initialize();
    }
    
    @Override
    public int requestUserInteraction() {
    	return 1;
    }
    
    @Override
    public void doUserInteraction() {
        if (playerEngine instanceof VoidPlayerEngine) {
            manageNoPlayerEngine(getOsManager(), getFrame());
        }
    }
    
	/**
	 * Called when no player engine is available
	 * @param osManager
	 * @param frame
	 */
	private void manageNoPlayerEngine(IOSManager osManager, IFrame frame) {
		// Delegate to specific OS code
		osManager.manageNoPlayerEngine(frame);
	}
	
    /**
     * Initializes all related to player engine
     */
    private void initialize() {
    	IPlayerEngine selectedPlayerEngine = getBean(PlayerEngineSelector.class).selectPlayerEngine();
    	if (selectedPlayerEngine != null) {
    		playerEngine = selectedPlayerEngine;
    		Logger.info("Selected player engine: ", playerEngine);
    	}
    	
        // Set volume on visual components
    	volumeController.setVolume(statePlayer.getVolume(), false);

        // Mute
        applyMuteState(statePlayer.isMuteEnabled());
    	
        if (statePlayer.isPlayAtStartup()) {
            playCurrentAudioObject(true);
        }
        
        // Show advanced controls
        playerControlsController.getComponentControlled().showAdvancedPlayerControls(stateUI.isShowAdvancedPlayerControls());

        // Init engine
        playerEngine.initializePlayerEngine();
    	
        // Add a shutdown hook to perform some actions before killing the JVM
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                Logger.debug("Final check for Zombie player engines");
                playerEngine.destroyPlayer();
                Logger.debug("Closing player ...");
            }

        }));        
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            IPlaybackStateListener listener = (IPlaybackStateListener) getBean(IPluginsHandler.class).getNewInstance(plugin);
            getBean(PlaybackStateListeners.class).addPlaybackStateListener(listener);
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        Logger.info(StringUtils.getString("Plugin deactivated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
        for (Plugin createdInstance : createdInstances) {
        	getBean(PlaybackStateListeners.class).removePlaybackStateListener((IPlaybackStateListener) createdInstance);
        }
    }

    @Override
    public void playbackStateChanged(PlaybackState newState, IAudioObject currentAudioObject) {
    	if (playbackState == null || !playbackState.equals(newState)) {
    		this.playbackState = newState;
    		Logger.debug("Playback state changed to:", newState);
    		submitAudioObjectIfNecessary(newState, currentAudioObject);
    		stopPlayerEngineIfNecessary(newState);
    	}
    }

	/**
	 * @param newState
	 * @param currentAudioObject
	 */
	private void submitAudioObjectIfNecessary(PlaybackState newState, IAudioObject currentAudioObject) {
		if (isSubmissionNeeded(newState, currentAudioObject)) { 
			getBean(IWebServicesHandler.class).submit((ILocalAudioObject) currentAudioObject, getCurrentAudioObjectPlayedTime() / 1000);
			getBean(IStatisticsHandler.class).updateAudioObjectStatistics(currentAudioObject);
			playerEngine.setSubmissionState(SubmissionState.SUBMITTED);
		}
	}
	
	private boolean isSubmissionNeeded(PlaybackState newState, IAudioObject currentAudioObject) {
		if (isStateForSubmission(newState) && playerEngine.getSubmissionState() == SubmissionState.PENDING && currentAudioObject instanceof ILocalAudioObject) {
			return true;
		}
		return false;
	}
	
	private boolean isStateForSubmission(PlaybackState newState) {
		return newState == PlaybackState.PLAY_FINISHED || 
		       newState == PlaybackState.PLAY_INTERRUPTED || 
		       newState == PlaybackState.STOPPED;
	}

	/**
	 * @param newState
	 */
	private void stopPlayerEngineIfNecessary(PlaybackState newState) {
		if (newState == PlaybackState.STOPPED) {
			playerEngine.setCurrentAudioObjectPlayedTime(0);
			playerEngine.interruptPlayAudioObjectThread();
		}
	}

    @Override
	public PlaybackState getPlaybackState() {
        return playbackState;
    }

    @Override
	public long getCurrentAudioObjectPlayedTime() {
        return playerEngine.getCurrentAudioObjectPlayedTime();
    }

    @Override
	public long getCurrentAudioObjectLength() {
        return playerEngine.getCurrentAudioObjectLength();
    }
    
	@Override
	public void setPlaying(boolean playing) {
		playerControlsController.setPlaying(playing);
	}

	@Override
	public void setAudioObjectLength(long currentLength) {
		playerControlsController.setAudioObjectLength(currentLength);		
	}

	@Override
	public void setCurrentAudioObjectTimePlayed(long actualPlayedTime, long currentAudioObjectLength) {
		playerControlsController.setCurrentAudioObjectTimePlayed(actualPlayedTime, currentAudioObjectLength);
	}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {
		playerControlsController.updatePlayerControls(audioObject);
	}

	@Override
	public void initializeAndCheck() {
		initialize();
        if (playerEngine instanceof VoidPlayerEngine) {
        	manageNoPlayerEngine(getOsManager(), getFrame());
        }
	}
}
