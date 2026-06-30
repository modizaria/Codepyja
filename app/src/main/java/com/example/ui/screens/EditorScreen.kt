package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SnippetEntity
import com.example.ui.viewmodel.CodingViewModel

@Composable
fun EditorScreen(
    viewModel: CodingViewModel,
    modifier: Modifier = Modifier
) {
    val editorCode by viewModel.editorCode.collectAsState()
    val editorLanguage by viewModel.editorLanguage.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val executionResult by viewModel.executionResult.collectAsState()

    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val selectedChallenge by viewModel.selectedChallenge.collectAsState()
    val savedSnippets by viewModel.savedSnippets.collectAsState()
    val snippetTitle by viewModel.snippetTitle.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadSheet by remember { mutableStateOf(false) }
    var showChallengeDialog by remember { mutableStateOf(false) }

    var consoleTab by remember { mutableStateOf("stdout") } // "stdout" or "feedback"

    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(verticalScrollState)
    ) {
        // --- TOP BAR CONFIG & CONTEXT ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (selectedLesson != null) {
                        "Practice Lesson"
                    } else if (selectedChallenge != null) {
                        "Challenge Arena"
                    } else {
                        "Coding Sandbox"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (selectedLesson != null) {
                        selectedLesson!!.title
                    } else if (selectedChallenge != null) {
                        selectedChallenge!!.title
                    } else {
                        "Free Practice - ${editorLanguage.uppercase()}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Quick reset or mode change buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { showChallengeDialog = true },
                    modifier = Modifier.testTag("challenges_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Challenges",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { showLoadSheet = true },
                    modifier = Modifier.testTag("load_snippets_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Open Snippets",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Context Card (Shows Lesson descriptions or Challenge conditions)
        AnimatedVisibility(
            visible = selectedLesson != null || selectedChallenge != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedLesson != null) "Lesson Objective:" else "Challenge instructions:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { viewModel.resetEditorToDefaults() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedLesson != null) selectedLesson!!.description else selectedChallenge!!.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (selectedLesson != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 Concept: ${selectedLesson!!.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (selectedChallenge != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "check",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Validation: ${selectedChallenge!!.testDescription}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }

        // --- LANGUAGE SWITCHER IN SANDBOX MODE ---
        if (selectedLesson == null && selectedChallenge == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = editorLanguage == "python",
                    onClick = { viewModel.setEditorLanguage("python") },
                    label = { Text("Python 3") },
                    leadingIcon = if (editorLanguage == "python") {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.testTag("chip_python")
                )

                FilterChip(
                    selected = editorLanguage == "javascript",
                    onClick = { viewModel.setEditorLanguage("javascript") },
                    label = { Text("JavaScript (ES6)") },
                    leadingIcon = if (editorLanguage == "javascript") {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.testTag("chip_javascript")
                )
            }
        }

        // --- CODE PLAYGROUND EDITOR PANEL ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E2E)) // Cozy terminal dark
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Line Number Column
                val lineCount = editorCode.split("\n").size.coerceAtLeast(1)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(36.dp)
                        .background(Color(0xFF181825))
                        .padding(top = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (i in 1..lineCount) {
                        Text(
                            text = i.toString(),
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFF585B70)
                            )
                        )
                    }
                }

                // Interactive Monospace Text Area
                TextField(
                    value = editorCode,
                    onValueChange = { viewModel.setEditorCode(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("code_editor_field"),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = Color(0xFFCDD6F4)
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    placeholder = {
                        Text(
                            "Write your code here...",
                            color = Color(0xFF7F849C),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- BUTTON BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.runCode() },
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp)
                    .testTag("run_code_button"),
                enabled = !isExecuting,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isExecuting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Run Code")
                }
            }

            OutlinedButton(
                onClick = { showSaveDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("save_snippet_button")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save")
            }

            IconButton(
                onClick = { viewModel.resetEditorToDefaults() },
                modifier = Modifier
                    .height(48.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .testTag("clear_editor_button")
            ) {
                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear", tint = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- OUTPUT CONSOLE (STDOUT & AI FEEDBACK TABS) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Tab(
                    selected = consoleTab == "stdout",
                    onClick = { consoleTab = "stdout" },
                    modifier = Modifier.testTag("console_tab_stdout")
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Terminal Stdout", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Tab(
                    selected = consoleTab == "feedback",
                    onClick = { consoleTab = "feedback" },
                    modifier = Modifier.testTag("console_tab_feedback")
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Smart Feedback", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Console output area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .background(Color(0xFF0F0F16))
                    .padding(12.dp)
            ) {
                if (consoleTab == "stdout") {
                    val outText = executionResult?.stdout ?: ""
                    val errText = executionResult?.error ?: ""
                    
                    Column {
                        if (errText.isNotEmpty()) {
                            Text(
                                text = "⚠️ Error log:\n$errText",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFFF38BA8)
                            )
                        } else if (outText.isNotEmpty()) {
                            Text(
                                text = outText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFFCDD6F4)
                            )
                        } else {
                            Text(
                                text = "Console empty. Write code and hit 'Run Code' above.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7F849C)
                            )
                        }
                    }
                } else {
                    val feedbackText = executionResult?.feedback ?: ""
                    if (feedbackText.isNotEmpty()) {
                        Text(
                            text = feedbackText,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                            color = Color(0xFFA6E3A1)
                        )
                    } else {
                        Text(
                            text = "No real-time feedback. Run code with a valid Gemini key to get comprehensive insights.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7F849C)
                        )
                    }
                }
            }
        }
    }

    // --- SAVE SNIPPET DIALOG ---
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Code Snippet") },
            text = {
                Column {
                    Text("Enter a name to save this snippet locally to your phone database.")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = snippetTitle,
                        onValueChange = { viewModel.setSnippetTitle(it) },
                        label = { Text("Snippet Title") },
                        placeholder = { Text("e.g. My Fibonacci Function") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("snippet_title_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentSnippet()
                        showSaveDialog = false
                    },
                    modifier = Modifier.testTag("dialog_confirm_save")
                ) {
                    Text("Save Snippet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- LOAD SNIPPETS BOTTOM SHEET ---
    if (showLoadSheet) {
        AlertDialog(
            onDismissRequest = { showLoadSheet = false },
            title = { Text("Saved Snippets Room DB") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    if (savedSnippets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No saved snippets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(savedSnippets) { snippet ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            viewModel.loadSnippet(snippet)
                                            showLoadSheet = false
                                        }
                                        .testTag("snippet_item_${snippet.id}"),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(snippet.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(snippet.language.uppercase(), fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteSnippet(snippet) },
                                            modifier = Modifier.testTag("delete_snippet_${snippet.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLoadSheet = false }) {
                    Text("Close")
                }
            }
        )
    }

    // --- CHALLENGES DIALOG ---
    if (showChallengeDialog) {
        AlertDialog(
            onDismissRequest = { showChallengeDialog = false },
            title = { Text("Choose a Challenge") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    Text("Build coding fundamentals by writing functions to solve specific criteria:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(viewModel.challenges) { challenge ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        viewModel.selectChallenge(challenge)
                                        showChallengeDialog = false
                                    }
                                    .testTag("challenge_item_${challenge.id}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(challenge.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(challenge.language.uppercase(), fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChallengeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
