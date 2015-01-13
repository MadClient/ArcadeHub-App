/*
 * This file is part of MAME4droid.
 *
 * Copyright (C) 2011 David Valdeita (Seleuco)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * In addition, as a special exception, Seleuco
 * gives permission to link the code of this program with
 * the MAME library (or with modified versions of MAME that use the
 * same license as MAME), and distribute linked combinations including
 * the two.  You must obey the GNU General Public License in all
 * respects for all of the code used other than MAME.  If you modify
 * this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so.  If you do not wish to
 * do so, delete this exception statement from your version.
 */


#include <dlfcn.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>

#include <math.h>

#include <pthread.h>

#include "com_yunluo_android_arcadehub_Emulator.h"

//#define DEBUG 0

//mame4droid funtions
int  (*android_main)(int argc, char **argv)=NULL;
void (*setAudioCallbacks)(void *func1,void *func2,void *func3)= NULL;
void (*setVideoCallbacks)(void *func1,void *func2,void *func3) = NULL;
void (*setPadStatus)(int i, unsigned long pad_status) = NULL;
void (*setGlobalPath)(const char *path) = NULL;

void  (*setMyValue)(int key,int i, int value)=NULL;
int  (*getMyValue)(int key, int i)=NULL;
void  (*setMyValueStr)(int key, int i,const char *value)=NULL;
char *(*getMyValueStr)(int key,int i)=NULL;

void  (*setMyAnalogData)(int i, float v1,float v2)=NULL;

void  (*droid_video_thread)()=NULL;

/* Callbacks to Android */
jmethodID android_dumpVideo;
jmethodID android_changeVideo;
jmethodID android_openAudio;
jmethodID android_dumpAudio;
jmethodID android_closeAudio;

static JavaVM *jVM = NULL;
static void *libdl = NULL;
static jclass cEmulator = NULL;

static jobject videoBuffer=NULL;//es un ByteBuffer wrappeando el buffer de video en la libreria 

static jbyteArray jbaAudioBuffer = NULL;

static jobject audioBuffer=NULL;
static unsigned char audioByteBuffer[882 * 2 * 2 * 10];

static pthread_t main_tid;

void (*openCheatItem) (int index) = NULL;
void (*closeCheatItem) (int index) = NULL;
int (*onCheatListCallbacks) (void *func1) = NULL;
jmethodID android_onCheatListCallback;

int (*gameLoadedCallbacks) (void *func1) = NULL;
jmethodID android_gameLoadedCallback;

int (*setMyStartGame) (const char *path, const char *romName) = NULL;

int (*setUpdateHighScore) () = NULL;
void (*onHighScoreCallbacks) (void *func1) = NULL;
jmethodID android_onHighScoreCallbacks;

void (*setPauseGame) () = NULL;

void (*setResumeGame) () = NULL;

void (*setDeviceIP) (const char *address) = NULL;

void (*setWIFIGame) (const char *path) = NULL;

void (*setBluetoochGame) (const char *path) = NULL;

int (*gameStopedCallbacks) (void *fun1) = NULL;
jmethodID android_gameStoped;

int (*gameResumedCallbacks) (void *fun1) = NULL;
jmethodID android_gameResumed;

int (*sktNetplayHangupCallbacks) (void *func1) = NULL;
jmethodID android_sktNetplayHangup;

void (*setSendMessageToSocket)(int state) = NULL;

void (*saveAndLoadFileName) (const char *fileName) = NULL;

void (*saveGame) (const char *fileName) = NULL;

void (*loadGame) (const char *fileName) = NULL;

jstring (*getArchivePath)() = NULL;

void (*setBroadcastDeviceInformation)(int os, const char *ip, int cpu, int ram, const char *rom) = NULL;

int (*onNetPlayInformationCallbacks) (void *func1) = NULL;
jmethodID android_onNetPlayInformationCallback;

void (*setStopBroadcast)() = NULL;

void (*resetGame)() = NULL;

int (*onNetPlayStatusCallbacks) (void *fun1) = NULL;
jmethodID android_onNetPlayStatus;

void (*closeGame) () = NULL;

void (*startScan) (const char *path) = NULL;

void (*sdcardDir) (const char *path) = NULL;

void (*onInsertGameInfomation) (void *func1) = NULL;
jmethodID android_onInsertGameInfomation;

jstring (*getGame) (const char *name, jint type) = NULL;

void (*test)() = NULL;

void (*stopScan) (int flag) = NULL;

void (*hintTransferSendRomCB) (const char *path) = NULL;

void (*scanFinishCallbacks) (void *func1) = NULL;
jmethodID android_scanFinishCallbacks;

jmethodID android_loadLibFinishCallbacks;

void (*playGamesCallbacks) (void *func1) = NULL;
jmethodID android_playGamesCallbacks;

void (*setMultiKey) (jint pressedBtn, jint *multiKeyArray, jint length) = NULL;
void (*restMultiKey) () = NULL;
void (*setMultiPress) (jint pressedBtn, jint *multiPressArray, jint length) = NULL;
void (*restMultiPress) (int key) = NULL;
void (*getVaildBtn) (jint *inputKeyArray, jint length) = NULL;

void (*androidGamekitReceivedData)(char* buffer, int len)=NULL;
void (*androidGamekitAction)(int state)=NULL;
void (*androidGamekitRegisterSendFunction)(void* func)=NULL;
void (*androidGamekitGetGamename)(char* name, int maxlen, int isServer)=NULL;
int (*androidGamekitQueryState)(int query)=NULL;

static jbyteArray gamekitSendDataJBuffer = NULL;
jmethodID gamekitSendDataJavaMethod;
void myJNI_androidGamekitRegisterSendFunction(char* buffer, int size);


static void load_lib(const char *str)
{
    char str2[256];
    
    strcpy(str2,str);
    strcpy(str2+strlen(str),"/libArcadeHub.so");

#ifdef DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Attempting to load %s\n", str2);
#endif

    if(libdl!=NULL)
        return;

    libdl = dlopen(str2, RTLD_NOW);
    if(!libdl)
    {
#ifdef DEBUG
        __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Unable to load libArcadeHub.so: %s\n", dlerror());
#endif
        return;
    }

    android_main = dlsym(libdl, "android_main");
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","android_main %d\n", android_main!=NULL);

    setVideoCallbacks = dlsym(libdl, "setVideoCallbacks");
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","setVideoCallbacks %d\n", setVideoCallbacks!=NULL);

    setAudioCallbacks = dlsym(libdl, "setAudioCallbacks");    
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","setAudioCallbacks %d\n", setAudioCallbacks!=NULL);

    setPadStatus = dlsym(libdl, "setPadStatus");
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","setPadStatus %d\n", setPadStatus!=NULL);

    setGlobalPath = dlsym(libdl, "setGlobalPath"); 
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","setGlobalPath %d\n", setGlobalPath!=NULL);

    setMyValue = dlsym(libdl, "setMyValue"); 
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","setMyValue %d\n",setMyValue!=NULL);

    getMyValue = dlsym(libdl, "getMyValue"); 
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","getMyValue %d\n", getMyValue!=NULL);

    setMyValueStr = dlsym(libdl, "setMyValueStr"); 
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","setMyValueStr %d\n",setMyValueStr!=NULL);

    getMyValueStr = dlsym(libdl, "getMyValueStr"); 
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","getMyValueStr %d\n", getMyValueStr!=NULL);

    setMyAnalogData = dlsym(libdl, "setMyAnalogData"); 
//     __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","setMyAnalogData %d\n", setMyAnalogData!=NULL);
    
    droid_video_thread = dlsym(libdl, "droid_ios_video_thread"); 
//    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","droid_ios_video_thread%d\n", droid_video_thread!=NULL);

    setMyStartGame = dlsym(libdl, "setStartGame");
    openCheatItem = dlsym(libdl, "openCheatItem");
    closeCheatItem = dlsym(libdl, "closeCheatItem");
    onCheatListCallbacks = dlsym(libdl, "onCheatListCallbacks");
    gameLoadedCallbacks = dlsym(libdl, "gameLoadedCallbacks");
    setUpdateHighScore = dlsym(libdl, "updateHighScore");
    onHighScoreCallbacks = dlsym(libdl, "onHighScoreCallbacks");
    setPauseGame = dlsym(libdl, "setPauseGame");
    setResumeGame = dlsym(libdl, "setResumeGame");
    setDeviceIP = dlsym(libdl, "setDeviceIP");
    gameStopedCallbacks = dlsym(libdl, "gameStopedCallbacks");
    gameResumedCallbacks = dlsym(libdl, "gameResumedCallbacks");
    sktNetplayHangupCallbacks = dlsym(libdl, "sktNetplayHangupCallbacks");
    setSendMessageToSocket = dlsym(libdl, "android_skt_action");
    setWIFIGame = dlsym(libdl, "setWIFIGame");
    setBluetoochGame = dlsym(libdl, "setBluetoochGame");
    saveAndLoadFileName = dlsym(libdl, "saveAndLoadFileName");
    saveGame = dlsym(libdl, "saveGameByName");
    loadGame = dlsym(libdl, "loadGameByName");
    getArchivePath = dlsym(libdl, "getLoadSaveDocumentPath");
    onNetPlayInformationCallbacks = dlsym(libdl, "onNetPlayInformationCallbacks");
    setBroadcastDeviceInformation = dlsym(libdl, "setBroadcastDeviceInformation");
    setStopBroadcast = dlsym(libdl, "setStopBroadcast");
    resetGame = dlsym(libdl, "resetGame");
    onNetPlayStatusCallbacks = dlsym(libdl, "onNetPlayStatus");
    closeGame = dlsym(libdl, "closeGame");
    startScan = dlsym(libdl, "startScan");
    sdcardDir = dlsym(libdl, "sdcardDir");
    onInsertGameInfomation = dlsym(libdl, "onInsertGameInfomation");
    getGame = dlsym(libdl, "getGameDesc");
    test = dlsym(libdl, "test");
    stopScan = dlsym(libdl, "stopScan");
    hintTransferSendRomCB = dlsym(libdl, "hintTransferSendRomCB");
    scanFinishCallbacks = dlsym(libdl, "scanFinishCallbacks");
    playGamesCallbacks = dlsym(libdl, "playGamesCallbacks");
    setMultiKey = dlsym(libdl, "setMultiKey");
    restMultiKey = dlsym(libdl, "restMultiKey");
    setMultiPress = dlsym(libdl, "setMultiPress");
    restMultiPress = dlsym(libdl, "restMultiPress");
    getVaildBtn = dlsym(libdl, "getVaildBtn");
	androidGamekitReceivedData = dlsym(libdl, "android_gamekit_received_data");
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","androidGamekitReceivedData %d\n", androidGamekitReceivedData!=NULL);
#endif

	androidGamekitAction = dlsym(libdl, "android_gamekit_action");
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","androidGamekitAction %d\n", androidGamekitAction!=NULL);
#endif

	androidGamekitRegisterSendFunction = dlsym(libdl, "android_gamekit_register_send_function");
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","androidGamekitRegisterSendFunction %d\n", androidGamekitRegisterSendFunction!=NULL);
#endif
	if (androidGamekitRegisterSendFunction){
		androidGamekitRegisterSendFunction(myJNI_androidGamekitRegisterSendFunction);
	}

	androidGamekitGetGamename = dlsym(libdl, "android_gamekit_get_gamename");
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","androidGamekitGetGamename %d\n", androidGamekitGetGamename!=NULL);
#endif

	androidGamekitQueryState = dlsym(libdl, "android_gamekit_query_state");
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni","androidGamekitQueryState %d\n", androidGamekitQueryState!=NULL);
#endif
	//android bt port end

    /***********************************************************************************/


}

void myJNI_initVideo(void *buffer)
{
    JNIEnv *env;
    jobject tmp;
    (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
#ifdef DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "initVideo");
#endif
    tmp = (*env)->NewDirectByteBuffer(env, buffer, 1024 * 1024 * 2);//640,480 power 2
    videoBuffer = (jobject)(*env)->NewGlobalRef(env, tmp);

    if(!videoBuffer) __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "yikes, unable to initialize video buffer");

}

void myJNI_dumpVideo(int emulating)
{

JNIEnv *env;
    (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
   // __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "dumpVideo emulating:%d",emulating);
#endif

    (*env)->CallStaticVoidMethod(env, cEmulator, android_dumpVideo, videoBuffer,(jboolean)emulating);
}

void myJNI_changeVideo(int newWidth, int newHeight,int newVisWidth, int newVisHeight)
{
    JNIEnv *env;
    (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "changeVideo");
#endif


    (*env)->CallStaticVoidMethod(env, cEmulator, android_changeVideo, (jint)newWidth,(jint)newHeight,(jint)newVisWidth,(jint)newVisHeight);
}

void myJNI_closeAudio()
{
    JNIEnv *env;
    (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "closeAudio");
#endif

    (*env)->CallStaticVoidMethod(env, cEmulator, android_closeAudio);
}

void myJNI_openAudio(int rate, int stereo)
{
    JNIEnv *env;
    (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "openAudio");
#endif


    (*env)->CallStaticVoidMethod(env, cEmulator, android_openAudio, (jint)rate,(jboolean)stereo);
}


void myJNI_dumpAudio(void *buffer, int size)
{
    JNIEnv *env;
    jobject tmp;
    (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
    //__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "dumpAudio %ld %d",buffer, size);
#endif

    if(jbaAudioBuffer==NULL)
    {
        jbaAudioBuffer=(*env)->NewByteArray(env, 882*2*2*10);
        tmp = jbaAudioBuffer;
        jbaAudioBuffer=(jbyteArray)(*env)->NewGlobalRef(env, jbaAudioBuffer);
        (*env)->DeleteLocalRef(env, tmp);
    }    

    (*env)->SetByteArrayRegion(env, jbaAudioBuffer, 0, size, (jbyte *)buffer);
   
    (*env)->CallStaticVoidMethod(env, cEmulator, android_dumpAudio,jbaAudioBuffer,(jint)size);
}

void myJNI_dumpAudio2(void *buffer, int size)
{
    JNIEnv *env;
    jobject tmp;
    (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
    //__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "dumpAudio %ld %d",buffer, size);
#endif

    if(audioBuffer==NULL)
    {
       tmp = (*env)->NewDirectByteBuffer(env, audioByteBuffer, 882*2*2*10);
       audioBuffer = (jobject)(*env)->NewGlobalRef(env, tmp);
    }
    
    memcpy(audioByteBuffer,buffer,size);

    (*env)->CallStaticVoidMethod(env, cEmulator, android_dumpAudio, audioBuffer,(jint)size);

}

void myJNI_hiscore(int score)
{
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
	//__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_hiscore");
#endif
	(*env)->CallStaticVoidMethod(env, cEmulator, android_onHighScoreCallbacks, (jint)score);
}

void myJNI_onCheatList(int index, const char *text) {
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
//	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_onCheatList");
#endif
	//test
	jstring strText = (*env)->NewStringUTF(env, text);
	(*env)->ReleaseStringUTFChars(env, strText, NULL);

	(*env)->CallStaticVoidMethod(env, cEmulator, android_onCheatListCallback, (jint)index, (jstring)strText);
}


void myJNI_gameLoaded() {
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_gameLoaded");
#endif
	//test

	(*env)->CallStaticVoidMethod(env, cEmulator, android_gameLoadedCallback);
}


void myJNI_gameStoped() {
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
	//    (*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_gameStoped");
#endif
	(*env)->CallStaticVoidMethod(env, cEmulator, android_gameStoped);
}


void myJNI_gameResumed()
{
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
	//    (*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_gameResumed");
#endif
	(*env)->CallStaticVoidMethod(env, cEmulator, android_gameResumed);
}


void myJNI_sktNetplayHangup()
{
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
//	(*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_sktNetplayHangup");
#endif
	(*env)->CallStaticVoidMethod(env, cEmulator, android_sktNetplayHangup);
//	(*jVM)->DetachCurrentThread(jVM);
}


void myJNI_onNetPlayInformation(int os, char *ip, int cpu, int ram, char *rom)
{
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
	(*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_onNetPlayInformation");
#endif

//	jstring mOs = (*env)->NewStringUTF(env, os);
//	(*env)->ReleaseStringUTFChars(env, mOs, NULL);
	jstring mIp = (*env)->NewStringUTF(env, ip);
//	jstring mCpu = (*env)->NewStringUTF(env, cpu);
//	(*env)->ReleaseStringUTFChars(env, mCpu, NULL);
//	jstring mRam = (*env)->NewStringUTF(env, ram);
//	(*env)->ReleaseStringUTFChars(env, mRam, NULL);
	jstring mRom = (*env)->NewStringUTF(env, rom);

	(*env)->CallStaticVoidMethod(env, cEmulator, android_onNetPlayInformationCallback, (jint) os, (jstring) mIp, (jint) cpu, (jint) ram, (jstring) mRom);
	(*env)->ReleaseStringUTFChars(env, mIp, NULL);
	(*env)->ReleaseStringUTFChars(env, mRom, NULL);
	(*jVM)->DetachCurrentThread(jVM);
}


void myJNI_onNetPlayStatus(int type, char *ip)
{
	JNIEnv *env;
	int status = 0;
	status = (*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
	if(status < 0) {
		(*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);
	}
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_onNetPlayStatus");
#endif

	jstring mIp = (*env)->NewStringUTF(env, ip);

	(*env)->CallStaticVoidMethod(env, cEmulator, android_onNetPlayStatus, (jint) type, (jstring) mIp);
	(*env)->ReleaseStringUTFChars(env, mIp, NULL);
	if(status < 0) {
		(*jVM)->DetachCurrentThread(jVM);
	}
}


void myJNI_onInsertGameInfomation(const char *romName, const char *gameName, const char *path, const char *size, const char *year, const char *cname, const char *filepath) {
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
	(*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_onInsertGameInfomation");
#endif

	jstring mRomName = (*env)->NewStringUTF(env, romName);
	jstring mGameName = (*env)->NewStringUTF(env, gameName);
	jstring mPath = (*env)->NewStringUTF(env, path);
	jstring mSize = (*env)->NewStringUTF(env, size);
	jstring mYear = (*env)->NewStringUTF(env, year);
	jstring mCname = (*env)->NewStringUTF(env, cname);
	jstring mFilePath = (*env)->NewStringUTF(env, filepath);

	(*env)->CallStaticVoidMethod(env, cEmulator, android_onInsertGameInfomation, (jstring) mRomName, (jstring) mGameName, (jstring) mPath, (jstring) mSize, (jstring) mYear, (jstring) mCname, (jstring) mFilePath);
	(*env)->ReleaseStringUTFChars(env, mRomName, NULL);
	(*env)->ReleaseStringUTFChars(env, mGameName, NULL);
	(*env)->ReleaseStringUTFChars(env, mPath, NULL);
	(*env)->ReleaseStringUTFChars(env, mSize, NULL);
	(*env)->ReleaseStringUTFChars(env, mYear, NULL);
	(*env)->ReleaseStringUTFChars(env, mCname, NULL);
	(*env)->ReleaseStringUTFChars(env, mFilePath, NULL);
	(*jVM)->DetachCurrentThread(jVM);
}


void myJNI_scanFinish()
{
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
	(*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);
#ifdef DEBUG
	//__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_scanFinish");
#endif
	(*env)->CallStaticVoidMethod(env, cEmulator, android_scanFinishCallbacks);
	(*jVM)->DetachCurrentThread(jVM);
}


void myJNI_loadLibFinish()
{
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
	(*env)->CallStaticVoidMethod(env, cEmulator, android_loadLibFinishCallbacks);
}

// playgames
void myJNI_playGames() {
	JNIEnv *env;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);
//	(*jVM)->AttachCurrentThread(jVM, (void**) &env, NULL);
#ifdef DEBUG
	//__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_playGames");
#endif
	(*env)->CallStaticVoidMethod(env, cEmulator, android_playGamesCallbacks);
//	(*jVM)->DetachCurrentThread(jVM);
}

/***************************************************************************************************/

int JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv *env;
    jVM = vm;

#ifdef DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "JNI_OnLoad called");
#endif

    if((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK)
    {
#ifdef DEBUG
        __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to get the environment using GetEnv()");
#endif
        return -1;
    }
    
//    cEmulator = (*env)->FindClass (env, "com/seleuco/mame4droid/Emulator");
    cEmulator = (*env)->FindClass (env, "com/yunluo/android/arcadehub/Emulator");

    if(cEmulator==NULL)
    {
#ifdef DEBUG
        __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find class com.yunluo.android.arcadehub.Emulator");
#endif
        return -1;
    } 

    cEmulator = (jclass) (*env)->NewGlobalRef(env,cEmulator );

    android_dumpVideo = (*env)->GetStaticMethodID(env,cEmulator,"bitblt","(Ljava/nio/ByteBuffer;Z)V");
    
    if(android_dumpVideo==NULL)
    {
#ifdef DEBUG
        __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method bitblt");
#endif
        return -1;
    }

    android_changeVideo= (*env)->GetStaticMethodID(env,cEmulator,"changeVideo","(IIII)V");
    
    if(android_changeVideo==NULL)
    {
#ifdef DEBUG
        __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method changeVideo");
#endif
        return -1;
    }

    //android_dumpAudio = (*env)->GetStaticMethodID(env,cEmulator,"writeAudio","(Ljava/nio/ByteBuffer;I)V");
    android_dumpAudio = (*env)->GetStaticMethodID(env,cEmulator,"writeAudio","([BI)V");

    if(android_dumpAudio==NULL)
    {
#ifdef DEBUG
        __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method writeAudio");
#endif
        return -1;
    }

    android_openAudio = (*env)->GetStaticMethodID(env,cEmulator,"initAudio","(IZ)V");

    if(android_openAudio==NULL)
    {
#ifdef DEBUG
        __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method openAudio");
#endif
        return -1;
    }

    android_closeAudio = (*env)->GetStaticMethodID(env,cEmulator,"endAudio","()V");

    if(android_closeAudio==NULL)
    {
#ifdef DEBUG
    	__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method closeAudio");
#endif
        return -1;
    }

    /************************************** call->Java*****************************************/

    //android onHighScore
    android_onHighScoreCallbacks = (*env)->GetStaticMethodID(env,cEmulator,"onHighScore","(I)V");
    if(android_onHighScoreCallbacks == NULL)
    {
#ifdef DEBUG
    	__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method onHighScoreCallback");
#endif
    	return -1;
    }

    //android onCheatList
	android_onCheatListCallback = (*env)->GetStaticMethodID(env,cEmulator,"onCheatList","(ILjava/lang/String;)V");
	if(android_onCheatListCallback == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_onCheatListCallback");
#endif
		return -1;
	}

	android_gameLoadedCallback = (*env)->GetStaticMethodID(env,cEmulator,"gameLoaded","()V");
	if(android_gameLoadedCallback == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_gameLoadedCallback");
#endif
		return -1;
	}

	android_gameStoped = (*env)->GetStaticMethodID(env, cEmulator, "gameStoped", "()V");
	if(android_gameStoped == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_gameStoped");
#endif
		return -1;
	}

	android_gameResumed = (*env)->GetStaticMethodID(env, cEmulator, "gameResumed", "()V");
	if(android_gameResumed == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_gameResumed");
#endif
		return -1;
	}

	android_sktNetplayHangup = (*env)->GetStaticMethodID(env,cEmulator, "sktNetplayHangup", "()V");
	if(android_sktNetplayHangup == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_sktNetplayHangup");
#endif
		return -1;
	}

	android_onNetPlayInformationCallback = (*env)->GetStaticMethodID(env, cEmulator, "onNetPlayInformation", "(ILjava/lang/String;IILjava/lang/String;)V");
	if(android_onNetPlayInformationCallback == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_onNetPlayInformationCallback");
#endif
		return -1;
	}

	android_onNetPlayStatus = (*env)->GetStaticMethodID(env, cEmulator, "onNetPlayStatus", "(ILjava/lang/String;)V");
	if(android_onNetPlayStatus == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_onNetPlayStatus");
#endif
		return -1;
	}

	android_onInsertGameInfomation = (*env)->GetStaticMethodID(env, cEmulator, "onInsertGameInfomation", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	if(android_onInsertGameInfomation == NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_onInsertGameInfomation");
#endif
		return -1;
	}
	gamekitSendDataJavaMethod = (*env)->GetStaticMethodID(env,cEmulator,"gamekitSendData","([BI)V");
	if(gamekitSendDataJavaMethod==NULL)
	{
#ifdef DEBUG
		__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method gamekitSendData");
#endif
		return -1;
	}

    android_scanFinishCallbacks = (*env)->GetStaticMethodID(env,cEmulator,"scanFinish","()V");
    if(android_scanFinishCallbacks == NULL)
    {
#ifdef DEBUG
    	__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_scanFinishCallbacks");
#endif
    	return -1;
    }

    android_loadLibFinishCallbacks = (*env)->GetStaticMethodID(env,cEmulator,"loadLibFinish","()V");
    if(android_loadLibFinishCallbacks == NULL)
    {
#ifdef DEBUG
    	__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_loadLibFinishCallbacks");
#endif
    	return -1;
    }

    android_playGamesCallbacks = (*env)->GetStaticMethodID(env,cEmulator,"playGames","()V");
    if(android_playGamesCallbacks == NULL)
    {
#ifdef DEBUG
    	__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Failed to find method android_playGamesCallbacks");
#endif
    	return -1;
    }

   
    return JNI_VERSION_1_4;
}

void* app_Thread_Start(void* args)
{
    android_main(0, NULL); 
    return NULL;
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_initGames()
{
//	__android_log_print(ANDROID_LOG_INFO, "test", "initGames");
	android_main(0, NULL);
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_init
  (JNIEnv *env, jclass c,  jstring rootPath, jstring libPath, jstring resPath)
{
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_INFO, "arcadehub-jni", "init");
#endif

    const char *str1 = (*env)->GetStringUTFChars(env, libPath, 0);

    load_lib(str1);

    (*env)->ReleaseStringUTFChars(env, libPath, str1);
    
#ifdef DEBUG
    __android_log_print(ANDROID_LOG_INFO, "arcadehub-jni","calling setVideoCallbacks");
#endif
    if(setVideoCallbacks!=NULL)
        setVideoCallbacks(&myJNI_initVideo,&myJNI_dumpVideo,&myJNI_changeVideo);
#ifdef DEBUG
    __android_log_print(ANDROID_LOG_INFO, "arcadehub-jni","calling setAudioCallbacks");
#endif
    if(setAudioCallbacks!=NULL)
       setAudioCallbacks(&myJNI_openAudio,&myJNI_dumpAudio,&myJNI_closeAudio);


    if(onHighScoreCallbacks != NULL)
    {
    	onHighScoreCallbacks(&myJNI_hiscore);
    }

    if(onCheatListCallbacks != NULL)
    {
    	onCheatListCallbacks(&myJNI_onCheatList);
    }

    if(gameLoadedCallbacks != NULL)
    {
    	gameLoadedCallbacks(&myJNI_gameLoaded);
    }

    if(gameStopedCallbacks != NULL)
    {
    	gameStopedCallbacks(&myJNI_gameStoped);
    }

    if(gameResumedCallbacks != NULL)
    {
    	gameResumedCallbacks(&myJNI_gameResumed);
    }

    if(sktNetplayHangupCallbacks != NULL)
    {
    	sktNetplayHangupCallbacks(&myJNI_sktNetplayHangup);
    }

	if(onNetPlayInformationCallbacks != NULL)
	{
		onNetPlayInformationCallbacks(&myJNI_onNetPlayInformation);
	}

	if(onNetPlayStatusCallbacks != NULL)
	{
		onNetPlayStatusCallbacks(&myJNI_onNetPlayStatus);
	}

	if(onInsertGameInfomation != NULL)
	{
		onInsertGameInfomation(&myJNI_onInsertGameInfomation);
	}

    if(scanFinishCallbacks != NULL)
    {
    	scanFinishCallbacks(&myJNI_scanFinish);
    }

    if(playGamesCallbacks != NULL)
    {
    	playGamesCallbacks(&myJNI_playGames);
    }


    const char *str2 = (*env)->GetStringUTFChars(env, resPath, 0);

#ifdef DEBUG
    __android_log_print(ANDROID_LOG_INFO, "arcadehub-jni", "path %s",str2);
#endif

    setGlobalPath(str2);

    (*env)->ReleaseStringUTFChars(env, resPath, str2);

    if(sdcardDir) {
    	const char *mPath = (*env)->GetStringUTFChars(env, rootPath, 0);
    	sdcardDir(mPath);
    	(*env)->ReleaseStringUTFChars(env, rootPath, mPath);
    }
    
    __android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Load static finish");
    myJNI_loadLibFinish();

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setPadData
  (JNIEnv *env, jclass c, jint i,  jlong jl)
{

    unsigned long l = (unsigned long)jl;

    if(setPadStatus!=NULL)
    {
       setPadStatus(i,l);
    }
    else
    {
#ifdef DEBUG
      __android_log_print(ANDROID_LOG_WARN, "arcadehub-jni", "error no setPadStatus!");
#endif
    }
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setAnalogData
  (JNIEnv *env, jclass c, jint i, jfloat v1, jfloat v2)
{
    if(setMyAnalogData!=NULL)
    {
       setMyAnalogData(i,v1,v2);
    }
    else
    {
#ifdef DEBUG
    	__android_log_print(ANDROID_LOG_WARN, "arcadehub-jni", "error no setMyAnalogData!");
#endif
    }
}

JNIEXPORT jint JNICALL Java_com_yunluo_android_arcadehub_Emulator_getValue
  (JNIEnv *env, jclass c, jint key, jint i)
{
#ifdef DEBUG
   // __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "getValue %d",key);
#endif
      if(getMyValue!=NULL)
         return getMyValue(key,i);
      else 
      {
#ifdef DEBUG
         __android_log_print(ANDROID_LOG_WARN, "arcadehub-jni", "error no getMyValue!");
#endif
         return -1;
      }
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setValue
  (JNIEnv *env, jclass c, jint key, jint i, jint value)
{
#ifdef DEBUG
//    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "setValue %d,%d=%d",key,i,value);
#endif
    if(setMyValue!=NULL)
    {
      setMyValue(key,i,value);
    }
    else
    {
#ifdef DEBUG
      __android_log_print(ANDROID_LOG_WARN, "arcadehub-jni", "error no setMyValue!");
#endif
    }
}

JNIEXPORT jstring JNICALL Java_com_yunluo_android_arcadehub_Emulator_getValueStr
  (JNIEnv *env, jclass c, jint key, jint i)
{
#ifdef DEBUG
   // __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "getValueStr %d",key);
#endif
      if(getMyValueStr!=NULL)
      {
         const char * r =  getMyValueStr(key,i);
         return (*env)->NewStringUTF(env,r);
      }
      else 
      {
#ifdef DEBUG
         __android_log_print(ANDROID_LOG_WARN, "arcadehub-jni", "error no getMyValueStr!");
#endif
         return NULL;
      }
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setValueStr
  (JNIEnv *env, jclass c, jint key, jint i, jstring s1)
{
    if(setMyValueStr!=NULL)
    {
       const char *value = (*env)->GetStringUTFChars(env, s1, 0);
#ifdef DEBUG
//    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "setValueStr %d,%d=%s",key,i,value);
#endif
       setMyValueStr(key,i,value);
       (*env)->ReleaseStringUTFChars(env, s1, value);
    }
    else
    {
#ifdef DEBUG
      __android_log_print(ANDROID_LOG_WARN, "arcadehub-jni", "error no setMyValueStr!");
#endif
    }
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_runVideoT
  (JNIEnv *env, jclass c){
#ifdef DEBUG
//    __android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "runVideoThread");
#endif
    if(droid_video_thread!=NULL)        
    {
    	droid_video_thread();
    }
    else
    {
#ifdef DEBUG
      __android_log_print(ANDROID_LOG_WARN, "arcadehub-jni", "error no droid_video_thread!");
#endif
    }
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_gamekitReceivedData
(JNIEnv *env, jclass c, jbyteArray b)
{
	jbyte* bytes = (*env)->GetByteArrayElements(env, b,NULL);
	int len = (*env)->GetArrayLength(env, b);
#ifdef DEBUG
	//				__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "jni received_data len=%d",len);
#endif

	if (androidGamekitReceivedData!=NULL){
		androidGamekitReceivedData(bytes, len);
	}

	(*env)->ReleaseByteArrayElements(env, b,bytes,JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_gamekitAction
(JNIEnv *env, jclass c, jint action)
{
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_gamekitAction");
#endif
	if (androidGamekitAction!=NULL){
		androidGamekitAction(action);
	}
}

JNIEXPORT jint JNICALL Java_com_yunluo_android_arcadehub_Emulator_gamekitQueryState
(JNIEnv *env, jclass c, jint query)
{
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_gamekitAction");
#endif
	jint result = 0;
	if (androidGamekitQueryState!=NULL){
		result = androidGamekitQueryState(query);
	}
	return result;
}

JNIEXPORT jstring JNICALL Java_com_yunluo_android_arcadehub_Emulator_gamekitGetGameName
(JNIEnv *env, jclass c, jboolean isServer)
{
	char gamename[20]={0};
	if (androidGamekitGetGamename!=NULL){
		androidGamekitGetGamename(gamename, 20, isServer);
	}
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_gamekitGetGameName=%s",gamename);
#endif
	return (*env)->NewStringUTF(env, gamename);
}
void myJNI_androidGamekitRegisterSendFunction(char* buffer, int size){
	JNIEnv *env;
	jobject tmp;
	(*jVM)->GetEnv(jVM, (void**) &env, JNI_VERSION_1_4);

#ifdef DEBUG
	//	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "myJNI_androidGamekitRegisterSendFunction");
#endif

	if(gamekitSendDataJBuffer==NULL)
	{
		gamekitSendDataJBuffer=(*env)->NewByteArray(env, 1024);
		tmp = gamekitSendDataJBuffer;
		gamekitSendDataJBuffer=(jbyteArray)(*env)->NewGlobalRef(env, gamekitSendDataJBuffer);
		(*env)->DeleteLocalRef(env, tmp);
	}

	(*env)->SetByteArrayRegion(env, gamekitSendDataJBuffer, 0, size, (jbyte *)buffer);

	(*env)->CallStaticVoidMethod(env, cEmulator, gamekitSendDataJavaMethod,gamekitSendDataJBuffer,(jint)size);
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_openCheatItem(JNIEnv *env, jclass c, jint index) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_openCheatItem");
#endif

	if(openCheatItem) {
		openCheatItem(index);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_closeCheatItem(JNIEnv *env, jclass c, jint index) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_closeCheatItem");
#endif

	if(closeCheatItem) {
		closeCheatItem(index);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setStartGame(JNIEnv *env, jclass c, jstring path, jstring romName) {

#ifdef DEBUG
	//	__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_setStartGameAtName path: %s romName: %s", path, romName);
#endif

	if(setMyStartGame) {
		const char *mPath = (*env)->GetStringUTFChars(env, path, 0);
		const char *mName = (*env)->GetStringUTFChars(env, romName, 0);
		setMyStartGame(mPath, mName);
		(*env)->ReleaseStringUTFChars(env, path, mPath);
		(*env)->ReleaseStringUTFChars(env, romName, mName);
	}

	//	__android_log_print(ANDROID_LOG_ERROR, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_setStartGameAtName end:");

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_updateHighScore(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_updateHighScore");
#endif

	if(setUpdateHighScore) {
		setUpdateHighScore();
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_pauseGame(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_pauseGame");
#endif

	if(setPauseGame) {
		setPauseGame();
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_resumeGame(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_resumeGame");
#endif

	if(setResumeGame) {
		setResumeGame();
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setDeviceIP(JNIEnv *env, jclass c, jstring address) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_setDeviceIP");
#endif

	if(setDeviceIP) {
		const char *mAddress = (*env)->GetStringUTFChars(env, address, 0);
		setDeviceIP(mAddress);
		(*env)->ReleaseStringUTFChars(env, address, mAddress);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setWIFIGame(JNIEnv *env, jclass c, jstring name) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_setWIFIGame");
#endif

	if(setWIFIGame) {
		const char *mName = (*env)->GetStringUTFChars(env, name, 0);
		setWIFIGame(mName);
		(*env)->ReleaseStringUTFChars(env, name, mName);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setBluetoochGame(JNIEnv *env, jclass c, jstring name) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_setBluetoochGame");
#endif

	if(setBluetoochGame) {
		const char *mName = (*env)->GetStringUTFChars(env, name, 0);
		setBluetoochGame(mName);
		(*env)->ReleaseStringUTFChars(env, name, mName);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_sendMessageToSocket(JNIEnv *env, jclass c, jint action)
{
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_sendMessageToSocket");
#endif
	if (setSendMessageToSocket != NULL ){
		setSendMessageToSocket(action);
	}
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_saveAndLoadFileName(JNIEnv *env, jclass c, jstring fileName) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_saveAndLoadFileName");
#endif

	if(saveAndLoadFileName) {
		const char *mFileName = (*env)->GetStringUTFChars(env, fileName, 0);
		saveAndLoadFileName(mFileName);
		(*env)->ReleaseStringUTFChars(env, fileName, mFileName);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_saveGame(JNIEnv *env, jclass c, jstring fileName) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_saveGame");
#endif

	if(saveGame) {
		const char *mFileName = (*env)->GetStringUTFChars(env, fileName, 0);
		saveGame(mFileName);
		(*env)->ReleaseStringUTFChars(env, fileName, mFileName);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_loadGame(JNIEnv *env, jclass c, jstring fileName) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_loadGame");
#endif

	if(loadGame) {
		const char *mFileName = (*env)->GetStringUTFChars(env, fileName, 0);
		loadGame(mFileName);
		(*env)->ReleaseStringUTFChars(env, fileName, mFileName);
	}

}

JNIEXPORT jstring JNICALL Java_com_yunluo_android_arcadehub_Emulator_getArchivePath(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_getArchivePath");
#endif

	if(getArchivePath) {
		const char *mPath = getArchivePath();
		return (*env)->NewStringUTF(env, mPath);
	}
	else
	{
		return NULL;
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_broadcastDeviceInformation(JNIEnv *env, jclass c, jint os, jstring ip, jint cpu, jint ram, jstring rom) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_broadcastDeviceInformation");
#endif

	if(setBroadcastDeviceInformation) {
		const char *mIp = (*env)->GetStringUTFChars(env, ip, 0);
		const char *mRom = (*env)->GetStringUTFChars(env, rom, 0);
		setBroadcastDeviceInformation(os, mIp, cpu, ram, mRom);
		(*env)->ReleaseStringUTFChars(env, ip, mIp);
		(*env)->ReleaseStringUTFChars(env, rom, mRom);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_stopBroadcast(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_stopBroadcast");
#endif

	if(setStopBroadcast) {
		setStopBroadcast();
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_resetGame(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_resetGame");
#endif

	if(resetGame) {
		resetGame();
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_closeGame(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_closeGame");
#endif

	if(closeGame) {
		closeGame();
	}
	else
	{
		return NULL;
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_startScan(JNIEnv *env, jclass c, jstring path) {

#ifdef DEBUG
//	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_startScan");
#endif

	if(startScan) {
		const char *mPath = (*env)->GetStringUTFChars(env, path, 0);
		startScan(mPath);
		(*env)->ReleaseStringUTFChars(env, path, mPath);
	}
	else
	{
		return NULL;
	}

}

JNIEXPORT jstring JNICALL Java_com_yunluo_android_arcadehub_Emulator_getGame(JNIEnv *env, jclass c, jstring name, jint type) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_getGame");
#endif

	if(getGame) {
		const char *mName = (*env)->GetStringUTFChars(env, name, 0);
		const char *mDesc = getGame(mName, type);
		(*env)->ReleaseStringUTFChars(env, name, mName);
		return (*env)->NewStringUTF(env, mDesc);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_test(JNIEnv *env, jclass c) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_test");
#endif

	if(test) {
		test();
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_stopScan(JNIEnv *env, jclass c, jint flag) {

#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_stopScan");
#endif

	if(stopScan) {
		stopScan(flag);
	}

}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_hintTransferSendRomCB(JNIEnv *env, jclass c, jstring path) {
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_hintTransferSendRomCB");
#endif

	if(hintTransferSendRomCB) {
		const char *mPath = (*env)->GetStringUTFChars(env, path, 0);
		hintTransferSendRomCB(mPath);
		(*env)->ReleaseStringUTFChars(env, path, mPath);
	}
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setMultiKey(JNIEnv *env, jclass c, jint pressedBtn, jintArray multiKeyArray, jint length) {
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_setMultiKey");
#endif

	if(setMultiKey) {
		jint* arr;
		jint length;
		arr = (*env)->GetIntArrayElements(env,multiKeyArray,NULL);
		length = (*env)->GetArrayLength(env,multiKeyArray);

		setMultiKey(pressedBtn, arr, length);
	}
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_restMultiKey(JNIEnv *env, jclass c) {
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_restMultiKey");
#endif

	if(restMultiKey) {
		restMultiKey();
	}
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_setMultiPress(JNIEnv *env, jclass c, jint pressedBtn, jintArray multiPressArray, jint length) {
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_setMultiPress");
#endif

	if(setMultiPress) {
		jint* arr;
		jint length;
		arr = (*env)->GetIntArrayElements(env,multiPressArray,NULL);
		length = (*env)->GetArrayLength(env,multiPressArray);

		setMultiPress(pressedBtn, arr, length);
	}
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_restMultiPress(JNIEnv *env, jclass c, jint key) {
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_restMultiPress %d = ", key);
#endif

	if(restMultiPress) {
		restMultiPress(key);
	}
}

JNIEXPORT void JNICALL Java_com_yunluo_android_arcadehub_Emulator_getVaildBtn(JNIEnv *env, jclass c, jintArray inputKeyArray, jint length) {
#ifdef DEBUG
	__android_log_print(ANDROID_LOG_DEBUG, "arcadehub-jni", "Java_com_yunluo_android_arcadehub_Emulator_getVaildBtn");
#endif

	if(getVaildBtn) {
		jint* arr;
		jint length;
		arr = (*env)->GetIntArrayElements(env,inputKeyArray,NULL);
		length = (*env)->GetArrayLength(env,inputKeyArray);

		getVaildBtn(arr, length);
	}
}
