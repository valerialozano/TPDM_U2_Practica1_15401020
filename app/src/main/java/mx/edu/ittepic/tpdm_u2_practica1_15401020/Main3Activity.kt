package mx.edu.ittepic.tpdm_u2_practica1_15401020

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main3.*
import java.text.SimpleDateFormat
import java.util.*

class Main3Activity : AppCompatActivity() {

    var idTarea=""
    var idLista=""
    var tarea=""
    var tareas=""
    var eliminarTarea : Button?=null
    var actualizarTarea : Button?=null
    var regresar3 : Button?=null
    var descripcion : EditText?=null
    var fecha : EditText?=null
    var check : CheckBox?=null
    var estatus : TextView?=null
    var basedatos = BaseDatos(this, "practica1", null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        eliminarTarea = findViewById(R.id.eliminarTarea)
        actualizarTarea = findViewById(R.id.actualizarTarea)
        regresar3 = findViewById(R.id.regresar3)
        descripcion = findViewById(R.id.descripcionTarea)
        fecha = findViewById(R.id.fechaTarea)
        check = findViewById(R.id.check)
        estatus = findViewById(R.id.estatus)

        idTarea = intent.extras?.getString("idTarea").toString()
        idLista = intent.extras?.getString("idLista").toString()
        tarea = intent.extras?.getString("tarea").toString()
        tareas = intent.extras?.getString("tareas").toString()
        var fechita = SimpleDateFormat("dd/MM/yyyy").format(Date())

        this.setTitle(tarea)

        buscar(idTarea)

        //validar si esta caducada la tarea o no
        if(fechita.compareTo(fecha?.text.toString())>0 && check?.isChecked==false){ //si es mayor
            estatus?.setText("Estatus: Caducada")
        }else if(fechita.compareTo(fecha?.text.toString())<0 && check?.isChecked==false){
            estatus?.setText("Estatus: Pendiente")
        }else if(fechita.compareTo(fecha?.text.toString())<0 && check?.isChecked==true){
            estatus?.setText("Estatus: Terminada")
        }else{
            estatus?.setText("Estatus: Terminada")
        }
        check?.setOnCheckedChangeListener { buttonView, isChecked ->
            save(check?.isChecked)
            //check?.isChecked
            if(fechita.compareTo(fecha?.text.toString())>0 && check?.isChecked==false){ //si es mayor
                estatus?.setText("Estatus: Caducada")
            }else if(fechita.compareTo(fecha?.text.toString())<0 && check?.isChecked==false){
                estatus?.setText("Estatus: Pendiente")
            }else if(fechita.compareTo(fecha?.text.toString())<0 && check?.isChecked==true){
                estatus?.setText("Estatus: Terminada")
            }else{
                estatus?.setText("Estatus: Terminada")
            }
        }
        actualizarTarea?.setOnClickListener {
            if(descripcionTarea.text.toString().isEmpty()||fechaTarea.text.toString().isEmpty()){
                mensaje("ADVERTENCIA","Por favor, llena todos los campos.")
            }else {
                actualizar(descripcionTarea.text.toString(),fechaTarea.text.toString(),idTarea)
            }
        }
        eliminarTarea?.setOnClickListener {
            AlertDialog.Builder(this).setTitle("ADVERTENCIA").
                setMessage("¿Estás seguro que deseas eliminar la tarea?").setPositiveButton("Sí"){ dialog, which->
                eliminar(idTarea)
            }.setNeutralButton("No"){dialog,which->
                return@setNeutralButton
            }.show()
        }
        regresar3?.setOnClickListener {
            var activity = Intent(this,Main2Activity::class.java)
            activity.putExtra("idLista",idLista)
            activity.putExtra("tareas",tareas)
            startActivity(activity)
        }
    }

    fun buscar(idTarea:String){
        try{
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM TAREAS WHERE IDTAREA=" +idTarea
            var resultado = transaccion.rawQuery(SQL, null)
            if(resultado.moveToFirst()){
                descripcionTarea.setText(resultado.getString(1))
                fechaTarea.setText(resultado.getString(2))
            }
        }catch(err: SQLiteException){
            mensaje("ERROR","No se pudieron buscar las tareas.")
        }
    }

    fun actualizar(descripcion:String, fecha:String,idTarea:String){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL = "UPDATE TAREAS SET DESCRIPCION='"+descripcion+ "', REALIZADO='"+fecha+ "' WHERE IDTAREA="+idTarea
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("ÉXITO","Se actualizó la tarea correctamente.")
        }catch(err: SQLiteException){
            mensaje("ERROR","No se pudo actualizar la tarea.")
        }
    }
    fun eliminar(idTarea:String){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "DELETE FROM TAREAS WHERE IDTAREA="+idTarea
            transaccion.execSQL(SQL)
            transaccion.close()
            AlertDialog.Builder(this).setTitle("ÉXITO").setMessage("Se eliminó la tarea correctamente.").setPositiveButton("Oki"){ dialog, which->
                var activity = Intent(this,Main2Activity::class.java)
                activity.putExtra("idLista",idLista)
                activity.putExtra("tareas",tareas)
                startActivity(activity)

            }.show()

        }catch (err:SQLiteException){
            mensaje("ERROR","No se pudo eliminar la lista.")
        }
    }

    fun mensaje(titulo:String,texto:String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(texto).setPositiveButton("Oki"){ dialog, which->}.show()
    }
}
