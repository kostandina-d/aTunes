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
package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Similar artists data source
 * 
 * @author alex
 * 
 */
public class YoutubeDataSource implements ContextInformationDataSource {

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Output parameter
     */
    public static final String OUTPUT_VIDEOS = "VIDEOS";

    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            result.put(OUTPUT_VIDEOS, getYoutubeVideos((AudioObject) parameters.get(INPUT_AUDIO_OBJECT)));
        }
        return result;
    }

    private List<YoutubeResultEntry> getYoutubeVideos(AudioObject audioObject) {
        String searchString = YoutubeService.getInstance().getSearchForAudioObject(audioObject);
        if (searchString.length() > 0) {
            return YoutubeService.getInstance().searchInYoutube(searchString, 1);
        }
        return null;
    }
}
