package pe.edu.upeu.asistenciaupeujcn.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materiales")
data class Materialesx(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var cui: String,
    var tipoCui: String,
    var materEntre: String,
    var fecha: String,
    var horaReg: String,
    var latituda: String,
    var longituda: String,
    var modFh: String,
    var offlinex: String,
    var actividadId: Long,
)