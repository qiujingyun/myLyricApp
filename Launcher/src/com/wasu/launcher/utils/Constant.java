package com.wasu.launcher.utils;

public class Constant {

	public static final int MOVIE_DETAILS = 20;
	public static final int MOVIE_SUB_DETAILS = 21;
	public static final int SERIES_DETAILS = 22;
	public static final int SERIES_SUB_DETAILS = 23;
	public static final int PLAYBACK_DETAILS = 24;
	public static final int PLAYBACK_SUB_DETAILS = 25;
	
	public static final int NEWS = 0;
	public static final int SPORTS = 1;
	public static final int ENTAINMENT = 2;
	public static final int RECORD = 3;
	public static final int LIVE = 4;
	public static final int COLUMN = 5;
	public static final int MOVIE = 6;
	public static final int SERIES = 7;
	public static final int MINOR = 8;
	public static final int MUSIC = 9;
	public static final int EDUCATION = 10;
	public static final int VIP = 11;
	public static final int COLLECT = 12;
	public static final int HISTORY = 13;
	public static final int LAUNCHER = 14;
	public static final int PLAYBACK = 15;
	
	public static final int ALL = 255;
	
	
	public final static int QUERY_FOLDER = 0x100;
	public final static int QUERY_CHILDFOLDER = 0x200;
	public final static int QUERY_RANKLIST = 0x300;
	public final static int QUERY_RECOMMEND = 0x400;
	public final static int QUERY_SEARCH = 0x500;
	public final static int QUERY_FILTER = 0x600;
	public final static int QUERY_HISTORY = 0x700;
	public final static int QUERY_COLLECT = 0x800;
	public final static int QUERY_COLLECTFOLDER = 0x900;
	
	public final static int QUERY_PLAYBACK = 0x1000;
	
	public final static int QUERY_MOVIEDETAILS = 0x1100;
	public final static int QUERY_SERIESDETAILS = 0x1200;
	public final static int QUERY_PLAYBACKDETAILS = 0x1300;
	
	public final static int QUERY_BIGDATA = 0x1400;
	
	public final static int QUERY_DELCOLLECT = 0x1500;
	public final static int QUERY_DELCOLLECTFOLDER = 0x1600;
	
	public final static int QUERY_ADDCOLLECT = 0x1700;
	public final static int QUERY_ADDCOLLECTFOLDER = 0x1800;
	
	public final static int QUERY_IP = 0x1900;
	public final static int QUERY_PLAYURL = 0x2000;
	
	public final static int QUERY_ADDHISTORY = 0x2100;
	public final static int QUERY_ADDHISTORYFOLDER = 0x2200;
	
	public final static int QUERY_DELHISTORY = 0x2300;
	public final static int QUERY_DELHISTORYFOLDER = 0x2400;
	
	public final static int QUERY_RECENTPLAY = 0x2500;
	
	public final static int QUERY_INVALID = 0x5000;
	
	
	public final static String TYPE_MOVIE ="36|10|1";
	public final static String TYPE_SERIES = "37|11|2";
	public final static String TYPE_COLUMN = "13";
	public final static String TYPE_MUSIC = "16";
	public final static String TYPE_NEWS = "15";
	
	
	public final static String IMAGE_TYPE_POSTER = "6|17";
	
	public final static String PREFIX_RECOMMEND = "RECOMMEND.";
	public final static String PREFIX_CHILD = "CHILD.";
	public final static String PREFIX_RANKLIST = "RANK.";
	public final static String PREFIX_HISTORY = "HISTORY.";
	public final static String PREFIX_COLLECT = "COLLECT.";
	
	
	public final static int PLAYER_LIVE = 0;
	public final static int PLAYER_VOD = 1;
	public final static int PLAYER_PLAYBACK = 2;
	
	
	public final static int RECOMMEND_IMAGE = 1;
	public final static int RECOMMEND_TEXT = 2;
	public final static int RECOMMEND_IMAGEANDTEXT = 3;
	public final static int RECOMMEND_VOD_VIDEO = 4;
	public final static int RECOMMEND_LIVE_VIDEO = 5;
	
	public final static int SKYWORTH_QUIT = 5011;

	/**
	 *	排行榜
	 */
//	public final static String RANK_CODE_DAY = "RECOMMEND.";
	public final static String RANK_CODE_WEEK = "WEEK";
//	public final static String RANK_CODE_MONTH = "RECOMMEND.";
	public final static String RANK_CODE_REAL = "REAL";
	
}
