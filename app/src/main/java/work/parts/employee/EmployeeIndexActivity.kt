package work.parts.employee
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.android.synthetic.main.activity_employee_index.*
import work.parts.R
import work.parts.register_login.RegisterCompanyActivity
import work.parts.utils.App
import work.parts.utils.Common
import work.parts.utils.EmployeeViewModel
import work.parts.utils.WorkEmployeeAdapter
import work.parts.utils.models.Work

// show me list work for current user for current user company
class EmployeeIndexActivity : AppCompatActivity() {
    lateinit var employeeViewModel: EmployeeViewModel
    private lateinit var mAdView: AdView
    lateinit var adLoader: AdLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_index)

        employeeViewModel = ViewModelProvider(this).get(EmployeeViewModel::class.java)
        val companyId = App.sharedPreferences.getString(Common.COMPANY_ID, Common.NONE).toString()
        val employeeName = App.sharedPreferences.getString(Common.EMPLOYEE_NAME, Common.NONE).toString()
        val email = App.sharedPreferences.getString(Common.EMPLOYEE_EMAIL, Common.NONE).toString()
        if( companyId == Common.NONE || employeeName == Common.NONE || email == Common.NONE ){
            Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
            startActivity(Intent(this, RegisterCompanyActivity::class.java)); finish()
        }

        employeeViewModel.fetchDataEmployee(email, this).observeForever { employee ->
            setTitleAndSubtitle(employee.employee_name, employee.employee_email)
            progress_employee_index.visibility = View.GONE
        }

        employeeViewModel.fetchWorkForThisWorker(companyId, this).observeForever { works ->
            listWorksEmployee.adapter = WorkEmployeeAdapter(this, works, null, 1, Common.EMPLOYEE)
            listWorksEmployee.setOnItemClickListener { _, _, i, _ ->
                createMenuOptions(i, works)
            }

        }

       MobileAds.initialize(this) {}
       mAdView = findViewById(R.id.adViewEmployeeIndexWork)
       val adRequest = AdRequest.Builder().build()
       mAdView.adListener = object: AdListener() {
           override fun onAdLoaded() {
               var size = 66
               val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
               if( mAdView.height > 15 ) size = mAdView.height
               params.setMargins(22, 0, 22, size)
               listWorksEmployee.layoutParams = params
           }
       }
       mAdView.loadAd(adRequest)








    }

    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = listWorksEmployee.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }



    private fun createMenuOptions(i: Int, works: MutableList<Work>){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.your_do))
        builder.setItems(arrayOf<CharSequence>(resources.getString(R.string.create_part), resources.getString(R.string.list_parts), resources.getString(R.string.nan), resources.getString(R.string.cancel_option))) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(this, EmployeeCreateNewPart::class.java)
                        intent.putExtra(Common.WORK_NAME, works[i].name)
                        intent.putExtra(Common.COMPANY_ID, works[i].company_id)
                        intent.putExtra(Common.WORK_ID, works[i].work_id)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(this, EmployeeListsWorkParts::class.java)
                        App.editor.putString(Common.WORK_NAME, works[i].name)
                        App.editor.putString(Common.COMPANY_ID, works[i].company_id)
                        App.editor.putString(Common.WORK_ID, works[i].work_id)
                        App.editor.apply()
                    startActivity(intent)
                }
                2 -> { dialog.dismiss() }
                3 -> { dialog.dismiss() }
            }
        }
        builder.create().show()
    }


    private fun setTitleAndSubtitle(title: String, subtitle: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setTitle(Html.fromHtml("<small>$title</small>", Html.FROM_HTML_MODE_COMPACT))
        } else {
            setTitle(Html.fromHtml("<small>$title</small>"))
        }
        supportActionBar!!.subtitle = subtitle
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item_employee, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_employee -> {
                App.editor.putString(Common.EMPLOYEE_NAME, Common.NONE)
                App.editor.putString(Common.COMPANY_ID, Common.NONE)
                App.editor.putString(Common.EMPLOYEE_EMAIL, Common.NONE)
                App.editor.apply()
                startActivity(Intent(this, RegisterCompanyActivity::class.java)); finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        if (App.sharedPreferences.getString(Common.EMPLOYEE_NAME, Common.NONE).toString() == Common.NONE && App.sharedPreferences.getString(Common.COMPANY_ID, Common.NONE).toString() == Common.NONE && App.sharedPreferences.getString(Common.EMPLOYEE_EMAIL, Common.NONE).toString() == Common.NONE){
            startActivity(Intent(this, RegisterCompanyActivity::class.java)); finish()
        }
    }
}