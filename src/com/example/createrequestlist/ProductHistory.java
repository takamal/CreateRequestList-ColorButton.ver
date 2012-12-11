package com.example.createrequestlist;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ProductHistory extends Activity {

	//おねがいリスト用レイアウト
	private LinearLayout lLayout;
	private TableLayout tLayout;	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		//ActivityクラスのonCreateを実行
		super.onCreate(savedInstanceState);
		
		//レイアウト設定ファイルの指定
		this.setContentView(R.layout.product_history);
		
		//初期化
		lLayout = (LinearLayout)this.findViewById(R.id.historyItem);
		lLayout.removeAllViews();
		
		//インテント取得
		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		getFiles(extras.getString("OrderedDate"));
	}
	
	//ファイル取得
	private void getFiles(String orderedDate){
		String[] pFiles = this.fileList();
		for(String pFile:pFiles){
			getDataMain(pFile, orderedDate);
		}
	}	
	
	//データ取得メイン処理
	private void getDataMain(String pFile, String orderedDate){
		//初期化
		String itemName = "";
		String itemNum = "";
		
		try {
			//一行目(日時)のみ取得
			FileInputStream stream = this.openFileInput(pFile);
			BufferedReader in = new BufferedReader(new InputStreamReader(stream));
			String historyDate = in.readLine();
			
			//取得した日時と一致しているか？
			if(("'" + orderedDate + "'").equals(historyDate)){
				createHeader(orderedDate);		//ヘッダー作成
				String dataLine ="";
				while((dataLine = in.readLine()) != null){
					//カンマでデータを分割する
					String[] itemInfo = dataLine.split(",");
					for(int i=0; i<itemInfo.length; i++){
						//検索位置設定
						Integer start = itemInfo[i].indexOf("'");
						Integer end = itemInfo[i].lastIndexOf("'");
						
						//データ設定
						if(i == 0){
							itemName = itemInfo[i].substring(start+1, end);		//品物名
						}else{
							itemNum = itemInfo[i].substring(start+1, end);		//数量
						}
					}
					createOrderedList(itemName,itemNum);	//品物情報設定
				}
				lLayout.addView(tLayout);	//レイアウト追加
			}
			in.close();		//クローズ処理
		} catch (IOException e) {
			Log.e("getDataMainErr","ファイルの読み込みに失敗しました。");
		}
	}
	
	private void createHeader(String date){
		//TextView取得
		TextView date_tv = (TextView)this.findViewById(R.id.date);
		date_tv.setText(date);
		
		//ヘッダー作成
		tLayout = new TableLayout(this);
		tLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		TableRow head = new TableRow(this);
		head.setPadding(20, 0, 0, 40);
		TextView hName = new TextView(this);
		hName.setText("品物名");
		hName.setTextSize(25);
		head.addView(hName);
		
		TextView hNum = new TextView(this);
		hNum.setText("数量");
		hNum.setTextSize(25);
		hNum.setGravity(Gravity.CENTER_HORIZONTAL);
		head.addView(hNum);
		tLayout.addView(head);
	}
	
	private void createOrderedList(String itemName, String itemNum){
		//おねがい情報生成
		TableRow row = new TableRow(this);
		row.setPadding(20, 0, 0, 30);
		TextView name = new TextView(this);
		name.setText(itemName);
		name.setTextSize(20);
		name.setWidth(380);
		name.setPadding(0, 0, 40, 0);
		row.addView(name);
		
		TextView num = new TextView(this);
		num.setText(itemNum);
		num.setTextSize(20);
		num.setGravity(Gravity.CENTER_HORIZONTAL);
		row.addView(num);
		tLayout.addView(row);
	}
	
	//トップページへ押下した場合
	public void returnPage(View view){
		this.finish();		//アクティビティ終了
	}
}
