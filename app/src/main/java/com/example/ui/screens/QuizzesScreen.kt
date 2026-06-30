package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.viewmodel.CodingViewModel

@Composable
fun QuizzesScreen(
    viewModel: CodingViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val questions by viewModel.currentQuizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsState()
    val isAnswered by viewModel.isQuizAnswered.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val isFinished by viewModel.isQuizFinished.collectAsState()

    val categories = listOf("Python", "JavaScript", "Logic")
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Interactive Quiz Practice",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Test your syntax knowledge and logic rules",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CATEGORY SELECTOR CHIPS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.selectQuizCategory(category) },
                    label = { Text(category) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quiz_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isFinished) {
            // --- QUIZ SCORE SUMMARY PAGE ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Category Completed!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You scored",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$score / ${questions.size}",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val rating = if (score == questions.size) "🏆 Flawless Victory!" else if (score >= questions.size / 2) "👍 Solid Effort!" else "📚 Review courses and retry!"
                    Text(
                        text = rating,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.restartQuiz() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_restart_quiz")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Practice Again")
                    }
                }
            }
        } else if (questions.isNotEmpty() && currentIndex < questions.size) {
            val q = questions[currentIndex]

            // --- QUIZ PROGRESS BAR ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${currentIndex + 1} of ${questions.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- QUESTION CARD ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = q.question,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, lineHeight = 22.sp),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- OPTIONS SELECTOR ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                q.options.forEachIndexed { idx, option ->
                    val isSelected = selectedOptionIndex == idx
                    val borderStroke = when {
                        isAnswered && idx == q.correctIndex -> BorderStroke(2.dp, Color(0xFF4CAF50)) // Correct green
                        isAnswered && isSelected && idx != q.correctIndex -> BorderStroke(2.dp, Color(0xFFF44336)) // Wrong red
                        isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    }
                    val bgCol = when {
                        isAnswered && idx == q.correctIndex -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        isAnswered && isSelected && idx != q.correctIndex -> Color(0xFFF44336).copy(alpha = 0.1f)
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswered) { viewModel.selectQuizOption(idx) }
                            .testTag("quiz_option_$idx"),
                        colors = CardDefaults.cardColors(containerColor = bgCol),
                        border = borderStroke
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = if (!isAnswered) { { viewModel.selectQuizOption(idx) } } else null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = if (isAnswered && idx == q.correctIndex) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- SUBMIT / NEXT CONTROLS ---
            if (!isAnswered) {
                Button(
                    onClick = { viewModel.submitQuizAnswer() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_submit_quiz_answer"),
                    enabled = selectedOptionIndex != null
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Check Answer")
                }
            } else {
                Button(
                    onClick = { viewModel.nextQuizQuestion() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_next_quiz_question"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(if (currentIndex + 1 < questions.size) "Next Question" else "See Results")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- EXPLANATION PANEL ---
                val isCorrect = selectedOptionIndex == q.correctIndex
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) Color(0xFF4CAF50).copy(alpha = 0.08f) else Color(0xFFF44336).copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, if (isCorrect) Color(0xFF4CAF50).copy(alpha = 0.4f) else Color(0xFFF44336).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isCorrect) "Excellent Work!" else "Not quite right!",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = q.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
