package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val codeTemplate: String,
    val expectedOutput: String,
    val language: String, // "python", "javascript"
    val isAdvanced: Boolean,
    val explanation: String
)

@JsonClass(generateAdapter = true)
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String // "Python", "JavaScript", "Logic"
)

@JsonClass(generateAdapter = true)
data class CodingChallenge(
    val id: String,
    val title: String,
    val description: String,
    val language: String, // "python", "javascript"
    val startCode: String,
    val expectedKeywords: List<String>,
    val testDescription: String
)

object CodingData {
    val lessons = listOf(
        // === PYTHON COURSES ===
        // Basic Python
        Lesson(
            id = "py_basic_syntax",
            title = "Python Basics & Variables",
            description = "Learn how to declare variables and output text to the console in Python. Python is clean, readable, and uses indentation for blocks.",
            codeTemplate = "name = \"Developer\"\nprint(\"Hello, \" + name)\nage = 25\nprint(\"Double age:\", age * 2)",
            expectedOutput = "Hello, Developer\nDouble age: 50",
            language = "python",
            isAdvanced = false,
            explanation = "Variables hold values. In Python, you do not need to declare types explicitly. The `print()` function outputs text."
        ),
        Lesson(
            id = "py_conditionals",
            title = "Conditional logic (If/Else)",
            description = "Use conditional statements `if`, `elif`, and `else` to control the flow of your program based on conditions.",
            codeTemplate = "score = 85\nif score >= 90:\n    print(\"Grade A\")\nelif score >= 80:\n    print(\"Grade B\")\nelse:\n    print(\"Grade C\")",
            expectedOutput = "Grade B",
            language = "python",
            isAdvanced = false,
            explanation = "In Python, block indentation is mandatory. Code with the same indentation belongs to the same block."
        ),
        Lesson(
            id = "py_loops",
            title = "Python Loops (For & While)",
            description = "Loops let you repeat code blocks. A `for` loop in Python iterates over a range or a list sequence.",
            codeTemplate = "sum_val = 0\nfor i in range(1, 5):\n    sum_val += i\nprint(\"Sum 1 to 4:\", sum_val)",
            expectedOutput = "Sum 1 to 4: 10",
            language = "python",
            isAdvanced = false,
            explanation = "`range(1, 5)` generates integers from 1 up to (but excluding) 5. The loop adds them up."
        ),
        // Advanced Python
        Lesson(
            id = "py_functions",
            title = "Functions and Parameters",
            description = "Functions are reusable blocks of code defined using the `def` keyword. They return values using `return`.",
            codeTemplate = "def power(base, exponent):\n    return base ** exponent\n\nresult = power(2, 5)\nprint(\"2 to the power of 5 is:\", result)",
            expectedOutput = "2 to the power of 5 is: 32",
            language = "python",
            isAdvanced = true,
            explanation = "The double asterisk `**` is the exponentiation operator in Python. Functions help modularize your code."
        ),
        Lesson(
            id = "py_oop",
            title = "Object-Oriented Python (OOP)",
            description = "Classes are blueprints for objects. Define attributes in `__init__` constructor and write instance methods.",
            codeTemplate = "class CodingRobot:\n    def __init__(self, name, lang):\n        self.name = name\n        self.lang = lang\n    \n    def greet(self):\n        return f\"I am {self.name}, specialized in {self.lang}!\"\n\nbot = CodingRobot(\"Alpha\", \"Python\")\nprint(bot.greet())",
            expectedOutput = "I am Alpha, specialized in Python!",
            language = "python",
            isAdvanced = true,
            explanation = "The `self` parameter represents the current object instance and is required for object constructors and methods."
        ),

        // === JAVASCRIPT COURSES ===
        // Basic JavaScript
        Lesson(
            id = "js_basic_syntax",
            title = "JavaScript Basics & Types",
            description = "JavaScript runs the web. Declare variables using `let` or `const` and output using `console.log()`.",
            codeTemplate = "const language = \"JavaScript\";\nlet level = \"Basics\";\nconsole.log(\"Learning: \" + language + \" - \" + level);",
            expectedOutput = "Learning: JavaScript - Basics",
            language = "javascript",
            isAdvanced = false,
            explanation = "`const` defines block-scoped read-only constants, while `let` defines block-scoped reassignable variables."
        ),
        Lesson(
            id = "js_arrays_objects",
            title = "JS Arrays and Object Literals",
            description = "Learn how to structure collections using Arrays `[]` and flexible dynamic Objects `{}`.",
            codeTemplate = "const skills = [\"HTML\", \"CSS\", \"JS\"];\nconst dev = {\n    name: \"Alex\",\n    experience: 3\n};\nconsole.log(dev.name + \" knows \" + skills[2]);",
            expectedOutput = "Alex knows JS",
            language = "javascript",
            isAdvanced = false,
            explanation = "Arrays are zero-indexed. Object properties can be accessed using dot notation or bracket notation."
        ),
        Lesson(
            id = "js_control_flow",
            title = "JS Control Flow & Arrays",
            description = "Loop through arrays or repeat statements using traditional `for` loops, `forEach`, or `for...of`.",
            codeTemplate = "const numbers = [10, 20, 30];\nlet total = 0;\nfor (let num of numbers) {\n    total += num;\n}\nconsole.log(\"Total sum:\", total);",
            expectedOutput = "Total sum: 60",
            language = "javascript",
            isAdvanced = false,
            explanation = "The `for...of` statement loops through values of an iterable object such as an array."
        ),
        // Advanced JavaScript
        Lesson(
            id = "js_async",
            title = "Asynchronous JS & Promises",
            description = "JavaScript is single-threaded but handles asynchronous tasks using Promises and the modern `async/await` syntax.",
            codeTemplate = "async function fetchData() {\n    return \"Success!\";\n}\n\nfetchData().then(result => {\n    console.log(\"API Status:\", result);\n});",
            expectedOutput = "API Status: Success!",
            language = "javascript",
            isAdvanced = true,
            explanation = "An `async` function always returns a Promise. `then` handles resolution once the task completes."
        ),
        Lesson(
            id = "js_closures",
            title = "Closures and Scoping",
            description = "A closure is the combination of a function bundled together with references to its surrounding state.",
            codeTemplate = "function makeCounter() {\n    let count = 0;\n    return function() {\n        count++;\n        return count;\n    };\n}\n\nconst counter = makeCounter();\nconsole.log(counter() + \", \" + counter());",
            expectedOutput = "1, 2",
            language = "javascript",
            isAdvanced = true,
            explanation = "The inner function retains access to the outer function's variable (`count`) even after the outer function finishes."
        )
    )

    val quizzes = listOf(
        // Python category
        QuizQuestion(
            id = "q_py_1",
            question = "Which of the following is NOT a valid variable name in Python?",
            options = listOf("my_variable", "_variable", "2variable", "variable2"),
            correctIndex = 2,
            explanation = "In Python, variable names cannot start with a number. They must start with a letter or an underscore.",
            category = "Python"
        ),
        QuizQuestion(
            id = "q_py_2",
            question = "What is the output of 'print(type([]))' in Python?",
            options = listOf("<class 'list'>", "<class 'tuple'>", "<class 'dict'>", "<class 'array'>"),
            correctIndex = 0,
            explanation = "Square brackets '[]' declare a list in Python, so its type is <class 'list'>.",
            category = "Python"
        ),
        QuizQuestion(
            id = "q_py_3",
            question = "How do you declare a function in Python?",
            options = listOf("function myFunc():", "def myFunc():", "void myFunc():", "declare myFunc():"),
            correctIndex = 1,
            explanation = "Python uses the 'def' keyword (short for define) to declare functions.",
            category = "Python"
        ),

        // JavaScript category
        QuizQuestion(
            id = "q_js_1",
            question = "Which keyword is used to declare a block-scoped constant in JavaScript?",
            options = listOf("var", "let", "const", "constant"),
            correctIndex = 2,
            explanation = "'const' is used to define block-scoped read-only constant variables that cannot be reassigned.",
            category = "JavaScript"
        ),
        QuizQuestion(
            id = "q_js_2",
            question = "What will the expression '3' + 2 evaluate to in JavaScript?",
            options = listOf("5", "6", "'32'", "TypeError"),
            correctIndex = 2,
            explanation = "Because of type coercion, JavaScript converts the number 2 to a string and performs concatenation, resulting in '32'.",
            category = "JavaScript"
        ),
        QuizQuestion(
            id = "q_js_3",
            question = "Which method is used to add an element to the end of an array in JavaScript?",
            options = listOf("push()", "append()", "add()", "pop()"),
            correctIndex = 0,
            explanation = "The push() method adds one or more elements to the end of an array and returns the new length.",
            category = "JavaScript"
        ),

        // Logic & Advanced category
        QuizQuestion(
            id = "q_logic_1",
            question = "In programming, what is a 'Closure'?",
            options = listOf(
                "A way to close a database stream securely",
                "A function that retains access to its outer lexical scope",
                "The termination statement of a loop block",
                "An encrypted variable accessible only to specific modules"
            ),
            correctIndex = 1,
            explanation = "A closure is a function that remembers and accesses variables from its outer scope, even after the outer function has returned.",
            category = "Logic"
        ),
        QuizQuestion(
            id = "q_logic_2",
            question = "Which data structure operates on a 'First In, First Out' (FIFO) basis?",
            options = listOf("Stack", "Tree", "Queue", "Hash Table"),
            correctIndex = 2,
            explanation = "A Queue uses First In First Out (FIFO) processing, whereas a Stack uses Last In First Out (LIFO).",
            category = "Logic"
        ),
        QuizQuestion(
            id = "q_logic_3",
            question = "What is the time complexity of searching in a sorted array using Binary Search?",
            options = listOf("O(N)", "O(log N)", "O(N^2)", "O(1)"),
            correctIndex = 1,
            explanation = "Binary search divides the search space in half with each step, yielding a logarithmic time complexity of O(log N).",
            category = "Logic"
        )
    )

    val challenges = listOf(
        CodingChallenge(
            id = "ch_even_odd",
            title = "Even or Odd Checker",
            description = "Write a function `is_even(num)` in Python that returns `True` if a number is even, and `False` if it is odd.",
            language = "python",
            startCode = "def is_even(num):\n    # Write your logic here\n    pass\n\n# Test cases\nprint(is_even(4))\nprint(is_even(7))",
            expectedKeywords = listOf("def is_even", "% 2", "return"),
            testDescription = "Returns True for 4 and False for 7"
        ),
        CodingChallenge(
            id = "ch_js_reverse",
            title = "Reverse a String",
            description = "Write a JavaScript function `reverseString(str)` that takes a string and returns it backwards.",
            language = "javascript",
            startCode = "function reverseString(str) {\n    // Write your code here\n    return str;\n}\n\nconsole.log(reverseString(\"hello\"));",
            expectedKeywords = listOf("function reverseString", "split", "reverse", "join"),
            testDescription = "Returns 'olleh' when given 'hello'"
        ),
        CodingChallenge(
            id = "ch_py_factorial",
            title = "Calculate Factorial",
            description = "Write a Python function `factorial(n)` that returns the factorial of n. For example, `factorial(5)` should return 120.",
            language = "python",
            startCode = "def factorial(n):\n    # Write your recursive or iterative logic here\n    pass\n\nprint(factorial(5))",
            expectedKeywords = listOf("def factorial", "*", "return"),
            testDescription = "Returns 120 when n is 5"
        )
    )
}
