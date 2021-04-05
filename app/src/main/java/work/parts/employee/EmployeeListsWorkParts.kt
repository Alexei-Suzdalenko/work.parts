package work.parts.employee
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_employee_index.*
import kotlinx.android.synthetic.main.activity_list_work_parts.*
import work.parts.R
import work.parts.utils.*
import work.parts.utils.models.Part

class EmployeeListsWorkParts : AppCompatActivity() {
    // employee show me all list part to current work
    lateinit var listWorkViewModel: EmplListWorkViewModel
    private lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_work_parts)

        title          = App.sharedPreferences.getString(Common.WORK_NAME, "").toString()
        val user_email = App.sharedPreferences.getString(Common.EMPLOYEE_EMAIL, null)
        if(user_email == null){
            Toast.makeText(applicationContext, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
            finish()
        }

        listWorkViewModel = ViewModelProvider(this).get(EmplListWorkViewModel::class.java)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adViewEmployeeListWork)
        val adRequest = AdRequest.Builder().build()
        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                var size = 66
                val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                if( mAdView.height > 15 ) size = mAdView.height
                params.setMargins(22, 0, 22, size)
                employeeListPart.layoutParams = params
            }
        }
        mAdView.loadAd(adRequest)
    }


    override fun onResume() {
        super.onResume()
        val company_id = App.sharedPreferences.getString(Common.COMPANY_ID, "").toString()
        val work_id    = App.sharedPreferences.getString(Common.WORK_ID, "").toString()
        val user_email = App.sharedPreferences.getString(Common.EMPLOYEE_EMAIL, null)

        listWorkViewModel.getListWorkCompanyWorkId(company_id, work_id, this).observeForever { listPart ->
            if (listPart.size == 0 ){ imageEmployeeListPart.visibility = View.VISIBLE }
            else { imageEmployeeListPart.visibility = View.GONE }
            progress_list_work_parts.visibility = View.GONE
            employeeListPart.adapter = EmployeeListPartAdapter(this, listPart)
            employeeListPart.setOnItemClickListener { _, _, i, _ ->
                showOptionsFromCurrentPart(listPart[i], user_email, company_id, work_id)
            }
        }
    }


    private fun showOptionsFromCurrentPart(part: Part, userEmail: String?, company_id: String, work_id: String) {
        val arrayOptions: Array<CharSequence>
        if ( userEmail == part.user_email ) arrayOptions = arrayOf(resources.getString(R.string.show_part), resources.getString(R.string.edit_part), resources.getString(R.string.delete_part), resources.getString(R.string.cancel_option))
        else arrayOptions = arrayOf(resources.getString(R.string.show_part), resources.getString(R.string.nan), resources.getString(R.string.nan), resources.getString(R.string.cancel_option))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.your_do))
        builder.setItems(arrayOptions) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(this, EmployeeShowPart::class.java)
                        intent.putExtra(Common.PART_ID, part.part_id)
                    startActivity(intent)
                }
                1 -> {
                    if ( userEmail == part.user_email ){
                        val intent = Intent(this, EmployeeEditPart::class.java)
                        intent.putExtra(Common.PART_ID, part.part_id)
                        startActivity(intent)
                    }
                    dialog.dismiss()
                }
                2 -> {
                    if ( userEmail == part.user_email ){
                        showConfirmToDeletePart(part.data_time, part.part_id, company_id, work_id)
                    }
                    dialog.dismiss()
                }
                3 -> {  dialog.dismiss() }
            }
        }
        builder.create().show()
    }


    private fun showConfirmToDeletePart(data_time: String ,partId: String, company_id: String, work_id: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.delete_this_part))
        builder.setMessage(data_time)
        builder.setNeutralButton(resources.getString(R.string.yes)){ a, _ ->
            Firebase.firestore.collection("p").document(company_id).collection(work_id).document(partId).delete().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, resources.getString(R.string.part_deleted), Toast.LENGTH_SHORT).show()
                    val intentInterior = intent; startActivity(intentInterior); finish()
                } else {
                    Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_SHORT).show()
                }
            }
            a.dismiss()
        }
        builder.setPositiveButton(resources.getString(R.string.no)){ a, _ ->
            a.dismiss()
        }
        builder.create().show()
    }


}