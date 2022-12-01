package com.example.absencemanagementapp.activities.seance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.ModuleActivity
import com.example.absencemanagementapp.activities.qrcode.QrCodeActivity
import com.example.absencemanagementapp.models.Seance
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SeanceActivity : AppCompatActivity() {
    lateinit var module_intitule_tv: TextView
    lateinit var back_iv: ImageView

    lateinit var absence_list_cv: MaterialCardView
    lateinit var show_qr_code: MaterialCardView

    lateinit var seance_type_tv: TextView
    lateinit var seance_date_tv: TextView
    lateinit var seance_start_time_tv: TextView
    lateinit var seance_end_time_tv: TextView
    lateinit var salle_nb_tv: TextView
    lateinit var total_absence_tv: TextView

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    var seance_id: String? = null
    var module_intitule: String? = null
    var module_id: Int? = null
    var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seance)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        seance_id = intent.getStringExtra("seance_id")
        module_id = intent.getIntExtra("module_id", -1)
        url = intent.getStringExtra("url").toString()
        module_intitule = intent.getStringExtra("module_intitule")

        //initiate views
        initViews()

        Log.w("on create", seance_id.toString())
        seance_id?.let { getCurrentSeance(it) }

        back_iv.setOnClickListener({ back() })

        absence_list_cv.setOnClickListener({ showAbsenceList() })

        show_qr_code.setOnClickListener({ showQrCode() })
    }

    private fun initViews() {
        module_intitule_tv = this.findViewById(R.id.module_intitule_tv)
        back_iv = findViewById(R.id.back_arrow)
        absence_list_cv = this.findViewById(R.id.absence_list_cv)
        show_qr_code = this.findViewById(R.id.show_qr_code)
        seance_type_tv = this.findViewById(R.id.seance_type_tv)
        seance_date_tv = this.findViewById(R.id.seance_date_tv)
        seance_start_time_tv = this.findViewById(R.id.seance_start_time_tv)
        seance_end_time_tv = this.findViewById(R.id.seance_end_time_tv)
        salle_nb_tv = this.findViewById(R.id.salle_nb_tv)
        total_absence_tv = this.findViewById(R.id.total_absence_tv)

        module_intitule = intent.getStringExtra("module_intitule")
        module_intitule_tv.text = module_intitule
    }

    private fun getCurrentSeance(id: String) {
        database.getReference("seances").child(id).get().addOnSuccessListener {
            if (it.exists()) {
                val seance = it.getValue(Seance::class.java)
                if (seance != null) {
                    setDbVariables(seance)
                }
            }
        }
    }

    private fun back() {
        val intent = Intent(this, ModuleActivity::class.java)
        intent.putExtra("module_intitule", module_intitule)
        intent.putExtra("module_id", module_id)
        startActivity(intent)
        finish()
    }

    private fun showQrCode() {
        val intent = Intent(this, QrCodeActivity::class.java)
        intent.putExtra("seance_id", seance_id)
        intent.putExtra("module_id", module_id)
        intent.putExtra("module_intitule", module_intitule)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun showAbsenceList() {
        //TODO: show absence list
    }

    private fun setDbVariables(seance: Seance) {
        seance_type_tv.text = seance.type
        seance_date_tv.text = seance.date
        seance_start_time_tv.text = seance.start_time
        seance_end_time_tv.text = seance.end_time
        salle_nb_tv.text = seance.n_salle
        total_absence_tv.text = seance.total_absences.toString()
    }
}