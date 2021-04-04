package work.parts.company
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import work.parts.R
import work.parts.register_login.CompanyEnterActivity
import work.parts.ui.main.SectionsPagerAdapter
import work.parts.utils.App
import work.parts.utils.App.Companion.auth
import work.parts.utils.Common
import work.parts.utils.models.Company

class CompanyIndexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        auth = FirebaseAuth.getInstance()

        // get company information
        App.companyReference.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               val company: Company? = snapshot.getValue(Company::class.java)
                titleActivity.text = company?.company_name
                progressCompanyIndex.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CompanyIndexActivity, resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show(); finish()
            }
        })

    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item_company, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> { auth.signOut(); startActivity(Intent(this, CompanyEnterActivity::class.java)); finish(); return true }
            R.id.new_employee -> { startActivity(Intent(this, NewEmployeerActivity::class.java)); return true }
            R.id.new_work -> { startActivity(Intent(this, NewWorkActivity::class.java)); return true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser == null) finish()
    }
}

// https://youtu.be/YcZ14k-xGHI?list=RDhxTSjiCA-dc music