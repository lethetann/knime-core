/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2006
 * University of Konstanz, Germany.
 * Chair for Bioinformatics and Information Mining
 * Prof. Dr. Michael R. Berthold
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any quesions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * --------------------------------------------------------------------- *
 * 
 * History
 *   31.10.2006 (ohl): created
 */
package org.knime.core.node.defaultnodesettings;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.config.Config;

/**
 * Implements a settings model that provides include and exclude lists. These
 * lists contain strings. It's currently used e.g. in the column filter
 * component and provides the list of column names to include and exclude.
 * 
 * @author ohl, University of Konstanz
 */
public class SettingsModelFilterString extends SettingsModel {

    private static final String CFGKEY_INCL = "InclList";

    private static final String CFGKEY_EXCL = "ExclList";

    private final String m_configName;

    private final List<String> m_inclList;

    private final List<String> m_exclList;

    /**
     * Creates a new object holding a list of strings in an exclude list and a
     * list of strings in an include list..
     * 
     * @param configName the identifier the values are stored with in the
     *            {@link org.knime.core.node.NodeSettings} object
     * @param defaultInclList the initial value for the include list
     * @param defaultExclList the initial value for the exclude list.
     */
    public SettingsModelFilterString(final String configName,
            final List<String> defaultInclList,
            final List<String> defaultExclList) {
        if ((configName == null) || (configName == "")) {
            throw new IllegalArgumentException("The configName must be a "
                    + "non-empty string");
        }

        m_configName = configName;

        m_inclList = new LinkedList<String>();
        m_exclList = new LinkedList<String>();

        if (defaultInclList != null) {
            for (String i : defaultInclList) {
                if (!m_inclList.contains(i)) {
                    // ignore double entries
                    m_inclList.add(i);
                }
            }
        }
        if (defaultExclList != null) {
            for (String e : defaultExclList) {
                // entries can't be in the include and exclude list!
                if (m_inclList.contains(e)) {
                    throw new IllegalArgumentException(
                            "The include and exclude"
                                    + "lists contain the same object.");
                }
                if (!m_exclList.contains(e)) {
                    m_exclList.add(e);
                }
            }
        }

    }

    /**
     * Creates a new object holding a list of strings in an exclude list and a
     * list of strings in an include list..
     * 
     * @param configName the identifier the values are stored with in the
     *            {@link org.knime.core.node.NodeSettings} object
     * @param defaultInclList the initial value for the include list
     * @param defaultExclList the initial value for the exclude list.
     */
    public SettingsModelFilterString(final String configName,
            final String[] defaultInclList, final String[] defaultExclList) {
        this(configName, Arrays.asList(defaultInclList), Arrays
                .asList(defaultExclList));
    }

    /**
     * Constructor initializing the value from the specified settings object. If
     * the settings object doesn't contain a valid value for this model, it will
     * throw an InvalidSettingsException.
     * 
     * @param configName the identifier the value is stored with in the
     *            {@link org.knime.core.node.NodeSettings} object
     * @param settings the object to read the initial value from
     * @throws InvalidSettingsException if the settings object doesn't contain a
     *             (valid) value for this object
     */
    public SettingsModelFilterString(final String configName,
            final NodeSettingsRO settings) throws InvalidSettingsException {
        this(configName, (List<String>)null, (List<String>)null);
        loadSettingsForModel(settings);
    }

    /**
     * @see SettingsModel#getModelTypeID()
     */
    @Override
    String getModelTypeID() {
        return "SMID_filterString";
    }

    /**
     * @see SettingsModel#getConfigName()
     */
    @Override
    String getConfigName() {
        return m_configName;
    }

    /**
     * @see SettingsModel
     *      #loadSettingsForDialog(org.knime.core.node.NodeSettingsRO,
     *      org.knime.core.data.DataTableSpec[])
     */
    @Override
    void loadSettingsForDialog(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
        try {
            Config lists = settings.getConfig(m_configName);
            // the way we do this, partially correct settings will be parially
            // transferred into the dialog. Which is okay, I guess.
            setIncludeList(lists.getStringArray(CFGKEY_INCL, (String[])null));
            setExcludeList(lists.getStringArray(CFGKEY_EXCL, (String[])null));
        } catch (IllegalArgumentException iae) {
            // if the argument is not accepted: keep the old value.
        } catch (InvalidSettingsException ise) {
            // no settings - keep the old value.
        } finally {
            // always notify the listeners. That is, because there could be an
            // invalid value displayed in the listener.
            notifyChangeListeners();
        }

    }

    /**
     * @see SettingsModel
     *      #saveSettingsForDialog(org.knime.core.node.NodeSettingsWO)
     */
    @Override
    void saveSettingsForDialog(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        saveSettingsTo(settings);
    }

    /**
     * set the value of the stored include list.
     * 
     * @param newValue the new value to store as include list.
     */
    public void setIncludeList(final String[] newValue) {
        setIncludeList(Arrays.asList(newValue));
    }

    /**
     * set the value of the stored include list.
     * 
     * @param newValue the new value to store as include list.
     */
    public void setIncludeList(final Collection<String> newValue) {
        m_inclList.clear();
        if (newValue != null) {
            m_inclList.addAll(newValue);
        }
    }

    /**
     * @return the currently stored include list. Don't modify the list.
     */
    public List<String> getIncludeList() {
        return Collections.unmodifiableList(m_inclList);
    }

    /**
     * set the value of the stored exclude list.
     * 
     * @param newValue the new value to store as exclude list.
     */
    public void setExcludeList(final String[] newValue) {
        setExcludeList(Arrays.asList(newValue));
    }

    /**
     * set the value of the stored exclude list.
     * 
     * @param newValue the new value to store as exclude list.
     */
    public void setExcludeList(final Collection<String> newValue) {
        m_exclList.clear();
        if (newValue != null) {
            m_exclList.addAll(newValue);
        }
    }

    /**
     * @return the currently stored exclude list. Don't modify the list.
     */
    public List<String> getExcludeList() {
        return Collections.unmodifiableList(m_exclList);
    }

    /**
     * @see SettingsModel
     *      #loadSettingsForModel(org.knime.core.node.NodeSettingsRO)
     */
    @Override
    public void loadSettingsForModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        try {
            // no default value, throw an exception instead
            Config lists = settings.getConfig(m_configName);
            String[] incl = lists.getStringArray(CFGKEY_INCL);
            String[] excl = lists.getStringArray(CFGKEY_EXCL);
            setIncludeList(incl);
            setExcludeList(excl);
        } catch (IllegalArgumentException iae) {
            throw new InvalidSettingsException(iae.getMessage());
        }
    }

    /**
     * @see SettingsModel #saveSettingsForModel(NodeSettingsWO)
     */
    @Override
    public void saveSettingsForModel(final NodeSettingsWO settings) {
        Config lists = settings.addConfig(m_configName);
        lists.addStringArray(CFGKEY_INCL, getIncludeList().toArray(
                new String[0]));
        lists.addStringArray(CFGKEY_EXCL, getExcludeList().toArray(
                new String[0]));
    }

    /**
     * @see SettingsModel
     *      #validateSettingsForModel(org.knime.core.node.NodeSettingsRO)
     */
    @Override
    public void validateSettingsForModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // expect a sub-config with two string arrays: include and exclude list
        Config lists = settings.getConfig(m_configName);
        lists.getStringArray(CFGKEY_INCL);
        lists.getStringArray(CFGKEY_EXCL);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " ('" + m_configName + "')";
    }

}
