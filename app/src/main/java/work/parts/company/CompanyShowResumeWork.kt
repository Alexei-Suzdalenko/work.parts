package work.parts.company
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.company_show_resume_work.*
import work.parts.R
import work.parts.utils.Common
import work.parts.utils.models.Part
class CompanyShowResumeWork : AppCompatActivity() {
    var numberParts: Int = 0
    var numberHours: Int = 0
    var numberKm: Int = 0
    var allExpenses: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.company_show_resume_work)

        title = intent.getStringExtra(Common.WORK_NAME)
        val companyId = intent.getStringExtra(Common.COMPANY_ID).toString()
        val workId = intent.getStringExtra(Common.WORK_ID).toString()

        // .orderBy("ml")
        Firebase.firestore.collection(Common.PARTS).document(companyId).collection(workId).get().addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val part: Part = document.toObject<Part>()
                        numberParts++
                        numberHours += part.working_time
                        numberKm += part.km
                        allExpenses += part.costs
                    }
                    id_parts_show.text = numberParts.toString()
                    total_hours.text = numberHours.toString()
                    total_km.text = numberKm.toString()
                    all_expenses.text = allExpenses.toString()

                    progressCompResum.visibility = View.GONE
                } else {
                    Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show(); finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show(); finish()
            }
    }

}