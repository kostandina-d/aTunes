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

package net.sourceforge.atunes.kernel.modules.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;

/**
 * The Class SetTrackNumberProcess.
 */
public class SetTrackNumberProcess extends AbstractChangeTagProcess {

    /** The files and tracks. */
    private Map<ILocalAudioObject, Integer> filesAndTracks;

    /**
     * Instantiates a new sets the track number process.
     * 
     * @param filesAndTracks
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     */
    SetTrackNumberProcess(Map<ILocalAudioObject, Integer> filesAndTracks, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler) {
        super(new ArrayList<ILocalAudioObject>(filesAndTracks.keySet()), state, playListHandler, repositoryHandler);
        this.filesAndTracks = filesAndTracks;
    }

    @Override
    protected void changeTag(ILocalAudioObject file) throws IOException {
        TagModifier.setTrackNumber(file, filesAndTracks.get(file));
    }
}
