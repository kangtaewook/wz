package com.vinetech.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.vinetech.wezone.Define;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class DownloadFile extends AsyncTask<String, Integer, String>
{
       public static final int		DOWNLOAD_PROGRESS  = -9996;
       public static final int		DOWNLOAD_END	   = -9997;
    public static final int		DOWNLOAD_ERRROR    = -9998;
      public static final int		DOWNLOAD_CANCEL    = -9999;

    private Context m_Context		   = null;
    private DownloadListener    m_listener		   = null;
    private boolean 			m_bLoop			   = false;
    private int 				m_nStateListener   = -1;

//		private CustomPopupManager 		mCustomPopupManager = null;


    public DownloadFile(Context context, DownloadListener listener) {
        m_Context  = context;
        m_listener = listener;
        m_bLoop	   = true;



    }

    public void DownloadExit(){
        m_bLoop    = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//    		if(mCustomPopupManager != null){
//    			mCustomPopupManager.onShow();
//    		}
    }

    protected String doInBackground(String... strData)
    {
        ByteBuffer byteBuffer 	   = ByteBuffer.allocate(4096);
        InputStream inputStream	   = null;
        ReadableByteChannel inputChannel   = null;
        FileChannel fileChannel	   = null;
        FileOutputStream fileOutput 	   = null;

        try {
            URL url 		  = new URL(strData[0]);
            URLConnection connection  = url.openConnection();
            connection.connect();

            int 		 lenghtOfFile = (connection.getContentLength());

            String filePath	  = strData[1];
            String fileName 	  = strData[2];

            inputStream 			  = url.openStream();
            inputChannel 			  = Channels.newChannel(inputStream);

            File dir = new File(filePath);
            dir.mkdirs();

            File outputFile = new File(dir, fileName);

            fileOutput 				  = new FileOutputStream(outputFile);
            fileChannel 			  = fileOutput.getChannel();

            long readBytes 			  = 0;
            int read 				  = 0;
            int currentPercent 		  = 0;

            if (lenghtOfFile > -1)
            {
                  while(m_bLoop == true && lenghtOfFile > readBytes)
                  {
                      byteBuffer.clear();

                      read 		 	 = inputChannel.read(byteBuffer);
                      readBytes 	+= read;

                      currentPercent = (int)((float)readBytes * (float)100 / (float)lenghtOfFile);

                      byteBuffer.flip();
                      fileChannel.write(byteBuffer);

                      /////////////////////////////////
                      // Listener
                      m_listener.result(currentPercent);
                  }
            }
            else if (lenghtOfFile == -1)
            {
                  m_listener.result(DOWNLOAD_PROGRESS);

                  while((read = inputChannel.read(byteBuffer)) != -1)
                  {
                      readBytes += read;
                      byteBuffer.flip();

                      fileChannel.write(byteBuffer);
                      byteBuffer.clear();
                  }

                  lenghtOfFile   = (int)readBytes;
                  currentPercent = (int)((float)readBytes * (float)100 / (float)lenghtOfFile);
            }

            m_nStateListener = DOWNLOAD_END;

        }
        catch (Exception e)
        {
            if(Define.LOG_YN) {
                Log.d(Define.LOG_TAG, e.getMessage());
            }
            m_listener.result(DOWNLOAD_ERRROR);

        } finally {


//				if(mCustomPopupManager != null){
//					mCustomPopupManager.onDestroy();
//					mCustomPopupManager = null;
//				}
//
            try {
                if(inputChannel != null)
                {
                    inputChannel.close();
                    inputChannel = null;
                }

                if(fileChannel != null)
                {
                    fileChannel.close();
                    fileChannel = null;
                }

                if(fileOutput != null)
                {
                    fileOutput.flush();
                }

                if(fileOutput != null){
                    fileOutput.close();
                    fileOutput = null;
                }

                m_listener.result(m_nStateListener);

            } catch (IOException e) {
                if(Define.LOG_YN) {
                    Log.d(Define.LOG_TAG, e.getMessage());
                }
            }
        }

        return null;
    }

    protected void onCancelled()
    {
        cancel(true);
        m_listener.result(DOWNLOAD_CANCEL);

//			if(mCustomPopupManager != null){
//				mCustomPopupManager.onDestroy();
//				mCustomPopupManager = null;
//			}

        super.onCancelled();
    }

       public interface DownloadListener {
        void result(int nState);
       }
}
