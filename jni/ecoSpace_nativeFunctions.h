/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ecoSpace_nativeFunctions */

#ifndef _Included_ecoSpace_nativeFunctions
#define _Included_ecoSpace_nativeFunctions
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    readVariablesFromCoords
 * Signature: ([F[F[IILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ecoSpace_nativeFunctions_readVariablesFromCoords
  (JNIEnv *, jclass, jfloatArray, jfloatArray, jintArray, jint, jstring);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    initProgressDistanceMatrix
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_ecoSpace_nativeFunctions_initProgressDistanceMatrix
  (JNIEnv *, jclass);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    computeDistanceMatrix
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_ecoSpace_nativeFunctions_computeDistanceMatrix
  (JNIEnv *, jclass, jstring, jstring, jlong);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    getProgressDistanceMatrix
 * Signature: (JZ)I
 */
JNIEXPORT jint JNICALL Java_ecoSpace_nativeFunctions_getProgressDistanceMatrix
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    computeKernelDensities
 * Signature: (Ljava/lang/String;Ljava/lang/String;[IIFZ)I
 */
JNIEXPORT jint JNICALL Java_ecoSpace_nativeFunctions_computeKernelDensities
  (JNIEnv *, jclass, jstring, jstring, jintArray, jint, jfloat, jboolean);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    openDistanceMatrix
 * Signature: (Ljava/lang/String;Ljava/lang/String;)[J
 */
JNIEXPORT jlongArray JNICALL Java_ecoSpace_nativeFunctions_openDistanceMatrix
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    exportDistanceMatrix
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ecoSpace_nativeFunctions_exportDistanceMatrix
  (JNIEnv *, jclass, jlong);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    getRelationships
 * Signature: (J[IIIZZ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ecoSpace_nativeFunctions_getRelationships
  (JNIEnv *, jclass, jlong, jintArray, jint, jint, jboolean, jboolean);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    closeDistanceMatrix
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_ecoSpace_nativeFunctions_closeDistanceMatrix
  (JNIEnv *, jclass, jlong);

/*
 * Class:     ecoSpace_nativeFunctions
 * Method:    getDensityPNG
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_ecoSpace_nativeFunctions_getDensityPNG
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
