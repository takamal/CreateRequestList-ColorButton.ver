package com.example.createrequestlist;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class OrderedConfirm extends Activity {
	//文言設定
	private String mailTxt;
	private String inputTxt;
	
	//おねがいリスト用レイアウト
	private LinearLayout lLayout;
	private TableLayout tLayout;
	
	//ユーザー情報取得用
	private boolean[] checkStatus;
	private String[] usersName;
	private Map<String,String> userMap;
	
	//メールアプリ起動後のリクエストコード
	private static final int REQUEST_CODE = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		//ActivityクラスのonCreateを実行
		super.onCreate(savedInstanceState);
		
		//レイアウト設定ファイルの指定
		this.setContentView(R.layout.ordered_confirm);
		
		//LinearLayout取得
		lLayout = (LinearLayout)this.findViewById(R.id.orderedItem);
		lLayout.removeAllViews();
		mailTxt = "";
		inputTxt = "";
		dateSet();		//日付作成
		
		//品物情報取得
		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		@SuppressWarnings("unchecked")
		HashMap<String,String> itemMap = (HashMap<String,String>)extras.getSerializable("ITEM_MAP");
		
		createHeader();									//ヘッダー作成
		for(String itemName : itemMap.keySet()){
			String itemNum = itemMap.get(itemName);	 	//個数取得
			createOrderedList(itemName,itemNum);		//品物情報作成
			createItemText(itemName,itemNum);			//文言作成
		}
		lLayout.addView(tLayout);
	}
	
	private void dateSet(){
		//日付設定
		DateFormat format = new SimpleDateFormat("yyyy年MMMMd日");
		String dateTxt = format.format(new Date());
		
		//TextView生成
		TextView date = new TextView(this);
		date.setText(dateTxt);
		date.setTextSize(30);
		date.setPadding(0, 0, 0, 30);
		lLayout.addView(date);	//LinearLayoutに追加
	}
	
	private void createHeader(){
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
	
	public void finishActivity(View view){
		finish();	//アクティビティ終了
	}
	
	public void callAddressMain(View view){
		if(getAddress()){
			multiDialog();
		}else{
			showMsg("メールアドレスが設定されているユーザーが電話帳に登録されていません。");
		}
	}
	
	private boolean getAddress(){
		//電話帳アプリからメールアドレスを取得
		Cursor cur = this.managedQuery(Email.CONTENT_URI, null, null, null, null);
		if(cur.getCount() == 0){
			return false;	//電話帳アプリにメールアドレスを持っているユーザーが登録されていない。
		}else{
			//初期化
			usersName = new String[cur.getCount()];		//ユーザー情報設定(ダイアログ表示用)
			checkStatus = new boolean[cur.getCount()];	//チェックボックス初期化
			userMap = new HashMap<String,String>();		//<ユーザー情報,メールアドレス>のHashMap
			int userIndex = 0;							//配列：userName,checkStatus のインデックス
			
			//メールアドレス分繰り返す
			while(cur.moveToNext()){
				setUserInfo(cur,userIndex);
				userIndex = userIndex + 1;
			}
			return true;
		}
	}
	
	private void setUserInfo(Cursor cur, Integer userIndex){
		//ユーザー名取得
		String name = cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME));		
		
		//メール種別取得
		int type = cur.getInt(cur.getColumnIndex(Email.DATA2));						
		int typeInt = Email.getTypeLabelResource(type);
		Resources res = this.getResources();
		String emailType = (res.getText(typeInt)).toString();
		
		//メールアドレス取得
		String email = cur.getString(cur.getColumnIndex(Email.DATA1));				
		
		//ユーザー情報設定(ダイアログ表示用)
		String userInfo = name + "さん\n" + "(" + emailType + ")";
		usersName[userIndex] = userInfo;		
		checkStatus[userIndex] = false;
		
		//userMapに登録<ユーザー情報,メールアドレス>
		userMap.put(userInfo, email);
	}

	//ダイアログ表示
	private void showMsg(String msg){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(android.R.drawable.ic_menu_info_details);
		dialog.setTitle("メッセージ");
		dialog.setMessage(msg);
		dialog.setCancelable(false);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.show();		//ダイアログ表示
	}	
	
	//複数選択ダイアログ生成
	private void multiDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(android.R.drawable.ic_menu_info_details);
		dialog.setTitle("宛先選択");
		dialog.setMultiChoiceItems(usersName, checkStatus, 
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					}
		});
		dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				emailMainHandling();
			}
		});
		dialog.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.show();		//ダイアログ表示
	}
	
	//E-MAIL起動メイン処理
	private void emailMainHandling(){
		//メールアドレスを格納
		String mailAddress = "";
		for(int i=0; i<usersName.length; i++){
			if(checkStatus[i]){
				mailAddress = mailAddress + userMap.get(usersName[i]) + ",";
			}
		}
		
		//選択チェック確認
		if("".equals(mailAddress)){
			showMsg("宛先が選択されいてません。");
		}else{
			emailAppStart(mailAddress);
		}
	}
	
	//E-MAIL起動処理
	private void emailAppStart(String mailAddress){
		//URI設定(宛先)
		Uri uri = Uri.parse("mailto:" + mailAddress);

		//インテント生成
		Intent intent = new Intent("android.intent.action.SENDTO",uri);

		//付属情報設定
		intent.putExtra(Intent.EXTRA_SUBJECT, "【おねがい】必要な品物あり");	//件名
		intent.putExtra(Intent.EXTRA_TEXT, createBodyMsg());			//本文

		//アクティビティ実行
		startActivityForResult(intent, REQUEST_CODE);		
	}
	
	//本文作成処理
	private String createBodyMsg(){
		String body = "";
		body = "下記品物が必要です。\n" + 
		"お手数ですが、品物を購入もしくはいただけないでしょうか？\n" + 
		"(※ご注意　このメールは複数宛先に送信されている場合がございます。" +
		"詳細は宛先一覧を参照してください。)\n\n\n" +
		"【おねがいリスト】\n" + mailTxt;
		return body;
	}
	
	//メールandファイル用文言作成
	private void createItemText(String itemName, String itemNum){
		//メール本文用文言
		mailTxt = mailTxt + "品物名：" + itemName + "\n数量：" + itemNum + "\n\n";
		
		//ファイル用文言
		inputTxt = inputTxt + "'" + itemName + "','" + itemNum + "'\n";
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == REQUEST_CODE){
			//日付作成
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			ProductFiles pf = new ProductFiles(this,df.format(date),inputTxt);
			pf.fileMain();
			
			//トップページへ
			Intent intent = new Intent(this,MainMenu.class);
			startActivity(intent);
		}
	}
}
