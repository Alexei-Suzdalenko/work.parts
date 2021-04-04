package work.parts.register_login
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_employee_login.*
import work.parts.R
import work.parts.employee.EmployeeIndexActivity
import work.parts.utils.App
import work.parts.utils.App.Companion.editor
import work.parts.utils.Common
import work.parts.utils.models.Employee

class EmployeeLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_login)
        employee_email.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        employee_password.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        employee_btn_login.setOnClickListener { emloyeeTryLogin() }
    }


    private fun emloyeeTryLogin() {
        val email = employee_email.text.toString().trim()
        val password = employee_password.text.toString().trim()
        if( email.length < 4 || password.length < 4 ) Toast.makeText(this, resources.getString(R.string.enter_data), Toast.LENGTH_LONG).show()
        else {
            App.employeeReference.orderByChild(Common.EMPLOYEE_EMAIL).equalTo(email).limitToFirst(1).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                         for(child in snapshot.children){
                             val employee = child.getValue(Employee::class.java)
                             comparePassword(employee, password)
                         }
                    } else Toast.makeText(applicationContext, resources.getString(R.string.employee_dont_found), Toast.LENGTH_LONG).show()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
                }
            })
        }
    }


    private fun comparePassword(employee: Employee?, password: String){
        if(employee != null){
            if (password == employee.employee_password) {
                editor.putString(Common.EMPLOYEE_NAME, employee.employee_name)
                editor.putString(Common.COMPANY_ID, employee.company_id)
                editor.putString(Common.EMPLOYEE_EMAIL, employee.employee_email)
                editor.apply()
                startActivity(Intent(applicationContext, EmployeeIndexActivity::class.java)); finish()
            } else Toast.makeText(applicationContext, resources.getString(R.string.employee_dont_found), Toast.LENGTH_LONG).show()
        }
    }


}