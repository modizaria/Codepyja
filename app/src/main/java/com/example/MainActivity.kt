package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.database.AppDatabase
import com.example.data.repository.AppRepository
import com.example.ui.screens.MainDashboard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CodingViewModel
import com.example.ui.viewmodel.CodingViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room Database, DAOs, Repository, and ViewModel
        val database = AppDatabase.getDatabase(applicationContext)
        val snippetDao = database.snippetDao()
        val progressDao = database.progressDao()
        val repository = AppRepository(snippetDao, progressDao)
        val factory = CodingViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[CodingViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainDashboard(viewModel = viewModel)
                }
            }
        }
    }
}
