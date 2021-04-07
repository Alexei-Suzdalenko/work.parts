package work.parts.employee
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.employee_new_work_part.*
import work.parts.R
import work.parts.register_login.RegisterCompanyActivity
import work.parts.utils.App
import work.parts.utils.Common
import work.parts.utils.models.Part
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
// current employee create new work part for current work
class EmployeeCreateNewPart : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    var titleWork: String = ""
    var company_id: String = ""
    var work_id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employee_new_work_part)

        titleWork = intent.getStringExtra(Common.WORK_NAME).toString() + " " + resources.getString(R.string.new_part)
        title_create_part.text = titleWork
        titleWork = intent.getStringExtra(Common.WORK_NAME).toString()
        val currentDataTime= SimpleDateFormat("dd/MMMM/yyyy", Locale.getDefault())
        val time: String = currentDataTime.format(Date())
        data_time.setText(time)
        time_hours.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        work_done.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        comments.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        kilometers_traveled.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        consumption.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))

        company_id = intent.getStringExtra(Common.COMPANY_ID).toString()
        work_id = intent.getStringExtra(Common.WORK_ID).toString()
        val user_email = App.sharedPreferences.getString(Common.EMPLOYEE_EMAIL, null)
        val user_name = App.sharedPreferences.getString(Common.EMPLOYEE_NAME, null)

        if( company_id.isEmpty() || work_id.isEmpty() || user_email == null ||  user_name == null ) {
            startActivity(Intent(this, RegisterCompanyActivity::class.java)); finish()
        }

        db = Firebase.firestore

        create_part.setOnClickListener {
            saveNewPart(company_id, work_id, user_email!!, user_name!!)
        }
    }

    private fun saveNewPart(company_id: String, work_id: String, user_email: String, user_name: String) {
      val  x_data_time = data_time.text.toString()
      val  x_working_time = Integer.parseInt(time_hours.getText().toString())
      val  x_work_done = work_done.text.toString()
      val  x_comment = comments.text.toString()
      val  x_km = Integer.parseInt(kilometers_traveled.getText().toString())
      val  x_costs = Integer.parseInt(consumption.getText().toString())

        create_part.isEnabled = false
      val id = db.collection(System.currentTimeMillis().toString()).document().id
       db.collection(Common.PARTS).document(company_id).collection(work_id).document(id)
           .set(Part(company_id, work_id, id, user_email, user_name, x_data_time, x_working_time, x_work_done, x_comment, x_km, x_costs, System.currentTimeMillis().toString())
       ).addOnCompleteListener { task ->
           if( task.isSuccessful ){
               // go to list part this work
               Toast.makeText(applicationContext, resources.getString(R.string.new_past_saved), Toast.LENGTH_LONG).show()
                   val intent = Intent(applicationContext, EmployeeListsWorkParts::class.java)
                   App.editor.putString(Common.WORK_NAME, titleWork)
                   App.editor.putString(Common.COMPANY_ID, company_id)
                   App.editor.putString(Common.WORK_ID, work_id)
                   App.editor.apply()
                   intent.putExtra(Common.WORK_NAME, titleWork)
                   intent.putExtra(Common.COMPANY_ID, company_id)
                   intent.putExtra(Common.WORK_ID, work_id)
                   startActivity(intent); finish()
           } else {
               create_part.isEnabled = true
               Toast.makeText(applicationContext, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
           }
       }

    }
}