package com.leia.cnsdkgettingstartedgl;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.leia.sdk.LeiaSDK;
import com.leia.sdk.views.InputViewsAsset;
import com.leia.sdk.views.InterlacedSurfaceView;
import com.leia.core.LogLevel;

public class MainActivity extends AppCompatActivity implements LeiaSDK.Delegate {

    private String LogTag = "CNSDKGettingStartedGL";
    InterlacedSurfaceView interlacedView = null;
    private LeiaSDK leiaSDK = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Leia SDK.
        try {
            LeiaSDK.InitArgs initArgs = new LeiaSDK.InitArgs();
            initArgs.delegate = this;
            initArgs.platform.logLevel = LogLevel.Trace;
            initArgs.platform.context = getApplicationContext();
            initArgs.faceTrackingServerLogLevel = LogLevel.Trace;
            initArgs.enableFaceTracking = true;
            leiaSDK = LeiaSDK.createSDK(initArgs);
        } catch (Exception e) {
            Log.e(LogTag, String.format("Failed to initialize LeiaSDK: %s", e.toString()));
            e.printStackTrace();
        }

        // Get interlaced view and set input asset containing a sample stereo 3d image.
        interlacedView = findViewById(R.id.interlacedView);
        InputViewsAsset viewsAsset = new InputViewsAsset();
        viewsAsset.LoadBitmapFromPathIntoSurface("image_0.jpg", this, null);
        interlacedView.setViewAsset(viewsAsset);
    }

    public void didInitialize(LeiaSDK leiaSDK) {
        this.leiaSDK = leiaSDK;
        leiaSDK.enableBacklight(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        interlacedView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        interlacedView.onPause();
    }

    public void onFaceTrackingStarted(LeiaSDK leiaSDK) {
        Log.i(LogTag, "onFaceTrackingStarted");
    }

    public void onFaceTrackingStopped(LeiaSDK leiaSDK) {
        Log.i(LogTag, "onFaceTrackingStopped");
    }

    public void onFaceTrackingFatalError(LeiaSDK leiaSDK) {
        LeiaSDK.FaceTrackingFatalError fatalError = leiaSDK.isFaceTrackingInFatalError();
        if (fatalError != null)
            Log.e(LogTag, String.format("Face tracking fatal error: %s (%d)", fatalError.message, fatalError.code));
    }
}