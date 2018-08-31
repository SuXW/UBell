
package cn.ubia.widget;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

public class ScoreAnimationCtrl {
    public final static int MAX_SCORE_DEFAULT = 100;
    
    private int mStartScore = 0;
    private int mScore = 0;
    private int mMaxScore = MAX_SCORE_DEFAULT;
    
    private TextView mTvScore = null;
    private IOnProgressListener mListener = null;
    
    public interface IOnProgressListener{
        public void onProgress(float fScorePercent, int nStepInterval, float fStep);
    }
    
    public ScoreAnimationCtrl(TextView tvScore){
        mTvScore = tvScore;
    }
    
    public void setScore(int nScore, int nMaxScore){
        mMaxScore = nMaxScore;
        setScore(nScore);
    }
    
    public void setScore(int nScore) {
        mScore = nScore;
    }
    
    public float getScorePercent(int nScore){
        return ((float)mScore)/mMaxScore;
    }
    
    public void setOnProgressListener(IOnProgressListener l){
        mListener = l;
    }
    
    /**
     * 设置当前分值并启动动画效果。<br>
     * 满分值由setMaxScore提前设置，未设置默认为100
     * 
     * @param nScore
     */
    public void setScoreAndStartAnimation(int nScore){
        setScore(nScore);  
        if(mStartScore == mScore) return;
        
        startScoreAnimation();
    }
    
    public void startScoreAnimation() {
        ScoreRotateAnimation anim = new ScoreRotateAnimation();
        anim.execute(mStartScore,mScore);
        mStartScore = mScore;
    }

    private class ScoreRotateAnimation extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            if ((params == null) || (params.length < 2)) return null;
            
            int nScoreStart = params[0]; 
            int nScoreEnd = params[1]; 
            
            if(nScoreStart > nScoreEnd){
                nScoreStart = 0;
            }
            
            final int STEP_INTERVAL = 10;
            
            for (int i_score = nScoreStart; i_score <= nScoreEnd; i_score ++) {
                publishProgress(i_score,STEP_INTERVAL);
                try {
                    Thread.sleep(STEP_INTERVAL);
                }
                catch (InterruptedException e) {
                }
            }
            
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            float fScorePercent = 0f;
            if(values[0] != 0){
                fScorePercent = values[0]/(float)mMaxScore;
            }
            
            float fStep = 1/(float)mMaxScore;
            int nStepInterval = 20;
            
            if(values.length > 1){
                if(values[1] != 0){
                    nStepInterval = values[1];
                }
            }
            
            if(values.length > 2){
                if(values[2] != 0){
                    fStep = values[2]/(float)mMaxScore;
                }
            }
            

            if(mTvScore != null){
                mTvScore.setText(String.valueOf(values[0]));
                mTvScore.setVisibility(View.VISIBLE);
            }
            
            if(mListener != null){
                mListener.onProgress(fScorePercent,nStepInterval,fStep);
            }
        }
    }

    public static void rotateBgCircle(View vRotateCircle, float fScorePercent, 
            int nStepInterval, float fStep) {
        if (vRotateCircle == null) return;
        
        final float ONESCORE_DEGREE = 3.6f;
        float fFromeDegree = ONESCORE_DEGREE*(fScorePercent - fStep)*100;
        float fToDegree = ONESCORE_DEGREE*fScorePercent*100;

        RotateAnimation animRotate = new RotateAnimation(
                fFromeDegree, fToDegree,Animation.RELATIVE_TO_SELF, 
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animRotate.setDuration(nStepInterval/2);
        animRotate.setFillAfter(true);
        vRotateCircle.startAnimation(animRotate);
    }
    
    public static int getPercentColor(int[] aColors, float fPosPercent){
        if ((aColors == null) || (aColors.length == 0)){
            return Color.GREEN;
        }
        
        fPosPercent *= 100;
        
        int nColor = aColors[0];
        int nStepPos = 100/aColors.length;
        for (int i_step = 0; i_step < aColors.length; i_step ++) {
            if (fPosPercent >= nStepPos*i_step) {
                nColor = aColors[i_step];
            }else{
                break;
            }
        }
        
        return nColor;
    }
}
