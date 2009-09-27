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
package net.sourceforge.atunes.kernel.modules.context.similar;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Similar artists data source
 * 
 * @author alex
 * 
 */
public class SimilarArtistsDataSource implements ContextInformationDataSource {

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Output parameter
     */
    public static final String OUTPUT_ARTISTS = "ARTISTS";

    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            result.put(OUTPUT_ARTISTS, getSimilarArtists((AudioObject) parameters.get(INPUT_AUDIO_OBJECT)));
        }
        return result;
    }

    /**
     * Returns similar artists
     * 
     * @param audioObject
     */
    private SimilarArtistsInfo getSimilarArtists(AudioObject audioObject) {
        if (!Artist.isUnknownArtist(audioObject.getArtist())) {
            SimilarArtistsInfo artists = LastFmService.getInstance().getSimilarArtists(audioObject.getArtist());
            if (artists != null) {
                for (int i = 0; i < artists.getArtists().size(); i++) {
                    ArtistInfo a = artists.getArtists().get(i);
                    Image img = LastFmService.getInstance().getImage(a);
                    a.setImage(ImageUtils.scaleImageBicubic(img, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
                    a.setAvailable(RepositoryHandler.getInstance().getArtistStructure().containsKey(a.getName()));
                }
            }
            return artists;
        }
        return null;
    }

}
