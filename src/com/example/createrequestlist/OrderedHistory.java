package com.example.createrequestlist;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OrderedHistory extends Activity implements OnItemClickListener{

	private ArrayList<String> dateList = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		//ActivityクラスのonCreateを実行
		super.onCreate(savedInstanceState);
		
		//レイアウト設定ファイルの指定
		this.setContentView(R.layout.ordered_history);
		
		//ListViewの設定
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		ListView listview = (ListView)this.findViewById(R.id.listview);
		getFiles();
		for(int i=0; i<dateList.size(); i++){
			adapter.add(dateList.get(i));		//日付追加	
		}
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);	//クリックリスナーの設定
	}
	
	//ファイル取得
	private void getFiles(){
		String[] pFiles = this.fileList();
		for(String pFile:pFiles){
			setDate(pFile);
		}
	}
	
	//日時設定
	private void setDate(String pFile){
		try {
			//一行目(日時)のみ取得
			FileInputStream stream = this.openFileInput(pFile);
			BufferedReader in = new BufferedReader(new InputStreamReader(stream));
			String date = in.readLine();
			
			//検索位置設定
			Integer start = date.indexOf("'");
			Integer end = date.lastIndexOf("'");
			
			//日時設定
			dateList.add(date.substring(start+1, end));
			in.close();
		} catch (IOException e) {
			Log.e("setDateErr","ファイルの読み込みに失敗しました。");
		}
	}
	
	//トップページへ押下した場合
	public void returnTopPage(View view){
		this.finish();		//アクティビティ終了
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		//ListView取得
		ListView listview = (ListView)parent;
		
		//選択された日時取得
		String selectedDate = (String)listview.getItemAtPosition(position);
		
		//インテント設定
		Intent intent = new Intent(this,ProductHistory.class);
		intent.putExtra("OrderedDate", selectedDate);
		startActivity(intent);		//アクティビティ起動
	}	
}
