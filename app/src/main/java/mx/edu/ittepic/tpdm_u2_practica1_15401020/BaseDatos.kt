package mx.edu.ittepic.tpdm_u2_practica1_15401020

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
        context: Context,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ): SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE LISTA (IDLISTA INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPCION VARCHAR(400), FECHACREACION DATE)")
        db?.execSQL("CREATE TABLE TAREAS (IDTAREA INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPCION VARCHAR(400), REALIZADO DATE, IDLISTA INTEGER, FOREIGN KEY (IDLISTA) REFERENCES LISTA(IDLISTA) )")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}