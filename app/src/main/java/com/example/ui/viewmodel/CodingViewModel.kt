package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.ProgressEntity
import com.example.data.database.SnippetEntity
import com.example.data.model.CodingChallenge
import com.example.data.model.CodingData
import com.example.data.model.Lesson
import com.example.data.model.QuizQuestion
import com.example.data.repository.AppRepository
import com.example.data.repository.ExecutionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CodingViewModel(private val repository: AppRepository) : ViewModel() {

    // --- DB DATA ---
    val savedSnippets: StateFlow<List<SnippetEntity>> = repository.allSnippets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedLessonIds: StateFlow<Set<String>> = repository.allProgress
        .map { progressList -> progressList.filter { it.isCompleted }.map { it.lessonId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // --- LESSONS ---
    val pythonLessons = CodingData.lessons.filter { it.language == "python" }
    val jsLessons = CodingData.lessons.filter { it.language == "javascript" }
    
    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = _selectedLesson.asStateFlow()

    // --- EDITOR STATE ---
    private val _editorLanguage = MutableStateFlow("python") // "python", "javascript"
    val editorLanguage: StateFlow<String> = _editorLanguage.asStateFlow()

    private val _editorCode = MutableStateFlow("")
    val editorCode: StateFlow<String> = _editorCode.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    private val _executionResult = MutableStateFlow<ExecutionResponse?>(null)
    val executionResult: StateFlow<ExecutionResponse?> = _executionResult.asStateFlow()

    // --- SNIPPET SAVING STATE ---
    private val _snippetTitle = MutableStateFlow("")
    val snippetTitle: StateFlow<String> = _snippetTitle.asStateFlow()

    // --- QUIZ ZONE STATE ---
    private val _selectedCategory = MutableStateFlow("Python") // "Python", "JavaScript", "Logic"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _currentQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuizQuestions: StateFlow<List<QuizQuestion>> = _currentQuizQuestions.asStateFlow()

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _isQuizAnswered = MutableStateFlow(false)
    val isQuizAnswered: StateFlow<Boolean> = _isQuizAnswered.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    // --- WEB/APP CREATOR STATE ---
    private val _webHtmlCode = MutableStateFlow("<h1>Hello PyJS App!</h1>\n<p>Edit HTML, CSS, and JS to build mobile web apps instantly!</p>\n<button onclick=\"sayHello()\">Click Me</button>")
    val webHtmlCode: StateFlow<String> = _webHtmlCode.asStateFlow()

    private val _webCssCode = MutableStateFlow("body {\n  background: #11111b;\n  color: #cdd6f4;\n  font-family: sans-serif;\n  text-align: center;\n  padding-top: 40px;\n}\nbutton {\n  background: #a6e3a1;\n  color: #11111b;\n  border: none;\n  padding: 12px 24px;\n  font-size: 16px;\n  border-radius: 8px;\n  font-weight: bold;\n  cursor: pointer;\n}")
    val webCssCode: StateFlow<String> = _webCssCode.asStateFlow()

    private val _webJsCode = MutableStateFlow("function sayHello() {\n  alert('Hello from your newly created mobile app!');\n}")
    val webJsCode: StateFlow<String> = _webJsCode.asStateFlow()

    private val _combinedWebPreview = MutableStateFlow("")
    val combinedWebPreview: StateFlow<String> = _combinedWebPreview.asStateFlow()

    // --- CODING CHALLENGES ---
    val challenges = CodingData.challenges
    private val _selectedChallenge = MutableStateFlow<CodingChallenge?>(null)
    val selectedChallenge: StateFlow<CodingChallenge?> = _selectedChallenge.asStateFlow()

    init {
        // Prepare initial quiz and editor state
        loadQuizzesForCategory("Python")
        resetEditorToDefaults()
        updateWebPreview()
    }

    // --- ACTIONS ---

    fun selectLesson(lesson: Lesson) {
        _selectedLesson.value = lesson
        _editorLanguage.value = lesson.language
        _editorCode.value = lesson.codeTemplate
        _executionResult.value = null
        _selectedChallenge.value = null
    }

    fun selectChallenge(challenge: CodingChallenge) {
        _selectedChallenge.value = challenge
        _editorLanguage.value = challenge.language
        _editorCode.value = challenge.startCode
        _executionResult.value = null
        _selectedLesson.value = null
    }

    fun setEditorCode(code: String) {
        _editorCode.value = code
    }

    fun setEditorLanguage(lang: String) {
        _editorLanguage.value = lang
        resetEditorToDefaults()
    }

    fun resetEditorToDefaults() {
        _executionResult.value = null
        if (_editorLanguage.value == "python") {
            _editorCode.value = "# Python Playground\nprint('Welcome to PyJS Python Editor!')\n\n# Try writing a loops or simple math\nfor i in range(1, 4):\n    print('Line', i)"
        } else {
            _editorCode.value = "// JavaScript Playground\nconsole.log('Welcome to JavaScript Editor!');\n\nconst greet = (user) => {\n  console.log('Hello ' + user);\n};\n\ngreet('Developer');"
        }
    }

    fun runCode() {
        val currentCode = _editorCode.value
        val currentLang = _editorLanguage.value
        _isExecuting.value = true
        _executionResult.value = null

        viewModelScope.launch {
            try {
                val result = repository.executeCode(currentCode, currentLang)
                _executionResult.value = result

                // Automatically check if this code completes the selected lesson or challenge
                val lesson = _selectedLesson.value
                if (lesson != null && result.error == null) {
                    val cleanedStdout = result.stdout.trim().replace("\r", "")
                    val expected = lesson.expectedOutput.trim().replace("\r", "")
                    if (cleanedStdout.contains(expected) || expected.contains(cleanedStdout)) {
                        repository.saveLessonCompletion(lesson.id, true)
                    }
                }

                val challenge = _selectedChallenge.value
                if (challenge != null && result.error == null) {
                    // Check expected keywords
                    val containsAllKeywords = challenge.expectedKeywords.all { currentCode.contains(it) }
                    if (containsAllKeywords) {
                        // Mark challenge as "completed" using a synthetic lesson ID for tracking progress
                        repository.saveLessonCompletion("challenge_${challenge.id}", true)
                    }
                }

            } catch (e: Exception) {
                _executionResult.value = ExecutionResponse(
                    stdout = "",
                    error = e.localizedMessage ?: "Execution interrupted",
                    feedback = "An unexpected error occurred during execution."
                )
            } finally {
                _isExecuting.value = false
            }
        }
    }

    // --- SNIPPET SAVING ---
    fun setSnippetTitle(title: String) {
        _snippetTitle.value = title
    }

    fun saveCurrentSnippet() {
        val title = _snippetTitle.value.ifBlank { "Untitled ${editorLanguage.value.uppercase()}" }
        val snippet = SnippetEntity(
            title = title,
            language = _editorLanguage.value,
            code = _editorCode.value,
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.saveSnippet(snippet)
            _snippetTitle.value = ""
        }
    }

    fun loadSnippet(snippet: SnippetEntity) {
        _editorLanguage.value = snippet.language
        _editorCode.value = snippet.code
        _executionResult.value = null
        _selectedLesson.value = null
        _selectedChallenge.value = null
    }

    fun deleteSnippet(snippet: SnippetEntity) {
        viewModelScope.launch {
            repository.deleteSnippet(snippet.id)
        }
    }

    // --- QUIZ FUNCTIONS ---
    fun selectQuizCategory(category: String) {
        _selectedCategory.value = category
        loadQuizzesForCategory(category)
    }

    private fun loadQuizzesForCategory(category: String) {
        val filtered = CodingData.quizzes.filter { it.category.equals(category, ignoreCase = true) }
        _currentQuizQuestions.value = filtered
        _currentQuizIndex.value = 0
        _selectedOptionIndex.value = null
        _isQuizAnswered.value = false
        _quizScore.value = 0
        _isQuizFinished.value = false
    }

    fun selectQuizOption(index: Int) {
        if (!_isQuizAnswered.value) {
            _selectedOptionIndex.value = index
        }
    }

    fun submitQuizAnswer() {
        val optionIndex = _selectedOptionIndex.value ?: return
        val currentQuestions = _currentQuizQuestions.value
        val currentIndex = _currentQuizIndex.value
        if (currentIndex < currentQuestions.size && !_isQuizAnswered.value) {
            val isCorrect = optionIndex == currentQuestions[currentIndex].correctIndex
            if (isCorrect) {
                _quizScore.value += 1
            }
            _isQuizAnswered.value = true
        }
    }

    fun nextQuizQuestion() {
        val currentQuestions = _currentQuizQuestions.value
        val currentIndex = _currentQuizIndex.value
        if (currentIndex + 1 < currentQuestions.size) {
            _currentQuizIndex.value = currentIndex + 1
            _selectedOptionIndex.value = null
            _isQuizAnswered.value = false
        } else {
            _isQuizFinished.value = true
        }
    }

    fun restartQuiz() {
        loadQuizzesForCategory(_selectedCategory.value)
    }

    // --- WEB APP CREATOR ---
    fun updateWebCodes(html: String, css: String, js: String) {
        _webHtmlCode.value = html
        _webCssCode.value = css
        _webJsCode.value = js
        updateWebPreview()
    }

    fun updateWebPreview() {
        val compiledHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                    ${_webCssCode.value}
                </style>
            </head>
            <body>
                ${_webHtmlCode.value}
                
                <script>
                    ${_webJsCode.value}
                </script>
            </body>
            </html>
        """.trimIndent()
        _combinedWebPreview.value = compiledHtml
    }
}

class CodingViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CodingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CodingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
