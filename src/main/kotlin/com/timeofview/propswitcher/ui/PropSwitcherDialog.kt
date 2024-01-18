package com.timeofview.propswitcher.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.timeofview.propswitcher.executor.GitCommandExecutor
import kotlinx.coroutines.*
import net.sf.cglib.proxy.Dispatcher
import org.apache.tools.ant.taskdefs.Execute.launch
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class PropSwitcherDialog(val project: Project) : DialogWrapper(project, true) {

    lateinit var fileSwitcherPanel: FileSwitcherPanel
    lateinit var propSwitcherPanel: PropSwitcherPanel

    private lateinit var panel: JPanel

    private lateinit var resetButton: JButton
    private lateinit var gitCommandExecutor: GitCommandExecutor


    init {
        init()
        title = "PropSwitcher"
    }

    private fun configureComponents() {
        fileSwitcherPanel = FileSwitcherPanel()
        propSwitcherPanel = PropSwitcherPanel()
        gitCommandExecutor = GitCommandExecutor()

        resetButton = JButton("Reset").apply {
            addActionListener {
                performVcsReset()
            }
        }
    }




    override fun createCenterPanel(): JComponent {
        configureComponents()

        panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(fileSwitcherPanel)
        panel.add(propSwitcherPanel)
        panel.add(resetButton)

        return panel
    }


    override fun doOKAction() {
        fileSwitcherPanel.doOKAction()
        super.doOKAction()
    }


    private fun performVcsReset() {
        val filePath = fileSwitcherPanel.getFileToReplacePath()
        val fileToReplacePropertyPath = propSwitcherPanel.getFileToReplacePropertyPath()
        CoroutineScope(Dispatchers.Main).launch {
            val revertJobs = mutableListOf<Job>()

            if (filePath?.isNotEmpty() == true && project.basePath != null) {
                revertJobs += launch {
                    gitCommandExecutor.revertGitChange(project.basePath!!, filePath)
                }
            }
            if (fileToReplacePropertyPath?.isNotEmpty() == true && project.basePath != null) {
                revertJobs += launch {
                    gitCommandExecutor.revertGitChange(project.basePath!!, fileToReplacePropertyPath)
                }
            }
            revertJobs.forEach { _ -> joinAll() }
            doOKAction()
        }
    }


}