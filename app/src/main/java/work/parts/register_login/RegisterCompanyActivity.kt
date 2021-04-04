package work.parts.register_login
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import work.parts.company.CompanyIndexActivity
import work.parts.R
import work.parts.employee.EmployeeIndexActivity
import work.parts.utils.App
import work.parts.utils.App.Companion.companyReference
import work.parts.utils.App.Companion.sharedPreferences
import work.parts.utils.Common

class RegisterCompanyActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var companyId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        if( auth.currentUser != null ){
            startActivity(Intent(this, CompanyIndexActivity::class.java)); finish()
        }

        company_name.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        company_email.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        company_password.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))

        btn_register.setOnClickListener { registerCompany() }
        company_entrance.setOnClickListener { startActivity(Intent(this, CompanyEnterActivity::class.java)) }
        worker_enter.setOnClickListener { startActivity(Intent(this, EmployeeLoginActivity::class.java)) }
    }


    private fun registerCompany() {
        val companyName = company_name.text.toString().trim()
        val companyEmail = company_email.text.toString().trim()
        val companyPassword = company_password.text.toString().trim()
        if( companyName.length < 4 || companyEmail.length < 5 || companyPassword.length < 3) Toast.makeText(this, resources.getString(R.string.enter_data),  Toast.LENGTH_LONG).show()
        else {
            auth.createUserWithEmailAndPassword(companyEmail, companyPassword).addOnCompleteListener { task ->
                if ( task.isSuccessful ) {
                    companyId = auth.currentUser!!.uid
                    val userHasMap = HashMap<String, Any>()
                        userHasMap[Common.UID] = companyId
                        userHasMap[Common.COMPANY_NAME] = companyName
                        userHasMap[Common.COMPANY_EMAIL] = companyEmail
                        userHasMap[Common.COMPANY_PASSWORD] = companyPassword

                    companyReference.child(companyId).updateChildren(userHasMap).addOnCompleteListener { task ->
                        if ( task.isSuccessful ){
                            val intent = Intent(this, CompanyIndexActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent); finish()
                        } else {
                            Toast.makeText(this, resources.getString(R.string.enter_data),  Toast.LENGTH_LONG).show()
                        }
                    }
                } else Toast.makeText(this,  task.exception?.message.toString(),  Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if( auth.currentUser == null ) {
            if ( App.sharedPreferences.getString(Common.EMPLOYEE_NAME, Common.NONE).toString() == Common.NONE &&
                App.sharedPreferences.getString(Common.COMPANY_ID, Common.NONE).toString() == Common.NONE &&
                App.sharedPreferences.getString(Common.EMPLOYEE_EMAIL, Common.NONE).toString() == Common.NONE ) {
            } else {
                startActivity(Intent(this, EmployeeIndexActivity::class.java)); finish()
            }
        }
    }



}