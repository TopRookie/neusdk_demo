
package com.neucore.neulink.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.CpuUsageInfo;
import android.os.Environment;
import android.os.Parcel;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.neucore.neulink.msg.DiskInfo;
import com.neucore.neulink.msg.SDInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 *
 * @author Jeff 2013-11-23
 */
public class DeviceUtils {

	private static String TAG = "DeviceUtils";
	public static final int SDCARD_TYPE = 0;			//当前的日志记录类型为存储在SD卡下面
	public static final int DISK_TYPE = 1;			//当前的日志记录类型为存储在磁盘中


	public static String getExternalFilesDir(Context context){
		String path = context.getExternalFilesDir(null).getPath() +File.separator+"neucore";
		new File(path).mkdirs();
		return path;
	}

	public static String getFilesDir(Context context){
		String path =  context.getFilesDir().getPath()+File.separator+"neucore";
		new File(path).mkdirs();
		return path;
	}

	public static String getExternalCacheDir(Context context){
		String path =   context.getExternalCacheDir().getPath() +File.separator+"neucore";
		new File(path).mkdirs();
		return path;
	}

	public static String getCacheDir(Context context){
		String path =   context.getCacheDir().getPath()+File.separator+"neucore";
		new File(path).mkdirs();
		return path;
	}

	private static String getFilesPath(Context context){
		int type = getStoreType();
		String path = null;
		if(type== DISK_TYPE){
			return getFilesDir(context);
		}
		else{
			return getExternalFilesDir(context);
		}
	}

	private static String getCachePath(Context context){
		int type = getStoreType();
		String path = null;
		if(type== DISK_TYPE){
			path = getCacheDir(context);
		}
		else{
			path = getExternalCacheDir(context);
		}
		new File(path).mkdirs();
		return path;
	}

	public static String getTmpPath(Context context){
		String path = getCachePath(context)+File.separator+"tmp";
		new File(path).mkdirs();
		return path;
	}

	public static String getLogPath(Context context){
		String path = getCachePath(context)+File.separator+"logs";
		new File(path).mkdirs();
		return path;
	}

	public static String getDBPath(Context context){
		String path = getFilesPath(context)+File.separator+"databases";
		new File(path).mkdirs();
		return path;
	}

	public static String getConfigPath(Context context){
		String path = getFilesPath(context)+File.separator+"config";
		new File(path).mkdirs();
		return path;
	}

	/**
	 * 获取当前应存储在内存中还是存储在SDCard中
	 * @return
	 */
	public static int getStoreType(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return DISK_TYPE;
		}else{
			return SDCARD_TYPE;
		}
	}
    /**
     *
     * @return
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     *
     * @return
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    /**
     * IMEI
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
	public static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
    /**
     * @return
     */
    public static String getOsVersion() {
        return "android" + android.os.Build.VERSION.SDK_INT;
    }

	protected static volatile UUID uuid;

	public static String getDeviceId(Context context){
		return getCPUSN(context);
	}

	public static String getCPUSN(Context context){

		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			//读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			//查找CPU序列号
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					//查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						//提取序列号
						strCPU = str.substring(str.indexOf(":") + 1,
								str.length());
						//去空格
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					//文件结尾
					break;
				}
			}
		} catch (Exception e) {
			//赋予默认值
			Log.e(TAG, e.getMessage(), e);
		}
		return cpuAddress;
	}

	public static String getMacAddress() {

		String wifiAddress = null;
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File("/sys/class/net/wlan0/address"));
			bufferedReader = new BufferedReader(fileReader);
			wifiAddress = bufferedReader.readLine();

		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException ex) {
				}
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException ex) {
					}
				}
			}
			return wifiAddress;
		}
	}

	public static String getIpAddress(Context context){
		String hostIp = null;
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					ia = ias.nextElement();
					if (ia instanceof Inet6Address) {
						continue;// skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						hostIp = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			Log.e(TAG,e.getMessage(),e);
		}
		return hostIp;
	}

	private static String intToIp(int i)  {
		return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public static CpuUsageInfo getCpuRate(){
		return android.os.CpuUsageInfo.CREATOR.createFromParcel(Parcel.obtain());
	}

	public static DiskInfo readSystem() {
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSizeLong();
		long blockCount = sf.getBlockCountLong();
		long availCount = sf.getAvailableBlocksLong();
		DiskInfo diskInfo = new DiskInfo();
		diskInfo.setTotal(blockSize*blockCount/1024/1024);
		diskInfo.setUsed(blockSize*blockCount/1024/1024-availCount*blockSize/1024/1024);

		return diskInfo;
	}
	public static SDInfo readSD() {
		int type = getStoreType();
		if(SDCARD_TYPE!=type){
			return null;
		}
		File root = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSizeLong();
		long blockCount = sf.getBlockCountLong();
		long availCount = sf.getAvailableBlocksLong();
		SDInfo sdInfo = new SDInfo();
		sdInfo.setTotal(blockSize*blockCount/1024/1024);
		sdInfo.setUsed(blockSize*blockCount/1024/1024-availCount*blockSize/1024/1024);

		return sdInfo;
	}

	public static boolean isSDCardExit(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
}
