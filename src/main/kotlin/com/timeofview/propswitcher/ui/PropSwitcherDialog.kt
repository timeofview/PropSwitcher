package com.timeofview.propswitcher

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.swing.*

class PropSwitcherDialog(val project: Project) : DialogWrapper(project, true) {
    private lateinit var panel: JPanel
    private lateinit var fileToReplaceField: JTextField
    private lateinit var folderField: JTextField
    private lateinit var browseFileButton: JButton
    private lateinit var browseFolderButton: JButton
    private lateinit var fileListModel: DefaultListModel<String>
    private lateinit var fileList: JList<String>
    private lateinit var resetButton: JButton
    private lateinit var fileToReplacePropertyField: JTextField
    private lateinit var browseFilePropertyButton: JButton
    private lateinit var propertyNameField: JTextField
    private lateinit var valueSwitch: JComboBox<String>

    init {
        init()

        title = "PropSwitcher - File and Folder Selection"

        loadSettings()
        updateFileList()
        updateVisibility()
    }

    private fun configureComponents() {
        setupFileToReplaceField()
        setupFolderField()
        setupFileList()
        setupButtons()
        setupSwithProperty()
    }

    private fun updateVisibility(){
        propertyNameField.isEnabled = getFileToReplacePropertyPath() != null
//        valueSwitch.isEnabled = getPropertyNameField() != null
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
                browseFolderButton.isEnabled = file != null // Abilita il pulsante se un file è stato scelto
            }
        }

        browseFolderButton = JButton("Browse").apply {
            addActionListener {
                val folderDescriptor = FileChooserDescriptor(false, true, false, false, false, false)
                val folder = FileChooser.chooseFile(folderDescriptor, null, null)
                folderField.text = folder?.path ?: ""
                updateFileList()
            }
        }
        resetButton = JButton("Reset to Git Base").apply {
            addActionListener {
                performVcsReset()
            }
        }

        browseFolderButton.isEnabled = getFileToReplacePath() != null
    }

    override fun createCenterPanel(): JComponent {
        configureComponents()

        panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(JLabel("Select File to Replace:"))
        panel.add(fileToReplaceField)
        panel.add(browseFileButton)
        panel.add(JLabel("Select Folder with Replacement Files:"))
        panel.add(folderField)
        panel.add(browseFolderButton)
        panel.add(JScrollPane(fileList))

        panel.add(JLabel("Select Properties File:"))
        panel.add(fileToReplacePropertyField)
        panel.add(browseFilePropertyButton)
        panel.add(JLabel("Property Name:"))
        panel.add(propertyNameField)
        panel.add(JLabel("Value:"))
        panel.add(valueSwitch)

        panel.add(resetButton)


        return panel
    }

    private fun setupSwithProperty() {
        panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        fileToReplacePropertyField = JTextField(20)
        propertyNameField = JTextField(20)
        valueSwitch = ComboBox(arrayOf("cmt", "om"))

        browseFilePropertyButton = JButton("Browse").apply {
            addActionListener {
                val fileDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
                val file = FileChooser.chooseFile(fileDescriptor, project, null)
                fileToReplacePropertyField.text = file?.path ?: ""
                updateVisibility()
            }
        }
    }

    fun getFileToReplacePath(): String? = fileToReplaceField.text.takeIf { it.isNotEmpty() }

    fun getSelectedFolderPath(): String? = folderField.text.takeIf { it.isNotEmpty() }
    fun getSelectedFilePath(): String? = fileList.selectedValue.takeIf { it.isNotEmpty() }
    fun getFileToReplacePropertyPath(): String? = fileToReplacePropertyField.text.takeIf { it.isNotEmpty() }
    fun getPropertyNameField(): String? = propertyNameField.text.takeIf { it.isNotEmpty() }

    private fun loadSettings() {
        val settings = PropSwitcherSettings.instance
        fileToReplaceField.text = settings.selectedFilePath
        folderField.text = settings.selectedFolderPath
    }

    override fun doOKAction() {
        val settings = PropSwitcherSettings.instance
        settings.selectedFilePath = fileToReplaceField.text
        settings.selectedFolderPath = folderField.text
        super.doOKAction()
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


    private fun performVcsReset() {
        val filePath = fileToReplaceField.text
        if (filePath.isNotEmpty() && project.basePath != null) {
            revertGitChange(project.basePath!!, filePath)
        }
    }

    fun revertGitChange(projectPath: String, filePath: String) {
        try {
            // Costruisci il comando Git
            val command = listOf("git", "checkout", "HEAD", filePath)

            // Esegui il comando nel contesto del progetto
            val processBuilder = ProcessBuilder(command)
            processBuilder.directory(File(projectPath))
            val process = processBuilder.start()

            // Leggi l'output del comando
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText()

            // Attendi il completamento del processo
            process.waitFor()

            // Gestisci l'output o eventuali errori
            if (process.exitValue() == 0) {
                println("Revert completato: $output")
            } else {
                println("Errore nel revert: $output")
            }
            doOKAction()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}