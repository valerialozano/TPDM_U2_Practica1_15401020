package mx.edu.ittepic.tpdm_u2_practica1_15401020

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var nuevaLista : Button?=null
    var layoutCampos : LinearLayout?=null
    var listView : ListView?=null
    var basedatos = BaseDatos(this, "practica1", null, 1)
    var lista = ArrayList<String>()
    var adapter : ArrayAdapter<String>?=null
    var ids = ArrayList<String>()
    var tareas = ArrayList<String>()
    var fechita = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.setTitle("Lista de actividades")

        nuevaLista = findViewById(R.id.nuevaLista)
        layoutCampos = findViewById(R.id.layoutCampos)
        listView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,lista)
        fechita = SimpleDateFormat("dd/MM/yyyy").format(Date())
        buscar()

        listView?.setOnItemClickListener { parent, view, position, id ->

            var idLista = ids.get(position)
            var tareas = tareas.get(position)
          //  Toast.makeText(this, idLista,Toast.LENGTH_LONG).show()
            var activity = Intent(this,Main2Activity::class.java)
            activity.putExtra("idLista",idLista)
            activity.putExtra("tareas",tareas)
            startActivity(activity)

        }

        nuevaLista?.setOnClickListener{
            layoutCampos?.removeAllViews()
            var descripcion = EditText(this)
            descripcion.setHint("Descripción")
            descripcion.textSize = 20F
            descripcion.id = R.id.etDescripcion

            var fecha = EditText(this)
            fecha.inputType = InputType.TYPE_CLASS_DATETIME
            fecha.setHint("Fecha de creación")
            fecha.textSize = 20F
            fecha.setText(fechita)
            fecha.id = R.id.etFechaCreacion

            var boton = Button(this)
            boton.setText("Crear lista")
            boton.textSize = 17F

            var boton2 = Button(this)
            boton2.setText("Cancelar")
            boton2.textSize = 17F

            layoutCampos?.addView(descripcion)
            layoutCampos?.addView(fecha)
            layoutCampos?.addView(boton)
            layoutCampos?.addView(boton2)

            boton.setOnClickListener {
                if(descripcion.text.toString().isEmpty()||fecha.text.toString().isEmpty()){
                    mensaje("ADVERTENCIA","Por favor, llena todos los campos.")
                }else {
                    insertar(descripcion.text.toString(), fecha.text.toString())
                }
            }
            boton2.setOnClickListener{
                layoutCampos?.removeAllViews()
            }
        }
    }
    fun insertar(descripcion:String, fecha:String){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO LISTA VALUES(null,'${descripcion}','${fecha}')"
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("ÉXITO","Se insertó la lista correctamente.")
            layoutCampos?.removeAllViews()
            buscar()

        }catch(err: SQLiteException){
            mensaje("ERROR","No se pudo insertar la lista.")
        }
    }
    fun buscar(){
        try {
            listView?.adapter = null
            lista.clear()
            ids.clear()
            tareas.clear()
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM LISTA"
            var resultado = transaccion.rawQuery(SQL, null)
            var cadena = ""
            while (resultado.moveToNext()) {
                cadena = resultado.getString(0) + "     "+ resultado.getString(1) + "               " + resultado.getString(2)
                lista.add(cadena)
                ids.add(resultado.getString(0))
                tareas.add(resultado.getString(1))
            }

            listView?.adapter = adapter
           (0..lista.size-1).forEach{
                System.out.println(lista.get(it))
            }
        }catch(err:SQLiteException){
            mensaje("ERROR","No se pudieron buscar las listas.")
        }
    }

    fun mensaje(titulo:String,texto:String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(texto).setPositiveButton("Oki"){ dialog, which->}.show()
    }

}
