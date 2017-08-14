#include "CryptHelper.h"
#include <string.h>
#include <stdio.h>


jstring Java_com_vinetech_util_crypt_CryptHelper_getKey( JNIEnv *env, jobject thiz)
{
	return (*env)->NewStringUTF(env,"07162009_02222011_03151978_790216");
}

jstring Java_com_vinetech_util_crypt_CryptHelper_toHashCryptKey(JNIEnv* env, jobject thiz, jint hash1, jint hash2)
{
	char buff [24] = {0,};
	jint ret = snprintf(buff, sizeof(buff), "%09d%09d", hash1,hash2);

//	int sprintf  (char *buffer, const char *format, ...)
//	int snprintf (char *buffer, int buf_size, const char *format, ...)

	return (*env)->NewStringUTF(env,buff);
}
