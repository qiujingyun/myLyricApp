package com.wasu.launcher.utils;

import org.davic.net.Locator;
import org.ngb.media.MediaManager;
import org.ngb.media.Player;
import org.ngb.media.PlayerEvent;
import org.ngb.media.PlayerListener;
import org.ngb.media.VODEvent;

import android.graphics.Rect;
import android.util.Log;

/**
 * paly 单例
 * 
 * @author 77071
 *
 */
public class PlayUtils {
	private static PlayUtils playUtils = null;// 当前类
	private MediaManager mmManager;// 启动manager
	private Player play = null;// 播放器资源
	public boolean isplay = false;// 是否在播放
	private String strUrl = "";// rul
	private Rect rect;// 位置
	private onPlayListener onPlay;// 播放listener
	private boolean isAddListener = false;// 是否添加Listener
	private long time = 0;// 跳转时间
	private boolean isDeallocate = true;// 是否重新创建
	private boolean isLive = false;// 是否是直播
	private String playString = "";// 点播地址

	private long lowTime = 0;// 老的时间

	private int retryCount = 5;// 重置次数

	/**
	 * 初始化
	 * 
	 * @return
	 */
	public static PlayUtils getInstance() {
		if (playUtils == null) {
			synchronized (PlayUtils.class) {
				if (playUtils == null) {
					playUtils = new PlayUtils();
					try {
//						playUtils.mmManager = MediaManager.getInstance();
						Log.d(PlayUtils.class.getSimpleName(), "initplay");
					} catch (java.lang.ExceptionInInitializerError e) {
						Log.e(PlayUtils.class.getSimpleName(), e.toString());
					}
				}
			}
		}
		return playUtils;
	}

	/**
	 * 停止播放
	 */
	public void playstop() {
		if (play != null) {
			Log.d(this.getClass().getSimpleName(), "Line 25  playstop");
			play.stop();
			isplay = false;
		}
	}

	/**
	 * 暂停播放
	 */
	public void playpause() {
		if (play != null) {
			isplay = false;
			play.pause();
			Log.d(this.getClass().getSimpleName(), "Line 33  playpause");
		}
	}

	/**
	 * 清空 play 对象
	 */
	public void playdeallocate() {
		if (isDeallocate) {
			playstop();
			if (play != null) {
				if (isAddListener) {
					play.removeListener(mPlayerListener);
					Log.d(this.getClass().getSimpleName(), "removeListener1");
					isAddListener = false;
				}
				play.deallocate();
				play = null;
				time = 0;
				Log.d(this.getClass().getSimpleName(), "Line 39  playdeallocate");
			}
		}
		isDeallocate = true;
	}

	/***
	 * 清空playUtils
	 */
	public void deallocate() {
		playdeallocate();
		if (playUtils != null) {
			playUtils.onPlay = null;
			playUtils.play = null;
			playUtils.mmManager = null;
			playUtils = null;
		}
	}

	/** 再播放 */
	public synchronized void rPlayer() {
		// playdeallocate();
		Log.d("rPlayer", "strUrl::" + strUrl);
		if (!"".equals(strUrl)) {
			new a().start();
		}
	}

	/** 再播放 */
	public synchronized void rPlayer(Rect rect) {
		this.rect = rect;
		// playdeallocate();
		if (!"".equals(strUrl)) {
			new a().start();
		}
	}

	/**
	 * 播放
	 * 
	 * @param url
	 *            播放地址
	 * @param rect
	 *            播放位置
	 * @param isLive
	 *            是否是直播
	 * @throws InstantiationError
	 * @throws java.lang.NoSuchMethodError
	 * @throws java.lang.UnsatisfiedLinkError
	 */
	public void hplayer(String url, Rect rect, boolean isLive) {
		this.isLive = isLive;
		strUrl = url;

		this.rect = rect;
		if (mmManager == null) {
			Log.d(this.getClass().getSimpleName(), "Line 62  mmManager null");
//			mmManager = MediaManager.getInstance();
		} else {
			try {

				Locator k = new Locator(url);
				if (play != null) {
					Log.d(this.getClass().getSimpleName(), "Line 80  setDataSource" + rect.toString());
					play.stop();
					play.setDataSource(k);
				} else {
					Log.d(this.getClass().getSimpleName(), "Line 85  createPlayer: " + rect.toString() + ".." + k.toString());
//					play = mmManager.createPlayer(k);

//					play.addListener(mPlayerListener);
					isAddListener = true;

					Log.d(this.getClass().getSimpleName(), "addListener1");
				}
//				play.getVideoControl().setBounds(rect);
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}

			if (!isLive && time != 0) {
//				play.setAbsoluteTimePosition(time);
				Log.d(this.getClass().getSimpleName(), "time" + time + "");
			}

			isplay = true;
			Log.d(this.getClass().getSimpleName(), "Line 96  start: " + url);
			if(play!=null){
				play.start();
			}
		}
	}

	public Player getPlay() {
		return play;
	}

	public void setPlay(Player play) {
		this.play = play;
	}

	public MediaManager getMmManager() {
		return mmManager;
	}

	public void setMmManager(MediaManager mmManager) {
		this.mmManager = mmManager;
	}

	private PlayerListener mPlayerListener = new PlayerListener() {

		@Override
		public void OnPlayerEvent(PlayerEvent playerEvent) {
			Log.e("mPlayerListener", onPlay + "\t Type:" + (playerEvent.getType()) + "\t Reason:" + (playerEvent.getReason()) + "\t time:"
					+ time);
			if (playerEvent.getType() == PlayerEvent.TYPE_STOP && playerEvent.getReason() == VODEvent.REASON_VOD_ANNOUNCE) {// 播放完成
				if (!isLive) {
					time = 0;
				}
				if (onPlay != null) {
					onPlay.OnPlayListener(0);
				}
				playUtils.rPlayer();
				Log.e("rPlayer", "+++++++OnPlayerEventrPlayer");
			} else if (playerEvent.getType() == PlayerEvent.TYPE_SUCCESS) {// 播放开始
				if (!isLive) {
					retryCount = 5;
					lowTime = System.currentTimeMillis();
				}
				isplay=true;
				if (onPlay != null) {
					onPlay.OnPlayListener(1);
				}
				// 没播了 播放停止
			} else if (playerEvent.getType() == 60 && playerEvent.getReason() == 3) {
				Log.e(this.getClass().getSimpleName(), "lowTime:" + lowTime);
				if (lowTime != 0) {
					time = System.currentTimeMillis() - lowTime + time;
				}
			} else if (playerEvent.getType() == VODEvent.TYPE_VOD_ERROR) {// 播放错误
				if (onPlay != null) {
					onPlay.OnPlayListener(-1);
				}
				if (!isLive) {
					if (retryCount > 0) {
						rPlayer();
						retryCount--;
					}

				}
			}
		}

	};

	/**
	 * 再播放线程
	 * 
	 * @author 77071
	 *
	 */
	class a extends Thread implements Runnable {
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(300);
				hplayer(strUrl, rect, false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/***/
	public interface onPlayListener {
		public void OnPlayListener(int i);
	}

	public onPlayListener getOnPlay() {
		return onPlay;
	}

	public void setOnPlay(onPlayListener onPlay) {
		this.onPlay = onPlay;
	}

	/** 是否调用Deallocate方法 */
	public void setDeallocate(boolean isDeallocate) {
		this.isDeallocate = isDeallocate;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public void setPlayArea(Rect rect) {
		if (play != null) {
			play.getVideoControl().setBounds(rect);
		}
	}
}
