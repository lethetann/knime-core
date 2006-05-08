/*
 * --------------------------------------------------------------------- *
 *   This source code, its documentation and all appendant files         *
 *   are protected by copyright law. All rights reserved.                *
 *                                                                       *
 *   Copyright, 2003 - 2006                                              *
 *   Universitaet Konstanz, Germany.                                     *
 *   Lehrstuhl fuer Angewandte Informatik                                *
 *   Prof. Dr. Michael R. Berthold                                       *
 *                                                                       *
 *   You may not modify, publish, transmit, transfer or sell, reproduce, *
 *   create derivative works from, distribute, perform, display, or in   *
 *   any way exploit any of the content, in whole or in part, except as  *
 *   otherwise expressly permitted in writing by the copyright owner.    *
 * --------------------------------------------------------------------- *
 * 
 * 2006-06-08 (tm): reviewed 
 */
package de.unikn.knime.core.node.property.hilite;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.unikn.knime.core.data.DataCell;

/**
 * Default implementation for a <code>HiLiteHandler</code> which receives
 * hilite change request, answers queries and notifies registered listeners. 
 * <br />
 * This implementation keeps a list of row keys only for the hilit items. 
 * Furthermore, an event is only sent for items whose status actually changed.
 * The list of hilite keys is modified (delete or add keys) before
 * the actual event is send.
 * 
 * @see HiLiteListener
 * 
 * @author Thomas Gabriel, University of Konstanz
 */
public class DefaultHiLiteHandler implements HiLiteHandler {
    /** Set of registered <code>HiLiteListener</code>s to fire event to. */
    private final Set<HiLiteListener> m_listenerList;

    /** Set of non-null hilit items. */
    private final Set<DataCell> m_hiLitKeys;

    /** 
     * Creates a new default hilite handler with an empty set of registered 
     * listeners and an empty set of hilit items.
     */
    public DefaultHiLiteHandler() {
        // inits empty event listener list
        m_listenerList = new LinkedHashSet<HiLiteListener>();
        // initialize item list
        m_hiLitKeys = new LinkedHashSet<DataCell>();
    }
    
    /**
     * Appends a new hilite listener at the end of the list, if the 
     * listener has not been added before. This method does not send a 
     * hilite event to the new listener.
     * 
     * @param listener the hilite listener to append to the list
     */
    public void addHiLiteListener(final HiLiteListener listener) {
        m_listenerList.add(listener);
    }

    /**
     * Removes the given hilite listener from the list.
     * 
     * @param l the hilite listener to remove from the list
     */
    public synchronized void removeHiLiteListener(final HiLiteListener l) {
        m_listenerList.remove(l);
    }
    
    /**
     * Removes all hilite listeners from the list. 
     */
    public void removeAllHiLiteListeners() {
        m_listenerList.clear();
    }

    /**
     * Returns <code>true</code> if the specified row IDs are hilit.
     * 
     * @param ids the row IDs to check the hilite status for
     * @return <code>true</code> if all row IDs are hilit
     * @throws NullPointerException if this array or one of its elements is 
     *         <code>null</code>.
     */
    public boolean isHiLit(final DataCell... ids) {
        if (ids == null) {
            throw new NullPointerException("Array of hilit keys is null.");
        }
        List list = Arrays.asList(ids);
        if (list.contains(null)) {
            throw new NullPointerException("Hilit key is null.");
        }
        return m_hiLitKeys.containsAll(list);
    } 

    /**
     * Sets the status of the specified row IDs to 'hilit'. It will send a
     * hilite event to all registered listeners - only if the keys were not 
     * hilit before.
     * 
     * @param ids the row IDs to set hilited.
     */
    public synchronized void hiLite(final DataCell... ids) {
        this.hiLite(new LinkedHashSet<DataCell>(Arrays.asList(ids)));
    }

    /**
     * Sets the status of the specified row IDs to 'unhilit'. It will send a
     * unhilite event to all registered listeners - only if the keys were hilit
     * before.
     * 
     * @param ids the row IDs to set unhilited
     */    
    public synchronized void unHiLite(final DataCell... ids) {
        this.unHiLite(new LinkedHashSet<DataCell>(Arrays.asList(ids)));
    }

    /**
     * Sets the status of all specified row IDs in the set to 'hilit'. 
     * It will send a hilite event to all registered listeners - only for the 
     * IDs that were not hilit before.
     * 
     * @param ids a set of row IDs to set hilited
     * @throws NullPointerException if the set or one of its elements is
     *      <code>null</code>
     */
    public synchronized void hiLite(final Set<DataCell> ids) {
        if (ids == null) {
            throw new NullPointerException("Set of hilit keys is null.");
        }
        // create list of row keys from input key array
        final HashSet<DataCell> changedIDs = new HashSet<DataCell>();
        // iterates over all keys and adds them to the changed set
        for (DataCell id : ids) {
            if (id == null) {
                throw new NullPointerException("Hilit key is null.");
            }
            // if the key is already hilit, remove it from the cleaned list
            if (m_hiLitKeys.add(id)) {
                changedIDs.add(id);
            }
        }
        // if at least on key changed
        if (changedIDs.size() > 0) {
            // throw hilite event
            fireHiLiteEvent(new KeyEvent(this, changedIDs));
        }
    } // hiLite(Set<DataCell>)

    /**
     * Sets the status of all specified row IDs in the set to 'unhilit'. 
     * It will send a unhilite event to all registered listeners - only for 
     * the IDs that were hilit before.
     * 
     * @param ids a set of row IDs to set unhilited
     * @throws NullPointerException if the set or one of its elements is
     *      <code>null</code>
     */
    public synchronized void unHiLite(final Set<DataCell> ids) {
        if (ids == null) {
            throw new NullPointerException("Set of unhilit keys is null.");
        }
        // create list of row keys from input key array
        final HashSet<DataCell> changedIDs = new HashSet<DataCell>();
        // iterate over all keys and removes all not hilit ones
        for (DataCell id : ids) {
            if (id == null) {
                throw new NullPointerException("Unhilit key is null.");
            }
            // if the ID has not been hilit remove from the cleaned list 
            if (m_hiLitKeys.remove(id)) {
                changedIDs.add(id);
            }
        }
        // if at least on key changed
        if (changedIDs.size() > 0) {
            // throw hilite event
            fireUnHiLiteEvent(new KeyEvent(this, changedIDs));
        }
    }   // unHiLite(Set<DataCell>)
        
    /**
     * Resets the hilit status of all row IDs. Every row ID will be unhilit
     * after the call to this method. Sends an event to all registered listeners
     * with all previously hilit row IDs, if at least one key was effected 
     * by this call.
     */
    public synchronized void resetHiLite() {
        if (!m_hiLitKeys.isEmpty()) {
            m_hiLitKeys.clear();
            fireResetHiLiteEvent();
        }
    } 
    
    /** 
     * Informs all registered hilite listener to hilite the row keys contained 
     * in the key event.
     * 
     * @param event Contains all rows keys to hilite.
     */
    private synchronized void fireHiLiteEvent(final KeyEvent event) {
        assert (event != null);
        for (HiLiteListener l : m_listenerList) {
            l.hiLite(event);
        }
    }

    /** 
     * Informs all registered hilite listener to unhilite the row keys contained
     * in the key event.
     * 
     * @param event Contains all rows keys to unhilite.
     */
    private synchronized void fireUnHiLiteEvent(final KeyEvent event) {
        assert (event != null);
        for (HiLiteListener l : m_listenerList) {
            l.unHiLite(event);
        }
    }
    
    /** 
     * Informs all registered hilite listener to reset all hilit rows.
     */
    private synchronized void fireResetHiLiteEvent() {
        for (HiLiteListener l : m_listenerList) {
            l.resetHiLite();
        }
    }

    /** 
     * @see HiLiteHandler#getHiLitKeys()
     */
    public Set<DataCell> getHiLitKeys() {
        return Collections.unmodifiableSet(m_hiLitKeys);
    }

} // DefaultHiLiteHandler
