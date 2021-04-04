package work.parts.company
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_employeer.*
import work.parts.R
import work.parts.utils.App
import work.parts.utils.Common

class NewEmployeerActivity : AppCompatActivity() {
    lateinit var testViewModel: TestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_employeer)

        emploeer_name.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        emploeer_email.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        employee_password.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.gris))
        App.auth = FirebaseAuth.getInstance()

        create_employee.setOnClickListener { createNewEmployee() }

    }

    private fun createNewEmployee() {
        val employeeName = emploeer_name.text.toString().trim()
        val employeeEmail = emploeer_email.text.toString().trim()
        val employeePassword = employee_password.text.toString().trim()
        if( employeeName.length < 3 || employeeEmail.length < 4 || employeePassword.length < 4 ) Toast.makeText(this, resources.getString(R.string.enter_data), Toast.LENGTH_LONG).show()
        else {
            val hashMap = HashMap<String, Any>()
                hashMap[Common.EMPLOYEE_NAME] = employeeName
                hashMap[Common.EMPLOYEE_EMAIL] = employeeEmail
                hashMap[Common.EMPLOYEE_PASSWORD] = employeePassword
                hashMap[Common.COMPANY_ID] = App.auth.currentUser!!.uid

            App.employeeReference.orderByChild(Common.EMPLOYEE_EMAIL).equalTo(employeeEmail).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()) {
                                if(snapshot.value == null){
                                    insertEmployeeToDataBase(hashMap)
                                } else {
                                    Toast.makeText(applicationContext, resources.getString(R.string.user_exist), Toast.LENGTH_LONG).show()
                                    emploeer_email.setText("")
                                }
                            } else { insertEmployeeToDataBase(hashMap) }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
                        }
                    })
        }



    }

    private fun insertEmployeeToDataBase(hashMap: HashMap<String, Any>){
        val key = App.employeeReference.push().key.toString()
        hashMap[Common.USER_ID] = key
        App.employeeReference.child(key).setValue(hashMap).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this, resources.getString(R.string.employee_create), Toast.LENGTH_LONG).show()
                // startActivity(Intent(this, CompanyIndexActivity::class.java));
                finish()
            } else {
                Toast.makeText(this, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
            }
        }
    }
}

class TestViewModel: ViewModel(){
    var number = 0
    val currentNumber: MutableLiveData<Int> by lazy {   MutableLiveData<Int>() }
    val currentBoolen: MutableLiveData<Boolean> by lazy {   MutableLiveData<Boolean>() }
}
