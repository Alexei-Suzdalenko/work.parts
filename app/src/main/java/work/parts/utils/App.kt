package work.parts.utils
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class App: Application() {

    companion object{
        lateinit var auth: FirebaseAuth
        lateinit var companyReference: DatabaseReference
        lateinit var employeeReference: DatabaseReference
        lateinit var workReference: DatabaseReference
        lateinit var sharedPreferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor

    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        companyReference = FirebaseDatabase.getInstance().reference.child(Common.COMPANY)
        employeeReference = FirebaseDatabase.getInstance().reference.child(Common.EMPLOYEE)
        workReference = FirebaseDatabase.getInstance().reference.child(Common.WORK)
        sharedPreferences = getSharedPreferences(Common.TAG, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

    }
}