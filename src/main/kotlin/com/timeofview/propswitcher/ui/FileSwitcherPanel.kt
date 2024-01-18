package com.timeofview.propswitcher.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.ui.components.JBList
import com.timeofview.propswitcher.PropSwitcherSettings
import java.io.File
import javax.swing.*

class FileSwitcherPanel: JPanel() {


    private lateinit var fileToReplaceField: JTextField
    private lateinit var folderField: JTextField
    private lateinit var browseFileButton: JButton
    private lateinit var browseFolderButton: JButton
    private lateinit var fileListModel: DefaultListModel<String>
    private lateinit var fileList: JList<String>
    private val settings = PropSwitcherSettings.instance
    
    init {
        configureComponents()
        loadSettings()
    }

    private fun configureComponents() {
        setupFileToReplaceField()
        setupFolderField()
        setupFileList()
        setupButtons()

        createCenterPanel()
    }

    private fun setupFileToReplaceField() {
        fileToReplaceField = JTextField(20)
    }

    private fun setupFolderField() {
        folderField = JTextField(20)
    }


    private fun setupFileList() {
        fileListModel = DefaultListModel()
        fileList = JBList(fileListModel)
        fileList.selectionMode = ListSelectionModel.SINGLE_SELECTION
    }

    private fun setupButtons() {
        browseFileButton = JButton("Browse").apply {
            addActionListener {
                val fileDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
                val file = FileChooser.chooseFile(fileDescriptor, null, null)
                fileToReplaceField.text = file?.path ?: ""
                browseFolderButton.isEnabled = file != null
                settings.selectedFilePath = fileToReplaceField.text
            }
        }

        browseFolderButton = JButton("Browse").apply {
            addActionListener {
                val folderDescriptor = FileChooserDescriptor(false, true, false, false, false, false)
                val folder = FileChooser.chooseFile(folderDescriptor, null, null)
                folderField.text = folder?.path ?: ""
                settings.selectedFolderPath = folderField.text
                updateFileList()
            }
        }


        browseFolderButton.isEnabled = getFileToReplacePath() != null
    }

     fun createCenterPanel(): JComponent {

        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

        this.add(JLabel("Select File to Replace:"))
        this.add(fileToReplaceField)
        this.add(browseFileButton)
        this.add(JLabel("Select Folder with Replacement Files:"))
        this.add(folderField)
        this.add(browseFolderButton)
        this.add(JScrollPane(fileList))

        return this
    }

    fun getFileToReplacePath(): String? = fileToReplaceField.text

    fun getSelectedFolderPath(): String? = folderField.text
    fun getSelectedFilePath(): String? = fileList.selectedValue

    private fun loadSettings() {
        fileToReplaceField.text = settings.selectedFilePath
        folderField.text = settings.selectedFolderPath
        browseFolderButton.isEnabled = settings.selectedFilePath.isNotBlank()
        updateFileList()
    }

    fun doOKAction() {
        val settings = PropSwitcherSettings.instance
        settings.selectedFilePath = fileToReplaceField.text
        settings.selectedFolderPath = folderField.text
    }

    private fun updateFileList() {
        fileListModel.clear()
        val selectedFile = File(fileToReplaceField.text)
        if (selectedFile.exists()) {
            val folder = File(folderField.text)
            if (folder.isDirectory) {
                val extension = selectedFile.extension
                folder.listFiles { _, name -> name.endsWith(".$extension") }?.forEach {
                    fileListModel.addElement(it.name)
                }
            }
        }
    }


}