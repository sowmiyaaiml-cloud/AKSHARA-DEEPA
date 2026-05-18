package com.aksharadeepa.tutor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChapterAdapter
    private lateinit var overallProgress: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var dailyGoalText: TextView
    private lateinit var quizBtn: Button
    private lateinit var strengthBtn: Button
    private lateinit var prefs: SharedPreferences
    private val chapterStatus = mutableMapOf<String, Boolean>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = getSharedPreferences("AksharaDeepa", Context.MODE_PRIVATE)
        
        initViews()
        setupSyllabus()
        updateProgress()
        setupDailyGoal()
        setupClickListeners()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        overallProgress = findViewById(R.id.overallProgress)
        progressText = findViewById(R.id.progressText)
        dailyGoalText = findViewById(R.id.dailyGoalText)
        quizBtn = findViewById(R.id.quizBtn)
        strengthBtn = findViewById(R.id.strengthBtn)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupClickListeners() {
        quizBtn.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }
        
        strengthBtn.setOnClickListener {
            startActivity(Intent(this, StrengthMapActivity::class.java))
        }
    }
    
    private fun setupSyllabus() {
        val chapters = mutableListOf<ChapterItem>()
        
        // Science Chapters
        chapters.add(ChapterItem("Science", "Chemical Reactions", false))
        chapters.add(ChapterItem("Science", "Acids and Bases", false))
        chapters.add(ChapterItem("Science", "Metals & Non-metals", false))
        chapters.add(ChapterItem("Science", "Carbon Compounds", false))
        chapters.add(ChapterItem("Science", "Life Processes", false))
        
        // Math Chapters
        chapters.add(ChapterItem("Math", "Real Numbers", false))
        chapters.add(ChapterItem("Math", "Polynomials", false))
        chapters.add(ChapterItem("Math", "Linear Equations", false))
        chapters.add(ChapterItem("Math", "Quadratic Equations", false))
        chapters.add(ChapterItem("Math", "Triangles", false))
        
        // Social Studies Chapters
        chapters.add(ChapterItem("Social", "The Rise of Nationalism", false))
        chapters.add(ChapterItem("Social", "Resources & Development", false))
        chapters.add(ChapterItem("Social", "Agriculture", false))
        chapters.add(ChapterItem("Social", "Political Parties", false))
        chapters.add(ChapterItem("Social", "Globalization", false))
        
        // Load saved progress
        chapters.forEach { chapter ->
            chapter.isCompleted = prefs.getBoolean(chapter.chapterName, false)
            chapterStatus[chapter.chapterName] = chapter.isCompleted
        }
        
        adapter = ChapterAdapter(chapters) { chapterName, isChecked ->
            prefs.edit().putBoolean(chapterName, isChecked).apply()
            chapterStatus[chapterName] = isChecked
            updateProgress()
            checkDailyGoal()
        }
        
        recyclerView.adapter = adapter
    }
    
    private fun updateProgress() {
        val total = chapterStatus.size
        val completed = chapterStatus.values.count { it }
        val progress = if (total > 0) (completed * 100) / total else 0
        overallProgress.progress = progress
        progressText.text = "Overall Progress: $progress%"
    }
    
    private fun setupDailyGoal() {
        val lastDate = prefs.getString("lastGoalDate", "")
        val today = LocalDate.now().toString()
        
        if (lastDate != today) {
            prefs.edit().putInt("todayTopics", 0).apply()
            prefs.edit().putString("lastGoalDate", today).apply()
        }
        
        val todayTopics = prefs.getInt("todayTopics", 0)
        dailyGoalText.text = "📅 Today's Goal: $todayTopics/1 topics completed"
    }
    
    private fun checkDailyGoal() {
        var todayTopics = prefs.getInt("todayTopics", 0)
        val lastDate = prefs.getString("lastGoalDate", "")
        val today = LocalDate.now().toString()
        
        if (lastDate != today) {
            todayTopics = 0
        }
        
        if (todayTopics >= 1) {
            dailyGoalText.text = "✅ Goal Achieved! Great job!"
        } else {
            dailyGoalText.text = "📅 Today's Goal: $todayTopics/1 topics completed"
        }
    }
    
    override fun onResume() {
        super.onResume()
        setupSyllabus()
        updateProgress()
    }
}

data class ChapterItem(
    val subject: String,
    val chapterName: String,
    var isCompleted: Boolean
)