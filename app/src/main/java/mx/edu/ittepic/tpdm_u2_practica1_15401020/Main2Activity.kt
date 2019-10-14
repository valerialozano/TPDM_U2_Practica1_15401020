package mx.edu.ittepic.tpdm_u2_practica1_15401020

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog

class Main2Activity : AppCompatActivity() {

    var layoutCamposT : LinearLayout?=null
    var listViewT : ListView?=null
    var nuevaTarea : Button?=null
    var eliminarLista : Button?=null
    var actualizarLista : Button?=null
    var aplicarCambios : Button?=null
    var regresar2 : Button?=null
    var idLista=""
    var tareas=""
    var basedatos = BaseDatos(this, "practica1", null, 1)

    var lista = ArrayList<String>()
    var adapter : ArrayAdapter<String>?=null
    var ids = ArrayList<String>()
    var tarea = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        listViewT = findViewById(R.id.listViewT)
        layoutCamposT = findViewById(R.id.layoutCamposT)
        aplicarCambios = findViewById(R.id.aplicarCambios)
        regresar2 = findViewById(R.id.regresar2)

        nuevaTarea = findViewById(R.id.nuevaTarea)
        eliminarLista = findViewById(R.id.eliminarLista)
        actualizarLista = findViewById(R.id.actualizarLista)
        adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,lista)

        idLista = intent.extras?.getString("idLista").toString()
        tareas = intent.extras?.getString("tareas").toString()
        this.setTitle(tareas)
        buscar(idLista)

        nuevaTarea?.setOnClickListener {
            layoutCamposT?.removeAllViews()
            var descripcion = EditText(this)
            descripcion.setHint("Descripción")
            descripcion.textSize = 20F

            var fecha = EditText(this)
            fecha.inputType = InputType.TYPE_CLASS_DATETIME
            fecha.setHint("Realizado en")
            fecha.textSize = 20F

            var boton = Button(this)
            boton.setText("Registrar tarea")
            boton.textSize = 17F

            var boton2 = Button(this)
            boton2.setText("Cancelar")
            boton2.textSize = 17F

            layoutCamposT?.addView(descripcion)
            layoutCamposT?.addView(fecha)
            layoutCamposT?.addView(boton)
            layoutCamposT?.addView(boton2)

            boton.setOnClickListener {
                if(descripcion.text.toString().isEmpty()||fecha.text.toString().isEmpty()){
                    mensaje("ADVERTENCIA","Por favor, llena todos los campos.")
                }else {
                    insertar(descripcion.text.toString(),fecha.text.toString(),idLista)
                }
            }
            boton2.setOnClickListener{
                layoutCamposT?.removeAllViews()
            }
        }
        actualizarLista?.setOnClickListener{
            layoutCamposT?.removeAllViews()
            var descripcion = EditText(this)
            descripcion.setHint("Descripción")
            descripcion.textSize = 20F

            var fecha = EditText(this)
            fecha.inputType = InputType.TYPE_CLASS_DATETIME
            fecha.setHint("Realizado en")
            fecha.textSize = 20F

            var boton = Button(this)
            boton.setText("Aplicar cambios")
            boton.textSize = 17F

            var boton2 = Button(this)
            boton2.setText("Cancelar")
            boton2.textSize = 17F

            layoutCamposT?.addView(descripcion)
            layoutCamposT?.addView(fecha)
            layoutCamposT?.addView(boton)
            layoutCamposT?.addView(boton2)

            //obtener datos en los campos
            try {
                var transaccion = basedatos.readableDatabase
                var SQL = "SELECT * FROM LISTA WHERE IDLISTA=" + idLista
                var resultado = transaccion.rawQuery(SQL, null)
                if (resultado.moveToFirst()) {
                    descripcion.setText(resultado.getString(1))
                    fecha.setText(resultado.getString(2))
                    println(descripcion)
                    println(fecha)
                }
            }catch(err:  Exception){
                mensaje("ERROR","No se pudieron obtener los datos de la lista.")
            }

            boton.setOnClickListener {
                if(descripcion.text.toString().isEmpty()||fecha.text.toString().isEmpty()){
                    mensaje("ADVERTENCIA","Por favor, llena todos los campos.")
                }else {
                    actualizar(descripcion.text.toString(),fecha.text.toString(),idLista)
                }
            }
            boton2.setOnClickListener{
                layoutCamposT?.removeAllViews()
            }
        }
        eliminarLista?.setOnClickListener {
            AlertDialog.Builder(this).setTitle("ADVERTENCIA").
                setMessage("¿Estás seguro que deseas eliminar la lista?").setPositiveButton("Sí"){ dialog, which->
                eliminar(idLista)
            }.setNeutralButton("No"){dialog,which->
                return@setNeutralButton
            }.show()

        }
        regresar2?.setOnClickListener {
            var activity = Intent(this,MainActivity::class.java)
            startActivity(activity)
        }
        listViewT?.setOnItemClickListener { parent, view, position, id ->
            var idTarea = ids.get(position)
            var tarea = tarea.get(position)
            //Toast.makeText(this, idTarea,Toast.LENGTH_LONG).show()
            var activity = Intent(this,Main3Activity::class.java)
            activity.putExtra("idLista",idLista)
            activity.putExtra("idTarea",idTarea)
            activity.putExtra("tarea",tarea)
            activity.putExtra("tareas",tareas)
            startActivity(activity)

        }
    }

    fun buscar(id:String){
        try {
            listViewT?.adapter = null
            lista.clear()
            ids.clear()
            tarea.clear()
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM TAREAS WHERE IDLISTA=" + id
            var resultado = transaccion.rawQuery(SQL, null)
            var cadena=""
            if(resultado.moveToFirst()){
                cadena = resultado.getString(0) + "      "+ resultado.getString(1) + "      " + resultado.getString(2)
                lista.add(cadena)
                ids.add(resultado.getString(0))
                tarea.add(resultado.getString(1))
            }
            listViewT?.adapter = adapter
        }catch(err: SQLiteException){
            mensaje("ERROR","No se pudieron buscar las tareas.")
        }
    }

    fun insertar(descripcion:String, fecha:String,idLista:String){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO TAREAS VALUES(null,'${descripcion}','${fecha}',${idLista})"
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("ÉXITO","Se registró la tarea correctamente.")
            layoutCamposT?.removeAllViews()
            buscar(idLista)
        }catch(err: SQLiteException){
            mensaje("ERROR","No se pudo insertar la lista.")
        }
    }
    fun actualizar(descripcion:String, fecha:String,idLista:String){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL = "UPDATE LISTA SET DESCRIPCION='"+descripcion+ "', FECHACREACION='"+fecha+ "' WHERE IDLISTA="+idLista
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("ÉXITO","Se actualizó la lista correctamente.")
            layoutCamposT?.removeAllViews()
        }catch(err: SQLiteException){
            mensaje("ERROR","No se pudo actualizar la lista.")
        }
    }
    fun eliminar(idLista:String){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "DELETE FROM TAREAS WHERE IDLISTA="+idLista
            var SQL2 = "DELETE FROM LISTA WHERE IDLISTA="+idLista
            transaccion.execSQL(SQL)
            transaccion.execSQL(SQL2)
            transaccion.close()
            AlertDialog.Builder(this).setTitle("ÉXITO").setMessage("Se eliminó la lista correctamente.").setPositiveButton("Oki"){ dialog, which->
                var activity = Intent(this,MainActivity::class.java)
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
