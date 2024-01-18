package com.timeofview.propswitcher

import com.intellij.openapi.components.*

@State(
        name = "com.timeofview.propswitcher.PropSwitcherSettings",
        storages = [Storage(StoragePathMacros.CACHE_FILE)]
)
class PropSwitcherSettings : PersistentStateComponent<PropSwitcherSettings.State> {

    companion object {
        val instance: PropSwitcherSettings
            get() = service()
    }

    data class State(
            var selectedFilePath: String = "",
            var selectedFolderPath: String = "",
            var fileToReplacePropertyField: String = "",
            var propertyNameField: String = "",
            var valueSwitch: String = "")

    private var myState = State()

    override fun getState(): State? = myState

    override fun loadState(state: State) {
        myState = state
    }

    var selectedFilePath: String
        get() = myState.selectedFilePath
        set(value) {
            myState.selectedFilePath = value
        }

    var selectedFolderPath: String
        get() = myState.selectedFolderPath
        set(value) {
            myState.selectedFolderPath = value
        }

    var fileToReplacePropertyField: String
        get() = myState.fileToReplacePropertyField
        set(value) {
            myState.fileToReplacePropertyField = value
        }

    var propertyNameField: String
        get() = myState.propertyNameField
        set(value) {
            myState.propertyNameField = value
        }

    var valueSwitch: String
        get() = myState.valueSwitch
        set(value) {
            myState.valueSwitch = value
        }


}