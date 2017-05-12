package org.kreal.wallpaper.service;

import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lthee on 2017/5/3.
 */

abstract public class GLWallpaperService2 extends WallpaperService {
    class EglHelper{

        public EGLConfig mEglConfig;

        public boolean createSurface() {
            return true;
        }

        public GL createGL() {
            return null;
        }

        public int swap() {
            return 0;
        }

        public void destroySurface() {

        }

        public void finish() {

        }

        public void start() {

        }
    }
    class GLEngine extends Engine{
        private  final GLThreadManager sGLThreadManager = new GLThreadManager();
        private GLThread mGLThread;
        private GLSurfaceView.Renderer mRenderer;
        private boolean mDetached;

        public void setRenderer(GLSurfaceView.Renderer renderer) {
            checkRenderThreadState();
//            if (mEGLConfigChooser == null) {
//                mEGLConfigChooser = new SimpleEGLConfigChooser(true);
//            }
//            if (mEGLContextFactory == null) {
//                mEGLContextFactory = new DefaultContextFactory();
//            }
//            if (mEGLWindowSurfaceFactory == null) {
//                mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
//            }
            mRenderer = renderer;
            mGLThread = new GLThread();
            mGLThread.start();
        }
        private void checkRenderThreadState() {
            if (mGLThread != null) {
                throw new IllegalStateException(
                        "setRenderer has already been called for this instance.");
            }
        }
        class GLThread extends Thread{
            public GLThread() {
                super();
                mWidth = 0;
                mHeight = 0;
                mRequestRender = true;
//                mRenderMode = RENDERMODE_CONTINUOUSLY;
//                mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
            }
            @Override
            public void run() {
                setName("GLThread " + getId());
//                if (LOG_THREADS) {
//                    Log.i("GLThread", "starting tid=" + getId());
//                }

                try {
                    guardedRun();
                } catch (InterruptedException e) {
                    // fall thru and exit normally
                } finally {
                    sGLThreadManager.threadExiting(this);
                }
            }

            /*
             * This private method should only be called inside a
             * synchronized(sGLThreadManager) block.
             */
            private void stopEglSurfaceLocked() {
                if (mHaveEglSurface) {
                    mHaveEglSurface = false;
                    mEglHelper.destroySurface();
                }
            }

            /*
             * This private method should only be called inside a
             * synchronized(sGLThreadManager) block.
             */
            private void stopEglContextLocked() {
                if (mHaveEglContext) {
                    mEglHelper.finish();
                    mHaveEglContext = false;
                    sGLThreadManager.releaseEglContextLocked(this);
                }
            }
            private void guardedRun() throws InterruptedException {
//                mEglHelper = new EglHelper(mGLSurfaceViewWeakRef);
                mHaveEglContext = false;
                mHaveEglSurface = false;
                try {
                    GL10 gl = null;
                    boolean createEglContext = false;
                    boolean createEglSurface = false;
                    boolean createGlInterface = false;
                    boolean lostEglContext = false;
                    boolean sizeChanged = false;
                    boolean wantRenderNotification = false;
                    boolean doRenderNotification = false;
                    boolean askedToReleaseEglContext = false;
                    int w = 0;
                    int h = 0;
                    Runnable event = null;
                    while (true){
                        synchronized (sGLThreadManager) {
                            while (true){
                                if (mShouldExit) {
                                    return;
                                }
                                if (! mEventQueue.isEmpty()) {
                                    event = mEventQueue.remove(0);
                                    break;
                                }


                                // Update the pause state.
                                boolean pausing = false;
                                if (mPaused != mRequestPaused) {
                                    pausing = mRequestPaused;
                                    mPaused = mRequestPaused;
                                    sGLThreadManager.notifyAll();
//                                    if (LOG_PAUSE_RESUME) {
//                                        Log.i("GLThread", "mPaused is now " + mPaused + " tid=" + getId());
//                                    }
                                }


                                // Do we need to give up the EGL context?
                                if (mShouldReleaseEglContext) {
//                                    if (LOG_SURFACE) {
//                                        Log.i("GLThread", "releasing EGL context because asked to tid=" + getId());
//                                    }
                                    stopEglSurfaceLocked();
                                    stopEglContextLocked();
                                    mShouldReleaseEglContext = false;
                                    askedToReleaseEglContext = true;
                                }

                                // Have we lost the EGL context?
                                if (lostEglContext) {
                                    stopEglSurfaceLocked();
                                    stopEglContextLocked();
                                    lostEglContext = false;
                                }

                                // When pausing, release the EGL surface:
                                if (pausing && mHaveEglSurface) {
//                                    if (LOG_SURFACE) {
//                                        Log.i("GLThread", "releasing EGL surface because paused tid=" + getId());
//                                    }
                                    stopEglSurfaceLocked();
                                }

                                // When pausing, optionally release the EGL Context:
                                if (pausing && mHaveEglContext) {
//                                    GLSurfaceView view = mGLSurfaceViewWeakRef.get();
//                                    boolean preserveEglContextOnPause = view == null ?
//                                            false : view.mPreserveEGLContextOnPause;
//                                    if (!preserveEglContextOnPause || sGLThreadManager.shouldReleaseEGLContextWhenPausing()) {
//                                        stopEglContextLocked();
//                                        if (LOG_SURFACE) {
//                                            Log.i("GLThread", "releasing EGL context because paused tid=" + getId());
//                                        }
//                                    }
                                }

                                // When pausing, optionally terminate EGL:
                                if (pausing) {
                                    if (sGLThreadManager.shouldTerminateEGLWhenPausing()) {
                                        mEglHelper.finish();
//                                        if (LOG_SURFACE) {
//                                            Log.i("GLThread", "terminating EGL because paused tid=" + getId());
//                                        }
                                    }
                                }
                                // Have we lost the SurfaceView surface?
                                if ((! mHasSurface) && (! mWaitingForSurface)) {
//                                    if (LOG_SURFACE) {
//                                        Log.i("GLThread", "noticed surfaceView surface lost tid=" + getId());
//                                    }
                                    if (mHaveEglSurface) {
                                        stopEglSurfaceLocked();
                                    }
                                    mWaitingForSurface = true;
                                    mSurfaceIsBad = false;
                                    sGLThreadManager.notifyAll();
                                }

                                // Have we acquired the surface view surface?
                                if (mHasSurface && mWaitingForSurface) {
//                                    if (LOG_SURFACE) {
//                                        Log.i("GLThread", "noticed surfaceView surface acquired tid=" + getId());
//                                    }
                                    mWaitingForSurface = false;
                                    sGLThreadManager.notifyAll();
                                }

                                if (doRenderNotification) {
//                                    if (LOG_SURFACE) {
//                                        Log.i("GLThread", "sending render notification tid=" + getId());
//                                    }
                                    wantRenderNotification = false;
                                    doRenderNotification = false;
                                    mRenderComplete = true;
                                    sGLThreadManager.notifyAll();
                                }

                                // Ready to draw?
                                if (readyToDraw()) {
                                    // If we don't have an EGL context, try to acquire one.
                                    if (! mHaveEglContext) {
                                        if (askedToReleaseEglContext) {
                                            askedToReleaseEglContext = false;
                                        } else if (sGLThreadManager.tryAcquireEglContextLocked(this)) {
                                            try {
                                                mEglHelper.start();
                                            } catch (RuntimeException t) {
                                                sGLThreadManager.releaseEglContextLocked(this);
                                                throw t;
                                            }
                                            mHaveEglContext = true;
                                            createEglContext = true;

                                            sGLThreadManager.notifyAll();
                                        }
                                    }

                                    if (mHaveEglContext && !mHaveEglSurface) {
                                        mHaveEglSurface = true;
                                        createEglSurface = true;
                                        createGlInterface = true;
                                        sizeChanged = true;
                                    }

                                    if (mHaveEglSurface) {
                                        if (mSizeChanged) {
                                            sizeChanged = true;
                                            w = mWidth;
                                            h = mHeight;
                                            wantRenderNotification = true;
//                                            if (LOG_SURFACE) {
//                                                Log.i("GLThread",
//                                                        "noticing that we want render notification tid="
//                                                                + getId());
//                                            }
                                            // Destroy and recreate the EGL surface.
                                            createEglSurface = true;

                                            mSizeChanged = false;
                                        }
                                        mRequestRender = false;
                                        sGLThreadManager.notifyAll();
                                        break;
                                    }
                                }

                                // By design, this is the only place in a GLThread thread where we wait().
//                                if (LOG_THREADS) {
//                                    Log.i("GLThread", "waiting tid=" + getId()
//                                            + " mHaveEglContext: " + mHaveEglContext
//                                            + " mHaveEglSurface: " + mHaveEglSurface
//                                            + " mFinishedCreatingEglSurface: " + mFinishedCreatingEglSurface
//                                            + " mPaused: " + mPaused
//                                            + " mHasSurface: " + mHasSurface
//                                            + " mSurfaceIsBad: " + mSurfaceIsBad
//                                            + " mWaitingForSurface: " + mWaitingForSurface
//                                            + " mWidth: " + mWidth
//                                            + " mHeight: " + mHeight
//                                            + " mRequestRender: " + mRequestRender
//                                            + " mRenderMode: " + mRenderMode);
//                                }
                                sGLThreadManager.wait();
                            }
                        }// end of synchronized(sGLThreadManager)

                        if (event != null) {
                            event.run();
                            event = null;
                            continue;
                        }
                        if (createEglSurface) {
//                            if (LOG_SURFACE) {
//                                Log.w("GLThread", "egl createSurface");
//                            }
                            if (mEglHelper.createSurface()) {
                                synchronized(sGLThreadManager) {
                                    mFinishedCreatingEglSurface = true;
                                    sGLThreadManager.notifyAll();
                                }
                            } else {
                                synchronized(sGLThreadManager) {
                                    mFinishedCreatingEglSurface = true;
                                    mSurfaceIsBad = true;
                                    sGLThreadManager.notifyAll();
                                }
                                continue;
                            }
                            createEglSurface = false;
                        }

                        if (createGlInterface) {
                            gl = (GL10) mEglHelper.createGL();

                            sGLThreadManager.checkGLDriver(gl);
                            createGlInterface = false;
                        }

                        if (createEglContext) {
//                            if (LOG_RENDERER) {
//                                Log.w("GLThread", "onSurfaceCreated");
//                            }
//                            GLSurfaceView view = mGLSurfaceViewWeakRef.get();
                            if (mRenderer != null) {
                               mRenderer.onSurfaceCreated(gl, mEglHelper.mEglConfig);
                            }
                            createEglContext = false;
                        }

                        if (sizeChanged) {
//                            if (LOG_RENDERER) {
//                                Log.w("GLThread", "onSurfaceChanged(" + w + ", " + h + ")");
//                            }
//                            GLSurfaceView view = mGLSurfaceViewWeakRef.get();
                            if (mRenderer != null) {
                                mRenderer.onSurfaceChanged(gl, w, h);
                            }
                            sizeChanged = false;
                        }
//                        if (LOG_RENDERER_DRAW_FRAME) {
//                            Log.w("GLThread", "onDrawFrame tid=" + getId());
//                        }
                        {
//                            GLSurfaceView view = mGLSurfaceViewWeakRef.get();
                            if (mRenderer != null) {
                                mRenderer.onDrawFrame(gl);
                            }
                        }

                        int swapError = mEglHelper.swap();
                        switch (swapError) {
                            case EGL10.EGL_SUCCESS:
                                break;
                            case EGL11.EGL_CONTEXT_LOST:
//                                if (LOG_SURFACE) {
//                                    Log.i("GLThread", "egl context lost tid=" + getId());
//                                }
                                lostEglContext = true;
                                break;
                            default:
                                // Other errors typically mean that the current surface is bad,
                                // probably because the SurfaceView surface has been destroyed,
                                // but we haven't been notified yet.
                                // Log the error to help developers understand why rendering stopped.
//                                EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", swapError);

                                synchronized(sGLThreadManager) {
                                    mSurfaceIsBad = true;
                                    sGLThreadManager.notifyAll();
                                }
                                break;
                        }
                        if (wantRenderNotification) {
                            doRenderNotification = true;
                        }
                    }
                }finally {
                    synchronized (sGLThreadManager) {
                        stopEglSurfaceLocked();
                        stopEglContextLocked();
                    }
                }
            }

            private boolean readyToDraw() {
                return (!mPaused) && mHasSurface && (!mSurfaceIsBad)
                        && (mWidth > 0) && (mHeight > 0);
//                        && (mRequestRender || (mRenderMode == RENDERMODE_CONTINUOUSLY));
            }

            // Once the thread is started, all accesses to the following member
            // variables are protected by the sGLThreadManager monitor
            private boolean mShouldExit;
            private boolean mExited;
            private boolean mRequestPaused;
            private boolean mPaused;
            private boolean mHasSurface;
            private boolean mSurfaceIsBad;
            private boolean mWaitingForSurface;
            private boolean mHaveEglContext;
            private boolean mHaveEglSurface;
            private boolean mFinishedCreatingEglSurface;
            private boolean mShouldReleaseEglContext;
            private int mWidth;
            private int mHeight;
            private int mRenderMode;
            private boolean mRequestRender;
            private boolean mRenderComplete;
            private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();
            private boolean mSizeChanged = true;

            // End of member variables protected by the sGLThreadManager monitor.

            private EglHelper mEglHelper;

            /**
             * Set once at thread construction time, nulled out when the parent view is garbage
             * called. This weak reference allows the GLSurfaceView to be garbage collected while
             * the GLThread is still alive.
             */
            private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;

            public void requestReleaseEglContextLocked() {
            }
        }

        private class GLThreadManager {
            private  String TAG = "GLThreadManager";

            public synchronized void threadExiting(GLThread thread) {
//                if (LOG_THREADS) {
//                    Log.i("GLThread", "exiting tid=" +  thread.getId());
//                }
                thread.mExited = true;
                if (mEglOwner == thread) {
                    mEglOwner = null;
                }
                notifyAll();
            }

            /*
             * Tries once to acquire the right to use an EGL
             * context. Does not block. Requires that we are already
             * in the sGLThreadManager monitor when this is called.
             *
             * @return true if the right to use an EGL context was acquired.
             */
            public boolean tryAcquireEglContextLocked(GLThread thread) {
                if (mEglOwner == thread || mEglOwner == null) {
                    mEglOwner = thread;
                    notifyAll();
                    return true;
                }
                checkGLESVersion();
                if (mMultipleGLESContextsAllowed) {
                    return true;
                }
                // Notify the owning thread that it should release the context.
                // TODO: implement a fairness policy. Currently
                // if the owning thread is drawing continuously it will just
                // reacquire the EGL context.
                if (mEglOwner != null) {
                    mEglOwner.requestReleaseEglContextLocked();
                }
                return false;
            }

            /*
             * Releases the EGL context. Requires that we are already in the
             * sGLThreadManager monitor when this is called.
             */
            public void releaseEglContextLocked(GLThread thread) {
                if (mEglOwner == thread) {
                    mEglOwner = null;
                }
                notifyAll();
            }

            public synchronized boolean shouldReleaseEGLContextWhenPausing() {
                // Release the EGL context when pausing even if
                // the hardware supports multiple EGL contexts.
                // Otherwise the device could run out of EGL contexts.
                return mLimitedGLESContexts;
            }

            public synchronized boolean shouldTerminateEGLWhenPausing() {
                checkGLESVersion();
                return !mMultipleGLESContextsAllowed;
            }

            public synchronized void checkGLDriver(GL10 gl) {
                if (! mGLESDriverCheckComplete) {
                    checkGLESVersion();
                    String renderer = gl.glGetString(GL10.GL_RENDERER);
                    if (mGLESVersion < kGLES_20) {
                        mMultipleGLESContextsAllowed =
                                ! renderer.startsWith(kMSM7K_RENDERER_PREFIX);
                        notifyAll();
                    }
                    mLimitedGLESContexts = !mMultipleGLESContextsAllowed;
//                    if (LOG_SURFACE) {
//                        Log.w(TAG, "checkGLDriver renderer = \"" + renderer + "\" multipleContextsAllowed = "
//                                + mMultipleGLESContextsAllowed
//                                + " mLimitedGLESContexts = " + mLimitedGLESContexts);
//                    }
                    mGLESDriverCheckComplete = true;
                }
            }

            private void checkGLESVersion() {
                if (! mGLESVersionCheckComplete) {
//                    mGLESVersion = SystemProperties.getInt(
//                            "ro.opengles.version",
//                            ConfigurationInfo.GL_ES_VERSION_UNDEFINED);
                    if (mGLESVersion >= kGLES_20) {
                        mMultipleGLESContextsAllowed = true;
                    }
//                    if (LOG_SURFACE) {
//                        Log.w(TAG, "checkGLESVersion mGLESVersion =" +
//                                " " + mGLESVersion + " mMultipleGLESContextsAllowed = " + mMultipleGLESContextsAllowed);
//                    }
                    mGLESVersionCheckComplete = true;
                }
            }

            /**
             * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides
             * support for hardware-accelerated views, therefore multiple EGL contexts are
             * supported on all Android 3.0+ EGL drivers.
             */
            private boolean mGLESVersionCheckComplete;
            private int mGLESVersion;
            private boolean mGLESDriverCheckComplete;
            private boolean mMultipleGLESContextsAllowed;
            private boolean mLimitedGLESContexts;
            private static final int kGLES_20 = 0x20000;
            private static final String kMSM7K_RENDERER_PREFIX =
                    "Q3Dimension MSM7500 ";
            private GLThread mEglOwner;
        }
    }
}
