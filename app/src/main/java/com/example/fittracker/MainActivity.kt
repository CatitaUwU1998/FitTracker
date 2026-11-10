package com.example.fittracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    // meta del dia en minutos
    val meta = 60

    // lista donde guardo los entrenamientos
    val entrenamientos = mutableListOf<Entrenamiento>()
    lateinit var adapter: EntrenamientoAdapter

    lateinit var spinnerTipo: Spinner
    lateinit var txtDescripcion: TextView
    lateinit var editMinutos: EditText
    lateinit var grupoIntensidad: RadioGroup
    lateinit var checkCalentamiento: CheckBox
    lateinit var checkHidratacion: CheckBox
    lateinit var checkEstiramiento: CheckBox
    lateinit var estrellas: RatingBar
    lateinit var txtEstrellas: TextView
    lateinit var barraMeta: ProgressBar
    lateinit var txtMeta: TextView
    lateinit var txtSesiones: TextView
    lateinit var txtMinutos: TextView
    lateinit var txtPromedio: TextView
    lateinit var txtVacio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // tambien sumo el teclado para que se pueda bajar cuando esta abierto
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // busco todas las vistas del layout
        spinnerTipo = findViewById(R.id.spinnerTipo)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        editMinutos = findViewById(R.id.editMinutos)
        grupoIntensidad = findViewById(R.id.grupoIntensidad)
        checkCalentamiento = findViewById(R.id.checkCalentamiento)
        checkHidratacion = findViewById(R.id.checkHidratacion)
        checkEstiramiento = findViewById(R.id.checkEstiramiento)
        estrellas = findViewById(R.id.estrellas)
        txtEstrellas = findViewById(R.id.txtEstrellas)
        barraMeta = findViewById(R.id.barraMeta)
        txtMeta = findViewById(R.id.txtMeta)
        txtSesiones = findViewById(R.id.txtSesiones)
        txtMinutos = findViewById(R.id.txtMinutos)
        txtPromedio = findViewById(R.id.txtPromedio)
        txtVacio = findViewById(R.id.txtVacio)

        // SPINNER: le paso los tipos que estan en strings.xml
        val adapterSpinner = ArrayAdapter.createFromResource(
            this, R.array.tipos_entrenamiento, android.R.layout.simple_spinner_item
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterSpinner

        // cuando cambia el tipo muestro una descripcion
        val descripciones = resources.getStringArray(R.array.descripciones_entrenamiento)
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                txtDescripcion.text = descripciones[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // RATINGBAR: muestro con texto lo que eligio
        val nombresEstrellas = arrayOf("Muy facil", "Facil", "Normal", "Dificil", "Muy dificil")
        estrellas.setOnRatingBarChangeListener { _, rating, _ ->
            val num = rating.toInt()
            if (num == 0) {
                txtEstrellas.text = getString(R.string.sin_calificar)
            } else {
                txtEstrellas.text = "$num de 5 - ${nombresEstrellas[num - 1]}"
            }
        }

        // RECYCLERVIEW
        val lista = findViewById<RecyclerView>(R.id.listaEntrenamientos)
        adapter = EntrenamientoAdapter(entrenamientos)
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = adapter

        // BOTON guardar
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            guardarEntrenamiento()
        }

        actualizarResumen()
    }

    fun guardarEntrenamiento() {
        // minutos
        val minutos = editMinutos.text.toString().toIntOrNull()
        if (minutos == null || minutos == 0) {
            editMinutos.error = "Pon cuantos minutos entrenaste"
            return
        }

        // tipo del spinner
        val tipo = spinnerTipo.selectedItem.toString()

        // intensidad del radiogroup
        val idMarcado = grupoIntensidad.checkedRadioButtonId
        if (idMarcado == -1) {
            Toast.makeText(this, "Falta elegir la intensidad", Toast.LENGTH_SHORT).show()
            return
        }
        val intensidad = findViewById<RadioButton>(idMarcado).text.toString()

        // checkbox marcados
        val checks = mutableListOf<String>()
        if (checkCalentamiento.isChecked) checks.add("Calentamiento")
        if (checkHidratacion.isChecked) checks.add("Hidratación")
        if (checkEstiramiento.isChecked) checks.add("Estiramiento")

        // estrellas
        val numEstrellas = estrellas.rating.toInt()
        if (numEstrellas == 0) {
            Toast.makeText(this, "Falta poner las estrellas", Toast.LENGTH_SHORT).show()
            return
        }

        // lo agrego arriba de la lista
        entrenamientos.add(0, Entrenamiento(tipo, minutos, intensidad, checks, numEstrellas))
        adapter.notifyItemInserted(0)

        actualizarResumen()
        limpiar()
        Toast.makeText(this, "Entrenamiento guardado!", Toast.LENGTH_SHORT).show()
    }

    // PROGRESSBAR y resumen de la tabla
    fun actualizarResumen() {
        var total = 0
        var sumaEstrellas = 0
        for (e in entrenamientos) {
            total += e.minutos
            sumaEstrellas += e.estrellas
        }

        barraMeta.max = meta
        barraMeta.progress = if (total > meta) meta else total

        var porcentaje = total * 100 / meta
        if (porcentaje > 100) porcentaje = 100

        if (total >= meta) {
            txtMeta.text = "$total / $meta min ($porcentaje%) - meta cumplida!"
        } else {
            txtMeta.text = "$total / $meta min ($porcentaje%)"
        }

        txtSesiones.text = entrenamientos.size.toString()
        txtMinutos.text = "$total min"

        if (entrenamientos.isEmpty()) {
            txtPromedio.text = "-"
            txtVacio.visibility = View.VISIBLE
        } else {
            val promedio = sumaEstrellas.toFloat() / entrenamientos.size
            txtPromedio.text = String.format("%.1f de 5", promedio)
            txtVacio.visibility = View.GONE
        }
    }

    // deja el formulario vacio otra vez
    fun limpiar() {
        spinnerTipo.setSelection(0)
        editMinutos.setText("")
        editMinutos.clearFocus()
        grupoIntensidad.clearCheck()
        checkCalentamiento.isChecked = false
        checkHidratacion.isChecked = false
        checkEstiramiento.isChecked = false
        estrellas.rating = 0f
    }
}