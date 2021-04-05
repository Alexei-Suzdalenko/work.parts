package work.parts.ui.main
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_main.view.*
import work.parts.R
import work.parts.company.CompanyShowResumeWork
import work.parts.company.CompanyShowWorkParts
import work.parts.utils.CSVFile
import work.parts.utils.Common
import work.parts.utils.Repos
import work.parts.utils.WorkEmployeeAdapter
import work.parts.utils.models.Employee
import work.parts.utils.models.Work
class PlaceholderFragment : Fragment() {
    private lateinit var mAdView: AdView
    private lateinit var pageViewModel: PageViewModel
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)
        index = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val listView = root.findViewById<ListView>(R.id.fragmentListView)
             MobileAds.initialize(context!!) {}
             mAdView = root.findViewById<AdView>(R.id.adView)
             val adRequest = AdRequest.Builder().build()
             mAdView.adListener = object: AdListener() {
                 override fun onAdLoaded() {
                     var size = 111
                     val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                     if( mAdView.height > 15 ) size = mAdView.height
                     params.setMargins(22, 0, 22, size)
                     listView.layoutParams = params
                 }
             }
             mAdView.loadAd(adRequest)
     // CSVFile.createCSVfile(context!!)
     // CSVFile.save("file.txt", context!!)
        when( index ){
            1 -> {
                pageViewModel.fetchWork().observe(this, Observer { listWork ->
                    listView.adapter = WorkEmployeeAdapter(context!!, listWork, null, 1, Common.COMPANY)
                    if( listWork.size == 0 ) {
                        root.imageViewWorkList.visibility = View.VISIBLE
                    } else {
                        root.imageViewWorkList.visibility = View.GONE
                    }
                    listView.setOnItemClickListener { _, _, i, _ ->
                        showWorkOptions(listWork[i])
                    }
                })
            }
            2 -> {
                pageViewModel.fetchEmployee().observe(this, Observer { listEmployee ->
                    if( listEmployee.size == 0 ) {
                        root.imageViewWorkList.visibility = View.VISIBLE
                    } else {
                        root.imageViewWorkList.visibility = View.GONE
                    }
                    listView.adapter = WorkEmployeeAdapter(context!!, null, listEmployee, 2, Common.COMPANY)
                    listView.setOnItemClickListener { _, _, i, _ ->
                        showEmployeeOption(listEmployee[i])
                    }
                })
            }
        }
        return root
    }


    private fun showWorkOptions(work: Work) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(work.name)
        builder.setItems(arrayOf<CharSequence>(resources.getString(R.string.show_resume_work), resources.getString(R.string.work_parts), resources.getString(R.string.file_csv), resources.getString(R.string.file_txt), resources.getString(R.string.nan), resources.getString(R.string.delete_work_and_parts), resources.getString(R.string.nan), resources.getString(R.string.cancel_option))) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(context!!, CompanyShowResumeWork::class.java)
                    intent.putExtra(Common.WORK_NAME, work.name)
                    intent.putExtra(Common.COMPANY_ID, work.company_id)
                    intent.putExtra(Common.WORK_ID, work.work_id)
                    context!!.startActivity(intent); dialog.dismiss()
                }
                1 -> {
                    val intent = Intent(context!!, CompanyShowWorkParts::class.java)
                    intent.putExtra(Common.WORK_NAME, work.name)
                    intent.putExtra(Common.COMPANY_ID, work.company_id)
                    intent.putExtra(Common.WORK_ID, work.work_id)
                    context!!.startActivity(intent); dialog.dismiss()
                }
                2 -> { createCSVFileAndShare( work.name, work.company_id, work.work_id ); dialog.dismiss() }
                3 -> { createTXTFileAndShare( work.name, work.company_id, work.work_id ); dialog.dismiss() }
                5 -> { deleteThisWorkAndParts(work.name, work.company_id, work.work_id); dialog.dismiss() }
                7 -> { dialog.dismiss() }
            }
        }
        builder.create().show()
    }


    private fun createCSVFileAndShare(name: String, companyId: String, workId: String) {
        CSVFile.save("file.csv", context!!, name, companyId, workId)
    }

    private fun createTXTFileAndShare(name: String, companyId: String, workId: String) {
        CSVFile.save("file.txt", context!!, name, companyId, workId)
    }



    private fun deleteThisWorkAndParts(name: String, companyId: String, workId: String) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(resources.getString(R.string.delete_this_work) + " " + name)
        builder.setNeutralButton(resources.getString(R.string.yes)) { a, _ -> deleteWork(name, companyId, workId); a.dismiss() }
        builder.setPositiveButton(resources.getString(R.string.no)) { a, _ -> a.dismiss()}
        builder.create().show()
    }

    private fun deleteWork(name: String, companyId: String, workId: String){
        Repos().companyDeleteWork(companyId, workId).observeForever { ok ->
            if ( ok == "ok" ) Toast.makeText(context!!, name + context!!.resources.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
        }
    }


    private fun showEmployeeOption(employee: Employee) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(employee.employee_name)
        builder.setItems(arrayOf<CharSequence>(resources.getString(R.string.send), resources.getString(R.string.cancel_option), resources.getString(R.string.nan), resources.getString(R.string.delete_user))) { dialog, which ->
            when (which) {
                0 -> {
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plain"
                    val text = context!!.resources.getString(R.string.employee) + "\n" + employee.employee_name + "\n" + employee.employee_email + "\n" + employee.employee_password + "\n" + "https://play.google.com/store/apps/details?id=work.parts"
                    share.putExtra(Intent.EXTRA_TEXT, text)
                    startActivity(Intent.createChooser(share, context!!.resources.getString(R.string.app_name)))
                    dialog.dismiss()
                }
                3 -> {
                    confirmDeleteEmployee(employee)
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }


    private fun confirmDeleteEmployee(employee: Employee) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(context!!.resources.getString(R.string.delete_employee) + " " + employee.employee_name)
        builder.setNeutralButton(context!!.resources.getString(R.string.yes)) { a, _ ->
            FirebaseDatabase.getInstance().reference.child(Common.EMPLOYEE).child(employee.id_user).removeValue().addOnCompleteListener { it ->
                if(it.isSuccessful){ Toast.makeText(context!!, context!!.resources.getString(R.string.employee_deleted), Toast.LENGTH_SHORT).show() }
            }
            a.dismiss()
        }
        builder.setPositiveButton(context!!.resources.getString(R.string.no)){ a, _ -> a.dismiss() }
        builder.create().show()
    }


    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}