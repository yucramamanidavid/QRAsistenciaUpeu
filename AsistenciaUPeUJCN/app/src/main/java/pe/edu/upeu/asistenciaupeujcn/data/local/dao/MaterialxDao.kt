package pe.edu.upeu.asistenciaupeujcn.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pe.edu.upeu.asistenciaupeujcn.modelo.Actividad
import pe.edu.upeu.asistenciaupeujcn.modelo.Materialesx

@Dao
interface MaterialxDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarActividad(actividad: Materialesx)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarActividades(actividad: List<Materialesx>)

    @Update
    suspend fun modificarActividad(actividad: Materialesx)

    @Delete
    suspend fun eliminarActividad(actividad: Materialesx)

    @Query("select * from materiales")
    fun reportatActividad(): LiveData<List<Materialesx>>

    @Query("select * from materiales where id=:idx")
    fun buscarActividad(idx: Long): LiveData<Materialesx>
}