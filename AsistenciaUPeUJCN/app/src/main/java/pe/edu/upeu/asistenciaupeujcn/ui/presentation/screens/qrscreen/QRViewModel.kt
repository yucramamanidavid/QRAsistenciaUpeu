package pe.edu.upeu.asistenciaupeujcn.ui.presentation.screens.qrscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pe.edu.upeu.asistenciaupeujcn.modelo.Asistenciax
import pe.edu.upeu.asistenciaupeujcn.repository.AsistenciaxRepository
import pe.edu.upeu.asistenciaupeujcn.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class QRViewModel @Inject constructor(
    private val asisRepo: AsistenciaxRepository
) : ViewModel() {
    private val _detectedBarcodes = MutableLiveData<List<Barcode>>()
    val detectedBarcodes: LiveData<List<Barcode>> = _detectedBarcodes
    private val _imageWidth = MutableLiveData<Int>()
    val imageWidth: LiveData<Int> = _imageWidth
    private val _imageHeight = MutableLiveData<Int>()
    val imageHeight: LiveData<Int> = _imageHeight
    private var isProcessing = false
    private var lastProcessedBarcode: String? = null
    private val _insertStatus = MutableLiveData<Boolean>()
    val insertStatus: LiveData<Boolean> = _insertStatus
    var asisTO = Asistenciax.createEmpty()
    fun updateBarcodes(barcodes: List<Barcode>, width: Int, height: Int)
    {
        if (isProcessing) return
        _detectedBarcodes.value = barcodes
        _imageWidth.value = width
        _imageHeight.value = height
        posicionObtencion()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (barcodes.size == 1) {
                    val dato = barcodes[0].displayValue
                    if (dato != null && dato != lastProcessedBarcode) {
                        isProcessing = true
                        asisTO.fecha="2024-09-14"
                        asisTO.horaReg="14:30"
                        asisTO.tipo="inspección"
                        asisTO.calificacion=5
                        asisTO.cui=dato
                        asisTO.tipoCui="C.Universitario"
                        asisTO.actividadId=TokenUtils.ID_ASIS_ACT
                        asisTO.subactasisId=0
                        asisTO.offlinex="NO"
                        asisTO.entsal="E"
                        if ((dato.length == 8 || dato.length == 9)){
                            val success =
                                asisRepo.insertarAsistenciax(asisTO)
                            if (success) {
                                lastProcessedBarcode = dato
                                _insertStatus.postValue(true)
                                resetLastProcessedBarcode()
                            }else {
                                _insertStatus.postValue(false)
                            } }
                        else{
                            _insertStatus.postValue(false)
                        }
                        isProcessing = false // Liberar el procesamiento
                        _detectedBarcodes.postValue(emptyList())
                    }
                }
            }catch (e:Exception){
                Log.i("ERRRRR", "${e.message}")
            }
        }}
    private fun resetLastProcessedBarcode() {
        viewModelScope.launch {
            delay(5000) // Espera de 5 segundos
            lastProcessedBarcode = null
        }
    }

    @SuppressLint("MissingPermission")
    fun posicionObtencion() {
        var context: Context= TokenUtils.CONTEXTO_APPX
        var locationCallback: LocationCallback? = null
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                if (p0.locations.isNotEmpty()) {
                    val location = p0.locations.last()
                    asisTO.latituda=(location.latitude.toString())
                    asisTO.longituda=(location.longitude.toString())
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }
        viewModelScope.launch {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
// Inicia las actualizaciones de ubicación
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper())
        }
    }
}