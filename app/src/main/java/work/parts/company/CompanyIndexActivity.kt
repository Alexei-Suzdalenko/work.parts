package work.parts.company
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import work.parts.R
import work.parts.register_login.CompanyEnterActivity
import work.parts.ui.main.SectionsPagerAdapter
import work.parts.utils.App
import work.parts.utils.App.Companion.auth

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 11);
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permi = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permi, 7)
            }
        }

        var i = 0

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
            R.id.comment -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=work.parts")))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser == null) finish()
    }
}

// https://youtu.be/YcZ14k-xGHI?list=RDhxTSjiCA-dc music
// grecheskiy
//