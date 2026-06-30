package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lesson
import com.example.ui.viewmodel.CodingViewModel

@Composable
fun LessonsScreen(
    viewModel: CodingViewModel,
    onNavigateToEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedIds by viewModel.completedLessonIds.collectAsState()
    val pythonLessons = viewModel.pythonLessons
    val jsLessons = viewModel.jsLessons

    var selectedLangTab by remember { mutableStateOf("python") } // "python" or "javascript"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // Hero Course Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = if (selectedLangTab == "python") {
                                listOf(Color(0xFF306998), Color(0xFFFFE873))
                            } else {
                                listOf(Color(0xFFF7DF1E), Color(0xFFF0DB4F))
                            }
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = if (selectedLangTab == "python") "Python Mastery" else "JavaScript Web Ninja",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedLangTab == "python") Color.White else Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedLangTab == "python") {
                            "Master data science, scripting, and OOP structures."
                        } else {
                            "Master web engineering, closures, and async Promises."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (selectedLangTab == "python") Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Custom Lang Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { selectedLangTab = "python" },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_python"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedLangTab == "python") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedLangTab == "python") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = "Python Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Python")
                }

                Button(
                    onClick = { selectedLangTab = "javascript" },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_javascript"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedLangTab == "javascript") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedLangTab == "javascript") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = "JS Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("JavaScript")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active list based on Lang selection
        val lessons = if (selectedLangTab == "python") pythonLessons else jsLessons
        val basicLessons = lessons.filter { !it.isAdvanced }
        val advancedLessons = lessons.filter { it.isAdvanced }

        item {
            Text(
                text = "Basic Courses",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (basicLessons.isEmpty()) {
            item {
                Text("No basic lessons available.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(basicLessons) { lesson ->
                LessonCard(
                    lesson = lesson,
                    isCompleted = completedIds.contains(lesson.id),
                    onClick = {
                        viewModel.selectLesson(lesson)
                        onNavigateToEditor()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Text(
                text = "Advanced Logic & Algorithms",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        if (advancedLessons.isEmpty()) {
            item {
                Text("No advanced lessons available.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(advancedLessons) { lesson ->
                LessonCard(
                    lesson = lesson,
                    isCompleted = completedIds.contains(lesson.id),
                    onClick = {
                        viewModel.selectLesson(lesson)
                        onNavigateToEditor()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Coding Challenges Quick Access Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Coding Challenges",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Practice algorithmic challenges with real test cases! Select a challenge in the Editor Playground to test your mettle.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lesson_card_${lesson.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (lesson.isAdvanced) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("ADVANCED", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    } else {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("BASIC", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "Practice Lesson",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
