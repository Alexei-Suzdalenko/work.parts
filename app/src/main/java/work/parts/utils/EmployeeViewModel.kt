package work.parts.utils
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import work.parts.R
import work.parts.utils.models.Employee
import work.parts.utils.models.Work

class EmployeeViewModel: ViewModel() {

    fun fetchDataEmployee(email: String, context: Context): LiveData<Employee> {
        val mutableData: MutableLiveData<Employee> = MutableLiveData<Employee>()
        App.employeeReference.orderByChild(Common.EMPLOYEE_EMAIL).equalTo(email).limitToFirst(1).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                if(snapshot.exists()){
                    var user: Employee? = null
                    for(child in snapshot.children){
                        user = child.getValue(Employee::class.java)!!
                    }
                    mutableData.value = user!!
                } else Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
            }
        })
        return mutableData
    }


    fun fetchWorkForThisWorker(id_company: String, context: Context): LiveData<MutableList<Work>> {
        val mutableWorks: MutableLiveData<MutableList<Work>> = MutableLiveData<MutableList<Work>>()
        App.workReference.child(id_company).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val listWork = mutableListOf<Work>()
                    for(child in snapshot.children){
                        val work = child.getValue(Work::class.java)!!
                        listWork.add(work)
                    }
                    val reverse: MutableList<Work> = listWork.reversed().toMutableList()
                    mutableWorks.value = reverse
                } else Toast.makeText(context, context.resources.getString(R.string.dont_have_workd), Toast.LENGTH_LONG ).show()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG ).show()
            }
        })
        return mutableWorks
    }


}

