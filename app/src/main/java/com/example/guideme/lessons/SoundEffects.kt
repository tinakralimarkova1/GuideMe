package com.example.guideme.lessons
import android.content.Context
import android.media.SoundPool
import com.example.guideme.R

object Sfx {
    private var soundPool: SoundPool? = null
    private var correctSoundId: Int = 0
    private var wrongSoundId: Int = 0

    private var completeSoundId: Int = 0

    private var clickSoundId: Int = 0

    fun init(context: Context) {
        soundPool = SoundPool.Builder().setMaxStreams(2).build()
        correctSoundId = soundPool!!.load(context, R.raw.correct, 1)
        wrongSoundId = soundPool!!.load(context, R.raw.wrong, 1)
        completeSoundId = soundPool!!.load(context, R.raw.complete, 1)
        clickSoundId = soundPool!!.load(context, R.raw.click, 1)
    }

    fun playCorrect() {
        soundPool?.play(correctSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playWrong() {
        soundPool?.play(wrongSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playComplete(){
        soundPool?.play(completeSoundId,1f,1f,1,0,1f)
    }
    fun playClick(){
        soundPool?.play(clickSoundId,1f,1f,1,0,1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
