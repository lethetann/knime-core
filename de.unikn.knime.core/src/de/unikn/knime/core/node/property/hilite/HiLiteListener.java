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

import java.util.EventListener;


/** 
 * The event listener interface has to be implemented by classes that are 
 * interested in receiving hilite events.
 * <br />
 * The listener object created from that class can then register with the
 * {@link HiLiteHandler} (calling
 * {@link HiLiteHandler#addHiLiteListener(HiLiteListener)}) or unregister
 * ({@link HiLiteHandler#removeHiLiteListener(HiLiteListener)}) if it
 * is no longer interested in hilite change events.
 * 
 * @author Thomas Gabriel, University of Konstanz
 */
public interface HiLiteListener extends EventListener {
    /** 
     * Invoked when some item(s) were hilit. 
     * 
     * @param event contains a list of row keys that were hilit
     */
    void hiLite(final KeyEvent event);

    /** 
     * Invoked when some item(s) were unhilit.
     * 
     * @param event contains a list of row keys that were unhilit
     */
    void unHiLite(final KeyEvent event);
    
    /**
     * Global reset which resets everything (all rows) in all registered views.
     */
    void resetHiLite();
}   // HiLiteListener
