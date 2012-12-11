package com.example.createrequestlist;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class Category extends Activity{
	
	//データベース
	private SQLiteDatabase productDB;
	
	//データベースの情報
	private static final String[] COLUMNS = {
		"id","product_name","product_category"
	};
	
	//カテゴリの種類
	public static final String[] CATEGORIES ={
		"卵・乳製品・飲料","加工品","魚介","肉類","野菜","果物","その他"
	};
	
	//Buttonオブジェクトに設定するタグ用カウント
	private Integer tagCount;
	
	//カテゴリカウンター
	private Integer categoryCount;
	
	//生成したEditTextをArrayListに格納
	private ArrayList<EditText> edit = new ArrayList<EditText>();
	
	//生成したTableRowをArrayListに格納
	private ArrayList<TableRow> tRow = new ArrayList<TableRow>();
	
	//リソースを持つ。
	private static Drawable pushBackminus; // 押された時の画像
	private static Drawable normalBackminus; // 通常時の画像
	private static Drawable pushBackplus; // 押された時の画像
	private static Drawable normalBackplus; // 通常時の画像
	private Resources res;	
	
//******************************************2012/12/10　押されたカテゴリ画像の変更ここから*******************************************************
	private static Drawable pushmilk;		// 押されたときの画像
	private static Drawable normalmilk;		//	通常時の画像
	
//******************************************2012/12/10　押されたカテゴリ画像の変更ここまで*******************************************************
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		//ActivityのOnCreateを実行
		super.onCreate(savedInstanceState);
		
		//レイアウト設定ファイルの指定
		this.setContentView(R.layout.category);
		
		//IME自動起動無効
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//DB取得
		this.getDB();
		
//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここから******************************************		
		//画像ID取得
		res = getResources();
//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここまで******************************************
	}

	//カテゴリのボタン押下した場合
	public void orderItemInfo(View view){

//******************************************2012/12/6 山野さん(ボタンコメントアウト)ここから******************************************		
		//Button型にキャスト
		//		Button categoryBtn = (Button)view;
//******************************************2012/12/6 山野さん(ボタン)コメントアウトここまで******************************************
		
		//ImageView型にキャスト
		ImageView categoryBtn = (ImageView)view;
		//表示するカテゴリ名称取得
		String categoryName = categoryBtn.getTag().toString();
		
//******************************************2012/12/10　押されたカテゴリ画像の変更ここから*******************************************************
		
		//ImageView各々のインスタンスを保持
		ImageView eggVw = (ImageView)findViewById(R.id.EggAndDairyAndDrink);
		ImageView processVw = (ImageView)findViewById(R.id.ProcessItems);
		ImageView fishVw = (ImageView)findViewById(R.id.FishItems);
		ImageView meetVw = (ImageView)findViewById(R.id.MeetItems);
		ImageView vegetableVw = (ImageView)findViewById(R.id.VegetableItems);
		ImageView fruitVw = (ImageView)findViewById(R.id.FruitItems);
		ImageView elseVw = (ImageView)findViewById(R.id.ElseItems);

		//一律カテゴリボタンを初期に戻す
		eggVw.setImageResource(R.drawable.egg);
		processVw.setImageResource(R.drawable.process);
		fishVw.setImageResource(R.drawable.fish);
		meetVw.setImageResource(R.drawable.meet);
		vegetableVw.setImageResource(R.drawable.vegetable);
		fruitVw.setImageResource(R.drawable.fruit);
		elseVw.setImageResource(R.drawable.etc);

		//押し下のボタンだけ反転
		if(categoryName.equals("卵・乳製品・飲料")){
			categoryBtn.setImageResource(R.drawable.push_egg);

		}else if(categoryName.equals("加工品")){
			categoryBtn.setImageResource(R.drawable.push_process);

		}else if(categoryName.equals("魚介")){
			categoryBtn.setImageResource(R.drawable.push_fish);

		}else if(categoryName.equals("肉類")){
			categoryBtn.setImageResource(R.drawable.push_meet);

		}else if(categoryName.equals("野菜")){
			categoryBtn.setImageResource(R.drawable.push_vegetable);

		}else if(categoryName.equals("果物")){
			categoryBtn.setImageResource(R.drawable.push_fruit);

		}else if(categoryName.equals("その他")){
			categoryBtn.setImageResource(R.drawable.push_etc);

		}
//******************************************2012/12/10　押されたカテゴリ画像の変更ここまで*******************************************************

		
		
		//表示・非表示の制御(ArrayListに格納したTableRow分繰り返す)
		String categoryTag;
		for(int i = 0; i < tRow.size(); i++){
			//TableRowに設定したタグを取得
			categoryTag = (tRow.get(i)).getTag().toString();
			
			//タグと押下されたカテゴリの一致確認
			if(categoryTag.equals(categoryName)){
				tRow.get(i).setVisibility(View.VISIBLE); //表示
			}else{
				tRow.get(i).setVisibility(View.GONE);	//非表示
			}
		}
	}
	
	//トップページへ押下した場合
	public void returnTopPage(View view){
		//アクティビティ終了
		this.finish();
	}
	
	//注文確認を押下した場合
	public void toConfirmPage(View view){
		//HashMap取得
		HashMap<String,String> itemMap = itemMapEntry();
		if(itemMap.isEmpty()){
			showMsg("全ての品物の個数が０個です。");
		}else{
			//インテント生成
			Intent nextActivity = new Intent(this, OrderedConfirm.class);
			nextActivity.putExtra("ITEM_MAP", itemMap);
			this.startActivity(nextActivity);		//アクティビティ起動
		}
	}
	
	//HashMapに品物情報を登録
	private HashMap<String,String> itemMapEntry(){
		//TableLayout取得
		TableLayout tLayout = (TableLayout)this.findViewById(R.id.itemInfo);
		
		//HashMap生成
		HashMap<String,String> checkMap = new HashMap<String,String>();
		
		for(int i=0; i<tLayout.getChildCount(); i++){
			//TableRow内のオブジェクト取得
			TableRow row = (TableRow)tLayout.getChildAt(i);
			TextView itemName = (TextView)row.getChildAt(0);
			EditText itemNum = (EditText)row.getChildAt(2);
			String num = itemNum.getText().toString();
			
			//itemMapに登録<品物名,個数>
			if(!("0".equals(num)) && !("".equals(num))){
				checkMap.put(itemName.getText().toString(), num);
			}
		}
		return checkMap;		//itemMapを返す
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
		dialog.show();
	}	
	
	//データベース取得
	private void getDB(){
		//データベースを開く
		ProductDBHelper pHelper = new ProductDBHelper(this);
		productDB = pHelper.openDataBase();
		
		//(EditText)タグ用カウント初期化
		tagCount = 0;
		categoryCount = 0;
		
		//カテゴリ数分繰り返す
		for(String categoryName:CATEGORIES){
			//TableLayout生成
			this.createTable(categoryName);
			categoryCount = categoryCount + 1;
		}
		//データベース閉じる
		productDB.close();
	}
	
	//TableLayout生成
	private void createTable(String categoryName){
		//データ取得
		Cursor cursor = this.getProductData(categoryName);
		
		//TableLayout取得
		TableLayout tLayout = (TableLayout)this.findViewById(R.id.itemInfo);
		
		//レコード数チェック
		//0件の場合:TextView生成
		//1件以上の場合:TableLayout生成
		if(cursor.moveToFirst()){
			//レコード数分作成する
			do{
				//TableRow生成
				TableRow row = new TableRow(this);
				row.setTag(categoryName);
				
				//初期表示用
				if(categoryCount != 0){
					row.setVisibility(View.GONE);
				}
				
				//TextView
				TextView tv = new TextView(this);
				tv.setText(cursor.getString(1));
				tv.setWidth(230);
				tv.setPadding(0, 0, 0, 70);
				tv.setTextSize(18);
				row.addView(tv);
				
//******************************************2012/11/27 今田さんロジック反映対応(旧Logicコメントアウト)ここから******************************************				
				//マイナスするButtonオブジェクトを追加
				//row.addView(this.createMinusButton());
//******************************************2012/11/27 今田さんロジック反映対応(旧Logicコメントアウト)ここまで******************************************

//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここから******************************************				
				//マイナスするImageViewオブジェクトを追加
				row.addView(this.createMinusView());
//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここまで******************************************
				
				//EditText
				EditText ed = new EditText(this);
				ed.setInputType(InputType.TYPE_CLASS_NUMBER);
				ed.setText("0");
				ed.setWidth(80);
				row.addView(ed);
				
				//EditTextをArrayListに格納
				edit.add(ed);
				
//******************************************2012/11/27 今田さんロジック反映対応(旧Logicコメントアウト)ここから******************************************				
				//プラスするButtonオブジェクトを追加
				//row.addView(this.createPlusButton());
//******************************************2012/11/27 今田さんロジック反映対応(旧Logicコメントアウト)ここまで******************************************

//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここから******************************************				
				//プラスするImageViewオブジェクトを追加
				row.addView(this.createPlusView());
//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここまで******************************************				
				
				//TableRowをArrayListに追加
				tRow.add(row);
				
				//生成したTableRowをTableLayoutに追加
				tLayout.addView(row);
				
				//Buttonオブジェクトに設定するタグ用カウントアップ
				tagCount = tagCount + 1;
				
			}while(cursor.moveToNext());
		}else{
			//TableRow生成
			TableRow row = new TableRow(this);
			row.setTag(categoryName);
			
			//初期表示用
			if(categoryCount != 0){
				row.setVisibility(View.GONE);
			}
			
			//TextView生成
			TextView nothing = new TextView(this);
			nothing.setText("選択されたカテゴリに品物は登録されていません。");
			row.addView(nothing);
			
			//TableRowをArrayListに格納
			tRow.add(row);
			
			//生成したTableRowをTableLayoutに追加
			tLayout.addView(row);
		}
		//検索結果クリア
		cursor.close();
	}
	
	//データ取得
	private Cursor getProductData(String categoryName){
		return productDB.query("product_info", COLUMNS, "product_category='" + categoryName + "'", null, null, null, "id");
	}

//******************************************2012/11/27 今田さんロジック反映対応(旧Logicコメントアウト)ここから******************************************	
	/*
	//マイナス用Buttonオブジェクト生成
	private Button createMinusButton(){
		//Button生成
		Button mBtn = new Button(this);
		mBtn.setWidth(120);
		mBtn.setText("マイナス");
		mBtn.setTag(tagCount);
		mBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Buttonクラスにキャスト
				Button b = (Button)v;
						
				//Buttonオブジェクトに設定したタグを取得(Integer変換)
				Integer i = (Integer)b.getTag();
						
				//ArrayListに格納したEditTextをタグ用カウントを利用して取得する
				EditText editNum = edit.get(i);
						
				//現在表示している数量からプラス１する
				//EditText設定している数字を取得する
				Integer num = Integer.valueOf(editNum.getText().toString());
						
				//プラス１する(現在設定してある数量が０以上の場合、マイナス)
				if(num > 0){
					num = num - 1;
				}
				//プラスした値をEditTextに再設定する
				editNum.setText(String.valueOf(num));
			}
		});
		return mBtn;
	}
	
	//プラス用Buttonオブジェクト生成
	private Button createPlusButton(){
		//Button生成
		Button pBtn = new Button(this);
		pBtn.setWidth(120);
		pBtn.setText("プラス");
		pBtn.setTag(tagCount);
		pBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Buttonクラスにキャスト
				Button b = (Button)v;
				
				//Buttonオブジェクトに設定したタグを取得(Integer変換)
				Integer i = (Integer)b.getTag();
				
				//ArrayListに格納したEditTextをタグ用カウントを利用して取得する
				EditText editNum = edit.get(i);
				
				//現在表示している数量からプラス１する
				//EditText設定している数字を取得する
				Integer num = Integer.valueOf(editNum.getText().toString());
				
				//プラス１する
				num = num + 1;
				
				//プラスした値をEditTextに再設定する
				editNum.setText(String.valueOf(num));
			}
		});
		return pBtn;
	}
	*/
//******************************************2012/11/27 今田さんロジック反映対応(旧Logicコメントアウト)ここまで******************************************

//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここから******************************************	
	//ImageView:プラス
	private ImageView createPlusView(){
		ImageView pImvw = new ImageView(this);
		pImvw.setImageResource(R.drawable.miniplus_hb);
		pImvw.setScaleType(ScaleType.CENTER_CROP);
		pImvw.setTag(tagCount);
		pImvw.setOnTouchListener(new View.OnTouchListener() 
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event){
				// TODO 自動生成されたメソッド・スタブ
				ImageView iV = (ImageView)v;
				
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					if(pushBackplus == null){
						pushBackplus = res.getDrawable(R.drawable.miniplus);
					}
					iV.setImageDrawable(pushBackplus);
				}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					if(normalBackplus == null){
						normalBackplus = res.getDrawable(R.drawable.miniplus_hb);
					}
					iV.setImageDrawable(normalBackplus);
				}else if(event.getAction() == MotionEvent.ACTION_UP) {
					if(normalBackplus == null){
						normalBackplus = res.getDrawable(R.drawable.miniplus_hb);
					}
					iV.setImageDrawable(normalBackplus);
				}
				return false;
			}
		});
		
		pImvw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Integer num = null;
				//ImageViewクラスにキャスト
				ImageView iV = (ImageView)v;
				
				//ImageViewオブジェクトに設定したタグを取得(Integer変換)
				Integer i = (Integer)iV.getTag();
				
				
				//ArrayListに格納したEditTextインスタンスをカウントを利用して取得する
				EditText editNum = edit.get(i);

				if (!((editNum.getText().toString()).equals(""))) {
					num = Integer.valueOf(editNum.getText().toString());
					num = num + 1;
				}else{
					num = 1;
				}
				//プラスした値をEditTextに再設定する
				editNum.setText(String.valueOf(num));
//				iV.setImageResource(R.drawable.miniplus_hb);

			}
		});

		return pImvw;
	}
		
	//ImageView:マイナス
	private ImageView createMinusView(){
		ImageView mImvw = new ImageView(this);
		mImvw.setImageResource(R.drawable.miniminus_hb);
		mImvw.setScaleType(ScaleType.CENTER_CROP);
		mImvw.setTag(tagCount);
		//押下している間の画像変更
		mImvw.setOnTouchListener(new View.OnTouchListener() 
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event){
				// TODO 自動生成されたメソッド・スタブ
				ImageView iV = (ImageView)v;
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					if(pushBackminus == null){
						pushBackminus = res.getDrawable(R.drawable.miniminus);
					}
					iV.setImageDrawable(pushBackminus);
					
				}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					if(normalBackminus == null){
						normalBackminus = res.getDrawable(R.drawable.miniminus_hb);
					}
					iV.setImageDrawable(normalBackminus);
					
				}else if(event.getAction() == MotionEvent.ACTION_UP) {
					if(normalBackminus == null){
						normalBackminus = res.getDrawable(R.drawable.miniminus_hb);
					}
					iV.setImageDrawable(normalBackminus);
				}
				return false;
			}
		});
		
		mImvw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Integer num = null;
				ImageView iV = (ImageView)v;
				Integer i = (Integer)iV.getTag();
				EditText editNum = edit.get(i);
				
				//Log.d("おしたボタン：", String.valueOf(i-1));
				if (!((editNum.getText().toString()).equals(""))) {
					num = Integer.valueOf(editNum.getText().toString());
				
					if ((num > 0) && (num != null)) {							
						num = num - 1;
					}
					editNum.setText(String.valueOf(num));
				}
				
				//Toast.makeText(Category.this, String.valueOf(count2), Toast.LENGTH_SHORT).show();
			}
		});
		
		
		return mImvw;
	}	
//******************************************2012/11/27 今田さんロジック反映対応(ImageViewに変更)ここまで******************************************
	
	//Backキー無効
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		//デバイスボタンが押下された場合
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			//Backキーが押下された場合
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
