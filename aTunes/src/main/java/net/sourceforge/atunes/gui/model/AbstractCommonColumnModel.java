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
package net.sourceforge.atunes.gui.model;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.images.ArrowImageIcon;
import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.renderers.ColorMutableTableCellRendererCode;
import net.sourceforge.atunes.gui.renderers.ImageIconTableCellRendererCode;
import net.sourceforge.atunes.gui.renderers.IntegerTableCellRendererCode;
import net.sourceforge.atunes.gui.renderers.PropertyTableCellRendererCode;
import net.sourceforge.atunes.gui.renderers.StringTableCellRendererCode;
import net.sourceforge.atunes.gui.renderers.TextAndIconTableCellRendererCode;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn.ColumnSort;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.columns.TextAndIcon;
import net.sourceforge.atunes.utils.StringUtils;

public abstract class AbstractCommonColumnModel extends DefaultTableColumnModel {

    private static final long serialVersionUID = -8202322203076350708L;
    /** The table. */
    private JTable table;
    /** Column set */
    private AbstractColumnSet columnSet;
    /** The model. */
    private AbstractCommonTableModel model;
    /** The column being moved. */
    private int columnBeingMoved = -1;
    /** The column moved to. */
    private int columnMovedTo = -1;
    private ColumnMoveListener columnMoveListener;
    private ColumnModelListener columnModelListener;
    private Timer timer;

    /**
     * Instantiates a new column model
     * 
     * @param table
     * @param columnSet
     */
    public AbstractCommonColumnModel(JTable table, AbstractColumnSet columnSet) {
        this(table);
        this.columnSet = columnSet;
    }

    /**
     * Instantiates a new column model.
     * 
     * @param table
     *            the play list
     */
    public AbstractCommonColumnModel(JTable table) {
        super();
        this.table = table;
        this.model = (AbstractCommonTableModel) this.table.getModel();
    }

    /**
     * Return column for x position.
     * 
     * @param x
     *            the x
     * 
     * @return the column index at position
     */
    public final int getColumnIndexAtPosition(int x) {
        int computedX = x;
        if (computedX < 0) {
            return -1;
        }

        for (int column = 0; column < getColumnCount(); column++) {
            computedX = computedX - getColumn(column).getPreferredWidth();
            if (computedX < 0) {
                return column;
            }
        }

        return -1;
    }

    /**
     * Updates columns width.
     */
    protected final void updateColumnWidth() {
        for (int i = 0; i < getColumnCount(); i++) {
            Class<? extends AbstractColumn> col = getColumnId(i);
            int width = getColumn(i).getPreferredWidth();
            setWidthForColumn(col, width);
        }
    }

    /**
     * Sets width for a column.
     * 
     * @param c
     *            the c
     * @param width
     *            the width
     */
    private final void setWidthForColumn(Class<? extends AbstractColumn> c, int width) {
        getColumn(c).setWidth(width);
    }

    /**
     * Arrange columns.
     * 
     * @param reapplyFilter
     *            the reapply filter
     */
    public final void arrangeColumns(boolean reapplyFilter) {
        setCurrentColumns();
        model.refresh(TableModelEvent.UPDATE);
        if (reapplyFilter) {
            reapplyFilter();
        }
    }

    /**
     * Return Column object for a given column number.
     * 
     * @param column
     *            the column
     * 
     * @return the column
     */
    public final AbstractColumn getColumnObject(int column) {
        return getColumn(getColumnId(column));
    }

    /**
     * Returns class of column given by index
     * 
     * @param index
     * @return
     */
    private final Class<? extends AbstractColumn> getColumnId(int index) {
        return columnSet.getColumnId(index);
    }

    /**
     * Returns a column object given its class
     * 
     * @param columnClass
     * @return
     */
    private final AbstractColumn getColumn(Class<? extends AbstractColumn> columnClass) {
        return columnSet.getColumn(columnClass);
    }

    /**
     * Returns alignment of current column
     * 
     * @param column
     * @return
     */
    public int getColumnAlignment(int column) {
        return getColumn(getColumnId(column)).getAlignment();
    }

    /**
     * Initializes columns
     */
    private void setCurrentColumns() {
        columnSet.setCurrentColumns();
    }

    private static final class HeaderTableCellRendererCode extends AbstractTableCellRendererCode {

        private final AbstractColumn column;

        private HeaderTableCellRendererCode(AbstractColumn column) {
            this.column = column;
        }

        @Override
        public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = superComponent;

            ColumnSort columnSort = column.getColumnSort();

            if (columnSort != null) {
                ((JLabel) c).setHorizontalTextPosition(SwingConstants.LEFT);
                if (columnSort.equals(ColumnSort.ASCENDING)) {
                    ((JLabel) c).setIcon(ArrowImageIcon.getUpIcon());
                } else if (columnSort.equals(ColumnSort.DESCENDING)) {
                    ((JLabel) c).setIcon(ArrowImageIcon.getDownIcon());
                }
            }

            return c;
        }
    }

    private class ColumnMoveListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (columnBeingMoved != -1) {
                // Swap order in model

            	if (columnBeingMoved != columnMovedTo) {
            		// Column moved to right
            		if (columnBeingMoved < columnMovedTo) {
            			int columnDestinyOrder = getColumnObject(columnMovedTo).getOrder();
            			for (int i = columnBeingMoved + 1; i <= columnMovedTo; i++) {
            				int order = getColumnObject(i).getOrder();
            				getColumnObject(i).setOrder(order - 1);
            			}
            			getColumnObject(columnBeingMoved).setOrder(columnDestinyOrder);
            		} // Column moved to left
            		else if (columnBeingMoved > columnMovedTo) {
            			int columnDestinyOrder = getColumnObject(columnMovedTo).getOrder();
            			for (int i = columnBeingMoved - 1; i >= columnMovedTo; i--) {
            				int order = getColumnObject(i).getOrder();
            				getColumnObject(i).setOrder(order + 1);
            			}
            			getColumnObject(columnBeingMoved).setOrder(columnDestinyOrder);
            		}
            		
           			arrangeColumns(false);           			
            	}
            }
            columnBeingMoved = -1;
            columnMovedTo = -1;
        }
    };

    private class ColumnModelListener implements TableColumnModelListener {
    	
    	private void saveColumnSet() {
            if (timer != null) {
            	timer.cancel();
            	timer = null;
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
    			
    			@Override
    			public void run() {
    				// One second after last column width change save column set
    				// This is to avoid saving column set after each column change event
    		        columnSet.saveColumnSet();
    			}
    		}, 1000);        
    	}
    	
        public void columnAdded(TableColumnModelEvent e) {
        	saveColumnSet();
        }

        public void columnMarginChanged(ChangeEvent e) {
            updateColumnWidth();
            saveColumnSet();
        }

        public void columnMoved(TableColumnModelEvent e) {
            if (columnBeingMoved == -1) {
                columnBeingMoved = e.getFromIndex();
            }
            columnMovedTo = e.getToIndex();
            saveColumnSet();
        }

        public void columnRemoved(TableColumnModelEvent e) {
        	saveColumnSet();
        }

        public void columnSelectionChanged(ListSelectionEvent e) {
            // Nothing to do
        }
    };

    private ColumnMoveListener getColumnMoveListener() {
        if (columnMoveListener == null) {
            columnMoveListener = new ColumnMoveListener();
        }
        return columnMoveListener;
    }

    private ColumnModelListener getColumnModelListener() {
        if (columnModelListener == null) {
            columnModelListener = new ColumnModelListener();
        }
        return columnModelListener;
    }

    public void enableColumnChange(boolean enable) {
        this.table.getTableHeader().setReorderingAllowed(enable);
        if (enable) {
            // Add listener for column size changes
            addColumnModelListener(getColumnModelListener());
            this.table.getTableHeader().addMouseListener(getColumnMoveListener());
        } else {
            removeColumnModelListener(getColumnModelListener());
            this.table.getTableHeader().removeMouseListener(getColumnMoveListener());
        }
    }

    /**
     * Apply filter
     */
    protected abstract void reapplyFilter();

    /**
     * Updates a column according to settings from column set
     * 
     * @param aColumn
     */
    protected void updateColumnSettings(TableColumn aColumn) {
        // Get column data
        AbstractColumn column = getColumnObject(aColumn.getModelIndex());

        // Set width
        aColumn.setPreferredWidth(column.getWidth());
        aColumn.setWidth(column.getWidth());

        // Set resizable
        aColumn.setResizable(column.isResizable());

        // If has cell editor, set cell editor
        TableCellEditor cellEditor = column.getCellEditor();
        if (cellEditor != null) {
            aColumn.setCellEditor(cellEditor);
        }

        // If has renderer, set cell renderer
        TableCellRenderer cellRenderer = column.getCellRenderer();
        if (cellRenderer != null) {
            aColumn.setCellRenderer(cellRenderer);
        }
    }

    /**
     * Updates a column header according to settings from column set
     * 
     * @param aColumn
     */
    protected void updateColumnHeader(TableColumn aColumn) {
        // Get column data
        final AbstractColumn column = getColumnObject(aColumn.getModelIndex());

        // Set header renderer to sortable columns
        if (column.isSortable()) {
            aColumn.setHeaderRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableHeaderCellRenderer(new HeaderTableCellRendererCode(column)));
        }
    }

    /**
     * @return the columnSet
     */
    public AbstractColumnSet getColumnSet() {
        return columnSet;
    }

    /**
     * @param columnSet
     *            the columnSet to set
     */
    public void setColumnSet(AbstractColumnSet columnSet) {
        this.columnSet = columnSet;
    }

    /**
     * Returns renderer code for given class
     * 
     * @param clazz
     * @return
     */
    public AbstractTableCellRendererCode getRendererCodeFor(Class<?> clazz) {
        if (clazz.equals(Integer.class)) {
            return new IntegerTableCellRendererCode();
        } else if (clazz.equals(ImageIcon.class)) {
            return new ImageIconTableCellRendererCode(this);
        } else if (clazz.equals(String.class)) {
            return new StringTableCellRendererCode(this);
        } else if (clazz.equals(TextAndIcon.class)) {
            return new TextAndIconTableCellRendererCode(this);
        } else if (clazz.equals(Property.class)) {
            return new PropertyTableCellRendererCode();
        } else if (clazz.equals(ColorMutableImageIcon.class)) {
        	return new ColorMutableTableCellRendererCode(this);
        }
        throw new IllegalArgumentException(StringUtils.getString("No renderer found for class: ", clazz.getName()));
    }

    /**
     * @return the table
     */
    protected JTable getTable() {
        return table;
    }
}
