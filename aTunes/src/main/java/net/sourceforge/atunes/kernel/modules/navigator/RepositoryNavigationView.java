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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.gui.images.AudioFileImageIcon;
import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RefreshFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RenameAudioFileInNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteAlbumFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteArtistFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteSongFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class RepositoryNavigationView extends AbstractNavigationView {

    private JTree tree;

    private JPopupMenu treePopupMenu;

    private JPopupMenu tablePopupMenu;

    public RepositoryNavigationView(IState state) {
    	super(state);
	}
    
    @Override
    public ColorMutableImageIcon getIcon() {    	
        return new ColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Paint paint) {
				return AudioFileImageIcon.getSmallImageIcon(paint);
			}
		};
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("REPOSITORY");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("REPOSITORY_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (tree == null) {
            tree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("REPOSITORY"))));
            tree.setToggleClickCount(0);
            tree.setCellRenderer(getTreeRenderer());
            ToolTipManager.sharedInstance().registerComponent(tree);
        }
        return tree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (treePopupMenu == null) {
            treePopupMenu = new JPopupMenu();
            treePopupMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            treePopupMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            treePopupMenu.add(getMenuItemForTreeAction(RefreshFolderFromNavigatorAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(new EditTagMenu(false, this));
            treePopupMenu.add(Actions.getAction(EditTitlesAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(getMenuItemForAction(SetFavoriteAlbumFromNavigatorAction.class));
            treePopupMenu.add(getMenuItemForAction(SetFavoriteArtistFromNavigatorAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(Actions.getAction(SearchArtistAction.class));
            treePopupMenu.add(Actions.getAction(SearchArtistAtAction.class));
        }
        return treePopupMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (tablePopupMenu == null) {
            tablePopupMenu = new JPopupMenu();
            tablePopupMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            tablePopupMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
            tablePopupMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            tablePopupMenu.add(Actions.getAction(PlayNowAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Actions.getAction(ShowNavigatorTableItemInfoAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(new EditTagMenu(false, this));
            tablePopupMenu.add(getMenuItemForAction(ExtractPictureAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            tablePopupMenu.add(Actions.getAction(RenameAudioFileInNavigationTableAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(getMenuItemForAction(SetFavoriteSongFromNavigatorAction.class));
        }
        return tablePopupMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
    	return RepositoryHandler.getInstance().getDataForView(viewMode);
    }

    @Override
    public void selectAudioObject(ViewMode viewMode, IAudioObject audioObject) {
    	getTreeGeneratorFactory().getTreeGenerator(viewMode).selectAudioObject(getTree(), audioObject);
    }
    
    @Override
	public void selectArtist(ViewMode viewMode, String artist) {
    	getTreeGeneratorFactory().getTreeGenerator(viewMode).selectArtist(getTree(), artist);
	}
    
    @SuppressWarnings("unchecked")
    @Override
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing ", this.getClass().getName());

        // Get model and root
        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<TreeObject<? extends IAudioObject>> objectsSelected = getTreeObjectsSelected(tree);
        // Get objects expanded before refreshing tree
        List<TreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(tree, root);
        
        // Build treeN
        getTreeGeneratorFactory().getTreeGenerator(viewMode).buildTree(getState(), "REPOSITORY", this, (Map<String, Year>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);
        
        getTree().expandRow(0);
    }

    @Override
    public List<? extends IAudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                songs.addAll(RepositoryHandler.getInstance().getAudioFilesList());
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    @SuppressWarnings("unchecked")
					TreeObject<ILocalAudioObject> obj = (TreeObject<ILocalAudioObject>) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
                    songs.addAll(obj.getAudioObjects());
                }
            }
        } else {
            @SuppressWarnings("unchecked")
			TreeObject<ILocalAudioObject> obj = (TreeObject<ILocalAudioObject>) node.getUserObject();
            songs = obj.getAudioObjects();
        }
        return songs;
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
        return true;
    }

    @Override
    public AbstractColumnSet getCustomColumnSet() {
        // Return null since use default navigator column set
        return null;
    }

    @Override
    public boolean isViewModeSupported() {
        return true;
    }

    /**
     * Gets the tool tip for repository.
     * 
     * @return the tool tip for repository
     */

    static String getToolTipForRepository() {
        int songs = RepositoryHandler.getInstance().getAudioFilesList().size();
        return StringUtils.getString(I18nUtils.getString("REPOSITORY"), " (", songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG")), ")");
    }
}
