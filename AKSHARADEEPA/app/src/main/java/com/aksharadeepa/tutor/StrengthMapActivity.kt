package com.aksharadeepa.tutor

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class StrengthMapActivity : AppCompatActivity() {
    
    private lateinit var radarChart: RadarChart
    private lateinit var strengthMessage: TextView
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strength)
        
        radarChart = findViewById(R.id.radarChart)
        strengthMessage = findViewById(R.id.strengthMessage)
        prefs = getSharedPreferences("AksharaDeepa", Context.MODE_PRIVATE)
        
        setupRadarChart()
    }
    
    private fun setupRadarChart() {
        val values = ArrayList<RadarEntry>()
        val subjects = arrayOf("Science", "Math", "Social")
        
        // Calculate subject-wise progress
        val scienceScore = calculateSubjectProgress("Science")
        val mathScore = calculateSubjectProgress("Math")
        val socialScore = calculateSubjectProgress("Social")
        
        values.add(RadarEntry(scienceScore.toFloat()))
        values.add(RadarEntry(mathScore.toFloat()))
        values.add(RadarEntry(socialScore.toFloat()))
        
        val dataSet = RadarDataSet(values, "Subject Mastery")
        dataSet.setColor(Color.rgb(0, 150, 136))
        dataSet.setFillColor(Color.rgb(0, 150, 136))
        dataSet.fillAlpha = 100
        dataSet.lineWidth = 2f
        
        val data = RadarData(dataSet)
        radarChart.data = data
        
        // Labels
        val labels = subjects.toList()
        radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        
        radarChart.description.isEnabled = false
        radarChart.animateXY(1000, 1000)
        
        // Generate strength message
        val avg = (scienceScore + mathScore + socialScore) / 3
        strengthMessage.text = when {
            avg >= 70 -> "🌟 Strong Foundation! Keep up the momentum!"
            avg >= 40 -> "📈 Growing! Focus on weaker subjects."
            else -> "💪 Gap Areas Detected! Review Science & Social daily."
        }
    }
    
    private fun calculateSubjectProgress(subject: String): Int {
        val chapters = when (subject) {
            "Science" -> listOf("Chemical Reactions", "Acids and Bases", "Metals & Non-metals", "Carbon Compounds", "Life Processes")
            "Math" -> listOf("Real Numbers", "Polynomials", "Linear Equations", "Quadratic Equations", "Triangles")
            else -> listOf("The Rise of Nationalism", "Resources & Development", "Agriculture", "Political Parties", "Globalization")
        }
        
        val completed = chapters.count { chapterName ->
            prefs.getBoolean(chapterName, false)
        }
        
        return (completed * 100) / chapters.size
    }
}