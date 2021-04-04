package work.parts.employee
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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