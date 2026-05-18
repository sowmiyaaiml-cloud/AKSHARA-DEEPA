package com.aksharadeepa.tutor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {
    
    private lateinit var questionText: TextView
    private lateinit var timerText: TextView
    private lateinit var scoreText: TextView
    private lateinit var reviewText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var option1: RadioButton
    private lateinit var option2: RadioButton
    private lateinit var option3: RadioButton
    private lateinit var option4: RadioButton
    private lateinit var submitBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var quizProgress: ProgressBar
    
    private lateinit var questions: MutableList<QuizQuestion>
    private var currentIndex = 0
    private var score = 0
    private var timer: CountDownTimer? = null
    private lateinit var prefs: SharedPreferences
    private val userAnswers = mutableListOf<Int>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        
        initViews()
        loadMockQuestions()
        setupQuiz()
        
        prefs = getSharedPreferences("AksharaDeepa", Context.MODE_PRIVATE)
    }
    
    private fun initViews() {
        questionText = findViewById(R.id.questionText)
        timerText = findViewById(R.id.timerText)
        scoreText = findViewById(R.id.scoreText)
        reviewText = findViewById(R.id.reviewText)
        optionsGroup = findViewById(R.id.optionsGroup)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        submitBtn = findViewById(R.id.submitBtn)
        nextBtn = findViewById(R.id.nextBtn)
        quizProgress = findViewById(R.id.quizProgress)
    }
    
    private fun loadMockQuestions() {
        questions = mutableListOf(
            QuizQuestion("What is the chemical formula of Water?", "H2O", "CO2", "NaCl", "O2", "H2O"),
            QuizQuestion("What is the square root of 144?", "12", "14", "16", "18", "12"),
            QuizQuestion("Who wrote the Indian National Anthem?", "Rabindranath Tagore", "Bankim Chandra", "Sarojini Naidu", "Mahatma Gandhi", "Rabindranath Tagore"),
            QuizQuestion("Which is the largest planet?", "Jupiter", "Saturn", "Mars", "Earth", "Jupiter"),
            QuizQuestion("What is 15 + 27?", "42", "35", "40", "38", "42")
        )
    }
    
    private fun setupQuiz() {
        currentIndex = 0
        score = 0
        userAnswers.clear()
        loadQuestion()
    }
    
    private fun loadQuestion() {
        if (currentIndex < questions.size) {
            val q = questions[currentIndex]
            questionText.text = "${currentIndex + 1}. ${q.question}"
            option1.text = q.option1
            option2.text = q.option2
            option3.text = q.option3
            option4.text = q.option4
            optionsGroup.clearCheck()
            submitBtn.isEnabled = true
            nextBtn.isEnabled = false
            reviewText.text = ""
            quizProgress.progress = (currentIndex * 100) / questions.size
            startTimer()
        } else {
            finishQuiz()
        }
    }
    
    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "⏱️ ${millisUntilFinished / 1000}s"
            }
            override fun onFinish() {
                timerText.text = "Time's up!"
                submitBtn.performClick()
            }
        }.start()
    }
    
    fun onSubmitClick(view: View) {
        val selectedId = optionsGroup.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer!", Toast.LENGTH_SHORT).show()
            return
        }
        
        val selected = findViewById<RadioButton>(selectedId)
        val answer = selected.text.toString()
        val q = questions[currentIndex]
        
        val isCorrect = answer == q.correctAnswer
        if (isCorrect) {
            score++
            reviewText.text = "✅ Correct!"
        } else {
            reviewText.text = "❌ Wrong! Correct: ${q.correctAnswer}"
        }
        
        userAnswers.add(if (isCorrect) 1 else 0)
        scoreText.text = "Score: $score"
        submitBtn.isEnabled = false
        nextBtn.isEnabled = true
        
        timer?.cancel()
    }
    
    fun onNextClick(view: View) {
        currentIndex++
        loadQuestion()
    }
    
    private fun finishQuiz() {
        val percentage = (score * 100) / questions.size
        
        // Update strength
        val quizCount = prefs.getInt("quizCount", 0)
        val totalScore = prefs.getInt("totalScore", 0)
        prefs.edit().putInt("quizCount", quizCount + 1).apply()
        prefs.edit().putInt("totalScore", totalScore + percentage).apply()
        
        val message = buildString {
            append("Quiz Complete!\nScore: $score/${questions.size} ($percentage%)\n\n")
            append(when {
                percentage >= 70 -> "🎉 Excellent! Keep going!"
                percentage >= 40 -> "📚 Good! Review the wrong answers."
                else -> "💪 Need practice! Try again."
            })
        }
        
        AlertDialog.Builder(this)
            .setTitle("Quiz Result")
            .setMessage(message)
            .setPositiveButton("Done") { _, _ -> finish() }
            .show()
    }
    
    data class QuizQuestion(
        val question: String,
        val option1: String,
        val option2: String,
        val option3: String,
        val option4: String,
        val correctAnswer: String
    )
}