package com.vinetech.wezone.Common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.vinetech.imagecrop.BitmapManager;
import com.vinetech.imagecrop.HighlightView;
import com.vinetech.imagecrop.MonitoredActivity;
import com.vinetech.imagecrop.Util;
import com.vinetech.util.FileCache;
import com.vinetech.util.UIBitmap;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.ShareApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author chokiyeon
 *
 */
@SuppressLint("NewApi")
public class Activity_ImageCrop extends MonitoredActivity implements
View.OnClickListener
{
    final int IMAGE_MAX_SIZE = 1024;

    public static final String INTENTKEY_PICKERIMAGE_POSITION = "pickerimage_position";
    //public static final  String IMAGE_PATH             = "image-path";
    public static final String SCALE                  = "scale";
    public static final String ORIENTATION_IN_DEGREES = "orientation_in_degrees";
    public static final String ASPECT_X               = "aspectX";
    public static final String ASPECT_Y               = "aspectY";
    public static final String OUTPUT_X               = "outputX";
    public static final String OUTPUT_Y               = "outputY";
    public static final String SCALE_UP_IF_NEEDED     = "scaleUpIfNeeded";
    public static final String CIRCLE_CROP            = "circleCrop";
    public static final String RETURN_DATA            = "return-data";
    public static final String RETURN_DATA_AS_BITMAP  = "data";
    public static final String ACTION_INLINE_DATA     = "inline-data";

    //private       Bitmap.CompressFormat mOutputFormat    = Bitmap.CompressFormat.JPEG;
    //private       boolean               mDoFaceDetection = false;
    
    private Data_PhotoPickerImage mPhotoPickerImage;
    
    private Uri mSaveUri;
    private       boolean               mCircleCrop;
    private final Handler mHandler         = new Handler();

    private int				mOriginWidth, mOriginHeight;
    private int				mImageWidth, mImageHeight;
    
    private int             mAspectX;
    private int             mAspectY;

    private CropImageView   mCropImageView;
    private ContentResolver mContentResolver;
    private Bitmap mBitmap;
    private String mImagePath;

    public boolean       mWaitingToPick; // Whether we are wait the user to pick a face.
    public boolean       mSaving;  // Whether the "save" button is already clicked.
    public HighlightView mCropHighlightView;

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
//    private boolean mScaleUp = true;
//    
//    private int             mOutputX;
//    private int             mOutputY;
//    private boolean         mScale;
    

    private final BitmapManager.ThreadSet mDecodingThreads = new BitmapManager.ThreadSet();
    
    private ShareApplication m_Share;

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        
        mContentResolver = getContentResolver();
        
        m_Share 	      = (ShareApplication)getApplication();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_crop);


        mCropImageView = (CropImageView)findViewById(R.id.imagecrop_cropimgview);
        findViewById(R.id.imagecrop_bottom_ok_textview).setOnClickListener(this);
        findViewById(R.id.imagecrop_bottom_cancel_textview).setOnClickListener(this);

        //showStorageToast(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) 
        {
    		int position = extras.getInt(INTENTKEY_PICKERIMAGE_POSITION, 0);
    		
    		ArrayList<Data_PhotoPickerImage> photoPickerImages = ((ShareApplication)getApplication()).getPhotoPickerImages();
    		mPhotoPickerImage = photoPickerImages.get(position);

            if (extras.getString(CIRCLE_CROP) != null) 
            {
	        	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
	        	{
	        		mCropImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	        	}

                mCircleCrop = true;
                mAspectX = 1;
                mAspectY = 1;
            }

            //mImagePath = extras.getString(IMAGE_PATH);
            mImagePath = mPhotoPickerImage.path;
            //SDSLog.d(SDSLog.TAGIDX_CKY,, "mPhotoPickerImage.path="+mPhotoPickerImage.path);

            mSaveUri = getImageUri(mImagePath);
            mBitmap = loadBitmap(mImagePath);
            if( mBitmap != null )
            {
            	mImageWidth = mBitmap.getWidth();
            	mImageHeight= mBitmap.getHeight();
            }

            if (extras.containsKey(ASPECT_X) && extras.get(ASPECT_X) instanceof Integer) {

                mAspectX = extras.getInt(ASPECT_X);
            } 
            else 
            {
            	throw new IllegalArgumentException("aspect_x must be integer");
            }
            
            if (extras.containsKey(ASPECT_Y) && extras.get(ASPECT_Y) instanceof Integer)
            {
                mAspectY = extras.getInt(ASPECT_Y);
            } 
            else 
            {
                throw new IllegalArgumentException("aspect_y must be integer");
            }
            
            /*
            mOutputX = extras.getInt(OUTPUT_X);
            mOutputY = extras.getInt(OUTPUT_Y);
            mScale = false;//extras.getBoolean(SCALE, true);
            mScaleUp = false;//extras.getBoolean(SCALE_UP_IF_NEEDED, true);
            */
        }

        if (mBitmap == null) 
        {
            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /*
        findViewById(R.id.discard).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

        findViewById(R.id.save).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        try {
                            onSaveClicked();
                        } catch (Exception e) {
                            finish();
                        }
                    }
                });
        findViewById(R.id.rotateLeft).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                    	mImageView.setHiddenHighlightViews(true);
                        mBitmap = Util.rotateImage(mBitmap, -90);

                        startFaceDetection();
                        
//                        RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
//                        mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
                        //mRunFaceDetection.run();
                    }
                });

        findViewById(R.id.rotateRight).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                    	mImageView.setHiddenHighlightViews(true);
                        mBitmap = Util.rotateImage(mBitmap, 90);
                        
//                        RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
//                        mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
//                        mRunFaceDetection.run();
                        startFaceDetection();
                        
                        
//                        RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
//                        mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
                        //mRunFaceDetection.run();
                    }
                });
        */
        
        
//        SDSLog.d(SDSLog.TAGIDX_CKY,,"mImageWidth="+mImageWidth);
//        SDSLog.d(SDSLog.TAGIDX_CKY,,"mImageHeight="+mImageHeight);

        int minimumCropSize = 160;
        if( mImageWidth <= minimumCropSize || mImageHeight <= minimumCropSize )	
        {
        	//Toast.makeText(this, "이미지 크기가 크롭 가능한 최소 크기보다 작습니다.", //Toast.LENGTH_SHORT).show();
//	        new Gori_Custom_Popup(this, "이미지 크기가 크롭 가능한 최소 크기보다 작습니다.",
//	        		new Gori_Custom_PopupOneBtnOnClickListener() 
//			{
//				@Override public void onClick() { finish(); }
//			}).show();
        }
        else
        {
        	startFaceDetection();
        }
    }
    
    public void setExchangeAspect()
    {
		int temp = mAspectX;
		mAspectX = mAspectY;
		mAspectY = temp;
		mRunFaceDetection.run();
    }
    
    @Override
	public void onClick(View v)
    {
    	final int clickedViewId = v.getId();
    	if( clickedViewId == R.id.imagecrop_bottom_ok_textview )
    	{
    		HighlightView cropHighlightView = mCropImageView.getHighlightView();
   			
    		final Rect cropRect = cropHighlightView.getCropRect();
    		
            final int cropWidth = cropRect.width();
            final int cropHeight = cropRect.height();
            
            int realCropWidth = mOriginWidth * cropWidth / mImageWidth;
            int realCropHeight = mOriginHeight * cropHeight / mImageHeight;
            
//            SDSLog.d(SDSLog.TAGIDX_CKY,, "mSaveUri = "+mSaveUri.getPath());
//            SDSLog.d(SDSLog.TAGIDX_CKY,, "CROP = "+cropWidth + "x " +cropHeight);
//            SDSLog.d(SDSLog.TAGIDX_CKY,, "CROP = "+cropRect.left+","+cropRect.top+","+cropRect.right+","+cropRect.bottom);

            Bitmap croppedImage = null;
            try 
            {
                croppedImage = Bitmap.createBitmap(realCropWidth, realCropHeight, Bitmap.Config.ARGB_8888);
                if( croppedImage != null )
                {
                    Canvas canvas = new Canvas(croppedImage);

                    
                    String path = mSaveUri.getPath();
                    if (WezoneUtil.isNeedFitContentMaxSizeImageFile(path) == true) {
                    	path = WezoneUtil.createFitContentMaxSizeImageFile(path);
        			}

                    BitmapFactory.Options bitmapDecodeOptions = new BitmapFactory.Options();
            		bitmapDecodeOptions.inJustDecodeBounds = true;
            		
                    Bitmap originBitmap = BitmapFactory.decodeFile(path,bitmapDecodeOptions);
                    if( originBitmap != null )
                    {
                        Rect srcRect = new Rect(
                                mOriginWidth * cropRect.left / mImageWidth,
                                mOriginHeight * cropRect.top / mImageHeight,
                                mOriginWidth * cropRect.right / mImageWidth,
                                mOriginHeight * cropRect.bottom / mImageHeight);
                    	Rect dstRect = new Rect(0, 0, realCropWidth, realCropHeight);

                    	canvas.drawBitmap(originBitmap, srcRect, dstRect, null);
                    	originBitmap.recycle();
                    }
                    else
                    {
                    	croppedImage.recycle();
                    	croppedImage = null;
                    }
                }
            } 
            catch (Exception e)
            {
            	Log.e("Activity_ImageCrop", "e = "+e);
            }
            
            if( croppedImage == null )
            {
            	croppedImage = Bitmap.createBitmap(cropWidth, cropHeight, Bitmap.Config.ARGB_8888);
            	
                Canvas canvas = new Canvas(croppedImage);
                Rect dstRect = new Rect(0, 0, cropWidth, cropHeight);
                canvas.drawBitmap(mBitmap, cropRect, dstRect, null);
            }
            
            if( croppedImage != null )
            {
				try 
				{
					String imagePath = mSaveUri.getPath();
					
					boolean isPng = imagePath.toLowerCase().endsWith(".png"); 
					
					String newFilePath;
					int offset = imagePath.lastIndexOf('/');
					if( offset > 0 )
						newFilePath = FileCache.getInstance().getTempFilePath()+imagePath.substring(offset+1);
					else
						newFilePath = FileCache.getInstance().getTempFilePath()+ String.valueOf(System.currentTimeMillis())+((isPng==true)?".png":".jpg");
					
					File file = new File(newFilePath);
					if( file.exists() == true ) file.delete();
					
					boolean isSucc = (isPng==true)?
							WezoneUtil.saveBitmapToPngFile(croppedImage, file, false):
                            WezoneUtil.saveBitmapToJpgFile(croppedImage, file, false);
					
					if( isSucc == true )
					{
//						SDSLog.d(SDSLog.TAGIDX_CKY,"크롭이미지 생성완료 ");
//						SDSLog.d(SDSLog.TAGIDX_CKY,newFilePath);
						
						mPhotoPickerImage.path = newFilePath;
						mPhotoPickerImage.release();
						
						int thumbSize = 284;
						mPhotoPickerImage.setRecycleThumbBitmap(ThumbnailUtils.extractThumbnail(croppedImage, thumbSize, thumbSize));
			            //Intent intent = new Intent();
			            //intent.putExtra(IMAGE_PATH, newFilePath);
			            //intent.putExtra(ORIENTATION_IN_DEGREES, Util.getOrientationInDegree(this));
			            setResult(RESULT_OK);
			            finish();
					}
					croppedImage.recycle();
				} 
				catch (Exception e2) {
                    if(Define.LOG_YN) {
                        Log.e(Define.LOG_TAG, "e2 = " + e2);
                    }
                }
            }
    	}
    	else if( clickedViewId == R.id.imagecrop_bottom_cancel_textview )
    	{
            setResult(RESULT_CANCELED);
            finish();
    	}
	}

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private Bitmap loadBitmap(String path)
    {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try 
        {
            in = mContentResolver.openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();
            
            mOriginWidth = o.outWidth;
            mOriginHeight= o.outHeight;

            int scale = 1;
            if (mOriginHeight > IMAGE_MAX_SIZE || mOriginWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(mOriginHeight, mOriginWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();
            return b;
        } catch (FileNotFoundException e) {
            if(Define.LOG_YN) {
                Log.e(Define.LOG_TAG, "file " + path + " not found");
            }
        } catch (IOException e) {
            if(Define.LOG_YN) {
                Log.e(Define.LOG_TAG, "file " + path + " not found");
            }
        }
        return null;
    }


    private void startFaceDetection() {

        if (isFinishing()) {
            return;
        }

        mCropImageView.setImageBitmapResetBase(mBitmap, true);

        String str = "Please wait";
        Util.startBackgroundJob(this, null,
        		str,
                new Runnable() {
                    public void run() {

                        final CountDownLatch latch = new CountDownLatch(1);
                        final Bitmap b = mBitmap;
                        mHandler.post(new Runnable() {
                            public void run() {

                                if (b != mBitmap && b != null) {
                                    mCropImageView.setImageBitmapResetBase(b, true);
                                    mBitmap.recycle();
                                    mBitmap = b;
                                }
                                if (mCropImageView.getScale() == 1F) {
                                    mCropImageView.center(true, true);
                                }
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        mRunFaceDetection.run();
                    }
                }, mHandler);
    }


    @Override
    protected void onPause() {

        super.onPause();
        BitmapManager.instance().cancelThreadDecoding(mDecodingThreads);
//        m_Share.setActivityName(this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() 
    {
        UIBitmap.bitmapDestory(mBitmap);
        
        super.onDestroy();
    }


    Runnable mRunFaceDetection = new Runnable() {
        @SuppressWarnings("hiding")
        float mScale = 1F;
        Matrix mImageMatrix;

//        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
//        int mNumFaces;

        // For each face, we create a HightlightView for it.
        private void handleFace(FaceDetector.Face f) {

            PointF midPoint = new PointF();

            int r = ((int) (f.eyesDistance() * mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= mScale;
            midPoint.y *= mScale;

            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;

            HighlightView hv = new HighlightView(mCropImageView);

            int width = mImageWidth;
            int height = mImageHeight;

            Rect imageRect = new Rect(0, 0, width, height);

            RectF faceRect = new RectF(midX, midY, midX, midY);
            faceRect.inset(-r, -r);
            if (faceRect.left < 0) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }

            if (faceRect.top < 0) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }

            if (faceRect.right > imageRect.right) {
                faceRect.inset(faceRect.right - imageRect.right,
                        faceRect.right - imageRect.right);
            }

            if (faceRect.bottom > imageRect.bottom) {
                faceRect.inset(faceRect.bottom - imageRect.bottom,
                        faceRect.bottom - imageRect.bottom);
            }

            hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop,
                    mAspectX != 0 && mAspectY != 0);

            mCropImageView.add(hv);
        }

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {

            HighlightView hv = new HighlightView(mCropImageView);

            Rect imageRect = new Rect(0, 0, mImageWidth, mImageHeight);

            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(mImageWidth, mImageHeight) * 4 / 5;
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {

                if (mAspectX > mAspectY) {

                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {

                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (mImageWidth - cropWidth) / 2;
            int y = (mImageHeight - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
                    mAspectX != 0 && mAspectY != 0);

            mCropImageView.clearHighlightViews();// Thong added for rotate

            mCropImageView.add(hv);
        }

        // Scale the image down for faster face detection.
        private Bitmap prepareBitmap() {

            if (mBitmap == null) {

                return null;
            }

            // 256 pixels wide is enough.
            if (mImageWidth > 256) {

                mScale = 256.0F / mImageWidth;
            }
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            return Bitmap.createBitmap(mBitmap, 0, 0, mImageWidth, mImageHeight, matrix, true);
        }

        public void run() {

            mImageMatrix = mCropImageView.getImageMatrix();
            mScale = 1.0F / mScale;
            
            /*
            Bitmap faceBitmap = prepareBitmap();
            if( faceBitmap != null && mDoFaceDetection == true ) 
            {
                FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
                        faceBitmap.getHeight(), mFaces.length);
                mNumFaces = detector.findFaces(faceBitmap, mFaces);
            }

            if (faceBitmap != null && faceBitmap != mBitmap) {
                faceBitmap.recycle();
            }
            */
            
            mHandler.post(new Runnable()
            {
                public void run() 
                {
                	/*
                    mWaitingToPick = mNumFaces > 1;
                    if (mNumFaces > 0) 
                    {
                        for (int i = 0; i < mNumFaces; i++) {
                            handleFace(mFaces[i]);
                        }
                    } 
                    else 
                    {
                        makeDefault();
                    }
                    */
                	makeDefault();
                    
                    if( mCropHighlightView != null )
                    	mCropHighlightView.setHidden(false); 
                    mCropImageView.invalidate();
                    
//                    if (mImageView.mHighlightViews.size() == 1) {
//                        mCrop = mImageView.mHighlightViews.get(0);
//                        mCrop.setFocus(true);
//                    }
//
//                    if (mNumFaces > 1) {
//                        //Toast.makeText(CropImage.this,
//                                "Multi face crop help",
//                                //Toast.LENGTH_SHORT).show();
//                    }
                }
            });

        }
    };

    public static final int NO_STORAGE_ERROR  = -1;
    public static final int CANNOT_STAT_ERROR = -2;

//    public static void showStorageToast(Activity activity) {
//
//        showStorageToast(activity, calculatePicturesRemaining(activity));
//    }

    public static void showStorageToast(Activity activity, int remaining) {

        String noStorageText = null;

        if (remaining == NO_STORAGE_ERROR) {

            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_CHECKING)) {

                noStorageText = "Preparing card";
            } else {

                noStorageText = "No storage card";
            }
        } else if (remaining < 1) {

            noStorageText = "Not enough space";
        }

        if (noStorageText != null) {

            //Toast.makeText(activity, noStorageText, 5000).show();
        }
    }

//    public static int calculatePicturesRemaining(Activity activity) {
//
//        try {
//            /*if (!ImageManager.hasStorage()) {
//                return NO_STORAGE_ERROR;
//            } else {*/
//        	String storageDirectory = "";
//        	String state = Environment.getExternalStorageState();
//        	if (Environment.MEDIA_MOUNTED.equals(state)) {
//        		storageDirectory = Environment.getExternalStorageDirectory().toString();
//        	}
//        	else {
//        		storageDirectory = activity.getFilesDir().toString();
//        	}
//            StatFs stat = new StatFs(storageDirectory);
//            float remaining = ((float) stat.getAvailableBlocks()
//                    * (float) stat.getBlockSize()) / 400000F;
//            return (int) remaining;
//            //}
//        } catch (Exception ex) {
//            // if we can't stat the filesystem then we don't know how many
//            // pictures are remaining.  it might be zero but just leave it
//            // blank since we really don't know.
//            return CANNOT_STAT_ERROR;
//        }
//    }

    /*
    private void onSaveClicked() throws Exception 
    {
        // TODO this code needs to change to use the decode/crop/encode single
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mSaving) return;

        if (mCropHighlightView == null) {

            return;
        }

        mSaving = true;

        Rect r = mCropHighlightView.getCropRect();

        int width = r.width();
        int height = r.height();

        // If we are circle cropping, we want alpha channel, which is the
        // third param here.
        Bitmap croppedImage;
        try {

            croppedImage = Bitmap.createBitmap(width, height,
                    mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        } catch (Exception e) {
            throw e;
        }
        if (croppedImage == null) {

            return;
        }

        {
            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mBitmap, r, dstRect, null);
        }

        if (mCircleCrop) {

            // OK, so what's all this about?
            // Bitmaps are inherently rectangular but we want to return
            // something that's basically a circle.  So we fill in the
            // area around the circle with alpha.  Note the all important
            // PortDuff.Mode.CLEAR.
            Canvas c = new Canvas(croppedImage);
            Path p = new Path();
            p.addCircle(width / 2F, height / 2F, width / 2F,
                    Path.Direction.CW);
            c.clipPath(p, Region.Op.DIFFERENCE);
            c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        }

		// If the output is required to a specific size then scale or fill 
        if (mOutputX != 0 && mOutputY != 0) {

            if (mScale) {

                // Scale the image to the required dimensions
                Bitmap old = croppedImage;
                croppedImage = Util.transform(new Matrix(),
                        croppedImage, mOutputX, mOutputY, mScaleUp);
                if (old != croppedImage) {

                    old.recycle();
                }
            } else {

//				Don't scale the image crop it to the size requested.
//                Create an new image with the cropped image in the center and
//				the extra space filled.

                // Don't scale the image but instead fill it so it's the
                // required dimension
                Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY,
                        Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(b);

                Rect srcRect = mCropHighlightView.getCropRect();
                Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

                int dx = (srcRect.width() - dstRect.width()) / 2;
                int dy = (srcRect.height() - dstRect.height()) / 2;

				// If the srcRect is too big, use the center part of it
                srcRect.inset(Math.max(0, dx), Math.max(0, dy));

				// If the dstRect is too big, use the center part of it.
                dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

				// Draw the cropped bitmap in the center
                canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

				// Set the cropped bitmap as the new bitmap
                croppedImage.recycle();
                croppedImage = b;
            }
        }

        // Return the cropped image directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null && (myExtras.getParcelable("data") != null
                || myExtras.getBoolean(RETURN_DATA))) 
        {

            Bundle extras = new Bundle();
            extras.putParcelable(RETURN_DATA_AS_BITMAP, croppedImage);
            setResult(RESULT_OK,
                    (new Intent()).setAction(ACTION_INLINE_DATA).putExtras(extras));
            finish();
        } 
        else 
        {
            final Bitmap b = croppedImage;
            Util.startBackgroundJob(this, null, "이미지를 저장 중 입니다. 잠시만 기다려주세요.",
                    new Runnable() {
                        public void run() {

                            saveOutput(b);
                        }
                    }, mHandler);
        }
    }

    private void saveOutput(Bitmap croppedImage) {

        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 90, outputStream);
                }
            } catch (IOException ex) {

                SDSLog.e(SDSLog.TAGIDX_CKY, "Cannot open file: " + mSaveUri, ex);
                setResult(RESULT_CANCELED);
                finish();
                return;
            } finally {

                Util.closeSilently(outputStream);
            }

            Bundle extras = new Bundle();
            Intent intent = new Intent(mSaveUri.toString());
            intent.putExtras(extras);
            intent.putExtra(IMAGE_PATH, mImagePath);
            intent.putExtra(ORIENTATION_IN_DEGREES, Util.getOrientationInDegree(this));
            setResult(RESULT_OK, intent);
        } else {

            SDSLog.e(SDSLog.TAGIDX_CKY, "not defined image url");
        }
        croppedImage.recycle();
        finish();
    }
    */
}


