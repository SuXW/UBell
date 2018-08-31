package cn.ubia.util;

import voice.encoder.VoicePlayer;

 

public class MyVoicePlayer {
	   static {
	        System.loadLibrary("voiceRecog");
	    }

		private static VoicePlayer player = null;
	 
		public synchronized static VoicePlayer getInstance() {
			if (null == player) {
				synchronized (MyVoicePlayer.class) {
					   player = new VoicePlayer(); 
					 
				}
			}
			return player;
		}
}
