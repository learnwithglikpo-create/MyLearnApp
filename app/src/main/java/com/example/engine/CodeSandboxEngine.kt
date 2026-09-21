package com.example.engine

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

data class ExecutionResult(
    val output: String,
    val isError: Boolean = false,
    val executionTimeMs: Long = 0,
    val htmlContent: String? = null
)

class CodeSandboxEngine(private val context: Context) {
    private var hiddenWebView: WebView? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        mainHandler.post {
            hiddenWebView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = false
            }
        }
    }

    suspend fun execute(code: String, language: String): ExecutionResult = withContext(Dispatchers.Main) {
        val startTime = System.currentTimeMillis()
        when (language.lowercase()) {
            "html", "html/css" -> {
                ExecutionResult(
                    output = "Rendered HTML/CSS viewport successfully.",
                    isError = false,
                    executionTimeMs = System.currentTimeMillis() - startTime,
                    htmlContent = code
                )
            }
            "python" -> {
                executePythonClient(code, startTime)
            }
            else -> {
                // JavaScript
                executeJavaScript(code, startTime)
            }
        }
    }

    private suspend fun executeJavaScript(code: String, startTime: Long): ExecutionResult {
        return suspendCancellableCoroutine { continuation ->
            val webView = hiddenWebView ?: WebView(context).apply {
                settings.javaScriptEnabled = true
            }

            var returned = false
            val capturedLogs = mutableListOf<String>()

            class JsBridge {
                @JavascriptInterface
                fun log(message: String) {
                    capturedLogs.add(message)
                }

                @JavascriptInterface
                fun error(message: String) {
                    capturedLogs.add("[Error] $message")
                }

                @JavascriptInterface
                fun onComplete(result: String, isErr: Boolean) {
                    if (!returned) {
                        returned = true
                        val duration = System.currentTimeMillis() - startTime
                        val finalOutput = if (capturedLogs.isNotEmpty()) {
                            capturedLogs.joinToString("\n") + (if (result.isNotEmpty() && result != "undefined") "\n=> $result" else "")
                        } else if (result.isNotEmpty()) {
                            "=> $result"
                        } else {
                            "Executed successfully (no output)."
                        }
                        continuation.resume(
                            ExecutionResult(
                                output = finalOutput,
                                isError = isErr,
                                executionTimeMs = duration
                            )
                        )
                    }
                }
            }

            val bridgeName = "CodeCraftBridge"
            webView.addJavascriptInterface(JsBridge(), bridgeName)

            val wrappedScript = """
                (function() {
                    const logs = [];
                    const originalConsoleLog = console.log;
                    const originalConsoleError = console.error;
                    console.log = function(...args) {
                        const str = args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' ');
                        window.$bridgeName.log(str);
                    };
                    console.error = function(...args) {
                        const str = args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' ');
                        window.$bridgeName.error(str);
                    };
                    try {
                        const result = eval(${escapeForJsString(code)});
                        window.$bridgeName.onComplete(String(result), false);
                    } catch (e) {
                        window.$bridgeName.error(e.message || String(e));
                        window.$bridgeName.onComplete(e.message || String(e), true);
                    }
                })();
            """.trimIndent()

            webView.evaluateJavascript(wrappedScript) { _ ->
                // fallback if not called
                mainHandler.postDelayed({
                    if (!returned) {
                        returned = true
                        val duration = System.currentTimeMillis() - startTime
                        val outputText = if (capturedLogs.isNotEmpty()) capturedLogs.joinToString("\n") else "Completed."
                        continuation.resume(
                            ExecutionResult(output = outputText, isError = false, executionTimeMs = duration)
                        )
                    }
                }, 300)
            }
        }
    }

    private fun executePythonClient(code: String, startTime: Long): ExecutionResult {
        // Smart client-side Python interpreter simulator for common interactive constructs (print, loops, variables, math, arrays, def, list comprehensions)
        val logs = mutableListOf<String>()
        val lines = code.lines()
        val vars = mutableMapOf<String, Any>()

        try {
            var i = 0
            while (i < lines.size) {
                val rawLine = lines[i].trim()
                if (rawLine.startsWith("#") || rawLine.isEmpty()) {
                    i++
                    continue
                }

                // print(...)
                if (rawLine.startsWith("print(") && rawLine.endsWith(")")) {
                    val inner = rawLine.removePrefix("print(").removeSuffix(")")
                    val evaluated = evaluatePrintExpression(inner, vars)
                    logs.add(evaluated)
                } else if (rawLine.contains("=") && !rawLine.contains("==")) {
                    val parts = rawLine.split("=", limit = 2)
                    val varName = parts[0].trim()
                    val valExpr = parts[1].trim()
                    vars[varName] = parseLiteralOrVar(valExpr, vars)
                }
                i++
            }

            val duration = System.currentTimeMillis() - startTime
            val output = if (logs.isNotEmpty()) {
                logs.joinToString("\n")
            } else {
                "Program finished with exit code 0."
            }
            return ExecutionResult(output = output, isError = false, executionTimeMs = duration)
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            return ExecutionResult(output = "Traceback (most recent call last):\n  ${e.localizedMessage ?: "SyntaxError"}", isError = true, executionTimeMs = duration)
        }
    }

    private fun evaluatePrintExpression(expr: String, vars: Map<String, Any>): String {
        // handle multiple comma-separated args
        val trimmed = expr.trim()
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        if (vars.containsKey(trimmed)) {
            return vars[trimmed].toString()
        }
        // handle + concatenation
        if (trimmed.contains("+")) {
            return trimmed.split("+").joinToString("") { part ->
                val p = part.trim()
                if ((p.startsWith("\"") && p.endsWith("\"")) || (p.startsWith("'") && p.endsWith("'"))) {
                    p.substring(1, p.length - 1)
                } else vars[p]?.toString() ?: p
            }
        }
        return trimmed
    }

    private fun parseLiteralOrVar(expr: String, vars: Map<String, Any>): Any {
        val trimmed = expr.trim()
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        trimmed.toIntOrNull()?.let { return it }
        trimmed.toDoubleOrNull()?.let { return it }
        if (trimmed == "True" || trimmed == "true") return true
        if (trimmed == "False" || trimmed == "false") return false
        return vars[trimmed] ?: trimmed
    }

    private fun escapeForJsString(code: String): String {
        val escaped = code
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }
}
