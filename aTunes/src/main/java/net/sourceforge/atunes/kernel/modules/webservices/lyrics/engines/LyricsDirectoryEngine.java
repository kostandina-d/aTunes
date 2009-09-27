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
package net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public class LyricsDirectoryEngine extends LyricsEngine {

    private static final String BASE_URL = "http://www.lyricsdir.com/";
    private static final String CHARSET = "UTF-8";

    private Logger logger = new Logger();

    public LyricsDirectoryEngine(Proxy proxy) {
        super(proxy);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Lyrics getLyricsFor(String artist, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append(encodeString(getString(artist, title)));
        sb.append("lyrics");
        sb.append(".html");

        try {
            String html = readURL(getConnection(sb.toString()), CHARSET);

            String lyrics = null;
            Source source = new Source(html);
            List<Element> elements = source.getAllElements("div");
            for (Element element : elements) {
                if ("protected_lyrics".equals(element.getAttributeValue("id"))) {
                    lyrics = element.getRenderer().toString();
                }
            }
            return lyrics != null && !lyrics.isEmpty() ? new Lyrics(lyrics, sb.toString()) : null;
        } catch (UnknownHostException e) {
            logger.error(LogCategories.SERVICE, e);
            return null;
        } catch (IOException e) {
            logger.debug(LogCategories.SERVICE, StringUtils.getString("No lyrics found at ", BASE_URL, " for ", artist, " - ", title));
            return null;
        }
    }

    private String getString(String a, String t) {
        StringBuilder sb = new StringBuilder();
        String artist = a.toLowerCase().trim();
        artist = artist.replaceAll("\\W", " ");
        String title = t.toLowerCase().trim();
        title = title.replaceAll("\\W", " ");
        for (String artistSplits : artist.split("\\s+")) {
            sb.append(artistSplits);
            sb.append("-");
        }
        for (String titleSplits : title.split(" ")) {
            sb.append(titleSplits);
            sb.append("-");
        }
        return sb.toString();
    }

    @Override
    public String getLyricsProviderName() {
        return "LyricsDirectory";
    }

    @Override
    public String getUrlForAddingNewLyrics(String artist, String title) {
        return "";
    }

}
