package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.database.ProgressDao
import com.example.data.database.ProgressEntity
import com.example.data.database.SnippetDao
import com.example.data.database.SnippetEntity
import com.example.data.network.Content
import com.example.data.network.GenerateContentRequest
import com.example.data.network.GenerationConfig
import com.example.data.network.Part
import com.example.data.network.RetrofitClient
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class ExecutionResponse(
    val stdout: String,
    val error: String?,
    val feedback: String
)

class AppRepository(
    private val snippetDao: SnippetDao,
    private val progressDao: ProgressDao
) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val executionAdapter = moshi.adapter(ExecutionResponse::class.java)

    val allSnippets: Flow<List<SnippetEntity>> = snippetDao.getAllSnippets()
    val allProgress: Flow<List<ProgressEntity>> = progressDao.getAllProgress()

    suspend fun saveSnippet(snippet: SnippetEntity) = withContext(Dispatchers.IO) {
        snippetDao.insertSnippet(snippet)
    }

    suspend fun deleteSnippet(id: Int) = withContext(Dispatchers.IO) {
        snippetDao.deleteSnippetById(id)
    }

    suspend fun saveLessonCompletion(lessonId: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        progressDao.saveProgress(ProgressEntity(lessonId = lessonId, isCompleted = isCompleted))
    }

    /**
     * Executes the user code either through Gemini (providing deep logic analysis + real simulation)
     * or via local basic simulation if offline or if no valid API key is present.
     */
    suspend fun executeCode(code: String, language: String): ExecutionResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isApiKeyConfigured = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (isApiKeyConfigured) {
            try {
                val promptText = """
                    You are a highly advanced virtual compiler and runtime environment for both Python and JavaScript.
                    Execute the following $language code and analyze its logic.
                    
                    Respond STRICTLY with a JSON object containing three fields:
                    1. "stdout": The standard text output that running this code on a terminal would print.
                    2. "error": Any syntax, compilation, or runtime error message, or null if execution was successful.
                    3. "feedback": Concise, smart, 1-2 sentence feedback explaining the key programming concept used, potential bugs, complexity, or optimization tip.
                    
                    Code to execute:
                    ${"```"}$language
                    $code
                    ${"```"}
                    
                    Ensure your entire response is a single, valid JSON object with NO markdown formatting (no ```json code blocks or backticks in the root response), no trailing commas, and is fully escaped.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = promptText)))),
                    generationConfig = GenerationConfig(
                        temperature = 0.2f,
                        responseMimeType = "application/json"
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: throw Exception("Empty response from AI engine")

                // Strip any accidental markdown formatting
                val cleanedJson = rawText.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val parsed = executionAdapter.fromJson(cleanedJson)
                if (parsed != null) {
                    return@withContext parsed
                } else {
                    throw Exception("Failed to parse AI execution output")
                }
            } catch (e: Exception) {
                Log.e("AppRepository", "Gemini execution failed, falling back to local simulator", e)
                return@withContext runLocalSimulation(code, language, "AI engine failed: ${e.localizedMessage}. Running in local mode.")
            }
        } else {
            return@withContext runLocalSimulation(code, language, "No Gemini API key found. Enter a key in the Secrets Panel for smart AI execution and real-time conceptual feedback!")
        }
    }

    private fun runLocalSimulation(code: String, language: String, note: String): ExecutionResponse {
        val stdoutBuilder = StringBuilder()
        var error: String? = null
        val lines = code.split("\n")

        try {
            if (language.equals("python", ignoreCase = true)) {
                // Basic local Python print simulator
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.startsWith("print(") && trimmed.endsWith(")")) {
                        val contentExpr = trimmed.substring(6, trimmed.length - 1).trim()
                        if (contentExpr.startsWith("\"") && contentExpr.endsWith("\"")) {
                            stdoutBuilder.append(contentExpr.substring(1, contentExpr.length - 1)).append("\n")
                        } else if (contentExpr.startsWith("'") && contentExpr.endsWith("'")) {
                            stdoutBuilder.append(contentExpr.substring(1, contentExpr.length - 1)).append("\n")
                        } else {
                            // Simple expression evaluation placeholder
                            stdoutBuilder.append("[Output of expression: $contentExpr]").append("\n")
                        }
                    }
                }
            } else {
                // Basic local JavaScript console.log simulator
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.startsWith("console.log(") && trimmed.endsWith(");")) {
                        val contentExpr = trimmed.substring(12, trimmed.length - 2).trim()
                        if (contentExpr.startsWith("\"") && contentExpr.endsWith("\"")) {
                            stdoutBuilder.append(contentExpr.substring(1, contentExpr.length - 1)).append("\n")
                        } else if (contentExpr.startsWith("'") && contentExpr.endsWith("'")) {
                            stdoutBuilder.append(contentExpr.substring(1, contentExpr.length - 1)).append("\n")
                        } else {
                            stdoutBuilder.append("[Output of expression: $contentExpr]").append("\n")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            error = "Local syntax simulator error: ${e.message}"
        }

        var stdout = stdoutBuilder.toString().trim()
        if (stdout.isEmpty() && error == null) {
            stdout = "[Code executed successfully with no print output]"
        }

        return ExecutionResponse(
            stdout = stdout,
            error = error,
            feedback = "💡 Offline Simulator: $note\nKeep coding to strengthen your logical fundamentals!"
        )
    }
}
