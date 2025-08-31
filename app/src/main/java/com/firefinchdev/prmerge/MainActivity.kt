package com.firefinchdev.prmerge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firefinchdev.prmerge.auth.BiometricAuthManager
import com.firefinchdev.prmerge.auth.BiometricAuthStatus
import com.firefinchdev.prmerge.data.GitHubRepository
import com.firefinchdev.prmerge.data.PreferencesManager
import com.firefinchdev.prmerge.ui.PRMergeScreen
import com.firefinchdev.prmerge.ui.PRMergeViewModel
import com.firefinchdev.prmerge.ui.SettingsScreen
import com.firefinchdev.prmerge.ui.theme.PRMergeTheme

class MainActivity : FragmentActivity() {

    val authManager = BiometricAuthManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (authManager.isBiometricAvailable() == BiometricAuthStatus.AVAILABLE) {
            authManager.authenticate(
                onSuccess = {
                    setContent {
                        PRMergeTheme {
                            PRMergeApp()
                        }
                    }
                },
                onError = {
                    Toast.makeText(this@MainActivity, "Enable Biometric to use the app", Toast.LENGTH_SHORT).show()
                },
                onFailed = {
                    Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this@MainActivity, "Enable Biometric to use the app", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun PRMergeApp() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val repository = remember { GitHubRepository() }
    val preferencesManager = remember { PreferencesManager(context) }

    val viewModel: PRMergeViewModel = viewModel {
        PRMergeViewModel(repository, preferencesManager)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main") {
                PRMergeScreen(
                    uiState = uiState,
                    onPRLinkChange = viewModel::updatePRLink,
                    onApproveBeforeMergeChange = viewModel::updateApproveBeforeMerge,
                    onMergePR = viewModel::mergePR,
                    onNavigateToSettings = { navController.navigate("settings") },
                    onClearMessages = viewModel::clearMessages
                )
            }

            composable("settings") {
                SettingsScreen(
                    currentToken = viewModel.getGitHubToken(),
                    onSaveToken = viewModel::saveGitHubToken,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}