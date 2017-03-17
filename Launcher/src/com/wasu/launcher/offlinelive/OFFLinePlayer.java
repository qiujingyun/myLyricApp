package com.wasu.launcher.offlinelive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.davic.net.dvb.DvbNetworkBoundLocator;
import org.ngb.broadcast.dvb.si.SIBouquet;
import org.ngb.broadcast.dvb.si.SICommonInformation;
import org.ngb.broadcast.dvb.si.SIElementaryStream;
import org.ngb.broadcast.dvb.si.SIEvent;
import org.ngb.broadcast.dvb.si.SIFailureRetrieveEvent;
import org.ngb.broadcast.dvb.si.SINetwork;
import org.ngb.broadcast.dvb.si.SIRequestFailureType;
import org.ngb.broadcast.dvb.si.SIRetrieveEvent;
import org.ngb.broadcast.dvb.si.SIRetrieveListener;
import org.ngb.broadcast.dvb.si.SIService;
import org.ngb.broadcast.dvb.si.SISuccessRetrieveEvent;
import org.ngb.broadcast.dvb.si.SITime;
import org.ngb.broadcast.dvb.si.SITransportStream;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.utils.PlayUtils;

public class OFFLinePlayer extends BaseActivity implements SIRetrieveListener,
		OnItemClickListener {
	// private SIDatabase mSIDatabase;
	List<SIService> mylist = null;
	ListView pListv;
	int position = 0;
	View view;
	Rect rect;
	private TextView textView1;
	private TextView textView3;
	private TextView textView4;
	private TextView textView5;
	private TextView textView6;
	private TextView textView7;
	private TextView textView8;
	private TextView textView10;
	Calendar c = Calendar.getInstance();
	private MyDialog dialog = null, DialogNuber = null, dialog2 = null;
	SurfaceView surfaceView;
	boolean isStop = true;
	String mas1;
	Date d;
	SimpleDateFormat sdf2;
	int year = 0;
	int month = 0;
	int day = 0;
	int hour = 0;
	int minute = 0;
	int second = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lau_player);
		initView();
		setListener();
		Intent intent = getIntent();
		position = intent.getIntExtra("position", 0);

		// timerun();
	}

	PBaseAdapter pAdapter;
	/** 判断是否调用刷新界面 */
	boolean isSINetwork = false;
	int width, height;

	@Override
	protected void initView() {
		// TODO initView
		WindowManager wm = this.getWindowManager();
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);

		mylist = new ArrayList<SIService>();
		// if (evmtvManager.getInstance() != null) {
		// mSIDatabase = Utils.getDefaultSIDatabase();// 获取节目列表
		// mSIDatabase.retrieveNetworks(null, this, 0xffff, null);// 获取节目列表
		// }
		// pAdapter = new PBaseAdapter(this, mylist);
		// pListv.setAdapter(pAdapter);
	}

	// public class Test {
	// public void main(String[] args) {
	//
	//
	// // Calendar c=Calendar.getInstance();//获得系统当前日期
	// year = c.get(Calendar.YEAR);
	// month = c.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
	// day = c.get(Calendar.DAY_OF_MONTH);
	// hour=c.get(Calendar.HOUR);
	// minute=c.get(Calendar.MINUTE);
	// second=c.get(Calendar.SECOND);
	// Log.e("shijian", year + month + day + "");
	// }
	//
	// }

	@Override
	protected void loadViewLayout() {
		// TODO loadViewLayout

	}

	@Override
	protected void findViewById() {
		// TODO findViewById

	}

	@Override
	protected void setListener() {
		// TODO setListener

	}

	@Override
	public void postEvent(SIRetrieveEvent event) {
		// TODO postEvent

		Log.d("TAG", "event：" + event);

		// TODO 发送SI信息获取事件
		if (event instanceof SISuccessRetrieveEvent) {
			@SuppressWarnings("rawtypes")
			Enumeration enumeration = ((SISuccessRetrieveEvent) event)
					.getResult();
			if (!enumeration.hasMoreElements()) {
				Log.d("TAG", "SISuccessRetrieveEvent nothing");
			}

			while (enumeration.hasMoreElements()) {
				SICommonInformation sicommoninfo = (SICommonInformation) enumeration
						.nextElement();

				// 获取节目列表
				if (sicommoninfo instanceof SINetwork) {
					DvbNetworkBoundLocator[] locaters = ((SINetwork) sicommoninfo)
							.getServicesLocators();
					for (DvbNetworkBoundLocator locater : locaters) {
						SIService service = ((SINetwork) sicommoninfo)
								.getService(locater);
						// TODO 添加数据 刷新
						mylist.add(service);
					}
					// EpgTest.removeDuplicateWithOrder(mylist);
					isSINetwork = true;
				} else if (sicommoninfo instanceof SIElementaryStream) {

					// sicommoninfo.getSIDatabase().retrieveTimeFromTDT(null,
					// EpgTest.this);
					Log.e("SIElementaryStream", "得到时间" + "");
					// } else {
					// Log.e("SIElementaryStream", "没有时间表" + "");
					// }

					int inttemp = 0;
					if (((SIElementaryStream) sicommoninfo).getComponentTag() < 0) {
						inttemp = ((SIElementaryStream) sicommoninfo)
								.getComponentTag() & 0xff;
					} else {
						inttemp = ((SIElementaryStream) sicommoninfo)
								.getComponentTag();
					}
					isSINetwork = false;
				} else if (sicommoninfo instanceof SITime) {
					Date date = ((SITime) sicommoninfo).getUTCTime();
					// startTime = date.getTime();
					// // 节目详情调用方法
					// programDetails(startTime);
					isSINetwork = false;
				} else if (sicommoninfo instanceof SITransportStream) {
					isSINetwork = false;
				} else if (sicommoninfo instanceof SIBouquet) {
					isSINetwork = false;
				} else if (sicommoninfo instanceof SIEvent) {
					// dlist.add((SIEvent) sicommoninfo);
					// removeDuplicateWithOrder(dlist);
					Log.e("TAG", ((SIEvent) sicommoninfo).getStartTime()
							.toString());
					Message message = myhHandler.obtainMessage();
					Bundle data = new Bundle();

					// “Sun Jan 29 14:34:06 格林尼治标准时间+0800 2012”解析

					data.putString("time",
							((SIEvent) sicommoninfo).getStartTime() + "");
					data.putString("time1",
							((SIEvent) sicommoninfo).getEndTime() + "");
					data.putString("name",
							((SIEvent) sicommoninfo).getEventName());
					message.setData(data);
					;
					if (ispres) {
						message.what = 3;

						ispres = false;
					} else {
						message.what = 4;
						ispres = true;
					}
					myhHandler.sendMessage(message);

					// isSIEvet = true;
					isSINetwork = false;
				}
			}
			Log.e("tag", "........................");
			if (isSINetwork) {
				// TODO 刷新
				Message message = myhHandler.obtainMessage();
				message.what = 0;
				myhHandler.sendMessage(message);
				isSINetwork = false;
				Log.e("tag", mylist.size() + "");
			}

		} else {
			Log.d("TAG", "失败");
			String infoString = "";
			switch (((SIFailureRetrieveEvent) event).getReason().getCode()) {
			case SIRequestFailureType.UNKNOWN:
				infoString = "未知";
				break;
			case SIRequestFailureType.CANCELED:
				infoString = "被应用取消";
				break;
			case SIRequestFailureType.DATA_UNAVAILABLE:
				infoString = "数据不可用";
				break;
			case SIRequestFailureType.INSUFFICIENT_RESOURCES:
				infoString = "资源不足";
				break;
			default:
				break;
			}
			Log.d("TAG", "SIFailureRetrieveEvent:" + infoString);
			Message message = myhHandler.obtainMessage();
			message.what = 1;
			myhHandler.sendMessage(message);

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		Log.e("TAg", "按键" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == 45) {
			if (position == mylist.size() - 1) {
				position = 0;
			} else
				position += 1;
			hplayer();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == 33) {
			if (position == 0) {
				position = mylist.size();
			} else
				position -= 1;
			hplayer();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == 51) {
			showDialogList();
			return true;
		}
		if (keyCode >= 7 && keyCode <= 16) {
			showDialogNuber();
		}
		return super.onKeyDown(keyCode, event);
	};

	Handler myhHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0 && mylist.size() != 0) {
				hplayer();
			} else if (msg.what == 1) {
				// 没有太
			} else if (msg.what == 2) {
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (DialogNuber != null) {
					DialogNuber.dismiss();
					DialogNuber = null;
				}

			} else if (msg.what == 3) {

				textView4.setText(MyTime(msg.getData().getString("time"))
						+ "-----");
				textView5.setText(MyTime(msg.getData().getString("time1"))
						+ "     ");
				textView6.setText("" + msg.getData().getString("name"));
			} else if (msg.what == 4) {

				textView3.setText(MyTime(msg.getData().getString("time"))
						+ "-----");
				textView7.setText(MyTime(msg.getData().getString("time1"))
						+ "     ");
				textView8.setText("" + msg.getData().getString("name"));
			}
		}
	};

	/** 播放 */
	private synchronized void hplayer() {
		if (mylist != null && mylist.size() != 0)
			if (mylist.size() > position) {
				PlayUtils.getInstance().hplayer(
						mylist.get(position).getDvbLocator().toString(),
						new Rect(0, 0, width, height), true);
				// play.addListener(this);// 添加监听器
				// play.setDataSource(plist.get(position).getDvbLocator())a;
				timerun();
				mylist.get(position).retrievePresentEvent(null, this, null);
				ispres = true;
				mylist.get(position).retrieveFollowingEvent(null, this, null);
				showDialog();
				showDialogNuber();
			} else {
				Toast.makeText(this, "没有频道", 0).show();
			}

	}

	boolean ispres = true;

	private void showDialogList() {
		View view1 = LayoutInflater.from(context).inflate(R.layout.player_x,
				null);
		pListv = (ListView) view1.findViewById(R.id.listView1);
		pAdapter = new PBaseAdapter(this, mylist);
		pListv.setAdapter(pAdapter);
		pListv.setOnItemClickListener(this);
		dialog2 = new MyDialog(context, view1, new RectD((15 / 16.0f),
				(1 / 4.0f), -(1 / 2.8f), 0));
		if (dialog2 != null) {
			dialog2.show();
		}
	}

	TextView textView;

	private void showDialog() {

		// 设置系统时间 显示
		SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd日 HH:mm");
		String s = sdf2.format(System.currentTimeMillis());

		if (dialog != null) {
			// dialog.dismiss();
			textView.setText(mylist.get(position).getServiceID()
					+ "                            "
					+ mylist.get(position).getServiceProviderName());
			textView1.setText(s);
			// textView1.setText(Test.mian.);
			// textView1.setText("    " +
			// mylist.get(position).getServiceProviderName());
		} else {
			try {
				view = LayoutInflater.from(context).inflate(R.layout.player_m,
						null);
				textView = (TextView) view.findViewById(R.id.textView1);
				textView1 = (TextView) view.findViewById(R.id.textView2);
				textView3 = (TextView) view.findViewById(R.id.textView3);
				textView4 = (TextView) view.findViewById(R.id.textView4);
				textView5 = (TextView) view.findViewById(R.id.textView5);
				textView6 = (TextView) view.findViewById(R.id.textView6);
				textView7 = (TextView) view.findViewById(R.id.textView7);
				textView8 = (TextView) view.findViewById(R.id.textView8);
				// 设置 电视台的ID和该电视台的名字

				textView.setText(mylist.get(position).getServiceID()
						+ "                         "
						+ mylist.get(position).getServiceProviderName());

				RectD rectd = new RectD(0.25f, 7 / 8.0f, 0, 0.25f);
				dialog = new MyDialog(context, view, rectd);
			} catch (IllegalStateException e) {
				// TODO
				e.printStackTrace();
			}
		}
		if (dialog != null) {
			textView1.setText(s);
			dialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.e("TAg", "dia按键" + keyCode);
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						if (keyCode == KeyEvent.KEYCODE_DPAD_UP
								|| keyCode == 45) {
							if (position == mylist.size() - 1) {
								position = 0;
							}
							position += 1;
							hplayer();
						} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
								|| keyCode == 33) {
							if (position == 0) {
								position = mylist.size();
							}
							position -= 1;
							hplayer();
						} else if (keyCode == KeyEvent.KEYCODE_BACK) {
							dialog.dismiss();
							OFFLinePlayer.this.dialog = null;
						}
					}

					return true;
				}
			});

			dialog.show();
			timerun();

		}
	}

	private void showDialogNuber() {

		// 设置系统时间 显示
		SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd日 HH:mm");
		String s = sdf2.format(System.currentTimeMillis());

		if (DialogNuber != null) {
			textView10.setText("5");

			// textView1.setText(Test.mian.);
			// textView1.setText("    " +
			// mylist.get(position).getServiceProviderName());
		} else {
			try {
				View view = LayoutInflater.from(context).inflate(
						R.layout.player_k, null);

				// 设置 台的ID
				textView10 = (TextView) view.findViewById(R.id.textView10);
				textView10.setText("5");

				DialogNuber = new MyDialog(context, view, new RectD(0, 0,
						7 / 8.0f, 1 / 8.0f));
			} catch (IllegalStateException e) {
				// TODO
				e.printStackTrace();
			}
		}
		if (DialogNuber != null) {
			textView1.setText(s);
			DialogNuber.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.e("TAg", "dia按键" + keyCode);
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						if (keyCode == KeyEvent.KEYCODE_DPAD_UP
								|| keyCode == 45) {
							if (position == mylist.size() - 1) {
								position = 0;
							}
							position += 1;
							hplayer();
						} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
								|| keyCode == 33) {
							if (position == 0) {
								position = mylist.size();
							}
							position -= 1;
							hplayer();
						} else if (keyCode == KeyEvent.KEYCODE_BACK) {
							dialog.dismiss();
							OFFLinePlayer.this.DialogNuber = null;
						}
					}

					return true;
				}
			});

			DialogNuber.show();
			timerun();

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		PlayUtils.getInstance().playstop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	Thread thread = null;
	int i = 3;
	boolean isrun = false;

	private void timerun() {
		Log.e("run", "" + isrun);
		if (isrun) {
			i = 3;
			Log.e("thread", "thread" + "不空");
		} else {
			Log.e("TAG", "" + i);
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO
					try {
						while (i > 0) {
							isrun = true;
							Thread.sleep(1 * 1000);
							i--;
							Log.e("TAG", "" + i);
						}
						Message message = myhHandler.obtainMessage();
						message.what = 2;
						myhHandler.sendMessage(message);
						i = 3;
						isrun = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			thread.start();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (pListv == parent) {
			this.position = position;
			hplayer();
			dialog2.dismiss();
		}
	}

	@SuppressLint("SimpleDateFormat")
	public String MyTime(String str) {
		// “Sun Jan 29 14:34:06 格林尼治标准时间+0800 2012”解析
		try {
			if (!"".equals(str)) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy",
						Locale.ENGLISH);
				Date d;
				d = sdf.parse(str);
				SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
				// Log.e("jiemu", sdf2.format(d));
				return sdf2.format(d);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sdf2.format(d);

		// str = "Sun Jan 29 14:34:06 格林尼治标准时间+0800 2012";

	}
}