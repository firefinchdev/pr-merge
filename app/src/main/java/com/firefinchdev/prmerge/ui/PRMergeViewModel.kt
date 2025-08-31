package com.firefinchdev.prmerge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firefinchdev.prmerge.data.GitHubRepository
import com.firefinchdev.prmerge.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PRMergeViewModel(
    private val repository: GitHubRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PRMergeUiState())
    val uiState: StateFlow<PRMergeUiState> = _uiState.asStateFlow()

    fun updatePRLink(link: String) {
        _uiState.value = _uiState.value.copy(prLink = link, errorMessage = null)
    }

    fun updateApproveBeforeMerge(approve: Boolean) {
        _uiState.value = _uiState.value.copy(approveBeforeMerge = approve)
    }

    fun mergePR() {
        val currentState = _uiState.value
        val prLink = currentState.prLink.trim()

        if (prLink.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter a PR link")
            return
        }

        val token = preferencesManager.getGitHubToken()
        if (token.isNullOrEmpty()) {
            _uiState.value =
                currentState.copy(errorMessage = "Please set your GitHub token in settings")
            return
        }

        val prInfo = repository.extractPRInfo(prLink)
        if (prInfo == null) {
            _uiState.value = currentState.copy(errorMessage = "Invalid GitHub PR link")
            return
        }

        _uiState.value =
            currentState.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                if (currentState.approveBeforeMerge) {
                    val approveResult = repository.approvePR(
                        prInfo.owner,
                        prInfo.repo,
                        prInfo.prNumber,
                        token
                    )

                    if (approveResult.isFailure) {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            errorMessage = "Failed to approve PR: ${approveResult.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }
                }

                val mergeResult = repository.mergePR(
                    prInfo.owner,
                    prInfo.repo,
                    prInfo.prNumber,
                    token
                )

                if (mergeResult.isSuccess) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        successMessage = "PR #${prInfo.prNumber} merged successfully!",
                        prLink = ""
                    )
                } else {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Failed to merge PR: ${mergeResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun getGitHubToken(): String? {
        return preferencesManager.getGitHubToken()
    }

    fun saveGitHubToken(token: String) {
        preferencesManager.saveGitHubToken(token)
    }
}

data class PRMergeUiState(
    val prLink: String = "",
    val approveBeforeMerge: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)