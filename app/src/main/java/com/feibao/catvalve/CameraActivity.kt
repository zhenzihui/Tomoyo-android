package com.feibao.catvalve
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feibao.catvalve.databinding.ActivityValveBinding
import com.feibao.catvalve.util.AnalyzerUtil
import com.feibao.catvalve.util.CameraUtil

class CameraActivity : AppCompatActivity() {
    private var _binding: ActivityValveBinding? = null
    private val binding get() = _binding!!


    private var _ia: ImageAnalysis? = null
    private val imageAnalysis get() = _ia!!

    private var _cs :CameraUtil? = null
    private val cameraUtil get() = _cs!!


    private val vm: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityValveBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _ia = AnalyzerUtil(this).initAnalysis { labels ->
            // analyze images here
        }

        _cs = CameraUtil(this, binding.cameraPreview, imageAnalysis)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 11)
        } else {
            cameraUtil.startCamera(vm.currentCameraSelector.value!!)
        }

        vm.currentCameraSelector.observe(this) {
            cameraUtil.startCamera(it)
        }

        binding.switchCameraButton.setOnClickListener {
            if(vm.currentCameraSelector.value==CameraSelector.DEFAULT_FRONT_CAMERA) {
                vm.currentCameraSelector.value = CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                vm.currentCameraSelector.value = CameraSelector.DEFAULT_FRONT_CAMERA
            }
        }





    }


}

class MainActivityViewModel: ViewModel() {
    val currentCameraSelector = MutableLiveData<CameraSelector>().apply { value = CameraSelector.DEFAULT_FRONT_CAMERA }
}