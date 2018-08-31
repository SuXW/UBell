package com.ubia.IOTC;

import java.nio.ByteBuffer; 

import android.media.AudioFormat; 
import android.media.AudioRecord; 
import android.media.MediaRecorder.AudioSource; 
import android.os.Process; 
 
/** 
 * Basically what this class does is construct a valid AudioRecord Object, wrap AudioRecord methods, and provide 
 * conversion to decibels. It also caches the AudioRecord configuration and prevents multiple instances of the recorder. 
 *  
 * @author Michael-Pardo/billhoo 
 * @version 0.1 2013-2-18 
 */ 
// TODO(billhoo) the AudioMeter.createAudioRecord() method always returned the first matched configuration currently, 
// if the first combination [8000Hz + PCM16 + IN_MONO] is valid, method returns, but there is no way if I wanna use 
// 16000Hz instead, so this method should be changed to a clever one in the future. 
public class AudioMeter extends Thread { 
 
    // /////////////////////////////////////////////////////////////// 
    // PRIVATE CONSTANTS 
 
    private static final float MAX_REPORTABLE_AMP = 32767f; 
    private static final float MAX_REPORTABLE_DB  = 90.3087f; 
 
    // /////////////////////////////////////////////////////////////// 
    // PRIVATE MEMBERS 
 
    private AudioRecord        mAudioRecord; 
    private int                mSampleRate; 
    private short              mAudioFormat; 
    private short              mChannelConfig; 
 
    private short[]            mBuffer; 
    private int                mBufferSize        = AudioRecord.ERROR_BAD_VALUE; 
 
    private int                mLocks             = 0; 
 
    // /////////////////////////////////////////////////////////////// 
    // CONSTRUCTOR 
 
    private AudioMeter() { 
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO); // set the AudioMeter thread in URGENT_AUDIO 
                                                                         // priority. 
        createAudioRecord(); 
    } 
 
    // /////////////////////////////////////////////////////////////// 
    // PUBLIC METHODS 
 
    public static AudioMeter getInstance() { 
        return InstanceHolder.INSTANCE; 
    } 
 
    public float getAmplitude() { 
        return (float) (MAX_REPORTABLE_DB + (20 * Math.log10(getRawAmplitude() 
                / MAX_REPORTABLE_AMP))); 
    } 
 
    public synchronized void startRecording() { 
        if (mAudioRecord == null 
                || mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) { 
            throw new IllegalStateException( 
                    "startRecording() called on an uninitialized AudioRecord."); 
        } 
 
        if (mLocks == 0) { 
            mAudioRecord.startRecording(); 
        } 
 
        mLocks++; 
    } 
 
    public synchronized void stopRecording() { 
        mLocks--; 
 
        if (mLocks == 0) { 
            if (mAudioRecord != null) { 
                mAudioRecord.stop(); 
                mAudioRecord.release(); 
                mAudioRecord = null; 
            } 
        } 
    } 
 
    /** 
     * Reads audio data from the audio hardware for recording into a direct buffer. If this buffer is not a direct 
     * buffer, this method will always return 0. 
     *  
     * @param audioBuffer 
     *            the direct buffer to which the recorded audio data is written. 
     * @param sizeInBytes 
     *            the number of requested bytes. 
     * @return the number of bytes that were read or or {@link #ERROR_INVALID_OPERATION} if the object wasn't properly 
     *         initialized, or {@link #ERROR_BAD_VALUE} if the parameters don't resolve to valid data and indexes. The 
     *         number of bytes will not exceed sizeInBytes. 
     */ 
    public int read(ByteBuffer audioBuffer, int sizeInBytes) { 
        return mAudioRecord.read(audioBuffer, sizeInBytes); 
    } 
 
    /** 
     * Reads audio data from the audio hardware for recording into a buffer. 
     *  
     * @param audioData 
     *            the array to which the recorded audio data is written. 
     * @param offsetInShorts 
     *            index in audioData from which the data is written expressed in shorts. 
     * @param sizeInShorts 
     *            the number of requested shorts. 
     * @return the number of shorts that were read or or {@link #ERROR_INVALID_OPERATION} if the object wasn't properly 
     *         initialized, or {@link #ERROR_BAD_VALUE} if the parameters don't resolve to valid data and indexes. The 
     *         number of shorts will not exceed sizeInShorts. 
     */ 
    public int read(short[] audioData, int offsetInShorts, int sizeInShorts) { 
        return mAudioRecord.read(audioData, offsetInShorts, sizeInShorts); 
    } 
 
    /** 
     * Reads audio data from the audio hardware for recording into a buffer. 
     *  
     * @param audioData 
     *            the array to which the recorded audio data is written. 
     * @param offsetInBytes 
     *            index in audioData from which the data is written expressed in bytes. 
     * @param sizeInBytes 
     *            the number of requested bytes. 
     * @return the number of bytes that were read or or {@link #ERROR_INVALID_OPERATION} if the object wasn't properly 
     *         initialized, or {@link #ERROR_BAD_VALUE} if the parameters don't resolve to valid data and indexes. The 
     *         number of bytes will not exceed sizeInBytes. 
     */ 
    public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) { 
        return mAudioRecord.read(audioData, offsetInBytes, sizeInBytes); 
    } 
 
    /** 
     * Returns the configured audio data sample rate in Hz 
     */ 
    public int getSampleRate() { 
        return mSampleRate; 
    } 
 
    /** 
     * Returns the configured audio data format. See {@link AudioFormat#ENCODING_PCM_16BIT} and 
     * {@link AudioFormat#ENCODING_PCM_8BIT}. 
     */ 
    public int getAudioFormat() { 
        return mAudioFormat; 
    } 
 
    /** 
     * Returns the configured channel configuration. See {@link AudioFormat#CHANNEL_IN_MONO} and 
     * {@link AudioFormat#CHANNEL_IN_STEREO}. 
     */ 
    public int getChannelConfiguration() { 
        return mChannelConfig; 
    } 
 
    /** 
     * Returns the state of the AudioRecord instance. This is useful after the AudioRecord instance has been created to 
     * check if it was initialized properly. This ensures that the appropriate hardware resources have been acquired. 
     *  
     * @see AudioRecord#STATE_INITIALIZED 
     * @see AudioRecord#STATE_UNINITIALIZED 
     */ 
    public int getAudioRecordState() { 
        return mAudioRecord.getState(); 
    } 
 
    // /////////////////////////////////////////////////////////////// 
    // PRIVATE METHODS 
 
    private void createAudioRecord() { 
        if (mSampleRate > 0 && mAudioFormat > 0 && mChannelConfig > 0) { 
            mAudioRecord = new AudioRecord(AudioSource.MIC, mSampleRate, 
                    mChannelConfig, mAudioFormat, mBufferSize); 
            return; 
        } 
 
        // TODO(billhoo) should try user's specific combinations first, if it's invalid, then do for loop to get a 
        // available combination instead. 
 
        // Find best/compatible AudioRecord 
        // If all combinations are invalid, throw IllegalStateException 
        for (int sampleRate : new int[] { 8000, 11025, 16000, 22050, 32000, 
                44100, 47250, 48000 }) { 
            for (short audioFormat : new short[] { 
                    AudioFormat.ENCODING_PCM_16BIT, 
                    AudioFormat.ENCODING_PCM_8BIT }) { 
                for (short channelConfig : new short[] { 
                        AudioFormat.CHANNEL_IN_MONO, 
                        AudioFormat.CHANNEL_IN_STEREO, 
                        AudioFormat.CHANNEL_CONFIGURATION_MONO, 
                        AudioFormat.CHANNEL_CONFIGURATION_STEREO }) { 
 
                    // Try to initialize 
                    try { 
                        mBufferSize = AudioRecord.getMinBufferSize(sampleRate, 
                                channelConfig, audioFormat); 
 
                        if (mBufferSize < 0) { 
                            continue; 
                        } 
 
                        mBuffer = new short[mBufferSize]; 
                        mAudioRecord = new AudioRecord(AudioSource.MIC, 
                                sampleRate, channelConfig, audioFormat, 
                                mBufferSize); 
 
                        if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) { 
                            mSampleRate = sampleRate; 
                            mAudioFormat = audioFormat; 
                            mChannelConfig = channelConfig; 
                            return; 
                        } 
 
                        mAudioRecord.release(); 
                        mAudioRecord = null; 
                    } catch (Exception e) { 
                        // Do nothing 
                    } 
                } 
            } 
        } 
 
        // ADDED(billhoo) all combinations are failed on this device. 
        throw new IllegalStateException( 
                "getInstance() failed : no suitable audio configurations on this device."); 
    } 
 
    private int getRawAmplitude() { 
        if (mAudioRecord == null) { 
            createAudioRecord(); 
        } 
 
        final int bufferReadSize = mAudioRecord.read(mBuffer, 0, mBufferSize); 
 
        if (bufferReadSize < 0) { 
            return 0; 
        } 
 
        int sum = 0; 
        for (int i = 0; i < bufferReadSize; i++) { 
            sum += Math.abs(mBuffer[i]); 
        } 
 
        if (bufferReadSize > 0) { 
            return sum / bufferReadSize; 
        } 
 
        return 0; 
    } 
 
    // /////////////////////////////////////////////////////////////// 
    // PRIVATE CLASSES 
 
    // Singleton 
    private static class InstanceHolder { 
        private static final AudioMeter INSTANCE = new AudioMeter(); 
    } 
} 