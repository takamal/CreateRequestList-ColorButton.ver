package com.example.createrequestlist;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class ProductFiles {
	
	private Context con;
	private String time;
	private String inputTxt;
	private final static Integer FILE_NUM = 5;
	private String[] pFiles;
	
	public ProductFiles(Context context, String createTime, String inputTxt){
		this.con = context;		//呼び出し元のアクティビティを取得
		this.time = createTime;
		this.inputTxt = inputTxt;
	}
	
	//ファイル作成メイン処理
	public void fileMain(){
		if(checkFiles() == FILE_NUM){
			removeFile();	//ファイル削除処理へ
		}
		createFile();		//ファイル作成処理へ
	}
	
	//既存ファイルチェック
	private Integer checkFiles(){
		Integer fileCount = 0;				//ファイル数
		pFiles = con.fileList();			//保存されているファイル取得
		if(pFiles.length != 0 || pFiles != null){
			fileCount = pFiles.length;		//ファイルがある
		}
		return fileCount;
	}
	
	//ファイル作成処理
	private void createFile(){
		String fileName = "Product_" + time + ".csv";		//ファイル名設定
		try{
			//ファイル作成
			FileOutputStream stream = con.openFileOutput(fileName,Context.MODE_PRIVATE);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream));
			out.write(setDate());
			out.write(inputTxt);
			out.flush();
			out.close();
		}catch (IOException e){
			Log.e("createFileErr", "ファイル作成処理で失敗しました。");
		}	
	}
	
	//日時設定
	private String setDate(){
		//年月日
		Date date = new Date();
		DateFormat fdate = new SimpleDateFormat("yyyy年MMMMd日");
		
		//時分秒
		Date time = new Date();
		DateFormat ftime = new SimpleDateFormat("HH:mm:ss");
		return "'" + fdate.format(date) + "　" + ftime.format(time) + "'\n";
	}
	
	//ファイル削除処理
	private void removeFile(){
		Long basisTime = 0L;		//基準日時
		Long nextTime = 0L;			//日時
		Integer oldIndex = 0;		//最も古い日時のindex
		
		for(int i=0; i<pFiles.length; i++){
			//日時部分の文字列取得
			Integer underBar = pFiles[i].indexOf("_");		//検索開始位置
			Integer comma = pFiles[i].indexOf(".");			//検索終了位置
			String resultStr = pFiles[i].substring(underBar + 1,comma);
			
			//最初の要素を基準日時とする
			if(i == 0){
				basisTime = Long.valueOf(resultStr);
			}else{
				nextTime = Long.valueOf(resultStr);
				//日時比較
				if(nextTime < basisTime){
					//trueの場合、基準日として設定する
					basisTime = nextTime;
					oldIndex = i;
				}
			}
		}
		
		//ファイル削除
		con.deleteFile(pFiles[oldIndex]);
	}
}
