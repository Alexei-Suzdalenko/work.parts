package work.parts.employee
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.employee_edit_part.*
import kotlinx.android.synthetic.main.employee_part.*
import kotlinx.android.synthetic.main.employee_part.data_time_show
import work.parts.R
import work.parts.utils.App
import work.parts.utils.Common
import work.parts.utils.models.Part
// employee edit current part
class EmployeeEditPart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employee_edit_part)

        val part_id    = intent.getStringExtra(Common.PART_ID).toString()
        title          = App.sharedPreferences.getString(Common.WORK_NAME, "").toString()
        val company_id = App.sharedPreferences.getString(Common.COMPANY_ID, "").toString()
        val work_id    = App.sharedPreferences.getString(Common.WORK_ID, "").toString()

        val db = Firebase.firestore
        db.collection("p").document(company_id).collection(work_id).document(part_id).get().addOnSuccessListener { document ->
                    if (document != null) {
                        val part: Part? = document.toObject(Part::class.java)
                        data_time_edit.setText(  part?.data_time )
                        time_hours_edit.setText( part?.working_time.toString())
                        work_done_edit.setText(part?.work_done)
                        comments_edit.setText( part?.comment )
                        kilometers_traveled_edit.setText(part?.km.toString())
                        consumption_edit.setText(part?.costs.toString())
                    } else {
                        Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
                }

        part_edit.setOnClickListener { savedEditedPart(company_id, work_id, part_id) }
    }

    private fun savedEditedPart(companyId: String, workId: String, partId: String) {
        val data_time: String = data_time_edit.text.toString()
        var time_hours = 0
        try{
            time_hours = Integer.parseInt( time_hours_edit.text.toString() )
        } catch (e: Exception){}
        val work = work_done_edit.text.toString()
        val comment = comments_edit.text.toString()
        var km = 0
        try{
            km = Integer.parseInt( kilometers_traveled_edit.text.toString() )
        } catch (e: Exception){}
        var consum = 0
        try{
            consum = Integer.parseInt( consumption_edit.text.toString() )
        } catch (e: Exception){}
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap[Common.DATA_TIME] = data_time
        hashMap[Common.WORKING_TIME] = time_hours
        hashMap[Common.WORK_DONE] = work
        hashMap[Common.COMMENT] = comment
        hashMap[Common.KM] = km
        hashMap[Common.COSTS] = consum

        val db = Firebase.firestore
        db.collection("p").document(companyId).collection(workId).document(partId).update(hashMap).addOnCompleteListener { task ->
            if( task.isSuccessful ){
                Toast.makeText(this, resources.getString(R.string.part_edited), Toast.LENGTH_LONG ).show()
                finish()
            } else Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
        }
    }
}