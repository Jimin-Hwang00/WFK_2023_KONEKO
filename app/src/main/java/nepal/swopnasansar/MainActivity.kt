package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nepal.swopnasansar.youtube.TeacherCheckYoutbeListActivity
import nepal.swopnasansar.youtube.TeacherPostYoutubeLinkActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, TeacherPostYoutubeLinkActivity::class.java)
        startActivity(intent)
    }
}