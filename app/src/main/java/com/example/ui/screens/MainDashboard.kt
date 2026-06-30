package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.viewmodel.CodingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    viewModel: CodingViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("courses") } // "courses", "editor", "quiz", "creator"

    val apiKey = BuildConfig.GEMINI_API_KEY
    val isAiActive = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "PyJS Studio",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        
                        // Smart AI Copilot Connectivity Indicator Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (isAiActive) Color(0xFF4CAF50).copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.secondaryContainer
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isAiActive) Color(0xFF4CAF50) else Color(0xFF7F849C))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAiActive) "AI COPILOT ACTIVE" else "LOCAL SANDBOX",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAiActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("app_top_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "courses",
                    onClick = { activeTab = "courses" },
                    icon = { Icon(imageVector = Icons.Default.List, contentDescription = "Courses") },
                    label = { Text("Courses", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_courses")
                )

                NavigationBarItem(
                    selected = activeTab == "editor",
                    onClick = { activeTab = "editor" },
                    icon = { Icon(imageVector = Icons.Default.Code, contentDescription = "Editor IDE") },
                    label = { Text("Playground", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_editor")
                )

                NavigationBarItem(
                    selected = activeTab == "quiz",
                    onClick = { activeTab = "quiz" },
                    icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Quiz Zone") },
                    label = { Text("Quiz Zone", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_quiz")
                )

                NavigationBarItem(
                    selected = activeTab == "creator",
                    onClick = { activeTab = "creator" },
                    icon = { Icon(imageVector = Icons.Default.Language, contentDescription = "App Creator") },
                    label = { Text("Creator", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_creator")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "courses" -> {
                    LessonsScreen(
                        viewModel = viewModel,
                        onNavigateToEditor = { activeTab = "editor" }
                    )
                }
                "editor" -> {
                    EditorScreen(viewModel = viewModel)
                }
                "quiz" -> {
                    QuizzesScreen(viewModel = viewModel)
                }
                "creator" -> {
                    CreatorScreen(viewModel = viewModel)
                }
            }
        }
    }
}
