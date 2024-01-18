package com.timeofview.propswitcher.executor

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class GitCommandExecutor {

    fun revertGitChange(projectPath: String, filePath: String) {
        try {
            val command = listOf("git", "checkout", "HEAD", filePath)

            val processBuilder = ProcessBuilder(command)
            processBuilder.directory(File(projectPath))
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText()

            process.waitFor()

            if (process.exitValue() == 0) {
                println("Revert completato: $output")
            } else {
                println("Errore nel revert: $output")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}