package work.parts.company
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.company_show_resume_work.*
import kotlinx.android.synthetic.main.company_show_resume_work.id_parts_show
import kotlinx.android.synthetic.main.company_show_work_parts.*
import work.parts.R
import work.parts.utils.Common
import work.parts.utils.CompanyListPartAdapter
import work.parts.utils.models.Part
import work.parts.utils.models.Work

class CompanyShowWorkParts : AppCompatActivity() {
    lateinit var listWorks: MutableList<Part>
    private lateinit var mAdView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.company_show_work_parts)

        title = intent.getStringExtra(Common.WORK_NAME)
        val companyId = intent.getStringExtra(Common.COMPANY_ID).toString()
        val workId = intent.getStringExtra(Common.WORK_ID).toString()
        listWorks = mutableListOf()

        Firebase.firestore.collection(Common.PARTS).document(companyId).collection(workId).orderBy("ml").get().addOnSuccessListener { documents ->
            if (documents != null) {
                listWorks.clear()
                for (document in documents) {
                    val part: Part = document.toObject<Part>()
                    listWorks.add(part)
                }

                if( listWorks.size == 0 ) imageCompanyWorkParts.visibility = View.VISIBLE
                else imageCompanyWorkParts.visibility = View.GONE

                listview_comp_part.adapter = CompanyListPartAdapter(this, listWorks)
                comp_show_parts.visibility = View.GONE
            } else {
                Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show(); finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show(); finish()
        }

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adViewListPartsCompany)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}