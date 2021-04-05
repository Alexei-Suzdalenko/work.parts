package work.parts.utils
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import work.parts.R
import work.parts.utils.models.Part
import java.io.File

object CSVFile {
    fun createCSVfile(context: Context){
      //  Toast.makeText(context,  context.resources.getString(R.string.app_name), Toast.LENGTH_SHORT).show()
    }

    fun save(fileName: String, context: Context, name: String, companyId: String, workId: String) {
        var data: String = "";
        val newLine = "\n"
        val path = context.getExternalFilesDir(null)
        try {
            val file = File(path, fileName)
            if ( !file.exists() ) file.mkdir()
            file.delete()
            file.createNewFile()

            Firebase.firestore.collection(Common.PARTS).document(companyId).collection(workId).get().addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val part: Part = document.toObject<Part>()
                        data += part.data_time + ", "
                        data += part.user_name + ", "
                        data += part.working_time.toString() + " " + context.resources.getString(R.string.item_hours)  + ", "
                        data += part.costs.toString() + " " + context.resources.getString(R.string.item_cost)  + ", "
                        data += part.km.toString() + " " + context.resources.getString(R.string.item_km)  + ", "
                        data += part.work_done + ", "
                        data += part.comment + " " + newLine
                    }
                    file.printWriter().use { out -> out.print(data) }

                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "$name/*"
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.absolutePath))
                    context.startActivity(Intent.createChooser(sharingIntent, name))
                    App.showMeInitincial = true

                } else Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_SHORT).show()
            }


            } catch (e: Exception) { Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_SHORT).show() }
    }

}