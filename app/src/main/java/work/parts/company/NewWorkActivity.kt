package work.parts.company
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_new_work.*
import work.parts.R
import work.parts.utils.App.Companion.auth
import work.parts.utils.App.Companion.workReference
import work.parts.utils.models.Work
import java.text.SimpleDateFormat
import java.util.*

class NewWorkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_work)

        job_name.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        job_direction.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        auth = FirebaseAuth.getInstance()
        create_job.setOnClickListener { createNewJob() }
    }

    private fun createNewJob() {
        val title = job_name.text.toString().trim()
        val direction = job_direction.text.toString().trim()
        if(title.length < 5 || direction.length < 5) Toast.makeText(this, resources.getString(R.string.enter_data), Toast.LENGTH_LONG).show()
        else {
            val currentDataTime= SimpleDateFormat("dd/MMMM/yyyy", Locale.getDefault())
            val time: String = currentDataTime.format(Date())

            val key = workReference.child(auth.currentUser!!.uid).push().key.toString()
            val work = Work(auth.currentUser!!.uid, key, title, direction, time)

            workReference.child(auth.currentUser!!.uid).child(key).setValue(work).addOnCompleteListener{ task ->
                if( task.isSuccessful ){
                    Toast.makeText(applicationContext, resources.getString(R.string.job_create), Toast.LENGTH_LONG).show()
                    // startActivity(Intent(applicationContext, CompanyIndexActivity::class.java));
                    finish()
                } else Toast.makeText(applicationContext, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
            }
        }
    }
}