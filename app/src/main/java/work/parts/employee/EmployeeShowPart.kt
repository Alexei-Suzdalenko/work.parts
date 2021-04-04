package work.parts.employee
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.employee_part.*
import work.parts.R
import work.parts.utils.App
import work.parts.utils.Common
import work.parts.utils.models.Part

// employee show current part
class EmployeeShowPart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employee_part)

        val part_id = intent.getStringExtra(Common.PART_ID).toString()
        val work_name      = App.sharedPreferences.getString(Common.WORK_NAME, "").toString()
            title          = App.sharedPreferences.getString(Common.WORK_NAME, "").toString()
        val company_id = App.sharedPreferences.getString(Common.COMPANY_ID, "").toString()
        val work_id    = App.sharedPreferences.getString(Common.WORK_ID, "").toString()

        val db = Firebase.firestore
        db.collection("p").document(company_id).collection(work_id).document(part_id).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val part: Part? = document.toObject(Part::class.java)
                    data_time_show.text = part?.data_time
                    time_hours_show.text = part?.working_time.toString()
                    work_done_show.text = part?.work_done
                    comments_show.text = part?.comment
                    kilometers_traveled_show.text = part?.km.toString()
                    consumption_show.text = part?.costs.toString()
                } else {
                    Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
            }
    }
}