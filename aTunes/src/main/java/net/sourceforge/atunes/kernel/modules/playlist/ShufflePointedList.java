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
package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.PointedList;

class ShufflePointedList extends PointedList<Integer> {

    private static final long serialVersionUID = -3538333832968145075L;

    void add(List<PlayListAudioObject> playListAudioObjects) {
        // Add positions
        for (PlayListAudioObject plao : playListAudioObjects) {
            add(plao.getPosition());
        }

        int currentPointer = getPointer();
        Integer pointedObject = getCurrentObject();

        // Shuffle
        shuffle();

        setPointer(currentPointer);
        int pointedObjectNewPosition = indexOf(pointedObject);
        Collections.swap(this.getList(), currentPointer, pointedObjectNewPosition);
    }

    @Override
    public boolean isCyclic() {
        return ApplicationState.getInstance().isRepeat();
    }

}
