/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel;

import java.util.List;

import net.sourceforge.atunes.model.AudioObject;

/**
 * Interface for classes that must execute code after application start (usually
 * handlers)
 * 
 * @author fleax
 * 
 */
public interface ApplicationStartListener {

    /**
     * Called after application start
     * @param playList 
     */
    public void applicationStarted(List<AudioObject> playList);
    
    /**
     * Code to be executed when all handlers have been initialized
     */
    public void allHandlersInitialized();

}
