package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.viewmodel.CodingViewModel

@Composable
fun CreatorScreen(
    viewModel: CodingViewModel,
    modifier: Modifier = Modifier
) {
    val htmlCode by viewModel.webHtmlCode.collectAsState()
    val cssCode by viewModel.webCssCode.collectAsState()
    val jsCode by viewModel.webJsCode.collectAsState()
    val previewHtml by viewModel.combinedWebPreview.collectAsState()

    var activeCreatorTab by remember { mutableStateOf("html") } // "html", "css", "js", "preview"
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "App & Web Creator",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Code HTML, CSS, JS and render live previews",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SUB TABS ROW ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            listOf(
                "html" to "HTML",
                "css" to "CSS",
                "js" to "JS (Logic)",
                "preview" to "Live Output"
            ).forEach { (key, label) ->
                val isSelected = activeCreatorTab == key
                Tab(
                    selected = isSelected,
                    onClick = {
                        activeCreatorTab = key
                        if (key == "preview") {
                            viewModel.updateWebPreview()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("creator_tab_$key")
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- DYNAMIC VIEWPORT RENDERER ---
        if (activeCreatorTab == "preview") {
            // Live Preview Mode
            Text(
                text = "📱 Web App Preview Output",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(
                            "https://pyjs-studio-creator",
                            previewHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.updateWebPreview() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("refresh_preview_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh Preview")
            }
        } else {
            // Code Writing Mode (HTML, CSS, JS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (activeCreatorTab) {
                        "html" -> "Structure (HTML)"
                        "css" -> "Styles (CSS)"
                        else -> "Interactions (JavaScript)"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = when (activeCreatorTab) {
                        "html" -> "index.html"
                        "css" -> "style.css"
                        else -> "app.js"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E2E))
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    val codeContent = when (activeCreatorTab) {
                        "html" -> htmlCode
                        "css" -> cssCode
                        else -> jsCode
                    }
                    val lineCount = codeContent.split("\n").size.coerceAtLeast(1)

                    // Line Numbers column
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

                    // TextField Input
                    TextField(
                        value = codeContent,
                        onValueChange = {
                            when (activeCreatorTab) {
                                "html" -> viewModel.updateWebCodes(it, cssCode, jsCode)
                                "css" -> viewModel.updateWebCodes(htmlCode, it, jsCode)
                                "js" -> viewModel.updateWebCodes(htmlCode, cssCode, it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("creator_editor_field_${activeCreatorTab}"),
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
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.updateWebPreview()
                    activeCreatorTab = "preview"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_preview_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run App")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Build & Run Preview")
            }
        }
    }
}
