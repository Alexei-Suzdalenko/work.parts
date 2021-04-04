package work.parts.register_login
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_company_enter.*
import work.parts.company.CompanyIndexActivity
import work.parts.R

class CompanyEnterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_enter)
        company_email_login.setHintTextColor(ContextCompat.getColor(applicationContext,
            R.color.gris
        ))
        company_password_login.setHintTextColor(ContextCompat.getColor(applicationContext,
            R.color.gris
        ))
        auth = FirebaseAuth.getInstance()

        btn_company_login.setOnClickListener {
            val emailCompany = company_email_login.text.toString().trim()
            val passwordCompany =  company_password_login.text.toString().trim()
            if( emailCompany.length < 4 || passwordCompany.length < 5 ) Toast.makeText(this, resources.getString(
                R.string.enter_data
            ),  Toast.LENGTH_LONG).show()
            else {
                auth.signInWithEmailAndPassword(emailCompany, passwordCompany).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val intent = Intent(this, CompanyIndexActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent); finish()
                    } else {
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


    }



}