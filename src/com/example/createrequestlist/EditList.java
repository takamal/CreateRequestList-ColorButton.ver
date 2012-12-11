package com.example.createrequestlist;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class EditList extends Activity{

	//データベース関連
	private ProductDBHelper pHelper;
	private SQLiteDatabase productDB;
	private static final String[] COLUMNS = {
		"id","product_name","product_category"
	};
	
	//処理モード
	private String modeFlag;
	private String modeType;
	
	//ラジオボタン表示領域
	private LinearLayout lLayout;
	
	//品物情報
	private String item_name;
	private String category_name;
	private HashMap<Integer,String[]> itemMap = new HashMap<Integer,String[]>();
	private Integer id;
	
	//選択ダイアログ位置情報
	private static int categoryWhich;
	private static int modeWhich; 
	
//＊＊＊＊＊＊＊＊＊＊＊＊＊＊初期処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edit_list);
		
		//データ取得
		this.getData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(productDB != null){
			productDB.close();		//データベースを閉じる
		}
	}
	
	private void getData(){
		//データベースを開く
		pHelper = new ProductDBHelper(this);
		productDB = pHelper.openDataBase();
		
		//ラジオボタン生成
		createRadio();
		productDB.close();		//データベースを閉じる
	}
	
	private void createRadio(){
		//データ取得
		Cursor cursor = productDB.query("product_info", COLUMNS, null, null, null, null, "product_category");
		
		//LinearLayoutオブジェクト取得
		if(lLayout == null){
			lLayout = (LinearLayout)this.findViewById(R.id.itemInfo);
		}
		lLayout.removeAllViews();		//LinearLayout初期化
		
		if(cursor.moveToFirst()){
			//RadioGroup生成(レコードが1件以上の場合)
			RadioGroup rGroup = new RadioGroup(this);
			rGroup.setId(0);

			do{	
				//品物情報を保持
				this.keepItemInfo(cursor);
				
				//RadioButton生成
				RadioButton rButton = new RadioButton(this);
				rButton.setId(cursor.getInt(0));
				String itemName = "　品物名：　" + cursor.getString(1) + "\n　種別：　　" + cursor.getString(2);
				rButton.setText(itemName);
				rGroup.addView(rButton);	//RadioGroupにRadioButtonを追加
			}while(cursor.moveToNext());			
			lLayout.addView(rGroup);		//LinearLayoutにRadioGroupを追加
		}else{
			//TextView生成(レコードが0件のとき)
			TextView nothing = new TextView(this);
			nothing.setText("品物は現在登録されていません。");
			lLayout.addView(nothing);		//LinearLayoutにTextViewを追加
		}
		//検索結果クリア
		cursor.close();
	}
//＊＊＊＊＊＊＊＊＊＊＊＊＊＊初期処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊

//＊＊＊＊＊＊＊＊＊＊＊＊＊＊追加処理メソッド ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	//追加処理メイン
	public void itemAddMain(View view){
		//フラグ初期化
		modeFlag = "0";
		modeType = "追加";
		
		//品物入力ダイアログ表示へ
		this.inputItemName();
	}
//＊＊＊＊＊＊＊＊＊＊＊＊＊＊追加処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
//＊＊＊＊＊＊＊＊＊＊＊＊＊＊変更処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	//変更モード選択ダイアログ生成
	private void editModeDialog(){
		//初期化
		modeWhich = 0;
		String[] EDIT_MODES = {"品物名と種別を変更", "品物名のみ変更", "種別のみ変更"};
		
		//ダイアログ生成
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(android.R.drawable.ic_menu_info_details);
		dialog.setTitle("変更モード選択ダイアログ");
		dialog.setSingleChoiceItems(EDIT_MODES, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				modeWhich = whichButton;						//選択位置保持
			}
		});
		dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				selectEditMode(String.valueOf(modeWhich));		//詳細フラグ設定
			}
		});
		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				showMsg("キャンセルが押されました。\n" + modeType + "処理を終了します。");
			}
		});
		dialog.show();											//ダイアログ表示
	}
	
	//モード別変更処理へ
	private void selectEditMode(String editFlag){
		modeFlag = modeFlag + editFlag;
		if(("10".equals(modeFlag)) || ("11".equals(modeFlag))){
			inputItemName();	//品物名入力へ
		}else if("12".equals(modeFlag)){
			selectCategory();	//種別選択へ
		}
	}	
//＊＊＊＊＊＊＊＊＊＊＊＊＊＊変更処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
//＊＊＊＊＊＊＊＊＊＊＊＊＊＊共通処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	//変更削除処理メイン
	public void itemUpDelMain(View view){
		//フラグ初期化
		switch(view.getId()){
		case R.id.ItemUpdate:
			modeFlag = "1";
			modeType = "変更";
			break;
		case R.id.ItemDelete:
			modeFlag = "2";
			modeType = "削除";
			break;
		}

		//ラジオボタン取得
		RadioButton rButton = this.checkRadioButton();
		if(rButton == null){
			showMsg("【品物リスト】から品物をひとつ選択してください。");
		}else{
			//選択したアイテム情報を取得
			id = rButton.getId();		//SQL文検索用
			String[] itemInfo = itemMap.get(id);
			item_name = itemInfo[0];
			category_name = itemInfo[1];
			confirmDialog();			//確認ダイアログへ
		}
	}	
	
	//品物名入力ダイアログ表示
	public void inputItemName(){
		//初期化
		item_name = "";
		
		//EditText生成
		final EditText itemName = new EditText(this);
		itemName.setHint("(例) トマト");
		itemName.setInputType(InputType.TYPE_CLASS_TEXT);
				
		//ダイアログ生成
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(android.R.drawable.ic_menu_info_details);
		dialog.setTitle("品物入力ダイアログ");
		dialog.setMessage("品物名を入力してください。");
		dialog.setView(itemName);
		dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//品物名入力チェック
				item_name = itemName.getText().toString();
				if(checkItemName()){
					if("11".equals(modeFlag)){
						confirmDialog();	//確認ダイアログ表示
					}else{
						selectCategory();	//カテゴリ選択ダイアログ画面へ
					}
				}
			}
		});
		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showMsg("キャンセルが押されました。\n" + modeType + "処理を終了します。");
			}
		});
		//ダイアログ表示
		dialog.show();
	}
		
	//カテゴリ選択画面表示
	private void selectCategory(){
		//初期化
		category_name = "";
		categoryWhich = 0;
		
		//ダイアログ生成
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(android.R.drawable.ic_menu_info_details);
		dialog.setTitle("種別選択ダイアログ");
		dialog.setSingleChoiceItems(Category.CATEGORIES, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				categoryWhich = whichButton;							//選択位置保持
			}
		});
		dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				category_name = Category.CATEGORIES[categoryWhich];		//種別情報取得
				confirmDialog();										//確認ダイアログ表示
			}
		});
		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				showMsg("キャンセルが押されました。\n" + modeType + "処理を終了します。");
			}
		});
		dialog.show();													//ダイアログ表示
	}
	
	//確認ダイアログ
	private void confirmDialog(){
		//ダイアログ生成
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(android.R.drawable.ic_menu_info_details);
		dialog.setTitle("確認ダイアログ");
		dialog.setMessage(confirmTxt());
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if("1".equals(modeFlag)){
					editModeDialog();	//変更モード選択へ
				}else{
					dbMainHandling();	//データ操作へ
				}
			}
		});
		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showMsg("キャンセルが押されました。\n" + modeType + "処理を終了します。");
			}
		});
		dialog.show();				//ダイアログ表示
	}	
	
	//チェックされているラジオボタン取得
	public RadioButton checkRadioButton(){
		//生成したRadioGroupを取得
		RadioGroup rGroup = (RadioGroup)this.findViewById(0);
									
		//チェックされているRadioButtonを戻り値として返す
		RadioButton rButton = (RadioButton)this.findViewById(rGroup.getCheckedRadioButtonId());
		return rButton;
	}
	
	//品物情報保持
	private void keepItemInfo(Cursor cursor){
		String[] itemArray = { cursor.getString(1), cursor.getString(2)};
		itemMap.put(cursor.getInt(0), itemArray);
	}
	
	//品物名入力チェック
	private boolean checkItemName(){
		if("".equals(item_name.trim())){
			showMsg("品物名が未入力です。\n" + modeType + "処理を終了します。");
			return false;
		}else if(item_name.indexOf(",") != -1){
			showMsg("品物名にコンマ(,)は使用できません。\n" + modeType + "処理を終了します。");
			return false;
		}else{
			return true;
		}
	}
	
	//ダイアログ表示
	private void showMsg(String msg){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(android.R.drawable.ic_menu_info_details);
		dialog.setTitle("メッセージ");
		dialog.setMessage(msg);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		dialog.show();
	}
	
	//確認ダイアログ用文言生成
	private String confirmTxt(){
		//定型文言
		String formatTxt;
		if(("10".equals(modeFlag)) || ("11".equals(modeFlag)) || ("12".equals(modeFlag))){
			formatTxt = "以下の内容で";
		}else{
			formatTxt = "以下の内容を";
		}
		
		return formatTxt + modeType + "しますか？" + 
				"\n品物名：　" + item_name +
				"\n種別：　　" + category_name;
	}
	
	//DBメイン処理
	private void dbMainHandling(){
		//データベース存在チェック
		if(productDB != null){
			productDB.close();
		}
		//データベースを開く
		productDB = pHelper.openDataBase();
		
		try{
			productDB.beginTransaction();			//トランザクション制御開始
			modeHandling();							//モード別処理へ
			productDB.setTransactionSuccessful();	//コミット
			productDB.endTransaction();				//トランザクション制御終了
		}catch(Exception e){
			Log.e(modeType + "処理",e.toString());
		}finally{
			productDB.close();						//データベースを閉じる
		}
		//後処理
		showMsg("データを" + modeType + "しました。");
		lLayout.removeAllViews();
		this.getData();
	}
	
	//モード別処理
	private void modeHandling(){
		if("2".equals(modeFlag)){
			//削除処理
			String condition = "id = '" + id + "'";
			productDB.delete("product_info", condition, null);
		}else{
			//データ設定
			ContentValues val = new ContentValues();
			val.put("product_name", item_name);
			val.put("product_category", category_name);
			if("0".equals(modeFlag)){
				//登録処理
				productDB.insert("product_info", null, val);
			}else if(("10".equals(modeFlag)) || ("11".equals(modeFlag)) || ("12".equals(modeFlag))){
				//変更処理
				String condition = "id = '" + id + "'";
				productDB.update("product_info", val, condition, null);
			}
		}
	}
	
	//トップページへ押下した場合
	public void returnTopPage(View view){
		this.finish();		//アクティビティ終了
	}
//＊＊＊＊＊＊＊＊＊＊＊＊＊＊共通処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
}
