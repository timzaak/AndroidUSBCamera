import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.callback.IDeviceConnectCallBack
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.demo.R

//import com.jiangdg.usb.USBMonitor.UsbControlBlock: Unresolved reference: usb, so use USBManager to do that.


class MultiCameraWithoutUIFragment : Fragment(R.layout.fragment_multi_camera_no_ui) {

    companion object {
        val TAG = "MultiCameraWithoutUI"
    }
    private lateinit var mCameraClient: MultiCameraClient
    private lateinit var mCamera:MultiCameraClient.Camera

    private lateinit var usbManager:UsbManager
    fun isSupportDevice(usbDevice: UsbDevice): Boolean {
        return true
        //return usbDevice.productId == 25453
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCameraClient = MultiCameraClient(requireContext(), null)
        mCameraClient.register()
        usbManager = requireActivity().getSystemService(Context.USB_SERVICE)  as UsbManager

        val button = view.findViewById<Button>(R.id.button_test)
        button.setOnClickListener {
            val usbDevice = usbManager.deviceList.firstNotNullOfOrNull { (deviceId, device) ->
                if(isSupportDevice(device)) {
                     device
                }else {
                    null
                }
            }
            if(usbDevice!=null) {
                if(!usbManager.hasPermission(usbDevice)) {
                    mCameraClient.requestPermission(usbDevice)
                } else {
                    mCamera = MultiCameraClient.Camera(requireContext(), usbDevice)

                    mCamera.openCamera(null,CameraRequest.Builder()
                        .setFrontCamera(false)
                        .setPreviewWidth(2320)
                        .setPreviewHeight(1744)
                        .create()
                    )

                    Thread.sleep(2*1000)
                    if(mCamera.isCameraOpened()) {
                        mCamera.captureImage(object: ICaptureCallBack{
                            override fun onBegin() {
                                Log.i(TAG, "CaptureImage Begin")
                            }

                            override fun onComplete(path: String?) {
                                Log.i(TAG, "CaptureImage Complete:${path}")
                            }

                            override fun onError(error: String?) {
                                Log.i(TAG, "CaptureImage Error:${error}")
                            }
                        },null)
                    }
                }
            }
        }

    }



    fun initCamera() {

//        mCameraClient = MultiCameraClient(requireContext(), object : IDeviceConnectCallBack {
//            override fun onAttachDev(device: UsbDevice?) {
//                device ?: return;
//                if (isSupportDevice(device)) {
//                    context?.let {
//                        mCamera = MultiCameraClient.Camera(it, device).apply {
//                            if (mCameraClient.hasPermission(device) != true) {
//                                mCameraClient.requestPermission(device)
//                            }
//                        }
//
//                    }
//
//                }
//
//            }
//
//            override fun onDetachDec(device: UsbDevice?) {
//
//            }
//
//            override fun onConnectDev(device: UsbDevice?, ctrlBlock: com.jiangdg.usb.USBMonitor.UsbControlBlock?) {
//
//            }
//
//            override fun onDisConnectDec(device: UsbDevice?, ctrlBlock: com.jiangdg.usb.USBMonitor.UsbControlBlock?) {
//
//            }
//
//            override fun onCancelDev(device: UsbDevice?) {
//            }
//        })
    }
}