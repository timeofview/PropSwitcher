package com.timeofview.propswitcher

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.timeofview.propswitcher.ui.PropSwitcherDialog
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class PropSwitcherAction : AnAction() {


    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            val dialog = PropSwitcherDialog(project)
            if (dialog.showAndGet()) {
                performFileCopy(dialog)
                performUpdateProperty(dialog)
            }
        }
    }

    private fun performFileCopy(dialog: PropSwitcherDialog) {
        val fileToReplacePath = dialog.fileSwitcherPanel.getFileToReplacePath()
        val selectedFilePath = dialog.fileSwitcherPanel.getSelectedFilePath()
        val selectedFolder = dialog.fileSwitcherPanel.getSelectedFolderPath()

        if (fileToReplacePath != null && selectedFilePath != null && selectedFolder != null) {
            try {
                val sourceFile = Paths.get(selectedFolder, selectedFilePath).toFile()
                val targetFile = File(fileToReplacePath)
                copyFile(sourceFile, targetFile)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun copyFile(source: File, destination: File) {
        if (source.exists() && destination.exists()) {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
            //TODO: Add success feedback to the user
        } else {
            //TODO: Handle the case where files do not exist or paths are invalid
        }
    }

    private fun performUpdateProperty(dialog: PropSwitcherDialog) {
        val propSwitcherPanel = dialog.propSwitcherPanel
        val propertyNameField = propSwitcherPanel.getPropertyNameField()
        val fileToReplacePropertyPath = propSwitcherPanel.getFileToReplacePropertyPath()
        val valueSwitch = propSwitcherPanel.getValueSwitch()

        if (propertyNameField != null && fileToReplacePropertyPath != null && valueSwitch.isNotBlank()) {
            val file = File(fileToReplacePropertyPath)
            if (file.exists() && file.isFile && propertyNameField.isNotBlank()) {
                changePropertyInFile(fileToReplacePropertyPath, propertyNameField, valueSwitch)
            }
        }
    }

    fun changePropertyInFile(fileName: String, propertyName: String, propertyValue: String) {
        val file = File(fileName)
        if (!file.exists()) {
            throw IllegalArgumentException("File $fileName non trovato.")
        }

        val lines = file.readLines().toMutableList()
        var propertyFound = false

        for (i in lines.indices) {
            if (lines[i].startsWith(propertyName)) {
                lines[i] = "$propertyName=$propertyValue"
                propertyFound = true
                break
            }
        }

        if (!propertyFound) {
            lines.add("$propertyName=$propertyValue")
        }

        file.writeText(lines.joinToString("\n"))
    }
}