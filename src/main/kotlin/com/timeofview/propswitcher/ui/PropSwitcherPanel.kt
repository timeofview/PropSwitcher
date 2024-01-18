package com.timeofview.propswitcher.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.ComboBox
import com.timeofview.propswitcher.PropSwitcherSettings
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class PropSwitcherPanel : JPanel() {

    private lateinit var fileToReplacePropertyField: JTextField
    private lateinit var browseFilePropertyButton: JButton
    private lateinit var propertyNameField: JTextField
    private lateinit var valueSwitch: JComboBox<String>
    private val settings = PropSwitcherSettings.instance

    init {
        setupSwichProperty()
        loadSettings()
    }


    private fun loadSettings() {
        fileToReplacePropertyField.text = settings.fileToReplacePropertyField
        propertyNameField.text = settings.propertyNameField
        propertyNameField.isEnabled = settings.propertyNameField.isNotBlank()
        if (settings.valueSwitch.isNotBlank()) {
            valueSwitch.selectedItem = settings.valueSwitch
        }
    }

    private fun setupSwichProperty() {

        fileToReplacePropertyField = JTextField(20)
        propertyNameField = JTextField(20)
        valueSwitch = ComboBox(arrayOf("cmt", "om"))

        browseFilePropertyButton = JButton("Browse").apply {
            addActionListener {
                val fileDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
                val file = FileChooser.chooseFile(fileDescriptor, null, null)
                fileToReplacePropertyField.text = file?.path ?: ""
                settings.fileToReplacePropertyField = fileToReplacePropertyField.text
                updateVisibility()
            }
        }

        propertyNameField.document.addDocumentListener(documentListener())
        valueSwitch.addItemListener { settings.valueSwitch =  getValueSwitch()}

        this.add(JLabel("Select Properties File:"))
        this.add(fileToReplacePropertyField)
        this.add(browseFilePropertyButton)
        this.add(JLabel("Property Name:"))
        this.add(propertyNameField)
        this.add(JLabel("Value:"))
        this.add(valueSwitch)
    }

    private fun documentListener() = object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) {
            textChanged()
        }

        override fun removeUpdate(e: DocumentEvent?) {
            textChanged()
        }

        override fun changedUpdate(e: DocumentEvent?) {
        }

        private fun textChanged() {
            settings.propertyNameField = propertyNameField.text
        }

    }

    private fun updateVisibility() {
        propertyNameField.isEnabled = getFileToReplacePropertyPath() != null
    }

    fun getFileToReplacePropertyPath(): String? = fileToReplacePropertyField.text.takeIf { it.isNotEmpty() }
    fun getPropertyNameField(): String? = propertyNameField.text.takeIf { it.isNotEmpty() }
    fun getValueSwitch(): String = valueSwitch.selectedItem.takeIf { it is String && it.isNotEmpty() } as String

}