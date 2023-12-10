package com.aswin.excerssitant.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aswin.excerssitant.R

import com.aswin.excerssitant.ml.LiteModelMovenetSingleposeLightningTfliteFloat164
import com.aswin.excerssitant.utils.Counter

import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.w3c.dom.ProcessingInstruction
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2


class MainActivity : AppCompatActivity() {
    val fpsRange = Range<Int>(30,30)
    var reps = 0
    var flag = "up"
    var id = 0
    var start: Boolean = false
    lateinit var sharedPreferences: SharedPreferences;
    lateinit var flip : ImageView
    lateinit var counter: Counter
    lateinit var imageProcessor: ImageProcessor
    lateinit var model : LiteModelMovenetSingleposeLightningTfliteFloat164
    lateinit var bitmap: Bitmap
    lateinit var imageView: ImageView
    lateinit var handler: Handler
    lateinit var repCounter:TextView
    lateinit var handlerThread: HandlerThread
    lateinit var textureView: TextureView
    lateinit var cameraManager: CameraManager
    lateinit var button : Button
    var paint: Paint = Paint()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermissions()

        paint.setColor(Color.CYAN)
        button = findViewById(R.id.button)
        repCounter =findViewById(R.id.nose)
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(192, 192, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = LiteModelMovenetSingleposeLightningTfliteFloat164.newInstance(this@MainActivity)
        textureView = findViewById(R.id.texture_view)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        handlerThread  = HandlerThread("video_thread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        imageView = findViewById(R.id.image_view)
        sharedPreferences = this@MainActivity.getSharedPreferences(getString(R.string.data),Context.MODE_PRIVATE)



        transformTexture()
        textureView.rotation = 90f

        button.setOnClickListener {
            if(start){
                start = false
                button.text = "START"
                val count = sharedPreferences.getInt("reps",0) + reps;
                sharedPreferences.edit().putInt("reps",count).commit();
                Toast.makeText(this@MainActivity as Context,"Saved",Toast.LENGTH_SHORT).show()
                finish()

            }
            else{
                start = true
                button.text = "STOP"
            }
        }
        textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_Camera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {

            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                if(start) {
                    bitmap = textureView.bitmap!!
                    var tensorImage = TensorImage(DataType.UINT8)
                    tensorImage.load(bitmap)
                    tensorImage = imageProcessor.process(tensorImage)

                    val inputFeature0 =
                        TensorBuffer.createFixedSize(intArrayOf(1, 192, 192, 3), DataType.UINT8)
                    inputFeature0.loadBuffer(tensorImage.buffer)

                    val outputs = model.process(inputFeature0)
                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                    var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    var canvas = Canvas(mutable)
                    var h = bitmap.height
                    var w = bitmap.width
                    var x = 0

                    while (x <= 49) {
                        if (outputFeature0.get(x + 2) > 0.45) {
                            canvas.drawCircle(
                                (outputFeature0.get(x + 1) * w),
                                (outputFeature0.get(x) * h),
                                10f,
                                paint
                            )
                            Log.d(
                                "X and Y",
                                "${outputFeature0.get(x + 1) * w} , ${outputFeature0.get(x) * h}"
                            )
                            var angle_left = calculate_angle(15, 21, 27, outputFeature0)
                            var angle_right = calculate_angle(18, 24, 30, outputFeature0)
                            var angle = (angle_left + angle_right) / 2
                            if (angle > 0)
                                if (angle < 90) {
                                    flag = "down"

                                }
                            if (angle > 120 && flag == "down") {
                                reps++

                                flag = "up"
                                repCounter.text = "REPS " + reps.toString()

                            }

                        }
                        x += 3
                    }
                    //if(outputFeature0.get(0+2) > 0.5) {
                    // nose.text = "reps = ${reps}  coords = ${outputFeature0.get(0) * h} $flag"
                    //}
                    imageView.setImageBitmap(mutable)
                }


            }

        }


    }


    @SuppressLint("MissingPermission")
    fun open_Camera(){
        cameraManager.openCamera(cameraManager.cameraIdList[1],object : CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                var captureRequest = p0.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                //captureRequest.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,fpsRange)
                var surfaceTexture = textureView.surfaceTexture

                if (surfaceTexture != null) {
                    //surfaceTexture.setDefaultBufferSize(textureView.width,textureView.height)
                }
               // var orientation = resources.configuration.orientation

                var surface = Surface(surfaceTexture )
                captureRequest.addTarget(surface)
                p0.createCaptureSession(listOf(surface),object:CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(),null,null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                        Log.d("Error","Line 148")
                    }

                },handler )
            }

            override fun onDisconnected(p0: CameraDevice) {

            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }

        }, handler)
    }

     fun getPermissions() {
        if(checkSelfPermission(android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]!=PackageManager.PERMISSION_GRANTED)getPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }
    fun calculate_angle(start:Int, middle:Int, end:Int, output:FloatArray):Float{
        var radian = atan2(output.get(end+1)-output.get(middle+1),output.get(end)-output.get(middle)) -
                atan2(output.get(start+1)-output.get(middle+1),output.get(start)-output.get(middle))
        var angle = abs(radian*180/ PI)
        if(angle>180){
            angle = angle -360
        }
        return abs(angle).toFloat()
    }
    private fun transformTexture() {
        val adjustment = Matrix()
        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        val scalex = textureView.width.toFloat() / textureView.height.toFloat()
        val scaley = textureView.height.toFloat() / textureView.width.toFloat()

        adjustment.postRotate(90F, centerX, centerY)
        adjustment.postScale(scalex, scaley, centerX, centerY)

        textureView.setTransform(adjustment)
    }
}

