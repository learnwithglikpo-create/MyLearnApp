package com.example.data.local

import com.example.data.model.*

object InitialData {
    val defaultUser = UserEntity(
        id = "user_default",
        name = "Alex Rivera",
        username = "alex_coder",
        email = "alex@codecraft.io",
        xp = 420,
        dailyXpGoal = 50,
        todayXp = 35,
        streakDays = 5,
        hearts = 5,
        maxHearts = 5,
        selectedGoal = "Build Websites",
        selectedLanguage = "Python",
        experienceLevel = "Some experience",
        onboardingCompleted = true,
        darkMode = true
    )

    val defaultCourses = listOf(
        CourseEntity(
            id = "course_python_101",
            title = "Python Fundamentals",
            category = "Programming Basics",
            language = "Python",
            totalLessons = 5,
            completedLessons = 2,
            description = "Master core Python syntax, variables, conditionals, loops, and list comprehensions.",
            pathTitle = "Python Specialist",
            isPremium = false,
            orderIndex = 1
        ),
        CourseEntity(
            id = "course_js_101",
            title = "JavaScript Essentials",
            category = "Web Development",
            language = "JavaScript",
            totalLessons = 4,
            completedLessons = 1,
            description = "Learn modern ES6+, arrow functions, array methods, and asynchronous concepts.",
            pathTitle = "Full-Stack Developer",
            isPremium = false,
            orderIndex = 2
        ),
        CourseEntity(
            id = "course_web_html",
            title = "Modern HTML & CSS",
            category = "Web Development",
            language = "HTML/CSS",
            totalLessons = 6,
            completedLessons = 0,
            description = "Construct responsive, accessible web layouts with Flexbox and Grid.",
            pathTitle = "Full-Stack Developer",
            isPremium = false,
            orderIndex = 3
        ),
        CourseEntity(
            id = "course_sql_data",
            title = "SQL & Data Queries",
            category = "Data Science",
            language = "SQL",
            totalLessons = 4,
            completedLessons = 0,
            description = "Query relational databases with SELECT, JOIN, GROUP BY, and aggregates.",
            pathTitle = "Data Analyst",
            isPremium = true,
            orderIndex = 4
        )
    )

    val defaultLessons = listOf(
        // Python 101 - Lesson 1: Print & Variables (Multiple Choice)
        LessonEntity(
            id = "py_lesson_1",
            courseId = "course_python_101",
            title = "Variables & Displaying Output",
            stepIndex = 1,
            totalSteps = 4,
            xpReward = 20,
            conceptTitle = "Printing with Python",
            conceptExplanation = "In Python, the print() function sends data to the standard output console. Text strings must be surrounded by quotation marks.",
            codeSnippet = "user_name = \"Alex\"\nprint(\"Hello, \" + user_name)",
            interactionType = InteractionType.MULTIPLE_CHOICE,
            questionPrompt = "Which function displays formatted text on the screen in Python?",
            optionsJson = "echo()|print()|display()|console.log()",
            correctAnswer = "print()",
            aiHint = "Think of sending words onto paper — Python uses 'print()' to output values to your console!",
            isCompleted = true
        ),
        // Python 101 - Lesson 2: Fill in the blank
        LessonEntity(
            id = "py_lesson_2",
            courseId = "course_python_101",
            title = "String Concatenation",
            stepIndex = 2,
            totalSteps = 4,
            xpReward = 25,
            conceptTitle = "Joining Words",
            conceptExplanation = "You can combine strings using the + operator or by passing multiple arguments separated by commas to print().",
            codeSnippet = "greeting = \"Code\"\nsubject = \"Craft\"\nfull_title = greeting + subject",
            interactionType = InteractionType.FILL_IN_THE_BLANK,
            questionPrompt = "Complete the code to print 'CodeCraft' using the variable:",
            optionsJson = "print|echo|full_title|title",
            correctAnswer = "print(full_title)",
            blankSnippet = "___(full_title)",
            aiHint = "The blank is right before the parentheses. You need the standard Python output function 'print'.",
            isCompleted = true
        ),
        // Python 101 - Lesson 3: Predict Output
        LessonEntity(
            id = "py_lesson_3",
            courseId = "course_python_101",
            title = "Conditional Logic",
            stepIndex = 3,
            totalSteps = 4,
            xpReward = 30,
            conceptTitle = "Making Decisions with If-Else",
            conceptExplanation = "Python evaluates conditions with if, elif, and else. Indentation defines the code block executed when the condition evaluates to True.",
            codeSnippet = "score = 85\nif score >= 80:\n    result = \"Passed\"\nelse:\n    result = \"Review\"\nprint(result)",
            interactionType = InteractionType.PREDICT_OUTPUT,
            questionPrompt = "What will this code print to the terminal?",
            optionsJson = "Passed|Review|85|SyntaxError",
            correctAnswer = "Passed",
            aiHint = "Since 85 is greater than or equal to 80, the first block executes and sets result to 'Passed'!",
            isCompleted = false
        ),
        // Python 101 - Lesson 4: Reorder Lines
        LessonEntity(
            id = "py_lesson_4",
            courseId = "course_python_101",
            title = "Looping with Ranges",
            stepIndex = 4,
            totalSteps = 4,
            xpReward = 35,
            conceptTitle = "For Loops",
            conceptExplanation = "For loops iterate over a sequence of numbers generated by range(start, stop). Arrange the lines in logical execution order.",
            codeSnippet = "# Target sequence:\ntotal = 0\nfor i in range(1, 4):\n    total += i\nprint(total)",
            interactionType = InteractionType.REORDER_LINES,
            questionPrompt = "Arrange these lines to correctly compute the sum of 1, 2, and 3:",
            optionsJson = "total = 0|for i in range(1, 4):|    total += i|print(total)",
            correctAnswer = "total = 0|for i in range(1, 4):|    total += i|print(total)",
            aiHint = "First initialize total = 0, then start the loop with 'for', indent the accumulation 'total += i', and finish by printing.",
            isCompleted = false
        ),
        // JavaScript 101 - Lesson 1: Modern JS Array Methods
        LessonEntity(
            id = "js_lesson_1",
            courseId = "course_js_101",
            title = "Array Transformation with .map()",
            stepIndex = 1,
            totalSteps = 3,
            xpReward = 25,
            conceptTitle = "Mapping Values",
            conceptExplanation = "The .map() method creates a new array populated with the results of calling a provided callback function on every element in the calling array.",
            codeSnippet = "const numbers = [1, 2, 3];\nconst doubled = numbers.map(n => n * 2);\nconsole.log(doubled);",
            interactionType = InteractionType.PREDICT_OUTPUT,
            questionPrompt = "What is the logged output of doubled?",
            optionsJson = "[2, 4, 6]|[1, 2, 3]|6|undefined",
            correctAnswer = "[2, 4, 6]",
            aiHint = "Each number (1, 2, 3) is multiplied by 2, yielding [2, 4, 6].",
            isCompleted = true
        ),
        // JavaScript 101 - Lesson 2: Free Type Code
        LessonEntity(
            id = "js_lesson_2",
            courseId = "course_js_101",
            title = "Arrow Functions",
            stepIndex = 2,
            totalSteps = 3,
            xpReward = 30,
            conceptTitle = "Concise Functions",
            conceptExplanation = "Arrow functions provide a clean shorthand. Complete the function returning a greeting message.",
            codeSnippet = "const greet = (name) => {\n  return `Hello, \${'$'}{name}!`;\n};",
            interactionType = InteractionType.FREE_TYPE_CODE,
            questionPrompt = "Define an arrow function 'square' that takes 'x' and returns 'x * x':",
            optionsJson = "",
            correctAnswer = "const square = (x) => x * x;",
            blankSnippet = "const square = (x) => ___",
            aiHint = "Return the multiplication expression: 'x * x' or '{ return x * x; }'.",
            isCompleted = false
        )
    )

    val defaultSnippets = listOf(
        SnippetEntity(
            id = "snip_1",
            title = "Fibonacci Generator",
            language = "javascript",
            code = "// JavaScript Generator\nfunction* fibonacci(limit) {\n  let [prev, curr] = [0, 1];\n  while (limit-- > 0) {\n    yield curr;\n    [prev, curr] = [curr, prev + curr];\n  }\n}\n\nconst seq = [...fibonacci(8)];\nconsole.log(\"Fibonacci:\", seq);",
            isFavorite = true
        ),
        SnippetEntity(
            id = "snip_2",
            title = "Quick Sort Algorithm",
            language = "python",
            code = "# Python Recursive QuickSort\ndef quicksort(arr):\n    if len(arr) <= 1:\n        return arr\n    pivot = arr[len(arr) // 2]\n    left = [x for x in arr if x < pivot]\n    mid = [x for x in arr if x == pivot]\n    right = [x for x in arr if x > pivot]\n    return quicksort(left) + mid + quicksort(right)\n\nprint(\"Sorted:\", quicksort([38, 27, 43, 3, 9, 82, 10]))",
            isFavorite = false
        ),
        SnippetEntity(
            id = "snip_3",
            title = "Neon Glow Button",
            language = "html",
            code = "<!DOCTYPE html>\n<html>\n<head>\n<style>\n  body { background: #12141C; display: flex; justify-content: center; align-items: center; height: 100vh; }\n  .glow-btn { padding: 14px 28px; background: #3B2FD4; color: #00E5C7; border: 2px solid #00E5C7; border-radius: 28px; font-weight: bold; cursor: pointer; box-shadow: 0 0 16px rgba(0,229,199,0.4); }\n</style>\n</head>\n<body>\n  <button class=\"glow-btn\">CodeCraft Active</button>\n</body>\n</html>",
            isFavorite = true
        )
    )

    val defaultPosts = listOf(
        CommunityPostEntity(
            id = "post_1",
            authorName = "Maya Lin",
            authorHandle = "@mayacodes",
            type = "Snippet",
            content = "Built a super clean custom filter function for arrays in JS! Check out how simple this looks with reduce().",
            codeSnippet = "const customFilter = (arr, fn) =>\n  arr.reduce((acc, item) => fn(item) ? [...acc, item] : acc, []);\n\nconsole.log(customFilter([1, 2, 3, 4, 5], n => n % 2 === 0));",
            language = "javascript",
            tags = "#javascript, #functional, #clean-code",
            likeCount = 24,
            commentCount = 5,
            isLiked = false
        ),
        CommunityPostEntity(
            id = "post_2",
            authorName = "Devon Vance",
            authorHandle = "@devon_v",
            type = "Question",
            content = "Hey everyone! When should I prefer list comprehensions over map() in Python? Does one have better performance in CPython 3.12?",
            codeSnippet = "# Option A:\nres = [x * 2 for x in data]\n\n# Option B:\nres = list(map(lambda x: x * 2, data))",
            language = "python",
            tags = "#python, #performance, #help",
            likeCount = 18,
            commentCount = 12,
            isLiked = true
        ),
        CommunityPostEntity(
            id = "post_3",
            authorName = "Sofia Zhang",
            authorHandle = "@sofia_z",
            type = "Achievement",
            content = "🎉 Just unlocked my 7-Day Streak & finished Python Fundamentals on CodeCraft! Loving the byte-sized lessons on my commute.",
            tags = "#streak, #achievement, #python",
            likeCount = 42,
            commentCount = 8,
            isLiked = false
        )
    )

    val defaultBadges = listOf(
        BadgeEntity(
            id = "badge_first_snippet",
            name = "First Snippet",
            description = "Wrote and ran your first working code snippet in Playground.",
            iconType = "code",
            isUnlocked = true,
            unlockedAt = "Yesterday"
        ),
        BadgeEntity(
            id = "badge_streak_5",
            name = "5-Day Streak",
            description = "Practiced coding 5 days in a row without missing.",
            iconType = "flame",
            isUnlocked = true,
            unlockedAt = "Today"
        ),
        BadgeEntity(
            id = "badge_bug_hunter",
            name = "Bug Hunter",
            description = "Solved 3 Bug Hunt mini-games with 100% accuracy.",
            iconType = "bug",
            isUnlocked = true,
            unlockedAt = "2 days ago"
        ),
        BadgeEntity(
            id = "badge_speed_coder",
            name = "Speed Demon",
            description = "Completed a daily code challenge in under 45 seconds.",
            iconType = "spark",
            isUnlocked = false
        ),
        BadgeEntity(
            id = "badge_polyglot",
            name = "Polyglot",
            description = "Complete at least one lesson in 3 different languages.",
            iconType = "shield",
            isUnlocked = false
        ),
        BadgeEntity(
            id = "badge_master",
            name = "Code Master",
            description = "Earn over 1,000 XP and complete 1 career path.",
            iconType = "trophy",
            isUnlocked = false
        )
    )
}
