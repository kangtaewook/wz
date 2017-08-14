package com.vinetech.util.crypt;

public final class CryptHelper {
    private static CryptHelper sInstance;

    static{
        System.loadLibrary("CryptHelper");
    }

//    static {
//        try {
//            setLibraryPath("CryptHelper"); // 여기서 system path 설정
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            System.loadLibrary("CryptHelper");
//        } catch (UnsatisfiedLinkError e) {
//            System.err.println("The dynamic link library for Java could not be" + "loaded .\nConsider using \njava -Djava.library.path =\n" + e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//	public static CryptHelper sharedInstance()
//	{
//		return (sInstance==null)?(sInstance=new CryptHelper()):sInstance;
//	}

    public static CryptHelper getInstance() {
        return (sInstance == null) ? (sInstance = new CryptHelper()) : sInstance;
    }

    public native String getKey();

    public native String toHashCryptKey(int hash1, int hash2);

    private CryptHelper() {
    }

//    public static void setLibraryPath(String path) throws Exception {
//        System.setProperty("java.library.path", path);
//        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
//        sysPathsField.setAccessible(true);
//        sysPathsField.set(null, null);
//    }

}
